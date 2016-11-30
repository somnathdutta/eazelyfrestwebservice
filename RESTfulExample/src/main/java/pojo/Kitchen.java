package pojo;


public class Kitchen implements Comparable<Kitchen>{

	private int kitchenId,singleOrder,singleOrderLunch,singleOrderDinner,itemStock,userItemQuantity,
				dbQty,freeQty,slotID,slotCapacity,totalItemStock;
	private String itemCode;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+kitchenId+" "+itemStock+" "+userItemQuantity+")";
	}
	
	public int getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(int kitchenId) {
		this.kitchenId = kitchenId;
	}

	
	public int getSingleOrder() {
		return singleOrder;
	}

	public void setSingleOrder(int singleOrder) {
		this.singleOrder = singleOrder;
	}

	public int getSingleOrderLunch() {
		return singleOrderLunch;
	}

	public void setSingleOrderLunch(int singleOrderLunch) {
		this.singleOrderLunch = singleOrderLunch;
	}

	public int getSingleOrderDinner() {
		return singleOrderDinner;
	}

	public void setSingleOrderDinner(int singleOrderDinner) {
		this.singleOrderDinner = singleOrderDinner;
	}

	public int getItemStock() {
		return itemStock;
	}

	public void setItemStock(int itemStock) {
		this.itemStock = itemStock;
	}
	
	@Override
	public int compareTo(Kitchen other) {
		// TODO Auto-generated method stub
		return other.itemStock - this.itemStock;
	}

	public int getUserItemQuantity() {
		return userItemQuantity;
	}

	public void setUserItemQuantity(int userItemQuantity) {
		this.userItemQuantity = userItemQuantity;
	}

	public int getTotalItemStock() {
		return totalItemStock;
	}

	public void setTotalItemStock(int totalItemStock) {
		this.totalItemStock = totalItemStock;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public int getDbQty() {
		return dbQty;
	}

	public void setDbQty(int dbQty) {
		this.dbQty = dbQty;
	}

	public int getFreeQty() {
		return freeQty;
	}

	public void setFreeQty(int freeQty) {
		this.freeQty = freeQty;
	}

	public int getSlotID() {
		return slotID;
	}

	public void setSlotID(int slotID) {
		this.slotID = slotID;
	}

	public int getSlotCapacity() {
		return slotCapacity;
	}

	public void setSlotCapacity(int slotCapacity) {
		this.slotCapacity = slotCapacity;
	}

}
