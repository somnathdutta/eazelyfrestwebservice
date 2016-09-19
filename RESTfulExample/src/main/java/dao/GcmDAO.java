package dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message.Builder;

public class GcmDAO {

	public static void main(String[] args) throws IOException {
		sendMessage(null, "1212", 1);
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
		//String messageId = deviceregId;
		String messageId = "APA91bEWwOkxeQsgVktxGJ_-QBxh9ARjnG4JXYhiWbYHDoqeLCYcng0fCWEPJMMmjPMAU-Y_Y0R9z5y6CpZuOcZsZg5-nBmcCs4QHszPapo1Mmre9Aemix3OmOpbUTF-9NjNiP6qGohS";		
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
}
