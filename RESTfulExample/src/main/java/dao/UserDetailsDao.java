package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

import pojo.Prepack;
import pojo.User;
import sql.UserQuery;

public class UserDetailsDao {

	public static User getUserDetails(String contactNumber, String orderNo){
		User user = new User();
		try {
				Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						if(contactNumber!=null){
							preparedStatement = connection.prepareStatement(UserQuery.userSqlQuery);
							preparedStatement.setString(1, contactNumber);
						}else{
							preparedStatement = connection.prepareStatement(UserQuery.userEmailSqlQuery);
							preparedStatement.setString(1, orderNo);
						}
						
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							user.setEmailId(resultSet.getString("email"));
							user.setUserName(resultSet.getString("username"));
							if(orderNo != null){
								user.setContactNumber(resultSet.getString("mobile_no"));
								user.setDeliveryAddress(resultSet.getString("delivery_address"));
							}else{
								user.setContactNumber(contactNumber);
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
		return user;
	}
}
