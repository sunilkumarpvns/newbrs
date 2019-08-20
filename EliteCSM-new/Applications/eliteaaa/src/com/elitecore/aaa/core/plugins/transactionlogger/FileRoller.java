package com.elitecore.aaa.core.plugins.transactionlogger;

import com.elitecore.aaa.radius.service.acct.TimeBoundryRollingIntervalTask;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

class FileRoller extends TimeBoundryRollingIntervalTask {
	
	private EliteFileWriter eliteFileWriter;

	public FileRoller(long rollingIntervalInMinute, EliteFileWriter eliteFileWriter) {
		super(rollingIntervalInMinute);
		this.eliteFileWriter = eliteFileWriter;
	}

	@Override
	public void execute(AsyncTaskContext context) {
		eliteFileWriter.doRollOver(true);
	}
	
}