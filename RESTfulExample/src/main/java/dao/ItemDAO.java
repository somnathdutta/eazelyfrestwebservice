package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
