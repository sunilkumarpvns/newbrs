package com.elitecore.netvertex.service.pcrf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.core.servicex.base.BaseServiceResponse;
import com.elitecore.corenetvertex.annotation.Lazy;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.rnc.ReportSummary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Manjil Purohit
 *
 */
public class PCRFResponseImpl extends BaseServiceResponse implements PCRFResponse {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private  PCRFPacket pcrfPacket = new PCRFPacket();


	private transient boolean furtherProcessingRequired;
	private transient boolean isPolicyChanged;
	private transient long sessionTimeOut;
	private transient Map<String, Object> parameter;
	private transient List<PCCRule> newInstallablePCCRules;
	private transient List<String> removablePCCRules;
	private transient List<UsageMonitoringInfo> reportedUsageMonitoringInfoList;
	private transient List<UsageMonitoringInfo> usageMonitoringInfoList;
	private transient Date sessionStartTime;
	private transient Date revalidationTime;
	@Nullable private transient ServiceUsage serviceUsage;
	@Nullable private transient ServiceUsage previousUsage;
	private transient LinkedHashMap<String, Subscription> subscriptions;
	private transient List<AFSessionRule> previousActiveAFSessions;
	private transient List<MediaComponent> mediaComponents;
	@Lazy private transient Map<String,String> usageReservations;
	@Lazy private transient Map<String,String> activePccRuleIdToSubscriptions;
	private transient QoSProfileDetail qosProfileDetail;
	private transient IPCANQoS sessionQoS;
	private transient boolean isEmergencySession = false;
	@Nullable private SessionUsage sessionUsage;
	@Nullable private SessionUsage sessionUsageTillLastUpdate;

	@Lazy private Map<String,String> activeChargingRuleIdToSubscriptions;
	private List<ChargingRuleBaseName> newInstallableChargingRuleBaseNames;
	private List<String> removableChargingRuleBaseNames;
	private transient List<MSCC> reportedMSCCs;
	private transient List<MSCC> grantedMSCCs;
	private transient SubscriberNonMonitoryBalance currentNonMonetoryBalance;
	private transient SubscriberRnCNonMonetaryBalance currentRnCNonMonetaryBalance;
	private transient SubscriberNonMonitoryBalance previousNonMonetoryBalance;
	private transient SubscriberRnCNonMonetaryBalance previousRnCNonMonetaryBalance;
	private transient SubscriberNonMonitoryBalance accountedNonMonetoryBalance;
	private transient SubscriberRnCNonMonetaryBalance accountedRnCNonMonetaryBalance;

	private transient SubscriberMonetaryBalance previousMonetaryBalance;
	private transient SubscriberMonetaryBalance currentMonetaryBalance;
	private transient SubscriberMonetaryBalance accountedMonetaryBalance;

	private transient ReportSummary reportSummary;


	private long sprFetchTimeInMillies = -1;
	private long sprReadTime = -1;
	private long usageFetchTimeInMillies = -1;
	private long usageReadTime = -1;
	private long subscriptionsFetchTimeInMillies = -1;
	private long subscriptionsReadTime = -1;
	private long interfaceQueueTimeInMillies = -1;
	private long engineQueueTimeInMillies = -1;
	private transient RatingGroupSelectionState pccProfileSelectionState;
	private transient QuotaReservation quotaReservation;
	private long resumeTime = -1;
	private long syCommunicationTime = -1;
	private long syPacketQueueTime = -1;
	private long sessionLoadTime = -1;
	private long interfacePacketCreateTime= -1;
	private transient Map<String, Long> handlerNameToProcessingTime;
	private transient Map<String, String> diagnosticInfomation;

	private transient QuotaReservation unAccountedQuota;
	private transient boolean isQuotaReservationChanged;
	private SimpleDateFormat dateFormat;

	public PCRFResponseImpl() {
		this.furtherProcessingRequired=true;
		this.parameter = new HashMap<String, Object>();
		this.sessionStartTime = new Date();
		this.handlerNameToProcessingTime = new HashMap<String, Long>();
		this.diagnosticInfomation = new HashMap<>(4);
	}

	@Override
	public void setParameter(String key, Object parameterValue) {
		parameter.put(key, parameterValue);
	}

