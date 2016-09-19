package sql;

public class SubscriptionPrePackQuery {

	public static String packQuery = "Select * from vw_fapp_saved_packs" ;
	
	public static String packDetailsQuery = "Select * from vw_pack_details where subscription_pack_id = ?" ;
	
	public static String insertMasterPackQuery = "INSERT INTO fapp_prepack_orders( "
									            +" order_no, total_price, contact_number, "
									            +" email_id, pack_type, pack_day, meal_type, order_by, payment_name) "
									            +" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
	
	public static String insertItemsPackQuery = "INSERT INTO fapp_prepack_orders_details( "
								            +" order_id, cuisine_id, category_id, item_code, quantity, "
								            +" price, day_name, meal_type, time_slot, delivery_zone, delivery_address, "
								            +" delivery_pincode, instruction, name, email, contact_number,address_type) "
								            +" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
	
	public static String maxIdQuery = "SELECT MAX(order_id) FROM fapp_prepack_orders"; 
	
	public static String recentOrderQuery = " select fpo.order_no,fpo.total_price,fpo.contact_number,"
			+ " fpo.pack_type,fpo.pack_day,fpo.meal_type,"
			+ " fpo.order_by,fpo.order_date from fapp_prepack_orders fpo"
			+ " where fpo.order_id = ?";

	public static String itemDetailsQuery = "select fpod.quantity,fpod.price,fpod.day_name,fpod.meal_type,"
			+ " fpod.delivery_zone,fpod.delivery_address, fpod.delivery_pincode,fpod.instruction,fpod.time_slot,"
			+ " fpod.name,fpod.email,fpod.contact_number,fpod.item_code,fi.item_name,fc.category_name,fcu.cuisin_name "
			+ " from fapp_prepack_orders_details fpod"
			+ " left join food_items fi "
			+ " on fpod.item_code = fi.item_code"
			+ " left join food_category fc"
			+ " on fc.category_id = fi.category_id "
			+ " left join fapp_cuisins fcu"
			+ " on fcu.cuisin_id = fc.cuisine_id"
			+ " where fpod.order_id = ?";
	
	public static String multiAddressdetailsQuery = "select distinct day_name,delivery_zone,delivery_address,time_slot,"
			+ " delivery_pincode,instruction,name,contact_number,address_type from vw_order_pack_details where order_id = ? "
											 +" and day_name IN('SUNDAY','MONDAY')";
	
	public static String singleAddressdetailsQuery = "select distinct day_name,delivery_zone,delivery_address,time_slot,"
			+ " delivery_pincode,instruction,name,contact_number,address_type from vw_order_pack_details where order_id = ? "
			 +" and day_name='MONDAY' ";
	/*public static String multiAddressdetailsQuery = "select * from vw_order_pack_details where order_id = ? "
											 +" and day_name IN('SUNDAY','MONDAY')";*/
	
	/*public static String singleAddressdetailsQuery = "select * from vw_order_pack_details where order_id = ? "
			 +" and day_name='MONDAY' ";*/
}
