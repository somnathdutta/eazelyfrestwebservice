package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.mkyong.rest.DBConnection;

import utility.Validation;

public class ForgotPasswordDAO {

	public static String createLinkForUser(String userMailId){
		String encrptedCode = Validation.encryption(userMailId);
		String server = "Please click on the link to reset your password. http://eazelyf.southindia.cloudapp.azure.com/FoodHomeDeliverySystem/view/reset.zul?rc=";
		if(updateHashCodeWithMailID(encrptedCode, userMailId)>0){
			System.out.println("encrptedCode updated!");
		}
		return server+encrptedCode;
	}
	
	public static int updateHashCodeWithMailID(String encrptedCode, String userMailId){
		int count = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_accounts set hash_code = ? where email = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, encrptedCode);
						preparedStatement.setString(2, userMailId);
						 count = preparedStatement.executeUpdate();
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
		return count;
	}
}
