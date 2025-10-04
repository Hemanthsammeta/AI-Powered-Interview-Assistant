package com.example.first.controller;

import com.example.first.model.InterviewSession;
import com.example.first.repository.SessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:3000")
public class SessionController {

    private final SessionRepository sessionRepository;

    public SessionController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    // Save a finished or in-progress session (full object including correctAnswer)
    @PostMapping
    public ResponseEntity<InterviewSession> save(@RequestBody InterviewSession session) {
        InterviewSession saved = sessionRepository.save(session);
        return ResponseEntity.ok(saved);
    }

    // Get sessions for a candidate (for interviewer)
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<InterviewSession>> getByCandidate(@PathVariable String candidateId) {
        List<InterviewSession> list = sessionRepository.findAll()
                .stream()
                .filter(s -> candidateId.equals(s.getCandidateId()))
                .toList();
        return ResponseEntity.ok(list);
    }

    // Admin: get all sessions
    @GetMapping
    public ResponseEntity<List<InterviewSession>> getAll() {
        return ResponseEntity.ok(sessionRepository.findAll());
    }
}
