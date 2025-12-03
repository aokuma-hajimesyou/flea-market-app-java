package com.example.flea_market_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Chat;
import com.example.flea_market_app.entity.Item;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findByItemOrderByCreatedAtAsc(Item item);
}
