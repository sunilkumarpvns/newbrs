package com.elitecore.config.core;


import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.core.annotations.ConfigurationProperties;
import com.elitecore.config.core.annotations.PostRead;
import com.elitecore.config.core.annotations.PostReload;
import com.elitecore.config.core.annotations.PostWrite;
import com.elitecore.config.core.factory.DefaultReaderFactory;
import com.elitecore.config.core.factory.DefaultWriterFactory;
import com.elitecore.config.core.factory.ReaderFactory;
import com.elitecore.config.core.factory.WriterFactory;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.config.exception.StoreConfigurationException;
import com.elitecore.config.util.ReflectionUtil;
import com.rits.cloning.Cloner;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class has the responsibility of providing parameters for global sharing.
 * Some configurable can be dependent on some parameters from some other configurable
 * 
 * Also this class is responsible for any dependencies of some external parameters
 * @author narendra.pathai
 *
 */
//FIXME the call to post reload and notify should be throwable safe
public class ConfigurationContext {
	private static final String MODULE = "CONF-CNTX";

	private String basePath;
	
	private Map<Class<? extends Configurable>, Configurable> globalSharingParameters;
	protected Readers readers;
	protected Writers writers;
	
	//This is the Deep clone helper for taking backup before reloading
	protected Cloner cloner = new Cloner();
	
	public ConfigurationContext(String basePath, ReaderFactory readerFactory, WriterFactory writerFactory) {

		Preconditions.checkNotNull(readerFactory, "Reader factory should not be null");
		Preconditions.checkNotNull(writerFactory, "Writer factory should not be null");

		this.basePath = basePath;
		this.globalSharingParameters = new ConcurrentHashMap<Class<? extends Configurable>, Configurable>();
		this.readers = new Readers(readerFactory);
		this.writers = new Writers(writerFactory);


		//Do not touch these cloner calls
		cloner.dontCloneInstanceOf(ConfigurationContext.class);
		cloner.dontCloneInstanceOf(Observer.class);
		cloner.dontClone(Field.class);
		cloner.dontCloneInstanceOf(ILogger.class);
		cloner.registerImmutable(ILogger.class);
	}

