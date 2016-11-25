package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class PaymentTypeDAO {

	public static JSONObject fetchPaymentTypeList() throws JSONException{
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
			paymentJson.put("paymentList", paymentJsonArray);
		}else{
			paymentJson.put("status", "204");
			paymentJson.put("message", "Payment type not found!");
			paymentJson.put("paymentList", paymentJsonArray);
		}
		System.out.println(paymentJson);
		return paymentJson;
	}
}
