package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pojo.MealTypePojo;
import sql.SameUserSQL;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class SameUserPlaceOrder {

	
	public static ArrayList<Integer> getLastKitchenId(ArrayList<OrderItems> orderItemList, String contactNumber,
			String deliveryAddress, MealTypePojo mealTypePojo, String pincode){
		System.out.println("*** *** *** SAME USER ORDER PLACEMENT CODE **** * ****");
		System.out.println("PINCODE: "+pincode+" CONTACT:: "+contactNumber);
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		Set<Integer> kitchenSet = new HashSet<Integer>();
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		ArrayList<String> iemcode = new ArrayList<String>();
		ArrayList<Integer> cuisineIdList = new ArrayList<Integer>();
		int totalOrderedItems = 0;
		for(OrderItems order : orderItemList){
			iemcode.add("'"+order.itemCode+"'");
			cuisineIdList.add(order.cuisineId);
			totalOrderedItems ++;
		}
		String a = iemcode.toString();
		String fb = a.replace("[", "(");
		String itemcodes = fb.replace("]", ")");
		
		String cu = cuisineIdList.toString();
		String cufb = cu.replace("[", "(");
		String cuisineIds = cufb.replace("]", ")");
		System.out.println("CUisine ids:: "+cuisineIds);
		System.out.println("Item code: "+itemcodes);
		String sqlQuery = "";
		if(mealTypePojo.isLunchToday()){
			sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? " 
					 +" and pincode= ? and item_code in "+itemcodes+""
					 +" and order_date =(select order_date from vw_last_order_user where contact_number = ?"
					 +" and pincode = ? "
					 +" order by order_date desc limit 1) and stock >0 order by cuisine_id ";
		}else if(mealTypePojo.isLunchTomorrow()){
			sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? " 
					 +" and pincode= ? and item_code in "+itemcodes+""
					 +" and order_date =(select order_date from vw_last_order_user where contact_number = ?"
					 +" and pincode = ? "
					 +" order by order_date desc limit 1) and stock_tomorrow >0 order by cuisine_id ";
		}else if(mealTypePojo.isDinnerToday()){
			sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? " 
					 +" and pincode= ? and item_code in "+itemcodes+""
					 +" and order_date =(select order_date from vw_last_order_user where contact_number = ?"
					 +" and pincode = ? "
					 +" order by order_date desc limit 1) and dinner_stock >0 order by cuisine_id ";
		}else{
			sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? " 
					 +" and pincode= ? and item_code in "+itemcodes+""
					 +" and order_date =(select order_date from vw_last_order_user where contact_number = ?"
					 +" and pincode = ? "
					 +" order by order_date desc limit 1) and dinner_stock_tomorrow >0 order by cuisine_id ";
		}
		
		/*String sqlQuery =  "select distinct order_id,kitchen_id "
				+" from vw_last_order_user where contact_number = ? "
				+" and delivery_address LIKE ? and cuisine_id IN "+cuisineIds+" "
				+" order by order_id desc limit 1";*/
		
		try {
				Connection connection= DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(sqlQuery);
						preparedStatement.setString(1, contactNumber);
						preparedStatement.setString(2, pincode);
						preparedStatement.setString(3, contactNumber);
						preparedStatement.setString(4, deliveryAddress);
					
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int kitchenId = resultSet.getInt("kitchen_id");
							if(isKitchenServingItem(itemcodes, kitchenId, totalOrderedItems)){
								if(FindKitchensByRoundRobin.isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
									kitchenSet.add(kitchenId);
								}
							}
							//kitchenSet.add(kitchenId);
							//selectedKitchenIds.add(kitchenId);
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
		
		/*if(dealingKitchenIds.size() > 0){
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						for(Integer kid : dealingKitchenIds){
							preparedStatement = connection.prepareStatement(SameUserSQL.kitchenStockQuery);
							preparedStatement.setInt(1, kid);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								int stock = resultSet.getInt("stock");
								if( stock > 0){
									kitchenStockMap.put(kid, stock );
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		}*/
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>(kitchenSet);
		
		for(int i=0;i<orderItemList.size();i++){
			dealingKitchenIds.addAll(selectedKitchenIds);
		}
		if(dealingKitchenIds.size()==0){
			System.out.println("***************************************************************************");
			System.out.println("***** NO LAST MATCHING KITCHEN FOUND FROM SAME USER ORDER PLACEMENT !******");
			System.out.println("***************************************************************************");
		}else{
			System.out.println("@@@@@@@ Lastly order kitchens found sucessfully::@@@@@ "+dealingKitchenIds);
			for(int i=0 ; i < orderItemList.size() && i < dealingKitchenIds.size(); i++){
				System.out.print("CuisineID:"+orderItemList.get(i).cuisineId+"\t");
				System.out.print("CatID:"+orderItemList.get(i).categoryId+"\t");
				System.out.print("ItemCode:"+orderItemList.get(i).itemCode+"\t");
	    		System.out.print("Quantity:"+orderItemList.get(i).quantity+"\t");
	    		System.out.print("kitchenID:"+dealingKitchenIds.get(i)+"\n");
	    	}
		}
		return dealingKitchenIds;
	}
	
	public static boolean isKitchenHavingStock(int kitchenId, MealTypePojo mealTypePojo){
		boolean stockAvailable = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select distinct(stock)As stock from fapp_kitchen_items where kitchen_id = ?";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select distinct(stock_tomorrow)As stock from fapp_kitchen_items where kitchen_id = ?";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select distinct(dinner_stock)As stock from fapp_kitchen_items where kitchen_id = ?";
					}else{
						sql = "select distinct(dinner_stock_tomorrow)As stock from fapp_kitchen_items where kitchen_id = ?";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return stockAvailable;
	}
	
	public static boolean isKitchenServingItem(String itemCodes, int kitchenId, int totalOrderedItems){
		boolean isKitchenServing = false;
		int totalItems = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select count(item_code)As total_items from "
							+ " fapp_kitchen_items where item_code in"+itemCodes+" and kitchen_id=?";
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
		}else{
			isKitchenServing = false;
		}
		return isKitchenServing;
	}
}