	public ConfigurationContext(String basePath) {
		this(basePath, new DefaultReaderFactory(), new DefaultWriterFactory());
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	protected void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	//FIXME take care to see this should never be null
	@SuppressWarnings("unchecked")
	public <T extends Configurable> T get(Class<T> clazz) {
		return (T) globalSharingParameters.get(clazz);
	}
	
	public Configurable read(Class<? extends Configurable> configurableClass)
	throws LoadConfigurationException {
		return readInternal(configurableClass);
	}
	
	Configurable readInternal(Class<? extends Configurable> configurableClass) 
	throws LoadConfigurationException {
		if (isComposite(configurableClass)) {
			return readComposite(configurableClass);
		} else {
			return readSimple(configurableClass);
		}
	}
	
	private boolean isComposite(Class<? extends Configurable> configurableClass) {
		return CompositeConfigurable.class.isAssignableFrom(configurableClass);
	}

	protected Configurable readComposite(Class<? extends Configurable> compositeConfigurableClass) 
	throws LoadConfigurationException {
		CompositeConfigurable compositeConfigurable = createCompositeConfigurableInstance(compositeConfigurableClass); 
		compositeConfigurable.setConfigurationContext(this);
		compositeConfigurable.read();
		sendPostReadNotification(compositeConfigurable);
		this.globalSharingParameters.put(compositeConfigurableClass, compositeConfigurable);
		return compositeConfigurable;
	}
	
	private CompositeConfigurable createCompositeConfigurableInstance(Class<? extends Configurable> compositeConfigurableClass)
	throws LoadConfigurationException {
		try {
			return (CompositeConfigurable) ReflectionUtil.createInstance(compositeConfigurableClass);
		} catch (Exception e) {
			throw new LoadConfigurationException(e.getMessage(), e);
		}
	}
	
	protected Configurable readSimple(Class<? extends Configurable> simpleConfigurableClass) 
	throws LoadConfigurationException {
		Class<? extends Reader> reader = extractReader(simpleConfigurableClass);
		Configurable configurable = readers.read(this, simpleConfigurableClass, reader);
		sendPostReadNotification(configurable);
		this.globalSharingParameters.put(simpleConfigurableClass, configurable);
		return configurable;
	}
	                         
	Configurable createDefaultInstanceInternal(Class<? extends Configurable> configurableClass){
		if (isComposite(configurableClass)) {
			return createCompositeDefaultInstance(configurableClass);
		} else {
			return createSimpleDefaultInstance(configurableClass);
		}
	}
	
	protected Configurable createCompositeDefaultInstance(Class<? extends Configurable> compositeConfigurableClass){
		try {
			CompositeConfigurable compositeConfigurable = createCompositeConfigurableInstance(compositeConfigurableClass);
			compositeConfigurable.createDefaultInstance();
			compositeConfigurable.setConfigurationContext(this);
			sendPostReadNotification(compositeConfigurable);
			return compositeConfigurable;
		} catch (Exception ex) {
			LogManager.getLogger().trace(MODULE, ex);
		}
		return null;
	}
	
	protected Configurable createSimpleDefaultInstance(Class<? extends Configurable> simpleConfigurableClass){
		try {
			Configurable simpleConfigurable = ReflectionUtil.createInstance(simpleConfigurableClass);
			simpleConfigurable.setConfigurationContext(this);
			sendPostReadNotification(simpleConfigurable);
			return simpleConfigurable;
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}
	
	public void write(Configurable configurable) throws StoreConfigurationException{
		writeInternal(configurable);
	}
	
	protected void writeInternal(Configurable configurable) throws StoreConfigurationException{
		if (configurable == null) {
			throw new StoreConfigurationException("Configuration not found");
		}
		if(isComposite(configurable)){
			writeComposite((CompositeConfigurable) configurable);
		}else{
			writeSimple(configurable);
		}
	}
	
	private boolean isComposite(Configurable configurable) {
		return configurable instanceof CompositeConfigurable;
	}

	protected void writeSimple(Configurable configurable) throws StoreConfigurationException{
		try{
			Class<? extends Writer> writer = extractWriter(configurable.getClass());
			writers.write(this, configurable, writer);
			sendPostWriteNotification(configurable);
		}catch(Exception ex){
			throw new StoreConfigurationException(ex);
		}
	}
	
	void writeComposite(CompositeConfigurable compositeConfigurable) throws StoreConfigurationException{
		try{
			compositeConfigurable.write();
			sendPostWriteNotification(compositeConfigurable);
		}catch (Exception ex) {
			throw new StoreConfigurationException(ex);
		}
	}
	
	public Configurable reload(Configurable configurable) throws LoadConfigurationException{
		Configurable configurableOnWhichReloadWillBePerformed = cloner.deepClone(configurable);
		if(isComposite(configurableOnWhichReloadWillBePerformed)){
			reloadComposite((CompositeConfigurable) configurableOnWhichReloadWillBePerformed);
		}else{
			reloadSimple(configurableOnWhichReloadWillBePerformed);
		}
		return configurableOnWhichReloadWillBePerformed;
	}
	
	void reloadInternal(Configurable configurable) throws LoadConfigurationException{
		if(isComposite(configurable)){
			reloadComposite((CompositeConfigurable) configurable);
		}else{
			reloadSimple(configurable);
		}
	}
	
	protected void reloadSimple(Configurable configurable) throws LoadConfigurationException{
		Class<? extends Reader> reloader = extractReloader(configurable.getClass());
		readers.reload(this, configurable, reloader);
		sendPostReloadNotification(configurable);
		/*
		 * this thing is not thread safe, there is assumption that sharing is only used
		 * during initialization time and reload time and only one thread will be using it
		 */
		this.globalSharingParameters.put(configurable.getClass(), configurable);
	}
	
	protected void reloadComposite(CompositeConfigurable compositeConfigurable) throws LoadConfigurationException{
		compositeConfigurable.reload();
		sendPostReloadNotification(compositeConfigurable);
		/*
		 * this thing is not thread safe, there is assumption that sharing is only used
		 * during initialization time and reload time and only one thread will be using it
		 */
		this.globalSharingParameters.put(compositeConfigurable.getClass(), compositeConfigurable);
	}
	
	
	
	protected Class<? extends Reader> extractReader(Class<? extends Configurable> configurableClass){
		ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
		return configurationProperties.readWith();
	}
	
	protected Class<? extends Writer> extractWriter(Class<? extends Configurable> configurableClass){
		ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
		return configurationProperties.writeWith();
	}
	
	protected Class<? extends Reader> extractReloader(Class<? extends Configurable> configurableClass){
		ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
		return configurationProperties.reloadWith();
	}
	
	protected void sendPostReadNotification(Configurable configurable) throws LoadConfigurationException{
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), PostRead.class).invoke(configurable, new Object[]{});
		} catch (Exception e) {
			throw new LoadConfigurationException(e.getMessage(), e);
		}
		
	}
	
	protected void sendPostWriteNotification(Configurable configurable) throws StoreConfigurationException {
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), PostWrite.class).invoke(configurable, new Object[]{});
		}catch (Exception ex) {
			throw new StoreConfigurationException(ex.getMessage(), ex);
		}
		
	}
	
	protected void sendPostReloadNotification(Configurable configurable) throws LoadConfigurationException{
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), PostReload.class).invoke(configurable, new Object[]{});
		}catch(Exception ex){
			throw new LoadConfigurationException(ex.getMessage(), ex);
		}
	}

}