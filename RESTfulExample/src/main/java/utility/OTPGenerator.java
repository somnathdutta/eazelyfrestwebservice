package utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OTPGenerator {

	public static void main(String[] args) {
		System.out.println(generateOTP(4));
	}
	public static String generateOTP(int size) {

		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			// Generate 20 integers 0..20
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return generatedToken.toString();
	}
}
