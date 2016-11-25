package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class FetchCuisineDAO {

	public static JSONObject fetchAllCuisineWithItemData(String pincode, String deliveryDay, String mobileNo, String area) throws Exception{
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONObject cuisineList = new JSONObject();
    	JSONArray cuisinesarrayList = new JSONArray();
    	JSONObject allcuisine = new JSONObject();
    	boolean isSingleOrderLunchAvailable = false,isSingleOrderDinnerAvailable = false,
    			isMultipleOrderLunchAvailable = true,isMultipleOrderDinnerAvailable = true,isOrderBetweenSpecialTime = false;
    	String alertMessage = "",multiOrderLunchAlertMessage = "",multiOrderDinnerAlertMessage = "";int cartCapacity = 0;
    	boolean[] isSingleOrder = new boolean[2];
    	int[] cartValue = new int[2];
    	int lunchCart = 0, dinnerCart = 0 ;
    	String[] slotTimings = new String[2];
    	
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String currentTime = sdf.format(date);
		
    	
    	try {
			SQL:{
	    		connection = DBConnection.createConnection();
	    		slotTimings = SlotDAO.getSlotTimings(connection);
	    		String initialTimings = slotTimings[0];
	    		String finalTimings = slotTimings[1];
	    		if(OrderTimeDAO.isTimeBetweenTwoTime(initialTimings, finalTimings, currentTime)  ){//showing lunch slot 2-3 for order time 11-12 
	    			System.out.println("Menu time: "+currentTime);
	    			isOrderBetweenSpecialTime = true;
	    		}
	    		ArrayList<Integer> kitchenList = KitchenDAO.findKitchensInArea(connection, area);
	    		
	    		//cartCapacity = SingleOrderDAO.getCartCapacity(connection,area);
	    		isSingleOrder = SingleOrderDAO.isSingleOrderAvailable(area, deliveryDay, connection);//single order availability
	    		cartValue = SingleOrderDAO.getCartValue(connection, area, deliveryDay,kitchenList,isOrderBetweenSpecialTime);//multiple order availability
	    		lunchCart = cartValue[0];
    			if(lunchCart==0){
    				System.out.println("Multiple biker not available for lunch");
    				isMultipleOrderLunchAvailable = false;
    				/*multiOrderLunchAlertMessage = "Currently we dont have biker to process multiple order for lunch!"
    						+ "\nPlease order single quantity.";*/
    				multiOrderLunchAlertMessage = "Currently all our delivery boys are busy for delivery of multiple order."
    						+ "\n Please only order single quantity & we will deliver.";
    				lunchCart = SingleOrderDAO.getSingleBikerLunchCartValue(connection, area, deliveryDay, kitchenList, isOrderBetweenSpecialTime);
    			}
	    		
	    		dinnerCart = cartValue[1];
	    		if(dinnerCart==0){
	    			System.out.println("Multiple biker not available for dinner");
	    			isMultipleOrderDinnerAvailable = false;
    				/*multiOrderDinnerAlertMessage =  "Currently we dont have biker to process multiple order for dinner!"
    						+ "\nPlease order single quantity.";*/
	    			multiOrderDinnerAlertMessage = "Currently all our delivery boys are busy for delivery of multiple order."
    						+ "\n Please only order single quantity & we will deliver.";
    				dinnerCart = SingleOrderDAO.getSingleBikerDinnerCartValue(connection, area, deliveryDay, kitchenList);
    			}
	    		/*if(isOrderBetweenSpecialTime){
    			lunchCart = SingleOrderDAO.getSpecialLunchCartValue(connection, area, deliveryDay,kitchenList);
    			}else{
    			lunchCart = cartValue[0];
    			if(lunchCart==0){
    				isMultipleOrderLunchAvailable = false;
    				multiOrderLunchAlertMessage = "Currently multiple order not possible, as our biker not available for lunch!";
    				lunchCart = SingleOrderDAO.getSingleBikerLunchCartValue(connection, area, deliveryDay, kitchenList);
    			}
    			}*/
	    		isSingleOrderLunchAvailable = isSingleOrder[0];
	        	isSingleOrderDinnerAvailable = isSingleOrder[1];
	        	
	        	String sql = "SELECT cuisin_id,cuisin_name,cuisine_image FROM fapp_cuisins "
    				+ " WHERE is_active = 'Y' order by cuisin_id  ";
	    		try {
					preparedStatement = connection.prepareStatement(sql);
					
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject tempCuisine = new JSONObject();
						
						tempCuisine.put("cuisineid", resultSet.getInt("cuisin_id"));
						String tempImage  = resultSet.getString("cuisine_image");
						String cuisineImage;
						if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
							cuisineImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}else if(tempImage.startsWith("http://")){
							cuisineImage = tempImage.replace("http://", "");
						}else{
							cuisineImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}
						tempCuisine.put("cuisineimage", cuisineImage);
						tempCuisine.put("cuisinename", resultSet.getString("cuisin_name"));
						tempCuisine.put("categorylist", FetchCategory.fetchCategoriesOfCuisineWithPincode(tempCuisine.getInt("cuisineid"),pincode,
								connection,deliveryDay,mobileNo,area));
						cuisinesarrayList.put(tempCuisine);
					}
					if(!FetchCuisineDAO.isAllActive()){
						allcuisine.put("cuisineid", cuisinesarrayList.length()+1);
						//http://i.imgur.com/o0HO5pL.png
			        	allcuisine.put("cuisineimage", "i.imgur.com/o0HO5pL.png");
			        	allcuisine.put("cuisinename", "All");
			        	allcuisine.put("categorylist", FetchCategory.fetchCategoriesOfAllCuisineWithPincode(pincode,connection,deliveryDay,mobileNo,area));
			        	cuisinesarrayList.put(allcuisine);
					}
					
		        
				}  catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("Lunch : "+lunchCart+" Dinner : "+dinnerCart);
    	cuisineList.put("status", "200");
    	cuisineList.put("message", "Our serving menus.");
    	if(!isMultipleOrderLunchAvailable){
    		cuisineList.put("isMultipleOrderLunchAvailable", isMultipleOrderLunchAvailable);
        	cuisineList.put("multipleLunchAlert", multiOrderLunchAlertMessage);
    	}else{
    		cuisineList.put("isMultipleOrderLunchAvailable", isMultipleOrderLunchAvailable);
        	cuisineList.put("multipleLunchAlert", multiOrderLunchAlertMessage);
    	}
    	
    	if(!isMultipleOrderDinnerAvailable){
    		cuisineList.put("isMultipleOrderDinnerAvailable", isMultipleOrderDinnerAvailable);
        	cuisineList.put("multipleDinnerAlert", multiOrderDinnerAlertMessage);
    	}else{
    		cuisineList.put("isMultipleOrderDinnerAvailable", isMultipleOrderDinnerAvailable);
        	cuisineList.put("multipleDinnerAlert", multiOrderDinnerAlertMessage);
    	}
    	
    	cuisineList.put("isSingleOrderLunchAvailable", isSingleOrderLunchAvailable);
    	if(!isSingleOrderLunchAvailable){
    		System.out.println("Single biker not available for lunch");
    		
    		/*alertMessage =  "Currently we dont have biker to process single order for lunch!"
					+ "\nPlease order more than asingle quantity.";*/
    		alertMessage = "Currently all our delivery boys are busy for delivery of single order."
					+ "\n Please order multiple quantity & we will deliver.";
    		cuisineList.put("lunchAlert", alertMessage);
    	}else{
    		cuisineList.put("lunchAlert", alertMessage);
    	}
    	cuisineList.put("isSingleOrderDinnerAvailable", isSingleOrderDinnerAvailable);
    	if(!isSingleOrderDinnerAvailable){
    		System.out.println("Single biker not available for dinner");
    		
    		/*alertMessage = "Currently we dont have biker to process single order for dinner!"
					+ "\nPlease order more than asingle quantity.";*/
    		alertMessage = "Currently all our delivery boys are busy for delivery of single order."
					+ "\n Please order multiple quantity & we will deliver.";
    		cuisineList.put("dinnerAlert", alertMessage);
    	}else{
    		cuisineList.put("dinnerAlert", alertMessage);
    	}
    	cuisineList.put("cartCapacity", cartCapacity);
    	cuisineList.put("lunchCartCapacity", lunchCart);
    	cuisineList.put("dinnerCartCapacity", dinnerCart);
    	cuisineList.put("cuisinelist", cuisinesarrayList);
    	return cuisineList;
    }
	
	
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
		 if(mobileNo!=null ){
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
		 }else{
			 isNewUser = false;
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
