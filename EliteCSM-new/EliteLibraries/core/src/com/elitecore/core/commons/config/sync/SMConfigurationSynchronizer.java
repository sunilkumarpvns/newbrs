package com.elitecore.core.commons.config.sync;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;

/**
 * Implementation for synchronizing the configuration from Server Manager.
 * @author narendra.pathai
 *
 */
/*
 * It is dependent on configuration API annotations for scanning the meta data
 */
public class SMConfigurationSynchronizer {

	private ServerContext serverContext;
	
	public SMConfigurationSynchronizer(ServerContext serverContext){
		this.serverContext = serverContext;
	}
	
	public boolean synchronizeTo(String base, String fileName, byte[] fileContents) throws IOException{
		boolean bSuccess = true;
		boolean bFileCreated = true;
		boolean bReadWritePermission = false;
		File file = new File(base);
		if(!file.exists()) {
			bFileCreated = file.mkdirs();
		}	
		File destFile = new File(file,fileName);
		if(destFile!= null && !destFile.exists() && bFileCreated) {
			bFileCreated = destFile.createNewFile();
		}
		bReadWritePermission = destFile.canRead() && destFile.canWrite();
		if(bFileCreated && bReadWritePermission) {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile));
			stream.write(fileContents);
			stream.close();
		}else{			
			bSuccess = false; 
		}
		return bSuccess;
	}
	
	public EliteNetConfigurationData synchronizeFrom(Class<? extends Configurable> configurableClass){
		EliteNetConfigurationData configurationData = new EliteNetConfigurationData();

		try {
			ConfigurationProperties annotation = configurableClass.getAnnotation(ConfigurationProperties.class);
			XMLProperties xmlPropertiesMetaData = configurableClass.getAnnotation(XMLProperties.class);
			
			if(annotation == null){
				throw new IllegalArgumentException("Annotate class: " + configurableClass + " with ConfigurationProperties annotation.");
			}
			
			String synchronizeKey = annotation.synchronizeKey();

			StreamResult result = null;
			Document document = null;

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			String[] configDir = xmlPropertiesMetaData.configDirectories();
			String configurationRelativepath = StringUtility.getDelimiterSeparatedString(configDir, File.separator);
			String configurationAbsolutePath = FileUtil.formAbsolutePath(configurationRelativepath, serverContext.getServerHome());
			FileUtil.createDirectories(configurationAbsolutePath);
			
			configurationAbsolutePath = configurationAbsolutePath + File.separator + xmlPropertiesMetaData.name() + ".xml";
			
			File file = new File(configurationAbsolutePath);
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			result = new StreamResult(outputStream);
			transformer.transform(source, result);	     
			configurationData.setNetConfigurationKey(synchronizeKey);
			configurationData.setNetConfigurationData(outputStream.toByteArray());	        

		} catch(Exception e){
			
		}
		return configurationData;
	}
}
