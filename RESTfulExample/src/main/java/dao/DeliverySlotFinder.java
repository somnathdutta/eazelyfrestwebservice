package dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;
import pojo.TimeSlot;

import com.mkyong.rest.OrderItems;

public class DeliverySlotFinder {

	public static JSONArray getSlotDetails(ArrayList<Integer> dealingKitchenIds, ArrayList<OrderItems> orderItems,
			MealTypePojo mealTypePojo)
	throws Exception{
		JSONArray slotDetailsJsonArray = new JSONArray();
		String[] slotTimings = new String[2];
		slotTimings = SlotDAO.getSlotTimings();
		String initialTimings = slotTimings[0];
		String finalTimings = slotTimings[1];
		/**********************
		 | Single item order  |
		 *********************/
		if(isSingleOrder(orderItems)){//When only one item is order then show only single biker slots
			//Find single type biker slot
			for(Integer kitchenId  : dealingKitchenIds){//kitchen for loop starts here
				
				JSONObject kitchenJson = new JSONObject();
				JSONArray bikersArray = new JSONArray();
				
				kitchenJson.put("kitchenId", kitchenId);
				ArrayList<String> singleBikerList = new ArrayList<String>();
				
				singleBikerList = BikerDAO.findBikerOfKitchen(kitchenId,true);//true means only single order type biker
				
				for(String bikerUserId : singleBikerList){//Biker for loop starts here 
					JSONObject bikerJson = new JSONObject();//Create new Biker json
					JSONArray itemsArrray = new JSONArray();//Create new Items json array
					bikerJson.put("bikerUserId", bikerUserId);//put biker id in biker json
					for(OrderItems orders : orderItems){//Order items for loop starts here
						JSONObject items = new JSONObject();// Create new item json 
						items.put("cuisineid", orders.cuisineId);//Add items to the item json array
						items.put("cuisine", orders.getCuisinName());
						items.put("itemName", orders.getItemName());
						items.put("itemCode", orders.getItemCode());
						items.put("stock", ItemDAO.itemCurrentStock(kitchenId, orders.itemCode, mealTypePojo));//find current stock of item with kitchen
						items.put("quanity", orders.getQuantity());
						itemsArrray.put(items);
					}//Order items for loop ends here
					
					bikerJson.put("itemDetails", itemsArrray );
					
					JSONArray slotJSONArray = new JSONArray();//Create new slot json array
					
					Date date = new Date();
					/**
					 * FInd current time for slot differentiations
					 */
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					String currentTime = sdf.format(date);
					if(OrderTimeDAO.isTimeBetweenTwoTime(initialTimings, finalTimings, currentTime) //showing lunch slot 2-3 for order time 11-12 
							&& ( mealTypePojo.isLunchToday() || mealTypePojo.isLunchTomorrow()) ){
						/*******************************************************************************
						 | ------- Show only 2-3 slot as order time between 11-12 Single order -------- |
						 *******************************************************************************/
						System.out.println("Order time: "+currentTime);
						ArrayList<TimeSlot> timeSlotList = SlotDAO.getSlotAfter11(bikerUserId, mealTypePojo);
						for(TimeSlot tSlot : timeSlotList){//Timeslot list for loop starts here
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", tSlot.slotId);
							slotJson.put("timeSlot", tSlot.timeSlot);
							slotJson.put("quantity", tSlot.quantity);
							slotJson.put("noOfOrders", tSlot.noOfOrders);
							slotJSONArray.put(slotJson);
						}//Timeslot list for loop ends here
					}else{
						/*************************************************************************************
						 | ------- Show All lunch slots as order time not between 11-12 Single order -------- |
						 *************************************************************************************/
						ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, mealTypePojo);
						for(TimeSlot tSlot : timeSlotList){//Timeslot list for loop starts here
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", tSlot.slotId);
							slotJson.put("timeSlot", tSlot.timeSlot);
							slotJson.put("quantity", tSlot.quantity);
							slotJson.put("noOfOrders", tSlot.noOfOrders);
							slotJSONArray.put(slotJson);
						}//Timeslot list for loop ends here
					}
					
					bikerJson.put("slotlist", slotJSONArray);
					bikersArray.put(bikerJson);
				}//Biker list for loop ends here
				
				kitchenJson.put("bikerList", bikersArray);
				slotDetailsJsonArray.put(kitchenJson);
			}//Kitchen id for loop ends here
		
		}else{
			
			
			/***************************************************************
			 | ------- Multi item order ---------------------------------- |
			 **************************************************************/
			//Find multi type biker slot
			for(Integer kitchenId  : dealingKitchenIds){//Multiple kitchen for loop starts here
				
				JSONObject kitchenJson = new JSONObject();//Kitchen new json object created 
				JSONArray bikersArray = new JSONArray();
				
				kitchenJson.put("kitchenId", kitchenId);// put kitchen id in kitchen json
				ArrayList<String> multipleBikerList = new ArrayList<String>();//Multiple biker list creation
				multipleBikerList = BikerDAO.findBikerOfKitchen(kitchenId,false);//false means only multiple order type biker
				
				for(String bikerUserId : multipleBikerList){//Biker userid list for loop starts here for multiple kitchen
					
					JSONObject bikerJson = new JSONObject();//Create new Biker json
					JSONArray itemsArrray = new JSONArray();//Create new Items json array
					bikerJson.put("bikerUserId", bikerUserId);//put biker id in biker json
					
					for(OrderItems orders : orderItems){//Order items for loop starts here
						JSONObject items = new JSONObject();// Create new item json 
						items.put("cuisineid", orders.cuisineId);//Add items to the item json array
						items.put("cuisine", orders.getCuisinName());
						items.put("itemName", orders.getItemName());
						items.put("itemCode", orders.getItemCode());
						items.put("stock", ItemDAO.itemCurrentStock(kitchenId, orders.itemCode, mealTypePojo));//find current stock of item with kitchen
						items.put("quanity", orders.getQuantity());
						itemsArrray.put(items);
					}//Order items for loop ends here
					
					bikerJson.put("itemDetails", itemsArrray );
					
					JSONArray slotJSONArray = new JSONArray();//Create new slot json array
					Date date = new Date();
					/**
					 * FInd current time for slot differentiations
					 */
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					String currentTime = sdf.format(date);
					
					if(OrderTimeDAO.isTimeBetweenTwoTime(initialTimings, finalTimings, currentTime) //showing lunch slot 2-3 for order time 11-12 
							&& ( mealTypePojo.isLunchToday() || mealTypePojo.isLunchTomorrow()) ){
						/*******************************************************************************
						 | ------- Show only 2-3 slot as order time between 11-12 Multiple order -------- |
						 *******************************************************************************/
						System.out.println("Order time: "+currentTime);
						ArrayList<TimeSlot> timeSlotList = SlotDAO.getSlotAfter11(bikerUserId, mealTypePojo);
						for(TimeSlot tSlot : timeSlotList){//Timeslot list for loop starts here for Multiple order
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", tSlot.slotId);
							slotJson.put("timeSlot", tSlot.timeSlot);
							slotJson.put("quantity", tSlot.quantity);
							slotJson.put("noOfOrders", tSlot.noOfOrders);
							slotJSONArray.put(slotJson);
						}//Timeslot list for loop ends here
					}else{
						/*************************************************************************************
						 | ------- Show All lunch slots as order time not between 11-12 Multiple order -------- |
						 *************************************************************************************/
						ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, mealTypePojo);
						for(TimeSlot tSlot : timeSlotList){//Timeslot list for loop starts here for Multiple order
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", tSlot.slotId);
							slotJson.put("timeSlot", tSlot.timeSlot);
							slotJson.put("quantity", tSlot.quantity);
							slotJson.put("noOfOrders", tSlot.noOfOrders);
							slotJSONArray.put(slotJson);
						}//Timeslot list for loop ends here
						
						bikerJson.put("slotlist", slotJSONArray);
						bikersArray.put(bikerJson);
					}
					
					kitchenJson.put("bikerList", bikersArray);
					slotDetailsJsonArray.put(kitchenJson);
				}//Biker userid list for loop ends here for multiple kitchen
				
			}//Multiple kitchen for loop ends here
		}
		
		return slotDetailsJsonArray;
	}
	
	public static boolean isSingleOrder(ArrayList<OrderItems> orderItemList){
		int totalNoOfQuantity = 0;
		for(OrderItems items : orderItemList){
			totalNoOfQuantity += items.quantity;
		}
		if(totalNoOfQuantity==1){
			return true;
		}else{
			return false;
		}
	}
}
