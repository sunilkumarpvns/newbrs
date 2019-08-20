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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.elitecore.aaa.core.conf.DigestConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.DigestConfigurable;
import com.elitecore.aaa.core.conf.impl.DigestConfigurationImpl;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.DummyRatingConfiguration;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadBWListConfiguration;
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
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.url.SocketDetail;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */

@ReadOrder(order = {
		"radAuthServiceConfigurable", "radBWListConbfigurable",
		 "digestConfigurable", "dummyRatingConfigurable"
		 })
public class RadAuthConfigurationImpl extends CompositeConfigurable implements RadAuthConfiguration {

	private static final String MODULE = "RAD-AUTH-CONF-IMPL";
	private final static String KEY = "RAD_AUTH";
	
	private Map<String, DigestConfiguration> digestConfigurationMap;
	private String logLocation;
	
	@Configuration private RadAuthServiceConfigurable radAuthServiceConfigurable;
	@Configuration private RadBWListConfigurable radBWListConbfigurable;
	@Configuration(conditional = true, required = false) private DummyRatingConfigurable dummyRatingConfigurable;
	@Configuration private DigestConfigurable digestConfigurable;

	public RadAuthConfigurationImpl() {
		this.digestConfigurationMap = new HashMap<String, DigestConfiguration>();
	}

	public boolean isRrdResponseTimeEnabled() {
		return radAuthServiceConfigurable.getRRDDetails().getIsRrdResponseTimeEnabled();
	}

	public boolean isRrdSummaryEnabled() {
		return radAuthServiceConfigurable.getRRDDetails().getIsRrdSummaryEnabled();
	}

	public boolean isRrdErrorsEnabled() {
		return radAuthServiceConfigurable.getRRDDetails().getIsRrdErrorsEnabled();
	}

	public boolean isRrdRejectReasonsEnabled() {
		return radAuthServiceConfigurable.getRRDDetails().getIsRrdRejectReasonsEnabled();
	}

	public String logLevel() {
		return radAuthServiceConfigurable.getLogger().getLogLevel();
	}

	public int logMaxRolledUnits() {
		return radAuthServiceConfigurable.getLogger().getLogMaxRolledUnits();
	}
	public int logRollingType() {
		return radAuthServiceConfigurable.getLogger().getLogRollingType();
	}

	public int logRollingUnit() {
		return radAuthServiceConfigurable.getLogger().getLogRollingUnit();
	}

	public boolean isServiceLevelLoggerEnabled() {
		return radAuthServiceConfigurable.getLogger().getIsLoggerEnabled();
	}

	public boolean isCompressLogRolledUnits() {
		return radAuthServiceConfigurable.getLogger().getIsbCompressRolledUnit();
	}
	
	@Override
	public String getLogLocation() {
		return logLocation;
	}

	@PostRead
	public void postReadProcessing(){
		/**
		 * In Authentication Service configuration, Post processing required for log location and to validate Queue size.
		 */		
		postReadProcessingForRadAuthServiceConfigurable();

		/**
		 * Post Processing is required after reading Digest Configuration.
		 */
		postReadProcessingForDigestConfigurable();

	}
	
	@PostWrite
	public void postWriteProcessing() {
		
	}

	@PostReload
	public void postReloadProcessing() {
		
	}

	private void postReadProcessingForDigestConfigurable() {
		List<DigestConfigurationImpl> digestConfList =new ArrayList<DigestConfigurationImpl>();
		digestConfList = digestConfigurable.getDigestConfigList();
		if(digestConfList!=null){
			int noOfDigestConf = digestConfList.size();
			Map<String, DigestConfiguration> tempDigestMap = new HashMap<String, DigestConfiguration>();
			DigestConfiguration digestConfiguration;

			for(int j=0;j<noOfDigestConf;j++){
				digestConfiguration =  digestConfList.get(j); 
				tempDigestMap.put(digestConfiguration.getConfId(), digestConfiguration);
			}
			this.digestConfigurationMap = tempDigestMap;
		}

	}

	private void postReadProcessingForRadAuthServiceConfigurable() {
		postReadForServiceLevelLogger();
		validateQueueSize();
	}