	@Override
	public Object getParameter(String str) {
		return parameter.get(str);
	}

	@Override
	public boolean isFurtherProcessingRequired() {
		return furtherProcessingRequired;
	}

	@Override
	public void setFurtherProcessingRequired(boolean value) {
		furtherProcessingRequired = value;
	}

	@Override
	public void setAttribute(String name, String value) {
		pcrfPacket.setAttribute(name, value);
	}

	@Override
	public String getAttribute(String name) {
		return pcrfPacket.getAttribute(name);
	}

	@Override
	public void removeAttribute(String name) {
		pcrfPacket.removeAttribute(name);
	}

	@Override
	public Set<String> getKeySet() {
		return pcrfPacket.getKeySet();
	}

	@Override
	public long getSessionTimeOut() {
		return sessionTimeOut;
	}



	@Override
	public void setSessionTimeOut(long sessionTimeOut) {
		if (this.sessionTimeOut == 0 || sessionTimeOut == 0 || this.sessionTimeOut > sessionTimeOut) {
			this.sessionTimeOut = sessionTimeOut;
			setAttribute(PCRFKeyConstants.SESSION_TIMEOUT.getVal(), String.valueOf(sessionTimeOut));
		}
	}

	@Override
	public boolean isPolicyChanged() {
		return Collectionz.isNullOrEmpty(this.newInstallablePCCRules) == false
				|| Collectionz.isNullOrEmpty(this.removablePCCRules) == false
				|| Collectionz.isNullOrEmpty(this.newInstallableChargingRuleBaseNames) == false
				|| Collectionz.isNullOrEmpty(this.removableChargingRuleBaseNames) == false
				|| isPolicyChanged;
	}

	@Override
	public void setPolicyChanged(boolean policyChanged) {
		this.isPolicyChanged = policyChanged;
	}

	@Override
	public Date getSessionStartTime() {
		return sessionStartTime;
	}

	@Override
	public void setSessionStartTime(Date startTime) {
		if(startTime != null)
			this.sessionStartTime = startTime;
	}

	@Override
	public Date getRevalidationTime() {
		return revalidationTime;
	}

	@Override
	public void setRevalidationTime(Date revalidationTime) {
		if (this.revalidationTime == null || revalidationTime == null || this.revalidationTime.after(revalidationTime)) {
			this.revalidationTime = revalidationTime;
		}
	}

	@Override
	public void setRevalidationTime(Date revalidationTime, long currentTimeInMillis) {
		this.revalidationTime = revalidationTime;
		setSessionTimeOut(TimeUnit.MILLISECONDS.toSeconds(revalidationTime.getTime() - currentTimeInMillis));
	}

	@Override
	public List<com.elitecore.corenetvertex.pm.pkg.PCCRule> getInstallablePCCRules() {
		return newInstallablePCCRules;
	}

	@Override
	public void setRemovablePCCRules(List<String> removablePCCRules) {
		if(removablePCCRules != null)
			this.removablePCCRules = removablePCCRules;
	}
	@Override
	public List<String> getRemovablePCCRules() {
		return removablePCCRules;
	}

	@Override
	public List<UsageMonitoringInfo> getUsageMonitoringInfoList() {
		return usageMonitoringInfoList;
	}


	@Override
	public List<UsageMonitoringInfo> getReportedUsageInfoList() {
		return reportedUsageMonitoringInfoList;
	}


	@Override
	public void setUsageMonitoringInfoList(List<UsageMonitoringInfo> usageMonitoringInfoList) {
		this.usageMonitoringInfoList = usageMonitoringInfoList;
	}

	@Override
	public void addUsageMonitoringInfo(UsageMonitoringInfo usageMonitoringInfo) {
		if(usageMonitoringInfoList == null) {
			usageMonitoringInfoList = new ArrayList<UsageMonitoringInfo>();
		}
		usageMonitoringInfoList.add(usageMonitoringInfo);
	}

	@Override
	public void setReportedUsageInfoList(List<UsageMonitoringInfo> reportedUsageMonitoringInfoList) {
		if(reportedUsageMonitoringInfoList != null)
			this.reportedUsageMonitoringInfoList = reportedUsageMonitoringInfoList;
	}


