package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pojo.MealTypePojo;

import com.appsquad.finder.FreeBikerDAO;
import com.mkyong.rest.DBConnection;

public class BikerFinder {

	public static boolean isBikerAvailable(int kitchenId, int totalQuantity, MealTypePojo mealTypePojo){
		boolean bikerAvailable = false;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		
		if(totalQuantity == 1){
			bikerAvailable = isSingleBikerAvailable(kitchenId, mealTypePojo, bikerCapacity, bikerOrders, totalQuantity);
		}else{
			bikerAvailable = isMultipleBikerAvailable(kitchenId, mealTypePojo, bikerCapacity, bikerOrders,totalQuantity);
		}
		return bikerAvailable;
	}
	
	public static boolean isSingleBikerAvailable(int kitchenId,  MealTypePojo mealTypePojo, 
			int bikerCapacaity, int bikerOrders, int totalNoOfQuantity){
		boolean isSingleBikerAvailable = false;int noOfFreeSlots = 0;
		System.out.println("-----------------------------------------");
		System.out.println("Single biker free quantity checking. . . ");
		System.out.println("-----------------------------------------");
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "";
				if(mealTypePojo.isLunchToday()){
					sql = "select sum(?-quantity) "
						+" as no_of_free_slots from "  
						+" vw_driver_today_status  "
						+" where is_single_order_biker = 'Y' "
						+" and is_slot_locked = 'N' "
						+" and is_lunch='Y' and kitchen_id= ? "
						+" and (quantity<? or no_of_orders <?)";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_tomorrow_status  "
							+" where is_single_order_biker = 'Y' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='Y' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_today_status  "
							+" where is_single_order_biker = 'Y' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='N' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}else{
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_tomorrow_status  "
							+" where is_single_order_biker = 'Y' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='N' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, bikerCapacaity);
					preparedStatement.setInt(2, kitchenId);
					preparedStatement.setInt(3, bikerCapacaity);
					preparedStatement.setInt(4, bikerOrders);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						noOfFreeSlots = resultSet.getInt("no_of_free_slots");
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
		System.out.println("Total free quantity : "+noOfFreeSlots);
		if(noOfFreeSlots > 0 && noOfFreeSlots >= totalNoOfQuantity){
			isSingleBikerAvailable = true;
		}else{
			System.out.println("Biker capacity is less than order quantity!");
		}
		return isSingleBikerAvailable;
	}
	
	public static boolean isMultipleBikerAvailable(int kitchenId,  MealTypePojo mealTypePojo, 
			int bikerCapacaity, int bikerOrders, int totalNoOfQuantity){
		boolean isMultipleBikerAvailable = false;int noOfFreeSlots = 0;
		System.out.println("-----------------------------------------");
		System.out.println("Multiple biker free quantity checking. . . ");
		System.out.println("-----------------------------------------");
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "";
				if(mealTypePojo.isLunchToday()){
					sql = "select sum(?-quantity) "
						+" as no_of_free_slots from "  
						+" vw_driver_today_status  "
						+" where is_single_order_biker = 'N' "
						+" and is_slot_locked = 'N' "
						+" and is_lunch='Y' and kitchen_id= ? "
						+" and (quantity<? or no_of_orders <?)";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_tomorrow_status  "
							+" where is_single_order_biker = 'N' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='Y' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}else if(mealTypePojo.isDinnerToday()){
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_today_status  "
							+" where is_single_order_biker = 'N' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='N' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}else{
					sql = "select sum(?-quantity) "
							+" as no_of_free_slots from "  
							+" vw_driver_tomorrow_status  "
							+" where is_single_order_biker = 'N' "
							+" and is_slot_locked = 'N' "
							+" and is_lunch='N' and kitchen_id= ? "
							+" and (quantity<? or no_of_orders <?)";
				}
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, bikerCapacaity);
					preparedStatement.setInt(2, kitchenId);
					preparedStatement.setInt(3, bikerCapacaity);
					preparedStatement.setInt(4, bikerOrders);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						noOfFreeSlots = resultSet.getInt("no_of_free_slots");
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
		System.out.println("Total free quantity : "+noOfFreeSlots);
		if(noOfFreeSlots > 0 && noOfFreeSlots >= totalNoOfQuantity){	
			isMultipleBikerAvailable = true;
		}else{
			System.out.println("Biker capacity is less than order quantity!");
		}
		return isMultipleBikerAvailable;
	}
}
