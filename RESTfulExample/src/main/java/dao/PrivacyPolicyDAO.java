package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class PrivacyPolicyDAO {

	public static JSONObject getPrivacyPolicy() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		String privacyPolicy = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select privacy_policy from  fapp_privacy_policy where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							privacyPolicy = resultSet.getString("privacy_policy");		
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}
					 if(connection!=null){
						 connection.close();
					 }
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(privacyPolicy!=null){
			jsonObject.put("status", "200");
			jsonObject.put("message", "Data found!");
			jsonObject.put("privacyPolicy", privacyPolicy);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "No Data found!");
			jsonObject.put("privacyPolicy", "");
		}
		return jsonObject ;
	}
}
