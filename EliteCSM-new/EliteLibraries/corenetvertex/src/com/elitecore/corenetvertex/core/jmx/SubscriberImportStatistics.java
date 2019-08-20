package com.elitecore.corenetvertex.core.jmx;

import java.io.Serializable;
import java.util.Date;

public interface SubscriberImportStatistics extends Serializable {

	public long getSubmittedTaskCount();
	
	public long getSuccessCount();
	
	public long getFailCount();

	public long getInprogressCount();
	
	public Date getImportStartTime();
}
