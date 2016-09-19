package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sql.LoginSQL;

import com.mkyong.rest.DBConnection;

public class LoginDAO {

	public static JSONObject checkUserlogin(String mobileNo, String password) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean loggedStatus = false;
    	String username = "";
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(LoginSQL.loginQuery);
						preparedStatement.setString(1, mobileNo);
						preparedStatement.setString(2, password);
						resultSet =  preparedStatement.executeQuery();
						if(resultSet.next()){
							loggedStatus = true;
							username = resultSet.getString("username");
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
    	if(loggedStatus){
    		if(username!=null){
				jsonObject.put("status", username);
			}else{
				jsonObject.put("status", mobileNo);
			}
    	}else{
    		jsonObject.put("status", "failed");
    	}
    	
    	return jsonObject;
    }
	
	public static JSONObject checkKitchenlogin(String userName, String password) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean loggedStatus = false;
    	String username = "";
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(LoginSQL.loginKitchenQuery);
						preparedStatement.setString(1, userName);
						preparedStatement.setString(2, password);
						resultSet =  preparedStatement.executeQuery();
						if(resultSet.next()){
							loggedStatus = true;
							username = resultSet.getString("username");
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
    	if(loggedStatus){
    		jsonObject.put("status", loggedStatus);
    		jsonObject.put("message", userName);
    	}else{
			jsonObject.put("status", loggedStatus);
			jsonObject.put("message", "Invalid credentials!");
		}
			
    	return jsonObject;
    }
}
