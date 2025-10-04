package com.example.first.model;

import java.util.Date;
import java.util.List;

public class InterviewSession {
    private String id;
    private String candidateId;
    private String role;
    private List<Question> questions;
    private Date askedAt;   // or startTime
    private Date answeredAt; // or endTime
    private Double score;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCandidateId() {
		return candidateId;
	}
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	public Date getAskedAt() {
		return askedAt;
	}
	public void setAskedAt(Date askedAt) {
		this.askedAt = askedAt;
	}
	public Date getAnsweredAt() {
		return answeredAt;
	}
	public void setAnsweredAt(Date answeredAt) {
		this.answeredAt = answeredAt;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}

    // getters and setters
    
}
