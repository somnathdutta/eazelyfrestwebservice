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

	public static ArrayList<TimeSlot> findCommonTimeSlots(String boyUserId, int kitchenId, 
			MealTypePojo mealTypePojo){
		System.out.println("$ $ $ $ $ $ $ $ START FINDING SLOTS FOR BOY USER ID:: "+boyUserId);
		ArrayList<TimeSlot> timeSlotList = new ArrayList<TimeSlot>();
		ArrayList<Integer> timeSlotIds = new ArrayList<Integer>();
		boolean firstSlotToday=false;
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
					if(mealTypePojo.isLunchToday() && noOfRows==2){
						firstSlotToday = true;
					}else if(mealTypePojo.isDinnerToday() && noOfRows==2){
						firstSlotToday = true;
					}else if(mealTypePojo.isLunchTomorrow() && noOfRows==2){
						firstSlotToday = true;
					}else if(mealTypePojo.isDinnerTomorrow() && noOfRows==2){
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
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders  "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='Y' and no_of_orders < 2 and quantity <11 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
							+ " and ftds.time_slot_id <4 ";
				}else if(mealTypePojo.isDinnerToday() ){
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='N' and no_of_orders < 2 and quantity <11 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";
							+ " and ftds.time_slot_id >3 "*/;
				}else if(mealTypePojo.isLunchTomorrow() ){
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status_tommorrow ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='Y' and no_of_orders < 2 and quantity <11 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";
							+ " and ftds.time_slot_id <4 "*/;
				}else{
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status_tommorrow ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and ft.is_lunch='N' and no_of_orders < 2 and quantity <11 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";
							+ " and ftds.time_slot_id >3 "*/;

				}
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
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
}
