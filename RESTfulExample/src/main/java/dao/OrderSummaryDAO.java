package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class OrderSummaryDAO {

	public static JSONObject kitchenOrderSummary(String kitchenName) throws JSONException{
		JSONObject orderSummaryJson = new JSONObject();
		int fishCount=0,eggCount=0,muttonCount=0,chickenCount=0;
		try {
			Connection connection = DBConnection.createConnection();
			SQLFISH:{
					
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select COUNT(ITEM_CODE)AS fish from vw_order_items_of_kitchen "
							+" where order_status_id != 7 and kitchen_name= ? "
							+" AND item_code IN ('1','5','9','12','20','24','26','27') and delivery_date = current_date;";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							fishCount = resultSet.getInt("fish");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}	
				}
			
			SQLEGG:{
					
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select COUNT(ITEM_CODE)AS egg from vw_order_items_of_kitchen "
							+" where order_status_id != 7 and kitchen_name= ? "
							+" AND item_code IN ('2','6','10','13') and delivery_date = current_date;";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							eggCount = resultSet.getInt("egg");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}	
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		orderSummaryJson.put("status", "200");
		orderSummaryJson.put("fish","");
		orderSummaryJson.put("mutton", "");
		orderSummaryJson.put("egg", "");
		orderSummaryJson.put("chicken", "");
		
		return orderSummaryJson;
	}
	
	public static JSONObject fetchOrderSummary(String kitchenName,String deliveryDay,String mealType) throws JSONException{
		JSONObject orderSummaryJson = new JSONObject();
		java.util.Date deliveryDate = new java.util.Date();
		deliveryDate = DBConnection.getDeliveryDate(deliveryDay);
		JSONArray orderSummaryArray = new JSONArray(); 
		
		try {
				
			SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql ;
						sql = "select distinct item_name,item_code"
								+ " from vw_order_items_of_kitchen "
								+ " where  kitchen_name = ? and delivery_date= ? and meal_type = ?";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, kitchenName);
							preparedStatement.setDate(2, new java.sql.Date(deliveryDate.getTime()));
							preparedStatement.setString(3, mealType.toUpperCase());
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject orderItem = new JSONObject();
								String itemCode = resultSet.getString("item_code");
								String qnty = "";
								if(itemCode!=null){
									orderItem.put("itemName", resultSet.getString("item_name"));
									qnty = getItemQuanity(kitchenName,deliveryDate,itemCode, mealType);
									if(qnty!=null){
										orderItem.put("quantity", getItemQuanity(kitchenName,deliveryDate,itemCode, mealType));
									}else{
										orderItem.put("quantity", "0");
									}
								}else{
									orderItem.put("itemName", "");
									orderItem.put("quantity", "0");
								}
								orderSummaryArray.put(orderItem);
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
		System.out.println("Order summary list size: "+orderSummaryArray.length());
		if(orderSummaryArray.length()>0){
			orderSummaryJson.put("status", "200");
			orderSummaryJson.put("message", "Order details");
			orderSummaryJson.put("itemList", orderSummaryArray);
		}else{
			orderSummaryJson.put("status", "204");
			orderSummaryJson.put("message", "No Order details found!");
			orderSummaryJson.put("itemList", orderSummaryArray);
		}
		
		return orderSummaryJson;
	}
	
	public static String getItemQuanity(String kitchenName, Date deliveryDate, String itemCode, String mealType){
		String itemQuantity = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql= "select SUM(qty)As no_of_item from "
							+ " vw_order_items_of_kitchen where  "
							+ " kitchen_name = ? and delivery_date=? and item_code=? and meal_type = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						preparedStatement.setDate(2, new java.sql.Date(deliveryDate.getTime()));
						preparedStatement.setString(3, itemCode);
						preparedStatement.setString(4, mealType.toUpperCase());
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							itemQuantity = resultSet.getString("no_of_item");
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
		return itemQuantity;
	}
	
	
}
