package com.example.flea_market_app.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.ItemRepository;
import com.example.flea_market_app.repository.ItemViewHistoryRepository;

@Service
public class RecommendationService {

	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private ItemViewHistoryRepository itemViewHistoryRepository;

	public List<Item> getRecommendedItems(User user) {
		List<Long> categoryId = itemViewHistoryRepository.findViewedCategoryIdsByUser(user);

		if (categoryId == null || categoryId.isEmpty()) {
			return Collections.emptyList();
		}
		return itemRepository.findRecommendedItems(categoryId, user.getId());
	}

}