package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class FetchLocationDAO {

	public static Map<String, String> kitchenServingLocations(){
		ArrayList<String> servingCodes = new ArrayList<String>();
		Map<String, String> servingMap = new HashMap<String, String>();
		System.out.println("Kitchen zipcodes finding...");
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select serving_zipcodes from fapp_kitchen where is_active= 'Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							servingCodes.add( resultSet.getString("serving_zipcodes"));
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
		for(String str : servingCodes){
	    	String[] slot = str.split("/");
	    	for(int i=0;i<slot.length;i++){
	    		servingMap.put(slot[i], "zip");
	    	}
		}
		//System.out.println(servingMap);
		return servingMap;
	}
	
	public static Map<String, String> kitchenServingAreas(){
		ArrayList<String> servingAreas = new ArrayList<String>();
		Map<String, String> servingMap = new HashMap<String, String>();
		System.out.println("Kitchen areas finding...");
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select serving_areas from fapp_kitchen where is_active= 'Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							servingAreas.add( resultSet.getString("serving_areas"));
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
		for(String str : servingAreas){
	    	String[] slot = str.split("\\$");
	    	for(int i=0;i<slot.length;i++){
	    		servingMap.put(slot[i], " ");
	    	}
		}
		//System.out.println(servingMap);
		return servingMap;
	}
	
	public static JSONArray fetchZipWithKitchenListArray(Map<String, String> locationMap) throws JSONException{
		JSONArray servingCodeList = new JSONArray();
		Map<String, String> kitchenServingZips = kitchenServingLocations();
		for (Map.Entry<String, String> me : kitchenServingZips.entrySet()){ // assuming your map is Map<String, String>
		    if(locationMap.containsKey(me.getKey())){
		       // System.out.println("Found Active zip -> " + me.getKey()+" value: "+locationMap.get(me.getKey()));
		        JSONObject zip = new JSONObject();
	    		zip.put("zipcode", me.getKey());
	    		zip.put("servingarea", locationMap.get(me.getKey()).toUpperCase());
	    		servingCodeList.put(zip);
		    }
		}
		return servingCodeList;
	}
}
