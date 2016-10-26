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
		int count = 0,totalQuantity = 0,promoTypeId = 0,promoCodeApplicationTypeId =0 ;
		double promoValue = 0, discountedValue =0, totalValue = 0,finalTotal = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select promo_code,promo_value,promo_type_id,promo_code_application_type_id "
							+ "from vw_promo_code_details where promo_code_is_active='Y'"
							+ " and promo_code = ? and current_date>=from_date AND current_date<=to_date";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, promoCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							count++;
							promoValue = resultSet.getDouble("promo_value");
							promoTypeId = resultSet.getInt("promo_type_id");
							promoCodeApplicationTypeId = resultSet.getInt("promo_code_application_type_id");
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
			System.out.println("Total: "+finalTotal);
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
				System.out.println("FLAT DISCOUNT::");
				discountedValue = promoValue;
			}else if(promoTypeId == 2){//Percentage
				System.out.println("PERCENTAGE DISCOUNT::");
				finalTotal = ( finalTotal * promoValue/100);
				discountedValue = finalTotal;
			}else{
				System.out.println("DABBA DISCOUNT::");
				if(totalQuantity>1){
					discountedValue = (totalQuantity - 1) * promoValue;
				}
			}
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", "Valid PromoCode");
			promoCodeValidJson.put("isValid", true);
			promoCodeValidJson.put("promoValue",(- discountedValue) );
		}else{
			promoCodeValidJson.put("status","200");
			promoCodeValidJson.put("message", "InValid PromoCode");
			promoCodeValidJson.put("isValid", false);
			promoCodeValidJson.put("promoValue", (- discountedValue) );
		}
		System.out.println(promoCodeValidJson.toString());
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
}
