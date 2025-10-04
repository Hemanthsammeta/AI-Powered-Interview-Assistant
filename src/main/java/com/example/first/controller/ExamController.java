package com.example.first.controller;

import com.example.first.model.Candidate;
import com.example.first.model.InterviewSession;
import com.example.first.model.Question;
import com.example.first.repository.CandidateRepository;
import com.example.first.repository.SessionRepository;
import com.example.first.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AIService aiService;

    // ---------- START EXAM ----------
    @PostMapping("/start")
    public ResponseEntity<?> startExam(@RequestBody Map<String, String> req) {
        String candidateId = req.get("candidateId");

        Candidate cand = candidateRepository.findById(candidateId).orElse(null);
        if (cand == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Candidate not found"));
        }

        // Create new interview session
        InterviewSession session = new InterviewSession();
        session.setCandidateId(candidateId);
        session.setRole("Java Developer");   // ✅ default role
        session.setAskedAt(new Date());      // ✅ session start time

        // Generate 5 questions using AIService
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Question q = aiService.generateQuestion(session, "MEDIUM", i);
            questions.add(q);
        }

        session.setQuestions(questions);
        sessionRepository.save(session);

        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "questions", questions
        ));
    }

    // ---------- SUBMIT EXAM ----------
    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody Map<String, Object> req) {
        String candidateId = (String) req.get("candidateId");
        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) req.get("answers");

        Optional<InterviewSession> latestSessionOpt =
                sessionRepository.findFirstByCandidateIdOrderByAskedAtDesc(candidateId);

        if (latestSessionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
        }

        InterviewSession session = latestSessionOpt.get();

        double score = 0;
        for (Question q : session.getQuestions()) {
            String ans = answers.get(q.getId());   // frontend must send only "A" / "B"
            q.setCandidateAnswer(ans);
            q.setAnsweredAt(Instant.now());
            if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equals(ans)) {
                q.setScore(1.0);
                score += 1.0;
            } else {
                q.setScore(0.0);
            }
        }

        session.setAnsweredAt(new Date()); // ✅ exam finished time
        session.setScore(score);
        sessionRepository.save(session);

        Candidate cand = candidateRepository.findById(candidateId).orElse(null);
        if (cand != null) {
            cand.setFinalScore(score);
            cand.setSummary("Candidate completed exam with score: " + score);
            candidateRepository.save(cand);
        }

        return ResponseEntity.ok(Map.of(
                "score", score,
                "summary", cand != null ? cand.getSummary() : "No summary"
        ));
    }

    // ---------- GET SESSIONS BY CANDIDATE ID ----------
    @GetMapping("/sessions/{candidateId}")
    public ResponseEntity<List<InterviewSession>> getSessionsByCandidateId(@PathVariable String candidateId) {
        List<InterviewSession> sessions =
                sessionRepository.findByCandidateIdOrderByAskedAtDesc(candidateId);
        return ResponseEntity.ok(sessions);
    }
}
