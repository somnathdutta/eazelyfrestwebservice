package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
