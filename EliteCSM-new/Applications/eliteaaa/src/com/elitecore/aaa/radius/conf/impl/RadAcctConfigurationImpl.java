/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 4th August 2010 by Ezhava Baiju D
 *  
 */

package com.elitecore.aaa.radius.conf.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.url.SocketDetail;


/**
 * 
 * @author Elitecore Technologies Ltd.
 */
	
@ReadOrder(order = { "acctServiceConfigurable"})
public class RadAcctConfigurationImpl extends CompositeConfigurable implements RadAcctConfiguration{
	
	private static final String MODULE = "RAD-ACCT-CONF-IMPL";
	private static final String KEY = "RAD_ACCT";
	
	@Configuration private RadAcctServiceConfigurable acctServiceConfigurable;
	
	private String logLocation;

	public String logLevel() {
		return acctServiceConfigurable.getLogger().getLogLevel();
	}

	public int logMaxRolledUnits() {
		return acctServiceConfigurable.getLogger().getLogMaxRolledUnits();
	}
	public int logRollingType() {
		return acctServiceConfigurable.getLogger().getLogRollingType();
	}

	public int logRollingUnit() {
		return acctServiceConfigurable.getLogger().getLogRollingUnit();
	}

	public boolean isServiceLevelLoggerEnabled() {
		return acctServiceConfigurable.getLogger().getIsLoggerEnabled();
	}

	public boolean isCompressLogRolledUnits() {
		return acctServiceConfigurable.getLogger().getIsbCompressRolledUnit();
	}
	
	public boolean isRrdResponseTimeEnabled() {
		return acctServiceConfigurable.getRRDDetails().getIsRrdResponseTimeEnabled();
	}

	public boolean isRrdSummaryEnabled() {
		return acctServiceConfigurable.getRRDDetails().getIsRrdSummaryEnabled();
	}

	public boolean isRrdErrorsEnabled() {
		return acctServiceConfigurable.getRRDDetails().getIsRrdErrorsEnabled();
	}
	
	public String getLogLocation() {
		return logLocation;
	}
	public boolean isRrdRejectReasonsEnabled() {
		return acctServiceConfigurable.getRRDDetails().getIsRrdRejectReasonsEnabled();
	}

	@PostRead
	public void postReadProcessing(){
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		/**
		 * In Accouting Service configuration, do processing required for log location and to validate Queue size.
		 */
		postReadProcessingForAcctServiceConfigurable(serverContext);
	}

	@PostWrite
	public void postWriteProcessing(){

	}

	@PostReload
	public void postReloadProcessing(){
	
	}
		
	private void postReadProcessingForAcctServiceConfigurable(AAAServerContext serverContext){
		postReadForServiceLevelLogger(serverContext);
		validateQueueSize();
	}
	
