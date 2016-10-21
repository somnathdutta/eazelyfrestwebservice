package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class OrderTimingsDAO {

	public static JSONObject getOrderTimings() throws JSONException{
		JSONObject orderTimings = new JSONObject();
		boolean timingFound = false;
		try {
			SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "select lunch_from,lunch_to,dinner_from,dinner_to,timings_id from fapp_timings";
						try {
							preparedStatement = connection.prepareStatement(sql);
							resultSet = preparedStatement.executeQuery();
							if(resultSet.next()){
								 timingFound = true;
								 orderTimings.put("status", "200");
								 orderTimings.put("message","Order timings found!");
								 JSONArray mealArray = new JSONArray();
								
								 if(resultSet.getString("lunch_from")!=null && resultSet.getString("lunch_to")!=null){
									 JSONObject lunch = new JSONObject();
									 lunch.put("mealtype", "LUNCH");
									 lunch.put("orderOpenTime", resultSet.getString("lunch_from"));
									 lunch.put("orderCloseTime", resultSet.getString("lunch_to"));
									 mealArray.put(lunch);
								 }
								 if(resultSet.getString("dinner_from")!=null && resultSet.getString("dinner_to")!=null){
									 JSONObject dinner = new JSONObject();
									 dinner.put("mealtype", "DINNER");
									 dinner.put("orderOpenTime", resultSet.getString("dinner_from"));
									 dinner.put("orderCloseTime", resultSet.getString("dinner_to"));
									 mealArray.put(dinner);
								 }
								 orderTimings.put("timingList", mealArray);
								 
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(!timingFound){
			 orderTimings.put("status", "204");
			 orderTimings.put("message","No Order timings found!");
			 orderTimings.put("timingList", new JSONArray());
		}
		return orderTimings;
	}
}
