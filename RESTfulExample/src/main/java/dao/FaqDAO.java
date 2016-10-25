package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sql.FaqSql;

import com.mkyong.rest.DBConnection;

public class FaqDAO {
	public static JSONObject fetchAllFaqs() throws JSONException{
		JSONArray faqList = new JSONArray();
		try {
			SQL:{ 
			Connection connection = DBConnection.createConnection();			        		
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			preparedStatement = connection.prepareStatement(FaqSql.loadAllFaqsQuery);
			try {
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					JSONObject faq = new JSONObject();

					faq.put("faqId", String.valueOf(resultSet.getInt("faq_id")));
					faq.put("faqQuestion", resultSet.getString("faq_question"));
					faq.put("faqAnswer", resultSet.getString("faq_answer"));

					faqList.put(faq);
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
			e.printStackTrace();
		}
		
		JSONObject faqJson = new JSONObject();
		if(faqList.length()>0){
			faqJson.put("status", "200");
			faqJson.put("message", "Faq Found!");
			faqJson.put("faqList", faqList);
		}else{
			faqJson.put("status", "204");
			faqJson.put("message", "Internal error!");
			faqJson.put("faqList", faqList);
		}
		return faqJson;
	}
}
