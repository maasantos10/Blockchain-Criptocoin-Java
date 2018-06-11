package com.mas.blockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mas.blockchain.chain.Blockchain;
import com.mas.blockchain.model.Block;
import com.mas.blockchain.model.TransactionInput;
import com.mas.blockchain.model.TransactionOutput;
import com.mas.blockchain.util.SHA256;
import com.mas.blockchain.util.Utils;

/**
 * 
 * @author Marcos Santos
 * Description: Sample that show how can we work with blockchain and wallet.
 *
 */

public class Transaction {

	public static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
	public String transactionId; //Contains a hash of transaction*
	public PublicKey sender; //Senders address/public key.
	public PublicKey reciepient; //Recipients address/public key.
	public float value; //Contains the amount we wish to send to the recipient.
	public byte[] signature; //This is to prevent anybody else from spending funds in our wallet.
	
	
	public Blockchain blockchain = new Blockchain();
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; //A rough count of how many transactions have been generated 
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	public boolean processTransaction() {
		
		if(verifySignature() == false) {
			LOGGER.warn("#Transaction Signature failed to verify");
			return false;
		}
				
		//Gathers transaction inputs (Making sure they are unspent):
		for(TransactionInput i : inputs) {
			i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
		}

		//Checks if transaction is valid:
		if(getInputsValue() < Blockchain.minimumTransaction) {
			LOGGER.warn("Transaction Inputs too small: " + getInputsValue());
			LOGGER.warn("Please enter the amount greater than " + Blockchain.minimumTransaction);
			return false;
		}
		
		//Generate transaction outputs:
		float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
		transactionId = calulateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
				
		//Add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			Blockchain.UTXOs.put(o.id , o);
		}
		
		//Remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			Blockchain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
			total += i.UTXO.value;
		}
		return total;
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(reciepient) + Float.toString(value)	;
		signature = Utils.applyECDSASig(privateKey,data);		
	}
	
	public boolean verifySignature() {
		String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(reciepient) + Float.toString(value)	;
		return Utils.verifyECDSASig(sender, data, signature);
	}
	
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return SHA256.digestSha256(
				Utils.getStringFromKey(sender) +
				Utils.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
}
