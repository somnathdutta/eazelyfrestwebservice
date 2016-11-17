package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONObject;

import pojo.Biker;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.Driver;
import com.mkyong.rest.TimeFormattor;

public class KitchenReceiveOrderDAO {

	 /**
     * A WEB SERVICE FOR RECEIVING ORDERS FROM KITCHEN APP
     * @param orderNo
     * @param kitchenName
     * @return
     * @throws Exception 
     */
    public static JSONObject receiveOrderFromKitchen(String orderNo, String kitchenName) throws Exception{
    	JSONObject receivedJsonObject = new JSONObject();
    	boolean orderReceived = false;
    	Biker biker = new Biker();
    	int totalNoOfQuantity = PlaceOrderDAO.getTotalNoOfQuantity(kitchenName, orderNo);
    	if(orderNo.startsWith("REG")){
    		/**
    			 * Update table order tracking with received is Y and current time stamp for order id and kitchen id..
    			 */
    			orderReceived = receiveOrder(orderNo,kitchenName);
	    			/**
	    			 * After order receive place order to pickji api
	    			 */
	    			if(orderReceived){
	    				JSONObject notifyObject ;
	    				/*if(totalNoOfQuantity == 1){//If total quantity ==1 then place order to pickji
	    					notifyObject = PickJiDAO.placeOrderToPickJi(orderNo, kitchenName);// need to be changed
		    				JSONObject pickJiJson = new JSONObject();
		    				pickJiJson = notifyObject;
		    				String responseCode = pickJiJson.getString("responseCode");
		    				if(responseCode.equals("200")){
		    					pickJiJson.put("kitchenName", kitchenName);
		    					pickJiJson.put("orderNo", orderNo);
		    					pickJiJson.put("pickJiOrderID", notifyObject.getString("orderID"));
		    					BookDriver.savePickjiOrderId(pickJiJson);
		    					receivedJsonObject.put("status", true);
	        				    receivedJsonObject.put("boyId", "PickJiBoyID");
	        				    receivedJsonObject.put("boyName", "PickjiBoyName");
	        				    receivedJsonObject.put("boyPhoneNo", "PickjiBoyContact");
		    				}
	    				}else{*/
	    					//Total qty >1 eazelyf biker details
	    					String bikerUserId = BookDriver.getBikerUserID(kitchenName, orderNo);//Get biker user id
	    					   if(BookDriver.isPickJiBoy(bikerUserId)){
	    						    notifyObject = PickJiDAO.placeOrderToPickJi(orderNo, kitchenName);// need to be changed
	    						    JSONObject pickJiJson = new JSONObject();
	    		    				pickJiJson = notifyObject;
	    		    				String responseCode = pickJiJson.getString("responseCode");
	    		    				if(responseCode.equals("200")){
	    		    					pickJiJson.put("kitchenName", kitchenName);
	    		    					pickJiJson.put("orderNo", orderNo);
	    		    					pickJiJson.put("pickJiOrderID", notifyObject.getString("orderID"));
	    		    					BookDriver.savePickjiOrderId(pickJiJson);
	    		    					receivedJsonObject.put("status", true);
	    	        				    receivedJsonObject.put("boyId", "PickJiBoyID");
	    	        				    receivedJsonObject.put("boyName", "PickjiBoyName");
	    	        				    receivedJsonObject.put("boyPhoneNo", "PickjiBoyContact");
	    		    				}
	    					   }else{
	    						   	biker = BikerDAO.getDriverDetailsFromKitchen(orderNo, kitchenName);
		        					receivedJsonObject.put("status", true);
		        				    receivedJsonObject.put("boyId", biker.getUserId());
		        				    receivedJsonObject.put("boyName", biker.getBikerName());
		        				    receivedJsonObject.put("boyPhoneNo", biker.getBikerContact());
	    					   }
	    						
	    			//	}
	    				
	    			}
    			/**
    			 * Check if all kitchen accepted that order or not..
    			 */
    			if(isAllKitchenReceive(orderNo)){
    				/**
    				 * Change the status of order...
    				 */
    				if(changeOrderStatus(orderNo)){
    					/**
    					 * Send confirmation message to customer for this order...
    					 */
    					 DBConnection.sendMessageToMobile(DBConnection.getCustomerMobile(orderNo,"REGULAR"), orderNo, DBConnection.getOrderTime(orderNo, "REGULAR"), 2);
    					/*if(PlaceOrderDAO.getTotalNoOfQuantity(kitchenName, orderNo) > 1){//Total qty >1 eazelyf biker details
    						biker = BikerDAO.getDriverDetailsFromKitchen(orderNo, kitchenName);
        					receivedJsonObject.put("status", true);
        				    receivedJsonObject.put("boyId", biker.getUserId());
        				    receivedJsonObject.put("boyName", biker.getBikerName());
        				    receivedJsonObject.put("boyPhoneNo", biker.getBikerContact());
    					}else{//pickji details with blank
    						receivedJsonObject.put("status", true);
        				    receivedJsonObject.put("boyId", "");
        				    receivedJsonObject.put("boyName", "");
        				    receivedJsonObject.put("boyPhoneNo", "");
    					}*/
    					
    				}
    			}
    	}
    	System.out.println(receivedJsonObject);
    	return receivedJsonObject;
    	
    }
    
	
	/**
	 * Receive order b kitchen
	 * @param neworderbean
	 * @return parent call from 199
	 */
	private static Boolean receiveOrder(String orderNo , String kitchenName){
		Boolean received = false;
		System.out.println("- - - Updating table order tracking for kitchen "+kitchenName +"- - - ");
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET received = 'Y',received_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				/*String sqlSubs = "UPDATE fapp_order_tracking SET received = 'Y',received_time=current_timestamp "
						   + " WHERE subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";*/
				
				try {
					/*if(orderNo.startsWith("REG")){
						preparedStatement =  connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
					}else{
						preparedStatement =  connection.prepareStatement(sqlSubs);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
					}*/
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				//System.out.println(preparedStatement);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					received =  true;
					
				}
				} catch (Exception e) {
					System.out.println(e);
					connection.rollback();
					//connection.close();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
		
			SQL:{
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id = 8"
							   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) ";
					try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
						received =  true;
					}
					} catch (Exception e) {
						System.out.println(e);
						connection.rollback();
						connection.close();
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
		System.out.println("- - - Updation table order tracking for kitchen "+kitchenName +" is - - - "+received);
		return received;
	}
	
	/**
	 * Check if all kicthen accepts order or not
	 * @param orderid
	 * @return parent call from 203
	 */
	private static Boolean isAllKitchenReceive(String orderNo){
		Integer totalOrders = 0,totalReceivedOrders = 0 ;
		System.out.println("- - Check if all kitchen accepted that order or not..");
		try {
			Connection connection = DBConnection.createConnection();	
			SQL:{  				 
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
							 	+" count(ORDER_ID)AS total_order "
								+" from fapp_order_tracking "
								+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)";
					
					String sqlSub = "SELECT "
						 	+" count(subscription_id)AS total_order "
							+" from fapp_order_tracking "
							+" where subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?)";
				 try {
					 
					 if(orderNo.startsWith("REG")){
						 preparedStatement =  connection.prepareStatement(sql);
						 preparedStatement.setString(1, orderNo);
					 }else{
						 preparedStatement =  connection.prepareStatement(sqlSub);
						 preparedStatement.setString(1, orderNo);
					 }
					/*preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);*/
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
						 	+" count(received)AS total_order_received "
							+" from fapp_order_tracking "
							+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)"
							+ " AND received = 'Y'";
					
					String sqlSub = "SELECT "
						 	+" count(received)AS total_order_received "
							+" from fapp_order_tracking "
							+" where subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?)"
							+ " AND received = 'Y'";
					 try {
							
						 if(orderNo.startsWith("REG")){
							 preparedStatement =  connection.prepareStatement(sql);
								preparedStatement.setString(1, orderNo);
						 }else{
							 preparedStatement =  connection.prepareStatement(sqlSub);
								preparedStatement.setString(1, orderNo);
						 }
						 	/*preparedStatement =  connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);*/
							resultSet = preparedStatement.executeQuery();
							if (resultSet.next()) {
								totalReceivedOrders = resultSet.getInt("total_order_received");
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
					 System.out.println("Total received orders::"+totalReceivedOrders);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if( totalOrders!=0 && totalReceivedOrders!=0 && totalOrders == totalReceivedOrders){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Receive the order
	 * @param neworderbean parent call from 205
	 */
	private static Boolean changeOrderStatus(String orderNo){
		Boolean receivedOrder = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=2 WHERE order_id="
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
				try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
					//	Messagebox.show("Order Received Successfully!");
						receivedOrder = true;
						//System.out.println("Device regid::"+ getDeviceRegId(orderNo) );
						//sendMessage(getDeviceRegId(orderNo),orderNo,2);
						//sendMessageToMobile(getCustomerMobile(orderNo,"REGULAR"), orderNo, getOrderTime(orderNo, "REGULAR"), 2);
					}
				} catch (Exception e) {
					System.out.println(e);
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return receivedOrder;
	}
}
