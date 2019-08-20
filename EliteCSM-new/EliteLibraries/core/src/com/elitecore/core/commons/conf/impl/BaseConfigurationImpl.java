package com.elitecore.core.commons.conf.impl;



import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;

public abstract class BaseConfigurationImpl implements ErrorHandler {
	
	@XmlTransient
	private ServerContext serverContext;
	@XmlTransient
	private static final String MODULE = "BASE CONFIGURATION IMPL";
			
	public BaseConfigurationImpl(ServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	protected final ServerContext getServerContext() {
		return this.serverContext;
	}
	
	public void error(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void warning(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public abstract void readConfiguration() throws LoadConfigurationException;

	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException{		
		LogManager.getLogger().warn(MODULE, "Update configuration of Base is called.");
		return false;
	}
	
	public EliteNetConfigurationData getNetConfigurationData(){
		//TODO Override this method where needed.
		LogManager.getLogger().warn(MODULE, "Base Configuration Implementation called.");
		return null;
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
		bReadWritePermission = destFile.canRead() && destFile.canWrite();
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
	public boolean stringToBoolean(String originalString,boolean defaultValue) {
		boolean resultValue = defaultValue;
		try{
			resultValue = Boolean.parseBoolean(originalString.trim());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}
	public int stringToInteger(String originalString,int defaultValue) {
		int resultValue = defaultValue;
		try{
			resultValue = Integer.parseInt(originalString);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}
	
	public int stringToPositiveInteger(String originalString,int defaultValue) {
		int retVal = stringToInteger(originalString, defaultValue); 
		return 	retVal > 0 ? retVal : defaultValue;	
	}

	public long stringToLong(String originalString,long defaultValue) {
		long resultValue = defaultValue;
		try{
			resultValue = Long.parseLong(originalString);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}
	public String getKey(){
		return "BaseImpl";
	}
	protected void trimALl(String[] strArray){
		if(strArray != null){
			for(int i=0;i<strArray.length;i++){
				strArray[i] = strArray[i].trim();
			}
		}
	}
	
	protected void appendChildNode(Document document,Node parentnode,String tag,String value) {
		 
		Node dataNode = document.createElement(tag);
		dataNode.setTextContent(String.valueOf(value));
		parentnode.appendChild(dataNode);
	 
		
	}
	
	protected String getValidFileLocation(String location,String defaultLocation){
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
				location = getServerContext().getServerHome() + File.separator + location;
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
}
