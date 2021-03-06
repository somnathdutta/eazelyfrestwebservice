package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;

import pojo.Kitchen;

public class SingleOrderDAO {

	/**
	 * This method will return the lunch and dinner availability of kitchen for single order
	 * @param area
	 * @param deliveryDay
	 * @param connection
	 * @return boolean array
	 * @throws JSONException
	 */
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
	
	/**
	 * Get the kitchen and no of single order from area
	 * @param area
	 * @param deliveryDay
	 * @param connection
	 * @return
	 */
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
	
	public static int getCartCapacity(Connection connection, String area){
		int cartCapacity = 16;

		return cartCapacity;
	}

	/**
	 * This method will return the lunch and dinner cart capacity for each kitchen for specific area
	 * @param connection
	 * @param area
	 * @param deliveryDay
	 * @return integer array for lunch and dinner cart capacity
	 * @throws ParseException 
	 */
	public static int[] getCartValue(Connection connection, String area,  String deliveryDay, 
			ArrayList<Integer> kitchenList, boolean isOrderSpecialTiming) throws ParseException{
		int[] cartCapacity = new int[2];
		int lunchCapacity = 0,dinnerCapacity = 0;
		
		//ArrayList<Integer> kitchenList = KitchenDAO.findKitchensInArea(connection, area);
		
		for(Integer kitchenId : kitchenList){
			ArrayList<String> bikerList = new ArrayList<String>();
			
			bikerList = BikerDAO.findMultiTypeBikerOfKitchen(connection, kitchenId);		
			
			for(String bikerUserId : bikerList){
				int lc = BikerDAO.getAvailableLunchQuantity(connection, bikerUserId, deliveryDay, isOrderSpecialTiming);
				int dc = BikerDAO.getAvailableDinnerQuantity(connection, bikerUserId, deliveryDay);
				lunchCapacity += lc;
				dinnerCapacity += dc;
				System.out.println("LC:"+lc+" DC:"+dc);
			}
		}
		System.out.println("LC-------:"+lunchCapacity+" DC-----------:"+dinnerCapacity);
		
		cartCapacity[0] = lunchCapacity;
		cartCapacity[1] = dinnerCapacity;
		return cartCapacity;
	}
	
	/*public static int getSpecialLunchCartValue(Connection connection, String area,  String deliveryDay,
			ArrayList<Integer> kitchenList){
		int lunchCapacity = 0;
		//ArrayList<Integer> kitchenList = KitchenDAO.findKitchensInArea(connection, area);
		
		for(Integer kitchenId : kitchenList){
			ArrayList<String> bikerList = new ArrayList<String>();
			
			bikerList = BikerDAO.findSingleTypeBikerOfKitchen(connection, kitchenId);		
			
			for(String bikerUserId : bikerList){
				int lc = BikerDAO.getAvailableLunchQuantity(connection, bikerUserId, deliveryDay);
				lunchCapacity += lc;
				System.out.println("LC:"+lc);
			}
		}
		return lunchCapacity;
	}*/
	
	public static int getSingleBikerLunchCartValue(Connection connection, String area, String deliveryDay,
			ArrayList<Integer> kitchenList, boolean isOrderSpecialTimings){
		int lunchCapacity = 0;
	
		for(Integer kitchenId : kitchenList){
			ArrayList<String> bikerList = new ArrayList<String>();
			
			bikerList = BikerDAO.findSingleTypeBikerOfKitchen(connection, kitchenId);		
			
			for(String bikerUserId : bikerList){
				int lc = BikerDAO.getSigleTypeAvailableLunchQuantity(connection, bikerUserId, deliveryDay,isOrderSpecialTimings);
				lunchCapacity += lc;
				System.out.println("LC for single biker:"+lc);
			}
		}
		System.out.println("LC-------:"+lunchCapacity);
		
		return lunchCapacity;
	}
	
	public static int getSingleBikerDinnerCartValue(Connection connection, String area, String deliveryDay,ArrayList<Integer> kitchenList){
		int dinnerCapacity = 0;
	
		for(Integer kitchenId : kitchenList){
			ArrayList<String> bikerList = new ArrayList<String>();
			
			bikerList = BikerDAO.findSingleTypeBikerOfKitchen(connection, kitchenId);		
			
			for(String bikerUserId : bikerList){
				int dc = BikerDAO.getSigleTypeAvailableDinnerQuantity(connection, bikerUserId, deliveryDay);
				dinnerCapacity += dc;
				System.out.println("DC for single biker:"+dc);
			}
		}
		System.out.println("DC-------:"+dinnerCapacity);
		
		return dinnerCapacity;
	}
}
