package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sql.ForgotPasswordSQL;

import com.mkyong.rest.DBConnection;

public class ForgotPassword {

	public static boolean emailExists(String userMailId){
		boolean emailExists = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(ForgotPasswordSQL.emailExistsQuery);
						preparedStatement.setString(1, userMailId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							emailExists = true;
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
		System.out.println("Email id::"+userMailId+" is exists::"+emailExists);
		return emailExists;
	}
	
	 public static JSONObject forgotPassword(String receiverEmailID) throws IOException{
	    	JSONObject jsonObject = new JSONObject();
	    	if(DBConnection.mailSender (receiverEmailID, "Regarding forgot password from eazelyf app", DBConnection.getPasswordFromDB(receiverEmailID) )){
	    		try {
					jsonObject.put("status", "200");
					jsonObject.put("message", "Password sent to your mailId "+receiverEmailID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}else{
	    		//JSONObject jsonObject = new JSONObject();
	    		try {
					jsonObject.put("status", "204");
					jsonObject.put("message", "Invalid email Id given");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		//return jsonObject;
	    	}
	    	return jsonObject;
	    }
}
