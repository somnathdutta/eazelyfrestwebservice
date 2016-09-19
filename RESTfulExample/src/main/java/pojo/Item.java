package pojo;

public class Item {

	public String itemName,itemCode,itemDescription,cusineName,categoryName;
	public int itemId,cuisineId,categoryId;
	public Double itemPrice;
	public Item() {
		// TODO Auto-generated constructor stub
	}
	public Item(String itemName, String itemCode, String itemDescription,
			int itemId, Double itemPrice) {
		super();
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.itemId = itemId;
		this.itemPrice = itemPrice;
	}
	
	

}
