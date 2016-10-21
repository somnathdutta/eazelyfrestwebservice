package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class QueryTypeDAO {

	public static JSONObject getQueryTypeList() throws JSONException{
		JSONObject queryJson = new JSONObject();
		JSONArray queryListArrJsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select * FROM fapp_query_type_master where is_active ='Y' and is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject queryType = new JSONObject();
							queryType.put("queryTypeId", resultSet.getInt("query_id"));
							queryType.put("queryType", resultSet.getString("query_type"));
							
							queryListArrJsonArray.put(queryType);
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
		if(queryListArrJsonArray.length()>0){
			queryJson.put("status", "200");
			queryJson.put("message", "Query List populated");
			queryJson.put("queryTypeList", queryListArrJsonArray);
		}else{
			queryJson.put("status", "204");
			queryJson.put("message", "Query List not found");
			queryJson.put("queryTypeList", queryListArrJsonArray);
		}
		System.out.println(queryJson);
		return queryJson;
	}
	
	public static JSONObject submitMessage(int queryId, String userName,String emailId, String message) throws JSONException{
		JSONObject submitJson = new JSONObject();
		boolean submitted = false;
		if(queryId==0){
			submitJson.put("status", "400");
			submitJson.put("message", "Query Type ID should not be zero");
		}else if(userName==null || userName.length()==0){
			submitJson.put("status", "400");
			submitJson.put("message", "User name required!");
		}else if(emailId== null || emailId.length()==0){
			submitJson.put("status", "400");
			submitJson.put("message", "Email id required!");
		}else if(message== null || message.length()==0){
			submitJson.put("status", "400");
			submitJson.put("message", "Message required!");
		}else{
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						String sql = "INSERT INTO fapp_query_from_user( "
								+" query_type_id, user_name, user_email, user_message ) "
								+" VALUES (?, ?, ?, ?);";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, queryId);
							preparedStatement.setString(2, userName);
							preparedStatement.setString(3, emailId);
							preparedStatement.setString(4, message);
							int count = preparedStatement.executeUpdate();
							if(count>0){
								submitted = true;
								System.out.println("Message submitted!");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}if(connection!=null){
								connection.close();
							}
						}
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(submitted){
				submitJson.put("status", "200");
				submitJson.put("message", "Thank you for writing to us.\nWe will get back to you shortly.");
			}else{
				submitJson.put("status", "204");
				submitJson.put("message", "NO content found!");
			}
		}
		return submitJson;
	}
}
