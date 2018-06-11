package com.mas.blockchain.model;

/**
 * 
 * @author Marcos Santos
 * Description: Sample that show how can we work with blockchain and wallet.
 *
 */

public class TransactionInput {

	public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO; //Contains the Unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
