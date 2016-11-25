package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;
import pojo.TimeSlot;

import com.mkyong.rest.OrderItems;

public class SingleOrder {

	/**
	 * This method returns a json array for biker,slot list and item list for 
	 * a particular kitchen
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public static JSONArray getKitchenDetails(Integer kitchenId, ArrayList<OrderItems> orderItems,
			MealTypePojo mealTypePojo) throws JSONException, Exception{
		
		JSONArray returningBikerJsonArray = new JSONArray();
		
		ArrayList<String> singleTypeBikerList = new ArrayList<String>();//Multiple biker list creation
		singleTypeBikerList = BikerDAO.findBikerOfKitchen( kitchenId, true);//true means only single order type biker
		int totalNoOfItems = 0;
		
		for(OrderItems item : orderItems){
			totalNoOfItems += item.dividedOrderQuantity;
		}
		System.out.println("Total no of items in kitchen "+kitchenId+" is ::"+totalNoOfItems);
		/**
		 * Iterate through bikerList for a particular kitchen
		 */
		for(String bikerUserId : singleTypeBikerList){
			JSONObject bikerJsonObject = new JSONObject();
			bikerJsonObject.put("bikerUserId", bikerUserId);
			bikerJsonObject.put("itemDetails", MultipleOrder.getItemDetailsJsonArray(orderItems));
			bikerJsonObject.put("slotlist", SingleOrder.getBikerSlotJsonArray(bikerUserId, mealTypePojo, totalNoOfItems));	
			
			returningBikerJsonArray.put(bikerJsonObject);
		}
		return returningBikerJsonArray;
	}
	
	/**
	 * Create json array of slots
	 * @param bikerUserId
	 * @param mealTypePojo
	 * @param totalNoOfItems
	 * @return
	 * @throws JSONException
	 * @throws ParseException 
	 */
	public static JSONArray getBikerSlotJsonArray(String bikerUserId, MealTypePojo mealTypePojo, int totalNoOfItems) 
			throws JSONException, ParseException{
		JSONArray slotJSONArray = new JSONArray();
		
		String[] slotTimings = new String[2];
    	boolean isOrderBetweenSpecialTime = false;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String currentTime = sdf.format(date);
		slotTimings = SlotDAO.getSlotTimings();
		String initialTimings = slotTimings[0];
		String finalTimings = slotTimings[1];
		
		if(OrderTimeDAO.isTimeBetweenTwoTime(initialTimings, finalTimings, currentTime)  ){//showing lunch slot 2-3 for order time 11-12 
			System.out.println("Menu time: "+currentTime);
			isOrderBetweenSpecialTime = true;
		}
		ArrayList<TimeSlot> timeSlotList = null;
		if(isOrderBetweenSpecialTime){
			timeSlotList = SlotDAO.getSlotAfter11(bikerUserId, mealTypePojo);
			Collections.sort(timeSlotList);
		}else{
			timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, mealTypePojo);
		}
		
		for(TimeSlot tSlot : timeSlotList){//Returning Timeslot list for loop starts here
			JSONObject slotJson = new JSONObject();
			slotJson.put("slotId", tSlot.slotId);
			slotJson.put("timeSlot", tSlot.timeSlot);
			slotJson.put("quantity", tSlot.quantity);
			slotJson.put("noOfOrders", tSlot.noOfOrders);
			slotJSONArray.put(slotJson);
		}//Returning Timeslot list for loop ends here
		
		return slotJSONArray;
	}
}
