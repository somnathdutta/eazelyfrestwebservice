package com.mkyong.rest;

public class OrderItemDetailsBean {

	public Integer orderId;
	
	public Integer cuisineId;
	
	public Integer categoryId;
	
	public String cuisineName;
	
	public String categoryName;
	
	public Integer quantity = 0;
	
	public Double price;
	
	public Integer stock;
	
	public String status ;
	
	public String kitchenName;
	
	public Integer kitchenId; 
	
	public Integer subscriptionMealDetailId;

	public String getCuisineName() {
		return cuisineName;
	}

	public void setCuisineName(String cuisineName) {
		this.cuisineName = cuisineName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

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

	public String getKitchenName() {
		return kitchenName;
	}

	public void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;
	}

	public Integer getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Integer kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getSubscriptionMealDetailId() {
		return subscriptionMealDetailId;
	}

	public void setSubscriptionMealDetailId(Integer subscriptionMealDetailId) {
		this.subscriptionMealDetailId = subscriptionMealDetailId;
	}
	
	
}
