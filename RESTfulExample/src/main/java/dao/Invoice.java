package dao;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import pojo.User;

import com.mkyong.rest.Order;
import com.mkyong.rest.OrderItems;

public class Invoice {

	/**
	 * Generate invoice for completed order
	 * @param user
	 * @param order
	 * @param orderItemList
	 * @return
	 */
	 public static Boolean generateAndSendEmail(User user, Order order, ArrayList<OrderItems> orderItemList) { 
    	System.out.println("Invoice generation starting . . .!");
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
		System.out.println("\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		try {
			generateMailMessage.setFrom(new InternetAddress("somnathdutta048@gmail.com"));
			generateMailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmailId()) );
			generateMailMessage.setSubject("Order delivered from EazeLyf");
			String emailBody = bodyMessage(user, order, orderItemList);
			generateMailMessage.setContent(emailBody, "text/html");
			System.out.println("Mail Session has been created successfully..");
			// Step3
			System.out.println("\n 4th ===> Get Session and Send mail");
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
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	 
	 public static String bodyMessage(User user,Order order, ArrayList<OrderItems> orderItemList){
		 StringBuilder body = new StringBuilder();
		 String userName = user.getUserName(),contactNo =user.getContactNumber();
		// String name= "Appsquad" ,orderNo ="REG/23/33/00011",orderDate = "22-05-2016",deliveryDate ="23-05-2016";
		// String deliveryAddress = "Street No 0315, DH Block(Newtown), Action Area I, Newtown, New Town, West Bengal 700156, India";
		//Body creation
			System.out.println("\n 3rd ===> Email body creation . . ");
		 body.append("<html>"
		 		+ "<style>  #lll {"
			    + "\"margin-right:88px\"; "
			    + "\"margin-block-end: 7px\";"
			    + "\"padding-right: 250px\";"
			    +"}"
		 		+ "</style><body><table bgcolor=\"orange\" width=\"100%\"> "
		 		+"<tbody width=\"100%\"><tr> "
				+"<td width=\"100%\" bgcolor=\"#ffffff\" style=\"padding:0 10px\"> "
				+"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" style=\"max-width:600px\"><tbody> "
	+" <tr><td><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">"
	+" <tbody><tr><td align=\"center\" style=\"padding:24px 0\"><img src=\"http://appsquad.cloudapp.net:8080/Myapp/image/logo.png\" " 
	+" alt=\"\" style=\"border:0\" width=\"148\" height=\"37\" class=\"CToWUd\"></td></tr>"
	+"</tbody></table>"
	+"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">"
	+"<tbody><tr>"
	+"<td bgcolor=\"#35b0e9\" align=\"center\" style=\"padding:16px 10px;min-width:276px;border-radius:3px 3px 0 0;font:bold 16px/1.65 Arial;color:#ffffff;font-weight:bold\">"
	+"Hi ");
	body.append(userName);
	body.append( "<br>Your order is delivered to you successfully. Thanks for choosing us. </td>"
	+"</tr></tbody></table>");
		body.append("<table width=\"100%\">");
		 body.append("<tr><td bgcolor=\"orange\" colspan=\"2\"></td></tr>"
		 		+ "<tr><td height=\"35px\" width=\"50%\" ><b>Order ID:</b>");
         body.append(order.orderNo+"</td>");
         body.append("<td height=\"35px\" width=\"50%\" align=\"right\"><b>Placed on:</b>"+order.orderDateValue+"</td></tr><tr><td bgcolor=\"orange\" colspan=\"2\"></td></tr>" 
         + "<tr><td height=\"135px\"><b>Mobile No:"+contactNo+"</b></td>"
         		+ "<td height=\"135px\"><b>Delivery Address:</b>"+user.getDeliveryAddress()+"</td></tr></table>");
         body.append("<table border=\"1\" cellspacing=\"0\" width=\"100%\" cellpadding=\"10px\">");
         body.append("<tr><th>Cuisin Name</th><th>Category Name</th><th>Item Name</th><th>Item Description</th><th>Quantity</th><th>Price</th>");
         /*orderItemList = new ArrayList<OrderItems>();
         OrderItems orderItems = new OrderItems();
         orderItems.cuisineId = 1;
         orderItems.categoryId = 44;
         orderItems.price = 80.0;
         orderItems.quantity = 2;
         orderItemList.add(orderItems);
         
         OrderItems orderItems1 = new OrderItems();
         orderItems1.cuisineId = 2;
         orderItems1.categoryId = 45;
         orderItems1.price = 70.0;
         orderItems1.quantity = 3;
         orderItemList.add(orderItems1);*/
         double gTotal = 0;
         for (int i = 0; i < orderItemList
                 .size(); i++) {

             String cuisineName = orderItemList.get(i).cuisinName;

             String categoryName = orderItemList.get(i).categoryName;
             
             String itemName = orderItemList.get(i).itemName;
             
             String itemDescription = orderItemList.get(i).itemDescription;
             
             String qty = orderItemList.get(i).quantity.toString();
             
             String price = orderItemList.get(i).price.toString();
             
             gTotal = gTotal + (orderItemList.get(i).price* orderItemList.get(i).quantity);
             
             body.append("<tr>" + "<td align=\"center\">" + cuisineName + "</td>"
             		+ " <td align=\"center\">" + categoryName +"</td>" 
             		+ " <td align=\"center\">" + itemName +"</td>"
             		+ " <td align=\"center\">" + itemDescription +"</td>"
             		+ " <td align=\"center\">" + qty +"</td>"
                    + " <td align=\"center\">" + price +"</td>"
                     + "  " + "</tr>");
         }

         body.append("<tr>" + "<td></td><td></td><td></td><td></td><td>" + "Grand Total is:-  "
                 + "</td><td>" + gTotal + " "
                 + "</td></tr>");
         body.append("</table><br><p align=\"center\"><b>We look forward to serve you again soon.Team EazeLyf</b> </p>"
         		+ "<br><p align=\"center\"><font face=\"verdana\">For all other queries and to browse our top FAQs, please <a href=\"www.eazelyf.com\">click here</a></p> </table></body></html>");
         
         return body.toString();
	 }
}
