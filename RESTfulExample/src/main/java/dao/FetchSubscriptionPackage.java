package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mkyong.rest.DBConnection;

public class FetchSubscriptionPackage {

	public static JSONObject fetchPackages(){
		JSONObject packageJson = new JSONObject();
		try {
			packageJson.put("status", "200");
			packageJson.put("message", "Packages Found!");
			packageJson.put("packageList", fetchPackageJsonArray());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packageJson;
	}
	
	public static JSONArray fetchPackageJsonArray(){
		JSONArray packageJsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT package_master_id, package_name, no_of_days, button_name "
							+ "FROM fapp_subs_package_master where is_active ='Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject packageJson = new JSONObject();
							int packageId = resultSet.getInt("package_master_id");
							packageJson.put("packageId", packageId);
							packageJson.put("packageName", resultSet.getString("package_name"));
							packageJson.put("no_of_days", resultSet.getInt("no_of_days"));
							packageJson.put("buttonDescription", resultSet.getString("button_name"));
							packageJson.put("packageMealList", fetchPackageMealsJsonArray(packageId, connection));
							packageJsonArray.put(packageJson);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR IN fetchPackageJsonArray");
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
		return packageJsonArray;
	}
	
	public static JSONArray fetchPackageMealsJsonArray(int packageId, Connection connection){
		JSONArray mealJsonArray = new JSONArray();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT meal_type_master_id,meal_type,package_meal_type_price "
							+ "FROM vw_subs_package_meals where package_master_id=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, packageId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject mealJson = new JSONObject();
							int mealTypeId = resultSet.getInt("meal_type_master_id");
							mealJson.put("mealTypeId", mealTypeId);
							mealJson.put("mealTypeName", resultSet.getString("meal_type"));
							mealJson.put("mealPrice", resultSet.getInt("package_meal_type_price"));
							mealJson.put("mealDescriptionList", fetchPackageMealsDescriptionJsonArray(packageId, mealTypeId, connection));
							mealJson.put("mealItemList", fetchPackageMealItemsJsonArray(packageId, mealTypeId, connection));
							mealJsonArray.put(mealJson);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR IN fetchPackageMealsJsonArray");
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						/*if(connection!=null){
							connection.close();
						}*/
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mealJsonArray;
	}
	
	public static JSONArray fetchPackageMealsDescriptionJsonArray(int packageId, int mealTypeId, Connection connection){
		JSONArray packageMealsDescriptionJsonArray = new JSONArray();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT meal_descriptions_id, package_meal_description "
							+ "FROM fapp_subs_package_meal_descriptions where package_master_id =? "
							+ " AND package_meal_type_id= ? AND is_active ='Y' order by meal_descriptions_id asc ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, packageId);
						preparedStatement.setInt(2, mealTypeId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject descJson = new JSONObject();
							int mealDescId = resultSet.getInt("meal_descriptions_id");
							descJson.put("descriptionId", mealDescId);
							descJson.put("description", resultSet.getString("package_meal_description"));
							packageMealsDescriptionJsonArray.put(descJson);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR IN fetchPackageMealsDescriptionJsonArray");
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						/*if(connection!=null){
							connection.close();
						}*/
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return packageMealsDescriptionJsonArray;
	}
	
	public static JSONArray fetchPackageMealItemsJsonArray(int packageId, int mealTypeId, Connection connection){
		JSONArray packageMealItemsJsonArray = new JSONArray();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT items_master_item_id, item_name,item_image,item_description,no_of_days "
							+ "FROM vw_subs_meal_items where package_master_id =? "
							+ " AND package_meal_type_id= ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, packageId);
						preparedStatement.setInt(2, mealTypeId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemJson = new JSONObject();
							int itemId = resultSet.getInt("items_master_item_id");
							String tempImage  = resultSet.getString("item_image");
							String itemImage = "";
							if(tempImage!=null){
								if(tempImage.startsWith("http://")){
									itemImage = tempImage.replace("http://", "");
								}
							}else{
								
							}
							itemJson.put("itemId", itemId);
							itemJson.put("itemName", resultSet.getString("item_name"));
							itemJson.put("itemImage", itemImage);
							String itemDescription = resultSet.getString("item_description");
							if(itemDescription!=null){
								itemJson.put("itemDescription", itemDescription);
							}else{
								itemJson.put("itemDescription", "");
							}
							
							itemJson.put("noOfDay", resultSet.getInt("no_of_days"));
							packageMealItemsJsonArray.put(itemJson);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR IN fetchPackageMealItemsJsonArray");
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						/*if(connection!=null){
							connection.close();
						}*/
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return packageMealItemsJsonArray;
	}
	
	
	public static JSONObject fetchNoOfMembers(){
		JSONObject memberJson =  new JSONObject();
		JSONArray memberJsonArray = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT package_members_id,member,no_of_member"
							+ " from fapp_subs_package_members where is_active='Y' order by package_members_id";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject member =  new JSONObject();
							member.put("memberId", resultSet.getInt("package_members_id"));
							member.put("memberText", resultSet.getString("member"));
							member.put("noOfMember", resultSet.getInt("no_of_member"));
							memberJsonArray.put(member);
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
		try {
			if(memberJsonArray.length() > 0){
				memberJson.put("status", "200");
				memberJson.put("message", "Members Found!");
				memberJson.put("memberList", memberJsonArray);
			}else{
				memberJson.put("status", "204");
				memberJson.put("message", "Members not Found!");
				memberJson.put("memberList", memberJsonArray);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberJson;
	}
}
