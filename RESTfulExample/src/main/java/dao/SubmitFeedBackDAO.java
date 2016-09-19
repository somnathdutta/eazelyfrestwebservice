package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class SubmitFeedBackDAO {

	public static JSONObject submitFeedback(String menu, String taste,String quantity,String packing,
			String timelyDelivered,String comment,String userMailId) throws JSONException{
		JSONObject submitfeedbackObject = new JSONObject();
		Boolean submitsuccess = false;
		try {
    		Connection connection = DBConnection.createConnection();
    		/***SQL BLOCK STARTS HERE***/
			SQL:{
    			  PreparedStatement preparedStatement = null;
    			 String sql ="UPDATE fapp_order_feedback "
							  +" SET menu=?, "
							  +" taste=?, timely_delivered=?,  "
							  +" comment=?," 
							  +" portion =?, packing=? , overeall_rating = ?"
							  +" WHERE user_mail_id=? AND order_id=?;";
    			  try {
					   preparedStatement = connection.prepareStatement(sql);
					   for(Integer orderid : DBConnection.getUnfeedBackedOrderIds(userMailId)){
						   if(menu!=null){
							   preparedStatement.setString(1, menu);
						   }else{
							   preparedStatement.setString(1, "1");
						   }
						   if(taste!= null){
							   preparedStatement.setString(2,taste);
						   }else{
							   preparedStatement.setString(2, "1");
						   }
						   if(timelyDelivered!= null){
							   preparedStatement.setString(3, timelyDelivered);
						   }else{
							   preparedStatement.setString(3, "1");
						   }
						   if(comment!=null){
							   preparedStatement.setString(4,comment);
						   }else{
							   preparedStatement.setString(4, "--NOT GIVEN--");
						   }
						  if(quantity!=null){
							   preparedStatement.setString(5, quantity);
						   }else{
							   preparedStatement.setString(5, "1");
						   }
						  if(packing!=null){
							   preparedStatement.setString(6, packing);
						   }else{
							   preparedStatement.setString(6, "1");
						   }
						  preparedStatement.setString(7, "5");
						  preparedStatement.setString(8, userMailId);
						  preparedStatement.setInt(9, orderid);
						  preparedStatement.addBatch();
					   }  
					int [] count = preparedStatement.executeBatch();
			    	   for(Integer integer : count){
			    		   submitsuccess = true;
			    	   }
			    	   
				} catch (Exception e) {
					System.out.println(e);
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
    		/***SQL BLOCK ENDS HERE***/
		} catch (Exception e) {
		}
		if(submitsuccess){
			submitfeedbackObject.put("status", "200");
			submitfeedbackObject.put("message", "Feedback submitted!");
		}else{
			submitfeedbackObject.put("status", "204");
			submitfeedbackObject.put("message", "Feedback submission failed !");
		}
		return submitfeedbackObject;
	}
}
