package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.User;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.Order;
import com.mkyong.rest.OrderItems;

public class PickjiCall implements Runnable {

	String orderNo,kitchenName,externalOrderID;
	private volatile boolean exit = false;
	
	public PickjiCall(String kitchenName, String orderNo, String externalOrderID) {
		super();
		System.out.println("New thread calling. . .");
		this.orderNo = orderNo;
		this.kitchenName = kitchenName;
		this.externalOrderID = externalOrderID;
	}

	@Override
	public void run() {
		String line = "";
		JSONObject responseJsonObject = null;
		try {
	        while (true) {
	            System.out.println(new Date());
	            try{
	    			DefaultHttpClient client = new DefaultHttpClient();
	    			org.json.simple.JSONObject shipMent = createCallPickJiJson(kitchenName,orderNo);
	    			String url ="http://api.pickji.com/corporateapi/orderdetails";
	    			HttpPost post = new HttpPost(url.trim());
	    			StringEntity input = new StringEntity(shipMent.toJSONString());
	    			post.addHeader("Content-Type", "application/json");
	    			post.setEntity(input);
	    			System.out.println("API HITTING. . .  ");
	    			HttpResponse response = client.execute(post);
	    		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
	    		      while ((line = rd.readLine()) != null) {
	    		    	  responseJsonObject = new JSONObject(line);
	    		      }
	    		}catch(UnsupportedEncodingException e){
	    			
	    		}catch(JSONException e){
	    			
	    		} catch (ClientProtocolException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	            
	            String code = responseJsonObject.getString("responseCode");
	            if(code.equals("200")){
	            	JSONObject orderDetails = responseJsonObject.getJSONObject("orderDetails");
	            	 System.out.println("Biker position:: "+orderDetails.getString("bikerPosition"));
					 System.out.println("Pick up time:: "+orderDetails.getString("pickupTime"));
					 System.out.println("Delivery time:: "+orderDetails.getString("deliveryTime"));
					 if(!orderDetails.getString("deliveryTime").equals("null")){
						 saveOrderDeliveryTime(kitchenName, orderNo, orderDetails.getString("deliveryTime")+".012345");
						// freePickJiBoy(externalOrderID);
						 if( DBConnection.makeOrderCompleted(orderNo) ){
				    			
				    			if(SendMessageDAO.isDeliveryMessageSend(orderNo)){
				    				System.out.println("Already message sent!!!");
				    			}else{
				    				/**
									 * SEND MESSAGE TO CUSTOMER FOR ORDER DELIVEREY
									 */
				    				DBConnection.sendMessageToMobile(DBConnection.getCustomerMobile(orderNo, "REGULAR"), 
				    						orderNo, "orderTime" , 7);
				    				User user = UserDetailsDao.getUserDetails(null, orderNo);
				    				Order order = OrderDetailsDAO.getOrderDetails(orderNo);
				    				ArrayList<OrderItems> orderItemList = OrderItemDAO.getOrderItemDetails(orderNo);
				    				/**
				    				 * SEND INVOICE TO CUSTOMER 
				    				 */
				    				Invoice.generateAndSendEmail(user, order, orderItemList);
				    				SendMessageDAO.updateSendMessageStatus(orderNo);
				    			}
				    		}
						 exit = true;
						 break;
					 }
	            }
	            Thread.sleep(2700 * 1000);
	            
	        }
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public void stop(){
	        exit = true;
	 }
	
	public static boolean isDeliveredToBoy(String orderNo , String kitchenName ){
		boolean isOrderGiven=false;
		try {
			Connection connection = com.mkyong.rest.DBConnection.createConnection();
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

	public static org.json.simple.JSONObject createCallPickJiJson(String kitchenName, String orderNo ) 
			throws JSONException{
		org.json.simple.JSONObject pickJiJson = new org.json.simple.JSONObject();
		//pickJiJson.put("appToken", "c151b4ae5325443152060828f149a7a7");
		pickJiJson.put("appToken", "2fdd360438620665ed3738fcc88feb70");
		pickJiJson.put("orderID", getPickJiOrderID(kitchenName, orderNo));
		return pickJiJson;
	}
	
	public static String getPickJiOrderID(String kitchenName, String orderNo){
		String pickjiOrderID = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select external_order_id from fapp_order_tracking where kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and order_id ="
							+ " (select order_id from fapp_orders where order_no = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						preparedStatement.setString(2, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							pickjiOrderID = resultSet.getString("external_order_id");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		System.out.println("Pickji order id:: "+pickjiOrderID);
		return pickjiOrderID;
	}

	public static void saveOrderDeliveryTime(String kitchenName, String orderNo, String deliveryTime){
		
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking set delivered ='Y', order_delivery_time = ? where order_id="
							+ " (select order_id from fapp_orders where order_no = ?) and "
							+ " kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						Timestamp deliveryts = Timestamp.valueOf(deliveryTime);
						preparedStatement.setTimestamp(1, deliveryts);
						preparedStatement.setString(2, orderNo);
						preparedStatement.setString(3, kitchenName);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Order delivery time updated!");
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
	}
	
	public static void freePickJiBoy( String externalOrderId){
		String userId = null;
		try {
				Connection connection = DBConnection.createConnection();
				SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet =null;
					String sql = "SELECT driver_boy_user_id FROM fapp_order_tracking "
							+ " WHERE external_order_id=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, externalOrderId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							userId = resultSet.getString("driver_boy_user_id");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
				
				SQL:{
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_delivery_boy set delivery_boy_status_id =2 where delivery_boy_user_id=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						if(userId!=null)
						preparedStatement.setString(1, userId);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println(userId+" freed successfully!");
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
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getKitchenName() {
		return kitchenName;
	}

	public void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;
	}

	public String getExternalOrderID() {
		return externalOrderID;
	}

	public void setExternalOrderID(String externalOrderID) {
		this.externalOrderID = externalOrderID;
	}

	
}
