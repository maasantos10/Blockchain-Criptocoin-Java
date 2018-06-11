package com.mas.blockchain.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHA256 {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SHA256.class);
	
	//Applies Sha256 to a string and returns the result. 
	public static String digestSha256(String input){
		StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return hexString.toString();
	}
	
	
}
