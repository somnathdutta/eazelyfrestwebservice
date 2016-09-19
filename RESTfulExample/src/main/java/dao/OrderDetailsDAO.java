package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pojo.User;
import sql.UserQuery;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.DateFormattor;
import com.mkyong.rest.Order;

public class OrderDetailsDAO {

	public static Order getOrderDetails( String orderNo){
		Order order = new Order();
		try {
				Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(UserQuery.orderQuery);
						preparedStatement.setString(1, orderNo);
						
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							order.setOrderNo(orderNo);
							order.setOrderDateValue(DateFormattor.toStringDate(resultSet.getString("order_date")));
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
		return order;
	}
}
