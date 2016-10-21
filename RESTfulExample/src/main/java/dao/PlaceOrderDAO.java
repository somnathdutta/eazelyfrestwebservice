package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.print.attribute.standard.PresentationDirection;

import pojo.MealTypePojo;

import com.mkyong.rest.DBConnection;

public class PlaceOrderDAO {

	public static int getTotalNoOfQuantity(String kitchen, String orderNo){
		int totalNoOfQuantity = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select SUM(qty) as total_quantity from fapp_order_item_details "
							+" where kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?)"
							+" and order_id = (select order_id from fapp_orders where order_no = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchen);
						preparedStatement.setString(2, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							totalNoOfQuantity = resultSet.getInt("total_quantity");
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
		System.out.println("Total no of quantity :: "+totalNoOfQuantity);
		return totalNoOfQuantity;
	}
	
	public static String findBikerAvailableKitchens(String itemCode, Connection connection, 
			String deliveryDay, boolean isLunch){
		ArrayList<Integer> bikerAvailableKitchenIds = new ArrayList<Integer>();
		String freeKitchens = "";
		ArrayList<Integer> servableKitchenIds = new ArrayList<Integer>();
		if(isLunch){
			servableKitchenIds = findServableKitchens(itemCode, connection, deliveryDay, true);
		}else{
			servableKitchenIds = findServableKitchens(itemCode, connection, deliveryDay, false);
		}
		for(Integer kitchenId : servableKitchenIds){
			if(isKitchenHavingFreeBikers(kitchenId, connection, deliveryDay, isLunch)){
				bikerAvailableKitchenIds.add(kitchenId);
			}
		}
		String freeBikers = bikerAvailableKitchenIds.toString();
		String fb = freeBikers.replace("[", "(");
		String bb = fb.replace("]", ")");
		freeKitchens = bb;
		return freeKitchens;
	}
	
	public static boolean isKitchenHavingFreeBikers(int kicthenId,Connection connection, String deliveryDay, boolean isLunch){
		boolean havingFreeBiker = false;int totalNoOfBikers = 0,bikerSlot = 0; 
		ArrayList<String> bikerList = findBikerOfKitchen(kicthenId,connection);
		for(String bikerUserId : bikerList){
			totalNoOfBikers ++;
			int totalFreeSlotsForBiker = totalFreeSlots(bikerUserId, connection, deliveryDay, isLunch);
			if(totalFreeSlotsForBiker == 0){
				bikerSlot++;
			}
		}
		if(totalNoOfBikers == bikerSlot){
			havingFreeBiker = false;
		}else{
			havingFreeBiker = true;
		}
		return havingFreeBiker;
	}
	
	public static boolean isServable(String itemCode, Connection connection, 
			String deliveryDay, boolean isLunch){
		boolean isServable = false;
		int totalNoOfBikers = 0,bikerSlot = 0; 
		ArrayList<Integer> servableKitchenIds = findServingKitchens(itemCode, connection, deliveryDay, isLunch);
		
		for(Integer kitchenId : servableKitchenIds){
			ArrayList<String> bikerList = findBikerOfKitchen(kitchenId,connection);
			for(String bikerUserId : bikerList){
				totalNoOfBikers ++;
				int totalFreeSlotsForBiker = totalFreeSlots(bikerUserId, connection, deliveryDay, isLunch);
				if(totalFreeSlotsForBiker == 0){
					bikerSlot++;
				}
			}
		}
		
		if(totalNoOfBikers == bikerSlot){
			isServable = false;
		}else{
			isServable = true;
		}
		//System.out.println("Total no of bikers:: "+totalNoOfBikers+" and bikerSlots:: "+bikerSlot);
		//System.out.println("Item code "+itemCode+" is available:"+isServable );	
		//System.out.println("- - - - - - - - - - - - - - - - - - - -  - - - -  - - - - -");
		return isServable;
	}
	
	/**
	 * Find servable kitchens who is capable of serving the item
	 * @param itemCode
	 * @param connection
	 * @param deliveryDay
	 * @return
	 */
	public static ArrayList<Integer> findServableKitchens(String itemCode, Connection connection, 
			String deliveryDay, boolean isLunch){
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		try {
			SQL:{
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 String sql = "";
				 if(deliveryDay.equalsIgnoreCase("TODAY") && isLunch){
					 sql = "select fki.kitchen_id from"
						  + " fapp_kitchen_items fki where fki.item_code = ? "
						  + " and fki.stock >0 and fki.is_active='Y'";
				 }else if(deliveryDay.equalsIgnoreCase("TODAY") && (isLunch==false)){
					 sql = "select fki.kitchen_id from"
							  + " fapp_kitchen_items fki where fki.item_code = ? "
							  + " and fki.dinner_stock >0 and fki.is_active='Y'";
				 }else if(deliveryDay.equalsIgnoreCase("TOMORROW") && isLunch){
					 sql = "select fki.kitchen_id from"
							  + " fapp_kitchen_items fki where fki.item_code = ? "
							  + " and fki.stock_tomorrow >0 and fki.is_active='Y'";
				 }else{
					 sql = "select fki.kitchen_id from"
							  + " fapp_kitchen_items fki where fki.item_code = ? "
							  + " and fki.dinner_stock_tomorrow > 0 and fki.is_active='Y'";
				 }
				 
				 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, itemCode);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						kitchenIds.add(resultSet.getInt("kitchen_id"));
					}
							
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
					}		
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//System.out.println(kitchenIds);
		return kitchenIds;
	}
	
	
	/**
	 * Find servable kitchens who is capable of serving the item
	 * @param itemCode
	 * @param connection
	 * @param deliveryDay
	 * @return
	 */
	public static ArrayList<Integer> findServingKitchens(String itemCode, Connection connection, 
			String deliveryDay,boolean isLunch){
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		try {
			SQL:{
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 String sql = "";
				 if(deliveryDay.equalsIgnoreCase("TODAY") && isLunch ){
					 sql = "select fki.kitchen_id from"
						  + " fapp_kitchen_items fki where fki.item_code = ? "
						  + " and fki.stock >0 and fki.is_active='Y'";
				 }else if(deliveryDay.equalsIgnoreCase("TODAY") && (isLunch==false)) {
					 sql = "select fki.kitchen_id from"
						  + " fapp_kitchen_items fki where fki.item_code = ? "
						  + " and fki.dinner_stock > 0 and fki.is_active='Y'";
				 }else if(deliveryDay.equalsIgnoreCase("TOMORROW") && isLunch ) {
					 sql = "select fki.kitchen_id from"
							  + " fapp_kitchen_items fki where fki.item_code = ? "
							  + " and fki.stock_tomorrow > 0 and fki.is_active='Y'";
				}else{
					sql = "select fki.kitchen_id from"
							  + " fapp_kitchen_items fki where fki.item_code = ? "
							  + " and fki.dinner_stock_tomorrow > 0 and fki.is_active='Y'";
				}
				 /*if(deliveryDay.equalsIgnoreCase("TODAY") ){
					 sql = "select fki.kitchen_id from"
						  + " fapp_kitchen_items fki where fki.item_code = ? "
						  + " and fki.stock >0 and fki.dinner_stock > 0 and fki.is_active='Y'";
				 }else{
					 sql = "select fki.kitchen_id from"
						  + " fapp_kitchen_items fki where fki.item_code = ? "
						  + " and fki.stock_tomorrow > 0 and fki.dinner_stock_tomorrow > 0 and fki.is_active='Y'";
				 }*/
				 
				 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, itemCode);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						kitchenIds.add(resultSet.getInt("kitchen_id"));
					}
							
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
					}		
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//System.out.println(kitchenIds);
		return kitchenIds;
	}
	
	/**
	 * Find bikers of kitchens
	 * @param kicthenId
	 * @param connection
	 * @return
	 */
	public static ArrayList<String> findBikerOfKitchen(int kicthenId,Connection connection){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select delivery_boy_user_id"
							+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kicthenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							bikerList.add(resultSet.getString("delivery_boy_user_id"));
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		//System.out.println(bikerList);
		return bikerList;
	}
	
	public static int totalFreeSlots(String bikerUserId , Connection connection, 
			String deliverDay, boolean isLunch){
		int noOfFreeSlots = 0 ;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliverDay.equalsIgnoreCase("TODAY") && isLunch){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id<4"
								+" and (quantity<8 or no_of_orders <2)" ; 
					}else if(deliverDay.equalsIgnoreCase("TODAY") && !isLunch){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id > 3"
								+" and (quantity<8 or no_of_orders <2)" ;
					}else if(deliverDay.equalsIgnoreCase("TOMORROW") && isLunch){
						//System.out.println("target - - ");
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id < 4"
								+" and (quantity<8 or no_of_orders <2)" ;
					}else if(deliverDay.equalsIgnoreCase("TOMORROW") && !isLunch){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id > 3"
								+" and (quantity<8 or no_of_orders <2)" ;
					}
					/*sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' "
								+" and (quantity<8 or no_of_orders <2)" ; */
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next())
								noOfFreeSlots = resultSet.getInt("no_of_free_slots");
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
		return noOfFreeSlots ;
	}
}