	@Override
	public PCRFResponseImpl clone() throws CloneNotSupportedException {
		PCRFResponseImpl tempPCRFResponse = (PCRFResponseImpl) super.clone();
		if(pcrfPacket != null){
			tempPCRFResponse.pcrfPacket = pcrfPacket.clone();
		}
		return tempPCRFResponse;
	}



	@Override
	public ServiceUsage getPreviousUsage() {
		return previousUsage;
	}

	@Override
	public void setServiceUsageTillLastUpdate(ServiceUsage previousUsage) {
		this.previousUsage = previousUsage;
	}

	@Override
	public ServiceUsage getCurrentUsage() {
		return serviceUsage;
	}

	@Override
	public void setServiceUsage(ServiceUsage serviceUsage) {
		this.serviceUsage = serviceUsage;
	}

	@Override
	public void setSubscriptions(LinkedHashMap<String, Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public void setInstallablePCCRules(List<com.elitecore.corenetvertex.pm.pkg.PCCRule> pccRule) {
		this.newInstallablePCCRules = pccRule;
	}

	@Override
	public LinkedHashMap<String, Subscription> getSubscriptions() {
		return subscriptions;
	}

	@Override
	public @Nullable Map<String, String> getUsageReservations() {
		return usageReservations;
	}

	@Override
	public void setUsageReservations(@Nullable Map<String, String> usageReservations) {
		this.usageReservations = usageReservations;
	}

	@Override
	public void addUsageReservation(String key, String value) {

		if(usageReservations == null) {
			usageReservations = new LinkedHashMap<String, String>();
		}

		usageReservations.put(key, value);
	}

	@Override
	public List<MediaComponent> getMediaComponents() {
		return mediaComponents;
	}

	@Override
	public void setMediaComponents(List<MediaComponent> mediaComponents) {
		this.mediaComponents =  mediaComponents;
	}

	@Override
	public List<AFSessionRule> getPreviousActiveAFSessions() {
		return previousActiveAFSessions;
	}

	@Override
	public void setPreviousActiveAFSessions(
			List<AFSessionRule> previousActiveAFSessions) {
		this.previousActiveAFSessions = previousActiveAFSessions;
	}

	@Override
	public QoSProfileDetail getQosProfileDetail() {
		return qosProfileDetail;
	}

	@Override
	public void setQosProfileDetail(QoSProfileDetail qosProfileDetail) {
		this.qosProfileDetail = qosProfileDetail;
	}

	@Override
	public IPCANQoS getSessionQoS() {
		return sessionQoS;
	}

	@Override
	public void setSessionQoS(IPCANQoS ipCanQoS) {
		sessionQoS = ipCanQoS;
	}

	@Override
	public String toString(){

		StringWriter stringBuffer=new StringWriter();
		PrintWriter out=new PrintWriter(stringBuffer);
		IndentingWriter tabbedPrintWriter = new IndentingPrintWriter(out);

		out.println();
		out.println(" -- Response Details --");
		out.println("      PCRF Packet ");
		if(pcrfPacket == null)
			out.println("         No pcrf packet found.");
		else
			out.println(pcrfPacket);

		if(isPolicyChanged()){
			out.println("           Policy Changed = YES");
		}else{
			out.println("           Policy Changed = NO");
		}

		if(isQuotaReservationChanged()){
			out.println("           Reservation Changed = YES");
		}else{
			out.println("           Reservation Changed = NO");
		}

		if(isEmergencySession){
			out.println("           Emergency Session = YES");
		}else{
			out.println("           Emergency Session = NO");
		}
		if(sessionStartTime != null)
			out.println("           Session Start Time = " + sessionStartTime);
		if(revalidationTime != null)
			out.println("           Revalidation Time = " + revalidationTime);
		if(sessionTimeOut > 0)
			out.println("           Session TimeOut = " + sessionTimeOut + " Sec.");
		out.println();

		tabbedPrintWriter.incrementIndentation();
		tabbedPrintWriter.println("Session Usage");
		tabbedPrintWriter.incrementIndentation();
		if (sessionUsage == null) {
			tabbedPrintWriter.println("No Session Usage Found");
		} else {
			tabbedPrintWriter.println("Total = " + sessionUsage.getTotalUsage().getTotalOctets()
					+ ", Download = " + sessionUsage.getTotalUsage().getDownloadOctets()
					+ ", Upload = " + sessionUsage.getTotalUsage().getUploadOctets()
					+ ", Time = " + sessionUsage.getTotalUsage().getTime());
		}
		tabbedPrintWriter.decrementIndentation();
		tabbedPrintWriter.decrementIndentation();

		out.println("      UsageReservation Key");
		if (Maps.isNullOrEmpty(usageReservations)) {
			out.println("        No usage reservation key found.");
		} else {
			for (Entry<String, String> entry : usageReservations.entrySet()) {
				out.println("        " + entry);
			}
		}

		out.println("      Quota Reservation");
		if (Objects.isNull(quotaReservation) || quotaReservation.isReservationExist() == false) {
			out.println("        No quota reservation found.");
		} else {
			for (Map.Entry<Long, MSCC> entry : quotaReservation.get()) {
				out.println("        " + entry.getKey());
			}
		}

		out.println("      Active PCCRules ");
		if (Maps.isNullOrEmpty(activePccRuleIdToSubscriptions)) {
			out.println("        No active pcc rules found.");
		} else {
			for (Entry<String, String> pccRuleIdToSubscriptionEntry : activePccRuleIdToSubscriptions.entrySet()) {
				out.println("        " + pccRuleIdToSubscriptionEntry.getKey() + "=" + pccRuleIdToSubscriptionEntry.getValue());
			}
		}

		out.println("      Installable PCCRules ");
		if(Collectionz.isNullOrEmpty(newInstallablePCCRules)){
			out.println("        No installable pcc rules found.");
		}else{
			for(com.elitecore.corenetvertex.pm.pkg.PCCRule pccRule : newInstallablePCCRules){
				out.println("        " + pccRule.getName());
			}
		}

		out.println("      Removable PCCRules ");
		if(removablePCCRules == null || removablePCCRules.isEmpty()){
			out.println("        No removable pcc rules found.");
		}else{
			for(String pccRule : removablePCCRules){
				out.println("        " + pccRule);
			}
		}

		out.println("      Active ChargingRuleBaseNames ");
		if (Maps.isNullOrEmpty(activeChargingRuleIdToSubscriptions)) {
			out.println("        No active ChargingRuleBaseNames found.");
		} else {
			for (Entry<String, String> chargingRuleBaseNameIdToSubscriptionEntry : activeChargingRuleIdToSubscriptions.entrySet()) {
				out.println("        " + chargingRuleBaseNameIdToSubscriptionEntry.getKey() + " = " + chargingRuleBaseNameIdToSubscriptionEntry.getValue());
			}
		}

		out.println("      Installable ChargingRuleBaseNames ");
		if(Collectionz.isNullOrEmpty(newInstallableChargingRuleBaseNames)){
			out.println("        No installable ChargingRuleBaseNames found.");
		}else{
			for(ChargingRuleBaseName chargingRuleBaseName : newInstallableChargingRuleBaseNames){
				out.println("        " + chargingRuleBaseName.getName());
			}
		}

		out.println("      Removable ChargingRuleBaseNames ");
		if(Collectionz.isNullOrEmpty(removableChargingRuleBaseNames)){
			out.println("        No removable ChargingRuleBaseNames found.");
		}else{
			for(String chargingRuleBaseName : removableChargingRuleBaseNames){
				out.println("        " + chargingRuleBaseName);
			}
		}

		out.println("      IP CAN Session QoS  ");
		if(sessionQoS == null) {
			out.println("        No IP CAN session found.");
		} else {
			out.println("        QCI = "+ (sessionQoS.getQCI() == null ? "No qci" : sessionQoS.getQCI().stringVal));
			out.println("        AAMBRDL = "+ sessionQoS.getAAMBRDLInBytes());
			out.println("        AAMBRUL = "+ sessionQoS.getAAMBRULInBytes());
			out.println("        MBRDL = "+ sessionQoS.getMBRDLInBytes());
			out.println("        MBRUL = "+ sessionQoS.getMBRULInBytes());
			out.println("        Priority Level = "+ (sessionQoS.getPriorityLevel() == null ? "No priority" : sessionQoS.getPriorityLevel().stringVal));
			out.println("        Pre-emption Capability = "+ sessionQoS.getPreEmptionCapability());
			out.println("        Pre-emption Vulnerability = "+ sessionQoS.getPreEmptionVulnerability());
		}

		tabbedPrintWriter.incrementIndentation();
		tabbedPrintWriter.println("Media Component");
		tabbedPrintWriter.incrementIndentation();
		if(mediaComponents != null && mediaComponents.isEmpty() == false) {
			for(int i=0; i < mediaComponents.size(); i++) {
				mediaComponents.get(i).toString(tabbedPrintWriter);
			}
		} else {
			tabbedPrintWriter.println("No media components");
		}
		tabbedPrintWriter.decrementIndentation();


		if(usageMonitoringInfoList != null && !usageMonitoringInfoList.isEmpty()) {
			out.println();
			out.println("      Usage Monitoring Information");
			for(UsageMonitoringInfo monitoringInfo : usageMonitoringInfoList){
				out.println("        Monitoring Key = " + monitoringInfo.getMonitoringKey());
				out.println("        Monitoring Level = " + monitoringInfo.getUsageMonitoringLevel());
				if(monitoringInfo.getUsageMonitoringSupport() != null)
					out.println("        Monitoring Support = " + monitoringInfo.getUsageMonitoringSupport());
				if(monitoringInfo.getUsageMonitoringReport() != null)
					out.println("        Monitoring Report = " + monitoringInfo.getUsageMonitoringReport());
				if(monitoringInfo.getGrantedServiceUnit() != null){
					out.println("        Granted Service Units : ");
					out.println(monitoringInfo.getGrantedServiceUnit());
				}
			}
		}

		if(reportedUsageMonitoringInfoList != null && !reportedUsageMonitoringInfoList.isEmpty()) {
			out.println();
			out.println("      Reported Usage Monitoring Information");
			for(UsageMonitoringInfo monitoringInfo : reportedUsageMonitoringInfoList){
				out.println("        Monitoring Key = " + monitoringInfo.getMonitoringKey());
				out.println("        Monitoring Level = " + monitoringInfo.getUsageMonitoringLevel());
				if(monitoringInfo.getUsageMonitoringSupport() != null)
					out.println("        Monitoring Support = " + monitoringInfo.getUsageMonitoringSupport());
				if(monitoringInfo.getUsageMonitoringReport() != null)
					out.println("        Monitoring Report = " + monitoringInfo.getUsageMonitoringReport());
				if(monitoringInfo.getUsedServiceUnit() != null){
					out.println("        Used Service Units : ");
					out.println(monitoringInfo.getUsedServiceUnit());
				}
			}
		}


		if (Collectionz.isNullOrEmpty(reportedMSCCs) == false) {
			tabbedPrintWriter.incrementIndentation();
			tabbedPrintWriter.println("Reported MSCC :");
			tabbedPrintWriter.incrementIndentation();

			for (int i = 0; i < reportedMSCCs.size(); i++) {
				MSCC mscc = reportedMSCCs.get(i);
				mscc.toString(tabbedPrintWriter);
			}
		}

		if (Collectionz.isNullOrEmpty(grantedMSCCs) == false) {
			tabbedPrintWriter.println("Granted MSCC :");
			tabbedPrintWriter.incrementIndentation();

			for (int i = 0; i < grantedMSCCs.size(); i++) {
				MSCC mscc = grantedMSCCs.get(i);
				mscc.toString(tabbedPrintWriter);
			}
			tabbedPrintWriter.decrementIndentation();
		}


		if(interfaceQueueTimeInMillies == -1) {
			out.println("      Interface Queue Time = Request not initiated from Interface, may be request is for re-authorization");
		} else {
			out.println("      Interface Queue Time = " + interfaceQueueTimeInMillies);
		}

		if(interfacePacketCreateTime == -1) {
			out.println("      Interface Packet Create Time = Request not initiated from Interface, may be request is for re-authorization");
		} else {
			out.println("      Interface Packet Create Time = " + interfacePacketCreateTime);
		}


		if(sprFetchTimeInMillies == -1) {
			out.println("      SPR Get Time = SPR Not fetched or fetched from cache");
		} else {
			out.println("      SPR Get Time = " + sprFetchTimeInMillies);
		}

		if(usageFetchTimeInMillies == -1) {
			out.println("      Usage Get Time = Usage Not fetched");
		} else {
			out.println("      Usage Get Time = " + usageFetchTimeInMillies);
		}

		if(subscriptionsFetchTimeInMillies == -1) {
			out.println("      Subscriptions Get Time = Subscription not fetched or fetched from Cache");
		} else {
			out.println("      Subscriptions Get Time = " + subscriptionsFetchTimeInMillies);
		}

		if(engineQueueTimeInMillies == -1) {
			out.println("      Engine Queue Time = Not Subsmitted to PCRF service");
		} else {
			out.println("      Engine Queue Time = " + engineQueueTimeInMillies);
		}

		if(syCommunicationTime == -1) {
			out.println("      Sy Communication Time = Sy communication didn't start");
		} else {
			out.println("      Sy Communication Time = " + syCommunicationTime);
		}

		if(syPacketQueueTime == -1) {
			out.println("      Sy Packet Queue Time = Sy packet didn't wait in queue");
		} else {
			out.println("      Sy Packet Queue Time = " + syPacketQueueTime);
		}

		if (sessionLoadTime == -1) {
			out.println("      Session Load Time = Session not loaded or fetched from cache");
		} else {
			out.println("      Session Load Time = " + sessionLoadTime);
		}

		if(handlerNameToProcessingTime.isEmpty() == false) {
			for(Entry<String, Long> handlerNameToProcessingTimeEntry : handlerNameToProcessingTime.entrySet()) {
				out.println("      " + handlerNameToProcessingTimeEntry.getKey() +" Handler Time = " + handlerNameToProcessingTimeEntry.getValue());
			}
		}

		out.println();
		tabbedPrintWriter.close();

		return stringBuffer.toString();
	}

	@Override
	public void setActivePccRules(Map<String, String> activePccIdToSubscriptions) {
		this.activePccRuleIdToSubscriptions = activePccIdToSubscriptions;
	}

	@Override
	public Map<String, String> getActivePccRules() {
		return activePccRuleIdToSubscriptions;
	}

	@Override
	public boolean isEmergencySession() {
		return isEmergencySession;
	}

	@Override
	public void setEmergencySession(boolean isEmergencySession) {
		this.isEmergencySession = isEmergencySession;
	}

	@Override
	public SessionUsage getSessionUsage() {
		return sessionUsage;
	}


	@Override
	public void setSessionUsageTillLastSessionUpdate(SessionUsage sessionUsage) {
		this.sessionUsageTillLastUpdate = sessionUsage;
	}

	@Override
	public void setSessionUsage(SessionUsage sessionUsage) {
		this.sessionUsage = sessionUsage;
	}

	@Override
	public Map<String, String> getActiveChargingRuleBaseNames() {
		return activeChargingRuleIdToSubscriptions;
	}

	@Override
	public void setActiveChargingRuleBaseNames(Map<String, String> activeChargingRuleIdToSubscriptions) {
		this.activeChargingRuleIdToSubscriptions = activeChargingRuleIdToSubscriptions;
	}

	@Override
	public List<ChargingRuleBaseName> getInstallableChargingRuleBaseNames() {
		return newInstallableChargingRuleBaseNames;
	}

	@Override
	public void setInstallableChargingRuleBaseNames(List<ChargingRuleBaseName> newInstallableChargingRuleBaseNames) {
		this.newInstallableChargingRuleBaseNames = newInstallableChargingRuleBaseNames;
	}

	@Override
	public List<String> getRemovableChargingRuleBaseNames() {
		return removableChargingRuleBaseNames;
	}

	@Override
	public void setRemovableChargingRuleBaseNames(List<String> removableChargingRuleBaseNames) {
		this.removableChargingRuleBaseNames = removableChargingRuleBaseNames;
	}

	@Override
	public List<MSCC> getGrantedMSCCs() {
		return grantedMSCCs;
	}

	@Override
	public void setSPRfetchTime(long sprFetchTime) {
		this.sprFetchTimeInMillies = sprFetchTime;
	}

	@Override
	public void setGrantedMSCCs(List<MSCC> grantedMSCCs) {
		this.grantedMSCCs = grantedMSCCs;
	}

	@Override
	public List<MSCC> getReportedMSCCs() {
		return reportedMSCCs;
	}

	@Override
	public void setReportedMSCCs(List<MSCC> reportedMSCCs) {
		this.reportedMSCCs = reportedMSCCs;
	}

	@Override
	public SubscriberNonMonitoryBalance getCurrentNonMonetoryBalance() {
		return currentNonMonetoryBalance;
	}

	@Override
	public void setCurrentNonMonetoryBalance(SubscriberNonMonitoryBalance currentUsage) {
		this.currentNonMonetoryBalance = currentUsage;
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getCurrentRnCNonMonetaryBalance() {
		return currentRnCNonMonetaryBalance;
	}

	@Override
	public void setCurrentRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance rnCNonMonetaryBalance) {
		this.currentRnCNonMonetaryBalance = rnCNonMonetaryBalance;
	}

	@Override
	public SubscriberMonetaryBalance getCurrentMonetaryBalance() {
		return currentMonetaryBalance;
	}

	@Override
	public void setCurrentMonetaryBalance(SubscriberMonetaryBalance currentMonetaryBalance) {
		this.currentMonetaryBalance = currentMonetaryBalance;
	}

	@Override
	public void setPreviousMonetaryBalance(SubscriberMonetaryBalance previousMonetaryBalance) {
		this.previousMonetaryBalance = previousMonetaryBalance;
	}

	@Override
	public void setQuotaReservation(QuotaReservation reservation) {
		this.quotaReservation = reservation;
	}

	@Override
	public QuotaReservation getQuotaReservation() {
		return quotaReservation;
	}

	@Override
	public QuotaReservation getUnAccountedQuota() {
		return unAccountedQuota;
	}

	@Override
	public long getSPRfetchTime() {
		return sprFetchTimeInMillies;
	}

	@Override
	public void setUsageFetchTime(long usageFetchTime) {
		this.usageFetchTimeInMillies = usageFetchTime;
	}

	@Override
	public long getUsageFetchTime() {
		return usageFetchTimeInMillies;
	}

	@Override
	public void setSubscriptionFetchTime(long subscriptionFetchTime) {
		this.subscriptionsFetchTimeInMillies = subscriptionFetchTime;
	}

	@Override
	public long getSubscriptionsFetchTime() {
		return subscriptionsFetchTimeInMillies;
	}

	@Override
	public void setInterfaceQueueTime(long interfaceQueueTime) {
		this.interfaceQueueTimeInMillies = interfaceQueueTime;
	}

	@Override
	public long getInterfaceQueueTime() {
		return interfaceQueueTimeInMillies;
	}

	@Override
	public void setInterfacePacketCreateTime(long interfacePacketCreateTime) {
		this.interfacePacketCreateTime = interfacePacketCreateTime;
	}

	@Override
	public long getInterfacePacketCreateTime() {
		return interfacePacketCreateTime;
	}

	@Override
	public void setEngineQueueime(long engineIdealTime) {
		this.engineQueueTimeInMillies = engineIdealTime;
	}

	@Override
	public long getEngineQueueTime() {
		return engineQueueTimeInMillies;
	}


	@Override
	public void setResumeTime(long resumeTime) {
		this.resumeTime = resumeTime;
	}

	@Override
	public void setPccProfileSelectionState(RatingGroupSelectionState pccProfileSelectionState) {
		this.pccProfileSelectionState = pccProfileSelectionState;
	}

	@Override
	public RatingGroupSelectionState getPccProfileSelectionState() {
		return pccProfileSelectionState;
	}

	@Override
	public SubscriberMonetaryBalance getPreviousMonetaryBalance() {
		return previousMonetaryBalance;
	}

	@Override
	public SubscriberNonMonitoryBalance getPreviousNonMonetoryBalance() {
		return previousNonMonetoryBalance;
	}

	@Override
	public void setPreviousNonMonetoryBalance(SubscriberNonMonitoryBalance previousNonMonetoryBalance) {
		this.previousNonMonetoryBalance = previousNonMonetoryBalance;
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getPreviousRnCNonMonetaryBalance(){
		return previousRnCNonMonetaryBalance;
	}

	@Override
	public void setPreviousRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance previousRnCNonMonetaryBalance) {
		this.previousRnCNonMonetaryBalance = previousRnCNonMonetaryBalance;
	}

	@Override
	public void setSyCommunicationTime(long syCommunicationTime) {
		this.syCommunicationTime = syCommunicationTime;
	}

	@Override
	public long getSyCommunicationTime() {
		return syCommunicationTime;
	}

	@Override
	public void setSyPacketQueueTime(long syPacketQueueTime) {
		this.syPacketQueueTime = syPacketQueueTime;
	}

	@Override
	public long getSyPacketQueueTime() {
		return syPacketQueueTime;
	}

	@Override
	public long getResumeTime() {
		return resumeTime;
	}

	@Override
	public void setSessionLoadTime(long sessionLoadTime) {
		if (sessionLoadTime == -1) {
			this.sessionLoadTime = sessionLoadTime;
		} else {
			this.sessionLoadTime += sessionLoadTime;
		}
	}

	@Override
	public long getSessionLoadTime() {
		return sessionLoadTime;
	}

	@Override
	public void addHandlerTime(String name, long executionTime) {
		handlerNameToProcessingTime.put(name, executionTime);
	}

	@Override
	public Map<String, Long> getHandlerNameToProcessingTime() {
		return handlerNameToProcessingTime;
	}

	@Override
	public void setUnAccountedQuota(QuotaReservation unAccountedReservation) {

		this.unAccountedQuota = unAccountedReservation;
	}


	@Override
	public SubscriberNonMonitoryBalance getAccountedNonMonetaryBalance() {
		return accountedNonMonetoryBalance;
	}

	@Override
	public void setAccountedNonMonetaryBalance(SubscriberNonMonitoryBalance subscriberNonMonitoryBalance) {
		this.accountedNonMonetoryBalance = subscriberNonMonitoryBalance;
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getAccountedRnCNonMonetaryBalance() {
		return accountedRnCNonMonetaryBalance;
	}

	@Override
	public void setAccountedRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance accountedRnCNonMonetaryBalance) {
		this.accountedRnCNonMonetaryBalance = accountedRnCNonMonetaryBalance;
	}

	@Override
	public SubscriberMonetaryBalance getAccountedMonetaryBalance() {
		return accountedMonetaryBalance;
	}

	@Override
	public void setAccountedMonetaryBalance(SubscriberMonetaryBalance subscriberMonetaryBalance) {
		this.accountedMonetaryBalance = subscriberMonetaryBalance;
	}


	@Override
	public boolean isQuotaReservationChanged() {
		return isQuotaReservationChanged;
	}

	@Override
	public void setQuotaReservationChanged(boolean isQuotaReservationChanged) {
		this.isQuotaReservationChanged = isQuotaReservationChanged;
	}

	@Override
	public ReportSummary getReportSummary() {
		return reportSummary;
	}

	@Override
	public void setReportSummary(ReportSummary reportSummary) {
		this.reportSummary = reportSummary;
	}

	@Override
	public long getSubscriptionsReadTime() {
		return subscriptionsReadTime;
	}

	@Override
	public PCRFResponseImpl setSubscriptionsReadTime(long subscriptionsReadTime) {
		this.subscriptionsReadTime = subscriptionsReadTime;
		return this;
	}

	@Override
	public long getUsageReadTime() {
		return usageReadTime;
	}

	@Override
	public PCRFResponseImpl setUsageReadTime(long usageReadTime) {
		this.usageReadTime = usageReadTime;
		return this;
	}

	@Override
	public long getSPRReadTime() {
		return sprReadTime;
	}

	@Override
	public PCRFResponseImpl setSPRReadTime(long sprReadTime) {
		this.sprReadTime = sprReadTime;
		return this;
	}

	@Override
	public void addDiagnosticInformation(String key, String value) {
		diagnosticInfomation.put(key, value);
	}

	@Override
	public Map<String, String> getDiagnosticInformation() {
		return diagnosticInfomation;
	}

	@Override
	public SimpleDateFormat getChargingCDRDateFormat() {
		return dateFormat;
	}
	@Override
	public void setChargingCDRDateFormat(SimpleDateFormat dateFormat){
		this.dateFormat = dateFormat;
	};

}
