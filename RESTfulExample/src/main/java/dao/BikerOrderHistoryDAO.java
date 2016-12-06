package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import utility.LatLng;

import com.mkyong.rest.DBConnection;

public class BikerOrderHistoryDAO {

	 public static JSONObject getdeliveryordersforbiker(String deliveryBoyUserId) throws Exception{
			JSONObject deliveryList = new JSONObject();
			JSONArray orderArrayValue = new JSONArray();
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement  preparedStatement = null;
						ResultSet resultSet = null;
						String sql ="select * from vw_driver_orders_history where driver_boy_user_id = ? ";
						try {
							preparedStatement= connection.prepareStatement(sql);
							preparedStatement.setString(1, deliveryBoyUserId);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next() ) {
								JSONObject jsonObject = new JSONObject();
								String orderNo = resultSet.getString("order_no");	
								String finalPrice = String.valueOf(resultSet.getDouble("final_price"));
								if(finalPrice!=null){
									jsonObject.put("finalPrice", finalPrice);
								}else{
									jsonObject.put("finalPrice", "");
								}
								String kitchenName = resultSet.getString("kitchen_name");
								String orderPicked = resultSet.getString("order_picked");
								String driverReached = resultSet.getString("driver_reached");
								jsonObject.put("orderno", orderNo);
								if(orderPicked.equalsIgnoreCase("Y")){
									jsonObject.put("picked",true );
								}else{
									jsonObject.put("picked", false);
								}
								if(driverReached.equalsIgnoreCase("Y")){
									jsonObject.put("driverReached",true );
								}else{
									jsonObject.put("driverReached", false);
								}
								/*if(resultSet.getString("payment_name").equalsIgnoreCase("CARD")){
									orders.put("payType", "PAID");
								}else {
									orders.put("payType", resultSet.getString("payment_name"));
								}*/
								String paymentName = resultSet.getString("payment_name");
								if(paymentName!= null){
									if(resultSet.getString("payment_name").equalsIgnoreCase("CARD")){
										jsonObject.put("paymenttype", "PAID BY CARD");
									}else {
										jsonObject.put("paymenttype", paymentName);
									}
								}else{
									jsonObject.put("paymenttype", " ");
								}
								jsonObject.put("mealtype", resultSet.getString("meal_type"));
								jsonObject.put("timeslot", resultSet.getString("time_slot"));
								jsonObject.put("kitchenname", kitchenName);
								jsonObject.put("kitchenmobileno", resultSet.getString("mobile_no"));
								jsonObject.put("kitchenaddress", resultSet.getString("address"));
								jsonObject.put("itemdetails", DBConnection.getKitchenItemdetails(orderNo, kitchenName));
								jsonObject.put("orderby", resultSet.getString("order_by"));
								jsonObject.put("contactnumber", resultSet.getString("contact_number"));
								jsonObject.put("deliveryzone", resultSet.getString("delivery_zone"));
								String deliveryaddress = resultSet.getString("delivery_address");
								String pincode = resultSet.getString("pincode");
								jsonObject.put("deliveryaddress", deliveryaddress);
								String landmark = resultSet.getString("instruction");
								if(landmark!=null){
									jsonObject.put("landmark", landmark);
								}else{
									jsonObject.put("landmark", "- - NOT GIVEN - -");
								}
								jsonObject.put("pincode", pincode);
								String lat="",lng="";
								String latLongs[] = LatLng.getLatLongPositions(deliveryaddress+" , "+pincode);
							    if(latLongs[0].equals("LAT") && latLongs[1].equals("LONG")){
							    	System.out.println("Invalid addres!");
							    	//lat = null;lng=null;
							    }else{
							    	System.out.println("SUCESS Latitude: "+latLongs[0]+" and Longitude: "+latLongs[1]);
							    	lat = latLongs[0];lng= latLongs[1];
							    }
								jsonObject.put("paymentname", resultSet.getString("payment_name"));
								jsonObject.put("latitude", lat);
								jsonObject.put("longitude", lng);
								
								orderArrayValue.put(jsonObject);
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
			System.out.println("No of orders:: "+orderArrayValue.length());
			return deliveryList.put("orderlist", orderArrayValue);
	    }
}
