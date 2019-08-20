package com.elitecore.core.commons.utilx.db;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.core.systemx.esix.LoadBalancerType;

public class TransactionFactoryGroupImpl extends ESCommunicatorGroupImpl<TransactionFactory>{

	public TransactionFactoryGroupImpl(LoadBalancerType loadBalancerType){
		super(loadBalancerType);
	}
	
	public TransactionFactoryGroupImpl(){
		this(LoadBalancerType.SWITCH_OVER);
	}

	public TransactionFactory getTransactionFactory(){
		return ((TransactionFactory)getCommunicator());
	}
	
	public DBDataSource getActiveDataSource(){
		TransactionFactory transactionFactory = getCommunicator();
		return transactionFactory!=null ? transactionFactory.getDataSource() : null;
	}
}
