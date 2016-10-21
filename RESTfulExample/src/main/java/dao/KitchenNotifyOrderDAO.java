package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Biker;

import com.mkyong.rest.DBConnection;

public class KitchenNotifyOrderDAO {

	/**
	 * 
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	public static JSONObject notifyLogistics(String orderNo , String kitchenName, String boyId) throws JSONException {
		JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		Boolean orderNotified = false;
		int totalNoOfQuantity = PlaceOrderDAO.getTotalNoOfQuantity(kitchenName, orderNo);
		orderNotified = notifyOrder(orderNo, kitchenName);
		if(isAllKitchenNotified(orderNo)){
			changeOrderStatusToReady(orderNo);
		}
		if(orderNotified){
			if(totalNoOfQuantity==1){
				JSONObject pickJiBiker = CallPickJiBikerDAO.callPickJi(orderNo, kitchenName);
				 Biker biker = new Biker();
				 String lat = null,lng = null ;
				  if(pickJiBiker.getString("responseCode").equalsIgnoreCase("200")){
					 JSONObject orderDetails = pickJiBiker.getJSONObject("orderDetails");
					  biker.setBikerName(orderDetails.getString("bikerName"));
					  biker.setBikerContact(orderDetails.getString("bikerMobile"));
					  biker.setBikerPosition(orderDetails.getString("bikerPosition"));
					  biker.setPickUpTime(orderDetails.getString("schedulePickupTime"));
					  biker.setDeliveryTime(orderDetails.getString("scheduleDeliveryTime"));
				  }
				  if( !biker.getBikerName().equals("null")){
					  System.out.println("Biker name: "+biker.getBikerName());
					  System.out.println("Biker contct:: "+biker.getBikerContact());
					  System.out.println("Biker position:: "+biker.getBikerPosition());
					  String[] latlng =  biker.getBikerPosition().split(",");
					  for(int i=0;i<latlng.length;i++){
						  lat = latlng[0];
						  lng = latlng[1];
					  }
					  System.out.println("LAT:: "+lat.trim());
					  System.out.println("LNG:: "+lng.trim());
					  System.out.println("Pickup:: "+biker.getPickUpTime());
					  System.out.println("Deliery :"+biker.getDeliveryTime());
					  biker.setLat(lat);
					  biker.setLng(lng);
					  notifiedJsonObject.put("status", true);
					  notifiedJsonObject.put("boyId", "PickJiBoyID");
					  notifiedJsonObject.put("boyName", biker.getBikerName());
					  notifiedJsonObject.put("boyPhoneNo", biker.getBikerContact());
					  if(savePickJiBikerDetails(biker, kitchenName, orderNo)){
						  System.out.println("Pickji biker details saved!");
					  }
				  }else{
					  notifiedJsonObject.put("status", true);
					  notifiedJsonObject.put("boyId", "NO BOY ID");
					  notifiedJsonObject.put("boyName", "NO BOY NAME");
					  notifiedJsonObject.put("boyPhoneNo", "NO BOY CONTACT");
				  }
				  
				
			}else{
				if(callDriver(orderNo, kitchenName, boyId)){
					Biker biker = BikerDAO.getDriverDetailsFromKitchen(orderNo, kitchenName);
					notifiedJsonObject.put("status", true);
					notifiedJsonObject.put("boyId", biker.getUserId());
					notifiedJsonObject.put("boyName", biker.getBikerName());
					notifiedJsonObject.put("boyPhoneNo", biker.getBikerContact());
				}
			}	
		}
		System.out.println(notifiedJsonObject);
		return notifiedJsonObject;
	}
	
	/**
	 * Update order tracking with notify 'Y' and current timestamp for order id and kitchen
	 * @param orderNo
	 * @param kitchenName
	 * @return parent call from 199
	 */
	private static Boolean notifyOrder(String orderNo , String kitchenName){
		Boolean notified = false;
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET notify = 'Y',notified_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				try {
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					notified =  true;
					System.out.println("1. Notifed !!");
				}
				} catch (Exception e) {
					System.out.println(e);
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
			SQL:{
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id = 3"
							   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) ";
					try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
						notified =  true;
						System.out.println("2. order status change!!");
					}
					} catch (Exception e) {
						System.out.println(e);
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
					}	
		} catch (Exception e) {
			// TODO: handle exception
		}
		return notified;
	}
	
	/**
	 * Check if all kicthen accepts order or not
	 * @param orderid
	 * @return parent call from 203
	 */
	private static Boolean isAllKitchenNotified(String orderNo){
		Integer totalOrders = 0,totalNotifiedOrders = 0 ;
		
		try {
			Connection connection = DBConnection.createConnection();	
			SQL:{  				 
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
							 	+" count(ORDER_ID)AS total_order "
								+" from fapp_order_tracking "
								+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?) and rejected='N'";
				 try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						totalOrders = resultSet.getInt("total_order");
					}
				} catch (Exception e) {
					// TODO: handle exceptio
					e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				 System.out.println("total orders-->"+totalOrders);
			}
			
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
						 	+" count(notify)AS total_order_notified "
							+" from fapp_order_tracking "
							+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)"
							+ " AND notify = 'Y' AND rejected='N'";
					 try {
							preparedStatement =  connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);
							resultSet = preparedStatement.executeQuery();
							if (resultSet.next()) {
								totalNotifiedOrders = resultSet.getInt("total_order_notified");
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
					 System.out.println("Total notified orders::"+totalNotifiedOrders);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if( totalOrders!=0 && totalNotifiedOrders!=0 && totalOrders == totalNotifiedOrders){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Receive the order
	 * @param neworderbean parent call from 205
	 */
	private static Boolean changeOrderStatusToReady(String orderNo){
		Boolean notifiedOrder = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=9 WHERE order_id="
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
				try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
					//	Messagebox.show("Order Received Successfully!");
						notifiedOrder = true;
						////System.out.println("Device regid::"+ getDeviceRegId(orderNo) );
						//sendMessage(getDeviceRegId(orderNo),orderNo,2);
						System.out.println("3. Final order status!!");
					}
				} catch (Exception e) {
					System.out.println(e);
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return notifiedOrder;
	}
	
	/**
	 * Update the driver assignment time for particular biker user id
	 * @param orderNo
	 * @param kitchenName
	 * @param boyID
	 * @return
	 */
	public static boolean callDriver(String orderNo , String kitchenName, String boyID){
		boolean called = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					/*String sql = "UPDATE fapp_order_tracking set driver_assignment_time = current_timestamp"
							+ " where order_id = (select order_id from fapp_orders where order_no=?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and driver_boy_user_id = ?";*/
					String sql = "UPDATE fapp_order_tracking set driver_assignment_time = current_timestamp"
							+ " where order_id = (select order_id from fapp_orders where order_no=?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						//preparedStatement.setString(3, boyID);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							called = true;
						}
					} catch (Exception e) {
						System.out.println("Driver calling failed due to::"+e.getMessage());
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
		if(called){
			System.out.println("--Biker notified!! - - ");
			Biker biker = BikerDAO.getDriverDetailsFromKitchen(orderNo, kitchenName);
			DBConnection.sendMessageToMobile(biker.getBikerContact(), orderNo, DBConnection.getOrderTime(orderNo, "REGULAR"), 1);
			//SendMessageDAO.sendMessageForDeliveryBoy(DBConnection.getCustomerMobile(orderNo, "REGULAR"), biker.getBikerContact(), biker.getBikerName(), orderNo);
		}
		return called;
	}
	
	public static boolean savePickJiBikerDetails(Biker biker,String kitchenName, String orderNo){
		boolean isSaved = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "Update fapp_order_tracking set driver_assignment_time=current_timestamp, driver_name = ?,driver_number = ?,"
							+ " latitude=?,longitude=?, driver_pickup_time = ?, order_delivery_time = ? "
							+ " where kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?)"
							+ " and order_id =(select order_id from fapp_orders where order_no = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, biker.getBikerName());
						preparedStatement.setString(2, biker.getBikerContact());
						preparedStatement.setString(3, biker.getLat());
						preparedStatement.setString(4, biker.getLng());
						Timestamp pickUpts = Timestamp.valueOf(biker.getPickUpTime());
						preparedStatement.setTimestamp(5, pickUpts);
						Timestamp deliveryts = Timestamp.valueOf(biker.getDeliveryTime());
						preparedStatement.setTimestamp(6, deliveryts);
						preparedStatement.setString(7, kitchenName);
						preparedStatement.setString(8, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							isSaved = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		return isSaved;
	}
}
