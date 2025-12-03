package com.example.flea_market_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.ItemViewHistory;

@Repository
public interface ItemViewHistoryRepository extends JpaRepository<ItemViewHistory, Integer> {
}