package dao;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class SendMessageDAO {

	public static boolean isSmsActive(){
		boolean isSmsActive = false;
		String isActive = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select is_active from fapp_sms_master";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							isActive = resultSet.getString("is_active");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
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
		if(isActive.equalsIgnoreCase("Y")){
			isSmsActive = true;
		}else{
			isSmsActive = false;
		}
		return isSmsActive ;
	}
	
	public static void sendMessageForDeliveryBoy(String recipient, String deliveryBoyMobile, 
			String boyName,String subscriptionNO){
		 if(SendMessageDAO.isSmsActive()){
			 try {
					//String recipient = "+917872979469";
					//String message = orderNo+" is assigned for you!";
					//String myAPI = "https://www.google.com/maps?q="+latitude+","+longitude+"&16z";
					String myAPI = "http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/map?id="+subscriptionNO;
					String message ="";
					message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order. Track your order here "+myAPI+" Thnx & Rgds Eazelyf.";
					
					/*
					 * Delivery boy ABCD having mobile no 91XXXXXXXX is assigned to deliver your order.Thnx & Rgds Eazelyf.
						ORDER/000001 has been successfully delivered to you.Thnx & Rgds Eazelyf.
					 */
					String username = "nextgenvision"; 
					String password = "sms@123";
					String senderId = "eazelyf";
					String requestUrl  = "http://fastsms.way2mint.com/SendSMS/sendmsg.php?" +
							 "uname=" + URLEncoder.encode(username, "UTF-8") +
							 "&pass=" + URLEncoder.encode(password, "UTF-8") +
							 "&send=" + URLEncoder.encode(senderId, "UTF-8") +
							 "&dest=" + URLEncoder.encode(recipient, "UTF-8") +
							 "&msg=" + URLEncoder.encode(message, "UTF-8") ;
					System.out.println("Message sent to mobile no::"+recipient+"::"+message);
					URL url = new URL(requestUrl);
					HttpURLConnection uc = (HttpURLConnection)url.openConnection();
					System.out.println("Message Response:::::"+uc.getResponseMessage());
					uc.disconnect();
					} catch(Exception ex) {
					System.out.println(ex.getMessage());
					}	
		 }
		
	}
	
	public static void sendMessageToCustomerForPickJiBiker(String recipient, String deliveryBoyMobile, 
			String boyName, String pickJiOrderId){
		if(SendMessageDAO.isSmsActive()){
			try {
				String myAPI = "http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/map1?id="+pickJiOrderId;
				String message ="";
				message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order. Track your order here "+myAPI+" Thnx & Rgds Eazelyf.";
				String username = "nextgenvision"; 
				String password = "sms@123";
				String senderId = "eazelyf";
				String requestUrl  = "http://fastsms.way2mint.com/SendSMS/sendmsg.php?" +
						"uname=" + URLEncoder.encode(username, "UTF-8") +
						"&pass=" + URLEncoder.encode(password, "UTF-8") +
						"&send=" + URLEncoder.encode(senderId, "UTF-8") +
						"&dest=" + URLEncoder.encode(recipient, "UTF-8") +
						"&msg=" + URLEncoder.encode(message, "UTF-8") ;
				System.out.println("Message sent to mobile no::"+recipient+"::"+message);
				URL url = new URL(requestUrl);
				HttpURLConnection uc = (HttpURLConnection)url.openConnection();
				System.out.println("Message Response:::::"+uc.getResponseMessage());
				uc.disconnect();
			} catch(Exception ex) {
				System.out.println(ex.getMessage());
			}	
		}

	}
	
	public static boolean isDeliveryMessageSend(String orderNo){
		boolean sent = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "Select is_message_send from fapp_orders where order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							if(resultSet.getString("is_message_send").equals("N")){
								sent = false;
							}else{
								sent = true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sent;
	}

	public static void updateSendMessageStatus(String orderNo){
		try {
			SQL3:{
    		Connection connection = DBConnection.createConnection();
    		PreparedStatement preparedStatement = null;
    		String sql = "UPDATE fapp_orders SET is_message_send='Y',is_invoice_send='Y' "
    				+ " WHERE order_no = ?";
    		try {
    			preparedStatement = connection.prepareStatement(sql);
    			preparedStatement.setString(1, orderNo);
    			int count = preparedStatement.executeUpdate();
    			if(count>0){
    				System.out.println("Message send status update!");
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}finally{
    			if(connection!=null){
    				connection.close();
    			}
    		}
		} 
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static int sendOTP(String recipient,String OTP){
		int done = 0;
		try {
			String message ="";
			message = "Please use this OTP "+ OTP +" \nThanks Eazelyf.";//Please use this OTP: "+OTP+". Eazelyf.";
			String username = "nextgenvision"; 
			String password = "sms@123";
			String senderId = "eazelyf";
			String requestUrl  = "http://fastsms.way2mint.com/SendSMS/sendmsg.php?" +
					"uname=" + URLEncoder.encode(username, "UTF-8") +
					"&pass=" + URLEncoder.encode(password, "UTF-8") +
					"&send=" + URLEncoder.encode(senderId, "UTF-8") +
					"&dest=" + URLEncoder.encode(recipient, "UTF-8") +
					"&msg=" + URLEncoder.encode(message, "UTF-8") ;
			System.out.println("Message sent to mobile no::"+recipient+"::"+message);
			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			System.out.println("Message Response:::::"+uc.getResponseMessage());
			System.out.println("Message Response code:::::"+uc.getResponseCode());
			done = uc.getResponseCode();
			uc.disconnect();
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}	
		return done;
	}
}
