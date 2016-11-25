package com.mkyong.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;

import pojo.MealTypePojo;
import pojo.Prepack;
import pojo.TimeSlot;
import pojo.User;
import utility.NumberCheck;
import dao.AboutUsDAO;
import dao.AddressDAO;
import dao.AllItemsDAO;
import dao.BikerDAO;
import dao.BookDriver;
import dao.CallPickJiBikerDAO;
import dao.ChangePasswordDAO;
import dao.ContactUsDAO;
import dao.DeliverySlotFinder;
import dao.FaqDAO;
import dao.FetchAlaCarteItemDAO;
import dao.FetchBannersDAO;
import dao.FetchCuisineDAO;
import dao.FetchLocationDAO;
import dao.FindDeliverySlots;
import dao.ForgotPassword;
import dao.ItemDAO;
import dao.KitchenDeliverOrderDAO;
import dao.KitchenNotifyOrderDAO;
import dao.KitchenOrderHistoryDAO;
import dao.KitchenOrdersDAO;
import dao.KitchenReceiveOrderDAO;
import dao.LoginDAO;
import dao.OrderSummaryDAO;
import dao.OrderTimingsDAO;
import dao.OtpDAO;
import dao.PaymentTypeDAO;
import dao.PickJiDAO;
import dao.PlaceSubscriptionOrderDAO;
import dao.PrivacyPolicyDAO;
import dao.PromoCodeDAO;
import dao.QueryTypeDAO;
import dao.SetItemDetailsDao;
import dao.ShareDAO;
import dao.SignUpDAO;
import dao.SingleOrderDAO;
import dao.SlotDAO;
import dao.StartMyTripDAO;
import dao.SubmitFeedBackDAO;
import dao.TermsAndConditionDAO;
import dao.TimeSlotFinder;
import dao.UserDetailsDao;

/*@Path("/json/category")*/
@Path("/category")
public class Category {

	/**
	 * This method is useful for the login check
	 * @param username
	 * @param password
	 * @return JSONObject
	 * @throws Exception
	 */
	@GET
	@Path("/chklogin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject chklogin(@QueryParam("username") String uname,@QueryParam("password")String pwd) throws Exception{
		
		JSONObject jobjChklogin = new JSONObject();

		jobjChklogin = DBConnection.checkLogin(uname, pwd);

		return jobjChklogin;
	}

	@POST
	@Path("/checkkitchenLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject checkKitchenLogin(@FormParam("username") String uname,@FormParam("password")String pwd) throws Exception{

		System.out.println("checkKitchenLogin webservice is called...");
		
		JSONObject jobjChkKitchenlogin = new JSONObject();

		jobjChkKitchenlogin = DBConnection.checkKitchenLogin(uname, pwd);
		System.out.println("chkkitchen login webservice object::"+jobjChkKitchenlogin);

		return jobjChkKitchenlogin;
	}

	@POST
	@Path("/checkdeliveryBoyLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject checkdeliveryBoyLogin(@FormParam("boyid") String uname,@FormParam("password")String pwd) throws Exception{

		System.out.println("checkdeliveryBoyLogin webservice is called..."+uname+" pwd->"+pwd);

		JSONObject jobjChkDelBoylogin = new JSONObject();

		jobjChkDelBoylogin = DBConnection.checkdeliveryBoyLogin(uname, pwd);

		return jobjChkDelBoylogin;
	}



	/*@GET
	@Path("/getOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getOrders(@QueryParam("userid") String uname, @QueryParam("password") String pwd) throws Exception{

		System.out.println("getOrders webservice is called...");

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.getOrders(uname, pwd);

		return jorders;
	}*/



	@POST
	@Path("/shareRegId")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject shareRegId(@FormParam("regid")String regid , @FormParam("emailid") String emailid) throws JSONException{


		System.out.println("shareRegId webservice is called... emailid--"+emailid);

		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", "inserted");
		//jsonObject = DBConnection.shareRegId(regid, emailid);
		return jsonObject;
	}


	@POST
	@Path("/saveaddress")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject saveAddress(@FormParam("addresstype")String addressType,
			@FormParam("mailid")String mailId,
			@FormParam("flatno")String flatNo,
			@FormParam("streetname")String streetName,
			@FormParam("landmark")String landMark,
			@FormParam("city")String city,
			@FormParam("location")String location,
			@FormParam("pincode")String pincode,
			@FormParam("name")String name,
			@FormParam("mobileno")String phNumber,
			@FormParam("user")String user,
			@FormParam("deliveryzone")String deliveryZone,
			@FormParam("deliveryaddress")String deliveryAddress,
			@FormParam("instruction")String instruction
			/*@FormParam("email")String email*/) throws JSONException{
		System.out.println("---------------------------------------------");
		System.out.println(" SAVEADDRESS API CALLED ");
		System.out.println("address type-->"+addressType+" user-->"+user);
		System.out.println("Mailid->"+mailId+" pincode->"+pincode);
		System.out.println("Zone-->"+deliveryZone);
		System.out.println("user length-->"+user.length());
		JSONObject saveAddress ; 
		/*if(email != null ){
			AddressDAO.updateMyEmailID(email, user);
		}*/
		if(mailId!=null){
			AddressDAO.updateMyEmailID(mailId, user);
		}
		saveAddress = DBConnection.saveAddress(addressType, mailId, flatNo, streetName, landMark, city, location, 
				pincode,user,deliveryZone.trim(),deliveryAddress.trim(),instruction.trim());

		System.out.println("saveaddress webservice json response::"+saveAddress);
		System.out.println("----------------------------------------------");
		return saveAddress;
	}

	/*@POST
	@Path("/retrieveaddress")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject retrieveaddress(@FormParam("mobileno")String mobileno,
			@FormParam("addtype")String addressType) throws JSONException{

		System.out.println("retrieveaddress webservice is called...");

		System.out.println("mailid-->"+mailId+ " addtyp-->"+addressType);
		JSONObject retreiveAddress ;

		retreiveAddress = DBConnection.retrieveAddress(mailId, addressType) ;

		System.out.println("retrieveaddress webservice end here * * *  * * * *:"+retreiveAddress);

		return  retreiveAddress;
	}*/


	@POST
	@Path("/fetchalladdresstype")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchalladdresstype(@FormParam("mobileno")String mobNo) throws JSONException{
		System.out.println("fetchalladdresstype web service called*****with mob no->"+mobNo);
		JSONObject fetchalladdresstypeObject = new JSONObject() ;
		/*if(mobNo.length()!=0){
			fetchalladdresstypeObject = DBConnection.fetchalladdresstype(mobNo);

			System.out.println("fetchalladdresstype web service end *****"+fetchalladdresstypeObject);
			return fetchalladdresstypeObject;
		}else{
			fetchalladdresstypeObject.put("addresstypelist", " ");
			return fetchalladdresstypeObject;
		}*/
		/*fetchalladdresstypeObject = DBConnection.fetchalladdresstype(mobNo);*/
		if(AddressDAO.isAddressExists(mobNo)){

			fetchalladdresstypeObject = AddressDAO.fetchalladdresstype(mobNo);
		}else{
			fetchalladdresstypeObject.put("status", "204");
			fetchalladdresstypeObject.put("message", "No address found!");
			fetchalladdresstypeObject.put("addresstypelist", new JSONArray());
			return fetchalladdresstypeObject;
		}

		System.out.println("fetchalladdresstype web service end *****");
		return fetchalladdresstypeObject;

	}

	@POST
	@Path("/deleteaddresstype")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteaddresstype(@FormParam("mobileno")String mobNo,
			@FormParam("addresstype")String addressType) throws JSONException{
		System.out.println("deleteaddresstype web service called***** mob no->"+mobNo+" adresstype->"+addressType);
		JSONObject deleteaddresstype  ;
		/*if(mobNo.length()!=0){
			fetchalladdresstypeObject = DBConnection.fetchalladdresstype(mobNo);

			System.out.println("fetchalladdresstype web service end *****"+fetchalladdresstypeObject);
			return fetchalladdresstypeObject;
		}else{
			fetchalladdresstypeObject.put("addresstypelist", " ");
			return fetchalladdresstypeObject;
		}*/
		deleteaddresstype = DBConnection.deleteaddresstype(mobNo,addressType);
		System.out.println("fetchalladdresstype web service end *****"+deleteaddresstype);
		return deleteaddresstype;

	}

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject login(@FormParam("mobileNumber")String mobNo,
			@FormParam("password")String password ) throws JSONException{
		System.out.println("** kitchen "+mobNo+" is trying for logging with password "+password+"***");
		JSONObject object = null ; 

		if( !NumberCheck.isNumeric(mobNo)){
			object = LoginDAO.checkKitchenlogin(mobNo, password);
		}

		//object = DBConnection.checklogin(mobNo, password);

		System.out.println("## login status::"+object);

		return object;

	}

	@POST
	@Path("/userlogin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject userLogin(@FormParam("mobileNumber")String mobileNo,
			@FormParam("password")String password ) throws JSONException{
		System.out.println("-----------------------------------------");
		System.out.println(" userlogin api called ");
		System.out.println(" MobileNo "+mobileNo+" Password "+password);
		JSONObject object ; 
		/*object = DBConnection.checkUserlogin(mobNo, password);*/
		object = LoginDAO.checkUserlogin(mobileNo, password);
		System.out.println(object);
		System.out.println("-----------------------------------------");
		return object;
	}
	
	@POST
	@Path("/sendOtp")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject sendOTP(@FormParam("mobileNo")String mobileNo,
			@FormParam("userType")String userType) throws JSONException{
		System.out.println("---------------------------------------");
		System.out.println("- - - - - sendOtp API CALLED - - - - - ");
		System.out.println("Mobile no: "+mobileNo+" User Type : "+userType);
		JSONObject otpJsonObject = new JSONObject();
		if(mobileNo!=null || mobileNo.trim().length()>0){
			if(userType!=null || userType.trim().length()>0){
				otpJsonObject = OtpDAO.sendOtp(mobileNo, userType);
			}else{
				otpJsonObject.put("status", "204");
				otpJsonObject.put("otpStatus", false);
				otpJsonObject.put("message", "User Type required!");
			}
		}else{
			otpJsonObject.put("status", "204");
			otpJsonObject.put("otpStatus", false);
			otpJsonObject.put("message", "Mobile no required!");
		}
		System.out.println(otpJsonObject);
		System.out.println("---------------------------------------");
		return otpJsonObject;
	}

	@POST
	@Path("/signup")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject signUp(@FormParam("name")String name,
			@FormParam("email")String email,
			@FormParam("contactnumber")String contactNumber,
			@FormParam("refcode")String referalCode,
			@FormParam("password")String password,
			@FormParam("otp")String otp,
			@FormParam("userType")String userType) throws JSONException{
		System.out.println("Sign up webservice is called...");
		System.out.println("name--"+name+" email-"+email+"  number-"+contactNumber+" password-"+password+
				"Referel code: "+referalCode);
		System.out.println("OTP:"+otp+" User type: "+userType);
		JSONObject jsonObject = new JSONObject(); 
		if(contactNumber.trim().length()==0){
			jsonObject.put("status", false);
			jsonObject.put("message", "Contact number required!");
		}else{
			if(userType.equalsIgnoreCase("GUEST")){
				if(OtpDAO.isValidOtp(contactNumber, otp)){
					OtpDAO.deleteOtp(contactNumber);
					jsonObject.put("status", true);
        			jsonObject.put("message", "Thank you for registration!");
				}else{
					jsonObject.put("status", false);
        			jsonObject.put("message", "Invalid OTP given!");
				}
			}else{
				jsonObject = DBConnection.signUp(name.trim(), email.trim(), contactNumber.trim(),  password, referalCode, otp);
			}
		//object = DBConnection.signUp(name,  contactNumber, password);
		}
		System.out.println("Sign up status::"+jsonObject);

		return jsonObject;
	}
	
	@POST
	@Path("/socialSignup")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject socialSignup(@FormParam("name")String name,
			@FormParam("email")String email,
			@FormParam("contactnumber")String contactNumber,
			@FormParam("refcode")String referalCode,
			@FormParam("password")String password,
			@FormParam("otp")String otp,
			@FormParam("userType")String userType) throws JSONException{
		System.out.println("------ socialSignup webservice is called ------");
		System.out.println("name--"+name+" email-"+email+"  number-"+contactNumber+" password-"+password+
				"Referel code: "+referalCode);
		System.out.println("otp: "+otp);
		System.out.println("User Type: "+userType);
		JSONObject jsonObject = new JSONObject(); 
		if(contactNumber.trim().length()==0){
			jsonObject.put("status", false);
			jsonObject.put("message", "Contact number required!");
		}else{
			jsonObject = SignUpDAO.socialSignup(name.trim(), email.trim(), contactNumber.trim(),  password, referalCode,otp);
		//object = DBConnection.signUp(name,  contactNumber, password);
		}
		System.out.println("socialSignup::"+jsonObject);
		System.out.println("------ socialSignup webservice is end ------");
		return jsonObject;
	}

