package pojo;

public class TimeSlot {

	public int slotId,kitchenID,quantity;
	public String timeSlot,bikerUserId;
	public String status;
	public boolean checked = false;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return slotId+" "+timeSlot+" "+bikerUserId+" "+kitchenID;
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
	
	
}
