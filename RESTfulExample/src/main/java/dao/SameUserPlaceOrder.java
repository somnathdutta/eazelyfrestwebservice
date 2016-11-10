package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import pojo.MealTypePojo;
import sql.SameUserSQL;
import utility.ValueComparator;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class SameUserPlaceOrder {

	
	public static ArrayList<Integer> getLastKitchenId(ArrayList<OrderItems> orderItemList, String contactNumber,
			String deliveryAddress, MealTypePojo mealTypePojo, String pincode,String area){
		System.out.println("*** *** *** SAME USER ORDER PLACEMENT CODE **** * ****");
		System.out.println("AREA: "+area+" CONTACT:: "+contactNumber);
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		Set<Integer> oldServedKitchenSet = new HashSet<Integer>();
		Set<Integer> kitchenSet = new HashSet<Integer>();
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		ArrayList<String> iemcode = new ArrayList<String>();
		ArrayList<Integer> cuisineIdList = new ArrayList<Integer>();
		int totalOrderedItems = 0,totalOrderedQuantity=0;
		for(OrderItems order : orderItemList){
			iemcode.add("'"+order.itemCode+"'");
			cuisineIdList.add(order.cuisineId);
			totalOrderedItems ++;
			totalOrderedQuantity += order.quantity;
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
			/*sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? " 
					 +" and pincode= ? and item_code in "+itemcodes
					 +" and order_ =(select created_date from vw_last_order_user where contact_number = ?"
					 +" and pincode = ? "
					 +" order by created_date desc limit 1) and stock >0 order by cuisine_id ";*/
			/*sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? and is_active='Y' " 
					 +" and pincode= ? and stock >0 and kitchen_active='Y' and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and pincode= ?  ) order by cuisine_id ";*/
			sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? and is_active='Y' " 
					 +" and delivery_zone = ? and stock >0 and kitchen_active='Y' and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and delivery_zone = ?  ) order by cuisine_id ";
		}else if(mealTypePojo.isLunchTomorrow()){
			/*sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? and is_active_tomorrow='Y' " 
					 +" and pincode = ?  and stock_tomorrow >0 and kitchen_active='Y'   and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and pincode= ?  ) order by cuisine_id ";*/
			sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? and is_active_tomorrow='Y' " 
					 +" and delivery_zone= ?  and stock_tomorrow >0 and kitchen_active='Y'   and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and delivery_zone = ?  ) order by cuisine_id ";
		}else if(mealTypePojo.isDinnerToday()){
			/*sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? and is_active='Y' " 
					 +" and pincode= ? and dinner_stock >0 and kitchen_active='Y'   and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and pincode= ?  ) order by cuisine_id ";*/
			sqlQuery = "select distinct kitchen_id,cuisine_id from vw_last_order_user where contact_number = ? and is_active='Y' " 
					 +" and delivery_zone = ? and dinner_stock >0 and kitchen_active='Y'   and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and delivery_zone = ?  ) order by cuisine_id ";
		}else{
			/*sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? and is_active_tomorrow='Y' " 
					 +" and pincode= ? and dinner_stock_tomorrow >0 and kitchen_active='Y'  and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and pincode= ?  ) order by cuisine_id ";*/
			sqlQuery = "select distinct kitchen_id,cuisine_id  from vw_last_order_user where contact_number = ? and is_active_tomorrow='Y' " 
					 +" and delivery_zone = ? and dinner_stock_tomorrow >0 and kitchen_active='Y'  and cuisine_id in "+cuisineIds+" and "
					 +" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					 +" and delivery_zone = ?  ) order by cuisine_id ";
		}
		
		try {
				Connection connection= DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(sqlQuery);
						preparedStatement.setString(1, contactNumber);
						//preparedStatement.setString(2, pincode);
						preparedStatement.setString(2, area);
						preparedStatement.setString(3, contactNumber);
						//preparedStatement.setString(4, pincode);
						preparedStatement.setString(4, area);
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int kitchenId = resultSet.getInt("kitchen_id");
							int kitchenCurrStock = RoundRobinKitchenFinder.getCurrentKitchenStock(kitchenId, mealTypePojo);
							kitchenStockMap.put(kitchenId, kitchenCurrStock);
							oldServedKitchenSet.add(kitchenId);
							/*//Check whether kitchen serving that item or not
							if(isKitchenServingItem(itemcodes, kitchenId, totalOrderedItems)){
								if(isKitchenCapable(kitchenId, mealTypePojo, totalOrderedQuantity, itemcodes)){
									if(FindKitchensByRoundRobin.isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
										kitchenSet.add(kitchenId);
									}
								}
							}
							//kitchenSet.add(kitchenId);
							//selectedKitchenIds.add(kitchenId);
*/						}
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
		System.out.println("Old served kitchens:::::::::::"+oldServedKitchenSet);
		ArrayList<Integer> sortedKitchenIds = new ArrayList<Integer>();
		ValueComparator bvc = new ValueComparator(kitchenStockMap);
        TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
        sorted_map.putAll(kitchenStockMap);
		System.out.println("::::::::Sorted map with max stock(125) ::::::"+sorted_map);
		
		for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
			sortedKitchenIds.add(mp.getKey());
		}
		System.out.println(":::Sorted kitchen id:: "+sortedKitchenIds);
		
		int bengItemQty =0 ,niItemQty = 0;
		for(Integer oldKitchen : sortedKitchenIds){
			if(TimeSlotFinder.findKitchenType(oldKitchen)==1){
				for(OrderItems orders : orderItemList){
					if(RoundRobinKitchenFinder.isKitchenServingItem(orders.itemCode, oldKitchen)){
							bengItemQty++;
					}
				}
			}else{
				for(OrderItems orders : orderItemList){
					if(RoundRobinKitchenFinder.isKitchenServingItem(orders.itemCode, oldKitchen)){
							niItemQty++;
					}
				}
			}
		}
		if(bengItemQty+niItemQty == totalOrderedItems){
			for(Integer oldkitchen : sortedKitchenIds){
				if(isKitchenCapable(oldkitchen, mealTypePojo, totalOrderedQuantity, itemcodes)){
					if(FindKitchensByRoundRobin.isKitchenHavingFreeBikers(oldkitchen, mealTypePojo)){
						kitchenSet.add(oldkitchen);
					}
				}
			}
			
		}
		
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>(kitchenSet);
		
		
		for(int i=0;i<orderItemList.size();i++){
			dealingKitchenIds.addAll(selectedKitchenIds);
		}
		if(selectedKitchenIds.size()==0){
	//	if(dealingKitchenIds.size()==0){
			System.out.println("***************************************************************************");
			System.out.println("***** NO LAST MATCHING KITCHEN FOUND FROM SAME USER ORDER PLACEMENT !******");
			System.out.println("***************************************************************************");
		}else{
			System.out.println("@@@@@@@ Lastly order kitchens found sucessfully::@@@@@ "+selectedKitchenIds);
			//System.out.println("@@@@@@@ Lastly order kitchens found sucessfully::@@@@@ "+dealingKitchenIds);
			/*for(int i=0 ; i < orderItemList.size() && i < dealingKitchenIds.size(); i++){
				System.out.print("CuisineID:"+orderItemList.get(i).cuisineId+"\t");
				System.out.print("CatID:"+orderItemList.get(i).categoryId+"\t");
				System.out.print("ItemCode:"+orderItemList.get(i).itemCode+"\t");
	    		System.out.print("Quantity:"+orderItemList.get(i).quantity+"\t");
	    		System.out.print("kitchenID:"+dealingKitchenIds.get(i)+"\n");
	    	}*/
		}
		return selectedKitchenIds;
		//return dealingKitchenIds;
	}
	
	public static int getKitchenCurrentStock(int kitchenId, MealTypePojo mealTypePojo){
		int stockAvailable = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select distinct (stock) AS stock from fapp_kitchen_items where kitchen_id= ?";
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
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							stockAvailable = resultSet.getInt("stock");
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
		System.out.println("Stock for "+kitchenId+" is ::"+stockAvailable);
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
							+ " fapp_kitchen_items where item_code in"+itemCodes+" and kitchen_id=?  ";
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
			System.out.println("Kitchen serving the item!");
		}else{
			isKitchenServing = false;
			System.out.println("Kitchen not serving the item!");
		}
		
		return isKitchenServing;
	}
	
	public static boolean isKitchenCapable(int kitchenId, MealTypePojo mealTypePojo, 
			int totalOrderedQuantity,String itemCodes){
		boolean isKitchenCapable = false;
		int availableStock = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					
					if(mealTypePojo.isLunchToday()){
						sql = "select distinct(stock)As stock from fapp_kitchen_items "
								+ "where kitchen_id = ? and is_active='Y' "
								+ "and item_code IN "+itemCodes;
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select distinct(stock_tomorrow)As stock from fapp_kitchen_items "
								+ "where kitchen_id = ? and is_active_tomorrow='Y'  "
								+ "and item_code IN "+itemCodes;
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select distinct(dinner_stock)As stock from fapp_kitchen_items "
								+ "where kitchen_id = ? and is_active='Y'  "
								+ "and item_code IN "+itemCodes;
					}else{
						sql = "select distinct(dinner_stock_tomorrow)As stock from fapp_kitchen_items "
								+ "where kitchen_id = ? and is_active_tomorrow='Y'  "
								+ "and item_code IN "+itemCodes;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							availableStock = resultSet.getInt("stock");
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
		if(availableStock>0){
			if(availableStock >= totalOrderedQuantity){
				isKitchenCapable = true;
				System.out.println("Kitchen "+kitchenId+" having stock "+availableStock+" > totalOrderedItems of the item!"+totalOrderedQuantity);
			}else{
				// check also for whether (totalOrderedQuantity-availableStock) >0
				
				isKitchenCapable = false;
				System.out.println("Kitchen "+kitchenId+"  not having stock "+availableStock+" < totalOrderedItems of the item!"+totalOrderedQuantity);
			}
				//changes on 23_09	
		}else{
			isKitchenCapable = false;
			System.out.println("Kitchen not having stock of the item!");
		}
		return isKitchenCapable;
	}
}
