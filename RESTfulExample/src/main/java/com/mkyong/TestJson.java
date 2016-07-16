package com.mkyong;

import org.codehaus.jettison.json.JSONObject;

public class TestJson {

	public static void main(String[] args)throws Exception {
		JSONObject my = new JSONObject();
		my.put("id", "1");
		my.put("name", "NIST");
		
		System.out.println(my);

	}

}
