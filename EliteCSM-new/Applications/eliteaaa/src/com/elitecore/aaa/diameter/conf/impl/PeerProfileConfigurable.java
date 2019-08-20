package com.elitecore.aaa.diameter.conf.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.util.LogicalNameParser;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.diameter.common.data.PeerData.DuplicateConnectionPolicyType;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public abstract class PeerProfileConfigurable extends Configurable {

	/*
	 * DB field constants
	 */
	private static final String PROFILE_ID = "PEERPROFILEID";
	private static final String PROFILE_NAME = "PROFILENAME";
	private static final String CER_AVPS = "CERAVPS";
	private static final String DPR_AVPS = "DPRAVPS";
	private static final String DWR_AVPS = "DWRAVPS";
	private static final String SESSION_CLEANUP_ON_CER = "SESSIONCLEANUPONCER";
	private static final String SESSION_CLEANUP_ON_DPR = "SESSIONCLEANUPONDPR";
	private static final String DWR_DURATION = "DWRDURATION";
	private static final String INIT_CONNECTION_DURATION = "INITCONNECTIONDURATION";
	private static final String SEND_DPR_CLOSE_EVENT = "SENDDPRCLOSEEVENT";
	private static final String SOCKET_RECEIVE_BUFFER_SIZE = "SOCKETRECEIVEBUFFERSIZE";
	private static final String SOCKET_SEND_BUFFER_SIZE = "SOCKETSENDBUFFERSIZE";
	private static final String TCP_NAGLE_ALGORITHM = "TCPNAGLEALGORITHM";
	private static final String TRANSPORT_PROTOCOL = "TRANSPORTPROTOCOL";
	private static final String MAXTLSVERSION = "MAXTLSVERSION";
	private static final String MINTLSVERSION = "MINTLSVERSION";
	private static final String SERVERCERTIFICATEID = "SERVERCERTIFICATEID";
	private static final String VALIDATECERTIFICATEEXPIRY = "VALIDATECERTIFICATEEXPIRY";
	private static final String ALLOWCERTIFICATECA = "ALLOWCERTIFICATECA";
	private static final String VALIDATECERTIFICATEREVOCATION = "VALIDATECERTIFICATEREVOCATION";
	private static final String CLIENTCERTIFICATEREQUEST = "CLIENTCERTIFICATEREQUEST";
	private static final String VALIDATEHOST = "VALIDATEHOST";
	private static final String CIPHERSUITELIST = "CIPHERSUITELIST";
	private static final String REDIRECT_HOST_AVP_FORMAT = "REDIRECTHOSTAVPFORMAT";
	private static final String FOLLOW_REDIRECTION = "FOLLOWREDIRECTION";
	private static final String SECURITY_STANDARD = "SECURITYSTANDARD";
	private static final String EXCLUSIVE_AUTH_APPIDS = "EXCLUSIVEAUTHAPPIDS";
	private static final String EXCLUSIVE_ACCT_APPIDS = "EXCLUSIVEACCTAPPIDS";
	private static final String HOTLINE_POLICY = "HOTLINEPOLICY";
	/*
	 * End of DB field constants
	 */
	
	@XmlTransient
	protected List<PeerProfileDetail> profiles;

	public PeerProfileConfigurable() {
		this.profiles = new ArrayList<PeerProfileDetail>();
	}

	@DBRead
	@DBReload
	public void readPeerProfileConfiguration() throws Exception {
		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		try{						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement("SELECT * FROM TBLMPEERPROFILE");
			if(preparedStatement == null){
				throw new SQLException("Prepared Statement is null, while fetching DiameterPeers");
			}
			
			List<PeerProfileDetail> tempProfileList = new ArrayList<PeerProfileDetail>();
			
			PeerProfileDetail peerProfileDetail;
			resultSet = preparedStatement.executeQuery();			
			while(resultSet.next()){
				peerProfileDetail = readPeerProfileDetails(resultSet);
				tempProfileList.add(peerProfileDetail);
			}
			this.profiles = tempProfileList;
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(getModule(), toString());
	}
	
	
	private PeerProfileDetail readPeerProfileDetails(ResultSet resultSet) throws SQLException, IOException{
		PeerProfileDetail peerProfileDetail = new PeerProfileDetail();
		
		peerProfileDetail.setId(resultSet.getString(PROFILE_ID));
		if(DBUtility.isValueAvailable(resultSet, HOTLINE_POLICY)) {
			peerProfileDetail.setHotlinePolicy(resultSet.getString(HOTLINE_POLICY).trim());
		}
		String profileName = "";
		if(resultSet.getString(PROFILE_NAME)!=null){
			profileName = resultSet.getString(PROFILE_NAME);
		}
		peerProfileDetail.setName(profileName);
		
		if(resultSet.getString(CER_AVPS)!=null && resultSet.getString(CER_AVPS).trim().length()>0){
			peerProfileDetail.setStrCERAvps(resultSet.getString(CER_AVPS));
		}
		
		if(resultSet.getString(DPR_AVPS)!=null && resultSet.getString(DPR_AVPS).trim().length()>0){
			peerProfileDetail.setStrDPRAvps(resultSet.getString(DPR_AVPS));
		}
		
		if(resultSet.getString(DWR_AVPS)!=null && resultSet.getString(DWR_AVPS).trim().length()>0){
			peerProfileDetail.setStrDWRAvps(resultSet.getString(DWR_AVPS));
		}
		
		//Exclusive Applications Ids
		String exclusiveAuthAppIDs = resultSet.getString(EXCLUSIVE_AUTH_APPIDS);
		if(Strings.isNullOrBlank(exclusiveAuthAppIDs) == false){
			LogicalNameParser parser = new LogicalNameParser();
			parser.parse(exclusiveAuthAppIDs);
			String appIDs = Strings.join(",", parser.getValueToLogicalName().keySet());
			peerProfileDetail.setStrExclusiveAuthAppIds(appIDs);
		}
		
		String exclusiveAcctAppIDs = resultSet.getString(EXCLUSIVE_ACCT_APPIDS);
		if(Strings.isNullOrBlank(exclusiveAcctAppIDs) == false){
			LogicalNameParser parser = new LogicalNameParser();
			parser.parse(exclusiveAcctAppIDs);
			String appIDs = Strings.join(",", parser.getValueToLogicalName().keySet());
			peerProfileDetail.setStrExclusiveAcctAppIds(appIDs);
		}
		
		peerProfileDetail.setIsSessionCleanupOnCER(ConfigurationUtil.stringToBoolean(resultSet.getString(SESSION_CLEANUP_ON_CER),peerProfileDetail.getIsSessionCleanupOnCER()));
		peerProfileDetail.setIsSessionCleanupOnDPR(ConfigurationUtil.stringToBoolean(resultSet.getString(SESSION_CLEANUP_ON_DPR),peerProfileDetail.getIsSessionCleanupOnDPR()));
		
		String redirectHostAVPFormat = resultSet.getString(REDIRECT_HOST_AVP_FORMAT); 
		if( redirectHostAVPFormat == null || redirectHostAVPFormat.trim().length() == 0){
			redirectHostAVPFormat = RedirectHostAVPFormat.DIAMETERURI.getStrFormat();
			LogManager.getLogger().warn(getModule(), "Redirection Host AVP format is not defined for Peer Profile ,"+profileName+", Using Default Format: " 
					+ redirectHostAVPFormat);
		}
		peerProfileDetail.setRedirectHostAVPFormat(redirectHostAVPFormat);

		peerProfileDetail.setFollowRedirection(ConfigurationUtil.stringToBoolean(resultSet.getString(FOLLOW_REDIRECTION), peerProfileDetail.getFollowRedirection()));

		PeerConnectionParameters peerConnectionParameters  = peerProfileDetail.getConnectionParameters();
		
		peerConnectionParameters.setDwrDuration(resultSet.getInt(DWR_DURATION));
		peerConnectionParameters.setInitConnectionDuration(resultSet.getInt(INIT_CONNECTION_DURATION));
		peerConnectionParameters.setIsSendDPREvent(ConfigurationUtil.stringToBoolean(resultSet.getString(SEND_DPR_CLOSE_EVENT),peerConnectionParameters.getIsSendDPREvent()));
		peerConnectionParameters.setSocketReceiveBuffetSize(Numbers.parseInt(resultSet.getString(SOCKET_RECEIVE_BUFFER_SIZE), peerConnectionParameters.getSocketReceiveBuffetSize()));
		peerConnectionParameters.setSocketSendBuffetSize(Numbers.parseInt(resultSet.getString(SOCKET_SEND_BUFFER_SIZE), peerConnectionParameters.getSocketSendBuffetSize()));
		peerConnectionParameters.setIsTCPNagleAlgoEnabled(ConfigurationUtil.stringToBoolean(resultSet.getString(TCP_NAGLE_ALGORITHM), peerConnectionParameters.getIsTCPNagleAlgoEnabled()));
	
		SecurityStandard standard = SecurityStandard.fromSecurityStandardVal(resultSet.getString(SECURITY_STANDARD));
		if (standard == null) {
			standard = SecurityStandard.NONE;
			LogManager.getLogger().warn(getModule(), "Security standard: " + resultSet.getString(SECURITY_STANDARD) + " is invalid for "
					+ "Peer Profile ,"+profileName+", Using Default security standard: " + standard);
		}
		peerConnectionParameters.setSecurityStandard(standard);
		//adding securing parameters
		PeerSecurityParameters peerSecurityParameters = new PeerSecurityParameters();
		peerSecurityParameters.setMaxTlsVersion(TLSVersion.fromVersion(resultSet.getString(MAXTLSVERSION)));
		if(resultSet.getString(TRANSPORT_PROTOCOL)!= null && resultSet.getString(TRANSPORT_PROTOCOL).length() > 0 ){
			peerConnectionParameters.setTransportProtocol(TransportProtocols.fromProtocolTypeString(resultSet.getString(TRANSPORT_PROTOCOL)));
		}else{
			LogManager.getLogger().debug(getModule(), "Connection Menthod is not Defined for Profile ,"+profileName+",while fetching DiameterPeerProfile Details.");
		}
		
		peerSecurityParameters.setMinTlsVersion(TLSVersion.fromVersion(resultSet.getString(MINTLSVERSION)));
		peerSecurityParameters.setServerCertificateId(resultSet.getString(SERVERCERTIFICATEID));
		peerSecurityParameters.setValidateCertificateExpiry(ConfigurationUtil.stringToBoolean(resultSet.getString(VALIDATECERTIFICATEEXPIRY)
				,peerSecurityParameters.getValidateCertificateExpiry()));
		peerSecurityParameters.setValidateCertificateCA(ConfigurationUtil.stringToBoolean(resultSet.getString(ALLOWCERTIFICATECA)
				,peerSecurityParameters.getValidateCertificateCA()));
		
		peerSecurityParameters.setValidateCertificateRevocation(ConfigurationUtil.stringToBoolean(resultSet.getString(VALIDATECERTIFICATEREVOCATION)
				,peerSecurityParameters.getValidateCertificateRevocation()));
		
		peerSecurityParameters.setClientCertificateRequest(ConfigurationUtil.stringToBoolean(resultSet.getString(CLIENTCERTIFICATEREQUEST)
				,peerSecurityParameters.getClientCertificateRequest()));
		
		peerSecurityParameters.setValidateHost(ConfigurationUtil.stringToBoolean(resultSet.getString(VALIDATEHOST)
				,peerSecurityParameters.getClientCertificateRequest()));
		
		
		//FIXME NEED TO MODIFY WHEN TIMEOUT FIELD WILL BE ADDED IN DIAMETER PEER
		peerSecurityParameters.setHandShakeTimeout(peerConnectionParameters.getTimeout());
		
		String cipherSuiteStr = resultSet.getString(CIPHERSUITELIST);
		
		peerSecurityParameters.setEnabledCiphersuites(cipherSuiteStr);
		
		peerProfileDetail.setSecurityParameters(peerSecurityParameters);
		
		if (DBUtility.isValueAvailable(resultSet, "HAIPADDRESS")) {
			peerProfileDetail.setHaIpAddress(resultSet.getString("HAIPADDRESS"));
		}
		
		if (DBUtility.isValueAvailable(resultSet, "DHCPIPADDRESS")) {
			peerProfileDetail.setDhcpIpAddress(resultSet.getString("DHCPIPADDRESS"));
		}
		
		return peerProfileDetail;
	}
	

	public void setProfiles(List<PeerProfileDetail> profiles) {
		this.profiles = profiles;
	}
	
	@XmlElement(name="peer-profile")
	public final List<PeerProfileDetail> getProfiles() {
		return profiles;
	}

	@PostRead
	public void postReadProcessing() {
		for (PeerProfileDetail peerProfileDetail : profiles) {
			postReadProcessingForDuplicateConnectionPolicy(peerProfileDetail);
		}
	}
		
	private void postReadProcessingForDuplicateConnectionPolicy(PeerProfileDetail peerProfileDetail) {
		String policy = System.getProperty(peerProfileDetail.getName() + "." + "duplicateConnectionPolicy");
		if (Strings.isNullOrBlank(policy) == false) {
			if (AAAServerConstants.DuplicatePeerConnectionPolicyConstants.DISCARD_OLD.equalsIgnoreCase(policy.trim())) {
				peerProfileDetail.setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType.DISCARD_OLD);
				LogManager.getLogger().info(getModule(), "Found duplicate connection policy: " + DuplicateConnectionPolicyType.DISCARD_OLD 
						+  " attached with peer profile: " + peerProfileDetail.getName());
			} else if (AAAServerConstants.DuplicatePeerConnectionPolicyConstants.DEFAULT.equalsIgnoreCase(policy.trim())) {
				peerProfileDetail.setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType.DEFAULT);
				LogManager.getLogger().info(getModule(), "Found duplicate connection policy: " + DuplicateConnectionPolicyType.DEFAULT 
						+  " attached with peer profile: " + peerProfileDetail.getName());
			} else {
				peerProfileDetail.setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType.DEFAULT);
				LogManager.getLogger().warn(getModule(), "Found unknown duplicate connection policy: " + policy 
						+  " attached with peer profile: " + peerProfileDetail.getName() + ", will use default value.");
	}
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		for (PeerProfileDetail peerProfileDetail : profiles) {
			postReadProcessingForDuplicateConnectionPolicy(peerProfileDetail);
	}
	}
	
	protected abstract String getModule();
}
