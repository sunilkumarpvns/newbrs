/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateDiameterpolicyForm.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DiameterPeerProfileForm extends BaseWebForm{
	
	    private String peerProfileId;
	    private String profileName;
	    private String description;
	    private String transportProtocol;
	    private Integer socketReceiveBufferSize= -1;
	    private Integer socketSendBufferSize= -1;
	    private String  tcpNagleAlgorithm = "false";
	    private String redirectHostAvpFormat="DiameterURI";
		private java.lang.Long dwrDuration;
	    private String localIp;
	    private java.lang.Long initConnectionDuration;
	    private java.lang.Long retryCount;
	    private Boolean sessionCleanUpCER = false;
	    private Boolean sessionCleanUpDPR = false;
	    private String cerAvps;
	    private String dprAvps;
	    private String dwrAvps;
	    private String sendDPRCloseEvent;
	    private String action;
	    private List<String> transportProtocolist =  new ArrayList<String>();
	    private String followRedirection;
	    private String hotlinePolicy;
	    private String minTlsVersion;
	    private String maxTlsVersion;
	    private List<String> lstTLSVersion;
	    private List<String> lstConnectionStandard;
		private String serverCertificateId;
		private List<ServerCertificateData> serverCertificateDataList;
		private List<String> lstCipherSuitesList;
		private List<String> cipherSuitList;
		private String[] strCipherSuite;
		private List lstTLSVersionList;
	    private String clientCertificateRequest="true";
	    private String validateCertificateExpiry;
	    private String allowCertificateCA;
	    private String validateCertificateRevocation;
	    private String validateHost;
	    private String securityStandard;
	    private String serverCertificateName;
	    private String[] listCipherSuites;
	    private String cipherSuites;
	    private List<String> remainingCipherList;
	    private String auditUId;
	    private String exclusiveAuthAppIds;
	    private String exclusiveAcctAppIds;
	    private String haIPAddress;
	    private String dhcpIPAddress;
	    
		public String getProfileName() {
			return profileName;
		}
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getTransportProtocol() {
			return transportProtocol;
		}
		public void setTransportProtocol(String transportProtocol) {
			this.transportProtocol = transportProtocol;
		}
		public Integer getSocketReceiveBufferSize() {
			return socketReceiveBufferSize;
		}
		public void setSocketReceiveBufferSize(Integer socketReceiveBufferSize) {
			this.socketReceiveBufferSize = socketReceiveBufferSize;
		}
		public Integer getSocketSendBufferSize() {
			return socketSendBufferSize;
		}
		public void setSocketSendBufferSize(Integer socketSendBufferSize) {
			this.socketSendBufferSize = socketSendBufferSize;
		}
		public String getTcpNagleAlgorithm() {
			return tcpNagleAlgorithm;
		}
		public void setTcpNagleAlgorithm(String tcpNagleAlgorithm) {
			this.tcpNagleAlgorithm = tcpNagleAlgorithm;
		}
		public java.lang.Long getDwrDuration() {
			return dwrDuration;
		}
		public void setDwrDuration(java.lang.Long dwrDuration) {
			this.dwrDuration = dwrDuration;
		}
		public String getLocalIp() {
			return localIp;
		}
		public void setLocalIp(String localIp) {
			this.localIp = localIp;
		}
		public java.lang.Long getInitConnectionDuration() {
			return initConnectionDuration;
		}
		public void setInitConnectionDuration(java.lang.Long initConnectionDuration) {
			this.initConnectionDuration = initConnectionDuration;
		}
		public java.lang.Long getRetryCount() {
			return retryCount;
		}
		public void setRetryCount(java.lang.Long retryCount) {
			this.retryCount = retryCount;
		}
		public Boolean getSessionCleanUpCER() {
			return sessionCleanUpCER;
		}
		public void setSessionCleanUpCER(Boolean sessionCleanUpCER) {
			this.sessionCleanUpCER = sessionCleanUpCER;
		}
		public Boolean getSessionCleanUpDPR() {
			return sessionCleanUpDPR;
		}
		public void setSessionCleanUpDPR(Boolean sessionCleanUpDPR) {
			this.sessionCleanUpDPR = sessionCleanUpDPR;
		}
		public String getCerAvps() {
			return cerAvps;
		}
		public void setCerAvps(String cerAvps) {
			this.cerAvps = cerAvps;
		}
		public String getRedirectHostAvpFormat() {
			return redirectHostAvpFormat;
		}
		public void setRedirectHostAvpFormat(String redirectHostAvpFormat) {
			this.redirectHostAvpFormat = redirectHostAvpFormat;
		}
		public String getDprAvps() {
			return dprAvps;
		}
		public void setDprAvps(String dprAvps) {
			this.dprAvps = dprAvps;
		}
		public String getDwrAvps() {
			return dwrAvps;
		}
		public void setDwrAvps(String dwrAvps) {
			this.dwrAvps = dwrAvps;
		}
		public String getSendDPRCloseEvent() {
			return sendDPRCloseEvent;
		}
		public void setSendDPRCloseEvent(String sendDPRCloseEvent) {
			this.sendDPRCloseEvent = sendDPRCloseEvent;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public List<String> getTransportProtocolist() {
			return transportProtocolist;
		}
		public void setTransportProtocolist(List<String> transportProtocolist) {
			this.transportProtocolist = transportProtocolist;
		}
		public String getPeerProfileId() {
			return peerProfileId;
		}
		public void setPeerProfileId(String peerProfileId) {
			this.peerProfileId = peerProfileId;
		}
		public String getFollowRedirection() {
			return followRedirection;
		}
		public void setFollowRedirection(String followRedirection) {
			this.followRedirection = followRedirection;
		}
		public String getHotlinePolicy() {
			return hotlinePolicy;
		}
		public void setHotlinePolicy(String hotlinePolicy) {
			this.hotlinePolicy = hotlinePolicy;
		}
		public String getServerCertificateId() {
			return serverCertificateId;
		}
		public void setServerCertificateId(String serverCertificateId) {
			this.serverCertificateId = serverCertificateId;
		}
		public String getClientCertificateRequest() {
			return clientCertificateRequest;
		}
		public void setClientCertificateRequest(
				String clientCertificateRequest) {
			this.clientCertificateRequest = clientCertificateRequest;
		}
		public String getValidateCertificateExpiry() {
			return validateCertificateExpiry;
		}
		public void setValidateCertificateExpiry(String validateCertificateExpiry) {
			this.validateCertificateExpiry = validateCertificateExpiry;
		}
		public String getAllowCertificateCA() {
			return allowCertificateCA;
		}
		public void setAllowCertificateCA(String allowCertificateCA) {
			this.allowCertificateCA = allowCertificateCA;
		}
		public String getValidateHost() {
			return validateHost;
		}
		public void setValidateHost(String validateHost) {
			this.validateHost = validateHost;
		}
		public String getValidateCertificateRevocation() {
			return validateCertificateRevocation;
		}
		public void setValidateCertificateRevocation(String validateCertificateRevocation) {
			this.validateCertificateRevocation = validateCertificateRevocation;
		}
		
		public List<ServerCertificateData> getServerCertificateDataList() {
			return serverCertificateDataList;
		}
		public void setServerCertificateDataList(
				List<ServerCertificateData> serverCertificateDataList) {
			this.serverCertificateDataList = serverCertificateDataList;
		}

		public List getLstTLSVersionList() {
			return lstTLSVersionList;
		}

		public void setLstTLSVersionList(List lstTLSVersionList) {
			this.lstTLSVersionList = lstTLSVersionList;
		}
		public List<String> getLstTLSVersion() {
			return lstTLSVersion;
		}
		public void setLstTLSVersion(List<String> lstTLSVersion) {
			this.lstTLSVersion = lstTLSVersion;
		}
		public List<String> getLstConnectionStandard() {
			return lstConnectionStandard;
		}
		public void setLstConnectionStandard(List<String> lstConnectionStandard) {
			this.lstConnectionStandard = lstConnectionStandard;
		}
		public String getSecurityStandard() {
			return securityStandard;
		}
		public void setSecurityStandard(String securityStandard) {
			this.securityStandard = securityStandard;
		}
		
		public String getServerCertificateName() {
			return serverCertificateName;
		}
		public void setServerCertificateName(String serverCertificateName) {
			this.serverCertificateName = serverCertificateName;
		}
	
		public String[] getStrCipherSuite() {
			return strCipherSuite;
		}
		public void setStrCipherSuite(String[] strCipherSuite) {
			this.strCipherSuite = strCipherSuite;
		}
		
		public List<String> getLstCipherSuitesList() {
			return lstCipherSuitesList;
		}
		public void setLstCipherSuitesList(List<String> lstCipherSuitesList) {
			this.lstCipherSuitesList = lstCipherSuitesList;
		}
		public String[] getListCipherSuites() {
			return listCipherSuites;
		}
		public void setListCipherSuites(String[] listCipherSuites) {
			this.listCipherSuites = listCipherSuites;
		}
		public String getCipherSuites() {
			return cipherSuites;
		}
		public void setCipherSuites(String cipherSuites) {
			this.cipherSuites = cipherSuites;
		}
		public String getMinTlsVersion() {
			return minTlsVersion;
		}
		public void setMinTlsVersion(String minTlsVersion) {
			this.minTlsVersion = minTlsVersion;
		}
		public String getMaxTlsVersion() {
			return maxTlsVersion;
		}
		public void setMaxTlsVersion(String maxTlsVersion) {
			this.maxTlsVersion = maxTlsVersion;
		}
		public List<String> getRemainingCipherList() {
			return remainingCipherList;
		}
		public void setRemainingCipherList(List<String> remainingCipherList) {
			this.remainingCipherList = remainingCipherList;
		}
		public List<String> getCipherSuitList() {
			return cipherSuitList;
		}
		public void setCipherSuitList(List<String> cipherSuitList) {
			this.cipherSuitList = cipherSuitList;
		}
		public String getAuditUId() {
			return auditUId;
		}
		public void setAuditUId(String auditUId) {
			this.auditUId = auditUId;
		}
		public String getExclusiveAuthAppIds() {
			return exclusiveAuthAppIds;
		}
		public void setExclusiveAuthAppIds(String exclusiveAuthAppIds) {
			this.exclusiveAuthAppIds = exclusiveAuthAppIds;
		}
		public String getExclusiveAcctAppIds() {
			return exclusiveAcctAppIds;
		}
		public void setExclusiveAcctAppIds(String exclusiveAcctAppIds) {
			this.exclusiveAcctAppIds = exclusiveAcctAppIds;
		}
		public String getHaIPAddress() {
			return haIPAddress;
		}
		public void setHaIPAddress(String haIPAddress) {
			this.haIPAddress = haIPAddress;
		}
		public String getDhcpIPAddress() {
			return dhcpIPAddress;
		}
		public void setDhcpIPAddress(String dhcpIPAddress) {
			this.dhcpIPAddress = dhcpIPAddress;
		}
}
