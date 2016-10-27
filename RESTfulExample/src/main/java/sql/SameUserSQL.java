package sql;

public class SameUserSQL {

	/*public static String sameUserQuery = "select distinct kitchen_id "
			+" from vw_last_order_user where item_code in itemcodes and contact_number = ? "
			+" and delivery_address = ? ";*/
	
	public static String sameUserQuery = "select distinct kitchen_id "
			+" from vw_last_order_user where contact_number = ? "
			+" and delivery_address = ? and cuisine_id in cuisineids ";
	
	public static String kitchenStockQuery = "select distinct stock from fapp_kitchen_items where kitchen_id =? ";
}
