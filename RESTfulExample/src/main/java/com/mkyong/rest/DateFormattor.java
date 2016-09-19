package com.mkyong.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormattor {

	public static String toStringDate(String yyyyMMDD){
		String reformattedDate = "";
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
		
			try { 
			    reformattedDate = myFormat.format(fromUser.parse(yyyyMMDD));
			} catch (ParseException e) {
			    e.printStackTrace();
			}
			System.out.println("Order placed on : "+reformattedDate);
			return reformattedDate;
	}
}
