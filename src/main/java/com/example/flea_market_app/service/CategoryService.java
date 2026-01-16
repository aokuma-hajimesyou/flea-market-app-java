package com.example.flea_market_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.flea_market_app.entity.Category;
import com.example.flea_market_app.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// --- 追加: 第1階層（親がいない）カテゴリーのみを取得 ---
	public List<Category> getRootCategories() {
		return categoryRepository.findByParentIsNull();
	}

	// --- 追加: 指定した親IDに紐づく子カテゴリーを取得 ---
	public List<Category> getChildCategories(Long parentId) {
		return categoryRepository.findByParentId(parentId);
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Optional<Category> getCategoryById(Long id) {
		return categoryRepository.findById(id);
	}

	public Optional<Category> getCategoryByName(String name) {
		return categoryRepository.findByName(name);
	}

	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}
}