package sql;

public class UserQuery {

	public static String userSqlQuery = "select username,email,my_balance,my_code,ref_code from fapp_accounts"
			+ " where mobile_no =?";
	
	public static String userEmailSqlQuery = "select fo.order_by as username,fo.user_mail_id as email,fo.contact_number as mobile_no,"
			+ " foud.delivery_address "
			+ " from fapp_orders fo"
			+ " left join fapp_order_user_details foud "
			+ " on fo.order_id = foud.order_id"
			+ " where order_no =?";
	public static String orderQuery = "select order_date ::date from fapp_orders where order_no=?";
}
