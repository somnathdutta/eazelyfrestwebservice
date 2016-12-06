package com.appsquad.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pojo.Kitchen;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

import dao.BikerDAO;

public class AllItemServingKitchenFinder {

	/*public static void main(String[] args) {
		ArrayList<OrderItems> orderItems = new ArrayList<OrderItems>();
		orderItems.add(new OrderItems(1, 66, "5", 4, 100.0));
		orderItems.add(new OrderItems(1, 66, "6", 4, 100.0));
		//orderItems.add(new OrderItems(1, 66, "21", 2, 100.0));
		//orderItems.add(new OrderItems(1, 66, "7", 2, 100.0));
		//findItemStockWithKitchen(orderItems, "LUNCH", "TODAY", "Salt Lake, Sector 1");
		findKitchens("LUNCH", "TODAY", "Salt Lake, Sector 1", orderItems);
	}*/

	/*public static ArrayList<Kitchen> findItemStockWithKitchen(ArrayList<OrderItems> orderItems, String mealType, String deliveryDay, String area){
		ArrayList<Kitchen> kitchenWithItemStock = new  ArrayList<Kitchen>();

		for(OrderItems items : orderItems){
			ArrayList<Kitchen> tempKitchens = findItemServingKitchen(items.cuisineId, items.itemCode, items.quantity, mealType, deliveryDay, area);
			kitchenWithItemStock.addAll(tempKitchens);
		}
		System.out.println(kitchenWithItemStock);
		System.out.println("Tot size: "+kitchenWithItemStock.size());
		return kitchenWithItemStock;
	}

	public static ArrayList<Kitchen> findItemServingKitchen(int cuisineId, String itemCode, int quantity, 
			String mealType, String deliveryDay, String area){
		ArrayList<Kitchen> tempKitchens = new ArrayList<Kitchen>();
		System.out.println("ITEM CODE:: "+itemCode+" USER QTY:: "+quantity);
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";
			if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
				sql = "select kitchen_id,stock AS stock from vw_active_kitchen_items"
						+ " where item_code = ? and serving_areas like ? and is_active='Y' and stock>0";
			}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
				sql = "select kitchen_id,stock_tomorrow AS stock from vw_active_kitchen_items"
						+ " where item_code = ? and serving_areas like ?  and is_active_tomorrow='Y' and stock_tomorrow>0";
			}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
				sql = "select kitchen_id,dinner_stock AS stock from vw_active_kitchen_items"
						+ " where item_code = ? and serving_areas like ?  and is_active='Y' and dinner_stock>0";
			}else{
				sql = "select kitchen_id,dinner_stock_tomorrow AS stock from vw_active_kitchen_items"
						+ " where item_code = ? and serving_areas like ? and is_active_tomorrow='Y' and dinner_stock_tomorrow>0 ";
			}

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, itemCode);
				preparedStatement.setString(2, "%"+area+"%");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					Kitchen kitchen =  new Kitchen();
					kitchen.setItemCode(itemCode);
					kitchen.setKitchenId(resultSet.getInt("kitchen_id"));
					kitchen.setItemStock(resultSet.getInt("stock"));
					kitchen.setUserItemQuantity(quantity);
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
		System.out.println(tempKitchens);
		System.out.println("--------- item ends ----------------");
		return tempKitchens;
	}*/

