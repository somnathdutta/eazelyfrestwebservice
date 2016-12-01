package com.appsquad.finder;

import java.util.ArrayList;

public class RRKitchen {

	public static int getRRSingleKitchen(ArrayList<Integer> selectedKitchenIds,int kitchenType){
		int tot=0, kitchenId = 0;
		for(Integer kitchen : selectedKitchenIds){
			if( RoundRobin.alreadyOrdered(kitchen, kitchenType)){
				tot++;
				System.out.println(kitchen+" Already ordered!");
			}else{
				RoundRobin.updateCurrentAndFutureStatus(kitchen, kitchenType);
				//selectedKitchenIds.add(kitchen);
				kitchenId = kitchen;
				break;
			}		
		}
		
		if(tot == selectedKitchenIds.size()){
				System.out.println("All are ordered!");
				RoundRobin.makeAllFree(selectedKitchenIds,kitchenType);
				for(Integer kitchenid : selectedKitchenIds){
					if( RoundRobin.alreadyOrdered(kitchenid,kitchenType)){
						tot++;
						System.out.println(kitchenId+" Tot Already ordered!");
					}else{
						RoundRobin.updateCurrentAndFutureStatus(kitchenid,kitchenType);
						//selectedKitchenIds.add(kitchenid);
						kitchenId = kitchenid;
						System.out.println("tot == selectedKitchenIds.size() Tot else part: "+selectedKitchenIds);
						break;
					}
				}
			}
		
		System.out.println("RR KITCHEN :: "+kitchenId);
		return kitchenId;
	}
	
	
}
