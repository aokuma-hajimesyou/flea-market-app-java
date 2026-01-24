package com.example.flea_market_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.flea_market_app.entity.Subject;
import com.example.flea_market_app.repository.SubjectRepository;

@Service
public class SubjectService {
	private final SubjectRepository subjectRepository;

	public SubjectService(SubjectRepository subjectRepository) {
		this.subjectRepository = subjectRepository;
	}

	public List<Subject> getAllSubjects() {
		return subjectRepository.findAll();
	}

	public Subject getSubjectById(Long id) {
		return subjectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("subject not found"));
	}
}
