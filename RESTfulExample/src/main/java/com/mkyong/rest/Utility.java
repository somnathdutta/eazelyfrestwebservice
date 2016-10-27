package com.mkyong.rest;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
 
public class Utility {

    /**
     * Method to construct JSON
     * 
     * @param tag
     * @param status
     * @return
     */
    public static ArrayList<Object> constructJSON(String tag, ArrayList<String> categoryname) {
        
    	JSONObject obj = new JSONObject();
        ArrayList<Object> categoryNameObjectList = new ArrayList<Object>();
        try {
            obj.put("categoryName", new ArrayList<String>(categoryname));
            categoryNameObjectList.add(obj);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return categoryNameObjectList;
    }
	
    
    public static String constructJSON(String tag, boolean status) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("tag", tag);
            obj.put("status", new Boolean(status));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return obj.toString();
    }
    
    public static String constructJSON(String msg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Login status", msg);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return obj.toString();
    }
    
    public static String constructJSON(Boolean locationAvailabilitystatus) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Availability", locationAvailabilitystatus);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return obj.toString();
    }
 
    
}
