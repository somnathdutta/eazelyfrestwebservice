package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class ContactUsDAO {

	public static JSONObject getCustomerCareNumber() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		String contactNO = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select contct_no from  fapp_contact_us where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							contactNO = resultSet.getString("contct_no");		
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
		if(contactNO!=null){
			jsonObject.put("status", "200");
			jsonObject.put("message", "Data found!");
			jsonObject.put("contactNo", contactNO);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "No Data found!");
			jsonObject.put("contactNo", "");
		}
		return jsonObject ;
	}
	
	
}