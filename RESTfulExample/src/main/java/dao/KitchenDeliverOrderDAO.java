package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Biker;

import com.mkyong.rest.DBConnection;

public class KitchenDeliverOrderDAO {

	public static JSONObject deliveryToBoy(String orderNo , String kitchenName, String boyID)throws Exception{
		JSONObject deliveryBoyJsonObj = new JSONObject();
		if(isDeliveredToBoy(orderNo, kitchenName)){
			deliveryBoyJsonObj.put("status", true);
			/*Biker pickJibiker = isOrderDeliverByPickJiBoy(orderNo, kitchenName);//If order will be delivered by pickji
			if( pickJibiker != null){
				Biker updatedBiker = callPickJiForStatus(pickJibiker.getUserId());// Get pickji pickup time
				if(updatedBiker!=null){
					savePickJiBikerPickUp(updatedBiker);// save pick up time from pickji
				}else{
					System.out.println("API DATA PROBLEM!");
				}
				String customerContact = DBConnection.getCustomerMobile(orderNo, "REGULAR");//getting customer mobile no.
				//Send cutomer message about biker details
				SendMessageDAO.sendMessageToCustomerForPickJiBiker(customerContact, pickJibiker.getBikerContact(), pickJibiker.getBikerName()
						, pickJibiker.getUserId());
				 PickjiCall myRunnable = new PickjiCall( kitchenName, orderNo, pickJibiker.getUserId());
			     Thread t = new Thread(myRunnable);
			     t.start();
			    
			}*/
		}else{
			deliveryBoyJsonObj.put("status", false);
		}
		System.out.println(deliveryBoyJsonObj);
		return deliveryBoyJsonObj;
	}
	
	public static boolean isDeliveredToBoy(String orderNo , String kitchenName ){
		boolean isOrderGiven=false;
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET delivered_to_boy = 'Y',delivery_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				try {
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					isOrderGiven =  true;
					System.out.println("1. Order given to boy!!");
				}
				} catch (Exception e) {
					System.out.println(e);
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
		return isOrderGiven;
	}

	
	/**
	 * Returns Biker object if pickup time updated from pickji
	 * @param pickJiOrderId
	 * @return
	 * @throws JSONException
	 */
	public static Biker callPickJiForStatus(String pickJiOrderId) throws JSONException{
		JSONObject pickJiBikerJson = CallPickJiBikerDAO.getBikerDetailsFromPickJiAPI(pickJiOrderId);
		Biker pickjiBiker = null;
		String statusCode = pickJiBikerJson.getString("status");
		if(statusCode.equals("200")){
			pickjiBiker = new Biker();
			pickjiBiker.setPickUpTime(pickJiBikerJson.getString("pickupTime"));
			pickjiBiker.setDeliveryTime(pickJiBikerJson.getString("deliveryTime"));
			pickjiBiker.setUserId(pickJiOrderId);
			pickjiBiker.setLat(pickJiBikerJson.getString("latitude"));
			pickjiBiker.setLng(pickJiBikerJson.getString("longitude"));
			return pickjiBiker;
		}else{
			return pickjiBiker;
		}
	}
	
	public static void savePickJiBikerPickUp(Biker biker){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "Update fapp_order_tracking set "
							+ " latitude=?,longitude=?, driver_pickup_time = ?,order_picked='Y',driver_reached='Y', "
							+ " driver_arrival_time=? where external_order_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, biker.getLat());
						preparedStatement.setString(2, biker.getLng());
						System.out.println("PICK TIME FROM PICKJI:::"+biker.getPickUpTime());
						Timestamp pickUpts = Timestamp.valueOf(biker.getPickUpTime()+".012345");
						preparedStatement.setTimestamp(3, pickUpts);
						preparedStatement.setTimestamp(4, pickUpts);
						preparedStatement.setString(5, biker.getUserId());
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							System.out.println("Pick up time:"+biker.getPickUpTime()+" updated!");
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
	}
	
	
	/**
	 * This method returns the biker object if pickji order id found from their API
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 */
	public static Biker isOrderDeliverByPickJiBoy(String orderNo , String kitchenName ){
		Biker biker = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select external_order_id,driver_name,driver_number from fapp_order_tracking where"
							+ " order_id = (select order_id from fapp_orders where order_no = ?)"
							+ " and kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							if(resultSet.getString("external_order_id")!=null){
								biker = new Biker();
								biker.setUserId(resultSet.getString("external_order_id")); 
								biker.setBikerName(resultSet.getString("driver_name"));
								biker.setBikerContact(resultSet.getString("driver_number"));
							}else{
								
							}
							
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(resultSet!=null){
							resultSet.close();
						}
						if(connection!=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Pickji boyId:"+biker.getUserId());
		System.out.println("PickJi BoyName : "+biker.getBikerName());
		System.out.println("PickJi BoyContact: "+biker.getBikerContact());
		return biker;
	}
}
