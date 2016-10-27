package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

import pojo.Prepack;
import pojo.User;
import sql.SubscriptionPrePackQuery;

public class PlaceSubscriptionOrderDAO {

	public static JSONObject checkOut(User user,Prepack prepack, ArrayList<OrderItems> orderItemList,boolean isMultiAddress){
		JSONObject subscriptionJson = new JSONObject();
		try {
			Connection connection = DBConnection.createConnection();
			try {
				if(placeSubscriptionOrder(user, prepack, orderItemList, isMultiAddress,connection)){
					subscriptionJson.put("status", "200");
					subscriptionJson.put("message", getRecentOrder(getMaxOrderId(connection), isMultiAddress ));	
				}else{
					subscriptionJson.put("status", "204");
					subscriptionJson.put("message", new JSONObject());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				if(connection!=null){
					connection.close();
				}
			}
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return subscriptionJson;
	}
	
	/**
	 * Placing subscription order
	 * @param user
	 * @param prepack
	 * @param orderItemList
	 * @param isMultiAddress
	 * @param connection
	 * @return
	 */
	public static boolean placeSubscriptionOrder(User user, Prepack prepack, ArrayList<OrderItems> orderItemList,
			boolean isMultiAddress,Connection connection){
		int maxOrderId = 0;boolean itemInserted = false;
		try {
			
			/**** *** *** *** *** ***
			 *  Insert master prepack data sql block starts here *** *** *** ***
			 **** *** *** ***  ***/
			SQL:{
				 String orderNo = generateSubcriptionNo(connection);
				 PreparedStatement preparedStatement = null;
				 try {
					preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.insertMasterPackQuery);
					preparedStatement.setString(1, orderNo);
					preparedStatement.setDouble(2, prepack.getPackPrice());
					preparedStatement.setString(3, user.getContactNumber());
					preparedStatement.setString(4, user.getEmailId());
					preparedStatement.setString(5, prepack.getPackType());
					preparedStatement.setString(6, prepack.getPackDay());
					preparedStatement.setString(7, prepack.getMealType());
					preparedStatement.setString(8, user.getUserName());
					preparedStatement.setString(9, prepack.getPaymentName());
					int insertCount = preparedStatement.executeUpdate();
					if(insertCount > 0){
						System.out.println("Subscription "+prepack.getPackType()+" master data inserted successfully!");
						maxOrderId = getMaxOrderId(connection);
					}
				} catch (Exception e) {
					// TODO: handle exception
					connection.rollback();
					e.printStackTrace();
				}finally{
					if(preparedStatement != null){
						preparedStatement.close();
					}
				}
			}
			/**** *** *** *** *** ***
			 *  Insert master prepack data sql block ending here *** *** *** ***
			 **** *** *** ***  ***/
			if (maxOrderId > 0) {
				itemInserted = saveItems(connection, prepack, orderItemList, isMultiAddress, maxOrderId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return itemInserted;
	}
	
	public static boolean saveItems(Connection connection, Prepack prepack, 
			ArrayList<OrderItems> orderItemList, boolean isMultiAddress, int maxOrderId ){
		boolean itemInserted = false;
		try {
			/**** *** *** *** *** ***
			 *  Insert prepack items data sql block starts here *** *** *** ***
			 **** *** *** ***  ***/
			SQL:{
				PreparedStatement preparedStatement = null;
				try {
					preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.insertItemsPackQuery);
					for(OrderItems item : orderItemList){
						preparedStatement.setInt(1, maxOrderId);
						preparedStatement.setInt(2, item.cuisineId);
						preparedStatement.setInt(3, item.categoryId);
						preparedStatement.setString(4, item.itemCode);
						preparedStatement.setInt(5, item.quantity);
						preparedStatement.setDouble(6, (item.price*item.quantity) );
						preparedStatement.setString(7, item.day);
						preparedStatement.setString(8, item.meal);
						if(isMultiAddress){
							if( item.day.equalsIgnoreCase("MONDAY") || item.day.equalsIgnoreCase("TUESDAY") ||
							    item.day.equalsIgnoreCase("WEDNESDAY") || item.day.equalsIgnoreCase("THURSDAY")  ||
							    item.day.equalsIgnoreCase("FRIDAY") ){
								preparedStatement.setString(9, prepack.getDinnerTimeSlot());
								preparedStatement.setString(10, prepack.getDinnerDeliveryZone());
								preparedStatement.setString(11, prepack.getDinnerDeliveryAddress());
								preparedStatement.setString(12, prepack.getDinnerPincode());
								preparedStatement.setString(13, prepack.getDinnerInstruction());
								preparedStatement.setString(14, prepack.getDinnerName());
								preparedStatement.setString(15, prepack.getDinnerMailid());
								preparedStatement.setString(16, prepack.getDinnerContactNumber());
								preparedStatement.setString(17, prepack.getDinnerAddressType());
								
							}else{
								preparedStatement.setString(9, prepack.getLunchTimeSlot());
								preparedStatement.setString(10, prepack.getLunchDeliveryZone());
								preparedStatement.setString(11, prepack.getLunchDeliveryAddress());
								preparedStatement.setString(12, prepack.getLunchPincode());
								preparedStatement.setString(13, prepack.getLunchInstruction());
								preparedStatement.setString(14, prepack.getLunchName());
								preparedStatement.setString(15, prepack.getLunchMailid());
								preparedStatement.setString(16, prepack.getLunchContactNumber());
								preparedStatement.setString(17, prepack.getLunchAddressType());
							}
						}else{
								preparedStatement.setString(9, prepack.getSameTimeSlot());
								preparedStatement.setString(10, prepack.getSameDeliveryZone());
								preparedStatement.setString(11, prepack.getSameDeliveryAddress());
								preparedStatement.setString(12, prepack.getSamePincode());
								preparedStatement.setString(13, prepack.getSameInstruction());
								preparedStatement.setString(14, prepack.getSameName());
								preparedStatement.setString(15, prepack.getSameMailid());
								preparedStatement.setString(16, prepack.getSameContactNumber());
								preparedStatement.setString(17, prepack.getSameAddressType());
						}
						preparedStatement.addBatch();
					}
					int count[] = preparedStatement.executeBatch();
					for(Integer c : count){
						itemInserted = true;
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					connection.rollback();
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					/*if(connection!=null){
						connection.close();
					}*/
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(itemInserted){
			System.out.println("Items inserted successfully!");
		}else{
			System.out.println("Items insertion failed!");
		}
		return itemInserted;
	}
	
	public static JSONObject getRecentOrder(int orderId, boolean isMultiAddress){
		JSONObject  valueJson = new JSONObject();
		try {
				Connection connection = DBConnection.createConnection();
			SQL:{
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 try {
					preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.recentOrderQuery);
					preparedStatement.setInt(1, orderId);
					resultSet = preparedStatement.executeQuery();
					if(resultSet.next()){
						valueJson.put("orderNo", resultSet.getString("order_no"));
						valueJson.put("packPrice",resultSet.getDouble("total_price"));
						valueJson.put("userName",resultSet.getString("order_by"));
						valueJson.put("user",resultSet.getString("contact_number"));
						valueJson.put("packType", resultSet.getString("pack_type"));
						valueJson.put("packDay",resultSet.getString("pack_day"));
						valueJson.put("mealType", resultSet.getString("meal_type"));
						String orderDate="",reformattedOrderDate="";
						SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
						orderDate = resultSet.getString("order_date");
						try {
							reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						valueJson.put("orderDate", reformattedOrderDate);
						valueJson.put("addressList", getAddressDetails(orderId, connection, isMultiAddress));
						valueJson.put("packItemDetails", getPackItems(orderId,connection));
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
		return valueJson;
	}
	
	public static JSONArray getPackItems(int orderId , Connection connection){
		JSONArray packItemList = new JSONArray();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.itemDetailsQuery);
						preparedStatement.setInt(1, orderId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject items = new JSONObject();
							items.put("quantity", resultSet.getInt("quantity")) ;
							items.put("price", resultSet.getDouble("price"))  ;
							items.put("day",resultSet.getString("day_name") );
							items.put("mealType", resultSet.getString("meal_type") );
							items.put("cuisinName", resultSet.getString("cuisin_name") );
							items.put("categoryName", resultSet.getString("category_name") );
							items.put("itemName", resultSet.getString("item_name") );
							packItemList.put(items);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return packItemList;
	}
	
	public static JSONArray getAddressDetails(int orderId, Connection connection, boolean isMultiAddress){
		JSONArray addressArray = new JSONArray();
		Set<Prepack> addressSet = new HashSet<Prepack>();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						if(isMultiAddress){
							preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.multiAddressdetailsQuery);
							preparedStatement.setInt(1, orderId);
							System.out.println("Multi address query-  - -"+preparedStatement);
						}else{
							preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.singleAddressdetailsQuery);
							preparedStatement.setInt(1, orderId);
							System.out.println("Single address query-  - -"+preparedStatement);
						}
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject address = new JSONObject();
							//Prepack addressobj = new Prepack();
							if(isMultiAddress){
								if(resultSet.getString("day_name").equalsIgnoreCase("SUNDAY")){
									
									/*addressobj.setSameContactNumber(resultSet.getString("contact_number"));
									addressobj.setSameName(resultSet.getString("name"));
									addressobj.setSameTimeSlot(resultSet.getString("time_slot"));
									addressobj.setSameDeliveryAddress(resultSet.getString("delivery_address"));
									addressobj.setSameDeliveryZone(resultSet.getString("delivery_zone"));
									addressobj.setSameInstruction( resultSet.getString("instruction"));
									addressobj.setSamePincode(resultSet.getString("delivery_pincode"));
									addressSet.add(addressobj);
									System.out.println("in sunday address set size ::"+addressSet.size());
									for(Prepack prepack : addressSet ){
										address.put("name", prepack.getSameName());
										address.put("contactNumber",prepack.getSameContactNumber());
										address.put("timeslot", prepack.getSameTimeSlot());
										address.put("deliveryAddress", prepack.getSameDeliveryAddress());
										address.put("deliveryZone",prepack.getSameDeliveryZone());
										address.put("deliveryPincode", prepack.getSamePincode());
										address.put("instruction", prepack.getSameInstruction());
										address.put("days", "SATURDAY-SUNDAY");
									}*/
									
									address.put("name", resultSet.getString("name"));
									address.put("contactNumber", resultSet.getString("contact_number"));
									address.put("timeslot", resultSet.getString("time_slot"));
									address.put("deliveryAddress", resultSet.getString("delivery_address"));
									address.put("deliveryZone", resultSet.getString("delivery_zone"));
									address.put("deliveryPincode", resultSet.getString("delivery_pincode"));
									address.put("instruction", resultSet.getString("instruction"));
									address.put("addressType", resultSet.getString("address_type"));
									address.put("days", "SATURDAY-SUNDAY");
								}
								if(resultSet.getString("day_name").equalsIgnoreCase("MONDAY")){
									/*//Prepack addressobj = new Prepack();
									System.out.println("in monday address set size ::"+addressSet.size());
									addressobj.setSameContactNumber(resultSet.getString("contact_number"));
									addressobj.setSameName(resultSet.getString("name"));
									addressobj.setSameTimeSlot(resultSet.getString("time_slot"));
									addressobj.setSameDeliveryAddress(resultSet.getString("delivery_address"));
									addressobj.setSameDeliveryZone(resultSet.getString("delivery_zone"));
									addressobj.setSameInstruction( resultSet.getString("instruction"));
									addressobj.setSamePincode(resultSet.getString("delivery_pincode"));
									addressSet.add(addressobj);
									
									for(Prepack prepack : addressSet ){
										address.put("name", prepack.getSameName());
										address.put("contactNumber",prepack.getSameContactNumber());
										address.put("timeslot", prepack.getSameTimeSlot());
										address.put("deliveryAddress", prepack.getSameDeliveryAddress());
										address.put("deliveryZone",prepack.getSameDeliveryZone());
										address.put("deliveryPincode", prepack.getSamePincode());
										address.put("instruction", prepack.getSameInstruction());
										address.put("days", "MONDAY-FRIDAY");
									}*/
									address.put("name", resultSet.getString("name"));
									address.put("contactNumber", resultSet.getString("contact_number"));
									address.put("timeslot", resultSet.getString("time_slot"));
									address.put("deliveryAddress", resultSet.getString("delivery_address"));
									address.put("deliveryZone", resultSet.getString("delivery_zone"));
									address.put("deliveryPincode", resultSet.getString("delivery_pincode"));
									address.put("instruction", resultSet.getString("instruction"));
									address.put("addressType", resultSet.getString("address_type"));
									address.put("days", "MONDAY-FRIDAY");
								}
								addressArray.put(address);
							}else{
								/*System.out.println("in all address set size ::"+addressSet.size());
								//Prepack addressobj = new Prepack();
								addressobj.setSameContactNumber(resultSet.getString("contact_number"));
								addressobj.setSameName(resultSet.getString("name"));
								addressobj.setSameTimeSlot(resultSet.getString("time_slot"));
								addressobj.setSameDeliveryAddress(resultSet.getString("delivery_address"));
								addressobj.setSameDeliveryZone(resultSet.getString("delivery_zone"));
								addressobj.setSameInstruction( resultSet.getString("instruction"));
								addressobj.setSamePincode(resultSet.getString("delivery_pincode"));
								addressSet.add(addressobj);
								
								for(Prepack prepack : addressSet ){
									address.put("name", prepack.getSameName());
									address.put("contactNumber",prepack.getSameContactNumber());
									address.put("timeslot", prepack.getSameTimeSlot());
									address.put("deliveryAddress", prepack.getSameDeliveryAddress());
									address.put("deliveryZone",prepack.getSameDeliveryZone());
									address.put("deliveryPincode", prepack.getSamePincode());
									address.put("instruction", prepack.getSameInstruction());
									address.put("days", "MONDAY-FRIDAY");
								}*/
								
								address.put("name", resultSet.getString("name"));
								address.put("contactNumber", resultSet.getString("contact_number"));
								address.put("timeslot", resultSet.getString("time_slot"));
								address.put("deliveryAddress", resultSet.getString("delivery_address"));
								address.put("deliveryZone", resultSet.getString("delivery_zone"));
								address.put("deliveryPincode", resultSet.getString("delivery_pincode"));
								address.put("instruction", resultSet.getString("instruction"));
								address.put("addressType", resultSet.getString("address_type"));
								address.put("days", "ALL DAYS");
								addressArray.put(address);
							}
						}
					} /*catch (Exception e) {
						e.printStackTrace();
					}*/finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(addressArray);
		return addressArray;
	}
	
	public JSONArray getPackDetails(ArrayList<OrderItems> orderItemList){
		JSONArray detailsArray = new JSONArray();
		try {
			for(OrderItems order : orderItemList){
				JSONObject item = new JSONObject();
				item.put("itemName", order.getItemName());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return detailsArray;
	}

	
	/**
	 * Get max orderId for subscription order
	 * @return
	 */
	public static int getMaxOrderId(Connection connection){
		int serialOrderid=0;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.maxIdQuery);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							serialOrderid = resultSet.getInt(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("Max Order Id: : "+serialOrderid);
		return serialOrderid; 
	}
	
	
	/**
	 * Generate order No for subscription order
	 * @return
	 */
	public static String generateSubcriptionNo(Connection connection){
		String subscriptionNumber = "";
		int serialorderid=0;
		try {
			 serialorderid = getMaxOrderId(connection);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month=(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String newDay = "",newMonth = "";
		if (day>=1 && day<10){
		 newDay = "0"+String.valueOf(day);
		}else{
			newDay = String.valueOf(day);
		}
		if(month>=1 && month<10){
			newMonth = "0"+String.valueOf(month);
		}else{
			newMonth = String.valueOf(month);
		}
	    subscriptionNumber = "SUB/"+newDay+"/"+newMonth+"/"+String.format("%06d", serialorderid+1);
		System.out.println("Subscription number->"+subscriptionNumber);
		return subscriptionNumber;
	}

}
