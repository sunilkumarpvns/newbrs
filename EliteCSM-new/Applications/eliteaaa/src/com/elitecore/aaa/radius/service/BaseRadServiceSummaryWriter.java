package com.elitecore.aaa.radius.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.EliteFileWriter.Builder;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public abstract class BaseRadServiceSummaryWriter extends BaseIntervalBasedTask{
		
	protected EliteFileWriter summaryReportWriter;
	
	private final long initialDelay = 0;
	private final long intervalSeconds = 60;
	protected RadServiceContext<?, ?> serviceContext;
	
	protected Date lastSummaryReportWriteDate;
	
	protected final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	private static final String MODULE = "BASE RAD SERVICE SUMMARY WRITER";

	public BaseRadServiceSummaryWriter(RadServiceContext<?, ?> serviceContext) {
		lastSummaryReportWriteDate = new Date();
		this.serviceContext = serviceContext;
		
		try {
			summaryReportWriter = new Builder()
					.fileName(serviceContext.getServiceIdentifier() + "-ServiceSummary")
					.activeFileExt("current")
					.destinationPath(serviceContext.getServerContext().getServerHome() + File.separator + "logs" + File.separator + "service-summary")
					.rollingType(EliteFileWriter.TIME_BASED_ROLLING)
					.rollingUnit(24*60)
					.taskScheduler(serviceContext.getServerContext().getTaskScheduler())
					.build();
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Service Summary for "+serviceContext.getServiceIdentifier()+" will not work . Reason :"+e.getMessage());
		}
	}
	
	public long getInterval() {
		return this.intervalSeconds;
	}
	
	public long getInitialDelay() {
		return this.initialDelay;
	}
	
	public void close() {
		if(summaryReportWriter!=null)
			this.summaryReportWriter.close();
	}


}
