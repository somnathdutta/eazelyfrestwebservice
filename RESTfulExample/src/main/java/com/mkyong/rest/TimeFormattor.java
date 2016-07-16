package com.mkyong.rest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormattor {

	public static String convert12To24Hour(String timeSlot) throws Exception{
		String time = null;
		   SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
	       SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
	       Date date = parseFormat.parse(timeSlot);
	      // System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
	       time = displayFormat.format(date);
	       System.out.println("24 hour format time:: "+time);
		return time;
	}
}
