package com.example.first.controller;

import com.example.first.model.InterviewSession;
import com.example.first.model.Question;
import com.example.first.repository.SessionRepository;
import com.example.first.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AIController {

    private final AIService aiService;
    private final SessionRepository sessionRepository;

    public AIController(AIService aiService, SessionRepository sessionRepository) {
        this.aiService = aiService;
        this.sessionRepository = sessionRepository;
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Question> testAI(@RequestParam(defaultValue = "EASY") String difficulty,
                                           @RequestParam(defaultValue = "1") int qnum) {
        InterviewSession session = new InterviewSession();
        session.setCandidateId("test");
        session.setRole("Java Developer");
        Question q = aiService.generateQuestion(session, difficulty, qnum);
        return ResponseEntity.ok(q);
    }

    // Generate an exam for the candidate (public) — returns questions WITHOUT correctAnswer
    // Query param numQuestions - number to generate, difficulty optional or could be mixed
    @GetMapping("/exam")
    public ResponseEntity<Map<String, Object>> generateExam(
            @RequestParam(defaultValue = "6") int numQuestions,
            @RequestParam(defaultValue = "MEDIUM") String difficulty,
            @RequestParam(defaultValue = "Anonymous") String candidateId,
            @RequestParam(defaultValue = "Full Stack") String role
    ) {
        // For reliability, choose distribution if you want: 2 EASY, 2 MEDIUM, 2 HARD
        List<String> difficultyOrder = new ArrayList<>();
        if (numQuestions == 6) {
            difficultyOrder = List.of("EASY","EASY","MEDIUM","MEDIUM","HARD","HARD");
        } else {
            for (int i = 0; i < numQuestions; i++) difficultyOrder.add(difficulty);
        }

        InterviewSession fullSession = new InterviewSession();
        fullSession.setCandidateId(candidateId);
        fullSession.setRole(role);

        List<Question> publicQuestions = new ArrayList<>();

        int idx = 1;
        for (String diff : difficultyOrder) {
            Question fullQ = aiService.generateQuestion(fullSession, diff, idx++);
            // store full question in session (including correctAnswer)
            fullSession.getQuestions().add(fullQ);

            // Build public question (strip correctAnswer and score)
            Question pub = new Question();
            pub.setId(fullQ.getId());
            pub.setDifficulty(fullQ.getDifficulty());
            pub.setText(fullQ.getText());
            pub.setTimerSeconds(fullQ.getTimerSeconds());
            pub.setAskedAt(fullQ.getAskedAt());
            pub.setOptions(fullQ.getOptions()); // options OK to expose
            // DO NOT set correctAnswer or score
            publicQuestions.add(pub);
        }

        // Persist the full session so interviewer can later retrieve answers + correct keys (optional)
        // We save an initial session with questions and correct answers stored server-side.
        sessionRepository.save(fullSession);

        Map<String, Object> resp = new HashMap<>();
        resp.put("sessionId", fullSession.getId());
        resp.put("questions", publicQuestions);
        return ResponseEntity.ok(resp);
    }
}
