package com.example.first.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "candidates")
public class Candidate {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String resumeFileId; 
    private String role; 
    private Double finalScore;
    private String summary;
    private Instant createdAt = Instant.now();

    // Getters & Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getResumeFileId() {
        return resumeFileId;
    }
    public void setResumeFileId(String resumeFileId) {
        this.resumeFileId = resumeFileId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Double getFinalScore() {
        return finalScore;
    }
    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
