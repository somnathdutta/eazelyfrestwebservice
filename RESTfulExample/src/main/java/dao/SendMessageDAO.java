package dao;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class SendMessageDAO {

	public static void sendMessageForDeliveryBoy(String recipient, String deliveryBoyMobile, 
			String boyName,String subscriptionNO){
		try {
			//String recipient = "+917872979469";
			//String message = orderNo+" is assigned for you!";
			//String myAPI = "https://www.google.com/maps?q="+latitude+","+longitude+"&16z";
			String myAPI = "http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/map?id="+subscriptionNO;
			String message ="";
			message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order. Track your order here"+myAPI+" Thnx & Rgds Eazelyf.";
			
		
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
}
