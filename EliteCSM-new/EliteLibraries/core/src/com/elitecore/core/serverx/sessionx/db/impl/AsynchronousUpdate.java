package com.elitecore.core.serverx.sessionx.db.impl;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionResultCode;
import com.elitecore.core.serverx.sessionx.db.impl.BatchDataSink.BatchUpdateData;

/**
 * 
 * @author malav.desai
 *
 */
class AsynchronousUpdate implements UpdateOperation {

	private static final String MODULE = "ASYNCHRONOUS-UPDATE-OPERATION";
	
	@Nonnull
	protected final BatchDataSink batchedOperationTask;
	
	public AsynchronousUpdate(BatchDataSink batchedOperationTask) {
		this.batchedOperationTask = batchedOperationTask;
	}

	@Override
	public int execute(SessionData sessionData) {
		boolean addedToBatch = batchedOperationTask.addBatchData(new BatchUpdateData(sessionData,BatchUpdateData.UPDATE));
		if (addedToBatch == false) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Queue is full or DB failure, so dropping update operation for ID: " + sessionData.getSessionId());
			}
			return SessionResultCode.FAILURE.code;
		}
		return SessionResultCode.SUCCESS.code;
	}

	@Override
	public int execute(SessionData sessionData, Criteria criteria) {
		boolean addedToBatch = batchedOperationTask.addBatchData(new BatchUpdateData(sessionData, criteria, BatchUpdateData.UPDATE));
		if (addedToBatch == false) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Queue is full or DB failure, so dropping update operation for ID: " + sessionData.getSessionId());
			}
			return SessionResultCode.FAILURE.code;
		}
		return SessionResultCode.SUCCESS.code;
	}

}
