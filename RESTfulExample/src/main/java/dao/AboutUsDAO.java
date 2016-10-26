package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class AboutUsDAO {

	public static JSONObject getAboutUS() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		String aboutUs = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select about_us from  fapp_about_us where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							aboutUs = resultSet.getString("about_us");		
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
		if(aboutUs!=null){
			jsonObject.put("status", "200");
			jsonObject.put("message", "Data found!");
			jsonObject.put("aboutUs", aboutUs);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "No Data found!");
			jsonObject.put("aboutUs", "");
		}
		return jsonObject ;
	}
}
