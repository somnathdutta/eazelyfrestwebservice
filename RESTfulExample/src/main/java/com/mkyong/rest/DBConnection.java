package com.mkyong.rest;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;

import pojo.Biker;
import pojo.KitchenStock;
import pojo.MealTypePojo;
import pojo.PickJi;
import pojo.PickjiItem;
import pojo.TimeSlot;
import pojo.User;
import sql.SubscriptionPrePackQuery;
import utility.DateTimeSlotFinder;
import utility.LatLng;

import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import dao.BalanceDAO;
import dao.BikerDAO;
import dao.BookDriver;
import dao.CreditValueDAO;
import dao.CuisineKitchenDAO;
import dao.FetchCuisineDAO;
import dao.FetchLocationDAO;
import dao.ForgotPasswordDAO;
import dao.Invoice;
import dao.OrderDetailsDAO;
import dao.OrderItemDAO;
import dao.OrderTimeDAO;
import dao.OtpDAO;
import dao.PlaceOrderDAO;
import dao.PromoCodeDAO;
import dao.RoundRobinKitchenFinder;
import dao.SendMessageDAO;
import dao.ShareDAO;
import dao.SingleOrderDAO;
import dao.StockUpdationDAO;
import dao.TimeSlotFinder;
import dao.UserDetailsDao;

@SuppressWarnings("deprecation")
public class DBConnection {

	/**
     * Method to create DB Connection
     * 
     * @return
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public static Connection createConnection() throws Exception {
        Connection con = null;
        try {
            Class.forName(Constants.dbClass);
            con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser,Constants.dbPwd);
        } catch (Exception e) {
            throw e;
        } finally {
            return con;
        }
    }
    
    /**
     * WEB SERVICE FOR FORGOT PASSWORD
     * @param email
     * @return
     * @throws JSONException 
     */
    public static JSONObject getPassword(String email) throws JSONException{
		JSONObject jsonObject = new JSONObject() ;
		if(generateAndSendEmail(email)){
			jsonObject.put("status", true);
		}else{
			jsonObject.put("status", false);
		}
		return jsonObject;
    }
    
    public static Boolean generateAndSendEmail(String toAddress) { 
    	 Properties mailServerProperties;
    	 Session getMailSession;
    	 MimeMessage generateMailMessage;
    	// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.host", "smtp.gmail.com");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.user", "eazelyf@gmail.com");
		mailServerProperties.put("mail.smtp.password", "eazelyf1234");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
 
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		try {
			//generateMailMessage.setFrom(new InternetAddress("somnathdutta048@gmail.com"));
			generateMailMessage.setFrom(new InternetAddress("eazelyf@gmail.com"));
			generateMailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(toAddress) );
			generateMailMessage.setSubject("Regarding Password retrieval");
			String emailBody = getPasswordFromDB(toAddress) + "<br><br> Regards, <br>Eaze Lyf";
			generateMailMessage.setContent(emailBody, "text/html");
			System.out.println("Mail Session has been created successfully..");
			// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
	 
			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			//transport.connect("smtp.gmail.com", "<----- Your GMAIL ID ----->", "<----- Your GMAIL PASSWORD ----->");
			transport.connect("eazelyf@gmail.com", "eazelyf1234");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
			return true;
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			System.out.println("***IN FORGOT PASSWORD**ADDRESS EXCEPTION OCCURED**"+e.getMessage());
			return false;
		} catch (MessagingException e) {
			// TODO Auto-generated catch blocG-926503k
			System.out.println("***IN FORGOT PASSWORD**MessagingException OCCURED**"+e.getMessage());
			return false;
		}
		
	}
    
    public static String getPasswordFromDB(String emailId){
    	String password = "";
    	try {
    			Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT password FROM fapp_accounts WHERE email = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, emailId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							password = resultSet.getString("password");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(password.length()!=0){
    		password = "Your password is ::"+password;
    		return password;
    	}else{
    		 password = "Sorry no password found for your email id.";
    		return password;
    	}
    	
    }
    
    public static JSONObject forgotPassword(String receiverEmailID) throws IOException{
    	JSONObject jsonObject = new JSONObject();
    	//if(mailSender (receiverEmailID, "Regarding forgot password from eazelyf app", getPasswordFromDB(receiverEmailID) )){
    	if(mailSender (receiverEmailID, "Regarding forgot password from eazelyf app", ForgotPasswordDAO.createLinkForUser(receiverEmailID) )){
    	try {
				jsonObject.put("status", "200");
				jsonObject.put("message", "Password sent to your mailId "+receiverEmailID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}else{
    		//JSONObject jsonObject = new JSONObject();
    		try {
				jsonObject.put("status", "204");
				jsonObject.put("message", "Invalid email Id given");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		//return jsonObject;
    	}
    	return jsonObject;
    }
    
    public static boolean mailSender(String receiverEmailID, String emailSubject,
			String emailBody) throws IOException{
    	
    	String senderEmailID = "eazelyf@gmail.com";// change this to eazelyf's own mail id
    	String senderPassword = "eazelyf1234";// change this to eazelyf;s password
    	/*String senderEmailID = new PropertyFile().getPropValues("SENDER_MAIL_ID");
    	String senderPassword = new PropertyFile().getPropValues("SENDER_PASSWORD");*/
    	String emailSMTPserver = "smtp.gmail.com";
    	String emailServerPort = "465";
    	
    	Properties props = new Properties();
		props.put("mail.smtp.user", senderEmailID);
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailServerPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", emailServerPort);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		SecurityManager security = System.getSecurityManager();
		try {
			//Authenticator auth = new SMTPAuthenticator("eazelyf@gmail.com", "eazelyf1234");
			Authenticator auth = new SMTPAuthenticator(senderEmailID, senderPassword);
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setText(emailBody);
			msg.setSubject(emailSubject);
			msg.setFrom(new InternetAddress(senderEmailID));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					receiverEmailID));
			Transport.send(msg);
			System.out.println("Message send Successfully:)");
			return true;
		} catch (Exception mex) {
			System.out.println("**EXCEPTION OCCURED ***"+mex);
			return false;
		}
    }
  
    /*public static JSONObject checkUserlogin(String mobileNo, String password) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean loggedStatus = false;
    	String username = "";
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT * FROM fapp_accounts WHERE mobile_no = ? AND password = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						preparedStatement.setString(2, password);
						resultSet =  preparedStatement.executeQuery();
						if(resultSet.next()){
							loggedStatus = true;
							username = resultSet.getString("username");
						}	
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
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
    	if(loggedStatus){
    		if(username!=null){
				jsonObject.put("status", username);
			}else{
				jsonObject.put("status", mobileNo);
			}
    	}else{
    		jsonObject.put("status", "failed");
    	}
    	
    	return jsonObject;
    }*/
   
    
    public static JSONObject checklogin(String mobileNo, String password) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean loggedStatus = false;
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT * FROM fapp_accounts WHERE mobile_no = ? AND password = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						preparedStatement.setString(2, password);
						resultSet =  preparedStatement.executeQuery();
						if(resultSet.next()){
							loggedStatus = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(resultSet!=null){
							resultSet.close();
						}
					}
			}
    		System.out.println("Login sucess status new user - - - - >"+loggedStatus);
			//If first fails then
			if(!loggedStatus){
				System.out.println("First login fails. . . .second check. . . ");
				SQL1:{
						
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "SELECT * FROM fapp_accounts WHERE username = ? AND password = ?";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, mobileNo);
							preparedStatement.setString(2, password);
							resultSet =  preparedStatement.executeQuery();
							if(resultSet.next()){
								loggedStatus = true;
							}else{
								loggedStatus =  false;
							}
							if(loggedStatus)
							jsonObject.put("status", loggedStatus);
							else
								jsonObject.put("status", loggedStatus);
					    	//return jsonObject;
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
					 }
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	jsonObject.put("status", loggedStatus);
    	return jsonObject;
    }
    
    private static Boolean isAddressTypeExist(String useraddressType , String user ){
    	Boolean isexists = false;
    	ArrayList<AddressBean> addressBeanList = new ArrayList<AddressBean>();
    	try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					/*String sql = "SELECT address_type,contact_number from fapp_address_details where login_mobile_no = ? ";*/
					String sql = "SELECT address_type from fapp_address_details where login_mobile_no = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, user);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							AddressBean addressBean = new AddressBean();
							addressBean.addressType =  resultSet.getString("address_type");
							//addressBean.contactNumber = resultSet.getString("contact_number");
							addressBeanList.add(addressBean);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	/*if(addresstype.equalsIgnoreCase(addressType)){
    		isexists = true;
    	}else{
    		isexists = false;
    	}*/
    	System.out.println("Address list size-->"+addressBeanList.size());
    	/*for(int i=0;i<addressBeanList.size();i++){
    		if(addressBeanList.get(i).addressType.equals(addressType) && addressBeanList.get(i).contactNumber.equals(mobileNo)){
    			isexists = true;
    			System.out.println("Inside if block"+isexists);
    		}
    	}*/
    	for(AddressBean addressBean : addressBeanList){
    		/*System.out.println("BEAN VALUE--"+addressBean.addressType+"  "+addressBean.contactNumber);*/
    		System.out.println("From DB Address type--"+addressBean.addressType);
    		//	System.out.println("USER VALUE with contact no.--"+addressType+" "+mobileNo);
    		System.out.println("USER VALUE with user login no.--"+useraddressType+" "+user);
    		/*if(addressBean.addressType.equals(addressType) && addressBean.contactNumber.equals(mobileNo)){*/
    		if(addressBean.addressType.equalsIgnoreCase(useraddressType) ){	
    			isexists = true;
    			System.out.println("inside if isexists->"+isexists);
    			break;
    		}
    		System.out.println("outside if isexists->"+isexists);
    	}
    	System.out.println("outside for is address exists-"+isexists);
    	return isexists;
    }
    
    
    public static JSONObject saveAddress(String addressType , String mailId, String flatNo,
    	String streetName, String landMark,String city,String location,
    	String pincode,String user,
    	String deliveryZone,String deliveryAddress,String instruction) throws JSONException{
    	
    	Boolean inserted = false;
    	JSONObject saveAddress =  new JSONObject();
    	String contactName = getUserName(user);
    	if(isAddressTypeExist(addressType, user )){
    		System.out.println("If address type exists update the address !!!");
    		try {
    			Connection connection = DBConnection.createConnection();
    			SQL:{
        				
        				PreparedStatement preparedStatement = null;
        				/*String sql = "UPDATE fapp_address_details SET mail_id=? , flat_no=? , street_name=?"
        						   + " ,landmark=? , city=? , location = ?, pincode=? ,contact_name = ? ,address_type = ?, "
        						   + " contact_number = ?,delivery_zone = ?,delivery_address=?,instruction = ? "
        						   + "  WHERE login_mobile_no = ?" ; */
        				String sql = "UPDATE fapp_address_details SET mail_id=? ,  city=? , location = ?, pincode=? ,contact_name = ?,"
        						  + " delivery_zone = ?,delivery_address=?,instruction = ? "
     						      + "  WHERE login_mobile_no = ? and LOWER(address_type) = LOWER(?)" ; 
        					   
        				try {
    						preparedStatement = connection.prepareStatement(sql);
    						if(mailId!=null){
    							preparedStatement.setString(1, mailId);
    						}else{
    							preparedStatement.setNull(1, Types.NULL);
    						}
    						/*if(flatNo!=null){
    							preparedStatement.setString(2, flatNo);
    						}else{
    							preparedStatement.setNull(2, Types.NULL);
    						}
    						if(streetName!=null){
    							preparedStatement.setString(3, streetName);
    						}else{
    							preparedStatement.setNull(3, Types.NULL);
    						}
    						if(landMark!=null){
    							preparedStatement.setString(4, landMark);
    						}else{
    							preparedStatement.setNull(4,Types.NULL);
    						}*/
    						if(city!=null){
    							preparedStatement.setString(2, toCamelCase(city) );
    						}else{
    							preparedStatement.setNull(2, Types.NULL);
    						}
    						if(location!=null){
    							preparedStatement.setString(3, toCamelCase(location) );
    						}else{
    							preparedStatement.setNull(3, Types.NULL);
    						}
    						if(pincode!=null){
    							preparedStatement.setString(4, pincode);
    						}else{
    							preparedStatement.setNull(4, Types.NULL);
    						}
    						if(contactName!=null){
    							preparedStatement.setString(5, contactName);
    						}else{
    							preparedStatement.setNull(5, Types.NULL);
    						}
    						
    						/*if(addressType!=null){
    							preparedStatement.setString(6, addressType);
    						}else{
    							preparedStatement.setNull(6, Types.NULL);
    						}*/
    						/*if(contactNumber!=null){
    							preparedStatement.setString(10, contactNumber);
    						}else{
    							preparedStatement.setNull(10, Types.NULL);
    						}
    						if(user!=null){
    							preparedStatement.setString(11, user);
    						}else{
    							preparedStatement.setNull(11, Types.NULL);
    						}*/
    						if(deliveryZone!=null){
    							preparedStatement.setString(6, deliveryZone );
    						}else{
    							preparedStatement.setNull(6, Types.NULL);
    						}
    						if(deliveryAddress!=null){
    							preparedStatement.setString(7, deliveryAddress);
    						}else{
    							preparedStatement.setNull(7, Types.NULL);
    						}
    						if(instruction!=null){
    							preparedStatement.setString(8, instruction);
    						}else{
    							preparedStatement.setNull(8, Types.NULL);
    						}
    						preparedStatement.setString(9, user);
    						
    						preparedStatement.setString(10, addressType.toUpperCase() );
    						int count  = preparedStatement.executeUpdate();
    						if(count>0){
    							inserted = true;
    							System.out.println("adress updated");
    						}
    					} catch (Exception e) {
    						System.out.println(" - - - EXCEPTION AT UPDATE ADDRESS - -  - ");
    						e.printStackTrace();
    					}finally{
    						if(preparedStatement!=null){
    							preparedStatement.close();
    						}
    					}
        		}
    		
    			SQL:{
        			 PreparedStatement preparedStatement = null;
        			 String sql = "UPDATE fapp_address_details SET mail_id=? "
						      + "  WHERE login_mobile_no = ?" ; 
        			 try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mailId);
						preparedStatement.setString(2, user);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Email updated in all addresses!");
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
    		
    		if(inserted){
        		saveAddress.put("status", inserted);
        	}else{
        		saveAddress.put("status", inserted);
        	}
    		
    		
    /******************************** UPDATE address done here ******************************************************/		
    	}else{
    		System.out.println("If address type does not exists insert the address !!!");
    		if(user.length()!=0){
	    		try {
	    			SQL:{
	        				Connection connection = DBConnection.createConnection();
	        				PreparedStatement preparedStatement = null;
	        				/*String sql = "INSERT INTO fapp_address_details(address_type, mail_id,flat_no,street_name,"
	        						   + " landmark,city,location,pincode,contact_name,contact_number,login_mobile_no,"
	        						   + " delivery_zone,delivery_address,instruction )VALUES "
	        						   + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";*/
	        				String sql = "INSERT INTO fapp_address_details(address_type, mail_id,city,location,pincode,"
	        						+ "contact_name,login_mobile_no,"
	        						   + " delivery_zone,delivery_address,instruction )VALUES "
	        						   + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        				try {
	    						preparedStatement = connection.prepareStatement(sql);
	    						if(addressType!=null){
	    							preparedStatement.setString(1, addressType.toUpperCase());
	    						}else{
	    							preparedStatement.setNull(1, Types.NULL);
	    						}
	    						if(mailId!=null){
	    							preparedStatement.setString(2, mailId);
	    						}else{
	    							preparedStatement.setNull(2, Types.NULL);
	    						}
	    						/*if(flatNo!=null){
	    							preparedStatement.setString(3, flatNo);
	    						}else{
	    							preparedStatement.setNull(3, Types.NULL);
	    						}
	    						if(streetName!=null){
	    							preparedStatement.setString(4, streetName);
	    						}else{
	    							preparedStatement.setNull(4, Types.NULL);
	    						}
	    						
	    						if(landMark!=null){
	    							preparedStatement.setString(5, landMark);
	    						}else{
	    							preparedStatement.setNull(5, Types.NULL);
	    						}*/
	    						if(city!=null){
	    							preparedStatement.setString(3, city);
	    						}else{
	    							preparedStatement.setNull(3, Types.NULL);
	    						}
	    						if(location!=null){
	    							preparedStatement.setString(4, location);
	    						}else{
	    							preparedStatement.setNull(4, Types.NULL);
	    						}
	    						if(pincode!=null){
	    							preparedStatement.setString(5, pincode);
	    						}else{
	    							preparedStatement.setNull(5, Types.NULL);
	    						}
	    						if(contactName!=null){
	    							preparedStatement.setString(6, contactName);
	    						}else{
	    							preparedStatement.setNull(6, Types.NULL);
	    						}
	    						/*if(contactNumber!=null){
	    							preparedStatement.setString(7, contactNumber);
	    						}else{
	    							preparedStatement.setNull(7, Types.NULL);
	    						}*/
	    							preparedStatement.setString(7, user);
	    						
	    						if(deliveryZone!=null){
	    							preparedStatement.setString(8, deliveryZone);
	    						}else{
	    							preparedStatement.setNull(8, Types.NULL);
	    						}
	    						if(deliveryAddress!=null){
	    							preparedStatement.setString(9, deliveryAddress);
	    						}else{
	    							preparedStatement.setNull(9, Types.NULL);
	    						}
	    						if(instruction!=null){
	    							preparedStatement.setString(10, instruction);
	    						}else{
	    							preparedStatement.setNull(10, Types.NULL);
	    						}
	    						int count  = preparedStatement.executeUpdate();
	    						if(count>0){
	    							inserted = true;
	    							System.out.println("adress inserted");
	    						}
	    					} catch (Exception e) {
	    						e.printStackTrace();
	    					}finally{
	    						if(connection!=null){
	    							connection.close();
	    						}
	    					}
	        		}
	    		} catch (Exception e) {
	    			// TODO: handle exception
	    		}
	    		
	    		if(inserted){
	        		saveAddress.put("status", inserted);
	            	
	        	}else{
	        		saveAddress.put("status", inserted);
	            	
	        	}
	    		
	    		
	    /******************************** INSERT as NEW address ******************************************************/		
    	}/*else{
    			try {
        			SQL:{
            				Connection connection = DBConnection.createConnection();
            				PreparedStatement preparedStatement = null;
            				String sql = "INSERT INTO fapp_address_details(address_type, mail_id,flat_no,street_name,"
            						   + " landmark,city,location,pincode,contact_name,contact_number,login_mobile_no)VALUES "
            						   + "(?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
            				try {
        						preparedStatement = connection.prepareStatement(sql);
        						preparedStatement.setString(1, addressType);
        						preparedStatement.setString(2, mailId);
        						preparedStatement.setString(3, flatNo);
        						preparedStatement.setString(4, streetName);
        						preparedStatement.setString(5, landMark);
        						preparedStatement.setString(6, city);
        						preparedStatement.setString(7, location);
        						preparedStatement.setString(8, pincode);
        						preparedStatement.setString(9, contactName);
        						preparedStatement.setString(10, contactNumber);
        						preparedStatement.setString(11, "guest");
        						int count  = preparedStatement.executeUpdate();
        						if(count>0){
        							inserted = true;
        							System.out.println("adress inserted as guest user!");
        						}
        					} catch (Exception e) {
        						e.printStackTrace();
        					}finally{
        						if(connection!=null){
        							connection.close();
        						}
        					}
            		}
        		} catch (Exception e) {
        			// TODO: handle exception
        		}
    			
    		}*/
    	}
    	
    	System.out.println("Outside all blocks inserted =="+inserted);
    	/*if(inserted){
    		saveAddress.put("status", inserted);
        	return saveAddress;
    	}else{
    		saveAddress.put("status", inserted);
        	return saveAddress;
    	}*/
    	return saveAddress;
    }
    
    public static JSONObject deleteaddresstype(String mobNo,String addressType) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean deleted = false;
    	try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				PreparedStatement preparedStatement = null;
				String sql = "DELETE FROM fapp_address_details WHERE contact_number = ? AND address_type = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, mobNo);
					preparedStatement.setString(2, addressType);
					int updaterows = preparedStatement.executeUpdate();
					if(updaterows>0){
						deleted = true;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	jsonObject.put("status", deleted);
    	return jsonObject;
    }
    
    /*public static JSONObject fetchalladdresstype(String mobNo) throws JSONException{
    	JSONObject fetchalladdresstypeObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	try { 
    			Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT * FROM fapp_address_details WHERE login_mobile_no = ? order by address_type_id";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject temp = new JSONObject();
							//temp.put("name", resultSet.getString("contact_name")) ;
							temp.put("addresstype", resultSet.getString("address_type"));
							//temp.put("flatno", resultSet.getString("flat_no"));
							//temp.put("streetname", resultSet.getString("street_name"));
							//temp.put("landmark", resultSet.getString("landmark"));
							if(resultSet.getString("mail_id")!=null){
								temp.put("email", resultSet.getString("mail_id"));
							}else{
								temp.put("email", "");
							}
							
							if(resultSet.getString("city")!=null){
								temp.put("city", resultSet.getString("city"));
							}else{
								temp.put("city", " ");
							}
							if(resultSet.getString("location")!=null){
								temp.put("location", resultSet.getString("location"));
							}else{
								temp.put("location", " ");
							}
							if( resultSet.getString("pincode")!=null){
								temp.put("pincode", resultSet.getString("pincode"));
							}else{
								temp.put("pincode"," ");
							}
							
							if(resultSet.getString("delivery_zone")!=null){
								temp.put("deliveryzone", resultSet.getString("delivery_zone"));
							}else{
								temp.put("deliveryzone", " ");
							}
							if(resultSet.getString("delivery_address")!=null){
								temp.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								temp.put("deliveryaddress", " ");
							}
							if(resultSet.getString("instruction")!=null){
								temp.put("instruction", resultSet.getString("instruction"));
							}else{
								temp.put("instruction", " ");
							}
							
							jsonArray.put(temp);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	fetchalladdresstypeObject.put("addresstypelist", jsonArray);
    	return fetchalladdresstypeObject;
    }*/
    
    public static JSONObject retrieveAddress(String mailId , String addressType) throws JSONException{
    	JSONObject retreiveAddress =  new JSONObject();
    	JSONObject jsonObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	//System.out.println("address type->"+addressType+" address type length-->"+addressType.length());
    	if( addressType != null ){
    		try {
					Connection  connection = DBConnection.createConnection();
					SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = " SELECT * FROM fapp_address_details WHERE mail_id = ? AND address_type = ?";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, mailId);
							preparedStatement.setString(2, addressType);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								//JSONObject jsonObject = new JSONObject();
								jsonObject.put("flatno", resultSet.getString("flat_no"));
								jsonObject.put("streetname", resultSet.getString("street_name"));
								jsonObject.put("landmark", resultSet.getString("landmark"));
								jsonObject.put("city", resultSet.getString("city"));
								jsonObject.put("location", resultSet.getString("location"));
								jsonObject.put("pincode", resultSet.getString("pincode"));	
								jsonArray.put(jsonObject);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
					}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
    		
	    	retreiveAddress.put("deliveryaddress", jsonArray);
    		
	    	/*return retreiveAddress;*/
    	}	
    	else{
    		try {
				Connection  connection = DBConnection.createConnection();
				SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = " SELECT * FROM fapp_address_details WHERE mail_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mailId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject tempjsonObject = new JSONObject();
							tempjsonObject.put("addresstype", resultSet.getString("address_type"));
							tempjsonObject.put("flatno", resultSet.getString("flat_no"));
							tempjsonObject.put("streetname", resultSet.getString("street_name"));
							tempjsonObject.put("landmark", resultSet.getString("landmark"));
							tempjsonObject.put("city", resultSet.getString("city"));
							tempjsonObject.put("location", resultSet.getString("location"));
							tempjsonObject.put("pincode", resultSet.getString("pincode"));	
							jsonArray.put(tempjsonObject);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
			
		} catch (Exception e) {
			// TODO: handle exception
			}
    		/*if(jsonArray.length()!=0)
    	    	retreiveAddress.put("deliveryaddress", jsonArray);
        		
        	else{
        		jsonArray.put(" ");
        		retreiveAddress.put("deliveryaddress", jsonArray);	
        	}
        		*/
    	    retreiveAddress.put("deliveryaddress", jsonArray);
    		
    		//retreiveAddress.put("deliveryaddress", jsonArray);
        	//return retreiveAddress;
    	}
    	return retreiveAddress;
    	
    }
    
   /* public  static JSONObject signUp(String name, String email, 
    		String contactNumber,String city ,String password) throws JSONException{*/
    public  static JSONObject signUp(String name, String email,
    		String contactNumber,String password,String referalCode, String otp) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean insertStatus = false;
    	String myReferalCode = null;
    	boolean isRefCodeExists = false,isNewUserCreated=false;
    	
    	double[] creditValue = new double[2];
    	creditValue = CreditValueDAO.getCreditAndSignUpValue();
    	double signUpCreditAmount = creditValue[0];
    	
    	
    //	if(!(getContactsFromDB().contains(contactNumber)) ){
    	if(referalCode.trim().length()>0 ){
    		System.out.println("Referral code given::"+referalCode);
    		//When referral code given by user
    		isRefCodeExists = isRefCodeExists(referalCode);
    		if(isRefCodeExists){
    			System.out.println("Referral code exists...");
    			//If given referral code exists for somebody
    			double myBalance = signUpCreditAmount;
    			if(OtpDAO.isValidOtp(contactNumber, otp)){
    				User referalUser = new User(name, password, email, contactNumber, referalCode, myBalance);
        			//just try to insert data in db for new user
        			isNewUserCreated = doSignUpFor(referalUser);
        			if(isNewUserCreated){
        				/*System.out.println("New user created...and now update balance. . .");
        				insertStatus = updateBalanceForReferredUserFrom(referalCode.trim());
        				if(insertStatus){
        					System.out.println("Referred user balance updated...");
        					myReferalCode = generateReferalCode(name);
        					System.out.println("New users code ::"+myReferalCode);
        					updateMyCode(myReferalCode, contactNumber);
        					OtpDAO.deleteOtp(contactNumber);
        					jsonObject.put("status", true);
                			jsonObject.put("message", "Thank you for registration!");
        				}else{
        					OtpDAO.deleteOtp(contactNumber);
        					jsonObject.put("status", true);
                			jsonObject.put("message", "User created but user balance updation failed!");
        				}*/
        				System.out.println("New User created!");
        				myReferalCode = generateReferalCode(name);
        				System.out.println("New users code ::"+myReferalCode);
    					updateMyCode(myReferalCode, contactNumber, false);
    					OtpDAO.deleteOtp(contactNumber);
    					jsonObject.put("status", true);
            			jsonObject.put("message", "Thank you for registration!");
        			}else{
            			jsonObject.put("status", false);
            			jsonObject.put("message", "The mobile number is already registered");
            		}
    			}else{
    				jsonObject.put("status", false);
        			jsonObject.put("message", "Invalid OTP given!");
    			}
    			
    		}else{
    			jsonObject.put("status", false);
    			jsonObject.put("message", "Referral code is Invalid");
    		}
    	}else{
    		System.out.println("Referral code not given::"+referalCode);
    		//when referral code not given by user, just try to insert data in db for new user
    		if(OtpDAO.isValidOtp(contactNumber, otp)){
    			User newUser = new User(name, password, email, contactNumber,referalCode,0.0);
        		isNewUserCreated = doSignUpFor(newUser);
        		if(isNewUserCreated){
        			insertStatus = true;
        			myReferalCode = generateReferalCode(name);
    				updateMyCode(myReferalCode, contactNumber, false);
    				OtpDAO.deleteOtp(contactNumber);
        			jsonObject.put("status", true);
        			jsonObject.put("message", "Thank you for registration!");
        		}else{
        			jsonObject.put("status", false);
        			jsonObject.put("message", "The mobile number is already registered");
        		}
    		}else{
    			jsonObject.put("status", false);
    			jsonObject.put("message", "Invalid OTP given!");
    		}
    		
    	}
    		/*try {
    			Connection connection = DBConnection.createConnection();
    			SQL:{
    					PreparedStatement preparedStatement = null;
    					String sql = "INSERT INTO fapp_accounts(username,email,mobile_no,password) "
    							   + " VALUES(?,?, ?,?)";
    					String sql = "INSERT INTO fapp_accounts(username,email,mobile_no,password) "
 							   + " VALUES(?, ?, ?,?)";
    					try {
    						preparedStatement = connection.prepareStatement(sql);
    						preparedStatement.setString(1, name);
    						preparedStatement.setString(2, email);
    						preparedStatement.setString(3, contactNumber); 
    						//preparedStatement.setString(4, city);
    						preparedStatement.setString(4, password);    		
    						preparedStatement.setString(1, name);
    						if(email!=null){
    							preparedStatement.setString(2, email);
    						}else{
    							preparedStatement.setNull(2, Types.NULL);
    						}
    						preparedStatement.setString(3, contactNumber); 
    						preparedStatement.setString(4, password);    
    						
    						int count =  preparedStatement.executeUpdate();
    						if(count>0){
    							insertStatus = true;
    							
    							System.out.println("Mobile no. "+contactNumber+" is registerd with us successfully!");
    						}
    					} catch (Exception e) {
    						// TODO: handle exception
    						System.out.println("ERROR DUE TO:"+e);
    						insertStatus = false;
    					}finally{
    						if(connection!=null){
    							connection.close();
    						}
    					}
    			}
    		} catch (Exception e) {
    			// TODO: handle exception
    		}*/
    	/*}else{
    		insertStatus = false;
    		System.out.println("Mobile no. "+contactNumber+" is already registerd!");
    	}*/
    		/*if(insertStatus){
    			myReferalCode = generateReferalCode(name);
    			if(myReferalCode!=null){
    				if(updateMyCode(myReferalCode, contactNumber)){
        				System.out.println("Your code is :: "+myReferalCode);
        			}else{
        				System.out.println("Updation failed in code generation falied. . ");
        			}
    			}else{
    				System.out.println("Referaal code generation falied. . ");
    			}
    		}
    	jsonObject.put("status", insertStatus);*/
    	return jsonObject;
    }
    
    public static boolean doSignUpFor(User user){
    	boolean signedUp = false;
    	try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					/**
					 * Initial share and earn logic no balance update on sign up with ref code
					 */
					/*String sql = "INSERT INTO fapp_accounts(username,email,mobile_no,password,ref_code) "
							   + " VALUES(?, ?, ?,?,?)";*/
					/**
					 * Current share and earn logic update balance 50 on sign up with ref code
					 */
					String sql = "INSERT INTO fapp_accounts(username,email,mobile_no,password,ref_code,my_balance,role_id) "
							   + " VALUES(?, ?, ?, ?, ?, ?, 4)";
					try {
						preparedStatement = connection.prepareStatement(sql);
					
						preparedStatement.setString(1, user.getUserName());
						if(user.getEmailId()!=null){
							preparedStatement.setString(2, user.getEmailId());
						}else{
							preparedStatement.setNull(2, Types.NULL);
						}
						preparedStatement.setString(3, user.getContactNumber()); 
						preparedStatement.setString(4, user.getPassword());    
						
						if(user.getReferalCode()!=null){
							preparedStatement.setString(5, user.getReferalCode());
						}else{
							preparedStatement.setNull(5, Types.NULL);
						}
						
						if(user.getReferalCode()!=null){
							preparedStatement.setDouble(6, user.getMyBalance());
						}else{
							preparedStatement.setNull(6,Types.NULL);
						}
						
						int count =  preparedStatement.executeUpdate();
						if(count>0){
							signedUp = true;
							
							System.out.println("Mobile no. "+user.getContactNumber()+" is registerd with us successfully!");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR in doSignUp() DUE TO: "+e);
						signedUp = false;
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return signedUp;
    }
    
    public static boolean updateBalanceForReferredUserFrom(String referralCode){
    	boolean balanceUpdated = false;
    	String userMobile = null;
    	
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL1:{
    			System.out.println("Balance updating called..");
    			 PreparedStatement preparedStatement = null;
    			 String sql = "UPDATE fapp_accounts SET my_balance = my_balance + 50.0 where my_code = ?";
    			 try {
					preparedStatement = connection.prepareStatement(sql);
					//preparedStatement.setDouble(1, 50);
					preparedStatement.setString(1, referralCode);
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
    	return balanceUpdated;
    }
    
    public static JSONObject getBalance(String mobileNo) throws JSONException{
    	JSONObject balanceObject = new JSONObject();
    	String myCode = null;
    	double myBalance = 0.0,credit=0.0;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT my_balance,my_code from fapp_accounts where mobile_no = ? ";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							 myCode = resultSet.getString("my_code");
							 myBalance = resultSet.getDouble("my_balance");
							 credit = ShareDAO.getShareCredit();
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Getting balance failed due to: "+e);
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
    	
    	if(myCode!=null){
    		balanceObject.put("status", "200");
    		balanceObject.put("mycode", myCode);
    		if(myBalance>0.0){
    			balanceObject.put("mybalance", myBalance);
    		}else{
    			balanceObject.put("mybalance", myBalance);
    		}
    		if(credit>0.0){
    			balanceObject.put("creditValue", credit);
    		}else{
    			balanceObject.put("creditValue", credit);
    		}
    		System.out.println("MY CODE: "+myCode+" BAL: "+myBalance+" Credit: "+credit);
    	}else{
    		balanceObject.put("status", "204");
    		balanceObject.put("mycode", "");
    		balanceObject.put("mybalance", myBalance);
    		balanceObject.put("creditValue", credit);
    	}
    	/*if(myCode!=null){
			balanceObject.put("mycode", myCode);
		}else{
			balanceObject.put("mycode", " ");
		}
		if(myBalance!=null){
			balanceObject.put("mybalance", myBalance);
		}else{
			balanceObject.put("mybalance", myBalance);
		}*/
    	return balanceObject;
    }
    
    public static Boolean isValidPassword(String oldPassword,  String mobNo){
    	Boolean isValid = false;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT * FROM fapp_accounts WHERE password = ?  AND mobile_no = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, oldPassword);
						//preparedStatement.setString(2, email);
						preparedStatement.setString(2, mobNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							isValid = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null ){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return isValid;
    }
    
    public static JSONObject changePassword(String phnNumber, String oldPassword, String newPassword) throws JSONException{
    	JSONObject changePasswordObject =  new JSONObject();
    	Boolean updated = false;
    	if(isValidPassword(oldPassword,  phnNumber)){
    		try {
        		
    			SQL:{
        				Connection connection = DBConnection.createConnection();
        				PreparedStatement preparedStatement = null;
        				String sql  = "UPDATE fapp_accounts SET password = ? WHERE mobile_no = ? ";
        				try {
    						preparedStatement =  connection.prepareStatement(sql);
    						preparedStatement.setString(1, newPassword);
    						//preparedStatement.setString(2, email);
    						preparedStatement.setString(2, phnNumber);
    						int count = preparedStatement.executeUpdate();
    						if(count>0){
    							updated = true;
    						}
    					} catch (Exception e) {
    						e.printStackTrace();
    					}finally{
    						if(connection!= null){
    							connection.close();
    						}
    					}
        		}
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
		}else{
			updated = false;
		}
    	
    	changePasswordObject.put("status", updated);
    	return changePasswordObject;
    }
    
    private static List<String> getContactsFromDB(){
		List<String> contactList = new ArrayList<String>();
		try {
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				Connection connection = DBConnection.createConnection();
				try {
					
					String sql = "SELECT mobile_no from fapp_accounts ";
					preparedStatement = connection.prepareStatement(sql);
					resultSet =  preparedStatement.executeQuery();
					while (resultSet.next()) {
						contactList.add( resultSet.getString(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
			}
		} catch (Exception e) {
			// 
		}	
		return contactList;
	}
    
   
    /**
     * A WEB SERVICE for sharing reg id to server
     * @throws JSONException 
     */
    public static JSONObject shareRegId(String regid, String emailid) throws JSONException{
    	JSONObject jsonObject = new JSONObject();
    	Boolean insertStatus =  false;
    	Connection connection = null;
		
		List<String> emailList = getEmailIdFromDB();
		
		if(emailList.contains(emailid)){
			try {
				connection = DBConnection.createConnection();
				SQL:{
						PreparedStatement preparedStatement = null;
						String sql = "UPDATE fapp_devices SET device_reg_id = ? WHERE email_id = ?";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, regid);
							preparedStatement.setString(2, emailid);
							int count = preparedStatement.executeUpdate();
							if(count>0){
								insertStatus = true;
							}else{
								insertStatus = false;
							}
						}  catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(connection!=null){
								connection.close();
							}
						}	
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			jsonObject.put("status", "inserted");
		}else{
			jsonObject.put("status","already exists");
			System.out.println("JSONObject in shareRegId:"+jsonObject);
		}
		
		List<String> regList = getRegIdFromDB();
		if(!regList.contains(regid)){
			
	    	try {
				 connection = DBConnection.createConnection();
				SQL:{
						PreparedStatement preparedStatement = null;
						String sql = "INSERT INTO fapp_devices( "
								 +" device_reg_id,email_id) "
	           				 +" VALUES (?,?)";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, regid);
							preparedStatement.setString(2, emailid);
							int count = preparedStatement.executeUpdate();
							if (count > 0){ 
								insertStatus = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(connection!=null){
								connection.close();
							}
						}	
					}	
			} catch (Exception e) {
			
				}

			jsonObject.put("status", "inserted");
			
	    	System.out.println("JSONObject in shareRegId:"+jsonObject);
		}else{
			jsonObject.put("status","already exists");
			System.out.println("JSONObject in shareRegId:"+jsonObject);
		}
    	return jsonObject;
    }
    
    
	private static List<String> getRegIdFromDB(){
		List<String> regIdSet = new ArrayList<String>();
		try {
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				Connection connection = DBConnection.createConnection();
				try {
					String sql = "SELECT device_reg_id from fapp_devices ";
					preparedStatement = connection.prepareStatement(sql);
					resultSet =  preparedStatement.executeQuery();
					while (resultSet.next()) {
						regIdSet.add( resultSet.getString(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
			}
		} catch (Exception e) {
			// 
		}	
		return regIdSet;
	}
	
	private static List<String> getEmailIdFromDB(){
		List<String> emailIdSet = new ArrayList<String>();
		try {
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				Connection connection = DBConnection.createConnection();
				try {
					
					String sql = "SELECT email_id from fapp_devices";
					preparedStatement = connection.prepareStatement(sql);
					resultSet =  preparedStatement.executeQuery();
					while (resultSet.next()) {
						emailIdSet.add( resultSet.getString(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
			}
		} catch (Exception e) {
			// 
		}	
		return emailIdSet;
	}
    
    
    public static JSONObject orderHistory(String userMailId , Date sDate, Date eDate) throws JSONException{
    	JSONArray orderHistoryArray = new JSONArray();
    	JSONObject orderHistory = new JSONObject();
    	try {
    		Connection dbConnection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select * from vw_order_history where user_mail_id = ? and order_date>= ? AND order_date<= ?";
    						/*"select " 
			    				+" foud.city,"
			    				+" foud.flat_no,"
			    				+" foud.street_name,"
			    				+" foud.landmark,"
			    				+" foud.pincode,"
			    				+" fo.order_id,"
			    				+" fo.order_no,"
			    				+" fos.order_status_name,"
			    				+" fo.meal_type,"
			    				+" fo.time_slot"
			    				+" foud.delivery_zone,"
			    				+" foud.delivery_address,"
			    				+" foud.instruction,"
			    				+" fo.order_date::date"
			    				+" from fapp_orders fo,fapp_order_user_details foud,"
			    				+" fapp_order_status fos "
			    				+" where "
			    				+" fo.user_mail_id =?"
			    				+" AND "
			    				+" fo.order_id = foud.order_id"
			    				+" AND fo.order_status_id = fos.order_status_id"
			    				+" AND (fo.order_status_id = 7"
			    				+ " OR fo.order_status_id = 10)";*/
    				
    			
    				try {
						preparedStatement = dbConnection.prepareStatement(sql);
						preparedStatement.setString(1, userMailId);
						preparedStatement.setDate(2, sDate);
						preparedStatement.setDate(3, eDate);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject orders = new JSONObject();
							Double payamount = resultSet.getDouble("final_price");
							if(payamount!=null){
								orders.put("payamount", payamount.toString());
							}else{
								orders.put("payamount", "0.0");
							}
							orders.put("pincode", resultSet.getString("pincode"));
							orders.put("orderid", resultSet.getInt("order_id"));
							orders.put("orderno", resultSet.getString("order_no"));
							if( resultSet.getString("meal_type")!=null){
								orders.put("mealtype", resultSet.getString("meal_type"));
							}else{
								orders.put("mealtype", " ");
							}
							if( resultSet.getString("time_slot")!=null){
								orders.put("timeslot", resultSet.getString("time_slot"));
							}else{
								orders.put("timeslot", " ");
							}
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							String orderDate="",reformattedOrderDate="",deliveryDate="",reformattedDeliveryDate="";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							orderDate = resultSet.getString("order_date");
							if(resultSet.getString("delivery_date")!=null){
								deliveryDate = resultSet.getString("delivery_date");
							}else{
								deliveryDate = orderDate;
							}
							try {
								reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
								reformattedDeliveryDate = myFormat.format(fromUser.parse(deliveryDate));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							orders.put("orderdate", reformattedOrderDate);
							orders.put("deliverydate", reformattedDeliveryDate);
							orders.put("startdate", " ");
							orders.put("enddate", " ");
							orders.put("orderstatus", resultSet.getString("order_status_name"));
							/*orders.put("itemdetails", getitemdetails(orders.getInt("orderid")));*/
							orders.put("itemdetails", getitemdetails(orders.getString("orderno")));
							orderHistoryArray.put(orders);
						}
					}  catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(dbConnection!=null){
							dbConnection.close();
						}
					}	
    			}
    		
		} catch (Exception e) {
		
		}
    	
    	if(orderHistoryArray.length()!=0){
    		
    		orderHistory.put("orderhistory", orderHistoryArray);
    	}
    	else{
    		System.out.println("No data found with date range!!!");
    		orderHistory.put("orderhistory", new JSONArray());
    	}
    	return orderHistory;
    	
    }
    
    /**
     * A WEB SERVICE for tracking orders
     * @param userMailId
     * @return
     * @throws JSONException
     */
    public static JSONObject trackOrder(String userMailId, String mobileno) throws JSONException{
    	JSONArray orderTrackArray = new JSONArray();
    	JSONObject orderTrack = new JSONObject();
    	boolean isGuestUser = false, isRegisteredUser = false;
    /*******************For regular order ********************************************/	
    	/*if(userMailId!=null && (mobileno==null || mobileno.trim().length()==0)){*/
    	if(mobileno.trim().length()==0){
    		System.out.println("Guest users try to track the order...");
    		isGuestUser = true;
    	}else{
    		System.out.println("Registered users try to track the order...");
    		isRegisteredUser = true;
    	}
    		try {
        		Connection connection = DBConnection.createConnection();
    			/******SQL BLOCK FROM HERE*************/
        		SQL:{
        				PreparedStatement preparedStatement = null;
        				ResultSet resultSet = null;
        				String sqlGuest = "SELECT * from vw_track_order where user_mail_id = ? and (order_date=current_date OR delivery_date=current_date)";
        				String sqlRegistered = "SELECT * from vw_track_order where contact_number = ? and (order_date=current_date OR delivery_date=current_date)";
        						/* "select " 
    			    				+" foud.city,"
    			    				+" foud.flat_no,"
    			    				+" foud.street_name,"
    			    				+" foud.pincode,"
    			    				+" foud.landmark,"
        							+" foud.pincode,"
    			    				+" fo.order_no,"
    			    				+" fo.order_id,"
    			    				+" fos.order_status_name,"
    			    				+" fo.meal_type,"
    			    				+" fo.time_slot,"
    			    				+" foud.delivery_zone,"
    			    				+" foud.delivery_address,"
    			    				+" foud.instruction,"
    			    				+" fo.order_date::date"
    			    				+" from fapp_orders fo,fapp_order_user_details foud,"
    			    				+" fapp_order_status fos "
    			    				+" where "
    			    				+" fo.user_mail_id =?"
    			    				+" AND "
    			    				+" fo.order_id = foud.order_id"
    			    				+" AND fo.order_status_id = fos.order_status_id";*/
    			    				
        				
        				try {
        					if(isGuestUser){
        						preparedStatement = connection.prepareStatement(sqlGuest);
        						preparedStatement.setString(1, userMailId);
        					}
        					if(isRegisteredUser){
        						preparedStatement = connection.prepareStatement(sqlRegistered);
        						preparedStatement.setString(1, mobileno);
        					}
    						
    						resultSet = preparedStatement.executeQuery();
    						while(resultSet.next()){
    							JSONObject orders = new JSONObject();
    							Double payamount = resultSet.getDouble("final_price");
    							if(payamount!=null){
    								orders.put("payamount", payamount.toString());
    							}else{
    								orders.put("payamount", "0.0");
    							}
    							orders.put("pincode", resultSet.getString("pincode"));
    							orders.put("orderid", resultSet.getInt("order_id"));
    							orders.put("orderno", resultSet.getString("order_no"));
    							if( resultSet.getString("meal_type")!=null){
    								orders.put("mealtype", resultSet.getString("meal_type"));
    							}else{
    								orders.put("mealtype", " ");
    							}
    							if( resultSet.getString("time_slot")!=null){
    								orders.put("timeslot", resultSet.getString("time_slot"));
    							}else{
    								orders.put("timeslot", " ");
    							}
    							
    							if( resultSet.getString("delivery_address")!=null){
    								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
    							}else{
    								orders.put("deliveryaddress", " ");
    							}
    							if(resultSet.getString("driver_name")!=null){
    								orders.put("drivername", resultSet.getString("driver_name"));
    							}else{
    								orders.put("drivername", " ");
    							}
    							if(resultSet.getString("driver_number")!=null){
    								orders.put("drivernumber", resultSet.getString("driver_number"));
    							}else{
    								orders.put("drivernumber", " ");
    							}
    							
    							orders.put("orderstatus", resultSet.getString("order_status_name"));
    							orders.put("startdate", " ");
    							orders.put("enddate", " ");
    							String orderDate="",reformattedOrderDate="",deliveryDate="",reformattedDeliveryDate="";
    							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
    							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
    							orderDate = resultSet.getString("order_date");
    							if(resultSet.getString("delivery_date")!=null){
    								deliveryDate = resultSet.getString("delivery_date");
    							}else{
    								deliveryDate = orderDate;
    							}
    							try {
    								reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
    								reformattedDeliveryDate = myFormat.format(fromUser.parse(deliveryDate));
    							} catch (Exception e) {
    								// TODO: handle exception
    								e.printStackTrace();
    							}
    							orders.put("orderdate", reformattedOrderDate);
    							orders.put("deliverydate", reformattedDeliveryDate);
    							
    							orders.put("itemdetails", getitemdetails(orders.getString("orderno")));
    							
    							
    							orderTrackArray.put(orders);
    						
    						}
    					}  catch (Exception e) {
    						e.printStackTrace();
    					} finally{
    						if(connection!=null){
    							connection.close();
    						}
    					}	
        			}
        		/******SQL BLOCK ENDS HERE*************/
    		} catch (Exception e) {
    		
    		}
    		/*System.out.println("Total numbers of Orders for guest user is::->"+orderTrackArray.length());
    		
        	orderTrack.put("ordertrack", orderTrackArray);
        	return orderTrack;*/
    /*	}*/
    	System.out.println("Total numbers of Orders for guest user is::->"+orderTrackArray.length());
		
    	orderTrack.put("ordertrack", orderTrackArray);
    	return orderTrack;
    	/****For Both **********************************************************//*
    	else{
    		System.out.println("Logged in users order track code...");
    		
    		try {
    			Connection connection = DBConnection.createConnection();
    			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select * from vw_all_orders where user_mail_id = ? AND contact_number = ?" ;
    				String sql = "select * from vw_all_orders where user_mail_id = ? OR contact_number = ?" ;
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, userMailId);
						preparedStatement.setString(2, mobileno);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject orders = new JSONObject();
							//orders.put("city", resultSet.getString("city"));
							//orders.put("flatno", resultSet.getString("flat_no"));
							//orders.put("streetname", resultSet.getString("street_name"));
							//orders.put("landmark", resultSet.getString("landmark"));
							
							orders.put("pincode", resultSet.getString("pincode"));
							
							orders.put("orderid", resultSet.getInt("order_id"));
							orders.put("orderno", resultSet.getString("order_no"));
							if( resultSet.getString("meal_type")!=null){
								orders.put("mealtype", resultSet.getString("meal_type"));
							}else{
								orders.put("mealtype", " ");
							}
							if( resultSet.getString("time_slot")!=null){
								orders.put("timeslot", resultSet.getString("time_slot"));
							}else{
								orders.put("timeslot", " ");
							}
							String startdate="",enddate="",orderDate = "",reformattedStartDate = "",reformattedEndDate = "",reformattedOrderDate = "";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							if(resultSet.getString("start_date")==null){
								startdate = null;
							}else{
								startdate =  resultSet.getString("start_date");
							}
							if(resultSet.getString("end_date")==null){
								enddate = null;
							}else{
								enddate =  resultSet.getString("end_date");
							}
							
							orderDate = resultSet.getString("order_date");
							try {
								reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							orders.put("orderdate", reformattedOrderDate);
							
							
							if(startdate!=null && enddate!=null){
								try {
								     reformattedStartDate = myFormat.format(fromUser.parse(startdate));
								    reformattedEndDate= myFormat.format(fromUser.parse(enddate));
								} catch (ParseException e) {
								    e.printStackTrace();
								}
							}
							
							if(startdate!=null){
								orders.put("startdate", reformattedStartDate);
							}else{
								orders.put("startdate", " ");
							}
							if(enddate!=null){
								orders.put("enddate", reformattedEndDate);
							}else{
								orders.put("enddate", " ");
							}
							//orders.put("orderdate", resultSet.getString("order_date"));
							if( resultSet.getString("delivery_zone")!=null){
								orders.put("deliveryzone", resultSet.getString("delivery_zone"));
							}else{
								orders.put("deliveryzone", " ");
							}
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							if( resultSet.getString("instruction")!=null){
								orders.put("instruction", resultSet.getString("instruction"));
							}else{
								orders.put("instruction", " ");
							}
							if( resultSet.getString("order_status_name")!= null){
								orders.put("orderstatus", resultSet.getString("order_status_name"));
							}else{
								orders.put("orderstatus", " " );
							}
							if( orders.getString("orderno").startsWith("REG") ){
								orders.put("itemdetails", getitemdetails(orders.getString("orderno")));
							}else{
								orders.put("itemdetails", getMealTypeDetails(orders.getString("orderno")));
							}
							
							orderTrackArray.put(orders);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
    			}
			} catch (Exception e) {
				// TODO: handle exception
			}
    		
    		System.out.println("Total numbers of Orders for logged in user is::->"+orderTrackArray.length());
    		orderTrack.put("ordertrack", orderTrackArray);
        	return orderTrack;
    	}*/
    	
    }
    
    /**
     * Array of ordered items used for order tracking and order history
     * @param orderid
     * @return
     */
  /*  public static JSONArray getitemdetails(Integer orderid){*/
    public static JSONArray getitemdetails(String orderNo){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				connection = DBConnection.createConnection();
				/*String cuisineSql="select cuisine_id, category_id , qty, total_price " 
						  +" from fapp_order_item_details "
						  +" where order_id = ?";*/
				/*String cuisineSql="select cuisine_id, category_id , qty, total_price " 
								  +" from fapp_order_item_details "
								  +" where order_id = "
								  + "(SELECT order_id FROM fapp_orders WHERE order_no = ?)";
				String cuisineSql="select cuisine_id, category_id , qty, category_price, item_code,"
						  +" (select item_name from food_items where item_code = foid.item_code) AS item_name, "
						  +" (select item_description from food_items where item_code = foid.item_code)AS item_description " 
						  +" from fapp_order_item_details "
						  +" where order_id = "
						  +" (SELECT order_id FROM fapp_orders WHERE order_no = ?)";*/
				String cuisineSql = "select * from vw_order_item_details_list where order_no = ?";
				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						/*preparedStatement.setInt(1, orderid);*/
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("day", " ");
							itemobject.put("type", " ");
							itemobject.put("timeslot", " ");
							itemobject.put("cuisineid", resultSet.getInt("cuisine_id"));
							itemobject.put("cuisinename", resultSet.getString("cuisin_name"));
							itemobject.put("categoryid", resultSet.getInt("category_id"));
							itemobject.put("categoryname", resultSet.getString("category_name"));
							itemobject.put("itemname", resultSet.getString("item_name"));
							itemobject.put("itemdescription", resultSet.getString("item_description"));
							itemobject.put("quantity", resultSet.getInt("qty"));
							//itemobject.put("price", resultSet.getDouble("total_price"));
							itemobject.put("price", resultSet.getDouble("category_price"));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}			
			}
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
    
    public static JSONArray getKitchenItemdetails(String orderNo, String kitchenName){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			SQL:{
				connection = DBConnection.createConnection();
				String cuisineSql="select * " 
						  +" from vw_order_item_details_list "
						  +" where order_no = ? and kitchen_name = ?";
						 
				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							if(resultSet.getString("item_name")!=null){
								itemobject.put("itemname", resultSet.getString("item_name"));
							}else{
								itemobject.put("itemname", "");
							}
							itemobject.put("cuisinename", resultSet.getString("cuisin_name"));
							itemobject.put("categoryname", resultSet.getString("category_name"));
							itemobject.put("quantity", String.valueOf(resultSet.getInt("qty")));
							itemobject.put("price", String.valueOf(resultSet.getDouble("total_price")) );
							//itemobject.put("price", resultSet.getDouble("category_price"));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
				
			}
		
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
    
    private static JSONArray getOrderitemdetailsWithKitchen(String orderNo, String kitchenName){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				connection = DBConnection.createConnection();
					String cuisineSql="select category_id , qty, category_price " 
						  +" from fapp_order_item_details "
						  +" where order_id = "
						  +"(SELECT order_id FROM fapp_orders WHERE order_no = ?)"
						  +" and kitchen_id = "
						  +" (SELECT kitchen_id FROM fapp_kitchen WHERE  kitchen_name = ? )";
				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("quantity", resultSet.getInt("qty"));
							itemobject.put("price", resultSet.getDouble("category_price"));
							itemobject.put("item", getItemName(resultSet.getInt("category_id")));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
				
			}
		
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
    
    private static JSONArray getOrderitemdetails(String orderNo){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				connection = DBConnection.createConnection();
					String cuisineSql="select category_id , qty, category_price " 
						  +" from fapp_order_item_details "
						  +" where order_id = "
						  + "(SELECT order_id FROM fapp_orders WHERE order_no = ?)";
				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("quantity", resultSet.getInt("qty"));
							itemobject.put("price", resultSet.getDouble("category_price"));
							itemobject.put("item", getItemName(resultSet.getInt("category_id")));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
				
			}
		
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
    
    /**
     * WEB SERVICE FOR UPDATE SUBSCRIPTION
     * @param orderItemList
     * @param subscriptionOrder
     * @return
     * @throws JSONException
     */
    public static JSONObject updateSubscription( ArrayList<OrderItems> orderItemList, String subscriptionOrder) throws JSONException{
    	JSONObject updatedObject = new JSONObject();
    	boolean updated= false, deleted = false, mealTypesInserted=false, itemDetailsInserted=false;
    	double totPrice = 0;
    	SubscriptionBean subscriptionBean = new SubscriptionBean();
    	subscriptionBean.subscriptionId = getSubcriptionId(subscriptionOrder);
    	subscriptionBean.subscriptionNo = subscriptionOrder;
    	
    	List<Integer> dealingKitchenIds = new ArrayList<Integer>();
    	ArrayList<KitchenDetailsBean> kitchenDetailsBeanList =  getKitchenDetails(getSubcriptionAreaId(subscriptionOrder));
    	
    	int listSize = kitchenDetailsBeanList.size();
    	for(int i=0; i< orderItemList.size() ; i++){
    		for(int j=0 ; j< listSize ;j++){
    			if(orderItemList.get(i).cuisineId.equals(kitchenDetailsBeanList.get(j).getCuisineId()) 
    			 && orderItemList.get(i).categoryId.equals(kitchenDetailsBeanList.get(j).getCategoryId())){
    				System.out.println("Selected kitchen id->"+kitchenDetailsBeanList.get(j).getKitchenId() );
    				dealingKitchenIds.add(kitchenDetailsBeanList.get(j).getKitchenId());
    			}
    		}
    	}
    	
    	System.out.println("Dealing kitchens-"+dealingKitchenIds);
    	
		for(int i=0;i<orderItemList.size();i++){
			totPrice += orderItemList.get(i).price;
		}
    	
    	
    	ArrayList<Integer> idlist = getSubscriptionMealIdList(subscriptionOrder);
    	try {
    			Connection connection = DBConnection.createConnection();
			SQLDELETEITEMS:{
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals_details SET is_delete = 'Y' WHERE subscription_meal_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						for(int i=0;i<idlist.size();i++){
							preparedStatement.setInt(1, idlist.get(i));
							preparedStatement.addBatch();
						}
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   deleted = true;
				    	   }
						  
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
    		}
    			if(deleted){
    				System.out.println("1.Updation complete in  fapp_subscription_meals_details !!!");	
    			}
    		
    		SQLDELETEMEALTYPE:{
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals SET is_delete='Y' WHERE subscription_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, subscriptionOrder);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
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
    			if(updated){
    				System.out.println("2.Updation complete in fapp_subscription_meals with sub no- "+subscriptionOrder+"!!!");	
    			}
    		
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	
    	ArrayList<Integer> mealTypeIdList = saveMealWithDay(subscriptionBean, orderItemList);
    	if(mealTypeIdList.size()>0){
    		mealTypesInserted = true;
    	}
    	
    	itemDetailsInserted = saveMealItems(mealTypeIdList, orderItemList,dealingKitchenIds,subscriptionBean.subscriptionId);
    	
    	if(updated && deleted && mealTypesInserted && itemDetailsInserted){
    		updatedObject.put("success", true);
    	}else{
    		updatedObject.put("success", false);
    	}
    	System.out.println("Edit subscription JSON response from web service is--->"+updatedObject);
    	return updatedObject;
    }
    
    private static ArrayList<Integer> getSubscriptionMealIdList(String subscriptionNumber){
    	ArrayList<Integer> subMealIdList = new ArrayList<Integer>();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT subscription_meal_id from fapp_subscription_meals where subscription_no = ? AND is_delete='N'";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, subscriptionNumber);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							subMealIdList.add(resultSet.getInt("subscription_meal_id"));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return subMealIdList;
    }
    
    
    /**
     * LOAD PACKS WEBSERVICE
     * @return JSON OBJECT
     * @throws JSONException 
     */
    public static JSONObject loadsubscriptionPacks() throws JSONException{
    	JSONObject packJsonObject = new JSONObject();
    	JSONArray packArray = new JSONArray();
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				try {
						preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.packQuery);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject pack =  new JSONObject();
							pack.put("packId", resultSet.getInt("pack_type_id"));
							pack.put("packName", resultSet.getString("pack_type"));
							pack.put("packPrice", resultSet.getDouble("pack_price"));
							pack.put("packType", resultSet.getString("flavour_type"));
							int subscriptioPackId = resultSet.getInt("subscription_pack_id");
							pack.put("packDetails", getPackDetails(subscriptioPackId, connection));
							
							packArray.put(pack);
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
    	if(packArray.length() > 0)
    		packJsonObject.put("packs", packArray);
    	
    	System.out.println("* * * * * * * subscriptionPacks web service ended! * * * * * * * ");
    	return packJsonObject;
    }
    
    /**
     * HELPING WEB SERVICE FOR PACK DETAILS
     * @param subscriptionPackId
     * @param connection
     * @return
     */
    public static JSONArray getPackDetails(int subscriptionPackId, Connection connection){
    	JSONArray packDetailsArray = new JSONArray();
    	try {
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				try {
						preparedStatement = connection.prepareStatement(SubscriptionPrePackQuery.packDetailsQuery);
						preparedStatement.setInt(1, subscriptionPackId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject details = new JSONObject();
							details.put("itemName", resultSet.getString("item_name"));
							details.put("itemQuantity", resultSet.getInt("item_qty"));
							details.put("itemCode", resultSet.getString("item_code"));
							details.put("itemPrice", resultSet.getDouble("item_price"));
							details.put("categoryId", resultSet.getString("category_id"));
							details.put("categoryName", resultSet.getString("category_name"));
							details.put("cuisineId", resultSet.getString("cuisin_id"));
							details.put("cuisineName", resultSet.getString("cuisin_name"));
							details.put("itemDescription", resultSet.getString("item_description"));
							String tempImage  = resultSet.getString("item_image");
							String itemImage;
							if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
								itemImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}else if(tempImage.startsWith("http://")){
								itemImage = tempImage.replace("http://", "");
							}else{
								itemImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
							}
							
							details.put("itemImage", itemImage);
							details.put("mealType", resultSet.getString("meal_type"));
							details.put("dayId", resultSet.getInt("day_id"));
							details.put("dayName", resultSet.getString("day"));
							
							packDetailsArray.put(details);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return packDetailsArray;
    }
    
    
    
    /**
     * WEB SERVICE FOR SUBSCRIPTION DETAILS
     * @param mobileno
     * @return
     * @throws JSONException
     */ 
    public static JSONObject subscriptionDetails(String mobileno) throws JSONException{
    	JSONArray orderTrackArray = new JSONArray();
    	JSONObject orderTrack = new JSONObject();
    	try {
    		Connection connection = DBConnection.createConnection();
			/******SQL BLOCK FROM HERE*************/
    		SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				
    				String sql = "SELECT * FROM fapp_subscription WHERE contact_number = ?";
			    				
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileno);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject orders = new JSONObject();
							//orders.put("flatno", resultSet.getString("flat_no"));
							//orders.put("streetname", resultSet.getString("street_name"));
							orders.put("pincode", resultSet.getString("pincode"));
							//orders.put("landmark", resultSet.getString("landmark"));
							orders.put("orderno", resultSet.getString("subscription_no"));
							orders.put("orderid", resultSet.getString("subscription_id"));
							orders.put("mealtype", " ");
							/*orders.put("timeslot", resultSet.getString("time_slot"));*/
							orders.put("timeslot", " ");
							orders.put("orderstatus", " ");
							orders.put("totalprice", resultSet.getDouble("price"));
							String startdate =  resultSet.getString("start_date");
							String enddate =  resultSet.getString("end_date");
							String orderDate = resultSet.getString("subscription_date");
							String reformattedStartDate = "",reformattedEndDate = "", reformattedOrderDate="";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							try {
							     reformattedStartDate = myFormat.format(fromUser.parse(startdate));
							    reformattedEndDate= myFormat.format(fromUser.parse(enddate));
							    reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
							} catch (ParseException e) {
							    e.printStackTrace();
							}
							orders.put("startdate", reformattedStartDate);
							orders.put("enddate", reformattedEndDate);
							orders.put("orderdate", reformattedOrderDate);
							/*orders.put("startdate", resultSet.getDate("start_date"));
							orders.put("enddate", resultSet.getDate("end_date"));*/
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							
							//Integer areaId = resultSet.getInt("area_id");
							//orders.put("city", getCityName(areaId));
							//orders.put("location", getAreaName(areaId));
							orders.put("itemdetails", getMealTypeDetails(orders.getString("orderno")) );
							
				
							
							ArrayList<String> trackOrderDetails = fetchDeliveryBoyCurrentAddress(Integer.valueOf(orders.getString("orderid")));
							if(trackOrderDetails.size()>0){
								orders.put("boyname", trackOrderDetails.get(0));
								orders.put("boymobileno", trackOrderDetails.get(1));
								orders.put("currentaddress", trackOrderDetails.get(2));
							}else{
								orders.put("boyname", " ");
								orders.put("boymobileno", " ");
								orders.put("currentaddress"," ");
							}
							
							orderTrackArray.put(orders);
						}
					}  catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    			}
    		/******SQL BLOCK ENDS HERE*************/
		} catch (Exception e) {
		
		}
    	/*if(orderTrackArray.length()!=0){
    		orderTrack.put("ordertrack", orderTrackArray);
    	}else{*/
    		orderTrack.put("ordertrack", orderTrackArray);
    	//}
    		if(orderTrackArray.length()!=0){
    			System.out.println("Subscription list available");
    		}
    	
    	return orderTrack;
    	
    }
    
    
    private static ArrayList<String> fetchDeliveryBoyCurrentAddress(Integer subId){
    	ArrayList<String> details = new ArrayList<String>();
    	try {
    			SQLAddress:{
    						Connection connection = DBConnection.createConnection();
    						PreparedStatement preparedStatement = null;
    						ResultSet resultSet = null;
    						String sql = "SELECT * from vw_track_subscription_details where subscription_id=?";
    						try {
								preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setInt(1, subId);
								resultSet = preparedStatement.executeQuery();
								while (resultSet.next()) {
									details.add(resultSet.getString("delivery_boy_name"));
									details.add(resultSet.getString("delivery_boy_phn_number"));
									details.add(resultSet.getString("delivery_boy_track_address"));
									details.add(resultSet.getString("delivery_boy_vehicle_reg_no"));
								}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}finally{
								if(connection!=null){
									connection.close();
								}
							}
    			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("track data ->"+details.size());
		return details;
    	
    }
   
    
    private static String getCityName(Integer areaId){
    	String city="";
    	try {
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				Connection connection = DBConnection.createConnection();
    				String sql = "select city_name from vw_area_data where area_id = ? ";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, areaId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							city=resultSet.getString("city_name");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return city;
    }
    
    private static String getAreaName(Integer areaId){
    	String area="";
    	try {
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				Connection connection = DBConnection.createConnection();
    				String sql = "select area_name from vw_area_data where area_id = ? ";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, areaId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							area=resultSet.getString("area_name");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return area;
    }
    
    /*private static JSONArray getMealTypeDetails(Integer subscriptionId){*/
    private static JSONArray getMealTypeDetails(String subscriptionNo){
    	JSONArray itemsDetailArray = new JSONArray();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				/*String sql = "SELECT * FROM vw_subscribed_orders "
				    			+" where subscription_id=?"
				    			+" and quantity <> 0";*/
    				String sql = "SELECT * FROM vw_subscribed_orders_app "
			    			+" where subscription_no = ?"
			    			+" and quantity <> 0";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						/*preparedStatement.setInt(1, subscriptionId);*/
						preparedStatement.setString(1, subscriptionNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("day", resultSet.getString("day_name"));
							itemobject.put("type", resultSet.getString("meal_type"));
							if(resultSet.getString("time_slot")!=null){
								itemobject.put("timeslot", resultSet.getString("time_slot"));
							}else{
								itemobject.put("timeslot", " ");
							}
							itemobject.put("cuisinename", resultSet.getString("cuisine_name"));
							itemobject.put("cuisineid", resultSet.getInt("cuisine_id"));
							itemobject.put("categoryname", resultSet.getString("category_name"));
							itemobject.put("categoryid", resultSet.getInt("category_id"));
							itemobject.put("quantity", resultSet.getInt("quantity"));
							/*Integer qty = resultSet.getInt("quantity");
							Double price = resultSet.getDouble("meal_price");
							itemobject.put("price", (qty * price) );*/
							itemobject.put("price", resultSet.getDouble("meal_price"));
							if(resultSet.getString("status")!=null){
								itemobject.put("status", resultSet.getString("status"));
							}else{
								itemobject.put("status", " ");
							}
							itemsDetailArray.put(itemobject);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return itemsDetailArray;	
    }

    
    public static JSONArray getSubscribeditemdetails(Integer subscriptionId){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				connection = DBConnection.createConnection();
				String cuisineSql="select cuisine_id, category_id , qty, total_price " 
								  +" from fapp_order_item_details "
								  +" where subscription_id = ?";

				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						preparedStatement.setInt(1, subscriptionId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("cuisineid", resultSet.getInt("cuisine_id"));
							itemobject.put("cuisinename", getCuisineName(itemobject.getInt("cuisineid")));
							itemobject.put("categoryid", resultSet.getInt("category_id"));
							itemobject.put("categoryname", getCategoryName(itemobject.getInt("categoryid")));
							itemobject.put("quantity", resultSet.getInt("qty"));
							itemobject.put("price", resultSet.getDouble("total_price"));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
				
			}
		
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
    /**
     * Returns a cuisine name with respect to cuisine id in order tracking and order history
     * @param cuisineId
     * @return
     */
    public static String getCuisineName(Integer cuisineId){
    	//JSONObject cuisineName = new JSONObject();
    	Connection connection  = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	String cuisineName ="";
    	try {
			SQL:{
    			connection = DBConnection.createConnection();
    			String sql = "SELECT cuisin_name FROM fapp_cuisins WHERE cuisin_id=?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, cuisineId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							cuisineName= resultSet.getString("cuisin_name");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
		} catch (Exception e) {
		
		}
    	
    	return cuisineName;
    }
    
    /**
     * Returns category name with resoect to category id used for order tracking and order history
     * @param categoryId
     * @return
     */
    public static String getCategoryName(Integer categoryId){
    	//JSONObject categoryName = new JSONObject();
    	Connection connection  = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	String categoryName ="";
    	try {
			SQL:{
    			connection = DBConnection.createConnection();
    			String sql = "SELECT category_name FROM food_category WHERE category_id=?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, categoryId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							categoryName= resultSet.getString("category_name");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
		} catch (Exception e) {
			
		}
    	
    	return categoryName;
    }
    
    public static JSONObject getItemName(Integer categoryId){
    	//JSONObject categoryName = new JSONObject();
    	Connection connection  = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	JSONObject itemName =new JSONObject();
    	try {
			SQL:{
    			connection = DBConnection.createConnection();
    			String sql = "SELECT category_name FROM food_category WHERE category_id=?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, categoryId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							itemName.put("name", resultSet.getString("category_name"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
		} catch (Exception e) {
			
		}
    	
    	return itemName;
    }
    /**
     * A WEB SERVICE for check the user feedback
     * @param userMailId
     * @return
     * @throws JSONException 
     */
    public static JSONObject checkfeedback(String userMailId) throws JSONException{
    	JSONObject feedbackobject = new JSONObject();
    	Boolean feedBacked =  false;
    	Integer count = 0;
    	if(isOrderExistForUser(userMailId)){
    		if(isOrderCompletedForUser(userMailId)){
    			try {
            		Connection connection = DBConnection.createConnection();
            		/***SQL BLOCK STARTS HERE***/
        			SQL:{
            			  PreparedStatement preparedStatement = null;
            			  ResultSet resultSet = null;
            			 // String sql = "SELECT overeall_rating FROM fapp_order_feedback WHERE user_mail_id=? ";
            			  String sql = "select overeall_rating from fapp_order_feedback fof " 
	    							  +" JOIN fapp_orders fo "
	    							  +" ON fo.order_id = fof.order_id " 
									  +" where "
									  +" fof.overeall_rating IS NULL "  
									  +" AND fof.user_mail_id = ? "
									  +" AND fo.order_status_id = 7";
            			  try {
        					   preparedStatement = connection.prepareStatement(sql);
        					   preparedStatement.setString(1, userMailId);
        					   resultSet = preparedStatement.executeQuery();
        					   while(resultSet.next()){
        						  String overeall_rating = resultSet.getString("overeall_rating");
        						  
        						   if( overeall_rating==null ){
        							  count ++;
        						   }
        					   }
        					   if(count > 0 ){
        						   feedBacked = false;
        					   }else{
        						   feedBacked = true;
        					   }
        					   feedbackobject.put("status", feedBacked);
        					 
        				} catch (Exception e) {
        					e.printStackTrace();
        				} finally{
        					if(connection!=null){
        						connection.close();
        					}
        				}	
            		}
        		} catch (Exception e) {
        			System.out.println("ERROR DUE TO:"+e.getMessage());
        		}
    		}else{
       		 	feedbackobject.put("status", true );
        	}
    		
    	}else{
    		 feedbackobject.put("status", true );
    	}
    	
    	 System.out.println("Checkfeedback JSONObject Response-"+feedbackobject);
    	return feedbackobject;
    }
    /**
     * Check whether any order is given by user or not
     * @param usermailId
     * @return
     */
    public static Boolean isOrderExistForUser(String usermailId){
    	Boolean orderExists =  false;
    	ArrayList<String> emailIdList = new ArrayList<String>();
    	int countEmail=0;
    	try {
    		 Connection connection =  DBConnection.createConnection();
    		    		 
			SQL:{
	    			 PreparedStatement preparedStatement = null;
	        		 ResultSet resultSet = null;
	        		// String sql = "SELECT user_mail_id from fapp_orders";
	        		 String sql = "SELECT count(*)AS user_mail_id from fapp_orders where  user_mail_id = ?";
	        		 try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, usermailId);
						resultSet = preparedStatement.executeQuery();
						/*while (resultSet.next()) {
							emailIdList.add(resultSet.getString("user_mail_id")) ;
						}*/
						if(resultSet.next()){
							countEmail = resultSet.getInt("user_mail_id");
						}
					} catch (Exception e) {
						System.out.println("ERROR DUE TO:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    			 
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	/*if(emailIdList.contains(usermailId)){
    		orderExists = true;
    	}else{
    		orderExists = false;
    	}*/
    	if(countEmail>0){
    		orderExists = true;
    	}else{
    		orderExists = false;
    	}
    	System.out.println("Total no of email id for given mail id "+usermailId+" is -- >"+countEmail+" and order exists status is==>"+orderExists);
    	return orderExists;
    }
    
    public static Boolean isOrderCompletedForUser(String usermailId){
    	Boolean orderCompleted =  false;
    	ArrayList<Integer> orderStatusList = new ArrayList<Integer>();
    	try {
    		 Connection connection =  DBConnection.createConnection();
    		    		 
			SQL:{
	    			 PreparedStatement preparedStatement = null;
	        		 ResultSet resultSet = null;
	        		// String sql = "SELECT order_status_id from fapp_orders where user_mail_id=?";
	        		 String sql = "SELECT order_status_id from fapp_orders where user_mail_id=? and order_status_id = 7";
	        		 try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, usermailId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							orderStatusList.add(resultSet.getInt("order_status_id")) ;
						}
						
					} catch (Exception e) {
						System.out.println("ERROR DUE TO:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    			 
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	/*if(orderStatusList.contains(7)){
    		System.out.println("inside if");
    		orderCompleted = true;
    	}else{
    		System.out.println("inside else");
    		orderCompleted = false;	
    	}*/
    	if(orderStatusList.size()>0){
    		orderCompleted = true;
    	}else{
    		orderCompleted = false;
    	}
    	return orderCompleted;
    }
    
    /**
     * A WEB SERVICE for submitting user feed back 
     * @param foodQuality
     * @param deliveryQuality
     * @param overallRating
     * @param userMailId
     * @return
     * @throws JSONException 
     */
    public static JSONObject submitfeedback( String taste, String portion, String hotness,
    		String packing, String timelydelivered,String overallRating, String foodcomment, String userMailId) throws JSONException{
		JSONObject submitfeedbackObject = new JSONObject();
		Boolean submitsuccess = false;
		
			try {
	    		Connection connection = DBConnection.createConnection();
	    		/***SQL BLOCK STARTS HERE***/
				SQL:{
	    			  PreparedStatement preparedStatement = null;
	    			  /*String sql = "UPDATE fapp_order_feedback SET overeall_rating=?, "
	    			  			  +" warm_food=?,on_time=?,behaviour=?,comment=?"
	    					      +" WHERE user_mail_id = ? and order_id=?";*/
	    			  
	    			  String sql ="UPDATE fapp_order_feedback "
								  +" SET overeall_rating=?, "
								  +" taste=?, timely_delivered=?,  "
								  +" hotness=?,comment=?," 
								  +" portion =?, packing=? "
								  +" WHERE user_mail_id=? AND order_id=?;";
	    			  try {
						   preparedStatement = connection.prepareStatement(sql);
						   for(Integer orderid : getUnfeedBackedOrderIds(userMailId)){
							   preparedStatement.setString(1, overallRating);
							   if(taste.equals("true")){
								   preparedStatement.setString(2,"NOT OK");
							   }else{
								   preparedStatement.setString(2, "OK");
							   }
							   if(timelydelivered.equals("true")){
								   preparedStatement.setString(3,"NOT OK");
							   }else{
								   preparedStatement.setString(3, "OK");
							   }
							  if(hotness.equals("true")){
								   preparedStatement.setString(4,"NOT OK");
							   }else{
								   preparedStatement.setString(4, "OK");
							   }
							   if(foodcomment!=null){
								   preparedStatement.setString(5,foodcomment);
							   }else{
								   preparedStatement.setString(5, " ");
							   }
							  if(portion.equals("true")){
								   preparedStatement.setString(6,"NOT OK");
							   }else{
								   preparedStatement.setString(6, "OK");
							   }
							  if(packing.equals("true")){
								   preparedStatement.setString(7,"NOT OK");
							   }else{
								   preparedStatement.setString(7, "OK");
							   }
							  preparedStatement.setString(8, userMailId);
							  preparedStatement.setInt(9, orderid);
							  preparedStatement.addBatch();
						   }  
						int [] count = preparedStatement.executeBatch();
				    	   for(Integer integer : count){
				    		   System.out.println("feedback submitted_");
				    		   submitsuccess = true;
				    	   }
				    	   submitfeedbackObject.put("status", submitsuccess);
						  /*if(count>0){
							  System.out.println("feedback submitted!");
							  submitfeedbackObject.put("status", true);
						  }*/  
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
	    		}
	    		/***SQL BLOCK ENDS HERE***/
			} catch (Exception e) {
			}
		return submitfeedbackObject;	
    }
    
    public static List<Integer> getUnfeedBackedOrderIds(String mailId){
    	List<Integer> orderIdList = new ArrayList<Integer>();
    	try {
			SQL:{
    				PreparedStatement preparedStatement = null;
    				Connection connection = DBConnection.createConnection();
    				ResultSet resultSet = null;
    				/*String sql =" select order_id from  fapp_order_feedback where delivery_quality IS NULL AND food_quality IS NULL "
	    					    +" AND overeall_rating IS NULL AND user_mail_id = ? ";*/
    				String sql = "select distinct fof.order_id from fapp_order_feedback fof "
    							+ " JOIN fapp_orders fo"
    							+ " ON fo.user_mail_id = fof.user_mail_id "
								+ " where "
								+ " fof.taste IS NULL AND fof.portion IS NULL AND fof.packing IS NULL AND"
								+ " fof.timely_delivered IS NULL AND fof.menu IS NULL" 
								+ " AND fof.user_mail_id =? "
								+ " AND fo.order_status_id = 7";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mailId);
						System.out.println(preparedStatement);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							orderIdList.add(resultSet.getInt("order_id"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		   }
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("Unfeedback order id size - ->"+orderIdList.size());
    	return orderIdList;
    }
    
    
    
    /**
     * A WEB SERVICE for placing orders
     * @param userMailId
     * @param contactName
     * @param contactNumber
     * @param cityName
     * @param flatNumber
     * @param streetName
     * @param pincode
     * @param landmark
     * @return
     * @throws Exception 
     */
    /*public static JSONObject placeOrder(String userMailId, String contactName ,String contactNumber , 
    		String cityName, String location, String flatNumber , String streetName ,String pincode , 
    		String landmark, Integer locationId,String mealType, String timeSlot, ArrayList<OrderItems> orderItemList,
    		String deliveryZone,String deliveryAddress,String instruction) throws JSONException{*/
    public static JSONObject placeOrder(String userType, String userMailId,  String contactNumber, String guestName,
    		String cityName, String location, String pincode , 
    		Integer locationId,String mealType, String timeSlot, ArrayList<OrderItems> orderItemList,
    		String deliveryZone,String deliveryAddress,String instruction,String deliveryDay,
    		String payAmount,boolean credit, String payType, int totalNoOfQuantity , MealTypePojo mealTypePojo, 
    		ArrayList<TimeSlot> timeSlotList,Set<Integer> servingKitchenIds, String promoCode ) throws Exception{
    	
    	boolean isGuestUser = false,isUserSameOrder = false ,sameCuisineSplit = false,
    			userDetailsInserted = false, itemDetailsInserted = false,
    			kitchenAssigned = false, onlyBengCuisine = false, onlyNiCuisine = false, bengNiCuisine = false,isPromoCodeReusable = false;
    	
    	java.util.Date delivery_Date = new java.util.Date();
    	int orderId = 0,totalBenQty =0, totalNiQty = 0;
    	
    	ArrayList<Integer> dealingKitchenIds = new ArrayList<Integer>();
    	ArrayList<OrderItems> niCuisineIdList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> bengCuisineIdList = new ArrayList<OrderItems>();
		ArrayList<OrderItems> sameKitchenOrderList = new ArrayList<OrderItems>();
    	
		JSONObject isOrderPlaced = new JSONObject();
    	String orderBy = null ;
    	
    	if(PromoCodeDAO.isReusablePromoCode(promoCode)){
    		isPromoCodeReusable = true;
    	}
    	
    	if(deliveryDay!=null){
    		 delivery_Date = getDeliveryDate(deliveryDay);
    	}	
    	
    	if(userType.equals("1")){
    		System.out.println("* * * * Guest User * * * ");
    		isGuestUser = true;
    	}else{
    		System.out.println("* * * * Registered user * * * ");
    		isGuestUser = false;
    	}
    	
    	if(isGuestUser){
    		orderBy = guestName;
    		System.out.println("Order by guest->"+orderBy);	
    	}else{
    		orderBy = getUserName(contactNumber);
    		System.out.println("Order by registered->"+orderBy);
    		userMailId = getUserMailId(contactNumber);
    		System.out.println("Mail id of registered user->"+userMailId);
    	}
    	
    	Double finalPrice = Double.valueOf(payAmount);
		System.out.println("payAmount :: "+payAmount+" finalPrice :: "+finalPrice);
    	
		
		System.out.println("Total item ordered: "+orderItemList.size());
		
		
		for(int i=0;i<orderItemList.size();i++){
			if(orderItemList.get(i).cuisineId==2){
				totalNiQty += orderItemList.get(i).quantity;
				niCuisineIdList.add(new OrderItems(orderItemList.get(i).cuisineId, orderItemList.get(i).categoryId, 
						orderItemList.get(i).itemCode,orderItemList.get(i).quantity, orderItemList.get(i).price) );
			}
			if(orderItemList.get(i).cuisineId==1){
				totalBenQty += orderItemList.get(i).quantity;
				bengCuisineIdList.add(new OrderItems(orderItemList.get(i).cuisineId, orderItemList.get(i).categoryId, 
						orderItemList.get(i).itemCode,orderItemList.get(i).quantity, orderItemList.get(i).price) );
			}
		}
		
		System.out.println("Total item order size: "+orderItemList.size());
		System.out.println("BEN cuisine order size: "+bengCuisineIdList.size());
		System.out.println("NI cuisine order size: "+niCuisineIdList.size());
		
		if(niCuisineIdList.size()>0 && niCuisineIdList.size()<orderItemList.size()){
			bengNiCuisine = true;
		}
		if( bengCuisineIdList.size() == orderItemList.size()){
			onlyBengCuisine = true;
		}
    	if(niCuisineIdList.size() == orderItemList.size()){
    		onlyNiCuisine = true;
    	}
    	
    	if(onlyBengCuisine){
    		System.out.println("** Order contains only bengali cuisine **");
    		int countKitchen = 0 ;
    		for(Integer kid : servingKitchenIds){
    			if(TimeSlotFinder.findKitchenType(kid)==1){
    				countKitchen++;
    			}
    		}
    		if(countKitchen == servingKitchenIds.size() && servingKitchenIds.size()>1){
    			sameCuisineSplit = true;
    		}
    		System.out.println("SAME CUISINE SPILT::::::::"+sameCuisineSplit);
    	}
    	if(onlyNiCuisine){
    		System.out.println("** Order contains only ni cuisine **");
    	}
    	if(bengNiCuisine){
    		System.out.println("** Order contains  bengali and ni cuisine **");
    	}
    	

    	if(totalNoOfQuantity > 1){//kitchen biker and slot already known
    		System.out.println("* * * * More than 1 quantity order * * *");
    		Map<Integer, Integer> cuisineKitchenMap = new HashMap<Integer, Integer>();//cuisine kitchen map
    		ArrayList<Integer> nikitchenids = new ArrayList<Integer>();//ni kitchens
    		ArrayList<Integer> bengkitchenids = new ArrayList<Integer>();// beng kitchens

    		

    		for(TimeSlot slot: timeSlotList){
    			int cuisine = CuisineKitchenDAO.kitchenCuisine(slot.kitchenID);//find kitchen type beng or ni
    			cuisineKitchenMap.put(cuisine, slot.kitchenID);//put cuisine and kitchen in a map
    		}
    		for(Entry<Integer, Integer> entry: cuisineKitchenMap.entrySet()){
    			if(entry.getKey()==1){//if map contains cuisine ==1 bengali
    				bengkitchenids.add(entry.getValue());//add all kitchen id to bengali kitchens
    			}
    			if(entry.getKey()==2){//if map contains cuisine ==2 ni
    				nikitchenids.add(entry.getValue());//add all kitchen id to ni kitchens
    			}
    		}
    		if(onlyBengCuisine){//when only bengali cuisine orders

    			if(sameCuisineSplit){
    					for(OrderItems items : orderItemList){
    						
    						for(TimeSlot kitchenSlot : timeSlotList){
    							
    							if(items.itemCode.equalsIgnoreCase(kitchenSlot.itemCode)){
    								OrderItems kio = new OrderItems();
        							kio.itemName = items.itemName;
        							kio.itemDescription = items.itemDescription;
        							kio.categoryId = items.categoryId;
        							kio.cuisineId = items.cuisineId;
        							kio.cuisinName = items.cuisinName;
        							kio.itemCode = items.itemCode;
        							kio.quantity = kitchenSlot.quantity;
        							kio.price = items.price;
        							kio.kitchenId = kitchenSlot.kitchenID;
        							kio.packing = items.packing;
        							kio.mealType = items.mealType;
        							sameKitchenOrderList.add(kio);
    							}
    						}
    					}
    				/*for(TimeSlot kitchenSlot: timeSlotList){
    					for(OrderItems items : orderItemList){
    						if(kitchenSlot.quantity>0){
    							if(items.quantity == 0){
    								continue;
    							}
    							OrderItems kio = new OrderItems();
    							kio.itemName = items.itemName;
    							kio.itemDescription = items.itemDescription;
    							kio.categoryId = items.categoryId;
    							kio.cuisineId = items.cuisineId;
    							kio.cuisinName = items.cuisinName;
    							kio.itemCode = items.itemCode;
    							kio.quantity = kitchenSlot.quantity;
    							kio.price = items.price;
    							kio.kitchenId = kitchenSlot.kitchenID;
    							kio.packing = items.packing;
    							kio.mealType = items.mealType;
    							sameKitchenOrderList.add(kio);

    						//}
    					}
    				}*/
    				if(orderItemList.size() > 0 ){
    					orderItemList.clear();
    				}
    				System.out.println("O size: "+orderItemList.size());
    				
    				orderItemList.addAll(sameKitchenOrderList);
    				System.out.println("Size : "+sameKitchenOrderList.size());
    				System.out.println(orderItemList);
    				for(OrderItems oritems : sameKitchenOrderList)
    					dealingKitchenIds.addAll(servingKitchenIds);
    			}else{

    				for(Integer kitchenid : servingKitchenIds){
    					for(OrderItems items : orderItemList){
    						/*if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenid)){
    							items.kitchenId = kitchenid;
    						}else{
    							continue;
    						}*/
    						items.kitchenId = kitchenid;
    						dealingKitchenIds.add(items.kitchenId);
    					}	
    				}
    			}
    			System.out.println("Only bengali dealing kitchen:: "+dealingKitchenIds);
    		}
    		if(onlyNiCuisine){//when only ni cuisine orders
    			for(Integer kitchenid : servingKitchenIds){
    				for(OrderItems items : orderItemList){
    					/*if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenid)){
    						items.kitchenId = kitchenid;
    					}else{
    						continue;
    					}*/
    					items.kitchenId = kitchenid;
    					dealingKitchenIds.add(items.kitchenId);
    				}
    			}
    			System.out.println("Only NI dealing kitchen:: "+dealingKitchenIds);
    		}
    		
    		if(bengNiCuisine){//when  bengali  & ni cuisine orders
    			Collections.sort(orderItemList);//sort order items from bengali to ni(1 then 2)
    			for(Integer kitchenid : servingKitchenIds){
    				for(OrderItems items : orderItemList){
    					if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenid)){
    						items.kitchenId = kitchenid;
    					}else{
    						continue;
    					}
    					dealingKitchenIds.add(items.kitchenId);
    				}

    			}
    			/*for(int i=0;i<orderItemList.size();i++){
    						dealingKitchenIds.addAll(bengkitchenids);// first add bengali
    						dealingKitchenIds.addAll(nikitchenids);// second add ni
    					}*/
    			System.out.println("Both dealing kitchen:: "+dealingKitchenIds);
    		}

    		for(int i=0 ; i < orderItemList.size() ; i++){
    			System.out.print("CUID::"+orderItemList.get(i).cuisineId+"\t");
    			System.out.print("CATID::"+orderItemList.get(i).categoryId+"\t");
    			System.out.print("ITEM::"+orderItemList.get(i).itemCode+"\t");
    			System.out.print("QTY::"+orderItemList.get(i).quantity+"\t");
    			System.out.println("KITCHEN::"+orderItemList.get(i).kitchenId+"\n");

    		}	

    	}else{
    		System.out.println("* * * * single quantity order * * *");
    		Map<Integer, Integer> cuisineKitchenMap = new HashMap<Integer, Integer>();//cuisine kitchen map
    		ArrayList<Integer> nikitchenids = new ArrayList<Integer>();//ni kitchens
    		ArrayList<Integer> bengkitchenids = new ArrayList<Integer>();// beng kitchens

    		for(TimeSlot slot: timeSlotList){
    			int cuisine = CuisineKitchenDAO.kitchenCuisine(slot.kitchenID);//find kitchen type beng or ni
    			cuisineKitchenMap.put(cuisine, slot.kitchenID);//put cuisine and kitchen in a map
    		}
    		for(Entry<Integer, Integer> entry: cuisineKitchenMap.entrySet()){
    			if(entry.getKey()==1){//if map contains cuisine ==1 bengali
    				bengkitchenids.add(entry.getValue());//add all kitchen id to bengali kitchens
    			}
    			if(entry.getKey()==2){//if map contains cuisine ==2 ni
    				nikitchenids.add(entry.getValue());//add all kitchen id to ni kitchens
    			}
    		}
    		if(onlyBengCuisine){//when only bengali cuisine orders
    			for(Integer kitchenid : servingKitchenIds){
    				for(OrderItems items : orderItemList){
    					/*if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenid)){
    						items.kitchenId = kitchenid;
    					}else{
    						continue;
    					}*/
    					items.kitchenId = kitchenid;
    					dealingKitchenIds.add(items.kitchenId);
    				}
    			}
    			/*for(int i=0;i<orderItemList.size();i++){
    				dealingKitchenIds.addAll(bengkitchenids);//add all kitchens to items for bangali
    			}*/
    		}
    		if(onlyNiCuisine){//when only ni cuisine orders
    			for(Integer kitchenid : servingKitchenIds){
    				for(OrderItems items : orderItemList){
    					/*if(RoundRobinKitchenFinder.isKitchenServingItem(items.itemCode, kitchenid)){
    						items.kitchenId = kitchenid;
    					}else{
    						continue;
    					}*/
    					items.kitchenId = kitchenid;
    					dealingKitchenIds.add(items.kitchenId);
    				}
    			}
    			/*for(int i=0;i<orderItemList.size();i++){
    				dealingKitchenIds.addAll(nikitchenids);//add all kitchens to items for ni
    			}*/
    		}
    		for(int i=0 ; i < orderItemList.size() ; i++){
    			System.out.print("CUID::"+orderItemList.get(i).cuisineId+"\t");
    			System.out.print("CATID::"+orderItemList.get(i).categoryId+"\t");
    			System.out.print("ITEM::"+orderItemList.get(i).itemCode+"\t");
    			System.out.print("QTY::"+orderItemList.get(i).quantity+"\t");
    			System.out.println("KITCHEN::"+orderItemList.get(i).kitchenId+"\n");
    		}	
    	}
    	
    	System.out.println("Order list size:: "+orderItemList.size()+" Dealing kitchdens :"+dealingKitchenIds.size());
    	/************** If kitchen found in given delivery pincode ******************/
    	if(dealingKitchenIds.size()!=0){
    		
    		try {
        		Connection connection = DBConnection.createConnection();
        		//***SQL BLOCK STARTS HERE***//*
        		SQL:{
        				PreparedStatement preparedStatement= null;
        				//connection.setAutoCommit(false);
        				/*String sql = "INSERT INTO fapp_orders(user_mail_id, order_by, contact_number,order_no,meal_type,time_slot)"
        							+" VALUES (?, ?, ?, ?,?,?)";*/
        				String sql = "INSERT INTO fapp_orders(user_mail_id , contact_number, order_no, meal_type, time_slot,order_by,"
        						+ "delivery_date,final_price,payment_name, user_type,promo_code)"
    							+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        				try {
    	    					preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
    	    					preparedStatement.setString(1, userMailId);
    	    					if(contactNumber!=null)
    	    						preparedStatement.setString(2, contactNumber);
    	    					else
    	    						preparedStatement.setString(2, " ");
    	    					/*preparedStatement.setString(2, contactName);
    	        				preparedStatement.setString(3, contactNumber);*/
    	        				preparedStatement.setString(3, generateOrderNo());
    	        				preparedStatement.setString(4, mealType);
    	        				preparedStatement.setString(5, timeSlot);
    	        				if(orderBy !=null){
    	        					preparedStatement.setString(6, orderBy.trim());
    	        				}else{
    	        					preparedStatement.setString(6, "");
    	        				}
    	        				if(delivery_Date!=null){
    	        					preparedStatement.setDate(7, new java.sql.Date(delivery_Date.getTime()) );
    	        				}else{
    	        					preparedStatement.setNull(7, Types.NULL);
    	        				}
    	        				
    	        				if(finalPrice != null){
    	        					preparedStatement.setDouble(8, finalPrice);
    	        				}else{
    	        					preparedStatement.setNull(8, Types.NULL);
    	        				}
    	        				if(payType != null){
    	        					preparedStatement.setString(9, payType);
    	        				}else{
    	        					preparedStatement.setString(9, "Cash on Delivery");
    	        				}
    	        				if(isGuestUser){
    	        					preparedStatement.setString(10, "GUEST USER");
    	        				}else{
    	        					preparedStatement.setString(10, "REGISTERED USER");
    	        				}
    	        				if(promoCode!=null && promoCode.trim().length()>0){
    	        					if(isPromoCodeReusable){
    	        						preparedStatement.setNull(11, Types.NULL);
    	        					}else{
    	        						preparedStatement.setString(11, promoCode.trim().toUpperCase());
    	        					}
    	        					
    	        				}else{
    	        					preparedStatement.setNull(11, Types.NULL);
    	        				}
    	        				//System.out.println(preparedStatement);
    	        				//preparedStatement.setDate(7, current_tim);
    	        				preparedStatement.execute();
    	        				//connection.commit();
    							ResultSet resultSet = preparedStatement.getGeneratedKeys();
    							if(resultSet.next()){
    							    orderId = resultSet.getInt(1);
    								System.out.println("Order created and Id is =====> "+orderId);
    								
    								/*userDetailsInserted = saveUserDetils(orderId, contactName, cityName, location, 
    										landmark, pincode, streetName, flatNumber, orderItemList,deliveryZone,deliveryAddress,instruction ) ;*/ 
    								userDetailsInserted = saveUserDetils(orderId, cityName, location, 
    										pincode, orderItemList,deliveryZone.trim(),deliveryAddress.trim(),instruction ) ;
    								
    								if(userDetailsInserted){
    									Set<OrderItems> orderItems = new HashSet<OrderItems>();
    									for(OrderItems items : orderItemList){
    										orderItems.add(items);
    									}
    									ArrayList<OrderItems> orderItemsWithKitchen = new ArrayList<OrderItems>(orderItems);
    									itemDetailsInserted = saveItemsWithKitchen(orderId, orderItemsWithKitchen, dealingKitchenIds);
    								}
    								if(itemDetailsInserted){
    									//kitchenAssigned = orderAssignToKitchen(orderId,  "REGULAR");//44
    									kitchenAssigned = assignOrderToKitchen(dealingKitchenIds, "REGULAR", orderId);//44
    								}
    								
    								/*userDetailsInserted = saveUserDetils(orderId, contactName, cityName, location, landmark, 
    	        							pincode, streetName, flatNumber, orderItemList);
    								if(userDetailsInserted){
    									//itemDetailsInserted = saveItemDetails(orderId, orderItemList);
    									itemDetailsInserted = saveItemsWithKitchen(orderId, orderItemList, dealingKitchenIds);
    								}
    								if(userDetailsInserted && itemDetailsInserted){
    						    		isOrderPlaced.put("success", true );		
    						    	}else{
    						    		isOrderPlaced.put("success", false );
    						    	}*/
    								
    							}
    	        				if( !(userDetailsInserted && itemDetailsInserted && kitchenAssigned) ){
    	        					
    	        					connection.rollback();
    	        				}
    					}  catch (Exception e) {
    						connection.rollback();
    						System.out.println("Error due to: "+e.getMessage());
    						e.printStackTrace();
    					} finally{
    						if(connection!=null){
    							///connection.setAutoCommit(true);
    							connection.close();
    						}
    					}	
        			}
        		/***SQL BLOCK END HERE***/
    		} catch (Exception e) {
    			
    		}
    		/*if(userDetailsInserted && itemDetailsInserted){
        		isOrderPlaced.put("success", "regular_success");
        	}
        	if(itemDetailsInserted && kitchenAssigned){
        		isOrderPlaced.put("success", "regular_success");
        	}*/
    		if(kitchenAssigned && orderId!=0){
    			//updateStock(orderItemList, dealingKitchenIds);
    			//if(totalNoOfQuantity > 1){
    			//	if(totalBenQty > 1 || totalNiQty > 1){
    					if(BookDriver.assignDriverWithKitchen(timeSlotList, orderId)){//assign driver id and current time wrt kitchen and order id
        					//BookDriver.bookDriverSlot(mealTypePojo,timeSlotList);//driver status table updation qty++ order++
        					BookDriver.saveBikersQtyWithKitchen(timeSlotList, orderId);
        					for(TimeSlot slot : timeSlotList){	
        						mealTypePojo.setBoyUSerId(slot.bikerUserId);
        						mealTypePojo.setSlotId(slot.slotId);
        						
        						BookDriver.bookDriverSlot(mealTypePojo, slot);//driver status table updation qty++ order++
        						
        						if(BookDriver.isSlotFullForQuantity(mealTypePojo, slot)){
        							BookDriver.makeSlotLockedForQuantity(mealTypePojo, slot);
        						}
        						if(BookDriver.isSlotFullForQuantity(mealTypePojo, slot)){
        							BookDriver.makeSlotLockedForQuantity(mealTypePojo, slot);
        						}
        						
        						if(BookDriver.isSlotFull(mealTypePojo, slot) ){//check for slot is full qty<9 && order==2
            						BookDriver.makeSlotLocked(mealTypePojo,slot);//make slot full as inactive
            						//BookDriver.makeSlotLockedForOrders(mealTypePojo, slot);
            						//BookDriver.makeSlotLockedForQuantity(mealTypePojo, slot);
            					}
        					}
        					/*if(BookDriver.isSlotFull(mealTypePojo,timeSlotList) ){//check for slot is full qty<9 && order==2
        						BookDriver.makeSlotLocked(mealTypePojo,timeSlotList);//make slot full as inactive
        					}*/
        				//}
    				//}
    			}
    			ArrayList<KitchenStock> kitchenQtyList = new ArrayList<KitchenStock>();
    			Collections.sort(orderItemList);
    			//for(int i=0 ; i < orderItemList.size() && i < dealingKitchenIds.size(); i++){
    			for(int i=0 ; i < orderItemList.size() ; i++){	
    			//kitchenQtyList.add(new KitchenStock(dealingKitchenIds.get(i) , orderItemList.get(i).quantity ));
    				//if(orderItemList.get(i).itemTypeId==1)
    					//continue;
    				kitchenQtyList.add(new KitchenStock(orderItemList.get(i).kitchenId , orderItemList.get(i).quantity ));
    	    	}
    			ArrayList<KitchenStock> updationKitchenStockList = new ArrayList<KitchenStock>();
    			
    			updationKitchenStockList = utility.Utility.findTotalItemsForStockUpdation(kitchenQtyList);
    			//System.out.println("Upadtion kitchen stock list size : "+updationKitchenStockList.size());
    			//updateNewStock(updationKitchenStockList, mealType, deliveryDay);
    			/*****************************************************************/
    			/*************************** update stock item wise  **********/
    			/*****************************************************************/
    			int updateRows = 0;
    			int singleOrderKitchenId = 0;
    			for(OrderItems items : orderItemList){
    				updateRows = StockUpdationDAO.updateKitchenItemStock(items.kitchenId, 
    						items.itemCode, items.quantity, mealType, deliveryDay);
    			}
    			if(updateRows > 0){
    				System.out.println("********************************");
    				System.out.println("*** Stock updated ****"+updateRows);
    				System.out.println("********************************");
    			}
    			if(totalNoOfQuantity == 1){
    				updateRows = 0;
    				for(OrderItems items : orderItemList){
        				singleOrderKitchenId = items.kitchenId;
        				updateRows = StockUpdationDAO.updateSingleOrder(singleOrderKitchenId,mealType,deliveryDay);
        				StockUpdationDAO.updateKitchenItemStock(singleOrderKitchenId, items.itemCode, items.quantity, mealType, deliveryDay);
        			}
    				if(updateRows > 0){
        				System.out.println("*** Single order updated ****"+updateRows);
        			}
    			}
    			
    			if(!isGuestUser){
    				//updateMyCreditBalance(contactNumber);//Old logic for share and earn
    				double[] creditValue = new double[2];
    		    	creditValue = CreditValueDAO.getCreditAndSignUpValue();
    		    	double OrderCreditAmount = creditValue[0];
    				
    		    	BalanceDAO.updateFriendBalance(contactNumber, OrderCreditAmount);
    				if(credit){
    					BalanceDAO.reduceMyBalance(contactNumber, OrderCreditAmount);
    				}
    				
    				PromoCodeDAO.applyRemovePromoCode(promoCode, contactNumber);
    				
    			}
    			isOrderPlaced.put("status", true);
    			java.util.Date date = new java.util.Date();
    			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				String currentTime = sdf.format(date);
				System.out.println("Order time: "+currentTime);
				///String[] orderTimings = new String[2];
				////orderTimings = OrderTimeDAO.getOrderTimings();
				//String initialTimings = orderTimings[0];
				//String finalTimings = orderTimings[1];
				/*if(!OrderTimeDAO.isOrderTimeBetweenKitchenHours(initialTimings, finalTimings, currentTime) ){
					isOrderPlaced.put("message", "Our kitchen is closed now,when it will open the order will be accepted!");
				}else{
					isOrderPlaced.put("message", "Your order placed successfully!");
				}*/
    			isOrderPlaced.put("message", "Your order placed successfully!");
    			isOrderPlaced.put("success", getRecentlyPlacedRegularOrderDetails(orderId));
    			
    			//return isOrderPlaced;
    		}else{
    			System.out.println("####### Regular order failed!! ###########");
    			isOrderPlaced.put("status", false);
        		isOrderPlaced.put("message", "Internal problem occured!");
    			isOrderPlaced.put("success", new JSONObject());
    			//return isOrderPlaced;
    		}
        	
        	/*if(kitchenAssigned){
        		isOrderPlaced.put("success", "regular_success");
        	}*/
    			
    		
    	}else{
    		System.out.println("No kitchen found in given Delivery pincode::: "+pincode);
    		System.out.println("####### Kitchen assignment failed!! Regular order failed!! ###########");
    		//isOrderPlaced.put("success", "regular_success");
    		isOrderPlaced.put("status", false);
    		isOrderPlaced.put("message", "No Kitchen Found!");
    		isOrderPlaced.put("success", new JSONObject());
    		
    		//return isOrderPlaced;	
    	}
    	
    	return isOrderPlaced;
    }
   
    public static ArrayList<Integer> fetchKitchenIDwithUserItems(ArrayList<OrderItems> orderItemList,String pincode){
    	ArrayList<Integer> kitchenIdList = new ArrayList<Integer>();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				/*String sql ="SELECT kitchen_id FROM fapp_kitchen_stock where kitchen_cuisine_id = ? and "
    						+ " kitchen_category_id= ? and cost_price IS NOT NULL " ;*/
    				String sql = "SELECT distinct fks.kitchen_id FROM fapp_kitchen_stock fks JOIN fapp_kitchen fkd "
    				 +" ON fkd.kitchen_id = fks.kitchen_id and "
    				 +" fks.kitchen_cuisine_id =? and  "
    				 +" fks.kitchen_category_id= ? and "
    				 +" fks.cost_price IS NOT NULL and fkd.serving_zipcodes like ? " ;
    				try {
						preparedStatement = connection.prepareStatement(sql);
						for(OrderItems items:orderItemList){
							preparedStatement.setInt(1, items.cuisineId);
							preparedStatement.setInt(2, items.categoryId);
							preparedStatement.setString(3, "%"+pincode+"%");
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								kitchenIdList.add(resultSet.getInt("kitchen_id"));
							}	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return kitchenIdList;
    }
    
    public static java.util.Date getDeliveryDate(String day){
		if(day.equalsIgnoreCase("Today")){
			java.util.Date currDate =  new java.util.Date();
			System.out.println("Today->"+currDate);
			return currDate;
		}else{
			java.util.Date currDate =  new java.util.Date();
			currDate.setTime( currDate.getTime()+1* 24 * 60 * 60 * 1000 );
			System.out.println("Tomorrow->"+currDate);
			return  currDate;
		}
	}
   
    private static JSONObject getRecentlyPlacedRegularOrderDetails(Integer orderID) throws JSONException{
    	JSONObject orders = new JSONObject();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql ="SELECT * FROM vw_recent_placed_order WHERE order_id = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, orderID);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							//JSONObject orders = new JSONObject();
							orders.put("pincode", resultSet.getString("pincode"));
							orders.put("orderid",orderID);
							orders.put("orderno", resultSet.getString("order_no"));
							if(resultSet.getString("final_price") != null){
								orders.put("payAmount", resultSet.getString("final_price"));
							}else{
								orders.put("payAmount", " ");
							}
							if( resultSet.getString("meal_type")!=null){
								orders.put("mealtype", resultSet.getString("meal_type"));
							}else{
								orders.put("mealtype", " ");
							}
							if( resultSet.getString("time_slot")!=null){
								orders.put("timeslot", resultSet.getString("time_slot"));
							}else{
								orders.put("timeslot", " ");
							}
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							orders.put("orderstatus", resultSet.getString("order_status_name"));
							orders.put("startdate", " ");
							orders.put("enddate", " ");
							String orderDate="",reformattedOrderDate="",deliveryOrderDate="",reformattedDeliveryOrderDate="";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							orderDate = resultSet.getString("order_date");
							deliveryOrderDate = resultSet.getString("delivery_date");
							
							try {
								reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
								if(deliveryOrderDate!=null){
									reformattedDeliveryOrderDate = myFormat.format(fromUser.parse(deliveryOrderDate));
									orders.put("deliverydate", reformattedDeliveryOrderDate);
								}else{
									orders.put("deliverydate", "");
								}
								
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							orders.put("orderdate", reformattedOrderDate);
							//orders.put("deliverydate", reformattedDeliveryOrderDate);
							orders.put("itemdetails", getitemdetails(orders.getString("orderno")));
							
						}
					} catch (Exception e) {
						System.out.println(e);
					} finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return orders;
    }
    
    private static boolean updateNewStock(ArrayList<KitchenStock> kitchenStockList, String mealType, String deliveryDay ){
    	boolean stockUpdated = false;
    	int updatecount = 0;
    	try {		
			SQLStockIDList:{
    					Connection connection = DBConnection.createConnection();
    					PreparedStatement preparedStatement = null;
    					String sql = "";
    					if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
    						sql = "UPDATE fapp_kitchen_items set stock = (stock - ?) where kitchen_id = ? and stock > 0";
    					}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
    						sql = "UPDATE fapp_kitchen_items set stock_tomorrow = (stock_tomorrow - ?) where kitchen_id = ? and stock_tomorrow > 0";
    					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
    						sql = "UPDATE fapp_kitchen_items set dinner_stock = (dinner_stock - ?) where kitchen_id = ? and dinner_stock > 0";
    					}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TOMORROW")){
    						sql = "UPDATE fapp_kitchen_items set dinner_stock_tomorrow = (dinner_stock_tomorrow - ?) where kitchen_id = ? and dinner_stock_tomorrow > 0";
    					}
    					
    					try {
							preparedStatement = connection.prepareStatement(sql);
							for(int i=0 ; i<kitchenStockList.size() ; i++){
								preparedStatement.setInt(1, kitchenStockList.get(i).stock);
								preparedStatement.setInt(2, kitchenStockList.get(i).kitchenId);
								preparedStatement.addBatch();
							}
							int[] updateCount = preparedStatement.executeBatch();
							updatecount = updateCount.length;
							for(Integer count : updateCount){
								stockUpdated = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
							connection.rollback();
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
    	System.out.println("Stock updated "+updatecount+" row sucessfully!");
    	return stockUpdated;
    }
    
    private static boolean updateMyCreditBalance(String mobileNo){
    	boolean creditUpdated = false;
    	try {		
			SQLStockIDList:{
    					Connection connection = DBConnection.createConnection();
    					PreparedStatement preparedStatement = null;
    					String sql = "UPDATE fapp_accounts set my_balance = (my_balance - 50) where mobile_no = ? and my_balance >0";
    					try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, mobileNo);
							
							int updateCount = preparedStatement.executeUpdate();
							if(updateCount>0){
								creditUpdated = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
							connection.rollback();
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
    	System.out.println("Credit updated "+creditUpdated);
    	return creditUpdated;
    }
    
    
    private static boolean updateStock(ArrayList<OrderItems> orderItemList , List<Integer> dealingKitchenIdList){
    	boolean stockUpdated = false;
    	int updatecount = 0;
    	try {	
    		
			SQLStockIDList:{
    					Connection connection = DBConnection.createConnection();
    					PreparedStatement preparedStatement = null;
    					String sql= "UPDATE fapp_kitchen_stock set category_stock = (category_stock - ?) "
								+" where kitchen_cuisine_id =  ?"
								+" and kitchen_category_id = ?"
								+" and kitchen_id = ?";
    					//String sql = "UPDATE fapp_kitchen_items set stock = (stock - ?) where kitchen_id = ?";
    					try {
							preparedStatement = connection.prepareStatement(sql);
							for(int i=0 ; i<orderItemList.size() ; i++){
								//for(int j=0 ; j<dealingKitchenIdList.size() ; j++ ){
									//preparedStatement.setInt(1, orderItemList.get(i).quantity);
									//preparedStatement.setInt(2, orderItemList.get(i).cuisineId);
									//preparedStatement.setInt(3, orderItemList.get(i).categoryId);
									preparedStatement.setInt(2, dealingKitchenIdList.get(i));
									System.out.println(preparedStatement);
									updatecount = preparedStatement.executeUpdate();
									if(updatecount>0){
										stockUpdated = true;
									}
								//}
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
    	System.out.println("Stock updated "+updatecount+" row sucessfully!");
    	return stockUpdated;
    }
    
    /*public static JSONObject placeSubscriptionOrder(String userMailId, String contactName ,String contactNumber , 
    		String cityName, String location, String flatNumber , String streetName ,String pincode , 
    		String landmark, String subcriptionType, Integer areaId, String day, 
    		ArrayList<OrderItems> orderItemList) throws JSONException{*/
    public static JSONObject placeSubscriptionOrder(String userMailId, String contactNumber , 
    		String cityName, String location ,String pincode , 
    		String subcriptionType, String deliveryZone, String deliveryAddress, String instruction, String day, 
    		ArrayList<OrderItems> orderItemList) throws Exception{
    	
    	JSONObject isOrderPlaced = new JSONObject();
    	Integer id = 0;
    	Boolean userDetailsInserted = false;
    	Boolean mealTypesInserted = false;
    	Boolean itemDetailsInserted = false;
    	Boolean kitchenAssigned = false;
    	Double totPrice=0d;
    	String userMailID = getUserMailId(contactNumber);
    	List<Integer> dealingKitchenIds = new ArrayList<Integer>();
    	ArrayList<KitchenDetailsBean> kitchenDetailsBeanList =  new ArrayList<KitchenDetailsBean>();
    	//String latlong[] = new String[2];
    	//latlong = getLatLongPositions(deliveryAddress+","+pincode+","+deliveryZone+","+cityName);
    	//System.out.println("Lat long of user->"+latlong[0]+"   "+latlong[1]);
    	
    	kitchenDetailsBeanList = getKitchenDetails(pincode);
    	
    	/*for(KitchenDetailsBean bean :  kitchenDetailsBeanList){
    		System.out.println("kitchen cuisine:"+bean.getKitchenId()+" kitchen cuisine:"
    	   +bean.getCuisineId()+" kitchen category:"+bean.getCategoryId());
    	}*/
    	
    	int listSize = kitchenDetailsBeanList.size();
    	System.out.println("order list size--"+orderItemList.size());
    	for(int i=0; i< orderItemList.size() ; i++){
    		for(int j=0 ; j< listSize ;j++){
    			
    			if(orderItemList.get(i).cuisineId.equals(kitchenDetailsBeanList.get(j).getCuisineId()) 
    			 && orderItemList.get(i).categoryId.equals(kitchenDetailsBeanList.get(j).getCategoryId())){
    				//System.out.println("Selected kitchen id->"+kitchenDetailsBeanList.get(j).getKitchenId() );
    				dealingKitchenIds.add(kitchenDetailsBeanList.get(j).getKitchenId());
    			}
    		}
    	}
    	
    	System.out.println("Selected kitchen ids * * * * * "+dealingKitchenIds);
    	System.out.println("order list size::"+orderItemList.size());
    	System.out.println("kitchen id list size:"+dealingKitchenIds.size());
    	System.out.println("Cart size - >"+orderItemList.size());
    	
    	for(int i=0 ; i < orderItemList.size() && i < dealingKitchenIds.size(); i++){
    		System.out.println("User's Cuisine-"+orderItemList.get(i).cuisineId);
    		System.out.println("User's category-"+orderItemList.get(i).categoryId);
    		System.out.println("Serving kitchen-"+dealingKitchenIds.get(i));
    	}
    	System.out.println("start date->"+orderItemList.get(0).startDate+" end date->"+orderItemList.get(0).endDate);
		
    	for(int i=0;i<orderItemList.size();i++){
			
			totPrice += (orderItemList.get(i).price*orderItemList.get(i).quantity);
		}
    	System.out.println("Total price-->"+totPrice);
    	
    	ArrayList<Integer> selectedKitchenIds = fetchKitchenIDwithUserItems(orderItemList, pincode);
    	if(selectedKitchenIds.size()>0){
    		System.out.println("Selected kitchens are for subscription order:="+selectedKitchenIds);
    	}else{
    		System.out.println("Not selected!");
    	}
    	/*SubscriptionBean subscriptionBean = createSubscriptionData(areaId, orderItemList.get(0).startDate, orderItemList.get(0).endDate,
    			contactName, contactNumber, userMailId, flatNumber, 
    			streetName, pincode, landmark, totPrice, generateSubcriptionNo());*/
    	if(dealingKitchenIds.size()!=0){
    		
    		SubscriptionBean subscriptionBean = createSubscriptionData(orderItemList.get(0).startDate, orderItemList.get(0).endDate,
        			getUserName(contactNumber), contactNumber, userMailID, pincode, deliveryZone ,
        			deliveryAddress, instruction ,totPrice, generateSubcriptionNo());
        	
        	subscriptionBean.subscriptionId = insertSubscriptionData(subscriptionBean);
        	if(subscriptionBean.subscriptionId>0){
        		userDetailsInserted= true;
        	}
        	
        	ArrayList<Integer> mealTypeIdList = saveMealWithDay(subscriptionBean, orderItemList);
        	if(mealTypeIdList.size()>0){
        		mealTypesInserted = true;
        	}
        	
        	itemDetailsInserted = saveMealItems(mealTypeIdList, orderItemList,dealingKitchenIds, subscriptionBean.subscriptionId);
        	
        	/*if(userDetailsInserted && mealTypesInserted && itemDetailsInserted){
        		isOrderPlaced.put("success", "subscription_success");
        	}
        	return isOrderPlaced;*/
        	
        	if(itemDetailsInserted){
        		kitchenAssigned = orderAssignToKitchen(subscriptionBean.subscriptionId , "SUB");
        		boolean finalPriceUpdated = updateFinalPrice(subscriptionBean.subscriptionId);
        		if(finalPriceUpdated)
        		System.out.println("Final price updated!!");
        	}
        	
        	if(userDetailsInserted && mealTypesInserted && itemDetailsInserted && kitchenAssigned){
        		isOrderPlaced.put("success", getRecentlyPlacedSubscriptionOrderDetails(subscriptionBean.subscriptionId));
        	}else{
        		System.out.println("###### Subscription failed!! Subscription failed!! ############");
        		isOrderPlaced.put("success", new JSONObject());
        	}
        	
        	return isOrderPlaced;
        	
    	}else{
    		
    		System.out.println("Kitchen assignment failed!! Subscription failed!!");
    		//isOrderPlaced.put("success", "subscription_success");
    		isOrderPlaced.put("success", new JSONObject());
    		return isOrderPlaced;	
    	}
    }
    
    private static boolean updateFinalPrice(Integer subscriptionId){
    	boolean finalPriceUpdated = false;
    	try {
			SQL:{
    			  Connection connection = DBConnection.createConnection();
    			  PreparedStatement preparedStatement = null;
    			  String sql ="UPDATE fapp_subscription "
    					  	+" SET price=(select sum(meal_price*quantity) from fapp_subscription_meals_details where subscription_id=?) "
    					  	+" WHERE  subscription_id=?" ;
    			  try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, subscriptionId);
					preparedStatement.setInt(2, subscriptionId);
					int count = preparedStatement.executeUpdate();
					if(count>0){
						finalPriceUpdated = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return finalPriceUpdated;
    }
    
    private static String getUserName(String mobileNo){
    	String userName= "";
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select username from fapp_accounts where mobile_no = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							userName = resultSet.getString("username");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(userName!=null){
    		return userName;
    	}else{
    		return mobileNo;
    	}
    }
    
    private static String getUserMailId(String mobileNo){
    	String userMailId= "";
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select email from fapp_accounts where mobile_no = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, mobileNo);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							userMailId = resultSet.getString("email");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(userMailId!=null){
    		return userMailId;
    	}else{
    		return "noemailid@appsquad.in";
    	}
    }
    
    private static void updateFriendBalance(String regUserMobileNo){
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
    		//Find that person whose ref code is using
    		boolean balUpdated = updateBalanceForFriend(refCode);
    	}else{
    		System.out.println("No ref code's person found so,Friend's balance not updated!");
    	}
    }
    
    private static boolean updateBalanceForFriend(String referralCode){
    	boolean balanceUpdated = false;
    	double[] creditValue = new double[2];
    	creditValue = CreditValueDAO.getCreditAndSignUpValue();
    	double orderCreditAmount = creditValue[1];
    	
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
    
    
    /*private static SubscriptionBean createSubscriptionData(Integer areaid,Date stdate,Date endate,String username,String contactno,String mailid,
    		String flatno,String streetname,String pincode,String landmark,Double price,String suscriptionNo){*/
    private static SubscriptionBean createSubscriptionData(Date stdate,Date endate,String username,String contactno,String mailid,
    		String pincode,String deliveryZone , String deliveryAddress, String instruction,Double price,String suscriptionNo){
    	
    	SubscriptionBean subscriptionBean = new SubscriptionBean();
    	//subscriptionBean.areaId = areaid;
    	subscriptionBean.contactName = username;
    	subscriptionBean.contactNo = contactno;
    	subscriptionBean.emailId = mailid;
    	//subscriptionBean.flatNo = flatno;
    	//subscriptionBean.streetName = streetname;
    	//subscriptionBean.landMark = landmark;
    	System.out.println("pincode - "+pincode);
    	System.out.println("dzone - "+deliveryZone);
    	System.out.println("add - -"+deliveryAddress);
    	System.out.println("ins - "+instruction);
    	
    	subscriptionBean.deliveryZone = deliveryZone;
    	subscriptionBean.deliveryAddress = deliveryAddress;
    	subscriptionBean.instruction = instruction;
    	subscriptionBean.pincode = pincode;
    	subscriptionBean.price = price;
    	subscriptionBean.startDate = stdate;
    	subscriptionBean.endDate = endate;
    	subscriptionBean.subscriptionNo = suscriptionNo;
    	return subscriptionBean;
    }
    
    private static Integer insertSubscriptionData(SubscriptionBean subscriptionBean){
    	Integer subscriptionId = null;
    	int count=0;
    	try {
    		Connection connection = DBConnection.createConnection();
				SQL:{
    					
			    		PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "INSERT INTO fapp_subscription( "
						           +" subscription_no, subscribed_by, "
						           +" user_mail_id, contact_number, "
						           +" pincode,start_date,end_date,price,delivery_zone,delivery_address,instruction)"
						           +" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, subscriptionBean.subscriptionNo);
							preparedStatement.setString(2, subscriptionBean.contactName);
							preparedStatement.setString(3, subscriptionBean.emailId);
							preparedStatement.setString(4, subscriptionBean.contactNo);
							//preparedStatement.setString(5, subscriptionBean.flatNo);
							//preparedStatement.setString(6, subscriptionBean.streetName);
							//preparedStatement.setString(7, subscriptionBean.landMark);
							System.out.println("pincode-- while save"+subscriptionBean.pincode+" dZ-"+subscriptionBean.deliveryZone+" dAdd--"+subscriptionBean.deliveryAddress
									+" inst --"+subscriptionBean.instruction);
							preparedStatement.setString(5, subscriptionBean.pincode);
							//preparedStatement.setInt(9, subscriptionBean.areaId);
							preparedStatement.setDate(6, subscriptionBean.startDate);
							preparedStatement.setDate(7, subscriptionBean.endDate);
							preparedStatement.setDouble(8, subscriptionBean.price);
							preparedStatement.setString(9, subscriptionBean.deliveryZone);
							preparedStatement.setString(10, subscriptionBean.deliveryAddress);
							preparedStatement.setString(11, subscriptionBean.instruction);
							count = preparedStatement.executeUpdate();
							if(count>0){
								System.out.println("Subscription created !");
							}
						} catch (Exception e) {
							System.out.println("Subscription user details insertion FAILED!!"+e.getMessage());
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}
							if(resultSet!=null){
								resultSet.close();
							}
						}
    			}
    	
    		if(count>0){
    			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT max(subscription_id)AS id from fapp_subscription ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet =  preparedStatement.executeQuery();
						if(resultSet.next()){
							subscriptionId = resultSet.getInt("id");
							
						}
					} catch (Exception e) {
						System.out.println("Subscription Id generation failed due to!!"+e.getMessage());
					}finally{
						if(preparedStatement!=null){
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
    		}
    		
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(subscriptionId>0){
    		System.out.println("User Details inserted successfully!");
    	}
    	return subscriptionId;
    }
    
    private static ArrayList<Integer> saveMealWithDay(SubscriptionBean subscriptionBean, ArrayList<OrderItems> orderItemList){
    	Integer insertedId = null  ;
    	Boolean inserted=false;
    	ArrayList<Integer> mealIdIst= new ArrayList<Integer>();
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "INSERT INTO fapp_subscription_meals( "
					           +" subscription_no, subscription_id, day_name,  meal_type, time_slot) "
					           +" VALUES ( ?, ?, ?, ?, ?);";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						
						for(OrderItems items:orderItemList){
							preparedStatement.setString(1, subscriptionBean.subscriptionNo);
							preparedStatement.setInt(2, subscriptionBean.subscriptionId);
							preparedStatement.setString(3, items.day);
							preparedStatement.setString(4, items.meal);
							if(items.timsSlot!=null){
								preparedStatement.setString(5, items.timsSlot);
							}else{
								preparedStatement.setNull(5, Types.NULL);
							}
							
							preparedStatement.addBatch();
						}
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   inserted = true;
				    	   }
					} catch (Exception e) {
						System.out.println("Meal types save failed!!"+e.getMessage());
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(resultSet!=null){
							resultSet.close();
						}
					}
	    	}
    		
    		if(inserted){
    			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT subscription_meal_id from fapp_subscription_meals"
							   + " where subscription_id = ? AND is_delete='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, subscriptionBean.subscriptionId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							mealIdIst.add(resultSet.getInt("subscription_meal_id"));
						}
					} catch (Exception e) {
						System.out.println("Meal types id creation failed!!"+e.getMessage());
					}finally{
						if(preparedStatement!=null){
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
    			
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(mealIdIst.size()>0){
    		System.out.println("Subscribed meal types inserted successfully!");
    	}
    	return mealIdIst;
    }
    
    private static Boolean saveMealItems(ArrayList<Integer> mealIdList, ArrayList<OrderItems> orderItemList,List<Integer> kitchenIdList,
    		Integer subscriptionId){
    	Boolean inserted = false;
    	System.out.println("Meal id list size-"+mealIdList.size());
    	System.out.println("Order item list size->"+orderItemList.size());
    	System.out.println("kitchen id list size->"+kitchenIdList.size());
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "INSERT INTO fapp_subscription_meals_details( "
					           +" subscription_meal_id, cuisine_id, "
					           +" category_id, quantity, meal_price,kitchen_id,subscription_id)"
					           +" VALUES (?, ?, ?, ?, ?, ?, ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						for(int i=0;i<mealIdList.size();i++){
							//for(int i=0 ; i < orderItemList.size() && i < kitchenIdList.size(); i++){
							preparedStatement.setInt(1, mealIdList.get(i));
							preparedStatement.setInt(2, orderItemList.get(i).cuisineId);
							preparedStatement.setInt(3, orderItemList.get(i).categoryId);
							preparedStatement.setInt(4, orderItemList.get(i).quantity);
							preparedStatement.setDouble(5, orderItemList.get(i).price);
							if(kitchenIdList.get(i)!=null){
								preparedStatement.setInt(6, kitchenIdList.get(i));
							}else{
								preparedStatement.setNull(6, Types.NULL);
							}
							preparedStatement.setInt(7, subscriptionId);
							
							preparedStatement.addBatch();
						}
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   inserted = true;
				    	   }
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Saving failed due to:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(inserted)
    		System.out.println("Subcribed Items inserted sucessfully!");
    	return inserted;
    }
    
    
    private static Integer getSubcriptionId(String subscriptionNumber){
    	Integer subscriptionID = null;
    	try {
			SQL:{
    			Connection connection = DBConnection.createConnection();
    			PreparedStatement preparedStatement = null;
    			ResultSet resultSet = null;
    			String sql ="SELECT subscription_id from fapp_subscription where subscription_no = ?";
    			
    			try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, subscriptionNumber);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						subscriptionID = resultSet.getInt("subscription_id");	
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return subscriptionID;
    } 
    private static String getSubcriptionAreaId(String subscriptionNumber){
    	/*Integer areaId = null;*/
    	String pincode = "";
    	try {
			SQL:{
    			Connection connection = DBConnection.createConnection();
    			PreparedStatement preparedStatement = null;
    			ResultSet resultSet = null;
    			/*String sql ="SELECT area_id from fapp_subscription where subscription_no = ?";*/
    			String sql ="SELECT pincode from fapp_subscription where subscription_no = ?";
    			try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, subscriptionNumber);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						/*areaId = resultSet.getInt("area_id");	*/
						pincode = resultSet.getString("pincode");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	/*System.out.println("area id-"+areaId);
    	return areaId;*/
    	System.out.println("pincode-"+pincode);
    	return pincode;
    }
  
    private static JSONObject getRecentlyPlacedSubscriptionOrderDetails(Integer subscriptionID){
    	JSONObject orders = new JSONObject();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT * FROM vw_recent_placed_subscription_order WHERE order_id= ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, subscriptionID);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							orders.put("pincode", resultSet.getString("pincode"));
							orders.put("orderid",subscriptionID);
							orders.put("orderno", resultSet.getString("order_no"));
							if( resultSet.getString("meal_type")!=null){
								orders.put("mealtype", resultSet.getString("meal_type"));
							}else{
								orders.put("mealtype", " ");
							}
							if( resultSet.getString("time_slot")!=null){
								orders.put("timeslot", resultSet.getString("time_slot"));
							}else{
								orders.put("timeslot", " ");
							}
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							orders.put("orderstatus", " ");
							/*orders.put("startdate", " ");
							orders.put("enddate", " ");*/
							String orderDate="",reformattedOrderDate="",startDate="",endDate="",reformattedStartDate="",reformattedEndDate="";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							orderDate = resultSet.getString("order_date");
							startDate = resultSet.getString("start_date");
							endDate = resultSet.getString("end_date");
							try {
								reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
								reformattedStartDate = myFormat.format(fromUser.parse(startDate));
								reformattedEndDate = myFormat.format(fromUser.parse(endDate));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							orders.put("orderdate", reformattedOrderDate);
							orders.put("startdate", reformattedStartDate);
							orders.put("enddate", reformattedEndDate);
							orders.put("itemdetails", getMealTypeDetails(orders.getString("orderno")));
						}
					} catch (Exception e) {
						System.out.println(e);
					} finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return orders;
    }
    
    
    /************************************/
    
    private static ArrayList<SubscriptionBean> getSubcriptionTypeAndDay(String userMailId){
    	ArrayList<SubscriptionBean> subscriptionBeanList = new ArrayList<SubscriptionBean>();
    	try {
			SQL:{
    			Connection connection = DBConnection.createConnection();
    			PreparedStatement preparedStatement = null;
    			ResultSet resultSet = null;
    			String sql ="SELECT subscription_id,subscription_type,day from fapp_subscription where user_mail_id = ?";
    			
    			try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, userMailId);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						SubscriptionBean bean = new SubscriptionBean();
						bean.subscriptionID = resultSet.getInt("subscription_id");
						bean.subscriptionType = resultSet.getString("subscription_type");
						bean.day = resultSet.getString("day");
						subscriptionBeanList.add(bean);
					}
					System.out.println("subscriptionBeanList::->"+subscriptionBeanList.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return subscriptionBeanList;
    }
    
    
    public static Boolean saveSubscriptionUserDetils(Integer subscriptionId,String name,String city, String location,
    		String landmark,String pincode,String streetName,String flatNo, ArrayList<OrderItems> orderItemList){
    	
    	Boolean success = false;
    	
    	try {
    		Connection connection = DBConnection.createConnection();
    		String latLongs[] = getLatLongPositions(flatNo+" "+streetName+" "+landmark+" "+location+" "+city+" "+pincode);
            System.out.println("Customer's Latitude: "+latLongs[0]+" and Longitude: "+latLongs[1]);
        	Double deliveryLatitude = Double.parseDouble(latLongs[0]);
        	Double deliveryLongitude = Double.parseDouble(latLongs[1]);
        	Boolean kitchenAssigned = false ,  orderedQuantityIsValid = false;
        	Integer nearestKitchenId, nextNearestKitchenId;
			SQL:{
    			PreparedStatement preparedStatement= null;
				int rows ;
				String sql = "INSERT INTO fapp_order_user_details( "
			            +" subscription_id, order_by, flat_no, street_name, landmark, delivery_location, city, pincode, "
			            + " delivery_latitude,delivery_longitude) "
			            +" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, subscriptionId);
						preparedStatement.setString(2, name);
						preparedStatement.setString(3, flatNo);
						preparedStatement.setString(4, streetName);
						preparedStatement.setString(5, landmark);
						preparedStatement.setString(6, location);
						preparedStatement.setString(7, city); 
						preparedStatement.setString(8, pincode);
						preparedStatement.setDouble(9, deliveryLatitude);
						preparedStatement.setDouble(10, deliveryLongitude);
						
						rows = preparedStatement.executeUpdate();
						
						if(rows>0){
							success = true;
							
							System.out.println("Subscribed User Details inserted:"+success);
						}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				 }	
			}
		} catch (Exception e) {
		
		}
			return success;			
    }
    
    /**
     * Saving user details used in PLACE ORDER WEB SERVICE
     * @param id
     * @param name
     * @param landmark
     * @param pincode
     * @param streetName
     * @param flatNo
     * @return
     */
    public static Boolean saveUserDetils(Integer orderId,String city, String location,
    		String pincode, ArrayList<OrderItems> orderItemList,
    		String deliveryZone,String deliveryAddress,String instruction){
    	Boolean success = false;
    	
    	try {
    		Connection connection = DBConnection.createConnection();
    		//connection.setAutoCommit(false);
    		//String latLongs[] = getLatLongPositions(flatNo+" "+streetName+" "+landmark+" "+location+" "+city+" "+pincode);
          //   System.out.println("---Customer's Latitude: "+latLongs[0]+" ----and Longitude: "+latLongs[1]);
        	//Double deliveryLatitude = Double.parseDouble(latLongs[0]);
        	//Double deliveryLongitude = Double.parseDouble(latLongs[1]);
        	Boolean kitchenAssigned = false ,  orderedQuantityIsValid = false;
        	Integer nearestKitchenId, nextNearestKitchenId;
			SQL:{
    			PreparedStatement preparedStatement= null;
				int rows ;
				/*String sql = "INSERT INTO fapp_order_user_details( "
			            +" order_id, order_by, flat_no, street_name, landmark, delivery_location, city, pincode, delivery_latitude,delivery_longitude) "
			            +" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";*/
				/*String sql = "INSERT INTO fapp_order_user_details( "
			            +" order_id, order_by, flat_no, street_name, landmark, delivery_location, city, pincode,deliveryZone, deliveryAddress, instruction) "
			            +" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";*/
				String sql = "INSERT INTO fapp_order_user_details( "
			            +" order_id,  delivery_location, city, pincode,delivery_zone, delivery_address, instruction) "
			            +" VALUES (?, ?, ?, ?, ?, ?, ?);";
				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, orderId);
						/*preparedStatement.setString(2, name);
						preparedStatement.setString(3, flatNo);
						preparedStatement.setString(4, streetName);
						if(landmark!=null){
							preparedStatement.setString(5, landmark);
						}else{
							preparedStatement.setNull(5, Types.NULL);
						}*/
						
						preparedStatement.setString(2, location);
						if(city!=null){
							preparedStatement.setString(3, city); 
						}else{
							preparedStatement.setString(3, "Kolkata"); 
						}
						preparedStatement.setString(4, pincode);
						//preparedStatement.setDouble(9, deliveryLatitude);
						//preparedStatement.setDouble(10, deliveryLongitude);
						if(deliveryZone!=null){
							if(deliveryZone.equalsIgnoreCase("New town")){
								preparedStatement.setString(5,"New Town (kolkata)");
							}else if(deliveryZone.equalsIgnoreCase("Saly lake")){
								preparedStatement.setString(5, "Salt Lake City");
							}else{
								preparedStatement.setString(5, toCamelCase(deliveryZone));
							}
						}else{
							preparedStatement.setNull(5, Types.NULL);
						}
						if(deliveryAddress!=null){
							preparedStatement.setString(6, deliveryAddress);
						}else{
							preparedStatement.setNull(6, Types.NULL);
						}
						if(instruction!=null){
							preparedStatement.setString(7, instruction);
						}else{
							preparedStatement.setNull(7, Types.NULL);
						}
						
						rows = preparedStatement.executeUpdate();
						
						if(rows>0){
							success = true;
							
							System.out.println("User Details inserted:"+success);
							
						/*	nearestKitchenId = getNearestKitchenId(deliveryLatitude, deliveryLongitude);
							System.out.println("Nearest KitchenId- - - >"+nearestKitchenId);
							if(isExcessQuantity(getUserQuantity(orderItemList), getkitchenStock(nearestKitchenId, orderItemList))){
							//if user quantity excess with nearest kitchen then find next nearest kitchen
								System.out.println("Order id->"+orderId+" is rejected from "+nearestKitchenId+" as quantity exceeds...");
									nextNearestKitchenId = getNextNearestKitchenId(nearestKitchenId);
									System.out.println("Next nearest KitchenId->"+nextNearestKitchenId);
									if(isExcessQuantity(getUserQuantity( orderItemList), getkitchenStock(nextNearestKitchenId, orderItemList))){
										
										orderedQuantityIsValid = false;
										
										kitchenAssigned = false;
										
										success = false;
										
										System.out.println("Again Order id->"+orderId+" is rejected from "+nextNearestKitchenId+" as quantity exceeds...");
										
									}else{
										
										orderedQuantityIsValid = true;
										
										kitchenAssigned = orderAssignToKitchen(orderId, nextNearestKitchenId);
										
										success = true;
										
										System.out.println("Order id->"+orderId+" is assigned to "+nextNearestKitchenId+" as quantity NOT exceeds...");
										
									}
								
							}else{
								
									orderedQuantityIsValid = true;
									
									kitchenAssigned = orderAssignToKitchen(orderId, nearestKitchenId);
									
									success = true;
									
									System.out.println("Order id->"+orderId+" is assigned to "+nearestKitchenId+" as quantity NOT exceeds...");
									
								} */
							//getkitchenStock(nearestKitchenId, orderItemList);
					
						}
						
				}  catch (Exception e) {
					connection.rollback();
					e.printStackTrace();
				} finally{
					if(connection!=null){
					//	connection.setAutoCommit(true);
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
		
		}
    	if(success){
    		System.out.println("User details successfully inserted!");
    	}
    	return success;
    }
    
    public static Boolean saveSubscribedItemDetails(Integer subscribedId, ArrayList<OrderItems> itemList) throws JSONException{
    	Boolean success = false;
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    			PreparedStatement preparedStatement= null;
				int rows ;
				String sql = "INSERT INTO fapp_order_item_details( "
				                +" subscription_id, category_id, qty,category_price, total_price, cuisine_id) "
				                +" VALUES ( ?, ?, ?, ?, ?, ?)";
    			try {
						preparedStatement = connection.prepareStatement(sql);
						
						for(OrderItems orderItems : itemList){
							preparedStatement.setInt(1, subscribedId);
							preparedStatement.setInt(2, orderItems.categoryId);
							
							preparedStatement.setInt(3, orderItems.quantity);
							
							preparedStatement.setDouble(4, orderItems.price);
							
							preparedStatement.setDouble(5, (orderItems.quantity*orderItems.price) );
							
							preparedStatement.setInt(6, orderItems.cuisineId);
							preparedStatement.addBatch();
						}
						
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   success = true;
				    	   }
						
				}  catch (Exception e) {
					System.out.println("ERROR::"+e);
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
		}
    	
    	return success;
    }
    
    public static Boolean saveItemDetails(Integer orderId,ArrayList<OrderItems> itemList) throws JSONException{
    	Boolean success = false;
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    			PreparedStatement preparedStatement= null;
				int rows ;
				String sql = "INSERT INTO fapp_order_item_details( "
				            +" order_id, category_id, qty,category_price, total_price, cuisine_id) "
				            +" VALUES ( ?, ?, ?, ?, ?, ?)";
    			try {
						preparedStatement = connection.prepareStatement(sql);
						
						for(OrderItems orderItems : itemList){
							preparedStatement.setInt(1, orderId);
							
							preparedStatement.setInt(2, orderItems.categoryId);
							
							preparedStatement.setInt(3, orderItems.quantity);
							
							preparedStatement.setDouble(4, orderItems.price);
							
							preparedStatement.setDouble(5, (orderItems.quantity*orderItems.price) );
							
							preparedStatement.setInt(6, orderItems.cuisineId);
							
							preparedStatement.addBatch();
						}
						
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   success = true;
				    	   }
						
				}  catch (Exception e) {
					System.out.println("ERROR::"+e);
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
		}
    	
    	return success;
    }
    
    
   /* private static Boolean saveItemsWithKitchen(Integer orderId , ArrayList<OrderItems> orderItemList ,
    		List<Integer> kitchenIdList){*/
    private static Boolean saveItemsWithKitchen(Integer orderId , ArrayList<OrderItems> orderItemList ,
    		ArrayList<Integer> kitchenIdList){
    	Boolean inserted = false;
    	try {
			Connection connection = DBConnection.createConnection();
			Collections.sort(orderItemList);
		//	connection.setAutoCommit(false);
			PreparedStatement preparedStatement = null;
			SQL:{
				String sql = "INSERT INTO fapp_order_item_details( "
			            +" order_id, category_id, qty,category_price, total_price, cuisine_id,kitchen_id,sub_order_no,item_code,pack_type, rice_roti) "
			            +" VALUES ( ?, ?, ?, ?, ?, ?, ? ,?,?,?,?)";
				try {
					
						preparedStatement = connection.prepareStatement(sql);
						Collections.sort(orderItemList);
						/*for(int i=0 ; i < orderItemList.size() && i < kitchenIdList.size(); i++){
				    		preparedStatement.setInt(1, orderId);
				    		preparedStatement.setInt(2, orderItemList.get(i).categoryId); 
				    		preparedStatement.setInt(3, orderItemList.get(i).quantity);
				    		preparedStatement.setDouble(4, orderItemList.get(i).price); 
				    		preparedStatement.setDouble(5, (orderItemList.get(i).quantity*orderItemList.get(i).price) );
				    		preparedStatement.setInt(6, orderItemList.get(i).cuisineId);
				    		preparedStatement.setInt(7, kitchenIdList.get(i));
				    		preparedStatement.setString(8, orderId.toString()+"/"+(i+1) );
				    		preparedStatement.setString(9, orderItemList.get(i).itemCode);
				    		preparedStatement.setString(10, orderItemList.get(i).packing);
				    		preparedStatement.addBatch();
				    	}*/
						int i=0;
						for(OrderItems items : orderItemList){
							preparedStatement.setInt(1, orderId);
				    		preparedStatement.setInt(2, items.categoryId); 
				    		preparedStatement.setInt(3, items.quantity);
				    		preparedStatement.setDouble(4, items.price); 
				    		preparedStatement.setDouble(5, (items.quantity*items.price) );
				    		preparedStatement.setInt(6, items.cuisineId);
				    		preparedStatement.setInt(7, items.kitchenId);
				    		preparedStatement.setString(8, orderId.toString()+"/"+(i+1) );
				    		preparedStatement.setString(9, items.itemCode);
				    		preparedStatement.setString(10, items.packing);
				    		preparedStatement.setString(11, items.mealType);
				    		//System.out.println("KIT Ch ENSSSSSSSSSS ------------ " + preparedStatement);
				    		preparedStatement.addBatch();
				    		i++;
						}
						int [] count = preparedStatement.executeBatch();
				    	 //  connection.commit();
				    	   for(Integer integer : count){
				    		   inserted = true;
				    	   }
				} catch (Exception e) {
//					connection.rollback();
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(connection!=null){
						//connection.setAutoCommit(true);
						connection.close();
					}
				}
			}
				 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(inserted){
    		System.out.println("Item details inserted successfully!");
    	}else{
    		System.out.println("Item details insertion failed!");
    	}
    	return inserted;
    }
    
    public static boolean assignOrderToKitchen(ArrayList<Integer> dealingKitchenIds, String orderType , Integer orderId){
    	Boolean kitchenAssigned = false;
    	Set<Integer> kitchenIdList = new HashSet<Integer>();
		kitchenIdList.addAll(dealingKitchenIds);
    	try {
			SQL:{
    			Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement= null;
				String sql = "INSERT INTO fapp_order_tracking"
						    +"(order_id, kitchen_id, logistics_id)"
						    +" VALUES (?, ?, ? );";
				
				String sqlsub = "INSERT INTO fapp_order_tracking"
					    +"(subscription_id, kitchen_id, logistics_id)"
					    +" VALUES (?, ?, ? );";
				try {
					if(orderType.equalsIgnoreCase("REGULAR")){
						preparedStatement = connection.prepareStatement(sql);
					}else{
						preparedStatement = connection.prepareStatement(sqlsub);
					}
					
					for(Integer kitchenid : kitchenIdList){
						preparedStatement.setInt(1, orderId);
						preparedStatement.setInt(2, kitchenid);
						preparedStatement.setInt(3, 20);
						preparedStatement.addBatch();
					}
					int [] count = preparedStatement.executeBatch();
			    	if(count.length==kitchenIdList.size()){
			    		   kitchenAssigned = true;  
			    		   for(Integer id : kitchenIdList){
			    			   //message to kitchen
			    			   if(orderType.equalsIgnoreCase("REGULAR")){
			    				  System.out.println("Right now messege is closed for temp. .");
			    				  
			    				    sendMessageToMobile(getKitchenMobile(id), getOrderNo(orderId,"REGULAR"), getOrderTime(getOrderNo(orderId,"REGULAR"),orderType), 1);	  
			    			   }else{
			    				  // sendMessageToMobile(getKitchenMobile(id), getOrderNo(orderId,"SUB"), getOrderTime(getOrderNo(orderId,"SUB"),orderType), 1);		
			    			   }
			    			   // sendMessage(getDeviceRegIdKitchen(id),getOrderNo(orderId),1);
			    		   }
			    		   
			    	   }
					/*preparedStatement.setInt(1, orderId);
					preparedStatement.setInt(2, kitchenId);
					int count = preparedStatement.executeUpdate();
					if(count > 0){
						kitchenAssigned = true;	
					}*/
				} catch (Exception e) {
					e.printStackTrace();
					connection.rollback();
				} finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(connection!=null){
					//	connection.setAutoCommit(true);
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return kitchenAssigned;
    }
   
	public static Integer getNextNearestKitchenId(Integer rejectionKitchenId){
    	Integer kitchenId = 0;
    	Double destlat = 0d, destlong = 0d;
    	try {
    		Connection connection = DBConnection.createConnection();
    		SQL:{
		    		PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "Select latitude,longitude from fapp_kitchen where kitchen_id=?";
					try {
						preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setInt(1, rejectionKitchenId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							destlat = resultSet.getDouble("latitude");
							destlong = resultSet.getDouble("longitude");
						}
						System.out.println("Excess Kitchen lat:"+destlat+" Kitchen long: "+destlong);
					}catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
    	
    	
    		SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql ="SELECT kitchen_name,kitchen_id," 
								+" 3956 * 2 * "
								+" ASIN(SQRT( "
								+" POWER( SIN((? - abs(dest.latitude)) * pi()/180 / 2),2) "
								+" + COS(? * pi()/180 )  "
								+" * COS(abs(dest.latitude) * pi()/180)  "
								+" * POWER(SIN((? - abs(dest.longitude)) * pi()/180 / 2), 2) )) "
								+"  as distance "
								+" FROM fapp_kitchen dest "
								+" order by distance asc LIMIT 2";
    				try {
    					preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setDouble(1, destlat);
						preparedStatement.setDouble(2, destlat);
						preparedStatement.setDouble(3, destlong);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							kitchenId = resultSet.getInt("kitchen_id");	
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    				
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return kitchenId;
    }
    
    public static Boolean orderAssignToKitchen(Integer orderId, String orderType ){
    	Boolean kitchenAssigned = false;
    	Boolean logisticsAssigned = false;
    	String mob="9831444100";
    	System.out.println("Order type-->"+orderType);
    	List<Integer> kitchenIdList =  new ArrayList<Integer>();
    	try {
    		Connection connection = DBConnection.createConnection();
    		//connection.setAutoCommit(false);
    		
    		SQL_FIND_KITCHEN_ID_LIST:{
	    			PreparedStatement preparedStatement = null;
	    			ResultSet resultSet = null;
	    			
	    			String sql = "select distinct kitchen_id "
								+" from  "
								+" fapp_order_item_details "
								+" where  "
								+" kitchen_id IS NOT NULL "
	 							+" and order_id = ? "
								+" order by kitchen_id " ; 
	    			
	    			String sqlSub = "select distinct kitchen_id "
								 +"from  "
								 +"fapp_subscription_meals_details "
								 +"where  "
								 +"kitchen_id IS NOT NULL " 
	 							 +"and subscription_id = ? "
								 +"order by kitchen_id ";
	    			
	    			try {
						if(orderType.equalsIgnoreCase("SUB")){
							preparedStatement = connection.prepareStatement(sqlSub);
							preparedStatement.setInt(1, orderId);
						}else{
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, orderId);
						}
	    				
						resultSet = preparedStatement.executeQuery();
					
						while (resultSet.next()) {
							kitchenIdList.add(resultSet.getInt("kitchen_id"));
						}
					} catch (Exception e) {
						e.printStackTrace();
						
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}
							if(resultSet!=null){
								resultSet.close();
							}
						 }
    		}
    		
			SQL_KITCHEN_WITH_ORDER:{
    				PreparedStatement preparedStatement= null;
    				String sql = "INSERT INTO fapp_order_tracking"
    						    +"(order_id, kitchen_id, logistics_id)"
    						    +" VALUES (?, ?, ? );";
    				
    				String sqlsub = "INSERT INTO fapp_order_tracking"
						    +"(subscription_id, kitchen_id, logistics_id)"
						    +" VALUES (?, ?, ? );";
    				try {
    					if(orderType.equalsIgnoreCase("REGULAR")){
							preparedStatement = connection.prepareStatement(sql);
						}else{
							preparedStatement = connection.prepareStatement(sqlsub);
						}
    					//preparedStatement = connection.prepareStatement(sql);
						
						for(Integer kitchenid : kitchenIdList){
							preparedStatement.setInt(1, orderId);
							preparedStatement.setInt(2, kitchenid);
							preparedStatement.setInt(3, 20);
							preparedStatement.addBatch();
						}
						int [] count = preparedStatement.executeBatch();
				    	//   connection.commit();
				    	   if(count.length==kitchenIdList.size()){
				    		   kitchenAssigned = true;
				    		   
				    		   for(Integer id : kitchenIdList){
				    			   //message to kitchen
				    			   if(orderType.equalsIgnoreCase("REGULAR")){
				    				  System.out.println("Right now messege is closed for temp. .");
				    				   // sendMessageToMobile(getKitchenMobile(id), getOrderNo(orderId,"REGULAR"), getOrderTime(getOrderNo(orderId,"REGULAR"),orderType), 1);	  
				    			   }else{
				    				   sendMessageToMobile(getKitchenMobile(id), getOrderNo(orderId,"SUB"), getOrderTime(getOrderNo(orderId,"SUB"),orderType), 1);		
				    			   }
				    			   // sendMessage(getDeviceRegIdKitchen(id),getOrderNo(orderId),1);
				    		   }
				    		   
				    	   }
						/*preparedStatement.setInt(1, orderId);
						preparedStatement.setInt(2, kitchenId);
    					int count = preparedStatement.executeUpdate();
    					if(count > 0){
    						kitchenAssigned = true;	
    					}*/
					} catch (Exception e) {
						e.printStackTrace();
						connection.rollback();
					} finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(connection!=null){
						//	connection.setAutoCommit(true);
							connection.close();
						}
					}	
    		}
    		
    		/*SQL_ORDER_WITH_LOGISTICS:{
				PreparedStatement preparedStatement= null;
				String sql = "UPDATE fapp_order_tracking SET logistics_id = ?"
						     +"WHERE order_id = ? AND kitchen_id = ? ";
						     
				try {
					
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, 20);
					preparedStatement.setInt(2, orderId);
					preparedStatement.setInt(3, kitchenId);
					int count = preparedStatement.executeUpdate();
					if(count > 0){
						logisticsAssigned = true;	
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
		}*/
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(kitchenAssigned){
    		System.out.println("Kitchens are assigned successfully!");
    	}
    	return kitchenAssigned ; 
    }
  
    private static String getOrderNo(Integer orderId,String orderType){
    	String orderno = "";
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT order_no from fapp_orders where order_id = ?";
    				String sqlSub = "SELECT subscription_no AS order_no from fapp_subscription where subscription_id = ?";
    				try {
    					if(orderType.equalsIgnoreCase("REGULAR")){
    						preparedStatement =  connection.prepareStatement(sql);
    						preparedStatement.setInt(1, orderId);
    					}else{
    						preparedStatement =  connection.prepareStatement(sqlSub);
    						preparedStatement.setInt(1, orderId);
    					}
						
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							orderno = resultSet.getString("order_no");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return orderno;
    }
    /**
     * Method to check whether uname and pwd combination are correct
     * 
     * @param uname
     * @param pwd
     * @return
     * @throws Exception
     */
    public static JSONObject checkLogin(String uname, String pwd ) throws Exception{
    	JSONObject jsonObject = new JSONObject();
    	Boolean isUserAvailable = false;
    	Connection dbConnect = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	try {
	    		try {
	    			dbConnect = DBConnection.createConnection();
				} catch (Exception e) {
					// TODO: handle exception
				}
			 preparedStatement = dbConnect.prepareStatement("SELECT * FROM fapp_accounts "
			 						+ " WHERE username = '"+uname+"' AND password = '"+pwd+"' ");
			 resultSet = preparedStatement.executeQuery();
			 while(resultSet.next()){
				 isUserAvailable = true;
			 }
		} catch (SQLException sqle) {
            throw sqle;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (dbConnect != null) {
                dbConnect.close();
            }
            throw e;
        } finally {
            if (dbConnect != null) {
                dbConnect.close();
            }
        }
    	jsonObject.put("logincheck", isUserAvailable);
    	return jsonObject;
    }
    
    
    /**
     * WEB SERVICE for delivery boy login check
     * @param userid
     * @param pwd
     * @return
     * @throws Exception
     */
    public static JSONObject checkKitchenLogin(String userid, String pwd ) throws Exception{
    	JSONObject jsonObject = new JSONObject();
    	Boolean isUserAvailable = false;
    	Connection connection = null;
    	try {
	    	connection = DBConnection.createConnection();
			SQL:{
	    		PreparedStatement preparedStatement = null;
	        	ResultSet resultSet = null;
	        	String sql = "SELECT * FROM fapp_accounts "
 						+ " WHERE username = ? AND password = ? ";
	        	try {
	        		 preparedStatement = connection.prepareStatement(sql);
					 preparedStatement.setString(1, userid);
					 preparedStatement.setString(2, pwd);
					 resultSet = preparedStatement.executeQuery();
					 if(resultSet.next()){
						 isUserAvailable = true;
					
					 }else{
						 isUserAvailable = false;
						
					 }
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
	    	}
			 
		} catch (Exception e) {
           
        } 
    	jsonObject.put("status", isUserAvailable);
    	return jsonObject;
    }
    
    
    /**
     * WEB SERVICE for delivery boy login check
     * @param userid
     * @param pwd
     * @return
     * @throws Exception
     */
    public static JSONObject checkdeliveryBoyLogin(String userid, String pwd ) throws Exception{
    	JSONObject jsonObject = new JSONObject();
    	Boolean isUserAvailable = false;
    	Connection connection = null;
    	String name = null;
    	try {
	    		
	    	connection = DBConnection.createConnection();
			SQL:{
	    		PreparedStatement preparedStatement = null;
	        	ResultSet resultSet = null;
	        	String sql = "SELECT delivery_boy_name FROM fapp_delivery_boy "
 						+ " WHERE delivery_boy_user_id = ? AND password = ? ";
	        	try {
	        		 preparedStatement = connection.prepareStatement(sql);
					 preparedStatement.setString(1, userid);
					 preparedStatement.setString(2, pwd);
					 
					 resultSet = preparedStatement.executeQuery();
					 while(resultSet.next()){
						 name = resultSet.getString("delivery_boy_name");
						 System.out.println("Biker name-- > "+name);
						 isUserAvailable = true;
					 }
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
	    	}
			 
		} catch (Exception e) {
           
        } 
    	jsonObject.put("status", isUserAvailable);
    	jsonObject.put("message", name);
    	System.out.println(jsonObject);
    	return jsonObject;
    }
    
    /**
     * WEB SERVICE FOR DELIVERY BOY ORDERS
     * @param deliveryBoyUserId
     * @param password
     * @return
     * @throws JSONException
     */
    public static JSONObject getDeliveryOrders(String deliveryBoyUserId)throws JSONException{
    	JSONObject orderId =  new JSONObject();
		JSONArray orderArray =  new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select * from vw_delivery_boy_order_list where delivery_boy_user_id=? AND delivery_date_time IS NULL";
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, deliveryBoyUserId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject temporder =  new JSONObject();
							temporder.put("orderno", resultSet.getString("subscription_no"));
							temporder.put("day", resultSet.getString("day_name"));
							temporder.put("meal", resultSet.getString("meal_type"));
							temporder.put("timeslot", resultSet.getString("time_slot"));
							int quantity = resultSet.getInt("quantity");
							temporder.put("quantity", String.valueOf(quantity));
							Double mealPrice = resultSet.getDouble("meal_price");
							Double totPrice = quantity * mealPrice;
							temporder.put("price", String.valueOf(totPrice) );
							temporder.put("deliveryaddress", resultSet.getString("delivery_address"));
							temporder.put("pincode", resultSet.getString("pincode"));
							temporder.put("orderby", resultSet.getString("subscribed_by"));
							temporder.put("contactnumber", resultSet.getString("contact_number"));
							if(resultSet.getString("on_trip").equals("N")){
								temporder.put("ontrip", "PENDING");
							}else{
								temporder.put("ontrip", "ORDER ON TRIP");
							}
							
							orderArray.put(temporder);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println("Delivery boy order list size::"+orderArray.length());
    	return orderId.put("orderlist", orderArray);    	
    }
    
    public static JSONObject getdeliveryordersforbiker(String deliveryBoyUserId) throws Exception{
		JSONObject deliveryList = new JSONObject();
		JSONArray orderArrayValue = new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement  preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="select * from vw_driver_orders where driver_boy_user_id = ? ";
					try {
						preparedStatement= connection.prepareStatement(sql);
						preparedStatement.setString(1, deliveryBoyUserId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next() ) {
							JSONObject jsonObject = new JSONObject();
							String orderNo = resultSet.getString("order_no");	
							String finalPrice = String.valueOf(resultSet.getDouble("final_price"));
							if(finalPrice!=null){
								jsonObject.put("finalPrice", finalPrice);
							}else{
								jsonObject.put("finalPrice", "");
							}
							String kitchenName = resultSet.getString("kitchen_name");
							String orderPicked = resultSet.getString("order_picked");
							String driverReached = resultSet.getString("driver_reached");
							jsonObject.put("orderno", orderNo);
							if(orderPicked.equalsIgnoreCase("Y")){
								jsonObject.put("picked",true );
							}else{
								jsonObject.put("picked", false);
							}
							if(driverReached.equalsIgnoreCase("Y")){
								jsonObject.put("driverReached",true );
							}else{
								jsonObject.put("driverReached", false);
							}
							/*if(resultSet.getString("payment_name").equalsIgnoreCase("CARD")){
								orders.put("payType", "PAID");
							}else {
								orders.put("payType", resultSet.getString("payment_name"));
							}*/
							String paymentName = resultSet.getString("payment_name");
							if(paymentName!= null){
								if(resultSet.getString("payment_name").equalsIgnoreCase("CARD")){
									jsonObject.put("paymenttype", "PAID BY CARD");
								}else {
									jsonObject.put("paymenttype", paymentName);
								}
							}else{
								jsonObject.put("paymenttype", " ");
							}
							jsonObject.put("mealtype", resultSet.getString("meal_type"));
							jsonObject.put("timeslot", resultSet.getString("time_slot"));
							jsonObject.put("kitchenname", kitchenName);
							jsonObject.put("kitchenmobileno", resultSet.getString("mobile_no"));
							jsonObject.put("kitchenaddress", resultSet.getString("address"));
							jsonObject.put("itemdetails", getKitchenItemdetails(orderNo, kitchenName));
							jsonObject.put("orderby", resultSet.getString("order_by"));
							jsonObject.put("contactnumber", resultSet.getString("contact_number"));
							jsonObject.put("deliveryzone", resultSet.getString("delivery_zone"));
							String deliveryaddress = resultSet.getString("delivery_address");
							String pincode = resultSet.getString("pincode");
							jsonObject.put("deliveryaddress", deliveryaddress);
							String landmark = resultSet.getString("instruction");
							if(landmark!=null){
								jsonObject.put("landmark", landmark);
							}else{
								jsonObject.put("landmark", "- - NOT GIVEN - -");
							}
							jsonObject.put("pincode", pincode);
							String lat="",lng="";
							String latLongs[] = LatLng.getLatLongPositions(deliveryaddress+" , "+pincode);
						    if(latLongs[0].equals("LAT") && latLongs[1].equals("LONG")){
						    	System.out.println("Invalid addres!");
						    	//lat = null;lng=null;
						    }else{
						    	System.out.println("SUCESS Latitude: "+latLongs[0]+" and Longitude: "+latLongs[1]);
						    	lat = latLongs[0];lng= latLongs[1];
						    }
							jsonObject.put("paymentname", resultSet.getString("payment_name"));
							jsonObject.put("latitude", lat);
							jsonObject.put("longitude", lng);
							
							orderArrayValue.put(jsonObject);
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
		System.out.println("No of orders:: "+orderArrayValue.length());
		return deliveryList.put("orderlist", orderArrayValue);
    }
    
    public static JSONObject pickuporder(String boyUserId, String orderNo, String kitchenId)throws Exception{
    	JSONObject pickUpJsonObj = new JSONObject();
    	if(pickUpOrder(boyUserId, orderNo, kitchenId)){
    		pickUpJsonObj.put("status", true);
    	}else{
    		pickUpJsonObj.put("status", false);
    	}
    	return pickUpJsonObj;
    }
    
    public static boolean pickUpOrder(String boyUserId, String orderNo, String kitchenId){
    	boolean orderPicked = false;
    	boolean driverBusy = false;
    	Connection connection = null;
    	try {
    		connection = DBConnection.createConnection();
			SQL:{//Make one flag Y for order picking
    			PreparedStatement  preparedStatement = null;
    			String sql = "UPDATE fapp_order_tracking set order_picked = 'Y',driver_pickup_time=current_timestamp "
    					+ " where driver_boy_user_id = ? and order_id= "
    					+ "(select order_id from fapp_orders where order_no = ?) and kitchen_id= "
    					+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?)";
    			try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
					preparedStatement.setString(2, orderNo);
					preparedStatement.setString(3, kitchenId);
					int count = preparedStatement.executeUpdate();
					if(count>0){
						orderPicked = true;
					}
				} catch (Exception e) {
					System.out.println("Order picking failed due to::"+e.getMessage());
					connection.rollback();
					
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
    		}
    		if(orderPicked){
	    		SQL:{//Make boy status busy by 3
	    			PreparedStatement  preparedStatement = null;
	    			String sql = "UPDATE fapp_delivery_boy set delivery_boy_status_id = 3 "
	    					+ " where delivery_boy_user_id = ? ";
	    			try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						
						int count = preparedStatement.executeUpdate();
						if(count>0){
							driverBusy = true;
						}
					} catch (Exception e) {
						System.out.println("Driver busy failed due to::"+e.getMessage());
						connection.rollback();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
						if(connection!=null){
							connection.close();
						}
					}
	    		}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	Biker biker = BikerDAO.getDriverDetails(boyUserId);
		SendMessageDAO.sendMessageForDeliveryBoy(getCustomerMobile(orderNo, "REGULAR"), biker.getBikerContact(), biker.getBikerName(),  orderNo);

    	return orderPicked;
    }
    
    /**
     * WEB SERVICE For order deliver to customer
     * @param boyUserId
     * @param orderNo
     * @param kitchenId
     * @return
     * @throws Exception
     */
    public static JSONObject deliverOrder(String boyUserId, String orderNo, String kitchenId)throws Exception{
    	JSONObject deliverJsonObj = new JSONObject();
    	if(orderDeliveredFromPickUpToDrop(boyUserId, orderNo, kitchenId)){//One flag "delivered" is upadted with current time wrt to orderid
    		if( makeOrderCompleted(orderNo) ){
    			/**
				 * SEND MESSAGE TO CUSTOMER FOR ORDER DELIVEREY
				 */
    			if(SendMessageDAO.isDeliveryMessageSend(orderNo)){
    				System.out.println("Already message sent!!!");
    			}else{
    				sendMessageToMobile(getCustomerMobile(orderNo, "REGULAR"), 
    						orderNo, "orderTime" , 7);
    				User user = UserDetailsDao.getUserDetails(null, orderNo);
    				Order order = OrderDetailsDAO.getOrderDetails(orderNo);
    				ArrayList<OrderItems> orderItemList = OrderItemDAO.getOrderItemDetails(orderNo);
    				/**
    				 * SEND INVOICE TO CUSTOMER 
    				 */
    				Invoice.generateAndSendEmail(user, order, orderItemList);
    				SendMessageDAO.updateSendMessageStatus(orderNo);
    			}
    		}
    		deliverJsonObj.put("status", true);
    		if(isAllOrdersDelivered(boyUserId)){// check whether the no of total order and delivered order are same or not
    			makeDriverIdle(boyUserId);//one flag boy status is change from busy to idle 
    		}
    	}
    	return deliverJsonObj;
    }
    
    public static boolean orderDeliveredFromPickUpToDrop(String boyUserId, String orderNo, String kitchenId){
    	boolean isDeliverd = false;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "UPDATE fapp_order_tracking set delivered ='Y', order_delivery_time=current_timestamp"
    						+ " where driver_boy_user_id = ? and order_id= "
        					+ "(select order_id from fapp_orders where order_no = ?) and kitchen_id= "
        					+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						preparedStatement.setString(2, orderNo);
						preparedStatement.setString(3, kitchenId);
						System.out.println(preparedStatement);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							isDeliverd = true;
						//	sendMessageToMobile(getCustomerMobile(orderNo,"REGULAR"), orderNo, null, 7);
						}
    				} catch (Exception e) {
						// TODO: handle exception
    					System.out.println("Order delivery failes due to::"+e.getMessage());
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
    	return isDeliverd;
    }
    
    
    public static boolean isAllOrdersDelivered(String boyUserId){
    	int totalOrders=0,totalDeliveredOrders = 0;
    	try {
			Connection connection = DBConnection.createConnection();
			//SQL FOR FINDING TOTAL ORDERS
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select count(order_id) AS total_orders from "
						+ "fapp_order_tracking where driver_boy_user_id = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
					resultSet = preparedStatement.executeQuery();
					if(resultSet.next()){
						totalOrders = resultSet.getInt("total_orders");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
			
			//SQL FOR FINDING TOTAL DELIVERD ORDERS
			SQL:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select count(delivered) AS total_delivered_orders from "
						+ "fapp_order_tracking where driver_boy_user_id = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, boyUserId);
					resultSet = preparedStatement.executeQuery();
					if(resultSet.next()){
						totalDeliveredOrders = resultSet.getInt("total_delivered_orders");
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
    	System.out.println("Total orders: "+totalOrders);
    	System.out.println("Total delivered orders: "+totalDeliveredOrders);
    	if( totalOrders == totalDeliveredOrders){
			return true;
		}else{
			return false;
		}
    }
    
    public static void makeDriverIdle(String boyUserId){
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "UPDATE fapp_delivery_boy set delivery_boy_status_id = 2 where delivery_boy_user_id = ?";
    				try {
						preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUserId);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Driver is now idle");
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
    
    public static boolean makeOrderCompleted(String orderNo){
    	boolean updated= false;
    	if(isAllKitchenDelivered(orderNo)){
    		try {
    			SQL3:{
        		Connection connection = DBConnection.createConnection();
        		PreparedStatement preparedStatement = null;
        		String sql = "UPDATE fapp_orders SET order_status_id=7,delivery_date_time=current_timestamp,"
        				+ " is_message_send='Y' WHERE order_no = ?";
        		try {
        			preparedStatement = connection.prepareStatement(sql);
        			preparedStatement.setString(1, orderNo);
        			int count = preparedStatement.executeUpdate();
        			if(count>0){
        				updated = true;
        				System.out.println("Order completed in main table!");
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
    	if(updated){
    		System.out.println("Order status changed to completed. . .");
    	}
    	return updated;
    }
    
    
    public static JSONObject getOrders(String deliveryBoyUserId, String password)throws JSONException{
    	JSONObject orderId =  new JSONObject();
		JSONArray orderArray =  new JSONArray();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select order_id from fapp_orders "
							   + " where delivery_boy_id = "
							   + "(select delivery_boy_id from fapp_delivery_boy "
							   + " where delivery_boy_user_id= "
                               + " ? and password = ? ) "
                               + " and order_status_id = 4";
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, deliveryBoyUserId);
						preparedStatement.setString(2, password);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject temporder =  new JSONObject();
							temporder.put("orderid", resultSet.getInt("order_id"));
							orderArray.put(temporder);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return orderId.put("orderlist", orderArray);    	
    }
    
    /**
     * WEB SERVICE FOR START TRIP
     * @param orderNo
     * @param dayName
     * @param mealType
     * @param boyId
     * @param gpsAddress
     * @return
     * @throws JSONException 
     */
    public static JSONObject startTripForOrders(ArrayList<StartTripBean> startTripBeanList,
    		String gpsAddress,String latitude, String longitude) throws JSONException{
    	
    	JSONObject jsonAddressStatus = new JSONObject();
		boolean addressedStatus = false,firstassignedStatus = false;
		System.out.println("Delievery list size - "+startTripBeanList.size());
		System.out.println("Data from app - - - - - -");
		
		for(StartTripBean bean : startTripBeanList){
			bean.displayData();
		}
		int dboyID = getBoyId(startTripBeanList.get(0).boyUserId);
		for(int i=0;i<startTripBeanList.size();i++){
			insertDriverAddress(dboyID, gpsAddress, latitude, longitude, startTripBeanList.get(i).orderNo);
		}
		
		try {
				SQL:{
					Connection	connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals "
						+" SET delivery_boy_id=? where subscription_no=? AND "
						+" day_name=? AND meal_type=? AND status IS NULL ";
					try {
						preparedStatement =connection.prepareStatement(sql);
						preparedStatement.setInt(1, dboyID );
						for(StartTripBean startTripBean : startTripBeanList){	
							preparedStatement.setString(2, startTripBean.orderNo);
							preparedStatement.setString(3, startTripBean.dayName);
							preparedStatement.setString(4, startTripBean.mealType);
							preparedStatement.addBatch();
						}
						int[] assigned = preparedStatement.executeBatch();
						for(Integer count : assigned){
							firstassignedStatus = true;
						}
						System.out.println("First updation Boy assigned is = :"+firstassignedStatus);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		ArrayList<StartTripBean> newstartTripBeanList = new ArrayList<StartTripBean>();
		for(int i =0 ;i<startTripBeanList.size() ; i++){
			if(isMealNotDelivered( dboyID , startTripBeanList.get(i).orderNo,
					startTripBeanList.get(i).dayName, startTripBeanList.get(i).mealType)){
				StartTripBean bean =  new StartTripBean();
				bean.status = "Active";
				bean.boyId = dboyID ; 
				bean.orderNo = startTripBeanList.get(i).orderNo;
				bean.dayName = startTripBeanList.get(i).dayName ;
				bean.mealType = startTripBeanList.get(i).mealType ;
				newstartTripBeanList.add(bean);
			}
		}
		
		System.out.println("Undelivered list size - "+newstartTripBeanList.size());
		System.out.println("Data from DB = = = = = =");
		for(StartTripBean bean : newstartTripBeanList){
			bean.displayData();
		}
		
		if(newstartTripBeanList.size()>0){
			
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						String sql ="UPDATE fapp_subscription_meals "
								   +" SET delivery_boy_track_address=?, on_trip = 'Y',is_assigned = 'Y', latitude = ?,longitude = ?   "
								   +" WHERE delivery_boy_id=? AND subscription_no=? AND "
								   +" day_name=? AND meal_type=? AND status IS NULL ";
						try {
							preparedStatement =connection.prepareStatement(sql);
							preparedStatement.setString(1, gpsAddress);
							preparedStatement.setString(2, latitude);
							preparedStatement.setString(3, longitude);
							preparedStatement.setInt(4, dboyID );
							//for(StartTripBean startTripBean : startTripBeanList){
							for(StartTripBean startTripBean : newstartTripBeanList){	
								preparedStatement.setString(5, startTripBean.orderNo);
								preparedStatement.setString(6, startTripBean.dayName);
								preparedStatement.setString(7, startTripBean.mealType);
								preparedStatement.addBatch();
							}
							
							int[] assigned = preparedStatement.executeBatch();
							for(Integer count : assigned){
								addressedStatus = true;
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.println("Address lat long updation Status of undelivered orders::"+addressedStatus);
			if(addressedStatus){
				if(isMessageSend(startTripBeanList)){
					System.out.println("Inside isMessage send Message send already . . .");
				}else{
					String[] boyDetails = new String[2];
					boyDetails = getBoyDetails( dboyID );
					String[] latlong = new String[3];
					latlong = trackMyOrder( newstartTripBeanList.get(0).orderNo );
					for(int i=0;i<newstartTripBeanList.size() ;i++){
						sendMessageForDeliveryBoy(getCustomerMobile(newstartTripBeanList.get(i).orderNo, "SUB"),
								boyDetails[1] , boyDetails[0], latlong[0], latlong[1], newstartTripBeanList.get(i).orderNo);	
					}
					setMessageSendStatus(newstartTripBeanList);
				}
				
				jsonAddressStatus.put("status", addressedStatus);
				return jsonAddressStatus;
			}else{
				jsonAddressStatus.put("status", addressedStatus);
				return jsonAddressStatus;
			}
		}else{
			System.out.println("Active list size->"+newstartTripBeanList.size());
			jsonAddressStatus.put("status", false);
			return jsonAddressStatus;
		}
    }
    
    
    private static boolean setMessageSendStatus( ArrayList<StartTripBean> startTripBeanList ){
    	boolean isSetMessageSendStatus = false;
    	
    	try {
				SQL:{
    					Connection connection = DBConnection.createConnection();
    					PreparedStatement preparedStatement = null;
    					String sql = "UPDATE fapp_subscription_meals "
 							   		+" SET is_message_send='Y'  "
 							   		+" WHERE delivery_boy_id=? AND subscription_no=? AND "
 							   		+" day_name=? AND meal_type=? ";
    					try {
							preparedStatement = connection.prepareStatement(sql);
							//preparedStatement.setInt(1, getBoyId(startTripBeanList.get(0).boyUserId) );
							preparedStatement.setInt(1, startTripBeanList.get(0).boyId);
							for(StartTripBean startTripBean : startTripBeanList){
								preparedStatement.setString(2, startTripBean.orderNo);
								preparedStatement.setString(3, startTripBean.dayName);
								preparedStatement.setString(4, startTripBean.mealType);
								preparedStatement.addBatch();
							}
							System.out.println("update status-->"+preparedStatement);
							int[] updateStatus = preparedStatement.executeBatch();
							for(Integer count : updateStatus){
								isSetMessageSendStatus = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
    			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("Message send status: : "+isSetMessageSendStatus);
    	
    	return isSetMessageSendStatus;
    }
    
    private static boolean isMessageSend( ArrayList<StartTripBean> startTripBeanList ){
    	boolean isMessageSend = false;
    	System.out.println("trip size - >"+startTripBeanList.size());
    	ArrayList<String> ordernolist = new ArrayList<String>();
		ArrayList<String> daynamelist = new ArrayList<String>();
		ArrayList<String> mealnamelist = new ArrayList<String>();
		for(StartTripBean bean : startTripBeanList){
		ordernolist.add("'"+bean.orderNo+"'");
		daynamelist.add("'"+bean.dayName+"'");
		mealnamelist.add("'"+bean.mealType+"'");
		}
		StringBuilder myorderNoBuilder = new StringBuilder();
		String temp = ordernolist.toString();
		String fb = temp.replace("[", "(");
		String bb = fb.replace("]", ")");
		myorderNoBuilder.append(bb);
		String orders = myorderNoBuilder.toString();
		System.out.println("orders--"+orders);
		
		StringBuilder dayNameBuilder = new StringBuilder();
		String temp1 = daynamelist.toString();
		String fb1 = temp1.replace("[", "(");
		String bb1 = fb1.replace("]", ")");
		dayNameBuilder.append(bb1);
		String days = dayNameBuilder.toString();
		System.out.println("days--"+days);
		
		StringBuilder mealNameBuilder = new StringBuilder();
		String temp2 = mealnamelist.toString();
		String fb2 = temp2.replace("[", "(");
		String bb2 = fb2.replace("]", ")");
		mealNameBuilder.append(bb2);
		String meals = mealNameBuilder.toString();
		System.out.println("meals--"+meals);
		ArrayList<String> messageSendStatusList = new ArrayList<String>();
		int dboyID = getBoyId(startTripBeanList.get(0).boyUserId);
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				
    					String sql1 = "SELECT is_message_send from fapp_subscription_meals "
    							+" WHERE delivery_boy_id=? AND subscription_no IN "+orders+"" 
						   		+" AND day_name IN "+days+" AND meal_type IN "+meals+" ";
    				
    					String sql2 = "SELECT is_message_send from fapp_subscription_meals "
    							+" WHERE delivery_boy_id=? AND subscription_no= ? AND "
						   		+" day_name =? AND meal_type = ? ";
    				
    				try {
    					if(startTripBeanList.size()>1){
    						preparedStatement = connection.prepareStatement(sql1);
    						preparedStatement.setInt(1, dboyID );
    						System.out.println("IN sql query called!");
    					}else{
    						preparedStatement = connection.prepareStatement(sql2);
    						preparedStatement.setInt(1, dboyID );
    						preparedStatement.setString(2, startTripBeanList.get(0).orderNo );
    						preparedStatement.setString(3, startTripBeanList.get(0).dayName );
    						preparedStatement.setString(4, startTripBeanList.get(0).mealType );
    						System.out.println("without IN sql query called!");
    					}
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							messageSendStatusList.add(resultSet.getString("is_message_send"));
						}
						System.out.println("is_message_send LIST-"+messageSendStatusList);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(messageSendStatusList.contains("Y")){
    		isMessageSend = true;
    		System.out.println("Message send already - >"+isMessageSend);
    		return isMessageSend;
    	}else{
    		System.out.println("Message not send already - >"+isMessageSend);
    		return isMessageSend;
    	}
    	
    }
    
    private static boolean isMealNotDelivered(
    		Integer boyId,String subNo, String  dayName,String mealName){
    	boolean isNotDelivered = false;
    	String status = null;
    	System.out.println("Checking whether meal is delivered or not!!");
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql ="select status from fapp_subscription_meals "
    						   + " WHERE delivery_boy_id=? AND subscription_no = ? "
						   	   +" AND day_name = ? AND meal_type =?  ";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, boyId); 
						preparedStatement.setString(2, subNo);
						preparedStatement.setString(3, dayName);
						preparedStatement.setString(4, mealName);
					
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							status = resultSet.getString("status");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	if(status==null){
    		isNotDelivered = true;
    	}else{
    		isNotDelivered = false;
    	}
    	System.out.println("Not Deliver - - >"+isNotDelivered);
    	return isNotDelivered;
    }
    
    private static void insertDriverAddress(Integer boyId, String address,String lat, String lng,String orderNo){
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "INSERT INTO fapp_db_address( boy_id, address, delivery_lat, delivery_long, order_no) "
    							+" VALUES (?, ?, ?, ?, ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, boyId);
						preparedStatement.setString(2, address);
						preparedStatement.setString(3, lat);
						preparedStatement.setString(4, lng);
						preparedStatement.setString(5, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							System.out.println("Address inserted!!");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    private static Integer getBoyId(String boyUid ){
		Integer boyId = null ;
		try {
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			SQL:{
					String sql = "SELECT delivery_boy_id FROM fapp_delivery_boy"
								+" WHERE delivery_boy_user_id = ? ";
								
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyUid);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							boyId = resultSet.getInt("delivery_boy_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Retured boy id-->"+boyId);
		return boyId;
	}
    
    private static Integer getBoyIdFromNumber(String name, String mobile ){
		Integer boyId = null ;
		try {
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			SQL:{
					String sql = "SELECT delivery_boy_id FROM fapp_delivery_boy"
								+" WHERE delivery_boy_name = ? and delivery_boy_phn_number=?";
								
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, name);
						preparedStatement.setString(2, mobile);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							boyId = resultSet.getInt("delivery_boy_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Retured boy id-->"+boyId);
		return boyId;
	}
    
    
    private static String[] getBoyDetails(Integer boyUid ){
		String[] boyDetails = new String[2];
		try {
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			SQL:{
					String sql = "SELECT delivery_boy_name,delivery_boy_phn_number,delivery_boy_vehicle_reg_no FROM fapp_delivery_boy"
								+" WHERE delivery_boy_id = ? ";
								
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, boyUid);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							boyDetails[0] = resultSet.getString("delivery_boy_name");
							boyDetails[1] = resultSet.getString("delivery_boy_phn_number");
							//boyDetails[2] = resultSet.getString("delivery_boy_vehicle_reg_no");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return boyDetails;
	}
    
    /**
     * WEB SERVICE FOR DELIVER THE ORDER
     * @param orderNo
     * @param dayName
     * @param mealType
     * @param boyid
     * @return
     * @throws JSONException 
     */
    public static JSONObject orderDelivered(String orderNo,
			String dayName,String mealType,String boyId) throws JSONException{
    	JSONObject jsonDeliveredStatus = new JSONObject();
    	String status = "Delivered";
		boolean deliverdStatus = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals "
							   +" SET delivery_date_time=current_timestamp,status= ? ,delivery_boy_track_address=?, on_trip = 'N'"
							   +" WHERE delivery_boy_id=? AND subscription_no=? AND  "
							   +" day_name=? AND meal_type=? ";
					try {
						preparedStatement =connection.prepareStatement(sql);
						preparedStatement.setString(1, status);
						preparedStatement.setNull(2, Types.NULL);
						preparedStatement.setInt(3, getBoyId(boyId));
						preparedStatement.setString(4, orderNo);
						preparedStatement.setString(5, dayName);
						preparedStatement.setString(6, mealType);
						int delivered = preparedStatement.executeUpdate();
						if(delivered>0){
							deliverdStatus = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		
			/*SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals "
							   +" SET delivery_boy_track_address=? "
							   +" WHERE delivery_boy_id=? AND subscription_no=? AND  "
							   +" day_name=? AND meal_type=? ";
					try {
						preparedStatement =connection.prepareStatement(sql);
						preparedStatement.setNull(1, Types.NULL);
						preparedStatement.setInt(2, getBoyId(boyId));
						preparedStatement.setString(3, orderNo);
						preparedStatement.setString(4, dayName);
						preparedStatement.setString(5, mealType);
						//preparedStatement.setString(5, status);
						System.out.println(preparedStatement);
						int assigned = preparedStatement.executeUpdate();
						if(assigned>0){
							assignedStatus = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}*/
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("Ststus of deliver order by delivery boy is::"+deliverdStatus+" order delivered!");
		if(deliverdStatus){
			if(isSubscriptionOrderEnd(orderNo)){
				sendMessageToMobile(getCustomerMobile(orderNo, "SUB"), 
						orderNo, "orderTime" , 7);
			}
			jsonDeliveredStatus.put("status", deliverdStatus);
			return jsonDeliveredStatus;
		}else{
			jsonDeliveredStatus.put("status", deliverdStatus);
			return jsonDeliveredStatus;
		}
    }
    
    private static boolean isSubscriptionOrderEnd(String orderNo){
    	boolean ended = false;
    	ArrayList<String> statusList = new ArrayList<String>();
    	int noOfSubs = 0,totalStatus = 0;
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT count(subscription_no)AS total_subscription from fapp_subscription_meals "
    						+ " where subscription_no = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							noOfSubs = resultSet.getInt("total_subscription");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
    		}
    	
    	SQL:{
    			PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "SELECT count(subscription_no)AS total_status from fapp_subscription_meals "
							+" where subscription_no =  ? and status IS NOT NULL";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						totalStatus = resultSet.getInt("total_status");
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
    	if(noOfSubs == totalStatus){
    		ended = true;
    	}
    	return ended;
    }

    /* *//**
     * A WEB SERVICE FOR fetching all cuisines
     * @return
     * @throws JSONException
     *//*
    public static JSONObject fetchCuisine(String destlat, String destlong) throws JSONException{
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
    	Double latitude =  Double.parseDouble(destlat);
    	Double longitude =  Double.parseDouble(destlong);
    	
    	JSONObject cuisineList = new JSONObject();
    	JSONArray cuisinesarrayList = new JSONArray();
    	try {
			SQL:{
    		connection = DBConnection.createConnection();
	    		//String sql ="SELECT cuisin_id,cuisin_name from fapp_cuisins";
    		String sql = "select fc.cuisin_id,"
	    				+" fc.cuisin_name, "
	    				+" fc.cuisine_image "
	    				+" from fapp_cuisins fc,"
	    				+" fapp_kitchen_details fkd "
	    				+" where fkd.cuisin_id = fc.cuisin_id "
	    				+" and kitchen_id =?";
    		String sql = "select distinct fkd.cuisin_id,"
						+"fc.cuisin_name"
						 +" from fapp_kitchen_details fkd "
						 +" join sa_area sa "
						 +" on sa.area_id = fkd.area_id "
						 +" join "
						 +" fapp_cuisins fc "
						 +" on fkd.cuisin_id = fc.cuisin_id "
						 +" where fkd.area_id =  "
						 +" (select area_id from sa_area where area_name ILIKE ? )" ;
	    		try {
					preparedStatement = connection.prepareStatement(sql);
					System.out.println("Nearest kitchen id:"+getNearestKitchenId(latitude, longitude));
					Integer kitchenId = getNearestKitchenId(latitude, longitude);
					preparedStatement.setInt(1, kitchenId);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject tempCuisine = new JSONObject();
						tempCuisine.put("cuisineid", resultSet.getInt("cuisin_id"));
						String tempImage  = resultSet.getString("cuisine_image");
						String cuisineImage;
						if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
							cuisineImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}else{
							cuisineImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}
						//String categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						//jobject.put("categoryimage", resultSet.getString("category_image"));
						tempCuisine.put("cuisineimage", cuisineImage);
						tempCuisine.put("cuisinename", resultSet.getString("cuisin_name"));
						tempCuisine.put("kitchenid", kitchenId);
						cuisinesarrayList.put(tempCuisine);
					}
				}  catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	cuisineList.put("cuisinelist", cuisinesarrayList);
    	return cuisineList;
    }*/
    
    public static JSONObject fetchAllCuisineWithItemData(String pincode, String deliveryDay, String mobileNo, String area) throws Exception{
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONObject cuisineList = new JSONObject();
    	JSONArray cuisinesarrayList = new JSONArray();
    	JSONObject allcuisine = new JSONObject();
    	boolean isSingleOrderLunchAvailable = false,isSingleOrderDinnerAvailable = false;
    	String alertMessage = "";int cartCapacity = 0;
    	boolean[] isSingleOrder = new boolean[2];
    	int[] cartValue = new int[2];
    	int lunchCart = 0, dinnerCart = 0 ;
    	
    	try {
			SQL:{
	    		connection = DBConnection.createConnection();
	    		
	        	/*allcuisine.put("cuisineid", 0);
	        	allcuisine.put("cuisineimage", "i.imgur.com/DwZ7bO1.jpg");
	        	allcuisine.put("cuisinename", "All");
	        	allcuisine.put("categorylist", fetchCategoriesOfAllCuisineWithPincode(pincode,connection));
	        	cuisinesarrayList.put(allcuisine);*/
	    		cartCapacity = SingleOrderDAO.getCartCapacity(connection,area);
	    		isSingleOrder = SingleOrderDAO.isSingleOrderAvailable(area, deliveryDay, connection);
	    		//cartValue = SingleOrderDAO.getCartValue(connection, area, deliveryDay);
	    		lunchCart = cartValue[0];
	    		dinnerCart = cartValue[1];
	    		isSingleOrderLunchAvailable = isSingleOrder[0];
	        	isSingleOrderDinnerAvailable = isSingleOrder[1];
	        	
	        	String sql = "SELECT cuisin_id,cuisin_name,cuisine_image FROM fapp_cuisins "
    				+ " WHERE is_active = 'Y' order by cuisin_id  ";
	    		try {
					preparedStatement = connection.prepareStatement(sql);
					
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject tempCuisine = new JSONObject();
						
						tempCuisine.put("cuisineid", resultSet.getInt("cuisin_id"));
						String tempImage  = resultSet.getString("cuisine_image");
						String cuisineImage;
						if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
							cuisineImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}else if(tempImage.startsWith("http://")){
							cuisineImage = tempImage.replace("http://", "");
						}else{
							cuisineImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}
						tempCuisine.put("cuisineimage", cuisineImage);
						tempCuisine.put("cuisinename", resultSet.getString("cuisin_name"));
						tempCuisine.put("categorylist", fetchCategoriesOfCuisineWithPincode(tempCuisine.getInt("cuisineid"),pincode,
								connection,deliveryDay,mobileNo,area));
						//tempCuisine.put("categorylist", fetchCategoriesOfCuisine(tempCuisine.getInt("cuisineid")));
						cuisinesarrayList.put(tempCuisine);
					}
					if(!FetchCuisineDAO.isAllActive()){
						allcuisine.put("cuisineid", cuisinesarrayList.length()+1);
						//http://i.imgur.com/o0HO5pL.png
			        	allcuisine.put("cuisineimage", "i.imgur.com/o0HO5pL.png");
			        	allcuisine.put("cuisinename", "All");
			        	allcuisine.put("categorylist", fetchCategoriesOfAllCuisineWithPincode(pincode,connection,deliveryDay,mobileNo,area));
			        	cuisinesarrayList.put(allcuisine);
					}
					
		        
				}  catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	cuisineList.put("status", "200");
    	cuisineList.put("message", "Our serving menus.");
    	
    	cuisineList.put("isSingleOrderLunchAvailable", isSingleOrderLunchAvailable);
    	if(!isSingleOrderLunchAvailable){
    		alertMessage = "Currently we do not have biker to serve single order.Please add more quantity.";
    		cuisineList.put("lunchAlert", alertMessage);
    	}else{
    		cuisineList.put("lunchAlert", alertMessage);
    	}
    	cuisineList.put("isSingleOrderDinnerAvailable", isSingleOrderDinnerAvailable);
    	if(!isSingleOrderDinnerAvailable){
    		alertMessage = "Currently we do not have biker to serve single order.Please add more quantity.";
    		cuisineList.put("dinnerAlert", alertMessage);
    	}else{
    		cuisineList.put("dinnerAlert", alertMessage);
    	}
    	cuisineList.put("cartCapacity", cartCapacity);
    	cuisineList.put("lunchCartCapacity", lunchCart);
    	cuisineList.put("dinnerCartCapacity", dinnerCart);
    	cuisineList.put("cuisinelist", cuisinesarrayList);
    	return cuisineList;
    }
    
    
   
    
    /**
     * A WEB SERVICE FOR fetching all cuisine list
     * @return
     * @throws JSONException
     */
   /* public static JSONObject fetchCuisineList(String city, String location) throws JSONException{*/
    public static JSONObject fetchCuisineList() throws JSONException{
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
    	
    	JSONObject cuisineList = new JSONObject();
    	JSONArray cuisinesarrayList = new JSONArray();
    	JSONObject allcuisine = new JSONObject();
    	allcuisine.put("cuisineid", 0);
    	allcuisine.put("cuisineimage", "i.imgur.com/DwZ7bO1.jpg");
    	allcuisine.put("cuisinename", "All");
    	allcuisine.put("categorylist", fetchCategoriesOfAllCuisine());
    	cuisinesarrayList.put(allcuisine);
    	try {
			SQL:{
    		connection = DBConnection.createConnection();
	    		/*String sql =" select distinct fkd.cuisin_id,"
	    				   	+" fc.cuisin_name,fc.cuisine_image"
		    				+" from fapp_kitchen_details fkd "
		    				+" join sa_area sa "
		    				+" on sa.area_id = fkd.area_id "
		    				+" join "
		    				+" fapp_cuisins fc "
		    				+" on fkd.cuisin_id = fc.cuisin_id "
		    				+"  where fkd.area_id =  "
		    				+" (select area_id from sa_area where area_name ILIKE ? and city_id = "
		    				+" (select city_id from sa_city where city_name ILIKE ?))";*/
    		String sql = "SELECT cuisin_id,cuisin_name,cuisine_image FROM fapp_cuisins "
    				+ " WHERE is_active = 'Y' order by cuisin_id  ";
	    		try {
					preparedStatement = connection.prepareStatement(sql);
					
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject tempCuisine = new JSONObject();
						
						tempCuisine.put("cuisineid", resultSet.getInt("cuisin_id"));
						String tempImage  = resultSet.getString("cuisine_image");
						String cuisineImage;
						if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
							cuisineImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}else if(tempImage.startsWith("http://")){
							cuisineImage = tempImage.replace("http://", "");
						}else{
							cuisineImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}
						
						//String categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						//jobject.put("categoryimage", resultSet.getString("category_image"));
						tempCuisine.put("cuisineimage", cuisineImage);
						tempCuisine.put("cuisinename", resultSet.getString("cuisin_name"));
						tempCuisine.put("categorylist", fetchCategoriesOfCuisine(tempCuisine.getInt("cuisineid")));
						cuisinesarrayList.put(tempCuisine);
					}
				}  catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	
    	System.out.println("Cuisine List size: "+cuisinesarrayList.length());
    	cuisineList.put("cuisinelist", cuisinesarrayList);
    	return cuisineList;
    }
    
    public static JSONArray fetchCategoriesOfCuisineWithPincode(int CuisineID, String pincode, Connection connection,
    		String deliveryDay, String mobileNo, String area){
    	JSONArray categoryJSONArray = new JSONArray();
    	try {
			SQL:{
    				//Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen "
    						+ " where kitchen_cuisine_id = ? and serving_areas like ? order by category_id ";
    				/*String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen "
    						+ " where kitchen_cuisine_id = ? and serving_zipcodes like ? order by category_id ";*/
    				/*String sql = "select category_id,category_name from food_category "
    						+ " where category_price IS NULL AND cuisine_id = ?";*/
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, CuisineID);
						preparedStatement.setString(2, "%"+area+"%");
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject categoryObject = new JSONObject();
							String categoryId = resultSet.getString("category_id");
							categoryObject.put("categoryid", categoryId);
							categoryObject.put("categoryname", resultSet.getString("category_name"));
							String mealTypeLunch = resultSet.getString("is_lunch");
							String mealTypeDinner = resultSet.getString("is_dinner");
							if(mealTypeLunch.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "LUNCH");
							}
								
							if(mealTypeDinner.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "DINNER");
							}
							
							if(mealTypeLunch.equalsIgnoreCase("Y") && mealTypeDinner.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "BOTH");
							}
							categoryObject.put("itemlist", fetchItemsWrtCategory( Integer.valueOf(categoryId), pincode, connection,deliveryDay, mobileNo, area));
							categoryJSONArray.put(categoryObject);
						}
					} catch (Exception e) {
						// TODO: handle exception
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
    	System.out.println("kitchen categories ends");
		
    	return categoryJSONArray;
    }
    
    
    public static JSONArray fetchCategoriesOfCuisine(int CuisineID){
    	JSONArray categoryJSONArray = new JSONArray();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select category_id,category_name from food_category "
    						+ " where category_price IS NULL AND cuisine_id = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, CuisineID);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject categoryObject = new JSONObject();
							String categoryId = resultSet.getString("category_id");
							categoryObject.put("categoryid", categoryId);
							categoryObject.put("categoryname", resultSet.getString("category_name"));
							categoryJSONArray.put(categoryObject);
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
    	return categoryJSONArray;
    }
    
    public static JSONArray fetchCategoriesOfAllCuisineWithPincode(String pincode,Connection connection,
    		String deliveryDay,String mobileNo, String area){
    	JSONArray categoryJSONArray = new JSONArray();
    	try {
			SQL:{
    				//Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select distinct category_id,category_name,is_lunch,is_dinner from vw_category_kitchen order by category_id ";
    						
    				/*String sql = "select category_id,category_name from food_category "
    						+ " where category_price IS NULL order by category_id";*/
    				try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject categoryObject = new JSONObject();
							String categoryId = resultSet.getString("category_id");
							categoryObject.put("categoryid", categoryId);
							categoryObject.put("categoryname", resultSet.getString("category_name"));
							String mealTypeLunch = resultSet.getString("is_lunch");
							String mealTypeDinner = resultSet.getString("is_dinner");
							if(mealTypeLunch.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "LUNCH");
							}
								
							if(mealTypeDinner.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "DINNER");
							}
							
							if(mealTypeLunch.equalsIgnoreCase("Y") && mealTypeDinner.equalsIgnoreCase("Y")){
								categoryObject.put("mealtype", "BOTH");
							}
							categoryObject.put("itemlist", fetchItemsWrtCategory( Integer.valueOf(categoryId), pincode ,connection, deliveryDay, 
									mobileNo, area));
							categoryJSONArray.put(categoryObject);
						}
					} catch (Exception e) {
						// TODO: handle exception
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
    	return categoryJSONArray;
    }
    
    public static JSONArray fetchCategoriesOfAllCuisine(){
    	JSONArray categoryJSONArray = new JSONArray();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select category_id,category_name from food_category "
    						+ " where category_price IS NULL order by category_id";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject categoryObject = new JSONObject();
							categoryObject.put("categoryid", resultSet.getString("category_id"));
							categoryObject.put("categoryname", resultSet.getString("category_name"));
							categoryJSONArray.put(categoryObject);
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
    	return categoryJSONArray;
    }
    
    /*public static JSONArray fetchCategoriesOfAllCuisineWithPincode(String pincode){
    	JSONArray categoryJSONArray = new JSONArray();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select category_id,category_name from food_category "
    						+ " where category_price IS NULL order by category_id";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject categoryObject = new JSONObject();
							String categoryId = resultSet.getString("category_id");
							categoryObject.put("categoryid", categoryId);
							categoryObject.put("categoryname", resultSet.getString("category_name"));
							categoryObject.put("itemlist", fetchItemsWrtCategory( Integer.valueOf(categoryId), pincode));
							categoryJSONArray.put(categoryObject);
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
    	return categoryJSONArray;
    }*/
    
    
    public static Integer getNearestKitchenId(Double destlat, Double destlong){
    	Integer kitchenId = 0;
    	try {
    		Connection connection = DBConnection.createConnection();
			SQL:{
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql ="SELECT kitchen_name,kitchen_id," 
								+" 3956 * 2 * "
								+" ASIN(SQRT( "
								+" POWER( SIN((? - abs(dest.latitude)) * pi()/180 / 2),2) "
								+" + COS(? * pi()/180 )  "
								+" * COS(abs(dest.latitude) * pi()/180)  "
								+" * POWER(SIN((? - abs(dest.longitude)) * pi()/180 / 2), 2) )) "
								+"  as distance "
								+" FROM fapp_kitchen dest "
								+" order by distance asc LIMIT 1";
    				try {
						preparedStatement= connection.prepareStatement(sql);
						preparedStatement.setDouble(1, destlat);
						preparedStatement.setDouble(2, destlat);
						preparedStatement.setDouble(3, destlong);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							kitchenId = resultSet.getInt("kitchen_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return kitchenId;
    }
  
    /**
     * Kitchen id and their respective cuisines and categories
     * @param locationId
     * @return
     */
    /*private static ArrayList<KitchenDetailsBean> getKitchenDetails(Integer locationId){*/
    private static ArrayList<KitchenDetailsBean> getKitchenDetails(String pincode){
    	ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = new ArrayList<KitchenDetailsBean>();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	try {
    		connection = DBConnection.createConnection();
			SQL:{
    				/*String sql = "select distinct fk.kitchen_id,"
								+" fks.kitchen_cuisine_id,"
								+" fks.kitchen_category_id"
								+" from fapp_kitchen fk"
								+" join sa_area sa"
								+" on sa.area_id = fk.area_id"
								+" join fapp_kitchen_stock fks"
								+" on fk.kitchen_id  =  fks.kitchen_id"
								+" where  fk.is_active = 'Y' "
								+" and sa.area_id = ? "
								+" order by fk.kitchen_id";*/
    				String sql = "select distinct "
								+" fk.kitchen_id,"
								+" fks.kitchen_cuisine_id,"
								+" fks.kitchen_category_id,"
								+" fk.area_id"
								+" from fapp_kitchen fk"
								+" join fapp_kitchen_stock fks"
								+" on fk.kitchen_id  =  fks.kitchen_id"
								+" where  fk.is_active = 'Y'"
								+" and serving_zipcodes LIKE ?";		
    				try {
						preparedStatement = connection.prepareStatement(sql);
						//preparedStatement.setInt(1, locationId);
						//if(pincode!=null){
							preparedStatement.setString(1, "%"+pincode+"%");
						//}
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							KitchenDetailsBean detailsBean = new KitchenDetailsBean();
							detailsBean.setCuisineId(resultSet.getInt("kitchen_cuisine_id"));
							detailsBean.setCategoryId(resultSet.getInt("kitchen_category_id"));
							detailsBean.setKitchenId(resultSet.getInt("kitchen_id"));
						
							kitchenDetailsBeanList.add(detailsBean);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						
						if(preparedStatement!=null){
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
    	System.out.println("kitchenDetailsBeanList size ->"+kitchenDetailsBeanList.size());
    	
    	return kitchenDetailsBeanList;
    }
    
    private static Integer getNearestKitchenId(ArrayList<OrderItems> orderItemList ,  Integer locationId){
    	Connection connection = null;
    	try {
			connection = DBConnection.createConnection();
			SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "select fk.kitchen_id"
									+" from fapp_kitchen fk "
									+" join sa_area sa "
								 	+" on sa.area_id = fk.area_id "
									+" join fapp_kitchen_stock fks "
									+" on fk.kitchen_id  =  fks.kitchen_id "
									+" where fks.kitchen_cuisine_id =  ? "
									+" and fks.kitchen_category_id IN (44,42,47) " 
									+" and fk.is_active = 'Y' "
									+" and sa.area_id = 39"; 
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return 1;
    }
    
    public static ArrayList<Integer> getkitchenStock(Integer kitchenId, ArrayList<OrderItems> orderItemList){
    	ArrayList<Integer> stockInKitchen =  new ArrayList<Integer>();
    	
    	Connection connection = null;
    	
    	if(isKitchenDealsWithCuisine(orderItemList, kitchenId)){
	    	try {
				SQL:{
	    				connection = DBConnection.createConnection();
	    				PreparedStatement preparedStatement = null;
	    				ResultSet resultSet = null;
	    				String sql= "select category_stock from fapp_kitchen_stock "
									+" where kitchen_cuisine_id =  ?"
									+" and kitchen_category_id = ?"
									+" and kitchen_id = ?";
	    				try {
	    					preparedStatement =  connection.prepareStatement(sql);
	    						for(OrderItems items : orderItemList){
									preparedStatement.setInt(1, items.cuisineId);
									preparedStatement.setInt(2, items.categoryId);
									preparedStatement.setInt(3, kitchenId);
								
									resultSet = preparedStatement.executeQuery();
									if (resultSet.next()) {
										stockInKitchen.add(resultSet.getInt("category_stock"));
									}
								}
								
								System.out.println("Stock list::"+stockInKitchen);
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("ERROR DUE TO:"+e.getMessage());
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
	    		}
			} catch (Exception e) {
				// TODO: handle exception
			}
	    }else{
	    	for(int i=0;i<orderItemList.size();i++){
	    		stockInKitchen.add(0);
	    	}
	    }
    	return stockInKitchen;
    }
    
   
    private static Boolean isKitchenDealsWithCuisine( ArrayList<OrderItems> orderItemList ,Integer kitchenId){
    	
    	ArrayList<Integer> kitchenCuisineList =  new ArrayList<Integer>();
    	
    	Boolean kitchenDealsWithCuisine =  false;
    	
    	Connection connection = null;
    	
    	try {
				SQL:{
    				connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql="select DISTINCT kitchen_cuisine_id from fapp_kitchen_stock "
							+" where  kitchen_id = ?";
							
    				try {
	    					preparedStatement =  connection.prepareStatement(sql);
	    					preparedStatement.setInt(1, kitchenId);
								
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								kitchenCuisineList.add(resultSet.getInt("kitchen_cuisine_id"));
							}
							System.out.println("Kitchen cuisine list::"+kitchenCuisineList);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR DUE TO:"+e.getMessage());
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	for(int i=0 ; i<orderItemList.size() ; i++){
    	
    		if( kitchenCuisineList.contains(orderItemList.get(i).cuisineId)){
    			kitchenDealsWithCuisine = true;
    		}else{
    			kitchenDealsWithCuisine = false;
    		}
    	}
    	System.out.println("kitchen Deals With Cuisine::"+kitchenDealsWithCuisine);
    	return kitchenDealsWithCuisine;
    }
    
    private static ArrayList<Integer> getUserQuantity(ArrayList<OrderItems> orderItemList){
    	ArrayList<Integer> userQuantityList = new ArrayList<Integer>();
    	for(OrderItems quantity : orderItemList){
    		userQuantityList.add(quantity.quantity);
    	}
    	System.out.println("User quantity list->"+userQuantityList);
    	return userQuantityList;
    }
    
    
    private static Boolean isExcessQuantity(ArrayList<Integer> userQuantityList, ArrayList<Integer> kitchenQuantityList) {
		Boolean excessQuantity = false;
		for(int i=0; i<userQuantityList.size() &&  i<kitchenQuantityList.size(); i++){
			if(userQuantityList.get(i) > kitchenQuantityList.get(i)){
				excessQuantity = true;
			}
		}
		System.out.println("Excess stock->"+excessQuantity);
		return excessQuantity;
	}
    
    /**
     * A WEB SERVICE for fetching all location list
     * @return
     * @throws JSONException 
     * @throws Exception
     */
    public static JSONObject fetchlocation() throws JSONException {
    	JSONArray jArrayCity = new JSONArray();
		JSONObject cityList =  new JSONObject();
		Connection connection = null;
		try {
			 
	    	 PreparedStatement preparedStatement = null;
			 ResultSet resultSet = null;
			 SQLCITY:{
					try {	
				    		connection = DBConnection.createConnection();
				    		String sqlCityQuery ="select city_id,city_name from sa_city where is_active='Y'";		    		
							preparedStatement = connection.prepareStatement(sqlCityQuery);
							resultSet = preparedStatement.executeQuery();
							while(resultSet.next()){
								JSONObject jsonObjectcity = new JSONObject();
								jsonObjectcity.put("cityid",resultSet.getString("city_id"));
								jsonObjectcity.put("cityname",resultSet.getString("city_name"));
								jsonObjectcity.put("arealist", getLocationList(jsonObjectcity.getInt("cityid"), false ));
								jArrayCity.put(jsonObjectcity);
							}
			    	}catch(Exception e){
			    		System.out.println("Error due to:"+e.getMessage());
			    	}finally{
			    		if(connection!=null){
							connection.close();
						}
			    	}
				}
		} catch (Exception e) {
			System.out.println("Error due to:"+e.getMessage());
		}
    	
		System.out.println("Fetch location output length--"+jArrayCity.length());
		cityList.put("citylist", jArrayCity);
		return cityList;
    }
    
    /**
     * A WEB SERVICE for fetching all location list of particular city
     * @return
     * @throws JSONException 
     * @throws Exception
     */
    public static JSONObject fetchServinglocation() throws JSONException {
    	JSONArray jArrayCity = new JSONArray();
		JSONObject cityList =  new JSONObject();
		Connection connection = null;
		try {
			 
	    	 PreparedStatement preparedStatement = null;
			 ResultSet resultSet = null;
			 SQLCITY:{
					try {	
				    		connection = DBConnection.createConnection();
				    		String sqlCityQuery ="select city_id,city_name from sa_city where is_active='Y'";		    		
							preparedStatement = connection.prepareStatement(sqlCityQuery);
							resultSet = preparedStatement.executeQuery();
							while(resultSet.next()){
								JSONObject jsonObjectcity = new JSONObject();
								jsonObjectcity.put("cityid",resultSet.getString("city_id"));
								jsonObjectcity.put("cityname",resultSet.getString("city_name"));
								jsonObjectcity.put("arealist", getLocationList( jsonObjectcity.getInt("cityid") , true));
								jArrayCity.put(jsonObjectcity);
							}
			    	}catch(Exception e){
			    		System.out.println("Error due to:"+e.getMessage());
			    	}finally{
			    		if(connection!=null){
							connection.close();
						}
			    	}
				}
		} catch (Exception e) {
			System.out.println("Error due to:"+e.getMessage());
		}
    	
		System.out.println("Fetch location output length--"+jArrayCity.length());
		cityList.put("citylist", jArrayCity);
		return cityList;
    }
    
    public static JSONObject getLocationName() throws JSONException{
    	JSONObject locationNameObj = new JSONObject();
    	JSONArray locationArray = new JSONArray();
    	TreeMap<String, String> kitchenServingAreas = FetchLocationDAO.kitchenServingAreas();
    	Set set = kitchenServingAreas.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
           Map.Entry me = (Map.Entry)iterator.next();
          JSONObject zip = new JSONObject();
	   		zip.put("areaname", me.getKey());
	   		zip.put("zipcode", "");
	   		locationArray.put(zip);
        }
    	/*for (TreeMap.Entry<String, String> me : kitchenServingAreas.entrySet()){ 
	        JSONObject zip = new JSONObject();
    		zip.put("areaname", me.getKey());
    		zip.put("zipcode", "");
    		locationArray.put(zip);
    	}*/
	
	locationNameObj.put("arealist", locationArray);
	
	//locationNameObj.put("arealist", FetchLocationDAO.fetchZipWithKitchenListArray(locationMap));
	return locationNameObj;
    	/*try {
			SQL:{
    			 Connection connection = DBConnection.createConnection();
    			 PreparedStatement preparedStatement = null;
    			 ResultSet resultSet = null;
    			 String sql = "select serving_areas from fapp_kitchen where is_active='Y'";
    			// String sql = "select locality_name,zip_code from sa_zipcode where is_delete='N' and is_active='Y'";
    			 //String sql = "select locality_name,zip_code from sa_zipcode where locality_name ILIKE ?'n%' and is_delete='N' and is_active='Y'";
    			 try {
					preparedStatement = connection.prepareStatement(sql);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						/*JSONObject name = new JSONObject();
						name.put("areaname", resultSet.getString("locality_name"));
						name.put("zipcode", resultSet.getString("zip_code"));
						locationArray.put(name);
						locationMap.put(resultSet.getString("zip_code"), 
								resultSet.getString("locality_name").toUpperCase());
						servingAreas.add(resultSet.getString("serving_areas"));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	for(String str : servingAreas){
    		String[] slot = str.split("\\$");
	    	System.out.println("Slot length:: "+slot.length);
	    	for(int i=0;i<slot.length;i++){
	    		System.out.println("SlOT:: "+slot[i]);
	    		JSONObject name = new JSONObject();
		    	name.put("areaname", slot[i]);
				name.put("zipcode", "");
				locationArray.put(name);
				System.out.println(name);
	    	}
		}*/
    	
    }
    
    public static JSONObject getLocationNames(String areaName) throws JSONException{
    	JSONObject locationNameObj = new JSONObject();
    	JSONArray locationArray = new JSONArray();
    	try {
			SQL:{
    			 Connection connection = DBConnection.createConnection();
    			 PreparedStatement preparedStatement = null;
    			 ResultSet resultSet = null;
    			 String sql = "select locality_name,zip_code from sa_zipcode where locality_name ILIKE ? and is_delete='N' and is_active='Y'";
    			 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, areaName+"%");
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject name = new JSONObject();
						name.put("areaname", resultSet.getString("locality_name"));
						name.put("zipcode", resultSet.getString("zip_code"));
						locationArray.put(name);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	locationNameObj.put("arealist", locationArray);
    	return locationNameObj;
    }
    
    public static JSONObject getLocationList(Integer cityId , boolean withAreaName) throws JSONException {
    	
		JSONObject jsonObject = new JSONObject();
		JSONArray jArrayLocation = new JSONArray();
		try {
				Connection connection = null;
		    	PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				SQLLOCATION:{
					try {	
				    		connection = DBConnection.createConnection();
				    		String sqlLocationQuery = "select area_id,area_name from sa_area where is_active = 'Y' and city_id=?";		    		
							preparedStatement = connection.prepareStatement(sqlLocationQuery);
							preparedStatement.setInt(1, cityId);
							resultSet = preparedStatement.executeQuery();
							while(resultSet.next()){
								JSONObject jsonlocObject = new JSONObject();
								jsonlocObject.put("areaid",resultSet.getString("area_id"));
								jsonlocObject.put("areaname",resultSet.getString("area_name"));
								//System.out.println("Location Name is:::"+jsonlocObject.getString("areaname"));
								if(withAreaName){
									jsonlocObject.put("zipcodes", getServingZipsWithName(jsonlocObject.getInt("areaid")));
								}else{
									jsonlocObject.put("zipcodes", getServingAreaCodes(jsonlocObject.getInt("areaid")));
								}
								
								jArrayLocation.put(jsonlocObject);
							}
			    	}catch(Exception e){
			    		System.out.println("Error Due to:"+e.getMessage());
			    	}finally{
			    		if(connection!=null){
							connection.close();
						}
			    	}
				}
				
		} catch (Exception e) {
			System.out.println("Error Due to:"+e.getMessage());
		}
    	
			if(jArrayLocation.length()!=0){
				jsonObject.put("locationlist", jArrayLocation);
			}else{
				JSONObject tempObj = new JSONObject();
				tempObj.put("areaid", "");
				tempObj.put("areaname", "");
				jArrayLocation.put(tempObj);
				jsonObject.put("locationlist", jArrayLocation);
			}
			
		return jsonObject;
    }
    
    private static JSONArray getServingAreaCodes(Integer areaId) throws JSONException{
    	JSONArray servingCodeList = new JSONArray();
    	String areacodes = "";
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select distinct serving_zipcodes from fapp_kitchen where "
    						+ " area_id = ? and serving_zipcodes IS NOT NULL";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, areaId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							areacodes = resultSet.getString("serving_zipcodes");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	//System.out.println("Zip codes for area id "+areaId+" are: "+areacodes);
    	String[] zipcodeList = areacodes.split("/");
    	for(int i=0;i<zipcodeList.length;i++){
    		JSONObject zip = new JSONObject();
    		zip.put("zipcode", zipcodeList[i]);
    		servingCodeList.put(zip);
    	}
    	return servingCodeList;
    }
   
    private static JSONArray getServingZipsWithName(Integer areaId) throws JSONException{
    	JSONArray servingCodeList = new JSONArray();
    	String areacodes = "";
    	Map<String, String> locationMap = new HashMap<String, String>();
		
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select zip_code,locality_name from sa_zipcode "
    						+ "where area_id = ? and is_delete = 'N' and is_active='Y'";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, areaId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject zip = new JSONObject();
				    		zip.put("zipcode", resultSet.getString("zip_code"));
				    		zip.put("servingarea", resultSet.getString("locality_name").toUpperCase());
				    		servingCodeList.put(zip);
							/*locationMap.put(resultSet.getString("zip_code"), 
									resultSet.getString("locality_name").toUpperCase());*/
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	//servingCodeList = FetchLocationDAO.fetchZipWithKitchenListArray(locationMap);
    	return servingCodeList;
    }
    
    
    public static JSONArray getCuisineIdList(Integer orderId) throws JSONException{
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONArray jArraycuisineid = new JSONArray();
		try {
			
			SQL:{
				try {	
			    		connection = DBConnection.createConnection();
			    		String sqlLocationQuery = "select cuisine_id from fapp_order_item_details where order_id =?";		    		
						preparedStatement = connection.prepareStatement(sqlLocationQuery);
						preparedStatement.setInt(1, orderId);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject jsoncuisineid = new JSONObject();
							jsoncuisineid.put("cuisineid",resultSet.getString("cuisine_id"));
							jArraycuisineid.put(jsoncuisineid);
						}
		    	} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
			}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
		return jArraycuisineid;
    }
    
   
    /**
     * 
     * Method to fetch item and sub item list 
     */
	public static JSONObject itemDetails(Integer itemId) throws JSONException  {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	
    	JSONObject jsonObjectItem = new JSONObject();
    	//JSONObject tempJSONobject = new  JSONObject();
    	JSONArray jsonArraySubItem = new JSONArray();
    	
    	String itemSqlQuery =  "SELECT item_name,item_banner FROM food_items WHERE item_id=?";
    	String subitemSqlQuery = "select "
				    			+" fsi.sub_item_id, "
				    			+" fsi.sub_item_name, "
				    			+" fsi.sub_item_image, "
				    			+" fsi.sub_item_description "
				    			+" from "
				    			+" food_items fi, "
				    			+" food_sub_items fsi "
				    			+" where "
				    			+" fi.item_id = fsi.item_id "
				    			+" and "
				    			+" fi.item_id="+itemId+" ";
    	SQlItem:{
	    	try {	
		    		connection = DBConnection.createConnection();
		    				    		
					preparedStatement = connection.prepareStatement(itemSqlQuery);
					preparedStatement.setInt(1, itemId);
					
					resultSet = preparedStatement.executeQuery();
					while(resultSet.next()){
				    	jsonObjectItem.put("itm_name",resultSet.getString("item_name"));
				    	jsonObjectItem.put("item_banner",resultSet.getString("item_image"));
					}
	    	}catch(Exception e){
	    		System.out.println(e);
	    	}
    	}
    	SQlsubItem:{
	    	try {	
		    		connection = DBConnection.createConnection();
		    		preparedStatement = connection.prepareStatement(subitemSqlQuery);
					resultSet = preparedStatement.executeQuery();
					while(resultSet.next()){
						JSONObject subitemJsonObject = new JSONObject();
						subitemJsonObject.put("id",resultSet.getString("sub_item_id"));
						subitemJsonObject.put("name",resultSet.getString("sub_item_name"));
						subitemJsonObject.put("description",resultSet.getString("sub_item_description"));
						subitemJsonObject.put("image",resultSet.getString("sub_item_image"));
						jsonArraySubItem.put(subitemJsonObject);
					}
	    	}catch(Exception e){
	    		System.out.println(e);
	    	}
    	}
    	jsonObjectItem.put("subitem_list", jsonArraySubItem);	
    	return jsonObjectItem;
    }
  
    
    /**
     * This method is useful to fetch category list
     * @return JSon object
     * @throws JSONException 
     * @throws Exception
     *//*
    public static JSONObject selectCategoryName(Integer cuisineId, Integer kitchenid) throws Exception {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONArray jArray = new JSONArray();
		JSONObject categoryNameList =  new JSONObject();
		
		String sqlQuery = "select fc.category_id "
						  +" ,fc.category_name"
						  +" ,fc.category_image"
						  +" ,(select sum(item_price) from food_items where category_id=fc.category_id  ) As category_price "
						  + " ,fks.category_stock"
						  + " ,fks.cost_price "
						  +" from  food_category fc,		"		
						  +" fapp_kitchen_stock fks "
						  +" where  "
						  +" fks.kitchen_category_id = fc.category_id "	
						  +" and "
						  +" fks.kitchen_cuisine_id = ?  "
						  +" and fks.kitchen_id = ? "
						  +" GROUP BY fc.category_id "
						  + " ,fks.category_stock"
						  + " ,fks.cost_price" ;
    	try {
    		
    		connection = DBConnection.createConnection();
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, cuisineId);
			preparedStatement.setInt(2, kitchenid);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				JSONObject jobject =  new JSONObject();
				jobject.put("categoryid",resultSet.getString("category_id"));
				jobject.put("categorydescription",  " "getCategoryDescription(jobject.getInt("categoryid")));
				String tempImage  = resultSet.getString("category_image");
				//String categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
				String categoryImage;
				if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
					 categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
				}else{
					 categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
				}
				//String categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
				//jobject.put("categoryimage", resultSet.getString("category_image"));
				jobject.put("categoryimage", categoryImage);
		    	jobject.put("categoryname",resultSet.getString("category_name"));
		    	jobject.put("stock", resultSet.getString("category_stock"));
		    	//jobject.put("stock", getStock(kitchenid, cuisineId, jobject.getInt("categoryid")));
		    	jobject.put("categoryprice", resultSet.getDouble("cost_price"));
		    	jArray.put(jobject);
			}
		} catch (SQLException sqle) {
			throw sqle;
		}catch (Exception e) {
            // TODO Auto-generated catch block
            if (connection != null) {
                connection.close();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    	categoryNameList.put("Categories", jArray);
    	return categoryNameList;
    }*/
   
	
	public static JSONObject fetchAllCategories(String city, String location,String pincode) throws JSONException{
		JSONObject fetchAllCategory = new JSONObject();
		JSONArray jArray = new JSONArray();
		if(pincode.length()!=0){
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						/*String sql ="SELECT * FROM vw_category_from_kitchen_details WHERE area_id = "
								   +" (select area_id from sa_area where area_name ILIKE ? and city_id = "
			    				   +" (select city_id from sa_city where city_name ILIKE ?))";*/
						String sql = "SELECT * FROM vw_category_from_kitchen_details WHERE serving_zipcodes LIKE ? ";
						try {
							preparedStatement = connection.prepareStatement(sql);
							//preparedStatement.setString(1, location);
							//preparedStatement.setString(2, city);
							preparedStatement.setString(1, "%"+pincode+"%");
							int serialNo = 0;
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject jobject =  new JSONObject();
								jobject.put("serial", serialNo);
								jobject.put("kitchenid", resultSet.getString("kitchen_id"));
								jobject.put("cuisineid", resultSet.getString("kitchen_cuisine_id"));
								jobject.put("categoryid",resultSet.getString("category_id"));
								jobject.put("categorydescription",  getCategoryDescription(jobject.getInt("categoryid")));
								String tempImage  = resultSet.getString("category_image");
								String categoryImage;
								if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
									 categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}else if(tempImage.startsWith("http://")){
									categoryImage = tempImage.replace("http://", "");
								}else{
									 categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}
								
								jobject.put("categoryimage", categoryImage);
						    	jobject.put("categoryname",resultSet.getString("category_name"));
						    	jobject.put("stock", resultSet.getString("category_stock"));
						    	if(  resultSet.getString("lunch_stock") != null){
						    		jobject.put("lunchstock", resultSet.getString("lunch_stock"));
						    	}else{
						    		jobject.put("lunchstock", "0");
						    	}
						    	if(resultSet.getString("dinner_stock")!=null ){
						    		jobject.put("dinnerstock", resultSet.getString("dinner_stock"));
						    	}else{
						    		jobject.put("dinnerstock", "0");
						    	}
						    	
						    	jobject.put("categoryprice", resultSet.getDouble("cost_price"));
						    	jArray.put(jobject);
						    	serialNo++;
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			fetchAllCategory.put("Categories", jArray);
	    	return fetchAllCategory;
		}else{
			fetchAllCategory.put("Categories", jArray);
	    	return fetchAllCategory;
		}
		
		
	}
	
	public static JSONArray fetchItemsWrtCategory(int categoryId,String pincode,Connection connection,
			String deliveryDay, String mobileNo, String area) throws JSONException{
		//JSONObject fetchAllCategory = new JSONObject();
		JSONArray jArray = new JSONArray();
		/*if( !(categoryId == 78 || categoryId == 79) ){*/
			//boolean isNewUser = FetchCuisineDAO.isNewUser(mobileNo);
			try {
				SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = null;
						/*String sql ="SELECT * FROM vw_category_from_kitchen_details WHERE area_id = "
								   +" (select area_id from sa_area where area_name ILIKE ? and city_id = "
			    				   +" (select city_id from sa_city where city_name ILIKE ?))";*/
						if(deliveryDay.equalsIgnoreCase("TODAY")){
							sql ="SELECT distinct kitchen_cuisine_id,category_id,item_name,item_code,"
									+" item_price,item_description,item_image "
									+" FROM vw_kitchen_items "
									+" WHERE category_id=? and serving_areas LIKE ? and is_active='Y' "
									+" order by item_code"	;
						}else{
							sql ="SELECT distinct kitchen_cuisine_id,category_id,item_name,item_code,"
									+" item_price,item_description,item_image "
									+" FROM vw_kitchen_items "
									+" WHERE category_id=? and serving_areas LIKE ? and is_active_tomorrow='Y' "
									+" order by item_code"	;
						}
							
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, categoryId);
							preparedStatement.setString(2, "%"+area+"%");
							int serialNo = 0;
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject jobject =  new JSONObject();
								boolean isBikerAvailableForLunch = false,isBikerAvailableForDinner = false;
								String bikerAvailableKitchensForLunch,bikerAvailableKitchensForDinner,stock,dinnerStock;
								jobject.put("serial", serialNo);
								String itemCode = resultSet.getString("item_code");
								//do as usual show all items
								jobject.put("itemcode", itemCode);
								//jobject.put("singleOrders", resultSet.getInt("no_of_single_order"));
								jobject.put("cuisineid", resultSet.getString("kitchen_cuisine_id"));
								jobject.put("categoryid",resultSet.getString("category_id"));
								jobject.put("categorydescription", resultSet.getString("item_description") );
								String tempImage  = resultSet.getString("item_image");
								String categoryImage;
								if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
									categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}else if(tempImage.startsWith("http://")){
									categoryImage = tempImage.replace("http://", "");
								}else{
									categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}

								jobject.put("categoryimage", categoryImage);
								jobject.put("categoryname",resultSet.getString("item_name"));

								//isBikerAvailableForLunch = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, true);
								//isBikerAvailableForDinner = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, false);
								bikerAvailableKitchensForLunch = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, true, area);
								bikerAvailableKitchensForDinner = PlaceOrderDAO.findBikerAvailableKitchens(itemCode, connection, deliveryDay, false, area);

								stock = getItemStock(pincode, itemCode, connection, "LUNCH", deliveryDay,bikerAvailableKitchensForLunch, area);
								dinnerStock = getItemStock(pincode, itemCode, connection, "DINNER", deliveryDay,bikerAvailableKitchensForDinner, area);

								jobject.put("stock", stock);
								jobject.put("lunchstock", stock);
								isBikerAvailableForLunch = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, true, area);
								jobject.put("availableBikerForLunch", isBikerAvailableForLunch);

								jobject.put("dinnerstock", dinnerStock);
								isBikerAvailableForDinner = PlaceOrderDAO.isServable(itemCode, connection, deliveryDay, false, area);
								jobject.put("availableBikerForDinner", isBikerAvailableForDinner);

								if(isBikerAvailableForLunch && isBikerAvailableForDinner){
									jobject.put("available",true);
								}else{
									jobject.put("available",false);
								}

								jobject.put("mealtype", getLunchOrDinner(pincode, itemCode,connection));
								jobject.put("categoryprice", resultSet.getDouble("item_price"));
								jArray.put(jobject);
								serialNo++;
							}
							
						} catch (Exception e) {
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
				e.printStackTrace();
			}
		/*}else{
			try {
				SQL:{
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						
						String sql ="select distinct category_image from vw_alacarte_item_details_from_kitchen "
								+" where category_id = ? and serving_zipcodes LIKE ? "	;	
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, categoryId);
							preparedStatement.setString(2, "%"+pincode+"%");
							int serialNo = 0;
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								JSONObject jobject =  new JSONObject();
								jobject.put("serial", serialNo);
								jobject.put("itemcode", "");
								jobject.put("cuisineid", "");
								jobject.put("categoryid","");
								jobject.put("categorydescription", "" );
								String tempImage  = resultSet.getString("category_image");
								String categoryImage;
								if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
									 categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}else if(tempImage.startsWith("http://")){
									categoryImage = tempImage.replace("http://", "");
								}else{
									 categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
								}
								
								jobject.put("categoryimage", categoryImage);
						    	jobject.put("categoryname","");
						    	
						    	boolean isBikerAvailableForLunch = false,isBikerAvailableForDinner = false;
						    	
						    	jobject.put("stock", 1);
						    	jobject.put("lunchstock", 1);
						    	jobject.put("availableBikerForLunch", true);
						    	
						    	jobject.put("dinnerstock", 1);
						    	jobject.put("availableBikerForDinner", true);
						    	jobject.put("available",true);
						    	
						    	
						    	jobject.put("mealtype", "BOTH");
						    	jobject.put("categoryprice", 0.0);
						    	jArray.put(jobject);
						    	serialNo++;
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}
						}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			 
		}*/
		
			
			System.out.println("Items size: "+jArray.length());
			//fetchAllCategory.put("Categories", jArray);
	    	return jArray;
	}
	
	
	/*public static Integer getItemStock(Integer itemId, Integer kitchenId){*/
	public static String getItemStock(String pincode, String itemCode,Connection connection,
			String mealType, String deliveryDay, String kitchenIds, String area){
    	Integer stock =0;
    	try {
			SQL:{
    				//Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				/*String sql = "select stock from fapp_kitchen_items where "
								+" kitchen_id = ? and item_id = ?";*/
    				String sql = "";
    				if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TODAY")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(stock) AS stock  "
	    						+" from vw_active_kitchen_items  "
	    						+" where item_code = ? "
	    						+" and is_active='Y' and serving_areas like ? "
	    						+"  and  kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code = ? "
    	    						+" and is_active='Y' and serving_areas like ? ";
    					}
    					/*sql = "select sum(stock)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}else if(mealType.equalsIgnoreCase("LUNCH") && deliveryDay.equalsIgnoreCase("TOMORROW")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y'and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and serving_areas like ? "
    								+" and is_active_tomorrow='Y' "
    								+" and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active_tomorrow='Y' and serving_areas like ? ";	
    					}
    					/*sql = "select sum(stock_tomorrow)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y'and fki.kitchen_id IN "+kitchenIds;*/
    				}else if(mealType.equalsIgnoreCase("DINNER") && deliveryDay.equalsIgnoreCase("TODAY")){
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(fki.dinner_stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(dinner_stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active='Y' "
    								+" and serving_areas like ? and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(fki.dinner_stock)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(dinner_stock)AS stock "
    								+" from vw_active_kitchen_items "
    								+" where item_code= ? "
    								+" and is_active='Y' "
    								+" and serving_areas like ? ";
    					}
    					
    					/*sql = "select sum(fki.dinner_stock)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}else{
    					
    					if( !kitchenIds.equalsIgnoreCase("()")){
    						/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    						sql = "select sum(dinner_stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items"
    								+" where item_code= ? and is_active_tomorrow='Y' "
    								+" and serving_areas like ? and kitchen_id IN "+kitchenIds;
    					}else{
    						/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
    								+" from fapp_kitchen_items fki "
    								+" join fapp_kitchen fk "
    								+" on fki.kitchen_id = fk.kitchen_id "
    								+" and fk.serving_zipcodes like ? "
    								+" where fki.item_code= ? and fki.is_active_tomorrow='Y' "
    								+" and fk.is_active='Y' ";*/
    						sql = "select sum(dinner_stock_tomorrow)AS stock "
    								+" from vw_active_kitchen_items"
    								+" where item_code= ?"
    								+" and serving_areas like ? "
    								+" and is_active_tomorrow='Y' ";
    					}
    					/*sql = "select sum(fki.dinner_stock_tomorrow)AS stock "
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and fk.serving_zipcodes like ? "
								+" where fki.item_code= ? and fki.is_active='Y' "
								+" and fk.is_active='Y' and fki.kitchen_id IN "+kitchenIds;*/
    				}
    				
    				try {
						preparedStatement = connection.prepareStatement(sql);
						
						preparedStatement.setString(1, itemCode);
						//preparedStatement.setString(2, "%"+pincode+"%");
						preparedStatement.setString(2, "%"+area+"%");
						/*preparedStatement.setString(1, "%"+pincode+"%");
						preparedStatement.setString(2, itemCode);*/
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							stock = resultSet.getInt("stock");
							if(stock<=0){
								stock = 0;
							}
						}
					} catch (Exception e) {
						//System.out.println("ERROR DUE TO:"+e.getMessage());
						//e.printStackTrace();
					}finally{
						/*if(connection!=null){
							connection.close();
						}*/
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return stock.toString();
    }
  
	
	public static String getLunchOrDinner( String pincode , String itemCode , Connection connection){
		Set<String> lunchOrDinnerSet = new HashSet<String>();
		try {
			SQL:{
					//Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select fk.dinner_available " 
								+" from fapp_kitchen_items fki "
								+" join fapp_kitchen fk "
								+" on fki.kitchen_id = fk.kitchen_id "
								+" and serving_zipcodes like ?"
								+" where fki.item_code=?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, "%"+pincode+"%");
						preparedStatement.setString(2, itemCode);
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							lunchOrDinnerSet.add(resultSet.getString(1));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						/*if(connection!=null){
							connection.close();
						}*/
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(lunchOrDinnerSet.contains("Y") && lunchOrDinnerSet.contains("N"))
			return "Both";
		else if(lunchOrDinnerSet.contains("Y"))
			return "Dinner";
		else
			return "Lunch";
		//return lunchOrDinnerSet;
	}
	
    public static JSONObject fetchCategoryList(String city, String location, Integer cuisineId) throws Exception {
    	
		JSONArray jArray = new JSONArray();
		JSONObject categoryNameList =  new JSONObject();
		try {
			SQL:{
				Connection connection = null;
		    	PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				connection = DBConnection.createConnection();
				String sql = "select fkd.kitchen_category_id AS category_id,"
							+" fkd.kitchen_id,"
							+" fc.category_name, "
							+" fc.category_image, "
							+" fkd.category_stock,"
							+" fkd.cost_price "
							+" from fapp_kitchen_stock fkd "
							+" join food_category fc "
							+" on fkd.kitchen_category_id = fc.category_id "
							+" where fkd.kitchen_cuisine_id = ? "
							+" and fkd.kitchen_id IN "+getKitchenIDListFromLocation(city, location, cuisineId)+"";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, cuisineId);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						JSONObject jobject =  new JSONObject();
						jobject.put("categoryid",resultSet.getString("category_id"));
						jobject.put("categorydescription",  getCategoryDescription(jobject.getInt("categoryid")));
						String tempImage  = resultSet.getString("category_image");
						//String categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						String categoryImage;
						if(tempImage.contains("C:\\apache-tomcat-7.0.62/webapps/")){
							 categoryImage = tempImage.replace("C:\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}else{
							 categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						}
						//String categoryImage = tempImage.replace("C:\\Joget-v4-Enterprise\\apache-tomcat-7.0.62/webapps/", "appsquad.cloudapp.net:8080/");
						//jobject.put("categoryimage", resultSet.getString("category_image"));
						jobject.put("categoryimage", categoryImage);
				    	jobject.put("categoryname",resultSet.getString("category_name"));
				    	jobject.put("stock", resultSet.getString("category_stock"));
				    	//jobject.put("stock", getStock(kitchenid, cuisineId, jobject.getInt("categoryid")));
				    	jobject.put("categoryprice", resultSet.getDouble("cost_price"));
				    	jArray.put(jobject);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
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
		categoryNameList.put("Categories", jArray);
    	return categoryNameList;
    }
    
    /**
     * Get kitchen Id from location and cuisines
     * @param areaId
     * @param cuisineId
     * @return
     */
    private static String getKitchenIDListFromLocation(String city,String location, Integer cuisineId){
    	String kitchenIds = "";
    	ArrayList<Integer> kitchenIdList = new ArrayList<Integer>();
		try {
			 SQLKITCHENLIST:{
					Connection connection = null;
			    	PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					connection = DBConnection.createConnection();
					String sql = "select distinct fkd.kitchen_id "
								+" from fapp_kitchen_details fkd "
								+" join sa_area sa "
								+" on fkd.area_id =  sa.area_id " 
								+" join fapp_kitchen fk "
								+" on fk.kitchen_id =  fkd.kitchen_id "
								+" where sa.area_id = "
								+" (select area_id from sa_area where area_name ILIKE ? and city_id = "
			    				+" (select city_id from sa_city where city_name ILIKE ?))"
								+" and fkd.cuisin_id = ? "
								+" and fk.is_active = 'Y'";
					 try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, location);
						preparedStatement.setString(2, city);
						preparedStatement.setInt(3, cuisineId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							kitchenIdList.add(resultSet.getInt("kitchen_id"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
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
		StringBuilder kitchenIdListBuilder = new StringBuilder();
		String temp = kitchenIdList.toString();
		String fb = temp.replace("[", "(");
		String bb = fb.replace("]", ")");
		kitchenIdListBuilder.append(bb);
		kitchenIds = kitchenIdListBuilder.toString();
		System.out.println("KitchenIds -->"+kitchenIds);
		return kitchenIds;
    }
    
    public static String getCategoryDescription(Integer categoryid){
    	String categoryDescription = "";
    	ArrayList<String> catDesList = new ArrayList<String>();
    	StringBuilder categoryBuilder = new StringBuilder();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				//String sql = "select item_name from food_items where category_id = ?";
    				String sql = "select item_description from food_items where category_id =? and kitchen_id IS NOT NULL";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, categoryid);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
					//		catDesList.add(resultSet.getString("item_name"));
							catDesList.add( toCamelCase(resultSet.getString("item_description")));
						}
					} catch (Exception e) {
						System.out.println("ERROR DUE TO:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	String temp = catDesList.toString();
		String fb = temp.replace("[", "");
		String bb = fb.replace("]", "");
		categoryBuilder.append(bb);
		categoryDescription = categoryBuilder.toString();
		
		return categoryDescription;
    }
	
    public static String getItemDescription(int categoryid){
    	String categoryDescription = "";
    	ArrayList<String> catDesList = new ArrayList<String>();
    	StringBuilder categoryBuilder = new StringBuilder();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				//String sql = "select item_name from food_items where category_id = ?";
    				String sql = "select item_id,item_description,item_code,item_price from food_items where category_id =?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, categoryid);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
					//		catDesList.add(resultSet.getString("item_name"));
							catDesList.add( toCamelCase(resultSet.getString("item_description")));
						}
					} catch (Exception e) {
						System.out.println("ERROR DUE TO:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	String temp = catDesList.toString();
		String fb = temp.replace("[", "");
		String bb = fb.replace("]", "");
		categoryBuilder.append(bb);
		categoryDescription = categoryBuilder.toString();
		
		return categoryDescription;
    }
    
    public static Integer getStock(Integer kitchenId, Integer cuisineId, Integer categoryId){
    	Integer stock =0;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "select category_stock from fapp_kitchen_stock where "
								+" kitchen_id = ?"
								+" and kitchen_cuisine_id = ?"
								+" and kitchen_category_id = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						preparedStatement.setInt(2, cuisineId);
						preparedStatement.setInt(3, categoryId);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							stock = resultSet.getInt("category_stock");
						}
					} catch (Exception e) {
						System.out.println("ERROR DUE TO:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return stock;
    }
	
    /**
	 * Method to fetch category and with respect to item list
	 */
	public static JSONObject categoryItemList(Integer categoryId) throws JSONException{
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	
    	JSONObject jsonObjectCategory = new JSONObject();
    	JSONArray jsonArrayItems = new JSONArray();
    	try{
    	/*String sqlCategory = "select category_name,category_banner from food_category where category_id=?";*/
    	String sqlItemList = "select fi.item_id,"
							+" fi.item_name,"
							+" 	fi.item_image,"
							+" fi.item_image_ws"
							+" 	from food_items fi,"
							+" 	food_category fc"
							+" 	where fc.category_id = fi.category_id AND "
							+"	fi.category_id = ?";
    	/*SQlCategory:{
			    	try {	
				    		connection = DBConnection.createConnection();
				    				    		
							preparedStatement = connection.prepareStatement(sqlCategory);
							preparedStatement.setInt(1, categoryId);
							resultSet = preparedStatement.executeQuery();
							while(resultSet.next()){
								jsonObjectCategory.put("category_name",resultSet.getString("category_name"));
								jsonObjectCategory.put("category_banner",resultSet.getString("category_banner"));
							}
			    	}catch(Exception e){
			    		System.out.println(e);
			    	}
    	}*/
    	
    	SQlItems:{
	    	try {	
		    		connection = DBConnection.createConnection();
		    		preparedStatement = connection.prepareStatement(sqlItemList);
		    		preparedStatement.setInt(1, categoryId);
					resultSet = preparedStatement.executeQuery();
					while(resultSet.next()){
						JSONObject subitemJsonObject = new JSONObject();
						subitemJsonObject.put("id",resultSet.getString("item_id"));
						subitemJsonObject.put("name",resultSet.getString("item_name"));
						subitemJsonObject.put("image",resultSet.getString("item_image"));
						//subitemJsonObject.put("image",resultSet.getBytes("item_image_ws"));
						jsonArrayItems.put(subitemJsonObject);
					}
	    	}catch(Exception e){
	    		System.out.println(e);
	    	}finally{
	    		if(connection!=null){
					connection.close();
				}
	    	}
    	}
    	}catch(Exception e){
    		
    	}
    	jsonObjectCategory.put("item_list", jsonArrayItems);	
    	return jsonObjectCategory;
    	
	}
	
	/**
	 * This method is useful to fetch all al a carte items
	 * @throws JSONException 
	 */
	public static JSONObject getalacarteItems() throws JSONException{
		JSONObject jsonalacarteItemList = new JSONObject();
		JSONArray jArray = new JSONArray();
		try {
			SQL:{
				Connection connection = null;
		    	PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql =  "SELECT * FROM food_alacarte_items";
				
				try {
					connection = DBConnection.createConnection();
					preparedStatement = connection.prepareStatement(sql);
					resultSet = preparedStatement.executeQuery();
					while(resultSet.next()){
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("alacarteItemId", resultSet.getString("alacarte_item_id"));
						jsonObject.put("alacarteItemName", resultSet.getString("alacarte_item_name"));
						jsonObject.put("alacarteItemPrice", resultSet.getString("alacarte_item_price"));
						jArray.put(jsonObject);
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.print(e);
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		jsonalacarteItemList.put("alacarte_item_list", jArray);
		return jsonalacarteItemList;
	}
	
	
	/**
	 * 
	 * Method to fetch deal details based on city and area
	 */
	public static JSONObject showDeals(String cityName,String areaName) throws Exception {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JSONArray jArray = new JSONArray();
		JSONObject dealList =  new JSONObject();
		
		String sqlQuery = "SELECT "
						+" fd.deal_id, "
						+" fd.deal_title, "
						+" fd.deal_banner "
						+" from "
						+" food_deals fd, "
						+" sa_area sa, "
						+" sa_city sc "
						+" where "
						+" sc.city_name ILIKE ? "
						+" and " 
						+" sa.area_name ILIKE ? "
						+" and "
						+" fd.area_id =  sa.area_id ";

    	try {
    		
    		connection = DBConnection.createConnection();
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setString(1, cityName);
			preparedStatement.setString(2,areaName);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				JSONObject jobject =  new JSONObject();
		    	jobject.put("deal_id",resultSet.getString("deal_id"));
		    	jobject.put("deal_title",resultSet.getString("deal_title"));
		    	//jobject.put("deal_image",resultSet.getString("deal_banner"));
		    	jobject.put("deal_image",resultSet.getBytes("deal_banner_ws"));
		    	jArray.put(jobject);
			}
		} catch (SQLException sqle) {
			throw sqle;
		}catch (Exception e) {
            // TODO Auto-generated catch block
            if (connection != null) {
                connection.close();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    	dealList.put("deal_list", jArray);
    	return dealList;
    }
    
	
	/*
     * Method to check city and location availability
     * 
     * 
     */
    public static JSONObject locationAvailability(String city,String location) throws Exception{
    	JSONObject jsonObject = new JSONObject();
    	Boolean isLocationAvailable = false;
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	String status = "";
    	try {
	    		try {
	    			connection = DBConnection.createConnection();
				} catch (Exception e) {
					// TODO: handle exception
				}
			 preparedStatement =connection.prepareStatement("select "
														+" sa.is_active "
														+" from "
														+" sa_city sc, "
														+" sa_area sa "
														+" where "
														+" sc.city_name ILIKE ? "
														+" and "
														+" sa.area_name ILIKE ? "
														+" and "
														+" sc.is_active='Y' "
														+" and "
														+" sa.city_id=sc.city_id ");
			
			 preparedStatement.setString(1, city);
			 preparedStatement.setString(2, location);
			 resultSet = preparedStatement.executeQuery();
			 
			 while(resultSet.next()){
				 status = resultSet.getString(1);
				 
				 if(status.equals("Y")){
					 isLocationAvailable = true;
				 }else{
					 isLocationAvailable = false;
				 }
				
			 }
		} catch (SQLException sqle) {
            throw sqle;
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	if(connection!=null){
				connection.close();
			}
            throw e;
        } finally {
        	if(connection!=null){
				connection.close();
			}
        }
    	jsonObject.put("availability", isLocationAvailable);
    	return jsonObject;
    }
    
    /**
   	 * Generate Promo code
   	 * @param deviceregId
   	 * @throws IOException
   	 */
    public static String generateReferalCode(String userName){
   		String referalCode = "";
   		Integer serialuserid=0;
   		Connection connection = null; 
   		try {
   			 connection = DBConnection.createConnection();
   			SQL:{
   					PreparedStatement preparedStatement = null;
   					ResultSet resultSet = null;
   					String sql = "SELECT MAX(login_id) FROM fapp_accounts";
   					try {
   						preparedStatement = connection.prepareStatement(sql);
   						resultSet = preparedStatement.executeQuery();
   						if(resultSet.next()){
   							serialuserid = resultSet.getInt(1);
   						}
   					} catch (Exception e) {
   						// TODO: handle exception
   					}finally{
   						if(connection!=null){
   							connection.close();
   						}
   					}
   			}
   		} catch (Exception e) {
   			// TODO: handle exception
   		}
   		
   		if(userName.length()==2){
			userName = userName + "E";
			referalCode = userName.substring(0,3);
			referalCode = referalCode+String.format("%06d", serialuserid);
		}else if(userName.length()==1){
			userName = userName + "EE";
			referalCode = userName.substring(0,3);
			referalCode = referalCode+String.format("%06d", serialuserid);
		}else{
			referalCode = userName.substring(0,3);
			referalCode = referalCode+String.format("%06d", serialuserid);
		}
   	    System.out.println("After calculation from Db referal no is::"+referalCode.toUpperCase());
   		return referalCode.toUpperCase();
   	}
    
    public static boolean updateMyCode(String myCode, String mobileNo, boolean isReffered){
    	boolean updateStatus = false;
    	System.out.println("My code : "+myCode+" Mobile No: "+mobileNo);
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "";
    				if(isReffered){
    					sql = "UPDATE fapp_accounts set my_code = ?,my_balance = my_balance + 50.0 where mobile_no=?";
    				}else{
    					sql = "UPDATE fapp_accounts set my_code = ? where mobile_no=?";
    				}
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, myCode.toUpperCase());
						preparedStatement.setString(2, mobileNo);
						System.out.println(preparedStatement);
						int count=preparedStatement.executeUpdate();
						if(count>0){
							updateStatus = true;
							System.out.println("New users code "+myCode+" is updated...");
						}
					} catch (Exception e) {
						System.out.println("Updation failed in update mycode!"+e.getMessage());
						
					}finally{
						if (preparedStatement!=null) {
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
    	return updateStatus;
    }
   
    private static boolean isUserRegistered(String contactNumber){
    	boolean isExists = false;
    	int count = 0;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement =  null;
    				ResultSet resultSet = null;
    				String sql = "SELECT COUNT(mobile_no)AS mobile_no from fapp_accounts where mobile_no = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, contactNumber);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							count = resultSet.getInt("mobile_no");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Count failed in isUserRegisteres "+e.getMessage());
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
    	if(count>0){
    		isExists = true;
    	}
    	return isExists;
    }
    
    
    public static boolean isRefCodeExists(String referalCode){
    	boolean isExists = false;
    	int count = 0;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement =  null;
    				ResultSet resultSet = null;
    				String sql = "SELECT COUNT(my_code)AS ref_code from fapp_accounts where my_code = ?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, referalCode);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							count = resultSet.getInt("ref_code");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Count failed in isRefCodeExists "+e.getMessage());
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
    	if(count>0){
    		isExists = true;
    	}
    	return isExists;
    }
    /**
	 * Generate order number
	 * @param deviceregId
	 * @throws IOException
	 */
 	public static String generateOrderNo(){
		String orderNumber = "";
		Integer serialorderid=0;
		Connection connection = null; 
		try {
			 connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT MAX(order_id) FROM fapp_orders";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							serialorderid = resultSet.getInt(1);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//orderNumber =  "REG/"+(Calendar.getInstance().get(Calendar.DATE))+(Calendar.getInstance().get(Calendar.MONTH) + 1)+String.format("%06d", serialorderid+1);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month=(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String newDay = "",newMonth = "";
		if (day>=1 && day<10){
		 newDay = "0"+String.valueOf(day);
		}else{
			newDay = String.valueOf(day);
		}
		if(month>=1 && month<10){
			newMonth = "0"+String.valueOf(month);
		}else{
			newMonth = String.valueOf(month);
		}
	    orderNumber = "REG/"+newDay+"/"+newMonth+"/"+String.format("%06d", serialorderid+1);
	    System.out.println("After calculation from Db order no is::"+orderNumber);
		return orderNumber;
	}
    
	
	private static String generateSubcriptionNo(){
		String subscriptionNumber = "";
		Integer serialorderid=0;
		Connection connection = null; 
		try {
			 connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT MAX(subscription_id) FROM fapp_subscription";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							serialorderid = resultSet.getInt(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//subscriptionNumber =  "SUBS/"+String.format("%06d", serialorderid+1);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month=(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String newDay = "",newMonth = "";
		if (day>=1 && day<10){
		 newDay = "0"+String.valueOf(day);
		}else{
			newDay = String.valueOf(day);
		}
		if(month>=1 && month<10){
			newMonth = "0"+String.valueOf(month);
		}else{
			newMonth = String.valueOf(month);
		}
	    subscriptionNumber = "SUB/"+newDay+"/"+newMonth+"/"+String.format("%06d", serialorderid+1);
		System.out.println("Subscription number->"+subscriptionNumber);
		return subscriptionNumber;
	}
    
	/**
	 * WEB SERVICE FOR order assignment to delivery boy
	 * @param orderNo
	 * @param dayName
	 * @param mealType
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject assignDeliveryBoyForSubscription(String orderNo,
			String dayName, String mealType,String boyId) throws JSONException{
		JSONObject jsonAssignedStatus = new JSONObject();
		boolean assignedStatus = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql ="UPDATE fapp_subscription_meals "
							   +" SET delivery_boy_id=? , is_assigned = 'Y'"
							   +" WHERE subscription_no=? AND  "
							   +" day_name=? AND meal_type=? ";
					try {
						preparedStatement =connection.prepareStatement(sql);
						preparedStatement.setInt(1, Integer.parseInt(boyId));
						preparedStatement.setString(2, orderNo);
						preparedStatement.setString(3, dayName);
						preparedStatement.setString(4, mealType);
						int assigned = preparedStatement.executeUpdate();
						if(assigned>0){
							assignedStatus = true;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("Status of assign delivery boy is::"+assignedStatus);
		if(assignedStatus){
			jsonAssignedStatus.put("status", assignedStatus);
			String[] details = new String[3];
			details = getBoyDetails(Integer.parseInt(boyId));
			//sendMessageForDeliveryBoy(getCustomerMobile(orderNo, "SUB"), details[0], details[1]);
			
			return jsonAssignedStatus;
		}else{
			jsonAssignedStatus.put("status", assignedStatus);
			return jsonAssignedStatus;
		}
	}
	
	 /**
     * A WEB SERVICE for kitchen boys
     * @param kitchenId
     * @return
     * @throws JSONException
     */
	public static JSONObject getKitchenDeliveryBoys(String kitchenName) throws JSONException{
		JSONArray kitchenDeliveryBoyArray = new JSONArray();
    	JSONObject jsonKitchenDeliveryBoys = new JSONObject();
    	try {
			SQL:{
		    		Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT * FROM vw_delivery_boy_data WHERE "
							+ " kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject boys = new JSONObject();
							if(resultSet.getString("delivery_boy_name")!=null){
								boys.put("name",resultSet.getString("delivery_boy_name") );
							}else{
								boys.put("name", " ");
							}
							if( resultSet.getString("delivery_boy_phn_number")!=null){
								boys.put("mobileno",resultSet.getString("delivery_boy_phn_number"));
							}else{
								boys.put("mobileno", " ");
							}
							/*if( resultSet.getString("delivery_boy_vehicle_reg_no")!=null){
								boys.put("vehicleno",resultSet.getString("delivery_boy_vehicle_reg_no") );
							}else{
								boys.put("vehicleno", " ");
							}*/
							if(resultSet.getString("delivery_boy_id")!=null){
								boys.put("boyid", resultSet.getString("delivery_boy_id"));
							}else{
								boys.put("boyid", " ");
							}
							kitchenDeliveryBoyArray.put(boys);
						}
					}  catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}

    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("deliveryboys web service is end here.."+kitchenDeliveryBoyArray.length());
    	jsonKitchenDeliveryBoys.put("boylist", kitchenDeliveryBoyArray);
		return jsonKitchenDeliveryBoys;
	}
	
	 /**
     * A WEB SERVICE for kitchen orders
     * @param kitchenId
     * @return
     * @throws JSONException
     */
    public static JSONObject getKitchenOrders(String kitchenName) throws JSONException{
    	JSONArray kitchenorderTrackArray = new JSONArray();
    	JSONObject jsonKitchenOrders = new JSONObject();
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				/*String sql = "select * from vw_kitchen_order_list where kitchen_name = ? AND order_date = CURRENT_DATE";
    				String sql = "select * from vw_kitchen_orders where kitchen_name = ? AND delivered_to_boy ='N' "
    						+ "AND (order_date = CURRENT_DATE OR CURRENT_DATE<=delivery_date) order by order_no DESC";*/
    				String sql = "select * from vw_all_kitchen_orders where kitchen_name = ? AND delivery_date = CURRENT_DATE order by order_no DESC";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							JSONObject orders = new JSONObject();
							if(resultSet.getString("order_by")!=null){
								orders.put("orderBy", resultSet.getString("order_by"));
							}else{
								orders.put("orderBy", " ");
							}
							if(resultSet.getString("contact_number")!=null){
								orders.put("contactNumber", resultSet.getString("contact_number"));
							}else{
								orders.put("contactNumber", " ");
							}
							if(resultSet.getString("payment_name")!=null){
								orders.put("payType", resultSet.getString("payment_name"));
							}else{
								orders.put("payType", "");
							}
							if(resultSet.getString("external_order_id")!=null){
								orders.put("pickjiOrderNo", resultSet.getString("external_order_id"));
							}else{
								orders.put("pickjiOrderNo", "");
							}
							/*if(resultSet.getString("city")!=null){
								orders.put("city", resultSet.getString("city"));
							}else{
								orders.put("city", " ");
							}
							if(resultSet.getString("flat_no")!=null){
								orders.put("flatno", resultSet.getString("flat_no"));
							}else{
								orders.put("flatno", " ");
							}
							if(resultSet.getString("street_name")!=null){
								orders.put("streetname", resultSet.getString("street_name"));
							}else{
								orders.put("streetname", " ");
							}
							if(resultSet.getString("landmark")!=null){
								orders.put("landmark", resultSet.getString("landmark"));
							}else{
								orders.put("landmark", " ");
							}*/
							orders.put("pincode", resultSet.getString("pincode"));	
							orders.put("orderid", resultSet.getInt("order_id"));
							orders.put("orderno", resultSet.getString("order_no"));
							if(resultSet.getString("meal_type")!=null){
								orders.put("mealtype", resultSet.getString("meal_type"));
							}else{
								orders.put("mealtype"," ");
							}
							if(resultSet.getString("time_slot")!=null){
								orders.put("timeslot", resultSet.getString("time_slot"));
							}else{
								orders.put("timeslot"," ");
							}
							if(resultSet.getString("order_status_name")!=null){
								orders.put("orderstatus", resultSet.getString("order_status_name"));
							}else{
								orders.put("orderstatus", " ");
							}
							if( resultSet.getString("delivery_address")!=null){
								orders.put("deliveryaddress", resultSet.getString("delivery_address"));
							}else{
								orders.put("deliveryaddress", " ");
							}
							
							String received = resultSet.getString("received");
							if(received!=null){
								if(received.equals("Y")){
									orders.put("orderreceived", true);
								}else{
									orders.put("orderreceived", false);
								}
							}else{
								orders.put("orderreceived"," ");
							}
							
							String notified =  resultSet.getString("notify");
							if(notified!=null){
								if(notified.equals("Y")){
									orders.put("ordernotified", true);
								}else{
									orders.put("ordernotified", false);
								}
							}else{
								orders.put("ordernotified", " ");
							}
							
							String rejected = resultSet.getString("rejected");
							if(rejected!=null){
								if(rejected.equals("Y")){
									orders.put("orderrejected", true);
								}else{
									orders.put("orderrejected", false);
								}
							}else{
								orders.put("orderrejected", " ");
							}
							String deliverdToBoy = resultSet.getString("delivered_to_boy");
							if(deliverdToBoy!=null){
								if(deliverdToBoy.equals("Y")){
									orders.put("orderdeliveredtoboy", true);
								}else{
									orders.put("orderdeliveredtoboy", false);
								}
							}else{
								orders.put("orderdeliveredtoboy", " ");
							}
							String driverReached = resultSet.getString("driver_reached");
							if(driverReached.equals("Y")){
								orders.put("driverReached", true);
							}else{
								orders.put("driverReached", false);
							}
							if(resultSet.getString("driver_name")!=null){
								orders.put("boyName", resultSet.getString("driver_name"));
							}else{
								orders.put("boyName", "");
							}
							if(resultSet.getString("driver_number")!=null){
								orders.put("boyPhoneNo", resultSet.getString("driver_number"));
							}else{
								orders.put("boyPhoneNo", "");
							}
							//String startdate =  resultSet.getString("start_date");
							//String enddate =  resultSet.getString("end_date");
							String orderDate = resultSet.getString("order_date");
							String reformattedStartDate = "",reformattedEndDate = "", reformattedOrderDate="";
							SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
							/*if(startdate!=null && enddate!=null){
								try {
								     reformattedStartDate = myFormat.format(fromUser.parse(startdate));
								    reformattedEndDate= myFormat.format(fromUser.parse(enddate));
								    reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
								} catch (ParseException e) {
								    e.printStackTrace();
								}
								orders.put("startdate", reformattedStartDate);
								orders.put("enddate", reformattedEndDate);
							}else{*/
								orders.put("startdate", " ");
								orders.put("enddate", " ");
							//}
							
							try {
							    reformattedOrderDate = myFormat.format(fromUser.parse(orderDate));
							} catch (ParseException e) {
							    e.printStackTrace();
							}
							orders.put("orderdate", reformattedOrderDate);
							
							
							orders.put("itemdetails", getitemdetailsOfKitchen( orders.getString("orderno"), kitchenName, connection) );
							
							kitchenorderTrackArray.put(orders);
						}
					}  catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}

    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	System.out.println("Total orders for kitchen:: "+kitchenorderTrackArray.length());
    	jsonKitchenOrders.put("ordertrack", kitchenorderTrackArray);
    	return jsonKitchenOrders;
    }
    

    /**
     * Array of ordered items used for order tracking and order history
     * @param orderid
     * @return
     */
    public static JSONArray getitemdetailsOfKitchen(String orderNo,String kitchenName, Connection connection){
    	JSONArray itemsDetailArray = new JSONArray();
    	//Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				//connection = DBConnection.createConnection();
				/*String cuisineSql="select cuisine_id, category_id , qty, category_price, pack_type " 
								  +" from fapp_order_item_details "
								  +" where order_id = (select order_id from fapp_orders where order_no = ?)"
								  +" and kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name=?)";*/
				String cuisineSql="select * from vw_order_items_of_kitchen where order_no = ? and kitchen_name = ?";

				/*String sqlSubitems ="select cuisine_id, category_id , quantity,meal_price"  
								 +"  from fapp_subscription_meals_details "
								 +" where subscription_id = (select subscription_id from fapp_subscription where subscription_no = ?)"
								 +"   and kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name=?)";*/
				String sqlSubitems ="select * from vw_kitchen_order_details"
									 +" where subscription_id = (select subscription_id from fapp_subscription where subscription_no = ?)"
									 +"   and kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name=?)";
				try {
					
						if(orderNo.startsWith("REG")){
							preparedStatement = connection.prepareStatement(cuisineSql);
							preparedStatement.setString(1, orderNo);
							preparedStatement.setString(2, kitchenName);
						}else{
							preparedStatement = connection.prepareStatement(sqlSubitems);
							preparedStatement.setString(1, orderNo);
							preparedStatement.setString(2, kitchenName);
						}
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("cuisineid", resultSet.getInt("cuisine_id"));
							//itemobject.put("cuisinename", getCuisineName(itemobject.getInt("cuisineid")));
							itemobject.put("cuisinename", resultSet.getString("cuisin_name"));
							itemobject.put("categoryid", resultSet.getInt("category_id"));
							//itemobject.put("categoryname", getCategoryName(itemobject.getInt("categoryid")));
							itemobject.put("categoryname",  resultSet.getString("category_name"));
							itemobject.put("itemname",  resultSet.getString("item_name"));
							String itemDescription = resultSet.getString("item_description");
							if(itemDescription!=null)
								itemobject.put("itemdescription",  itemDescription);
							else
								itemobject.put("itemdescription",  "");
							
							if(orderNo.startsWith("REG")){
								itemobject.put("quantity", resultSet.getInt("qty"));
								itemobject.put("price", resultSet.getDouble("category_price"));
								itemobject.put("day"," ");
								itemobject.put("type", " ");
								itemobject.put("timeslot", " ");
								itemobject.put("assignedstatus", " ");
								itemobject.put("orderstatus", " ");
								String packing = resultSet.getString("pack_type");
								if(packing != null){
									itemobject.put("packing", packing);
								}else{
									itemobject.put("packing", "");
								}
								String riceRoti = resultSet.getString("rice_roti");
								if(riceRoti != null){
									itemobject.put("riceRoti", riceRoti);
								}else {
									itemobject.put("riceRoti", "");
								}
							}else{
								itemobject.put("quantity", resultSet.getInt("quantity"));
								itemobject.put("price", resultSet.getDouble("meal_price"));
								if(resultSet.getString("day_name")!=null){
									itemobject.put("day", resultSet.getString("day_name"));
								}else{
									itemobject.put("day"," ");
								}
								if(resultSet.getString("meal_type")!=null){
									itemobject.put("type", resultSet.getString("meal_type"));
								}else{
									itemobject.put("type", " ");
								}
								if(resultSet.getString("time_slot")!=null){
									itemobject.put("timeslot", resultSet.getString("time_slot"));
								}else{
									itemobject.put("timeslot", " ");
								}
								if(resultSet.getString("is_assigned").equals("N")){
									itemobject.put("assignedstatus", "N");
								}else{
									itemobject.put("assignedstatus", "Y");
								}
								if(resultSet.getString("status")!=null){
									itemobject.put("orderstatus", resultSet.getString("status"));
								}else{
									itemobject.put("orderstatus", " ");
								}
								itemobject.put("packing", "");
								
								itemobject.put("riceRoti", "");
							}
							
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					/*if(connection!=null){
						connection.close();
					}*/
				}	
				
			}
		
		} catch (Exception e) {
		
		}
		return itemsDetailArray;
    }
    
    
    /**
     * A WEB SERVICE FOR RECEIVING ORDERS FROM KITCHEN APP
     * @param orderNo
     * @param kitchenName
     * @return
     * @throws Exception 
     */
    public static JSONObject receiveOrderFromKitchen(String orderNo, String kitchenName) throws Exception{
    	JSONObject receivedJsonObject = new JSONObject();
    	Boolean orderReceived = false;
    	ArrayList<Driver> drivers = null;
    	Driver driver = null;
    	int groupId =0;
    	if(orderNo.startsWith("REG")){
    		/*orderReceived = receiveOrder(orderNo,kitchenName);
			if(isAllKitchenReceive(orderNo)){
				if(changeOrderStatus(orderNo)){
					sendMessageToMobile(getCustomerMobile(orderNo,"REGULAR"), orderNo, getOrderTime(orderNo, "REGULAR"), 2);
				}
			}*/
    		groupId = getGroupId(kitchenName);//find group id of this kitchen
    		if(groupId!=0){
    			drivers = getFreeDriversFromGroup(groupId);//find free bikers from this group
    		}
    		if(drivers.size()>0){
    			/**
    			 * Update table order tracking with received is Y and current time stamp for order id and kitchen id..
    			 */
    			orderReceived = receiveOrder(orderNo,kitchenName);
    			/**
    			 * Check if all kitchen accepted that order or not..
    			 */
    			if(isAllKitchenReceive(orderNo)){
    				/**
    				 * Change the status of order...
    				 */
    				if(changeOrderStatus(orderNo)){
    					/**
    					 * Send confirmation message to customer for this order...
    					 */
    					// sendMessageToMobile(getCustomerMobile(orderNo,"REGULAR"), orderNo, getOrderTime(orderNo, "REGULAR"), 2);
    				}
    			}
    			
    			ArrayList<Integer> driverIdList = new ArrayList<Integer>();
    			for(Driver drvr: drivers){
    				driverIdList.add(drvr.getDriverId());
    			}
    			String driverIdlist = driverIdList.toString();
    			String fb = driverIdlist.replace("[", "(");
    			String driverIds = fb.replace("]", ")");
    			System.out.println("Ids:: "+driverIds);
    			String orderTimeSlot = getOrderTimeSlot(orderNo);
    			String time = TimeFormattor.convert12To24Hour(orderTimeSlot);
    			driver = findFirstDriver(time, driverIds);
				if(assignBiker(driver, orderNo, kitchenName)){
    				receivedJsonObject.put("status", true);
    			    receivedJsonObject.put("boyId", driver.getDriverUserId());
    			    receivedJsonObject.put("boyName", driver.getDriverName());
    			    receivedJsonObject.put("boyPhoneNo", driver.getContactNo());
    			}
    		}else{
    			System.out.println(" - - - NO BOY FOUND FROM EAZELYF! - - - ");
    			receivedJsonObject.put("status", false);
    			receivedJsonObject.put("boyId", "");
		    	receivedJsonObject.put("boyName", "");
		    	receivedJsonObject.put("boyPhoneNo", "");
    			//System.out.println("----GO FOR QUICK DROP!------");
    			//receivedJsonObject = placeOrderByPost(createPickJiJson(kitchenName, orderNo));
    			/****************************
    			 * GET PICKJI BOY DETAILS   *
    			 ***************************/
    			/*ArrayList<Kitchen> kitchenList = getPickUpList(orderNo);
				Customer customer = getUserDetails(orderNo);
				Collections.sort(kitchenList);
				Order order = getOrderDetails(orderNo);
				
				String emailBody = getBody(order, orderNo, kitchenList, customer);
				///change this email id to quickdrop's email id
				String receiverMailId = "somnathdutta048@gmail.com";
				mailSender(receiverMailId, "Order Delivery Details", emailBody);
    			receivedJsonObject.put("status", true);
    			receivedJsonObject.put("boyId", "QUICKBOYID-1");
		    	receivedJsonObject.put("boyName", "QUICK BOY");
		    	receivedJsonObject.put("boyPhoneNo", "QUICK BOY CONTACT");*/		
    		}
    	}
    	System.out.println(receivedJsonObject);
    	return receivedJsonObject;
    	/*if(orderNo.startsWith("REG")){
    		//System.out.println("User qty::"+getUserQuantityList(getOrderItemDetailList(orderNo,kitchenName)) );
    		//System.out.println("Kitchen old stock::"+getkitchenStockQuantity(kitchenName, getOrderItemDetailList(orderNo,kitchenName)) );
    		//System.out.println("Updated kitchen stock::"+ updatedKitchenStock(getUserQuantityList(getOrderItemDetailList(orderNo,kitchenName)), 
    			//	 getkitchenStockQuantity(kitchenName, getOrderItemDetailList(orderNo,kitchenName)) ));
    		//System.out.println("Kitchen stock id ::"+getkitchenStockIdList(kitchenName, getOrderItemDetailList(orderNo,kitchenName)) );
    		
        	//updateStockKitchen(getkitchenStockIdList(kitchenName, getOrderItemDetailList(orderNo,kitchenName) ),
    			//	updatedKitchenStock(getUserQuantityList(getOrderItemDetailList(orderNo,kitchenName)),
    				//		getkitchenStockQuantity(kitchenName, getOrderItemDetailList(orderNo,kitchenName))) );
    			
    			orderReceived = receiveOrder(orderNo,kitchenName);
    			
    			if(isAllKitchenReceive(orderNo)){
    				
    				isOrderReceived(orderNo);
    				
    				ArrayList<Kitchen> kitchenList = getPickUpList(orderNo);
    				Customer customer = getUserDetails(orderNo);
    				Collections.sort(kitchenList);
    				//String leadTimeKitchen = String.valueOf(kitchenList.get(0).leadTime);
    				
    				Order order = getOrderDetails(orderNo);
    				
    				String emailBody = getBody(order, orderNo, kitchenList, customer);
    				///change this email id to quickdrop's email id
    				String receiverMailId = "somnathdutta048@gmail.com";
    				mailSender(receiverMailId, "Order Delivery Details", emailBody);
    				
    			}else{
    				System.out.println("All kitchen not receive their orders!");
    			}
    			if(orderReceived){
    				receivedJsonObject.put("status", orderReceived);
    			}else{
    				receivedJsonObject.put("status", orderReceived);
    			}
        	return receivedJsonObject;
    	}else{
    		System.out.println(orderNo+" sub order receiving by "+kitchenName+". . .");
    		orderReceived = receiveSubscriptionOrder(orderNo, kitchenName);
    		if(orderReceived){
    			System.out.println(orderNo+" sub order received by "+kitchenName+"sucessfully!!");
    			if(isAllKitchenReceive(orderNo)){
    				//sendMessageToMobile(recipient, orderNo, orderTime, orderStatusId);
    				System.out.println(orderNo+" sub order received by all kitchens sucessfully!");
    				sendMessageToMobile(getCustomerMobile(orderNo, "SUB"), orderNo, getOrderTime(orderNo, "SUB"), 2);
    				receivedJsonObject.put("status", orderReceived);
    			}
    		}else{
    			receivedJsonObject.put("status", orderReceived);
    		}
    		return receivedJsonObject;
    	}*/
    	
    }
    
    public static int getGroupId(String kitchenName){
    	System.out.println("- - - Getting group id of kitchen "+kitchenName+" - - - ");
    	int groupId =0;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet= null;
    				String sql ="SELECT group_id from fapp_kitchen_group_details where kitchen_name =?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							groupId = resultSet.getInt("group_id");
						}
					} catch (Exception e) {
						System.out.println("Failed finding groupID::"+e.getMessage());
						connection.close();
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
    	System.out.println("- - - Group id of kitchen "+kitchenName+" is - - - "+groupId);
    	return groupId;
    }
    
    public static ArrayList<Driver> getFreeDriversFromGroup(int groupId){
    	ArrayList<Driver> drivers = new ArrayList<Driver>();
    	System.out.println("- - - Getting free drivers from group "+groupId+" - - - ");
    	try {
    		SQL:{
				 Connection connection = DBConnection.createConnection();
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 String sql ="select fgd.driver_id,fdb.delivery_boy_name,fdb.delivery_boy_phn_number,"
							+" fdb.delivery_boy_status_id,fdb.delivery_boy_user_id "
							+" from fapp_delivery_boy fdb "
							+" join fapp_group_driver fgd "
							+" on fgd.driver_id = fdb.delivery_boy_id and fgd.group_id = ?"
							+" and fdb.delivery_boy_status_id=2";
				 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, groupId);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						Driver newDriver = new Driver();
						newDriver.setDriverId(resultSet.getInt("driver_id"));
						newDriver.setDriverUserId(resultSet.getString("delivery_boy_user_id"));
						newDriver.setDriverName(resultSet.getString("delivery_boy_name"));
						newDriver.setStatusId(resultSet.getInt("delivery_boy_status_id"));
						newDriver.setContactNo(resultSet.getString("delivery_boy_phn_number"));
						
						drivers.add(newDriver);
					}
				} catch (Exception e) {
					System.out.println("Failed finding drivers::"+e.getMessage());
					connection.close();
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
    	System.out.println("- - - Getting free drivers size from group "+groupId+" is- - - >"+drivers.size());
    	return drivers;
    }
    
    
    public static Driver getBiker(ArrayList<Driver> drivers){
    	for(Driver driver : drivers)
    	System.out.println("Free Driver User ID: "+driver.getDriverUserId());
    	return drivers.get(0);
    }
    
    public static Driver findFirstDriver(String time,String driverIds){
		// Set<String> timeSlotList = new HashSet<String>();
		 Driver newDriver = null;
			Map<Integer,Integer> slotWrtDriverMap = new HashMap<Integer, Integer>();
			
			ArrayList<Integer> slotIds = new ArrayList<Integer>();
			try {
				Connection connection = DBConnection.createConnection();
				SQL:{
						PreparedStatement  preparedStatement = null;
						ResultSet resultSet = null;
						
						String sql ="select slot_id, driver_id, time_slot_from from fapp_driver_slots where driver_id IN "+driverIds;
						try {
							preparedStatement= connection.prepareStatement(sql);
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next() ) {
								String timeSlotFrom = resultSet.getString("time_slot_from");
								int slotId = resultSet.getInt("slot_id");
								int driverId =  resultSet.getInt("driver_id");
								slotWrtDriverMap.put(slotId, driverId);
								
								if(timeSlotFrom.startsWith(time)){
									//String driver = String.valueOf(slotId)+"$"+timeSlotFrom+"$"+String.valueOf(driverId);
									slotIds.add(slotId);
									//timeSlotList.add(driver);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(preparedStatement!=null){
								preparedStatement.close();
							}
							
						}
				}
				Collections.sort(slotIds);
				System.out.println("Priorities :: "+slotIds);
				//System.out.println(timeSlotList);
				int slotId = 0;
				for(int i=0;i<slotIds.size();i++){
					System.out.println("Priority "+i+" "+slotIds.get(i));
					slotId = slotIds.get(0);
				}
				/**
				 * Randomly select driver
				 */
				Random rand = new java.util.Random();
				int r ;
				r = rand.nextInt(slotIds.size());
				slotId = slotIds.get(r);
				
				int boyId =0;
				boyId = slotWrtDriverMap.get(slotId);
				System.out.println("Driver data from priority::"+boyId);
				
				System.out.println("boy id = "+boyId);
				SQL:{
					 PreparedStatement preparedStatement = null;
					 ResultSet resultSet = null;
					 String sql ="select delivery_boy_name,delivery_boy_phn_number,delivery_boy_user_id "
					 		+ "from vw_delivery_boy_data where delivery_boy_id=?";
					 try {
						preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setInt(1, boyId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							String driverUserId = (resultSet.getString("delivery_boy_user_id"));
							String driverName = (resultSet.getString("delivery_boy_name"));
							String contactNo =(resultSet.getString("delivery_boy_phn_number"));
							newDriver = new Driver(driverName, driverUserId, contactNo);
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
			return newDriver;
	 }
   
    public static String getOrderTimeSlot(String orderNo){
    	String timeSlot=null;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT time_slot from fapp_orders where order_no =?";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							timeSlot = resultSet.getString("time_slot");
						}
					} catch (Exception e) {
						System.out.println("Failed finding timeslot::"+e.getMessage());
						connection.close();
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
    	String[] timeSlotArray = timeSlot.split(" - ");
		timeSlot = timeSlotArray[0];
		System.out.println("Starting order time slot:: "+timeSlot);
    	return timeSlot;
    }
    
    public static boolean assignBiker(Driver driver,String orderNo , String kitchenName ){
    	boolean assigned = false;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				String sql = "UPDATE fapp_order_tracking SET driver_name =?,driver_number=?,driver_boy_user_id=? "
    						+ "where order_id=(select order_id from fapp_orders where order_no = ?) and kitchen_id = "
    						+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?)";
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, driver.getDriverName());
						preparedStatement.setString(2, driver.getContactNo());
						preparedStatement.setString(3, driver.getDriverUserId());
						preparedStatement.setString(4, orderNo);
						preparedStatement.setString(5, kitchenName);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							assigned = true;
						}
					} catch (Exception e) {
						System.out.println("Driver assign in Database failed due to::"+e.getMessage());
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
    	return assigned;
    }
    
	public static String getBody(Order order,String orderNo ,ArrayList<Kitchen> kitchenList , Customer customer ){
		String orderMail = "";
		ArrayList<Item> itemList = new ArrayList<Item>();
		orderMail = "ORDER DETAILS::\n ORDER NO = "+orderNo+"\n TIME SLOT = "+order.timeSlot+"\n MEAL TYPE = "+order.mealType
				+ "\n ORDER DATE = "+order.deliveryDateValue+"\n ORDER TIME = "+order.orderTimeStampValue+"\n PAYMENT TYPE = COD"
				+"\n\nPICK UP LIST DETAILS: ";
		for(Kitchen kitchen :kitchenList){
			orderMail += "\n\n KITCHEN NAME = "+kitchen.kitchenName +"\n KITCHEN CONTACT = "
					+kitchen.kitchenNo+"\n KITCHEN ADDRESS = "+kitchen.kitchenAddress+"\n LEAD TIME="+kitchen.leadTime+" Mins."
					+"\n PICK UP TIME = "+TimeCalculation.getNewTime(order.orderTimeStampValue, "00:"+kitchen.leadTime+":00");
			itemList = getKitchenItem(kitchen.kitchenId , orderNo);
			for(Item item : itemList){
				orderMail += "\n ITEM FROM KITCHEN: CUISIN NAME = "+item.cuisineName+"\t CATEGORY NAME = "+item.categoryName+
						"\t QUANTITY ="+item.quantity+"\t PRICE = "+item.price +
						"\n ITEM NAME = "+item.itemName+"\t DESCRIPTION = "+item.itemDescription;
			}
		}
		orderMail += "\n\nDROP DETAILS: \n CUSTOMER NAME ="+customer.userName+"\n ADDRESS = "+customer.userAddress+"\n CONTACT="+
				" = "+customer.userContact;
		
		return orderMail;
	}
	
	public static ArrayList<Kitchen> getPickUpList(String orderNo){
		ArrayList<Kitchen> kitchenList = new ArrayList<Kitchen>();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					Connection connection = DBConnection.createConnection();
					String sql = "select * from vw_kitchen_with_order where order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							Kitchen kitchen = new Kitchen();
							kitchen.kitchenName = resultSet.getString("kitchen_name");
							kitchen.kitchenAddress = resultSet.getString("address");
							kitchen.kitchenNo = resultSet.getString("mobile_no");
							kitchen.receivedTime = resultSet.getString("received_time");
							kitchen.kitchenId = resultSet.getInt("kitchen_id");
							kitchen.leadTime = resultSet.getInt("lead_time");
							kitchenList.add(kitchen);
						}
					} catch (Exception e) {
						System.out.println("*** EXCEPTION AT GETPICKUPLIST ***"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenList;
	}
	
	public static Customer getUserDetails(String orderNo){
	   Customer customer = new Customer();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					Connection connection =DBConnection.createConnection();
					String sql = "select * from vw_orders_delivery_address where order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							customer.userName = resultSet.getString("order_by");
							customer.userContact = resultSet.getString("contact_number");
							customer.userAddress = resultSet.getString("delivery_address");
						}
					} catch (Exception e) {
						System.out.println("*** EXCEPTION AT GETDROPADDRESS ***"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return customer;
	}
	
	public static ArrayList<Item> getKitchenItem(int kitchenId,String orderNo){
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					Connection connection = DBConnection.createConnection();
					String sql = "SELECT * FROM vw_order_item_details_list where kitchen_id = ? and order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						preparedStatement.setString(2, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							Item item = new Item();
							item.cuisineName = resultSet.getString("cuisin_name");
							item.categoryName = resultSet.getString("category_name");
							item.quantity = resultSet.getInt("qty");
							item.price = resultSet.getDouble("total_price");
							item.paymentType = "COD";//need to changed
							item.itemName = resultSet.getString("item_name");
							item.itemDescription = resultSet.getString("item_description");
							itemList.add(item);
						}
					} catch (Exception e) {
						System.out.println("*** EXCEPTION AT GETDROPADDRESS ***"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return itemList;
	}
	
	public static Order getOrderDetails(String orderNo){
		Order order = new Order();
		try {
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					Connection connection = DBConnection.createConnection();
					String sql = "select meal_type,time_slot,order_date::date AS ordered_date,"
							+ "order_date::timestamp as order_date from fapp_orders where order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							order.mealType = resultSet.getString("meal_type");
							order.timeSlot = resultSet.getString("time_slot");
							String userDate = resultSet.getString("ordered_date");
							Timestamp orderDate =  resultSet.getTimestamp("order_date");
							System.out.println("************"+new SimpleDateFormat(" HH:mm:ss").format(orderDate));
							order.orderTimeStampValue = new SimpleDateFormat(" HH:mm:ss").format(orderDate);
						//	order.leadTime = TimeCalculation.getNewTime(DateFormattor.toStringDate(userDate), "00:"+pickUpTime+":00");
							order.deliveryDateValue = DateFormattor.toStringDate(userDate);
						}
					} catch (Exception e) {
						System.out.println("*** EXCEPTION AT ORDER DETAILS ***"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
					
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return order;
	}
	
	
    /** 
	 * Get user's ordered item list
	 * @param orderId
	 * @return parent call 185
	 */
	private static ArrayList<OrderItemDetailsBean> getOrderItemDetailList(String orderNo,String kitchenName){
		ArrayList<OrderItemDetailsBean> orderItemDetailsBeanList = new ArrayList<OrderItemDetailsBean>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					try {
						preparedStatement = connection.prepareStatement("SELECT * FROM vw_order_item_details_list"
								+ " WHERE order_no=? and kitchen_name=  ?");
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						while(resultSet.next()){
							OrderItemDetailsBean detailsBean =  new OrderItemDetailsBean();
							detailsBean.cuisineName = resultSet.getString("cuisin_name");
							detailsBean.cuisineId = resultSet.getInt("cuisine_id");
							detailsBean.categoryName = resultSet.getString("category_name");
							detailsBean.categoryId = resultSet.getInt("category_id");
							detailsBean.quantity = resultSet.getInt("qty");
							detailsBean.price = resultSet.getDouble("total_price");
							detailsBean.status = resultSet.getString("order_status_name");
							
							orderItemDetailsBeanList.add(detailsBean);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return orderItemDetailsBeanList;
	}
	
	/**
	 * Get user quantity List
	 * @param orderItemDetailList
	 * @return parent call 187
	 */
	private static  ArrayList<Integer> getUserQuantityList(ArrayList<OrderItemDetailsBean> orderItemDetailList){
    	ArrayList<Integer> userQuantityList = new ArrayList<Integer>();
    	for(OrderItemDetailsBean userQuantity : orderItemDetailList){
    		userQuantityList.add(userQuantity.quantity);
    	}
    	return userQuantityList;
    }
	
	/**
	 * Get kitchen stock quantity List
	 * @param kitchenId
	 * @param orderItemDetailList
	 * @return parent call from 189
	 */
	private static ArrayList<Integer> getkitchenStockQuantity(String kitchenName, ArrayList<OrderItemDetailsBean> orderItemDetailList){
    	ArrayList<Integer> stockInKitchen =  new ArrayList<Integer>();
    	try {
				SQL:{
    					Connection connection = DBConnection.createConnection();
	    				PreparedStatement preparedStatement = null;
	    				ResultSet resultSet = null;
	    				String sql= "select category_stock from fapp_kitchen_stock "
									+" where kitchen_cuisine_id =  ?"
									+" and kitchen_category_id = ?"
									+" and kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?) ";
	    				try {
	    					preparedStatement =  connection.prepareStatement(sql);
	    						for(OrderItemDetailsBean items : orderItemDetailList){
									preparedStatement.setInt(1, items.cuisineId);
									preparedStatement.setInt(2, items.categoryId);
									preparedStatement.setString(3, kitchenName);
								
									resultSet = preparedStatement.executeQuery();
									if (resultSet.next()) {
										stockInKitchen.add(resultSet.getInt("category_stock"));
									}
								}
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("ERROR DUE TO:"+e.getMessage());
							e.printStackTrace();
						}finally{
							if(connection != null){
								connection.close();
							}
						}
	    		}
			} catch (Exception e) {
				// TODO: handle exception
			}

		//System.out.println("Kitchen Stock list::"+stockInKitchen);
    	return stockInKitchen;
    }
	
	/**
	 * Get updated stock List for kitchen
	 * @param userStockList
	 * @param kitchenStockList
	 * @return parent call 191
	 */
	public static ArrayList<Integer> updatedKitchenStock(ArrayList<Integer> userStockList, ArrayList<Integer> kitchenStockList){
		ArrayList<Integer> updatedStockList = new ArrayList<Integer>();
		
		for(int i=0 ; i< kitchenStockList.size() && i<userStockList.size() ;i++){
			
			Integer upStock = kitchenStockList.get(i)- userStockList.get(i);
			updatedStockList.add(upStock);
		}
		//System.out.println("Updated Stock List->"+updatedStockList);
		return updatedStockList;
	}
	
	/**
	 * Get kitchen stock quantity List
	 * @param kitchenId
	 * @param orderItemDetailList
	 * @return go to line 193
	 */
	private static ArrayList<Integer> getkitchenStockIdList(String kitchenName, ArrayList<OrderItemDetailsBean> orderItemDetailList){
    	ArrayList<Integer> kitchenStockIdList =  new ArrayList<Integer>();
    	try {
				SQL:{
    					Connection connection = DBConnection.createConnection();
	    				PreparedStatement preparedStatement = null;
	    				ResultSet resultSet = null;
	    				String sql= "select kitchen_stock_id from fapp_kitchen_stock "
									+" where kitchen_cuisine_id =  ?"
									+" and kitchen_category_id = ?"
									+" and kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
	    				try {
	    					preparedStatement =  connection.prepareStatement(sql);
	    						for(OrderItemDetailsBean items : orderItemDetailList){
									preparedStatement.setInt(1, items.cuisineId);
									preparedStatement.setInt(2, items.categoryId);
									preparedStatement.setString(3, kitchenName);
								
									resultSet = preparedStatement.executeQuery();
									if (resultSet.next()) {
										kitchenStockIdList.add(resultSet.getInt("kitchen_stock_id"));
									}
								}
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("ERROR DUE TO:"+e.getMessage());
							e.printStackTrace();
						}finally{
							if(connection != null){
								connection.close();
							}
						}
	    		}
			} catch (Exception e) {
				// TODO: handle exception
			}

		//System.out.println("Kitchen Stock Id list::"+kitchenStockIdList);
    	return kitchenStockIdList;
    }
	
	/**
	 * Update kitchen stock
	 * @param stockIdList
	 * @param updateStockValueList parent call from 195
	 */
	public static void updateStockKitchen(ArrayList<Integer> stockIdList, ArrayList<Integer> updateStockValueList){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_kitchen_stock "
								+" SET category_stock = ?"
							    +" WHERE kitchen_stock_id = ?";
					
					try {
						preparedStatement =  connection.prepareStatement(sql);
						
						for(int i = 0 ; i< updateStockValueList.size() ; i++){
							
							preparedStatement.setInt(1, updateStockValueList.get(i));
							preparedStatement.setInt(2, stockIdList.get(i));
							
							preparedStatement.addBatch();
						}
						int [] count = preparedStatement.executeBatch();
				    	   
				    	   for(Integer integer : count){
				    		   
				    		   System.out.println(integer);
				    		   // System.out.println("Kitchen Stock is Updated... ");  
				    	   }
					} catch (Exception e) {
						System.out.println("ERROR Due to:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Receive order b kitchen
	 * @param neworderbean
	 * @return parent call from 199
	 */
	private static Boolean receiveOrder(String orderNo , String kitchenName){
		Boolean received = false;
		System.out.println("- - - Updating table order tracking for kitchen "+kitchenName +"- - - ");
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET received = 'Y',received_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				/*String sqlSubs = "UPDATE fapp_order_tracking SET received = 'Y',received_time=current_timestamp "
						   + " WHERE subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";*/
				
				try {
					/*if(orderNo.startsWith("REG")){
						preparedStatement =  connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
					}else{
						preparedStatement =  connection.prepareStatement(sqlSubs);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
					}*/
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				//System.out.println(preparedStatement);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					received =  true;
					
				}
				} catch (Exception e) {
					System.out.println(e);
					connection.rollback();
					//connection.close();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
		
			SQL:{
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id = 8"
							   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) ";
					try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
						received =  true;
					}
					} catch (Exception e) {
						System.out.println(e);
						connection.rollback();
						connection.close();
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
		System.out.println("- - - Updation table order tracking for kitchen "+kitchenName +" is - - - "+received);
		return received;
	}
	
	private static Boolean receiveSubscriptionOrder(String orderNo, String kitchenName){
		Boolean received = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sqlSubs = "UPDATE fapp_order_tracking SET received = 'Y',received_time=current_timestamp "
							   + " WHERE subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?) "
							   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
					try {
						preparedStatement = connection.prepareStatement(sqlSubs);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						int updatedRow = preparedStatement.executeUpdate();
						if(updatedRow>0){
							received =  true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return received;
	}
	
	public static String[] getDriverNameNum(String subsNo){
		String[] details = new String[2];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(subsNo.startsWith("SUB")){
						sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
								+" WHERE delivery_boy_id = (select DISTINCT delivery_boy_id from fapp_subscription_meals where subscription_no = ? "
								+ " and is_assigned='Y' )";
					}else{
						sql = "SELECT delivery_boy_name,delivery_boy_phn_number FROM fapp_delivery_boy"
								+" WHERE delivery_boy_user_id = (select driver_boy_user_id from fapp_order_tracking where order_id = "
								+ " (select order_id from fapp_orders where order_no =?) ) ";
								
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, subsNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							details[0] = resultSet.getString("delivery_boy_name");
							details[1] = resultSet.getString("delivery_boy_phn_number");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return details;
	}
	
	
	
	/**
	 * Check if all kicthen accepts order or not
	 * @param orderid
	 * @return parent call from 203
	 */
	private static Boolean isAllKitchenReceive(String orderNo){
		Integer totalOrders = 0,totalReceivedOrders = 0 ;
		System.out.println("- - Check if all kitchen accepted that order or not..");
		try {
			Connection connection = DBConnection.createConnection();	
			SQL:{  				 
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
							 	+" count(ORDER_ID)AS total_order "
								+" from fapp_order_tracking "
								+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)";
					
					String sqlSub = "SELECT "
						 	+" count(subscription_id)AS total_order "
							+" from fapp_order_tracking "
							+" where subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?)";
				 try {
					 
					 if(orderNo.startsWith("REG")){
						 preparedStatement =  connection.prepareStatement(sql);
						 preparedStatement.setString(1, orderNo);
					 }else{
						 preparedStatement =  connection.prepareStatement(sqlSub);
						 preparedStatement.setString(1, orderNo);
					 }
					/*preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);*/
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						totalOrders = resultSet.getInt("total_order");
					}
				} catch (Exception e) {
					// TODO: handle exceptio
					e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				 System.out.println("total orders-->"+totalOrders);
			}
			
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
						 	+" count(received)AS total_order_received "
							+" from fapp_order_tracking "
							+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)"
							+ " AND received = 'Y'";
					
					String sqlSub = "SELECT "
						 	+" count(received)AS total_order_received "
							+" from fapp_order_tracking "
							+" where subscription_id = (SELECT subscription_id from fapp_subscription where subscription_no = ?)"
							+ " AND received = 'Y'";
					 try {
							
						 if(orderNo.startsWith("REG")){
							 preparedStatement =  connection.prepareStatement(sql);
								preparedStatement.setString(1, orderNo);
						 }else{
							 preparedStatement =  connection.prepareStatement(sqlSub);
								preparedStatement.setString(1, orderNo);
						 }
						 	/*preparedStatement =  connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);*/
							resultSet = preparedStatement.executeQuery();
							if (resultSet.next()) {
								totalReceivedOrders = resultSet.getInt("total_order_received");
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
					 System.out.println("Total received orders::"+totalReceivedOrders);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if( totalOrders!=0 && totalReceivedOrders!=0 && totalOrders == totalReceivedOrders){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Receive the order
	 * @param neworderbean parent call from 205
	 */
	private static Boolean changeOrderStatus(String orderNo){
		Boolean receivedOrder = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=2 WHERE order_id="
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
				try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
					//	Messagebox.show("Order Received Successfully!");
						receivedOrder = true;
						//System.out.println("Device regid::"+ getDeviceRegId(orderNo) );
						//sendMessage(getDeviceRegId(orderNo),orderNo,2);
						//sendMessageToMobile(getCustomerMobile(orderNo,"REGULAR"), orderNo, getOrderTime(orderNo, "REGULAR"), 2);
					}
				} catch (Exception e) {
					System.out.println(e);
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return receivedOrder;
	}
	
	public static String[] trackMyOrder(String subscriptionNo){
		String[] latlong = new String[3];
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement  preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "";
					if(subscriptionNo.startsWith("SUB")){
						sql = "select latitude,longitude,delivery_boy_track_address from fapp_subscription_meals where subscription_no = ?"
								+ " AND is_assigned= 'Y' AND delivery_boy_id IS NOT NULL";
					}else{
						sql = "select latitude,longitude,delivery_boy_address from fapp_order_tracking where order_id = "
								+ " (select order_id from fapp_orders where order_no = ?)";	
					}
					
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1,subscriptionNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							latlong[0] = resultSet.getString("latitude");
							latlong[1] = resultSet.getString("longitude");
							if(subscriptionNo.startsWith("SUB")){
								if(resultSet.getString("delivery_boy_track_address")!=null){
									latlong[2] = resultSet.getString("delivery_boy_track_address");
								}else{
									latlong[2] = "Order Delivered!";
								}
							}else{
								if(resultSet.getString("delivery_boy_address")!=null){
									latlong[2] = resultSet.getString("delivery_boy_address");
								}else{
									latlong[2] = "Order From EazeLyf!";
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return latlong;
	}
	
	
	
	/**
	 * WEB SERVICE FOR SAVE STATUS NAME AND TIMESTAMP
	 * @param input
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject saveTrackDetailStatus(JSONObject input, String kitchenName, String orderNo) throws JSONException{
		JSONObject status = new JSONObject();
		boolean updated = false;
		if(input.getString("status").equalsIgnoreCase("SHIPPED")){
			System.out.println("Driver "+input.getString("driver_name")+" is shipped from '"+kitchenName+"' successfully!!");
			try {
				SQL1:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking SET driver_pickup_time = current_timestamp "
							+ "WHERE order_id = (select order_id from fapp_orders where order_no = ?) "
							+ " AND kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?) AND rejected='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						//preparedStatement.setString(1, input.getString("driver_name"));
					//	preparedStatement.setString(2, input.getString("driver_number"));
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
							/*sendMessageForDeliveryBoy(getCustomerMobile(input.getString("order_id"), "REGULAR"),  
									input.getString("driver_number"), input.getString("driver_name"),
									"dummyLat","dummyLong","dummySubNo");*/
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
				
			 SQL2:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=5 WHERE order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
					//	preparedStatement.setString(1, input.getString("driver_name"));
					//	preparedStatement.setString(2, input.getString("driver_number"));
						preparedStatement.setString(1, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
							/*sendMessageForDeliveryBoy(getCustomerMobile(input.getString("order_id"), "REGULAR"),  
									input.getString("driver_number"), input.getString("driver_name"));
*/						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else if(input.getString("status").equalsIgnoreCase("COMPLETE")){
			System.out.println("Driver "+input.getString("driver_name")+" is completed the order from '"+kitchenName+"' successfully!!");
			
			try {
				SQL1:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking SET delivery_time = current_timestamp,delivered='Y' "
							+ " WHERE order_id = (select order_id from fapp_orders where order_no = ?) and kitchen_id="
							+ " (select kitchen_id from fapp_kitchen where kitchen_name = ?) AND rejected='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
							/*sendMessageToMobile(getCustomerMobile(orderNo, "REGULAR"), 
									orderNo, "orderTime" , 7);*/
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
			
				SQL2:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=6 WHERE order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
							/*sendMessageToMobile(getCustomerMobile(orderNo, "REGULAR"), 
									orderNo, "orderTime" , 7);*/
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
				if(isAllKitchenDelivered(orderNo)){
					SQL3:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=7,delivery_date_time=current_timestamp,is_message_send='Y' WHERE order_no = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
							sendMessageToMobile(getCustomerMobile(orderNo, "REGULAR"), 
									orderNo, "orderTime" , 7);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
				
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else if(input.getString("status").equalsIgnoreCase("REACHED_PICKUP")){
			System.out.println("Driver "+input.getString("driver_name")+" is reached from road runner to '"+kitchenName+"' successfully!!");
			try {
				SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking SET driver_arrival_time = current_timestamp "
							+ "WHERE order_id = (select order_id from fapp_orders where order_no = ?)"
							+ " AND kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?) AND rejected='N'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							updated = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			System.out.println("Driver "+input.getString("driver_name")+" is assigned from road runner to '"+kitchenName+"' successfully!!");
			try {
				SQL1:{
				Connection connection = DBConnection.createConnection();
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET driver_name = ?,driver_number = ?,driver_assignment_time=current_timestamp "
						+ "WHERE order_id = (select order_id from fapp_orders where order_no = ?) "
						+ " AND kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?) AND rejected='N'";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, input.getString("driver_name"));
					preparedStatement.setString(2, input.getString("driver_number"));
					preparedStatement.setString(3, orderNo);
					preparedStatement.setString(4, kitchenName);
					int count = preparedStatement.executeUpdate();
					if(count>0){
						updated = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			//updated = true;
		}
		/*if(saveOrderStatus(input))
			status.put("status", true);
		else
			status.put("status", false);
		return status;*/
		if(updated){
			status.put("status", true);
		}else{
			status.put("status", false);
		}
		return status;
	}
	
	/**
	 * 
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	public static JSONObject notifyLogistics(String orderNo , String kitchenName, String boyId) throws JSONException {
		JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		Boolean orderNotified = false;
		orderNotified = notifyOrder(orderNo, kitchenName);
		if(isAllKitchenNotified(orderNo)){
			changeOrderStatusToReady(orderNo);
		}
		if(orderNotified){
			if(callDriver(orderNo, kitchenName, boyId))
			notifiedJsonObject.put("status", true);
		}
		System.out.println(notifiedJsonObject);
		return notifiedJsonObject;
			/*try {
				responseJsonObject  = createShipmentByPost( createRoadRunnrJsonForAPI(orderNo, kitchenName) );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		JSONObject status = responseJsonObject.getJSONObject("status");
		String code = status.getString("code");
		Boolean trip = responseJsonObject.getBoolean("new_trip");
		String driverName = responseJsonObject.getString("driver_name");
		String driverPhn = responseJsonObject.getString("driver_phone") ;
		String roadrunnerId = responseJsonObject.getString("order_id") ;
		String deliveryId = responseJsonObject.getString("delivery_id");
		String orderId = responseJsonObject.getString("external_order_id") ;
		String trackingLink = responseJsonObject.getString("tracking_link") ;
			System.out.println("Response json- - - > > "+responseJsonObject.toString());
			JSONObject status = responseJsonObject.getJSONObject("status");
			String code = status.getString("code");
			if(code.equals("706")){
				System.out.println("706 status code");
				//if(responseJsonObject.getString("message").equals("No driver is available.")){
				JSONObject response1 = new JSONObject();
				JSONObject codeobj = new JSONObject();
				codeobj.put("code", 200);
				response1.put("status", codeobj);
				response1.put("new_trip", true);
				response1.put("driver_name", "No Driver");
				response1.put("driver_phone", "99999910493");
				response1.put("order_id", "87bfa2e4");
				response1.put("delivery_id", "15735898909");
				response1.put("external_order_id", orderNo);
				//response1.put( "tracking_link", "http://128.199.241.199/otrack?tracking_id=87bfa2e4");
				response1.put("tracking_link","http://128.199.241.199/tracking/order?id="+response1.getString("order_id"));
				notifiedJsonObject.put("status", response1);
				System.out.println("JSon - - ->"+response1);
				//}
			}else if(code.equals("200")){
				System.out.println("200 status code");
				String externalOrderID = responseJsonObject.getString("order_id");
				if( saveExterOrderId(externalOrderID, orderNo, kitchenName) ){
					System.out.println("External order id "+externalOrderID+" is saved!");
				}
				orderNotified = notifyOrder(orderNo, kitchenName);
				if(isAllKitchenNotified(orderNo)){
					orderNotified(orderNo);
				}
				notifiedJsonObject.put("status", responseJsonObject);
			}
		boolean inserted = false;
		inserted = saveOrderAndDriver(responseJsonObject, kitchenName);
		if(inserted){
			System.out.println("Order inserted with driver details!");
		}else{
			System.out.println("Order already placed!");
		}
		//sendMessageForDeliveryBoy(getCustomerMobile(orderNo, "REGULAR"), responseJsonObject.getString("driver_phone"), responseJsonObject.getString("driver_name"));
		//notifiedJsonObject.put("status", true);
		 * return notifiedJsonObject;
		*/
		
	}
	
	/**
	 * Update the driver assignment time for particular biker user id
	 * @param orderNo
	 * @param kitchenName
	 * @param boyID
	 * @return
	 */
	public static boolean callDriver(String orderNo , String kitchenName, String boyID){
		boolean called = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					/*String sql = "UPDATE fapp_order_tracking set driver_assignment_time = current_timestamp"
							+ " where order_id = (select order_id from fapp_orders where order_no=?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and driver_boy_user_id = ?";*/
					String sql = "UPDATE fapp_order_tracking set driver_assignment_time = current_timestamp"
							+ " where order_id = (select order_id from fapp_orders where order_no=?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						//preparedStatement.setString(3, boyID);
						int count = preparedStatement.executeUpdate();
						if(count>0){
							called = true;
						}
					} catch (Exception e) {
						System.out.println("Driver calling failed due to::"+e.getMessage());
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
		if(called){
			System.out.println("--Biker notified!! - - ");
			Biker biker = BikerDAO.getDriverDetailsFromKitchen(orderNo, kitchenName);
		//	sendMessageToMobile(biker.getBikerContact(), orderNo, getOrderTime(orderNo, "REGULAR"), 1);
		//	SendMessageDAO.sendMessageForDeliveryBoy(getCustomerMobile(orderNo, "REGULAR"), biker.getBikerContact(), biker.getBikerName(), null, null, null);
		}
		return called;
	}
	
	public static String getBikerNumber(String boyID){
		String mobileNo = "";
		try {
				Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select delivery_boy_phn_number from fapp_delivery_boy where delivery_boy_user_id = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, boyID);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							mobileNo = resultSet.getString("delivery_boy_phn_number");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Biker mobile no:: "+mobileNo);
		return mobileNo;
	}
	
	public static JSONObject deliveryToBoy(String orderNo , String kitchenName, String boyID)throws Exception{
		JSONObject deliveryBoyJsonObj = new JSONObject();
		if(isDeliveredToBoy(orderNo, kitchenName)){
			deliveryBoyJsonObj.put("status", true);
		}else{
			deliveryBoyJsonObj.put("status", false);
		}
		System.out.println(deliveryBoyJsonObj);
		return deliveryBoyJsonObj;
	}
	
	private static boolean saveExterOrderId(String externalOrderId, String orderNo , String kitchenName){
		boolean saved =  false;
		try {
			SQL:{
					Connection connection =DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_order_tracking set external_order_id = ? "
							+ "where order_id = (select order_id from fapp_orders where order_no=?) "
							+ "and kitchen_id = (select kitchen_id from fapp_kitchen where kitchen_name = ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, externalOrderId);
						preparedStatement.setString(2, orderNo);
						preparedStatement.setString(3, kitchenName);
						int count= preparedStatement.executeUpdate();
						if(count>0){
							saved = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return saved;
	}
	
	private static String[] getApiDetails(){
		String[] apiDetails = new String[2];
		try {
			SQL:{
					Connection connection =DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT client_id , client_secret from fapp_api_details";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							apiDetails[0] = resultSet.getString("client_id") ; 
							apiDetails[1] = resultSet.getString("client_secret");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("API DETILS->"+apiDetails[0]+" "+apiDetails[1]);
		return apiDetails;
	}

	public static String generateAuthToken() throws org.json.simple.parser.ParseException, JSONException{
		String authToken = "";
		String[] apiDetails = getApiDetails();
		try {
			  /*URL url = new URL(" https://apitest.roadrunnr.in/oauth/token?grant_type=client_credentials&client_"
				 +"id="+apiDetails[0]+"&client_secret="+apiDetails[1]);*/
			/* LIVE URL */
			//10j30V08qQFfc2E1DD8ujSTPmSXSEOzrft9VmyoS  yBKKMvMxHU4HrehbtkoOxRJkDnGo20CcCe8dFmwQ
			/*String urlstr ="http://roadrunnr.in/oauth/token?grant_type="
					+"client_credentials&client_id="+apiDetails[0]+"&client_secret="+apiDetails[1];*/
			/*URL url = new URL("http://roadrunnr.in/oauth/token?grant_type="
					+"client_credentials&client_id="+apiDetails[0]+"&client_secret="+apiDetails[1]);*/
			String urlstr = "https://runnr.in/oauth/token";
			URL url = new URL(urlstr.trim());
			 System.out.println("URL for token->"+url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			String output ;
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				org.json.simple.JSONObject json = (org.json.simple.JSONObject)new JSONParser().parse(output);
				String token = (String) json.get("access_token");
				authToken = "Token "+token ; 	
			}
		   conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {
			e.printStackTrace();
		  }
		System.out.println("access_token->"+authToken);
		return authToken;
	}
	
	private static JSONObject createShipmentByPost(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		//String ship = shipMent.toJSONString();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	   // System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//client = (DefaultHttpClient) wrapClient(client);
			/* TESTING URL */
			//String url="https://apitest.roadrunnr.in/v1/orders/ship";
			/* LIVE URL :  https://runnr.in/v1/orders/ship
			//String url="http://roadrunnr.in/v1/orders/ship";
			//New url*/
			String url ="https://runnr.in/v1/orders/ship";
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			//post.addHeader("Authorization" , generateAuthToken());
			post.addHeader("Authorization" ,getToken(false));
			
			post.setEntity(input);
			System.out.println("StringEntity - - ->"+input.toString());
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	  System.out.println("Line - - >"+line);
		    	  newJObject = new JSONObject(line);
		      }
		}catch(UnsupportedEncodingException e){
			
		}catch(JSONException e){
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newJObject;  
	}
	
	private static JSONObject createShipmentByPostTest(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		//String ship = shipMent.toJSONString();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	   // System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//client = (DefaultHttpClient) wrapClient(client);
			/* TESTING URL */
			String url="https://apitest.roadrunnr.in/v1/orders/ship";
			/* LIVE URL */
			//String url="http://roadrunnr.in/v1/orders/ship";
			//String url =" https://runnr.in/v1/orders/ship";
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			post.addHeader("Authorization" ,getToken(true));
			
			post.setEntity(input);
			System.out.println("StringEntity - - ->"+input.toString());
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	  System.out.println("Line - - >"+line);
		    	  newJObject = new JSONObject(line);
		      }
		}catch(UnsupportedEncodingException e){
			
		}catch(JSONException e){
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newJObject;  
	}
	
	public static String trackOrderFromRoadRunnrAPI(String orderid) throws ParseException, 
	org.json.simple.parser.ParseException, JSONException, IOException{
		JSONObject jObject = new JSONObject();
		//orderid = "d08f5fa8";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	    String url = "https://apitest.roadrunnr.in/v1/orders/"+orderid+"/track";
		System.out.println("URL-"+url);
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		String[] idSecret = new String[2];
		idSecret = getApiDetails();
		// optional default is GET
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Authorization", new Token().generateAuthToken(idSecret[0], idSecret[1]));
		conn.setRequestProperty("Accept", "application/json");
		//add request header
				int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			newJObject = new JSONObject(inputLine);
		}
		in.close();
		System.out.println("OUTPUT JSON:"+newJObject);
		String link =null;
		link = newJObject.getString("tracking_link");
		System.out.println("Track limk->"+link);
		return link;
	}
	
	private static boolean saveOrderAndDriver(JSONObject orderObj, String kitchenName) throws JSONException{
		
		boolean inserted  = false;
		if( !isOrderNoSaved(orderObj.getString("external_order_id")) ){
			try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						String sql = "INSERT INTO fapp_track_order( "
					            +"order_no, driver_name, driver_phone, external_order_id, "
					            +"delivery_id, kitchen_name) "
					            +"VALUES ( ?, ?, ?, ?, ?, ?)";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, orderObj.getString("external_order_id"));
							preparedStatement.setString(2, orderObj.getString("driver_name"));
							preparedStatement.setString(3, orderObj.getString("driver_phone"));
							preparedStatement.setString(4, orderObj.getString("order_id"));
							preparedStatement.setString(5, orderObj.getString("delivery_id"));
							preparedStatement.setString(6, kitchenName);
							int count = preparedStatement.executeUpdate();
							if(count>0){
								inserted = true;
							}
									
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}			
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			inserted = false;
		}
		return inserted;
	}
	
	private static boolean saveOrderStatus(JSONObject orderObj){
		boolean inserted  = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "INSERT INTO fapp_track_status( "
							+" order_no, status_name, status_time) "
							+" VALUES ( ?, ?, ?)";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderObj.getString("order_id"));
						preparedStatement.setString(2, orderObj.getString("status"));
						preparedStatement.setString(3, orderObj.getString("timestamp"));
						
						int count = preparedStatement.executeUpdate();
						if(count>0){
							inserted = true;
						}
								
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}			
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return inserted;
	}
	
	
	private static boolean isOrderNoSaved(String orderNo){
		boolean orderPlaced = false;
		ArrayList<String> orderNoList = new ArrayList<String>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT order_no from fapp_track_order ";
					try {
						preparedStatement= connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							orderNoList.add(resultSet.getString("order_no"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(orderNoList.contains(orderNo)){
			orderPlaced = true;
			System.out.println("Order already placed:"+orderPlaced);
			return orderPlaced;
		}else{
			return orderPlaced;
		}
	}
	
	/**
	 * Update order tracking with notify 'Y' and current timestamp for order id and kitchen
	 * @param orderNo
	 * @param kitchenName
	 * @return parent call from 199
	 */
	private static Boolean notifyOrder(String orderNo , String kitchenName){
		Boolean notified = false;
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET notify = 'Y',notified_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				try {
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					notified =  true;
					System.out.println("1. Notifed !!");
				}
				} catch (Exception e) {
					System.out.println(e);
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				}
		
			SQL:{
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id = 3"
							   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) ";
					try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
						notified =  true;
						System.out.println("2. order status change!!");
					}
					} catch (Exception e) {
						System.out.println(e);
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
					}	
		} catch (Exception e) {
			// TODO: handle exception
		}
		return notified;
	}
	
	/**
	 * Check if all kicthen accepts order or not
	 * @param orderid
	 * @return parent call from 203
	 */
	private static Boolean isAllKitchenNotified(String orderNo){
		Integer totalOrders = 0,totalNotifiedOrders = 0 ;
		
		try {
			Connection connection = DBConnection.createConnection();	
			SQL:{  				 
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
							 	+" count(ORDER_ID)AS total_order "
								+" from fapp_order_tracking "
								+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?) and rejected='N'";
				 try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						totalOrders = resultSet.getInt("total_order");
					}
				} catch (Exception e) {
					// TODO: handle exceptio
					e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				 System.out.println("total orders-->"+totalOrders);
			}
			
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
						 	+" count(notify)AS total_order_notified "
							+" from fapp_order_tracking "
							+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)"
							+ " AND notify = 'Y' AND rejected='N'";
					 try {
							preparedStatement =  connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);
							resultSet = preparedStatement.executeQuery();
							if (resultSet.next()) {
								totalNotifiedOrders = resultSet.getInt("total_order_notified");
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
					 System.out.println("Total notified orders::"+totalNotifiedOrders);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if( totalOrders!=0 && totalNotifiedOrders!=0 && totalOrders == totalNotifiedOrders){
			return true;
		}else{
			return false;
		}
	}
	
	private static Boolean isAllKitchenDelivered(String orderNo){
		Integer totalOrders = 0,totalDeliveredOrders = 0 ;
		
		try {
			Connection connection = DBConnection.createConnection();	
			SQL:{  				 
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
							 	+" count(ORDER_ID)AS total_order "
								+" from fapp_order_tracking "
								+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?) and rejected='N'";
				 try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						totalOrders = resultSet.getInt("total_order");
					}
				} catch (Exception e) {
					// TODO: handle exceptio
					e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
				 System.out.println("total orders-->"+totalOrders);
			}
			
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					
					String sql = "SELECT "
						 	+" count(order_delivery_time)AS total_order_delivered "
							+" from fapp_order_tracking "
							+" where ORDER_ID = (SELECT order_id from fapp_orders where order_no = ?)"
							+ " AND notify = 'Y' AND rejected='N' AND delivered ='Y'";
					 try {
							preparedStatement =  connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);
							resultSet = preparedStatement.executeQuery();
							if (resultSet.next()) {
								totalDeliveredOrders = resultSet.getInt("total_order_delivered");
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
					 System.out.println("Total deliverd orders::"+totalDeliveredOrders);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if( totalOrders!=0 && totalDeliveredOrders!=0 && totalOrders == totalDeliveredOrders){
			System.out.println("All items are delivered!!");
			return true;
		}else{
			System.out.println("Some items are not delivered!!");
			return false;
		}
	}
	
	/**
	 * Receive the order
	 * @param neworderbean parent call from 205
	 */
	private static Boolean changeOrderStatusToReady(String orderNo){
		Boolean notifiedOrder = false;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "UPDATE fapp_orders SET order_status_id=9 WHERE order_id="
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
				try {
					preparedStatement =  connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					int updatedRow = preparedStatement.executeUpdate();
					if(updatedRow>0){
					//	Messagebox.show("Order Received Successfully!");
						notifiedOrder = true;
						////System.out.println("Device regid::"+ getDeviceRegId(orderNo) );
						//sendMessage(getDeviceRegId(orderNo),orderNo,2);
						System.out.println("3. Final order status!!");
					}
				} catch (Exception e) {
					System.out.println(e);
				}finally{
					if(connection!=null){
						connection.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return notifiedOrder;
	}
	
	public static boolean isDeliveredToBoy(String orderNo , String kitchenName ){
		boolean isOrderGiven=false;
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
				
				PreparedStatement preparedStatement = null;
				String sql = "UPDATE fapp_order_tracking SET delivered_to_boy = 'Y',delivery_time=current_timestamp "
						   + " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) "
						   + " AND kitchen_id = (SELECT kitchen_id from fapp_kitchen where kitchen_name = ?)";
				
				try {
				preparedStatement =  connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				preparedStatement.setString(2, kitchenName);
				int updatedRow = preparedStatement.executeUpdate();
				if(updatedRow>0){
					isOrderGiven =  true;
					System.out.println("1. Order given to boy!!");
				}
				} catch (Exception e) {
					System.out.println(e);
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
		return isOrderGiven;
	}
	
	/**
	 * Getting the device reg id 
	 * @param orderId
	 * @return
	 */
	public static String getDeviceRegId(String orderNo ){
		String deviceregId = "";
		try {
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			SQL:{
					String sql = "SELECT device_reg_id FROM fapp_devices"
								+" WHERE email_id = (SELECT user_mail_id FROM fapp_orders"
								+ " WHERE order_id = (SELECT order_id from fapp_orders where order_no = ?) )";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							deviceregId = resultSet.getString("device_reg_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceregId;
	}
	
	/**
	 * Getting the device reg id 
	 * @param orderId
	 * @return
	 */
	public static String getDeviceRegIdKitchen(Integer kitchenId ){
		String deviceregId = "";
		try {
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			SQL:{
					String sql = "SELECT device_reg_id FROM fapp_devices"
								+" WHERE email_id = (SELECT email_id FROM fapp_kitchen"
								+ " WHERE kitchen_id = ? )";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							deviceregId = resultSet.getString("device_reg_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection !=null){
							connection.close();
						}
					}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceregId;
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
        	 System.out.println(status+" from given address!");
          // throw new Exception("Error from the API - response status: "+status);
        }
      }
      return null;
    }
    
    public static String[] fetchLatLongOfAddress(String address) throws Exception
    {
    	System.out.println("Calculating lat long from given address "+address+". . . ");
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
        	 System.out.println(status+" from given address!");
          // throw new Exception("Error from the API - response status: "+status);
        	  return new String[] {"LAT", "LONG"};
        }
      }
      return null;
    }
    
    public static String getAddressOfUser(String subNo){
    	String address = null;
    	String delAddress =  null;
    	String pin= null;
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "";
    				if(subNo.startsWith("SUB")){
    					sql = "SELECT delivery_address,pincode from fapp_subscription where subscription_no = ?";
    				}else{
    					sql = "SELECT delivery_address,pincode from fapp_order_user_details where order_id = "
    							+ " (select order_id from fapp_orders where order_no = ?)";
    				}
    				try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, subNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							delAddress = resultSet.getString("delivery_address");
							pin = resultSet.getString("pincode");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	address = delAddress +","+pin+","+"Kolkata";
    	return address;
    }
    /**
	 * Send notification to App
	 * @param deviceregId
	 * @throws IOException
	 */
	public static void sendMessage(String deviceregId , String orderNo,Integer orderStatusId) throws IOException
	{
		//String API_KEY = "AIzaSyCvaJt0tbW3Nn1pCPynPduVxo3T3l3_Yek"; //sender id got from google api console project(My)
		String API_KEY = "AIzaSyA03muwGMLqGmk2mwY3x1di5mI3jEVViqM";//(sir)
		String collpaseKey = "gcm_message"; //if messages are sent and not delivered yet to android device (as device might not be online), then only deliver latest message when device is online
		//String messageStr = "message content here"; //actual message content
		String messageStr = "";
		if(orderStatusId!=2){
			 messageStr = "New order "+ orderNo +" is assigned!";
		}
		else{
			 messageStr = "Your order "+ orderNo +" is in process!";
		}
			
		//String messageId = "APA91bGgGzVQWb88wkRkACGmHJROeJSyQbzLvh3GgP2CASK_NBsuIXH15HcnMta3e9ZXMhdPN6Z3FSD2Pezf6bhgUuM2CF74SgZbG4Zr57LA76VVaNvSi7XM7QEuAVLIiTsXnVq3QAUFDo-ynD316bF10JGT3ZOaSQ"; //gcm registration id of android device
		String messageId = deviceregId;
				
		Sender sender = new Sender(API_KEY);
		//Message.Builder builder = new Message.Builder();
		com.google.android.gcm.server.Message.Builder builder = new Builder();
		builder.collapseKey(collpaseKey);
		builder.timeToLive(30);
		builder.delayWhileIdle(true);
		builder.addData("message", messageStr);
		
		//Message message = builder.build();
		com.google.android.gcm.server.Message message = builder.build();
		
		List<String> androidTargets = new ArrayList<String>();
		//if multiple messages needs to be deliver then add more message ids to this list
		androidTargets.add(messageId);
		
		MulticastResult result = sender.send(message, androidTargets, 1);
		System.out.println("result = "+result);
		
		if (result.getResults() != null) 
		{
			System.out.println("Status:"+messageStr+" is sent to device reg id:"+messageId);
			int canonicalRegId = result.getCanonicalIds();
			System.out.println("canonicalRegId = "+canonicalRegId);
			
			if (canonicalRegId != 0) 
			{
            }
		}
		else 
		{
			int error = result.getFailure();
			System.out.println("Broadcast failure: " + error);
		}
	}
	

    
	public static void sendMessageToMobile(String recipient, String orderNo, String orderTime, Integer orderStatusId){
		if(SendMessageDAO.isSmsActive()){
			try {
				//String recipient = "+917872979469";
				//String message = orderNo+" is assigned for you!";
				String message = "";
				if(orderStatusId==1){
					if(orderNo.startsWith("SUB")){
						message = "New Subscription order "+orderNo+" is assigned to you :  "+orderTime+". Eazelyf.";
						//message = "New Subscription order "+ orderNo +" is assigned to you : "+orderTime+". Thnx & Rgds Eazelyf.";
					}else{
						message = "New Retail order "+orderNo+" is assigned to you : "+orderTime+". Eazelyf.";
						//message = "New order "+ orderNo +" is assigned to you : "+orderTime+". Thnx & Rgds Eazelyf.";
					}	
				}else if(orderStatusId==7){
					message = orderNo+" has been successfully delivered to you. Eazelyf.";
					//message = orderNo +" has been successfully delivered to you. Thnx & Rgds Eazelyf. ";
				}else{
					if(orderNo.startsWith("REG")){
						message = "Your order "+orderNo+" has been accepted. Eazelyf.";
						//message = "Your order "+orderNo+" is under process"+". Thnx & Rgds Eazelyf.";
						//Your order "+orderNo+" has been accepted.Thank you for choosing us. Have a great day! Thnx & Rgds Eazelyf.";
					}else{
						message = "Your order "+orderNo+" for subscription is accepted. Thank you for choosing us. Have a great day!. Eazelyf.";
						//message = "Your order "+orderNo+" for subscription is accepted. "
							//	+ "Thank you for choosing us. Have a great day! Thnx & Rgds Eazelyf.";
					}
				}
				/*
				 * Delivery boy ABCD having mobile no 91XXXXXXXX is assigned to deliver your order.Thnx & Rgds Eazelyf.
					ORDER/000001 has been successfully delivered to you.Thnx & Rgds Eazelyf.
				 */
				String username = "nextgenvision"; 
				String password = "sms@123";
				String senderId = "eazelyf";
				String requestUrl  = "http://fastsms.way2mint.com/SendSMS/sendmsg.php?" +
						 "uname=" + URLEncoder.encode(username, "UTF-8") +
						 "&pass=" + URLEncoder.encode(password, "UTF-8") +
						 "&send=" + URLEncoder.encode(senderId, "UTF-8") +
						 "&dest=" + URLEncoder.encode(recipient, "UTF-8") +
						 "&msg=" + URLEncoder.encode(message, "UTF-8") ;
				System.out.println("Message sent to mobile no::"+recipient+"::"+message);
				URL url = new URL(requestUrl);
				HttpURLConnection uc = (HttpURLConnection)url.openConnection();
				System.out.println("Message Response:::::"+uc.getResponseMessage());
				uc.disconnect();
				} catch(Exception ex) {
				System.out.println(ex.getMessage());
				}	
		}
		
	}
	
	private static void sendMessageForDeliveryBoy(String recipient, String deliveryBoyMobile, 
			String boyName, String latitude, String longitude ,String subscriptionNO){
		if(SendMessageDAO.isSmsActive()){
			try {
				//String recipient = "+917872979469";
				//String message = orderNo+" is assigned for you!";
				//String myAPI = "https://www.google.com/maps?q="+latitude+","+longitude+"&16z";
				String myAPI = "http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/map?id="+subscriptionNO;
				String message ="";
				if(latitude.contains("dummyLat") && longitude.contains("dummyLong")){
				 message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order. You may track your order "+myAPI+" Eazelyf.";
					//message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order.Thnx & Rgds Eazelyf.";
				}else{//you may track your order here
					 /*message =" Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile +" is assigned to deliver your order. Track order here "+
							 myAPI+"Thnx & Rgds Eazelyf.";*/
					 message = "Delivery boy "+boyName+" having mobile no "+deliveryBoyMobile+" is assigned to deliver your order. You may track your order "+myAPI+" Eazelyf.";
				}
			
				/*
				 * Delivery boy ABCD having mobile no 91XXXXXXXX is assigned to deliver your order.Thnx & Rgds Eazelyf.
					ORDER/000001 has been successfully delivered to you.Thnx & Rgds Eazelyf.
				 */
				String username = "nextgenvision"; 
				String password = "sms@123";
				String senderId = "eazelyf";
				String requestUrl  = "http://fastsms.way2mint.com/SendSMS/sendmsg.php?" +
						 "uname=" + URLEncoder.encode(username, "UTF-8") +
						 "&pass=" + URLEncoder.encode(password, "UTF-8") +
						 "&send=" + URLEncoder.encode(senderId, "UTF-8") +
						 "&dest=" + URLEncoder.encode(recipient, "UTF-8") +
						 "&msg=" + URLEncoder.encode(message, "UTF-8") ;
				System.out.println("Message sent to mobile no::"+recipient+"::"+message);
				URL url = new URL(requestUrl);
				HttpURLConnection uc = (HttpURLConnection)url.openConnection();
				System.out.println("Message Response:::::"+uc.getResponseMessage());
				uc.disconnect();
				} catch(Exception ex) {
				System.out.println(ex.getMessage());
				}	
		}
		
	}
	
	public static String getCustomerMobile(String orderNo,String orderType){
		String mob="";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT contact_number from fapp_orders where order_id = "
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
					
					String sqlSub = "SELECT contact_number from fapp_subscription where subscription_id = "
							 +" (SELECT subscription_id from fapp_subscription where subscription_no = ?)";
					try {
						if(orderType.equalsIgnoreCase("REGULAR")){
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);
						}else{
							preparedStatement = connection.prepareStatement(sqlSub);
							preparedStatement.setString(1, orderNo);
						}
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							mob = resultSet.getString("contact_number");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Contact number search failed due to:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Order from the mobile no:: "+mob);
		return mob;
	}
	
	private static String getKitchenMobile(Integer kitchenId){
		String mob="";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT mobile_no from fapp_kitchen where kitchen_id = ?";
							
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							mob = resultSet.getString("mobile_no");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Contact number search failed due to:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mob;
	}
	
	public static String getOrderTime(String orderNo,String orderType){
		String time="";
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT order_date from fapp_orders where order_id = "
							+ " (SELECT order_id from fapp_orders where order_no = ?)";
					
					String sqlSub = "SELECT subscription_date::date AS order_date from fapp_subscription where subscription_id = "
							+ " (SELECT subscription_id from fapp_subscription where subscription_no = ?)";
					try {
						if(orderType.equalsIgnoreCase("REGULAR")){
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, orderNo);
						}else{
							preparedStatement = connection.prepareStatement(sqlSub);
							preparedStatement.setString(1, orderNo);
						}
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							time = resultSet.getString("order_date");
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Order date search failed due to:"+e.getMessage());
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return time;
	}
	
	private static org.json.simple.JSONObject createRoadRunnrJsonForAPI(String orderNo , String kitchenName) throws JSONException{
		org.json.simple.JSONObject shipmentJSON = new org.json.simple.JSONObject();
		JSONObject kitchenDetails = new JSONObject();
		JSONObject dropDetailsJson = new JSONObject();
		JSONObject orderDetailsJson = new JSONObject();
		String createdDate = "";
		try {
			Connection connection = DBConnection.createConnection();
			SQLKitchenPICKUP:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select * from vw_kitchens_details where kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							//kitchenDetails.put("name", resultSet.getString("kitchen_name") );
							kitchenDetails.put("name", "EazeLyf" );
							kitchenDetails.put("phone_no", resultSet.getString("mobile_no") );
							kitchenDetails.put("email", resultSet.getString("email_id"));
							kitchenDetails.put("type", "merchant");
							kitchenDetails.put("external_id", orderNo);
							JSONObject geoJsonValue = new JSONObject();
							geoJsonValue.put("latitude", resultSet.getString("latitude"));
							geoJsonValue.put("longitude", resultSet.getString("longitude"));
							/*geoJsonValue.put("latitude", "12.9394382");
							geoJsonValue.put("longitude", "77.6269071");*/
							JSONObject cityJsonValue = new JSONObject();
							cityJsonValue.put("name", toCamelCase(resultSet.getString("city_name")));
							//cityJsonValue.put("name", "Bangalore");//HARD CODED NEED TO BE CHANGE
							JSONObject localityJsonValue = new JSONObject();
							localityJsonValue.put("name", toCamelCase(resultSet.getString("area_name")));
							//localityJsonValue.put("name", "Bellandur");//HARD CODED NEED TO BE CHANGE
							//JSONObject sublocalityJsonValue = new JSONObject();
							//sublocalityJsonValue.put("name", "8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							//sublocalityJsonValue.put("name", resultSet.getString("address"));
							JSONObject fulladdress = new JSONObject();
							fulladdress.put("address", resultSet.getString("address"));
							////fulladdress.put("address","8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							//fulladdress.put("locality", localityJsonValue);
							fulladdress.put("city", cityJsonValue);
							//fulladdress.put("sub_locality", sublocalityJsonValue);
							fulladdress.put("geo", geoJsonValue);
							
							kitchenDetails.put("full_address",fulladdress);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
			
			SQLUserDROP:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select * from vw_orders_delivery_address where order_no = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						dropDetailsJson.put("name", resultSet.getString("order_by") );
						dropDetailsJson.put("phone_no", resultSet.getString("contact_number") );
						dropDetailsJson.put("email", resultSet.getString("user_mail_id"));
						dropDetailsJson.put("type", "customer");
						dropDetailsJson.put("external_id", resultSet.getString("user_details_id"));
						//String[] latlong = new String[2];
							//latlong =	getLatLongPositions(resultSet.getString("delivery_address")+","+resultSet.getString("pincode")+","+resultSet.getString("city"));
						/*JSONObject geoJsonValue = new JSONObject();
						geoJsonValue.put("latitude", latlong[0]);
						geoJsonValue.put("longitude", latlong[1]);*/
						/*geoJsonValue.put("latitude", "12.935322");
						geoJsonValue.put("longitude", "77.618754");*/
						//String cityName = resultSet.getString("city");
						//cityJsonValue.put("name", resultSet.getString("city"));
						/*JSONObject localityJsonValue = new JSONObject();
						if( resultSet.getString("delivery_zone").equalsIgnoreCase("New Town")){
							localityJsonValue.put("name", "New Town(kolkata)");
						}else if(resultSet.getString("delivery_zone").equalsIgnoreCase("Salt Lake")){
							localityJsonValue.put("name", "Salt Lake City");
						}else{
							localityJsonValue.put("name",toCamelCase(resultSet.getString("delivery_zone")) );
						}*/
						//localityJsonValue.put("name", "Koramangala");//HARD CODED NEED TO BE CHANGE
						/*JSONObject sublocalityJsonValue = new JSONObject();
						sublocalityJsonValue.put("name", resultSet.getString("delivery_address"));*/
						//sublocalityJsonValue.put("name", "1st Block");
						//fulladdress.put("address","345,221st street,kormangala");
						//fulladdress.put("locality", localityJsonValue);
						//fulladdress.put("sub_locality", sublocalityJsonValue);
						//fulladdress.put("geo", geoJsonValue);
						JSONObject cityJsonValue = new JSONObject();
						cityJsonValue.put("name", "Kolkata");//HARD CODED NEED TO BE CHANGE
						JSONObject zipCodeJsonValue = new JSONObject();
						zipCodeJsonValue.put("code", resultSet.getString("pincode"));
						JSONObject fulladdress = new JSONObject();
						fulladdress.put("address", resultSet.getString("delivery_address"));
						fulladdress.put("zip_code", zipCodeJsonValue);
						fulladdress.put("city", cityJsonValue);
						dropDetailsJson.put("full_address",fulladdress);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
			SQLorderDetails:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select order_no,sum(total_price)AS total_price,order_date from vw_order_item_details_list "
						+ "where order_no =? and kitchen_name = ? group by order_no,order_date ";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					preparedStatement.setString(2, kitchenName);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						orderDetailsJson.put("order_id", resultSet.getString("order_no") );
						orderDetailsJson.put("order_value", resultSet.getString("total_price") );
						orderDetailsJson.put("amount_to_be_collected", resultSet.getString("total_price") );
						orderDetailsJson.put("amount_to_be_paid", resultSet.getString("total_price") );
						orderDetailsJson.put("note","Fragile");
						JSONObject ordertypejson = new JSONObject();
						ordertypejson.put("name", "CashOnDelivery");
						//orderDetailsJson.put("order_type", resultSet.getString("payment_name"));
						orderDetailsJson.put("order_type", ordertypejson);
						createdDate =  resultSet.getString("order_date");
						//orderDetailsJson.put("order_items", getOrderitemdetails(orderNo));
						orderDetailsJson.put("order_items", getOrderitemdetailsWithKitchen(orderNo, kitchenName));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		JSONObject pickupjson = new JSONObject();
		pickupjson.put("user", kitchenDetails);
		JSONObject dropjson = new JSONObject();
		dropjson.put("user", dropDetailsJson);
		shipmentJSON.put("pickup", pickupjson);
		shipmentJSON.put("drop", dropjson);
		shipmentJSON.put("order_details", orderDetailsJson);
		shipmentJSON.put("created_at",createdDate);
		shipmentJSON.put("callback_url","http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/trackstatus?id="+kitchenName+"&orderno="+orderNo);
		System.out.println("CALL BACK URL-"+shipmentJSON.get("callback_url"));
		System.out.println("Shipment - "+shipmentJSON.toString());
		return shipmentJSON;
	}
    
	public static JSONObject getCityList() throws JSONException{
		JSONArray cityList = new JSONArray();
		JSONObject cityJsonObject = new JSONObject();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT city_id,city_name from sa_city where is_active='Y'";
					try {
						preparedStatement =connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject cityJson = new JSONObject();
							cityJson.put("cityid", resultSet.getString("city_id"));
							cityJson.put("cityname", resultSet.getString("city_name"));
							cityList.put(cityJson);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("City list size:"+cityList.length());
		cityJsonObject.put("citylist", cityList);
		return cityJsonObject;
	}
	
	public static JSONObject getAreaList(String cityId) throws JSONException{
		JSONArray areaList = new JSONArray();
		JSONObject areaJsonObject = new JSONObject();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT area_id,area_name from sa_area where is_active='Y' and city_id= ?";
					try {
						preparedStatement =connection.prepareStatement(sql);
						preparedStatement.setInt(1, Integer.valueOf(cityId));
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject areaJson = new JSONObject();
							areaJson.put("areaid", resultSet.getString("area_id"));
							areaJson.put("areaname", resultSet.getString("area_name"));
							areaList.put(areaJson);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Area list size:"+areaList.length());
		areaJsonObject.put("arealist", areaList);
		return areaJsonObject;
	}
	
	public static JSONObject fetchZipCodes(String id) throws JSONException{
		JSONArray servingCodeList = new JSONArray();
		JSONObject zipcodeJson = new JSONObject();
		String zipcodes = null;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql ="select distinct serving_zipcodes from fapp_kitchen where area_id=? and is_active='Y'";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, Integer.valueOf(id) );
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							zipcodes = resultSet.getString("serving_zipcodes");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		String[] zipcodeList = zipcodes.split("/");
    	for(int i=0;i<zipcodeList.length;i++){
    		JSONObject zip = new JSONObject();
    		zip.put("zipcode", zipcodeList[i]);
    		servingCodeList.put(zip);
    	}
		return zipcodeJson.put("zipcodelist", servingCodeList);
	}
	
	public static String toCamelCase(String inputString) {
	       String result = "";
	       if (inputString.length() == 0) {
	           return result;
	       }
	       char firstChar = inputString.charAt(0);
	       char firstCharToUpperCase = Character.toUpperCase(firstChar);
	       result = result + firstCharToUpperCase;
	       for (int i = 1; i < inputString.length(); i++) {
	           char currentChar = inputString.charAt(i);
	           char previousChar = inputString.charAt(i - 1);
	           if (previousChar == ' ') {
	               char currentCharToUpperCase = Character.toUpperCase(currentChar);
	               result = result + currentCharToUpperCase;
	           } else {
	               char currentCharToLowerCase = Character.toLowerCase(currentChar);
	               result = result + currentCharToLowerCase;
	           }
	       }
	       return result;
	   }
	
	public static JSONObject getPostalCodes(String cityId, String pincode) throws JSONException{
		JSONObject jsonObject = new JSONObject();	
		ArrayList<String> zipcodelist = new ArrayList<String>();
		try {
				SQL:{
						Connection connection = DBConnection.createConnection();
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String sql = "SELECT serving_zipcodes FROM vw_city_with_zipcode_data where city_id = ? and serving_zipcodes like ?";
					//	String sql = "SELECT serving_zipcodes FROM vw_city_with_zipcode_data where serving_zipcodes like ?";
						try {
							preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setInt(1, Integer.valueOf(cityId));
							preparedStatement.setString(2, "%"+pincode+"%");
							resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								zipcodelist.add(resultSet.getString("serving_zipcodes"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(connection!=null){
								connection.close();
							}
						}
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
		JSONArray servingCodeList = new JSONArray();
		if(zipcodelist.size()>0){
			StringBuilder zipcodeBuilder = new StringBuilder();
			String tem = zipcodelist.toString();
			String fb = tem.replace("[", "");
			String bb = fb.replace("]", "");
			String cma = bb.replace(", ", "/");
			zipcodeBuilder.append(cma);
			String[] zipcodeList = zipcodeBuilder.toString().split("/");
	    	for(int i=0;i<zipcodeList.length;i++){
	    		JSONObject zip = new JSONObject();
	    		zip.put("zipcode", zipcodeList[i]);
	    		servingCodeList.put(zip);
	    	}
	    	return jsonObject.put("zipcodelist", servingCodeList);
		}else{
			return jsonObject.put("zipcodelist", servingCodeList);
		}
		
	}
	
	public static JSONObject checkOut(String userMailId,  String contactNumber, String guestName,
    		String cityName, String location, String pincode , 
    		Integer locationId,String mealType, String timeSlot, ArrayList<OrderItems> orderItemList,
    		String deliveryZone,String deliveryAddress,String instruction,String deliveryDay) throws Exception{
    	
    	boolean isGuestUser = false;java.util.Date delivery_Date = new java.util.Date();
    	JSONObject checkOutJsonObject = new JSONObject();
    	JSONObject isOrderPlaced = new JSONObject();
    	
    	boolean orderGenerated = false;
    	boolean userInserted = false;
    	boolean itemInserted = false;
    	boolean kitchenAssigned = false;
    	
    	if(deliveryDay!=null){
    		 delivery_Date = getDeliveryDate(deliveryDay);
    	}
    	if(guestName!=null && guestName.trim().length()!=0){
    		System.out.println("Guest user is placing order! ");
    		System.out.println("Order item size->"+orderItemList.size());
    		isGuestUser = true;
    	}else{
    		System.out.println("Registered user is placing order!");
    		System.out.println("Order item size->"+orderItemList.size());
    		isGuestUser = false;
    	}
    	
    	String orderBy = null ;
    	if(isGuestUser){
    		orderBy = guestName;
    		System.out.println("Order by guest->"+orderBy);	
    	}else{
    		orderBy = getUserName(contactNumber);
    		System.out.println("Order by registered->"+orderBy);
    		userMailId = getUserMailId(contactNumber);
    		System.out.println("Mail id of registered user->"+userMailId);
    	}
    	
    	for(OrderItems items : orderItemList){
    		System.out.println("Cui id- "+items.getCuisineId()+" cat id -"+items.getCategoryId()+" qty->"+items.getQuantity()+" price->"+items.getPrice());
    	}
    	
    	ArrayList<Integer> selectedKitchenIds = fetchKitchenIDwithUserItems(orderItemList,pincode);
    	if(selectedKitchenIds.size()>0){
    		System.out.println("Selected kitchens by serving order items are:="+selectedKitchenIds);
    	}else{
    		System.out.println("Not selected!");
    	}
    	
    	String[] deliveryLatLng = fetchLatLongOfAddress(deliveryAddress+","+cityName+","+pincode);
    	boolean kitchenFound = false;

    	ArrayList<Double> distanceList = new ArrayList<Double>();
    	
    	List<Integer> nearestKitchenIds = new ArrayList<Integer>();
    	 if(deliveryLatLng[0].equals("LAT") && deliveryLatLng[1].equals("LONG")){
    	    	System.out.println("Invalid address(404)!");
    	    	isOrderPlaced.put("message", "404");//404 Invalid address given!
    	 }else{
    	    System.out.println("Latitude: "+deliveryLatLng[0]+" and Longitude: "+deliveryLatLng[1]);
    	   // ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = fetchKitchenLatLongDetailsWithZipcode(pincode);
    	    ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = new ArrayList<KitchenDetailsBean>();
    	    if(selectedKitchenIds.size() > 0){
    	    	kitchenDetailsBeanList = fetchKitchenLatLong(selectedKitchenIds);
    	    	for(int i=0;i<kitchenDetailsBeanList.size() ; i++){
        	    	Double distanceValue = distance( Double.parseDouble(deliveryLatLng[0]) , Double.parseDouble(deliveryLatLng[1]) , 
        	    			kitchenDetailsBeanList.get(i).getLatitude(), kitchenDetailsBeanList.get(i).getLongitude(), "K") ;
        	    	System.out.println("Distance - - >"+distanceValue+"KM"+" kitchen id-"+kitchenDetailsBeanList.get(i).getKitchenId());
        	    	 kitchenDetailsBeanList.get(i).setDistance(distanceValue);
        	    }
        	    Collections.sort(kitchenDetailsBeanList) ;
        	    System.out.println("After sorting::");
        	   for(int i=0,j=kitchenDetailsBeanList.size();i<kitchenDetailsBeanList.size();i++,j--){
        		      kitchenDetailsBeanList.get(i).setKithcenNearestPriority(j);
        	   }
        	   //Long distance kitchen have serving priority 1 
        	    //short distance have serving priority > 1
        	   for(KitchenDetailsBean bean:kitchenDetailsBeanList){
        		   System.out.println("Dis-"+bean.getDistance()+" kitch-"+bean.getKitchenId()+" Serving priority-"+bean.getKithcenNearestPriority());
        	   }
        	   System.out.println("Order list size-"+orderItemList.size());
        	   System.out.println("After adding priority list size-"+kitchenDetailsBeanList.size());
        	   int orderId = generateOrderID(userMailId, contactNumber, mealType, timeSlot, orderBy, deliveryDay);
        	    if(kitchenDetailsBeanList.size()>0){
    				if(orderId!=0){
    					orderGenerated = true;
    				}else{
    					isOrderPlaced.put("message", "Order generation failed!");
    				}
    				if(orderGenerated){
    					userInserted = saveUserDetils(orderId, cityName, location, pincode, orderItemList, deliveryZone, deliveryAddress, instruction);
    				}else{
    					isOrderPlaced.put("message", "User insertion failed!");
    				}
        	    	if(userInserted){
        	    		itemInserted = saveItemsWithKitchen(orderId, orderItemList, selectedKitchenIds);
        	    	}else{
    					isOrderPlaced.put("message", "Item saved failed!");
    				}
        	    	if(itemInserted){
        	    		kitchenAssigned = assignOrdersWithKitchen(orderId, kitchenDetailsBeanList);
        	    	}else{
    					isOrderPlaced.put("message", "Order assignment to kitchen failed!");
    				}
        	    	if(kitchenAssigned){
        	    		System.out.println("200 order placed successfully");
        	    		isOrderPlaced.put("message", "200");//200 order placed successfully
        	    		isOrderPlaced.put("order",getRecentlyPlacedRegularOrderDetails(orderId));
        	    		
        	    	}
    				/*
    				 * isOrderPlaced.put("message", "kitchen found");
    				 * isOrderPlaced.put("kitchenid",
    				 * kitchenDetailsBeanList.toArray());
    				 */
    			} else {
    				System.out.println("760 No kitchens found!!");
    				isOrderPlaced.put("message", "760");//760 No kitchens found!!
    			}
    	    }else {
    	    	System.out.println("760 No kitchens found!!");
				isOrderPlaced.put("message", "760");
			}
    	   
    	    
    	 }
    	 
		return isOrderPlaced;
    }
	
	public static boolean assignOrdersWithKitchen(Integer orderID, ArrayList<KitchenDetailsBean> kitchenDetailsBeanList){
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					String sql = "INSERT INTO fapp_order_tracking(order_id, kitchen_id,serving_priority,sub_order_ref)VALUES (?, ?, ?,?);";
					try {
						preparedStatement = connection.prepareStatement(sql);
						for(KitchenDetailsBean bean : kitchenDetailsBeanList){
							preparedStatement.setInt(1, orderID);
							preparedStatement.setInt(2, bean.getKitchenId());
							preparedStatement.setInt(3, bean.getKithcenNearestPriority());
							preparedStatement.setString(4, orderID+"/"+bean.getKithcenNearestPriority());
							preparedStatement.addBatch();
						}
						int[] insertCount = preparedStatement.executeBatch();
						if(insertCount.length == kitchenDetailsBeanList.size()){
							return true ;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public static Integer generateOrderID(String userMailId,String contact,String mealType,
			String timeSlot, String orderBy,String deliveryDay){
		java.util.Date delivery_Date = new java.util.Date();
		int orderId = 0;
		if(deliveryDay!=null){
   		 delivery_Date = getDeliveryDate(deliveryDay);
		}	
		try {
    		Connection connection = DBConnection.createConnection();
    		//***SQL BLOCK STARTS HERE***//*
    		SQL:{
    				PreparedStatement preparedStatement= null;
    				String sql = "INSERT INTO fapp_orders(user_mail_id , contact_number, order_no, meal_type, time_slot,order_by,"
    						+ " delivery_date)"
							+ " VALUES (?,?, ?,?,?,?,?)";
    				try {
	    					preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	    					preparedStatement.setString(1, userMailId);
	    					if(contact!=null)
	    						preparedStatement.setString(2, contact);
	    					else
	    						preparedStatement.setString(2, " ");
	    					preparedStatement.setString(3, generateOrderNo());
	        				preparedStatement.setString(4, mealType);
	        				preparedStatement.setString(5, timeSlot);
	        				if(orderBy !=null){
	        					preparedStatement.setString(6, orderBy);
	        				}else{
	        					preparedStatement.setString(6, "");
	        				}
	        				if(delivery_Date!=null){
	        					preparedStatement.setDate(7, new java.sql.Date(delivery_Date.getTime()) );
	        				}else{
	        					preparedStatement.setNull(7, Types.NULL);
	        				}
	        				preparedStatement.execute();
							ResultSet resultSet = preparedStatement.getGeneratedKeys();
							if(resultSet.next()){
							    orderId = resultSet.getInt(1);
								System.out.println("Order created and Id is =====> "+orderId);
							}
	        				
					}  catch (Exception e) {
						e.printStackTrace();
					} finally{
						if(connection!=null){
							connection.close();
						}
					}	
    			}
    		/***SQL BLOCK END HERE***/
		} catch (Exception e) {
			
		}
		return orderId;
	}
	
	private static ArrayList<KitchenDetailsBean> fetchKitchenLatLongDetails(){
		ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = new ArrayList<KitchenDetailsBean>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT kitchen_id,latitude, longitude FROM fapp_kitchen where is_active='Y' ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							KitchenDetailsBean bean = new KitchenDetailsBean();
							bean.setKitchenId( resultSet.getInt("kitchen_id") );
							bean.setLatitude(resultSet.getDouble("latitude") );
							bean.setLongitude(resultSet.getDouble("longitude") );
							kitchenDetailsBeanList.add(bean);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenDetailsBeanList;
	}
	
	private static ArrayList<KitchenDetailsBean> fetchKitchenLatLongDetailsWithZipcode(String pincode){
		ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = new ArrayList<KitchenDetailsBean>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT kitchen_id,latitude, longitude FROM fapp_kitchen where is_active='Y'"
							+ " and serving_zipcodes like ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						if(pincode!=null && pincode.trim().length()!=0){
							preparedStatement.setString(1, "%"+pincode+"%");
						}else{
							preparedStatement.setString(1, "%%");
						}
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							KitchenDetailsBean bean = new KitchenDetailsBean();
							bean.setKitchenId( resultSet.getInt("kitchen_id") );
							bean.setLatitude(resultSet.getDouble("latitude") );
							bean.setLongitude(resultSet.getDouble("longitude") );
							kitchenDetailsBeanList.add(bean);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenDetailsBeanList;
	}
	
	public static ArrayList<KitchenDetailsBean> fetchKitchenLatLong(ArrayList<Integer> kitchenIDS){
    	ArrayList<KitchenDetailsBean> kitchenDetailsBeanList = new ArrayList<KitchenDetailsBean>();
    	StringBuilder kitchenIdsBuilder = new StringBuilder();
    	String tempKitchenId = kitchenIDS.toString();
    	String fb = tempKitchenId.replace("[", "(");
    	String bb =  fb.replace("]", ")");
    	String kitchenids = bb.toString();
    	System.out.println("kitchdnids"+kitchenids);
    	try {
			SQL:{
    				Connection connection = DBConnection.createConnection();
    				PreparedStatement preparedStatement = null;
    				ResultSet resultSet = null;
    				String sql = "SELECT distinct kitchen_id,latitude,longitude FROM fapp_kitchen "
    						+ " where is_active='Y' and kitchen_id IN "+kitchenids;
    				try {
						preparedStatement = connection.prepareStatement(sql);
						resultSet = preparedStatement.executeQuery();
							while (resultSet.next()) {
								KitchenDetailsBean bean = new KitchenDetailsBean();
								bean.setKitchenId(resultSet.getInt("kitchen_id"));
								bean.setLatitude(resultSet.getDouble("latitude"));
								bean.setLongitude(resultSet.getDouble("longitude"));
								kitchenDetailsBeanList.add(bean);
							}	
						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return kitchenDetailsBeanList;
    }
	
	private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}

		return (dist);
	}
	
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	public static String getToken(boolean isTestUrl){
		 String authToken="";
		 System.setProperty("jsse.enableSNIExtension","false");
			try{
			org.json.simple.JSONObject aouth = new org.json.simple.JSONObject();
			DefaultHttpClient client = new DefaultHttpClient();
			String url;
			if(isTestUrl){
				aouth.put("client_id","kkwJrVg65WdIRjMT6c6v7kRXHtwK8xHqCIlgpBLb");
				aouth.put("client_secret","vwLw3bNHTMe9hTy0LxSjiHt9bSRkq81eTfyzIzXu");
				aouth.put("grant_type", "client_credentials");
			}else{
				aouth.put("client_id","10j30V08qQFfc2E1DD8ujSTPmSXSEOzrft9VmyoS");
				aouth.put("client_secret","yBKKMvMxHU4HrehbtkoOxRJkDnGo20CcCe8dFmwQ");
				aouth.put("grant_type", "client_credentials");
			}
			//aouth.put("client_id", "10j30V08qQFfc2E1DD8ujSTPmSXSEOzrft9VmyoS");
			//aouth.put("client_secret", "yBKKMvMxHU4HrehbtkoOxRJkDnGo20CcCe8dFmwQ");
			/*aouth.put("client_id", "kkwJrVg65WdIRjMT6c6v7kRXHtwK8xHqCIlgpBLb");//TESTING CREDENTIAL
			aouth.put("client_secret", "vwLw3bNHTMe9hTy0LxSjiHt9bSRkq81eTfyzIzXu");//TESTING CREDENTIAL 
			aouth.put("grant_type", "client_credentials");*/
			
		//	System.out.println(aouth.toJSONString());
			
			
			if(isTestUrl){
				url = "https://apitest.roadrunnr.in/oauth/token";//TESTING url
			}else{
			    url = "https://runnr.in/oauth/token";//LIVE URL
			}
			
			//String url = "https://runnr.in/oauth/token";//LIVE URL
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(aouth.toJSONString());
			post.addHeader("Content-Type", "application/json");
			post.setEntity(input);
			HttpResponse response = client.execute(post);
		    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    String line = "";
		    
		    while ((line = rd.readLine()) != null) {
		  //  System.out.println(line);
		    org.json.simple.JSONObject json = (org.json.simple.JSONObject)new JSONParser().parse(line);
			String token = (String) json.get("access_token");
			authToken = "Token "+token ; 
		      }
			}catch (MalformedURLException e) {
				System.out.println("catch at Malformed url 9224"+e);
				//e.printStackTrace();

			  } catch (IOException e) {
				System.out.println("catch at Io url 9228"+e);
				//e.printStackTrace();

			 } catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("catch at Jsonparser url 9233"+e);
				 //e.printStackTrace();
			}
			System.out.println("tOKEN NAME=>"+authToken);
			return authToken;
	}
	
	
	
	/**
	 * 
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	public static JSONObject notifyLogistics1(String orderNo , String kitchenName) throws JSONException {
		JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		Boolean orderNotified = false;
		
		try {
				responseJsonObject  = createShipmentByPostTest( createRunnrJson(orderNo, kitchenName) );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Response json- - - > > "+responseJsonObject.toString());
			JSONObject status = responseJsonObject.getJSONObject("status");
			String code = status.getString("code");
			if(code.equals("706")){
				System.out.println("706 status code");
				
			}else if(code.equals("200")){
				System.out.println("200 status code");
				orderNotified = notifyOrder(orderNo, kitchenName);
				if(isAllKitchenNotified(orderNo)){
					changeOrderStatusToReady(orderNo);
				}
				notifiedJsonObject.put("status", responseJsonObject);
			}
		return notifiedJsonObject;
	}
	
	/**
	 * 
	 * @param orderNo
	 * @param kitchenName
	 * @return
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	public static JSONObject placeOrderToPickJi(String orderNo , String kitchenName) throws JSONException {
		JSONObject notifiedJsonObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		Boolean orderNotified = false;
		String pickJiOrderID = "";
		try {
				responseJsonObject  = placeOrderByPost(createPickJiJson(kitchenName, orderNo));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("Response json- - - > > "+responseJsonObject.toString());
			//JSONObject status = responseJsonObject.getJSONObject("status");
			String code = responseJsonObject.getString("responseCode");
			if(code.equals("200")){
				pickJiOrderID = responseJsonObject.getString("orderID");
				System.out.println("Pickji order ID:: "+pickJiOrderID);
				System.out.println(responseJsonObject.getString("responseMessage"));
				//notifiedJsonObject.put("status", responseJsonObject);
			}else {
				//notifiedJsonObject.put("status", responseJsonObject);
			}
			
		return responseJsonObject;
	}
	
	public static org.json.simple.JSONObject createRunnrJson(String orderNo , String kitchenName) throws JSONException{
		org.json.simple.JSONObject shipmentJSON = new org.json.simple.JSONObject();
		int nextkitchenId;
		JSONObject kitchenDetails = new JSONObject();
		JSONObject dropDetailsJson = new JSONObject();
		JSONObject orderDetailsJson = new JSONObject();
		ArrayList<Integer> priorityList = getPriorityList(orderNo);
		int kitchenPriority = getKitchenPriority(orderNo ,kitchenName);
		String createdDate = getOrderTime(orderNo, "REGULAR");
		System.out.println("Priority list-"+priorityList);
		System.out.println(kitchenName+" kitchen's pr. is-"+kitchenPriority);
		System.out.println(kitchenName+" is last kitchen->"+isLast(priorityList, kitchenPriority));
		boolean isLast = isLast(priorityList, kitchenPriority) ;
		if(isLast){
			System.out.println("Drop Customer address required!");
			String previds = findPreviousKitchenIds(orderNo, kitchenPriority);
			System.out.println("ALL IDS-"+previds);
			System.out.println("Pickup address creation . . .");
			JSONObject pickUpJson = createPickUpJson(orderNo, kitchenName);
			//System.out.println("Pick up json ->"+pickUpJson);
			System.out.println("Drop address creation . . .");
			JSONObject dropUpJson = createDropUpJson(orderNo);
			//System.out.println("Drop json - >"+dropUpJson);
			System.out.println("Order details creation . . .");
			JSONObject orderJson = createOrderDetailsJson(orderNo, previds);
			//System.out.println("Order details json->"+orderJson);
			
			JSONObject pickupjson = new JSONObject();
			pickupjson.put("user", pickUpJson);
			JSONObject dropjson = new JSONObject();
			dropjson.put("user", dropUpJson);
			shipmentJSON.put("pickup", pickupjson);
			shipmentJSON.put("drop", dropjson);
			shipmentJSON.put("order_details", orderJson);
			shipmentJSON.put("created_at",createdDate);
			shipmentJSON.put("callback_url","http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/trackstatus?id="+kitchenName+"&orderno="+orderNo);
			System.out.println("CALL BACK URL-"+shipmentJSON.get("callback_url"));
			//System.out.println("Shipment - "+shipmentJSON.toString());	
			return shipmentJSON;
		}else{
			
			System.out.println("This kitchen is not last kitchen...");
			nextkitchenId = findNextKitchenId(orderNo, (kitchenPriority+1) );// find the next nearest kitchen id by priority
			System.out.println("Next kitchen id = "+nextkitchenId);
			String previds = findPreviousKitchenIds(orderNo, kitchenPriority);// find previous kitchen ids by current kitchen's priority
			System.out.println("prev ids = "+previds);
			System.out.println("Pickup address creation . . .");
			JSONObject pickUpJson = createPickUpJson(orderNo, kitchenName);
			//System.out.println("Pick up json ->"+pickUpJson);
			System.out.println("Drop address creation . . .");
			JSONObject dropUpJson = createIntermediateDropUpJson(nextkitchenId, orderNo) ;
			//System.out.println("Drop json - >"+dropUpJson);
			System.out.println("Order details creation . . .");
			JSONObject orderJson = createOrderDetailsJson(orderNo, previds);
			//System.out.println("Order details json->"+orderJson);
			
			JSONObject pickupjson = new JSONObject();
			pickupjson.put("user", pickUpJson);
			JSONObject dropjson = new JSONObject();
			dropjson.put("user", dropUpJson);
			shipmentJSON.put("pickup", pickupjson);
			shipmentJSON.put("drop", dropjson);
			shipmentJSON.put("order_details", orderJson);
			shipmentJSON.put("created_at",createdDate);
			shipmentJSON.put("callback_url","http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/trackstatus?id="+kitchenName+"&orderno="+orderNo);
			System.out.println("CALL BACK URL-"+shipmentJSON.get("callback_url"));
			//System.out.println("Shipment - "+shipmentJSON.toString());
			return shipmentJSON;
		}
	}
	
 	private static org.json.simple.JSONObject createRoadRunnrJson(String orderNo , String kitchenName) throws JSONException{
		org.json.simple.JSONObject shipmentJSON = new org.json.simple.JSONObject();
		JSONObject kitchenDetails = new JSONObject();
		JSONObject dropDetailsJson = new JSONObject();
		JSONObject orderDetailsJson = new JSONObject();
		String createdDate = "";
		try {
			Connection connection = DBConnection.createConnection();
			SQLKitchenPICKUP:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select * from vw_kitchens_details where kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							//kitchenDetails.put("name", resultSet.getString("kitchen_name") );
							kitchenDetails.put("name", "EazeLyf" );
							kitchenDetails.put("phone_no", resultSet.getString("mobile_no") );
							kitchenDetails.put("email", resultSet.getString("email_id"));
							kitchenDetails.put("type", "merchant");
							kitchenDetails.put("external_id", orderNo);
							JSONObject geoJsonValue = new JSONObject();
							//geoJsonValue.put("latitude", resultSet.getString("latitude"));
							//geoJsonValue.put("longitude", resultSet.getString("longitude"));
							geoJsonValue.put("latitude", "12.9394382");
							geoJsonValue.put("longitude", "77.6269071");
							JSONObject cityJsonValue = new JSONObject();
							//cityJsonValue.put("name", toCamelCase(resultSet.getString("city_name")));
							cityJsonValue.put("name", "Bangalore");//HARD CODED NEED TO BE CHANGE
							JSONObject localityJsonValue = new JSONObject();
							//localityJsonValue.put("name", toCamelCase(resultSet.getString("area_name")));
							localityJsonValue.put("name", "Bellandur");//HARD CODED NEED TO BE CHANGE
							JSONObject sublocalityJsonValue = new JSONObject();
							//sublocalityJsonValue.put("name", "8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							sublocalityJsonValue.put("name", resultSet.getString("address"));
							JSONObject fulladdress = new JSONObject();
							fulladdress.put("address", resultSet.getString("address"));
							////fulladdress.put("address","8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							//fulladdress.put("locality", localityJsonValue);
							fulladdress.put("city", cityJsonValue);
							//fulladdress.put("sub_locality", sublocalityJsonValue);
							fulladdress.put("geo", geoJsonValue);
							
							kitchenDetails.put("full_address",fulladdress);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
			
			
			SQLUserDROP:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select * from vw_orders_delivery_address where order_no = ?";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						dropDetailsJson.put("name", resultSet.getString("order_by") );
						dropDetailsJson.put("phone_no", resultSet.getString("contact_number") );
						dropDetailsJson.put("email", resultSet.getString("user_mail_id"));
						dropDetailsJson.put("type", "customer");
						dropDetailsJson.put("external_id", resultSet.getString("user_details_id"));
						String[] latlong = new String[2];
							latlong =	getLatLongPositions(resultSet.getString("delivery_address")+","+resultSet.getString("pincode")+","+resultSet.getString("city"));
						JSONObject geoJsonValue = new JSONObject();
						geoJsonValue.put("latitude", latlong[0]);
						geoJsonValue.put("longitude", latlong[1]);
						/*geoJsonValue.put("latitude", "12.935322");
						geoJsonValue.put("longitude", "77.618754");*/
						JSONObject cityJsonValue = new JSONObject();
						//String cityName = resultSet.getString("city");
						//cityJsonValue.put("name", resultSet.getString("city"));
						cityJsonValue.put("name", "Kolkata");//HARD CODED NEED TO BE CHANGE
						JSONObject localityJsonValue = new JSONObject();
						if( resultSet.getString("delivery_zone").equalsIgnoreCase("New Town")){
							localityJsonValue.put("name", "New Town(kolkata)");
						}else if(resultSet.getString("delivery_zone").equalsIgnoreCase("Salt Lake")){
							localityJsonValue.put("name", "Salt Lake City");
						}else{
							localityJsonValue.put("name",toCamelCase(resultSet.getString("delivery_zone")) );
						}
						
						//localityJsonValue.put("name", "Koramangala");//HARD CODED NEED TO BE CHANGE
						JSONObject sublocalityJsonValue = new JSONObject();
						sublocalityJsonValue.put("name", resultSet.getString("delivery_address"));
						//sublocalityJsonValue.put("name", "1st Block");
						JSONObject fulladdress = new JSONObject();
						fulladdress.put("address", resultSet.getString("delivery_address"));
						//fulladdress.put("address","345,221st street,kormangala");
						fulladdress.put("locality", localityJsonValue);
						//fulladdress.put("sub_locality", sublocalityJsonValue);
						fulladdress.put("city", cityJsonValue);
						//fulladdress.put("geo", geoJsonValue);
						dropDetailsJson.put("full_address",fulladdress);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
			SQLorderDetails:{
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				String sql = "select order_no,sum(total_price)AS total_price,order_date from vw_order_item_details_list "
						+ "where order_no =? and kitchen_name = ? group by order_no,order_date ";
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					preparedStatement.setString(2, kitchenName);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						orderDetailsJson.put("order_id", resultSet.getString("order_no") );
						orderDetailsJson.put("order_value", resultSet.getString("total_price") );
						orderDetailsJson.put("amount_to_be_collected", resultSet.getString("total_price") );
						orderDetailsJson.put("amount_to_be_paid", resultSet.getString("total_price") );
						orderDetailsJson.put("note","Fragile");
						JSONObject ordertypejson = new JSONObject();
						ordertypejson.put("name", "CashOnDelivery");
						//orderDetailsJson.put("order_type", resultSet.getString("payment_name"));
						orderDetailsJson.put("order_type", ordertypejson);
						createdDate =  resultSet.getString("order_date");
						//orderDetailsJson.put("order_items", getOrderitemdetails(orderNo));
						orderDetailsJson.put("order_items", getOrderitemdetailsWithKitchen(orderNo, kitchenName));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		JSONObject pickupjson = new JSONObject();
		pickupjson.put("user", kitchenDetails);
		JSONObject dropjson = new JSONObject();
		dropjson.put("user", dropDetailsJson);
		shipmentJSON.put("pickup", pickupjson);
		shipmentJSON.put("drop", dropjson);
		shipmentJSON.put("order_details", orderDetailsJson);
		shipmentJSON.put("created_at",createdDate);
		shipmentJSON.put("callback_url","http://appsquad.cloudapp.net:8080/RESTfulExample/rest/category/trackstatus?id="+kitchenName+"&orderno="+orderNo);
		System.out.println("CALL BACK URL-"+shipmentJSON.get("callback_url"));
		System.out.println("Shipment - "+shipmentJSON.toString());
		return shipmentJSON;
	}
	
	/*public static int getKitchenPriority(String orderNo, String kitchenName){
		int priority = 0 ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql =  "SELECT serving_priority from fapp_order_tracking where order_id = "
							+ "(select order_id from fapp_orders where order_no = ?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and rejected= 'N' " ;
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							priority = resultSet.getInt("serving_priority");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return priority;
	}*/
	
	public static ArrayList<Integer> getPriorityList(String orderNo){
		ArrayList<Integer> priorityIdList = new ArrayList<Integer>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select serving_priority from fapp_order_tracking where order_id = "
							+ "(select order_id from fapp_orders where order_no = ?) and rejected= 'N' order by serving_priority asc ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							priorityIdList.add(resultSet.getInt("serving_priority"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return priorityIdList;
	}
	
	public static int getKitchenPriority(String orderNo, String kitchenName){
		int priority = 0 ;
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql =  "SELECT serving_priority from fapp_order_tracking where order_id = "
							+ "(select order_id from fapp_orders where order_no = ?) and kitchen_id="
							+ "(select kitchen_id from fapp_kitchen where kitchen_name = ?) and rejected= 'N' " ;
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setString(2, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							priority = resultSet.getInt("serving_priority");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return priority;
	}
	
	/*public static ArrayList<Integer> getPriorityList(String orderNo){
		ArrayList<Integer> priorityIdList = new ArrayList<Integer>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select serving_priority from fapp_order_tracking where order_id = "
							+ "(select order_id from fapp_orders where order_no = ?) and rejected= 'N' order by serving_priority asc ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							priorityIdList.add(resultSet.getInt("serving_priority"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return priorityIdList;
	}*/

	public static boolean isLast(ArrayList<Integer> priorityList , int priority){
		if(priority == priorityList.get(priorityList.size()-1))
			return true;
		return false;
	}

	public static int findNextKitchenId(String orderNO, int priority){
		int kitchenid = 0;
		try {
			SQL:{
					Connection connection  = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT kitchen_id from fapp_order_tracking where order_id = "
							+ "(select order_id from fapp_orders where order_no = ?) and serving_priority = ? ";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNO);
						preparedStatement.setInt(2, priority);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							kitchenid =  resultSet.getInt("kitchen_id");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenid ;
	}

	public static String findPreviousKitchenIds(String orderNo, int priority){
		String prevKitchenIds = null;
		ArrayList<Integer> prevKitIds = new ArrayList<Integer>();
		try {
			SQL:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "SELECT kitchen_id from fapp_order_tracking where order_id ="
							+ "(SELECT order_id FROM fapp_orders WHERE order_no = ?) and rejected = 'N' and serving_priority <= ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						preparedStatement.setInt(2, priority);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							prevKitIds.add(resultSet.getInt("kitchen_id"));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}		
		} catch (Exception e) {
			// TODO: handle exception
		}
		String previds = prevKitIds.toString();
		String fb = previds.replace("[", "(");
		String bb = fb.replace("]", ")");
		prevKitchenIds = bb;
		return prevKitchenIds;
	}
	
	
	/**
	 * PickJi Api Calling service
	 */
	private static JSONObject placeOrderByPost(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	    System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//client = (DefaultHttpClient) wrapClient(client);
			/* TESTING URL */
			//String url="https://apitest.roadrunnr.in/v1/orders/ship";
			/* LIVE URL :  https://runnr.in/v1/orders/ship
			//String url="http://roadrunnr.in/v1/orders/ship";
			//New url*/
			String url ="http://api.pickji.com/corporateapi/sandbox/placeorder";
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			//post.addHeader("Authorization" , generateAuthToken());
			//post.addHeader("Authorization" ,getToken(false));
			
			post.setEntity(input);
			//System.out.println("StringEntity - - ->"+input.toString());
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	//  System.out.println("Line - - >"+line);
		    	  newJObject = new JSONObject(line);
		      }
		}catch(UnsupportedEncodingException e){
			
		}catch(JSONException e){
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newJObject;  
	}
	
	
	public static JSONObject getBikersFromPickJi(org.json.simple.JSONObject shipMent) throws org.json.simple.parser.ParseException {
		JSONObject jObject = new JSONObject();
		String line = "";
	    JSONParser parser = new JSONParser();
   	    JSONObject newJObject = null;
   	    System.out.println("shipment object-->"+shipMent.toJSONString());
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//client = (DefaultHttpClient) wrapClient(client);
			/* TESTING URL */
			//String url="https://apitest.roadrunnr.in/v1/orders/ship";
			/* LIVE URL :  https://runnr.in/v1/orders/ship
			//String url="http://roadrunnr.in/v1/orders/ship";
			//New url*/
			String url ="http://api.pickji.com/corporateapi/sandbox/placeorder";
			HttpPost post = new HttpPost(url.trim());
			StringEntity input = new StringEntity(shipMent.toJSONString());
			post.addHeader("Content-Type", "application/json");
			//post.addHeader("Authorization" , generateAuthToken());
			//post.addHeader("Authorization" ,getToken(false));
			
			post.setEntity(input);
			System.out.println("StringEntity - - ->"+input.toString());
			HttpResponse response = client.execute(post);
		      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));   
		      while ((line = rd.readLine()) != null) {
		    	  System.out.println("Line - - >"+line);
		    	  newJObject = new JSONObject(line);
		      }
		}catch(UnsupportedEncodingException e){
			
		}catch(JSONException e){
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newJObject;  
	}
	
	
	/**
	 * PIckji JSON creation
	 * @return
	 * @throws JSONException 
	 */
	public static org.json.simple.JSONObject createPickJiJson(String kitchenName, String orderNo) throws JSONException{
		org.json.simple.JSONObject pickJiJson = new org.json.simple.JSONObject();
		PickJi pickji = getPickUp(kitchenName,orderNo);
		pickJiJson.put("appToken", pickji.getAppToken());
		pickJiJson.put("orderTitle", pickji.getOrderTitle());
		pickJiJson.put("pickupAddress", pickji.getPickupAddress());
		pickJiJson.put("pickupArea", pickji.getPickupArea());
		pickJiJson.put("pickupPincode", pickji.getPickupPincode());
		pickJiJson.put("pickupFromName", pickji.getPickupFromName());
		pickJiJson.put("pickupMobileNo", pickji.getPickupMobileNo());
		
		pickJiJson.put("deliveryAddress", pickji.getDeliveryAddress());
		pickJiJson.put("deliveryArea", pickji.getDeliveryArea());
		pickJiJson.put("deliveryPincode", pickji.getDeliveryPincode());
		pickJiJson.put("deliveryToName", pickji.getDeliveryToName());
		pickJiJson.put("deliveryMobileNo", pickji.getDeliveryMobileNo());
		pickJiJson.put("cashToCollect", pickji.getCashToCollect());
		
		pickJiJson.put("schedulePickupTime", pickji.getSchedulePickupTime());
		pickJiJson.put("scheduleDeliveryTime", pickji.getScheduleDeliveryTime());
		pickJiJson.put("quantityDetails", pickji.getQuantityDetails());
		pickJiJson.put("hasPillon", pickji.getHasPillon());
		
		return pickJiJson;
	}
	
	public static PickJi getPickUp(String kitchenName, String orderNo){
		PickJi pickUp = new PickJi();
		pickUp.setAppToken("c151b4ae5325443152060828f149a7a7");
		
		pickUp.setOrderTitle("EAZELYF ORDER");
		try {
			Connection connection = DBConnection.createConnection();
			SQL:{
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "Select address,pincode,kitchen_name,mobile_no from vw_kitchens_details where kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchenName);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							pickUp.setPickupAddress(resultSet.getString("address"));
							pickUp.setPickupArea(resultSet.getString("address"));
							pickUp.setPickupFromName(resultSet.getString("kitchen_name"));
							pickUp.setPickupMobileNo(resultSet.getString("mobile_no"));
							pickUp.setPickupPincode(resultSet.getString("pincode"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
		
			SQL:{
				 	PreparedStatement preparedStatement = null;
				 	ResultSet resultSet = null;
				 	String sql = "select * from vw_orders_delivery_address where order_no = ?";
				 	try {
						preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setString(1, orderNo);
						resultSet = preparedStatement.executeQuery();
						if(resultSet.next()){
							pickUp.setDeliveryAddress(resultSet.getString("delivery_address"));
							String city = resultSet.getString("city");
							String pincode = resultSet.getString("pincode");
							String zone = resultSet.getString("delivery_zone");
							pickUp.setDeliveryArea(zone+","+"Kolkata"+","+pincode);
							pickUp.setDeliveryPincode(pincode);
							pickUp.setDeliveryMobileNo(resultSet.getString("contact_number"));
							pickUp.setDeliveryToName(resultSet.getString("order_by"));
							String timeSlotValue = resultSet.getString("time_slot");
							String dateValue = resultSet.getString("order_date");
							String[] timeValues = DateTimeSlotFinder.findDateTime(dateValue, timeSlotValue);
							pickUp.setSchedulePickupTime(timeValues[0]);
							pickUp.setScheduleDeliveryTime(timeValues[1]);
						}
				 	} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(preparedStatement!=null){
							preparedStatement.close();
						}
					}
			}
			
			SQL:{
				 PreparedStatement preparedStatement = null;
				 ResultSet resultSet = null;
				 String sql = "select voi.category_name,voi.item_name,voi.qty,voi.total_price,final_price from vw_order_item_details_list voi "
							 +" join fapp_orders fo on fo.order_no = voi.order_no "
							 +"	 where voi.order_no =? and kitchen_name =?";
				 Set<Double> finalPrice = new HashSet<Double>();
				 ArrayList<PickjiItem> orders = new ArrayList<PickjiItem>();
				 try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, orderNo);
					preparedStatement.setString(2, kitchenName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						finalPrice.add(resultSet.getDouble("final_price"));
						pickUp.setCashToCollect(finalPrice.toString());
						PickjiItem order = new PickjiItem();
						order.categoryName = resultSet.getString("category_name");
						order.itemName = resultSet.getString("item_name");
						order.quantity = resultSet.getInt("qty");
						order.price = resultSet.getDouble("total_price");
						orders.add(order);
					}
					pickUp.setQuantityDetails(orders.toString());
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
		
		return pickUp;
	}
	
	/**
	 * 
	 * @param orderNo
	 * @param kitchen
	 * @return
	 */
	public static JSONObject createPickUpJson(String orderNo, String kitchen){
		JSONObject kitchenDetails = new JSONObject();
		try {
			SQLKitchenPICKUP:{
					Connection connection = DBConnection.createConnection();
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String sql = "select * from vw_kitchens_details where kitchen_name = ?";
					try {
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, kitchen);
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							//kitchenDetails.put("name", resultSet.getString("kitchen_name") );
							kitchenDetails.put("name", "EazeLyf" );
							kitchenDetails.put("phone_no", resultSet.getString("mobile_no") );
							kitchenDetails.put("email", resultSet.getString("email_id"));
							kitchenDetails.put("type", "merchant");
							kitchenDetails.put("external_id", orderNo);
							JSONObject geoJsonValue = new JSONObject();
							/*geoJsonValue.put("latitude", resultSet.getString("latitude"));
							geoJsonValue.put("longitude", resultSet.getString("longitude"));*/
							geoJsonValue.put("latitude", "12.9394382");
							geoJsonValue.put("longitude", "77.6269071");
							JSONObject cityJsonValue = new JSONObject();
						//	cityJsonValue.put("name", toCamelCase(resultSet.getString("city_name")));
							cityJsonValue.put("name", "Bangalore");//HARD CODED NEED TO BE CHANGE
							JSONObject localityJsonValue = new JSONObject();
							//localityJsonValue.put("name", toCamelCase(resultSet.getString("area_name")));
							localityJsonValue.put("name", "Bellandur");//HARD CODED NEED TO BE CHANGE
							JSONObject sublocalityJsonValue = new JSONObject();
							//sublocalityJsonValue.put("name", "8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							sublocalityJsonValue.put("name", resultSet.getString("address"));
							JSONObject fulladdress = new JSONObject();
							fulladdress.put("address", resultSet.getString("address"));
							////fulladdress.put("address","8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
							//fulladdress.put("locality", localityJsonValue);
							fulladdress.put("city", cityJsonValue);
							//fulladdress.put("sub_locality", sublocalityJsonValue);
							fulladdress.put("geo", geoJsonValue);
							
							kitchenDetails.put("full_address",fulladdress);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(connection!=null){
							connection.close();
						}
					}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return kitchenDetails;
	}
	
	public static JSONObject createDropUpJson(String orderNo){
		JSONObject dropDetailsJson = new JSONObject();
		try {
			SQLUserDROP:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select * from vw_orders_delivery_address where order_no = ?";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					dropDetailsJson.put("name", resultSet.getString("order_by") );
					dropDetailsJson.put("phone_no", resultSet.getString("contact_number") );
					dropDetailsJson.put("email", resultSet.getString("user_mail_id"));
					dropDetailsJson.put("type", "customer");
					dropDetailsJson.put("external_id", resultSet.getString("user_details_id"));
					String[] latlong = new String[2];
						latlong =	getLatLongPositions(resultSet.getString("delivery_address")+","+resultSet.getString("city")+","+resultSet.getString("pincode"));
					JSONObject geoJsonValue = new JSONObject();
					/*geoJsonValue.put("latitude", latlong[0]);
					geoJsonValue.put("longitude", latlong[1]);*/
					geoJsonValue.put("latitude", "12.935322");
					geoJsonValue.put("longitude", "77.618754");
					JSONObject cityJsonValue = new JSONObject();
					//String cityName = resultSet.getString("city");
					//cityJsonValue.put("name", resultSet.getString("city"));
					cityJsonValue.put("name", "Kolkata");//HARD CODED NEED TO BE CHANGE
					JSONObject localityJsonValue = new JSONObject();
					if( resultSet.getString("delivery_zone").equalsIgnoreCase("New Town")){
						localityJsonValue.put("name", "New Town(kolkata)");
					}else if(resultSet.getString("delivery_zone").equalsIgnoreCase("Salt Lake")){
						localityJsonValue.put("name", "Salt Lake City");
					}else{
						localityJsonValue.put("name",toCamelCase(resultSet.getString("delivery_zone")) );
					}
					
					//localityJsonValue.put("name", "Koramangala");//HARD CODED NEED TO BE CHANGE
					JSONObject sublocalityJsonValue = new JSONObject();
					sublocalityJsonValue.put("name", resultSet.getString("delivery_address"));
					//sublocalityJsonValue.put("name", "1st Block");
					JSONObject fulladdress = new JSONObject();
					fulladdress.put("address", resultSet.getString("delivery_address"));
					//fulladdress.put("address","345,221st street,kormangala");
					fulladdress.put("locality", localityJsonValue);
					//fulladdress.put("sub_locality", sublocalityJsonValue);
					fulladdress.put("city", cityJsonValue);
					//fulladdress.put("geo", geoJsonValue);
					dropDetailsJson.put("full_address",fulladdress);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(connection!=null){
					connection.close();
				}
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dropDetailsJson;
	}
	
	public static JSONObject createOrderDetailsJson(String orderNo , String previds){
		JSONObject orderDetailsJson = new JSONObject();
		String createdDate = ""; 
		try {
			SQLorderDetails:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select order_no,sum(total_price)AS total_price,order_date from vw_order_item_details_list "
					+ "where order_no =? and kitchen_id IN "+previds+"group by order_no,order_date ";
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, orderNo);
				//preparedStatement.setInt(2, kitchenId);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					orderDetailsJson.put("order_id", resultSet.getString("order_no") );
					orderDetailsJson.put("order_value", resultSet.getString("total_price") );
					orderDetailsJson.put("amount_to_be_collected", resultSet.getString("total_price") );
					orderDetailsJson.put("amount_to_be_paid", resultSet.getString("total_price") );
					orderDetailsJson.put("note","Fragile");
					JSONObject ordertypejson = new JSONObject();
					ordertypejson.put("name", "CashOnDelivery");
					//orderDetailsJson.put("order_type", resultSet.getString("payment_name"));
					orderDetailsJson.put("order_type", ordertypejson);
					createdDate =  resultSet.getString("order_date");
					//orderDetailsJson.put("order_items", getOrderitemdetails(orderNo));
					orderDetailsJson.put("order_items", getOrderitemdetailsWithKitchenIds(orderNo, previds));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(connection!=null){
					connection.close();
				}
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return orderDetailsJson;
	}
	
	public static JSONObject createIntermediateDropUpJson(int nextkitchenId, String orderNo){
		JSONObject dropDetailsJson = new JSONObject();
		try {
			SQLKitchenPICKUP:{
			Connection connection = DBConnection.createConnection();
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String sql = "select * from vw_kitchens_details where kitchen_id = ?";
					
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, nextkitchenId);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					dropDetailsJson.put("name", resultSet.getString("kitchen_name") );
					//kitchenDetails.put("name", "EazeLyf" );
					dropDetailsJson.put("phone_no", resultSet.getString("mobile_no") );
					dropDetailsJson.put("email", resultSet.getString("email_id"));
					dropDetailsJson.put("type", "merchant");
					dropDetailsJson.put("external_id", orderNo);
					JSONObject geoJsonValue = new JSONObject();
					//geoJsonValue.put("latitude", resultSet.getString("latitude"));
					//geoJsonValue.put("longitude", resultSet.getString("longitude"));
					geoJsonValue.put("latitude", "12.9394382");
					geoJsonValue.put("longitude", "77.6269071");
					JSONObject cityJsonValue = new JSONObject();
					//cityJsonValue.put("name", toCamelCase(resultSet.getString("city_name")));
					cityJsonValue.put("name", "Bangalore");//HARD CODED NEED TO BE CHANGE
					JSONObject localityJsonValue = new JSONObject();
					//localityJsonValue.put("name", toCamelCase(resultSet.getString("area_name")));
					localityJsonValue.put("name", "Bellandur");//HARD CODED NEED TO BE CHANGE
					JSONObject sublocalityJsonValue = new JSONObject();
					//sublocalityJsonValue.put("name", "8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
					sublocalityJsonValue.put("name", resultSet.getString("address"));
					JSONObject fulladdress = new JSONObject();
					fulladdress.put("address", resultSet.getString("address"));
					////fulladdress.put("address","8TqwewqH sMAIN 3RD CR3OSS NEAR POST OFFICE SARASWATHIPURAM MYSORE");
					fulladdress.put("locality", localityJsonValue);
					fulladdress.put("city", cityJsonValue);
					//fulladdress.put("sub_locality", sublocalityJsonValue);
					fulladdress.put("geo", geoJsonValue);
					
					dropDetailsJson.put("full_address",fulladdress);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(connection!=null){
					connection.close();
				}
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dropDetailsJson;
	}
	
	private static JSONArray getOrderitemdetailsWithKitchenIds(String orderNo, String kitchenIds){
    	JSONArray itemsDetailArray = new JSONArray();
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		
			SQL:{
				connection = DBConnection.createConnection();
					String cuisineSql="select category_id , qty, category_price " 
						  +" from fapp_order_item_details "
						  +" where order_id = "
						  +"(SELECT order_id FROM fapp_orders WHERE order_no = ?)"
						  +" and kitchen_id IN"+kitchenIds;
				try {
						preparedStatement = connection.prepareStatement(cuisineSql);
						preparedStatement.setString(1, orderNo);
						//preparedStatement.setInt(2, kitchenId);
						resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							JSONObject itemobject = new JSONObject();
							itemobject.put("quantity", resultSet.getInt("qty"));
							itemobject.put("price", resultSet.getDouble("category_price"));
							itemobject.put("item", getItemName(resultSet.getInt("category_id")));
							itemsDetailArray.put(itemobject);
						}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(connection!=null){
						connection.close();
					}
				}	
				
			}
		
		} catch (Exception e) {
		
		}
    	return itemsDetailArray;
    }
	
	private DefaultHttpClient getSecuredHttpClient(HttpClient httpClient) throws Exception {
		final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				
				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, new SecureRandom());
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(ccm, httpClient.getParams());
		} catch (Exception e) {
			throw e;
		}
	}
}


/*
 *
    	if(subscriptionBeanList.size()>0){
    	 for(SubscriptionBean bean : subscriptionBeanList){
	    		if(bean.subscriptionType.equals(subcriptionType) && bean.day.equals(day) ){
	    			System.out.println("Update block-----");	
	    			try {
	    	    		Connection connection = DBConnection.createConnection();
	    	    		SQL:{
	    	    				PreparedStatement preparedStatement= null;
	    	    				String sql = "UPDATE fapp_subscription SET day = ?, subscription_type = ? WHERE user_mail_id = ? AND subscription_id=?";
	    	    							
	    	    				try {
	    		    					preparedStatement = connection.prepareStatement(sql);
	    		    					
	    		        				preparedStatement.setString(1, day);
	    		        				preparedStatement.setString(2,subcriptionType);
	    		        				preparedStatement.setString(3, userMailId);
	    		        				preparedStatement.setInt(4, bean.subscriptionID);
	    		        				int count = preparedStatement.executeUpdate();
	    								if(count > 0){
	    									userDetailsInserted = true;
	    									itemDetailsInserted = true;
	    								}
	    		        				
	    						}  catch (Exception e) {
	    							e.printStackTrace();
	    						} finally{
	    							if(connection!=null){
	    								connection.close();
	    							}
	    						}	
	    	    			}
	    	    	
	    			} catch (Exception e) {
	    				
	    			}
	    			
	    			
	    		}else{
	    			
	    			try {
	    	    		Connection connection = DBConnection.createConnection();
	    	    		SQL:{
	    	    				PreparedStatement preparedStatement= null;
	    	    				String sql = "INSERT INTO fapp_subscription(user_mail_id, subscribed_by, contact_number,subscription_no,day,subscription_type)"
	    	    							+" VALUES (?, ?, ?, ?,?,?)";
	    	    				try {
	    		    					preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	    		    					preparedStatement.setString(1, userMailId);
	    		    					preparedStatement.setString(2, contactName);
	    		        				preparedStatement.setString(3, contactNumber);
	    		        				preparedStatement.setString(4, generateSubcriptionNo());
	    		        				preparedStatement.setString(5, day);
	    		        				preparedStatement.setString(6,subcriptionType);
	    		        				preparedStatement.execute();
	    								ResultSet resultSet = preparedStatement.getGeneratedKeys();
	    								if(resultSet.next()){
	    									int subscriptionId = resultSet.getInt(1);
	    									System.out.println("Subscription created and Id is =====> "+subscriptionId);
	    									
	    									userDetailsInserted = saveSubscriptionUserDetils(subscriptionId, contactName, cityName, location, landmark, 
	    		        							pincode, streetName, flatNumber, orderItemList);
	    									if(userDetailsInserted){
	    										itemDetailsInserted = saveSubscribedItemDetails(subscriptionId, orderItemList);
	    									}
	    									//if(userDetailsInserted && itemDetailsInserted){
	    							    		//isOrderPlaced.put("success", true );		
	    							    	//}else{
	    							    		//isOrderPlaced.put("success", false );
	    							    //	}
	    									userDetailsInserted = true;
	    									itemDetailsInserted = true;
	    								}
	    		        				
	    						}  catch (Exception e) {
	    							e.printStackTrace();
	    						} finally{
	    							if(connection!=null){
	    								connection.close();
	    							}
	    						}	
	    	    			}
	    	    		
	    			} catch (Exception e) {
	    				
	    			}
	    			
	    		}
	    	}
    	}else{
    		
    		try {
	    		Connection connection = DBConnection.createConnection();
	    		SQL:{
	    				PreparedStatement preparedStatement= null;
	    				String sql = "INSERT INTO fapp_subscription(user_mail_id, subscribed_by, contact_number,subscription_no,day,subscription_type)"
	    							+" VALUES (?, ?, ?, ?,?,?)";
	    				try {
		    					preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		    					preparedStatement.setString(1, userMailId);
		    					preparedStatement.setString(2, contactName);
		        				preparedStatement.setString(3, contactNumber);
		        				preparedStatement.setString(4, generateSubcriptionNo());
		        				preparedStatement.setString(5, day);
		        				preparedStatement.setString(6,subcriptionType);
		        				preparedStatement.execute();
								ResultSet resultSet = preparedStatement.getGeneratedKeys();
								if(resultSet.next()){
									int subscriptionId = resultSet.getInt(1);
									System.out.println("Subscription created and Id is =====> "+subscriptionId);
									
									userDetailsInserted = saveSubscriptionUserDetils(subscriptionId, contactName, cityName, location, landmark, 
		        							pincode, streetName, flatNumber, orderItemList);
									if(userDetailsInserted){
										itemDetailsInserted = saveSubscribedItemDetails(subscriptionId, orderItemList);
									}
									//if(userDetailsInserted && itemDetailsInserted){
							    		//isOrderPlaced.put("success", true );		
							    	//}else{
							    		isOrderPlaced.put("success", false );
							    	//}
								}
		        				
						}  catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(connection!=null){
								connection.close();
							}
						}	
	    			}
	    		
			} catch (Exception e) {
				
			}
    	}
 * 
 * */
