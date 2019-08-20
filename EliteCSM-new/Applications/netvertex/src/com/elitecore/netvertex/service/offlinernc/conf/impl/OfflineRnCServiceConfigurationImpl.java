package com.elitecore.netvertex.service.offlinernc.conf.impl;

import com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.service.offlinernc.conf.OfflineRnCServiceConfiguration;

public class OfflineRnCServiceConfigurationImpl implements OfflineRnCServiceConfiguration, ToStringable {

	
	private int minThread = 5;
	private int maxThread = 10;
	private int fileBatchSize = 1;
	private int fileBatchQueue = 10;
	private int scanIntervalInSeconds = 20;
	private int threadPriority = 5;
	private String intermediateOutputPath;
	
	public OfflineRnCServiceConfigurationImpl(OfflineRncServerProfileData offlineRnCServerProfileData,
			String intermediateOutputPath) {
		this.minThread = offlineRnCServerProfileData.getMinThread();
		this.maxThread = offlineRnCServerProfileData.getMaxThread();
		this.fileBatchSize = offlineRnCServerProfileData.getFileBatchSize();
		this.fileBatchQueue = offlineRnCServerProfileData.getFileBatchQueue();
		this.scanIntervalInSeconds = offlineRnCServerProfileData.getScanInterval();
		this.threadPriority = offlineRnCServerProfileData.getThreadPriority();
		this.intermediateOutputPath = intermediateOutputPath;
	}
	
	/**
	 * To create default configuration when instance is of PCC type
	 */
	public OfflineRnCServiceConfigurationImpl() {
		
	}

	@Override
	public int getMinThread() {
		return minThread;
	}

	@Override
	public int getMaxThread() {
		return maxThread;
	}

	@Override
	public int getFileBatchSize() {
		return fileBatchSize;
	}

	@Override
	public int getFileBatchQueueSize() {
		return fileBatchQueue;
	}

	@Override
	public int getScanIntervalInSeconds() {
		return scanIntervalInSeconds;
	}

	@Override
	public int getThreadPriority() {
		return threadPriority;
	}

	@Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Offline RnC Service Configuration -- ");
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Minimum thread", minThread);
        out.append("Maximum thread", maxThread);
        out.append("File batch size", fileBatchSize);
        out.append("File batch queue size", fileBatchQueue);
        out.append("Thread priority", threadPriority);
        out.append("Scan interval", scanIntervalInSeconds);
        out.decrementIndentation();
    }

	
	@Override
	public String getFileRange() {
		return null;
	}

	@Override
	public String getIntermediateOutputPath() {
		return intermediateOutputPath;
	}
}
