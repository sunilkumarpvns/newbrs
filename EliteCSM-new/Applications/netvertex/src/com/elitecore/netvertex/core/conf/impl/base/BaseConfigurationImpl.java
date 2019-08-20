package com.elitecore.netvertex.core.conf.impl.base;


import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BaseConfigurationImpl implements ErrorHandler {


	private static final String BASE_IMPL = "BaseImpl";
	private ServerContext serverContext;
	private static final String MODULE = "BASE CONFIGURATION IMPL";
	public static final int POSITIVE_INT_MIN_VALUE = 1;
	public static final int MAX_THREADS = 500;

	public BaseConfigurationImpl(ServerContext serverContext) {
		this.serverContext = serverContext;
	}


	protected final ServerContext getServerContext() {
		return this.serverContext;
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {


	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {


	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {


	}

	public abstract void readConfiguration() throws LoadConfigurationException;

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
			LogManager.getLogger().trace(MODULE, "Error occurred while reading configuration reason, " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e.getMessage());
			throw new ReadConfigurationFailedException("Error occurred while reading configuration for " + moduleName, e);
		}finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				}catch(Exception exp) {
					LogManager.getLogger().warn(MODULE, "Error occurred while closing stream for " + moduleName);
					LogManager.ignoreTrace(exp);
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
		}
		File destFile = new File(file,filename);
		if(destFile!= null && !destFile.exists() && bFileCreated) {
			bFileCreated = destFile.createNewFile();

		}
		bReadWritePermission = destFile.canRead() && destFile.canWrite();
		if(bFileCreated && bReadWritePermission) {

			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile))) {

				stream.write(fileContent);
			}
		}else{
			LogManager.getLogger().warn(MODULE, "Configuration file creation failed or read-write accees not allowed.");
			bSuccess = false;
		}
		return bSuccess;
	}
	public boolean stringToBoolean(String parameterName, String originalString, boolean defaultValue) {

		if (Strings.isNullOrBlank(originalString)) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified");
			return defaultValue;
		}
		return Boolean.parseBoolean(originalString.trim());

	}
	public int stringToInteger(String parameterName, String originalString, int defaultValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		int resultValue = defaultValue;
		try{
				resultValue = Integer.parseInt(originalString.trim());
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;
		
	}
	public int stringToInteger(String parameterName, String originalString, int defaultValue, int minValue, int maxValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		int resultValue = defaultValue;
		try{
				resultValue = Integer.parseInt(originalString.trim());
			if(resultValue < minValue || resultValue > maxValue){
				LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
				resultValue = defaultValue ; 
			}
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;

	}
	public long stringToLong(String parameterName,String originalString, long defaultValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		long resultValue = defaultValue;
		try{
				resultValue = Long.parseLong(originalString.trim());
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;

	}
	
	public String trim(String string) {
		return string == null ? null : string.trim();
	}

	public EliteNetConfigurationData getNetConfigurationData(){
		LogManager.getLogger().warn(MODULE, "Base Configuration Implementation called.");
		return null;
	}

	public String getKey(){
		return BASE_IMPL;
	}
	
}
