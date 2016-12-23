package dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;

import com.appsquad.finder.AllItemServingKitchenFinder;
import com.appsquad.finder.KitchenFinder;
import com.mkyong.rest.OrderItems;

public class FindDeliverySlots {

	public static JSONObject getDeliverySlots(String contactNumber, String deliveryAddress, ArrayList<OrderItems>
	orderItemList, String mealType,String deliveryDay, String pincode,  MealTypePojo mealTypePojo, String area)
	throws Exception{
		JSONObject timsSlotObject = new JSONObject();
		JSONArray slotDetailsJsonArray = new JSONArray();
		
		boolean isStaggeredOrder = false, isSplitOrder = false,slotFound = false;
		int totalNoOfQuantity = 0;
		ArrayList<Integer> lastKitchenIdList = new ArrayList<Integer>();  
		
		for(OrderItems items : orderItemList){
			totalNoOfQuantity += items.quantity;
		}
		
		/**
		 * Find last kitchen
		 */
		//lastKitchenIdList = SameKitchenFinder.getLastKitchenId(orderItemList, contactNumber, mealTypePojo, area);
		
		//if(lastKitchenIdList.size() == 1){
		//	ArrayList<OrderItems> ordersWithKitchen = SameKitchenFinder.orderWithLastKitchen(orderItemList, lastKitchenIdList.get(0));
		//}
		
		/**
		 * Add items with kitchen servable stock
		 */
		//ArrayList<OrderItems> ordersWithKitchen = KitchenFinder.getKitchenOfOrderedItem(orderItemList, mealType, deliveryDay, area);
		ArrayList<OrderItems> ordersWithKitchen = AllItemServingKitchenFinder.findKitchens(mealType, deliveryDay, area, orderItemList);
		
	//	isStaggeredOrder = isStaggeredOrder(ordersWithKitchen);
		
		/**
		 * Find dealing kitchen id's with respect to ordered items
		 */
		ArrayList<Integer> dealingKitchens = KitchenFinder.getDealingKitchenIds(ordersWithKitchen);
		
		
		if(dealingKitchens.size() > 1){
			isSplitOrder = true;
		}
		System.out.println("DEALING KITCHEN IDS "+dealingKitchens);
		
		/**
		 * Find delivery slots for dealing kitchens, if found
		 */
		if(dealingKitchens.size()>0){
			slotDetailsJsonArray = DeliverySlotFinder.getSlotDetails(dealingKitchens, ordersWithKitchen, mealTypePojo);
		}
		
		if(isSlotFound(slotDetailsJsonArray)){
			slotFound = true;
		}
		
		isStaggeredOrder = isUncommonSlotFound(slotDetailsJsonArray, totalNoOfQuantity);
		
		/*if(isStaggeredOrder){
			isSplitOrder = true;
		}*/
		
		if(dealingKitchens.size()>0){
			timsSlotObject.put("status", "200");
			timsSlotObject.put("staggered", isStaggeredOrder);
			timsSlotObject.put("message", "Slot found successfully!");
			timsSlotObject.put("splitOrder", isSplitOrder);
			timsSlotObject.put("slotDetails", slotDetailsJsonArray);
		}else{
			timsSlotObject.put("status", "204");
			timsSlotObject.put("staggered", false);
			timsSlotObject.put("message", "Oops!..Currently all our bikers are busy. Please order for dinner or tomorrow’s lunch.");
			timsSlotObject.put("splitOrder", false);
			timsSlotObject.put("slotDetails", slotDetailsJsonArray);
		}
		if(!slotFound){
			timsSlotObject.put("status", "204");
			timsSlotObject.put("staggered", false);
			timsSlotObject.put("message", "Oops!..Currently all our bikers are busy. Please order for dinner or tomorrow’s lunch.");
			timsSlotObject.put("splitOrder", false);
			timsSlotObject.put("slotDetails", slotDetailsJsonArray);
		}
		
		return timsSlotObject;
	}
	
	
	/**
	 * @author somnath dutta
	 * Check whether order is staggered or not?
	 * @param orderItemList
	 * @return true if total quantity exceeds biker capacity else false
	 */
	public static boolean isStaggeredOrder(ArrayList<OrderItems> orderItemList){
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int totalNoOfQuantity = 0, totalBengQty = 0,totalNIQty = 0;
		for(OrderItems items : orderItemList){
			totalNoOfQuantity += items.quantity;
			if(items.cuisineId == 1){
				totalBengQty += items.quantity;
			}
			if(items.cuisineId == 2){
				totalNIQty += items.quantity;
			}
		}
		if(totalNoOfQuantity>bikerCapacity || totalBengQty>bikerCapacity || totalNIQty>bikerCapacity ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * This method finds whether slot available or not 
	 */
	public static boolean isSlotFound(JSONArray slotDetailsJsonArray)throws Exception{
		int slotLengthCount = 0;
		for(int i=0; i<slotDetailsJsonArray.length(); i++){
			
			JSONObject kitchenJsonObj = slotDetailsJsonArray.getJSONObject(i);
			JSONArray bikerJsonArray = kitchenJsonObj.getJSONArray("bikerList");
			
			for(int j=0; j<bikerJsonArray.length(); j++){
				
				JSONObject bikerJsonObj = bikerJsonArray.getJSONObject(j);
				JSONArray slotJsonArray = bikerJsonObj.getJSONArray("slotlist");
				slotLengthCount += slotJsonArray.length();
			}
		}
		System.out.println("Total slots : "+slotLengthCount);
		if(slotLengthCount > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Method to find whether uncommon slot or not 
	 * if common slot available then return true
	 * else return false
	 */
	public static boolean isUncommonSlotFound(JSONArray slotDetailsJsonArray, int totalNoOfItems)throws Exception{
		int noOfKitchen = slotDetailsJsonArray.length();
		ArrayList<Integer> slotIdList = new ArrayList<Integer>();
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		
		for(int i=0; i<slotDetailsJsonArray.length(); i++){
			
			JSONObject kitchenJsonObj = slotDetailsJsonArray.getJSONObject(i);
			JSONArray bikerJsonArray = kitchenJsonObj.getJSONArray("bikerList");
			
			for(int j=0; j<bikerJsonArray.length(); j++){
				
				JSONObject bikerJsonObj = bikerJsonArray.getJSONObject(j);
				JSONArray slotJsonArray = bikerJsonObj.getJSONArray("slotlist");
				for(int k=0; k< slotJsonArray.length() ; k++){
					JSONObject slotJson = slotJsonArray.getJSONObject(k);
					slotIdList.add(slotJson.getInt("slotId"));
				}
			}
		}
		System.out.println("Total slot id list : "+slotIdList);
		Map<Integer, Integer> slotFrequencyMap = new HashMap<Integer, Integer>();
		
		for(Integer slotID : slotIdList){
			slotFrequencyMap.put(slotID, Collections.frequency(slotIdList, slotID));
		}
		
		int count = 0;
		for(Map.Entry<Integer, Integer> me: slotFrequencyMap.entrySet()){
			if(me.getValue() == noOfKitchen){
				count ++;
			}
		}
		
		if(noOfKitchen == 1 && totalNoOfItems>bikerCapacity){
			System.out.println("Single kitchen uncommon slot!");
			return true;
		}else{
			if( count == slotFrequencyMap.size()){
				System.out.println("All are common slot!");
				return false;
			}else{
				System.out.println("Uncommon slot found!");
				return true;
			}
		}
		
	}
}
