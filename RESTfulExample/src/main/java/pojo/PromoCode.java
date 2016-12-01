package pojo;

public class PromoCode {

	private String promoCode;
	private int typeId;
	private double promoValue,userValue;
	private String  isreusable;
	private String is_active;
	public String getPromoCode() {
		return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public double getPromoValue() {
		return promoValue;
	}
	public void setPromoValue(double promoValue) {
		this.promoValue = promoValue;
	}
	public double getUserValue() {
		return userValue;
	}
	public void setUserValue(double userValue) {
		this.userValue = userValue;
	}
	public String getIsreusable() {
		return isreusable;
	}
	public void setIsreusable(String isreusable) {
		this.isreusable = isreusable;
	}
	public String getIs_active() {
		return is_active;
	}
	public void setIs_active(String is_active) {
		this.is_active = is_active;
	}
	
	
}
