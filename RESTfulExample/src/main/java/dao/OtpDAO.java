package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

import utility.OTPGenerator;

public class OtpDAO {

	public static JSONObject sendOtp(String mobileNo, String userType) throws JSONException{
		JSONObject otpJsonObject = new JSONObject();
		if(!userType.equalsIgnoreCase("GUEST")){//FOR REGISTERED USER
			if(SignUpDAO.isMobileExists(mobileNo)){
				//otpJsonObject.put("status", "200");
				//otpJsonObject.put("otpStatus", true);
				String otp = OTPGenerator.generateOTP(4);
				int otpStatus = SendMessageDAO.sendOTP(mobileNo, otp);
				if(otpStatus==200){
					saveOtp(mobileNo, otp);
					otpJsonObject.put("status", "200");
					otpJsonObject.put("otpStatus", true);
					otpJsonObject.put("message", "OTP send to your mobile number");
				}else{
					otpJsonObject.put("status", "200");
					otpJsonObject.put("otpStatus", false);
					otpJsonObject.put("message", "Try again");
				}
				otpJsonObject.put("message", "This Mobile no is already registered!");
			}else{
				String otp = OTPGenerator.generateOTP(4);
				int otpStatus = SendMessageDAO.sendOTP(mobileNo, otp);
				if(otpStatus==200){
					saveOtp(mobileNo, otp);
					otpJsonObject.put("status", "200");
					otpJsonObject.put("otpStatus", true);
					otpJsonObject.put("message", "OTP send to your mobile number");
				}else{
					otpJsonObject.put("status", "200");
					otpJsonObject.put("otpStatus", false);
					otpJsonObject.put("message", "Try again");
				}
			}
		}else{//FOR GUEST USER
			String otp = OTPGenerator.generateOTP(4);
			int otpStatus = SendMessageDAO.sendOTP(mobileNo, otp);
			if(otpStatus==200){
				saveOtp(mobileNo, otp);
				otpJsonObject.put("status", "200");
				otpJsonObject.put("otpStatus", true);
				otpJsonObject.put("message", "OTP send to your mobile number");
			}else{
				otpJsonObject.put("status", "200");
				otpJsonObject.put("otpStatus", false);
				otpJsonObject.put("message", "Try again");
			}
		}
		return otpJsonObject;
	}

	public static void saveOtp(String mobileNo, String otp){
		try {
			if(isOtpMobileExists(mobileNo)){
				updateOTP(mobileNo,otp);
			}else{
				insertOTP(mobileNo,otp);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean isOtpMobileExists(String mobile){
		boolean isMobileExists = false;
		try {
			SQL:{
			Connection connection= DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			String sql = "Select count(mobile_no)As mobile_no from fapp_otp_store where mobile_no = ?";
			ResultSet resultSet = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, mobile);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					int count = resultSet.getInt("mobile_no");
					if(count>0){
						isMobileExists = true;
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
		return isMobileExists;
	}

	public static void updateOTP(String mobileNo, String otp){
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			String sql = "UPDATE fapp_otp_store SET otp=? "
					+ " WHERE mobile_no = ?";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, otp);
				preparedStatement.setString(2, mobileNo);
				int count = preparedStatement.executeUpdate();
				if(count>0){
					System.out.println("OTP send status update!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(connection!=null){
					connection.close();
				}
			}
		} 
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void insertOTP(String mobileNO, String otp){
		try {
			SQL:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			String sql = "INSERT INTO fapp_otp_store(mobile_no, otp) VALUES (?, ?)";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, mobileNO);
				preparedStatement.setString(2, otp);
				int count = preparedStatement.executeUpdate();
				if(count>0){
					System.out.println("OTP saved with us!");
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
	}
	
	public static boolean isValidOtp(String mobileNo, String otp){
		boolean isValidOTP = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement= null;
					ResultSet resultSet = null;
					String sql = "select count(otp)as otp from fapp_otp_store where mobile_no=? and otp=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						preparedStatement.setString(2, otp);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int count = resultSet.getInt("otp");
							if(count>0){
								isValidOTP = true;
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
		return isValidOTP;
	}
	
	public static void deleteOtp(String mobileNo){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "DELETE FROM fapp_otp_store WHERE mobile_no=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Otp deleted!");
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
	}
}
