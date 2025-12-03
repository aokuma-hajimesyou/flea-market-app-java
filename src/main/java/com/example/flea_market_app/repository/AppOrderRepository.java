package com.example.flea_market_app.repository;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.entity.AppOrder;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {
	List<AppOrder> findByBuyer(User Buyer);

	List<AppOrder> findByItem_Seller(User seller);
}
