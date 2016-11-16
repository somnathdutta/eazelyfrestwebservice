package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;

import com.mkyong.rest.DBConnection;

public class ItemDAO {

	public static JSONArray fetchItemsWrtCategory(int categoryId,String pincode,Connection connection,
			String deliveryDay, String mobileNo, String area) throws JSONException{
		//JSONObject fetchAllCategory = new JSONObject();
		JSONArray jArray = new JSONArray();
		/*if( !(categoryId == 78 || categoryId == 79) ){*/
			//boolean isNewUser = FetchCuisineDAO.isNewUser(mobileNo);
			try {
				SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = null;
						/*String sql ="SELECT * FROM vw_category_from_kitchen_details WHERE area_id = "
								   +" (select area_id from sa_area where area_name ILIKE ? and city_id = "
			    				   +" (select city_id from sa_city where city_name ILIKE ?))";*/
						if(deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="SELECT distinct kitchen_cuisine_id,category_id,item_name,item_code,"
									+" item_price,item_description,item_image "
									+" FROM vw_kitchen_items "
									+" WHERE category_id=? and serving_areas LIKE ? and is_active='Y' "
									+" order by item_code"	;
						}else{
							sql ="SELECT distinct kitchen_cuisine_id,category_id,item_name,item_code,"
									+" item_price,item_description,item_image "
									+" FROM vw_kitchen_items "
									+" WHERE category_id=? and serving_areas LIKE ? and is_active_tomorrow='Y' "
									+" order by item_code"	;
						}
							
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, categoryId);
							preparedStatement.setString(2, "%"+area+"%");
							int serialNo = 0;
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject jobject =  new JSONObject();
								boolean isBikerAvailableForLunch = false,isBikerAvailableForDinner = false;
								String bikerAvailableKitchensForLunch,bikerAvailableKitchensForDinner,stock,dinnerStock;
								jobject.put("serial", serialNo);
								String itemCode = resultSet.getString("item_code");
								//do as usual show all items
								jobject.put("itemcode", itemCode);
								//jobject.put("singleOrders", resultSet.getInt("no_of_single_order"));
								jobject.put("cuisineid", resultSet.getString("kitchen_cuisine_id"));
								jobject.put("categoryid",resultSet.getString("category_id"));
								jobject.put("categorydescription", resultSet.getString("item_description") );
								String tempImage  = resultSet.getString("item_image");
								String categoryImage;
								if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
									categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}else if(tempImage.startsWith("http://")){
									categoryImage = tempImage.replace("http://", "");
								}else{
									categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}

								jobject.put("categoryimage", categoryImage);
								jobject.put("categoryname",resultSet.getString("item_name"));

								//isBikerAvailableForLunch = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, true);
								//isBikerAvailableForDinner = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, false);
								bikerAvailableKitchensForLunch = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, true, area);
								bikerAvailableKitchensForDinner = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, false, area);

								stock = StockUpdationDAO.getItemStock(pincode, itemCode, connection, "LUNCH", deliveryDay,bikerAvailableKitchensForLunch, area);
								dinnerStock = StockUpdationDAO.getItemStock(pincode, itemCode, connection, "DINNER", deliveryDay,bikerAvailableKitchensForDinner, area);

								jobject.put("stock", stock);
								jobject.put("lunchstock", stock);
								isBikerAvailableForLunch = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, true, area);
								jobject.put("availableBikerForLunch", isBikerAvailableForLunch);

								jobject.put("dinnerstock", dinnerStock);
								isBikerAvailableForDinner = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, false, area);
								jobject.put("availableBikerForDinner", isBikerAvailableForDinner);

								if(isBikerAvailableForLunch && isBikerAvailableForDinner){
									jobject.put("available",true);
								}else{
									jobject.put("available",false);
								}

								jobject.put("mealtype", DBConnection.getLunchOrDinner(pincode, itemCode,connection));
								jobject.put("categoryprice", resultSet.getDouble("item_price"));
								jArray.put(jobject);
								serialNo++;
							}
							
						} catch (Exception e) {
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
				e.printStackTrace();
			}
		/*}else{
			try {
				SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						
						String sql ="select distinct category_image from vw_alacarte_item_details_from_kitchen "
								+" where category_id = ? and serving_zipcodes LIKE ? "	;	
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, categoryId);
							preparedStatement.setString(2, "%"+pincode+"%");
							int serialNo = 0;
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject jobject =  new JSONObject();
								jobject.put("serial", serialNo);
								jobject.put("itemcode", "");
								jobject.put("cuisineid", "");
								jobject.put("categoryid","");
								jobject.put("categorydescription", "" );
								String tempImage  = resultSet.getString("category_image");
								String categoryImage;
								if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
									 categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}else if(tempImage.startsWith("http://")){
									categoryImage = tempImage.replace("http://", "");
								}else{
									 categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}
								
								jobject.put("categoryimage", categoryImage);
						    	jobject.put("categoryname","");
						    	
						    	boolean isBikerAvailableForLunch = false,isBikerAvailableForDinner = false;
						    	
						    	jobject.put("stock", 1);
						    	jobject.put("lunchstock", 1);
						    	jobject.put("availableBikerForLunch", true);
						    	
						    	jobject.put("dinnerstock", 1);
						    	jobject.put("availableBikerForDinner", true);
						    	jobject.put("available",true);
						    	
						    	
						    	jobject.put("mealtype", "BOTH");
						    	jobject.put("categoryprice", 0.0);
						    	jArray.put(jobject);
						    	serialNo++;
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}
						}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			 
		}*/
		
			
			System.out.println("Items size: "+jArray.length());
			//fetchAllCategory.put("Categories", jArray);
	    	return jArray;
	}
	
	public static String[] getItemDetails(String itemCode){
		String[] itemDetails = new String[3];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select cuisin_name,category_name,item_name from vw_food_item_details "
							+ " where item_code = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							itemDetails[0] = resultSet.getString("cuisin_name");
							itemDetails[1] = resultSet.getString("category_name");
							itemDetails[2] = resultSet.getString("item_name");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}if(connection!=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return itemDetails;
	}
	
	public static int getItemTypeId(String itemCode){
		int itemTypeId = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select item_type_id from vw_food_item_details where item_code = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							itemTypeId = resultSet.getInt("item_type_id");
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
		return itemTypeId;
	}
	
	public static int itemCurrentStock(int kitchenId, String itemCode, MealTypePojo mealTypePojo){
		int currStock = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select stock AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active='Y'";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select stock_tomorrow AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active_tomorrow='Y'";
					}else if (mealTypePojo.isDinnerToday()) {
						sql = "select dinner_stock AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active='Y'";
					}else{
						sql = "select dinner_stock_tomorrow AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active_tomorrow='Y'";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						preparedStatement.setInt(2, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							currStock = resultSet.getInt("stock");
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
		
		return currStock;
	}
}
