package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pojo.Address;

import com.mkyong.rest.DBConnection;

public class AddressFinder {

	public static Address getAddressOfUser(String subNo){
    	String address = null, delAddress =  null, pin= null, deliveryZone = null;	
    	Address userAddress =  null;
    	
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "";
    				if(subNo.startsWith("SUB")){
    					sql = "SELECT delivery_address,delivery_zone,pincode from fapp_subscription where subscription_no = ?";
    				}else{
    					sql = "SELECT delivery_address,delivery_zone,pincode from fapp_order_user_details where order_id = "
    							+ " (select order_id from fapp_orders where order_no = ?)";
    				}
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, subNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							delAddress = resultSet.getString("delivery_address");
							pin = resultSet.getString("pincode");
							deliveryZone = resultSet.getString("delivery_zone");
							userAddress = new Address();
							userAddress.setDeliveryAddress(delAddress);
							userAddress.setDeliveryZone(deliveryZone);
							userAddress.setDeliveryPincode(pin);
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
    	//address = delAddress +","+pin+","+"Kolkata";
    	System.out.println("USER ADDRESS: "+userAddress.getDeliveryZone()+" "+userAddress.getDeliveryAddress()
    			+userAddress.getDeliveryPincode());
    	return userAddress;
    }
}
