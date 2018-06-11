package com.mas.blockchain.chain;

import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mas.blockchain.transaction.Transaction;
import com.mas.blockchain.wallet.Wallet;
import com.mas.blockchain.model.Block;
import com.mas.blockchain.model.TransactionInput;
import com.mas.blockchain.model.TransactionOutput;
import com.mas.blockchain.util.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 * @author Marcos Santos
 * Description: Sample that show how can we work with blockchain and wallet.
 *
 */

public class Blockchain {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Blockchain.class);
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Transaction firstTransaction;
	public static Wallet walletOne;
	public static Wallet walletTwo;

	public void run() {	
		//add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		
		//Create wallets:
		walletOne = new Wallet();
		walletTwo = new Wallet();		
		Wallet coinExchange = new Wallet();
		
		//create first transaction, which sends 1000 MasCoin to walletOne: 
		firstTransaction = new Transaction(coinExchange.publicKey, walletOne.publicKey, 1000f, null);
		firstTransaction.generateSignature(coinExchange.privateKey);	 //manually sign the first transaction	
		firstTransaction.transactionId = "0"; //manually set the transaction id
		firstTransaction.outputs.add(new TransactionOutput(firstTransaction.reciepient, firstTransaction.value, firstTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		LOGGER.info("Creating and Mining the First block... ");
		Block first = new Block("0");
		first.addTransaction(firstTransaction);
		addBlock(first);
		
		//testing
		Block block1 = new Block(first.hash);
		LOGGER.info("walletOne's balance is: " + walletOne.getBalance());
		LOGGER.info("walletOne is Attempting to send funds (60) to walletTwo...");
		block1.addTransaction(walletOne.sendFunds(walletTwo.publicKey, 60f));
		addBlock(block1);
		LOGGER.info("walletOne's balance is: " + walletOne.getBalance());
		LOGGER.info("walletTwo's balance is: " + walletTwo.getBalance());
		
		Block block2 = new Block(block1.hash);
		LOGGER.info("walletOne Attempting to send more funds (2000) than it has...");
		block2.addTransaction(walletOne.sendFunds(walletTwo.publicKey, 2000f));
		addBlock(block2);
		LOGGER.info("walletOne's balance is: " + walletOne.getBalance());
		LOGGER.info("walletTwo's balance is: " + walletTwo.getBalance());
		
		Block block3 = new Block(block2.hash);
		LOGGER.info("walletTwo is Attempting to send funds (30) to walletOne...");
		block3.addTransaction(walletTwo.sendFunds( walletOne.publicKey, 30));
		LOGGER.info("walletOne's balance is: " + walletOne.getBalance());
		LOGGER.info("walletTwo's balance is: " + walletTwo.getBalance());
		
		isChainValid();
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				LOGGER.warn("#Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				LOGGER.warn("#Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				LOGGER.warn("#This block hasn't been mined");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					LOGGER.warn("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					LOGGER.warn("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						LOGGER.warn("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						LOGGER.warn("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					LOGGER.warn("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					LOGGER.warn("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		LOGGER.warn("Blockchain is valid");
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
