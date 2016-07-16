package com.mkyong.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.jws.Oneway;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.mkyong.rest.OrderItems; 

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;

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
		
		JSONObject jsonObject ;
		jsonObject = DBConnection.shareRegId(regid, emailid);
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
			@FormParam("instruction")String instruction) throws JSONException{
		
		System.out.println("saveaddress webservice is called * * * * * * *with address type-->"+addressType+" and phone no--->"+phNumber+" user-->"+user);
		System.out.println("Mailid->"+mailId+" city->"+city+" location->"+location+" pincode->"+pincode);
		System.out.println("user length-->"+user.length());
		JSONObject saveAddress ; 
		
		saveAddress = DBConnection.saveAddress(addressType, mailId, flatNo, streetName, landMark, city, location, 
				pincode,name,phNumber,user,deliveryZone,deliveryAddress,instruction);
		
		System.out.println("saveaddress webservice json response::"+saveAddress);
		
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
		JSONObject fetchalladdresstypeObject  ;
		/*if(mobNo.length()!=0){
			fetchalladdresstypeObject = DBConnection.fetchalladdresstype(mobNo);
			
			System.out.println("fetchalladdresstype web service end *****"+fetchalladdresstypeObject);
			return fetchalladdresstypeObject;
		}else{
			fetchalladdresstypeObject.put("addresstypelist", " ");
			return fetchalladdresstypeObject;
		}*/
		fetchalladdresstypeObject = DBConnection.fetchalladdresstype(mobNo);
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
		System.out.println("login webservice is called * * * * * * * * with mobile no-->"+mobNo+" and password-->"+password);
		JSONObject object ; 
		
		object = DBConnection.checklogin(mobNo, password);
		
		System.out.println("login webservice json response status::"+object);
		
		return object;
		
	}
	
	@POST
	@Path("/userlogin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject userLogin(@FormParam("mobileNumber")String mobNo,
			@FormParam("password")String password ) throws JSONException{
		System.out.println("User login webservice is called * * * * * * * * with mobile no-->"+mobNo+" and password-->"+password);
		JSONObject object ; 
		
		object = DBConnection.checkUserlogin(mobNo, password);
		
		System.out.println("User login webservice json response status::"+object);
		
		return object;
		
	}
	
	@POST
	@Path("/signup")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject signUp(@FormParam("name")String name,
			@FormParam("email")String email,
			@FormParam("contactnumber")String contactNumber,
			//@FormParam("city")String city,
			@FormParam("password")String password) throws JSONException{
		System.out.println("Sign up webservice is called...");
		System.out.println("name--"+name+" email-"+email+"  number-"+contactNumber+" password-"+password);
		JSONObject object ; 
		
		object = DBConnection.signUp(name, email, contactNumber,  password);
		//object = DBConnection.signUp(name,  contactNumber, password);
		System.out.println("Sign up status::"+object);
		
		return object;
	}
	
	@POST
	@Path("/forgotPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject forgotPassword(@FormParam("email")String email) throws Exception{
		System.out.println("forgotPassword webservice is called...with mail id-->"+email);
		JSONObject object ; 
		//object = DBConnection.getPassword(email);
		object = DBConnection.forgotPassword(email);
		System.out.println("forgotPassword status::"+object);
		return object;
	}
	
	@POST
	@Path("/changepassword")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject changePassword(
			@FormParam("phonenumber")String phoneNumber,
			@FormParam("oldpassword")String oldPassword,
			@FormParam("newpassword")String newPassword) throws JSONException{
		System.out.println("Phnumber--->"+phoneNumber);
		System.out.println("Old password---->"+oldPassword);
		System.out.println("New password--->"+newPassword);
		JSONObject changepasswordObject ; 
		changepasswordObject = DBConnection.changePassword(phoneNumber, oldPassword, newPassword);
		System.out.println("change password json response::"+changepasswordObject);
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
	@Path("/getdeliveryordersforbiker")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getDeliveryOrdersForBikers(@FormParam("boyid") String uname) throws Exception{
		
		System.out.println("getdeliveryordersforbiker webservice is called..."+uname);
		
		JSONObject jorders = new JSONObject();
		
		jorders = DBConnection.getdeliveryordersforbiker(uname);
		
		return jorders;
	}
	
	
	@POST
	@Path("/pickuporder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject pickUpOrder(@FormParam("boyid") String boyUserId, @FormParam("orderno")String orderNo,
			@FormParam("kitchenId")String kitchenId) throws Exception{
		
		System.out.println("pickuporder webservice is called..."+boyUserId+" "+orderNo+" "+kitchenId);
		
		JSONObject jorders = new JSONObject();
		
		jorders = DBConnection.pickuporder(boyUserId, orderNo, kitchenId);
		
		return jorders;
	}
	
	@POST
	@Path("/deliverorder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deliverOrder(@FormParam("boyid") String boyUserId, @FormParam("orderno")String orderNo,
			@FormParam("kitchenId")String kitchenId) throws Exception{
		
		System.out.println("deliverorder webservice is called..."+boyUserId+" "+orderNo+" "+kitchenId);
		
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
	
	@GET
	@Path("/map")
	@Produces(MediaType.TEXT_HTML)
	public String map(@QueryParam("id")String subcriptionNo) throws Exception{
		System.out.println("Map called . . . . ");
		String[] latlng = new String[3];
		latlng = DBConnection.trackMyOrder(subcriptionNo);
		String[] homeltlng = new String[2];
		homeltlng = DBConnection.getLatLongPositions( DBConnection.getAddressOfUser(subcriptionNo) );
		String userAddress = DBConnection.getAddressOfUser(subcriptionNo);
		String DbDetails[] =  new String[2];;
			DbDetails =	DBConnection.getDriverNameNum(subcriptionNo);
		
		JSONArray markers =  new JSONArray();
			JSONObject markersource = new JSONObject();
			markersource.put("title", "Food Source");
			markersource.put("lat", latlng[0]);
			markersource.put("lng",latlng[1]);
			markersource.put("description", latlng[2]);
			
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
	@Path("/kitchenorders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject kitchenorders(@FormParam("kitchenid")String kitchenid) throws JSONException{
		System.out.println(" kitchenorders web service is called...");
		JSONObject kitchenorders = DBConnection.getKitchenOrders(kitchenid);
		System.out.println("kitchenorders web service is end here:"+kitchenorders.length());
		return kitchenorders;
	}
	
	@POST
	@Path("/receiveorder")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject receiveOrder(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno) throws Exception{
		System.out.println("Kitchen's receiveorder webservice is called * * * * * *Kitchen Name:"+kitchenName+" Order No-->"+orderno);
		JSONObject receiveOrderObject ;
		receiveOrderObject = DBConnection.receiveOrderFromKitchen(orderno, kitchenName);// need to be changed
		System.out.println("Kitchen's receiveorder webservice ends here * * * * * * "+receiveOrderObject.length());
		return receiveOrderObject;
	}
	
	@POST
	@Path("/notifylogistics")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject notifyLogistics(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno,@FormParam("boyId")String boyId) throws JSONException{
		System.out.println("Notify logistics web service is called*  * * * *"+kitchenName+" "+orderno+" "+boyId);
		JSONObject notifyObject ;
		notifyObject = DBConnection.notifyLogistics(orderno, kitchenName, boyId);// need to be changed
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		return notifyObject;
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
	
	@POST
	@Path("/deliveryToBoy")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deliveryToBoy(@FormParam("user")String kitchenName,
			@FormParam("orderno")String orderno,@FormParam("boyId")String boyId) throws Exception{
		System.out.println("deliveryToBoy web service is called*  * * * *");
		JSONObject notifyObject ;
		notifyObject = DBConnection.deliveryToBoy(orderno, kitchenName, boyId);
		System.out.println("Notify logistics web service JSON Object - - "+notifyObject);
		return notifyObject;
	}
	
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
			//@FormParam("contactName")String contactName,
		//	@FormParam("contactNumber")String contactNumber,
		//	@FormParam("flatNumber")String flatNumber,
		//	@FormParam("streetName")String streetName,
		//	@FormParam("landmark")String landmark,
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
		//System.out.println("Landmark-->"+landmark);  
		System.out.println("mobileno-->"+contactNumber);
		//System.out.println("streetName -->"+streetName);
		//System.out.println("flatNumber -->"+flatNumber);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.startDate = new java.sql.Date(stdate.getTime());
	    	    	items.endDate = new java.sql.Date(endate.getTime());
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
	  System.out.println("sub-"+sub);
		/*if(orderType.equalsIgnoreCase("SUBSCRIPTION")){*/
		if(sub){
			//place subscription order
			System.out.println("Subscription order. . .");
			/*orderPlaced = DBConnection.placeSubscriptionOrder(mailid, contactName,
					contactNumber, city, location, flatNumber,streetName,pincode,landmark , subscriptiontype,
					getLocationId(location, city), day, orderItemList);*/
			System.out.println("deliverypincode-"+pincode+" dZone--"+deliveryZone+" dAdd--"+deliveryAddress+" ins - -"+instruction);
			orderPlaced = DBConnection.placeSubscriptionOrder(mailid, contactNumber
					, city, location,pincode, subscriptiontype , deliveryZone, deliveryAddress, instruction
					, day, orderItemList);
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
			orderPlaced = DBConnection.placeOrder(mailid, contactNumber, guestName,
					 city, location,pincode, getLocationId(location, city),mealtype,timeslot, orderItemList,
					deliveryZone,deliveryAddress,instruction,deliveryDay);
		}
	 	
		System.out.println("Place Order status::"+orderPlaced);
		return orderPlaced;
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
		
		System.out.println("checkfeedback webservice is called...emailid->"+usermailId);
		
		JSONObject feedbackobject;
		 feedbackobject =  DBConnection.checkfeedback(usermailId);
		return feedbackobject;
	}
	
	@POST
	@Path("/submitfeedback")
	@Produces(MediaType.APPLICATION_JSON)
	/*public JSONObject submitfeedback(@FormParam("foodquality")String foodQuality,@FormParam("deliveryquality") String deliveryQuality,
			@FormParam("overallrating")String overallRating,@FormParam("usermailid") String userMailId  )throws Exception{*/
	/*public JSONObject submitfeedback(@FormParam("taste")String taste,@FormParam("ingredients")String ingredients,
			@FormParam("hygiene")String hygiene,@FormParam("spillage")String spillage,@FormParam("packing")String packing,
			@FormParam("delayed")String delayed,@FormParam("deliveryboy")String deliveryboy,
			@FormParam("cold")String coldFood,@FormParam("longprocess") String longprocess,
			@FormParam("userfriendly")String userfriendly,@FormParam("overallrating") String overallRating,
			@FormParam("foodcomment")String foodComment, @FormParam("usermailid") String userMailId  )throws Exception{*/
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
		
		System.out.println("fetchlocation webservice is called * * * * * * *");
		
		JSONObject jobjLocation ;
		
		jobjLocation = DBConnection.fetchlocation();
		
		System.out.println("End of fetchlocation webservice * * * * * * * * * * * * *");
		return jobjLocation;
	}
	
	@POST
	@Path("/fetchservinglocation")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchservinglocation() throws JSONException{
		
		System.out.println("fetchservinglocation webservice is called * * * * * * * ");
		
		JSONObject jobjLocation= DBConnection.fetchServinglocation();
		
		System.out.println("End of fetchservinglocation webservice * * * * * * * * * * * * *");
		return jobjLocation;
	}
	
	@POST
	@Path("/fetchlocationname")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchLocationList() throws JSONException{
		System.out.println("fetchServinglocationname webservice is called * * * * * * * ");
		
		JSONObject jobjLocation= DBConnection.getLocationName();
		
		System.out.println("End of fetchservinglocation webservice * * * * * * * * * * * * *");
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
	@POST
	@Path("/fetchcuisine")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchcuisinelist(@FormParam("city")String city,
			@FormParam("location")String location) throws Exception{
		
		System.out.println("fetchcuisine webservice is called * * * * * * *");
		
		JSONObject jobjCusine ;
		
		jobjCusine = DBConnection.fetchCuisineList(city,location);
		
		System.out.println("JSONObject in fetchCuisine webservice->"+jobjCusine);
		System.out.println("End of fetchCuisine webservice * * * * * * * * * * * * *");
		return jobjCusine;
	}
	
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
	@Path("/fetchallcategory")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject fetchAllCategories(@FormParam("city")String city,
			@FormParam("location")String location,@FormParam("pincode")String pincode) throws JSONException{
		
		System.out.println("fetchallcategory web service is called * * * pincode-"+pincode);
		System.out.println("pincode length->"+pincode.length());
		JSONObject fetchAllCategory;
		
		fetchAllCategory = DBConnection.fetchAllCategories(city, location, pincode);
		
		System.out.println("End of fetchallcategory webservice * * * * * * * * *");
		return fetchAllCategory;
	}
	
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
