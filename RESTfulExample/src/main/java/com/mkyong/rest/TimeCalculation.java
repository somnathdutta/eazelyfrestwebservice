package com.mkyong.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeCalculation {

	public static String getNewTime(String orderTime, String leadTime){
		 /*orderTime="0:01:30";
		leadTime="0:01:35";
*/
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		Date date1 = null,date2 = null;
		try {
			date1 = timeFormat.parse(orderTime);
			date2 = timeFormat.parse(leadTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		long sum = date1.getTime() + date2.getTime();

		String date3 = timeFormat.format(new Date(sum));
		System.out.println("The new time is "+date3);
		return date3;
	}
}
