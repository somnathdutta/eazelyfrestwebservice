package com.appsquad.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pojo.Kitchen;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class KitchenFinder {

	/*public static void main(String[] args) {
		ArrayList<OrderItems> orderItems = new ArrayList<OrderItems>();
		orderItems.add(new OrderItems(1, 66, "5", 12, 100.0));
		orderItems.add(new OrderItems(1, 66, "6", 10, 100.0));
		orderItems.add(new OrderItems(1, 66, "7", 10, 100.0));
		getKitchenOfOrderedItem(orderItems, "LUNCH", "TODAY", "Salt Lake, Sector 1");
		//ArrayList<Integer> dealingKitchenIds = getKitchenIds(orderItems, "LUNCH", "TODAY", "Salt Lake, Sector 1");
		//System.out.println("dealing kitchens " +dealingKitchenIds);
	}*/
	
		
	public static ArrayList<OrderItems> getKitchenOfOrderedItem( ArrayList<OrderItems> orderItems, String mealType,
			String deliveryDay, String area){	
		for(OrderItems  items : orderItems){
			 ArrayList<Kitchen> dealingKitchens = getItemServableKitchens(items.cuisineId, items.itemCode, items.quantity, mealType, deliveryDay, area);
			 items.setKitchenList(dealingKitchens);			
		 }
		return orderItems;
	}
	
	public static ArrayList<Integer> getDealingKitchenIds( ArrayList<OrderItems> orderItems){
		Set<Integer> kitchenIdSet = new HashSet<Integer>();
		
		for(OrderItems  items : orderItems){
			for(Kitchen itemKitchen : items.getKitchenList()){
				kitchenIdSet.add(itemKitchen.getKitchenId());
			}	
		}
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>(kitchenIdSet);
		return dealingKitchenIds;
	}
	
	
	
	public static ArrayList<Kitchen> getItemServableKitchens(int cuisineId, String itemCode, int quantity, 
			String mealType, String deliveryDay, String area){
		ArrayList<Kitchen> tempKitchens = new ArrayList<Kitchen>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		ArrayList<Kitchen> selectedKitchens = new ArrayList<Kitchen>();
		System.out.println("\n===========================================================");
		System.out.println("|      ITEM CODE:: ("+itemCode+") USER QTY:: "+quantity);
		System.out.println("=============================================================");
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select kitchen_id,stock AS stock from vw_active_kitchen_items"
							+ " where item_code = ? and serving_areas like ? and is_active='Y'";
					}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "select kitchen_id,stock_tomorrow AS stock from vw_active_kitchen_items"
								+ " where item_code = ? and serving_areas like ?  and is_active_tomorrow='Y'";
					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select kitchen_id,dinner_stock AS stock from vw_active_kitchen_items"
								+ " where item_code = ? and serving_areas like ?  and is_active='Y'";
					}else{
						sql = "select kitchen_id,dinner_stock_tomorrow AS stock from vw_active_kitchen_items"
								+ " where item_code = ? and serving_areas like ? and is_active_tomorrow='Y' ";
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						preparedStatement.setString(2, "%"+area+"%");
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							Kitchen kitchen =  new Kitchen();
							kitchen.setKitchenId(resultSet.getInt("kitchen_id"));
							kitchen.setItemStock(resultSet.getInt("stock"));
							kitchen.setTotalItemStock(resultSet.getInt("stock"));
							tempKitchens.add(kitchen);
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
		java.util.Collections.sort(tempKitchens);
		
		System.out.println("Kitchen item stock for item code "+itemCode+" is : "+tempKitchens);
		int totalItemStock = 0 , servable=0;ArrayList<Integer> tempKitchenIds = new ArrayList<Integer>();
		boolean servableByAllKitchen = false,isKitchenFound= false, servableByMultiKitchen = false, servableBySingleKitchen = false;
	    if(tempKitchens.size()>0){
			isKitchenFound = true;
		}
		
		if(isKitchenFound){
			for(Kitchen kitchen : tempKitchens){
				if(kitchen.getItemStock() >= quantity){
					//System.out.println("Kitchen "+kitchen.getKitchenId()+" is servable!");
					selectedKitchenIds.add(kitchen.getKitchenId());
					servable++;
				}
				totalItemStock += kitchen.getItemStock();
			}
			
			if(totalItemStock >= quantity){
				servableByMultiKitchen = true;
			}
			
			if(servable == tempKitchens.size()){
				servableByAllKitchen = true;
			}
			if(servableByAllKitchen){
				servableByMultiKitchen = false;
			}
			
			if(selectedKitchenIds.size()==1){
				servableBySingleKitchen = true;
				servableByMultiKitchen = false;
				servableByAllKitchen = false;
			}
			
			if(servableByAllKitchen){
				System.out.println("Do R.R. between kitchens.. ");
				
				for(Kitchen kitchen : tempKitchens){
					tempKitchenIds.add(kitchen.getKitchenId());
				}	
				selectedKitchenIds.clear();
				//int rrKitchenId = RRKitchen.getRRSingleKitchen(tempKitchenIds,cuisineId);
				Kitchen orderKitchen = new Kitchen();
				orderKitchen.setUserItemQuantity(quantity);
				orderKitchen.setKitchenId(tempKitchenIds.get(0));
				//orderKitchen.setKitchenId(rrKitchenId);
				orderKitchen.setTotalItemStock(totalItemStock);
				selectedKitchens.add(orderKitchen);
				selectedKitchenIds.add(tempKitchenIds.get(0));
				//selectedKitchenIds.add(rrKitchenId);
			}
			
			if(servableBySingleKitchen){
				System.out.println("Single kicthen");	
				for(Integer kitchen : selectedKitchenIds){
					Kitchen orderKitchen = new Kitchen();
					orderKitchen.setUserItemQuantity(quantity);
					orderKitchen.setKitchenId(kitchen);
					orderKitchen.setTotalItemStock(totalItemStock);
					selectedKitchens.add(orderKitchen);
				}
				
			}
			
			if(servableByMultiKitchen){
				System.out.println(" - - - -  - -Item will be servable by more than one kitchen - - - - - - -");
				for(Kitchen kitchen : tempKitchens){
					Kitchen userQtyKitchen = new Kitchen();
					if(quantity>=kitchen.getItemStock()){
						userQtyKitchen.setKitchenId(kitchen.getKitchenId());
						userQtyKitchen.setUserItemQuantity(kitchen.getItemStock());
						userQtyKitchen.setTotalItemStock(kitchen.getTotalItemStock());
					}else{
						userQtyKitchen.setKitchenId(kitchen.getKitchenId());
						userQtyKitchen.setUserItemQuantity(quantity);
						userQtyKitchen.setTotalItemStock(kitchen.getTotalItemStock());
					}
					selectedKitchens.add(userQtyKitchen);
					quantity = quantity - kitchen.getItemStock();
					selectedKitchenIds.add(kitchen.getKitchenId());
				}
					
			}
			
		}else{
			
		}
		System.out.println("==========================================================");
		System.out.println("| 		Selected kitchen with order qty : "+selectedKitchens);
		System.out.println("==========================================================");
		return selectedKitchens;
	}
	
	
	/**
	 * This method tells whether a kitchen is of bengali or NI type
	 * if returns 1 it means bengali else NI
	 * @param kitchenId
	 * @return kitchenType bengali or NI kitchen
	 */
	public static int findKitchenType(int kitchenId){
		int cuisinId = 0;
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select kitchen_cuisine_id from fapp_kitchen_stock where kitchen_id = ?";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, kitchenId);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					cuisinId = resultSet.getInt("kitchen_cuisine_id");
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
		return cuisinId;
	}
}
