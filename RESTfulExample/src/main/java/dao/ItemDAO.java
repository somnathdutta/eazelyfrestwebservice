package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pojo.MealTypePojo;

import com.mkyong.rest.DBConnection;

public class ItemDAO {

	public static String[] getItemDetails(String itemCode){
		String[] itemDetails = new String[3];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select cuisin_name,category_name,item_name from vw_food_item_details "
							+ " where item_code = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							itemDetails[0] = resultSet.getString("cuisin_name");
							itemDetails[1] = resultSet.getString("category_name");
							itemDetails[2] = resultSet.getString("item_name");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		return itemDetails;
	}
	
	public static int getItemTypeId(String itemCode){
		int itemTypeId = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select item_type_id from vw_food_item_details where item_code = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							itemTypeId = resultSet.getInt("item_type_id");
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
		return itemTypeId;
	}
	
	public static int itemCurrentStock(int kitchenId, String itemCode, MealTypePojo mealTypePojo){
		int currStock = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(mealTypePojo.isLunchToday()){
						sql = "select stock AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active='Y'";
					}else if(mealTypePojo.isLunchTomorrow()){
						sql = "select stock_tomorrow AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active_tomorrow='Y'";
					}else if (mealTypePojo.isDinnerToday()) {
						sql = "select dinner_stock AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active='Y'";
					}else{
						sql = "select dinner_stock_tomorrow AS stock from vw_active_kitchen_items where item_code = ?"
								+ " and kitchen_id = ? and is_active_tomorrow='Y'";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						preparedStatement.setInt(2, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							currStock = resultSet.getInt("stock");
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
		
		return currStock;
	}
}
