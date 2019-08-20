package com.elitecore.nvsmx.commons.model.sessionmanager;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class CoreSessionData {

	private Integer csId;
	private String coreSessionId;
	private String userIdentity;
	private String sessionId;
    private String gatewatAddress;
    private String sessionManagerId;
    private String sessionIPV4;
    private String sessionIPV6;
    private String accessNetwork;
    private String gatewayRealm;
    private String sesionType;
    private String multiSessionId;
    private Timestamp startTime;
    private Timestamp lastUpdateTime;
    private String timezone;
    private String qosProfile;
    private String addOns;
    private String subscriberIdentity;
    private String dataPackage;
    private String imsPackage;
    private String sourceGateway;
    private String sySessionId;
    private String gatewayName;
    private String syGatewayName;
    private String congestionStatus;
    private String imsi;
    private String msisdn;
    private String nai;
    private String naiRealm;
    private String naiUsername;
    private String sipURL;
    private String pccRules;
    private String requestedQoS;
    private String sessionUsage;
    private String requestNumber;
    private String usageReservation;
    
    @Id
    @Column(name = "CSID")
	public Integer getCsId() {
		return csId;
	}
	public void setCsId(Integer csId) {
		this.csId = csId;
	}
	
	@Column(name = "CORESESSIONID")
	public String getCoreSessionId() {
		return coreSessionId;
	}
	public void setCoreSessionId(String coreSessionId) {
		this.coreSessionId = coreSessionId;
	}
	
	@Column(name = "USERIDENTITY")
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	
	@Column(name = "SESSIONID")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Column(name = "GATEWAYADDRESS")
	public String getGatewatAddress() {
		return gatewatAddress;
	}
	public void setGatewatAddress(String gatewatAddress) {
		this.gatewatAddress = gatewatAddress;
	}
	
	@Column(name = "SESSIONMANAGERID")
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	
	@Column(name = "SESSIONIPV4")
	public String getSessionIPV4() {
		return sessionIPV4;
	}
	public void setSessionIPV4(String sessionIPV4) {
		this.sessionIPV4 = sessionIPV4;
	}
	
	@Column(name = "SESSIONIPV6")
	public String getSessionIPV6() {
		return sessionIPV6;
	}
	public void setSessionIPV6(String sessionIPV6) {
		this.sessionIPV6 = sessionIPV6;
	}
	
	@Column(name = "ACCESSNETWORK")
	public String getAccessNetwork() {
		return accessNetwork;
	}
	public void setAccessNetwork(String accessNetwork) {
		this.accessNetwork = accessNetwork;
	}
	
	@Column(name = "GATEWAYREALM")
	public String getGatewayRealm() {
		return gatewayRealm;
	}
	public void setGatewayRealm(String gatewayRealm) {
		this.gatewayRealm = gatewayRealm;
	}
	
	@Column(name = "SESSIONTYPE")
	public String getSessionType() {
		return sesionType;
	}
	public void setSessionType(String sesionType) {
		this.sesionType = sesionType;
	}
	
	@Column(name = "MULTISESSIONID")
	public String getMultiSessionId() {
		return multiSessionId;
	}
	public void setMultiSessionId(String multiSessionId) {
		this.multiSessionId = multiSessionId;
	}
	
	@Column(name = "STARTTIME")
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	
	@Column(name = "LASTUPDATETIME")
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@Column(name = "TIMEZONE")
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	@Column(name = "QOSPROFILE")
	public String getQosProfile() {
		return qosProfile;
	}
	public void setQosProfile(String qosProfile) {
		this.qosProfile = qosProfile;
	}
	
	@Column(name = "ADDONS")
	public String getAddOns() {
		return addOns;
	}
	public void setAddOns(String addOns) {
		this.addOns = addOns;
	}
	
	@Column(name = "SUBSCRIBERIDENTITY")
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	
	@Column(name = "DATAPACKAGE")
	public String getDataPackage() {
		return dataPackage;
	}
	public void setDataPackage(String dataPackage) {
		this.dataPackage = dataPackage;
	}
	
	@Column(name = "IMSPACKAGE")
	public String getImsPackage() {
		return imsPackage;
	}
	public void setImsPackage(String imsPackage) {
		this.imsPackage = imsPackage;
	}
	
	@Column(name = "SOURCEGATEWAY")
	public String getSourceGateway() {
		return sourceGateway;
	}
	public void setSourceGateway(String sourceGateway) {
		this.sourceGateway = sourceGateway;
	}
	
	@Column(name = "SYSESSIONID")
	public String getSySessionId() {
		return sySessionId;
	}
	public void setSySessionId(String sySessionId) {
		this.sySessionId = sySessionId;
	}
	
	@Column(name = "GATEWAYNAME")
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	
	@Column(name = "SYGATEWAYNAME")
	public String getSyGatewayName() {
		return syGatewayName;
	}
	public void setSyGatewayName(String syGatewayName) {
		this.syGatewayName = syGatewayName;
	}
	
	@Column(name = "CONGESTIONSTATUS")
	public String getCongestionStatus() {
		return congestionStatus;
	}
	public void setCongestionStatus(String congestionStatus) {
		this.congestionStatus = congestionStatus;
	}
	
	@Column(name = "IMSI")
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	
	@Column(name = "MSISDN")
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@Column(name = "NAI")
	public String getNai() {
		return nai;
	}
	public void setNai(String nai) {
		this.nai = nai;
	}
	
	@Column(name = "NAIREALM")
	public String getNaiRealm() {
		return naiRealm;
	}
	public void setNaiRealm(String naiRealm) {
		this.naiRealm = naiRealm;
	}
	
	@Column(name = "NAIUSERNAME")
	public String getNaiUsername() {
		return naiUsername;
	}
	public void setNaiUsername(String naiUsername) {
		this.naiUsername = naiUsername;
	}
	
	@Column(name = "SIPURL")
	public String getSipURL() {
		return sipURL;
	}
	public void setSipURL(String sipURL) {
		this.sipURL = sipURL;
	}
	
	@Column(name = "PCCRULES")
	public String getPccRules() {
		return pccRules;
	}
	public void setPccRules(String pccRules) {
		this.pccRules = pccRules;
	}
	
	@Column(name = "REQUESTEDQOS")
	public String getRequestedQoS() {
		return requestedQoS;
	}
	public void setRequestedQoS(String requestedQoS) {
		this.requestedQoS = requestedQoS;
	}
	
	@Column(name = "SESSIONUSAGE")
	public String getSessionUsage() {
		return sessionUsage;
	}
	public void setSessionUsage(String sessionUsage) {
		this.sessionUsage = sessionUsage;
	}
	
	@Column(name = "REQUESTNUMBER")
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	
	@Column(name = "USAGERESERVATION")
	public String getUsageReservation() {
		return usageReservation;
	}
	public void setUsageReservation(String usageReservation) {
		this.usageReservation = usageReservation;
	}
    
    
}
