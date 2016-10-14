package pojo;

public class Biker {

	private String bikerName,userId,bikerContact,bikerPosition,lat,lng,pickUpTime,deliveryTime;
	private int id;
	public String getBikerName() {
		return bikerName;
	}
	public void setBikerName(String bikerName) {
		this.bikerName = bikerName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBikerContact() {
		return bikerContact;
	}
	public void setBikerContact(String bikerContact) {
		this.bikerContact = bikerContact;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBikerPosition() {
		return bikerPosition;
	}
	public void setBikerPosition(String bikerPosition) {
		this.bikerPosition = bikerPosition;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getPickUpTime() {
		return pickUpTime;
	}
	public void setPickUpTime(String pickUpTime) {
		this.pickUpTime = pickUpTime;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
}
