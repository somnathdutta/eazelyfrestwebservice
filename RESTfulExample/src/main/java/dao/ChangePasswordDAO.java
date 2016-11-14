package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class ChangePasswordDAO {

	public static JSONObject changePassword(String phnNumber, String oldPassword, String newPassword) throws JSONException{
    	JSONObject changePasswordObject =  new JSONObject();
    	Boolean updated = false;
    	if(DBConnection.isValidPassword(oldPassword,  phnNumber)){
    		try {	
    			SQL:{
        				Connection connection = DBConnection.createConnection();
        				PreparedStatement preparedStatement = null;
        				String sql  = "UPDATE fapp_accounts SET password = ? WHERE mobile_no = ? ";
        				try {
    						preparedStatement =  connection.prepareStatement(sql);
    						preparedStatement.setString(1, newPassword);
    						preparedStatement.setString(2, phnNumber);
    						int count = preparedStatement.executeUpdate();
    						if(count>0){
    							updated = true;
    						}
    					} catch (Exception e) {
    						e.printStackTrace();
    					}finally{
    						if(connection!= null){
    							connection.close();
    						}
    					}
        		}
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
		}else{
			updated = false;
			//inValidOldPassword = true;
		}
    	if(updated){
    		changePasswordObject.put("status", "200");
    		changePasswordObject.put("message", "Password changed successfully!");
    	}else{
    		changePasswordObject.put("status", "204");
    		changePasswordObject.put("message", "Old Password not correct!");
    	}
    	//changePasswordObject.put("status", updated);
    	return changePasswordObject;
    }
}
