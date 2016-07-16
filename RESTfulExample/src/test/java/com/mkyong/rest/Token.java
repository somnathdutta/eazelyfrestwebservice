package com.mkyong.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Token {

	public String[] getApiDetails(){
		System.out.println("Get api details calling . . . ");
		String[] apiDetails = new String[2];
		try {
			SQL:{
					Connection connection =DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT client_id , client_secret from fapp_api_details";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							apiDetails[0] = resultSet.getString("client_id") ; 
							apiDetails[1] = resultSet.getString("client_secret");
						}
					} catch (Exception e) {
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
		System.out.println("API DETILS->"+apiDetails[0]+" "+apiDetails[1]);
		return apiDetails;
	}
	
	public String generateAuthToken(String clientId, String clientSecret){
		String authToken = "";
		  try {
			  URL url = new URL(" https://apitest.roadrunnr.in/oauth/token?grant_type=client_credentials&client_"
				 +"id="+clientId+"&client_secret="+clientSecret);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			String output ;
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				JSONObject json = (JSONObject)new JSONParser().parse(output);
				String token = (String) json.get("access_token");
				authToken = "Token "+token ; 
			}
		   conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {
			e.printStackTrace();
		  } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		  System.out.println("Token ->"+authToken);
		return authToken;
	}
	
	public String generateTrackLink(String orderid){
		System.out.println("Track link calling . . .");
		org.codehaus.jettison.json.JSONObject myjson = new org.codehaus.jettison.json.JSONObject();
		String[] idData = getApiDetails();
		try {
		 URL url = new URL(" https://apitest.roadrunnr.in/v1/orders/d08f5fa8/track");
		 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
	 	conn.setRequestProperty("Authorization", generateAuthToken(idData[0], idData[1]));
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		String output ;
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			myjson = new org.codehaus.jettison.json.JSONObject(output);
		}
	   conn.disconnect();
	  } catch (MalformedURLException e) {
		e.printStackTrace();
	  } catch (IOException e) {
		e.printStackTrace();
	  } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	  String link =null;
	try {
		link = myjson.getString("tracking_link");
	} catch (org.codehaus.jettison.json.JSONException e) {
		e.printStackTrace();
	}
	  System.out.println("Track here->"+link);
	  return link;
	}
}
