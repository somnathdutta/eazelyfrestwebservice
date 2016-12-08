package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderTimingValidationDAO {

	public static boolean isOrderTimingValid(String mealType, String deliveryDay) throws ParseException{
		boolean isValidOrderTimings = false;
		String[] slotTimings = new String[4];
    	
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String currentTime = sdf.format(date);
		System.out.println("CURRENT TIME: "+currentTime);
		slotTimings = SlotDAO.getOrderTimings();
		String lunchInitialTimings = slotTimings[0];
		System.out.println("Lunch start time: "+lunchInitialTimings);
		String lunchFinalTimings = slotTimings[1];
		System.out.println("Lunch final time: "+lunchFinalTimings);
		String dinnerInitialTimings = slotTimings[2];
		System.out.println("Dinner start time: "+dinnerInitialTimings);
		String dinnerFinalTimings = slotTimings[3];
		System.out.println("Dinner end time: "+dinnerFinalTimings);
		
		if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			if(OrderTimeDAO.isOrderTimeBetweenKitchenHours(lunchInitialTimings, lunchFinalTimings, currentTime)  ){
				System.out.println("--------------------------------------------");
				System.out.println("--- Order lunch time: "+currentTime);
				System.out.println("--------------------------------------------");
				isValidOrderTimings = true;
			}
		}
		
		if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
			if(OrderTimeDAO.isOrderTimeBetweenKitchenHours(dinnerInitialTimings, dinnerFinalTimings, currentTime)  ){
				System.out.println("--------------------------------------------");
				System.out.println("-- Order dinner time: "+currentTime);
				System.out.println("--------------------------------------------");
				isValidOrderTimings = true;
			}
		}
		
		if(deliveryDay.equalsIgnoreCase("TOMORROW")){
			isValidOrderTimings = true;
		}
		
		return isValidOrderTimings;
	}
}
