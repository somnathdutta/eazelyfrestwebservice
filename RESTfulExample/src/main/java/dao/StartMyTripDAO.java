package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;
import com.mkyong.rest.StartTripBean;

public class StartMyTripDAO {

	public static JSONObject startMyTripForOrders(ArrayList<StartTripBean> startTripBeanList, String gpsAddress, 
			String latitude, String longitude) throws JSONException{
		JSONObject jsonAddressStatus = new JSONObject();
		boolean addressUpdated = false;
		
		for(int i=0;i<startTripBeanList.size();i++){
			insertDriverAddress( gpsAddress, latitude, longitude, startTripBeanList.get(i).orderNo, startTripBeanList.get(i).boyUserId);
		}
		
		if(updateBoyAddress(gpsAddress, latitude, longitude, startTripBeanList)){
			System.out.println("Boy "+ startTripBeanList.get(0).boyUserId+" Address updated in fapp_order_tracking table!");
			addressUpdated = true;
		}
		
		if(addressUpdated){
			jsonAddressStatus.put("status", "200");
			jsonAddressStatus.put("message", "Location Updated");
		}else{
			jsonAddressStatus.put("status", "204");
			jsonAddressStatus.put("message", "Location not Updated");
		}
		
		return jsonAddressStatus;
	}
	
	public static void insertDriverAddress( String address,String lat, String lng,String orderNo ,String boyUserID){
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "INSERT INTO fapp_db_address(  address, delivery_lat, delivery_long, order_no, boy_user_id) "
    							+" VALUES (?, ?, ?, ?, ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, address);
						preparedStatement.setString(2, lat);
						preparedStatement.setString(3, lng);
						preparedStatement.setString(4, orderNo);
						preparedStatement.setString(5, boyUserID);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Address inserted in fapp_db_address table!!");
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
    }

	public static boolean updateBoyAddress(  String gpsAddress,String latitude, 
			String longitude, ArrayList<StartTripBean> startTripBeanList){
		boolean updated = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_order_tracking "
							   +" SET start_trip='Y',delivery_boy_address=?, latitude = ?,longitude = ?   "
							   +" WHERE driver_boy_user_id=? AND order_id="
							   + "(select order_id from fapp_orders where order_no = ?) AND "
							   +" delivered = 'N' ";
					try {
						preparedStatement =connection.prepareStatement(sql);
						
						for(StartTripBean startTripBean : startTripBeanList){
							preparedStatement.setString(1, gpsAddress);
							preparedStatement.setString(2, latitude);
							preparedStatement.setString(3, longitude);
							preparedStatement.setString(4, startTripBean.boyUserId );
							preparedStatement.setString(5, startTripBean.orderNo);
							preparedStatement.addBatch();
						}
						
						int[] assigned = preparedStatement.executeBatch();
						for(Integer count : assigned){
							updated = true;
						}
						
					} catch (Exception e) {
						// TODO: handle exception
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
		return updated;
	}
}
