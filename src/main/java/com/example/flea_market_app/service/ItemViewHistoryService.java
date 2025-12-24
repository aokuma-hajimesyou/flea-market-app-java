package com.example.flea_market_app.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.ItemViewHistory;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.ItemViewHistoryRepository;

@Service
public class ItemViewHistoryService {

	@Autowired
	private ItemViewHistoryRepository repository;

	@Transactional
	public void recordView(User user, Item item) {
		// 出品者本人の閲覧はカウントしない
		if (item.getSeller().getId().equals(user.getId())) {
			return;
		}

		// 過去の履歴があるか確認
		Optional<ItemViewHistory> historyOpt = repository.findByUserAndItem(user, item);

		if (historyOpt.isPresent()) {
			// すでに履歴があれば、時刻だけを現在に更新する
			ItemViewHistory history = historyOpt.get();
			history.setViewedAt(new Timestamp(System.currentTimeMillis()));
			repository.save(history);
		} else {
			// なければ新しく作る
			ItemViewHistory history = new ItemViewHistory();
			history.setUser(user);
			history.setItem(item);
			repository.save(history);
		}
	}
}