	public static ArrayList<OrderItems> findKitchens(String mealType, String deliveryDay, String area, ArrayList<OrderItems> orderItems){
		ArrayList<Kitchen> tempKitchens = new ArrayList<Kitchen>();
		ArrayList<String> itemcodeList = new ArrayList<String>();
		ArrayList<OrderItems> orderItemListWithKitchen = new ArrayList<OrderItems>();

		for(OrderItems order : orderItems){
			itemcodeList.add("'"+order.itemCode+"'");
		}

		String a = itemcodeList.toString();
		String fb = a.replace("[", "(");
		String itemcodes = fb.replace("]", ")");
		System.out.println("Item codes:: "+itemcodes);
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet  = null;
			String sql = "";
			if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY") ){
				sql = "select kitchen_id,item_code,stock AS stock "
						+" from vw_active_kitchen_items "
						+" where item_code IN "+itemcodes
						+" and serving_areas LIKE ? "
						+" and is_active='Y' and stock > 0";
			}else if( mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
				sql = "select kitchen_id,item_code,stock_tomorrow AS stock "
						+" from vw_active_kitchen_items "
						+" where item_code IN "+itemcodes
						+" and serving_areas LIKE ? "
						+" and is_active_tomorrow = 'Y' and stock_tomorrow > 0";
			}else if( mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
				sql = "select kitchen_id,item_code,dinner_stock AS stock "
						+" from vw_active_kitchen_items "
						+" where item_code IN "+itemcodes
						+" and serving_areas LIKE ? "
						+" and is_active='Y' and dinner_stock > 0";
			}else {
				sql = "select kitchen_id,item_code,dinner_stock_tomorrow AS stock "
						+" from vw_active_kitchen_items "
						+" where item_code IN "+itemcodes
						+" and serving_areas LIKE ? "
						+" and is_active_tomorrow='Y' and dinner_stock_tomorrow > 0";
			}

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, "%"+area+"%");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					Kitchen kitchen = new Kitchen();
					kitchen.setKitchenId(resultSet.getInt("kitchen_id"));
					kitchen.setItemCode(resultSet.getString("item_code"));
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
		Collections.sort(tempKitchens);
		Map<String, ArrayList<Kitchen>> kitchenItemMap = new HashMap<String, ArrayList<Kitchen>>();
		Map<Integer, ArrayList<Kitchen>> kitchenServingItemsMap = new HashMap<Integer, ArrayList<Kitchen>>();
		ArrayList<Kitchen> itemServableKitchenList = new ArrayList<Kitchen>();

		System.out.println("Ordered item qty:::::::");
		for(OrderItems items : orderItems){
			System.out.println("Item code: "+items.getItemCode()+" Qty : "+items.quantity);
		}

		System.out.println("\nKitchen item stock for the ordered items:   ");
		for(Kitchen kitchen : tempKitchens){
			System.out.println("Kitchen id: "+kitchen.getKitchenId()+ " item code: "+kitchen.getItemCode()+" Stock: "+kitchen.getItemStock());
		}

		Set<Integer> kitchenSet = new HashSet<Integer>();
		for(Kitchen k : tempKitchens){
			kitchenSet.add(k.getKitchenId());
		}

		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];

		System.out.println("\nKitchen free slot qty :");
		ArrayList<Kitchen> kitchenFreeSlotList = FreeBikerDAO.findSlotCapacity(kitchenSet, mealType, deliveryDay, bikerCapa);
		
		
		System.out.println("\nKitchen free qty:");
		//ArrayList<Kitchen> kitchenFreeQtyList = FreeBikerDAO.findKitchenBikerFreeQuantity(kitchenSet,mealType,deliveryDay,bikerCapa);
		ArrayList<Kitchen> kitchenFreeQtyList = new ArrayList<Kitchen>();
		Map<Integer,Integer> kitchenTotalFreeSlotMap = new HashMap<Integer, Integer>();
		for(Kitchen freeKitchen : kitchenFreeSlotList){
			if(kitchenTotalFreeSlotMap.containsKey(freeKitchen.getKitchenId())){
				kitchenTotalFreeSlotMap.put(freeKitchen.getKitchenId(), kitchenTotalFreeSlotMap.get(freeKitchen.getKitchenId())+freeKitchen.getSlotCapacity());
			}else{
				kitchenTotalFreeSlotMap.put(freeKitchen.getKitchenId(), freeKitchen.getSlotCapacity());
			}
		}
		System.out.println(kitchenTotalFreeSlotMap);
		for(Map.Entry<Integer, Integer> me: kitchenTotalFreeSlotMap.entrySet()){
			Kitchen freeKitchen = new Kitchen();
			freeKitchen.setKitchenId(me.getKey());
			freeKitchen.setFreeQty(me.getValue());
			kitchenFreeQtyList.add(freeKitchen);
		}
		

		System.out.println("\nKitchen total free qty: ");
		for(Kitchen kitchen : kitchenFreeQtyList){
			System.out.println("Kitchen id: "+kitchen.getKitchenId()+" Free qty: "+kitchen.getFreeQty() );
		}
		
		
		ArrayList<Integer> kitchenIdList = servableBySingleKitchenList(orderItems, tempKitchens, mealType,  
				deliveryDay, kitchenFreeQtyList,bikerCapacity);
		
		if(kitchenIdList.size() == 1){
			System.out.println("------- SINGLE KITCHEN FOUND -------");
			for(OrderItems items : orderItems){
				ArrayList<Kitchen> kitchenList = new ArrayList<Kitchen>();
				Kitchen kitchen = new Kitchen();
				kitchen.setItemCode(items.itemCode);
				kitchen.setKitchenId(kitchenIdList.get(0));
				kitchen.setUserItemQuantity(items.quantity);
				for(Kitchen dbKitchen : tempKitchens){
					if(kitchen.getKitchenId() == dbKitchen.getKitchenId() && items.getItemCode().equals(dbKitchen.getItemCode())){
						kitchen.setTotalItemStock(dbKitchen.getTotalItemStock());
					}
				}
				kitchenList.add(kitchen);
				items.setKitchenList(kitchenList);
			}
			orderItemListWithKitchen.addAll(orderItems);
			for(OrderItems newItems : orderItemListWithKitchen){
				System.out.println("Item code: "+newItems.getItemCode()+" Kitchen List: "+newItems.getKitchenList());
			}
		}else{
			System.out.println("------- MULTI KITCHEN FOUND -------");
			orderItemListWithKitchen = getSplitKitchenListWithItem(orderItems, tempKitchens, kitchenFreeQtyList);
			System.out.println("NEW SPLIT SIZE: "+orderItemListWithKitchen.size());
		}

		/*for(OrderItems items : orderItems){
			ArrayList<Kitchen> kitchenServingList = new ArrayList<Kitchen>();
			for(Kitchen kitchenItemStock : tempKitchens){
				if(items.itemCode.equals(kitchenItemStock.getItemCode()) ){
					Kitchen serveKitchen = new Kitchen();
					serveKitchen.setKitchenId(kitchenItemStock.getKitchenId());
					serveKitchen.setItemCode(items.itemCode);
					serveKitchen.setItemStock(kitchenItemStock.getItemStock());
					serveKitchen.setUserItemQuantity(items.quantity);
					kitchenServingList.add(serveKitchen);
					itemServableKitchenList.add(serveKitchen);
					kitchenItemMap.put(items.itemCode, kitchenServingList);
				}
			}
		}

		servableBySingleKitchenList(orderItems, tempKitchens);

		System.out.println("\nItem kitchens map kitchenItemMap: "+kitchenItemMap);

		System.out.println("\nItems servable kitchen itemServableKitchenList: "+itemServableKitchenList);*/

		/*Set<Integer> kitchenIdSet = new HashSet<Integer>();

		for(Kitchen kitchenItems: itemServableKitchenList){
			kitchenIdSet.add(kitchenItems.getKitchenId());
		}


		for(Integer kitchenId: kitchenIdSet){
			ArrayList<Kitchen> kitchenServingList = new ArrayList<Kitchen>();
			for(Kitchen kitchen : itemServableKitchenList){
				if(kitchenId == kitchen.getKitchenId()){
					Kitchen item = new Kitchen();
					item.setKitchenId(kitchenId);
					item.setItemCode(kitchen.getItemCode());
					item.setUserItemQuantity(kitchen.getUserItemQuantity());
					item.setItemStock(kitchen.getItemStock());
					kitchenServingList.add(item);
				}
			}
			kitchenServingItemsMap.put(kitchenId, kitchenServingList);
		}

		Map<Integer, Integer> kitchenServingNoOfItemsMap = new HashMap<Integer, Integer>();

		for(Map.Entry<Integer,	ArrayList<Kitchen>> me : kitchenServingItemsMap.entrySet()){
			int listSize = 0;
			listSize = me.getValue().size();
			kitchenServingNoOfItemsMap.put(me.getKey(), listSize);
		}

		System.out.println("kitchenServingNoOfItemsMap: "+kitchenServingNoOfItemsMap);

		System.out.println("\nKitchen serving items kitchenServingItemsMap:: "+kitchenServingItemsMap);

		ArrayList<Kitchen> commonKitchenItemList = new ArrayList<Kitchen>();

		Map<String, ArrayList<Kitchen>> itemKitchenMap = new HashMap<String, ArrayList<Kitchen>>();

		for(OrderItems orderItem : orderItems){

			for(Map.Entry<Integer, ArrayList<Kitchen>> me : kitchenServingItemsMap.entrySet()){
				for(Kitchen kitchenCommonItem : me.getValue()){
					if(orderItem.getItemCode().equals(kitchenCommonItem.getItemCode())){

					}
				}
			}

		}

		for(Map.Entry<Integer, ArrayList<Kitchen>> me : kitchenServingItemsMap.entrySet()){
			for(Kitchen kitchenCommonItem : me.getValue()){
				commonKitchenItemList.add(kitchenCommonItem);
			}
		}
		System.out.println("\ncommonKitchenItemList: "+commonKitchenItemList);*/


		//System.out.println("commonKitchen: "+commonKitchen);
		return orderItemListWithKitchen;
	}


	/**
	 * Return duplicate objects
	 * @param listContainingDuplicates
	 * @return
	 */
	public static Set<Kitchen> findDuplicates(ArrayList<Kitchen> listContainingDuplicates) {

		final Set<Kitchen> setToReturn = new HashSet<Kitchen>();
		final Set<Kitchen> set1 = new HashSet<Kitchen>();

		for (Kitchen kitchen : listContainingDuplicates) {
			if (!set1.add(kitchen)) {
				setToReturn.add(kitchen);
			}
		}
		return setToReturn;
	}

	public static ArrayList<Integer> servableBySingleKitchenList(ArrayList<OrderItems> orderItems, ArrayList<Kitchen> tempKitchens,
			String mealType, String deliveryDay, ArrayList<Kitchen> kitchenFreeQtyList, int bikerCapacity){
		ArrayList<Integer> kitchenIdList = new ArrayList<Integer>();
		Map<Integer, Integer> kitchenServingNoOfItemsMap = new HashMap<Integer, Integer>();
		Map<Integer, ArrayList<Kitchen>> kitchenServingItemsMap = new HashMap<Integer, ArrayList<Kitchen>>();
		ArrayList<Kitchen> itemServableKitchenList = new ArrayList<Kitchen>();
		int totalNoOfQuantity = 0;
		for(OrderItems items : orderItems)
			totalNoOfQuantity += items.quantity;

		for(OrderItems items : orderItems){

			for(Kitchen kitchenItemStock : tempKitchens){
				if(items.itemCode.equals(kitchenItemStock.getItemCode()) && kitchenItemStock.getItemStock()>= items.quantity){
					Kitchen serveKitchen = new Kitchen();
					serveKitchen.setKitchenId(kitchenItemStock.getKitchenId());
					serveKitchen.setItemCode(items.itemCode);
					serveKitchen.setItemStock(kitchenItemStock.getItemStock());
					serveKitchen.setUserItemQuantity(items.quantity);
					itemServableKitchenList.add(serveKitchen);
				}
			}
		}

		Set<Integer> kitchenIdSet = new HashSet<Integer>();

		for(Kitchen kitchenItems: itemServableKitchenList){
			kitchenIdSet.add(kitchenItems.getKitchenId());
		}

		for(Integer kitchenId: kitchenIdSet){
			ArrayList<Kitchen> kitchenServingList = new ArrayList<Kitchen>();
			for(Kitchen kitchen : itemServableKitchenList){
				if(kitchenId == kitchen.getKitchenId()){
					Kitchen item = new Kitchen();
					item.setKitchenId(kitchenId);
					item.setItemCode(kitchen.getItemCode());
					item.setUserItemQuantity(kitchen.getUserItemQuantity());
					item.setItemStock(kitchen.getItemStock());
					kitchenServingList.add(item);
				}
			}
			kitchenServingItemsMap.put(kitchenId, kitchenServingList);
		}

		for(Map.Entry<Integer,	ArrayList<Kitchen>> me : kitchenServingItemsMap.entrySet()){
			int listSize = 0;
			listSize = me.getValue().size();
			kitchenServingNoOfItemsMap.put(me.getKey(), listSize);
		}

		System.out.println("\nServing kitchn item count:::::: "+kitchenServingNoOfItemsMap);
		System.out.println("Total No of order items: "+orderItems.size());
		System.out.println("Total no of order quantity::: "+totalNoOfQuantity);

		for(Map.Entry<Integer, Integer> me : kitchenServingNoOfItemsMap.entrySet()){
			if(me.getValue() == orderItems.size()){
				System.out.println("Kitchen "+me.getKey()+" is capable of serving all ordered items!");
				kitchenIdList.add(me.getKey());
				//break;
			}else{
				System.out.println("Kitchen "+me.getKey()+" is NOT capable of serving all ordered items!");
			}
		}



		ArrayList<Integer> servableKitchenIdList = new ArrayList<Integer>();
		for(Integer kitchen : kitchenIdList){
			for(Kitchen freeKitchen : kitchenFreeQtyList){
				if(kitchen==freeKitchen.getKitchenId()){
					System.out.println("totalNoOfQuantity: "+totalNoOfQuantity+" freeKitchenQty :"+freeKitchen.getFreeQty());
					System.out.println("bikerCapacity: "+bikerCapacity);
					if(totalNoOfQuantity <= freeKitchen.getFreeQty() && totalNoOfQuantity<=bikerCapacity){
						System.out.println("Sevable by kitchen :::::"+kitchen);
						servableKitchenIdList.add(kitchen);
					}
				}
			}
			//break;
		}
		System.out.println("Servable ::::::::::"+servableKitchenIdList);
		int rrKitchenId = 0;
		if(servableKitchenIdList.size()>0){
			rrKitchenId = RRKitchen.getRRSingleKitchen(servableKitchenIdList,1);
			servableKitchenIdList.clear();
			servableKitchenIdList.add(rrKitchenId);
		}
		 
		return servableKitchenIdList;
	}

	/**
	 * This method returns order item list with kitchens
	 * @param orderItems
	 * @param dbKitchens
	 * @return
	 */
	public static ArrayList<OrderItems> getSplitKitchenListWithItem(ArrayList<OrderItems> orderItems , ArrayList<Kitchen> dbKitchens,
			ArrayList<Kitchen> freeBikerKitchenList){
		System.out.println("\nSpilt kitchen code executing . . ");
		ArrayList<OrderItems> orderItemListWithKitchen = new ArrayList<OrderItems>();

		for(OrderItems items : orderItems){
			int orderedQuantity= items.quantity;
			ArrayList<Kitchen> itemKitchenList = new ArrayList<Kitchen>();
			
			for(Kitchen kitchen : dbKitchens){
				if(items.getItemCode().equals(kitchen.getItemCode()) && orderedQuantity > 0){
					
					   Kitchen bean = new Kitchen ();
				       bean.setKitchenId (kitchen. getKitchenId ());
				       bean.setItemStock (kitchen. getItemStock ());
				      // bean.setUserItemQuantity (kitchen. getUserItemQuantity ());
				       bean.setItemCode (items. getItemCode ());
				       if (orderedQuantity >= kitchen.getItemStock() ) {
				        bean.setUserItemQuantity(kitchen.getItemStock ());
				        orderedQuantity = orderedQuantity-kitchen.getItemStock ();
				       } else {
				    	   bean.setUserItemQuantity(orderedQuantity);
				    	   orderedQuantity = orderedQuantity-kitchen.getItemStock ();
				       }
				       bean.setTotalItemStock(kitchen.getTotalItemStock());
				       itemKitchenList.add (bean);
				       items.setKitchenList(itemKitchenList);
				       
				}
			}
		}
		
		
		orderItemListWithKitchen.addAll(orderItems);
		for(OrderItems  items : orderItems){
			System.out.println("item code:"+items.getItemCode());
			System.out.println(items.getKitchenList());
		}
			
		return orderItemListWithKitchen;
	}
	
	
	public static ArrayList<Kitchen> getServableKitchens(int cuisineId, String itemCode, int quantity,
			ArrayList<Kitchen> tempKitchens){
		//ArrayList<Kitchen> tempKitchens = new ArrayList<Kitchen>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		ArrayList<Kitchen> selectedKitchens = new ArrayList<Kitchen>();
		System.out.println("\n===========================================================");
		System.out.println("|      ITEM CODE:: ("+itemCode+") USER QTY:: "+quantity);
		System.out.println("===========================================================");
		
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
				//orderKitchen.setTotalItemStock(totalItemStock);
				selectedKitchens.add(orderKitchen);
				selectedKitchenIds.add(tempKitchenIds.get(0));
				//selectedKitchenIds.add(rrKitchenId);
			}
			
			if(servableBySingleKitchen){
				System.out.println("Single kicthen");	
				for(Integer kitchen : selectedKitchenIds){
					Kitchen orderKitchen = new Kitchen();
					orderKitchen.setItemCode(itemCode);
					orderKitchen.setUserItemQuantity(quantity);
					orderKitchen.setKitchenId(kitchen);
					//orderKitchen.setTotalItemStock(totalItemStock);
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
					//	userQtyKitchen.setTotalItemStock(kitchen.getTotalItemStock());
					}else{
						userQtyKitchen.setKitchenId(kitchen.getKitchenId());
						userQtyKitchen.setUserItemQuantity(quantity);
					//	userQtyKitchen.setTotalItemStock(kitchen.getTotalItemStock());
					}
					selectedKitchens.add(userQtyKitchen);
					quantity = quantity - kitchen.getItemStock();
					selectedKitchenIds.add(kitchen.getKitchenId());
				}
					
			}
			
		}else{
			
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Selected kitchen with order qty : "+selectedKitchens);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		return selectedKitchens;
	}
}
