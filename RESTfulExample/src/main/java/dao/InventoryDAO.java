package dao;

import com.mkyong.rest.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryDAO {

	public static int getCarryBagCapacity(){
		int carryBagCapacity = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select bag_capacity from fapp_carry_bag where is_active='Y' and is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							carryBagCapacity = resultSet.getInt("bag_capacity");
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
		return carryBagCapacity;
	}
	
	public static void updateCarryBagForKitchen(int kitchenId, int totalNoOfItemsForKitchen){
		int carryBagCapacity = getCarryBagCapacity();
		int div = totalNoOfItemsForKitchen/carryBagCapacity;
		int rem = totalNoOfItemsForKitchen%carryBagCapacity;
		int totalRequiredCarryBag = div + rem ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_kitchen_inventry set sold = sold + ?"
							+ " where kitchen_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, totalRequiredCarryBag);
						preparedStatement.setInt(2, kitchenId);
						int count = preparedStatement.executeUpdate();
						System.out.println(totalRequiredCarryBag+" are Sold for the kitchen "+kitchenId+" count row:: "+count);
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
	}
}
