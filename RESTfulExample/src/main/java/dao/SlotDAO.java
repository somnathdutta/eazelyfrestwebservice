package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;
import pojo.TimeSlot;

import com.mkyong.rest.DBConnection;

public class SlotDAO {

	public static ArrayList<TimeSlot> findCommonTimeSlots(String boyUserId, MealTypePojo mealTypePojo){
		System.out.println("======== START FINDING SLOTS FOR BOY USER ID:: ============"+boyUserId);
		ArrayList<TimeSlot> timeSlotList = new ArrayList<TimeSlot>();
		ArrayList<Integer> timeSlotIds = new ArrayList<Integer>();
		boolean firstSlotToday=false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				/*String sql = "select ftds.time_slot_id ,ft.time_slot "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
			 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
			 				+ " and assigned_date = current_date";*/
				String sql = "";
				if(mealTypePojo.isLunchToday()){
					sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and quantity = 0 and no_of_orders = 0 "
							+ " and assigned_date IS NULL and is_slot_active = 'Y' and is_lunch='Y' ";

				}else if(mealTypePojo.isDinnerToday()){
					sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and quantity = 0 and no_of_orders = 0 "
							+ " and assigned_date IS NULL and is_slot_active = 'Y' and is_lunch='N' ";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status_tommorrow"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and quantity = 0 and no_of_orders = 0 "
							+ " and assigned_date IS NULL and is_slot_active = 'Y' and is_lunch='Y' ";
				}else{
					sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status_tommorrow"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and quantity = 0 and no_of_orders = 0 "
							+ " and assigned_date IS NULL and is_slot_active = 'Y' and is_lunch='N' ";
				}
				/*sql = "select count(*)AS time_slot_id from fapp_timeslot_driver_status"
			 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
			 				+ " and quantity = 0 and no_of_orders = 0";*/
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
					//System.out.println("1. sql::"+preparedStatement);
					resultSet = preparedStatement.executeQuery();
					int noOfRows = 0;
					while(resultSet.next()){
						//noOfRows = resultSet.getInt("time_slot_id");
						timeSlotIds.add(resultSet.getInt("time_slot_id"));
					}
					noOfRows = timeSlotIds.size();
					System.out.println("-- NO of Rows:: "+noOfRows);	
					/*if(noOfRows == 0){
							firstSlotToday = true;
						}*/
					int lunchSlots = BikerDAO.getNoOfLunchSlot(connection);
					int dinnerSlots = BikerDAO.getNoOfDinnerSlot(connection);
					
					if(mealTypePojo.isLunchToday() && noOfRows==lunchSlots){
						firstSlotToday = true;
					}else if(mealTypePojo.isDinnerToday() && noOfRows==dinnerSlots){
						firstSlotToday = true;
					}else if(mealTypePojo.isLunchTomorrow() && noOfRows==lunchSlots){
						firstSlotToday = true;
					}else if(mealTypePojo.isDinnerTomorrow() && noOfRows==dinnerSlots){
						firstSlotToday = true;
					}else{
						firstSlotToday = false;
					}
					System.out.println("firstSlotToday:: "+firstSlotToday);
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}if(resultSet!=null){
						resultSet.close();
					}

				}
			}

			if(firstSlotToday){
				SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "";
				/*sql = "select time_slot_id ,time_slot "
								+ " from fapp_timeslot ";*/
				if(mealTypePojo.isLunchToday()){
					sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where is_active='Y' and is_lunch ='Y'";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where is_active='Y' and is_lunch ='N' ";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where is_active='Y' and is_lunch ='Y' ";
				}else{
					sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where is_active='Y' and is_lunch ='N'  ";
				}

				try {
					preparedStatement = connection.prepareStatement(sql);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						TimeSlot timeSlot = new TimeSlot();
						timeSlot.slotId = resultSet.getInt("time_slot_id");
						timeSlot.timeSlot = resultSet.getString("time_slot");
						timeSlot.quantity = 0;
						timeSlot.noOfOrders = 0;
						/*JSONObject timeSlotJson = new JSONObject();
								timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
								timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));*/
						/*timeSlot.bikerUserId = boyUserId;
								timeSlot.kitchenID = kitchenId;
						 */
						//timeSlotArray.put(timeSlotJson);
						timeSlotList.add(timeSlot);

					}
					//System.out.println("Length of slot array :: "+timeSlotArray.length());
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
			}
			if(!firstSlotToday){
				SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql ="";
				/*sql= "select ftds.time_slot_id ,ft.time_slot "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
			 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
			 				+ " and is_slot_active ='Y' and no_of_orders < 3 and quantity <9 "
			 				+ " and assigned_date = current_date";*/
				if(mealTypePojo.isLunchToday()){
					/*sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders  "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='Y' and no_of_orders <= ? and quantity <= ? ";
							+ " and assigned_date = current_date and ftds.time_slot_id <4 ";
							+ " and ftds.time_slot_id <4 ";*/
					sql= "select time_slot_id ,time_slot,quantity,no_of_orders  "
							+ " from vw_driver_today_status "
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and is_lunch='Y' and no_of_orders <= ? and quantity <= ? ";
				}else if(mealTypePojo.isDinnerToday() ){
					/*sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='N' and no_of_orders <= ? and quantity <= ? ";
							+ " and assigned_date = current_date and ftds.time_slot_id >3 ";
							+ " and ftds.time_slot_id >3 "*/;
					sql= "select time_slot_id ,time_slot,quantity,no_of_orders "
							+ " from vw_driver_today_status "
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and is_lunch='N' and no_of_orders <= ? and quantity <= ? ";
				}else if(mealTypePojo.isLunchTomorrow() ){
					/*sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status_tommorrow ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='Y' and no_of_orders <= ? and quantity <= ? ";
							+ " and assigned_date = current_date and ftds.time_slot_id <4 ";
							+ " and ftds.time_slot_id <4 "*/
					sql= "select time_slot_id ,time_slot,quantity,no_of_orders "
							+ " from vw_driver_tomorrow_status "
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and is_lunch='Y' and no_of_orders <= ? and quantity <= ? ";
				}else{
					/*sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status_tommorrow ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='N' and no_of_orders <= ? and quantity <= ? ";
							+ " and assigned_date = current_date and ftds.time_slot_id >3 ";
							+ " and ftds.time_slot_id >3 "*/
					sql= "select time_slot_id ,time_slot,quantity,no_of_orders "
							+ " from vw_driver_tomorrow_status "
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and is_lunch='N' and no_of_orders <= ? and quantity <= ? ";
				}
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
					preparedStatement.setInt(2, bikerOrders);
					preparedStatement.setInt(3, bikerCapacity);
					
					resultSet = preparedStatement.executeQuery();
					//	System.out.println("Not first:: "+preparedStatement);
					while (resultSet.next()) {
						TimeSlot timeSlot = new TimeSlot();
						timeSlot.slotId = resultSet.getInt("time_slot_id");
						timeSlot.timeSlot = resultSet.getString("time_slot");
						timeSlot.quantity = resultSet.getInt("quantity");
						timeSlot.noOfOrders = resultSet.getInt("no_of_orders");

						timeSlotList.add(timeSlot);

						/*JSONObject timeSlotJson = new JSONObject();
							timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
							timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));

							timeSlotArray.put(timeSlotJson);*/
						//timeSlotList.add(timeSlot);
					}
				} catch (Exception e) {
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
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Return Length of slot array :: "+timeSlotList.size());
		return timeSlotList;
	}
	
	public static ArrayList<TimeSlot> getSlotAfter11(String boyUserId, MealTypePojo mealTypePojo){
		ArrayList<TimeSlot> timeSlotList = new ArrayList<TimeSlot>();
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
				sql= "select time_slot_id,time_slot,quantity,no_of_orders from vw_driver_today_status "
						+" where driver_user_id =? and is_lunch='Y'"
						+" and is_slot_locked ='N' and no_of_orders <= ? and quantity <= ?"
						+" order by time_slot_id desc limit 1";
						
			}else if(mealTypePojo.isLunchTomorrow() ){
				sql= "select time_slot_id,time_slot,quantity,no_of_orders from vw_driver_tomorrow_status "
						+" where driver_user_id =? and is_lunch='Y'"
						+" and is_slot_locked ='N' and no_of_orders <= ? and quantity <= ?"
						+" order by time_slot_id desc limit 1";
			}else if(mealTypePojo.isDinnerToday() ){
				sql= "select time_slot_id,time_slot,quantity,no_of_orders from vw_driver_tomorrow_status "
						+" where driver_user_id =? and is_lunch='N'"
						+" and is_slot_locked ='N' and no_of_orders <= ? and quantity <= ?";
						
			}else{
				sql= "select time_slot_id,time_slot,quantity,no_of_orders from vw_driver_tomorrow_status "
						+" where driver_user_id =? and is_lunch='Y'"
						+" and is_slot_locked ='N' and no_of_orders <= ? and quantity <= ?";
					
			}
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, boyUserId);
				preparedStatement.setInt(2, bikerOrders);
				preparedStatement.setInt(3, bikerCapacity);
				resultSet = preparedStatement.executeQuery();
				//	System.out.println("Not first:: "+preparedStatement);
				while (resultSet.next()) {
					TimeSlot timeSlot = new TimeSlot();
					timeSlot.slotId = resultSet.getInt("time_slot_id");
					timeSlot.timeSlot = resultSet.getString("time_slot");
					timeSlot.quantity = resultSet.getInt("quantity");
					timeSlot.noOfOrders = resultSet.getInt("no_of_orders");

					timeSlotList.add(timeSlot);
				}
			} catch (Exception e) {
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
		return timeSlotList;
	}
	
	public static JSONArray findAllSlots(MealTypePojo mealTypePojo){
		JSONArray timeSlotArray = new JSONArray();
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";
			/*sql = "select time_slot_id ,time_slot "
						+ " from fapp_timeslot ";*/
			if(mealTypePojo.isLunchToday()){
				sql = "select time_slot_id ,time_slot "
						+ " from fapp_timeslot where is_active='Y' and is_delete='N' and is_lunch='Y' ";
			}else if(mealTypePojo.isDinnerToday()){
				sql = "select time_slot_id ,time_slot "
						+ " from fapp_timeslot where is_active='Y' and is_delete='N' and is_lunch='N' ";
			}else if(mealTypePojo.isLunchTomorrow()){
				sql = "select time_slot_id ,time_slot "
						+ " from fapp_timeslot where is_active='Y' and is_delete='N' and is_lunch='Y'";
			}else{
				sql = "select time_slot_id ,time_slot "
						+ " from fapp_timeslot where is_active='Y' and is_delete='N' and is_lunch='N'";
			}

			try {
				preparedStatement = connection.prepareStatement(sql);
				//preparedStatement.setString(1, boyUserId);
				System.out.println(preparedStatement);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject timeSlotJson = new JSONObject();
					timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
					timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));

					timeSlotArray.put(timeSlotJson);
				}
				System.out.println("Length of all slot array :: "+timeSlotArray.length());
			} catch (Exception e) {
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
		return timeSlotArray;
	}

	public static String[] getSlotTimings(){
		String[] slotTimings = new String[2];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet =null;
					String sql = "select lunch_from,lunch_to from fapp_slot_timings ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							slotTimings[0] = resultSet.getString("lunch_from");
							slotTimings[1] = resultSet.getString("lunch_to");
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
		return slotTimings;
	}
	
	public static String[] getSlotTimings(Connection connection){
		String[] slotTimings = new String[2];
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet =null;
					String sql = "select lunch_from,lunch_to from fapp_slot_timings ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							slotTimings[0] = resultSet.getString("lunch_from");
							slotTimings[1] = resultSet.getString("lunch_to");
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		return slotTimings;
	}
	
	public static String[] getOrderTimings(){
		String[] slotTimings = new String[4];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet =null;
					String sql = "select lunch_from,lunch_to,dinner_from,dinner_to from fapp_timings ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							slotTimings[0] = resultSet.getString("lunch_from");
							slotTimings[1] = resultSet.getString("lunch_to");
							slotTimings[2] = resultSet.getString("dinner_from");
							slotTimings[3] = resultSet.getString("dinner_to");
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
		return slotTimings;
	}
	
}
