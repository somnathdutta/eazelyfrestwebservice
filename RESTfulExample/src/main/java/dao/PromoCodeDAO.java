package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.PromoCode;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class PromoCodeDAO {

	public static JSONObject isPromoCodeValid(String promoCode,ArrayList<OrderItems> orderItems ) throws JSONException{
		JSONObject promoCodeValidJson = new JSONObject();
		int count = 0,totalQuantity = 0,promoTypeId = 0,promoCodeApplicationTypeId =0,volumeQuantity=0 ;
		double promoValue = 0, discountedValue =0, totalValue = 0,finalTotal = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select promo_code,promo_value,promo_type_id,promo_code_application_type_id,volume_quantity "
							+ "from vw_promo_code_details where promo_code_is_active='Y'"
							+ " and promo_code = ? and current_date>=from_date AND current_date<=to_date";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, promoCode);
						//System.out.println("PROMO CODE VALIDATION CHECKING QRY -- >> " + preparedStatement);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							count++;
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
		
		if(count>0){
			for(OrderItems items : orderItems){
				/*if(items.categoryId==78 || items.categoryId==79){
					finalTotal += items.price;
					continue;
				}else{*/
					finalTotal += items.price;
					totalQuantity += items.quantity;
				//}
			}
			//System.out.println("Total -- >>  "+finalTotal);
			//System.out.println("Total QUANTITY -- >>  "+totalQuantity);
			/*if(totalQuantity>1){
				if( promoCodeApplicationTypeId == 1 ){//ON volume
					if((promoTypeId == 1)){	//FLAT
						discountedValue = ( totalQuantity - 1 ) * promoValue;
						System.out.println("Discounted on volume flat: "+discountedValue);
					}else{//Percentage
						//discountedValue = ( totalQuantity - 1 ) * promoValue;
						discountedValue =finalTotal - ( finalTotal * (promoValue/100) );
						System.out.println("Discounted on %: "+discountedValue);
					}
				}
			}else{
				discountedValue = -0.0;
			}*/
			if(promoTypeId==1){//FLAT
				
				discountedValue = promoValue;
				System.out.println("FLAT DISCOUNT -- >> "+discountedValue);
			}else if(promoTypeId == 2){//Percentage
				
				finalTotal = ( finalTotal * promoValue/100);
				discountedValue = finalTotal;
				System.out.println("PERCENTAGE DISCOUNT -- >> "+discountedValue);
			}else{
				if(totalQuantity >= volumeQuantity){
					//discountedValue = (totalQuantity - 1) * promoValue;
					discountedValue = (totalQuantity  - volumeQuantity) * promoValue;
				}
				System.out.println("VOLUME DISCOUNT -- >> "+discountedValue);
			}
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", "Valid promoCode");
			promoCodeValidJson.put("isValid", true);
			promoCodeValidJson.put("promoValue",(- discountedValue) );
			
			//System.out.println("---------------- >>>>>>>>>>> 11");
			//System.out.println("1 PROMO CODE VALID JSON DAO -- >> " + promoCodeValidJson );
		}else{
			//System.out.println("---------------- >>>>>>>>>>> 22");
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", "InValid promoCode");
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
							+ " promo_code = ?";
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
}
