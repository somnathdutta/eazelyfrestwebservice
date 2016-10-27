package pojo;

public class MealTypePojo {

	private boolean lunchToday = false;
	private boolean dinnerToday = false;
	private boolean lunchTomorrow = false;
	private boolean dinnerTomorrow = false;
	
	private int slotId,kitchenId,quantity,noOfOrders;
	private String boyUSerId;
	
	public boolean isLunchToday() {
		return lunchToday;
	}
	public void setLunchToday(boolean lunchToday) {
		this.lunchToday = lunchToday;
	}
	public boolean isDinnerToday() {
		return dinnerToday;
	}
	public void setDinnerToday(boolean dinnerToday) {
		this.dinnerToday = dinnerToday;
	}
	public boolean isLunchTomorrow() {
		return lunchTomorrow;
	}
	public void setLunchTomorrow(boolean lunchTomorrow) {
		this.lunchTomorrow = lunchTomorrow;
	}
	public boolean isDinnerTomorrow() {
		return dinnerTomorrow;
	}
	public void setDinnerTomorrow(boolean dinnerTomorrow) {
		this.dinnerTomorrow = dinnerTomorrow;
	}
	public int getSlotId() {
		return slotId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	public int getKitchenId() {
		return kitchenId;
	}
	public void setKitchenId(int kitchenId) {
		this.kitchenId = kitchenId;
	}
	public String getBoyUSerId() {
		return boyUSerId;
	}
	public void setBoyUSerId(String boyUSerId) {
		this.boyUSerId = boyUSerId;
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
	
	
	
}
