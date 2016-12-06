package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mkyong.rest.DBConnection;

public class BalanceDAO {

	public static boolean reduceMyBalance(String mobileNo, double orderCreditAmount){
		boolean balanceUpdated = false;
		try {
			Connection connection = DBConnection.createConnection();
			SQL1:{
				System.out.println("Balance reducingg called..");
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_accounts SET my_balance = my_balance - ? where mobile_no = ? ";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setDouble(1, orderCreditAmount);
					preparedStatement.setString(2, mobileNo);
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

	public static void updateFriendBalance(String regUserMobileNo, double orderCreditAmount){
		String userMailId= "",refCode = null;
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select ref_code from fapp_accounts where mobile_no = ?";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, regUserMobileNo);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					refCode = resultSet.getString("ref_code");
				}
			} catch (Exception e) {
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

		if(refCode != null){
			System.out.println("Find that person whose ref code is using");
			boolean balUpdated = updateBalanceForFriend(refCode, orderCreditAmount);
		}else{
			System.out.println("No ref code's person found so,Friend's balance not updated!");
		}
	}

	private static boolean updateBalanceForFriend(String referralCode, double orderCreditAmount){
		boolean balanceUpdated = false;
		
		try {
			Connection connection = DBConnection.createConnection();
			SQL1:{
				System.out.println("Balance updating called..");
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_accounts SET my_balance = my_balance + ? where my_code = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setDouble(1, orderCreditAmount);
					preparedStatement.setString(2, referralCode);
					System.out.println(preparedStatement);
					int count = preparedStatement.executeUpdate();
					if(count>0){
						balanceUpdated = true;
					}
				} catch (Exception e) {
					System.out.println("Balance updation failed in updateBalance() due to: "+e);
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
		System.out.println("Friend's balance updated:: "+balanceUpdated);
		return balanceUpdated;
	}
}
