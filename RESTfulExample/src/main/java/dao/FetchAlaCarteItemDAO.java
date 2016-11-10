package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class FetchAlaCarteItemDAO {

	public static JSONObject fetchAlacarteItem(String pincode, String categoryId, String deliveryDay,
			String area) throws JSONException{
		JSONObject alacarteJSonObject = new JSONObject();
		JSONArray itemArray = fetchItems(pincode, Integer.valueOf(categoryId), deliveryDay, area);
		System.out.println("Item list size: "+itemArray.length());
		if(itemArray.length()>0){
			alacarteJSonObject.put("status", "200");
			alacarteJSonObject.put("message", "Alacarte Item found");
			alacarteJSonObject.put("itemList", itemArray);
		}else{
			alacarteJSonObject.put("status", "204");
			alacarteJSonObject.put("message", "Internal problem occured!");
			alacarteJSonObject.put("itemList", new JSONArray());
		}
		
		return alacarteJSonObject;
	}
	
	public static JSONArray fetchItems(String pincode, int categoryId, String deliveryDay,String area){
		JSONArray itemJSonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql =null;
					if(deliveryDay.equals("TODAY")){
						sql ="SELECT distinct cuisine_id,category_id,item_name,item_code,item_image,item_price,item_type_id,"
								+ " type_name "
								+ " FROM vw_alacarte_item_details_from_kitchen where category_id = ?"
								+ " and serving_zipcodes LIKE ? and is_active='Y'";
					}else{
						sql ="SELECT distinct cuisine_id,category_id,item_name,item_code,item_image,item_price,item_type_id,"
								+ " type_name "
								+ " FROM vw_alacarte_item_details_from_kitchen where category_id = ?"
								+ " and serving_zipcodes LIKE ? and is_active_tomorrow='Y'";
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, categoryId);
						preparedStatement.setString(2, "%"+pincode+"%");
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject alaItem = new JSONObject();
							alaItem.put("cuisineid", resultSet.getInt("cuisine_id"));
							alaItem.put("categoryid", resultSet.getInt("category_id"));
							alaItem.put("alaTypeId", resultSet.getInt("item_type_id"));
							alaItem.put("alaType", resultSet.getString("type_name"));
							String itemCode =  resultSet.getString("item_code");
							alaItem.put("itemCode", itemCode);
							alaItem.put("itemName", resultSet.getString("item_name"));
							String tempImage  = resultSet.getString("item_image");
							String alaImage;
							if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
								alaImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}else if(tempImage.startsWith("http://")){
								alaImage = tempImage.replace("http://", "");
							}else{
								alaImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}
							alaItem.put("itemImage", alaImage);
							String bikerAvailableKitchensForLunch = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, true, area);
					    	String bikerAvailableKitchensForDinner = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, false, area);
					    	
					    	String lunchStock = DBConnection.getItemStock(pincode, itemCode, connection, "LUNCH", deliveryDay,
					    			bikerAvailableKitchensForLunch,area);
					    	String dinnerStock = DBConnection.getItemStock(pincode, itemCode, connection, "DINNER", deliveryDay,
					    			bikerAvailableKitchensForDinner,area);
					    	
							//int lunchStock = 20;
							//int dinnerStock = 20;
							alaItem.put("lunchStock", lunchStock);
							alaItem.put("dinnerStock", dinnerStock);
							alaItem.put("availableBikerForLunch", true);
							alaItem.put("availableBikerForDinner", true);
							alaItem.put("itemPrice", resultSet.getDouble("item_price"));
							
							itemJSonArray.put(alaItem);
						}
					} catch (Exception e) {
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
		return itemJSonArray;
	}
}
