package com.mkyong.rest;

public class Customer {

	String userName;
	String userContact;
	String userAddress;
	
	public Customer(){}
	
	public Customer(String userName, String userContact, String userAddress) {
		super();
		this.userName = userName;
		this.userContact = userContact;
		this.userAddress = userAddress;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Customer Name = "+userName+" \nCustomer Contact = "+userContact+" \nCustomer Address = "+userAddress;
	}
}
