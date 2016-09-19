package sql;

public class ForgotPasswordSQL {

	public static String emailExistsQuery = "select email from fapp_accounts where email = ?";
}
