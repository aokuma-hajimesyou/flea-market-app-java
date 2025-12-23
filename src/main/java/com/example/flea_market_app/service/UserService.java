package com.example.flea_market_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flea_market_app.entity.Review;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.ReviewRepository;
import com.example.flea_market_app.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;

	public UserService(UserRepository userRepository, ReviewRepository reviewRepository) {
		this.userRepository = userRepository;
		this.reviewRepository = reviewRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	@Transactional
	public void toggleUserEnabled(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setEnabled(!user.isEnabled());
		userRepository.save(user);
	}

	public Double averageRating(Long userId) {
		List<Review> reviews = reviewRepository.findBySellerId(userId);
		if (reviews.isEmpty()) {
			return null;
		}
		return reviews.stream()
				.mapToInt(Review::getRating)
				.average()
				.orElse(0.0);
	}

	public long complaintCount(Long userId) {
		List<Review> reviews = reviewRepository.findBySellerId(userId);
		return reviews.stream()
				.filter(review -> review.getRating() <= 2) // 評価2以下を苦情とみなす
				.count();
	}

	public List<Review> complaints(Long userId) {
		List<Review> reviews = reviewRepository.findBySellerId(userId);
		return reviews.stream()
				.filter(review -> review.getRating() <= 2) // 評価2以下を苦情とみなす
				.toList();
	}

	@Transactional
	public void banUser(Long userId, Long adminId, String reason, boolean disableLogin) {
		userRepository.findById(userId).ifPresent(user -> {
			user.setEnabled(!disableLogin);
			userRepository.save(user);
			System.out.println("User " + userId + " banned by admin " + adminId + " for reason: " + reason); // 仮のログ出力
		});
	}

	@Transactional
	public void unbanUser(Long userId) {
		userRepository.findById(userId).ifPresent(user -> {
			user.setEnabled(true);
			userRepository.save(user);
			System.out.println("User " + userId + " unbanned."); // 仮のログ出力
		});
	}

	// UserService.java の registerNewUser メソッドを修正

	@Transactional
	public void registerNewUser(String name, String email, String password) {
		User user = new User();
		user.setName(name);
		user.setEmail(email);

		// 現在のSecurityConfigの設定に合わせ、生パスワードの頭に {noop} を付与
		user.setPassword("{noop}" + password);

		// DBの必須項目をセット
		user.setRole("USER");
		user.setEnabled(true);
		user.setBanned(false);

		userRepository.save(user);
	}
}
