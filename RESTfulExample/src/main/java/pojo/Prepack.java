package pojo;

public class Prepack {

	private String packType,mealType,user,paymentName,packDay,orderNo,userName,orderDate;
	
	private double packPrice;
	/**
	 * Lunch details
	 */
	private String lunchContactNumber,lunchName,lunchMailid,
					lunchTimeSlot,lunchDeliveryZone,
					lunchDeliveryAddress,lunchInstruction,lunchPincode,lunchAddressType;
	/**
	 * Dinner details
	 */
	private String dinnerContactNumber,dinnerName,dinnerMailid,
					dinnerTimeSlot,dinnerDeliveryZone,
					dinnerDeliveryAddress,dinnerInstruction,dinnerPincode,dinnerAddressType;
	/**
	 * Same details
	 */
	private String sameContactNumber,sameName,sameMailid,
					sameTimeSlot,sameDeliveryZone,
					sameDeliveryAddress,sameInstruction,samePincode,sameAddressType;
	
	
	
	public Prepack() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Lunch dinner both
	 * @param packType
	 * @param mealType
	 * @param packPrice
	 * @param lunchContactNumber
	 * @param lunchName
	 * @param lunchMailid
	 * @param lunchTimeSlot
	 * @param lunchDeliveryZone
	 * @param lunchDeliveryAddress
	 * @param lunchInstruction
	 * @param lunchPincode
	 * @param dinnerContactNumber
	 * @param dinnerName
	 * @param dinnerMailid
	 * @param dinnerTimeSlot
	 * @param dinnerDeliveryZone
	 * @param dinnerDeliveryAddress
	 * @param dinnerInstruction
	 * @param dinnerPincode
	 */
	public Prepack(String user,String paymentName,String packType, String packDay,String mealType, double packPrice,
			String lunchContactNumber, String lunchName, String lunchMailid,
			String lunchTimeSlot, String lunchDeliveryZone,
			String lunchDeliveryAddress, String lunchInstruction,
			String lunchPincode, String lunchAddressType ,
			String dinnerContactNumber, String dinnerName,
			String dinnerMailid, String dinnerTimeSlot,
			String dinnerDeliveryZone, String dinnerDeliveryAddress,
			String dinnerInstruction, String dinnerPincode,String dinnerAddressType) {
		super();
		this.user = user;
		this.paymentName = paymentName;
		this.packType = packType;
		this.packDay = packDay;
		this.mealType = mealType;
		this.packPrice = packPrice;
		this.lunchContactNumber = lunchContactNumber;
		this.lunchName = lunchName;
		this.lunchMailid = lunchMailid;
		this.lunchTimeSlot = lunchTimeSlot;
		this.lunchDeliveryZone = lunchDeliveryZone;
		this.lunchDeliveryAddress = lunchDeliveryAddress;
		this.lunchInstruction = lunchInstruction;
		this.lunchPincode = lunchPincode;
		this.lunchAddressType = lunchAddressType;
		this.dinnerContactNumber = dinnerContactNumber;
		this.dinnerName = dinnerName;
		this.dinnerMailid = dinnerMailid;
		this.dinnerTimeSlot = dinnerTimeSlot;
		this.dinnerDeliveryZone = dinnerDeliveryZone;
		this.dinnerDeliveryAddress = dinnerDeliveryAddress;
		this.dinnerInstruction = dinnerInstruction;
		this.dinnerPincode = dinnerPincode;
		this.dinnerAddressType = dinnerAddressType;
	}

	
	/**
	 * ONLY LUNCH SINGLE ADDRESS
	 * @param packType
	 * @param mealType
	 * @param packPrice
	 * @param lunchContactNumber
	 * @param lunchName
	 * @param lunchMailid
	 * @param lunchTimeSlot
	 * @param lunchDeliveryZone
	 * @param lunchDeliveryAddress
	 * @param lunchInstruction
	 * @param lunchPincode
	 */
	public Prepack(String user, String paymentName, String packType, String packDay, String mealType, double packPrice,
			String lunchContactNumber, String lunchName, String lunchMailid,
			String lunchTimeSlot, String lunchDeliveryZone,
			String lunchDeliveryAddress, String lunchInstruction,
			String lunchPincode,String lunchAddressType) {
		super();
		this.user = user;
		this.paymentName = paymentName;
		this.packType = packType;
		this.packDay = packDay;
		this.mealType = mealType;
		this.packPrice = packPrice;
		this.sameContactNumber = lunchContactNumber;
		this.sameName = lunchName;
		this.sameMailid = lunchMailid;
		this.sameTimeSlot = lunchTimeSlot;
		this.sameDeliveryZone = lunchDeliveryZone;
		this.sameDeliveryAddress = lunchDeliveryAddress;
		this.sameInstruction = lunchInstruction;
		this.samePincode = lunchPincode;
		this.sameAddressType = lunchAddressType;
	}


	


	public String getPackType() {
		return packType;
	}

