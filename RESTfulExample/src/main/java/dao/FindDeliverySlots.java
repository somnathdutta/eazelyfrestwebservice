package dao;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import pojo.MealTypePojo;

import com.appsquad.finder.KitchenFinder;
import com.mkyong.rest.OrderItems;

public class FindDeliverySlots {

	public static JSONObject getDeliverySlots(String contactNumber, String deliveryAddress, ArrayList<OrderItems>
	orderItemList, String mealType,String deliveryDay, String pincode,  MealTypePojo mealTypePojo, String area)
	throws Exception{
		JSONObject timsSlotObject = new JSONObject();
		JSONArray slotDetailsJsonArray = new JSONArray();
		
		boolean isStaggeredOrder = false, isSplitOrder = false;
		
		/**
		 * Add items with kitchen servable stock
		 */
		ArrayList<OrderItems> ordersWithKitchen = KitchenFinder.getKitchenOfOrderedItem(orderItemList, mealType, deliveryDay, area);
		
		isStaggeredOrder = isStaggeredOrder(ordersWithKitchen);
		
		
		/**
		 * Find dealing kitchen id's with respect to ordered items
		 */
		ArrayList<Integer> dealingKitchens = KitchenFinder.getDealingKitchenIds(ordersWithKitchen);
		if(dealingKitchens.size() > 1){
			isSplitOrder = true;
		}
		
		
		/**
		 * Find delivery slots for dealing kitchens, if found
		 */
		if(dealingKitchens.size()>0){
			slotDetailsJsonArray = DeliverySlotFinder.getSlotDetails(dealingKitchens, ordersWithKitchen, mealTypePojo);
		}
		
		
		if(dealingKitchens.size()>0){
			timsSlotObject.put("status", "200");
			timsSlotObject.put("message", "Slot found successfully!");
			timsSlotObject.put("staggered", isStaggeredOrder);
			timsSlotObject.put("splitOrder", isSplitOrder);
			timsSlotObject.put("slotDetails", slotDetailsJsonArray);
		}else{
			timsSlotObject.put("status", "204");
			timsSlotObject.put("message", "Sorry!Items sold out at this time!");
			timsSlotObject.put("staggered", false);
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
	
	
}
