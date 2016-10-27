package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sql.AllItemSqlQuery;

import com.mkyong.rest.DBConnection;

public class AllItemsDAO {

	public static JSONObject fetchAllItems() throws JSONException{
		JSONObject itemJson = new JSONObject();
		JSONArray itemJsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(AllItemSqlQuery.allItemQuery);
						resultSet = preparedStatement.executeQuery();
						while ( resultSet.next() ) {
							JSONObject item = new JSONObject();
							item.put("cuisineId", resultSet.getInt("cuisine_id"));
							item.put("cuisineName", resultSet.getString("cuisin_name"));
							item.put("categoryId", resultSet.getInt("category_id"));
							item.put("categoryName", resultSet.getString("category_name"));
							item.put("itemId",  resultSet.getInt("item_id"));
							item.put("itemCode",  resultSet.getString("item_code"));
							item.put("itemPrice", resultSet.getDouble("item_price"));
							item.put("itemQty", 0);
							item.put("itemDescription", resultSet.getString("item_description"));
							String tempImage = resultSet.getString("item_image");
							String itemImage ; 
							if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
								itemImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}else if(tempImage.startsWith("http://")){
								itemImage = tempImage.replace("http://", "");
							}else{
								itemImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}
							item.put("itemImage",itemImage );
							
							itemJsonArray.put(item);
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
		if(itemJsonArray.length()>0){
			itemJson.put("items", itemJsonArray);
		}else{
			itemJson.put("items", itemJsonArray);
		}
		return itemJson;
	}
}
