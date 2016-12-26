package sql;

public class KitchenCategorySql {

	public static final String kitchenCategorySql = "select kitchen_cuisine_id,cuisin_name,category_id,category_name "
						+" from vw_category_kitchen "
						+" where kitchen_id=? order by category_id";
	
	public static final String kitchenCategoryItemSql = "SELECT distinct item_name,item_code, "
									 +" item_price,item_description,item_image "
									 +" FROM vw_kitchen_items "
									 +" WHERE kitchen_id=? and category_id= ? "
									 +" order by item_code";
}
