package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pojo.KitchenStock;

public class Reasearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<KitchenStock> kitchenItemList = new ArrayList<KitchenStock>();
		kitchenItemList.add(new KitchenStock(45, 3));
		kitchenItemList.add(new KitchenStock(45, 7));
		kitchenItemList.add(new KitchenStock(46, 6));
		kitchenItemList.add(new KitchenStock(46, 5));
		
		ArrayList<KitchenStock> newKitchenStockList = findTotalItemsForStockUpdation(kitchenItemList);
		System.out.println(newKitchenStockList.toString());
	}
	
	public static ArrayList<KitchenStock> findTotalItemsForStockUpdation(ArrayList<KitchenStock> kitchenStockList){
		ArrayList<KitchenStock> returningKitchenItemList = new ArrayList<KitchenStock>();
		Map<Integer, Integer> kitchenStockMap = new HashMap<Integer, Integer>();
		
		for(KitchenStock kitchenStock : kitchenStockList){
			int kitchenId = kitchenStock.getKitchenId();
			if(kitchenStockMap.containsKey(kitchenId)){
				int stock = kitchenStockMap.get(kitchenId);
				kitchenStockMap.put(kitchenId, stock + kitchenStock.getStock());
			}else{
				kitchenStockMap.put(kitchenId, kitchenStock.getStock());
			}
		}
		
		for(Entry<Integer, Integer> kitchenMapEntry : kitchenStockMap.entrySet()){
			returningKitchenItemList.add(new KitchenStock(kitchenMapEntry.getKey(), kitchenMapEntry.getValue()));
		}
		return returningKitchenItemList;
	}

}
