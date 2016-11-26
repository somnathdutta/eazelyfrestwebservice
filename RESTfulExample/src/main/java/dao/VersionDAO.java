package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class VersionDAO {

	
	public static JSONObject getCurrenVersion() throws JSONException{
		JSONObject versionObject = new JSONObject();
		String version = null;
		try {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			String sql = "select server_version from fapp_server_version order by server_version_id desc limit 1";
			try {
				connection = DBConnection.createConnection();
				preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					version = resultSet.getString("server_version");
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				if(connection != null){
					connection.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(version != null){
			versionObject.put("status", "200");
			versionObject.put("message", version);
			
		}else {
			versionObject.put("status", "204");
			versionObject.put("message", "");
		}
		
		return versionObject;
	}
	
	
}
