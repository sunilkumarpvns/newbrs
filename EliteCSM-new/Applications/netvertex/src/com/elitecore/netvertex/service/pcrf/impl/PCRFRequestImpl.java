package com.elitecore.netvertex.service.pcrf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.base.BaseServiceRequest;
import com.elitecore.corenetvertex.annotation.Lazy;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;
import com.google.gson.Gson;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Manjil Purohit
 *
 */
public class PCRFRequestImpl extends BaseServiceRequest implements PCRFRequest {

	private static final String MODULE = "PCRF-REQ";
	private PCRFPacket pcrfPacket = new PCRFPacket();
	private Map<String, Object> parameter;
	private List<UsageMonitoringInfo> usageMonitoringInfoList;
	private List<String> removedPCCRules;
	private Date sessionStartTime;
	private long creationTimeInMillies;
	private SPRInfo userProfile;
	private boolean sessionFound;
	private LinkedHashMap<String, Subscription> subscriptions;
	/*
	 * Note : PCRFEvent should not be null
	 */
	private Set<PCRFEvent> pcrfEvents;
	private IPCANQoS ipcanQoS;
	private List<AFSessionRule> afActivePCCRules;
	private List<MediaComponent> mediaComponents;
	@Lazy
	private LinkedHashMap<String, String> usageReservations;
	@Lazy
	private Map<String, String> activePccRuleIdToSubscriptions;

	private String papPassword;
	private byte[] chapPasswordBytes;
	private byte[] chapChallengeBytes;
	private long sprFetchTimeInMillies = -1;
	private long interfacePacketCreateTime = -1;
	private long interfaceQueueTimeInMillies = -1;
	private long usageFetchTimeInMillies = -1;
	private long subscriptionsFetchTimeInMillies = -1;
	private long engineQueueTimeInMillies = -1;
	private RatingGroupSelectionState pccProfileSelectionState;
	private long sessionLoadTimeInMillies = -1;
	@Nullable
	private SessionUsage sessionUsage;
	@Lazy
	private Map<String, String> activeChargingRuleIdToSubscriptions;

	private List<String> removedChargingRuleBaseNames;

	private List<MSCC> mSCCs;
	private QuotaReservation quotaReservation;
	private QuotaReservation unAccountedQuota;
	private long sprReadTime;
	private Map<String, String> diagnosticInfomation;

	public PCRFRequestImpl() {
		initialize();
		creationTimeInMillies = System.currentTimeMillis();
	}

	public PCRFRequestImpl(TimeSource timeSource) {
		super(timeSource);
		initialize();
		creationTimeInMillies = timeSource.currentTimeInMillis();
	}

