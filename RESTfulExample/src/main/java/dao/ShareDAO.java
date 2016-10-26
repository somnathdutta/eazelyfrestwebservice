package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import pojo.Share;

import com.mkyong.rest.DBConnection;

public class ShareDAO {

	public static JSONObject fetchShareEarn() throws JSONException{
		JSONObject shareJsonObject = new JSONObject();
		boolean dataFound = false;
		Share share = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "Select app_msg,inviting_text,amount,img_url from fapp_share_and_earn where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							dataFound = true;
							share = new Share();
							String tempImage =  resultSet.getString("img_url");
							String shareImage;
							if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
								shareImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}else if(tempImage.startsWith("http://")){
								shareImage = tempImage.replace("http://", "");
							}else{
								shareImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}
							share.setImageUrl(shareImage);
							share.setAppMessage((resultSet.getString("app_msg")));
							share.setInviteMessage(resultSet.getString("inviting_text"));
							share.setAmount( resultSet.getDouble("amount"));
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
		if(dataFound){
			shareJsonObject.put("status", "200");
			shareJsonObject.put("message", "Data found!");
			shareJsonObject.put("imageUrl", share.getImageUrl());
			shareJsonObject.put("appMessage", share.getAppMessage());
			shareJsonObject.put("inviteMessage", share.getInviteMessage());
			shareJsonObject.put("amount", String.valueOf(share.getAmount()));
		}else{
			shareJsonObject.put("status", "204");
			shareJsonObject.put("message", "No Data found!");
			shareJsonObject.put("imageUrl", "");
			shareJsonObject.put("appMessage", "");
			shareJsonObject.put("inviteMessage", "");
			shareJsonObject.put("amount", "");
		}
		return shareJsonObject;
	}
	
	public static double getShareCredit(){
		double credit = 0.0;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select amount from fapp_share_and_earn where is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							credit = resultSet.getDouble("amount");
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
		return credit;
	}
}
