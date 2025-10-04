package com.example.first.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.first.model.Candidate;

public interface CandidateRepository extends MongoRepository<Candidate,String> {
	List<Candidate> findByNameContainingIgnoreCase(String q);

}
