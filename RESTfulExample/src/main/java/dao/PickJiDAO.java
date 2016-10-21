package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mkyong.rest.DBConnection;

import pojo.PickJi;
import pojo.PickjiItem;
import utility.DateTimeSlotFinder;

public class PickJiDAO {

	/**
	 * 
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	public static JSONObject placeOrderToPickJi(String orderNo , String kitchenName) throws JSONException {
		//JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		//Boolean orderNotified = false;
		String pickJiOrderID = "";
		try {
				responseJsonObject  = placeOrderByPost(createPickJiJson(kitchenName, orderNo));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Response json- - - > > "+responseJsonObject.toString());
			//JSONObject status = responseJsonObject.getJSONObject("status");
			String code = responseJsonObject.getString("responseCode");
			if(code.equals("200")){
				pickJiOrderID = responseJsonObject.getString("orderID");
				System.out.println("Pickji order ID:: "+pickJiOrderID);
				System.out.println(responseJsonObject.getString("responseMessage"));
				//notifiedJsonObject.put("status", responseJsonObject);
			}else {
				//notifiedJsonObject.put("status", responseJsonObject);
			}
			
		return responseJsonObject;
	}
	
	/**
	 * PIckji JSON creation
	 * @return
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public static org.json.simple.JSONObject createPickJiJson(String kitchenName, String orderNo) throws JSONException{
		org.json.simple.JSONObject pickJiJson = new org.json.simple.JSONObject();
		PickJi pickji = getPickUp(kitchenName,orderNo);
		pickJiJson.put("appToken", pickji.getAppToken());
		pickJiJson.put("orderTitle", pickji.getOrderTitle());
		pickJiJson.put("pickupAddress", pickji.getPickupAddress());
		pickJiJson.put("pickupArea", pickji.getPickupArea());
		pickJiJson.put("pickupPincode", pickji.getPickupPincode());
		pickJiJson.put("pickupFromName", pickji.getPickupFromName());
		pickJiJson.put("pickupMobileNo", pickji.getPickupMobileNo());
		
		pickJiJson.put("deliveryAddress", pickji.getDeliveryAddress());
		pickJiJson.put("deliveryArea", pickji.getDeliveryArea());
		pickJiJson.put("deliveryPincode", pickji.getDeliveryPincode());
		pickJiJson.put("deliveryToName", pickji.getDeliveryToName());
		pickJiJson.put("deliveryMobileNo", pickji.getDeliveryMobileNo());
		pickJiJson.put("cashToCollect", pickji.getCashToCollect());
		
		pickJiJson.put("schedulePickupTime", pickji.getSchedulePickupTime());
		pickJiJson.put("scheduleDeliveryTime", pickji.getScheduleDeliveryTime());
		pickJiJson.put("quantityDetails", pickji.getQuantityDetails());
		pickJiJson.put("hasPillon", pickji.getHasPillon());
		
		return pickJiJson;
	}
	
	/**
	 * PickJi Api Calling service
	 */
	private static JSONObject placeOrderByPost(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	    System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//client = (DefaultHttpClient) wrapClient(client);
			/* TESTING URL */
			//String url="https://apitest.roadrunnr.in/v1/orders/ship";
			/* LIVE URL :  https://runnr.in/v1/orders/ship
			//String url="http://roadrunnr.in/v1/orders/ship";
			//New url*/
			//String url ="http://api.pickji.com/corporateapi/sandbox/placeorder";
			//String url ="http://api.pickji.com/corporateapi/placeorder";
			String url = CallPickJiBikerDAO.getPickJiPlaceOrderApi();
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			//post.addHeader("Authorization" , generateAuthToken());
			//post.addHeader("Authorization" ,getToken(false));
			
			post.setEntity(input);
			//System.out.println("StringEntity - - ->"+input.toString());
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	//  System.out.println("Line - - >"+line);
		    	  newJObject = new JSONObject(line);
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
		return newJObject;  
	}
	
	public static PickJi getPickUp(String kitchenName, String orderNo){
		PickJi pickUp = new PickJi();
		//pickUp.setAppToken("c151b4ae5325443152060828f149a7a7");
		pickUp.setAppToken(CallPickJiBikerDAO.getPickJiApiToken());
		pickUp.setOrderTitle("EAZELYF ORDER");
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "Select address,pincode,kitchen_name,mobile_no from vw_kitchens_details where kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							pickUp.setPickupAddress(resultSet.getString("address"));
							pickUp.setPickupArea(resultSet.getString("address"));
							pickUp.setPickupFromName(resultSet.getString("kitchen_name"));
							pickUp.setPickupMobileNo(resultSet.getString("mobile_no"));
							pickUp.setPickupPincode(resultSet.getString("pincode"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		
			SQL:{
				 	PreparedStatement preparedStatement = null;
				 	ResultSet resultSet = null;
				 	String sql = "select * from vw_orders_delivery_address where order_no = ?";
				 	try {
						preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							pickUp.setDeliveryAddress(resultSet.getString("delivery_address"));
							String city = resultSet.getString("city");
							String pincode = resultSet.getString("pincode");
							String zone = resultSet.getString("delivery_zone");
							pickUp.setDeliveryArea(zone+","+"Kolkata"+","+pincode);
							pickUp.setDeliveryPincode(pincode);
							pickUp.setDeliveryMobileNo(resultSet.getString("contact_number"));
							pickUp.setDeliveryToName(resultSet.getString("order_by"));
							String timeSlotValue = resultSet.getString("time_slot");
							String dateValue = resultSet.getString("order_date");
							String[] timeValues = DateTimeSlotFinder.findDateTime(dateValue, timeSlotValue);
							pickUp.setSchedulePickupTime(timeValues[0]);
							pickUp.setScheduleDeliveryTime(timeValues[1]);
						}
				 	} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
			
			SQL:{
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 String sql = "select voi.category_name,voi.item_name,voi.qty,voi.total_price,final_price from vw_order_item_details_list voi "
							 +" join fapp_orders fo on fo.order_no = voi.order_no "
							 +"	 where voi.order_no =? and kitchen_name =?";
				 Set<Double> finalPrice = new HashSet<Double>();
				 ArrayList<PickjiItem> orders = new ArrayList<PickjiItem>();
				 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					preparedStatement.setString(2, kitchenName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						finalPrice.add(resultSet.getDouble("final_price"));
						pickUp.setCashToCollect(finalPrice.toString());
						PickjiItem order = new PickjiItem();
						order.categoryName = resultSet.getString("category_name");
						order.itemName = resultSet.getString("item_name");
						order.quantity = resultSet.getInt("qty");
						order.price = resultSet.getDouble("total_price");
						orders.add(order);
					}
					pickUp.setQuantityDetails(orders.toString());
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
		
		return pickUp;
	}
}
