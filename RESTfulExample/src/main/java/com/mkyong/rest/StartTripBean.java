package com.mkyong.rest;

public class StartTripBean {

	public String orderNo;
	
	public String dayName; 
	
	public String mealType;
	
	public String boyUserId;
	
	public Integer boyId;
	
	public String status=null;
	
	public void displayData(){
		System.out.println("Boy id-"+this.boyUserId+" Order No-"+this.orderNo+" Day-"+this.dayName+" Meal-"+this.mealType+" "+this.status);
	}
}
