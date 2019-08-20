package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

public class CoreSessionData {
	private long csID;
	private String coreSessionID;
	private String userIdentity;
	private String sessionID;
	private String gatewayAddress;
	private String sessionManagerID;
	private String sessionIPV4;
	private String sessionIPV6;
	private String accessNetwork;
	private String sessionState;
	private String deviceID;
	private String gatewayRealm;
	private String sessionType;
	private String multiSessionID;
	private Timestamp startTime;
	private Timestamp lastUpdateTime;
	private String afApplicationID;
	private String mediaType;
	private Integer maxRequestBWU;
	private Integer maxRequestBWD;
	private String pdnConnectionID;
	private String location;
	private String timeZone;
	private String csgInfoReport;
	private String csgID;
	private String csgAccessMode;
	private String csgMeberIndicate;
	
	private String pccRules;
	private List<PCCRuleData> pccRuleDataList;
	
	Map<String,Object> tblColumnMap;
	private List<SessionRuleData> sessionRuleDataList;
	private String totalSessionTime;

	public String getTotalSessionTime() {
		return totalSessionTime;
	}
	public void setTotalSessionTime(String totalSessionTime) {
		this.totalSessionTime = totalSessionTime;
	}
	public List<SessionRuleData> getSessionRuleDataList() {
		return sessionRuleDataList;
	}
	public void setSessionRuleDataList(List<SessionRuleData> sessionRuleDataList) {
		this.sessionRuleDataList = sessionRuleDataList;
	}
	public Map<String, Object> getTblColumnMap() {
		return tblColumnMap;
	}
	public void setTblColumnMap(Map<String, Object> tblColumnMap) {
		this.tblColumnMap = tblColumnMap;
	}
	public long getCsID() {
		return csID;
	}
	public void setCsID(long csID) {
		this.csID = csID;
	}
	public String getCoreSessionID() {
		return coreSessionID;
	}
	public void setCoreSessionID(String coreSessionID) {
		this.coreSessionID = coreSessionID;
	}
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getGatewayAddress() {
		return gatewayAddress;
	}
	public void setGatewayAddress(String gatewayAddress) {
		this.gatewayAddress = gatewayAddress;
	}
	
	public String getSessionManagerID() {
		return sessionManagerID;
	}
	public void setSessionManagerID(String sessionManagerID) {
		this.sessionManagerID = sessionManagerID;
	}
	
	public String getSessionIPV4() {
		return sessionIPV4;
	}
	public void setSessionIPV4(String sessionIPV4) {
		this.sessionIPV4 = sessionIPV4;
	}
	public String getSessionIPV6() {
		return sessionIPV6;
	}
	public void setSessionIPV6(String sessionIPV6) {
		this.sessionIPV6 = sessionIPV6;
	}
	public String getAccessNetwork() {
		return accessNetwork;
	}
	public void setAccessNetwork(String accessNetwork) {
		this.accessNetwork = accessNetwork;
	}
	public String getSessionState() {
		return sessionState;
	}
	public void setSessionState(String sessionState) {
		this.sessionState = sessionState;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getGatewayRealm() {
		return gatewayRealm;
	}
	public void setGatewayRealm(String gatewayRealm) {
		this.gatewayRealm = gatewayRealm;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	public String getMultiSessionID() {
		return multiSessionID;
	}
	public void setMultiSessionID(String multiSessionID) {
		this.multiSessionID = multiSessionID;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getAfApplicationID() {
		return afApplicationID;
	}
	public void setAfApplicationID(String afApplicationID) {
		this.afApplicationID = afApplicationID;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public Integer getMaxRequestBWU() {
		return maxRequestBWU;
	}
	public void setMaxRequestBWU(Integer maxRequestBWU) {
		this.maxRequestBWU = maxRequestBWU;
	}
	public Integer getMaxRequestBWD() {
		return maxRequestBWD;
	}
	public void setMaxRequestBWD(Integer maxRequestBWD) {
		this.maxRequestBWD = maxRequestBWD;
	}
	public String getPdnConnectionID() {
		return pdnConnectionID;
	}
	public void setPdnConnectionID(String pdnConnectionID) {
		this.pdnConnectionID = pdnConnectionID;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getCsgInfoReport() {
		return csgInfoReport;
	}
	public void setCsgInfoReport(String csgInfoReport) {
		this.csgInfoReport = csgInfoReport;
	}
	public String getCsgID() {
		return csgID;
	}
	public void setCsgID(String csgID) {
		this.csgID = csgID;
	}
	public String getCsgAccessMode() {
		return csgAccessMode;
	}
	public void setCsgAccessMode(String csgAccessMode) {
		this.csgAccessMode = csgAccessMode;
	}
	public String getCsgMeberIndicate() {
		return csgMeberIndicate;
	}
	public void setCsgMeberIndicate(String csgMeberIndicate) {
		this.csgMeberIndicate = csgMeberIndicate;
	}

	public String getPccRules() {
		return pccRules ;
	}
	public void setPccRules(String pccRules) {
		this.pccRules = pccRules;
	}
	public List<PCCRuleData> getPccRuleDataList() {
		return pccRuleDataList;
	}
	public void setPccRuleDataList(List<PCCRuleData> pccRuleDataList) {
		this.pccRuleDataList = pccRuleDataList;
	}
}

