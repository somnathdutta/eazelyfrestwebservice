package com.mkyong;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AddressAPI {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String address = "Pecon tower,Street no 0315,Newtown, West Bengal 700135, India";
		 int responseCode = 0;
		 String YOUR_API_KEY = "AIzaSyAanDNwibbSYrhBbVA_jcuxaQYAqoTElpg";
		String api = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=YOUR_API_KEY";
	      URL url = new URL(api);
	      HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
	      httpConnection.connect();
	      responseCode = httpConnection.getResponseCode();
	      if(responseCode == 200)
	      {
	    	  System.out.println("OK");
	      }else{
	    	  System.out.println("NOT OK");
	      }
	}

}
