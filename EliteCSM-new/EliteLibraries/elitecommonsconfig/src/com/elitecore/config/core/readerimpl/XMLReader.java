package com.elitecore.config.core.readerimpl;


import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.xml.sax.InputSource;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.core.Configurable;
import com.elitecore.config.core.ConfigurationContext;
import com.elitecore.config.core.Reader;
import com.elitecore.config.core.annotations.ConfigurationProperties;
import com.elitecore.config.core.annotations.Reloadable;
import com.elitecore.config.core.annotations.XMLProperties;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.config.util.FileUtil;
import com.elitecore.config.util.ReflectionUtil;

/**
 * This class uses the JAXB library to read the contents from the XML
 * @author narendra.pathai
 *
 */
public class XMLReader extends Reader{

	private static final String CONFIGURATION_EXTENSION = ".xml";
	private static final String SCHEMA_EXTENSION = ".xsd";

	@Override
	public Configurable read(ConfigurationContext configurationContext, Class<? extends Configurable> configurableClass) throws LoadConfigurationException {
		XMLProperties xmlConfigurationProperties = configurableClass.getAnnotation(XMLProperties.class);
		ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
		
		//Fetching all the configurations required from the annotations
		String basePath = configurationContext.getBasePath();
	
		String moduleName = configurationProperties.moduleName();
		String baseNameWithoutExtension = xmlConfigurationProperties.name();
		String relativeConfigurationDirectories = Strings.join(File.separator, xmlConfigurationProperties.configDirectories());
		String relativeSchemaDirectories = Strings.join(File.separator, xmlConfigurationProperties.schemaDirectories());
		String absoluteConfigurationPath = FileUtil.formAbsolutePath(relativeConfigurationDirectories, basePath);
		String absoluteSchemaPath = FileUtil.formAbsolutePath(relativeSchemaDirectories, basePath);
		
		//creating the directories required for configuration and schema file
		FileUtil.createDirectories(absoluteSchemaPath);
		
		try{
			JAXBContext context = JAXBContext.newInstance(configurableClass);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setValidating(false);
			org.xml.sax.XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			
			//generating schema
			generateSchema(context, baseNameWithoutExtension, absoluteSchemaPath);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			File schemaFile  = new File(absoluteSchemaPath + File.separator + baseNameWithoutExtension + SCHEMA_EXTENSION);
			Schema schema = schemaFactory.newSchema(schemaFile);
			spf.setSchema(schema);
			
			SAXSource source = new SAXSource(xmlReader, new InputSource(new FileInputStream(absoluteConfigurationPath + File.separator + xmlConfigurationProperties.name() + CONFIGURATION_EXTENSION)));
			Unmarshaller unmarshaler = context.createUnmarshaller();
			ValidationEventCollector collector = new ValidationEventCollector();
			unmarshaler.setEventHandler(collector);
			Configurable configurable = (Configurable) unmarshaler.unmarshal(source);
			injectConfigurationContext(configurable, configurationContext);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(moduleName, "Successfully completed reading configuration: " + absoluteConfigurationPath + File.separator + xmlConfigurationProperties.name() + CONFIGURATION_EXTENSION);
			}
			return configurable;
		}catch (Throwable ex) {
			throw new LoadConfigurationException("Error in reading configuration from xml for class: "
												+ configurableClass.getSimpleName() + ", due to reason: " 
												+ ex.getMessage(), ex);
		}
	}

	@Override
	public void reload(ConfigurationContext configurationContext,Configurable configurable) throws LoadConfigurationException {
		try{
			Configurable newConfigurable = read(configurationContext, configurable.getClass());
			if(isTotallyReloadable(configurable)){
				BeanUtils.copyProperties(configurable, newConfigurable);
			}else{
				doRecursiveProcessing(configurable.getClass(), configurable, newConfigurable);
			}
		}catch(Throwable ex){
			throw new LoadConfigurationException("Error in reloading configuration for class: "
												+ configurable.getClass().getSimpleName() + 
												" due to reason: "+ ex.getMessage(), ex);
		}
	}

	private boolean isTotallyReloadable(Configurable configurable){
		return configurable.getClass().isAnnotationPresent(Reloadable.class);
	}
	
	private void doRecursiveProcessing(Class<?> clazz, Object obj,Object newObj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(obj == null && newObj == null)
			return;
		
		List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(clazz, Reloadable.class);
		for(Method method : methods){

			boolean isCollection = isReturnTypeCollection(method);
			Reloadable reloadable = method.getAnnotation(Reloadable.class);
			Class<?> type = reloadable.type();
			
			if(isCollection){
				boolean isUserDefinedCollection = isUserDefinedType(type);

				if(isUserDefinedCollection){
					
					if(type.getAnnotation(Reloadable.class) != null){
						String setMethodName = removeAndPutSet(method.getName());
						Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
						setMethod.setAccessible(true);
						setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
					}else{
						compareCollectionAndReplace(type, method, obj, newObj);
					}
				}else{
					//The generic type in List is JAVA defined. so reload it completely
					String setMethodName = removeAndPutSet(method.getName());
					Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
					setMethod.setAccessible(true);
					setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
				}
			}else{
				//The return type is not collection but can be user defined type
				boolean isUserDefinedType = isUserDefinedType(type);

				if(isUserDefinedType){
					Object Obj1 = method.invoke(obj, new Object[]{});
					Object Obj2 = method.invoke(newObj, new Object[]{});
					if(type.getAnnotation(Reloadable.class) != null){
						String setMethodName = removeAndPutSet(method.getName());
						Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
						setMethod.setAccessible(true);
						setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
					}else{
						doRecursiveProcessing(type, Obj1, Obj2);
					}
				}else{
					String setMethodName = removeAndPutSet(method.getName());
					Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
					setMethod.setAccessible(true);
					setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
				}
			}
		}
	}
	
	/*
	 *Compare Collection Objects which are obtained using reflection.
	 *if both objects are equals then return true and do recursive processing.
	 */
	@SuppressWarnings("unchecked")
	private void compareCollectionAndReplace(Class<?> type, Method method, Object configurableInstance, Object newInstance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{

		Collection<Object> cltnObj1 = (Collection<Object>) method.invoke(configurableInstance, new Object[]{});
		Collection<Object> cltnObj2 = (Collection<Object>) method.invoke(newInstance, new Object[]{});

		for(Object obj : cltnObj1){
			for(Object innerObj : cltnObj2){
				if(compare(obj, innerObj)){
					doRecursiveProcessing(type, obj, innerObj);
				}
			}
		}
	}

	/*
	 * Compare two Collection type of Objects 
	 * 
	 */
	private boolean compare( Object obj1, Object obj2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		if(obj1 == null || obj2 == null)
			return false;

		try {
			Method equals = obj1.getClass().getMethod("equals", new Class<?>[]{Object.class});
			if(!isUserDefinedType(equals.getDeclaringClass())){
				return false;
			}
			return obj1.equals(obj2);
		} catch (NoSuchMethodException e) { 
			ignoreTrace(e);
		} catch (SecurityException e) {
			ignoreTrace(e);
		} 

		return false;
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
	
	private static boolean isUserDefinedType(Class<?> type) {
		//this logic is dependent on explicit interface or annotation
		//return UserDefined.class.isAssignableFrom(type);
		
		//this logic is dependent on whether the same class loader loaded the class as this class
		//If yes then the same application class loader is used which means that this class
		//belongs to our application
		return XMLReader.class.getClassLoader().equals(type.getClassLoader());
	}

	private static boolean isReturnTypeCollection(Method method) {
		return Collection.class.isAssignableFrom(method.getReturnType());
	}
	
	private static String removeAndPutSet(String name){
		return name.replaceFirst("g", "s");
	}
}
