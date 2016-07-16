package com.mkyong.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/json/login")
public class Login {
	
	/*@GET
	//@Path("/{param}")
	//@Path("/{param}")
	//users?name=Peter,Jack&client=Starbucks
	public Response getValidLoginMsg(@PathParam("param") String uname, @PathParam("param") String pswd) throws Exception {
 
		String output = "";
		
		Boolean chkValidUser = false;
		
		chkValidUser = DBConnection.checkLogin(uname, pswd);
		
		if(chkValidUser){
			output = "Login Sucessful!";
		}
		else{
			output = "Invalid Login!";
		}
 
		return Response.status(200).entity(output).build();
 
	}*/
	
	
}
