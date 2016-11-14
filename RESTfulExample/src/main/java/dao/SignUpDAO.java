package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

import pojo.User;

public class SignUpDAO {

	 public static JSONObject socialSignup(String name, String email,
	    		String contactNumber,String password,String referalCode) throws JSONException{
	    	JSONObject jsonObject = new JSONObject();
	    	Boolean insertStatus = false;
	    	String myReferalCode = null;
	    	boolean isRefCodeExists = false,isNewUserCreated=false;
	    	
	    //	if(!(getContactsFromDB().contains(contactNumber)) ){
	    	if(referalCode.trim().length()>0 ){
	    		System.out.println("Referral code given::"+referalCode);
	    		//When referral code given by user
	    		isRefCodeExists = DBConnection.isRefCodeExists(referalCode);
	    		if(isRefCodeExists){
	    			System.out.println("Referral code exists...");
	    			//If given referral code exists for somebody
	    			double myBalance = 50.0;
	    			User referalUser = new User(name, contactNumber, email, contactNumber, referalCode, myBalance);
	    			//just try to insert data in db for new user
	    			isNewUserCreated = DBConnection.doSignUpFor(referalUser);
	    			if(isNewUserCreated){
	    				System.out.println("New user created...and now update balance. . .");
	    				insertStatus = DBConnection.updateBalanceForReferredUserFrom(referalCode.trim());
	    				if(insertStatus){
	    					System.out.println("Referred user balance updated...");
	    					myReferalCode = DBConnection.generateReferalCode(name);
	    					System.out.println("New users code ::"+myReferalCode);
	    					DBConnection.updateMyCode(myReferalCode, contactNumber);
	    					jsonObject.put("status", true);
	            			jsonObject.put("message", "Thank you for registration!");
	    				}else{
	    					jsonObject.put("status", true);
	            			jsonObject.put("message", "User created but user balance updation failed!");
	    				}
	    			}else{
	        			if(isMobileNoRegistered(contactNumber)){
	        				jsonObject.put("status", true);
		        			jsonObject.put("message", "Logged in successfully!");
	        			}else{
	        				jsonObject.put("status", false);
		        			jsonObject.put("message", "The mobile number is already registered!");
	        			}
	        		}
	    		}else{
	    			jsonObject.put("status", false);
	    			jsonObject.put("message", "Referral code is Invalid");
	    		}
	    	}else{
	    		System.out.println("Referral code not given::"+referalCode);
	    		//when referral code not given by user, just try to insert data in db for new user
	    		User newUser = new User(name, contactNumber, email, contactNumber,referalCode,0.0);
	    		isNewUserCreated = DBConnection.doSignUpFor(newUser);
	    		if(isNewUserCreated){
	    			insertStatus = true;
	    			myReferalCode = DBConnection.generateReferalCode(name);
	    			DBConnection.updateMyCode(myReferalCode, contactNumber);
	    			jsonObject.put("status", true);
	    			jsonObject.put("message", "Thank you for registration!");
	    		}else{
	    			if(isMobileNoRegistered(contactNumber)){
        				jsonObject.put("status", true);
	        			jsonObject.put("message", "Logged in successfully!");
        			}else{
        				jsonObject.put("status", false);
	        			jsonObject.put("message", "The mobile number is already registered!");
        			}
	    		}
	    	}
	    	return jsonObject;
	    }
	 
	 public static boolean isMobileNoRegistered(String contactNumber){
		 boolean isMobileNoRegistered = false;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		ResultSet resultSet = null;
			 		String sql ="select password from fapp_accounts where mobile_no = ?";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, contactNumber);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							String password = resultSet.getString("password");
							if(password.equals(contactNumber)){
								isMobileNoRegistered = true;
							}else{
								isMobileNoRegistered = false;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(connection!=null){
							connection.close();
						}
					}
		 		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		 return isMobileNoRegistered;
	 }
}
