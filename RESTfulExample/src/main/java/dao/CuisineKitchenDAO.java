package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class CuisineKitchenDAO {

	public static int kitchenCuisine(int kitchenID){
		int cuisineId= 0 ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select cuisin_id from fapp_kitchen_details where kitchen_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenID);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							cuisineId = resultSet.getInt("cuisin_id");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
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
		System.out.println("returned cuisine id:: "+cuisineId);
		return cuisineId;
	}
}
