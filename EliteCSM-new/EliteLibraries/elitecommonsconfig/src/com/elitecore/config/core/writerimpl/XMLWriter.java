package com.elitecore.config.core.writerimpl;



import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.core.Configurable;
import com.elitecore.config.core.ConfigurationContext;
import com.elitecore.config.core.Writer;
import com.elitecore.config.core.annotations.ConfigurationProperties;
import com.elitecore.config.core.annotations.XMLProperties;
import com.elitecore.config.exception.StoreConfigurationException;
import com.elitecore.config.util.FileUtil;

/**
 * 
 * @author narendra.pathai
 *
 */
public class XMLWriter extends Writer{

	private static final String CONFIG_EXTENSION = ".xml";
	private static final String SCHEMA_EXTENSION = ".xsd";

	@Override
	public void write(ConfigurationContext configurationContext, Configurable configurable) throws StoreConfigurationException {
		if(configurable == null){
			throw new StoreConfigurationException("Configuration instance not found");
		}
		
		ConfigurationProperties configurationProperties = configurable.getClass().getAnnotation(ConfigurationProperties.class);
		XMLProperties xmlProperties = configurable.getClass().getAnnotation(XMLProperties.class);
		if(xmlProperties == null){
			throw new StoreConfigurationException("XML Properties not found");
		}
		
		String moduleName = configurationProperties.moduleName();
		String name = xmlProperties.name();
		String basePath = configurationContext.getBasePath();
		String relativeConfigurationPath = Strings.join(File.separator, xmlProperties.configDirectories());
		String relativeSchemaPath = Strings.join(File.separator, xmlProperties.schemaDirectories());
		String absoluteConfigurationPath = FileUtil.formAbsolutePath(relativeConfigurationPath, basePath);
		String absoluteSchemaPath = FileUtil.formAbsolutePath(relativeSchemaPath, basePath);
		FileUtil.createDirectories(absoluteSchemaPath);
		FileUtil.createDirectories(absoluteConfigurationPath);
		
		try{
			File configurationFile  = new File(absoluteConfigurationPath + File.separator + name + CONFIG_EXTENSION);

			//creating the JAXB context the essential entity for JAXB processing
			JAXBContext jaxbContext = JAXBContext.newInstance(configurable.getClass());

			//creating the marshaler
			Marshaller marshaler = jaxbContext.createMarshaller();
			marshaler.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaler.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,  "file:///" + absoluteSchemaPath + File.separator + name + SCHEMA_EXTENSION);
			marshaler.marshal(configurable, configurationFile);
			
			generateSchema(jaxbContext, name, absoluteSchemaPath);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(moduleName, "Successfully created last good configuration file: " + name + CONFIG_EXTENSION);
			}
		}catch(Throwable ex){
			throw new StoreConfigurationException("Error in writing configuration" + ex.getMessage(), ex);
		}
	}
	
	private void generateSchema(JAXBContext context, final String name,String schemaPath ) throws IOException{
		
		final File schemaFile  = new File(schemaPath + File.separator + name + SCHEMA_EXTENSION);
		context.generateSchema(new SchemaOutputResolver() {

			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				return new StreamResult(schemaFile);
			}
		});
	}
	
}
