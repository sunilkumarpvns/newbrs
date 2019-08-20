package com.elitecore.aaa.radius.conf.impl;

import java.net.MalformedURLException;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;

public abstract class RadiusServiceConfigurable extends Configurable {
	
	protected void validateDuplicateRequestPurgeInterval() {
		
		if (getDuplicateRequestQueuePurgeInterval() < 3) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(getModuleName(), "Invalid duplicate request purge interval configured: " + 
													getDuplicateRequestQueuePurgeInterval() + 
													" So, considering default value: " + 
													AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL);
			}
			setDuplicateRequestQueuePurgeInterval(AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL);
			return;
		}
	
		int purgeInterval = getDuplicateRequestQueuePurgeInterval();
		int remainder = purgeInterval % 3;
		
		if (remainder != 0) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(getModuleName(), "Duplicate request purge interval: " + 
													getDuplicateRequestQueuePurgeInterval() + 
													" is not multiple of 3. So, considering value: " +  
													(purgeInterval - remainder) +
													", the highest multiple of 3, less than configured value");
			}
			setDuplicateRequestQueuePurgeInterval(purgeInterval - remainder);
		}
	}
	
	@PostRead
	public void postReadProcessing() throws MalformedURLException {
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}

	@XmlTransient
	public abstract String getModuleName();
	
	@XmlTransient
	public abstract int getDuplicateRequestQueuePurgeInterval();
	
	public abstract void setDuplicateRequestQueuePurgeInterval(int duplicateRequestQueuePurgeInterval);
}
