package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mkyong.rest.DBConnection;

public class RoundRobin {
	
	public static void updateCurrentAndFutureStatus(int kitchenId, int cuisine){
		System.out.println("inside updateCurrentAndFutureStatus ordered block kitchen: "+kitchenId+" cuisine:: "+cuisine);
		try {
			SQL:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql ="";
				if(cuisine==1){
					sql = "UPDATE fapp_order_token set cv='Y', fv='N' where kid = ?";
				}else{
					sql = "UPDATE fapp_order_token_ni set cv='Y', fv='N' where kid = ?";
				}
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, kitchenId);
					System.out.println("Current :: "+preparedStatement);
					int count = preparedStatement.executeUpdate();
					if(count>0){
						//System.out.println("Current and future updated for current kitchen!");
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
	}
	
	public static boolean alreadyOrdered(int kitchenId, int cuisine){
		boolean alreadyOrdered =false;
		String cv=null,fv=null;
		System.out.println("inside already ordered block kitchen: "+kitchenId+" cuisine:: "+cuisine);
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(cuisine==1){
						sql = "select cv,fv from fapp_order_token where kid = ?";
					}else{
						sql = "select cv,fv from fapp_order_token_ni where kid = ?";
					}
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							cv = resultSet.getString("cv");
							fv = resultSet.getString("fv");
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
			e.printStackTrace();
		}
		if(cv.equals("Y") && fv.equals("N")){
			alreadyOrdered = true;
			
		}else if(cv.equals("N") && fv.equals("Y")){
			alreadyOrdered = false;
			
		}
		return alreadyOrdered;
	}
	
	public static void makeAllFree(ArrayList<Integer> kitchenIds, int cuisine){
		System.out.println("-- MAke all kitchen unordered!");
		String ids = kitchenIds.toString();
		if(ids.startsWith("[")){
			ids = ids.replace("[", "(");
		}
		if(ids.endsWith("]")){
			ids = ids.replace("]", ")");
		}
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "";
					if(cuisine==1){
						sql = "UPDATE fapp_order_token set cv = 'N',fv='Y' where kid IN"+ids;
					}else{
						sql = "UPDATE fapp_order_token_ni set cv = 'N',fv='Y' where kid IN"+ids;
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						int c = preparedStatement.executeUpdate();
					} catch (Exception e) {
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
	}
}