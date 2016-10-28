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

import pojo.KitchenStock;
import pojo.MealTypePojo;
import pojo.TimeSlot;
import utility.ValueComparator;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class TimeSlotFinder {

	public static JSONObject getFreeSlots(String contactNumber, String deliveryAddress, ArrayList<OrderItems>
	orderItemList, String mealtype,String deliveryDay, String pincode,  MealTypePojo mealTypePojo) throws JSONException{
		int totalQty = 0;
		for(OrderItems items : orderItemList){
			totalQty += items.quantity;
		}
		System.out.println("Total qty: "+totalQty);
		JSONObject timsSlotObject = new JSONObject();
		JSONArray kitchens = findServableKitchens(orderItemList, pincode, mealTypePojo,
				contactNumber, deliveryAddress, mealtype, deliveryDay );
		
		System.out.println("KITCHEN JSON ARRAY LENGTH:: "+kitchens.length());
		if(kitchens.length() >0){
			timsSlotObject.put("status", "200");
			timsSlotObject.put("message", "Slot found successfully!");
			if(kitchens.length()>1 || totalQty>10){
				System.out.println("##");
				timsSlotObject.put("splitOrder", true);		
			}else{
				System.out.println("%%");
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
		boolean  onlyBengCuisine = false, onlyNiCuisine = false, bengNiCuisine = false,isStaggeredDelivery=false;
		int totalNoOfQuantity = 0, totalBengQty = 0,totalNIQty = 0;

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
				totalNIQty += orderItemList.get(i).quantity;
				niCuisineIdList.add(new OrderItems(orderItemList.get(i).cuisineId, orderItemList.get(i).categoryId, 
						orderItemList.get(i).itemCode,orderItemList.get(i).quantity, orderItemList.get(i).price) );
			}
			if(orderItemList.get(i).cuisineId==1){
				totalBengQty += orderItemList.get(i).quantity;
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


		System.out.println("dealingKitchenIds:: "+dealingKitchenIds);

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

		Set<Integer> kitchenSet = new HashSet<Integer>();
		for(Integer kitchenId : dealingKitchenIds){
			kitchenSet.add(kitchenId);
		}

		ArrayList<Integer> kitchenIds =  new ArrayList<Integer>(kitchenSet);

		System.out.println("Final kitchenlist : "+dealingKitchenIds);
		System.out.println("Final kitchenlist size : "+dealingKitchenIds.size());
		System.out.println("New kitchensss:: "+kitchenIds);


		ArrayList<KitchenStock> kitchenStockList = new ArrayList<KitchenStock>();
		ArrayList<OrderItems> kitchenOrderItems = new ArrayList<OrderItems>();
		for(Integer kitchen : kitchenIds){
			KitchenStock kitchenStock = new KitchenStock();
			kitchenStock.kitchenId = kitchen;
			kitchenStock.stock = SameUserPlaceOrder.getKitchenCurrentStock(kitchen, mealTypePojo);
			kitchenStockList.add(kitchenStock);
		}
		Collections.sort(orderItemList);
		for(KitchenStock kitchenStock: kitchenStockList){
			for(OrderItems items : orderItemList){
				//24_09_2016 added for item serving kitchens
				if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenStock.kitchenId)){
					if(kitchenStock.stock>0){
						if(items.quantity == 0){
							continue;
						}
						if(kitchenStock.stock>items.quantity){
							OrderItems kio = new OrderItems();
							kio.itemName = items.itemName;
							kio.itemDescription = items.itemDescription;
							kio.cuisineId = items.cuisineId;
							kio.cuisinName = items.cuisinName;
							kio.itemCode = items.itemCode;
							kio.quantity = items.quantity;
							kio.kitchenId = kitchenStock.kitchenId;
							kitchenOrderItems.add(kio);
							kitchenStock.stock = kitchenStock.stock - items.quantity;
							items.quantity = 0;
						} else {
							OrderItems kio = new OrderItems();
							kio.itemName = items.itemName;
							kio.itemDescription = items.itemDescription;
							kio.cuisineId = items.cuisineId;
							kio.cuisinName = items.cuisinName;
							kio.itemCode = items.itemCode;
							kio.quantity = kitchenStock.stock;
							kio.kitchenId = kitchenStock.kitchenId;
							kitchenOrderItems.add(kio);
							items.quantity = items.quantity - kitchenStock.stock;
							break;
						}
					}
				}
			}
		}

		System.out.println("TotalNoOfQuantity:: "+totalNoOfQuantity);
		System.out.println("TotalNoOfBengQuantity:: "+totalBengQty);
		System.out.println("TotalNoOfNIQuantity:: "+totalNIQty);

		for(int i=0 ; i < kitchenItemsOrderList.size() ; i++){
			System.out.print("CUID::"+kitchenItemsOrderList.get(i).cuisineId);
			System.out.print(" CUI ::"+kitchenItemsOrderList.get(i).cuisinName);
			//System.out.print(" CATID::"+kitchenItemsOrderList.get(i).categoryId);
			//System.out.print(" CAT::"+kitchenItemsOrderList.get(i).categoryName);
			System.out.print(" ITEM::"+kitchenItemsOrderList.get(i).itemCode);
			System.out.print(" ITEM NAME::"+kitchenItemsOrderList.get(i).itemName);
			System.out.print(" QTY::"+kitchenItemsOrderList.get(i).quantity);
			System.out.println(" KITCHEN::"+kitchenItemsOrderList.get(i).kitchenId+"\n");

		}	
		if(totalNoOfQuantity>10 || totalBengQty>10 || totalNIQty>10 ){
			isStaggeredDelivery = true;
		}
		System.out.println("IS sttaggered delivery:: "+isStaggeredDelivery);
		System.out.println("IS ordder spilt::"+isOrdeSplit);


		if(isOrdeSplit){
			//isStaggeredDelivery = false;
			int totalBengQtyOrig = totalBengQty;
			int totalNIQtyOrig = totalNIQty;
			for(Integer kitchenid : kitchenIds){
				if(findKitchenType(kitchenid)==1){// 1 means bengali kitchen
					int currentkitchenStock = RoundRobinKitchenFinder.getCurrentKitchenStock(kitchenid, mealTypePojo);
					if(totalBengQtyOrig > currentkitchenStock){
						totalBengQty = currentkitchenStock;
					}
					JSONObject kitchenJson = new JSONObject();
					JSONArray bikersArray = new JSONArray();
					kitchenJson.put("kitchenId", kitchenid );
					ArrayList<String> bikerList = new ArrayList<String>();
					if(totalNoOfQuantity>1){//If quantity >1
						bikerList = findBikerOfKitchen(kitchenid);
					}else{
						bikerList.add("dummy");
					}
					System.out.println("Final bikerlist : "+bikerList);

					for(String bikerUserId : bikerList){
						if(totalBengQty < 1){
							continue;
						}
						JSONObject bikerJson = new JSONObject();
						JSONArray itemsArrray = new JSONArray();
						bikerJson.put("bikerUserId", bikerUserId);
						for(OrderItems orders : kitchenOrderItems){
							System.out.println("Orders kitchen:: "+orders.kitchenId);
							System.out.println("kicthenid:: "+kitchenid);
							if(orders.kitchenId==kitchenid){
								JSONObject items = new JSONObject();
								items.put("cuisineid", orders.cuisineId);
								items.put("cuisine", orders.getCuisinName());
								items.put("itemName", orders.getItemName());
								items.put("itemCode", orders.getItemCode());
								items.put("quanity", orders.getQuantity());
								itemsArrray.put(items);
							}
						}
						bikerJson.put("itemDetails", itemsArrray );

						JSONArray slotJSONArray = new JSONArray();
						if(bikerUserId.equalsIgnoreCase("dummy")){
							slotJSONArray = SlotDAO.findAllSlots(mealTypePojo);
						}else{
							ArrayList<TimeSlot> returningTimeSlotList = new ArrayList<TimeSlot>();
							ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, kitchenid, mealTypePojo);
							Collections.sort(timeSlotList);
							int Qty = 0;
							for(TimeSlot slot : timeSlotList){
								Qty = Qty + slot.quantity;
							}
							if(mealTypePojo.isLunchToday()||mealTypePojo.isLunchTomorrow()){
								if(totalBengQty > 30 - Qty){
									continue;
								}
							}else{
								if(totalBengQty > 20 - Qty){
									continue;
								}
							}
							/*if(totalBengQty > 30 - Qty){
								continue;
							}*/
							if(isStaggeredDelivery){
								for(TimeSlot slot : timeSlotList){
									if(totalBengQty<=0){
										continue;
									}
									if(10-slot.quantity <= 10){
										TimeSlot reslot = new TimeSlot();
										reslot.slotId = slot.slotId;
										reslot.timeSlot = slot.timeSlot;
										reslot.quantity = slot.quantity;
										reslot.noOfOrders = slot.noOfOrders;
										returningTimeSlotList.add(reslot);
										totalBengQty = totalBengQty -(10 - slot.quantity);
										System.out.println(returningTimeSlotList);
										System.out.println("remain qty :"+totalBengQty);
									}
								}
							} else {
								for(TimeSlot slot : timeSlotList){
									if(totalBengQty + slot.quantity > 10){
										continue;
									} else {
										TimeSlot reslot = new TimeSlot();
										reslot.slotId = slot.slotId;
										reslot.timeSlot = slot.timeSlot;
										reslot.quantity = slot.quantity;
										reslot.noOfOrders = slot.noOfOrders;
										returningTimeSlotList.add(reslot);
									}
								}
								if(returningTimeSlotList.size() == 0){
									for(TimeSlot slot : timeSlotList){
										if(totalBengQty<=0){
											continue;
										}
										if(10-slot.quantity <= 10){
											TimeSlot reslot = new TimeSlot();
											reslot.slotId = slot.slotId;
											reslot.timeSlot = slot.timeSlot;
											reslot.quantity = slot.quantity;
											reslot.noOfOrders = slot.noOfOrders;
											returningTimeSlotList.add(reslot);
											totalBengQty = totalBengQty -(10 - slot.quantity);
											System.out.println(returningTimeSlotList);
											System.out.println("remain qty :"+totalBengQty);
										}
									}
								}
							}
							for(TimeSlot tSlot : returningTimeSlotList){
								JSONObject slotJson = new JSONObject();
								slotJson.put("slotId", tSlot.slotId);
								slotJson.put("timeSlot", tSlot.timeSlot);
								slotJson.put("quantity", tSlot.quantity);
								slotJson.put("noOfOrders", tSlot.noOfOrders);
								slotJSONArray.put(slotJson);
							}
							System.out.println("::::::::::::::::::::Biker ends here:::::::::::::::::::::::::::::::::");
							System.out.println("BENGALI"+returningTimeSlotList);
						}
						bikerJson.put("slotlist", slotJSONArray);
						bikersArray.put(bikerJson);
					}
					kitchenJson.put("bikerList", bikersArray);
					servableKitchens.put(kitchenJson);
				}else{
					//NI KITCHEN
					int currentkitchenStockNI = RoundRobinKitchenFinder.getCurrentKitchenStock(kitchenid, mealTypePojo);
					if(totalNIQtyOrig > currentkitchenStockNI){
						totalNIQty = currentkitchenStockNI;
					}
					
					JSONObject kitchenJson = new JSONObject();
					JSONArray bikersArray = new JSONArray();
					kitchenJson.put("kitchenId", kitchenid );
					ArrayList<String> bikerList = new ArrayList<String>();
					if(totalNoOfQuantity>1){//If quantity >1
						bikerList = findBikerOfKitchen(kitchenid);
					}else{
						bikerList.add("dummy");
					}
					System.out.println("Final bikerlist : "+bikerList);
					
					for(String bikerUserId : bikerList){
						if(totalNIQty < 1){
							continue;
						}
						JSONObject bikerJson = new JSONObject();
						JSONArray itemsArrray = new JSONArray();
						bikerJson.put("bikerUserId", bikerUserId);
						for(OrderItems orders : kitchenOrderItems){
							System.out.println("Orders kitchen:: "+orders.kitchenId);
							System.out.println("kicthenid:: "+kitchenid);
							if(orders.kitchenId==kitchenid){
								JSONObject items = new JSONObject();
								items.put("cuisineid", orders.cuisineId);
								items.put("cuisine", orders.getCuisinName());
								items.put("itemName", orders.getItemName());
								items.put("itemCode", orders.getItemCode());
								items.put("quanity", orders.getQuantity());
								itemsArrray.put(items);
							}
						}
						bikerJson.put("itemDetails", itemsArrray );

						JSONArray slotJSONArray = new JSONArray();
						if(bikerUserId.equalsIgnoreCase("dummy")){
							slotJSONArray = SlotDAO.findAllSlots(mealTypePojo);
						}else{
							ArrayList<TimeSlot> returningTimeSlotList = new ArrayList<TimeSlot>();
							ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, kitchenid, mealTypePojo);
							Collections.sort(timeSlotList);
							int Qty = 0;
							for(TimeSlot slot : timeSlotList){
								Qty = Qty + slot.quantity;
							}
							if(mealTypePojo.isLunchToday()||mealTypePojo.isLunchTomorrow()){
								if(totalNIQty > 30 - Qty){
									continue;
								}
							}else{
								if(totalNIQty > 20 - Qty){
									continue;
								}
							}
							/*if(totalNIQty > 30 - Qty){
								continue;
							}*/
							if(isStaggeredDelivery){
								for(TimeSlot slot : timeSlotList){
									if(totalNIQty<=0){
										continue;
									}
									if(10-slot.quantity <= 10){
										TimeSlot reslot = new TimeSlot();
										reslot.slotId = slot.slotId;
										reslot.timeSlot = slot.timeSlot;
										reslot.quantity = slot.quantity;
										reslot.noOfOrders = slot.noOfOrders;
										returningTimeSlotList.add(reslot);
										totalNIQty = totalNIQty -(10 - slot.quantity);
										System.out.println(returningTimeSlotList);
										System.out.println("remain qty :"+totalNIQty);
									}
								}
							} else {
								for(TimeSlot slot : timeSlotList){
									if(totalNIQty + slot.quantity > 10){
										continue;
									} else {
										TimeSlot reslot = new TimeSlot();
										reslot.slotId = slot.slotId;
										reslot.timeSlot = slot.timeSlot;
										reslot.quantity = slot.quantity;
										reslot.noOfOrders = slot.noOfOrders;
										returningTimeSlotList.add(reslot);
									}
								}
								if(returningTimeSlotList.size() == 0){
									for(TimeSlot slot : timeSlotList){
										if(totalNIQty<=0){
											continue;
										}
										if(10-slot.quantity <= 10){
											TimeSlot reslot = new TimeSlot();
											reslot.slotId = slot.slotId;
											reslot.timeSlot = slot.timeSlot;
											reslot.quantity = slot.quantity;
											reslot.noOfOrders = slot.noOfOrders;
											returningTimeSlotList.add(reslot);
											totalNIQty = totalNIQty -(10 - slot.quantity);
											System.out.println(returningTimeSlotList);
											System.out.println("remain qty :"+totalNIQty);
										}
									}
								}
							}
							for(TimeSlot tSlot : returningTimeSlotList){
								JSONObject slotJson = new JSONObject();
								slotJson.put("slotId", tSlot.slotId);
								slotJson.put("timeSlot", tSlot.timeSlot);
								slotJson.put("quantity", tSlot.quantity);
								slotJson.put("noOfOrders", tSlot.noOfOrders);
								slotJSONArray.put(slotJson);
							}
							System.out.println("::::::::::::::::::::Biker ends here:::::::::::::::::::::::::::::::::");
							System.out.println("NORTH "+returningTimeSlotList);
						}
						bikerJson.put("slotlist", slotJSONArray);
						bikersArray.put(bikerJson);
					}
					kitchenJson.put("bikerList", bikersArray);
					servableKitchens.put(kitchenJson);
				}
				
			}
			
		}else{
			System.out.println("Not spilt::::::::::::::::::::::tot qty:"+totalNoOfQuantity);
			for(Integer kicthenid : kitchenIds){
				JSONObject kitchenJson = new JSONObject();
				JSONArray bikersArray = new JSONArray();
				kitchenJson.put("kitchenId", kicthenid );

				ArrayList<String> bikerList = new ArrayList<String>();
				if(totalNoOfQuantity>1){//If quantity >1
					bikerList = findBikerOfKitchen(kicthenid);
				}else{
					bikerList.add("dummy");
				}
				System.out.println("Final bikerlist : "+bikerList);

				for(String bikerUserId : bikerList){
					if(totalNoOfQuantity < 1){
						break;
					}
					JSONObject bikerJson = new JSONObject();
					JSONArray itemsArrray = new JSONArray();
					bikerJson.put("bikerUserId", bikerUserId);
					for(OrderItems orders : kitchenOrderItems){
						System.out.println("Orders kitchen:: "+orders.kitchenId);
						System.out.println("kicthenid:: "+kicthenid);
						if(orders.kitchenId==kicthenid){
							JSONObject items = new JSONObject();
							items.put("cuisineid", orders.cuisineId);
							items.put("cuisine", orders.getCuisinName());
							items.put("itemName", orders.getItemName());
							items.put("itemCode", orders.getItemCode());
							items.put("quanity", orders.getQuantity());
							itemsArrray.put(items);
						}
					}
					bikerJson.put("itemDetails", itemsArrray );

					JSONArray slotJSONArray = new JSONArray();
					if(bikerUserId.equalsIgnoreCase("dummy")){
						slotJSONArray = SlotDAO.findAllSlots(mealTypePojo);
					}else{
						ArrayList<TimeSlot> returningTimeSlotList = new ArrayList<TimeSlot>();
						ArrayList<TimeSlot> timeSlotList = SlotDAO.findCommonTimeSlots(bikerUserId, kicthenid, mealTypePojo);
						Collections.sort(timeSlotList);
						int Qty = 0;
						for(TimeSlot slot : timeSlotList){
							Qty = Qty + slot.quantity;
						}
						if(mealTypePojo.isLunchToday()||mealTypePojo.isLunchTomorrow()){
							if(totalNoOfQuantity > 30 - Qty){
								continue;
							}
						}else{
							if(totalNoOfQuantity > 20 - Qty){
								continue;
							}
						}
						/*if(totalNoOfQuantity > 30 - Qty){
							continue;
						}*/
						if(isStaggeredDelivery){
							for(TimeSlot slot : timeSlotList){
								if(totalNoOfQuantity<=0){
									continue;
								}
								if(10-slot.quantity <= 10){
									TimeSlot reslot = new TimeSlot();
									reslot.slotId = slot.slotId;
									reslot.timeSlot = slot.timeSlot;
									reslot.quantity = slot.quantity;
									reslot.noOfOrders = slot.noOfOrders;
									returningTimeSlotList.add(reslot);
									totalNoOfQuantity = totalNoOfQuantity -(10 - slot.quantity);
									System.out.println(returningTimeSlotList);
									System.out.println("remain qty :"+totalNoOfQuantity);
								}
							}
						} else {
							for(TimeSlot slot : timeSlotList){
								if(totalNoOfQuantity + slot.quantity > 10){
									continue;
								} else {
									TimeSlot reslot = new TimeSlot();
									reslot.slotId = slot.slotId;
									reslot.timeSlot = slot.timeSlot;
									reslot.quantity = slot.quantity;
									reslot.noOfOrders = slot.noOfOrders;
									returningTimeSlotList.add(reslot);
								}
							}
							if(returningTimeSlotList.size() == 0){
								for(TimeSlot slot : timeSlotList){
									if(totalNoOfQuantity<=0){
										continue;
									}
									if(10-slot.quantity <= 10){
										TimeSlot reslot = new TimeSlot();
										reslot.slotId = slot.slotId;
										reslot.timeSlot = slot.timeSlot;
										reslot.quantity = slot.quantity;
										reslot.noOfOrders = slot.noOfOrders;
										returningTimeSlotList.add(reslot);
										totalNoOfQuantity = totalNoOfQuantity -(10 - slot.quantity);
										System.out.println(returningTimeSlotList);
										System.out.println("remain qty :"+totalNoOfQuantity);
									}
								}
							}
						}
						for(TimeSlot tSlot : returningTimeSlotList){
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", tSlot.slotId);
							slotJson.put("timeSlot", tSlot.timeSlot);
							slotJson.put("quantity", tSlot.quantity);
							slotJson.put("noOfOrders", tSlot.noOfOrders);
							slotJSONArray.put(slotJson);
						}
						System.out.println("::::::::::::::::::::Biker ends here:::::::::::::::::::::::::::::::::");
						System.out.println("SLOTS "+returningTimeSlotList);
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
	
	//********************old code****************************//
	/*for(Integer kicthenid : kitchenIds){
	JSONObject kitchenJson = new JSONObject();
	JSONArray bikersArray = new JSONArray();
	kitchenJson.put("kitchenId", kicthenid );

	ArrayList<String> bikerList = new ArrayList<String>();
	if(totalNoOfQuantity>1){//If quantity >1
		bikerList = findBikerOfKitchen(kicthenid);
	}else{
		bikerList.add("dummy");
	}
	System.out.println("Final bikerlist : "+bikerList);

	for(String bikerUserId : bikerList){
		JSONObject bikerJson = new JSONObject();
		JSONArray itemsArrray = new JSONArray();
		bikerJson.put("bikerUserId", bikerUserId);
		for(OrderItems orders : kitchenOrderItems){
			System.out.println("Orders kitchen:: "+orders.kitchenId);
			System.out.println("kicthenid:: "+kicthenid);
			if(orders.kitchenId==kicthenid){
				JSONObject items = new JSONObject();
				items.put("cuisine", orders.getCuisinName());
				items.put("itemName", orders.getItemName());
				items.put("itemCode", orders.getItemCode());
				items.put("quanity", orders.getQuantity());
				itemsArrray.put(items);
			}
		}
		bikerJson.put("itemDetails", itemsArrray );
		JSONArray slotJSONArray = new JSONArray();
		if(bikerUserId.equalsIgnoreCase("dummy")){
			slotJSONArray = findAllSlots(mealTypePojo);
		}else{
			ArrayList<TimeSlot> timeSlotList = findCommonTimeSlots(bikerUserId, kicthenid, mealTypePojo);
			for(TimeSlot slot : timeSlotList){
				if(  (10-slot.quantity) >= totalBengQty && (isStaggeredDelivery==false)){
						System.out.println("Quantity apply beng:: "+(10-slot.quantity));
						System.out.println("My test slot id:: "+slot.slotId);
						JSONObject slotJson = new JSONObject();
						slotJson.put("slotId", slot.slotId);
						slotJson.put("timeSlot", slot.timeSlot);
						slotJson.put("quantity", slot.quantity);
						slotJson.put("noOfOrders", slot.noOfOrders);
						slotJSONArray.put(slotJson);
					}else{
						if(totalBengQty>0){
							totalBengQty = totalBengQty -(10-slot.quantity);
							System.out.println("Total qnty beng :: "+totalBengQty);
							JSONObject slotJson = new JSONObject();
							slotJson.put("slotId", slot.slotId);
							slotJson.put("timeSlot", slot.timeSlot);
							slotJson.put("quantity", slot.quantity);
							slotJson.put("noOfOrders", slot.noOfOrders);
							slotJSONArray.put(slotJson);
						}
					}


				if(totalNoOfQuantity+slot.quantity > 10){
					//skip 
				}else{
					JSONObject slotJson = new JSONObject();
					slotJson.put("slotId", slot.slotId);
					slotJson.put("timeSlot", slot.timeSlot);
					slotJson.put("quantity", slot.quantity);
					slotJson.put("noOfOrders", slot.noOfOrders);
					slotJSONArray.put(slotJson);
				}
			}
		}
		bikerJson.put("slotlist", slotJSONArray);
		bikersArray.put(bikerJson);
	}	
	kitchenJson.put("bikerList", bikersArray);
	servableKitchens.put(kitchenJson);
}*/

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

	/**
	 * This method returns the bikers of kitchen
	 * @param kicthenId
	 * @return
	 */
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

	

	public static ArrayList<TimeSlot> findTimeSlot(String boyUserId, int kitchenId, MealTypePojo mealTypePojo,
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
						TimeSlot timeSlot = new TimeSlot();
						timeSlot.slotId = resultSet.getInt("time_slot_id");
						timeSlot.timeSlot = resultSet.getString("time_slot");
						timeSlot.quantity = 0;
						timeSlot.noOfOrders = 0;
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
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
							+ " and ftds.time_slot_id <4 ";
				}else if(mealTypePojo.isDinnerToday()){
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id >3 ";*/
							+ " and ftds.time_slot_id >3 ";
				}else if(mealTypePojo.isLunchTomorrow()){
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
							+ " from fapp_timeslot_driver_status_tommorrow ftds "
							+ " join fapp_timeslot ft "
							+ " on ftds.time_slot_id = ft.time_slot_id"
							+ " where driver_user_id = ? and is_slot_locked ='N'  "
							+ " and is_slot_active ='Y' and no_of_orders < 2 and quantity <9 "
							/*+ " and assigned_date = current_date and ftds.time_slot_id <4 ";*/
							+ " and ftds.time_slot_id <4 ";
				}else{
					sql= "select ftds.time_slot_id ,ft.time_slot,ftds.quantity,ftds.no_of_orders "
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
						TimeSlot timeSlot = new TimeSlot();
						timeSlot.slotId = resultSet.getInt("time_slot_id");
						timeSlot.timeSlot = resultSet.getString("time_slot");
						timeSlot.quantity = resultSet.getInt("quantity");
						timeSlot.noOfOrders = resultSet.getInt("no_of_orders");

						timeSlotList.add(timeSlot);

						/*JSONObject timeSlotJson = new JSONObject();
							timeSlotJson.put("slotId", resultSet.getInt("time_slot_id"));
							timeSlotJson.put("timeSlot", resultSet.getString("time_slot"));
							timeSlotJson.put("quantity", resultSet.getInt("quantity"));
							if( (timeSlotJson.getInt("quantity")+totalNoOfQuantity) > 8){
								///Skip the slot
							}else{
								timeSlotArray.put(timeSlotJson);
							}*/

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
		return timeSlotList;
	}

	

	public static boolean isDifferentCuisineKitchen(ArrayList<Integer> dealingKitchenIds){
		boolean differentCuisineKitchen = false;
		Set<Integer> commonKitchens = new HashSet<Integer>();

		if(dealingKitchenIds.size()>1){
			commonKitchens.addAll(dealingKitchenIds);
			if(commonKitchens.size()>1){
				differentCuisineKitchen = true;
			}
		}
		System.out.println("* * * * Order spilt :: "+differentCuisineKitchen);
		return differentCuisineKitchen ;
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

	public static int getTimeSlotId(String timeSlot){
		int slotId = 0;
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select time_slot_id from fapp_timeslot where time_slot = ?";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, timeSlot);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					slotId = resultSet.getInt("time_slot_id");
				}
			} catch (Exception e) {
				// TODO: handle exception
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
		return slotId;
	}
	
	public static int getBikerStock(String boyUserId, int slotID, MealTypePojo mealTypePojo){
		int currentAvailability=0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql= "select ftds.quantity from fapp_timeslot_driver_status ftds "
							+ " where driver_user_id = ? and time_slot_id = ? and is_lunch='Y' ";
								
					}else if(mealTypePojo.isDinnerToday() ){
						sql= "select ftds.quantity from fapp_timeslot_driver_status ftds "
								+ " where driver_user_id = ? and time_slot_id = ? and is_lunch='N' ";
					}else if(mealTypePojo.isLunchTomorrow() ){
						sql= "select ftds.quantity from fapp_timeslot_driver_status_tommorrow ftds "
								+ " where driver_user_id = ? and time_slot_id = ? and is_lunch='Y' ";
					}else{
						sql= "select ftds.quantity from fapp_timeslot_driver_status_tommorrow ftds "
								+ " where driver_user_id = ? and time_slot_id = ? and is_lunch='N' ";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						preparedStatement.setInt(2, slotID);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							currentAvailability = resultSet.getInt("quantity");
						}
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
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		return currentAvailability;
	}
}
