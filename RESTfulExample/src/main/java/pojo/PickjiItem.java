package pojo;


public class PickjiItem {
	public String cuisineName;
	
	public String categoryName;
	
	public String itemName;
	
	public Integer quantity;
	
	public Double price;
	
	

	public String toString() {
		// TODO Auto-generated method stub
		return categoryName+" "+itemName+" "+quantity+" "+" "+price;
	}



	public String getCuisineName() {
		return cuisineName;
	}



	public void setCuisineName(String cuisineName) {
		this.cuisineName = cuisineName;
	}



	public String getCategoryName() {
		return categoryName;
	}



	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}



	public String getItemName() {
		return itemName;
	}



	public void setItemName(String itemName) {
		this.itemName = itemName;
	}



	public Integer getQuantity() {
		return quantity;
	}



	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



	public Double getPrice() {
		return price;
	}



	public void setPrice(Double price) {
		this.price = price;
	}
}
