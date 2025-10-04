package com.example.first.repository;


import com.example.first.model.InterviewSession;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<InterviewSession, String> { 
	List<InterviewSession> findByCandidateIdOrderByAskedAtDesc(String candidateId);
	
	Optional<InterviewSession> findFirstByCandidateIdOrderByAskedAtDesc(String candidateId);
}

