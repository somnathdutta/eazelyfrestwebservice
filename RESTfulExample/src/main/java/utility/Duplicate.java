package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class Duplicate {

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
	
	public static ArrayList<Integer> getKitchenId(ArrayList<OrderItems> orderList , String pincode ){
		ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> kitchenMaxFreq = new HashMap<Integer, Integer>();
		ArrayList<Integer> kitchenIds = new ArrayList<Integer>();
		boolean kitchenFoundFromPincode = false;
		ArrayList<Integer> niKitchenidList = new ArrayList<Integer>();
		ArrayList<OrderItems> niCuisineIdList = new ArrayList<OrderItems>();
		System.out.println("Total item ordered: "+orderList.size());
		for(int i=0;i<orderList.size();i++){
			if(orderList.get(i).cuisineId==2){
				niCuisineIdList.add(new OrderItems(orderList.get(i).cuisineId, orderList.get(i).categoryId, 
						orderList.get(i).itemCode,orderList.get(i).quantity, orderList.get(i).price) );
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
									+" and fk.serving_zipcodes LIKE ? and fki.stock <>0 "
									+ " and fk.is_active='Y' and fki.is_active='Y'";
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
					
					int maxCapa = Collections.max(maxCapaMap.values());
					
					ArrayList<Integer> selectedkitchens = new ArrayList<Integer>();
					System.out.println("Maxcapacity : "+maxCapa);
					for(Entry<Integer, Integer> mp: maxCapaMap.entrySet() ){
						if(mp.getValue()==maxCapa){
							selectedkitchens.add(mp.getKey());
						}
					}
					System.out.println(" Selected kitchen id for max Capacity : "+selectedkitchens+" ");
					
					if(selectedkitchens.size()>1){
						System.out.println("More than 1 kitchen having same stock..Kitchen Find randomly");
						int pick =0;
						Random rand = new Random();      
						for (int j = 0; j<selectedkitchens.size(); j++){
							pick = rand.nextInt(selectedkitchens.size());       
						}
						System.out.println("Generated kitchen ID: " + selectedkitchens.get(pick));
						//dealingKitchenIds.add(selectedkitchens.get(pick));
						
						for(int i =0;i<max;i++){
							dealingKitchenIds.add(selectedkitchens.get(pick));
						}
						if(niKitchenidList.size()>0)
							dealingKitchenIds.addAll(niKitchenidList);
						System.out.println("* * * BENGALI KITCHEN IDS ON MORE MAX CAPACITY * * * *"+dealingKitchenIds);
						return dealingKitchenIds;
					}else{
						System.out.println("Only one kitchen having max capacity...");
						//dealingKitchenIds = selectedkitchens;
						
						for(int i =0;i<max;i++){
							dealingKitchenIds.add(selectedkitchens.get(0));
						}
						if(niKitchenidList.size()>0)
							dealingKitchenIds.addAll(niKitchenidList);
						System.out.println("* * * BENGALI KITCHEN IDS ON SINGLE MAX CAPACITY * * * *"+dealingKitchenIds);
						return dealingKitchenIds;
					}
					
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
		if(orderList.size()==1){
			if( Integer.valueOf(orderList.get(0).itemCode)>=28 && Integer.valueOf(orderList.get(0).itemCode)<=43 
					&& orderList.get(0).quantity<=25 && orderList.get(0).quantity<=35){
				System.out.println("FB");
				dealingIds.add(53);
			}
			if(orderList.get(0).quantity>25 && orderList.get(0).quantity<35){
				System.out.println("Qty FB");
				dealingIds.add(53);
			}
			System.out.println("* * * * NI KITCHEN IDS FOR SINGLE ORDER * * * * : "+dealingIds);
			return dealingIds;
		}else{
		
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
								+" and fk.serving_zipcodes LIKE ? and fki.stock <>0"
								+" and fk.is_active='Y' and fki.is_active='Y'";
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
				
				int maxCapa = Collections.max(maxCapaMap.values());
				ArrayList<Integer> selectedkitchens = new ArrayList<Integer>();
				System.out.println("Maxcap : "+maxCapa);
				for(Entry<Integer, Integer> mp: maxCapaMap.entrySet() ){
					if(mp.getValue()==maxCapa){
						selectedkitchens.add(mp.getKey());
					}
				}
				
				if(selectedkitchens.size()>1){
					System.out.println("More than 1 kitchen having same stock..Kitchen Find randomly");
					int pick =0;
					Random rand = new Random();      
					for (int j = 0; j<selectedkitchens.size(); j++){
						pick = rand.nextInt(selectedkitchens.size());       
					}
					System.out.println("Generated kitchen ID: " + selectedkitchens.get(pick));
					//ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
					dealingIds.add(selectedkitchens.get(pick));
					for(int i =0;i<max;i++){
						dealingIds.add(selectedkitchens.get(0));
					}
					System.out.println("* * * * * NI KITCHEN IDS ON SAME CAPACITY * * * * * *: "+dealingIds);
					return dealingIds;
				}else{
					System.out.println("Only one kitchen having max capacity...");
					for(int i =0;i<max;i++){
						dealingIds.add(selectedkitchens.get(0));
					}
					System.out.println("* * * * * NI KITCHEN IDS ON MAX CAPACITY * * * * * * *: "+dealingIds);
					return dealingIds;
				}
				
			}else{
				System.out.println("* * * NO ANY NI KITCHEN IDS * * * *"+dealingIds);
				return dealingIds;
			}
			
		}	
	}
}
