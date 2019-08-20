package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.DiameterTGPPServerConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.diameterapi.diameter.common.data.PeerInfo;

@ConfigurationProperties(moduleName = "TGPP-SERVER-PLCY-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class, reloadWith = DBReader.class)
@XMLProperties(name = "tgpp-server-policies", configDirectories = {"conf", "db", "diameter", "policy"}, schemaDirectories = {"system", "schema"})
@XmlRootElement(name = "tgpp-server-policies")
public class TGPPServerPolicyConfigurable extends Configurable {
	
	private List<TGPPServerPolicyData> policies = new ArrayList<TGPPServerPolicyData>();

	/*
	 * Transient properties
	 */
	private Map<String, DiameterServicePolicyConfiguration> idToPolicyConfigurationMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
	private Set<String> selectedDriverIds = new HashSet<String>();
	private Set<String> requiredPeerNames = new HashSet<String>();
	
	@XmlElement(name = "tgpp-server-policy")
	public List<TGPPServerPolicyData> getPolicies() {
		return policies;
	}

	@XmlTransient
	public Set<String> getSelectedDriverIds() {
		return selectedDriverIds;
	}
	
	@XmlTransient
	public Set<String> getRequiredPeers() {
		return requiredPeerNames;
	}

	
	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		JAXBContext context = JAXBContext.newInstance(TGPPServerPolicyData.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();

		DiameterTGPPServerConfigurable serverConfigurable = getConfigurationContext().get(DiameterTGPPServerConfigurable.class);
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();

			String queryForDiameterServicePolicyConfiguration = getQueryForSelectiveServicePolicies(serverConfigurable.getServicePolicies());
			preparedStatement = connection.prepareStatement(queryForDiameterServicePolicyConfiguration);
			preparedStatement.setString(1, CommonConstants.DATABASE_POLICY_STATUS_ACTIVE);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				byte[] tgppPolicyConfigurationData = resultSet.getBytes("TGPPAAAPOLICYXML");
				InputStream inputStream = new ByteArrayInputStream(tgppPolicyConfigurationData);
				Object servicePolicyData = unMarshaller.unmarshal(inputStream);
				((TGPPServerPolicyData) servicePolicyData).setId(resultSet.getString("TGPPAAAPOLICYID"));
				policies.add((TGPPServerPolicyData) servicePolicyData);
			}

		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@DBReload
	public void reloadFromDB() {
		
	}

	private String getQueryForSelectiveServicePolicies(List<String> servicePolicies) {
		if (servicePolicies==null || !(servicePolicies.size() > 0) || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select * from TBLMTGPPAAASERVICEPOLICY where STATUS=? ORDER BY ORDERNUMBER";
		} else {
			String query= "select * from TBLMTGPPAAASERVICEPOLICY where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";
			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for (int i=0; i<numOfPolicy; i++) {
				caseString = caseString + " WHEN NAME = '" + servicePolicies.get(i) + "' THEN " + i;
			}
			query = query + caseString + " END )";

			return query;
		}
	}

	@PostRead
	public void postRead() {
		for (TGPPServerPolicyData data : policies) {
			data.setConfigurationContext(((AAAConfigurationContext)getConfigurationContext()));
			data.postRead();
			
			/*
			 * Only DiameterPeers are being registered here because the RADIUS ESIs
			 * which might be bound in a RADIUS proxy handler are read earlier only by the class 
			 * RadESIConfigurable. 
			 *
			*/
			
			DiameterPeerGroupConfigurable diameterPeerGroupConfigurable = getConfigurationContext().get(DiameterPeerGroupConfigurable.class);
			for (String peerGroupId : data.getRequiredPeerGroupIds()) {
				PeerGroupData peerGroup = diameterPeerGroupConfigurable.getPeerGroup(peerGroupId);
				if (peerGroup != null) {
					for (PeerInfo peerInfo : peerGroup.getPeers()) {
						requiredPeerNames.add(peerInfo.getPeerName());
					}
				}
			}
			
			idToPolicyConfigurationMap.put(data.getId(), data);
			selectedDriverIds.addAll(data.getRequiredDriversIds());
		}
	}
	
	@PostWrite
	public void postWrite() {
		
	}
	
	@PostReload
	public void postReload() {
		
	}
	
	@Override
	public String toString() {
		return policies.toString();
	}
	
	@XmlTransient
	public Map<String, DiameterServicePolicyConfiguration> getIdToPolicyConfigurationMap() {
		return idToPolicyConfigurationMap;
	}
}
