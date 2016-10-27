package com.mkyong.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/myservice")
public class Myservice {
	
	@GET
	@Path("/printHello")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject myHello(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("message", "Hello NISTIANS");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

}
