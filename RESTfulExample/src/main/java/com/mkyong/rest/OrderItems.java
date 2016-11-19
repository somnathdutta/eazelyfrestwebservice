package com.mkyong.rest;

import java.sql.Date;
import java.util.ArrayList;

public class OrderItems implements Comparable<OrderItems>{
	
	public Integer cuisineId,itemTypeId;
	
	public Integer categoryId;
	
	public String itemCode, itemName, cuisinName, categoryName, packing, itemDescription,mealType;
	
	public Integer quantity;
	
	public Double price;
	
	public String day;
	
	public String meal;
	
	public Date startDate;
	
	public Date endDate;
	
	public String timsSlot;
	
	public int kitchenId; 
	
	private ArrayList<Integer> kitchenIdlist = new ArrayList<Integer>();


	public OrderItems() {
		super();
	}

	public OrderItems(Integer cuisineId, Integer categoryId, String itemCode,
			Integer quantity, Double price) {
		super();
		this.cuisineId = cuisineId;
		this.categoryId = categoryId;
		this.itemCode = itemCode;
		this.quantity = quantity;
		this.price = price;
	}

	@Override
	public int compareTo(OrderItems another) {
		// TODO Auto-generated method stub
		return this.cuisineId - another.cuisineId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(itemcode:"+itemCode+" cui: "+cuisineId+" kitchn:"+kitchenId+" qty: "+quantity+")";
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

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCuisinName() {
		return cuisinName;
	}

	public void setCuisinName(String cuisinName) {
		this.cuisinName = cuisinName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getPacking() {
		return packing;
	}

	public void setPacking(String packing) {
		this.packing = packing;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public int getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(int kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Integer getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(Integer itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public ArrayList<Integer> getKitchenIdlist() {
		return kitchenIdlist;
	}

	public void setKitchenIdlist(ArrayList<Integer> kitchenIdlist) {
		this.kitchenIdlist = kitchenIdlist;
	}
}
  