	public void initialize() {
		this.parameter = new HashMap<>();
		this.pcrfEvents = EnumSet.noneOf(PCRFEvent.class);
		sessionFound = false;
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
	public List<UsageMonitoringInfo> getReportedUsageInfoList() {
		return usageMonitoringInfoList;
	}

	@Override
	public void setReportedUsageInfoList(List<UsageMonitoringInfo> usageMonitoringInfoList) {
		if (usageMonitoringInfoList != null) {
			pcrfEvents.add(PCRFEvent.USAGE_REPORT);
		} else {
			pcrfEvents.remove(PCRFEvent.USAGE_REPORT);
		}
		this.usageMonitoringInfoList = usageMonitoringInfoList;
	}

	@Override
	public void addReportedUsageInfo(UsageMonitoringInfo usageMonitoringInfo) {
		if (usageMonitoringInfo != null) {
			if (usageMonitoringInfoList == null)
				usageMonitoringInfoList = new ArrayList<UsageMonitoringInfo>();
			this.usageMonitoringInfoList.add(usageMonitoringInfo);
			pcrfEvents.add(PCRFEvent.USAGE_REPORT);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to create usage info. Reason: usage info is null");
		}

	}

	@Override
	public Date getSessionStartTime() {
		return sessionStartTime;
	}

	@Override
	public void setSessionStartTime(Date startTime) {
		if (startTime != null)
			this.sessionStartTime = startTime;
	}

	@Override
	public Set<PCRFEvent> getPCRFEvents() {
		return pcrfEvents;
	}

	@Override
	public void addPCRFEvent(PCRFEvent pcrfEvent) {
		this.pcrfEvents.add(pcrfEvent);
	}

	@Override
	public void setPCRFEvents(Set<PCRFEvent> event) {
		if (event != null)
			pcrfEvents = EnumSet.copyOf(event);
	}

	@Override
	public void setRemovedPCCRules(List<String> removedPCCRules) {
		this.removedPCCRules = removedPCCRules;
	}

	@Override
	public List<String> getRemovedPCCRules() {
		return removedPCCRules;
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println(" -- Request Details --");
		out.println("      PCRF Packet ");
		if (pcrfPacket == null)
			out.println("         No pcrf packet found.");
		else
			out.println(pcrfPacket);

		out.println("           Session Found = " + sessionFound);

		if (sessionStartTime != null)
			out.println("           Start Time = " + sessionStartTime);
		if (!pcrfEvents.isEmpty()) {
			out.println("           PCRF Service Events ");
			for (PCRFEvent pcrfEvent : pcrfEvents)
				out.println("             " + pcrfEvent.name());
		}

		IndentingWriter indentingWriter = new IndentingPrintWriter(out);

		indentingWriter.incrementIndentation();
		indentingWriter.println();
		indentingWriter.println("AF active PCCRules");
		indentingWriter.incrementIndentation();
		if (afActivePCCRules == null || afActivePCCRules.isEmpty()) {
			indentingWriter.println("        No active rules found.");
		} else {
			for (int i = 0; i < afActivePCCRules.size(); i++) {
				afActivePCCRules.get(i).toString(indentingWriter);
			}
		}
		indentingWriter.decrementIndentation();
		indentingWriter.println("Session Usage");
		indentingWriter.incrementIndentation();
		if (sessionUsage == null) {
			indentingWriter.println("No Session Usage Found");
		} else {
			indentingWriter.println("Total = " + sessionUsage.getTotalUsage().getTotalOctets()
					+ ", Download = " + sessionUsage.getTotalUsage().getDownloadOctets()
					+ ", Upload = " + sessionUsage.getTotalUsage().getUploadOctets()
					+ ", Time = " + sessionUsage.getTotalUsage().getTime());
		}
		indentingWriter.decrementIndentation();
		indentingWriter.decrementIndentation();

		out.println("      UsageReservation Key");
		if (Maps.isNullOrEmpty(usageReservations)) {
			out.println("        No usage reservation key found.");
		} else {
			for (Entry<String, String> entry : usageReservations.entrySet()) {
				out.println("        " + entry);
			}
		}

		out.println("      Already Active PCCRules ");
		if (Maps.isNullOrEmpty(activePccRuleIdToSubscriptions)) {
			out.println("        No active pcc rules found.");
		} else {
			for (Entry<String, String> pccRuleIdToSubscription : activePccRuleIdToSubscriptions.entrySet()) {
				out.println("        " + pccRuleIdToSubscription.getKey() + "=" + pccRuleIdToSubscription.getValue());
			}
		}

		out.println("      Removed PCCRules ");
		if (removedPCCRules == null || removedPCCRules.isEmpty()) {
			out.println("        No removed pcc rules found.");
		} else {
			for (String pccRule : removedPCCRules) {
				out.println("        " + pccRule);
			}
		}


		out.println("      Already Active ChargingRuleBaseNames ");
		if (Maps.isNullOrEmpty(activeChargingRuleIdToSubscriptions)) {
			out.println("        No active ChargingRuleBaseNames found.");
		} else {
			for (Map.Entry<String, String> chargingRuleBaseNameIdToSubscription : activeChargingRuleIdToSubscriptions.entrySet()) {
				out.println("        " + chargingRuleBaseNameIdToSubscription.getKey() + " = " + chargingRuleBaseNameIdToSubscription.getValue());
			}
		}

		out.println("      Removed ChargingRuleBaseNames ");
		if (Collectionz.isNullOrEmpty(removedChargingRuleBaseNames)) {
			out.println("        No removed ChargingRuleBaseNames found.");
		} else {
			for (String chargingRuleBaseName : removedChargingRuleBaseNames) {
				out.println("        " + chargingRuleBaseName);
			}
		}

		out.println();
		out.println("      Requested QoS");
		if (ipcanQoS == null) {
			out.println("        No request QoS found.");
		} else {
			out.println("        AAMBRDL = " + ipcanQoS.getAAMBRDLInBytes());
			out.println("        AAMBRUL = " + ipcanQoS.getAAMBRULInBytes());
			out.println("        MBRDL = " + ipcanQoS.getMBRDLInBytes());
			out.println("        MBRUL = " + ipcanQoS.getMBRULInBytes());
			out.println("        QCI = " + (ipcanQoS.getQCI() == null ? "No qci" : ipcanQoS.getQCI().val));
			out.println("        Priority Level = " + (ipcanQoS.getPriorityLevel() == null ? "No priority " : ipcanQoS.getPriorityLevel().val));
			out.println("        Pre-Emption Capability = " + ipcanQoS.getPreEmptionCapability());
			out.println("        Pre-Emption Vulnerability = " + ipcanQoS.getPreEmptionVulnerability());
			out.println("        QoS-Upgrade = " + (ipcanQoS.isQosUpgrade() ? "Suppoted" : "Not Supported"));
		}

		indentingWriter.incrementIndentation();
		indentingWriter.println("Media Component");
		indentingWriter.incrementIndentation();
		if (mediaComponents != null && mediaComponents.isEmpty() == false) {
			for (int i = 0; i < mediaComponents.size(); i++) {
				mediaComponents.get(i).toString(indentingWriter);
			}
		} else {
			indentingWriter.println("No media components");
		}
		indentingWriter.decrementIndentation();
		indentingWriter.decrementIndentation();

		out.println();
		out.println("      Usage Monitoring Information");
		if (usageMonitoringInfoList == null || usageMonitoringInfoList.isEmpty()) {
			out.println("        No Usage Monitoring Information found");
		} else {
			for (UsageMonitoringInfo monitoringInfo : usageMonitoringInfoList) {
				out.println("        Monitoring Key = " + monitoringInfo.getMonitoringKey());
				out.println("        Monitoring Level = " + monitoringInfo.getUsageMonitoringLevel());
				if (monitoringInfo.getUsageMonitoringSupport() != null)
					out.println("        Monitoring Support = " + monitoringInfo.getUsageMonitoringSupport());
				if (monitoringInfo.getUsageMonitoringReport() != null)
					out.println("        Monitoring Report = " + monitoringInfo.getUsageMonitoringReport());
				if (monitoringInfo.getUsedServiceUnit() != null) {
					out.println("        Used Service Units : ");
					out.println(monitoringInfo.getUsedServiceUnit());
				}
			}
		}

		if (Collectionz.isNullOrEmpty(mSCCs) == false) {
			indentingWriter.incrementIndentation();
			indentingWriter.println("Reported MSCC :");
			indentingWriter.incrementIndentation();

			for (int i = 0; i < mSCCs.size(); i++) {
				MSCC mscc = mSCCs.get(i);
				mscc.toString(indentingWriter);
			}

			indentingWriter.decrementIndentation();
			indentingWriter.decrementIndentation();
		}

		indentingWriter.println();

		printPccSelectionState(indentingWriter);

		indentingWriter.incrementIndentation();

		indentingWriter.println();
		if (interfaceQueueTimeInMillies == -1) {
			indentingWriter.println("Interface Queue Time = Request not initiated from Interface, may be request is for re-authorization");
		} else {
			indentingWriter.println("Interface Queue Time = " + interfaceQueueTimeInMillies);
		}

		if (interfacePacketCreateTime == -1) {
			indentingWriter.println("Interface Packet Queue = Request not initiated from Interface, may be request is for re-authorization");
		} else {
			indentingWriter.println("Interface Packet Create Time = " + interfacePacketCreateTime);
		}


		if (sprFetchTimeInMillies == -1) {
			indentingWriter.println("SPR Get Time = SPR fetched from cache or not fetched");
		} else {
			indentingWriter.println("SPR Get Time = " + sprFetchTimeInMillies);
		}

		if (usageFetchTimeInMillies == -1) {
			indentingWriter.println("Usage Get Time = Usage Not fetched");
		} else {
			indentingWriter.println("Usage Get Time = " + usageFetchTimeInMillies);
		}

		if (subscriptionsFetchTimeInMillies == -1) {
			indentingWriter.println("Subscriptions Get Time = Subscription not fetched or fetched from Cache");
		} else {
			indentingWriter.println("Subscriptions Get Time = " + subscriptionsFetchTimeInMillies);
		}

		if (engineQueueTimeInMillies == -1) {
			indentingWriter.println("Engine Queue Time = Not Subsmitted to PCRF service");
		} else {
			indentingWriter.println("Engine Queue Time = " + engineQueueTimeInMillies);
		}

		if (sessionLoadTimeInMillies == -1) {
			indentingWriter.println("Session Load Time = Session not loaded or fetched from cache");
		} else {
			indentingWriter.println("Session Load Time = " + sessionLoadTimeInMillies);
		}

		indentingWriter.decrementIndentation();
		indentingWriter.println();

		indentingWriter.close();

		return stringBuffer.toString();
	}

	private void printPccSelectionState(IndentingWriter indentingWriter) {
		indentingWriter.incrementIndentation();
		indentingWriter.println("PCC selection state information");
		indentingWriter.incrementIndentation();
		if (pccProfileSelectionState != null) {
			pccProfileSelectionState.toString(indentingWriter);
		} else {
			indentingWriter.println("No PCC profile selection state");
		}
		indentingWriter.decrementIndentation();
		indentingWriter.decrementIndentation();
	}

	@Override
	public void setSPRInfo(SPRInfo userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public SPRInfo getSPRInfo() {
		return userProfile;
	}

	@Override
	public boolean isSessionFound() {
		return sessionFound;
	}

	@Override
	public void setSessionFound(boolean val) {
		sessionFound = val;
	}

	@Override
	public IPCANQoS getRequestedQoS() {
		return ipcanQoS;
	}

	@Override
	public void setUsageReservations(@Nullable LinkedHashMap<String, String> usageReservations) {
		this.usageReservations = usageReservations;
	}

	@Override
	public @Nullable
	LinkedHashMap<String, String> getUsageReservations() {
		return usageReservations;
	}

	@Override
	public void setRequestedQoS(IPCANQoS ipcanQoS) {
		this.ipcanQoS = ipcanQoS;
		if (this.ipcanQoS == null) {
			setAttribute(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, "");
			removeAttribute(PCRFKeyConstants.REQ_AAMBRDL.val);
			removeAttribute(PCRFKeyConstants.REQ_AAMBRUL.val);
			removeAttribute(PCRFKeyConstants.REQ_PREEMPTION_CAPABILITY.val);
			removeAttribute(PCRFKeyConstants.REQ_PREEMPTION_VULNERABILITY.val);
			removeAttribute(PCRFKeyConstants.REQ_PRIORITY_LEVEL.val);
			removeAttribute(PCRFKeyConstants.REQ_QCI.val);

		} else {
			setAttribute(PCRFKeyConstants.REQ_AAMBRDL.val, String.valueOf(ipcanQoS.getAAMBRDLInBytes()));
			setAttribute(PCRFKeyConstants.REQ_AAMBRUL.val, String.valueOf(ipcanQoS.getAAMBRULInBytes()));
			setAttribute(PCRFKeyConstants.REQ_MBRDL.val, String.valueOf(ipcanQoS.getMBRDLInBytes()));
			setAttribute(PCRFKeyConstants.REQ_MBRUL.val, String.valueOf(ipcanQoS.getMBRULInBytes()));
			setAttribute(PCRFKeyConstants.REQ_PREEMPTION_CAPABILITY.val, ipcanQoS.getPreEmptionVulnerability() == true ?
					PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_ENABLE.val
					: PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_DISABLE.val);
			setAttribute(PCRFKeyConstants.REQ_PREEMPTION_VULNERABILITY.val, ipcanQoS.getPreEmptionVulnerability() == true ?
					PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_ENABLE.val
					: PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_DISABLE.val);

			if (ipcanQoS.getPriorityLevel() != null) {
				setAttribute(PCRFKeyConstants.REQ_PRIORITY_LEVEL.val, String.valueOf(ipcanQoS.getPriorityLevel().val));
			}

			if (ipcanQoS.getQCI() != null) {
				setAttribute(PCRFKeyConstants.REQ_QCI.val, String.valueOf(ipcanQoS.getQCI().val));
			}

			Gson gson = GsonFactory.defaultInstance();
			setAttribute(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, gson.toJson(ipcanQoS));
		}

	}

	@Override
	public void setAFActivePCCRule(ArrayList<AFSessionRule> afActivePCCRules) {
		this.afActivePCCRules = afActivePCCRules;
	}

	@Override
	public List<AFSessionRule> getAFActivePCCRule() {
		return this.afActivePCCRules;

	}

	@Override
	public List<MediaComponent> getMediaComponents() {
		return mediaComponents;
	}

	@Override
	public void setMediaComponents(List<MediaComponent> mediaComponents) {
		this.mediaComponents = mediaComponents;
	}

	@Override
	public byte[] getChapPasswordBytes() {
		return chapPasswordBytes;
	}

	@Override
	public void setChapPasswordBytes(byte[] chapPasswordBytes) {
		this.chapPasswordBytes = chapPasswordBytes;
	}

	@Override
	public byte[] getChapChallengeBytes() {
		return chapChallengeBytes;
	}

	@Override
	public void setChapChallengeBytes(byte[] chapChallengeBytes) {
		this.chapChallengeBytes = chapChallengeBytes;
	}

	@Override
	public String getPapPassword() {
		return papPassword;
	}

	@Override
	public void setPapPassword(String papPassword) {
		this.papPassword = papPassword;
	}

	@Override
	public Map<String, String> getActivePccRules() {
		return activePccRuleIdToSubscriptions;
	}

	@Override
	public void setActivePccRules(Map<String, String> activePccRuleIdToSubscriptions) {
		this.activePccRuleIdToSubscriptions = activePccRuleIdToSubscriptions;
	}

	@Override
	public SessionUsage getSessionUsage() {
		return sessionUsage;
	}

	@Override
	public void setActiveChargingRuleBaseNames(@Nullable Map<String, String> chargingRuleIdToSubscriptions) {
		this.activeChargingRuleIdToSubscriptions = chargingRuleIdToSubscriptions;
	}

	@Nullable
	@Override
	public Map<String, String> getActiveChargingRuleBaseNames() {
		return activeChargingRuleIdToSubscriptions;
	}

	@Override
	public void setSessionUsage(SessionUsage sessionUsage) {
		this.sessionUsage = sessionUsage;
	}

	@Override
	public long creationTimeMillis() {
		return creationTimeInMillies;
	}

	@Override
	public List<String> getRemovedChargingRuleBaseNames() {
		return removedChargingRuleBaseNames;
	}

	@Override
	public boolean isSessionCreatedToday(Calendar currentTime) {
		Calendar sessionStartTimeCal = Calendar.getInstance();
		sessionStartTimeCal.setTime(sessionStartTime);
		return sessionStartTimeCal.get(Calendar.DATE) == currentTime.get(Calendar.DATE);
	}

	@Override
	public void setRemovedChargingRuleBaseNames(List<String> removedChargingRuleBaseNames) {
		this.removedChargingRuleBaseNames = removedChargingRuleBaseNames;
	}

	@Override
	public boolean isSessionCreatedInCurrentWeek(Calendar currentTime) {

		Calendar startDayOfWeek = Calendar.getInstance();

		startDayOfWeek.setFirstDayOfWeek(currentTime.getFirstDayOfWeek());
		startDayOfWeek.setTimeInMillis(currentTime.getTimeInMillis());
		startDayOfWeek.set(Calendar.DAY_OF_WEEK, currentTime.getFirstDayOfWeek());
		startDayOfWeek.set(Calendar.HOUR_OF_DAY, 0);
		startDayOfWeek.set(Calendar.MINUTE, 0);
		startDayOfWeek.set(Calendar.SECOND, 0);
		startDayOfWeek.set(Calendar.MILLISECOND, 0);

		long nextWeekStartTime = startDayOfWeek.getTimeInMillis() + TimeUnit.DAYS.toMillis(7);

		return sessionStartTime.getTime() >= startDayOfWeek.getTimeInMillis() && sessionStartTime.getTime() < nextWeekStartTime;
	}

	@Override
	public void setReportedMSCCs(List<MSCC> msccList) {
		this.mSCCs = msccList;
	}

	@Override
	public void setInterfaceQueueTimeInMillies(long interfaceQueueTimeInMillies) {
		this.interfaceQueueTimeInMillies = interfaceQueueTimeInMillies;
	}

	@Override
	public List<MSCC> getReportedMSCCs() {
		return mSCCs;
	}

	@Override
	public void setQuotaReservation(QuotaReservation quotaReservation) {
		this.quotaReservation = quotaReservation;
	}

	@Override
	public QuotaReservation getQuotaReservation() {
		return quotaReservation;
	}

	@Override
	public long getInterfaceQueueTimeInMillies() {
		return interfaceQueueTimeInMillies;
	}

	@Override
	public void setInterfacePacketCreateTime(long interfacePacketProcessingTime) {
		this.interfacePacketCreateTime = interfacePacketProcessingTime;
	}

	@Override
	public long getInterfacePacketCreateTime() {
		return interfacePacketCreateTime;
	}

	@Override
	public void setSPRFetchTime(long sprFetchTime) {
		this.sprFetchTimeInMillies = sprFetchTime;
	}

	@Override
	public long getSPRFetchTime() {
		return sprFetchTimeInMillies;
	}

	@Override
	public void setPCCProfileSelectionState(RatingGroupSelectionState pccProfileSelectionState) {
		this.pccProfileSelectionState = pccProfileSelectionState;
	}

	@Override
	public RatingGroupSelectionState getPCCProfileSelectionState() {
		return pccProfileSelectionState;
	}

	@Override
	public void setSessionLoadTime(long sessionLoadTimeInMillies) {
		this.sessionLoadTimeInMillies = sessionLoadTimeInMillies;
	}

	@Override
	public long getSessionLoadTime() {
		return sessionLoadTimeInMillies;
	}

	@Override
	public void setUnAccountedQuota(QuotaReservation unAccountedQuota) {
		this.unAccountedQuota = unAccountedQuota;
	}

	@Override
	public QuotaReservation getUnAccountedQuota() {
		return unAccountedQuota;
	}

	@Override
	public long getSPRReadTime() {
		return sprReadTime;
	}

	@Override
	public void setSPRReadTime(long sprReadTime) {
		this.sprReadTime = sprReadTime;
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
	public void setSubscriptions(LinkedHashMap<String, Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public LinkedHashMap<String, Subscription> getSubscriptions() {
		return subscriptions;
	}
}
