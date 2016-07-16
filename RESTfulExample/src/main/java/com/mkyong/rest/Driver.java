package com.mkyong.rest;

public class Driver {

	private String driverName;
	private String driverUserId;
	private String contactNo;
	private int statusId,driverId;
	
	public Driver() {
		super();
	}
	public Driver(String driverName, String driverUserId, String contactNo) {
		super();
		this.driverName = driverName;
		this.driverUserId = driverUserId;
		this.contactNo = contactNo;
	}
	
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverUserId() {
		return driverUserId;
	}
	public void setDriverUserId(String driverUserId) {
		this.driverUserId = driverUserId;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	
	
}
