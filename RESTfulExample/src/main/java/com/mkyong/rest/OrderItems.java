package com.mkyong.rest;

import java.sql.Date;

public class OrderItems {
	
	public Integer cuisineId;
	
	public Integer categoryId;
	
	public Integer quantity;
	
	public Double price;
	
	public String day;
	
	public String meal;
	
	public Date startDate;
	
	public Date endDate;
	
	public String timsSlot;

	public Integer getCuisineId() {
		return cuisineId;
	}

	public void setCuisineId(Integer cuisineId) {
		this.cuisineId = cuisineId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimsSlot() {
		return timsSlot;
	}

	public void setTimsSlot(String timsSlot) {
		this.timsSlot = timsSlot;
	}
	
	
}
  