	@POST
	@Path("/getbalance")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getBalance(@FormParam("mobileno")String mobileNo)throws Exception{
		System.out.println("getbalance web service is called with mob no * * * *"+mobileNo);
		JSONObject jsonObject;
		jsonObject = DBConnection.getBalance(mobileNo);
		System.out.println("getbalance ended ***");
		return jsonObject;

	}

	@POST
	@Path("/forgotPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject forgotPassword(@FormParam("email")String email) throws Exception{
		System.out.println("-----------------------------------------------------------");
		System.out.println("forgotPassword webservice is called...with mail id-->"+email);
		JSONObject object = new JSONObject() ; 
		if(ForgotPassword.emailExists(email)){
			object = DBConnection.forgotPassword(email);
		}else{
			object.put("status", "204");
			object.put("message", "Email id is not registered!");
		}
		System.out.println("forgotPassword status::"+object);
		System.out.println("-----------------------------------------------------------");
		return object;
	}

	@POST
	@Path("/changepassword")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject changePassword(
			@FormParam("phonenumber")String phoneNumber,
			@FormParam("oldpassword")String oldPassword,
			@FormParam("newpassword")String newPassword) throws JSONException{
		System.out.println("------------------------------------------------------");
		System.out.println(" changepassword api called ");
		System.out.println("Phnumber--->"+phoneNumber+" Old password---->"+oldPassword);
		System.out.println("New password--->"+newPassword);
		JSONObject changepasswordObject ; 
		changepasswordObject = ChangePasswordDAO.changePassword(phoneNumber, oldPassword, newPassword);
		System.out.println(changepasswordObject);
		System.out.println("------------------------------------------------------");
		
		return changepasswordObject;
	}

	@POST
	@Path("/getdeliveryorders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getDeliveryOrders(@FormParam("boyid") String uname) throws Exception{

		System.out.println("getdeliveryorders webservice is called..."+uname);

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.getDeliveryOrders(uname);

		return jorders;
	}

	@POST
	@Path("/assigndeliveryboy")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject assignDeliveryBoyForSubscription(@FormParam("orderno")String orderNo,
			@FormParam("day")String dayName,
			@FormParam("meal")String mealType,
			@FormParam("boyid")String boyid) throws JSONException{
		System.out.println(" assigndeliveryboy web service is called orderNo->"+orderNo+" day->"+dayName+" Meal->"+mealType+" boyId->"+boyid);
		JSONObject kitchenorders ; 
		kitchenorders = DBConnection.assignDeliveryBoyForSubscription(orderNo, dayName, mealType, boyid);

		return kitchenorders;
	}

	@POST
	@Path("/fetchdeliveryboy")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getDeliveryBoysForKitchen(@FormParam("kitchenid")String kitchenid) throws JSONException{
		System.out.println(" deliveryboys web service is called..."+kitchenid);
		JSONObject kitchenorders ; 
		kitchenorders = DBConnection.getKitchenDeliveryBoys(kitchenid);

		return kitchenorders;
	}

	@POST
	@Path("/orderdelivered")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject orderDelivered(@FormParam("orderno")String orderNo,
			@FormParam("day")String dayName,
			@FormParam("meal")String mealType,
			@FormParam("boyid")String boyid) throws Exception{

		System.out.println("orderdelivered webservice is called..."+orderNo+" dayName->"+dayName+" meal->"+mealType+" boyid->"+boyid);

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.orderDelivered(orderNo, dayName, mealType, boyid);

		return jorders;
	}

	@POST
	@Path("/getdeliveryordersforbiker")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getDeliveryOrdersForBikers(@FormParam("boyid") String uname) throws Exception{

		System.out.println("getdeliveryordersforbiker webservice is called..."+uname);

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.getdeliveryordersforbiker(uname);

		return jorders;
	}

	@POST
	@Path("/reached")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject reachedKitchen(@FormParam("boyid") String boyUserId, @FormParam("orderno")String orderNo,
			@FormParam("kitchenId")String kitchenId) throws Exception{
		System.out.println("*******************************************************************");
		System.out.println("Biker boy "+boyUserId+" REACHED webservice is called for "+orderNo+" From "+kitchenId);
		System.out.println("*******************************************************************");
		JSONObject jorders = new JSONObject();

		jorders = BikerDAO.reachedKitchen(boyUserId, orderNo, kitchenId);
		System.out.println(jorders);
		return jorders;
	}

	@POST
	@Path("/pickuporder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject pickUpOrder(@FormParam("boyid") String boyUserId, @FormParam("orderno")String orderNo,
			@FormParam("kitchenId")String kitchenId) throws Exception{

		System.out.println("*******************************************************************");
		System.out.println("Biker boy "+boyUserId+" PICKUP webservice is called for "+orderNo+" From "+kitchenId);
		System.out.println("*******************************************************************");

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.pickuporder(boyUserId, orderNo, kitchenId);

		return jorders;
	}

	@POST
	@Path("/deliverorder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deliverOrder(@FormParam("boyid") String boyUserId, @FormParam("orderno")String orderNo,
			@FormParam("kitchenId")String kitchenId) throws Exception{

		System.out.println("*******************************************************************");
		System.out.println("Biker boy "+boyUserId+" DELIVER webservice is called for "+orderNo+" From "+kitchenId);
		System.out.println("*******************************************************************");

		JSONObject jorders = new JSONObject();

		jorders = DBConnection.deliverOrder(boyUserId, orderNo, kitchenId);
		return jorders;
	}


	@POST
	@Path("/starttrip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject startTripForOrders(@FormParam("orderdetails")String orderDetails,
			@FormParam("gpsaddress")String gpsAddress,
			@FormParam("latitude")String latitude,@FormParam("longitude")String longitude) throws Exception{
		System.out.println("START TRIP CALLING * * * * * * * * *with address-"+gpsAddress+" Lat-"+latitude+" Long-"+longitude);
		System.out.println("orderdetails->"+orderDetails+" gps->"+gpsAddress);

		ArrayList<StartTripBean> tripItemList = new  ArrayList<StartTripBean>();

		String[] mystrarr = orderDetails.split("-");
		for(int i=0;i<mystrarr.length;i++){
			String[] mystrnewarr = mystrarr[i].split("\\$");
			//for(int j=0;j<mystrnewarr.length;j++){
			StartTripBean startTripBean = new StartTripBean();
			startTripBean.orderNo = mystrnewarr[0];
			startTripBean.dayName = mystrnewarr[1];
			startTripBean.mealType = mystrnewarr[2];
			startTripBean.boyUserId = mystrnewarr[3];

			tripItemList.add(startTripBean);
			//}
		}
		for(StartTripBean bean : tripItemList){
			System.out.println("Order"+bean.orderNo+" day"+bean.dayName+" type"+bean.mealType+" Uisd"+bean.boyUserId);
		}
		JSONObject jorders = new JSONObject();
		//String lat = "" ,lng = "";
		jorders = DBConnection.startTripForOrders(tripItemList, gpsAddress, latitude, longitude);
		System.out.println("START TRIP END ^ ^ ^ ^ ^ ^ "+jorders);
		return jorders;
	}


	@POST
	@Path("/startmytrip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject startMyTrip(@FormParam("orderdetails")String orderDetails,
			@FormParam("gpsaddress")String gpsAddress,
			@FormParam("latitude")String latitude,
			@FormParam("longitude")String longitude) throws Exception{
		System.out.println("START MY TRIP CALLING * * * * * * * * *with address-"+gpsAddress+" "
				+ "Lat->"+latitude+" Long->"+longitude);
		System.out.println("orderdetails->"+orderDetails);

		ArrayList<StartTripBean> tripItemList = new  ArrayList<StartTripBean>();

		if(! orderDetails.equals("[]")){
			if(orderDetails.startsWith("[")){
				orderDetails = orderDetails.replace("[", "");
			}
			if(orderDetails.endsWith("]")){
				orderDetails = orderDetails.replace("]", "");
			}
			String[] mystrarr = orderDetails.split(",");
			for(String str : mystrarr){
				String[] mystrnewarr = str.split("\\$");
				StartTripBean startTripBean = new  StartTripBean();
				startTripBean.orderNo = mystrnewarr[0];
				startTripBean.boyUserId = mystrnewarr[1];
				tripItemList.add(startTripBean);
			}	
		}else{
			System.out.println("Blank order list!");
		}

		JSONObject jorders = new JSONObject();
		if(orderDetails == null || latitude == null || longitude == null || gpsAddress==null || tripItemList.size()==0){
			jorders.put("status", "400");
			jorders.put("message", "Orderdetails = "+orderDetails+" Latitude = "+latitude+" Longitude = "+longitude+" Gpsaddress = "+gpsAddress);
			System.out.println("START MY TRIP END ^ ^ ^ ^ ^ ^ "+jorders);
			return jorders;
		}else if(tripItemList.size()>0){
			jorders = StartMyTripDAO.startMyTripForOrders(tripItemList, gpsAddress, latitude, longitude);
			System.out.println("START MY TRIP END ^ ^ ^ ^ ^ ^ "+jorders);
			return jorders;
		}else{
			jorders.put("status", "400");
			jorders.put("message", "No order list");
			System.out.println("START MY TRIP END ^ ^ ^ ^ ^ ^ "+jorders);
			return jorders;
		}

	}


