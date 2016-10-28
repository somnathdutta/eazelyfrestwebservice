package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class SetItemDetailsDao {

	public static JSONObject fetchSetDetails() throws JSONException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONObject jsonObject = new JSONObject();
		JSONArray setItems = new JSONArray();
		String sql = "select item_code, item_name, item_description, set_name, set_id from vw_set_item_food_details where item_active = 'Y' ";
		try {
			connection = DBConnection.createConnection();
			try {
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject itemjson = new JSONObject();
					
					itemjson.put("item code", resultSet.getString("item_code"));
					itemjson.put("itemName", resultSet.getString("item_name"));
					itemjson.put("itemDescription", resultSet.getString("item_description"));
					itemjson.put("setName", resultSet.getString("set_name"));
					itemjson.put("setId", String.valueOf(resultSet.getInt("set_id")));
					
					setItems.put(itemjson);
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(preparedStatement !=null){
					preparedStatement.close();
				}
				if(resultSet != null){
					resultSet.close();
				}
				if(connection != null){
					connection.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(setItems.length()>0){
			jsonObject.put("status", "200");
			jsonObject.put("message", "found");
			jsonObject.put("setList", setItems);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "not found");
			jsonObject.put("setList", setItems);
		}
		return jsonObject;
		
	}
	
}