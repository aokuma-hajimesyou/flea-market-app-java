package com.example.flea_market_app.controller;

import java.io.PrintWriter;
import java.time.LocalDate;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.flea_market_app.service.AppOrderService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	private final ItemService itemService;
	private final AppOrderService appOrderService;
	private final UserService userService;

	public AdminController(ItemService itemService, AppOrderService appOrderService, UserService userService) {
		this.itemService = itemService;
		this.appOrderService = appOrderService;
		this.userService = userService;
	}

	@GetMapping("/items")
	public String manageItems(Model model) {
		model.addAttribute("items", itemService.getAllItems());
		return "admin_items";
	}

	@PostMapping("/items/{Id}/delete")
	public String deleteItemByIdAdmin(@PathVariable("id") Long itemId) {
		itemService.deleteItem(itemId);
		return "redirect:/admin/items?success=deleted";
	}

	@GetMapping("/users")
	public String manageUsers(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin_users";
	}

	@PostMapping("/users/{id}/toggle-enabled")
	public String toggleUserEnabled(@PathVariable("id") Long userId) {
		userService.toggleUserEnabled(userId);
		return "redirect:/admin/users?success=toggled";
	}

	@GetMapping("/statistics")
	public String showStatistics(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			Model model) {
		if (startDate == null)
			startDate = LocalDate.now().minusMonths(1);
		if (endDate == null)
			endDate = LocalDate.now();
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("totalSales", appOrderService.getTotalSales(startDate, endDate));
		model.addAttribute("orderCountByStatus",
				appOrderService.getOrderCountByStatus(startDate, endDate));
		return "admin_statistics";
	}

	@GetMapping("/statistics/csv")
	public String exportStatisticsCSV(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) {
		if (startDate == null)
			startDate = LocalDate.now().minusMonths(1);
		if (endDate == null)
			endDate = LocalDate.now();
		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename=\"flea_market_statistics.csv\"");
		try (PrintWriter writer = response.getWriter()) {
			writer.append("統計期間: " + startDate + " から " + endDate + "\n\n");
			writer.append("総売上: " + appOrderService.getTotalSales(startDate, endDate) + "\n\n");
			writer.append("ステータス別注文数\n");

			appOrderService.getOrderCountByStatus(startDate, endDate).forEach((status, count) -> {
				writer.append(status + "," + count + "\n");
			});
		}
	}
}
