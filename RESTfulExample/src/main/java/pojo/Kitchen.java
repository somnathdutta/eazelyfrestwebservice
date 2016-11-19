package pojo;

public class Kitchen {

	private int kitchenId,singleOrder,singleOrderLunch,singleOrderDinner,itemStock;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+kitchenId+" "+itemStock+")";
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
	
	
}
