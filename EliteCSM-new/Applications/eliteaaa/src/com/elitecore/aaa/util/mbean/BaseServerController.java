package com.elitecore.aaa.util.mbean;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanException;

import com.elitecore.aaa.license.nfv.WebServiceParams;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.sync.SMConfigurationSynchronizer;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetPluginData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.core.util.mbean.data.dictionary.EliteDictionaryData;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public abstract class BaseServerController extends BaseMBeanController {

	private static final String MODULE = "AAA Controller";
	private static final String CONFIG_EXTENSION = ".xml";
	private Map<String, Class<? extends Configurable>> serverConfigMap = null;
	private Map<String, Map<String, Class<? extends Configurable>>> serviceConfigMap = null;
	private Map<String, Map<String, Class<? extends Configurable>>> pluginConfigMap = null;
	private static final String SYS_INFO_FILE = "_sys.info";
	private ServerContext serverContext;
	private SMConfigurationSynchronizer smConfigurationSynchronizer;
	
	public BaseServerController(ServerContext serverContext,SMConfigurationSynchronizer smConfigurationSynchronizer) {
		this.serverContext = serverContext;
		this.smConfigurationSynchronizer = smConfigurationSynchronizer;
		this.serverConfigMap = Maps.newHashMap();
		this.serviceConfigMap = Maps.newHashMap();
		this.pluginConfigMap = Maps.newHashMap();
	}

	public abstract String getModuleName();

	public String javaHome() {
		return System.getProperty("java.home");
	}

	
	protected abstract Map<String, List<Class<? extends Configurable>>> getServiceConfigurationClasses(); 
	protected abstract List<Class<? extends Configurable>> getServerConfigurationClasses();
	protected abstract Map<String, List<Class<? extends Configurable>>> getPluginConfigurationClasses();
						
	
	final protected Map<String, Class<? extends Configurable>> getSynchronizeKeyToConfigigurationClassMap(List<Class<? extends Configurable>> ConfClasses){
		return Collectionz.asHashMap(ConfClasses, new Function<Class<? extends Configurable>, String>(){

			@Override
			public String apply(Class<? extends Configurable> arg) {
				ConfigurationProperties annotation = arg.getAnnotation(ConfigurationProperties.class);
				if(annotation == null){
					throw new IllegalArgumentException("Annotate class: " + arg + " with ConfigurationProperties annotation.");
				}
				return annotation.synchronizeKey();
			}
		});
	}

	final public void registerConfigurations() {
		this.serverConfigMap = getSynchronizeKeyToConfigigurationClassMap(getServerConfigurationClasses());
		Map<String, List<Class<? extends Configurable>>> serviceConfigClasses = getServiceConfigurationClasses();
		for (Entry<String, List<Class<? extends Configurable>>> configClassEntry : serviceConfigClasses.entrySet()) {

			this.serviceConfigMap.put(configClassEntry.getKey(),
					getSynchronizeKeyToConfigigurationClassMap(configClassEntry.getValue()));
		}
//		Map<String, List<Class<? extends Configurable>>> pluginConfigClasses = getPluginConfigurationClasses();
//		for (Entry<String, List<Class<? extends Configurable>>> configClassEntry : pluginConfigClasses.entrySet()) {
//			this.pluginConfigMap.put(configClassEntry.getKey(),
//					getSynchronizeKeyToConfigigurationClassMap(configClassEntry.getValue()));
//		}
	}

	public abstract String serverHome();
	/**
	 * Writes system given information to system info file.
	 * 
	 * @param serverID
	 *            the information to be written.
	 */
	public abstract String getSystemPath();
	final public void writeServerInstanceDetails(String serverID, String serverName) {
		if (serverID == null || serverID.length() < 1 || serverName == null || serverName.trim().length() == 0)
			return;
		
		final String SERVER_ID = "id";
		final String SERVER_NAME = "name";
		
		serverID = SERVER_ID + "=" + serverID;
		serverName = SERVER_NAME + "=" + serverName;
		
		File systemFolder = new File(serverHome() + File.separator + getSystemPath());
		PrintWriter detailWriter = null;
		try {

			if (!systemFolder.exists())
				systemFolder.mkdirs();
			detailWriter = new PrintWriter(new FileWriter(new File(systemFolder, SYS_INFO_FILE), false)); //NOSONAR - Reason: Resources should be closed
			detailWriter.println(serverID);
			detailWriter.println(serverName);
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE,"Error occured while writing system info, reason: "+ e.getMessage());
		} finally {
			if (detailWriter != null)
				detailWriter.close();
		}
	}

	final public String getVersionInformation() {
		String version = serverContext.getServerVersion();
		int index = version.lastIndexOf('.');
		if (index != -1){
			version = version.substring(0, index);
		}
		return version;
	}

	final public void updateServerConfiguration(EliteNetServerData eliteNetServerData, String version) throws Exception {
		String strTempConf = serverHome() + File.separator + "tempconf";
		String strConf = serverHome() + File.separator + "conf";
		try{

			doBackUp(new File(strConf),new File(strTempConf));
			if (eliteNetServerData == null || !getVersionInformation().equalsIgnoreCase(version)) {
				throw new Exception("Server Version mismatch Or NetServerData is NULL.");
			}			
			String configurationName = eliteNetServerData.getNetServerName();
			List<EliteNetConfigurationData> lstServerConfig = eliteNetServerData.getNetConfigurationList();
			Iterator<EliteNetConfigurationData> iterator = lstServerConfig.iterator();

			while (iterator.hasNext()) {
				EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData) iterator.next();
				String configKey = (eliteNetConfigurationData).getNetConfigurationKey();
				Class<? extends Configurable> configurableClass = serverConfigMap.get(configKey);
				if (configurableClass != null) {
					try {

						XMLProperties xmlPropertiesMetaData = configurableClass.getAnnotation(XMLProperties.class);
						String xmlFileName = xmlPropertiesMetaData.name()+CONFIG_EXTENSION;
						String strReturn = serverContext.getServerHome() +File.separator + "tempconf";

						String[]configPath = xmlPropertiesMetaData.configDirectories();
						if(configPath.length >1){
							for(int i=1;i<configPath.length;i++){
								List<String> configPaths = new ArrayList<String>();
								configPaths.add(configPath[i]);
								strReturn = strReturn+File.separator+StringUtility.getDelimitirSeparatedString(configPaths, File.separator);
							}	
						}
						boolean bSuccess = smConfigurationSynchronizer.synchronizeTo(strReturn, xmlFileName, eliteNetConfigurationData.getNetConfigurationData());

						if (!bSuccess) {
							LogManager.getLogger().debug(MODULE,"Error while updating configuration for "+ configurationName);
							throw new UpdateConfigurationFailedException("Updating configuration failed for "+ configurationName);
						}
					} catch (UpdateConfigurationFailedException upde) {
						LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: "+ upde.getMessage());
						LogManager.getLogger().trace(MODULE, upde);
						throw new UpdateConfigurationFailedException(upde
								.getMessage(), upde);
					} catch (Exception e) {
						LogManager.getLogger().error(MODULE,"Updating configuration failed for "+ configurationName + " ,reason: "+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						throw new UpdateConfigurationFailedException(e
								.getMessage(), e);
					}

				} else {
					LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: object not found for key:"+ configKey);
					throw new UpdateConfigurationFailedException("Error Updating Configuration, reason: object not found for key:"+ configKey);
				}
			}

			List<EliteNetServiceData> lstServiceInstance = eliteNetServerData.getNetServiceList();
			Iterator<EliteNetServiceData> itrService = lstServiceInstance.iterator();
			while (itrService.hasNext()) {
				// bSuccess = false;
				EliteNetServiceData eliteNetServiceData = (EliteNetServiceData) itrService.next();
				writeConfiguration(eliteNetServiceData);
			}
			List<EliteNetPluginData> lstPluginList = eliteNetServerData.getPluginList();
			if(lstPluginList!=null){
				Iterator<EliteNetPluginData> itrPlugin = lstPluginList.iterator();
				while(itrPlugin.hasNext()) {
					EliteNetPluginData eliteNetPluginData = (EliteNetPluginData)itrPlugin.next();
					writeConfiguration(eliteNetPluginData);
				}
			}

			try {
				doBackUp(new File(strConf));
			}catch(IOException e){
				LogManager.getLogger().trace(e);
				LogManager.getLogger().warn(MODULE, "problem while taking backup of conf folder, reason : " + e.getMessage());
			}

			File tempConfFile = new File(strTempConf);
			File confFile = new File(strConf);
			if(confFile.exists()){
				if(!confFile.canWrite()){
					LogManager.getLogger().warn(MODULE, "Updating configuration failed for " + configurationName + ", Reason: permission denied for conf directory");
					throw new UpdateConfigurationFailedException("Updating configuration failed for " + configurationName + ", Reason: permission denied for conf directory");
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "conf directory does not exist, creating new conf directory");
				}
			}
			if(tempConfFile != null && tempConfFile.canWrite()) {
				deleteFile(confFile);
				if(!tempConfFile.renameTo(confFile)){
					LogManager.getLogger().warn(MODULE, "Updating configuration failed for " + configurationName + ", Reason: cannot rename the diractory tempconf to conf");
					throw new UpdateConfigurationFailedException("Updating configuration failed for " + configurationName + ", Reason: cannot rename the diractory tempconf to conf");
				}
				LogManager.getLogger().info(MODULE, "Renamed File "+tempConfFile.getName()+ " to " + confFile.getName());
			}
			LogManager.getLogger().info(MODULE, "Synchronize To completed successfully");
		}finally{
			deleteFile(strTempConf);
		}
	}


	final public void writeConfiguration(EliteNetServiceData eliteNetServiceData) throws UpdateConfigurationFailedException {
		String serviceConfigName = eliteNetServiceData.getNetServiceName();
		List<EliteNetConfigurationData> lstServiceConfig = eliteNetServiceData.getNetConfigurationList();
		Iterator<EliteNetConfigurationData> iterator = lstServiceConfig.iterator();

		while (iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData) iterator.next();
			String configKey = (eliteNetConfigurationData).getNetConfigurationKey();
			Map<String,Class<? extends Configurable>> configMap = this.serviceConfigMap.get(serviceConfigName);
			if(configMap != null){
				Class<? extends Configurable> configurableClass = configMap.get(configKey);				
				if (configurableClass != null) {
					try {
						XMLProperties xmlPropertiesMetaData = configurableClass.getAnnotation(XMLProperties.class);
						String xmlFileName = xmlPropertiesMetaData.name()+CONFIG_EXTENSION;
						String strReturn = serverContext.getServerHome() +File.separator + "tempconf";

						String[]configPath = xmlPropertiesMetaData.configDirectories();
						if(configPath.length >1){
							for(int i=1;i<configPath.length;i++){
								List<String> configPaths = new ArrayList<String>();
								configPaths.add(configPath[i]);
								strReturn = strReturn+File.separator+StringUtility.getDelimitirSeparatedString(configPaths, File.separator);
							}	
						}

						boolean bSuccess = smConfigurationSynchronizer.synchronizeTo(strReturn, xmlFileName, eliteNetConfigurationData.getNetConfigurationData());

						if (!bSuccess) {
							LogManager.getLogger().debug(MODULE,"Error while updating configuration for "+ serviceConfigName);
							throw new UpdateConfigurationFailedException("Updating configuration failed for "+ serviceConfigName);
						}
					} catch (UpdateConfigurationFailedException upde) {
						LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: "+ upde.getMessage());
						LogManager.getLogger().trace(MODULE, upde);
						throw new UpdateConfigurationFailedException(upde.getMessage(), upde);
					} catch (Exception e) {
						LogManager.getLogger().error(MODULE,"Updating configuration failed for "+ serviceConfigName + " ,reason: "+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						throw new UpdateConfigurationFailedException(e.getMessage(), e);
					}

				} else {
					LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: object not found for key:"+ configKey);
					throw new UpdateConfigurationFailedException("Error Updating Configuration, reason: object not found for key:"+ configKey);											
				}
			}
		}
	}
	final public void writeConfiguration(EliteNetPluginData eliteNetPluginData) throws UpdateConfigurationFailedException {
		String pluginConfigName = eliteNetPluginData.getPluginName();
		List<EliteNetConfigurationData> lstPluginConfig = eliteNetPluginData.getNetConfigurationDataList();
		Iterator<EliteNetConfigurationData> iterator = lstPluginConfig.iterator();
		
		while (iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData) iterator.next();
			String configKey = (eliteNetConfigurationData).getNetConfigurationKey();
			Map<String,Class<? extends Configurable>> configMap = this.pluginConfigMap.get(pluginConfigName);
			if(configMap != null){
				Class<? extends Configurable> configurableClass = configMap.get(configKey);				
				if (configurableClass != null) {
					try {
						XMLProperties xmlPropertiesMetaData = configurableClass.getAnnotation(XMLProperties.class);
						String xmlFileName = xmlPropertiesMetaData.name()+CONFIG_EXTENSION;
						String strReturn = serverContext.getServerHome() +File.separator + "tempconf";

						String[]configPath = xmlPropertiesMetaData.configDirectories();
						if(configPath.length >1){
							for(int i=1;i<configPath.length;i++){
								List<String> configPaths = new ArrayList<String>();
								configPaths.add(configPath[i]);
								strReturn = strReturn+File.separator+StringUtility.getDelimitirSeparatedString(configPaths, File.separator);
							}	
						}
						boolean bSuccess = smConfigurationSynchronizer.synchronizeTo(strReturn, xmlFileName, eliteNetConfigurationData.getNetConfigurationData());

						if (!bSuccess) {
							LogManager.getLogger().debug(MODULE,"Error while updating configuration for "+ pluginConfigName);
							throw new UpdateConfigurationFailedException("Updating configuration failed for "+ pluginConfigName);
						}
						return;
					} catch (UpdateConfigurationFailedException upde) {
						LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: "+ upde.getMessage());
						LogManager.getLogger().trace(MODULE, upde);
						throw new UpdateConfigurationFailedException(upde.getMessage(), upde);
					} catch (Exception e) {
						LogManager.getLogger().error(MODULE,"Updating configuration failed for "+ pluginConfigName + " ,reason: "+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						throw new UpdateConfigurationFailedException(e.getMessage(), e);
					}

				} else {
					LogManager.getLogger().debug(MODULE,"Error Updating Configuration, reason: object not found for key:"+ configKey);
					throw new UpdateConfigurationFailedException("Error Updating Configuration, reason: object not found for key:"+ configKey);											
				}
			}
		}
	}
	
	public final void writeWebServiceDetail(String userName, String password, String address, Integer port,
			String contextPath) throws MBeanException {			
		WebServiceParams webServiceParams = new WebServiceParams(serverHome() + File.separator + getSystemPath());
		webServiceParams.setUserName(userName);
		webServiceParams.setPassword(password);
		webServiceParams.setAddress(address);
		webServiceParams.setPort(port);
		webServiceParams.setContextPath(contextPath);
		try {
			webServiceParams.writeToFile();
		} catch (NoSuchEncryptionException e) {
			throw new MBeanException(e, "Failed to write web service details , Reason : " + e.getMessage());
		} catch (EncryptionFailedException e) {
			throw new MBeanException(e, "Failed to write web service details , Reason : " + e.getMessage());
		} catch (IOException e) {
			throw new MBeanException(e, "Failed to write web service details , Reason : " + e.getMessage());
		}
	}	
	
	final public String readServerInstanceId() {
		try {
			return PasswordEncryption.getInstance().crypt(serverContext.getServerInstanceId(),PasswordEncryption.ELITECRYPT);
		} catch (NoSuchEncryptionException e) {
			e.printStackTrace();
		} catch (EncryptionFailedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	final public String getName() {
		return MBeanConstants.CONFIGURATION;
	}

	final protected List<EliteNetServiceData> getNetServiceDataList(){
		//ServiceConfigMap
		List<EliteNetServiceData> newServiceList = new ArrayList<EliteNetServiceData>();
		
		for(Entry<String,Map<String,Class<? extends Configurable>>> serviceConfigMapEntry : serviceConfigMap.entrySet()){
			EliteNetServiceData serviceData = new EliteNetServiceData();
			serviceData.setNetServiceId(serviceConfigMapEntry.getKey());
			serviceData.setNetInstanceId("000");
			serviceData.setNetServiceName(serviceConfigMapEntry.getKey());
			List<EliteNetConfigurationData> confDataList = new ArrayList<EliteNetConfigurationData>();				
			for(Entry<String,Class<? extends Configurable>> entrySet : serviceConfigMapEntry.getValue().entrySet()){
				Class<? extends Configurable> configurableClass = entrySet.getValue();
				if(configurableClass !=null){	
					EliteNetConfigurationData confData = smConfigurationSynchronizer.synchronizeFrom(configurableClass);
					if( confData != null){
						confDataList.add(confData);
					}					
				}
			}
			serviceData.setNetConfigurationList(confDataList);
			newServiceList.add(serviceData);
		}
		return newServiceList;
	}
	final protected List<EliteNetConfigurationData> getNetServerDataList(){
		List<EliteNetConfigurationData> confList = new ArrayList<EliteNetConfigurationData>();
		
		for(Entry<String, Class<? extends Configurable>> confSet : serverConfigMap.entrySet() ){
			Class<? extends Configurable> configurable = confSet.getValue();
			if(configurable != null){				
				EliteNetConfigurationData confData = smConfigurationSynchronizer.synchronizeFrom(configurable);
				if(confData !=null){
					confList.add(confData);
				}
			}				
		}
						
		return confList;
	}

	final protected List<EliteNetPluginData> getNetPluginDataList(){
		//ServiceConfigMap
		List<EliteNetPluginData> newPluginList = new ArrayList<EliteNetPluginData>();
		
		for(Entry<String,Map<String,Class<? extends Configurable>>> pluginConfigMapEntry : pluginConfigMap.entrySet()){
			EliteNetPluginData pluginData = new EliteNetPluginData();
			pluginData.setPluginName(pluginConfigMapEntry.getKey());
			List<EliteNetConfigurationData> confDataList = new ArrayList<EliteNetConfigurationData>();				
			for(Entry<String,Class<? extends Configurable>> entrySet : pluginConfigMapEntry.getValue().entrySet()){
				Class<? extends Configurable> configurableClass = entrySet.getValue();
				if(configurableClass !=null){	
					EliteNetConfigurationData confData = smConfigurationSynchronizer.synchronizeFrom(configurableClass);
					if( confData != null){
						confDataList.add(confData);
					}					
				}
			}
			pluginData.setNetConfigurationDataList(confDataList);
			newPluginList.add(pluginData);
		}
		return newPluginList;
	}

	protected abstract String getServerKey();
	final public EliteNetServerData readServerConfiguration() {			
		
		EliteNetServerData netServerData = new EliteNetServerData();
		
		netServerData.setNetServerId(getServerKey());
		netServerData.setVersion(serverContext.getServerVersion());
		netServerData.setNetServerName(serverContext.getServerName());	
		
		netServerData.setNetConfigurationList(getNetServerDataList());						
		
		netServerData.setNetServiceList(getNetServiceDataList());//EliteNetServiceData
		netServerData.setPluginList(getNetPluginDataList());
		LogManager.getLogger().info(MODULE, "Server Configuration Data returned");
		return netServerData;			
	}

	final public List<String> updateDictionary(List<EliteDictionaryData> eliteDictionaryList) throws Exception {
		List<String> invalidDictionaryList = new ArrayList<String>();
		List<EliteDictionaryData> validDictionaryData = new ArrayList<EliteDictionaryData>();
		if(eliteDictionaryList == null || eliteDictionaryList.isEmpty()){
			throw new Exception("Dictionary List is empty");
		}
		for(EliteDictionaryData eliteDictionaryData : eliteDictionaryList){
			if(!getVersionInformation().equals(eliteDictionaryData.getVersion())){
				throw new Exception("Server Version mismatch");
			}
			if(eliteDictionaryData.getDictionaryData() == null || eliteDictionaryData.getDictionaryData().length <= 0){
				invalidDictionaryList.add(eliteDictionaryData.getVendorName());
			}else if(!serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_SUPPORTED_VENDOR,String.valueOf(eliteDictionaryData.getVendorId()))){
				invalidDictionaryList.add(eliteDictionaryData.getVendorName());
			}else{
				validDictionaryData.add(eliteDictionaryData);
			}
		}

		for(EliteDictionaryData eliteDictionaryData : validDictionaryData){
			String dictionaryDirectory = serverHome()+ File.separator + "dictionary" + File.separator + eliteDictionaryData.getType().toLowerCase();
			String dictionaryFileName = null;
			boolean bFileCreated = true;
			boolean bReadWritePermission=  false;
			File file = new File(dictionaryDirectory);
			if(!file.exists()) {
				bFileCreated = file.mkdirs();
				LogManager.getLogger().info(MODULE,"Creating a directory : " + file.getAbsoluteFile());
			}else{
				if(eliteDictionaryData.getType().equalsIgnoreCase("radius")){
					dictionaryFileName = Dictionary.getInstance().findDictionaryName(file,eliteDictionaryData.getVendorId());
				}else if(eliteDictionaryData.getType().equalsIgnoreCase("diameter")){
					dictionaryFileName = com.elitecore.diameterapi.diameter.common.util.DiameterDictionary.getInstance().findDictionaryName(file,eliteDictionaryData.getVendorId(),eliteDictionaryData.getApplicationId());
				}
			}
			if(dictionaryFileName == null){
				dictionaryFileName = eliteDictionaryData.getVendorName().toLowerCase() + CONFIG_EXTENSION;
			}					

			File destFile = new File(file,dictionaryFileName);
			if(destFile!= null && !destFile.exists() && bFileCreated) {
				bFileCreated = destFile.createNewFile();
				LogManager.getLogger().info(MODULE, "Creating dictionary file : " +  destFile.getAbsoluteFile());
			}
			bReadWritePermission = destFile.canRead() & destFile.canWrite();
			if(bFileCreated && bReadWritePermission) {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile));
				stream.write(eliteDictionaryData.getDictionaryData());
				stream.close();
				LogManager.getLogger().info(MODULE, "Writing dictionary content in " +  destFile.getAbsoluteFile());
			}else{			
				throw new Exception("Dictionary creation failed or read-write accees not allowed");
			}				
		}

		return invalidDictionaryList;
	}
	
	public Map<String,Object> retriveServerLogFileList() throws Exception {
		String logFileDirectory = serverHome() + File.separator + "logs";
		Map<String, Object> fileMap = new LinkedHashMap<String,Object>();
		fillDirectoryFiles(logFileDirectory,fileMap);
		return fileMap;
	}
	protected  void fillDirectoryFiles(String strDirectory, Map<String, Object> fileMap) throws IOException {
		File directory = new File(strDirectory);
		if(directory.exists() && directory.isDirectory()){
			fileMap.put(directory.getCanonicalPath(),retriveFileMap(directory));
		}
	}
	protected Map<String,Object>  retriveFileMap(File file) throws IOException{
		Map<String,Object> fileMap = new LinkedHashMap<String,Object>();
		if(file!=null && file.isDirectory()){
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(files[i].isDirectory() && !files[i].isHidden()){

					fileMap.put(files[i].getCanonicalPath(),retriveFileMap(files[i]));
				}else{
					if(files[i].getName().toLowerCase().endsWith(".log") || 
							files[i].getName().toLowerCase().endsWith(".local") || 
							files[i].getName().toLowerCase().endsWith(".csv") || 
							files[i].getName().toLowerCase().endsWith(".zip") ||
							files[i].getName().toLowerCase().endsWith(".ar") ||
							files[i].getName().toLowerCase().endsWith(".lzma") ||
							files[i].getName().toLowerCase().endsWith(".rar") ||
							files[i].getName().toLowerCase().endsWith(".jar") ||
							files[i].getName().toLowerCase().endsWith(".gz") ||
							files[i].getName().toLowerCase().endsWith(".bz2") ||
							files[i].getName().toLowerCase().endsWith(".xz"))
					{
						
							fileMap.put(files[i].getCanonicalPath(),files[i].getCanonicalPath());
					}
				}

			}

		}
		return fileMap;
	}

	
	public byte[] retriveFileData(String strFile, Integer offset)	throws Exception {
		InputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		byte[] fileData = null;
		try{
			File file = new File(strFile);
			
			if(file.canRead()){
				byte[] tempData = null;
				if(file.length()>50000){
					tempData = new byte[50000];
				}else{
					tempData = new byte[(int)file.length()];
				}
				fileInputStream = new FileInputStream(file);
				int length = tempData.length;
				if(offset+length > file.length()){
					length = (int)(file.length() - offset.intValue());
				}
				fileInputStream.skip(offset);
				int n = fileInputStream.read(tempData,0,length);
				fileData = new byte[n];
				System.arraycopy(tempData, 0, fileData, 0, n);
				
			}
		}catch(FileNotFoundException e) {
			LogManager.getLogger().error(MODULE, "File Not Found : " + e.getMessage());
			throw e;
		} catch(Exception e){
			LogManager.getLogger().error(MODULE, "Problem while reading file: " + e.getMessage());
			throw e;
		}finally{
			try{
				if(fileInputStream!=null)
					fileInputStream.close();
				if(dataInputStream!=null)
					dataInputStream.close();
			}catch(IOException ioExc){
				LogManager.getLogger().error(MODULE, "Problem while Reading Logs "+ioExc.getMessage());
			}
		}
		return fileData;
	}

	private final void doBackUp(File srcDir) throws IOException{
		String strFDate = null;
		Date date = null;
		SimpleDateFormat sdf = null;
		
		try {
			sdf = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
			date = new Date();
			strFDate = sdf.format(date);
		}catch(IllegalArgumentException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().warn(MODULE, "Problem while formatting date, reason : " + e.getMessage());
		}
		
		String backUPDir = serverHome() + File.separator + File.separator + "data" + File.separator + "backup" + File.separator + "conf_" + strFDate;
		doBackUp(srcDir,new File(backUPDir));
	}
	
	public final void doBackUp(File srcDir,File destDir) throws IOException{
		boolean bFileCreated = true;
		boolean bReadWritePermission = false;
		 
		if(srcDir.exists()){
			if(srcDir.isDirectory()) {
				
				if(!destDir.exists()) {
					bFileCreated = destDir.mkdirs();
					//getControllerLogger().info(MODULE,"Creating a directory : " + destDir.getAbsoluteFile());
				}	

				String[] listSrcFile = srcDir.list();		
				for(int i=0;i<listSrcFile.length;i++) {
					doBackUp(new File(srcDir, listSrcFile[i]),new File(destDir, listSrcFile[i]));
				}
				
			}else{

				if(destDir!= null && !destDir.exists() && bFileCreated) {
					bFileCreated = destDir.createNewFile();
					//getControllerLogger().info(MODULE, "Creating configuration file : " +  destDir.getAbsoluteFile());
				}

				bReadWritePermission = destDir.canRead() & destDir.canWrite();

				if(bFileCreated && bReadWritePermission) {
					FileInputStream fileInputStream = null;
					ByteArrayOutputStream outStream = null;
					BufferedOutputStream stream = null;
					try{
						fileInputStream = new FileInputStream(srcDir);
						outStream = new ByteArrayOutputStream();
						byte[] srcBytes = new byte[1024];

						for (int readNum; (readNum = fileInputStream.read(srcBytes)) != -1;) {
							outStream.write(srcBytes, 0, readNum); 
						}

						byte[] fileContents = outStream.toByteArray();


						stream = new BufferedOutputStream(new FileOutputStream(destDir)); //NOSONAR - Reason: Resources should be closed
						stream.write(fileContents);
					}finally{
						if(fileInputStream!=null){
							fileInputStream.close();
						}
						if(outStream!=null){
							outStream.close();
						}
						if(stream!=null){
							stream.close();
						}
					}
					//	getControllerLogger().info(MODULE, "Writing updated configuration in " +  destDir.getAbsoluteFile());
				}else{			
					LogManager.getLogger().warn(MODULE, "Configuration file creation failed or read-write accees not allowed.  File: "+destDir.getAbsolutePath());
				}
			}	
		}
	}
	private void deleteFile(String strFile){
		deleteFile(new File(strFile));
	}
	private void deleteFile(File file) {
		try {
			if(file==null){
				return;
			}
			if(file.exists()) {
				if(file.isDirectory()){
					File[] childFiles = file.listFiles();
					if(childFiles!=null && childFiles.length>0){
						for (int i = 0; i < childFiles.length; i++) {
							deleteFile(childFiles[i]);	
						}
						
					}
					file.delete();
				}else{
					file.delete();
				}
			}
		}catch(NullPointerException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().warn(MODULE, "Problem while Deleting temperory Config folder, reason : " + e.getMessage());
		}catch(SecurityException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().warn(MODULE, "Problem while Deleting temperory Config folder, reason : " + e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
