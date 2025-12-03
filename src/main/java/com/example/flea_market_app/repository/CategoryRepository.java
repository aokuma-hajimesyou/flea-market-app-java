package com.example.flea_market_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByName(String name);
}
