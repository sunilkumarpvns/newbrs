package com.sterlite.netvertex.nvsampler.cleanup.util;

import com.sterlite.netvertex.nvsampler.cleanup.Result;

public class MiscCleaner {

	private static final String DELETE_TBLM_NOTIFICATION_TEMPLATE = "DELETE FROM TBLM_NOTIFICATION_TEMPLATE WHERE ID=?";
	private final DBQueryExecutor dbQueryExecutor;

	public MiscCleaner(DBQueryExecutor dbQueryExecutor) {
		this.dbQueryExecutor = dbQueryExecutor;
	}

	public Result cleanTemplate(String id) {
		Result result = new Result("Clean Template: " + id);
		result.addResult(dbQueryExecutor.executeUpdate(DELETE_TBLM_NOTIFICATION_TEMPLATE, id));
		return result;
	}
}
