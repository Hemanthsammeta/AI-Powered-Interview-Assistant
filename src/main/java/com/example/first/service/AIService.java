package com.example.first.service;

import com.example.first.model.InterviewSession;
import com.example.first.model.Question;

public interface AIService {
    Question generateQuestion(InterviewSession session, String difficulty, int questionNumber);
    double scoreAnswer(Question question, String answer, String resumeText);
    String finalSummary(InterviewSession session);
}
