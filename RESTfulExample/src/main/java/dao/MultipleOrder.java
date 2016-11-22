package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Kitchen;
import pojo.MealTypePojo;
import pojo.TimeSlot;

import com.mkyong.rest.OrderItems;

public class MultipleOrder {

	/**
	 * This method returns a map containing kitchen id and respective item list
	 * @param orderItemList
	 * @param kitchenList
	 * @return a Map with kitchen and item list
	 */
	public static Map<Integer, ArrayList<OrderItems>> getKitchenWithItemListMap(ArrayList<OrderItems> orderItemList,
			ArrayList<Integer> kitchenIdList){
		Map<Integer, ArrayList<OrderItems>> kitchenWithItemMap = new HashMap<Integer, ArrayList<OrderItems>>();
		if(kitchenWithItemMap.size() > 0){ //If map size greater than zero please make it clear
			kitchenWithItemMap.clear();//Clear the map
		}
		/**
		 * Create a blank item list so that it will go into the map with kitchen id
		 */
		ArrayList<OrderItems> kitchenItemList = null;
		
		/**
		 * Iterate through the Integer list of kitchen ids to retrieve the key value for our map
		 */
		for(Integer kitchenId : kitchenIdList){ //Parent Kitchen id For loop starts here
			/**
			 * Create a item list for kitchen
			 */
			kitchenItemList = new ArrayList<OrderItems>();
			/**
			 * Now iterate through the order item list to match the item kitchen with the parent kitchen id
			 */
			for(OrderItems items : orderItemList){//Item for loop starts here
				
				/**
				 * Now iterate through item's kitchen list
				 */
				for(Kitchen itemKitchen : items.getKitchenList()){//Item kitchen list for loop starts here
					/**
					 * Store the parent kitchen id into a variable so that it will became our key in the returning map
					 */
					Integer mapKitchenId = kitchenId;
					/**
					 * If we found the parent kitchen id equals item's kitchen id then put that kitchen id and respective item details into our returning map
					 */
					if(kitchenId == itemKitchen.getKitchenId()){// if parent kitchen id matches item's kitchen id block starts here
						/**
						 * If we found kitchen in the map then populate the map
						 */
						if( kitchenWithItemMap.containsKey(mapKitchenId)){
							/**
							 * Crete a new object for item
							 */
							OrderItems orderItems = new OrderItems();
							orderItems.setCuisineId(items.getCuisineId());
							orderItems.setCuisinName(items.getCuisinName());
							orderItems.setItemName(items.getItemName());
							orderItems.setItemCode(items.getItemCode());
							orderItems.setDividedOrderQuantity(itemKitchen.getUserItemQuantity());
							orderItems.setStockQuantity(itemKitchen.getTotalItemStock());
							kitchenItemList.add(orderItems);
							/**
							 * put kitchen id and item list in map
							 */
							kitchenWithItemMap.put(kitchenId, kitchenItemList);
						}else{
							/**
							 * Crete a new object for item
							 */
							OrderItems orderItems = new OrderItems();
							orderItems.setCuisineId(items.getCuisineId());
							orderItems.setCuisinName(items.getCuisinName());
							orderItems.setItemName(items.getItemName());
							orderItems.setItemCode(items.getItemCode());
							orderItems.setDividedOrderQuantity(itemKitchen.getUserItemQuantity());
							orderItems.setStockQuantity(itemKitchen.getTotalItemStock());
							kitchenItemList.add(orderItems);
							/**
							 * put kitchen id and item list in map
							 */
							kitchenWithItemMap.put(kitchenId, kitchenItemList);
						}
					}// if parent kitchen id matches item's kitchen id block ends here
					
				}//Item kitchen list for loop ends here
				
			}//Item for loop ends here
			
		}//Kitchen id For loop ends here
		
		return kitchenWithItemMap;	
	}


	/**
	 * This method returns a json array for biker,slot list and item list for 
	 * a particular kitchen
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public static JSONArray getKitchenDetails(Integer kitchenId, ArrayList<OrderItems> orderItems,
			MealTypePojo mealTypePojo) throws JSONException, Exception{
		
		JSONArray returningBikerJsonArray = new JSONArray();
		
		ArrayList<String> multipleBikerList = new ArrayList<String>();//Multiple biker list creation
		multipleBikerList = BikerDAO.findBikerOfKitchen( kitchenId, false);//false means only multiple order type biker
		int totalNoOfItems = 0;
		
		for(OrderItems item : orderItems){
			totalNoOfItems += item.dividedOrderQuantity;
		}
		System.out.println("Total no of items in kitchen "+kitchenId+" is ::"+totalNoOfItems);
		/**
		 * Iterate through bikerList for a particular kitchen
		 */
		for(String bikerUserId : multipleBikerList){
			JSONObject bikerJsonObject = new JSONObject();
			bikerJsonObject.put("bikerUserId", bikerUserId);
			bikerJsonObject.put("itemDetails", getItemDetailsJsonArray(orderItems));
			bikerJsonObject.put("slotlist", getBikerSlotJsonArray(bikerUserId, mealTypePojo, totalNoOfItems));	
			
			returningBikerJsonArray.put(bikerJsonObject);
		}
		return returningBikerJsonArray;
	}
	
	/**
	 * Create json array for itemList
	 */
	public static JSONArray getItemDetailsJsonArray(ArrayList<OrderItems> orderItems)throws Exception{
		JSONArray itemJsonArray = new JSONArray();
		for(OrderItems items : orderItems){
			JSONObject itemJson = new JSONObject();
			itemJson.put("cuisineid", items.getCuisineId());
			itemJson.put("cuisine", items.getCuisinName());
			itemJson.put("itemName", items.getItemName());
			itemJson.put("itemCode", items.getItemCode());
			itemJson.put("stock", items.getStockQuantity());
			itemJson.put("quanity", items.getDividedOrderQuantity());
			itemJsonArray.put(itemJson);
		}
		return itemJsonArray;
	}
	
	/**
	 * Create json array of slots
	 * @param bikerUserId
	 * @param mealTypePojo
	 * @param totalNoOfItems
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray getBikerSlotJsonArray(String bikerUserId, MealTypePojo mealTypePojo, int totalNoOfItems) throws JSONException{
		JSONArray slotJSONArray = new JSONArray();
		ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, mealTypePojo);
		for(TimeSlot tSlot : timeSlotList){//Timeslot list for loop starts here
			JSONObject slotJson = new JSONObject();
			slotJson.put("slotId", tSlot.slotId);
			slotJson.put("timeSlot", tSlot.timeSlot);
			slotJson.put("quantity", tSlot.quantity);
			slotJson.put("noOfOrders", tSlot.noOfOrders);
			slotJSONArray.put(slotJson);
		}//Timeslot list for loop ends here
		return slotJSONArray;
	}
}
