
package com.example.flea_market_app.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	@Transactional
	public void banUser(Long userId, Long adminId, String reason, boolean disableLogin) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setBanned(true);
		if (disableLogin)
			user.setEnabled(false);
		userRepository.save(user);
	}

	@Transactional
	public void unbanUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setBanned(false);
		user.setEnabled(true);
		userRepository.save(user);
	}

	// ダミー実装（必要に応じて本実装に変更）
	public Double averageRating(Long userId) {
		return 0.0;
	}

	public long complaintCount(Long userId) {
		return 0L;
	}

	public List<String> complaints(Long userId) {
		return Collections.emptyList();
	}
}
