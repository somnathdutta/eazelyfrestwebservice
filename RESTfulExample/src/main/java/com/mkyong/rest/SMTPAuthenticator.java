package com.mkyong.rest;

import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends javax.mail.Authenticator {
	String senderEmailID , senderPassword;
	public SMTPAuthenticator(String senderEmailID, String senderPassword){
		this.senderEmailID = senderEmailID;
		this.senderPassword = senderPassword;
	}
	
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(senderEmailID, senderPassword);
	}
}
