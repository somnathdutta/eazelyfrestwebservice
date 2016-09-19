package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.mkyong.rest.DBConnection;

public class BalanceDAO {

	 public static boolean reduceMyBalance(String mobileNo){
	    	boolean balanceUpdated = false;
	    	try {
	    		Connection connection = DBConnection.createConnection();
				SQL1:{
	    			System.out.println("Balance reducingg called..");
	    			 PreparedStatement preparedStatement = null;
	    			 String sql = "UPDATE fapp_accounts SET my_balance = my_balance - 50.0 where mobile_no = ? ";
	    			 try {
						preparedStatement = connection.prepareStatement(sql);
						//preparedStatement.setDouble(1, 50);
						preparedStatement.setString(1, mobileNo);
						System.out.println(preparedStatement);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							balanceUpdated = true;
						}
					} catch (Exception e) {
						System.out.println("Balance reducing failed in reduceMyBalance() due to: "+e);
						connection.rollback();
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
	    	System.out.println("My balance reduced:: "+balanceUpdated);
	    	return balanceUpdated;
	    }
}
