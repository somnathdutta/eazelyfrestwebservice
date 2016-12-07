package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.PromoCode;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class PromoCodeDAO {

	public static JSONObject isPromoCodeValid(String promoCode,ArrayList<OrderItems> orderItems, 
			String mobileNo, List<String> orderDetails) throws JSONException{
		JSONObject promoCodeValidJson = new JSONObject();
		String message = "";boolean isValid=false,isPromoCodeApplied = false,isValidPromoCode=false;
		int count = 0,totalQuantity = 0,promoTypeId = 0,promoCodeApplicationTypeId =0,volumeQuantity=0 ;
		double promoValue = 0, discountedValue =0, totalValue = 0,finalTotal = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select promo_code,promo_value,promo_type_id,promo_code_application_type_id,volume_quantity "
							+ "from vw_promo_code_details where promo_code_is_active='Y'"
							+ " and UPPER(promo_code) = UPPER(?) and current_date>=from_date AND current_date<=to_date";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, promoCode);
						//System.out.println("PROMO CODE VALIDATION CHECKING QRY -- >> " + preparedStatement);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							count++;
							isValidPromoCode = true;
							promoValue = resultSet.getDouble("promo_value");
							promoTypeId = resultSet.getInt("promo_type_id");
							volumeQuantity = resultSet.getInt("volume_quantity");		
							promoCodeApplicationTypeId = resultSet.getInt("promo_code_application_type_id");
						}
						///System.out.println("PROMO CODE VALUE -- >> " + promoValue );
						//System.out.println("PROMO TYPE ID -- >> " + promoTypeId);
						//System.out.println("PROMO CODE APPLICATON TYPE ID -- >> " + promoCodeApplicationTypeId);
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}if(connection!=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(isValidPromoCode){
			if(isPromoCodeApplied(promoCode, mobileNo,orderDetails)){
				isPromoCodeApplied = true;
			}
			for(OrderItems items : orderItems){
				finalTotal += (items.quantity * items.price);
				totalQuantity += items.quantity;
			}
			System.out.println("Final total :: "+finalTotal);
			System.out.println("Total quantity:: "+totalQuantity);
			if(promoTypeId==1){//FLAT
				
				if(!isPromoCodeApplied){
					discountedValue = promoValue;
					message = "Valid PromoCode";
					isValid = true;
					applyPromoCode(promoCode, mobileNo,orderDetails);
					System.out.println("FLAT DISCOUNT -- >> "+discountedValue);
				}else{
					discountedValue = 0;
					message = "PromoCode is already applied!";
					isValid = false;
				}
				
			}else if(promoTypeId == 2){//Percentage
				if(!isPromoCodeApplied){
					finalTotal = ( finalTotal * promoValue/100);
					discountedValue = finalTotal;
					message = "Valid PromoCode";
					isValid = true;
					applyPromoCode(promoCode, mobileNo,orderDetails);
					System.out.println("PERCENTAGE DISCOUNT -- >> "+discountedValue);
				}else{
					discountedValue = 0;
					message = "PromoCode is already applied!";
					isValid = false;
				}
				
			}else if(promoTypeId == 4){//EAZE FIRST FREE 1 meal into order item of same type 2
				
				if(isValidOrderForFreeMeal(orderItems)){//Block for free meal valid order(same item with quantity 2) starts here 
					if(!isPromoCodeApplied){//check whether promocode already applied or not starts here.
						message = "Valid PromoCode";
						discountedValue =  getFreeMealPrice(orderItems);
						isValid = true;	
						applyPromoCode(promoCode, mobileNo, orderDetails);
					}else{//check whether promocode already applied or not ends here.
						discountedValue = 0;
						message = "PromoCode is already applied!";
						isValid = false;
					}
				}//Block for free meal valid order(same item with quantity 2) ends here 
				else{
					isValid = false;
					
					message = "1 Meal free on purchase of 1 or more meals on 1st order. \nPlease select 1 more item.";
				}
				
				
			}else{
				if(totalQuantity == volumeQuantity){
					
					if(!isPromoCodeApplied){
						message = "Valid PromoCode";
						discountedValue =  promoValue;
						isValid = true;	
						applyPromoCode(promoCode, mobileNo, orderDetails);
					}else{
						discountedValue = 0;
						message = "PromoCode is already applied!";
						isValid = false;
					}
					
				
				}else if(totalQuantity > volumeQuantity){
					if(!isPromoCodeApplied){
						message = "Valid PromoCode";
						discountedValue = ((totalQuantity  - volumeQuantity) * promoValue)+promoValue;
						isValid = true;
						applyPromoCode(promoCode, mobileNo, orderDetails);
					}else{
						discountedValue = 0;
						message = "PromoCode is already applied!";
						isValid = false;
					}
					
					
				}else{
					isValid = false;
					message = "Total quantity must be greater than "+(volumeQuantity-1)+" for this PromoCode";
				}
				System.out.println("VOLUME DISCOUNT -- >> "+discountedValue);
			}
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", message);
			promoCodeValidJson.put("isValid", isValid);
			promoCodeValidJson.put("promoValue",(- discountedValue) );
			
			//System.out.println("---------------- >>>>>>>>>>> 11");
			//System.out.println("1 PROMO CODE VALID JSON DAO -- >> " + promoCodeValidJson );
		}else{
			//System.out.println("---------------- >>>>>>>>>>> 22");
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", "Invalid PromoCode");
			promoCodeValidJson.put("isValid", false);
			promoCodeValidJson.put("promoValue", ( discountedValue) );
			
			//System.out.println("2 PROMO CODE VALID JSON DAO -- >> " + promoCodeValidJson );
		}
		//System.out.println("3 PROMO CODE VALID JSON DAO -- >> " + promoCodeValidJson );
		return promoCodeValidJson;
	}
	
	public static PromoCode getPromoTypeValue(String promoCode){
		PromoCode code = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select promo_value,promo_type_id from vw_promo_code_details where promo_code = ? AND promo_code_is_active='Y'";
							
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, promoCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							code = new PromoCode();
							code.setTypeId(resultSet.getInt("promo_type_id"));
							code.setPromoValue(resultSet.getDouble("promo_value"));
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}if(connection!=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return code;
	}
	
	
	public static double applyCoupon(PromoCode code){
		if(code.getTypeId()==1){
			return code.getUserValue() - code.getPromoValue();
		}else{
			return code.getUserValue() - ( code.getUserValue() * (code.getPromoValue()/100) );
		}
	}
	
	public static boolean isUsedPromoCode(String promoCode, String mobileNo){
		boolean isUsedPromoCode = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select count(promo_code)AS promo_code from fapp_orders where contact_number = ? and "
							+ " UPPER(promo_code) = UPPER(?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						preparedStatement.setString(2, promoCode);
						//System.out.println("IS USER PROMO CODE QRY -- >> " + preparedStatement);
						resultSet = preparedStatement.executeQuery();
						
						while (resultSet.next() ) {
							int count = resultSet.getInt("promo_code");
							if(count > 0){
								System.out.println("PROMO CODE COUNT -- >> " + count );
								isUsedPromoCode = true;
							}else{
								isUsedPromoCode = false;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}if(connection!=null){
						connection.close();
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//System.out.println("IS USER PROMO CODE -- >> " + isUsedPromoCode);
		return isUsedPromoCode;
	}
	
	public static boolean isPromoCodeApplied(String promoCode, String mobileNo,List<String> orderDetails){
		boolean isPromoCodeApplied = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					/*String sql = "select count(applied_promo_code)AS promo_code from fapp_accounts where mobile_no = ? and "
							+ " applied_promo_code = ?";*/
					String sql = "select applied_promo_code AS promo_code from fapp_accounts where mobile_no = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
					//	preparedStatement.setString(2, promoCode);
						//System.out.println("IS USER PROMO CODE QRY -- >> " + preparedStatement);
						resultSet = preparedStatement.executeQuery();
						
						while (resultSet.next() ) {
							String isApplied = resultSet.getString("promo_code");
							if(isApplied.contentEquals(orderDetails.toString())){
								isPromoCodeApplied = true;
							}else{
								isPromoCodeApplied = false;
							}
							/*int count = resultSet.getInt("promo_code");
							if(count > 0){
								System.out.println("PROMO CODE applied COUNT -- >> " + count );
								isPromoCodeApplied = true;
							}else{
								isPromoCodeApplied = false;
							}*/
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}if(connection!=null){
						connection.close();
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("IS USER PROMO CODE APPLIED -- >> " + isPromoCodeApplied);
		return isPromoCodeApplied;
	}
	
	public static boolean applyPromoCode(String promoCode, String mobileNo,List<String> orderDetails){
		boolean isPromoCodeApplied = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "update fapp_accounts set applied_promo_code = ? where mobile_no = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderDetails.toString());
						preparedStatement.setString(2, mobileNo);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							isPromoCodeApplied = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}if(connection!=null){
						connection.close();
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("PROMO CODE APPLIED FOR -- >> "+mobileNo +" is ->"+ isPromoCodeApplied);
		return isPromoCodeApplied;
	}
	
	public static boolean applyRemovePromoCode(String promoCode, String mobileNo){
		boolean isPromoCodeApplied = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "update fapp_accounts set applied_promo_code = 'N' where mobile_no = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						//preparedStatement.setString(1, promoCode);
						preparedStatement.setString(1, mobileNo);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							isPromoCodeApplied = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}if(connection!=null){
						connection.close();
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("PROMO CODE APPLIED FOR -- >> "+mobileNo +" is ->"+ isPromoCodeApplied);
		return isPromoCodeApplied;
	}
	
	/**
	 * This method returns the promo value for EAZEKARO
	 * @param orderItems
	 * @return
	 */
	public static double getFreeMealPrice(ArrayList<OrderItems> orderItems){
		double promoValue = 0, lowestPrice = 0, totalPrice = 0;
		ArrayList<Double> itemPriceList = new ArrayList<Double>();
		
		for(OrderItems items : orderItems){
			itemPriceList.add(items.price);
			totalPrice += (items.price * items.quantity);
		}
		
		lowestPrice = Collections.min(itemPriceList);
		System.out.println("Total price: "+totalPrice+" Lowest price: "+lowestPrice);
		
		promoValue =  lowestPrice ;
		System.out.println("Promo value for EAZEKARO: "+promoValue);
		/*if(orderItems.size()==1){
			if(orderItems.get(0).quantity == 2){
				promoValue = orderItems.get(0).price;
			}
		}*/
		return (promoValue);
	}
	
	/**
	 * Check whether the order is valid for free meal promo code or not?
	 * @param orderItems
	 * @return
	 */
	public static boolean isValidOrderForFreeMeal(ArrayList<OrderItems> orderItems){
		int totalNoOfQuantity = 0;
		for(OrderItems items : orderItems){
			totalNoOfQuantity += items.quantity;
		}
		if(totalNoOfQuantity > 1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * This methods returns whether a promo code is reusable or not
	 * @param promoCode
	 * @return
	 */
	public static boolean isReusablePromoCode(String promoCode){
		boolean isReusable = false;
		try {
			SQL:{
					Connection connection  = DBConnection.createConnection();
					PreparedStatement  preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select is_reusable from vw_promo_code_details where promo_code = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, promoCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							String reuse = resultSet.getString("is_reusable");
							if(reuse.equalsIgnoreCase("Y")){
								isReusable = true;
							}else{
								isReusable = false;
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
		System.out.println(promoCode+" is reusable: "+isReusable);
		return isReusable;
	}
}
