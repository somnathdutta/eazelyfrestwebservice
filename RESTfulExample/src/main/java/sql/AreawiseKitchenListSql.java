package sql;

public class AreawiseKitchenListSql {

	public static final String fetchKitchenSql = "select kitchen_id, kitchen_name, mobile_no, address, image from  vw_kitchens_details "
												 + "where serving_areas LIKE ? and is_active = 'Y' ";
	
	
}
