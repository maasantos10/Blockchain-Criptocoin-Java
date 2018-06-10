package com.mas.blockchain;

import com.mas.blockchain.chain.Blockchain;

public class BlockChainApp {

	public static void main (String [] args) {
		
		Blockchain blockchain = new Blockchain();

		blockchain.run();

	}
}
