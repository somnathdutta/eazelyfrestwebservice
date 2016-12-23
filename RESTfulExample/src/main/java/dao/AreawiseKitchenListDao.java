package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import sql.AreawiseKitchenListSql;

import com.mkyong.rest.DBConnection;

public class AreawiseKitchenListDao {

	public static JSONObject fetchKitchens(String area){
		JSONObject finalObject = new JSONObject();
		JSONArray kitchenList = new JSONArray();
		try {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;	
		
				try {
				connection = DBConnection.createConnection();	
				preparedStatement = connection.prepareStatement(AreawiseKitchenListSql.fetchKitchenSql);	
				preparedStatement.setString(1, "%"+area+"%");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject kitchenObj = new JSONObject();
					
					if(resultSet.getInt("kitchen_id")>0){
						kitchenObj.put("kitchenId", resultSet.getInt("kitchen_id"));
					}else {
						kitchenObj.put("kitchenId", "");
					}
					
					if(resultSet.getString("kitchen_name") != null){
						kitchenObj.put("kitchenName", resultSet.getString("kitchen_name"));
					}else {
						kitchenObj.put("kitchenName", "");
					}
					
					if(resultSet.getString("address") != null){
						kitchenObj.put("kitchenAddress", resultSet.getString("address"));
					}else {
						kitchenObj.put("kitchenAddress", "");
					}
					
					if(resultSet.getString("mobile_no") !=null){
						kitchenObj.put("kitchenContactNo", resultSet.getString("mobile_no"));
					}else {
						kitchenObj.put("kitchenContactNo", "");
					}
					
					if(resultSet.getString("image") != null){
						String img =  resultSet.getString("image");
						if(img.startsWith("http://")){
							img.replace("http://", "");
						}		
						kitchenObj.put("image", img);
					}else {
						kitchenObj.put("image", "");
					}
					
					kitchenList.put(kitchenObj);
					
				}
					
				} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			if(connection != null){
				connection.close();
			}
			if(preparedStatement != null){
				preparedStatement.close();
			}
			if(resultSet != null){
				resultSet.close();
			}
		}
			if(kitchenList.length()>0){
				finalObject.put("status", "200");
				finalObject.put("message", "kitchen found");
				finalObject.put("kitchenList", kitchenList);
			}else {
				finalObject.put("status", "204");
				finalObject.put("message", "not found");
				finalObject.put("kitchenList", new JSONArray());
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return finalObject;
	}
	
	
	
}
