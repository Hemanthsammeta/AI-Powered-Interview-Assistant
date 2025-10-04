package com.example.first.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.first.model.Candidate;
import com.example.first.repository.CandidateRepository;
import com.example.first.repository.SessionRepository;
import com.example.first.service.ResumeService;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) String sort) {
        List<Candidate> list = (q == null)
                ? candidateRepository.findAll()
                : candidateRepository.findByNameContainingIgnoreCase(q);

        if ("score".equals(sort)) {
            list.sort(Comparator.comparingDouble(c -> c.getFinalScore() == null ? 0.0 : -c.getFinalScore()));
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> history(@PathVariable String id) {
        var sessions = sessionRepository.findAll().stream()
                .filter(s -> id.equals(s.getCandidateId()))
                .toList();
        return ResponseEntity.ok(Map.of("sessions", sessions));
    }

    /**
     * Upload resume and extract basic details (name, email, phone).
     * Frontend calls this API before creating a Candidate entry.
     */
    @Autowired
    private ResumeService resumeParserService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("resume") MultipartFile file) {
        try {
            Map<String, String> extracted = resumeParserService.parseResume(file);

            if (!extracted.containsKey("name")) extracted.put("name", "");
            if (!extracted.containsKey("email")) extracted.put("email", "");
            if (!extracted.containsKey("phone")) extracted.put("phone", "");

            return ResponseEntity.ok(extracted);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to process resume", "details", e.getMessage()));
        }
    }


    @PostMapping
    public ResponseEntity<?> saveCandidate(@RequestBody Candidate cand) {
        Candidate saved = candidateRepository.save(cand);
        return ResponseEntity.ok(saved);
    }
}
