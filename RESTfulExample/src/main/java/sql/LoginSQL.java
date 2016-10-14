package sql;

public class LoginSQL {

	public static String loginQuery = "SELECT username,mobile_no,email FROM fapp_accounts WHERE mobile_no = ? AND password = ?";
	
	public static String loginKitchenQuery = "SELECT * FROM fapp_accounts WHERE username = ? AND password = ?";
}
