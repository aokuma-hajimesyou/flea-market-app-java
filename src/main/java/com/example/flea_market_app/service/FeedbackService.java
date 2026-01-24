package com.example.flea_market_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flea_market_app.entity.Feedback;
import com.example.flea_market_app.entity.Subject;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.FeedbackRepository;
import com.example.flea_market_app.repository.SubjectRepository;

@Service
public class FeedbackService {
	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private SubjectService subjectService;

	public List<Feedback> getAllFeedbacks() {
		return feedbackRepository.findAllByOrderByIdAsc();
	}

	public List<Subject> getAllSubjects() {
		return subjectRepository.findAll();
	}

	public Feedback getFeedbackById(Long id) {
		return feedbackRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("feedbacknotfound"));
	}

	public void updateStatus(Long id, String status) {
		Feedback feedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("feedbacknotfound"));
		feedback.setStatus(status);
		feedbackRepository.save(feedback);
	}

	public void deleteFeedback(Long id) {
		feedbackRepository.deleteById(id);
	}

	public void saveFeedback(User user, Long subjectId, String content) {
		Feedback feedback = new Feedback();
		feedback.setUser(user);
		feedback.setSubject(subjectService.getSubjectById(subjectId));
		feedback.setContent(content);
		feedback.setStatus("未対応");

		feedbackRepository.save(feedback);

	}
}
