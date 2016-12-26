package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import sql.KitchenCategorySql;
import utility.PreparedStatementGenerator;

import com.mkyong.rest.DBConnection;

public class FetchKitchenItemsDao {

	/**
	 * Fetch kitchen categories 
	 * @param kitchenId
	 * @return
	 */
	public static JSONObject getKitchenItems(int kitchenId){
		JSONObject kitchenItemJson = new JSONObject();
		try {
			if(kitchenId > 0){
				JSONArray kitchenCategoriesArray = fetchKitchenCategories(kitchenId);
				if(kitchenCategoriesArray.length() > 0){
					kitchenItemJson.put("status", "200");
					kitchenItemJson.put("message", "Data found!");
					kitchenItemJson.put("categoryList", kitchenCategoriesArray);
				}else{
					kitchenItemJson.put("status", "204");
					kitchenItemJson.put("message", "No data found!");
					kitchenItemJson.put("categoryList", new JSONArray());
				}
			}else{
				kitchenItemJson.put("status", "204");
				kitchenItemJson.put("message", "Kitchen Id required");
				kitchenItemJson.put("categoryList", new JSONArray());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("------------ fetch-kitchen-items api ended--------------");
		return kitchenItemJson ;
	}
	
	/**
	 * Load all categories of kitchen
	 * @param kitchenId
	 * @return
	 */
	public static JSONArray fetchKitchenCategories(int kitchenId){
		JSONArray kitchenCategoryJsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = PreparedStatementGenerator.createQuery(connection, KitchenCategorySql.kitchenCategorySql, 
								Arrays.asList(kitchenId));
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject kitchenCategory = new JSONObject();
							kitchenCategory.put("cuisineId", resultSet.getInt("kitchen_cuisine_id"));
							kitchenCategory.put("cuisineName", resultSet.getString("cuisin_name"));
							int categoryId = resultSet.getInt("category_id");
							kitchenCategory.put("categoryId", categoryId );
							kitchenCategory.put("categoryName", resultSet.getString("category_name"));
							kitchenCategory.put("itemList", fetchCategoryItems(categoryId, kitchenId));
							kitchenCategoryJsonArray.put(kitchenCategory);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(connection!=null){
							connection.close();
						}
					}
					
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenCategoryJsonArray;
	}
	
	/**
	 * Fetch items wrt to kitchen and category
	 * @param categoryId
	 * @param kitchenId
	 * @return
	 */
	public static JSONArray fetchCategoryItems(int categoryId, int kitchenId){
		JSONArray kitchenItemsJsonArray = new JSONArray();
		Connection connection = null;
		try {
			SQL:{
					connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = PreparedStatementGenerator.createQuery(connection, KitchenCategorySql.kitchenCategoryItemSql, 
								Arrays.asList(kitchenId,categoryId));
						resultSet = preparedStatement.executeQuery();
						int serialNo = 0;
						while (resultSet.next()) {
							JSONObject kitchenItem = new JSONObject();
							kitchenItem.put("serial", serialNo);
							kitchenItem.put("itemCode", resultSet.getString("item_code"));
							kitchenItem.put("itemName", resultSet.getString("item_name"));
							kitchenItem.put("itemDescription", resultSet.getString("item_description"));
							kitchenItem.put("itemPrice", resultSet.getDouble("item_price"));
							String tempImage  = resultSet.getString("item_image");
							String itemImage = null;
							if(tempImage.startsWith("http://")){
								itemImage = tempImage.replace("http://", "");
							}
							kitchenItem.put("itemImage", itemImage);
							kitchenItem.put("stock", 20);
							serialNo++;
							kitchenItemsJsonArray.put(kitchenItem);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(connection!=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenItemsJsonArray;
	}
}
