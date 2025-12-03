package com.example.flea_market_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Page<Item> findByNameContainingIgnoreCaseAndStatus(String name, String staus, Pageable pageable);

	Page<Item> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

	Page<Item> findByNameContainingIgnoreCaseAndCategoryIdAndStatus(String name, String status, Long categoryId,
			Pageable pageable);

	Page<Item> findByStatus(String status, Pageable pageable);

	List<Item> findBySeller(User seller);
}
