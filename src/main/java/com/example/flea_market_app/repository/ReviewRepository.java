package com.example.flea_market_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Review;
import com.example.flea_market_app.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findBySeller(User Seller);

	Optional<Review> findByOrderId(Long OrderId);

	List<Review> findByReviewer(User reviewer);

	List<Review> findBySellerId(Long sellerId);
}
