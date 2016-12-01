package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.PromoCode;

import com.mkyong.rest.DBConnection;

public class PaymentTypeDAO {

	public static JSONObject fetchPaymentTypeList(String mobileNo) throws JSONException{
		JSONObject paymentJson = new JSONObject();
		JSONArray paymentJsonArray = new JSONArray();
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select * from fapp_payment_methods where is_active ='Y' order by payment_method_id";
			try {
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject paymentTypeJson = new JSONObject();
					paymentTypeJson.put("paymentTypeId", resultSet.getInt("payment_method_id"));
					paymentTypeJson.put("paymentType", resultSet.getString("payment_method"));
					paymentJsonArray.put(paymentTypeJson);
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

		if(paymentJsonArray.length() > 0){
			paymentJson.put("status", "200");
			paymentJson.put("message", "Payment type found!");
			boolean isAvailable = false;
			ArrayList<String> unUsedPromoCodeList = new ArrayList<String>();
			if(mobileNo==null || mobileNo.trim().length()==0){

			}else{
				unUsedPromoCodeList = getUnusedPromoCodeList(mobileNo);
				if(isUserHavingFreePromoCode(unUsedPromoCodeList)){
					isAvailable = true;
				}
			}

			if(isAvailable){
				paymentJson.put("isAvailable",true);

				StringBuilder promoMessage = new StringBuilder();
				promoMessage.append("Your Promocodes are:\n");
				for(String promo : unUsedPromoCodeList){
					if(promo.equalsIgnoreCase("EAZEKARO")){
						promoMessage.append("1 Meal free on purchase of 1 or more meals on 1st order. "
								+ "\nWould you like to use promocode \"EAZEKARO\"");
					}
					/*if(promo.equalsIgnoreCase("EAZEVOL")){
						promoMessage.append("\n AND Volume discount promocode "
								+ "\nWould you like to use promocode \"EAZEVOL\"");
					}*/
				}

				paymentJson.put("promoMessage", promoMessage.toString());

			}else{
				paymentJson.put("isAvailable",false);

				paymentJson.put("promoMessage", "");

			}


			//paymentJson.put("promoMessage", "1 Meal free on purchase of 1 or more meals on 1st order. \nWould you like to use promocode \"EAZEKARO\"");
			paymentJson.put("paymentList", paymentJsonArray);
		}else{
			paymentJson.put("status", "204");
			paymentJson.put("message", "Payment type not found!");
			paymentJson.put("promoMessage", "1 Meal free on purchase of 1 or more meals on 1st order. \nWould you like to use promocode \"EAZEKARO\"");
			paymentJson.put("paymentList", paymentJsonArray);
		}
		System.out.println(paymentJson);
		return paymentJson;
	}


	public static JSONObject fetchPaymentTypeListWithPromocode(String mobileNo) throws JSONException{
		boolean isGuest = false;
		JSONObject paymentJson = new JSONObject();
		JSONArray paymentJsonArray = new JSONArray();
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select * from fapp_payment_methods where is_active ='Y' order by payment_method_id";
			try {
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject paymentTypeJson = new JSONObject();
					paymentTypeJson.put("paymentTypeId", resultSet.getInt("payment_method_id"));
					paymentTypeJson.put("paymentType", resultSet.getString("payment_method"));
					paymentJsonArray.put(paymentTypeJson);
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
		ArrayList<String> promoCodeList = new ArrayList<String>();
		if(mobileNo!=null && mobileNo.trim().length()>0)
		promoCodeList = fetchPromoCodeList(mobileNo);

		String promoCodes = "";

		if(promoCodeList.size()>0){
			String temp = promoCodeList.toString();
			String fb = temp.replace("[", "");
			promoCodes = fb.replace("]", "");
		}


		JSONArray promoArray = new JSONArray();
		if(promoCodeList.size()>0){

			for(String str : promoCodeList){
				JSONObject promJsonObject = new JSONObject();
				System.out.println("str1 >>> >> > " + str);
				if(str.equals("EAZEKARO")){
					System.out.println("str2 >>> >> > " + str);
					promJsonObject.put("promoCode", str);
					promJsonObject.put("codeMessage", "1 Meal free on purchase of 1 or more meals on 1st order. \nWould you like to use promocode \"EAZEKARO\"");
					promoArray.put(promJsonObject);
				}
				/*if(str.equals("EAZELYF001")){
		     promJsonObject.put("promoCode", str);
		     promJsonObject.put("codeMessage", str+"- Message");
		    }
		    if(str.equals("EAZEVOL")){
		     promJsonObject.put("promoCode", str);
		     promJsonObject.put("codeMessage", str+"- Message");
		    }*/

			}
		}





		if(paymentJsonArray.length() > 0){
			paymentJson.put("status", "200");
			paymentJson.put("message", "Payment type found!");
			//paymentJson.put("promoMessage", "1 Meal free on purchase of 1 or more meals on 1st order. \nWould you like to use promocode \"EAZEKARO\"");
			if(mobileNo.trim().length()==0){
				isGuest = true;
			}
			if(isGuest){
				paymentJson.put("isAvailable", false);
				paymentJson.put("promoMessage", new JSONArray());
			}else{
				if(promoCodeList.size()>0 && promoArray.length()>0){
					paymentJson.put("isAvailable", true);
					//paymentJson.put("promoMessage", promoCodes);
					paymentJson.put("promoMessage", promoArray);
				}else {
					paymentJson.put("isAvailable", false);
					paymentJson.put("promoMessage", new JSONArray());
				}
			}

			/*if(promoCodeList.size()>0 && promoArray.length()>0){
		    paymentJson.put("isAvailable", true);
		    //paymentJson.put("promoMessage", promoCodes);
		    paymentJson.put("promoMessage", promoArray);
		   }else {
		    paymentJson.put("isAvailable", false);
		    paymentJson.put("promoMessage", new JSONArray());
		   }*/
			paymentJson.put("paymentList", paymentJsonArray);
		}else{
			paymentJson.put("status", "204");
			paymentJson.put("message", "Payment type not found!");
			paymentJson.put("isAvailable", false);
			paymentJson.put("promoMessage", " ");
			paymentJson.put("paymentList", paymentJsonArray);
		}
		System.out.println(paymentJson);
		return paymentJson;
	}


	public static ArrayList<String> fetchPromoCodeList(String mobileNo){

		JSONObject promocodeOfferJson = new JSONObject();
		JSONArray promoOfferJsonArray = new JSONArray();

		ArrayList<PromoCode> usedpromocodeList = usedPromocode(mobileNo);
		ArrayList<PromoCode> newPromocodeList = unUsedPromocode();
		ArrayList<String> finalPromocodeList = new ArrayList<String>();
		System.out.println("MOB " + mobileNo);
		if(mobileNo != null){
			if(usedpromocodeList.size()>0 && newPromocodeList.size()>0){
				for(PromoCode promoCode : usedpromocodeList){
					for(PromoCode code : newPromocodeList){
						if(!promoCode.getPromoCode().equals(code.getPromoCode())){
							finalPromocodeList.add(code.getPromoCode());
						}
					}
				}
			}
			else {
				for(PromoCode code : newPromocodeList){
					finalPromocodeList.add(code.getPromoCode());
					System.out.println("PROMO List -- >>> >> > "  +finalPromocodeList);
				}
			}
		}

		if(finalPromocodeList.size()>0){

		}



		return finalPromocodeList;

	}

	public static ArrayList<PromoCode> usedPromocode(String mobileNo){
		ArrayList<PromoCode> list = new ArrayList<PromoCode>();
		if(list.size()>0){
			list.clear();
		}
		
		try {
			used_promo_code_sql:{
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			String sql = "select promo_code from fapp_orders where contact_number = ? and user_type = 'REGISTERED USER' ";
			ResultSet resultSet = null;
			try {
				connection = DBConnection.createConnection();
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, mobileNo);

				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					PromoCode code = new PromoCode();
					if(resultSet.getString("promo_code")!= null){
						code.setPromoCode(resultSet.getString("promo_code"));
						list.add(code);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				if(connection != null){
					connection.close();
				}
			}
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<PromoCode> unUsedPromocode(){
		ArrayList<PromoCode> list = new ArrayList<PromoCode>();
		if(list.size()>0){
			list.clear();
		}

		try {
			used_promo_code_sql:{
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			String sql = "select * from vw_promo_code_details where promo_code_is_active = 'Y'";
			ResultSet resultSet = null;
			try {
				connection = DBConnection.createConnection();
				preparedStatement = connection.prepareStatement(sql);

				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					PromoCode code = new PromoCode();
					if(resultSet.getString("promo_code") != null){
						code.setPromoCode(resultSet.getString("promo_code"));
						code.setIsreusable(resultSet.getString("is_reusable"));
						list.add(code);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				if(connection != null){
					connection.close();
				}
			}
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean isUserHavingFreePromoCode(ArrayList<String> unUsedPromoCodeList){
		if(unUsedPromoCodeList.size() == 0)
			return false;
		else{
			return true;
		}
	}

	/**
	 * This method returns the unUsed promocode list for user
	 * @param dbPromoCodeList
	 * @param userPromoCodeList
	 * @return
	 */
	public static ArrayList<String> getUnusedPromoCodeList(String mobileNo){
		ArrayList<String> unUsedPromoCodeList = new ArrayList<String>();
		ArrayList<String> dbPromoCodeList = new ArrayList<String>();
		ArrayList<String> userPromoCodeList = new ArrayList<String>();

		try {
			Connection connection = DBConnection.createConnection();
			dbPromoCodeList = fetchActivePromoCodes(connection);
			userPromoCodeList = fetchUsedPromoCodes(mobileNo, connection);
		} catch (Exception e) {
			// TODO: handle exception
		}
		for(String dbPromoCode : dbPromoCodeList){
			if(!userPromoCodeList.contains(dbPromoCode)){
				System.out.println(dbPromoCode);
				unUsedPromoCodeList.add(dbPromoCode);
			}
		}
		return dbPromoCodeList;
	}


	/**
	 * Return all active procodes from db
	 * @return
	 */
	public static ArrayList<String> fetchActivePromoCodes(Connection connection){
		ArrayList<String> promoCodeList = new ArrayList<String>();
		try {
			SQL:{
			//Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null; 
			String sql = "SELECT promo_code FROM vw_promo_code_details where promo_code_is_active='Y'";

			try {
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					promoCodeList.add(resultSet.getString("promo_code"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				if(preparedStatement!=null){
					preparedStatement.close();
				}
				/*if(connection!=null){
							connection.close();
						}*/
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return promoCodeList;
	}

	/**
	 * Return all procodes from db of a user
	 * @return
	 */
	public static ArrayList<String> fetchUsedPromoCodes(String mobileNo, Connection connection){
		ArrayList<String> promoCodeList = new ArrayList<String>();
		try {
			SQL:{
			//Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null; 
			String sql = "SELECT promo_code FROM fapp_orders where contact_number = ?";

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, mobileNo);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					promoCodeList.add(resultSet.getString("promo_code"));
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
		return promoCodeList;
	}




}