	public void setPackType(String packType) {
		this.packType = packType;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public double getPackPrice() {
		return packPrice;
	}

	public void setPackPrice(double packPrice) {
		this.packPrice = packPrice;
	}

	public String getLunchContactNumber() {
		return lunchContactNumber;
	}

	public void setLunchContactNumber(String lunchContactNumber) {
		this.lunchContactNumber = lunchContactNumber;
	}

	public String getLunchName() {
		return lunchName;
	}

	public void setLunchName(String lunchName) {
		this.lunchName = lunchName;
	}

	public String getLunchMailid() {
		return lunchMailid;
	}

	public void setLunchMailid(String lunchMailid) {
		this.lunchMailid = lunchMailid;
	}

	public String getLunchTimeSlot() {
		return lunchTimeSlot;
	}

	public void setLunchTimeSlot(String lunchTimeSlot) {
		this.lunchTimeSlot = lunchTimeSlot;
	}

	public String getLunchDeliveryZone() {
		return lunchDeliveryZone;
	}

	public void setLunchDeliveryZone(String lunchDeliveryZone) {
		this.lunchDeliveryZone = lunchDeliveryZone;
	}

	public String getLunchDeliveryAddress() {
		return lunchDeliveryAddress;
	}

	public void setLunchDeliveryAddress(String lunchDeliveryAddress) {
		this.lunchDeliveryAddress = lunchDeliveryAddress;
	}

	public String getLunchInstruction() {
		return lunchInstruction;
	}

	public void setLunchInstruction(String lunchInstruction) {
		this.lunchInstruction = lunchInstruction;
	}

	public String getLunchPincode() {
		return lunchPincode;
	}

	public void setLunchPincode(String lunchPincode) {
		this.lunchPincode = lunchPincode;
	}

	public String getDinnerContactNumber() {
		return dinnerContactNumber;
	}

	public void setDinnerContactNumber(String dinnerContactNumber) {
		this.dinnerContactNumber = dinnerContactNumber;
	}

	public String getDinnerName() {
		return dinnerName;
	}

	public void setDinnerName(String dinnerName) {
		this.dinnerName = dinnerName;
	}

	public String getDinnerMailid() {
		return dinnerMailid;
	}

	public void setDinnerMailid(String dinnerMailid) {
		this.dinnerMailid = dinnerMailid;
	}

	public String getDinnerTimeSlot() {
		return dinnerTimeSlot;
	}

	public void setDinnerTimeSlot(String dinnerTimeSlot) {
		this.dinnerTimeSlot = dinnerTimeSlot;
	}

	public String getDinnerDeliveryZone() {
		return dinnerDeliveryZone;
	}

	public void setDinnerDeliveryZone(String dinnerDeliveryZone) {
		this.dinnerDeliveryZone = dinnerDeliveryZone;
	}

	public String getDinnerDeliveryAddress() {
		return dinnerDeliveryAddress;
	}

	public void setDinnerDeliveryAddress(String dinnerDeliveryAddress) {
		this.dinnerDeliveryAddress = dinnerDeliveryAddress;
	}

	public String getDinnerInstruction() {
		return dinnerInstruction;
	}

	public void setDinnerInstruction(String dinnerInstruction) {
		this.dinnerInstruction = dinnerInstruction;
	}

	public String getDinnerPincode() {
		return dinnerPincode;
	}

	public void setDinnerPincode(String dinnerPincode) {
		this.dinnerPincode = dinnerPincode;
	}

	public String getSameContactNumber() {
		return sameContactNumber;
	}

	public void setSameContactNumber(String sameContactNumber) {
		this.sameContactNumber = sameContactNumber;
	}

	public String getSameName() {
		return sameName;
	}

	public void setSameName(String sameName) {
		this.sameName = sameName;
	}

	public String getSameMailid() {
		return sameMailid;
	}

	public void setSameMailid(String sameMailid) {
		this.sameMailid = sameMailid;
	}

	public String getSameTimeSlot() {
		return sameTimeSlot;
	}

	public void setSameTimeSlot(String sameTimeSlot) {
		this.sameTimeSlot = sameTimeSlot;
	}

	public String getSameDeliveryZone() {
		return sameDeliveryZone;
	}

	public void setSameDeliveryZone(String sameDeliveryZone) {
		this.sameDeliveryZone = sameDeliveryZone;
	}

	public String getSameDeliveryAddress() {
		return sameDeliveryAddress;
	}

	public void setSameDeliveryAddress(String sameDeliveryAddress) {
		this.sameDeliveryAddress = sameDeliveryAddress;
	}

	public String getSameInstruction() {
		return sameInstruction;
	}

	public void setSameInstruction(String sameInstruction) {
		this.sameInstruction = sameInstruction;
	}

	public String getSamePincode() {
		return samePincode;
	}

	public void setSamePincode(String samePincode) {
		this.samePincode = samePincode;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPaymentName() {
		return paymentName;
	}


	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}


	public String getPackDay() {
		return packDay;
	}


	public void setPackDay(String packDay) {
		this.packDay = packDay;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getOrderDate() {
		return orderDate;
	}


	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}


	public String getLunchAddressType() {
		return lunchAddressType;
	}


	public void setLunchAddressType(String lunchAddressType) {
		this.lunchAddressType = lunchAddressType;
	}


	public String getDinnerAddressType() {
		return dinnerAddressType;
	}


	public void setDinnerAddressType(String dinnerAddressType) {
		this.dinnerAddressType = dinnerAddressType;
	}


	public String getSameAddressType() {
		return sameAddressType;
	}


	public void setSameAddressType(String sameAddressType) {
		this.sameAddressType = sameAddressType;
	}

	
	
}
