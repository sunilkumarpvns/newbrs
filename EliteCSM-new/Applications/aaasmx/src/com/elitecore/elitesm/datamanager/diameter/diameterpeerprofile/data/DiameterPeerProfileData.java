/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */
package com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.DiameterPeerProfilesConstant;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.IntegerValidatorAdaptor;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterpeerprofiles.FollowRedirectionAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPTLSCipherSuitesAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.sf.json.JSONObject;

@ValidObject
@XmlRootElement(name = "diameter-peer-profiles")
@XmlType(propOrder = { "profileName", "description", "exclusiveAuthAppIds", "exclusiveAcctAppIds", "cerAvps", "dprAvps", "dwrAvps", "sessionCleanUpCER", "sessionCleanUpDPR", "redirectHostAvpFormat", "followRedirection", "hotlinePolicy", "transportProtocol", "securityStandard", "socketSendBufferSize", "socketReceiveBufferSize", "tcpNagleAlgorithm", "dwrDuration", "initConnectionDuration", "retryCount", "sendDPRCloseEvent", "minTlsVersion", "maxTlsVersion", "serverCertificateName", "clientCertificateRequest", "ciphersuiteList", "certificateValidationCheckBox", "haIPAddress", "dhcpIPAddress" })
public class DiameterPeerProfileData extends BaseData implements Differentiable, Validator {

	private String peerProfileId;

