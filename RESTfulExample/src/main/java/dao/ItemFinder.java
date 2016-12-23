package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

import pojo.MealTypePojo;

public class ItemFinder {

	public static boolean isKitchenServingAllItem(String itemCodes, int kitchenId, 
			int totalOrderedItems,MealTypePojo mealTypePojo){
		boolean isKitchenServing = false;
		int totalItems = 0;
		
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql="select count(item_code)As total_items from "
								+ " vw_active_kitchen_items where item_code in"+itemCodes+" "
							    + "and kitchen_id=? and is_active = 'Y' and stock > 0";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql ="select count(item_code)As total_items from "
								+ " vw_active_kitchen_items where item_code in"+itemCodes+" "
								+ "and kitchen_id=? and is_active_tomorrow = 'Y' and stock_tomorrow >0";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(item_code)As total_items from "
								+ " vw_active_kitchen_items where item_code in"+itemCodes+" "
								+ "and kitchen_id=?  and  is_active = 'Y' and dinner_stock > 0";
					}else{
						sql = "select count(item_code)As total_items from "
								+ " vw_active_kitchen_items where item_code in"+itemCodes+" "
								+ "and kitchen_id=?  and  is_active_tomorrow = 'Y' and dinner_stock_tomorrow > 0";
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
						 totalItems = resultSet.getInt("total_items");
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		if(totalOrderedItems==totalItems){
			isKitchenServing = true;
			System.out.println("Kitchen serving all the item!");
		}else{
			isKitchenServing = false;
			System.out.println("Kitchen not serving all the item!");
		}
		
		return isKitchenServing;
	}
}
