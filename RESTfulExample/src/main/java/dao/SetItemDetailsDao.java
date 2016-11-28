package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class SetItemDetailsDao {

	public static JSONObject fetchSetDetails(String kitchenName) throws JSONException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONObject jsonObject = new JSONObject();
		JSONArray setItems = new JSONArray();
		/*String sql = "select item_code, item_name, item_description, set_name, set_id from vw_set_item_food_details_with_kitchen"
				+ " where item_active = 'Y' and kitchen_id=(select kitchen_id from fapp_kitchen where kitchen_name = ?)";*/
		String sql = "select item_code, item_name, item_description,is_active,is_active_tomorrow from vw_all_kitchen_items where kitchen_name = ?";
		try {
			connection = DBConnection.createConnection();
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, kitchenName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject itemjson = new JSONObject();
					
					itemjson.put("item code", resultSet.getString("item_code"));
					itemjson.put("itemName", resultSet.getString("item_name"));
					itemjson.put("itemDescription", resultSet.getString("item_description"));
					String today = "",tomorrow = "";
					today = resultSet.getString("is_active");
					tomorrow = resultSet.getString("is_active_tomorrow");
					if(today.equalsIgnoreCase("Y")){
						itemjson.put("setName", "");
						itemjson.put("setId", "1");
					}/*else{
						itemjson.put("setName", "");
						itemjson.put("setId", "2");
					}*/
					if(tomorrow.equalsIgnoreCase("Y")){
						itemjson.put("setName", "");
						itemjson.put("setId", "2");
					}/*else{
						itemjson.put("setName", "");
						itemjson.put("setId", "1");
					}*/
					/*itemjson.put("setName", resultSet.getString("set_name"));
					itemjson.put("setId", String.valueOf(resultSet.getInt("set_id")));*/
					
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
