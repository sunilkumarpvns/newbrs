package com.elitecore.aaa.core.plugins.conf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@ConfigurationProperties(moduleName ="PLUGINS-CONF",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin"},name = "plugins-conf")
@XmlRootElement(name = "plugins-conf")
public class PluginTypeWiseConfigurable extends Configurable {
	
	private PluginDetail pluginDetail;
	
	public PluginTypeWiseConfigurable() {
	}

	@XmlElement(name = "plugin-details")
	public PluginDetail getPluginDetail() {
		return pluginDetail;
	}
	
	public void setPluginDetail(PluginDetail pluginDetail) {
		this.pluginDetail = pluginDetail;
	}

	@DBRead
	public void readFromDB() throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		this.pluginDetail = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getPluginDetail();
		try {
			Set<String> pluginNames = pluginDetail.getPluginNames();
			if (Collectionz.isNullOrEmpty(pluginNames) == false) {
				
				connection = EliteAAADBConnectionManager.getInstance().getConnection();
				String query = generateQuery(pluginNames);
				ps = connection.prepareStatement(query);
				rs = ps.executeQuery();

				while (rs.next()) {
					String name = rs.getString("name");
					String id = rs.getString("id");
					PluginType type = PluginType.from(rs.getInt("type"));
					pluginDetail.set(name, id, type);
				}
			}
		} finally {
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@DBReload
	public void reloadFromDB() {
		
	}
	
	@PostRead
	public void postRead() {
		if (((AAAConfigurationContext) getConfigurationContext()).state() == AAAConfigurationState.FALLBACK_CONFIGURATION) {
			this.pluginDetail.postRead();
			PluginDetail serverPluginDetail = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getPluginDetail();
			for (PluginInfo info : this.pluginDetail.getNameToInfoMap().values()) {
				serverPluginDetail.getNameToInfoMap().get(info.getName()).set(info.getId(), info.getType());
			}
		}
	}
	
	@PostWrite
	public void postWrite() {
		
	}
	
	@PostReload
	public void postReload() {
		
	}
	
	private String generateQuery(Set<String> pluginNames) {
		String values = "";
		
		int i = 0;
		for (String name : pluginNames) {
			i++;
			String tmpName = "'" + name + "'";
			values += tmpName;
			if (i != pluginNames.size()) {
				values +=",";
			}
		}
		return "SELECT PLUGININSTANCEID as id,NAME as name,PLUGINTYPEID as type FROM TBLMPLUGININSTANCEDATA WHERE NAME IN ( "
			+values+") AND STATUS='" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE +"'";
	}
}
