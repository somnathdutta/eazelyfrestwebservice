package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.mkyong.rest.DBConnection;

public class StockUpdationDAO {

	public static int updateKitchenItemStock(int kitchenId, String itemCode,int itemQty, String mealType, String deliveryDay){
		int updatedRows = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "";
					if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "UPDATE fapp_kitchen_items set stock = (stock - ?)"
								+ " where kitchen_id = ? and item_code = ? and stock > 0";
					}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "UPDATE fapp_kitchen_items set stock_tomorrow = (stock_tomorrow - ?)"
								+ " where kitchen_id = ? and item_code = ? and stock_tomorrow > 0";
					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "UPDATE fapp_kitchen_items set dinner_stock = (dinner_stock - ?)"
								+ "  where kitchen_id = ? and item_code = ? and dinner_stock > 0";
					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "UPDATE fapp_kitchen_items set dinner_stock_tomorrow = (dinner_stock_tomorrow - ?)"
								+ " where kitchen_id = ? and item_code = ?  and dinner_stock_tomorrow > 0";
					}
					System.out.println("*** Stock updation start with ****"+updatedRows);
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, itemQty);
						preparedStatement.setInt(2, kitchenId);
						preparedStatement.setString(3, itemCode);
						System.out.println(preparedStatement);
						updatedRows = preparedStatement.executeUpdate();
								
					} catch (Exception e) {
						// TODO: handle exception
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
		System.out.println("*** Stock updation ends with ****"+updatedRows);
		return updatedRows;
	}
	
	public static int updateSingleOrder(int kitchenId, String mealType, String deliveryDay){
		int updatedRows = 0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "";
					if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "UPDATE fapp_kitchen_items set no_of_single_order = (no_of_single_order - 1)"
								+ " where kitchen_id = ? and no_of_single_order > 0";
					}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "UPDATE fapp_kitchen_items set no_of_single_order_lunch_tomorrow = (no_of_single_order_lunch_tomorrow - 1)"
								+ " where kitchen_id = ? and no_of_single_order_lunch_tomorrow > 0";
					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "UPDATE fapp_kitchen_items set no_of_single_order_dinner = (no_of_single_order_dinner - 1)"
								+ " where kitchen_id = ? and no_of_single_order_dinner > 0";
					}else{
						sql = "UPDATE fapp_kitchen_items set no_of_single_order_dinner_tomorrow = (no_of_single_order_dinner_tomorrow - 1)"
								+ " where kitchen_id = ? and no_of_single_order_dinner_tomorrow > 0";
					}
					
					
					System.out.println("*** Stock updation start with ****"+updatedRows);
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						System.out.println(preparedStatement);
						updatedRows = preparedStatement.executeUpdate();
								
					} catch (Exception e) {
						// TODO: handle exception
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
		System.out.println("*** Single Order updation ends with ****"+updatedRows);
		return updatedRows;
	}
}
