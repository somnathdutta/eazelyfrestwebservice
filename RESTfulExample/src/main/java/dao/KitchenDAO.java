package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KitchenDAO {

	public static ArrayList<Integer> findKitchensInArea(Connection connection, String area){
		ArrayList<Integer> kitchenIdList = new ArrayList<Integer>();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select distinct kitchen_id from vw_active_kitchen_items where serving_areas LIKE ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, "%"+area+"%");
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int kitcenId = resultSet.getInt("kitchen_id");
							kitchenIdList.add(kitcenId);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenIdList;
	}
	
}
