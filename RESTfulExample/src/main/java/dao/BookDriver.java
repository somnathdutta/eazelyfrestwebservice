package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
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
	public static boolean bookDriverSlot(MealTypePojo mealTypePojo, TimeSlot timeSlot ){
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
						/*for(TimeSlot slot : timeSlotList){
							preparedStatement.setInt(1, slot.quantity);
							preparedStatement.setString(2, slot.bikerUserId);
							preparedStatement.setInt(3, slot.slotId);
							preparedStatement.addBatch();
						}*/
						preparedStatement.setInt(1, timeSlot.getQuantity());
						preparedStatement.setString(2, timeSlot.bikerUserId);
						preparedStatement.setInt(3, timeSlot.getSlotId());
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
			/*for(TimeSlot slot : timeSlotList){
				System.out.println("Slot "+slot.getSlotId()+" booked for boy id:: "+slot.bikerUserId);
			}*/
			System.out.println("Item code "+timeSlot.getItemCode()+" Slot "+timeSlot.getSlotId()+"  BOOKED for boy id:: "+timeSlot.bikerUserId+" with qty: "+timeSlot.quantity);
		}else{
			/*for(TimeSlot slot : timeSlotList){
				System.out.println("Slot "+slot.getSlotId()+" booked for boy id:: "+slot.bikerUserId);
			}*/
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT BOOKED for boy id:: "+timeSlot.bikerUserId+" with qty: "+timeSlot.quantity);
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
	public static boolean freeDriverSlot(MealTypePojo mealTypePojo, ArrayList<TimeSlot> timeSlotList ){
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
	public static boolean isSlotFull(MealTypePojo mealTypePojo,TimeSlot timeSlot ){
		boolean isSlotFull = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="";
					if(mealTypePojo.isLunchToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " (no_of_orders = ? or quantity = ?) and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " (no_of_orders = ? or quantity = ?) and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " (no_of_orders = ? or quantity = ?) and driver_user_id = ? and time_slot_id = ?";
					}else{
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " (no_of_orders = ? or quantity = ?) and driver_user_id = ? and time_slot_id = ?";
					}	
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, bikerOrders);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setString(3, timeSlot.bikerUserId);
						preparedStatement.setInt(4, timeSlot.getSlotId());
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
			System.out.println("Slot "+timeSlot.getSlotId()+" FULL for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT FULL for boy id:: "+timeSlot.bikerUserId);
		}	
		return isSlotFull;
	}
	
	/**
	 * Make the slot locked when (no_of_orders=2 or quantity=8) for a biker
	 * update is_slot_locked = 'Y'
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean makeSlotLocked(MealTypePojo mealTypePojo , TimeSlot timeSlot){
		boolean slotInactivation  = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(mealTypePojo.isLunchToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? or quantity>=?)";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? or quantity>=?)";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? or quantity>=?)";
				}else{
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? or quantity>=?)";
				}
				
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, timeSlot.bikerUserId);
					preparedStatement.setInt(2, timeSlot.getSlotId());
					preparedStatement.setInt(3, bikerOrders);
					preparedStatement.setInt(4, bikerCapacity);
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
			System.out.println("Slot "+timeSlot.getSlotId()+" locked for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT locked for boy id:: "+timeSlot.bikerUserId);
		}
		return slotInactivation ;
	}
	
	
	/**
	 * Check whether a slot is full or not??
	 * if no of orders == 2
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean isSlotFullForOrders(MealTypePojo mealTypePojo,TimeSlot timeSlot ){
		boolean isSlotFull = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="";
					if(mealTypePojo.isLunchToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " no_of_orders >= ?  and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " no_of_orders >= ? and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " no_of_orders = ? and driver_user_id = ? and time_slot_id = ?";
					}else{
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " no_of_orders = ? and driver_user_id = ? and time_slot_id = ?";
					}	
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, bikerOrders);
						//preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setString(2, timeSlot.bikerUserId);
						preparedStatement.setInt(3, timeSlot.getSlotId());
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
			System.out.println("Slot "+timeSlot.getSlotId()+" FULL for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT FULL for boy id:: "+timeSlot.bikerUserId);
		}	
		return isSlotFull;
	}
	
	
	/**
	 * Check whether a slot is full or not??
	 * if no of orders == 2
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean isSlotFullForQuantity(MealTypePojo mealTypePojo,TimeSlot timeSlot ){
		boolean isSlotFull = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="";
					if(mealTypePojo.isLunchToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " quantity >= ?  and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status where"
								+ " quantity >= ? and driver_user_id = ? and time_slot_id = ?";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " quantity = ? and driver_user_id = ? and time_slot_id = ?";
					}else{
						sql = "select count(time_slot_id)AS time_slot_id from fapp_timeslot_driver_status_tommorrow where"
								+ " quantity = ? and driver_user_id = ? and time_slot_id = ?";
					}	
					try {
						preparedStatement = connection.prepareStatement(sql);
						//preparedStatement.setInt(1, bikerOrders);
						preparedStatement.setInt(1, bikerCapacity);
						preparedStatement.setString(2, timeSlot.bikerUserId);
						preparedStatement.setInt(3, timeSlot.getSlotId());
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
			System.out.println("Slot "+timeSlot.getSlotId()+" FULL for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT FULL for boy id:: "+timeSlot.bikerUserId);
		}	
		return isSlotFull;
	}
	
	/**
	 * Make the slot locked when (no_of_orders=2 or quantity=8) for a biker
	 * update is_slot_locked = 'Y'
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean makeSlotLockedForOrders(MealTypePojo mealTypePojo , TimeSlot timeSlot){
		boolean slotInactivation  = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerOrders = bikerCapa[1];
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(mealTypePojo.isLunchToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? )";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? )";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? )";
				}else{
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and (no_of_orders=? )";
				}
				
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, timeSlot.bikerUserId);
					preparedStatement.setInt(2, timeSlot.getSlotId());
					preparedStatement.setInt(3, bikerOrders);
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
			System.out.println("Slot "+timeSlot.getSlotId()+" locked for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT locked for boy id:: "+timeSlot.bikerUserId);
		}
		return slotInactivation ;
	}
	
	/**
	 * Make the slot locked when (no_of_orders=2 or quantity=8) for a biker
	 * update is_slot_locked = 'Y'
	 * @param mealTypePojo
	 * @return
	 */
	public static boolean makeSlotLockedForQuantity(MealTypePojo mealTypePojo , TimeSlot timeSlot){
		boolean slotInactivation  = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(mealTypePojo.isLunchToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and quantity>=?";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "UPDATE fapp_timeslot_driver_status SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and quantity>=?";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and quantity>=?";
				}else{
					sql = "UPDATE fapp_timeslot_driver_status_tommorrow SET is_slot_locked = 'Y'"
							+ " where driver_user_id = ? and time_slot_id = ? and quantity>=?";
				}
				
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, timeSlot.bikerUserId);
					preparedStatement.setInt(2, timeSlot.getSlotId());
					preparedStatement.setInt(3, bikerCapacity);
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
			System.out.println("Slot "+timeSlot.getSlotId()+" locked for boy id:: "+timeSlot.bikerUserId);
		}else{
			System.out.println("Slot "+timeSlot.getSlotId()+" NOT locked for boy id:: "+timeSlot.bikerUserId);
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
	
	
	public static void saveBikersQtyWithKitchen(ArrayList<TimeSlot> bikerSlotList, int orderId){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="INSERT INTO public.fapp_biker_orders"
							   + " ( biker_user_id, kitchen_id, order_id, slot_id,order_quantity, item_code)"
							   +" VALUES (?, ?, ?, ?, ?, ?) ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						for(TimeSlot slot : bikerSlotList){
							preparedStatement.setString(1, slot.bikerUserId);
							preparedStatement.setInt(2, slot.kitchenID);
							preparedStatement.setInt(3, orderId);
							preparedStatement.setInt(4, slot.slotId);
							preparedStatement.setInt(5, slot.quantity);
							preparedStatement.setString(6, slot.itemCode);
							preparedStatement.addBatch();
						}
						int[] insertedData = preparedStatement.executeBatch();
						if(insertedData.length>0){
							System.out.println("Bikers orders qty inserted successfully!");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR AT saveBikersQty BookDriver 364"+e);
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
	 
	 public static boolean isPickJiBoy(String bikerUserId){
		 boolean isPickJi = false;
		 String pickJiBoy = null;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		ResultSet resultSet = null;
			 		String sql = "select is_pickji_boy from fapp_delivery_boy where delivery_boy_user_id = ?";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							pickJiBoy = resultSet.getString("is_pickji_boy");
							if(pickJiBoy.equals("Y")){
								isPickJi = true;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
		 		}
		} catch (Exception e) {
			// TODO: handle exception
		} 
		 return isPickJi;
	 }
	 
	 public static String getBikerUserID(String kitchenName, String orderNo){
		 String bikerUserId = null;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		ResultSet resultSet = null;
			 		String sql = "select driver_boy_user_id from fapp_order_tracking where kitchen_id = "
			 				+ " (select kitchen_id from fapp_kitchen where kitchen_name = ?) and order_id="
			 				+ " (select order_id from fapp_orders where order_no = ? )";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						preparedStatement.setString(2, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							bikerUserId = resultSet.getString("driver_boy_user_id");
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
		 return bikerUserId;
	 }
}
