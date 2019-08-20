package com.elitecore.aaa.core.plugins.conf;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

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
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.plugins.script.RadiusGroovyPlugin;
import com.elitecore.aaa.radius.plugins.script.RadiusScriptPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
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
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;

@ConfigurationProperties(moduleName ="RAD-GROOVY-PLGN-CONFIGURABLE", synchronizeKey ="", readWith = CustomReader.class, writeWith = XMLWriter.class, reloadWith = CustomReader.class, fallbackReadWith = XMLReader.class)
@XMLProperties(configDirectories = {"conf", "db", "plugin", "radius"}, name = "rad-groovy-plugins", schemaDirectories = { "system", "schema" })
@XmlRootElement(name = "rad-groovy-plugins")
public class RadiusGroovyPluginConfigurable extends BasePluginConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>> {

	public static final String MODULE = "RAD-GRVY-PLGN-CONF";

	private List<RadiusGroovyPluginMapping> mappings = new ArrayList<RadiusGroovyPluginMapping>();
	
	/* Transient fields */
	private Map<String, ScriptPlugin> pluginScripts = null;
	private PluginContext pluginContext;
	private GroovyClassLoader groovyLoader = new GroovyClassLoader(getClass().getClassLoader());

	public RadiusGroovyPluginConfigurable() {
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

	@XmlElement(name = "rad-groovy-plugin-mapping")
	public List<RadiusGroovyPluginMapping> getMappings() {
		return mappings;
	}
	
	@CustomRead
	public void readConfiguration() throws SQLException {
		AAAConfigurationContext aaaConfigurationContext = (AAAConfigurationContext)getConfigurationContext();

		readPluginsFromDB(aaaConfigurationContext);
		
		LogManager.getLogger().info(MODULE, "Succesfully read groovy plugin file(s): " + pluginScripts.keySet());
	}

	private void readPluginsFromDB(AAAConfigurationContext aaaConfigurationContext) throws DataSourceException, SQLException {
		LogManager.getLogger().info(MODULE, "Reading groovy plugin files.");

		final AAAServerContext serverContext = aaaConfigurationContext.getServerContext();
		String radGroovyHome = radiusGroovyHome(serverContext);

		File directory = new File(radGroovyHome);
		if (directory.exists() == false) {
			directory.mkdirs();
		}

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resulSet = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			AAAServerContext context = (AAAServerContext)this.pluginContext.getServerContext();
			Set<PluginInfo> radGroovyTypePlugin = context.getPluginDetail().getTypeSpecific(PluginType.RADIUS_GROOVY_PLUGIN);
			for (PluginInfo pluginInfo : radGroovyTypePlugin) {
				RadiusGroovyPluginMapping mapping = new RadiusGroovyPluginMapping();
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
						
						mapping.getFilenames().add(fileName);
						
						createPluginInstance(pluginInfo, file);
						
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
	public void reloadFromDB() {

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
		if (((AAAConfigurationContext)getConfigurationContext()).state() == AAAConfigurationState.FALLBACK_CONFIGURATION) {
			readGroovyFilesFromFileSystem();
		}
	}

	@PostReload
	public void postReload() {

	}

	@PostWrite
	public void postWrite() {

	}
	
	private void readGroovyFilesFromFileSystem() {
		AAAConfigurationContext aaaConfigurationContext = ((AAAConfigurationContext)getConfigurationContext());
		AAAServerContext context = aaaConfigurationContext.getServerContext();
		PluginDetail pluginDetails = context.getPluginDetail();
		String radGroovyHome = radiusGroovyHome(context);
		
		for (RadiusGroovyPluginMapping mapping : getMappings()) {
			for (String filename : mapping.getFilenames()) {
				File file = new File(radGroovyHome + File.separator + filename);
				createPluginInstance(pluginDetails.getNameToInfoMap().get(mapping.getPluginName()), file);
			}
		}
		LogManager.getLogger().info(MODULE, "Succesfully read groovy plugin file(s): " + pluginScripts.keySet());
	}

	private String radiusGroovyHome(AAAServerContext context) {
		String radGroovyHome = context.getServerHome() + File.separator + "conf" + 
				File.separator + "db" + File.separator + "scripts" + File.separator + "plugins" + File.separator + "radius";
		return radGroovyHome;
	}

	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> nameToPlugin) {
		for (Entry<String, ScriptPlugin> script : this.pluginScripts.entrySet()) {
			com.elitecore.core.commons.plugins.PluginInfo pluginInfo = new com.elitecore.core.commons.plugins.PluginInfo();
			pluginInfo.setPluginName(script.getKey());
			RadiusGroovyPlugin groovyPlugin = new RadiusGroovyPlugin((RadiusScriptPlugin)script.getValue(), pluginContext, pluginInfo);
			try {
				groovyPlugin.init();
				LogManager.getLogger().info(MODULE, "Successfully initailized Radius Groovy Plugin: " + script.getKey());
				nameToPlugin.put(script.getKey(), groovyPlugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().warn(MODULE, "Plugin with name: " + script.getKey() + " initialization failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
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
		} catch (FileNotFoundException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: File does not exist at " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}catch (IOException ex) {
			LogManager.getLogger().warn(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
		return obj;
	}
}
