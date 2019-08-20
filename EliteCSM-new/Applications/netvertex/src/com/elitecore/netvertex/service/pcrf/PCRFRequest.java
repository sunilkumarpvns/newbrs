package com.elitecore.netvertex.service.pcrf;

import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import javax.annotation.Nullable;
import java.util.*;


/**
 * Defines an object to provide client request information to PCRF Service. Various Listeners creates object of <tt>PCRFRequest</tt>
 * and submits it for further processing. 
 * 
 * @author Subhash Punani
 */
public interface PCRFRequest extends ServiceRequest{
	/**
	 * Stores an attribute in this request.  If the value passed in is <tt>null</tt>, the effect is the same as calling {@link #removeAttribute(String)}  
	 * @param name - a <tt>String</tt> specifying the name of the attribute.
	 * @param value - the <tt>String</tt> to be stored
	 */
	void setAttribute(String name, String value);
	
	/**
	 * Returns the value of the named attribute as an <tt>String</tt>, or <tt>null</tt> if no attribute of the given name exists.
	 * @param <tt>name</tt> - a <tt>String</tt> specifying the name of the attribute
	 * @return A <tt>String</tt> containing the value of the attribute, or <tt>null</tt> if the attribute does not exist
	 */
	String getAttribute(String name);
	
	
	/**
	 * Removes an attribute from this request.
	 * @param name - a <tt>String</tt> specifying the name of the attribute to remove
	 */
	void removeAttribute(String name);
	
	void setSPRInfo(SPRInfo userProfile);
	
	SPRInfo getSPRInfo();
	
	Set<String> getKeySet();

	
	List<UsageMonitoringInfo> getReportedUsageInfoList();
	
	void setReportedUsageInfoList(List<UsageMonitoringInfo> usageMonitoringInfoList);
	
	void addReportedUsageInfo(UsageMonitoringInfo usageMonitoringInfo);
	
	
	
	Date getSessionStartTime();
	void setSessionStartTime(Date sessionStartTime);

	
	public Set<PCRFEvent> getPCRFEvents();
	public void addPCRFEvent(PCRFEvent pcrfEvent);
	public void setPCRFEvents(Set<PCRFEvent> event);
	public void setRemovedPCCRules(List<String> removedPCCRules);
	public List<String> getRemovedPCCRules();
	
	public boolean isSessionFound();
	public void setSessionFound(boolean val);

	void setRequestedQoS(IPCANQoS ipcanQoS);

	IPCANQoS getRequestedQoS();

	void setUsageReservations(@Nullable LinkedHashMap<String, String> ruleToPackage);

	@Nullable LinkedHashMap<String,String> getUsageReservations();
	
	void setActivePccRules(@Nullable Map<String, String> pccIdToSubscriptions);
	
	@Nullable Map<String,String> getActivePccRules();
	
	void setAFActivePCCRule(ArrayList<AFSessionRule> afActivePCCRules);
	List<AFSessionRule> getAFActivePCCRule();
	
	List<MediaComponent> getMediaComponents();
	void setMediaComponents(List<MediaComponent> mediaComponents);
	
	public String getPapPassword();
	public void setPapPassword(String papPassword);
	
	public byte[] getChapPasswordBytes();
	public void setChapPasswordBytes(byte[] chapPasswordBytes);

	public byte[] getChapChallengeBytes();
	public void setChapChallengeBytes(byte[] papPasswordBytes);

	void setSessionUsage(SessionUsage sessionUsage);

	public long creationTimeMillis();

	SessionUsage getSessionUsage();

	void setActiveChargingRuleBaseNames(@Nullable Map<String, String> chargingRuleIdToSubscriptions);
	@Nullable Map<String,String> getActiveChargingRuleBaseNames();

	public List<String> getRemovedChargingRuleBaseNames();
	public void setRemovedChargingRuleBaseNames(List<String> removedChargingRuleBaseNames);

	boolean isSessionCreatedToday(Calendar calendar);

	boolean isSessionCreatedInCurrentWeek(Calendar currentTime);

    void setReportedMSCCs(List<MSCC> msccList);

    @Nullable List<MSCC> getReportedMSCCs();


	void setQuotaReservation(QuotaReservation quotaReservation);

	QuotaReservation getQuotaReservation();
	void setInterfaceQueueTimeInMillies(long interfaceQueueTimeInMillies);

	long getInterfaceQueueTimeInMillies();

	void setInterfacePacketCreateTime(long interfacePacketProcessingTime);

	long getInterfacePacketCreateTime();

	Map<String, String> getDiagnosticInformation();
	void setSPRFetchTime(long sprFetchTime);
	long getSPRFetchTime();
	RatingGroupSelectionState getPCCProfileSelectionState();
	void setPCCProfileSelectionState(RatingGroupSelectionState pccProfileSelectionState);
	void setSessionLoadTime(long sessionFetchTime);
	long getSessionLoadTime();
	void setUnAccountedQuota(QuotaReservation unAccountedReservation);
	QuotaReservation getUnAccountedQuota();

	long getSPRReadTime();

	void setSPRReadTime(long sprReadTime);

	void addDiagnosticInformation(String key, String value);

	void setSubscriptions(LinkedHashMap<String, Subscription> subscriptions);

	LinkedHashMap<String, Subscription> getSubscriptions();

}
