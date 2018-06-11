package com.mas.blockchain.util;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.mas.blockchain.transaction.Transaction;

/**
 * 
 * @author Marcos Santos
 * Description: Sample that show how can we work with blockchain and wallet.
 *
 */

public class Utils {

	public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	SHA256 sha256 = new SHA256();
	
	//To turn Object into a Json string with Google Gson
	public String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"  
	public String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
		public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
			Signature dsa;
			byte[] output = new byte[0];
			try {
				dsa = Signature.getInstance("ECDSA", "BC");
				dsa.initSign(privateKey);
				byte[] strByte = input.getBytes();
				dsa.update(strByte);
				byte[] realSig = dsa.sign();
				output = realSig;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return output;
		}
		
		//Verifies a String signature 
		public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
			try {
				Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
				ecdsaVerify.initVerify(publicKey);
				ecdsaVerify.update(data.getBytes());
				return ecdsaVerify.verify(signature);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static String getStringFromKey(Key key) {
			return Base64.getEncoder().encodeToString(key.getEncoded());
		}
		
		public String getMerkleRoot(ArrayList<Transaction> transactions) {
			int count = transactions.size();
			
			List<String> previousTreeLayer = new ArrayList<String>();
			for(Transaction transaction : transactions) {
				previousTreeLayer.add(transaction.transactionId);
			}
			List<String> treeLayer = previousTreeLayer;
			
			while(count > 1) {
				treeLayer = new ArrayList<String>();
				for(int i=1; i < previousTreeLayer.size(); i+=2) {
					treeLayer.add(sha256.digestSha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
				}
				count = treeLayer.size();
				previousTreeLayer = treeLayer;
			}
			
			String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
			return merkleRoot;
		}
}
