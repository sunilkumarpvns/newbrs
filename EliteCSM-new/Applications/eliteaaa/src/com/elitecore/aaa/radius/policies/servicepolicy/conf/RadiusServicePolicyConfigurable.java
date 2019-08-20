package com.elitecore.aaa.radius.policies.servicepolicy.conf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.conf.impl.RadAcctServiceConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadAuthServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServicePolicyFlow;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;

@ConfigurationProperties(moduleName = "RADIUS-SERVICE-POLICY-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(configDirectories = {"conf","db","services","servicepolicy"}, name = "radius-service-policies", schemaDirectories = {"system","schema"})
@XmlRootElement(name = "radius-service-policies")
public class RadiusServicePolicyConfigurable extends Configurable {

	private List<RadiusServicePolicyData> authenticationPoliciesData = new ArrayList<RadiusServicePolicyData>();
	private List<RadiusServicePolicyData> accountingPoliciesData = new ArrayList<RadiusServicePolicyData>();

	/* Transient Fields */
	private List<RadiusServicePolicyData> radiusPoliciesData = new ArrayList<RadiusServicePolicyData>();
	private Set<String> selectedDriverIds = new HashSet<String>();
	
	@XmlTransient
	public List<RadiusServicePolicyData> getRadiusPoliciesData() {
		return radiusPoliciesData;
	}
	
	@XmlElementWrapper(name = "authentication-policies")
	@XmlElement(name = "radius-policy")
	public List<RadiusServicePolicyData> getAuthenticationFlowPolicies() {
		return authenticationPoliciesData;
	}
	
	@XmlElementWrapper(name = "accounting-policies")
	@XmlElement(name = "radius-policy")
	public List<RadiusServicePolicyData> getAccountingFlowPolicies() {
		return accountingPoliciesData;
	}
	
	@DBRead
	public void readConfiguration() throws Exception {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();

		RadAuthServiceConfigurable authServiceConfigurable = getConfigurationContext().get(RadAuthServiceConfigurable.class);
		if (authServiceConfigurable != null) {
			try {
				connection = EliteAAADBConnectionManager.getInstance().getConnection();
				
				String queryForRadiusServicePolicyConfiguration = getQueryForSelectiveServicePolicies(authServiceConfigurable.getServicePolicies());
				preparedStatement = connection.prepareStatement(queryForRadiusServicePolicyConfiguration);
				preparedStatement.setString(1, CommonConstants.DATABASE_POLICY_STATUS_ACTIVE);
				resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					byte[] servicePolicyBytes = resultSet.getBytes("RADIUSPOLICYXML");
					InputStream inputStream = new ByteArrayInputStream(servicePolicyBytes);
					Object servicePolicyData = unMarshaller.unmarshal(inputStream);
					authenticationPoliciesData.add((RadiusServicePolicyData) servicePolicyData);
				}

			} finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
		
		RadAcctServiceConfigurable acctServiceConfigurable = getConfigurationContext().get(RadAcctServiceConfigurable.class);
		if (acctServiceConfigurable != null) {
			try {
				connection = EliteAAADBConnectionManager.getInstance().getConnection();
				
				String queryForRadiusServicePolicyConfiguration = getQueryForSelectiveServicePolicies(acctServiceConfigurable.getServicePolicies());
				preparedStatement = connection.prepareStatement(queryForRadiusServicePolicyConfiguration);
				preparedStatement.setString(1, CommonConstants.DATABASE_POLICY_STATUS_ACTIVE);
				resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					byte[] servicePolicyBytes = resultSet.getBytes("RADIUSPOLICYXML");
					InputStream inputStream = new ByteArrayInputStream(servicePolicyBytes);
					Object servicePolicyData = unMarshaller.unmarshal(inputStream);
					accountingPoliciesData.add((RadiusServicePolicyData) servicePolicyData);
				}

			} finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
		
	}
	
	private String getQueryForSelectiveServicePolicies(List<String> servicePolicies) {
		if (servicePolicies==null || !(servicePolicies.size() > 0) || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select * from TBLMRADIUSSERVICEPOLICY where STATUS=? ORDER BY ORDERNUMBER";
		} else {
			String query= "select * from TBLMRADIUSSERVICEPOLICY where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";
			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for (int i=0; i<numOfPolicy; i++) {
				caseString = caseString + " WHEN NAME = '" + servicePolicies.get(i) + "' THEN " + i;
			}
			query = query + caseString + " END )";

			return query;
		}
	}

	@DBReload
	public void reloadFromDB() {
		
	}
	
	@PostRead
	public void postReadProcessing() {
		for (RadiusServicePolicyData servicePolicyData : authenticationPoliciesData) {
			if (servicePolicyData.getSupportedMessages().isAuthenticationMessageEnabled()) {
				servicePolicyData.setServerContext((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext());
				servicePolicyData.postRead();
				radiusPoliciesData.add(servicePolicyData);
				selectedDriverIds.addAll(servicePolicyData.getRequiredDriverIds());

				registerAuthPlugins(servicePolicyData);
			}
		}
		
		for (RadiusServicePolicyData servicePolicyData : accountingPoliciesData) {
			if (servicePolicyData.getSupportedMessages().isAccountingMessageEnabled()) {
				servicePolicyData.setServerContext(((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()));
				servicePolicyData.postRead();
				radiusPoliciesData.add(servicePolicyData);
				selectedDriverIds.addAll(servicePolicyData.getRequiredDriverIds());

				registerAcctPlugins(servicePolicyData);
			}
		}
	}
	
	private void registerAuthPlugins(RadiusServicePolicyData data) {
		
		String policyName = data.getName();
		
		List<PluginEntryDetail> prePluginData = data.getAuthenticationPolicyData().getPrePluginDataList();
		setPluginsCallerId(policyName, prePluginData, PluginMode.PRE, ServiceTypeConstants.RAD_AUTH, ServicePolicyFlow.AUTH_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(prePluginData);
		
		List<PluginEntryDetail> postPluginData = data.getAuthenticationPolicyData().getPostPluginDataList();
		setPluginsCallerId(policyName, postPluginData, PluginMode.POST, ServiceTypeConstants.RAD_AUTH, ServicePolicyFlow.AUTH_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(postPluginData);		
	}
	
	private void registerAcctPlugins(RadiusServicePolicyData data) {
		
		String policyName = data.getName();
		
		List<PluginEntryDetail> prePluginData = data.getAccountingPolicyData().getPrePluginDataList();
		setPluginsCallerId(policyName, prePluginData, PluginMode.PRE, ServiceTypeConstants.RAD_ACCT, ServicePolicyFlow.ACCT_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(prePluginData);
		
		List<PluginEntryDetail> postPluginData = data.getAccountingPolicyData().getPostPluginDataList();
		setPluginsCallerId(policyName, postPluginData, PluginMode.POST, ServiceTypeConstants.RAD_ACCT, ServicePolicyFlow.ACCT_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(postPluginData);		
	}
	
	private void setPluginsCallerId(String policyName, List<PluginEntryDetail> plugins, PluginMode mode, ServiceTypeConstants serviceType
			, ServicePolicyFlow flow) {
		
		if (Collectionz.isNullOrEmpty(plugins)) {
			return;
		}
		
		for (int i = 0; i < plugins.size(); i++) {
			PluginEntryDetail data = plugins.get(i);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(serviceType, mode, i, data.getPluginName())
					.setServicePolicyName(policyName).setServicePolicyFlow(flow).getId();
			data.setCallerId(key);
		}
	}
	
	@PostReload 
	public void postReloadProcessing() {
		
	}
	
	@PostWrite
	public void postWriteProcessing() {
		
	}
	
	public Set<String> getSelectedDriverIds() {
		return selectedDriverIds;
	}
}
