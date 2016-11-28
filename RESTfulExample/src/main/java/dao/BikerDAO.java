package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Biker;
import pojo.MealTypePojo;

import com.mkyong.rest.DBConnection;

public class BikerDAO {

	public static Biker getDriverDetailsFromKitchen(String orderNo, String kitchenName){
		Biker biker = new Biker();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select delivery_boy_name,delivery_boy_phn_number,driver_boy_user_id from vw_biker_from_kitchen  "
    						+ "where order_id=(select order_id from fapp_orders where order_no = ?) and kitchen_id = "
    						+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?)";
					/*String sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
								+" WHERE delivery_boy_user_id = ? ";*/
					try {
						preparedStatement = connection.prepareStatement(sql);
						/*preparedStatement.setString(1, userID);*/
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							biker.setBikerName(resultSet.getString("delivery_boy_name"));
							biker.setBikerContact(resultSet.getString("delivery_boy_phn_number")); 
							biker.setUserId(resultSet.getString("driver_boy_user_id"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("On notify biker details for message ::");
		System.out.println("Biker name: "+biker.getBikerName());
		System.out.println("Biker id: "+biker.getUserId());
		System.out.println("Biker phn: "+biker.getBikerContact());
		return biker;
	}
	
	public static Biker getDriverDetails(String userID){
		Biker biker = new Biker();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
					+" WHERE delivery_boy_user_id = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, userID);
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							biker.setBikerName(resultSet.getString("delivery_boy_name"));
							biker.setBikerContact(resultSet.getString("delivery_boy_phn_number")); 
							biker.setUserId(userID);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("On pickUp biker details for customer ::");
		System.out.println("Biker name: "+biker.getBikerName());
		System.out.println("Biker id: "+biker.getUserId());
		System.out.println("Biker phn: "+biker.getBikerContact());
		return biker;
	}
	
	public static JSONObject reachedKitchen(String boyUserId, String orderNo, String kitchenId) throws JSONException{
		JSONObject reachedJson = new JSONObject();
		boolean reached =false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking set driver_reached = 'Y', driver_arrival_time=current_timestamp"
							+ " where driver_boy_user_id = ? and kitchen_id = "
							+ " (select kitchen_id from fapp_kitchen where kitchen_name= ?) and order_id ="
							+ " (select order_id from fapp_orders where order_no = ? )";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						preparedStatement.setString(2, kitchenId);
						preparedStatement.setString(3, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count > 0){
							reached = true;
						}
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
		if(reached){
			reachedJson.put("status", "200");
			reachedJson.put("message", "Biker reached");
			reachedJson.put("value", true);
		}else{
			reachedJson.put("status", "204");
			reachedJson.put("message", "Biker reached failed");
			reachedJson.put("value", false);
		}
		return reachedJson;
		
	}
	
	/**
	 * This method returns the bikers of kitchen
	 * @param kicthenId
	 * @return
	 */
	public static ArrayList<String> findBikerOfKitchen(int kicthenId,boolean isSingleOrder){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";
			if(isSingleOrder){
				sql ="select delivery_boy_user_id"
					+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y' "
					+ " and is_single_order_biker='Y'";
			}else{
				sql ="select delivery_boy_user_id"
						+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'"
						+ " and is_single_order_biker='N'";
			}
					
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, kicthenId);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					bikerList.add(resultSet.getString("delivery_boy_user_id"));
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
		System.out.println(bikerList);
		return bikerList;
	}
	
	/**
	 * This method returns the multi type bikers of kitchen
	 * @param kicthenId
	 * @return
	 */
	public static ArrayList<String> findMultiTypeBikerOfKitchen(Connection connection, int kicthenId){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";

			sql ="select delivery_boy_user_id"
					+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'"
					+ " and is_single_order_biker='N'";

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, kicthenId);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					bikerList.add(resultSet.getString("delivery_boy_user_id"));
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
		System.out.println(bikerList);
		return bikerList;
	}
	
