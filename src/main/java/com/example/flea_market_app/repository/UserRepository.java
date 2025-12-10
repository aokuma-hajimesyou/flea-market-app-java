package com.example.flea_market_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByEmailIgnoreCase(String username);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller.id = :userId")
	Double averageRatingForUser(Long userId);
}
