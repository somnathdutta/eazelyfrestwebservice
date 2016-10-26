package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class TermsAndConditionDAO {

	public static JSONObject getTermsAndConditions() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		String termsAndConditions = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select terms_and_condition from  fapp_terms_and_condition where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							termsAndConditions = resultSet.getString("terms_and_condition");		
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
		if(termsAndConditions!=null){
			jsonObject.put("status", "200");
			jsonObject.put("message", "Data found!");
			jsonObject.put("termsAndConditions", termsAndConditions);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "No Data found!");
			jsonObject.put("termsAndConditions", "");
		}
		return jsonObject ;
	}
}
