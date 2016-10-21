package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class FetchCuisineDAO {

	 public static boolean isAllActive(){
	    	boolean isAllActive = false;
	    	try {
				SQL:{
	    				Connection connection = DBConnection.createConnection();
	    				PreparedStatement preparedStatement = null;
	    				ResultSet resultSet = null;
	    				String sql = "SELECT count(is_active)AS cuisins from fapp_cuisins where is_delete='N' and is_active='Y'";
	    				try {
							preparedStatement = connection.prepareStatement(sql);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								int count = resultSet.getInt("cuisins");
								if(count==1){
									isAllActive = true;
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
	    	
	    	return isAllActive;
	    }
	 
	 public static boolean isNewUser(String mobileNo){
		 boolean isNewUser = false;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		ResultSet resultSet = null;
			 		String sql = "select count(contact_number)AS new_user from fapp_orders where contact_number = ?";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int count = resultSet.getInt("new_user");
							if(count == 0){
								isNewUser = true;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(connection!=null){
						connection.close();
					}
		 		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		 return isNewUser ;
	 }

	 public static boolean isNewUserItem(String itemCode){
		 boolean isNewUserItem = false;
		 try {
			SQL:{
			 		Connection connection = DBConnection.createConnection();
			 		PreparedStatement preparedStatement = null;
			 		ResultSet resultSet = null;
			 		String sql ="select apply_new_user from food_items where item_code = ?";
			 		try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							if(resultSet.getString("apply_new_user").equalsIgnoreCase("Y")){
								isNewUserItem = true;
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
		 return isNewUserItem;
	 }
}
