package com.mkyong.rest;

import java.sql.Date;

public class Order {

	public String orderNo,orderDateValue;
	public String mealType ;
	public String timeSlot;
	public String deliveryDateValue;
	public Date deliveryDate;
	public String leadTime;
	public String orderTimeStampValue;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderDateValue() {
		return orderDateValue;
	}
	public void setOrderDateValue(String orderDateValue) {
		this.orderDateValue = orderDateValue;
	}
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	public String getTimeSlot() {
		return timeSlot;
	}
	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}
	public String getDeliveryDateValue() {
		return deliveryDateValue;
	}
	public void setDeliveryDateValue(String deliveryDateValue) {
		this.deliveryDateValue = deliveryDateValue;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	public String getOrderTimeStampValue() {
		return orderTimeStampValue;
	}
	public void setOrderTimeStampValue(String orderTimeStampValue) {
		this.orderTimeStampValue = orderTimeStampValue;
	}
	
	
}
