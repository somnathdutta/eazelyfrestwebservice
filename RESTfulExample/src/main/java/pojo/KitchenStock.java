package pojo;

public class KitchenStock implements Comparable<KitchenStock> {

	public Integer kitchenId,stock;
	private String itemCode;
	
	public KitchenStock(Integer kitchenId, Integer stock) {
		super();
		this.kitchenId = kitchenId;
		this.stock = stock;
	}

	
	public KitchenStock() {
		// TODO Auto-generated constructor stub
	}


	public Integer getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Integer kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@Override
	public int compareTo(KitchenStock another) {
		// TODO Auto-generated method stub
		int out =this.kitchenId - another.kitchenId;
		System.out.println("returning: "+out);
		return this.kitchenId - another.kitchenId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return kitchenId.toString()+"\t"+stock.toString();
	}


	public String getItemCode() {
		return itemCode;
	}


	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}


	
}
