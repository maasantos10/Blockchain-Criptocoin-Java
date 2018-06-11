package com.mas.blockchain;

import com.mas.blockchain.chain.Blockchain;

/**
 * 
 * @author Marcos Santos
 * Description: Sample that show how can we work with blockchain and wallet.
 *
 */

public class BlockchainApp {

	public static void main (String [] args) {
		
		Blockchain blockchain = new Blockchain();
		
		blockchain.run();
	}
	
}
