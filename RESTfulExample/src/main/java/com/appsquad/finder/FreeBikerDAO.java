package com.appsquad.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mkyong.rest.DBConnection;

import dao.BikerDAO;
import pojo.Kitchen;

public class FreeBikerDAO {

	public static ArrayList<Kitchen> findKitchenBikerFreeQuantity(Set<Integer> kitchenIdSet, String mealType, String deliveryDay
			,int[] bikerCapa){
		ArrayList<Kitchen> kitchenBikerFreeQtyList =  new ArrayList<Kitchen>();
		if(kitchenBikerFreeQtyList.size() > 0){
			kitchenBikerFreeQtyList.clear();
		}
		String temp = kitchenIdSet.toString();
		String fb = temp.replace("[", "(");
		String kitchens = fb.replace("]", ")");
		
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		
		Map<Integer, Integer> kitchenFreeQtyMap = new HashMap<Integer, Integer>();
		try {
			SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "";
						if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="select KITCHEN_ID,quantity from "
									+" vw_driver_today_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='Y' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="select KITCHEN_ID,quantity from "
									+" vw_driver_today_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='N' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
							sql ="select KITCHEN_ID,quantity from "
									+" vw_driver_tomorrow_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='Y' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else{
							sql ="select KITCHEN_ID,quantity from "
									+" vw_driver_tomorrow_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='N' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}
						
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, bikerCapacity);
							preparedStatement.setInt(2, bikerOrders);
							resultSet = preparedStatement.executeQuery();
							
							while (resultSet.next()) {
								int dbQty  = resultSet.getInt("quantity");
								int kitchenId = resultSet.getInt("KITCHEN_ID");
								if(kitchenFreeQtyMap.containsKey(kitchenId)){
									kitchenFreeQtyMap.put(kitchenId, kitchenFreeQtyMap.get(kitchenId)+( bikerCapacity-dbQty) );
								}else{
									kitchenFreeQtyMap.put(kitchenId,  bikerCapacity-dbQty);
								}
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
		
		for(Map.Entry<Integer, Integer> me : kitchenFreeQtyMap.entrySet()){
			Kitchen kitchen = new Kitchen();
			kitchen.setKitchenId(me.getKey());
			kitchen.setFreeQty(me.getValue());
			kitchenBikerFreeQtyList.add(kitchen);
		}
				
		for(Kitchen kitchen : kitchenBikerFreeQtyList)
			System.out.println("Kitchen id: "+kitchen.getKitchenId()+" Free qty : "+kitchen.getFreeQty());
		return kitchenBikerFreeQtyList;
	}
	
	/**
	 * Method to find kitchen list wrt to slot id and slot free capacity
	 * @param kitchenIdSet
	 * @param mealType
	 * @param deliveryDay
	 * @param bikerCapa
	 * @return
	 */
	public static ArrayList<Kitchen> findSlotCapacity(Set<Integer> kitchenIdSet, String mealType, String deliveryDay
			,int[] bikerCapa){
		ArrayList<Kitchen> kitchenSlotList =  new ArrayList<Kitchen>();
		if(kitchenSlotList.size() > 0){
			kitchenSlotList.clear();
		}
		String temp = kitchenIdSet.toString();
		String fb = temp.replace("[", "(");
		String kitchens = fb.replace("]", ")");
		
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		
		try {
			SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "";
						if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="select KITCHEN_ID,time_slot_id,(? -quantity)AS slot_capacity from "
									+" vw_driver_today_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='Y' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="select KITCHEN_ID,time_slot_id,(? -quantity)AS slot_capacity from "
									+" vw_driver_today_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='N' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
							sql ="select KITCHEN_ID,time_slot_id,(? -quantity)AS slot_capacity from "
									+" vw_driver_tomorrow_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='Y' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}else{
							sql ="select KITCHEN_ID,time_slot_id,(? -quantity)AS slot_capacity from "
									+" vw_driver_tomorrow_status "
									+" where kitchen_id in "+kitchens
									+" and is_single_order_biker = 'N' "
									+" and is_lunch='N' and is_slot_locked = 'N'" 
									+" and (quantity<? or no_of_orders <?)";
						}
						
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, bikerCapacity);
							preparedStatement.setInt(2, bikerCapacity);
							preparedStatement.setInt(3, bikerOrders);
							resultSet = preparedStatement.executeQuery();
							
							while (resultSet.next()) {
								int slotCapacity  = resultSet.getInt("slot_capacity");
								int timeSlotId= resultSet.getInt("time_slot_id");
								int kitchenId = resultSet.getInt("KITCHEN_ID");
								Kitchen kitchenSlotCapacity = new Kitchen();
								kitchenSlotCapacity.setKitchenId(kitchenId);
								kitchenSlotCapacity.setSlotID(timeSlotId);
								kitchenSlotCapacity.setSlotCapacity(slotCapacity);
								kitchenSlotList.add(kitchenSlotCapacity);
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
		
	
		for(Kitchen kitchen : kitchenSlotList)
			System.out.println("Kitchen id: "+kitchen.getKitchenId()+" Slot ID : "+kitchen.getSlotID()
					+" Slot Capacity : "+kitchen.getSlotCapacity());
		return kitchenSlotList;
	}
	
	/**
	 * This method returns the multi type bikers of kitchen
	 * @param kicthenId
	 * @return
	 */
	public static ArrayList<String> findMultiTypeBikerOfKitchen(int kicthenId){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";

			sql ="select delivery_boy_user_id"
					+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'"
					+ " and is_single_order_biker='N'";

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, kicthenId);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					bikerList.add(resultSet.getString("delivery_boy_user_id"));
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
		System.out.println(bikerList);
		return bikerList;
	}
	
	public static int getAvailableLunchQuantity(String bikerUserId, String deliveryday ){
		int noOfFreeSlots = 0 ,lunchSlot = 0;
		lunchSlot = getNoOfLunchSlot();
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/* noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots = (Integer)(bikerCapacity*lunchSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			
			noOfFreeSlots = (bikerCapacity*lunchSlot) - noOfFreeSlots;
				
		}
		System.out.println("free lunch: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	public static int getAvailableDinnerQuantity( String bikerUserId, String deliveryday ){
		int noOfFreeSlots = 0 ,dinnerSlot = 0;
		dinnerSlot = getNoOfDinnerSlot();
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/*noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots =(Integer) (bikerCapacity*dinnerSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			noOfFreeSlots = (bikerCapacity*dinnerSlot) - noOfFreeSlots;	
		}
		System.out.println("free dinner: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	public static int getNoOfLunchSlot(){
		int lunchSlot =  0 ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = " select count(is_lunch)AS lunch "
								+" from fapp_timeslot where "
								+" is_lunch='Y' AND "
								+" is_active='Y' and is_delete='N' ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							lunchSlot = resultSet.getInt("lunch");
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
		return lunchSlot;
	}
	
	public static int getNoOfDinnerSlot(){
		int dinnerSlot =  0 ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = " select count(is_dinner)AS dinner "
								+" from fapp_timeslot where "
								+" is_dinner='Y' AND "
								+" is_active='Y' and is_delete='N' ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							dinnerSlot = resultSet.getInt("dinner");
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
		return dinnerSlot;
	}
}
