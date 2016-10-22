package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class FetchBannersDAO {

	public static JSONArray fetchBanners(){
		JSONArray jsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select banner_details_id,banner_image from fapp_banner_details";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject banner = new JSONObject();
							banner.put("bannerId", resultSet.getString("banner_details_id"));
							String tempImage  = resultSet.getString("banner_image");
							String cuisineImage;
							if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
								cuisineImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}else if(tempImage.startsWith("http://")){
								cuisineImage = tempImage.replace("http://", "");
							}else{
								cuisineImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}
							banner.put("bannerImage",cuisineImage);
							jsonArray.put(banner);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(connection!=null){
						connection.close();
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return jsonArray;
	}
}