	private void validateQueueSize() {
		if (radAuthServiceConfigurable.getQueueSize() <= 0) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Configured queue size: " + radAuthServiceConfigurable.getQueueSize() 
						+ " is invalid. Using default queue size: " + AAAServerConstants.DEFAULT_QUEUE_SIZE);
			}
			radAuthServiceConfigurable.setQueueSize(AAAServerConstants.DEFAULT_QUEUE_SIZE);
			return;
		} else if (radAuthServiceConfigurable.getQueueSize() > 50000) {
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Configured queue size: " + radAuthServiceConfigurable.getQueueSize() 
						+ " is greater than maximum queue size: " + AAAServerConstants.MAX_QUEUE_SIZE + 
						", so using " + AAAServerConstants.MAX_QUEUE_SIZE + " as max queue size.");
		}
			radAuthServiceConfigurable.setQueueSize(AAAServerConstants.MAX_QUEUE_SIZE);
			return;
	}
	}

	private void postReadForServiceLevelLogger() {
		if(radAuthServiceConfigurable.getLogger().getIsLoggerEnabled()){
			String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(radAuthServiceConfigurable.getLogger().getLogLocation() != null && radAuthServiceConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(radAuthServiceConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				 this.logLocation = logLocation + File.separator + "rad-auth";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rad-auth";
			}	
		}
	}
	
	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException {
		boolean bSuccess = true;
		String strReturn = "";
		//strReturn = getServerContext().getServerHome() + File.separator + "conf" + File.separator + "services" ;
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		strReturn = serverContext.getServerHome() + File.separator + "tempconf" + File.separator + "services" ;
		Iterator iterator = lstConfiguration.iterator();
		while(iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)iterator.next();

			String configurationKey = eliteNetConfigurationData.getNetConfigurationKey();

			if(configurationKey != null && configurationKey.equalsIgnoreCase(KEY)) {
				try{
					bSuccess = write(strReturn, "rad-auth.xml", eliteNetConfigurationData.getNetConfigurationData());
				}catch(Exception e){
					bSuccess = false;
					LogManager.getLogger().debug(MODULE, e.getMessage());
					LogManager.getLogger().trace(MODULE,e);					
					throw new UpdateConfigurationFailedException("Update Rad Auth Service configuration failed. Reason: " + e.getMessage(),e);
				}
			}				
		}

		return bSuccess;
	}
	public int mainThreadPriority() {
		return radAuthServiceConfigurable.getMainThreadPriority();
	}

	public int maxRequestQueueSize() {
		return radAuthServiceConfigurable.getQueueSize();
	}

	public int maxThreadPoolSize() {
		return radAuthServiceConfigurable.getMaximumThread();
	}

	public int minThreadPoolSize() {
		return radAuthServiceConfigurable.getMinimumThread();
	}

	public String serviceAddress() {
		return radAuthServiceConfigurable.getServiceAddress();

	}

	public int socketReceiveBufferSize() {
		return radAuthServiceConfigurable.getSocketReceiveBufferSize();
	}

	public int socketSendBufferSize() {
		return radAuthServiceConfigurable.getSocketSendBufferSize();
	}

	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	public int workerThreadPriority() {
		return radAuthServiceConfigurable.getWorkerThreadPriority();
	}

	public String toString () {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- RAD Auth Service Configuration -- ");
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
		out.println("      Worker Priority = " + workerThreadPriority() );
		out.println("      Main Thread Priority = " + mainThreadPriority() );
		out.println("    Logging"); 
		out.println("    	 Service Logger Enabled  = " + isServiceLevelLoggerEnabled());
		out.println("        Level = " + logLevel());
		out.println("        Rolling Type = " + logRollingType());
		out.println("        Rolling Unit = " + logRollingUnit());
		out.println("        Max Rolled Units = " + logMaxRolledUnits());
		out.println("        Compress Rolled Unit = " + isCompressLogRolledUnits());
		out.println("        Log File Location = " + getLogLocation());
		out.println("        SYS Log");
		out.println("        	 "+getSysLogConfiguration());
		out.println("    Pre Plugins : " + getPrePluginList());
		out.println("    Post Plugins: " + getPostPluginList());
		out.println("    RRD"); 
		out.println("        Resonse Time Enabled = " + isRrdResponseTimeEnabled());
		out.println("        Summary Enabled = " + isRrdSummaryEnabled());
		out.println("        Errors Enabled = " + isRrdErrorsEnabled());
		out.println("        Reject Reasons Enabled = " + isRrdRejectReasonsEnabled());
		out.println("    ");

		out.close();
 
		return stringBuffer.toString();
	}

	private String getFileName(){
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		return serverContext.getServerHome() + File.separator + "conf" + File.separator + "services" + File.separator + "rad-auth.xml";
	}

	public void reloadConfiguration() throws LoadConfigurationException {}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public RadBWListConfiguration getBwListConfiguration() {
		return radBWListConbfigurable;
	}

	@Override
	public DigestConfiguration getDigestConfiguration(String authPolicyId) {
		return this.digestConfigurationMap.get(authPolicyId);		
	}

	public EliteNetConfigurationData getNetConfigurationData(){
		try {
			return read(getFileName(),MODULE, getKey());
		} catch (ReadConfigurationFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
		}		
		return null;
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return radAuthServiceConfigurable.getPrePlugins();
	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {		
		return radAuthServiceConfigurable.getPostPlugins();
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return this.radAuthServiceConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public DummyRatingConfiguration getDummyRatingConfiguration(){
		return dummyRatingConfigurable;
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return radAuthServiceConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return radAuthServiceConfigurable.getDuplicateRequestQueuePurgeInterval();
	}

	protected final EliteNetConfigurationData read(String strFile,String moduleName,String confKey) throws ReadConfigurationFailedException{
		EliteNetConfigurationData configurationData = new EliteNetConfigurationData();
		StreamResult result = null;
		Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(false);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {			
			File file = new File(strFile);
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			TransformerFactory tFactory =TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			result = new StreamResult(outputStream);
			transformer.transform(source, result);	     
			configurationData.setNetConfigurationKey(confKey);
			configurationData.setNetConfigurationData(outputStream.toByteArray());	        
			LogManager.getLogger().info(MODULE, "Reading configuration from " +  file.getAbsoluteFile());
		}catch(Exception e) {			
			LogManager.getLogger().trace(MODULE, "Error occured while reading configuration reason, " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e.getMessage());			
			throw new ReadConfigurationFailedException("Error occured while reading configuration for " + moduleName);
		}finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				}catch(Exception exp) {					
					LogManager.getLogger().warn(MODULE, "Error occured while closing stream for " + moduleName);
				}
			}
		}
		return configurationData;
	}

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

	private String getValidFileLocation(String location,String defaultLocation,String serverHome){
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
				location = serverHome + File.separator + location;
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
	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		if(DummyRatingConfigurable.class.isAssignableFrom(configurableClass)){
			String miscValue = System.getProperty(AAAServerConstants.DUMMY_RATING);
			if(miscValue != null && (miscValue.trim().equalsIgnoreCase("true")|| miscValue.trim().equalsIgnoreCase("yes"))){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return radAuthServiceConfigurable.getSocketDetails();
	}
}