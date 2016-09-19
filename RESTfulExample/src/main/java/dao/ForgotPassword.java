package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sql.ForgotPasswordSQL;

import com.mkyong.rest.DBConnection;

public class ForgotPassword {

	public static boolean emailExists(String userMailId){
		boolean emailExists = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(ForgotPasswordSQL.emailExistsQuery);
						preparedStatement.setString(1, userMailId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							emailExists = true;
						}
					} catch (Exception e) {
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
		System.out.println("Email id::"+userMailId+" is exists::"+emailExists);
		return emailExists;
	}
}
