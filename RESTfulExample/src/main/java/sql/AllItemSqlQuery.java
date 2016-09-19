package sql;

public class AllItemSqlQuery {

	public static String allItemQuery = "select * from vw_food_item_details";
	
	public static String orderItemQuery =" select cuisin_name,category_name,qty,"
			+ " total_price,item_name,item_description from vw_order_item_details_list where order_no = ?";
}
