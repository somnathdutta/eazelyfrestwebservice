package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

import pojo.Kitchen;

public class SingleOrderDAO {

	
	public static JSONObject getSingleOrderJSON(String area,String deliveryDay, String mealType) throws JSONException{
		JSONObject singleOrderJson = new JSONObject();
		boolean isSingleOrderAvailable = false;
		isSingleOrderAvailable = SingleOrderDAO.isSingleOrderAvailable(area, mealType, deliveryDay);
    	singleOrderJson.put("status", "200");
    	singleOrderJson.put("isSingleOrderAvailable", isSingleOrderAvailable);
    	singleOrderJson.put("message", "Currently we do not have biker to serve single order.Please add more quantity!");
    	return singleOrderJson;
	}
	
	
	
	public static boolean[] isSingleOrderAvailable(String area, String deliveryDay, Connection connection) throws JSONException{
		boolean[] isSingleOrderAvailable = new boolean[2];
		ArrayList<Kitchen> kitchenSingleOrders = getKitchenWithSingleOrders(area, deliveryDay, connection);
		int totNoSingleOrderLunch = 0,totNoSingleOrderDinner = 0;
		for(Kitchen kitchen : kitchenSingleOrders){
			System.out.println("Kitchen: "+kitchen.getKitchenId()+" L:"+kitchen.getSingleOrderLunch()+" D:"+kitchen.getSingleOrderDinner());
			
			if(deliveryDay.equalsIgnoreCase("TODAY")){
				if(kitchen.getSingleOrderLunch()==0){
					totNoSingleOrderLunch++;
				}
				if(kitchen.getSingleOrderDinner()==0){
					totNoSingleOrderDinner++;
				}
			}else{
				if(kitchen.getSingleOrderLunch()==0){
					totNoSingleOrderLunch++;
				}
				if(kitchen.getSingleOrderDinner()==0){
					totNoSingleOrderDinner++;
				}
			}
		}
		System.out.println("lunch : "+totNoSingleOrderLunch+" dinner: "+totNoSingleOrderDinner);
		
		if(totNoSingleOrderLunch != kitchenSingleOrders.size()){
			isSingleOrderAvailable[0] = true;
			//isSingleOrderAvailable[1] = true;
		}
		if(totNoSingleOrderDinner != kitchenSingleOrders.size()){
			//isSingleOrderAvailable[0] = true;
			isSingleOrderAvailable[1] = true;
		}
		System.out.println("L:: "+isSingleOrderAvailable[0]+" D:"+isSingleOrderAvailable[1]);
		return isSingleOrderAvailable;
	}
	
	public static boolean isSingleOrderAvailable(String area, String mealType, String deliveryDay){
		boolean isSingleOrderAvailableForLunch = false;
		ArrayList<Kitchen> kitchenSingleOrders = getKitchenWithSingleOrders(area, mealType, deliveryDay);
		int totNoSingleOrder = 0;
		for(Kitchen kitchen : kitchenSingleOrders){
			if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
				if(kitchen.getSingleOrder()==0){
					totNoSingleOrder ++;
				}
			}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
				if(kitchen.getSingleOrder()==0){
					totNoSingleOrder ++;
				}
			}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
				if(kitchen.getSingleOrder()==0){
					totNoSingleOrder ++;
				}
			}else{
				if(kitchen.getSingleOrder()==0){
					totNoSingleOrder ++;
				}
			}
		}
		if(totNoSingleOrder != kitchenSingleOrders.size()){
			isSingleOrderAvailableForLunch = true;
		}else{
			isSingleOrderAvailableForLunch = false;
		}
		return isSingleOrderAvailableForLunch;
	}
	
	
	public static ArrayList<Kitchen> getKitchenWithSingleOrders(String area, String deliveryDay, 
			Connection connection){
		ArrayList<Kitchen> kitchenSingleOrders = new ArrayList<Kitchen>();
		try {
			SQL:{
					//Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = null;
					if(deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select distinct kitchen_id,no_of_single_order AS lunch,no_of_single_order_dinner AS dinner"
								+ "  from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else{
						sql = "select distinct kitchen_id,no_of_single_order_lunch_tomorrow AS lunch,no_of_single_order_dinner_tomorrow AS dinner "
								+ "from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}
					/*if(mealType[0].equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select distinct kitchen_id,no_of_single_order AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else if(mealType[0].equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "select distinct kitchen_id,no_of_single_order_lunch_tomorrow AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else if(mealType[1].equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select distinct kitchen_id,no_of_single_order_dinner AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else{
						sql = "select distinct kitchen_id,no_of_single_order_dinner_tomorrow AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}*/
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, "%"+area+"%");
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							Kitchen kitchen = new Kitchen();
							kitchen.setKitchenId(resultSet.getInt("kitchen_id"));
							kitchen.setSingleOrderLunch(resultSet.getInt("lunch"));
							kitchen.setSingleOrderDinner(resultSet.getInt("dinner"));
							kitchenSingleOrders.add(kitchen);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						/*if(connection!=null){
							connection.close();
						}*/
					}			
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenSingleOrders;
	}
	
	public static ArrayList<Kitchen> getKitchenWithSingleOrders(String area,String mealType, String deliveryDay){
		ArrayList<Kitchen> kitchenSingleOrders = new ArrayList<Kitchen>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = null;
					if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select distinct kitchen_id,no_of_single_order AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
						sql = "select distinct kitchen_id,no_of_single_order_lunch_tomorrow AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
						sql = "select distinct kitchen_id,no_of_single_order_dinner AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}else{
						sql = "select distinct kitchen_id,no_of_single_order_dinner_tomorrow AS single_orders from vw_active_kitchen_items"
								+ " where serving_areas LIKE ? ";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, "%"+area+"%");
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							Kitchen kitchen = new Kitchen();
							kitchen.setKitchenId(resultSet.getInt("kitchen_id"));
							kitchen.setSingleOrder(resultSet.getInt("single_orders"));
							
							kitchenSingleOrders.add(kitchen);
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
		return kitchenSingleOrders;
	}
	
	public static int getCartCapacity(Connection connection){
		int cartCapacity = 0;
		try {
			SQL:{
					//Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select max_cart_capacity from fapp_biker_capacity where is_active='Y' and is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							cartCapacity = resultSet.getInt("max_cart_capacity");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						/*if(connection!=null){
							connection.close();
						}*/
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cartCapacity;
	}
}