	/**
	 * This method returns the single type bikers of kitchen
	 * @param kicthenId
	 * @return
	 */
	public static ArrayList<String> findSingleTypeBikerOfKitchen(Connection connection, int kitchenId){
		ArrayList<String> bikerList = new ArrayList<String>();
		try {
			SQL:{
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "";

			sql ="select delivery_boy_user_id"
					+ " from fapp_delivery_boy where kitchen_id = ? and is_active = 'Y'"
					+ " and is_single_order_biker='Y'";

			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, kitchenId);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					bikerList.add(resultSet.getString("delivery_boy_user_id"));
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
		System.out.println(bikerList);
		return bikerList;
	}
	
	public static int[] getBikerCapacityAndOrders(){
		int[] bikerCapa =  new int[2];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select biker_capacity,serving_location_per_slot from fapp_biker_capacity"
							+ " where is_active='Y' and is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							bikerCapa[0] = resultSet.getInt("biker_capacity");
							bikerCapa[1] = resultSet.getInt("serving_location_per_slot");
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
		return bikerCapa;
	}
	
	public static int[] getBikerCapacityAndOrders(Connection connection){
		int[] bikerCapa =  new int[2];
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select biker_capacity,serving_location_per_slot from fapp_biker_capacity"
							+ " where is_active='Y' and is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							bikerCapa[0] = resultSet.getInt("biker_capacity");
							bikerCapa[1] = resultSet.getInt("serving_location_per_slot");
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
		return bikerCapa;
	}
	
	
	public static int getAvailableLunchQuantity(Connection connection, String bikerUserId, String deliveryday ,boolean isOrderSpecialTiming){
		int noOfFreeSlots = 0 ,lunchSlot = 0;
		lunchSlot = getNoOfLunchSlot(connection);
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders(connection);
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/* noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots = (Integer)(bikerCapacity*lunchSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			if(isOrderSpecialTiming){
				noOfFreeSlots = (bikerCapacity*1) - noOfFreeSlots;
			}else{
				noOfFreeSlots = (bikerCapacity*lunchSlot) - noOfFreeSlots;
			}		
		}
		System.out.println("free lunch: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	public static int getAvailableDinnerQuantity(Connection connection, String bikerUserId, String deliveryday ){
		int noOfFreeSlots = 0 ,dinnerSlot = 0;
		dinnerSlot = getNoOfDinnerSlot(connection);
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/*noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots =(Integer) (bikerCapacity*dinnerSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			noOfFreeSlots = (bikerCapacity*dinnerSlot) - noOfFreeSlots;	
		}
		System.out.println("free dinner: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	
	public static int getSigleTypeAvailableLunchQuantity(Connection connection, String bikerUserId, 
			String deliveryday, boolean isOrderSpecialTimings ){
		int noOfFreeSlots = 0 ,lunchSlot = 0;
		lunchSlot = getNoOfLunchSlot(connection);
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders(connection);
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_lunch='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/* noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots = (Integer)(bikerCapacity*lunchSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			if(isOrderSpecialTimings){
				noOfFreeSlots = (bikerOrders*1) - noOfFreeSlots;
			}else{
				noOfFreeSlots = (bikerOrders*lunchSlot) - noOfFreeSlots;
			}
				
		}
		System.out.println("free lunch: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	public static int getSigleTypeAvailableDinnerQuantity(Connection connection, String bikerUserId, String deliveryday ){
		int noOfFreeSlots = 0 ,dinnerSlot = 0;
		dinnerSlot = getNoOfDinnerSlot(connection);
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0];
		int bikerOrders = bikerCapa[1];
		boolean found = false;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(deliveryday.equalsIgnoreCase("TODAY") ){
						sql = "select quantity AS free from vw_driver_today_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}else{
						sql = "select quantity AS free from vw_driver_tomorrow_status "
								+" where driver_user_id = ? "
								+" and is_slot_locked = 'N' and is_dinner='Y' "
								+" and (quantity<? or no_of_orders <?) " ;
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, bikerUserId);
						preparedStatement.setInt(2, bikerCapacity);
						preparedStatement.setInt(3, bikerOrders);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							found = true;
							int free = resultSet.getInt("free");   
							noOfFreeSlots = noOfFreeSlots + free;
							/*noOfFreeSlots = (Integer) resultSet.getObject("free");
								//noOfFreeSlots=Integer.valueOf(noOfFreeSlots);
								if(noOfFreeSlots==null){
									noOfFreeSlots = 0;
								}else{
									noOfFreeSlots =(Integer) (bikerCapacity*dinnerSlot) - noOfFreeSlots;		
								}	*/
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
		if(!found){
			noOfFreeSlots = 0;
		}else{
			noOfFreeSlots = (bikerOrders*dinnerSlot) - noOfFreeSlots;	
		}
		System.out.println("free dinner: "+noOfFreeSlots);
		return noOfFreeSlots ;
	}
	
	public static int getNoOfLunchSlot(Connection connection){
		int lunchSlot =  0 ;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = " select count(is_lunch)AS lunch "
								+" from fapp_timeslot where "
								+" is_lunch='Y' AND "
								+" is_active='Y' and is_delete='N' ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							lunchSlot = resultSet.getInt("lunch");
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
		return lunchSlot;
	}
	
	public static int getNoOfDinnerSlot(Connection connection){
		int dinnerSlot =  0 ;
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = " select count(is_dinner)AS dinner "
								+" from fapp_timeslot where "
								+" is_dinner='Y' AND "
								+" is_active='Y' and is_delete='N' ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							dinnerSlot = resultSet.getInt("dinner");
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
		return dinnerSlot;
	}
}
