package com.elitecore.aaa.core.wimax;

import java.io.PrintWriter;
import java.io.StringWriter;


public class WimaxRequest {
	
	private String CUI;
	private String userName;
	private Integer DHCP_RK_KEY_ID ;
	private String AAA_Session_ID;
	private Integer serviceType;
	private SourceInfo sourceInfo;
	private HA ha;
	private WimaxCapabilities wimaxCapabilities;
	private EAP eap;
	private PPAQ ppaq;
	private long requestReceivedTimeInMillis;
	private CISCO cisco;

	public WimaxRequest(){
		
		sourceInfo = new SourceInfo();
		ha = new HA();
		wimaxCapabilities = new WimaxCapabilities();
		eap = new EAP();
		ppaq = new PPAQ();
		cisco = new CISCO();
		requestReceivedTimeInMillis = System.currentTimeMillis();
	}
	public SourceInfo getSourceInfo(){
		return sourceInfo;
	}
	public HA getHA(){
		return ha;
	}
	public WimaxCapabilities getWimaxCapabilities(){
		return wimaxCapabilities;
	}
	public EAP getEAP(){
		return eap;
	}
	public PPAQ getPPAQ(){
		return ppaq;
	}
	public CISCO getCisco() {
		return cisco;
	}
	public void setCisco(CISCO cisco) {
		this.cisco = cisco;
	}

	public long getRequestReceivedTimeInMillis() {
		return requestReceivedTimeInMillis;
	}
	
	public class CISCO{
		private String cisco_ssg_service_info;

		public String getCisco_ssg_service_info() {
			return cisco_ssg_service_info;
		}

		public void setCisco_ssg_service_info(String cisco_ssg_service_info) {
			this.cisco_ssg_service_info = cisco_ssg_service_info;
		}
		
	}
	
	public class SourceInfo{
		private String source_IP;
		private String NAS_IP_Address;
		private String NAS_Identifier;
		private Integer NAS_Port;
		private Integer NAS_Port_Type;
		private String NAS_Port_ID;
		private String calling_Station_ID;
		
		public String getCalling_Station_ID() {
			return this.calling_Station_ID;
		}
		public void setCalling_Station_ID(String calling_Station_ID) {
			this.calling_Station_ID = calling_Station_ID;
		}
		public String getSourceIP(){
			return this.source_IP;
		}
		public void setSourceIP(String sourceIP){
			this.source_IP = sourceIP;
		}
		
		public String getNAS_IP_Address(){
			return this.NAS_IP_Address;
		}
		public void setNAS_IP_Address(String NAS_IP_Address){
			this.NAS_IP_Address = NAS_IP_Address;
		}
		
		public String getNAS_Identifier(){
			return this.NAS_Identifier;
		}
		public void setNAS_Identifier(String NAS_Identifier){
			this.NAS_Identifier = NAS_Identifier;
		}
		
		public Integer getNAS_Port(){
			return this.NAS_Port;
		}
		public void setNAS_Port(int NAS_Port){
			this.NAS_Port = NAS_Port;
		}
		
		public Integer getNAS_Port_Type(){
			return this.NAS_Port_Type;
		}
		public void setNAS_Port_Type(int NAS_Port_Type){
			this.NAS_Port_Type = NAS_Port_Type;
		}
		
		public String getNAS_Port_ID(){
			return this.NAS_Port_ID;
		}
		public void setNAS_Port_ID(String NAS_Port_ID){
			this.NAS_Port_ID = NAS_Port_ID;
		}
		
	}
	
