package com.example.first.model;

import java.time.Instant;
import java.util.List;

public class Question {
    private String id;
    private String difficulty;
    private String text;
    private int timerSeconds;
    private List<String> options; 
    private String correctAnswer; 
    private String candidateAnswer;
    private Double score;
    private Instant askedAt;
    private Instant answeredAt;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getTimerSeconds() {
        return timerSeconds;
    }
    public void setTimerSeconds(int timerSeconds) {
        this.timerSeconds = timerSeconds;
    }
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public String getCandidateAnswer() {
        return candidateAnswer;
    }
    public void setCandidateAnswer(String candidateAnswer) {
        this.candidateAnswer = candidateAnswer;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public Instant getAskedAt() {
        return askedAt;
    }
    public void setAskedAt(Instant askedAt) {
        this.askedAt = askedAt;
    }
    public Instant getAnsweredAt() {
        return answeredAt;
    }
    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }
}
