package com.mkyong.rest;

import java.sql.Date;

public class Item {

	public Integer cuisineId;
	
	public String cuisineName;
	
	public Integer categoryId;
	
	public String categoryName;
	
	public Integer quantity;
	
	public Double price;
	
	public String day;
	
	public String meal;
	
	public Date startDate;
	
	public Date endDate;
	
	public String timsSlot;
	
	public String paymentType;

	public Item(){}
	
	public Item(String cuisineName,	String categoryName, Integer quantity, Double price, String day,
			String meal, String timsSlot,String paymentType) {
		super();
		this.cuisineName = cuisineName;
		this.categoryName = categoryName;
		this.quantity = quantity;
		this.price = price;
		this.day = day;
		this.meal = meal;
		this.timsSlot = timsSlot;
		this.paymentType = paymentType;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Cuisine Name = "+cuisineName+" Category Name = "+categoryName+" Quantity = "+quantity
				+" \nPrice = "+price+" TimeSlot = "+timsSlot+" Meal Type = "+meal
				+" PaymentType = "+paymentType;
	}
	
	
	
	
}
