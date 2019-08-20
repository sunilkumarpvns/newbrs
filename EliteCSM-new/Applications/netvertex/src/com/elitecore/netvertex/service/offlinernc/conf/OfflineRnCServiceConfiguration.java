package com.elitecore.netvertex.service.offlinernc.conf;

import com.elitecore.corenetvertex.util.ToStringable;

public interface OfflineRnCServiceConfiguration extends ToStringable{

	int getMinThread();

	int getMaxThread();

	int getFileBatchSize();

	int getFileBatchQueueSize();

	int getScanIntervalInSeconds();

	int getThreadPriority();

	String getFileRange();

	String getIntermediateOutputPath();
}
