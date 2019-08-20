package com.elitecore.aaa.radius.plugins.userstatistic.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.DatabaseDSConfigurationImpl;
import com.elitecore.aaa.core.plugins.conf.BasePluginConfigurable;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.plugins.userstatistic.UserStatisticPostAuthPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.serverx.ServerContext;

@XmlType(propOrder = {})
@XmlRootElement(name = "user-statistic-post-auth-plugins")
@ConfigurationProperties(moduleName="USER_STATISTIC_CONFIGURABLE", synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system", "schema"} , configDirectories = {"conf", "db", "plugin", "radius"}, name = "user-statistic-post-auth-plugin")
public class UserStatisticPostAuthPluginConfigurable extends BasePluginConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>>{

	
	private static final String MODULE = "USER-STATISTIC-POST-AUTH-PLUGIN-CONFIGURABLE";

	private List<UserStatisticPostAuthPluginConfigurationImpl> pluginConfigurations;
	private PluginContext context;
	
	public UserStatisticPostAuthPluginConfigurable() {
		this.pluginConfigurations = new ArrayList<UserStatisticPostAuthPluginConfigurationImpl>();
	}
	
	@XmlElement(name = "user-statistic-post-auth-plugin")
	public List<UserStatisticPostAuthPluginConfigurationImpl> getPluginConfigurations() {
		return pluginConfigurations;
	}
	
	@DBRead
	public void readConfiguration() throws JAXBException, SQLException {
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		JAXBContext context = JAXBContext.newInstance(UserStatisticPostAuthPluginConfigurationImpl.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			Set<PluginInfo> pluginInfos = ((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).getPluginDetail().getTypeSpecific(PluginType.USER_STATISTICS_POST_AUTH_PLUGIN);
			List<UserStatisticPostAuthPluginConfigurationImpl> tmpData = new ArrayList<UserStatisticPostAuthPluginConfigurationImpl>();
			
			for (PluginInfo pluginInfo : pluginInfos) {
				
				pstmt = connection.prepareStatement(getQueryForConfReading());
				pstmt.setString(1, pluginInfo.getId());
				resultSet=pstmt.executeQuery();
				if (resultSet.next()) {
					
					byte[] pluginDataBytes = resultSet.getBytes("PLUGINDATA");
					InputStream xmlDataAsAStream = new ByteArrayInputStream(pluginDataBytes);
					UserStatisticPostAuthPluginConfigurationImpl configData = (UserStatisticPostAuthPluginConfigurationImpl) unMarshaller.unmarshal(xmlDataAsAStream);
					tmpData.add(configData);
				}
				
			}
			this.pluginConfigurations = tmpData;
		} finally {
			
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@DBReload
	public void reloadFromDB() {
		
	}
	
	@PostRead
	public void postReadProcessing() {
		createContext();
		for (UserStatisticPostAuthPluginConfigurationImpl pluginConf : this.pluginConfigurations) {
			pluginConf.postRead();
		}
	}

	private void createContext() {
		this.context = new PluginContext() {

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

	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	
	private String getQueryForConfReading() {
		return "SELECT * FROM TBLMUSERSTATPOSTAUTHPLUGIN WHERE PLUGININSTANCEID = ?"; 
	}




	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> rad) {
		
		DatabaseDSConfigurationImpl databaseDSConfiguration = getConfigurationContext().get(DatabaseDSConfigurationImpl.class);
		
		for (UserStatisticPostAuthPluginConfigurationImpl conf : this.pluginConfigurations) {
			
			UserStatisticPostAuthPlugin plugin = new UserStatisticPostAuthPlugin(context, databaseDSConfiguration, conf);
			try {
				plugin.init();
				rad.put(conf.getName(), plugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to initialize user statistics post auth plugin , Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
			
		}
	}
}
	
