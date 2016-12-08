package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class StockUpdationDAO {

	public static int updateKitchenItemStock(int kitchenId, String itemCode,int itemQty, String mealType, String deliveryDay){
		int updatedRows = 0;
		System.out.println("---------------------------");
		System.out.println("Now updating Kitchen Item Stock. . .");
		System.out.println("---------------------------");
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
		System.out.println("*** Kitchen Item Stock updation ends with ****"+updatedRows);
		return updatedRows;
	}
	
	public static int updateSingleOrder(int kitchenId, String mealType, String deliveryDay){
		int updatedRows = 0;
		System.out.println("-------------------------------------");
		System.out.println("Updating SingleOrder for kitchen. . .");
		System.out.println("-------------------------------------");
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
		System.out.println("*** Kitchen Single Order updation ends with ****"+updatedRows);
		return updatedRows;
	}
	
	public static String getItemStock(String pincode, String itemCode,Connection connection,
			String mealType, String deliveryDay, String kitchenIds, String area){
    	Integer stock =0;
    	try {
			SQL:{
    				//Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				/*String sql = "select stock from fapp_kitchen_items where "
								+" kitchen_id = ? and item_id = ?";*/
    				String sql = "";
    				if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(stock) AS stock  "
	    						+" from vw_active_kitchen_items  "
	    						+" where item_code = ? "
	    						+" and is_active='Y' and serving_areas like ? "
	    						+"  and  kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code = ? "
    	    						+" and is_active='Y' and serving_areas like ? ";
    					}
    					/*sql = "select sum(stock)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y'and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and serving_areas like ? "
    								+" and is_active_tomorrow='Y' "
    								+" and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active_tomorrow='Y' and serving_areas like ? ";	
    					}
    					/*sql = "select sum(stock_tomorrow)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y'and fki.kitchen_id IN "+kitchenIds;*/
    				}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(fki.dinner_stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(dinner_stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active='Y' "
    								+" and serving_areas like ? and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(fki.dinner_stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(dinner_stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active='Y' "
    								+" and serving_areas like ? ";
    					}
    					
    					/*sql = "select sum(fki.dinner_stock)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}else{
    					
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(dinner_stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items"
    								+" where item_code= ? and is_active_tomorrow='Y' "
    								+" and serving_areas like ? and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(dinner_stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items"
    								+" where item_code= ?"
    								+" and serving_areas like ? "
    								+" and is_active_tomorrow='Y' ";
    					}
    					/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}
    				
    				try {
						preparedStatement = connection.prepareStatement(sql);
						
						preparedStatement.setString(1, itemCode);
						//preparedStatement.setString(2, "%"+pincode+"%");
						preparedStatement.setString(2, "%"+area+"%");
						/*preparedStatement.setString(1, "%"+pincode+"%");
						preparedStatement.setString(2, itemCode);*/
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							stock = resultSet.getInt("stock");
							if(stock<=0){
								stock = 0;
							}
						}
					} catch (Exception e) {
						//System.out.println("ERROR DUE TO:"+e.getMessage());
						//e.printStackTrace();
					}finally{
						/*if(connection!=null){
							connection.close();
						}*/
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return stock.toString();
    }
}
