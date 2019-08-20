package com.elitecore.aaa.radius.plugins.quotamgmt.conf.impl;

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
import com.elitecore.aaa.core.plugins.conf.BasePluginConfigurable;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.plugins.quotamgmt.QuotaManagementPlugin;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
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

@XmlType(propOrder = {})
@XmlRootElement(name = "quota-management-plugins")
@ConfigurationProperties(moduleName ="QUOTA_MANAGEMENT_PLUGIN_CONFIGURABLE",synchronizeKey ="QUOTA_MANAGEMENT_PLUGIN", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","quota"},name = "quota-management-plugin")
public class QuotaManagementPluginConfigurable extends BasePluginConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>> {

	private static final String MODULE = "QUOTA_MANAGEMENT_PLUGIN_CONFIGURABLE";
	
	private List<QuotaManagementPluginConfiguration> pluginData;

	public QuotaManagementPluginConfigurable() {
		this.pluginData = new ArrayList<QuotaManagementPluginConfiguration>();
	}
	
	@DBRead
	@DBReload
	public void readConfiguration() throws SQLException, JAXBException {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		JAXBContext context = JAXBContext.newInstance(QuotaManagementPluginConfiguration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			Set<PluginInfo> pluginInfo = ((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).getPluginDetail().getTypeSpecific(PluginType.QUOTA_MANAGEMENT);
			
			List<QuotaManagementPluginConfiguration> tmpPluginData = new ArrayList<QuotaManagementPluginConfiguration>();
			
			for (PluginInfo info : pluginInfo) {

				statement = connection.prepareStatement(getPluginQuery());
				statement.setString(1, info.getId());
				resultSet = statement.executeQuery();

				if (resultSet.next()) {
					byte[] plguinDataBytes = resultSet.getBytes("PLUGINDATA");
					InputStream xmlDataAsAStream = new ByteArrayInputStream(plguinDataBytes);
					QuotaManagementPluginConfiguration configData = (QuotaManagementPluginConfiguration) unmarshaller.unmarshal(xmlDataAsAStream);
					tmpPluginData.add(configData);
				}
			}
			
			this.pluginData = tmpPluginData;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private String getPluginQuery() {
		return "SELECT * FROM TBLMQUOTAMGTPLUGIN WHERE PLUGININSTANCEID = ?";
	}

	@PostRead
	public void postReadProcessing() {
		for (QuotaManagementPluginConfiguration data : this.pluginData) {
			data.postRead();				
		}
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	
	@XmlElement(name = "quota-management-plugin")
	public List<QuotaManagementPluginConfiguration> getPluginData() {
		return pluginData;
	}
	
	public void setPluginData(List<QuotaManagementPluginConfiguration> pluginData) {
		this.pluginData = pluginData;
	}
	
	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> nameToPlugin) {

		for (QuotaManagementPluginConfiguration data: this.pluginData) {
			try {
				QuotaManagementPlugin plugin = new QuotaManagementPlugin(null, data);
				plugin.init();
				nameToPlugin.put(data.getName(), plugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to initialize quota management plugin: " + data.getName());
				LogManager.getLogger().trace(e);
			}
		}
	}
}
