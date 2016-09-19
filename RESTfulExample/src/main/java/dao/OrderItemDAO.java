package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import sql.AllItemSqlQuery;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.OrderItems;

public class OrderItemDAO {

	public static ArrayList<OrderItems> getOrderItemDetails(String orderNo){
		ArrayList<OrderItems> orderItemList = new ArrayList<OrderItems>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement(AllItemSqlQuery.orderItemQuery);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							OrderItems items = new OrderItems();
							items.cuisinName = resultSet.getString("cuisin_name");
							items.categoryName = resultSet.getString("category_name");
							items.itemName = resultSet.getString("item_name");
							items.itemDescription = resultSet.getString("item_description");
							items.quantity = resultSet.getInt("qty");
							items.price = resultSet.getDouble("total_price");
							orderItemList.add(items);
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
		
		return orderItemList;
	}
}
