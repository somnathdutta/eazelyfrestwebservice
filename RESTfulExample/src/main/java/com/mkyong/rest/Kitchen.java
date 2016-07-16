package com.mkyong.rest;

import java.util.ArrayList;

public class Kitchen implements Comparable<Kitchen>{

	String kitchenName;
	String kitchenAddress;
	String kitchenNo;
	int kitchenId;
	String receivedTime; 
	String kitchenZip;
	Integer leadTime;
	ArrayList<Item> itemList = new ArrayList<Item>();
	
	
	public Kitchen(){
		
	}
	
	@Override
	public int compareTo(Kitchen o) {
		if(this.leadTime > o.leadTime)
		return 1;
		else return -1;
	}
	
	public Kitchen(String kitchenName, String kitchenAddress, String kitchenNo,
			int kitchenId, String kitchenZip, ArrayList<Item> itemList) {
		super();
		this.kitchenName = kitchenName;
		this.kitchenAddress = kitchenAddress;
		this.kitchenNo = kitchenNo;
		this.kitchenId = kitchenId;
		this.kitchenZip = kitchenZip;
		this.itemList = itemList;
	}




	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Kitchen Name = "+kitchenName+"\n Kitchen Address = "+kitchenAddress+"\n Kitchen No = "+kitchenNo
				+"\n Kitchen zipcode = "+kitchenZip;
	}
	
	
}