	private void validateQueueSize() {
		if (acctServiceConfigurable.getQueueSize() <= 0) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Configured queue size: " + acctServiceConfigurable.getQueueSize() 
						+ " is invalid. Using default queue size: " + AAAServerConstants.DEFAULT_QUEUE_SIZE);
			}
			acctServiceConfigurable.setQueueSize(AAAServerConstants.DEFAULT_QUEUE_SIZE);
			return;
		} else if (acctServiceConfigurable.getQueueSize() > 50000) {
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Configured queue size: " + acctServiceConfigurable.getQueueSize() 
						+ " is greater than maximum queue size: " + AAAServerConstants.MAX_QUEUE_SIZE + 
						", so using " + AAAServerConstants.MAX_QUEUE_SIZE + " as max queue size.");
		}
			acctServiceConfigurable.setQueueSize(AAAServerConstants.MAX_QUEUE_SIZE);
			return;
	}
	}

	private void postReadForServiceLevelLogger(AAAServerContext serverContext) {
		if(acctServiceConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverContext.getServerHome() + File.separator + "logs";
			if(acctServiceConfigurable.getLogger().getLogLocation() != null && acctServiceConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(acctServiceConfigurable.getLogger().getLogLocation(),defaultLogLocation, serverContext);
				 this.logLocation = logLocation + File.separator + "rad-acct";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rad-acct";
			}	
		}
	}


	public int mainThreadPriority() {
		return acctServiceConfigurable.getMainThreadPriority();
	}

	public int maxRequestQueueSize() {
		return acctServiceConfigurable.getQueueSize();
	}

	public int maxThreadPoolSize() {
		return acctServiceConfigurable.getMaximumThread();
	}

	public int minThreadPoolSize() {
		return acctServiceConfigurable.getMinimumThread();
	}

	public String serviceAddress() {
		return acctServiceConfigurable.getServiceAddress();
	}

	public int socketReceiveBufferSize() {
		return acctServiceConfigurable.getSocketReceiveBufferSize();
	}

	public int socketSendBufferSize() {
		return acctServiceConfigurable.getSocketSendBufferSize();
	}

	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	public int workerThreadPriority() {
		return acctServiceConfigurable.getWorkerThreadPriority();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	public String toString () {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- RAD Acct Service Configuration -- ");
		out.println();
		out.println("    Service Address = " + getSocketDetails());
		out.println("    Socket Recevie Buffer Size = " + socketReceiveBufferSize());
		out.println("    Socket Send Buffer Size = " + socketSendBufferSize());
		out.println("    Duplicate Request Check Enabled = " + isDuplicateRequestDetectionEnabled());
		out.println("    Duplicate Request Check Queue Purge Interval = " + getDupicateRequestQueuePurgeInterval());
		out.println("    Threads");
		out.println("      Minimum Pool Size = " + minThreadPoolSize());
		out.println("      Maximum Pool Size = " + maxThreadPoolSize());
		out.println("      Queue Size = " + maxRequestQueueSize());
		out.println("      Worker Priority = " + workerThreadPriority());
		out.println("      Main Thread Priority = " + mainThreadPriority() );

		out.println("    Logging"); 
		out.println("        Level = " + logLevel());
		out.println("        Rolling Type = " + logRollingType());
		out.println("        Rolling Unit = " + logRollingUnit());
		out.println("        Compress Rolled Unit = " + isCompressLogRolledUnits());
		out.println("        Max Rolled Unit = " + logMaxRolledUnits());
		out.println("        LogFile Location = " + logLocation);
		out.println("    Pre Plugins : " + getPrePluginList());
		out.println("    Post Plugins: " + getPostPluginList());
		out.println("    RRD"); 
		out.println("        Resonse Time Enabled = " + isRrdResponseTimeEnabled());
		out.println("        Summary Enabled = " + isRrdSummaryEnabled());
		out.println("        Errors Enabled = " + isRrdErrorsEnabled());
		out.println("    ");

		out.close();
		return stringBuffer.toString();

	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {		
		return this.acctServiceConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return this.acctServiceConfigurable.getPrePlugins();
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return this.acctServiceConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return acctServiceConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return acctServiceConfigurable.getDuplicateRequestQueuePurgeInterval();
	}

	private String getValidFileLocation(String location,String defaultLocation,AAAServerContext serverContext){
		if(location == null || location.trim().length() == 0)
			return defaultLocation;
		
		File file = new File(location);
		try{
			if(file.isAbsolute()){
				if(file.exists()){
					if(file.canWrite()){
						return location;
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "No write access at location : " + location + ". Using default location : " + defaultLocation);
						}
					}
				}else{
					if(file.mkdirs())
						return location;
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Cannot create directory at location : " + location);
					}
				}
			}else{
				//in case of relative path like ../../../../../.. it will make it absolute and check whether access is available
				location = serverContext.getServerHome() + File.separator + location;
				file = new File(location);
				if(file.exists()){
					if(file.isDirectory() && file.canWrite()){
						return location;
					}
				}else{
					if(file.mkdirs())
						return location;
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Cannot create directory at location : " + location);
					}
				}
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Invalid location : " + location + ". Using default location : " + defaultLocation);
			}
			return defaultLocation;
		}catch(SecurityException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getKey(), "Invalid location : "+ location +" Reason: " + ex.getMessage() + ". Using default location : " + defaultLocation);
			return defaultLocation;
		}
	}
	
	//FIXME narendra - this method is to be checked for removal
	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException {
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		boolean bSuccess = true;
		String strReturn = "";
		//strReturn = getServerContext().getServerHome() + File.separator + "conf" + File.separator + "services" ;
		strReturn = serverContext.getServerHome() + File.separator + "tempconf" + File.separator + "services" ;
		Iterator iterator = lstConfiguration.iterator();
		while(iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)iterator.next();

			String configurationKey = eliteNetConfigurationData.getNetConfigurationKey();

			if(configurationKey != null && configurationKey.equalsIgnoreCase(KEY)) {
				try{
					bSuccess = write(strReturn, "rad-acct.xml", eliteNetConfigurationData.getNetConfigurationData());
				}catch(Exception e){
					bSuccess = false;
					throw new UpdateConfigurationFailedException("Update Rad Acct Service configuration failed. Reason: " + e.getMessage(),e);
				}
			}				
		}

		return bSuccess;
	}
	
	//FIXME narendra - this method is to be checked for removal
	public final boolean write(String base,String filename, byte[] fileContent)throws UpdateConfigurationFailedException, IOException {
		boolean bSuccess = true;
		boolean bFileCreated = true;
		boolean bReadWritePermission=  false;
		File file = new File(base);
		if(!file.exists()) {
			bFileCreated = file.mkdirs();
			LogManager.getLogger().info(MODULE,"Creating a directory : " + file.getAbsoluteFile());
		}	
		File destFile = new File(file,filename);
		if(destFile!= null && !destFile.exists() && bFileCreated) {
			bFileCreated = destFile.createNewFile();
			LogManager.getLogger().info(MODULE, "Creating configuration file : " +  destFile.getAbsoluteFile());
		}
		bReadWritePermission = destFile.canRead() & destFile.canWrite();
		if(bFileCreated && bReadWritePermission) {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile));
			stream.write(fileContent);
			stream.close();
			LogManager.getLogger().info(MODULE, "Writing updated configuration in " +  destFile.getAbsoluteFile());
		}else{			
			LogManager.getLogger().warn(MODULE, "Configuration file creation failed or read-write accees not allowed.");
			bSuccess = false; 
		}
		return bSuccess;
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return acctServiceConfigurable.getSocketDetails();
	}
}
