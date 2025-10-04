package com.example.first.service;

import com.example.first.config.AIConfig;
import com.example.first.model.InterviewSession;
import com.example.first.model.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class OpenAIServiceImpl implements AIService {

    private final RestTemplate restTemplate;
    private final AIConfig aiConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAIServiceImpl(RestTemplateBuilder builder, AIConfig aiConfig) {
        this.restTemplate = builder.build();
        this.aiConfig = aiConfig;
    }

    @Override
    public Question generateQuestion(InterviewSession session, String difficulty, int questionNumber) {
        String apiKey = aiConfig.getKey();
        String endpoint = aiConfig.getEndpoint();
        String role = session.getRole() == null ? "Full Stack" : session.getRole();

        String prompt = String.format(
            "Generate one %s-level multiple-choice interview question for a %s role. " +
            "Return STRICT JSON ONLY like this:\n" +
            "{ \"question\": \"<question text>\", \"options\": [\"A. ...\", \"B. ...\", \"C. ...\", \"D. ...\"], \"correctAnswer\": \"B\" }",
            difficulty, role
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                "model", "mistral-small",
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                )
            );

            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = mapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();

                JsonNode parsed = mapper.readTree(content);

                Question q = new Question();
                q.setId(UUID.randomUUID().toString());
                q.setDifficulty(difficulty);
                q.setAskedAt(Instant.now());
                q.setTimerSeconds(getTimer(difficulty));
                q.setText(parsed.path("question").asText());

                List<String> opts = new ArrayList<>();
                parsed.path("options").forEach(opt -> opts.add(opt.asText()));
                q.setOptions(opts);

                q.setCorrectAnswer(parsed.path("correctAnswer").asText());

                return q;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // fallback stub
        return stubQuestion(session, difficulty, questionNumber, role);
    }

    @Override
    public double scoreAnswer(Question question, String answer, String resumeText) {
        if (answer == null) return 0.0;
        return answer.equalsIgnoreCase(question.getCorrectAnswer()) ? 100.0 : 0.0;
    }

    @Override
    public String finalSummary(InterviewSession session) {
        double sum = 0; int count = 0;
        for (var q : session.getQuestions()) {
            if (q.getScore() != null) { sum += q.getScore(); count++; }
        }
        double avg = count==0 ? 0.0 : sum / count;
        return String.format("Average score: %.2f. Candidate answered %d/%d questions.",
                avg, count, session.getQuestions().size());
    }

    private Question stubQuestion(InterviewSession session, String difficulty, int questionNumber, String role) {
        Question q = new Question();
        q.setId(UUID.randomUUID().toString());
        q.setDifficulty(difficulty);
        q.setAskedAt(Instant.now());
        q.setTimerSeconds(getTimer(difficulty));
        q.setText(String.format("(%s) %s question #%d (stub).", role, difficulty, questionNumber));
        q.setOptions(List.of("A. Option 1", "B. Option 2", "C. Option 3", "D. Option 4"));
        q.setCorrectAnswer("B");
        return q;
    }

    private int getTimer(String difficulty) {
        return switch (difficulty) {
            case "EASY" -> 20;
            case "MEDIUM" -> 60;
            case "HARD" -> 120;
            default -> 60;
        };
    }
}
