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

import pojo.Biker;
import pojo.MealTypePojo;
import utility.ValueComparator;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class FindKitchensByRoundRobin {

	public static ArrayList<Integer> getKitchenId(ArrayList<OrderItems> orderList , String pincode,
			String mealType, String deliveryDay ){
		System.out.println("***************************************************************************");
		System.out.println(" APPLYING ROUND ROBIN ALGORITHM ******");
		System.out.println("***************************************************************************");
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> selectedKitchenIds = new ArrayList<Integer>();
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
		
		boolean kitchenFoundFromPincode = false;
		
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
		
		System.out.println("Total item ordered: "+orderList.size());
		
		for(int i=0 ; i < orderList.size() ; i++){
			System.out.print("CUID::"+orderList.get(i).cuisineId+"\t");
			System.out.print("CATID::"+orderList.get(i).categoryId+"\t");
			System.out.print("ITEM::"+orderList.get(i).itemCode+"\t");
    		System.out.print("QTY::"+orderList.get(i).quantity+"\n");
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
			System.out.println("--ORDER is going to split---");
			niKitchenidList = findKitchenIdsOfCuisine(niCuisineIdList, pincode, mealType , deliveryDay);
		}
	
		if(orderList.size() == niCuisineIdList.size()){
				System.out.println("- - - -  Only NI cuisines found on ordered items- - - ");
				dealingKitchenIds = findKitchenIdsOfCuisine(niCuisineIdList, pincode, mealType , deliveryDay);
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
							String sql = "";
							/*sql = "select  fki.kitchen_id,fki.stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.stock <>0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";*/
							if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
								sql = "select  fki.kitchen_id,fki.stock AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN ("+itemcodes+") "
										+" and fk.serving_zipcodes LIKE ? and fki.stock > 0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";
							}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
								sql = "select  fki.kitchen_id,fki.stock_tomorrow AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN ("+itemcodes+") "
										+" and fk.serving_zipcodes LIKE ? and fki.stock_tomorrow >0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";
							}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
								sql = "select  fki.kitchen_id,fki.dinner_stock AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN ("+itemcodes+") "
										+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock >0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";
							}else{
								sql = "select  fki.kitchen_id,fki.dinner_stock_tomorrow AS stock "
										+" from fapp_kitchen_items fki "
										+" join fapp_kitchen fk on "
										+" fki.kitchen_id = fk.kitchen_id "
										+" where fki.item_code IN ("+itemcodes+") "
										+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock_tomorrow >0 "
										+ " and fk.is_active='Y' and fki.is_active='Y'";
							}
							
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
						sortedKitchenIds.add(mp.getKey());
					}
					System.out.println(":::Sorted kitchen id:: "+sortedKitchenIds);
					
					/**
					 * Separating free biker kitchens from sorted kitchen ids
					 */
					ArrayList<Integer> freeBikerKitchenIds = new ArrayList<Integer>();
					for(Integer sortedIds : sortedKitchenIds){
						if(isKitchenHavingFreeBikers(sortedIds, mealTypePojo)){
							freeBikerKitchenIds.add(sortedIds);
						}
					}
					
					if(freeBikerKitchenIds.size()>1){
						//Do round robin logic
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
					
					for(int i =0;i<bengCuisineIdList.size();i++){
						dealingKitchenIds.addAll(selectedKitchenIds);
					}
					if(niKitchenidList.size()>0){
						dealingKitchenIds.addAll(niKitchenidList);
					}
					System.out.println("Returning dealing kitchens with RR::"+dealingKitchenIds);
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
	
	public static ArrayList<Integer> findKitchenIdsOfCuisine(ArrayList<OrderItems> orderList,String pincode,
			String mealType,  String deliveryDay){
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
			String fb = a.replace("[", "");
			String itemcodes = fb.replace("]", "");
			
			ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
			
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
							sql = "select  fki.kitchen_id,fki.stock AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.stock > 0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";
						}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
							sql = "select  fki.kitchen_id,fki.stock_tomorrow AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.stock_tomorrow >0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";
						}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
							sql = "select  fki.kitchen_id,fki.dinner_stock AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock >0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";
						}else{
							sql = "select  fki.kitchen_id,fki.dinner_stock_tomorrow AS stock "
									+" from fapp_kitchen_items fki "
									+" join fapp_kitchen fk on "
									+" fki.kitchen_id = fk.kitchen_id "
									+" where fki.item_code IN ("+itemcodes+") "
									+" and fk.serving_zipcodes LIKE ? and fki.dinner_stock_tomorrow >0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";
						}
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
				

				/**
				 * Separating free biker kitchens from sorted kitchen ids
				 */
				ArrayList<Integer> freeBikerKitchenIds = new ArrayList<Integer>();
				for(Integer sortedIds : sortedKitchenIds){
					if(isKitchenHavingFreeBikers(sortedIds, mealTypePojo)){
						freeBikerKitchenIds.add(sortedIds);
					}
				}
				
				if(freeBikerKitchenIds.size()>1){
					//Do round robin logic
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
				for(int i=0;i<orderList.size();i++)
					dealingIds.addAll(selectedKitchenIds);
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
	
	public static boolean isKitchenHavingFreeBikers(int kitchenId, MealTypePojo mealTypePojo){
		boolean isFreeBikerAvailable = false;
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
				
			for(String biker : bikerList){
				if(totalFreeSlots(biker,mealTypePojo) > 0 ){
					isFreeBikerAvailable = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(isFreeBikerAvailable){
			System.out.println("Kitchen having free bikers!");
		}else{
			System.out.println("Kitchen not having free bikers!");
		}
		return isFreeBikerAvailable;
	}
	
	public static int totalFreeSlots(String bikerUserId, MealTypePojo mealTypePojo ){
		int noOfFreeSlots = 0 ;
		try {
			SQL:{
			
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' and is_slot_active='N' "
								+" and (quantity<10 or no_of_orders <3)" ; 
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' and is_slot_active='N'  "
								+" and (quantity<10 or no_of_orders <3)" ; 
					}else if(mealTypePojo.isDinnerToday()){
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and  is_lunch='N' and is_slot_active='N' "
								+" and (quantity<10 or no_of_orders <3)" ; 
					}else{
						sql = "select count(time_slot_id) "
								+" as no_of_free_slots from  "
								+" fapp_timeslot_driver_status_tommorrow " 
								+" where driver_user_id= ? "
								+" and is_slot_locked = 'N' and is_lunch='N' and is_slot_active='N' "
								+" and (quantity<10 or no_of_orders <3)" ;
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
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
		System.out.println("Free slots for "+bikerUserId+" is :: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
}

