package com.elitecore.aaa.radius.service.auth.plugins.conf.impl;

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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.plugins.AAAUniversalPluginPolicyDetail;
import com.elitecore.aaa.core.plugins.UniversalPluginConfigurationImpl;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.plugins.conf.impl.BaseUniversalPluginConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.plugins.UniversalAuthPlugin;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "universal-auth-plugins")
@ConfigurationProperties(moduleName ="UNIVERSAL_AUTH_PLUGIN_CONFIGURABLE",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","radius"},name = "universal-auth-plugin")
public class UniversalAuthPluginCofigurable extends BaseUniversalPluginConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>> {

	public static final String MODULE = "UNIVERSAL_AUTH_PLUGIN_CONFIGURABLE";
	
	private List<UniversalPluginConfigurationImpl> data = new ArrayList<UniversalPluginConfigurationImpl>();
	
	public UniversalAuthPluginCofigurable() {
		
	}
	
	@Reloadable(type = UniversalPluginConfigurationImpl.class)
	@XmlElementWrapper(name = "universal-auth-plugin")
	@XmlElement(name = "universal-plugin-detail")
	public List<UniversalPluginConfigurationImpl> getData() {
		return data;
	}
	
	public void setData(List<UniversalPluginConfigurationImpl> data) {
		this.data = data;
	}

	@DBRead
	public void readConfiguration() throws SQLException, JAXBException {
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		JAXBContext context = JAXBContext.newInstance(UniversalPluginConfigurationImpl.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			
			Set<PluginInfo> authTypePlugin = ((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).getPluginDetail().getTypeSpecific(PluginType.UNIVERSAL_AUTH);

			List<UniversalPluginConfigurationImpl> tmpData = new ArrayList<UniversalPluginConfigurationImpl>();
			
			for (PluginInfo pluginInfo : authTypePlugin) {
				String queryForConfReading = getQueryForConfReading();
				pstmt = connection.prepareStatement(queryForConfReading);
				pstmt.setString(1, pluginInfo.getId());
				resultSet = pstmt.executeQuery();
				if (resultSet.next()) {
					byte[] pluginConfigurationDataBytes = resultSet.getBytes("PLUGINDATA");
					InputStream xmlDataAsAStream = new ByteArrayInputStream(pluginConfigurationDataBytes);
					UniversalPluginConfigurationImpl configData = (UniversalPluginConfigurationImpl) unMarshaller.unmarshal(xmlDataAsAStream);
					tmpData.add(configData);
				}
			}
			this.data = tmpData;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private String getQueryForConfReading() {
		return "SELECT * FROM TBLMUNIVERSALPLUGIN WHERE PLUGININSTANCEID = ?"; 
	}
	
	@DBReload
	public void reloadFromDB() {
		
	}

	@PostRead
	@PostReload
	public void postProcessing() {

		if (Collectionz.isNullOrEmpty(data)) {
			return;
		}

		for (UniversalPluginConfigurationImpl configData : data) {
			
			configData.postRead();
			
			if(Collectionz.isNullOrEmpty(configData.getPrePolicyLists()) == false){
				List<AAAUniversalPluginPolicyDetail> parsePolicies = getParsePolicies(configData.getPrePolicyLists());
				this.getPrePolicyList().addAll(parsePolicies);
			}
			
			if(Collectionz.isNullOrEmpty(configData.getPostPolicyLists()) == false){
				this.getPostPolicyList().addAll(getParsePolicies(configData.getPostPolicyLists()));
			}
		}
	}

	@PostWrite
	public void postWriteProcessing() {

	}
	
	@XmlTransient
	@Override
	protected String getModuleName() {
		return MODULE;
	}
	
	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> rad) {
		for (UniversalPluginConfigurationImpl conf : data) {
			UniversalAuthPlugin universalAuthPlugin = new UniversalAuthPlugin(null, conf);
			try {
				universalAuthPlugin.init();
				rad.put(conf.getName(), universalAuthPlugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to initialize universal auth plugin, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}