package com.elitecore.corenetvertex.spr;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Collection;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

/**
 * 
 * @author Jay Trivedi
 * 
 * <br> <br>
 * 
 * <i> This class should be used as failover utility in case where batch operation fails. 
 * Single commit operation should be done through single record operation property.</i>
 *
 */

public class DBSingleRecordFailoverOperation<T> implements FailOverOperation<T>{

	private static final String MODULE = "DB-SINGLE-REC-FAILOVER";
	private final SingleRecordOperation<T> singleRecordOperation;
	private final TransactionFactory transactionFactory;
	@Nullable private FailOverOperation<T> failOverOperation;
	private Executor executor;
	
	public DBSingleRecordFailoverOperation(SingleRecordOperation<T> singleRecordOperation, 
			@Nullable FailOverOperation<T> failOverOperation, 
			TransactionFactory transactionFactory,
			Executor executor) {

		this.singleRecordOperation = singleRecordOperation;
		this.failOverOperation = failOverOperation;
		this.transactionFactory = transactionFactory;
		this.executor = executor;
	}
	

	@Override
	public void doFailover(Collection<T> records) {
		
		executor.execute(() -> {
			records.forEach(this::processRecord);
		});
		
	}


	private void processRecord(T record) {
		
		try {
			singleRecordOperation.process(record, transactionFactory);
		} catch (OperationFailedException e) {
			
			if (failOverOperation == null) {
				getLogger().warn(MODULE, "Skip failed record failover from batch. Reason: No failover method configured for single record failover operation.");
				return;
			}

			failOverOperation.doFailover(record);
		}
	}


	@Override
	public void doFailover(T t) {
		
		executor.execute(() -> {
			processRecord(t);
		});
	}
	
	
}
