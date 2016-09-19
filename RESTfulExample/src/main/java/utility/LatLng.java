package utility;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * This class will get the lat long values.
 * @author SANTHOSH REDDY MANDADI
 * @version 1.0
 * @since 20-Sep-2012
 */
public class LatLng
{
  /*public static void main(String[] args) throws Exception
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Please enter a location:");
    String postcode=reader.readLine();
    boolean latlngfound = true;
    String latLongs[] = getLatLongPositions(postcode);
    if(latLongs[0].equals("LAT") && latLongs[1].equals("LONG")){
    	System.out.println("Invalid addres!");
    	latlngfound = false;
    }else{
    	System.out.println("Latitude: "+latLongs[0]+" and Longitude: "+latLongs[1]);
    	latlngfound = true;
    }
    if(latlngfound){
    	System.out.println("Lat long found!!!");
    }else{
    	System.out.println("No Lat long found!!!");
    }
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
    	  System.out.println("OUTPUT:: "+status);
    	 // return new String[] {"LAT", "LONG"};
         //throw new Exception("Error from the API - response status: "+status);
    	  return new String[] {"LAT", "LONG"};
      }
    }
    return null;
  }
}