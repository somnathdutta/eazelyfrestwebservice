package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Biker;

import com.mkyong.rest.DBConnection;

public class BikerDAO {

	public static Biker getDriverDetailsFromKitchen(String orderNo, String kitchenName){
		Biker biker = new Biker();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select delivery_boy_name,delivery_boy_phn_number,driver_boy_user_id from vw_biker_from_kitchen  "
    						+ "where order_id=(select order_id from fapp_orders where order_no = ?) and kitchen_id = "
    						+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?)";
					/*String sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
								+" WHERE delivery_boy_user_id = ? ";*/
					try {
						preparedStatement = connection.prepareStatement(sql);
						/*preparedStatement.setString(1, userID);*/
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							biker.setBikerName(resultSet.getString("delivery_boy_name"));
							biker.setBikerContact(resultSet.getString("delivery_boy_phn_number")); 
							biker.setUserId(resultSet.getString("driver_boy_user_id"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("On notify biker details for message ::");
		System.out.println("Biker name: "+biker.getBikerName());
		System.out.println("Biker id: "+biker.getUserId());
		System.out.println("Biker phn: "+biker.getBikerContact());
		return biker;
	}
	
	public static Biker getDriverDetails(String userID){
		Biker biker = new Biker();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
					+" WHERE delivery_boy_user_id = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, userID);
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							biker.setBikerName(resultSet.getString("delivery_boy_name"));
							biker.setBikerContact(resultSet.getString("delivery_boy_phn_number")); 
							biker.setUserId(userID);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("On pickUp biker details for customer ::");
		System.out.println("Biker name: "+biker.getBikerName());
		System.out.println("Biker id: "+biker.getUserId());
		System.out.println("Biker phn: "+biker.getBikerContact());
		return biker;
	}
	
	public static JSONObject reachedKitchen(String boyUserId, String orderNo, String kitchenId) throws JSONException{
		JSONObject reachedJson = new JSONObject();
		boolean reached =false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking set driver_reached = 'Y', driver_arrival_time=current_timestamp"
							+ " where driver_boy_user_id = ? and kitchen_id = "
							+ " (select kitchen_id from fapp_kitchen where kitchen_name= ?) and order_id ="
							+ " (select order_id from fapp_orders where order_no = ? )";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						preparedStatement.setString(2, kitchenId);
						preparedStatement.setString(3, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							reached = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		if(reached){
			reachedJson.put("status", "200");
			reachedJson.put("message", "Biker reached");
			reachedJson.put("value", true);
		}else{
			reachedJson.put("status", "204");
			reachedJson.put("message", "Biker reached failed");
			reachedJson.put("value", false);
		}
		return reachedJson;
		
	}
}
