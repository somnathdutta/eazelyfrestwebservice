package pojo;

public class User {

	private String userName,password,emailId,contactNumber,referalCode,myCode,deliveryAddress;
	private double myBalance;
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	
	public User(String userName, String password, String emailId,
			String contactNumber) {
		super();
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.contactNumber = contactNumber;
	}


	public User(String userName, String password, String emailId,
			String contactNumber, String referalCode) {
		super();
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.contactNumber = contactNumber;
		this.referalCode = referalCode;
	}

	

	public User(String userName, String password, String emailId,
			String contactNumber, String referalCode, double myBalance) {
		super();
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.contactNumber = contactNumber;
		this.referalCode = referalCode;
		this.myBalance = myBalance;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getReferalCode() {
		return referalCode;
	}

	public void setReferalCode(String referalCode) {
		this.referalCode = referalCode;
	}

	public String getMyCode() {
		return myCode;
	}

	public void setMyCode(String myCode) {
		this.myCode = myCode;
	}

	public double getMyBalance() {
		return myBalance;
	}

	public void setMyBalance(double myBalance) {
		this.myBalance = myBalance;
	}


	public String getDeliveryAddress() {
		return deliveryAddress;
	}


	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	
}
