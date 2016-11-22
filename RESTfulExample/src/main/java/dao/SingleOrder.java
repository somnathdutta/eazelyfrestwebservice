package dao;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;

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
			bikerJsonObject.put("slotlist", MultipleOrder.getBikerSlotJsonArray(bikerUserId, mealTypePojo, totalNoOfItems));	
			
			returningBikerJsonArray.put(bikerJsonObject);
		}
		return returningBikerJsonArray;
	}
}
