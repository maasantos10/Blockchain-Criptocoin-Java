package com.mas.blockchain.model;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mas.blockchain.util.Utils;

import com.mas.blockchain.transaction.Transaction;
import com.mas.blockchain.util.SHA256;

public class Block {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Block.class);
	public String hash;
	public String previousHash; 
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
	public long timeStamp; //as number of milliseconds since 1/1/1970.
	public int nonce;
	
	SHA256 sha256 = new SHA256();
	Utils utils = new Utils();
	
	//Block Constructor.  
	public Block(String previousHash ) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); //Making sure we do this after we set the other values.
	}
	
	//Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = sha256.digestSha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				merkleRoot
				);
		return calculatedhash;
	}
	
	//Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = utils.getMerkleRoot(transactions);
		String target = utils.getDificultyString(difficulty); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		LOGGER.info("Block Mined!!! : " + hash);
	}
	
	//Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((!"0".equals(previousHash))) {
			if((transaction.processTransaction() != true)) {
				LOGGER.warn("Transaction failed to process. Discarded.");
				return false;
			}
		}

		transactions.add(transaction);
		LOGGER.info("Transaction Successfully added to Block");
		return true;
	}
}