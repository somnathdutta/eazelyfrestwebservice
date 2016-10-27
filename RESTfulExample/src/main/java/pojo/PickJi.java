package pojo;

public class PickJi {

	private String appToken;
	private String orderTitle;
	private String pickupAddress;
	private String pickupArea;
	private String pickupPincode;
	private String pickupFromName;
	private String pickupMobileNo;
	
	private String deliveryAddress;
	private String deliveryArea;
	private String deliveryPincode;
	private String deliveryToName;
	private String deliveryMobileNo;
	
	private String cashToCollect;
	private String schedulePickupTime;
	private String scheduleDeliveryTime;
	private String quantityDetails;
	private String hasPillon;
	
	
	public PickJi() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PickJi(String appToken, String orderTitle, String pickupAddress,
			String pickupArea, String pickupPincode, String pickupFromName,
			String pickupMobileNo, String deliveryAddress, String deliveryArea,
			String deliveryPincode, String deliveryToName,
			String deliveryMobileNo, String cashToCollect,
			String schedulePickupTime, String scheduleDeliveryTime,
			String quantityDetails, String hasPillon) {
		super();
		this.appToken = appToken;
		this.orderTitle = orderTitle;
		this.pickupAddress = pickupAddress;
		this.pickupArea = pickupArea;
		this.pickupPincode = pickupPincode;
		this.pickupFromName = pickupFromName;
		this.pickupMobileNo = pickupMobileNo;
		this.deliveryAddress = deliveryAddress;
		this.deliveryArea = deliveryArea;
		this.deliveryPincode = deliveryPincode;
		this.deliveryToName = deliveryToName;
		this.deliveryMobileNo = deliveryMobileNo;
		this.cashToCollect = cashToCollect;
		this.schedulePickupTime = schedulePickupTime;
		this.scheduleDeliveryTime = scheduleDeliveryTime;
		this.quantityDetails = quantityDetails;
		this.hasPillon = hasPillon;
	}
	public String getAppToken() {
		return appToken;
	}
	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}
	public String getOrderTitle() {
		return orderTitle;
	}
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}
	public String getPickupAddress() {
		return pickupAddress;
	}
	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}
	public String getPickupArea() {
		return pickupArea;
	}
	public void setPickupArea(String pickupArea) {
		this.pickupArea = pickupArea;
	}
	public String getPickupPincode() {
		return pickupPincode;
	}
	public void setPickupPincode(String pickupPincode) {
		this.pickupPincode = pickupPincode;
	}
	public String getPickupFromName() {
		return pickupFromName;
	}
	public void setPickupFromName(String pickupFromName) {
		this.pickupFromName = pickupFromName;
	}
	public String getPickupMobileNo() {
		return pickupMobileNo;
	}
	public void setPickupMobileNo(String pickupMobileNo) {
		this.pickupMobileNo = pickupMobileNo;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getDeliveryArea() {
		return deliveryArea;
	}
	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}
	public String getDeliveryPincode() {
		return deliveryPincode;
	}
	public void setDeliveryPincode(String deliveryPincode) {
		this.deliveryPincode = deliveryPincode;
	}
	public String getDeliveryToName() {
		return deliveryToName;
	}
	public void setDeliveryToName(String deliveryToName) {
		this.deliveryToName = deliveryToName;
	}
	public String getDeliveryMobileNo() {
		return deliveryMobileNo;
	}
	public void setDeliveryMobileNo(String deliveryMobileNo) {
		this.deliveryMobileNo = deliveryMobileNo;
	}
	public String getCashToCollect() {
		return cashToCollect;
	}
	public void setCashToCollect(String cashToCollect) {
		this.cashToCollect = cashToCollect;
	}
	public String getSchedulePickupTime() {
		return schedulePickupTime;
	}
	public void setSchedulePickupTime(String schedulePickupTime) {
		this.schedulePickupTime = schedulePickupTime;
	}
	public String getScheduleDeliveryTime() {
		return scheduleDeliveryTime;
	}
	public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
		this.scheduleDeliveryTime = scheduleDeliveryTime;
	}
	public String getQuantityDetails() {
		return quantityDetails;
	}
	public void setQuantityDetails(String quantityDetails) {
		this.quantityDetails = quantityDetails;
	}
	public String getHasPillon() {
		return hasPillon;
	}
	public void setHasPillon(String hasPillon) {
		this.hasPillon = hasPillon;
	}
}