	@GET
	@Path("/map")
	@Produces(MediaType.TEXT_HTML)
	public String map(@QueryParam("id")String subcriptionNo) throws Exception{
		System.out.println("Map called . . . . ");
		String[] boylatlng = new String[3];
		boylatlng = DBConnection.trackMyOrder(subcriptionNo);

		String userAddress = DBConnection.getAddressOfUser(subcriptionNo);
		String[] homeltlng = new String[2];
		homeltlng = DBConnection.getLatLongPositions( userAddress );


		String DbDetails[] =  new String[2];;
		DbDetails =	DBConnection.getDriverNameNum(subcriptionNo);

		JSONArray markers =  new JSONArray();
		JSONObject markersource = new JSONObject();
		markersource.put("title", "Food Source");
		markersource.put("lat", boylatlng[0]);
		markersource.put("lng",boylatlng[1]);
		markersource.put("description", boylatlng[2]);

		JSONObject markerdestination =  new JSONObject();
		markerdestination.put("title", "Delivery destination");
		markerdestination.put("lat", homeltlng[0]);
		markerdestination.put("lng", homeltlng[1]);
		markerdestination.put("description", userAddress);

		markers.put(markersource);
		markers.put(markerdestination);


		StringBuilder contentBuilder = new StringBuilder();
		try {
			// BufferedReader in = new BufferedReader(new FileReader("http://192.168.1.116:8080/Myapp/ordertrackmap.html"));
			//BufferedReader in = new BufferedReader(new FileReader("C:\\apache-tomcat-7.0.62\\webapps\\Myapp\\ordertrackmap.html")); 
			//String fileName = "C:\\Bitnami\\tomcatstack-8.0.39-0\\apache-tomcat\\webapps\\Myapp\\ordertrackmap.html";
			String fileName = "C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62\\webapps\\Myapp\\ordertrackmap.html";
			//BufferedReader in = new BufferedReader(new FileReader("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62\\webapps\\Myapp\\ordertrackmap.html")); 
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {
				while ( (str = in.readLine()) != null) {
					contentBuilder.append(str + "\n");
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String newDAta = contentBuilder.toString();
		String data = null;
		StringBuilder markerFDataBuilder = new StringBuilder();
		markerFDataBuilder.append(" var markers =");
		markerFDataBuilder.append(markers.toString());
		if(newDAta.contains("<HERE>")){
			data = newDAta.replaceAll("<HERE>", markerFDataBuilder.toString());
			if(newDAta.contains("<NAME>")){
				System.out.println("NAME TAG EXISTS and plz replace it with->"+DbDetails[0]);
				data   = data.replaceAll("<NAME>", DbDetails[0].toString());
			}
			if(newDAta.contains("<MOBILE>")){
				System.out.println("MOBILE NO TAG EXISTS and plz replace it with->"+DbDetails[1]);
				data = data.replaceAll("<MOBILE>", DbDetails[1].toString());
			}
		}
		return data;
	}

	
	@GET
	@Path("/map1")
	@Produces(MediaType.TEXT_HTML)
	public String mapPickJi(@QueryParam("id")String pickJiOrderId) throws Exception{
		System.out.println("*******************************************");
		System.out.println("**** Pick Ji Map called Service called ****");
		System.out.println("*******************************************");
		JSONObject pickJiBikerJson = CallPickJiBikerDAO.getBikerDetailsFromPickJiAPI(pickJiOrderId);
		String[] boylatlng = new String[3];
		String DbDetails[] =  new String[2];
		String statusCode = pickJiBikerJson.getString("status");
		if(statusCode.equals("200")){
			boylatlng[0] = pickJiBikerJson.getString("latitude");
			boylatlng[1] = pickJiBikerJson.getString("longitude");
			boylatlng[2] = pickJiBikerJson.getString("title");
			DbDetails[0] = pickJiBikerJson.getString("bikerName");
			DbDetails[1] = pickJiBikerJson.getString("bikerContact");
		}
		
		String userAddress = CallPickJiBikerDAO.getUserAddress(pickJiOrderId);
		String[] userLtLng = new String[2];
		userLtLng = DBConnection.getLatLongPositions( userAddress );

		JSONArray markers =  new JSONArray();
		JSONObject markersource = new JSONObject();
		markersource.put("title", "Food Source");
		markersource.put("lat", boylatlng[0]);
		markersource.put("lng",boylatlng[1]);
		markersource.put("description", boylatlng[2]);

		JSONObject markerdestination =  new JSONObject();
		markerdestination.put("title", "Delivery destination");
		markerdestination.put("lat", userLtLng[0]);
		markerdestination.put("lng", userLtLng[1]);
		markerdestination.put("description", userAddress);

		markers.put(markersource);
		markers.put(markerdestination);


		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62\\webapps\\Myapp\\ordertrackmap.html")); 
			String str;
			while ((str = in.readLine()) != null) {
				while ( (str = in.readLine()) != null) {
					contentBuilder.append(str + "\n");
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String newDAta = contentBuilder.toString();
		String data = null;
		StringBuilder markerFDataBuilder = new StringBuilder();
		markerFDataBuilder.append(" var markers =");
		markerFDataBuilder.append(markers.toString());
		if(newDAta.contains("<HERE>")){
			data = newDAta.replaceAll("<HERE>", markerFDataBuilder.toString());
			if(newDAta.contains("<NAME>")){
				System.out.println("NAME TAG EXISTS and plz replace it with->"+DbDetails[0]);
				data   = data.replaceAll("<NAME>", DbDetails[0].toString());
			}
			if(newDAta.contains("<MOBILE>")){
				System.out.println("MOBILE NO TAG EXISTS and plz replace it with->"+DbDetails[1]);
				data = data.replaceAll("<MOBILE>", DbDetails[1].toString());
			}
		}
		return data;
	}

	@POST
	@Path("/kitchenorders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject kitchenorders(@FormParam("kitchenid")String kitchenid) throws JSONException{
		System.out.println(" kitchenorders web service is called...");
		JSONObject kitchenorders = KitchenOrdersDAO.getKitchenOrders(kitchenid);
		System.out.println("kitchenorders web service is end here:");
		return kitchenorders;
	}
	
	@POST
	@Path("/kitchenOrderHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject kitchenOrderHistory(@FormParam("kitchenid")String kitchenName) throws JSONException{
		System.out.println("------------------------------------------------");
		System.out.println(" kitchenOrderHistory web service is called by "+kitchenName);
		System.out.println("------------------------------------------------");
		JSONObject kitchenorders = KitchenOrderHistoryDAO.fetchKitchenOrderHistory(kitchenName);
		return kitchenorders;
	}
	
	/**
	 * This method is used for order summmary
	 * @param kitchenName
	 * @param deliveryDay
	 * @param mealType
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("/orderSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject kitchenOrderSummary(@FormParam("kitchenid")String kitchenName,
			@FormParam("deliveryDay")String deliveryDay,@FormParam("mealType")String mealType) throws JSONException{
		System.out.println("*******************************************");
		System.out.println(" orderSummary web service is called...");
		System.out.println("Kitchen: "+kitchenName+" DeliveryDay : "+deliveryDay+" Meal Type: "+mealType);
		JSONObject kitchenorders = OrderSummaryDAO.fetchOrderSummary(kitchenName, deliveryDay, mealType);
		System.out.println("orderSummary web service is end here:");
		System.out.println("*********************************************");
		return kitchenorders;
	}

	/**
	 * set details
	 * @throws JSONException 
	 * 
	 * */
	@POST
	@Path("/setItemDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject setItemDetails(@FormParam("kitchenId")String kitchenName) throws JSONException{
		System.out.println("--------------------------------------");
		System.out.println("setItemDetails api called for: "+kitchenName);
		JSONObject setItemJson = SetItemDetailsDao.fetchSetDetails(kitchenName);
		System.out.println("--------------------------------------");
		return setItemJson;
	}
	
	@POST
	@Path("/receiveorder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject receiveOrder(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno) throws Exception{
		System.out.println("*******************************************************************");
		System.out.println("KITCHEN "+kitchenName+" RECEIVED webservice is called for "+orderno);
		System.out.println("*******************************************************************");

		JSONObject receiveOrderObject ;
		//receiveOrderObject = DBConnection.receiveOrderFromKitchen(orderno, kitchenName);// need to be changed
		receiveOrderObject = KitchenReceiveOrderDAO.receiveOrderFromKitchen(orderno, kitchenName);// need to be changed
		System.out.println("Kitchen's receiveorder webservice ends here * * * * * * "+receiveOrderObject.length());
		return receiveOrderObject;
	}

	@POST
	@Path("/notifylogistics")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject notifyLogistics(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno,@FormParam("boyId")String boyId) throws JSONException{

		System.out.println("*******************************************************************");
		System.out.println("KITCHEN "+kitchenName+" NOTIFY webservice is called for "+orderno);
		System.out.println("*******************************************************************");

		JSONObject notifyObject ;
		notifyObject = KitchenNotifyOrderDAO.notifyLogistics(orderno, kitchenName, boyId);
		//notifyObject = DBConnection.notifyLogistics(orderno, kitchenName, boyId);// need to be changed
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		return notifyObject;
	}

	@POST
	@Path("/deliveryToBoy")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deliveryToBoy(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno,@FormParam("boyId")String boyId) throws Exception{
		System.out.println("*******************************************************************");
		System.out.println("KITCHEN "+kitchenName+" DELIVER webservice is called for "+orderno);
		System.out.println("*******************************************************************");

		JSONObject notifyObject ;
		notifyObject = KitchenDeliverOrderDAO.deliveryToBoy(orderno, kitchenName, boyId);
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		return notifyObject;
	}
	
	
	@POST
	@Path("/ordertimings")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ordertimings() throws JSONException{
		System.out.println("*******************************************************************");
		System.out.println("ordertimings webservice is called!");
		JSONObject timingsObject = OrderTimingsDAO.getOrderTimings();
		System.out.println("ordertimings webservice is ended!");
		System.out.println("*******************************************************************");
		return timingsObject;
	}

	@POST
	@Path("/notifylogistics1")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject notifyLogistics1(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno) throws JSONException{
		System.out.println("Notify logistics web service is called*  * * * *");
		JSONObject notifyObject ;
		notifyObject = DBConnection.notifyLogistics1(orderno, kitchenName);// need to be changed
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		return notifyObject;
	}

	/****************************
	 * **********************
	 * @param kitchenName
	 * @param orderno
	 * @return
	 * @throws Exception *********************
	 ***************************/
	@POST
	@Path("/notifyPickJi")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject notifyPickJi(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno) throws Exception{
		System.out.println("Notify logistics web service is called*  * * * *");
		JSONObject notifyObject ;
		notifyObject = PickJiDAO.placeOrderToPickJi(orderno, kitchenName);// need to be changed
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		JSONObject pickJiJson = new JSONObject();
		pickJiJson = notifyObject;
		String responseCode = pickJiJson.getString("responseCode");
		if(responseCode.equals("200")){
			pickJiJson.put("kitchenName", kitchenName);
			pickJiJson.put("orderNo", orderno);
			pickJiJson.put("pickJiOrderID", notifyObject.getString("orderID"));
			BookDriver.savePickjiOrderId(pickJiJson);
		}
		return notifyObject;
	}

	/*@POST
	@Path("/apitest")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject apiTest() throws JSONException{
		System.out.println("API TEST web service is called*  * * * *");
		JSONObject testJsonObject = new JSONObject();
		testJsonObject.put("status", "200");
		testJsonObject.put("message", "Hello somnath!");
		System.out.println("Main thread!!");
		  PickjiCall myRunnable = new PickjiCall("CCF","REG/04/10/001686");
	        Thread t = new Thread(myRunnable);
	        t.start();
	        System.out.println("end main thread!!");
		System.out.println("API TEST web service JSON Object - - "+testJsonObject);
		return testJsonObject;
	}*/


	@POST
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public String token() throws JSONException{
		System.out.println("Get Token web service is called*  * * * *");
		String notifyObject = DBConnection.getToken(true);
		System.out.println("Get Token web service JSON Object - - "+notifyObject);
		return notifyObject;
	}

	@POST
	@Path("/trackstatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject trackStatus(final JSONObject input,@QueryParam("id")String id,
			@QueryParam("orderno")String orderNo) throws JSONException {

		System.out.println("trackstatus web service is called * * * * * *::"+input+" id-->"+id+" order No->"+orderNo);

		JSONObject saved = DBConnection.saveTrackDetailStatus(input , id, orderNo);

		System.out.println("trackstatus web service is end here * * * * *::"+saved);
		return saved;
	}

	@POST
	@Path("/ordertrackbyrunnr")
	@Produces(MediaType.TEXT_PLAIN)
	public String ordertrackbyrunnr(@FormParam("id")String orderid) throws URISyntaxException, 
	ParseException, org.json.simple.parser.ParseException, JSONException, IOException{
		System.out.println("ordertrackbyrunnr called . . "+orderid);
		//String trackOrder = DBConnection.trackOrderFromRoadRunnrAPI(orderid);
		String trackOrder = DBConnection.generateAuthToken();
		System.out.println("t-"+trackOrder);
		return trackOrder;
	}


	@POST
	@Path("/placeOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject placeOrder(
			@FormParam("usertype")String userType,
			@FormParam("payAmount")String payAmount,
			@FormParam("credit")boolean credit,
			@FormParam("mobileno")String contactNumber,
			@FormParam("name")String guestName,
			@FormParam("usermailId")String mailid,
			@FormParam("cityName")String city,
			@FormParam("location")String location,
			@FormParam("deliverypincode")String pincode,
			@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("ordertype") String orderType ,
			@FormParam("subscriptiontype") String subscriptiontype,
			@FormParam("day")String day,
			@FormParam("locationId")Integer locationId,
			@FormParam("payType")String payType,
			@FormParam("timeslot")String timeslot,
			@FormParam("mealtype")String mealType,
			@FormParam("deliveryzone")String deliveryZone,
			@FormParam("deliveryaddress")String deliveryAddress,
			@FormParam("instruction")String instruction,
			@FormParam("deliverydate")String deliveryDay,
			@FormParam("slotdetails[]")List<String> timeSlotDetails,
			@FormParam("promoCode")String promoCode
			) throws Exception{

		System.out.println("-------------------------------------------------");
		System.out.println("------- PLACE ORDER STARTS HERE - - - - - - --- -");
		System.out.println("--------------------------------------------------");
		System.out.println("Usertype - >"+userType);
		System.out.println("Name ->"+guestName);
		System.out.println("deliveryday-->"+deliveryDay);
		System.out.println("City -->"+city);
		System.out.println("usermailId-->"+mailid);
		System.out.println("payAmount-->"+payAmount);  
		System.out.println("mobileno-->"+contactNumber);
		System.out.println("credit -->"+credit);
		System.out.println("payType -->"+payType);
		System.out.println("fooddetails[]-->"+orderDetails.toString());
		System.out.println("slotdetails[]-->"+timeSlotDetails.toString());
		System.out.println("deliverypincode -->"+pincode);
		System.out.println("Meal type-->"+mealType);
		System.out.println("timeslot-->"+timeslot);
		System.out.println("deliery zone-->"+deliveryZone);
		System.out.println("delivery address-->"+deliveryAddress);
		System.out.println("instruction-->"+instruction);
		System.out.println("Promo code--> "+promoCode);

		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject orderPlaced = new JSONObject();
		Boolean sub = false;int totalNoOfQuantity = 0,totalStQuantity=0;
		for(String str : orderDetails){

			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==5){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = "Meal Tray";
					//System.out.println("PACKING 1 ------ >>> >> > " + items.packing);
					items.itemTypeId = ItemDAO.getItemTypeId(items.itemCode);
				}else if(order.length==6){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = order[5].toUpperCase();
					//System.out.println("PACKING 2 ------ >>> >> > " + items.packing);
					items.itemTypeId = ItemDAO.getItemTypeId(items.itemCode);
				}
				else if(order.length==7){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = order[5].toUpperCase();
					items.itemTypeId = ItemDAO.getItemTypeId(items.itemCode);
					items.mealType = order[6].trim().toUpperCase();
					//System.out.println("PACKING 3 ----------------------------------------- >>> >> > " + items.packing);
					//System.out.println("MEAL TYPE 1 ------ >>> >> > " + items.mealType);
					//sub = true;
					/*items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.day = order[5];
					items.meal = order[6];*/
					/*String stDateUserString = order[6].trim();
	    	    	String enDateUserString = order[7].trim();
	    	    	DateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
	    	    	java.util.Date stdate = null;
	    	    	java.util.Date endate = null;
	    	    	try {
						stdate = sdf1.parse(stDateUserString);
						endate = sdf1.parse(enDateUserString);
	    	    		stdate = format1.parse(stDateUserString);
						endate = format1.parse(enDateUserString);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.startDate = new java.sql.Date(stdate.getTime());
	    	    	items.endDate = new java.sql.Date(endate.getTime());*/
					/*SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
	    	    	java.util.Date stdate = null;
	    	    	java.util.Date endate = null;
					try {
						stdate = sdf1.parse(stDateUserString);
						endate = sdf1.parse(enDateUserString);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.startDate = new java.sql.Date(stdate.getTime());
	    	    	items.endDate = new java.sql.Date(endate.getTime());*/

				}else{
					//System.out.println("MMMMMMMMMMMM " + order[0] + "//" + order[1] + "//" + order[2]+ order[3] + "//" + order[4] + "//" + order[5] + order[6] + "//" + order[7] + "//" + order[8]);
					sub = true;
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.quantity = Integer.valueOf(order[2]);
					items.price = Double.valueOf(order[3]);
					items.day = order[4];
					items.meal = order[5];
					String stDateUserString = order[6].trim();
					String enDateUserString = order[7].trim();
					items.timsSlot = order[8];
					SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
					//SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
					//DateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
					java.util.Date stdate = null;
					java.util.Date endate = null;
					try {
						stdate = sdf1.parse(stDateUserString);
						endate = sdf1.parse(enDateUserString);
						//	System.out.println("con stdate->"+stdate+" conv endate->"+endate);
						/*stdate = format1.parse(stDateUserString);
						endate = format1.parse(enDateUserString);*/
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.startDate = new java.sql.Date(stdate.getTime());
					items.endDate = new java.sql.Date(endate.getTime());
					//System.out.println("st date-"+items.startDate+" end date--"+items.endDate);
				}

			}
			orderItemList.add(items);
		}
		boolean isSttagegredDelivery = false;String bikerUserId=null;
		if(timeslot.contains("&")){
			isSttagegredDelivery = true;
		}
		System.out.println("Is staggerd delivery:: "+isSttagegredDelivery);
		/**
		 * Find time slot id,boy id and kitchen id 
		 */
		ArrayList<TimeSlot> timeSlotList = new ArrayList<TimeSlot>();
		//List<String> timeSlotDetails = new ArrayList<String>();
		//timeSlotDetails.add("45$DLV0013$1");
		for(OrderItems items : orderItemList){
			totalNoOfQuantity += items.quantity;
		}


		ArrayList<Integer> slotIdList = new ArrayList<Integer>();
		/*if(isSttagegredDelivery){
			totalStQuantity = totalNoOfQuantity;
			String[] timeslots = timeslot.split("\\&");
			for(String str : timeslots){
				int slotId = TimeSlotFinder.getTimeSlotId(str.trim());
				slotIdList.add(slotId);
			}
		}
		//System.out.println("Slot ids:: "+slotIdList);

		if(isSttagegredDelivery){

			for(String str : timeSlotDetails){
		    	TimeSlot slotPojo = new TimeSlot();
				String[] slot = str.split("\\$");
		    	for(int i=0;i<slot.length;i++){
		    		if(slot.length==2){
		    			slotPojo.kitchenID = Integer.valueOf(slot[0]);
		    			slotPojo.bikerUserId = slot[1];
		    			kitchen = slotPojo.kitchenID;
		    			bikerUserId = slotPojo.bikerUserId;
		    		}
		    	}
		    	timeSlotList.add(slotPojo);
		    }
		}else{
			for(String str : timeSlotDetails){
		    	TimeSlot slotPojo = new TimeSlot();
				String[] slot = str.split("\\$");
		    	for(int i=0;i<slot.length;i++){
		    		if(slot.length==3){
		    			slotPojo.kitchenID = Integer.valueOf(slot[0]);
		    			slotPojo.bikerUserId = slot[1];
		    			slotPojo.slotId = Integer.valueOf(slot[2]);
		    		}
		    	}
		    	timeSlotList.add(slotPojo);
		    }
		}*/


		ArrayList<TimeSlot> dealingTimeSlots = new ArrayList<TimeSlot>();
		ArrayList<Integer> kids= new ArrayList<Integer>();
		MealTypePojo mealTypePojo = new MealTypePojo();

		Set<Integer> servingKitchenIds = new HashSet<Integer>();

		for(String str : timeSlotDetails){
			TimeSlot slotPojo = new TimeSlot();
			String[] slot = str.split("\\$");
			for(int i=0;i<slot.length;i++){
				if(slot.length>3){
					slotPojo.kitchenID = Integer.valueOf(slot[0]);
					servingKitchenIds.add(Integer.valueOf(slot[0]));
					slotPojo.bikerUserId = slot[1];
					slotPojo.cuisineId = Integer.valueOf(slot[2]);
					slotPojo.itemCode = slot[3];
					slotPojo.quantity = Integer.valueOf(slot[4]);
					slotPojo.slotId = Integer.valueOf(slot[5]);
				}else{
					slotPojo.kitchenID = Integer.valueOf(slot[0]);
					servingKitchenIds.add(Integer.valueOf(slot[0]));
					slotPojo.bikerUserId = slot[1];
					slotPojo.slotId = Integer.valueOf(slot[2]);
				}
			}
			timeSlotList.add(slotPojo);
		}		
		System.out.println("Kitchens who will serve the order:: "+servingKitchenIds);


		int totNoOfQtyOrig =  totalNoOfQuantity; 
		int kitchen = 0;
		String biker = "";
		int cuisineId = 0;
		String itemCode = "";
		int qty = 0;
		int slotQuantity = 0;
		int otherslot = 0;
		int[] bikerCapa = new int[2];
		bikerCapa = BikerDAO.getBikerCapacityAndOrders();
		int bikerCapacity = bikerCapa[0]; 
		
		for(int i = 0; i < timeSlotList.size(); i++){
			TimeSlot slot = timeSlotList.get(i);
			if(slot.quantity <1){
				continue;
			}
			int currQnty = TimeSlotFinder.getBikerStock(slot.bikerUserId, slot.slotId, mealTypePojo);
			System.out.println("Biker "+slot.bikerUserId+" dbqty:"+currQnty+" slot id: "+slot.slotId+" slot qty:"+slot.quantity);
			System.out.println("Kitchen id: "+slot.kitchenID);
			if(i != 0){
				if(kitchen == slot.kitchenID && biker.equalsIgnoreCase(slot.bikerUserId) && cuisineId == slot.cuisineId
						&& itemCode.equalsIgnoreCase(slot.itemCode) && qty == slot.quantity){
					otherslot = slot.slotId;
					continue;
				}
				if(kitchen == slot.kitchenID && cuisineId == slot.cuisineId
						&& itemCode.equalsIgnoreCase(slot.itemCode) && qty == slot.quantity && slot.quantity < bikerCapacity){
					otherslot = slot.slotId;
					continue;
				}
			}
			kitchen = slot.kitchenID;
			biker = slot.bikerUserId;
			cuisineId = slot.cuisineId;
			itemCode = slot.itemCode;
			qty = slot.quantity;
			//slotId = slot.slotId;

			if( (bikerCapacity - currQnty) >= slot.quantity){
				if(slotQuantity + slot.quantity > bikerCapacity){
					if(otherslot != 0){
						slot.setQuantity(slot.quantity);
						slot.slotId = otherslot;
						slot.itemCode = itemCode;
						slot.kitchenID = kitchen;
						dealingTimeSlots.add(slot);
						slotQuantity = slotQuantity + slot.quantity;
						System.out.println("First if: "+slot.quantity);
						System.out.println("Kitchen id: "+slot.kitchenID);
						
					}
				} else {
					slot.setQuantity(slot.quantity);
					slot.setItemCode(slot.itemCode);
					slot.kitchenID = kitchen;
					dealingTimeSlots.add(slot);
					slotQuantity = slotQuantity + slot.quantity;
					System.out.println("else part: "+slot.quantity);
					System.out.println("Kitchen id: "+slot.kitchenID);
					
				}
			}
			//slot.setQuantity(slot.quantity);
			System.out.println("end part: "+slot.quantity);
		}
		System.out.println("TIME SLOT::::::::::"+dealingTimeSlots);
		Set<Integer> kitchens = new HashSet<Integer>();

		if(dealingTimeSlots.size() ==0 ){
			for(TimeSlot slot : timeSlotList){
				if(!kitchens.contains(slot.kitchenID)){
					kitchens.add(slot.kitchenID);
					dealingTimeSlots.add(slot);
					mealTypePojo.setBoyUSerId(slot.getBikerUserId());
					mealTypePojo.setSlotId(slot.slotId);
				}
			}

			for(int i=0;i<orderItemList.size();i++){
				TimeSlot slot = dealingTimeSlots.get(0);
				TimeSlot newSlot = new TimeSlot();
				newSlot.kitchenID = slot.kitchenID;
				newSlot.itemCode = orderItemList.get(i).itemCode;
				newSlot.bikerUserId = slot.bikerUserId;
				newSlot.quantity = orderItemList.get(i).quantity;
				newSlot.slotId = slot.slotId;
				dealingTimeSlots.add(newSlot);
			}

			System.out.println("New dealing slots:"+dealingTimeSlots);
		}


		//slot.setQuantity(slot.quantity);


		/*	
			}else{
				for(TimeSlot slot : timeSlotList){
					    if(slot.quantity <1){
					    	continue;
					    }
						int currQnty = TimeSlotFinder.getBikerStock(slot.bikerUserId, slot.slotId, mealTypePojo);
						if( (10 - currQnty) >= slot.quantity){
							slot.setQuantity(slot.quantity);
							slot.quantity -= slot.quantity;
						}else{
							slot.setQuantity(10-currQnty);
							slot.quantity -= slot.quantity;
						}
						slot.setQuantity(slot.quantity);
						kids.add(slot.kitchenID);
						dealingTimeSlots.add(slot);
						mealTypePojo.setBoyUSerId(slot.getBikerUserId());
						mealTypePojo.setSlotId(slot.slotId);
					}

			}*/

		System.out.println("Total no of quantites:: "+	totalNoOfQuantity );

		if(isSttagegredDelivery){
			System.out.println("Staggred delivery Dealing time slotss: "+dealingTimeSlots);
			
			
		}else{
			System.out.println("Dealing time slotss: "+dealingTimeSlots);
		}


		if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealTypePojo.setLunchToday(true);
			mealTypePojo.setQuantity(totalNoOfQuantity);
		}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealTypePojo.setDinnerToday(true);
			mealTypePojo.setQuantity(totalNoOfQuantity);
		}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealTypePojo.setLunchTomorrow(true);
			mealTypePojo.setQuantity(totalNoOfQuantity);
		}else{
			mealTypePojo.setDinnerTomorrow(true);
			mealTypePojo.setQuantity(totalNoOfQuantity);
		}
		System.out.println("It is subscription order: -"+sub);
		/*if(orderType.equalsIgnoreCase("SUBSCRIPTION")){*/
		if(sub){
			//place subscription order
			System.out.println("Subscription order. . .");
			/*orderPlaced = DBConnection.placeSubscriptionOrder(mailid, contactName,
					contactNumber, city, location, flatNumber,streetName,pincode,landmark , subscriptiontype,
					getLocationId(location, city), day, orderItemList);*/
			System.out.println("deliverypincode-"+pincode+" dZone--"+deliveryZone+" dAdd--"+deliveryAddress+" ins - -"+instruction);
			//orderPlaced = DBConnection.placeSubscriptionOrder(mailid, contactNumber
			//		, city, location,pincode, subscriptiontype , deliveryZone, deliveryAddress, instruction
			//		, day, orderItemList);
			/*orderPlaced.put("success", "subscription_success");*/
			System.out.println("status send to app--"+orderPlaced);
			return orderPlaced; 
		}else{
			//place regular order
			System.out.println("Regular order. . .");

			/*orderPlaced = DBConnection.placeOrder(mailid, contactName,
			contactNumber, city, location, flatNumber,streetName,pincode,landmark , 
			getLocationId(location, city),mealtype,timeslot, orderItemList,
			deliveryZone,deliveryAddress,instruction);*/
			orderPlaced = DBConnection.placeOrder(userType, mailid, contactNumber, guestName,
					city, location,pincode, getLocationId(location, city),mealType,timeslot, orderItemList,
					deliveryZone,deliveryAddress,instruction,deliveryDay,payAmount,credit, payType , totalNoOfQuantity, 
					mealTypePojo , dealingTimeSlots, servingKitchenIds, promoCode );
			System.out.println("----------------------------------------");
			System.out.println("------- PLACE ORDER ENDS HERE ----------");
			System.out.println("----------------------------------------");
			return orderPlaced;
		}

	}



	@POST
	@Path("/findSlot")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject findSlot(
			@FormParam("mobileno")String contactNumber,
			@FormParam("name")String guestName,
			@FormParam("deliverypincode")String pincode,
			@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("mealtype")String mealtype,
			@FormParam("deliveryaddress")String deliveryAddress,
			@FormParam("deliverydate")String deliveryDay,
			@FormParam("area")String area
			) throws Exception{
		System.out.println("------------------------------------------------------");
		System.out.println("|     findSlot webservice is called		  			  |");
		System.out.println("------------------------------------------------------");
		System.out.println("Name -->"+guestName+"\t mobileno-->"+contactNumber);
		System.out.println("deliveryday-->"+deliveryDay+"\t Meal type-->"+mealtype);
		System.out.println("fooddetails[]-->"+orderDetails.toString());
		System.out.println("deliverypincode -->"+pincode+"\t Area - - >"+area);
		
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject timeSlot = new JSONObject();
		MealTypePojo mealType = new MealTypePojo();
	
		for(String str : orderDetails){	
			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==6){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2].trim();
					String[] itemDetails = ItemDAO.getItemDetails(items.itemCode);
					items.cuisinName = itemDetails[0];
					items.categoryName = itemDetails[1];
					items.itemName = itemDetails[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = order[5].trim().toUpperCase();
				}
			}
			orderItemList.add(items);
		}
	
		
	
