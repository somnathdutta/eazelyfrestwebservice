package utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Validation {
	
	/*public static void main(String[] args) {
		System.out.println("encrp"+encryption("somnath.dutta@appsquad.in"));
		System.out.println(generateCode());
	}*/
	
	public static String encryption(String password)
	{
	SecureRandom random = new SecureRandom();
	MessageDigest encrypt=null;
	try {
		encrypt = MessageDigest.getInstance("SHA1");
	encrypt.reset();
		encrypt.update(password.getBytes("UTF-8"));
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String salt = "";
	for(int i=0;i<4;i++)
		{salt=salt+(char)(random.nextInt(255)+1);
		}
	byte[] digest = encrypt.digest();
	String encryptPass = "";
	for (int i = 0; i < digest.length; i++) {
		encryptPass +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
	}
	return encryptPass;	
	}
	
	public static String generateCode() {
		String password = "";
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 8; i++) {
			password = password + (char) (random.nextInt(26) + 97);
		}
		return password;
	}
}
