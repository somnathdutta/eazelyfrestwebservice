package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class KitchenOrderHistoryDAO {

	public static JSONObject fetchKitchenOrderHistory(String kitchenName){
		JSONObject kitchenOrderHistoryJson = new JSONObject();
		JSONArray kitchenOrderHistoryJsonArray = new JSONArray();
		
			try {
					SQL1:{
							Connection connection = DBConnection.createConnection();
							PreparedStatement preparedStatement = null;
							ResultSet resultSet = null;
							String sql = "select * from vw_all_kitchen_order_history where kitchen_name = ? order by order_id DESC";
							try {
								preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, kitchenName);
								resultSet = preparedStatement.executeQuery();
								while (resultSet.next()) {
									JSONObject orders = new JSONObject();
									if(resultSet.getString("order_by") != null){
										orders.put("orderBy", resultSet.getString("order_by"));
									}else {
										orders.put("orderBy", " ");
									}
									if(resultSet.getString("contact_number")!=null){
										orders.put("contactNumber", resultSet.getString("contact_number"));
									}else{
										orders.put("contactNumber", " ");
									}
									if(resultSet.getString("payment_name")!=null){
										orders.put("payType", resultSet.getString("payment_name"));
									}else{
										orders.put("payType", "");
									}
									if(resultSet.getString("external_order_id")!=null){
										orders.put("pickjiOrderNo", "");
									}else{
										orders.put("pickjiOrderNo", "");
									}
									orders.put("pincode", resultSet.getString("pincode"));	
									orders.put("orderid", resultSet.getInt("order_id"));
									orders.put("orderno", resultSet.getString("order_no"));
									if(resultSet.getString("meal_type")!=null){
										orders.put("mealtype", resultSet.getString("meal_type"));
									}else{
										orders.put("mealtype"," ");
									}
									if(resultSet.getString("time_slot")!=null){
										orders.put("timeslot", resultSet.getString("time_slot"));
									}else{
										orders.put("timeslot"," ");
									}
									if(resultSet.getString("order_status_name")!=null){
										orders.put("orderstatus", resultSet.getString("order_status_name"));
									}else{
										orders.put("orderstatus", " ");
									}
									if( resultSet.getString("delivery_address")!=null){
										orders.put("deliveryaddress", resultSet.getString("delivery_address"));
									}else{
										orders.put("deliveryaddress", " ");
									}
									
									String received = resultSet.getString("received");
									if(received!=null){
										if(received.equals("Y")){
											orders.put("orderreceived", true);
										}else{
											orders.put("orderreceived", false);
										}
									}else{
										orders.put("orderreceived"," ");
									}
									
									String notified =  resultSet.getString("notify");
									if(notified!=null){
										if(notified.equals("Y")){
											orders.put("ordernotified", true);
										}else{
											orders.put("ordernotified", false);
										}
									}else{
										orders.put("ordernotified", " ");
									}
									
									String rejected = resultSet.getString("rejected");
									if(rejected!=null){
										if(rejected.equals("Y")){
											orders.put("orderrejected", true);
										}else{
											orders.put("orderrejected", false);
										}
									}else{
										orders.put("orderrejected", " ");
									}
									String deliverdToBoy = resultSet.getString("delivered_to_boy");
									if(deliverdToBoy!=null){
										if(deliverdToBoy.equals("Y")){
											orders.put("orderdeliveredtoboy", true);
										}else{
											orders.put("orderdeliveredtoboy", false);
										}
									}else{
										orders.put("orderdeliveredtoboy", " ");
									}
									String driverReached = resultSet.getString("driver_reached");
									if(driverReached.equals("Y")){
										orders.put("driverReached", true);
									}else{
										orders.put("driverReached", false);
									}
									if(resultSet.getString("driver_name")!=null){
										orders.put("boyName", resultSet.getString("driver_name"));
									}else{
										orders.put("boyName", "");
									}
									if(resultSet.getString("driver_number")!=null){
										orders.put("boyPhoneNo", resultSet.getString("driver_number"));
									}else{
										orders.put("boyPhoneNo", "");
									}
									
									String orderDate = resultSet.getString("order_date");
									String deliveryDate = resultSet.getString("delivery_date");
									String reformattedStartDate = "",reformattedEndDate = "", reformattedOrderDate="",reformattedDeliveryDate="";
									SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
									SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
									
									orders.put("startdate", " ");
									orders.put("enddate", " ");
									
									try {
									    reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
									    reformattedDeliveryDate = myFormat.format(fromUser.parse(deliveryDate));
									} catch (ParseException e) {
									    e.printStackTrace();
									}
									orders.put("orderdate", reformattedOrderDate);
									orders.put("deliveryDate", reformattedDeliveryDate);
									
									orders.put("itemdetails", DBConnection.getitemdetailsOfKitchen( orders.getString("orderno"), kitchenName, connection) );
									
									kitchenOrderHistoryJsonArray.put(orders);
								}
								
								
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							finally{
								if(preparedStatement != null){
									preparedStatement.close();
								}
								if(connection != null){
									connection.close();
								}
							}
						}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Total orders history for kitchen:: "+kitchenOrderHistoryJsonArray.length());
			try {
				if(kitchenOrderHistoryJsonArray.length()>0){
					kitchenOrderHistoryJson.put("status", "200");
					kitchenOrderHistoryJson.put("message", "Data Found");
					kitchenOrderHistoryJson.put("ordertrack", kitchenOrderHistoryJsonArray);
					
				}else {
					kitchenOrderHistoryJson.put("status", "204");
					kitchenOrderHistoryJson.put("message", "Data Not Found");
					kitchenOrderHistoryJson.put("ordertrack", new JSONArray());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return kitchenOrderHistoryJson;
	}
}