	public void setCUI(String CUI){
		this.CUI = CUI;
	}
	public String getCUI(){
		return this.CUI;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	public String getUserName(){
		return this.userName;
	}
	
	public Integer getDHCP_RK_KEY_ID(){
		return this.DHCP_RK_KEY_ID;
	}
	public void setDHCP_RK_KEY_ID(int DHCP_RK_KEY_ID){
		this.DHCP_RK_KEY_ID = DHCP_RK_KEY_ID;
	}
	
	public class HA{
		private Integer MN_HA_MIP4_SPI;
		private Integer HA_RK_SPI;
		
		public Integer getHA_RK_SPI() {
			return this.HA_RK_SPI;
		}
		public void setHA_RK_SPI(int HA_RK_SPI) {
			this.HA_RK_SPI = HA_RK_SPI;
		}
		
		public Integer getMN_HA_MIP4_SPI(){
			return this.MN_HA_MIP4_SPI;
		}
		public void setMN_HA_MIP4_SPI(int MN_HA_RK_SPI){
			this.MN_HA_MIP4_SPI = MN_HA_RK_SPI;
		}
		
	}

	public String getAAA_Session_ID(){
		return this.AAA_Session_ID;
	}
	
	public void setAAA_Session_ID(String AAA_Session_ID){
		this.AAA_Session_ID = AAA_Session_ID;
	}
	
	public class WimaxCapabilities{
		private Integer wimaxRelease;
		private Integer accountingCapabilities;
		private Integer hotliningCapabilities;
		private Integer idleModeNotificationCapabilities;
		
		public Integer getWimaxRelease() {
			return this.wimaxRelease;
		}
		public void setWimaxRelease(int wimaxRelease) {
			this.wimaxRelease = wimaxRelease;
		}
		public Integer getAccountingCapabilities() {
			return this.accountingCapabilities;
		}
		public void setAccountingCapabilities(int accountingCapabilities) {
			this.accountingCapabilities = accountingCapabilities;
		}
		public Integer getHotliningCapabilities() {
			return this.hotliningCapabilities;
		}
		public void setHotliningCapabilities(int hotliningCapabilities) {
			this.hotliningCapabilities = hotliningCapabilities;
		}
		public Integer getIdleModeNotificationCapabilities() {
			return this.idleModeNotificationCapabilities;
		}
		public void setIdleModeNotificationCapabilities(int idleModeNotificationCapabilities) {
			this.idleModeNotificationCapabilities = idleModeNotificationCapabilities;
		}
		
	}

	public Integer getServiceType() {
		return this.serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	
	public class EAP{
		private byte[] MSK;
		private boolean bEapRequest = false;
		private String eapIdentity;
		private String eapMessage; 
		private Integer currentMethod;
		private String eapSessionID;
		
		public String getSessionID(){
			return eapSessionID;
		}
		
		public void setSessionID(String sessionID){
			this.eapSessionID = sessionID;
		}
		
		public Integer getCurrentMethod() {
			return currentMethod;
		}

		public void setCurrentMethod(int currentMethod) {
			this.currentMethod = currentMethod;
		}

		public String getEapMessage() {
			return eapMessage;
		}

		public void setEapMessage(String eapMessage) {
			this.eapMessage = eapMessage;
		}

		public String getEapIdentity() {
			return this.eapIdentity;
		}

		public void setEapIdentity(String eapIdentity) {
			this.eapIdentity = eapIdentity;
		}

		public boolean isEapRequest(){
			return bEapRequest;
		}
		
		public void setEapRequest(boolean bEapRequest){
			this.bEapRequest = bEapRequest;
		}
		
		public void setMSK(byte[] MSK){
			this.MSK = MSK;
		}
		public byte[] getMSK(){
			return this.MSK;
		}
	}
	
	public class PPAQ{
		private Integer quotaIdentifier;
		private Integer durationQuota;
		private Integer durationThreshold;
		private Integer volumeQuota;
		private Integer volumeThreshold;
		private Integer resourceQuota;
		private Integer resourceThreshold;
		private Integer updateReason;
		private String prepaidServer;
		private String serviceID;
		private Integer ratingGroupID;
		private Integer terminationAction;
		private Integer poolID;
		private Integer poolMultiplier;
		private Integer requestedAction;
		private Integer checkBalanceResult;
		private byte[] costInformationAVP;
		
		public Integer getQuotaIdentifier() {
			return this.quotaIdentifier;
		}
		public void setQuotaIdentifier(int quotaIdentifier) {
			this.quotaIdentifier = quotaIdentifier;
		}
		public Integer getDurationQuota() {
			return this.durationQuota;
		}
		public void setDurationQuota(int durationQuota) {
			this.durationQuota = durationQuota;
		}
		public Integer getDurationThreshold() {
			return this.durationThreshold;
		}
		public void setDurationThreshold(int durationThreshold) {
			this.durationThreshold = durationThreshold;
		}
		public Integer getVolumeQuota() {
			return this.volumeQuota;
		}
		public void setVolumeQuota(int volumeQuota) {
			this.volumeQuota = volumeQuota;
		}
		public Integer getVolumeThreshold() {
			return this.volumeThreshold;
		}
		public void setVolumeThreshold(int volumeThreshold) {
			this.volumeThreshold = volumeThreshold;
		}
		public Integer getResourceQuota() {
			return this.resourceQuota;
		}
		public void setResourceQuota(int resourceQuota) {
			this.resourceQuota = resourceQuota;
		}
		public Integer getResourceThreshold() {
			return this.resourceThreshold;
		}
		public void setResourceThreshold(int resourceThreshold) {
			this.resourceThreshold = resourceThreshold;
		}
		public Integer getUpdateReason() {
			return this.updateReason;
		}
		public void setUpdateReason(int updateReason) {
			this.updateReason = updateReason;
		}
		public String getPrepaidServer() {
			return this.prepaidServer;
		}
		public void setPrepaidServer(String prepaidServer) {
			this.prepaidServer = prepaidServer;
		}
		public String getServiceID() {
			return this.serviceID;
		}
		public void setServiceID(String serviceID) {
			this.serviceID = serviceID;
		}
		public Integer getRatingGroupID() {
			return this.ratingGroupID;
		}
		public void setRatingGroupID(int ratingGroupID) {
			this.ratingGroupID = ratingGroupID;
		}
		public Integer getTerminationAction() {
			return this.terminationAction;
		}
		public void setTerminationAction(int terminationAction) {
			this.terminationAction = terminationAction;
		}
		public Integer getPoolID() {
			return this.poolID;
		}
		public void setPoolID(int poolID) {
			this.poolID = poolID;
		}
		public Integer getPoolMultiplier() {
			return this.poolMultiplier;
		}
		public void setPoolMultiplier(int poolMultiplier) {
			this.poolMultiplier = poolMultiplier;
		}
		public Integer getRequestedAction() {
			return this.requestedAction;
		}
		public void setRequestedAction(int requestedAction) {
			this.requestedAction = requestedAction;
		}
		public Integer getCheckBalanceResult() {
			return this.checkBalanceResult;
		}
		public void setCheckBalanceResult(int checkBalanceResult) {
			this.checkBalanceResult = checkBalanceResult;
		}
		public byte[] getCostInformationAVP() {
			return this.costInformationAVP;
		}
		public void setCostInformationAVP(byte[] costInformationAVP) {
			this.costInformationAVP = costInformationAVP;
		}
		
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.print("------------------------WIMAX REQUEST------------------------ ");
		out.print((getUserName()!=null)?("Username 		        			 	: " + getUserName()):"");
		out.print((getAAA_Session_ID()!=null)?("AAA-Session-ID  						: " + getAAA_Session_ID()+"\n"):"");
		out.print((getSourceInfo().getSourceIP()!=null)?("Source IP       						: " + getSourceInfo().getSourceIP()+"\n"):"");
		out.print((getSourceInfo().getCalling_Station_ID()!=null)?("Calling Station ID       			 	: " + getSourceInfo().getCalling_Station_ID()+"\n"):"");
		out.print((getSourceInfo().getNAS_Identifier()!=null)?("NAS Identifier        			 	: " + getSourceInfo().getNAS_Identifier()+"\n"):"");
		out.print((getSourceInfo().getNAS_IP_Address()!=null)?("NAS IP Address        			 	: " + getSourceInfo().getNAS_IP_Address()+"\n"):"");
		out.print((getSourceInfo().getNAS_Port_ID()!=null)?("NAS Port ID	        			 	: " + getSourceInfo().getNAS_Port_ID()+"\n"):"");
		out.print((getSourceInfo().getNAS_Port()!=null)?("NAS Port		         				: " + getSourceInfo().getNAS_Port()+"\n"):"");
		out.print((getSourceInfo().getNAS_Port_Type()!=null)?("NAS Port Type            				: " + getSourceInfo().getNAS_Port_Type()+"\n"):"");
		out.print((getHA().getMN_HA_MIP4_SPI()!=null)?("MN_HA_MIP4_SPI        			 	: " + getHA().getMN_HA_MIP4_SPI()+"\n"):"");
		out.print((getCUI()!=null)?("CUI        	        			 	: " + getCUI()):"");
		out.print((getEAP().getEapIdentity()!=null)?("EAP Identity	        			 	: " + getEAP().getEapIdentity()+"\n"):"");
		out.print((getEAP().getEapMessage()!=null)?("EAP Message 		       			 	: " + getEAP().getEapMessage()+"\n"):"");
		out.print((getEAP().getMSK()!=null)?("MSK        			 	: " + getEAP().getMSK()+"\n"):"");
		out.print((getPPAQ().getPrepaidServer()!=null)?("Prepaid server        			 	: " + getPPAQ().getPrepaidServer()+"\n"):"");
		out.print((getPPAQ().getServiceID()!=null)?("Service ID	        			 	: " + getPPAQ().getServiceID()+"\n"):"");
		out.print((getPPAQ().getCheckBalanceResult()!=null)?("Check Balance Result       			: " + getPPAQ().getCheckBalanceResult()+"\n"):"");
		out.print((getPPAQ().getDurationQuota()!=null)?("Duration Quota        			 	: " + getPPAQ().getDurationQuota()+"\n"):"");
		out.print((getPPAQ().getDurationThreshold()!=null)?("Duration Threshold       			 	: " + getPPAQ().getDurationThreshold()+"\n"):"");
		out.print((getPPAQ().getPoolID()!=null)?("Pool ID		        			 	: " + getPPAQ().getPoolID()+"\n"):"");
		out.print((getPPAQ().getPoolMultiplier()!=null)?("Pool Multiplier        			 	: " + getPPAQ().getPoolMultiplier()+"\n"):"");
		out.print((getPPAQ().getQuotaIdentifier()!=null)?("Quota Identifier        			 	: " + getPPAQ().getQuotaIdentifier()+"\n"):"");
		out.print((getPPAQ().getRatingGroupID()!=null)?("Rating Group ID       			 	: " + getPPAQ().getRatingGroupID()+"\n"):"");
		out.print((getPPAQ().getRequestedAction()!=null)?("Requested Action       			 	: " + getPPAQ().getRequestedAction()+"\n"):"");
		out.print((getPPAQ().getResourceQuota()!=null)?("Resource Quota	       			 	: " + getPPAQ().getResourceQuota()+"\n"):"");
		out.print((getPPAQ().getResourceThreshold()!=null)?("Resource Threshold       			 	: " + getPPAQ().getResourceThreshold()+"\n"):"");
		out.print((getPPAQ().getTerminationAction()!=null)?("Termination Action       			 	: " + getPPAQ().getTerminationAction()+"\n"):"");
		out.print((getPPAQ().getUpdateReason()!=null)?("Update Reason	        			 	: " + getPPAQ().getUpdateReason()+"\n"):"");
		out.print((getPPAQ().getVolumeQuota()!=null)?("Volume Quota    	    			 	: " + getPPAQ().getVolumeQuota()+"\n"):"");
		out.print((getPPAQ().getVolumeThreshold()!=null)?("Volume Threshold        			 	: " + getPPAQ().getVolumeThreshold()+"\n"):"");
		out.print((getWimaxCapabilities().getWimaxRelease()!=null)?("Wimax Release     				  	: " + getWimaxCapabilities().getWimaxRelease()+"\n"):"");
		out.print((getWimaxCapabilities().getAccountingCapabilities()!=null)?("Accounting Capabilities				: " + getWimaxCapabilities().getAccountingCapabilities()+"\n"):"");
		out.print((getWimaxCapabilities().getHotliningCapabilities()!=null)?("Hotlining Capabilities 				: " + getWimaxCapabilities().getHotliningCapabilities()+"\n"):"");
		out.print((getWimaxCapabilities().getIdleModeNotificationCapabilities()!=null)?("Idle Mode Notification Capabilities	: " + getWimaxCapabilities().getIdleModeNotificationCapabilities()+"\n"):"");
		
		
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
}
