package com.elitecore.netvertex.core.transaction;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.GyTransactionType;

public class TransactionFactory {
	
	private final static String MODULE = "TRANS-FACTRY";
	private Map<String,Transaction> transactionsMap;
	private Map<GyTransactionType,Transaction> gyTransactionsMap;
	DiameterGatewayControllerContext context;
	
	public TransactionFactory(DiameterGatewayControllerContext context){
		transactionsMap = new HashMap<String, Transaction>();
		gyTransactionsMap = new HashMap<>();
		this.context = context;
	}
	
	public void registerTransaction(String type, Transaction transaction){
		if(transaction != null){
			transactionsMap.put(type, transaction);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Transaction Registeration failed. Reason: Transaction of Type: " + type + " is null");		
		}
	}
	
	public void registerTransaction(GyTransactionType type, Transaction transaction) {
		if (transaction != null) {
			gyTransactionsMap.put(type, transaction);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Transaction Registeration failed. Reason: Transaction of Type: " + type + " is null");
			}
		}
	}
	
	public Transaction createTransaction(String type) {
		Transaction transaction = transactionsMap.get(type);
		if (transaction != null) {
			try {
				return (Transaction) transaction.clone();
			} catch (CloneNotSupportedException e) {
				getLogger().error(MODULE, "Error while cloning transaction of type: " + type + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		return null;
	}
	
	public Transaction createTransaction(GyTransactionType type) {
		Transaction transaction = gyTransactionsMap.get(type);
		if (transaction != null) {
			try {
				return (Transaction) transaction.clone();
			} catch (CloneNotSupportedException e) {
				getLogger().error(MODULE, "Error while cloning transaction of type: " + type + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);

			}
		}
		return null;
	}
}