		//System.out.println("Total order quantity:: "+ totalQuantity);
		if(mealtype.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealType.setLunchToday(true);
		}else if(mealtype.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealType.setDinnerToday(true);
		}else if(mealtype.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealType.setLunchTomorrow(true);
		}else{
			mealType.setDinnerTomorrow(true);
		}
	
		/*timeSlot = TimeSlotFinder.getFreeSlots(contactNumber, deliveryAddress, orderItemList,
				mealtype, deliveryDay, pincode, mealType, area);*/
		
		timeSlot = 	FindDeliverySlots.getDeliverySlots(contactNumber, deliveryAddress, orderItemList,
				mealtype, deliveryDay, pincode, mealType, area);	
		
		System.out.println(timeSlot);
		System.out.println("---------------------------------------------------------");
		System.out.println("|           FINDSLOT webservice is ended here           |");
		System.out.println("---------------------------------------------------------");
		return timeSlot;
	}
	
	
	@POST
	@Path("/findDeliverySlots")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject findDeliverySlots(
			@FormParam("mobileno")String contactNumber,
			@FormParam("name")String guestName,
			@FormParam("deliverypincode")String pincode,
			@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("mealtype")String mealtype,
			@FormParam("deliveryaddress")String deliveryAddress,
			@FormParam("deliverydate")String deliveryDay,
			@FormParam("area")String area
			) throws Exception{
		System.out.println("------------------------------------------------------");
		System.out.println("|     findDeliverySlots webservice is called		  |");
		System.out.println("------------------------------------------------------");
		System.out.println("Name -->"+guestName+"\t mobileno-->"+contactNumber);
		System.out.println("deliveryday-->"+deliveryDay+"\t Meal type-->"+mealtype);
		System.out.println("fooddetails[]-->"+orderDetails.toString());
		System.out.println("deliverypincode -->"+pincode+"\t Area - - >"+area);
		
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject timeSlot = new JSONObject();
		MealTypePojo mealType = new MealTypePojo();
	
		for(String str : orderDetails){	
			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==6){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2].trim();
					String[] itemDetails = ItemDAO.getItemDetails(items.itemCode);
					items.cuisinName = itemDetails[0];
					items.categoryName = itemDetails[1];
					items.itemName = itemDetails[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = order[5].trim().toUpperCase();
				}
			}
			orderItemList.add(items);
		}
	
		if(mealtype.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
			mealType.setLunchToday(true);
		}else if(mealtype.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY") ){
			mealType.setDinnerToday(true);
		}else if(mealtype.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW") ){
			mealType.setLunchTomorrow(true);
		}else{
			mealType.setDinnerTomorrow(true);
		}
	
		timeSlot = FindDeliverySlots.getDeliverySlots(contactNumber, deliveryAddress, orderItemList,
				mealtype, deliveryDay, pincode, mealType, area);
		System.out.println(timeSlot);
		System.out.println("--------------------------------------------------------");
		System.out.println("| findDeliverySlots webservice is ended here           |");
		System.out.println("--------------------------------------------------------");
		return timeSlot;
	}
	
	@POST
	@Path("/trackOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject trackOrder(@FormParam("usermailid")String usermailid,
			@FormParam("mobileno")String mobileno) throws JSONException {
	
		System.out.println("trackOrder webservice is called * * * * * - - - - Mail Id>>"+usermailid+" And Mobile No>>"+mobileno);
	
		JSONObject jsonObject ;
		jsonObject = DBConnection.trackOrder(usermailid,mobileno);
		System.out.println("trackorder end here******");
		return jsonObject;
	}
	
	
	
	@POST
	@Path("/orderHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject orderHistory(@FormParam("usermailid")String usermailid,
			@FormParam("startdate")String startDate,@FormParam("enddate")String endDate) throws JSONException {
	
		System.out.println("orderHistory webservice is called* * * * "+usermailid+" Start date->"+startDate+"End date->"+endDate);
	
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		java.util.Date stdate = null;
		java.util.Date endate = null;
		try {
			stdate = sdf1.parse(startDate);
			endate = sdf1.parse(endDate);
			// System.out.println("con stdate->"+stdate+" conv endate->"+endate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Date startDateForDb = new java.sql.Date(stdate.getTime());
		java.sql.Date endDateForDb = new java.sql.Date(endate.getTime());
		System.out.println("Start Date for db->"+startDateForDb+" and End date for db - >"+endDateForDb);
		JSONObject jsonObject ;
		jsonObject = DBConnection.orderHistory(usermailid , startDateForDb , endDateForDb);
		System.out.println("orderHistory webservice ened here *******");
		return jsonObject;
	
	}
	
	
	@POST
	@Path("/subscriptionPacks")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject subscriptionPack() throws JSONException{
		System.out.println("* * * * * * * subscriptionPacks web service called! * * * * * * * ");
		JSONObject jsonObject = null;
		jsonObject = DBConnection.loadsubscriptionPacks();
		return jsonObject;
	}
	
	/**
	 * Place order for subscription
	 * @param user
	 * @param packType
	 * @param mealType
	 * @param packPrice
	 * @param paymentName
	 * @param fooddetails[]
	 * @param lunchMobileno
	 * @param lunchName
	 * @param lunchUserMailId
	 * @param lunchTimeSlot
	 * @param lunchDeliveryZone
	 * @param lunchDeliveryAddress
	 * @param lunchInstruction
	 * @param lunchDeliveryPincode
	 * @param dinnerMobileno
	 * @param dinnerName
	 * @param dinnerUserMailId
	 * @param dinnerTimeSlot
	 * @param dinnerDeliveryZone
	 * @param dinnerDeliveryAddress
	 * @param dinnerInstruction
	 * @param dinnerDeliveryPincode
	 * @param sameMobileno
	 * @param sameName
	 * @param sameUserMailId
	 * @param sameTimeSlot
	 * @param sameDeliveryZone
	 * @param sameDeliveryAddress
	 * @param sameInstruction
	 * @param sameDeliveryPincode
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/placeSubscriptionOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject placeSubscriptionOrder(
			@FormParam("user")String user,
			@FormParam("packDay")String packDay,
			@FormParam("packType")String packType,
			@FormParam("mealType")String mealType,
			@FormParam("packPrice")String packPrice,
			@FormParam("paymentName")String paymentName,
			@FormParam("isMultiAddress")String isMultiAddress,
			@FormParam("fooddetails[]")List<String> orderDetails,
	
			@FormParam("lunchMobileno")String lunchContactNumber,
			@FormParam("lunchName")String lunchName,
			@FormParam("lunchUserMailId")String lunchMailid,
			@FormParam("lunchTimeSlot")String lunchTimeSlot,
			@FormParam("lunchDeliveryZone")String lunchDeliveryZone,
			@FormParam("lunchDeliveryAddress")String lunchDeliveryAddress,
			@FormParam("lunchInstruction")String lunchInstruction,
			@FormParam("lunchDeliveryPincode")String lunchPincode,
			@FormParam("lunchAddressType")String lunchAddressType,
	
			@FormParam("dinnerMobileno")String dinnerContactNumber,
			@FormParam("dinnerName")String dinnerName,
			@FormParam("dinnerUserMailId")String dinnerMailid,
			@FormParam("dinnerTimeSlot")String dinnerTimeSlot,
			@FormParam("dinnerDeliveryZone")String dinnerDeliveryZone,
			@FormParam("dinnerDeliveryAddress")String dinnerDeliveryAddress,
			@FormParam("dinnerInstruction")String dinnerInstruction,
			@FormParam("dinnerDeliveryPincode")String dinnerPincode,
			@FormParam("dinnerAddressType")String dinnerAddressType,
	
			@FormParam("sameMobileno")String sameContactNumber,
			@FormParam("sameName")String sameName,
			@FormParam("sameUserMailId")String sameMailid,
			@FormParam("sameTimeSlot")String sameTimeSlot,
			@FormParam("sameDeliveryZone")String sameDeliveryZone,
			@FormParam("sameDeliveryAddress")String sameDeliveryAddress,
			@FormParam("sameInstruction")String sameInstruction,
			@FormParam("sameDeliveryPincode")String samePincode,
			@FormParam("sameAddressType")String sameAddressType
	
			) throws Exception{
	
		System.out.println("placeSubscriptionOrder webservice is called * * * * * * * * *for user "+user);
		System.out.println("packType-->"+packType+"packday-->"+packDay+" packPrice-->"+packPrice+" mealType -- >"+mealType
				+"paymentName - >"+paymentName +" isMultiAddress -- >"+isMultiAddress);  
		System.out.println("fooddetails[]-->"+orderDetails.toString());
	
		/*System.out.println("*** *** Lunch details *** ***");
			System.out.println("lunchName ->"+lunchName+" lunchmobileno--> "+lunchContactNumber+" lunchusermailId-->"+lunchMailid);
			System.out.println("lunchdeliverypincode -->"+lunchPincode);
			System.out.println("lunch TimeSlot-->"+lunchTimeSlot+" lunch deliery zone-->"+lunchDeliveryZone+" lunch delivery address-->"+lunchDeliveryAddress
					+" lunch instruction-->"+lunchInstruction);
	
			System.out.println("*** *** Dinner details *** ***");
			System.out.println("dinnerName ->"+dinnerName+" dinnermobileno--> "+dinnerContactNumber+" dinnerusermailId-->"+dinnerMailid);
			System.out.println("dinner deliverypincode -->"+dinnerPincode);
			System.out.println("dinner TimeSlot-->"+dinnerTimeSlot+" dinner deliery zone-->"+dinnerDeliveryZone+" dinner delivery address-->"+dinnerDeliveryAddress
					+" dinner instruction-->"+dinnerInstruction);
	
			System.out.println("*** *** Same details *** ***");
			System.out.println("SameName ->"+sameName+" Samemobileno--> "+sameContactNumber+" Same usermailId-->"+sameMailid);
			System.out.println("Same deliverypincode -->"+samePincode);
			System.out.println("Same TimeSlot-->"+sameTimeSlot+" Same deliery zone-->"+sameDeliveryZone+" Same delivery address-->"+sameDeliveryAddress
					+" Same instruction-->"+sameInstruction);*/
	
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject orderPlaced = new JSONObject();
		Boolean sub = false;
	
		for(String str : orderDetails){	
			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==7){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2];
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.day = order[5].toUpperCase();
					items.meal = order[6];
				}
			}
			orderItemList.add(items);
		}
		System.out.println("Final item list size:: "+orderItemList.size());
		Prepack prepack = null;	
		boolean isMultiAddresses = false;
		if(isMultiAddress.equalsIgnoreCase("true")){
			isMultiAddresses = true;
		}else{
			isMultiAddresses = false;
		}
		if(!isMultiAddresses){
			//Same 
			System.out.println("*** *** same address given *** *** ");
			System.out.println("Address type in single : "+sameAddressType);
			prepack = new Prepack(user.trim(),paymentName.trim(),packType.trim(),packDay.trim(), mealType.trim(), Double.valueOf(packPrice), 
					sameContactNumber.trim(), sameName.trim(), sameMailid.trim(), sameTimeSlot.trim(),
					sameDeliveryZone.trim(), sameDeliveryAddress.trim(), sameInstruction.trim(), samePincode.trim(), sameAddressType.trim());
		}else{
			//Both lunch and dinner
			System.out.println("*** *** Both lunch and dinner address given *** *** ");
			System.out.println("Address type in multiple: dinnerAddressType: "+dinnerAddressType+" lunchAddressType: "+lunchAddressType);
			prepack = new Prepack(user.trim(),paymentName.trim(),packType.trim(), packDay.trim(),mealType.trim(), Double.valueOf(packPrice), 
					lunchContactNumber.trim(), lunchName.trim(), lunchMailid.trim(), lunchTimeSlot.trim(), 
					lunchDeliveryZone.trim(), lunchDeliveryAddress.trim(), lunchInstruction.trim(), lunchPincode.trim(), lunchAddressType.trim(),
					dinnerContactNumber.trim(), dinnerName.trim(),dinnerMailid.trim(), dinnerTimeSlot.trim(),
					dinnerDeliveryZone.trim(), dinnerDeliveryAddress.trim(), dinnerInstruction.trim(), dinnerPincode.trim(), dinnerAddressType.trim());
		}
	
		User registeredUser = UserDetailsDao.getUserDetails(user, null); //Get user details
		orderPlaced = PlaceSubscriptionOrderDAO.checkOut(registeredUser, prepack, orderItemList,isMultiAddresses);
		//System.out.println(orderPlaced);
		return orderPlaced; 
	
	}
	
	
	
	@POST
	@Path("/subscriptionDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject subscriptionDetails(@FormParam("mobileno")String mobileno) throws JSONException {
	
		System.out.println("SubscriptionDetails webservice is called***** with mobile no--->"+mobileno);
	
		JSONObject jsonObject;
		jsonObject = DBConnection.subscriptionDetails(mobileno);
		System.out.println("SubscriptionDetails webservice ended ********");
		return jsonObject;
	
	}
	
	@POST
	@Path("/updateSubscription")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject updateSubscription(@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("subscriptionordernumber")String subscriptionorderno) throws JSONException {
	
		System.out.println("Update SubscriptionDetails webservice is called***** with sub no--->"+subscriptionorderno);
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		System.out.println("fooddetails:"+orderDetails);
		JSONObject orderUpdated = new JSONObject();
		for(String str : orderDetails){
	
			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==9){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.quantity = Integer.valueOf(order[2]);
					items.price = Double.valueOf(order[3]);
					items.day = order[4];
					items.meal = order[5];
					String stDate = order[6].trim();
					String enDate = order[7].trim();
					items.timsSlot = order[8];
					SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
					java.util.Date stdate = null;
					java.util.Date endate = null;
					try {
						stdate = sdf1.parse(stDate);
						endate = sdf1.parse(enDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.startDate = new java.sql.Date(stdate.getTime());
					items.endDate = new java.sql.Date(endate.getTime());
				}
			}
			orderItemList.add(items);
		}
	
		for(OrderItems items:orderItemList){
			System.out.println(items.cuisineId+" ,"+items.categoryId+" ,"+items.quantity
					+" ,"+items.price+" ,"+items.day+" ,"+items.meal+" ,"+items.startDate+" ,"+items.endDate+" ,"+items.timsSlot);
		}
	
		//orderUpdated.put("status", true);
		orderUpdated = DBConnection.updateSubscription(orderItemList, subscriptionorderno);
		System.out.println("SubscriptionDetails webservice ended ********");
		return orderUpdated;
	
	}
	
	private Integer getLocationId(String location , String city){
		Integer areaid= 0;
		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			Connection connection = null;
			SQL:{
				connection = DBConnection.createConnection();
				String sql = "select area_id from sa_area "
						+" where area_name ILIKE ? "
						+" and city_id = "
						+" (select city_id from sa_city where city_name ILIKE ?)" ; 
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, location);
					preparedStatement.setString(2, city);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						areaid =  resultSet.getInt("area_id");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (preparedStatement!=null) {
						preparedStatement.close();
					}
					if(resultSet!=null){
						resultSet.close();
					}
					if(connection!=null){
						connection.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return areaid;
	}
	
	@POST
	@Path("/checkfeedback")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject checkfeedback(@FormParam("usermailid")String usermailId) throws JSONException{
	
		System.out.println("**********************************************");
		System.out.println("****** CHECKFEEDBACK webservice is called...emailid->"+usermailId);
	    
		JSONObject feedbackobject;
		feedbackobject =  DBConnection.checkfeedback(usermailId);
		System.out.println("**********************************************");
		return feedbackobject;
	}
	
	
	/*@POST
		@Path("/submitfeedback")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject submitfeedback(@FormParam("foodquality")String foodQuality,@FormParam("deliveryquality") String deliveryQuality,
				@FormParam("overallrating")String overallRating,@FormParam("usermailid") String userMailId  )throws Exception{
		public JSONObject submitfeedback(@FormParam("taste")String taste,@FormParam("ingredients")String ingredients,
				@FormParam("hygiene")String hygiene,@FormParam("spillage")String spillage,@FormParam("packing")String packing,
				@FormParam("delayed")String delayed,@FormParam("deliveryboy")String deliveryboy,
				@FormParam("cold")String coldFood,@FormParam("longprocess") String longprocess,
				@FormParam("userfriendly")String userfriendly,@FormParam("overallrating") String overallRating,
				@FormParam("foodcomment")String foodComment, @FormParam("usermailid") String userMailId  )throws Exception{
		public JSONObject submitfeedback(@FormParam("overallrating") String overallRating,@FormParam("taste")String taste,
				@FormParam("hotness")String hotness,@FormParam("portion")String portion,@FormParam("packing")String packaging,
				@FormParam("timelydelivered")String timely, @FormParam("usermailid") String userMailId,
				@FormParam("foodcomment")String comment) throws JSONException{
	
			System.out.println("submitfeedback webservice is called...");
			System.out.println("taste-"+taste+" packing-"+packaging+" timelydelivered-"+timely+" overall rating-"+overallRating+
					" portion-"+portion+" hotness-"+hotness+" comment-"+comment+" mail-"+userMailId);
			JSONObject submitfeedbackobject = new JSONObject();
	
			submitfeedbackobject = DBConnection.submitfeedback(taste,portion,hotness,packaging,timely,overallRating,comment,userMailId);
			System.out.println("Submit feedback--"+submitfeedbackobject);
			return submitfeedbackobject;
	
		}*/
	
	/**
	 * FOR SUBMIT FEEDBACK
	 * @param menu
	 * @param taste
	 * @param quantity
	 * @param packing
	 * @param timelyDelivered
	 * @param userMailId
	 * @param comment
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("/submitfeedback")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject submitfeedback(@FormParam("menu") String menu,@FormParam("taste")String taste,
			@FormParam("quantity")String quantity,@FormParam("packing")String packing,
			@FormParam("timelydelivered")String timelyDelivered, @FormParam("usermailid") String userMailId,
			@FormParam("foodcomment")String comment) throws JSONException{
	
		System.out.println("submitfeedback webservice is called * * * * *");
		System.out.println("taste-"+taste+" packing-"+packing+" timelydelivered-"+timelyDelivered+
				" quantity-"+quantity+" menu-"+menu+" comment-"+comment+" mail-"+userMailId);
		JSONObject submitfeedbackobject = new JSONObject();
	
		submitfeedbackobject = SubmitFeedBackDAO.submitFeedback(menu, taste, quantity, packing, timelyDelivered, comment, userMailId);
		System.out.println("Submit feedback ends with *****"+submitfeedbackobject);
		return submitfeedbackobject;
	
	}
	
	@POST
	@Path("/fetchlocationname")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchLocationList() throws JSONException{
		System.out.println("---------------------------------------------------------");
		System.out.println("* * * fetchlocationname webservice is called * * * * * * ");
		JSONObject jobjLocation= FetchLocationDAO.fetchLocationOfKitchen();
		System.out.println("---------------------------------------------------------");
		return jobjLocation;
	}
	
	@POST
	@Path("/fetchBanners")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchBanners() throws Exception{
		System.out.println("---------------------------------------------------------");
		System.out.println("fetchBanners webservice is called...");
	
		JSONObject jobjBanner = new JSONObject();
				
		JSONArray bannerList = FetchBannersDAO.fetchBanners();
		
		if(bannerList.length()>0){
			jobjBanner.put("status", "200");
			jobjBanner.put("message", "Banner found!");
			jobjBanner.put("bannerList", bannerList);
		}else{
			jobjBanner.put("status", "204");
			jobjBanner.put("message", "Banner not found!");
			jobjBanner.put("bannerList", bannerList);
		}
		System.out.println("fetchBanners webservice end..."+bannerList.length());
		System.out.println("---------------------------------------------------------");
		return jobjBanner;
	}
	
	@POST
	@Path("/fetchcuisine")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchCuisineList(@FormParam("pincode")String pincode,
			@FormParam("deliveryday")String deliveryDay,
			@FormParam("mobileNo")String mobileNo,
			@FormParam("area")String area) throws Exception{
		System.out.println("-------------------------------------------------------");
		System.out.println("***** fetchcuisine webservice called ***************");
		System.out.println(" Pincode: "+pincode+" Day: "+deliveryDay+" Area: "+area);
		JSONObject jsonObject = new JSONObject();
		if(area!=null ){
			jsonObject = FetchCuisineDAO.fetchAllCuisineWithItemData(pincode, deliveryDay, mobileNo,area);
		}else{
			jsonObject.put("status", "204");
			jsonObject.put("message", "Currently we are not serving in this zip code!");
			jsonObject.put("isSingleOrderLunchAvailable", true);
			jsonObject.put("lunchAlert", "");
			jsonObject.put("isSingleOrderDinnerAvailable", true);
			jsonObject.put("isMultipleOrderLunchAvailable", true);
			jsonObject.put("multipleLunchAlert", "");
			jsonObject.put("isMultipleOrderDinnerAvailable", true);
			jsonObject.put("multipleDinnerAlert", "");
			jsonObject.put("dinnerAlert","");
			jsonObject.put("cartCapacity", 0);
			jsonObject.put("lunchCartCapacity", 0);
			jsonObject.put("dinnerCartCapacity", 0);
			jsonObject.put("cuisinelist", new JSONArray());
		}
		System.out.println("------------------------------------------------------");
		return jsonObject;
		/*if(mobileNo!=null){
			System.out.println("Mobile no: "+mobileNo+" length :: "+mobileNo.length());
		}*/
		
		//System.out.println(pincode.trim().length());
		//String mobileNo = "9934170084";
		/*if(pincode!=null && pincode.trim().length()>0){
			//System.out.println("Pincode given!");
			if(PincodeDAO.isPincodeAvailable(pincode)){
				jsonObject = DBConnection.fetchAllCuisineWithItemData(pincode, deliveryDay, mobileNo,area);
			}else{
				jsonObject.put("status", "204");
				jsonObject.put("message", "Currently we are not serving in this zip code!");
				jsonObject.put("cuisinelist", new JSONArray());
			}
			System.out.println("********** fetchcuisine ended here ***************");
			return jsonObject;
		}else{
			System.out.println("No Pincode found!");
			System.out.println("fetchcuisine ended with null here * * * * * ");
			jsonObject.put("status", "204");
			jsonObject.put("message", "Currently we are not serving in this zip code!");
			System.out.println("********** fetchcuisine ended here ***************");
			return jsonObject.put("cuisinelist", new JSONArray());
		}*/
	}
	
	
	
	@POST
	@Path("/fetchAlacarteItems")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchAlaCarteItems(@FormParam("pincode")String pincode,
			@FormParam("categoryId")String categoryId,
			@FormParam("deliveryday")String deliveryDay,
			@FormParam("area")String area) throws JSONException{
		
		System.out.println("***************************");
		System.out.println("** fetchAlacarteItems WEB SERVICE CALLED ********");
		System.out.println("Pincode: "+pincode+" Catefory id: "+categoryId+" delievry day: "+deliveryDay);
		System.out.println("***************************");
		JSONObject alacarteItemJson = new JSONObject();
		if(pincode.length()==0){
			alacarteItemJson.put("status", "400");
			alacarteItemJson.put("message", "Pincode required!");
		}else if(categoryId.equals("0")){
			alacarteItemJson.put("status", "400");
			alacarteItemJson.put("message", "Category id required!");
		}else if(deliveryDay.length() == 0){
			alacarteItemJson.put("status", "400");
			alacarteItemJson.put("message", "Delivery day required!");
		}else{
			alacarteItemJson = FetchAlaCarteItemDAO.fetchAlacarteItem(pincode, categoryId, deliveryDay,area);
		}
		return alacarteItemJson;
	}
	
	/**
	 *  1 st webservice to fetch location list
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("/fetchlocation")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchlocation() throws JSONException{
	
		System.out.println("*** fetchlocation webservice is called * * * * * * *");
	
		JSONObject jobjLocation ;
	
		jobjLocation = DBConnection.fetchlocation();
	
		//System.out.println("End of fetchlocation webservice * * * * * * * * * * * * *");
		return jobjLocation;
	}
	
	@POST
	@Path("/fetchservinglocation")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchservinglocation() throws JSONException{
	
		System.out.println("*** fetchservinglocation webservice is called * * * * * * * ");
	
		JSONObject jobjLocation= DBConnection.fetchServinglocation();
	
		//System.out.println("End of fetchservinglocation webservice * * * * * * * * * * * * *");
		return jobjLocation;
	}
	
	
	@POST
	@Path("/fetchlocationnamelist")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchLocationNameList(@FormParam("area")String areaName) throws JSONException{
		System.out.println("locationname LIST webservice is called * * * * * * * areaName->"+areaName);
	
		JSONObject jobjLocation= DBConnection.getLocationNames(areaName);
	
		System.out.println("End of locationname LIST webservice * * * * * * * * * * * * *");
		return jobjLocation;
	}
	
	@POST
	@Path("/fetchPaymentTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchPaymentTypes()throws JSONException{
		System.out.println("--------------------------------------------");
		System.out.println("--    fetchPaymentTypes api called  --------");
		JSONObject paymentTypeJson = PaymentTypeDAO.fetchPaymentTypeList();
		System.out.println("-- fetchPaymentTypes api ends here ---------");
		System.out.println("--------------------------------------------");
		return paymentTypeJson;
	}
	
	@POST
	@Path("/getQueryTypelist")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject getQueryTypeList() throws JSONException{
		System.out.println("************************************************************");
		System.out.println("***** getQueryTypelist webservice  * * * * * * * * * * * * *");
		JSONObject queryTypeJsonObject = QueryTypeDAO.getQueryTypeList();
		System.out.println("***** getQueryTypelist webservice  ends* * * * * * * * * *  *");
		return queryTypeJsonObject;
	}
	
	@POST
	@Path("/fetchFaq")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchFaqList() throws JSONException{
		System.out.println("-----------------------------------------------------");
		System.out.println("***** fetchFaq webservice  * * * * * * * * * * * * *");
		JSONObject faqJsonObject = FaqDAO.fetchAllFaqs();
		System.out.println("-----------------------------------------------------");
		return faqJsonObject;
	}
	
	@POST
	@Path("/fetchShareEarn")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchShareEarn() throws JSONException{
		System.out.println("-----------------------------------------------------------");
		System.out.println("***** fetchShareEarn webservice  * * * * * * * * * * * * *");
		JSONObject faqJsonObject = ShareDAO.fetchShareEarn();
		System.out.println("-----------------------------------------------------------");
		return faqJsonObject;
	}
	
	@POST
	@Path("/fetchContactUs")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchContactUs() throws JSONException{
		System.out.println("************************************************************");
		System.out.println("***** fetchContactUs webservice  * * * * * * * * * * * * *");
		JSONObject faqJsonObject = ContactUsDAO.getCustomerCareNumber();
		System.out.println("***** fetchContactUs webservice  ends* * * * * * * * * *  *");
		return faqJsonObject;
	}
	
	@POST
	@Path("/fetchOrderContactUs")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchOrderContactUs() throws JSONException{
		System.out.println("----------------------------------------------------------");
		System.out.println(" fetchOrderContactUs webservice  ");
		JSONObject faqJsonObject = ContactUsDAO.getOrderMessge();
		System.out.println("----- fetchOrderContactUs webservice  ends----------------");
		return faqJsonObject;
	}
	
	@POST
	@Path("/fetchAboutUs")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject fetchAboutUs() throws JSONException{
		System.out.println("************************************************************");
		System.out.println("***** fetchAboutUs webservice  * * * * * * * * * * * * *");
		JSONObject faqJsonObject = AboutUsDAO.getAboutUS();
		System.out.println("***** fetchAboutUs webservice  ends* * * * * * * * * *  *");
		return faqJsonObject;
	}
	
	@POST
	@Path("/termsAndConditions")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject termsAndConditions() throws JSONException{
		System.out.println("************************************************************");
		System.out.println("***** termsAndConditions webservice  * * * * * * * * * * * * *");
		JSONObject faqJsonObject = TermsAndConditionDAO.getTermsAndConditions();
		System.out.println("***** termsAndConditions webservice  ends* * * * * * * * * *  *");
		return faqJsonObject;
	}
	
	@POST
	@Path("/privacyPolicy")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject privacyPolicy() throws JSONException{
		System.out.println("---------------------------------------------------------");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String currentTime = sdf.format(date);
		
		System.out.println("***** privacyPolicy webservice  * * * * * * * * * * * * *"+currentTime);
		JSONObject faqJsonObject = PrivacyPolicyDAO.getPrivacyPolicy();
		Date endate = new Date();
		String rcurrentTime = sdf.format(endate);
		System.out.println("------- privacyPolicy webservice ends------------------*"+rcurrentTime);
		return faqJsonObject;
	}
	
	@POST
	@Path("/submitMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject submitMessage(@FormParam("queryTypeId")String queryTypeId,
			@FormParam("name")String userName, @FormParam("emailId")String emailId,
			@FormParam("message")String message) throws JSONException{
		System.out.println("***********************************************");
		System.out.println("***** submitMessage webservice  ***************");
		System.out.println("queryTypeId=>"+queryTypeId);
		System.out.println("name=>"+userName+"emailId=>"+emailId+"message=>"+message);
		JSONObject queryTypeJsonObject = QueryTypeDAO.submitMessage(Integer.valueOf(queryTypeId), userName, emailId, message);
		System.out.println("***** submitMessage webservice  ends ***********");
		return queryTypeJsonObject;
	}
	
	@POST
	@Path("/isPromoCodeValid")
	@Produces(MediaType.APPLICATION_JSON)
	public static JSONObject checkValidPromoCode(@FormParam("promoCode")String promoCode,
			@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("mobileNo")String mobileNo) throws JSONException{
		System.out.println("--------------------------------------------------------*");
		System.out.println("***** isPromoCodeValid webservice >> "+promoCode+" << * * * * * * * * * * * * *");
		System.out.println("food details:: "+orderDetails);
		System.out.println("Mobile no:: "+mobileNo);
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject promoCodeValidJson = new JSONObject();
		for(String str : orderDetails){	
			OrderItems items = new OrderItems();
			String[] order = str.split("\\$");
			for(int i=0;i<order.length;i++){
				if(order.length==7){
					items.cuisineId = Integer.valueOf(order[0]);
					items.categoryId = Integer.valueOf(order[1]);
					items.itemCode = order[2].trim();
					items.price = Double.valueOf(order[3]);
					items.quantity = Integer.valueOf(order[4]);
					items.packing = order[5].trim().toUpperCase();
					items.mealType = order[6].trim().toUpperCase();
					//System.out.println("M E A L T Y P E >>> >> > " + items.mealType);
				}
			}
			orderItemList.add(items);
		}
		
		if(mobileNo!=null){
			
			//System.out.println("MOBILE NO AVIAIL -- >> ");
			if(!PromoCodeDAO.isUsedPromoCode(promoCode, mobileNo)){
				promoCodeValidJson = PromoCodeDAO.isPromoCodeValid(promoCode,orderItemList,mobileNo, orderDetails);
				//System.out.println("1 N ------------------- >>> >> > " + queryTypeJsonObject);
				//System.out.println("***** isPromoCodeValid webservice  ends* * * * * * * * * *  *");
				//return queryTypeJsonObject;
			}else{
				//JSONObject promoCodeValidJson = new JSONObject();
				promoCodeValidJson.put("status","200");
				promoCodeValidJson.put("message", "PromoCode already used by you!");
				promoCodeValidJson.put("isValid", false);
				promoCodeValidJson.put("promoValue", ( 0.0) );
				//queryTypeJsonObject.put("JSON", promoCodeValidJson);
				//System.out.println("2 N ------------------- >>> >> > " + queryTypeJsonObject);
			}
		}else{
			    promoCodeValidJson = PromoCodeDAO.isPromoCodeValid(promoCode,orderItemList,mobileNo, orderDetails);
				//System.out.println("3 N ------------------- >>> >> > " + queryTypeJsonObject);
		}
		System.out.println(promoCodeValidJson);
		System.out.println("----- isPromoCodeValid webservice  ends -----");
		//System.out.println("4 IS VALID PROMO CODE ------- >>> >> >  " + queryTypeJsonObject);
		return promoCodeValidJson;
	}
	
	@POST
	@Path("/getpostcode")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getpostcode(@FormParam("cityid")String cityId,@FormParam("pincode")String pincode) 
	//public JSONObject getpostcode(@FormParam("pincode")String pincode) 
			throws JSONException{
		System.out.println("getpostcode webservice called!");
		JSONObject postcodes = DBConnection.getPostalCodes( cityId , pincode);
		return postcodes;
	
	}
	
	@POST
	@Path("/checkitemexists")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject checkAllCategories(@FormParam("pincode")String pincode) throws JSONException{
	
		System.out.println("checkitem web service is called * * * pincode-"+pincode);
		JSONObject fetchAllCategory;
	
		fetchAllCategory = DBConnection.fetchAllCategories("city", "location", pincode);
		if(fetchAllCategory.getJSONArray("Categories").length()>0){
			fetchAllCategory.put("status", "OK");
		}else{
			fetchAllCategory.put("status", "NOT OK");
		}
		System.out.println("End of fetchallcategory webservice * * * * * * * * *");
		return fetchAllCategory;
	}
	
	/*@POST
		@Path("/fetchcuisine")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject fetchcuisinelist(@FormParam("city")String city,
				@FormParam("location")String location) throws Exception{
	
			System.out.println("fetchcuisine webservice is called...");
	
			JSONObject jobjCusine ;
	
			String address = city +""+ location ;
			String latLongs[] = getLatLongPositions(address);
			System.out.println("Latitude: "+latLongs[0]+" and Longitude: "+latLongs[1]);
			jobjCusine = DBConnection.fetchCuisine(latLongs[0], latLongs[1]);
			System.out.println("JSONObject in fetchCuisine webservice->"+jobjCusine);
			return jobjCusine;
		}*/
	/**
	 * 2nd web service to fetch cuisine list
	 * @param location
	 * @return
	 * @throws Exception
	 */
	/*@POST
		@Path("/fetchcuisine")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject fetchcuisinelist(@FormParam("city")String city,
				@FormParam("location")String location) throws Exception{*/
	/*@POST
		@Path("/fetchcuisine")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject fetchcuisinelist() throws Exception{
	
			System.out.println("fetchcuisine webservice is called * * * * * * *");
	
			JSONObject jobjCusine ;
	
			jobjCusine = DBConnection.fetchCuisineList();
	
			//System.out.println("JSONObject in fetchCuisine webservice->"+jobjCusine);
			System.out.println("End of fetchCuisine webservice * * * * * * * * * * * * *");
			return jobjCusine;
		}*/
	
	
	
	
	/**
	 * 3Rd web service for fetching all categories
	 * @return JSONObject
	 * @throws Exception
	 */
	@POST
	@Path("/fetchCategory")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getCategoryName(@FormParam("cuisineId") Integer cuisineid , 
			@FormParam("city")String city,
			@FormParam("location")String location)throws Exception{
		//	@FormParam("locationId")Integer  locationid) throws Exception{
	
		System.out.println("fetchCategory webservice is called * * * * * * *");
	
		JSONObject jobjCat ;
	
		jobjCat = DBConnection.fetchCategoryList( city, location , cuisineid);	
	
		System.out.println("Fetch category JSONObject -->"+jobjCat);
		System.out.println("End of fetchCategory webservice * * * * * * * * * * * * *");
		return jobjCat;
	}
	
	@POST
	@Path("/fetchAllItems")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchAllItems() throws JSONException{
		JSONObject item = null;
		item = AllItemsDAO.fetchAllItems();
		return item;
	}
	
	
	/*@POST
		@Path("/fetchallcategory")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject fetchAllCategories(
				@FormParam("city")String city,
				@FormParam("location")String location,@FormParam("pincode")String pincode) throws JSONException{
	
			System.out.println("fetchallcategory web service is called * * * pincode-"+pincode);
			System.out.println("pincode length->"+pincode.length());
			JSONObject fetchAllCategory;
	
			fetchAllCategory = DBConnection.fetchAllCategories(city, location, pincode);
	
			System.out.println("End of fetchallcategory webservice * * * * * * * * *");
			return fetchAllCategory;
		}*/
	
	/*@POST
		@Path("/fetchallcategory")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject fetchAllCategories(@FormParam("categoryid")String categoryId,
				@FormParam("pincode")String pincode) throws JSONException{
	
			System.out.println("fetchallcategory web service is called * * * pincode-"+pincode);
			System.out.println("category ID ->"+categoryId);
			JSONObject fetchAllCategory = new JSONObject();
			if(categoryId != null){
				if(pincode!=null || pincode.trim().length()>0){
					fetchAllCategory = DBConnection.fetchItemsWrtCategory(Integer.parseInt(categoryId), pincode);
				}else{
					fetchAllCategory.put("message", "No value for pincode!");
					fetchAllCategory.put("Categories", new JSONArray());
				}
			}else{
				fetchAllCategory.put("message", "No value for categoryid!");
				fetchAllCategory.put("Categories", new JSONArray());
			}
	
	
			System.out.println("End of fetchallcategory webservice * * * * * * * * *");
			return fetchAllCategory;
		}*/
	
	public static String[] getLatLongPositions(String address) throws Exception
	{
		int responseCode = 0;
		String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
		URL url = new URL(api);
		HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
		httpConnection.connect();
		responseCode = httpConnection.getResponseCode();
		if(responseCode == 200)
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
			Document document = builder.parse(httpConnection.getInputStream());
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/GeocodeResponse/status");
			String status = (String)expr.evaluate(document, XPathConstants.STRING);
			if(status.equals("OK"))
			{
				expr = xpath.compile("//geometry/location/lat");
				String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
				expr = xpath.compile("//geometry/location/lng");
				String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
				return new String[] {latitude, longitude};
			}
			else
			{
				throw new Exception("Error from the API - response status: "+status);
			}
		}
		return null;
	}
	
	
	
	/**
	 * This method is useful for the location availability check
	 * @param city
	 * @param location
	 * @return JSONObject
	 * @throws Exception
	 */
	@POST
	@Path("/locationAvailability")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject locationAvailability(@FormParam("city") String city,@FormParam("location") String location) throws Exception{
	
	
		System.out.println("locationAvailability webservice is called...");
	
		JSONObject jobjCat=new JSONObject();
	
		jobjCat = DBConnection.locationAvailability(city, location);
	
		return jobjCat;
	}
	
	/**
	 * This method is useful for fetching all categories
	 * @return JSONObject
	 * @throws Exception
	 *//*
		@POST
		@Path("/fetchCategory")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject getCategoryName(@FormParam("cuisineId") Integer cuisineid, @FormParam("kitchenId") Integer kitchenid) throws Exception{
	
			System.out.println("fetchCategory webservice is called...");
	
			JSONObject jobjCat ;
	
			jobjCat = DBConnection.selectCategoryName(cuisineid, kitchenid);	
	
			System.out.println("Fetch category JSONObject -->"+jobjCat);
	
			return jobjCat;
		}*/
	
	
	
	/**
	 * This method is useful for fetching all item list wrt to category id
	 * @param categoryid
	 * @return JSONObject
	 * @throws Exception
	 */
	@POST
	@Path("/fetchcategoryitemlist")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getCategoryItemDetails(@FormParam("categoryid") Integer categoryID) throws Exception{
	
		System.out.println("fetchcategoryitemlist webservice is called...");
	
		JSONObject jsonObject = new JSONObject();
	
		jsonObject = DBConnection.categoryItemList(categoryID);
	
		return jsonObject;
	}
	
	/**
	 * This method is useful for fetching all item details wrt to item id
	 * @param itemid
	 * @return JSONObject
	 * @throws Exception
	 */
	@POST
	@Path("/fetchitemdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getItemDetails(@FormParam("itemid") Integer itemID) throws Exception{
	
		System.out.println("fetchitemdetails webservice is called...");
	
		JSONObject jobjitem = new JSONObject();
	
		jobjitem = DBConnection.itemDetails(itemID);
	
		return jobjitem;
	}
	
	@POST
	@Path("/fetchdealdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject showDeals(@FormParam("city") String city, @FormParam("area") String area) throws Exception{
	
		System.out.println("fetchdealdetails webservice is called...");
	
		JSONObject jobjdeal = DBConnection.showDeals(city, area);
	
		return jobjdeal;
	}
	
	
	
	@POST
	@Path("/getCityList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getCityList() throws JSONException{
		System.out.println("getCityList web service is called. . . ");
		JSONObject cityJson = DBConnection.getCityList();
		return cityJson;
	
	}
	
	@POST
	@Path("/getAreaList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAreaList(@FormParam("cityId") String cityId) throws JSONException{
		System.out.println("getAreaList web service is called. . . ");
		JSONObject areaJson = DBConnection.getAreaList(cityId);
		return areaJson;
	
	}
	
	@POST
	@Path("/fetchzipcodes")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchzipcodes(@FormParam("areaId") String areaId) throws Exception{
	
		System.out.println("fetchzipcodes webservice is called...area id"+areaId);
	
		JSONObject jsonZipcodes = DBConnection.fetchZipCodes(areaId);
	
		return jsonZipcodes;
	}
	
	@GET
	@Path("/fetchalacarte")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject showalacarte() throws Exception{
	
		System.out.println("fetchalacarte webservice is called...");
	
		JSONObject jsonObject = new JSONObject();
	
		jsonObject = DBConnection.getalacarteItems();
	
		return jsonObject;		
	}
	
	
	@POST
	@Path("/checkout")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject checkOutOrder(
			@FormParam("mobileno")String contactNumber,
			@FormParam("name")String guestName,
			@FormParam("usermailId")String mailid,
			@FormParam("cityName")String city,
			@FormParam("location")String location,
			@FormParam("deliverypincode")String pincode,
			@FormParam("itemdetails")String data,
			@FormParam("fooddetails[]")List<String> orderDetails,
			@FormParam("ordertype") String orderType ,
			@FormParam("subscriptiontype") String subscriptiontype,
			@FormParam("day")String day,
			@FormParam("locationId")Integer locationId,
			@FormParam("timeslot")String timeslot,
			@FormParam("mealtype")String mealtype,
			@FormParam("deliveryzone")String deliveryZone,
			@FormParam("deliveryaddress")String deliveryAddress,
			@FormParam("instruction")String instruction,
			@FormParam("deliverydate")String deliveryDay
			) throws Exception{
	
		System.out.println("placeOrder webservice is called * * * * * * * * *");
		System.out.println("Name ->"+guestName);
		System.out.println("deliveryday-->"+deliveryDay);
		System.out.println("City -->"+city);
		System.out.println("usermailId-->"+mailid);
		System.out.println("mobileno-->"+contactNumber);
		System.out.println("itemdetails-->"+data);
		System.out.println("fooddetails[]-->"+orderDetails.toString());
		System.out.println("deliverypincode -->"+pincode);
		System.out.println("Meal type-->"+mealtype);
		System.out.println("timeslot-->"+timeslot);
		System.out.println("deliery zone-->"+deliveryZone);
		System.out.println("delivery address-->"+deliveryAddress);
		System.out.println("instruction-->"+instruction);
	
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		JSONObject orderPlaced = new JSONObject();
		Boolean sub = false;
		Boolean ios = false;
		System.out.println("ios order size-"+data.length());
		if(data.length()!=0){
			ios = true;
			System.out.println("ios order!");
		}
		if(ios){
			if(data.contains(",")){
				String[] item = data.split(",");
				for(int i=0 ;i<item.length ; i++){
					OrderItems items = new OrderItems();
					String itemDetailStr = item[i];
					String[] itemArr = itemDetailStr.split("\\$");
					for(int j=0;j<1 ; j++){
						items.cuisineId = Integer.valueOf(itemArr[0]);
						items.categoryId = Integer.valueOf(itemArr[1]);
						items.quantity = Integer.valueOf(itemArr[2]);
						items.price = Double.valueOf(itemArr[3]);
						orderItemList.add(items);
					}
				}
			}else{
				OrderItems items = new OrderItems();
				String itemDetailStr = data;
				String[] itemArr = itemDetailStr.split("\\$");
				for(int j=0;j<1 ; j++){
					items.cuisineId = Integer.valueOf(itemArr[0]);
					items.categoryId = Integer.valueOf(itemArr[1]);
					items.quantity = Integer.valueOf(itemArr[2]);
					items.price = Double.valueOf(itemArr[3]);
					orderItemList.add(items);
				}
			}
		}else{
	
	
			for(String str : orderDetails){
	
				OrderItems items = new OrderItems();
				String[] order = str.split("\\$");
				for(int i=0;i<order.length;i++){
					if(order.length==4){
						items.cuisineId = Integer.valueOf(order[0]);
						items.categoryId = Integer.valueOf(order[1]);
						items.quantity = Integer.valueOf(order[2]);
						items.price = Double.valueOf(order[3]);
	
					}else if(order.length==8){
						sub = true;
						items.cuisineId = Integer.valueOf(order[0]);
						items.categoryId = Integer.valueOf(order[1]);
						items.quantity = Integer.valueOf(order[2]);
						items.price = Double.valueOf(order[3]);
						items.day = order[4];
						items.meal = order[5];
						String stDateUserString = order[6].trim();
						String enDateUserString = order[7].trim();
						DateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
						java.util.Date stdate = null;
						java.util.Date endate = null;
						try {
							/*stdate = sdf1.parse(stDateUserString);
							endate = sdf1.parse(enDateUserString);*/
							stdate = format1.parse(stDateUserString);
							endate = format1.parse(enDateUserString);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						items.startDate = new java.sql.Date(stdate.getTime());
						items.endDate = new java.sql.Date(endate.getTime());
					}else{
	
						sub = true;
						items.cuisineId = Integer.valueOf(order[0]);
						items.categoryId = Integer.valueOf(order[1]);
						items.quantity = Integer.valueOf(order[2]);
						items.price = Double.valueOf(order[3]);
						items.day = order[4];
						items.meal = order[5];
						String stDateUserString = order[6].trim();
						String enDateUserString = order[7].trim();
						items.timsSlot = order[8];
						SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
						java.util.Date stdate = null;
						java.util.Date endate = null;
						try {
							stdate = sdf1.parse(stDateUserString);
							endate = sdf1.parse(enDateUserString);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						items.startDate = new java.sql.Date(stdate.getTime());
						items.endDate = new java.sql.Date(endate.getTime());
					}
				}
				orderItemList.add(items); 	
			}
		}
		System.out.println("sub-"+sub);
		/*if(orderType.equalsIgnoreCase("SUBSCRIPTION")){*/
		if(sub){
			//place subscription order
			System.out.println("Subscription order. . .");
			orderPlaced = DBConnection.placeSubscriptionOrder(mailid, contactNumber
					, city, location,pincode, subscriptiontype , deliveryZone, deliveryAddress, instruction
					, day, orderItemList);
			System.out.println("status send to app--"+orderPlaced);
			return orderPlaced; 
		}else{
			//place regular order
			System.out.println("Regular order. . .");
	
			orderPlaced = DBConnection.checkOut(mailid, contactNumber, guestName,
					city, location,pincode, getLocationId(location, city),mealtype,timeslot, orderItemList,
					deliveryZone,deliveryAddress,instruction,deliveryDay);
		}
		System.out.println("Place Order status::"+orderPlaced);
		return orderPlaced;
	}
	
	@POST
	@Path("/check")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject check(@FormParam("details")String data ) throws JSONException{
		JSONObject object = new JSONObject();
		System.out.println("Data - "+data);
		ArrayList<OrderItems> orderItemList = new  ArrayList<OrderItems>();
		if(data.contains(",")){
			//object.put("message", "MORE THAN 1 DATA");
			//sepatere all items
			String[] item = data.split(",");
	
			System.out.println("ios data length->"+item.length);
			for(int i=0 ;i<item.length ; i++){
				//System.out.println(i+ " pos string = "+item[i]);
				OrderItems items = new OrderItems();
				String itemDetailStr = item[i];
				String[] itemArr = itemDetailStr.split("\\$");
				//System.out.println("item arr length->"+itemArr.length);
				for(int j=0;j<1 ; j++){
					//System.out.println("- - - - -"+j+" round iteration start- - - -");
					items.cuisineId = Integer.valueOf(itemArr[0]);
					//	System.out.println("Cuisine id->"+items.cuisineId);
					items.categoryId = Integer.valueOf(itemArr[1]);
					//	System.out.println("Category id->"+items.categoryId);
					items.quantity = Integer.valueOf(itemArr[2]);
					//	System.out.println("qty id->"+items.quantity);
					items.price = Double.valueOf(itemArr[3]);
					//	System.out.println("Price->"+items.price);
					orderItemList.add(items);
					//	System.out.println("- - - - -"+j+" round iteration end list added- - - -");
				}
			}
			//System.out.println("Order item list size - "+orderItemList.size());
			object.put("message", orderItemList.size());
		}else{
	
			OrderItems items = new OrderItems();
			String itemDetailStr = data;
			String[] itemArr = itemDetailStr.split("\\$");
			//System.out.println("item arr length->"+itemArr.length);
			for(int j=0;j<1 ; j++){
				//System.out.println("- - - - -"+j+" round iteration start- - - -");
				items.cuisineId = Integer.valueOf(itemArr[0]);
				//	System.out.println("Cuisine id->"+items.cuisineId);
				items.categoryId = Integer.valueOf(itemArr[1]);
				//	System.out.println("Category id->"+items.categoryId);
				items.quantity = Integer.valueOf(itemArr[2]);
				//	System.out.println("qty id->"+items.quantity);
				items.price = Double.valueOf(itemArr[3]);
				//	System.out.println("Price->"+items.price);
				orderItemList.add(items);
				//	System.out.println("- - - - -"+j+" round iteration end list added- - - -");
			}
			object.put("message", orderItemList.size());
		}
		return object;
	}
	
	
	
	}
