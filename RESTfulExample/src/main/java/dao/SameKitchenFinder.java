package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pojo.MealTypePojo;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class SameKitchenFinder {

	/*
	public static void main(String[] args) {
		ArrayList<OrderItems> orderItems = new ArrayList<OrderItems>();
		orderItems.add(new OrderItems(1, 66, "3", 3, 100.0));
		orderItems.add(new OrderItems(1, 66, "20", 1, 100.0));
		//orderItems.add(new OrderItems(1, 66, "21", 2, 100.0));
		orderItems.add(new OrderItems(1, 66, "2", 5, 100.0));
		//findItemStockWithKitchen(orderItems, "LUNCH", "TODAY", "Salt Lake, Sector 1");
		MealTypePojo mealTypePojo = new MealTypePojo();
		mealTypePojo.setLunchToday(true);
		
		ArrayList<Integer> lastKitchen = getLastKitchenId(orderItems,"8017383169",mealTypePojo, "Salt Lake, Sector 5" );
		if(lastKitchen.size() == 1){
			orderWithLastKitchen(orderItems, lastKitchen.get(0));
		}
	}*/
	
	public static ArrayList<OrderItems> orderWithLastKitchen(ArrayList<OrderItems> orderItems, int kitchenId){
		ArrayList<OrderItems> orderItemList = new ArrayList<OrderItems>();
		
		for(OrderItems items:  orderItems){
			items.kitchenId = kitchenId;
		}

		for(OrderItems items:  orderItems){
			System.out.println("Item : "+items.itemCode+ " kitchen : "+items.kitchenId);
		}
		return orderItemList;
	}
	
	public static ArrayList<Integer> getLastKitchenId(ArrayList<OrderItems> orderItemList, String contactNumber,
			MealTypePojo mealTypePojo, String area){
		System.out.println("-------------------------------------------");
		System.out.println("LASTLY ORDERED KITCHEN FINDING STARTS. . . ");
		System.out.println("-------------------------------------------");
		String sqlQuery = "";
		Set<Integer> oldServedKitchenSet = new HashSet<Integer>();
		ArrayList<Integer> kitchenIdList = new ArrayList<Integer>();
		ArrayList<Integer> oldServedKitchen = new ArrayList<Integer>();
		
		int totalNoOfQty = 0;
		boolean isAllItemServable =  false,isBikerAvailable = false;
		
		
		if(mealTypePojo.isLunchToday()){
			sqlQuery = "select distinct kitchen_id from vw_last_order_user where contact_number = ? " 
					+" and delivery_zone = ? and kitchen_active='Y' and "
					+" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					+" and delivery_zone = ?  ) ";
		}else if(mealTypePojo.isLunchTomorrow()){
			sqlQuery = "select distinct kitchen_id from vw_last_order_user where contact_number = ? " 
					+" and delivery_zone = ? and kitchen_active='Y' and "
					+" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					+" and delivery_zone = ?  ) ";
		}else if(mealTypePojo.isDinnerToday()){
			sqlQuery = "select distinct kitchen_id from vw_last_order_user where contact_number = ? " 
					+" and delivery_zone = ? and kitchen_active='Y' and "
					+" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					+" and delivery_zone = ?  ) ";
		}else{
			sqlQuery = "select distinct kitchen_id from vw_last_order_user where contact_number = ? " 
					+" and delivery_zone = ? and kitchen_active='Y' and "
					+" order_id=(select max(order_id) from vw_last_order_user where contact_number =? "  
					+" and delivery_zone = ?  ) ";
		}

		try {
			Connection connection= DBConnection.createConnection();
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				try {
					preparedStatement = connection.prepareStatement(sqlQuery);
					preparedStatement.setString(1, contactNumber);
					preparedStatement.setString(2, area);
					preparedStatement.setString(3, contactNumber);
					preparedStatement.setString(4, area);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						int kitchenId = resultSet.getInt("kitchen_id");
						System.out.println("Last kitchen: "+kitchenId);
						oldServedKitchenSet.add(kitchenId);
						kitchenIdList.add(kitchenId);
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

		ArrayList<String> itemcodeList = new ArrayList<String>();
		for(OrderItems order : orderItemList){
			itemcodeList.add("'"+order.itemCode+"'");
			totalNoOfQty += order.quantity;
		}
		String a = itemcodeList.toString();
		String fb = a.replace("[", "(");
		String itemcodes = fb.replace("]", ")");
		
		
		if(oldServedKitchenSet.size()==1){
			isAllItemServable = ItemFinder.isKitchenServingAllItem(itemcodes, kitchenIdList.get(0), orderItemList.size(), mealTypePojo);
			if(isAllItemServable){
				isBikerAvailable = BikerFinder.isBikerAvailable(kitchenIdList.get(0), totalNoOfQty, mealTypePojo);
				if(isBikerAvailable){
					oldServedKitchen.add(kitchenIdList.get(0));
				}else{
					System.out.println("------------------------------------------");
					System.out.println("Biker not availabale for old kitchen");
					System.out.println("------------------------------------------");
				}
			}else{
				System.out.println("------------------------------------------");
				System.out.println("All items are not servable by old kitchen");
				System.out.println("------------------------------------------");
			}
		}else{
			System.out.println("------------------------------------------");
			System.out.println("No kitchen");
			System.out.println("------------------------------------------");
		}
	
		System.out.println(oldServedKitchen);
		return oldServedKitchen;
	}
	
	
}
