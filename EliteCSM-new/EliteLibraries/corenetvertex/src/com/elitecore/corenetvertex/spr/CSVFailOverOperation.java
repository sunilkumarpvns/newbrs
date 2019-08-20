package com.elitecore.corenetvertex.spr;

import java.util.Collection;
import java.util.concurrent.Executor;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 * 
 * Utility provided for failover operation, currently limited to CSV record generation. 
 * Can be used for other purposes - Dependent upon the RecordProcessor parameter 
 * 
 */
public class CSVFailOverOperation<T> implements FailOverOperation<T> {

	private static final String MODULE = "CSV-FAILOVER";
	private RecordProcessor<T> recordProcessor;
	private Executor executor;

	@Override
	public void doFailover(Collection<T> batchOperationDatas) {

		executor.execute(() -> {
			for (T t : batchOperationDatas) {
				processRecord(t);
			}
		});
	}

	public CSVFailOverOperation(RecordProcessor<T> recordProcessor, Executor executor) {
		this.recordProcessor = recordProcessor;
		this.executor = executor;
	}

	private void processRecord(T t) {

		try {

			recordProcessor.process(t);
		} catch (Exception e) {
			getLogger().error(MODULE, "Cant add record to csv. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	@Override
	public void doFailover(T t) {
		processRecord(t);
	}

}
