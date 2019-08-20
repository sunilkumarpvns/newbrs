package com.elitecore.aaa.core.plugins.conf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.groovy.control.CompilationFailedException;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.io.ByteStreams;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.script.ScriptPlugin;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.diameterapi.plugins.DiameterPlugin;
import com.elitecore.diameterapi.plugins.script.DiameterGroovyPlugin;
import com.elitecore.diameterapi.plugins.script.DiameterScriptPlugin;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

@ConfigurationProperties(moduleName ="DIA-GROOVY-PLGN-CONFIGURABLE", synchronizeKey ="", readWith = CustomReader.class, reloadWith = CustomReader.class, writeWith = XMLWriter.class,fallbackReadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system", "schema"} ,configDirectories = {"conf","db","plugin","diameter"}, name = "dia-groovy-plugins")
@XmlRootElement(name = "dia-groovy-plugins")
public class DiameterGroovyPluginConfigurable extends BasePluginConfigurable<DiameterPlugin> {
	
	public static final String MODULE = "DIA-GRVY-PLGN-CONF";
	
	private List<DiameterGroovyPluginMapping> mappings = new ArrayList<DiameterGroovyPluginMapping>();

	private Map<String, ScriptPlugin> pluginScripts = null;
	private PluginContext pluginContext;
	private GroovyClassLoader groovyLoader = new GroovyClassLoader(getClass().getClassLoader());

	public DiameterGroovyPluginConfigurable() {
		this.pluginScripts = new HashMap<String, ScriptPlugin>();
		this.pluginContext = new PluginContext() {

			@Override
			public ServerContext getServerContext() {
				return ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		};
	}
	
	@XmlElement(name = "dia-groovy-plugin-mapping")
	public List<DiameterGroovyPluginMapping> getMappings() {
		return mappings;
	}
	
	@CustomRead
	public void readConfiguration() throws SQLException {
		AAAConfigurationContext aaaConfigurationContext = (AAAConfigurationContext)getConfigurationContext();
		
		readFromDB(aaaConfigurationContext);
		
		LogManager.getLogger().info(MODULE, "Succesfully read groovy plugin file(s): " + pluginScripts.keySet());
	}
	
	private void readFromDB(AAAConfigurationContext aaaConfigurationContext) throws SQLException {
		LogManager.getLogger().info(MODULE, "Reading groovy plugin files.");
		final AAAServerContext serverContext = aaaConfigurationContext.getServerContext();
		String diaGroovyHome = diameterGroovyHome(serverContext);
		
		File directory = new File(diaGroovyHome);
		directory.mkdirs();
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resulSet = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			AAAServerContext context = (AAAServerContext)this.pluginContext.getServerContext();
			Set<PluginInfo> diaGroovyPlugin = context.getPluginDetail().getTypeSpecific(PluginType.DIAMETER_GROOVY_PLUGIN);
			for (PluginInfo pluginInfo : diaGroovyPlugin) {
				DiameterGroovyPluginMapping mapping = new DiameterGroovyPluginMapping();
				mapping.setPluginName(pluginInfo.getName());
				pstmt = connection.prepareStatement(getConfigurationQuery());
				pstmt.setString(1, pluginInfo.getId());
				resulSet = pstmt.executeQuery();

				while (resulSet.next()) {
					String fileName = resulSet.getString("GROOVYFILENAME");
					long timestamp = resulSet.getTimestamp("LASTUPDATETIME").getTime();
					byte[] groovyFileContent = resulSet.getBytes("GROOVYFILE");
					try {
						File file = new File(directory.getPath() + File.separator + fileName);
						ByteArrayInputStream groovyFileContentBinaryStream = new ByteArrayInputStream(groovyFileContent);
						if (file.exists()) {
							if (timestamp > file.lastModified()) {
								changeFileContent(file, groovyFileContentBinaryStream);
							} else {
								LogManager.getLogger().info(MODULE, "Using the newer version, found in local repository, kindly update" +
										" central DB repository as per analysis - " + fileName);
								context.generateSystemAlert(AlertSeverity.INFO, Alerts.OTHER_GENERIC, MODULE, 
										"Using the newer version, found in local repository, kindly update" +
												" central DB repository as per analysis - " + fileName);
							}
						} else {
							createFile(file, groovyFileContentBinaryStream);
							file.setLastModified(timestamp);
						}
						mapping.getFileNames().add(fileName);
						createPluginInstance(pluginInfo,file);
					} catch (IOException e) {
						LogManager.getLogger().warn(MODULE, "Configuration reading for " + pluginInfo.getName() +
								" plugin failed, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				
				getMappings().add(mapping);
			}
		} finally {
			DBUtility.closeQuietly(resulSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
		}

	}
	
	private void createPluginInstance(PluginInfo pluginInfo, File file) {
		GroovyObject groovyObject = createGroovyObject(file, pluginInfo);
		if (groovyObject instanceof ScriptPlugin) {
			pluginScripts.put(pluginInfo.getName(), (ScriptPlugin)groovyObject);
		}
		
	}

	@CustomReload
	public void reload() {
		/**
		 * This method is empty as this configurable is not reloaded.
		 */
	}
	
	private void readGroovyFilesFromFileSystem() {
		AAAServerContext context = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		PluginDetail pluginDetails = context.getPluginDetail();
		String diaGroovyHome = diameterGroovyHome(context);
		
		for (DiameterGroovyPluginMapping mapping : getMappings()) {
			for(String fileName : mapping.getFileNames()) {
				File file = new File(diaGroovyHome + File.separator + fileName);
				createPluginInstance(pluginDetails.getNameToInfoMap().get(mapping.getPluginName()), file);
			}
		}
		LogManager.getLogger().info(MODULE, "Succesfully read groovy plugin file(s): " + pluginScripts.keySet());
	}

	private String diameterGroovyHome(AAAServerContext context) {
		String radGroovyHome = context.getServerHome() + File.separator + "conf" + 
				File.separator + "db" + File.separator + "scripts" + File.separator + "plugins" + File.separator + "diameter";
		return radGroovyHome;
	}

	private File createFile(File file, InputStream binaryStream) throws IOException {
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file); //NOSONAR - Reason: Resources should be closed
		stream.write(ByteStreams.readFully(binaryStream));
		stream.close();
		return file;
	}

	private void changeFileContent(File file, InputStream binaryStream) throws IOException {
		file.delete();
		createFile(file, binaryStream);
	}

	private String getConfigurationQuery() {
		return "SELECT GROOVYFILENAME,GROOVYFILE,LASTUPDATETIME FROM TBLMGROOVYFILES WHERE " +
				"PLUGINID IN (SELECT PLUGINID FROM TBLMGROOVYPLUGIN WHERE PLUGININSTANCEID = ?)";
	}
	
	@PostRead
	public void postRead() {
		if(((AAAConfigurationContext) getConfigurationContext()).state() == AAAConfigurationState.FALLBACK_CONFIGURATION) {
			readGroovyFilesFromFileSystem();
		}
	}
	
	@PostReload
	public void postReload() {
		
	}
	
	@PostWrite
	public void postWrite() {
		
	}
	
	public GroovyObject createGroovyObject(File file, PluginInfo info) {
		GroovyObject obj = null;
		try{
			Class<?> groovyClass = groovyLoader.parseClass(file);
			Constructor<?> groovyConst = groovyClass.getConstructor(PluginContext.class, PluginInfo.class);
			obj = (GroovyObject) groovyConst.newInstance(new Object[]{pluginContext, info});
		}catch(CompilationFailedException ex){
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}catch (SecurityException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (NoSuchMethodException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (IllegalArgumentException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (InstantiationException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (IllegalAccessException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (InvocationTargetException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}catch (FileNotFoundException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: File does not exist at " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (IOException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
		return obj;
	}

	@Override
	public void createPlugin(Map<String, DiameterPlugin> nameToPlugin) {

		for (Entry<String, ScriptPlugin> script : this.pluginScripts.entrySet()) {
			com.elitecore.core.commons.plugins.PluginInfo pluginInfo = new com.elitecore.core.commons.plugins.PluginInfo();
			pluginInfo.setPluginName(script.getKey());
			DiameterGroovyPlugin groovyPlugin = new DiameterGroovyPlugin((DiameterScriptPlugin)script.getValue(), pluginContext, pluginInfo);
			try {
				groovyPlugin.init();
				LogManager.getLogger().info(MODULE, "Successfully initailized Diameter Groovy Plugin: " + script.getKey());
				nameToPlugin.put(script.getKey(), groovyPlugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().warn(MODULE, "Plugin with name: " + script.getKey() + " initialization failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}