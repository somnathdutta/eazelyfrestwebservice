package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Period;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;
import pojo.TimeSlot;

import com.mkyong.rest.DBConnection;

public class BookDriver {

	/**
	 * Update slot table with driver 
	 * and quantity + ordered quantity and no of order+1
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean bookDriverSlot(MealTypePojo mealTypePojo ){
		boolean booked = false;
		try {
			SQL:{
			      Connection connection = DBConnection.createConnection();
			      PreparedStatement preparedStatement = null;
			      String sql = "";
			      
			      if(mealTypePojo.isLunchToday()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status "
				    		  	+" SET assigned_date=current_date,quantity=quantity+?, no_of_orders=no_of_orders+1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else if(mealTypePojo.isDinnerToday()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status "
				    		  	+" SET assigned_date=current_date,quantity=quantity+?, no_of_orders=no_of_orders+1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else if(mealTypePojo.isLunchTomorrow()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status_tommorrow "
				    		  	+" SET assigned_date=current_date,quantity=quantity+?, no_of_orders=no_of_orders+1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else{
			    	  sql  = "UPDATE fapp_timeslot_driver_status_tommorrow "
				    		  	+" SET assigned_date=current_date,quantity=quantity+?, no_of_orders=no_of_orders+1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }
			      
				  try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, mealTypePojo.getQuantity());
						preparedStatement.setString(2, mealTypePojo.getBoyUSerId());
						preparedStatement.setInt(3, mealTypePojo.getSlotId());
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							booked = true;
						}
							
				  } catch (Exception e) {
						// TODO: handle exception
					  System.out.println(e);
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
		if(booked){
			System.out.println("Slot "+mealTypePojo.getSlotId()+" booked for boy id:: "+mealTypePojo.getBoyUSerId());
		}else{
			System.out.println("Slot "+mealTypePojo.getSlotId()+" NOT BOOKED for boy id:: "+mealTypePojo.getBoyUSerId());
		}
		return booked;
	}
	
	/**
	 * Update slot table after delivery completion 
	 * with driver and no of order for freeing
	 * Quantity - no of ordered quantity and no Of orders -1  
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean freeDriverSlot(MealTypePojo mealTypePojo ){
		boolean freed = false;
		try {
			SQL:{
			      Connection connection = DBConnection.createConnection();
			      PreparedStatement preparedStatement = null;
			      String sql = "";
			      
			      if(mealTypePojo.isLunchToday()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status "
				    		  	+" SET assigned_date=current_date,quantity=quantity-?, no_of_orders=no_of_orders-1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else if(mealTypePojo.isDinnerToday()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status "
				    		  	+" SET assigned_date=current_date,quantity=quantity-?, no_of_orders=no_of_orders-1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else if(mealTypePojo.isLunchTomorrow()){
			    	  sql  = "UPDATE fapp_timeslot_driver_status_tommorrow "
				    		  	+" SET assigned_date=current_date,quantity=quantity-?, no_of_orders=no_of_orders-1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }else{
			    	  sql  = "UPDATE fapp_timeslot_driver_status_tommorrow "
				    		  	+" SET assigned_date=current_date,quantity=quantity-?, no_of_orders=no_of_orders-1 "
				    		  	+" WHERE driver_user_id=? and  time_slot_id=? ";
			      }
			      
				  try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, mealTypePojo.getQuantity());
						preparedStatement.setString(2, mealTypePojo.getBoyUSerId());
						preparedStatement.setInt(3, mealTypePojo.getSlotId());
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							freed = true;
						}
							
				  } catch (Exception e) {
						// TODO: handle exception
					  System.out.println(e);
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
		if(freed){
			System.out.println("Slot "+mealTypePojo.getSlotId()+" freed for boy id:: "+mealTypePojo.getBoyUSerId());
		}else{
			System.out.println("Slot "+mealTypePojo.getSlotId()+" NOT FREED for boy id:: "+mealTypePojo.getBoyUSerId());
		}
		return freed;
	}
	
	/**
	 * Check whether a slot is full or not??
	 * if no of orders == 2
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean isSlotFull(MealTypePojo mealTypePojo ){
		boolean isSlotFull = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="";
					if(mealTypePojo.isLunchToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " (no_of_orders = 2 or quantity >= 8) and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " (no_of_orders = 2 or quantity >= 8) and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " (no_of_orders = 2 or quantity >= 8) and driver_user_id = ? and time_slot_id = ?";
					}else{
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " (no_of_orders = 2 or quantity >= 8) and driver_user_id = ? and time_slot_id = ?";
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mealTypePojo.getBoyUSerId());
						preparedStatement.setInt(2, mealTypePojo.getSlotId());
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int timeSlotId = resultSet.getInt("time_slot_id");
							if(timeSlotId>0){
								isSlotFull = true;
							}
						}	 
					} catch (Exception e) {
						System.out.println(e);
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
		if(isSlotFull){
			System.out.println("Slot "+mealTypePojo.getSlotId()+" FULL for boy id:: "+mealTypePojo.getBoyUSerId());
		}else{
			System.out.println("Slot "+mealTypePojo.getSlotId()+" NOT FULL for boy id:: "+mealTypePojo.getBoyUSerId());
		}	
		return isSlotFull;
	}
	
	/**
	 * Make the slot locked when (no_of_orders=2 or quantity=8) for a biker
	 * update is_slot_locked = 'Y'
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean makeSlotLocked(MealTypePojo mealTypePojo){
		boolean slotInactivation  = false;
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(mealTypePojo.isLunchToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=2 or quantity>=8)";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=2 or quantity>=8)";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=2 or quantity>=8)";
				}else{
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=2 or quantity>=8)";
				}
				
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, mealTypePojo.getBoyUSerId());
					preparedStatement.setInt(2, mealTypePojo.getSlotId());
					int countUpdate = preparedStatement.executeUpdate();
					if(countUpdate>0){
							slotInactivation = true;
					}	 
				} catch (Exception e) {
					System.out.println(e);
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
		if(slotInactivation){
			System.out.println("Slot "+mealTypePojo.getSlotId()+" locked for boy id:: "+mealTypePojo.getBoyUSerId());
		}else{
			System.out.println("Slot "+mealTypePojo.getSlotId()+" NOT locked for boy id:: "+mealTypePojo.getBoyUSerId());
		}
		return slotInactivation ;
	}
	
	/**
	 * Make the slot unlocked when no of orders <2 for a biker
	 * update is_slot_locked = 'N'
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean makeSlotUnLocked(MealTypePojo mealTypePojo){
		boolean slotActivation  = false;
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(mealTypePojo.isLunchToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET assigned_date IS NULL, is_slot_locked = 'N' "
							+ " where driver_user_id = ? and time_slot_id = ? and no_of_orders<2";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET assigned_date IS NULL, is_slot_locked = 'N'"
							+ " where driver_user_id = ? and time_slot_id = ? and no_of_orders<2";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET assigned_date IS NULL, is_slot_locked = 'N'"
							+ " where driver_user_id = ? and time_slot_id = ? and no_of_orders<2";
				}else{
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET assigned_date IS NULL, is_slot_locked = 'N'"
							+ " where driver_user_id = ? and time_slot_id = ? and no_of_orders<2";
				}
				
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, mealTypePojo.getBoyUSerId());
					preparedStatement.setInt(2, mealTypePojo.getSlotId());
					int countUpdate = preparedStatement.executeUpdate();
					if(countUpdate>0){
						slotActivation = true;
					}	 
				} catch (Exception e) {
					System.out.println(e);
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
		if(slotActivation){
			System.out.println("Slot "+mealTypePojo.getSlotId()+" unlocked for boy id:: "+mealTypePojo.getBoyUSerId());
		}else{
			System.out.println("Slot "+mealTypePojo.getSlotId()+" NOT unlocked for boy id:: "+mealTypePojo.getBoyUSerId());
		}
		return slotActivation ;
	}
	
	/**
	 * Method to update order tracking table 
	 * with driver user id and current time 
	 * with kitchen id and order id
	 * @param driverKitchenList
	 * @param orderId
	 * @return
	 */
	 public static boolean assignDriverWithKitchen(ArrayList<TimeSlot> driverKitchenList, 
			 int orderId){
		 boolean isAssigned = false;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		String sql = "Update fapp_order_tracking set "
			 				+ " driver_boy_user_id = ?,driver_name = "
			 				+ " (select delivery_boy_name from fapp_delivery_boy where delivery_boy_user_id = ?),"
			 				+ " driver_number=(select delivery_boy_phn_number from fapp_delivery_boy where delivery_boy_user_id = ?)"
			 				+ " where kitchen_id = ? and order_id = ? ";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						for(TimeSlot driverSlot : driverKitchenList){
							preparedStatement.setString(1, driverSlot.bikerUserId);
							preparedStatement.setString(2, driverSlot.bikerUserId);
							preparedStatement.setString(3, driverSlot.bikerUserId);
							preparedStatement.setInt(4, driverSlot.kitchenID);
							preparedStatement.setInt(5, orderId);
							preparedStatement.addBatch();
						}
						int count [] = preparedStatement.executeBatch();
						for(Integer c : count){
							
							isAssigned = true;
							System.out.println("Driver assigned "+isAssigned);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		 return isAssigned ;
	 }
	
	 public static boolean savePickjiOrderId(JSONObject pickJiJson) throws Exception{
		 boolean isSaved = false;
		 String kitchenName = pickJiJson.getString("kitchenName");
		 String orderNo = pickJiJson.getString("orderNo");
		 String pickJiOrderId = pickJiJson.getString("pickJiOrderID");
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		String sql = "UPDATE fapp_order_tracking set external_order_id = ? where kitchen_id ="
			 				+ " (select kitchen_id from fapp_kitchen where kitchen_name=  ?) and order_id = "
			 				+ " (select order_id from fapp_orders where order_no = ?)";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, pickJiOrderId);
						preparedStatement.setString(2, kitchenName);
						preparedStatement.setString(3, orderNo);
						
						int count = preparedStatement.executeUpdate();
						if(count > 0 ){
							isSaved = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
						 System.out.println("Pickji orderID saved failed due to::"+e);
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
		 if(isSaved){
			 System.out.println("Pickji orderId:: "+pickJiOrderId+" is saved with "+kitchenName+" and order no "+orderNo);
		 }else{
			 System.out.println("Pickji orderID saved failed!");
		 }
		
		 return isSaved;
	 }
}
