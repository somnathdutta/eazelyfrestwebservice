package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import utility.CamelCase;

import com.mkyong.rest.AddressBean;
import com.mkyong.rest.DBConnection;

public class AddressDAO {

	public static JSONObject saveAddress(String addressType , String mailId, String flatNo,
	    	String streetName, String landMark,String city,String location,
	    	String pincode,String contactName,String contactNumber,String user,
	    	String deliveryZone,String deliveryAddress,String instruction) throws JSONException{
	    	
	    	System.out.println("city - "+city+" location-"+location);
	    	Boolean inserted = false;
	    	JSONObject saveAddress =  new JSONObject();
	    	//contactName = getUserName(user);
	    	if(isAddressTypeExist(addressType, user , contactNumber)){
	    		System.out.println("If address type exists update the address !!!");
	    		try {
	    			SQL:{
	        				Connection connection = DBConnection.createConnection();
	        				PreparedStatement preparedStatement = null;
	        				String sql = "UPDATE fapp_address_details SET mail_id=? ,  city=? , location = ?, pincode=? ,contact_name = ?,"
	        						  + " delivery_zone = ?,delivery_address=?,instruction = ? "
	     						      + "  WHERE login_mobile_no = ? and LOWER(address_type) = LOWER(?)" ; 
	        					   
	        				try {
	    						preparedStatement = connection.prepareStatement(sql);
	    						if(mailId!=null){
	    							preparedStatement.setString(1, mailId);
	    						}else{
	    							preparedStatement.setNull(1, Types.NULL);
	    						}
	    						if(city!=null){
	    							preparedStatement.setString(2, CamelCase.toCamelCase(city) );
	    						}else{
	    							preparedStatement.setNull(2, Types.NULL);
	    						}
	    						if(location!=null){
	    							preparedStatement.setString(3, CamelCase.toCamelCase(location) );
	    						}else{
	    							preparedStatement.setNull(3, Types.NULL);
	    						}
	    						if(pincode!=null){
	    							preparedStatement.setString(4, pincode);
	    						}else{
	    							preparedStatement.setNull(4, Types.NULL);
	    						}
	    						if(contactName!=null){
	    							preparedStatement.setString(5, contactName);
	    						}else{
	    							preparedStatement.setNull(5, Types.NULL);
	    						}
	    						if(deliveryZone!=null){
	    							preparedStatement.setString(6, CamelCase.toCamelCase(deliveryZone.trim()) );
	    						}else{
	    							preparedStatement.setNull(6, Types.NULL);
	    						}
	    						if(deliveryAddress!=null){
	    							preparedStatement.setString(7, deliveryAddress.trim());
	    						}else{
	    							preparedStatement.setNull(7, Types.NULL);
	    						}
	    						if(instruction!=null){
	    							preparedStatement.setString(8, instruction.trim());
	    						}else{
	    							preparedStatement.setNull(8, Types.NULL);
	    						}
	    						preparedStatement.setString(9, user);
	    						
	    						preparedStatement.setString(10, addressType.trim() );
	    						System.out.println(preparedStatement);
	    						int count  = preparedStatement.executeUpdate();
	    						if(count>0){
	    							inserted = true;
	    							System.out.println("adress updated");
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
	    		
	    		if(inserted){
	        		saveAddress.put("status", inserted);
	        	}else{
	        		saveAddress.put("status", inserted);
	        	}
	    		
	    		
	    /******************************** UPDATE address done here ******************************************************/		
	    	}else{
	    		System.out.println("If address type does not exists insert the address !!!");
	    		if(user.length()!=0){
		    		try {
		    			SQL:{
		        				Connection connection = DBConnection.createConnection();
		        				PreparedStatement preparedStatement = null;
		        					String sql = "INSERT INTO fapp_address_details(address_type, mail_id,city,location,pincode,"
		        						+ "contact_name,login_mobile_no,"
		        						   + " delivery_zone,delivery_address,instruction )VALUES "
		        						   + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		        				try {
		    						preparedStatement = connection.prepareStatement(sql);
		    						if(addressType!=null){
		    							preparedStatement.setString(1, addressType.toUpperCase().trim());
		    						}else{
		    							preparedStatement.setNull(1, Types.NULL);
		    						}
		    						if(mailId!=null){
		    							preparedStatement.setString(2, mailId);
		    						}else{
		    							preparedStatement.setNull(2, Types.NULL);
		    						}
		    						if(city!=null){
		    							preparedStatement.setString(3, city);
		    						}else{
		    							preparedStatement.setNull(3, Types.NULL);
		    						}
		    						if(location!=null){
		    							preparedStatement.setString(4, location);
		    						}else{
		    							preparedStatement.setNull(4, Types.NULL);
		    						}
		    						if(pincode!=null){
		    							preparedStatement.setString(5, pincode);
		    						}else{
		    							preparedStatement.setNull(5, Types.NULL);
		    						}
		    						if(contactName!=null){
		    							preparedStatement.setString(6, contactName);
		    						}else{
		    							preparedStatement.setNull(6, Types.NULL);
		    						}
		    						preparedStatement.setString(7, user);
		    						
		    						if(deliveryZone!=null){
		    							preparedStatement.setString(8, deliveryZone.trim());
		    						}else{
		    							preparedStatement.setNull(8, Types.NULL);
		    						}
		    						if(deliveryAddress!=null){
		    							preparedStatement.setString(9, deliveryAddress.trim());
		    						}else{
		    							preparedStatement.setNull(9, Types.NULL);
		    						}
		    						if(instruction!=null){
		    							preparedStatement.setString(10, instruction.trim());
		    						}else{
		    							preparedStatement.setNull(10, Types.NULL);
		    						}
		    						int count  = preparedStatement.executeUpdate();
		    						if(count>0){
		    							inserted = true;
		    							System.out.println("adress inserted");
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
		    		
		    		if(inserted){
		        		saveAddress.put("status", inserted);
		            	
		        	}else{
		        		saveAddress.put("status", inserted);
		            	
		        	}
		    /******************************** INSERT as NEW address ******************************************************/		
	    	}
	    	}
	    	
	    	System.out.println("Outside all blocks inserted =="+inserted);
	    	
	    	return saveAddress;
	    }
	
	
	private static Boolean isAddressTypeExist(String useraddressType , String user ,String mobileNo){
    	Boolean isexists = false;
    	try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT address_type from fapp_address_details where login_mobile_no = ? and UPPER(address_type) = UPPER(?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, user);
						preparedStatement.setString(2, useraddressType);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							isexists = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
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
    	System.out.println("Address type exists-"+isexists);
    	return isexists;
    }
	
	
	public static void updateMyEmailID(String email, String user){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_accounts set email = ? where mobile_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, email);
						preparedStatement.setString(2, user);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							System.out.println("Email"+email+" updated in main table for "+user+" !");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
	}
	
	public static Boolean isAddressExists( String user ){
    	Boolean isexists = false;
    	try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT count(address_type) AS total from fapp_address_details where login_mobile_no = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, user);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							int total = resultSet.getInt("total");
							if(total > 0){
								isexists = true;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
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
    	System.out.println("Address exists-"+isexists);
    	return isexists;
    }
	
	public static JSONObject fetchalladdresstype(String mobNo) throws JSONException{
    	JSONObject fetchalladdresstypeObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	try { 
    			Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT * FROM fapp_address_details WHERE login_mobile_no = ? order by address_type_id";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject temp = new JSONObject();
							//temp.put("name", resultSet.getString("contact_name")) ;
							temp.put("addresstype", resultSet.getString("address_type"));
							//temp.put("flatno", resultSet.getString("flat_no"));
							//temp.put("streetname", resultSet.getString("street_name"));
							//temp.put("landmark", resultSet.getString("landmark"));
							if(resultSet.getString("mail_id")!=null){
								temp.put("email", resultSet.getString("mail_id"));
							}else{
								temp.put("email", "");
							}
							if(resultSet.getString("city")!=null){
								temp.put("city", resultSet.getString("city"));
							}else{
								temp.put("city", " ");
							}
							if(resultSet.getString("location")!=null){
								temp.put("location", resultSet.getString("location"));
							}else{
								temp.put("location", " ");
							}
							if( resultSet.getString("pincode")!=null){
								temp.put("pincode", resultSet.getString("pincode"));
							}else{
								temp.put("pincode"," ");
							}
							
							if(resultSet.getString("delivery_zone")!=null){
								temp.put("deliveryzone", resultSet.getString("delivery_zone"));
							}else{
								temp.put("deliveryzone", " ");
							}
							if(resultSet.getString("delivery_address")!=null){
								temp.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								temp.put("deliveryaddress", " ");
							}
							if(resultSet.getString("instruction")!=null){
								temp.put("instruction", resultSet.getString("instruction"));
							}else{
								temp.put("instruction", " ");
							}
							
							jsonArray.put(temp);
						}
					} catch (Exception e) {
						// TODO: handle exception
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
		fetchalladdresstypeObject.put("status", "200");
		fetchalladdresstypeObject.put("message", "Address fetched successfully");
    	fetchalladdresstypeObject.put("addresstypelist", jsonArray);
    	return fetchalladdresstypeObject;
    	   	
    }
}
