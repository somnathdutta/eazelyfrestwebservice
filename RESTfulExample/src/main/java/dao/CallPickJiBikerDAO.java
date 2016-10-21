package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mkyong.rest.DBConnection;

public class CallPickJiBikerDAO {

	public static JSONObject callPickJi(String orderNO , String kitchenName) throws JSONException {
		JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		try {
				responseJsonObject  = getBikersFromPickJi(createCallPickJiJson(kitchenName, orderNO));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Response json- - - > > "+responseJsonObject.toString());
			String code = responseJsonObject.getString("responseCode");
			if(code.equals("200")){
				System.out.println("200 status code");
				notifiedJsonObject.put("status", responseJsonObject);
			}else {
				notifiedJsonObject.put("status", responseJsonObject);
			}
		return responseJsonObject;
	}
	
	@SuppressWarnings("unchecked")
	public static org.json.simple.JSONObject createCallPickJiJson(String kitchenName, String orderNo) 
			throws JSONException{
		org.json.simple.JSONObject pickJiJson = new org.json.simple.JSONObject();
		//pickJiJson.put("appToken", "c151b4ae5325443152060828f149a7a7");
		pickJiJson.put("appToken", getPickJiApiToken());
		pickJiJson.put("orderID", getPickJiOrderID(kitchenName, orderNo));
		return pickJiJson;
	}
	
	public static JSONObject getBikersFromPickJi(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject respnseJsonObject = null;
   	    System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//String url ="http://api.pickji.com/corporateapi/sandbox/orderdetails";
			//String url ="http://api.pickji.com/corporateapi/orderdetails";
			String url = getPickJiOrderDetailsApi();
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			
			post.setEntity(input);
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	 // System.out.println("Line - - >"+line);
		    	  respnseJsonObject = new JSONObject(line);
		      }
		}catch(UnsupportedEncodingException e){
			
		}catch(JSONException e){
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respnseJsonObject;  
	}
	
	public static String getPickJiOrderID(String kitchenName, String orderNo){
		String pickjiOrderID = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select external_order_id from fapp_order_tracking where kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and order_id ="
							+ " (select order_id from fapp_orders where order_no = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						preparedStatement.setString(2, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							pickjiOrderID = resultSet.getString("external_order_id");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		System.out.println("Pickji order id:: "+pickjiOrderID);
		return pickjiOrderID;
	}

	public static JSONObject getBikerDetailsFromPickJiAPI(String pickJiOrderId) throws JSONException {
		JSONObject pickJiJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		try {
				responseJsonObject  = getBikersFromPickJi(createPickJiJsonForBiker(pickJiOrderId));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String code = responseJsonObject.getString("responseCode");
			if(code.equals("200")){
				System.out.println("200 status code");
				pickJiJsonObject.put("status", "200");
				JSONObject orderDetails = responseJsonObject.getJSONObject("orderDetails");
				pickJiJsonObject.put("bikerName", orderDetails.getString("bikerName")) ;
				pickJiJsonObject.put("bikerContact", orderDetails.getString("bikerMobile")) ;
				pickJiJsonObject.put("pickupTime", orderDetails.getString("pickupTime"));
				pickJiJsonObject.put("deliveryTime", orderDetails.getString("deliveryTime"));
				String position =   orderDetails.getString("bikerPosition");
				String lat = null,lng = null;
				String[] latlng =  position.split(",");
				 for(int i=0;i<latlng.length;i++){
					  lat = latlng[0];
					  lng = latlng[1];
				 }
				 pickJiJsonObject.put("latitude", lat);
				 pickJiJsonObject.put("longitude", lng);
				 pickJiJsonObject.put("title", "Order From EazeLyf");
			}else {
				pickJiJsonObject.put("status", "204");
			}
		return pickJiJsonObject;
	}
	
	@SuppressWarnings("unchecked")
	public static org.json.simple.JSONObject createPickJiJsonForBiker(String pickJiOrderId) 
			throws JSONException{
		org.json.simple.JSONObject pickJiJson = new org.json.simple.JSONObject();
		//pickJiJson.put("appToken", "c151b4ae5325443152060828f149a7a7");
		pickJiJson.put("appToken", getPickJiApiToken());
		pickJiJson.put("orderID", pickJiOrderId);
		return pickJiJson;
	}
	
	/**
	 * This method returns the pickJi API token from database
	 * @return String api Token
	 */
	public static String getPickJiApiToken(){
		String pickjiApiToken = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT pickji_token from fapp_pickji_api_token where is_active ='Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							pickjiApiToken = resultSet.getString("pickji_token");
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		return pickjiApiToken;
	}
	
	/**
	 * This method returns the pickJi placeOrderApi URL API from database
	 * @return String api Token
	 */
	public static String getPickJiPlaceOrderApi(){
		String placeOrderApi = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT place_order_api from fapp_pickji_api_token where is_active ='Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							placeOrderApi = resultSet.getString("place_order_api");
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		return placeOrderApi;
	}
	
	/**
	 * This method returns the pickJi placeOrderApi URL API from database
	 * @return String api Token
	 */
	public static String getPickJiOrderDetailsApi(){
		String orderDetailsApi = "";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT order_details_api from fapp_pickji_api_token where is_active ='Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							orderDetailsApi = resultSet.getString("order_details_api");
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		return orderDetailsApi;
	}
	
	public static String getUserAddress(String pickJiOrderID){
    	String address = null;
    	String delAddress =  null;
    	String pin= null;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql ="SELECT delivery_address,pincode from fapp_order_user_details where order_id = "
    						+" (select order_id from fapp_order_tracking where external_order_id = ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, pickJiOrderID);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							delAddress = resultSet.getString("delivery_address");
							pin = resultSet.getString("pincode");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	address = delAddress +","+pin+","+"Kolkata";
    	return address;
    }
	
}
