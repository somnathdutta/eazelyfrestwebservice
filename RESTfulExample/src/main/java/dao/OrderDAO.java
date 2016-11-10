package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mkyong.rest.DBConnection;

import pojo.KitchenStock;

public class OrderDAO {

	public static ArrayList<KitchenStock> getKitchenItemStock(String itemCode, String deliveryDay, 
			String mealType, String area){
		ArrayList<KitchenStock> kitchenItemStockList = new ArrayList<KitchenStock>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryDay.equalsIgnoreCase("TODAY")&&mealType.equalsIgnoreCase("LUNCH")){
						sql = "select kitchen_id,stock AS stock from vw_active_kitchen_items"
								+ " where is_active='Y' and item_code = ? and serving_areas LIKE ? and stock>0";
					}else if(deliveryDay.equalsIgnoreCase("TODAY")&&mealType.equalsIgnoreCase("DINNER")){
						sql = "select kitchen_id,dinner_stock AS stock from vw_active_kitchen_items"
								+ " where is_active='Y' and item_code = ? and serving_areas LIKE ?  and dinner_stock>0"
								+ " order by stock desc";
					}else if(deliveryDay.equalsIgnoreCase("TOMORROW")&&mealType.equalsIgnoreCase("LUNCH")){
						sql = "select kitchen_id,stock_tomorrow AS stock from vw_active_kitchen_items"
								+ " where is_active_tomorrow='Y' and item_code = ? and serving_areas LIKE ? and stock_tomorrow>0"
								+ " order by stock desc";
					}else{
						sql = "select kitchen_id,dinner_stock_tomorrow AS stock from vw_active_kitchen_items"
								+ " where is_active_tomorrow='Y' and item_code = ? and serving_areas LIKE ? and dinner_stock_tomorrow>0"
								+ " order by stock desc";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, itemCode);
						preparedStatement.setString(2, "%"+area+"%");
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							KitchenStock kitchenStock = new KitchenStock();
							kitchenStock.kitchenId = resultSet.getInt("kitchen_id");
							kitchenStock.stock = resultSet.getInt("stock");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenItemStockList;
	}
}
