package dao;

import com.mkyong.rest.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreditValueDAO {

	public static double[] getCreditAndSignUpValue(){
		double[] creditValue = new double[2];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "select sign_up_credit,order_credit from fapp_credits";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							creditValue[0] = resultSet.getDouble("sign_up_credit");
							creditValue[1] = resultSet.getDouble("order_credit");
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
		System.out.println("Sign up credit value:: "+creditValue[0]+" Order credit value:: "+creditValue[1]);
		return creditValue;
	}
}
