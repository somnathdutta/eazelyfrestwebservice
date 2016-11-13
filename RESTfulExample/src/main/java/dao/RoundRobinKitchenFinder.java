package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.logging.impl.AvalonLogger;

import pojo.KitchenStock;
import pojo.MealTypePojo;
import utility.ValueComparator;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class RoundRobinKitchenFinder {

	public static ArrayList<Integer> getUniqueKitchen(ArrayList<OrderItems> orderList , String pincode,
			String mealType, String deliveryDay, String area ){
		System.out.println("***************************************************************************");
		System.out.println(" APPLYING ROUND ROBIN ALGORITHM FOR KITCHEN******");
		System.out.println("***************************************************************************");
		
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
		
		boolean kitchenFoundFromPincode = false;
		int totalQuantity = 0,	 totalAvailableStock = 0;;
		
		ArrayList<Integer> niKitchenidList = new ArrayList<Integer>();
		ArrayList<OrderItems> niCuisineIdList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> bengCuisineIdList = new ArrayList<OrderItems>();
		
		MealTypePojo mealTypePojo = new MealTypePojo();
		if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealTypePojo.setLunchToday(true);
		}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealTypePojo.setDinnerToday(true);
		}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealTypePojo.setLunchTomorrow(true);
		}else{
			mealTypePojo.setDinnerTomorrow(true);
		}
		
		//System.out.println("Total item ordered: "+orderList.size());
		
		for(OrderItems items:orderList){
			/*System.out.print("CUID::"+orderList.get(i).cuisineId+"\t");
			System.out.print("CATID::"+orderList.get(i).categoryId+"\t");
			System.out.print("ITEM::"+orderList.get(i).itemCode+"\t");
    		System.out.print("QTY::"+orderList.get(i).quantity+"\n");*/
			totalQuantity += items.quantity;
		}	
		
		for(int i=0;i<orderList.size();i++){
			if(orderList.get(i).cuisineId==2){
				niCuisineIdList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, 
						orderList.get(i).itemCode,orderList.get(i).quantity, orderList.get(i).price) );
			}
			if(orderList.get(i).cuisineId==1){
				bengCuisineIdList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, 
						orderList.get(i).itemCode,orderList.get(i).quantity, orderList.get(i).price) );
			}
		}
		
		System.out.println("Total item order size: "+orderList.size());
		System.out.println("BEN cuisine order size: "+bengCuisineIdList.size());
		System.out.println("NI cuisine order size: "+niCuisineIdList.size());
		
		if(niCuisineIdList.size()>0 && niCuisineIdList.size()<orderList.size()){
			System.out.println("*** *** *** ORDER is going to split*** * *** *** ");
			niKitchenidList = findNIKitchenIds(niCuisineIdList, pincode, mealType , deliveryDay, area);
		}
	
		if(orderList.size() == niCuisineIdList.size()){
				System.out.println("- - - -  Only NI cuisines found on ordered items- - - ");
				dealingKitchenIds = findNIKitchenIds(niCuisineIdList, pincode, mealType , deliveryDay, area);
				return dealingKitchenIds;
		}else{
				ArrayList<String> iemcode = new ArrayList<String>();
				for(OrderItems order : orderList){
					if(order.cuisineId==1)
					iemcode.add("'"+order.itemCode+"'");
				}
				
				int totalOrderedItems = 0,totalOrderedQuantity=0;
				for(OrderItems order : bengCuisineIdList){
					totalOrderedItems ++;
					totalOrderedQuantity += order.quantity;
				}
				
				String a = iemcode.toString();
				String fb = a.replace("[", "(");
				String itemcodes = fb.replace("]", ")");
				System.out.println("Item codes:: "+itemcodes);
				try {
					SQL:{
							Connection connection = DBConnection.createConnection();
							
							PreparedStatement preparedStatement = null;
							ResultSet resultSet = null;
							String sql = "";
							/*sql = "select  fki.kitchen_id,fki.stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.stock <>0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";*/
							if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
								/*sql = "select  fki.kitchen_id,fki.stock AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN "+itemcodes
										+" and fk.serving_zipcodes LIKE ? and fki.stock > 0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";*/
								sql = "select kitchen_id,stock AS stock "
										+" from vw_active_kitchen_items "
										+" where item_code IN "+itemcodes
										+" and serving_areas LIKE ? "
										+" and is_active='Y'";
							}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
								/*sql = "select  fki.kitchen_id,fki.stock_tomorrow AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN "+itemcodes
										+" and fk.serving_zipcodes LIKE ? and fki.stock_tomorrow >0 "
										+ " and fk.is_active='Y' and fki.is_active_tomorrow='Y'";*/
								sql = "select kitchen_id,stock_tomorrow AS stock "
										+" from vw_active_kitchen_items "
										+" where item_code IN "+itemcodes
										+" and serving_areas LIKE ? "
										+" and is_active_tomorrow = 'Y'";
							}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
								/*sql = "select  fki.kitchen_id,fki.dinner_stock AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN "+itemcodes
										+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock >0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";*/
								sql = "select kitchen_id,dinner_stock AS stock "
										+" from vw_active_kitchen_items "
										+" where item_code IN "+itemcodes
										+" and serving_areas LIKE ? "
										+" and is_active='Y'";
							}else{
								/*sql = "select  fki.kitchen_id,fki.dinner_stock_tomorrow AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN "+itemcodes
										+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock_tomorrow >0 "
										+ " and fk.is_active='Y' and fki.is_active_tomorrow='Y'";*/
								sql = "select kitchen_id,dinner_stock_tomorrow AS stock "
										+" from vw_active_kitchen_items "
										+" where item_code IN "+itemcodes
										+" and serving_areas LIKE ? "
										+" and is_active_tomorrow='Y'";
							}
							
							try {
								preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, "%"+area+"%");
								resultSet = preparedStatement.executeQuery();
								while (resultSet.next()) {
								kitchenFoundFromPincode = true;
								 int kid = resultSet.getInt("kitchen_id");
								 int stock = resultSet.getInt("stock");
								 //Check if kitchen having stock but having bikers or not?
								 if(isKitchenHavingFreeBikers(kid, mealTypePojo)){
									 kitchenIds.add(kid);
									 kitchenStockMap.put(kid, stock);
								 }
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
					System.out.println("kitchen stock Map: "+maxCapaMap);
					
					ValueComparator bvc = new ValueComparator(maxCapaMap);
			        TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
			        sorted_map.putAll(maxCapaMap);
					System.out.println("::::::::Sorted map(245) ::::::"+sorted_map);
					
					
					ArrayList<Integer> sortedKitchenIds = new ArrayList<Integer>();
					
					for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
						if(totalQuantity == mp.getValue()){
							//System.out.println("---Qty matches :: "+mp.getKey());
						}
						//sortedKitchenIds.add(mp.getKey());
					}
					
					for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
						sortedKitchenIds.add(mp.getKey());
					}
					System.out.println(":::Sorted kitchen id:: "+sortedKitchenIds);
					
					
					/**
					 * Separating free biker kitchens from sorted kitchen ids
					 */
					ArrayList<Integer> freeBikerKitchenIds = new ArrayList<Integer>();
					totalAvailableStock = getTotalAvailableStock(itemcodes,mealTypePojo,totalOrderedItems);
					
					for(Integer sortedIds : sortedKitchenIds){
					/*********** NO NEED FOR ALL STOCK *********************/
					/*********** NEW LOGIC ********************************/
					/************ STOCK NOT REQUIRED *********************/
						//	if(SameUserPlaceOrder.isKitchenCapable(sortedIds, mealTypePojo, totalOrderedQuantity, itemcodes)){
							if(isKitchenHavingFreeBikers(sortedIds, mealTypePojo)){
								freeBikerKitchenIds.add(sortedIds);	
							}
					//	}
					}
					System.out.println("After first calculation freeBikerKitchenIds:: "+freeBikerKitchenIds);
					boolean sameCuisineSpilt = false;
					System.out.println("TOATL AVAIL STOCK::::"+totalAvailableStock);
					//if all kitchens not capable
					if(freeBikerKitchenIds.size()==0){
						if(isAllKitchenServable(sorted_map, totalAvailableStock)){
							sameCuisineSpilt = true;
							freeBikerKitchenIds.addAll(sortedKitchenIds);
						}
					}
					
					System.out.println("freeBikerKitchenIds:: "+freeBikerKitchenIds);
					System.out.println("IS SAME CUISINE SPILT :: "+sameCuisineSpilt);
					if(freeBikerKitchenIds.size()>1 && !sameCuisineSpilt){
						//Do round robin logic
						System.out.println("Only single kitchen do round robin. . . .");
						int tot=0;
						for(Integer kitchenId : freeBikerKitchenIds){
							if( RoundRobin.alreadyOrdered(kitchenId,1)){
								tot++;
								System.out.println("Already ordered!");
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
								selectedKitchenIds.add(kitchenId);
								System.out.println("else part: "+selectedKitchenIds);
								break;
							}		
						}
						if(tot == freeBikerKitchenIds.size()){
							System.out.println("All are ordered!");
							RoundRobin.makeAllFree(freeBikerKitchenIds,1);
							for(Integer kitchenId : freeBikerKitchenIds){
								if( RoundRobin.alreadyOrdered(kitchenId,1)){
									tot++;
									System.out.println("Tot Already ordered!");
								}else{
									RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
									selectedKitchenIds.add(kitchenId);
									System.out.println("Tot else part: "+selectedKitchenIds);
									break;
								}
							}
						}
					}else{
						//assign the kitchen
						System.out.println("Multi kitchen found dont do round robin . . . !");
						selectedKitchenIds.addAll(freeBikerKitchenIds);
					}
					
					/*int tot=0;
					for(Integer kitchenId : sortedKitchenIds){
						if(isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
							if( RoundRobin.alreadyOrdered(kitchenId,1)){
								tot++;
								System.out.println("Already ordered!");
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
								selectedKitchenIds.add(kitchenId);
								System.out.println("else part: "+selectedKitchenIds);
								break;
							}
							break;
						}
						if( RoundRobin.alreadyOrdered(kitchenId,1)){
							tot++;
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
							selectedKitchenIds.add(kitchenId);
							break;
						}
					}
					
					if(tot == sortedKitchenIds.size()){
						System.out.println("All are ordered!");
						RoundRobin.makeAllFree(sortedKitchenIds,1);
						for(Integer kitchenId : sortedKitchenIds){
							if(isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
								if( RoundRobin.alreadyOrdered(kitchenId,1)){
									tot++;
									System.out.println("Tot Already ordered!");
								}else{
									RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
									selectedKitchenIds.add(kitchenId);
									System.out.println("Tot else part: "+selectedKitchenIds);
									break;
								}
								break;
							}
							if( RoundRobin.alreadyOrdered(kitchenId,1)){
								tot++;
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,1);
								selectedKitchenIds.add(kitchenId);
								break;
							}
						}
					}*/
					
					/*if(niKitchenidList.size()>0){
						dealingKitchenIds.addAll(selectedKitchenIds);
						dealingKitchenIds.addAll(niKitchenidList);
					}else{
						for(int i = 0 ; i<orderList.size() ; i++){
							dealingKitchenIds.addAll(selectedKitchenIds);
						}
					}*/
					//Removing multipless
					/*for(int i =0;i<bengCuisineIdList.size();i++){
						dealingKitchenIds.addAll(selectedKitchenIds);
					}*/
					dealingKitchenIds.addAll(selectedKitchenIds);
					if(niKitchenidList.size()>0){
						dealingKitchenIds.addAll(niKitchenidList);
					}
					System.out.println("*******************************************************");
					System.out.println("Returning dealing kitchens with RR::"+dealingKitchenIds);
					System.out.println("*******************************************************");
					return dealingKitchenIds;
				}else{
					System.out.println("* * * NO ANY BENGALI KITCHEN IDS * * * *"+dealingKitchenIds);
					if(niKitchenidList.size()>0){
						dealingKitchenIds.addAll(niKitchenidList);
					}
					for(int i = 0 ; i<orderList.size() ; i++){
						dealingKitchenIds.addAll(selectedKitchenIds);
					}
					System.out.println("Returning dealing kitchens with RR::"+dealingKitchenIds);
					return dealingKitchenIds;
				}
		}
	}
	
	public static ArrayList<Integer> findNIKitchenIds(ArrayList<OrderItems> orderList,String pincode,
			String mealType,  String deliveryDay, String area){
		ArrayList<Integer> dealingIds = new ArrayList<Integer>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		boolean kitchenFoundFromPincode= false;
		MealTypePojo mealTypePojo = new MealTypePojo();
		if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealTypePojo.setLunchToday(true);
		}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealTypePojo.setDinnerToday(true);
		}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealTypePojo.setLunchTomorrow(true);
		}else{
			mealTypePojo.setDinnerTomorrow(true);
		}
		
			ArrayList<String> iemcode = new ArrayList<String>();
			Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
			for(OrderItems order : orderList){
				iemcode.add("'"+order.itemCode+"'");
			}
			String a = iemcode.toString();
			String fb = a.replace("[", "(");
			String itemcodes = fb.replace("]", ")");
			
			ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
			
			int totalOrderedItems = 0,totalOrderedQuantity=0;
			for(OrderItems order : orderList){
				totalOrderedItems ++;
				totalOrderedQuantity += order.quantity;
			}
			
			Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "";
						/*sql = "select  fki.kitchen_id,fki.stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk on "
								+" fki.kitchen_id = fk.kitchen_id "
								+" where fki.item_code IN ("+itemcodes+") "
								+" and fk.serving_zipcodes LIKE ? and fki.stock <>0"
								+" and fk.is_active='Y' and fki.is_active='Y'";*/
						if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
							/*sql = "select  fki.kitchen_id,fki.stock AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN "+itemcodes
									+" and fk.serving_zipcodes LIKE ? and fki.stock > 0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";*/
							sql = "select kitchen_id,stock AS stock "
									+" from vw_active_kitchen_items "
									+" where item_code IN "+itemcodes
									+" and serving_areas LIKE ? and stock > 0 "
									+" and is_active='Y'";
						}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
							/*sql = "select  fki.kitchen_id,fki.stock_tomorrow AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN "+itemcodes
									+" and fk.serving_zipcodes LIKE ? and fki.stock_tomorrow >0 "
									+ " and fk.is_active='Y' and fki.is_active_tomorrow='Y'";*/
							sql = "select kitchen_id,stock_tomorrow AS stock "
									+" from vw_active_kitchen_items "
									+" where item_code IN "+itemcodes
									+" and serving_areas LIKE ? and stock_tomorrow > 0 "
									+" and is_active_tomorrow ='Y'";
						}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
							/*sql = "select  fki.kitchen_id,fki.dinner_stock AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN "+itemcodes
									+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock >0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";*/
							sql = "select kitchen_id,dinner_stock AS stock "
									+" from vw_active_kitchen_items "
									+" where item_code IN "+itemcodes
									+" and serving_areas LIKE ? and dinner_stock > 0 "
									+" and is_active='Y'";
						}else{
							/*sql = "select  fki.kitchen_id,fki.dinner_stock_tomorrow AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN "+itemcodes
									+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock_tomorrow >0 "
									+ " and fk.is_active='Y' and fki.is_active_tomorrow='Y'";*/
							sql = "select kitchen_id,dinner_stock_tomorrow AS stock "
									+" from vw_active_kitchen_items "
									+" where item_code IN "+itemcodes
									+" and serving_areas LIKE ? and dinner_stock_tomorrow > 0 "
									+" and is_active_tomorrow='Y'";
						}
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, "%"+area+"%");
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
						     kitchenFoundFromPincode = true;
							 int kid = resultSet.getInt("kitchen_id");
							 int stock = resultSet.getInt("stock");
							//Check if kitchen having stock but having bikers or not?
							 if(isKitchenHavingFreeBikers(kid, mealTypePojo)){
								 kitchenIds.add(kid);
								 kitchenStockMap.put(kid, stock);
							 }
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
				

				/**
				 * Separating free biker kitchens from sorted kitchen ids
				 */
				ArrayList<Integer> freeBikerKitchenIds = new ArrayList<Integer>();
				int totalAvailableStock = getTotalAvailableStock(itemcodes,mealTypePojo,totalOrderedItems);
				
				for(Integer sortedIds : sortedKitchenIds){
					if(SameUserPlaceOrder.isKitchenCapable(sortedIds, mealTypePojo, totalOrderedQuantity, itemcodes)){
						if(isKitchenHavingFreeBikers(sortedIds, mealTypePojo)){
							freeBikerKitchenIds.add(sortedIds);
						}
					}
				}
				System.out.println("After first calculation freeBikerKitchenIds:: "+freeBikerKitchenIds);
				boolean sameCuisineSpilt = false;
				System.out.println("TOATL AVAIL STOCK::::"+totalAvailableStock);
				//if all kitchens not capable
				if(freeBikerKitchenIds.size()==0){
					if(isAllKitchenServable(sorted_map, totalAvailableStock)){
						sameCuisineSpilt = true;
						freeBikerKitchenIds.addAll(sortedKitchenIds);
					}
				}
				System.out.println("freeBikerKitchenIds:: "+freeBikerKitchenIds);
				System.out.println("IS SAME CUISINE SPILT :: "+sameCuisineSpilt);
				
				if(freeBikerKitchenIds.size()>1 && !sameCuisineSpilt){
					//Do round robin logic
					System.out.println("Only single kitchen do round robin. . . .");
					
					int tot=0;
					for(Integer kitchenId : freeBikerKitchenIds){
						if( RoundRobin.alreadyOrdered(kitchenId,2)){
							tot++;
							System.out.println("Already ordered!");
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
							selectedKitchenIds.add(kitchenId);
							System.out.println("else part: "+selectedKitchenIds);
							break;
						}		
					}
					if(tot == freeBikerKitchenIds.size()){
						System.out.println("All are ordered!");
						RoundRobin.makeAllFree(freeBikerKitchenIds,2);
						for(Integer kitchenId : freeBikerKitchenIds){
							if( RoundRobin.alreadyOrdered(kitchenId,2)){
								tot++;
								System.out.println("Tot Already ordered!");
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
								selectedKitchenIds.add(kitchenId);
								System.out.println("Tot else part: "+selectedKitchenIds);
								break;
							}
						}
					}
				}else{
					//assign the kitchen
					selectedKitchenIds.addAll(freeBikerKitchenIds);
				}
				
				
				/*int tot=0;
				for(Integer kitchenId : sortedKitchenIds){
					if(isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
						if( RoundRobin.alreadyOrdered(kitchenId,2)){
							tot++;
							System.out.println("Already ordered!");
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
							selectedKitchenIds.add(kitchenId);
							System.out.println("Else ni::"+selectedKitchenIds);
							break;
						}
						break;
					}
					if( RoundRobin.alreadyOrdered(kitchenId,2)){
						tot++;
					}else{
						RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
						selectedKitchenIds.add(kitchenId);
						break;
					}
				}
				if(tot == sortedKitchenIds.size()){
					System.out.println("All are ordered!");
					RoundRobin.makeAllFree(sortedKitchenIds,2);
					for(Integer kitchenId : sortedKitchenIds){
						if(isKitchenHavingFreeBikers(kitchenId, mealTypePojo)){
							if( RoundRobin.alreadyOrdered(kitchenId,2)){
								tot++;
								System.out.println("TOT ni Already ordered!");
							}else{
								RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
								selectedKitchenIds.add(kitchenId);
								System.out.println("Tot Else ni::"+selectedKitchenIds);
								break;
							}
							break;
						}
						if( RoundRobin.alreadyOrdered(kitchenId,2)){
							tot++;
						}else{
							RoundRobin.updateCurrentAndFutureStatus(kitchenId,2);
							selectedKitchenIds.add(kitchenId);
							break;
						}
					}
				}*/
				/*for(int i=0;i<orderList.size();i++)
					dealingIds.addAll(selectedKitchenIds);*/
					dealingIds.addAll(selectedKitchenIds);
					System.out.println("*********************************************");
					System.out.println("* * * NI KITCHEN RETURNED IDS * * * *"+dealingIds);
					System.out.println("*********************************************");
				    
				return dealingIds;
			}else{
				System.out.println("* * * NO ANY NI KITCHEN IDS * * * *"+dealingIds);
				return dealingIds;
			}	
	}
	
	
	public static ArrayList<Integer> findDealingKitchenIds(ArrayList<OrderItems> orderList , String pincode,
			String mealType, String deliveryDay, String area ){
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		ArrayList<OrderItems> niCuisineItemList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> bengCuisineItemList = new ArrayList<OrderItems>();
		ArrayList<KitchenStock> kitchenItemStockList = new ArrayList<KitchenStock>();
		Set<Integer> kitchenIds = new HashSet<Integer>();
		int kitchenItemStock = 0;
		ArrayList<Integer> freeBikerKitchenIds = new ArrayList<Integer>();
		
		MealTypePojo mealTypePojo = new MealTypePojo();
		if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealTypePojo.setLunchToday(true);
		}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealTypePojo.setDinnerToday(true);
		}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealTypePojo.setLunchTomorrow(true);
		}else{
			mealTypePojo.setDinnerTomorrow(true);
		}
		/**************** SEPARATING BENGALI AND NI ITEMS ************************/
		for(int i=0;i<orderList.size();i++){
			if(orderList.get(i).cuisineId==2){
				niCuisineItemList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, 
						orderList.get(i).itemCode,orderList.get(i).quantity, orderList.get(i).price) );
			}
			if(orderList.get(i).cuisineId==1){
				bengCuisineItemList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, 
						orderList.get(i).itemCode,orderList.get(i).quantity, orderList.get(i).price) );
			}
		}
		
		if(orderList.size() == niCuisineItemList.size()){
			System.out.println("****** ONLY NI ITEMS ************");
		}else if(orderList.size() == bengCuisineItemList.size()){
			System.out.println("****** ONLY BENGALI ITEMS ************");
			for(OrderItems orderItems : bengCuisineItemList){
				/********** GET kitchen id and stock of the item code **************/
				kitchenItemStockList = OrderDAO.getKitchenItemStock(orderItems.itemCode, deliveryDay, mealType, area);
				for(KitchenStock kitchenItem : kitchenItemStockList){
					if(kitchenItem.stock >= orderItems.quantity){
						kitchenIds.add(kitchenItem.kitchenId);
						break;
					}else{
						kitchenItemStock += kitchenItem.stock ;
					}
				}
				if(kitchenItemStock ==  orderItems.quantity){
					for(KitchenStock kitchen : kitchenItemStockList){
						kitchenIds.add(kitchen.kitchenId);
					}
				}
			}
		}else{
			System.out.println("****** ALL ITEMS ************");
		}
		for(Integer sortedIds : kitchenIds){
			if(isKitchenHavingFreeBikers(sortedIds, mealTypePojo)){
					freeBikerKitchenIds.add(sortedIds);	
			}
		}
		
		
		
		return dealingKitchenIds;
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
	
	public static boolean isKitchenHavingFreeBikers(int kitchenId, MealTypePojo mealTypePojo){
		boolean isFreeBikerAvailable = false;
		int totalNoOfBikers=0,noFreeBikerSlot=0;
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
				Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select delivery_boy_user_id"
							+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							bikerList.add(resultSet.getString("delivery_boy_user_id"));
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
				
			/*OLd code
			 * for(String biker : bikerList){
				if(totalFreeSlots(biker,mealTypePojo) > 0 ){
					isFreeBikerAvailable = true;
				}
			}*/
			
			for(String bikerUserId : bikerList){
				totalNoOfBikers ++;
				int totalFreeSlotsForBiker = totalFreeSlots(bikerUserId, mealTypePojo);
				if(totalFreeSlotsForBiker == 0){
					noFreeBikerSlot++;
				}
			}
			if(totalNoOfBikers == noFreeBikerSlot){
				isFreeBikerAvailable = false;
			}else{
				isFreeBikerAvailable = true;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isFreeBikerAvailable;
	}
	
	public static int totalFreeSlots(String bikerUserId, MealTypePojo mealTypePojo ){
		int noOfFreeSlots = 0 ;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		try {
			SQL:{
			
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						/*sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id <4 "
								+" and (quantity<10 or no_of_orders <2)" ; */
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" vw_driver_today_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?)" ; 
					}else if(mealTypePojo.isLunchTomorrow()){
						/*sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id <4 "
								+" and (quantity<10 or no_of_orders <2)" ;*/ 
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" vw_driver_tomorrow_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N'  and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?)" ;
					}else if(mealTypePojo.isDinnerToday()){
						/*sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id > 3 "
								+" and (quantity<10 or no_of_orders <2)" ; */
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" vw_driver_today_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='N' "
								+" and (quantity<? or no_of_orders <?)" ; 
					}else{
						/*sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and time_slot_id > 3 "
								+" and (quantity<10 or no_of_orders <2)" ;*/
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" vw_driver_tomorrow_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='N' "
								+" and (quantity<? or no_of_orders <?)" ;
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next())
								noOfFreeSlots = resultSet.getInt("no_of_free_slots");
					} catch (Exception e) {
						// TODO: handle exception
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
		return noOfFreeSlots ;
	}
	
	public static boolean isKitchenServingItem(String itemCode, int kitchenId){
		boolean isServing = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select count(kitchen_id) AS kitchen "
							+ " from fapp_kitchen_items where item_code = ? and kitchen_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						preparedStatement.setInt(2, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int count = resultSet.getInt("kitchen");
							if(count > 0){
								isServing = true;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isServing;
	}
	
	public static int getTotalAvailableStock(String orderItemCodes, MealTypePojo mealTypePojo,int totalOrderedQuantity){
		int totalAvailableStock = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						/*sql = "select sum(stock)As stock from fapp_kitchen_items "
								+" where is_active='Y' and item_code IN "+orderItemCodes;*/
						sql = "select sum(stock)As stock from vw_active_kitchen_items "
								+" where is_active='Y' and item_code IN "+orderItemCodes;
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select sum(stock_tomorrow)As stock from vw_active_kitchen_items "
								+" where is_active_tomorrow='Y' and item_code IN "+orderItemCodes;
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select sum(dinner_stock)As stock from vw_active_kitchen_items "
								+" where is_active='Y' and item_code IN "+orderItemCodes;
					}else{
						sql = "select sum(dinner_stock_tomorrow)As stock from vw_active_kitchen_items "
								+" where is_active_tomorrow='Y' and item_code IN "+orderItemCodes;
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							totalAvailableStock = resultSet.getInt("stock");
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
		
		return (totalAvailableStock/totalOrderedQuantity);
	}
	
	public static boolean isAllKitchenServable(TreeMap<Integer, Integer> sorted_map,int totalStock){
		boolean isAllServable = false;
		int currValStock = 0;
		for(Entry<Integer, Integer> mp: sorted_map.entrySet() ){
			currValStock += mp.getValue();
		}
		if(totalStock==currValStock){
			isAllServable = true;
		}else{
			isAllServable = false;
		}
		System.out.println("isAllKitchenServable :: "+isAllServable);
		return isAllServable;
	}
	
	public static int getCurrentKitchenStock(int kitchenID, MealTypePojo mealTypePojo){
		int currentStock = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet= null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select distinct(stock)As stock from fapp_kitchen_items "
							+ " where kitchen_id = ? ";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select distinct(stock_tomorrow)As stock from fapp_kitchen_items "
								+ " where kitchen_id = ? ";
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select distinct(dinner_stock)As stock from fapp_kitchen_items "
							+ " where kitchen_id = ? ";
					}else{
						sql = "select distinct(dinner_stock_tomorrow)As stock from fapp_kitchen_items "
							+ " where kitchen_id = ? ";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenID);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							currentStock = resultSet.getInt("stock");
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
		return currentStock;
	}
}
