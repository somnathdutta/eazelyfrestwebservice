package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import pojo.KitchenStock;

public class Utility {
	
	public static ArrayList<KitchenStock> finalList = new ArrayList<KitchenStock>();
	
	public static ArrayList<KitchenStock> getKitchenStockList(ArrayList<KitchenStock> kitchenStockList){
		ArrayList<KitchenStock> finalList = new ArrayList<KitchenStock>();
		compareData(kitchenStockList);
		func1(kitchenStockList);
		finalList = Utility.finalList;
		for(KitchenStock finalQty : finalList ){
			System.out.println(" "+finalQty.kitchenId+"\t"+finalQty.stock);
		}
		return finalList;
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
		for(KitchenStock finalQty : returningKitchenItemList ){
			System.out.println(" "+finalQty.kitchenId+"\t"+finalQty.stock);
		}
		return returningKitchenItemList;
	}
	
	public static void compareData(ArrayList<KitchenStock> kitchenStockList){
		Collections.sort(kitchenStockList	, new Comparator<KitchenStock>() {

			@Override
			public int compare(KitchenStock o1, KitchenStock o2) {
				// TODO Auto-generated method stub
				return o1.kitchenId.compareTo(o2.kitchenId);
			}
		});
	}
	
	public static void func1(ArrayList<KitchenStock> kitchenStockList){
		ArrayList<KitchenStock> newkitchenStockList = new ArrayList<KitchenStock>();
		ListIterator<KitchenStock> kitchenStockListIterator = kitchenStockList.listIterator();
		while (kitchenStockListIterator.hasNext()) {
			KitchenStock kitchenStock = kitchenStockListIterator.next();
			if(kitchenStockListIterator.hasNext()){
				KitchenStock kitchenStock2 = kitchenStockListIterator.next();
				if(kitchenStock.kitchenId.equals(kitchenStock2.kitchenId)){
					newkitchenStockList.add(kitchenStock);
					kitchenStockListIterator.previous();
				}else{
					newkitchenStockList.add(kitchenStock);
					func2(newkitchenStockList, kitchenStock);
					kitchenStockListIterator.previous();
					newkitchenStockList = new ArrayList<KitchenStock>();
				}
			}else{
				newkitchenStockList.add(kitchenStock);
				func2(newkitchenStockList, kitchenStock);
			}
		}
	}

	public static void func2(ArrayList<KitchenStock> kitchenStockList, KitchenStock kitchenStock){
		Integer quantity = 0;
		for(KitchenStock stock : kitchenStockList){
			quantity += stock.stock;
		}
		kitchenStock.stock = quantity;
		finalList.add(kitchenStock);
	}
	
}
