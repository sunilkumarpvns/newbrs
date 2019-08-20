package com.elitecore.aaa.radius.service.acct.plugins.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.elitecore.aaa.core.plugins.UniversalPluginConfigurationImpl;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.plugins.conf.impl.BaseUniversalPluginConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.plugins.UniversalAcctPlugin;
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
@XmlRootElement(name = "universal-acct-plugins")
@ConfigurationProperties(moduleName ="UNIVERSAL_ACCT_PLUGIN_CONFIGURABLE",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","radius"}, name = "universal-acct-plugin")
public class UniversalAcctPluginConfigurable extends BaseUniversalPluginConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>> {

	public static final String MODULE = "UNIVERSAL_ACCT_PLUGIN_CONFIGURABLE";

	private List<UniversalPluginConfigurationImpl> data = new ArrayList<UniversalPluginConfigurationImpl>();
	
	public UniversalAcctPluginConfigurable() {
		
	}	
	
	@Reloadable(type = UniversalPluginConfigurationImpl.class)
	@XmlElementWrapper(name = "universal-acct-plugin")
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
			
			Set<PluginInfo> acctTypePlugin = ((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).getPluginDetail().getTypeSpecific(PluginType.UNIVERSAL_ACCT);
			
			List<UniversalPluginConfigurationImpl> tmpData = new ArrayList<UniversalPluginConfigurationImpl>();
			for (PluginInfo pluginInfo : acctTypePlugin) {
				String queryForConfReading = getQueryForConfReading();
				pstmt = connection.prepareStatement(queryForConfReading);
				pstmt.setString(1, pluginInfo.getId());
				resultSet = pstmt.executeQuery();
				while (resultSet.next()) {

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
	
	@DBReload
	public void reloadFromDB() {
		
	}
	
	private String getQueryForConfReading() {
		return "SELECT * FROM TBLMUNIVERSALPLUGIN WHERE PLUGININSTANCEID = ?"; 
	}
	
	@PostRead
	public void postReadProcessing() {
		
		if (Collectionz.isNullOrEmpty(data)) {
			return;
		}
		
		for (UniversalPluginConfigurationImpl configData : data) {
		
			configData.postRead();
			
			if(Collectionz.isNullOrEmpty(configData.getPrePolicyLists()) == false){
				this.getPrePolicyList().addAll(getParsePolicies(configData.getPrePolicyLists()));
			}
			
			if(Collectionz.isNullOrEmpty(configData.getPostPolicyLists()) == false){
				this.getPostPolicyList().addAll(getParsePolicies(configData.getPostPolicyLists()));
			}
		}
	}

	@PostWrite
	public void postWriteProcessing() {
	
	}
	
	@PostReload
	public void postReloadProcessing() {
		postReadProcessing();
	}
	
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println("\n ----Universal Acct Plugin Configuration---- ");
        out.println();
        out.println("----Universal Pre Acct Policy Details----");
        out.println("	--Pre Acct Policy--");
        if(getPrePolicyList() != null && !getPrePolicyList().isEmpty()){
        	for (int i=0; i< getPrePolicyList().size(); i++){
                out.println(getPrePolicyList().get(i) );
        }
        }else { 
        	out.println("No Radius Pre Acct-policy configured");
        }
        
        out.println("----Universal Post Acct Policy Details----");
       
        if(getPostPolicyList() != null && !getPostPolicyList().isEmpty()){
        	for (int i=0; i< getPostPolicyList().size(); i++){
        		out.println("	--Post Acct Policy--");
                out.println(getPostPolicyList().get(i) );
        }
        }else { 
        	out.println("No Radius Post Acct-policy configured");
        }
        out.close();

        return stringBuffer.toString();
	}
	
	@XmlTransient
	@Override
	protected String getModuleName() {
		return MODULE;
	}

	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> nameToPlugin) {
		for (UniversalPluginConfigurationImpl conf : data) {
			UniversalAcctPlugin universalAuthPlugin = new UniversalAcctPlugin(null, conf);
			try {
				universalAuthPlugin.init();
				nameToPlugin.put(conf.getName(), universalAuthPlugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to initialize universal accounting plugin, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}		
		
	}
}