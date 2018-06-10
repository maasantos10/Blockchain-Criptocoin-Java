package com.mas.blockchain.chain;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mas.blockchain.model.Block;
import com.mas.blockchain.ultil.Utils;

public class Blockchain {

	public static ArrayList<Block> blockChain = new ArrayList<Block>();
	public static int difficulty = 5;
	public final static Logger LOGGER = LoggerFactory.getLogger(Blockchain.class);
	Utils utils = new Utils();
	
	
	public void run() {
		//add our blocks to the blockchain ArrayList:
		
		LOGGER.info("Mine block 1... ");
		addBlock(new Block("the first block", "0"));
		
		LOGGER.info("Mine block 2... ");
		addBlock(new Block("the second block",blockChain.get(blockChain.size()-1).hash));
		
		LOGGER.info("Mine block 3... ");
		addBlock(new Block("the third block",blockChain.get(blockChain.size()-1).hash));	
		
		LOGGER.info("\nBlockchain is Valid: " + isValidChain());
		
		String blockchainJson = utils.getJson(blockChain);

		LOGGER.info("###################################################");
		LOGGER.info("The block chain: ");
		LOGGER.info(blockchainJson);
	}
	
	public static Boolean isValidChain() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockChain.size(); i++) {
			
			currentBlock = blockChain.get(i);
			previousBlock = blockChain.get(i-1);
			
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				LOGGER.warn("Current Hashes not equal");			
				return false;
			}
			
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				LOGGER.warn("Previous Hashes not equal");
				return false;
			}
			
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				LOGGER.warn("This block hasn't been mined");
				return false;
			}
			
		}
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockChain.add(newBlock);
	}
		
	
}
