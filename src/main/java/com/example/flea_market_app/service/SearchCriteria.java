package com.example.flea_market_app.service;

import java.io.Serializable;

public class SearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keyword;
    private Long categoryId;
    private Integer minPrice;
    private Integer maxPrice;
    private boolean includeSold = false;
    private int page = 0;
    private int size = 12; // Default size

    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isIncludeSold() {
        return includeSold;
    }

    public void setIncludeSold(boolean includeSold) {
        this.includeSold = includeSold;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
