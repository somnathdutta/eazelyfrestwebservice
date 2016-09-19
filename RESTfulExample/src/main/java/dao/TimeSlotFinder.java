package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;
import pojo.TimeSlot;
import utility.ValueComparator;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class TimeSlotFinder {

	public static JSONObject getFreeSlots(String contactNumber, String deliveryAddress, ArrayList<OrderItems>
	orderItemList, String mealtype,String deliveryDay, String pincode,  MealTypePojo mealTypePojo) throws JSONException{
		
		JSONObject timsSlotObject = new JSONObject();
		JSONArray kitchens = findServableKitchens(orderItemList, pincode, mealTypePojo,
				contactNumber, deliveryAddress, mealtype, deliveryDay );
		System.out.println("KITCHEN JSON ARRAY LENGTH:: "+kitchens.length());
		if(kitchens.length() >0){
			timsSlotObject.put("status", "200");
			timsSlotObject.put("message", "Slot found successfully!");
			if(kitchens.length()>1){
				timsSlotObject.put("splitOrder", true);		
			}else{
				timsSlotObject.put("splitOrder", false);	
			}
			timsSlotObject.put("slotDetails", kitchens);
			
		}else{
			timsSlotObject.put("status", "204");
			timsSlotObject.put("message", "No slot!");
			timsSlotObject.put("splitOrder", false);
			timsSlotObject.put("slotDetails", new JSONArray());
		}
		return timsSlotObject;
	}
	
	/**
	 * Function to find kitchen and deliveryList
	 * @throws JSONException 
	 */
	public static JSONArray findServableKitchens(ArrayList<OrderItems> orderItemList, String pincode, 
			MealTypePojo mealTypePojo, String contactNumber, String deliveryAddress,
			String mealType, String deliveryDay) throws JSONException{
		JSONArray servableKitchens = new JSONArray();
		boolean  onlyBengCuisine = false, onlyNiCuisine = false, bengNiCuisine = false;
		int totalNoOfQuantity = 0;
		for(OrderItems items : orderItemList){
			totalNoOfQuantity += items.quantity;
		}
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		ArrayList<OrderItems> niCuisineIdList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> bengCuisineIdList = new ArrayList<OrderItems>();
		//System.out.println("Total item ordered: "+orderItemList.size());
		
		/*for(int i=0 ; i < orderItemList.size() ; i++){
			System.out.print("CUID::"+orderItemList.get(i).cuisineId+"\t");
			System.out.print("CATID::"+orderItemList.get(i).categoryId+"\t");
			System.out.print("ITEM::"+orderItemList.get(i).itemCode+"\t");
    		System.out.print("QTY::"+orderItemList.get(i).quantity+"\n");
		}	*/	
		for(int i=0;i<orderItemList.size();i++){
			if(orderItemList.get(i).cuisineId==2){
				niCuisineIdList.add(new OrderItems(orderItemList.get(i).cuisineId, orderItemList.get(i).categoryId, 
						orderItemList.get(i).itemCode,orderItemList.get(i).quantity, orderItemList.get(i).price) );
			}
			if(orderItemList.get(i).cuisineId==1){
				bengCuisineIdList.add(new OrderItems(orderItemList.get(i).cuisineId, orderItemList.get(i).categoryId, 
						orderItemList.get(i).itemCode,orderItemList.get(i).quantity, orderItemList.get(i).price) );
			}
		}
		System.out.println("Total item order size: "+orderItemList.size());
		System.out.println("Total no of quantity: "+totalNoOfQuantity);
		System.out.println("BEN cuisine order size: "+bengCuisineIdList.size());
		System.out.println("NI cuisine order size: "+niCuisineIdList.size());
		
		if(niCuisineIdList.size()>0 && niCuisineIdList.size()<orderItemList.size()){
			bengNiCuisine = true;
		}
		if( bengCuisineIdList.size() == orderItemList.size()){
			onlyBengCuisine = true;
		}
    	if(niCuisineIdList.size() == orderItemList.size()){
    		onlyNiCuisine = true;
    	}
    	
    	if(onlyBengCuisine){
    		System.out.println("** Order contains only bengali cuisine **");
    		dealingKitchenIds = SameUserPlaceOrder.getLastKitchenId(orderItemList, contactNumber, deliveryAddress, mealTypePojo, pincode);
    		if(dealingKitchenIds.size()==0){
    			dealingKitchenIds = RoundRobinKitchenFinder.getUniqueKitchen(orderItemList, pincode, mealType, deliveryDay);
    			//dealingKitchenIds = FindKitchensByRoundRobin.getKitchenId(orderItemList, pincode, mealType, deliveryDay);
    		}
    	}
    	if(onlyNiCuisine){
    		System.out.println("** Order contains only ni cuisine **");
    		dealingKitchenIds = SameUserPlaceOrder.getLastKitchenId(orderItemList, contactNumber, deliveryAddress,mealTypePojo, pincode);
    		if(dealingKitchenIds.size()==0){
    			dealingKitchenIds = RoundRobinKitchenFinder.getUniqueKitchen(orderItemList, pincode, mealType, deliveryDay);
    			//dealingKitchenIds = FindKitchensByRoundRobin.getKitchenId(orderItemList, pincode, mealType, deliveryDay);
    		}
    	}
    	if(bengNiCuisine){
    		System.out.println("** Order contains  bengali and ni cuisine **");
    		dealingKitchenIds = SameUserPlaceOrder.getLastKitchenId(orderItemList, contactNumber, deliveryAddress,mealTypePojo, pincode);
    		if(dealingKitchenIds.size()==0){
    			dealingKitchenIds = RoundRobinKitchenFinder.getUniqueKitchen(orderItemList, pincode, mealType, deliveryDay);
    			//dealingKitchenIds = FindKitchensByRoundRobin.getKitchenId(orderItemList, pincode, mealType, deliveryDay);
    		}	
    	}
		//kitchens = SameUserPlaceOrder.getLastKitchenId(orderItemList, contactNumber, deliveryAddress);
		/*if(kitchens.size()==0){*/
		//	dealingKitchenIds = getKitchenId(orderItemList, pincode);
		//}
		
		System.out.println("Final kitchenlist : "+dealingKitchenIds);
		System.out.println("Final kitchenlist size : "+dealingKitchenIds.size());
		boolean isOrdeSplit = isDifferentCuisineKitchen(dealingKitchenIds);
		ArrayList<OrderItems> kitchenItemsOrderList = new ArrayList<OrderItems>();
		for(OrderItems orderItems : orderItemList){
			for(Integer kitchen : dealingKitchenIds){
				if(RoundRobinKitchenFinder.isKitchenServingItem(orderItems.itemCode, kitchen)){
					orderItems.kitchenId = kitchen;
					kitchenItemsOrderList.add(orderItems);
				}
			}
		}
		
		Collections.sort(orderItemList);
		/*for(int i=0 ; i < orderItemList.size() ; i++){
			System.out.print("CUID::"+orderItemList.get(i).cuisineId);
			System.out.print(" CUI ::"+orderItemList.get(i).cuisinName);
			System.out.print(" CATID::"+orderItemList.get(i).categoryId);
			System.out.print(" CAT::"+orderItemList.get(i).categoryName);
			System.out.print(" ITEM::"+orderItemList.get(i).itemCode);
			System.out.print(" ITEM NAME::"+orderItemList.get(i).itemName);
    		System.out.print(" QTY::"+orderItemList.get(i).quantity);
    		System.out.println(" KITCHEN::"+orderItemList.get(i).kitchenId+"\n");
			
		}	*/
		
		if(isOrdeSplit){
			
			for(Integer kicthenId : dealingKitchenIds){
				JSONObject kitchenJson = new JSONObject();
				JSONArray bikersArray = new JSONArray();
				JSONArray itemsArrray = new JSONArray();
				kitchenJson.put("kitchenId", kicthenId );
				for(OrderItems orders : kitchenItemsOrderList){
					if(orders.kitchenId==kicthenId){
						JSONObject items = new JSONObject();
						items.put("cuisine", orders.getCuisinName());
						items.put("itemName", orders.getItemName());
						items.put("itemCode", orders.getItemCode());
						items.put("quanity", orders.getQuantity());
						itemsArrray.put(items);
					}
				}
				kitchenJson.put("itemDetails", itemsArrray );
				ArrayList<String> bikerList = new ArrayList<String>();
				if(totalNoOfQuantity>1){//If quantity >1
					bikerList = findBikerOfKitchen(kicthenId);
				}else{
					bikerList.add("dummy");
				}
				System.out.println("Final bikerlist : "+bikerList);
				Map<Integer, String> slotBoyMap = new HashMap<Integer, String>();
				for(String bikerUserId : bikerList){
					JSONObject bikerJson = new JSONObject();
					bikerJson.put("bikerUserId", bikerUserId);
					JSONArray slotJSONArray = new JSONArray();
					if(bikerUserId.equalsIgnoreCase("dummy")){
						 slotJSONArray = findAllSlots(mealTypePojo);
					}else{
						ArrayList<TimeSlot> timeSlotList = findCommonTimeSlots(bikerUserId, kicthenId, mealTypePojo, totalNoOfQuantity);
						for(TimeSlot slot : timeSlotList){
							//if(!slotBoyMap.containsKey(slot.slotId) ){
								slotBoyMap.put(slot.slotId, bikerUserId);
								JSONObject slotJson = new JSONObject();
								slotJson.put("slotId", slot.slotId);
								slotJson.put("timeSlot", slot.timeSlot);
								slotJSONArray.put(slotJson);
							//}
						}
							//slotJSONArray = findTimeSlot(bikerUserId, kicthenId, mealTypePojo);
					}
					/*if(slotJSONArray.length()!=0){
						bikerJson.put("bikerUserId", bikerUserId);
						bikerJson.put("slotlist", slotJSONArray);
					}*/
					bikerJson.put("slotlist", slotJSONArray);
					bikersArray.put(bikerJson);
				}
				kitchenJson.put("bikerList", bikersArray);
				servableKitchens.put(kitchenJson);
			}
		}else{
			for(Integer kicthenId : dealingKitchenIds){
				JSONObject kitchenJson = new JSONObject();
				JSONArray bikersArray = new JSONArray();
				JSONArray itemsArrray = new JSONArray();
				kitchenJson.put("kitchenId", kicthenId );
				for(OrderItems orders : kitchenItemsOrderList){
					if(orders.kitchenId==kicthenId){
						JSONObject items = new JSONObject();
						items.put("cuisine", orders.getCuisinName());
						items.put("itemName", orders.getItemName());
						items.put("itemCode", orders.getItemCode());
						items.put("quanity", orders.getQuantity());
						itemsArrray.put(items);
					}
				}
				kitchenJson.put("itemDetails", itemsArrray );
				ArrayList<String> bikerList = new ArrayList<String>();
				if(totalNoOfQuantity>1){//If quantity >1
					bikerList = findBikerOfKitchen(kicthenId);
				}else{
					bikerList.add("dummy");
				}
				System.out.println("Final bikerlist : "+bikerList);
			
				for(String bikerUserId : bikerList){
					JSONObject bikerJson = new JSONObject();
					bikerJson.put("bikerUserId", bikerUserId);
					JSONArray slotJSONArray = new JSONArray();
					if(bikerUserId.equalsIgnoreCase("dummy")){
						 slotJSONArray = findAllSlots(mealTypePojo);
					}else{
						
						slotJSONArray = findTimeSlot(bikerUserId, kicthenId, mealTypePojo, totalNoOfQuantity);
					}
					bikerJson.put("slotlist", slotJSONArray);
					bikersArray.put(bikerJson);
				}
				kitchenJson.put("bikerList", bikersArray);
				servableKitchens.put(kitchenJson);
			}
		}
		
		
		return servableKitchens;
	}
	
	/**
	 * Find eligible kitchens
	 */
	public static ArrayList<Integer> getKitchenId(ArrayList<OrderItems> orderList , String pincode ){
		System.out.println("*** SLOT FINDER CALLING ****");
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> niKitchenidList = new ArrayList<Integer>();
		
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
		
		boolean kitchenFoundFromPincode = false;
		
		ArrayList<OrderItems> niCuisineIdList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> bengCuisineIdList = new ArrayList<OrderItems>();
		
		System.out.println("Total item ordered: "+orderList.size());
		for(int i=0;i<orderList.size();i++){
			System.out.println("cuisineid:: "+orderList.get(i).cuisineId);
			if(orderList.get(i).cuisineId==1){
				bengCuisineIdList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, orderList.get(i).itemCode,orderList.get(i).quantity,orderList.get(i).price) );
			}
			if(orderList.get(i).cuisineId==2){
				niCuisineIdList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, orderList.get(i).itemCode,orderList.get(i).quantity,orderList.get(i).price) );
			}
		}
		System.out.println("NI cuisine order size: "+niCuisineIdList.size());
		if(niCuisineIdList.size()>0 && niCuisineIdList.size()<orderList.size()){
			System.out.println("--ORDER is going to split---");
			niKitchenidList = findKitchenIdsOfCuisine(niCuisineIdList, pincode);
		}
	
	
		if(orderList.size() == niCuisineIdList.size()){
				System.out.println("- - - -  Only NI cuisines found on ordered items- - - ");
				dealingKitchenIds = findKitchenIdsOfCuisine(niCuisineIdList, pincode);
				return dealingKitchenIds;
		}else{
				ArrayList<String> iemcode = new ArrayList<String>();
				for(OrderItems order : orderList){
					if(order.cuisineId==1)
					iemcode.add("'"+order.itemCode+"'");
				}
				String a = iemcode.toString();
				String fb = a.replace("[", "");
				String itemcodes = fb.replace("]", "");
				try {
					SQL:{
							Connection connection = DBConnection.createConnection();
							
							PreparedStatement preparedStatement = null;
							ResultSet resultSet = null;
							String sql = "select  fki.kitchen_id,fki.stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.stock >0";
							try {
								preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, "%"+pincode+"%");
								resultSet = preparedStatement.executeQuery();
								while (resultSet.next()) {
								kitchenFoundFromPincode = true;
								 int kid = resultSet.getInt("kitchen_id");
								 int stock = resultSet.getInt("stock");
								 kitchenIds.add(kid);
								 kitchenStockMap.put(kid, stock);
								}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}finally{
								if(connection!=null){
									connection.close();
								}
								
							}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				//System.out.println(kitchenStockMap);
				if(kitchenFoundFromPincode){
					System.out.println("Item serving kitchen ids "+kitchenIds);
					
					ArrayList<Integer> singleCombinationIds = new ArrayList<Integer>();
					for(Integer dup : kitchenIds){
						if(Collections.frequency(kitchenIds, dup)==1){
							singleCombinationIds.add(dup);
						}
					}
					Set<Integer> dupliactes = findDuplicates(kitchenIds);
					for(Integer singleComb : singleCombinationIds){
						dupliactes.add(singleComb);
					}
					
					for(Integer dup : dupliactes){
						kitchenMaxFreq.put(dup, Collections.frequency(kitchenIds, dup));
					}
					
					System.out.println("Combination Map : "+kitchenMaxFreq);
					int max = Collections.max(kitchenMaxFreq.values());
					System.out.println("Maximum combination :: "+max);
					ArrayList<Integer> maxItemCombinationKitchenId = new ArrayList<Integer>();
					for(Entry<Integer, Integer> mp: kitchenMaxFreq.entrySet() ){
						if(mp.getValue()==max){
							//System.out.println("Kitchen id: "+mp.getKey());
							maxItemCombinationKitchenId.add(mp.getKey());
						}
					}
					/**
					 * Kitchens with max combinations
					 */
					System.out.println("Order to be placed between kitchen ids :"+maxItemCombinationKitchenId);
					
					
					Map<Integer,Integer> maxCapaMap = new HashMap<Integer, Integer>();
					for(Entry<Integer, Integer> mp: kitchenStockMap.entrySet() ){
						for(Integer ids : maxItemCombinationKitchenId){
							if(mp.getKey().equals(ids)){
								//System.out.println("kitchen stock : "+mp);
								maxCapaMap.put(mp.getKey(), mp.getValue());
							}
						}	
					}
					System.out.println("kitchen stock Map(maxCapaMap): "+maxCapaMap);
					
					ValueComparator bvc = new ValueComparator(maxCapaMap);
			        TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
			        sorted_map.putAll(maxCapaMap);
					System.out.println("::::::::Sorted map(245) ::::::"+sorted_map);
					
					ArrayList<Integer> sortedKitchenIds = new ArrayList<Integer>();
					for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
						sortedKitchenIds.add(mp.getKey());
					}
					System.out.println(":::Sorted kitchen id:: "+sortedKitchenIds+"\n");
					
					int tot=0;
					for(Integer kitchenId : sortedKitchenIds){
						if( RoundRobin.alreadyOrdered(kitchenId,1)){
							tot++;
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
							dealingKitchenIds.add(kitchenId);
							break;
						}
					}
					if(tot == sortedKitchenIds.size()){
						System.out.println("All are ordered!");
						RoundRobin.makeAllFree(sortedKitchenIds,1);
						for(Integer kitchenId : sortedKitchenIds){
							if( RoundRobin.alreadyOrdered(kitchenId,1)){
								tot++;
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
								dealingKitchenIds.add(kitchenId);
								break;
							}
						}
					}
					System.out.println("Dealing kitchen id list  : "+dealingKitchenIds);
					if(niKitchenidList.size()>0)
						dealingKitchenIds.addAll(niKitchenidList);
					
					return dealingKitchenIds;
				}else{
					System.out.println("* * * NO ANY BENGALI KITCHEN IDS * * * *"+dealingKitchenIds);
					if(niKitchenidList.size()>0)
						dealingKitchenIds.addAll(niKitchenidList);
					return dealingKitchenIds;
				}
		}
		
	}
	
	public static ArrayList<Integer> findKitchenIdsOfCuisine(ArrayList<OrderItems> orderList,String pincode){
		ArrayList<Integer> dealingIds = new ArrayList<Integer>();
		boolean kitchenFoundFromPincode= false;
		
			ArrayList<String> iemcode = new ArrayList<String>();
			Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
			for(OrderItems order : orderList){
				iemcode.add("'"+order.itemCode+"'");
			}
			String a = iemcode.toString();
			String fb = a.replace("[", "");
			String itemcodes = fb.replace("]", "");
			
			ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
			
			Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "select  fki.kitchen_id,fki.stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk on "
								+" fki.kitchen_id = fk.kitchen_id "
								+" where fki.item_code IN ("+itemcodes+") "
								+" and fk.serving_zipcodes LIKE ? and fki.stock >0";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, "%"+pincode+"%");
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
							 kitchenFoundFromPincode = true;
							 int kid = resultSet.getInt("kitchen_id");
							 int stock = resultSet.getInt("stock");
							 kitchenIds.add(kid);
							 kitchenStockMap.put(kid, stock);
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			if(kitchenFoundFromPincode){
				 
				ArrayList<Integer> singleCombinationIds = new ArrayList<Integer>();
				for(Integer dup : kitchenIds){
				if(Collections.frequency(kitchenIds, dup)==1){
						singleCombinationIds.add(dup);
					}
				}
				Set<Integer> dupliactes = findDuplicates(kitchenIds);
				for(Integer singleComb : singleCombinationIds){
					dupliactes.add(singleComb);
				}
				System.out.println("Dup :: "+dupliactes);
				for(Integer dup : dupliactes){
					System.out.println("Kitchen id "+dup+" have : "+Collections.frequency(kitchenIds, dup)+" ordered items.");
					kitchenMaxFreq.put(dup, Collections.frequency(kitchenIds, dup));
				}
				System.out.println("NI kitchen item combination map :"+ kitchenMaxFreq);
				
				int max = Collections.max(kitchenMaxFreq.values());
				System.out.println("Maximum combination :: "+max);
				ArrayList<Integer> maxItemCombinationKitchenId = new ArrayList<Integer>();
				for(Entry<Integer, Integer> mp: kitchenMaxFreq.entrySet() ){
					if(mp.getValue()==max){
						//System.out.println("Kitchen id: "+mp.getKey());
						maxItemCombinationKitchenId.add(mp.getKey());
					}
				}
				System.out.println("--NI Order to be placed between kitchen ids :"+maxItemCombinationKitchenId);
				
				
				Map<Integer,Integer> maxCapaMap = new HashMap<Integer, Integer>();
				for(Entry<Integer, Integer> mp: kitchenStockMap.entrySet() ){
					for(Integer ids : maxItemCombinationKitchenId){
						if(mp.getKey().equals(ids)){
							//System.out.println("kitchen stock : "+mp);
							maxCapaMap.put(mp.getKey(), mp.getValue());
						}
					}	
				}
				System.out.println("kitchen stock for NI kitchen: "+maxCapaMap);
				
				ValueComparator bvc = new ValueComparator(maxCapaMap);
				TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
		        sorted_map.putAll(maxCapaMap);
				System.out.println("::::::::Sorted map(245) ::::::"+sorted_map);
				
				ArrayList<Integer> sortedKitchenIds = new ArrayList<Integer>();
				for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
					sortedKitchenIds.add(mp.getKey());
				}
				System.out.println(":::Sorted kitchen id:: "+sortedKitchenIds+"\n");
				
				int tot=0;
				for(Integer kitchenId : sortedKitchenIds){
					if( RoundRobin.alreadyOrdered(kitchenId,2)){
						tot++;
					}else{
						RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
						dealingIds.add(kitchenId);
						break;
					}
				}
				if(tot == sortedKitchenIds.size()){
					System.out.println("All are ordered!");
					RoundRobin.makeAllFree(sortedKitchenIds,2);
					for(Integer kitchenId : sortedKitchenIds){
						if( RoundRobin.alreadyOrdered(kitchenId,2)){
							tot++;
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
							dealingIds.add(kitchenId);
							break;
						}
					}
				}
				
				return dealingIds;
			}else{
				System.out.println("* * * NO ANY NI KITCHEN IDS * * * *"+dealingIds);
				return dealingIds;
			}
		
	}
	
	public static Set<Integer> findDuplicates(ArrayList<Integer> listContainingDuplicates) {		 
		final Set<Integer> setToReturn = new HashSet<Integer>();
		final Set<Integer> set1 = new HashSet<Integer>();
 
		for (Integer yourInt : listContainingDuplicates) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		return setToReturn;
	}
	
	public static ArrayList<String> findBikerOfKitchen(int kicthenId){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
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
							+ " from fapp_timeslot where time_slot_id <4 ";
		 		}else if(mealTypePojo.isDinnerToday()){
		 			sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where time_slot_id >3 ";
		 		}else if(mealTypePojo.isLunchTomorrow()){
		 			sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where time_slot_id <4 ";
		 		}else{
		 			sql = "select time_slot_id ,time_slot "
							+ " from fapp_timeslot where time_slot_id >3 ";
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
	
	
	public static JSONArray findTimeSlot(String boyUserId, int kitchenId, MealTypePojo mealTypePojo,
			int totalNoOfQuantity){
		JSONArray timeSlotArray = new JSONArray();
		System.out.println("TIME SLOT FOR BOY USER ID:: "+boyUserId);
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
			 			sql = "select time_slot_id from fapp_timeslot_driver_status"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id <4 "
				 				+ " and assigned_date IS NULL";
				 				
			 		}else if(mealTypePojo.isDinnerToday()){
			 			sql = "select time_slot_id from fapp_timeslot_driver_status"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id >3 "
				 				+ " and assigned_date IS NULL";
			 		}else if(mealTypePojo.isLunchTomorrow()){
			 			sql = "select time_slot_id from fapp_timeslot_driver_status_tommorrow"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id <4 "
				 				+ " and assigned_date IS NULL";
			 		}else{
			 			sql = "select time_slot_id from fapp_timeslot_driver_status_tommorrow"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id >3 "
				 				+ " and assigned_date IS NULL";
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
						//System.out.println("-- NO of Rows:: "+noOfRows);	
						/*if(noOfRows == 0){
							firstSlotToday = true;
						}*/
						if(mealTypePojo.isLunchToday() && noOfRows==3){
							firstSlotToday = true;
						}else if(mealTypePojo.isDinnerToday() && noOfRows==2){
							firstSlotToday = true;
						}else if(mealTypePojo.isLunchTomorrow() && noOfRows==3){
							firstSlotToday = true;
						}else if(mealTypePojo.isDinnerTomorrow() && noOfRows==2){
							firstSlotToday = true;
						}else{
							firstSlotToday = false;
						}
						System.out.println("firstSlotToday:: "+firstSlotToday);
					} catch (Exception e) {
						System.out.println(e);
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
									+ " from fapp_timeslot where time_slot_id <4 ";
				 		}else if(mealTypePojo.isDinnerToday()){
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id >3 ";
				 		}else if(mealTypePojo.isLunchTomorrow()){
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id <4 ";
				 		}else{
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id >3 ";
				 		}
							
				 		try {
							preparedStatement = connection.prepareStatement(sql);
							//preparedStatement.setString(1, boyUserId);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								/*TimeSlot timeSlot = new TimeSlot();
								timeSlot.slotId = resultSet.getInt("time_slot_id");
								timeSlot.timeSlot = resultSet.getString("time_slot");*/
								JSONObject timeSlotJson = new JSONObject();
								timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
								timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));
								/*timeSlot.bikerUserId = boyUserId;
								timeSlot.kitchenID = kitchenId;
								*/
								timeSlotArray.put(timeSlotJson);
								//timeSlotList.add(timeSlot);
							}
							//System.out.println("Length of slot array :: "+timeSlotArray.length());
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
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
				 				+ " and ftds.time_slot_id <4 ";
			 		}else if(mealTypePojo.isDinnerToday()){
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";*/
				 				+ " and ftds.time_slot_id >3 ";
			 		}else if(mealTypePojo.isLunchTomorrow()){
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status_tommorrow ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
				 				+ " and ftds.time_slot_id <4 ";
			 		}else{
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status_tommorrow ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";*/
				 				+ " and ftds.time_slot_id >3 ";
			 		}
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						resultSet = preparedStatement.executeQuery();
						///System.out.println("Not first:: "+preparedStatement);
						while (resultSet.next()) {
							
							JSONObject timeSlotJson = new JSONObject();
							timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
							timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));
							timeSlotJson.put("quantity", resultSet.getInt("quantity"));
							if( (timeSlotJson.getInt("quantity")+totalNoOfQuantity) > 8){
								///Skip the slot
							}else{
								timeSlotArray.put(timeSlotJson);
							}
							
							//timeSlotList.add(timeSlot);
						}
					} catch (Exception e) {
						System.out.println(e);
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
		 //System.out.println("Return Length of slot array :: "+timeSlotArray.length());
		 return timeSlotArray;
	}
	
	public static ArrayList<TimeSlot> findCommonTimeSlots(String boyUserId, int kitchenId, 
			MealTypePojo mealTypePojo, int totalNoOfQuantity){
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
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id <4 "
				 				+ " and assigned_date IS NULL";
				 				
			 		}else if(mealTypePojo.isDinnerToday()){
			 			sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id >3 "
				 				+ " and assigned_date IS NULL";
			 		}else if(mealTypePojo.isLunchTomorrow()){
			 			sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status_tommorrow"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id <4 "
				 				+ " and assigned_date IS NULL";
			 		}else{
			 			sql = "select time_slot_id,quantity  from fapp_timeslot_driver_status_tommorrow"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and quantity = 0 and no_of_orders = 0 and time_slot_id >3 "
				 				+ " and assigned_date IS NULL";
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
						if(mealTypePojo.isLunchToday() && noOfRows==3){
							firstSlotToday = true;
						}else if(mealTypePojo.isDinnerToday() && noOfRows==2){
							firstSlotToday = true;
						}else if(mealTypePojo.isLunchTomorrow() && noOfRows==3){
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
									+ " from fapp_timeslot where time_slot_id <4 ";
				 		}else if(mealTypePojo.isDinnerToday()){
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id >3 ";
				 		}else if(mealTypePojo.isLunchTomorrow()){
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id <4 ";
				 		}else{
				 			sql = "select time_slot_id ,time_slot "
									+ " from fapp_timeslot where time_slot_id >3 ";
				 		}
							
				 		try {
							preparedStatement = connection.prepareStatement(sql);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								TimeSlot timeSlot = new TimeSlot();
								timeSlot.slotId = resultSet.getInt("time_slot_id");
								timeSlot.timeSlot = resultSet.getString("time_slot");
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
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity  "
								+ " from fapp_timeslot_driver_status ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
				 				+ " and ftds.time_slot_id <4 ";
			 		}else if(mealTypePojo.isDinnerToday() ){
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";*/
				 				+ " and ftds.time_slot_id >3 ";
			 		}else if(mealTypePojo.isLunchTomorrow() ){
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
								+ " from fapp_timeslot_driver_status_tommorrow ftds "
								+ " join fapp_timeslot ft "
								+ " on ftds.time_slot_id = ft.time_slot_id"
				 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
				 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
				 				/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
				 				+ " and ftds.time_slot_id <4 ";
			 		}else{
			 			sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity "
									+ " from fapp_timeslot_driver_status_tommorrow ftds "
									+ " join fapp_timeslot ft "
									+ " on ftds.time_slot_id = ft.time_slot_id"
					 				+ " where driver_user_id = ? and is_slot_locked ='N'  "
					 				+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
					 				/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";*/
					 				+ " and ftds.time_slot_id >3 ";
			 				
			 		}
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						resultSet = preparedStatement.executeQuery();
						System.out.println("Not first:: "+preparedStatement);
						while (resultSet.next()) {
							TimeSlot timeSlot = new TimeSlot();
							timeSlot.slotId = resultSet.getInt("time_slot_id");
							timeSlot.timeSlot = resultSet.getString("time_slot");
							timeSlot.quantity = resultSet.getInt("quantity");
							if( (timeSlot.quantity + totalNoOfQuantity) > 8 ){
								//Skip the slot
							}else{
								timeSlotList.add(timeSlot);
							}
							
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
	
	public static boolean isDifferentCuisineKitchen(ArrayList<Integer> dealingKitchenIds){
		boolean differentCuisineKitchen = false;
		if(dealingKitchenIds.size()>1){
			differentCuisineKitchen = true;
		}
		return differentCuisineKitchen ;
	}
}
