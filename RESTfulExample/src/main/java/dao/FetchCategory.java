package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class FetchCategory {

	/**
	 * This method will returns the categories from the cisine and serving area kitchen
	 * @param CuisineID
	 * @param pincode
	 * @param connection
	 * @param deliveryDay
	 * @param mobileNo
	 * @param area
	 * @return json array for category list
	 */
	public static JSONArray fetchCategoriesOfCuisineWithPincode(int CuisineID, String pincode, Connection connection,
			String deliveryDay, String mobileNo, String area){
		JSONArray categoryJSONArray = new JSONArray();
		try {
			SQL:{
			//Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen "
					+ " where kitchen_cuisine_id = ? and serving_areas like ? order by category_id ";
			/*String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen "
	    						+ " where kitchen_cuisine_id = ? and serving_zipcodes like ? order by category_id ";*/
			/*String sql = "select category_id,category_name from food_category "
	    						+ " where category_price IS NULL AND cuisine_id = ?";*/
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, CuisineID);
				preparedStatement.setString(2, "%"+area+"%");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject categoryObject = new JSONObject();
					String categoryId = resultSet.getString("category_id");
					categoryObject.put("categoryid", categoryId);
					categoryObject.put("categoryname", resultSet.getString("category_name"));
					String mealTypeLunch = resultSet.getString("is_lunch");
					String mealTypeDinner = resultSet.getString("is_dinner");
					if(mealTypeLunch.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "LUNCH");
					}

					if(mealTypeDinner.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "DINNER");
					}

					if(mealTypeLunch.equalsIgnoreCase("Y") && mealTypeDinner.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "BOTH");
					}
					categoryObject.put("itemlist", ItemDAO.fetchItemsWrtCategory( Integer.valueOf(categoryId), pincode, connection,deliveryDay, mobileNo, area));
					categoryJSONArray.put(categoryObject);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				if(preparedStatement!=null){
					preparedStatement.close();
				}
				/*if(connection!=null){
								connection.close();
							}*/
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("kitchen categories ends");

		return categoryJSONArray;
	}

	/**
	 * This method will returns the categories from the cisine and serving area kitchen
	 * @param CuisineID
	 * @param pincode
	 * @param connection
	 * @param deliveryDay
	 * @param mobileNo
	 * @param area
	 * @return json array for category list
	 */
	public static JSONArray fetchCategoriesOfAllCuisineWithPincode(String pincode,Connection connection,
			String deliveryDay,String mobileNo, String area){
		JSONArray categoryJSONArray = new JSONArray();
		try {
			SQL:{
			//Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen order by category_id ";

			/*String sql = "select category_id,category_name from food_category "
	    						+ " where category_price IS NULL order by category_id";*/
			try {
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject categoryObject = new JSONObject();
					String categoryId = resultSet.getString("category_id");
					categoryObject.put("categoryid", categoryId);
					categoryObject.put("categoryname", resultSet.getString("category_name"));
					String mealTypeLunch = resultSet.getString("is_lunch");
					String mealTypeDinner = resultSet.getString("is_dinner");
					if(mealTypeLunch.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "LUNCH");
					}

					if(mealTypeDinner.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "DINNER");
					}

					if(mealTypeLunch.equalsIgnoreCase("Y") && mealTypeDinner.equalsIgnoreCase("Y")){
						categoryObject.put("mealtype", "BOTH");
					}
					categoryObject.put("itemlist", ItemDAO.fetchItemsWrtCategory( Integer.valueOf(categoryId), pincode ,connection, deliveryDay, 
							mobileNo, area));
					categoryJSONArray.put(categoryObject);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				if(preparedStatement!=null){
					preparedStatement.close();
				}
				/*if(connection!=null){
								connection.close();
							}*/
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return categoryJSONArray;
	}
}
