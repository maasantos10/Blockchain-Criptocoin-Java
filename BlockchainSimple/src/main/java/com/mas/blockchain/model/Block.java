package com.mas.blockchain.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mas.blockchain.ultil.SHA256;
import com.mas.blockchain.ultil.Utils;

public class Block {
	
	public final static Logger LOGGER = LoggerFactory.getLogger(Block.class);
	Utils utils = new Utils();
	SHA256 sha256 = new SHA256();
	
	public String hash;
	public String previousHash; 
	private String data; //our data will be a simple message.
	private long timeStamp; //as number of milliseconds since 1/1/1970.
	private int nonce;
	
	//Block Constructor.  
	public Block(String data,String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash(); //Making sure we do this after we set the other values.
	}
	
	//Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = sha256.digestSha256( String.valueOf( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				data) 
				);
		return calculatedhash;
	}

	//Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		String target = utils.getDificultyString(difficulty); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		LOGGER.info("Block Mined!!! : " + hash);
	}
	
}