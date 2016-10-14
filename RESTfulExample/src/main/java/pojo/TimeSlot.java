package pojo;

public class TimeSlot implements Comparable<TimeSlot>{

	public int slotId,kitchenID,quantity,noOfOrders,cuisineId;
	public String timeSlot,bikerUserId,itemCode;
	public String status;
	public boolean checked = false;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "( Kithen id "+kitchenID+" Qty "+quantity+" SLOTID "+slotId+" ITEM "+itemCode+")";
	}
	
	@Override
	public int compareTo(TimeSlot another) {
		// TODO Auto-generated method stub
		return this.slotId - another.slotId;
	}
	
	public int getSlotId() {
		return slotId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	public String getTimeSlot() {
		return timeSlot;
	}
	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public int getKitchenID() {
		return kitchenID;
	}
	public void setKitchenID(int kitchenID) {
		this.kitchenID = kitchenID;
	}
	public String getBikerUserId() {
		return bikerUserId;
	}
	public void setBikerUserId(String bikerUserId) {
		this.bikerUserId = bikerUserId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getNoOfOrders() {
		return noOfOrders;
	}

	public void setNoOfOrders(int noOfOrders) {
		this.noOfOrders = noOfOrders;
	}

	public int getCuisineId() {
		return cuisineId;
	}

	public void setCuisineId(int cuisineId) {
		this.cuisineId = cuisineId;
	}
	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	
	
}