	@Expose
	@SerializedName("Profile Name")
	@NotEmpty(message = "Profile Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 64, message = "Length of Profile Name must not more than 64.")
	private String profileName;

	@Expose
	@SerializedName("Description")
	@Length(max = 255, message = "Length of Description must not more than 255.")
	private String description;

	@Expose
	@SerializedName("Exclusive Auth Application Ids")
	@Length(max = 255, message = "Length of Exclusive Auth Application Ids must not more than 255.")
	private String exclusiveAuthAppIds;

	@Expose
	@SerializedName("Exclusive Acct Application Ids")
	@Length(max = 255, message = "Length of Exclusive Acct Application Ids must not more than 255.")
	private String exclusiveAcctAppIds;

	@Expose
	@SerializedName("CER AVPs")
	@Length(max = 2000, message = "Length of CER AVPs must not more than 2000.")
	private String cerAvps;

	@Expose
	@SerializedName("DPR AVPs")
	@Length(max = 2000, message = "Length of DPR AVPs must not more than 2000.")
	private String dprAvps;

	@Expose
	@SerializedName("DWR AVPs")
	@Length(max = 2000, message = "Length of DWR AVPs must not more than 2000.")
	private String dwrAvps;

	@Expose
	@SerializedName("Redirect-Host AVP Format")
	@Pattern(regexp = "IP|Host Identity|DiameterURI|NONE", message = "Invalid value of Redirect-Host AVP Format.")
	private String redirectHostAvpFormat;

	@Expose
	@SerializedName("Follow Redirection")
	@Pattern(regexp = "true|false", message = "Invalid value of Follow Redirection. Value could be 'Enabled' or 'Disabled'.")
	private String followRedirection;

	@Expose
	@SerializedName("Session Clean Up On CER")
	@Pattern(regexp = "true|false", message = "Invalid value of Session Clean Up On CER. Value could be 'true' or 'false'.")
	private String sessionCleanUpCER;

	@Expose
	@SerializedName("Session Clean Up On DPR")
	@Pattern(regexp = "true|false", message = "Invalid value of Session Clean Up On DPR. Value could be 'true' or 'false'.")
	private String sessionCleanUpDPR;

	@Expose
	@SerializedName("Hotline Policy")
	@Length(max = 255, message = "Length of Hotline Policy must not more than 255.")
	private String hotlinePolicy;

	private String transportProtocol;

	private Integer socketReceiveBufferSize;

	private Integer socketSendBufferSize;

	@Pattern(regexp = "true|false", message = "Invalid value of TCP Nagle Algorithm. Value could be 'true' or 'false'.")
	private String tcpNagleAlgorithm;

	private Long dwrDuration;
	private String localIp;
	private Long initConnectionDuration;
	private Long retryCount;

	@Pattern(regexp = "true|false", message = "Invalid value of DPR on CER Timeout. Value could be 'true' or 'false'.")
	private String sendDPRCloseEvent;

	private String createdByStaffId;
	private Timestamp createDate;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private String ciphersuiteList;
	
	@Contains(allowedValues = { EAPConfigConstant.TLSv_1, EAPConfigConstant.TLSv_1_1, EAPConfigConstant.TLSv_1_2 }, nullMessage = "Minimum TLS Version must be specified", invalidMessage = "Enter valid Minimum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	private String minTlsVersion;
	
	@Contains(allowedValues = { EAPConfigConstant.TLSv_1, EAPConfigConstant.TLSv_1_1, EAPConfigConstant.TLSv_1_2 }, nullMessage = "Maximum TLS Version must be specified", invalidMessage = "Enter valid Maximum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	private String maxTlsVersion;
	
	private String serverCertificateId;

	@Pattern(regexp = "true|false", message = "Invalid value of Client Certificate Request. Value could be 'true' or 'false'.")
	private String clientCertificateRequest;

	private String validateCertificateExpiry;
	private String allowCertificateCA;
	private String validateCertificateRevocation;
	private String validateHost;
	private String securityStandard;
	private String serverCertificateName;
	private String auditUId;

	@Length(max = 100, message = "Length of HA IP Address must not more than 100.")
	private String haIPAddress;

	@Length(max = 100, message = "Length of DHCP IP Address must not more than 100.")
	private String dhcpIPAddress;

	private CertificateValidationCheckBox certificateValidationCheckBox;
	
	public DiameterPeerProfileData() {
		description = RestUtitlity.getDefaultDescription();
		sessionCleanUpCER = DiameterPeerProfilesConstant.SESSION_CLEAN_UP_CER;
		sessionCleanUpDPR = DiameterPeerProfilesConstant.SESSION_CLEAN_UP_DPR;
		redirectHostAvpFormat = DiameterPeerProfilesConstant.REDIRECT_HOST_AVP_FORMAT;
		socketReceiveBufferSize = DiameterPeerProfilesConstant.SOCKET_RECEIVE_BUFFER_SIZE;
		socketSendBufferSize = DiameterPeerProfilesConstant.SOCKET_SEND_BUFFER_SIZE;
		followRedirection = DiameterPeerProfilesConstant.FOLLOW_REDIRECTION;
		transportProtocol = DiameterPeerProfilesConstant.TRANSPORT_PROTOCOL;
		securityStandard = DiameterPeerProfilesConstant.SECURITY_STANDARD;
		tcpNagleAlgorithm = DiameterPeerProfilesConstant.TCP_NAGLE_ALGORITHM;
		dwrDuration = DiameterPeerProfilesConstant.DWR_DURATION;
		initConnectionDuration = DiameterPeerProfilesConstant.INIT_CONNECTION_DURATION;
		retryCount = DiameterPeerProfilesConstant.RETRY_COUNT;
		sendDPRCloseEvent = DiameterPeerProfilesConstant.SEND_DPR_CLOSE_EVENT;
		minTlsVersion = DiameterPeerProfilesConstant.MAX_TLS_VERSION;
		maxTlsVersion = DiameterPeerProfilesConstant.MIN_TLS_VERSION;
		clientCertificateRequest = DiameterPeerProfilesConstant.CLIENT_CERTIFICATE_REQUEST;
		certificateValidationCheckBox = new CertificateValidationCheckBox();
	}
	
	@XmlTransient
	public String getPeerProfileId() {
		return peerProfileId;
	}

	public void setPeerProfileId(String peerProfileId) {
		this.peerProfileId = peerProfileId;
	}

	@XmlElement(name = "profile-name")
	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "transport-protocol")
	public String getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(String transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	@XmlElement(name = "socket-receive-buffer-size")
	@XmlJavaTypeAdapter(IntegerValidatorAdaptor.class)
	public Integer getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}

	public void setSocketReceiveBufferSize(Integer socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}

	@XmlElement(name = "redirect-host-avp-format")
	public String getRedirectHostAvpFormat() {
		return redirectHostAvpFormat;
	}

	public void setRedirectHostAvpFormat(String redirectHostAvpFormat) {
		this.redirectHostAvpFormat = redirectHostAvpFormat;
	}

	@XmlElement(name = "socket-send-buffer-size")
	@XmlJavaTypeAdapter(IntegerValidatorAdaptor.class)
	public Integer getSocketSendBufferSize() {
		return socketSendBufferSize;
	}

	public void setSocketSendBufferSize(Integer socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}

	@XmlElement(name = "tcp-nagle-algorithm")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getTcpNagleAlgorithm() {
		return tcpNagleAlgorithm;
	}

	public void setTcpNagleAlgorithm(String tcpNagleAlgorithm) {
		this.tcpNagleAlgorithm = tcpNagleAlgorithm;
	}

	@XmlElement(name = "dwr-duration-tw")
	@XmlJavaTypeAdapter(NumericAdapter.class)
	public Long getDwrDuration() {
		return dwrDuration;
	}

	public void setDwrDuration(Long dwrDuration) {
		this.dwrDuration = dwrDuration;
	}

	@XmlTransient
	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	@XmlElement(name = "init-connection-duration-tc")
	@XmlJavaTypeAdapter(NumericAdapter.class)
	public Long getInitConnectionDuration() {
		return initConnectionDuration;
	}

	public void setInitConnectionDuration(Long initConnectionDuration) {
		this.initConnectionDuration = initConnectionDuration;
	}

	public void setSessionCleanUpCER(String sessionCleanUpCER) {
		this.sessionCleanUpCER = sessionCleanUpCER;
	}
	
	@XmlElement(name = "session-clean-up-on-cer")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getSessionCleanUpCER() {
		return sessionCleanUpCER;
	}

	@XmlElement(name = "session-clean-up-on-dpr")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getSessionCleanUpDPR() {
		return sessionCleanUpDPR;
	}

	public void setSessionCleanUpDPR(String sessionCleanUpDPR) {
		this.sessionCleanUpDPR = sessionCleanUpDPR;
	}

	@XmlElement(name = "retry-count")
	@XmlJavaTypeAdapter(NumericAdapter.class)
	public Long getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Long retryCount) {
		this.retryCount = retryCount;
	}

	@XmlElement(name = "cer-avps")
	public String getCerAvps() {
		return cerAvps;
	}

	public void setCerAvps(String cerAvps) {
		this.cerAvps = cerAvps;
	}

	@XmlElement(name = "dpr-avps")
	public String getDprAvps() {
		return dprAvps;
	}

	public void setDprAvps(String dprAvps) {
		this.dprAvps = dprAvps;
	}

	@XmlElement(name = "dwr-avps")
	public String getDwrAvps() {
		return dwrAvps;
	}

	public void setDwrAvps(String dwrAvps) {
		this.dwrAvps = dwrAvps;
	}

	@XmlElement(name = "dpr-on-cer-timeout")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getSendDPRCloseEvent() {
		return sendDPRCloseEvent;
	}

	public void setSendDPRCloseEvent(String sendDPRCloseEvent) {
		this.sendDPRCloseEvent = sendDPRCloseEvent;
	}

	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}

	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}

	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@XmlElement(name = "follow-redirection")
	@XmlJavaTypeAdapter(FollowRedirectionAdapter.class)
	public String getFollowRedirection() {
		return followRedirection;
	}

	public void setFollowRedirection(String followRedirection) {
		this.followRedirection = followRedirection;
	}

	@XmlElement(name = "ciphersuite-list")
	@XmlJavaTypeAdapter(EAPTLSCipherSuitesAdapter.class)
	public String getCiphersuiteList() {
		return ciphersuiteList;
	}

	public void setCiphersuiteList(String ciphersuiteList) {
		this.ciphersuiteList = ciphersuiteList;
	}

	@XmlElement(name = "hotline-policy")
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}

	@XmlTransient
	public String getServerCertificateId() {
		return serverCertificateId;
	}

	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}

	@XmlElement(name = "client-certificate-request")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getClientCertificateRequest() {
		return clientCertificateRequest;
	}

	public void setClientCertificateRequest(String clientCertificateRequest) {
		this.clientCertificateRequest = clientCertificateRequest;
	}

	@XmlTransient
	public String getAllowCertificateCA() {
		return allowCertificateCA;
	}

	public void setAllowCertificateCA(String allowCertificateCA) {
		this.allowCertificateCA = allowCertificateCA;
	}

	@XmlTransient
	public String getValidateCertificateRevocation() {
		return validateCertificateRevocation;
	}

	public void setValidateCertificateRevocation(String validateCertificateRevocation) {
		this.validateCertificateRevocation = validateCertificateRevocation;
	}

	@XmlTransient
	public String getValidateHost() {
		return validateHost;
	}

	public void setValidateHost(String validateHost) {
		this.validateHost = validateHost;
	}

	@XmlElement(name = "security-standard")
	public String getSecurityStandard() {
		return securityStandard;
	}

	public void setSecurityStandard(String securityStandard) {
		this.securityStandard = securityStandard;
	}

	@XmlTransient
	public String getValidateCertificateExpiry() {
		return validateCertificateExpiry;
	}

	public void setValidateCertificateExpiry(String validateCertificateExpiry) {
		this.validateCertificateExpiry = validateCertificateExpiry;
	}

	@XmlElement(name = "server-certificate-profile")
	public String getServerCertificateName() {
		return serverCertificateName;
	}

	public void setServerCertificateName(String serverCertificateName) {
		this.serverCertificateName = serverCertificateName;
	}

	@XmlElement(name = "minimum-tls-version")
	public String getMinTlsVersion() {
		return minTlsVersion;
	}

	public void setMinTlsVersion(String minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}

	@XmlElement(name = "maximum-tls-version")
	public String getMaxTlsVersion() {
		return maxTlsVersion;
	}

	public void setMaxTlsVersion(String maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElement(name = "exclusive-auth-application-ids")
	public String getExclusiveAuthAppIds() {
		return exclusiveAuthAppIds;
	}

	public void setExclusiveAuthAppIds(String exclusiveAuthAppIds) {
		this.exclusiveAuthAppIds = exclusiveAuthAppIds;
	}

	@XmlElement(name = "exclusive-acct-application-ids")
	public String getExclusiveAcctAppIds() {
		return exclusiveAcctAppIds;
	}

	public void setExclusiveAcctAppIds(String exclusiveAcctAppIds) {
		this.exclusiveAcctAppIds = exclusiveAcctAppIds;
	}

	@XmlElement(name = "dhcp-ip-address")
	public String getDhcpIPAddress() {
		return dhcpIPAddress;
	}

	public void setDhcpIPAddress(String dhcpIPAddress) {
		this.dhcpIPAddress = dhcpIPAddress;
	}

	@XmlElement(name = "ha-ip-address")
	public String getHaIPAddress() {
		return haIPAddress;
	}

	public void setHaIPAddress(String haIPAddress) {
		this.haIPAddress = haIPAddress;
	}

	@Valid
	@XmlElement(name = "certificate-validation")
	public CertificateValidationCheckBox getCertificateValidationCheckBox() {
		return certificateValidationCheckBox;
	}

	public void setCertificateValidationCheckBox(CertificateValidationCheckBox certificateValidationCheckBox) {
		this.certificateValidationCheckBox = certificateValidationCheckBox;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Profile Name", profileName);
		object.put("Description", description);
		object.put("CER AVPs", cerAvps);
		object.put("DPR AVPs", dprAvps);
		object.put("DWR AVPs", dwrAvps);

		JSONObject sessionCleanUp = new JSONObject();
		sessionCleanUp.put("CER", sessionCleanUpCER);
		sessionCleanUp.put("DPR", sessionCleanUpDPR);
		object.put("Session Clean Up On", sessionCleanUp);

		object.put("Redirect-Host AVP Format", redirectHostAvpFormat);
		object.put("Follow Redirection", (followRedirection.equals("true")) ? "Enabled" : "Disabled");
		object.put("Hotline Policy", hotlinePolicy);
		object.put("Transport Protocol", transportProtocol);
		object.put("Security Standard", securityStandard);
		object.put("Socket Receive Buffer Size", socketReceiveBufferSize);
		object.put("Socket Send Buffer Size", socketSendBufferSize);
		object.put("TCP Nagle Algorithm", tcpNagleAlgorithm);
		object.put("DWR Duration (Sec) - Tw", dwrDuration);
		object.put("Init Connection Duration (Sec) - Tc", initConnectionDuration);
		object.put("Retry Count", retryCount);
		object.put("DPR on CER Timeout", sendDPRCloseEvent);
		object.put("Minimum TLS Version", minTlsVersion);
		object.put("Maximum TLS Version", maxTlsVersion);
		object.put("Server Certificate Profile", (serverCertificateId == null) ? "NONE" : EliteSMReferencialDAO.fetchServerCertificateDetails(serverCertificateId));
		object.put("Client Certificate Request", clientCertificateRequest);
		object.put("CipherSuite List", EliteSMReferencialDAO.fetchCipherSuitList(ciphersuiteList));
		object.put("Exclusive Auth Application Ids", exclusiveAuthAppIds);
		object.put("Exclusive Acct Application Ids", exclusiveAcctAppIds);
		object.put("HA IP Address", haIPAddress);
		object.put("DHCP IP Address", dhcpIPAddress);

		JSONObject certificateValidation = new JSONObject();
		certificateValidation.put("Expiry Date", validateCertificateExpiry);
		certificateValidation.put("Unknown CA", allowCertificateCA);
		certificateValidation.put("Revoked Certificate", validateCertificateRevocation);
		certificateValidation.put("Unknown Host", validateHost);
		object.put("Certificate Validation", certificateValidation);

		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;
		
		if(socketReceiveBufferSize != null){
			if(socketReceiveBufferSize < -1){
				RestUtitlity.setValidationMessage(context, "Invalid Socket Receive Buffer Size value");
				isValid = false;
			}
		}
		
		if(socketSendBufferSize != null){
			if(socketSendBufferSize < -1){
				RestUtitlity.setValidationMessage(context, "Invalid Socket Send Buffer Size value");
				isValid = false;
			}
		}
		
		if(dwrDuration != null){
			if(dwrDuration < 0){
				RestUtitlity.setValidationMessage(context, "Invalid DWR Duration value");
				isValid = false;
			}
		}
		
		if(initConnectionDuration != null){
			if(initConnectionDuration < 0){
				RestUtitlity.setValidationMessage(context, "Invalid Init Connection Duration value");
				isValid = false;
			}
		}
		
		if(retryCount != null){
			if(retryCount < 0){
				RestUtitlity.setValidationMessage(context, "Invalid Retry Count value");
				isValid = false;
			}
		}
		
		if (Strings.isNullOrBlank(minTlsVersion) == false && Strings.isNullOrBlank(maxTlsVersion) == false) {
			Boolean isValidTlsVersionSelection = EAPConfigUtils.isMaxTLSVersionGreaterThanOrEqualToMinTLSVersion(minTlsVersion, maxTlsVersion);
			if (isValidTlsVersionSelection == false) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Maximum TLS Version must be greater than or equals to Minimum TLS Version");
			}
		}

		if (Strings.isNullOrBlank(ciphersuiteList) == false) {
			if (Strings.isNullOrBlank(minTlsVersion) == false && Strings.isNullOrBlank(maxTlsVersion) == false) {
				if (EAPConfigUtils.isMaxTLSVersionGreaterThanOrEqualToMinTLSVersion(minTlsVersion, maxTlsVersion)) {
					TreeSet<String> invalidCipherSuites = EAPConfigUtils.getTLSVersionSpecificUnsupportedCipherSuiteList(minTlsVersion, maxTlsVersion, ciphersuiteList);
					if (Collectionz.isNullOrEmpty(invalidCipherSuites) == false) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Invalid Cipher Suites :" + invalidCipherSuites);
					}
				}
			}
		}
		
		if (Strings.isNullOrBlank(serverCertificateName) == false) {
			if (serverCertificateName.trim().equalsIgnoreCase("NONE")) {
				serverCertificateId = null;
			} else {
				EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
				try {
					serverCertificateId = eapConfigBLManager.getServerCertificateIdFromName(serverCertificateName.trim());
				} catch (DataManagerException e) {
					if (e.getMessage().contains("No matching")) {
						serverCertificateId = null;
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Invalid Server Certificate Profile name.");
					} else {
						e.printStackTrace();
					}
				}
			}
		} 

		if (Strings.isNullOrBlank(securityStandard)) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Security Standard must be specified.");
		} else {
			List<SecurityStandard> securityStandards = Arrays.asList(SecurityStandard.values());

			if (Collectionz.isNullOrEmpty(securityStandards) == false) {
				if (securityStandards.contains(SecurityStandard.fromSecurityStandardVal(securityStandard.trim())) == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Security Standard.");
				}
			}
		}

		if (Strings.isNullOrBlank(transportProtocol)) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Transport Protocol must be specified.");
		} else {
			if(TransportProtocols.getprotocolList().contains((transportProtocol.trim())) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid value of Transport Protocol.");
			}
		}
		return isValid;
	}
}
