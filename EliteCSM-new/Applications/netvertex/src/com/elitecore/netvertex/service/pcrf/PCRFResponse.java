package com.elitecore.netvertex.service.pcrf;

import com.elitecore.core.servicex.ServiceResponse;
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
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Defines an object to provide client response information to PCRF Service. Various Listeners creates object of <tt>PCRFResponse</tt>
 * and submits it for further processing. 
 * 
 * @author Subhash Punani
 */
public interface PCRFResponse extends ServiceResponse, Cloneable, Serializable {
	/**
	 * Stores an attribute in this response.  If the value passed in is <tt>null</tt>, the effect is the same as calling {@link #removeAttribute(String)}  
	 * @param name - a <tt>String</tt> specifying the name of the attribute.
	 * @param value - the <tt>String</tt> to be stored
	 */
	void setAttribute(String name, String value);
	
	/**
	 * Returns the value of the named attribute as an <tt>String</tt>, or <tt>null</tt> if no attribute of the given name exists.
	 * @param name - a <tt>String</tt> specifying the name of the attribute
	 * @return A <tt>String</tt> containing the value of the attribute, or <tt>null</tt> if the attribute does not exist
	 */
	String getAttribute(String name);
	
	/**
	 * Removes an attribute from this response.
	 * @param name - a <tt>String</tt> specifying the name of the attribute to remove
	 */
	void removeAttribute(String name);
	
	/**
	 * Returns the set of PCRF Attribute that the PCRFResponse contain.
	 * @return  <code>Set</code>
	 */
	Set<String> getKeySet();
	
	/**
	 * set SessionTimeOut
	 */
	public void setSessionTimeOut(long sessionTimeOut);
	
	/**
	 * get SessionTimeOut
	 */
	public long getSessionTimeOut();
	
	/**
	 * This flag indicates that Session Auth Rule stored in on going Session is changed.
	 * <br> 
	 * This flag is Set to TRUE for below cases.
	 * <li> If Subscriber Package level Session Auth Rule is changed</li>
	 * <li> If BoD Package level Session Auth Rule is changed</li>
	 * <br><br>
	 * Note: This flag always returns TRUE for Initial Requests
	 */
	public boolean isPolicyChanged();
	
	public void setPolicyChanged(boolean policyChanged);

	List<UsageMonitoringInfo> getUsageMonitoringInfoList();
	
	void setUsageMonitoringInfoList(List<UsageMonitoringInfo> usageMonitoringInfoList);
	
	void addUsageMonitoringInfo(UsageMonitoringInfo usageMonitoringInfo);
	
	Date getSessionStartTime();
	void setSessionStartTime(Date sessionStartTime);

	Date getRevalidationTime();

	void setRevalidationTime(Date revalidationTime);
	
	void setRevalidationTime(Date revalidationTime, long currentTimeInMillis);
	
	public void setRemovablePCCRules(List<String> removablePCCRules);
	
	public List<String> getRemovablePCCRules();

	void setReportedUsageInfoList(
			List<UsageMonitoringInfo> reportedUsageMonitoringInfoList);

	List<UsageMonitoringInfo> getReportedUsageInfoList();

	PCRFResponse clone() throws CloneNotSupportedException;
	
	public ServiceUsage getCurrentUsage();
	
	public void setServiceUsage(ServiceUsage map);

	void setServiceUsageTillLastUpdate(ServiceUsage previousUsage);

	ServiceUsage getPreviousUsage();

	Map<String, String> getUsageReservations();

	void setUsageReservations(Map<String, String> usageReservations);
	
	void setActivePccRules(@Nullable Map<String, String> pccIdToSubscriptions);
	
	@Nullable Map<String,String> getActivePccRules();
	
	void setSubscriptions(LinkedHashMap<String, Subscription> subscriptions);
	
	LinkedHashMap<String, Subscription> getSubscriptions();

	void addUsageReservation(String key, String value);

	void setInstallablePCCRules(List<PCCRule> arrayList);

	List<PCCRule> getInstallablePCCRules();
	
	QoSProfileDetail getQosProfileDetail();
	
	IPCANQoS getSessionQoS();

	List<MediaComponent> getMediaComponents();

	void setMediaComponents(List<MediaComponent> mediaComponents);

	public List<AFSessionRule> getPreviousActiveAFSessions();

	public void setPreviousActiveAFSessions(List<AFSessionRule> previousActiveAFSessions);

	void setQosProfileDetail(QoSProfileDetail qosProfileDetail);

	void setSessionQoS(IPCANQoS ipCanQoS);

	boolean isEmergencySession();

	void setEmergencySession(boolean isEmergencySession);

	void setSessionUsage(SessionUsage sessionUsage);

	SessionUsage getSessionUsage();

	void setSessionUsageTillLastSessionUpdate(SessionUsage sessionUsage);

	Map<String, String> getActiveChargingRuleBaseNames();

	void setActiveChargingRuleBaseNames(Map<String, String> activeChargingRuleIdToSubscriptions);

	List<ChargingRuleBaseName> getInstallableChargingRuleBaseNames();

	void setInstallableChargingRuleBaseNames(List<ChargingRuleBaseName> newInstallableChargingRuleBaseNames);

	List<String> getRemovableChargingRuleBaseNames() ;

	void setRemovableChargingRuleBaseNames(List<String> removableChargingRuleBaseNames);

	List<MSCC> getGrantedMSCCs();
	
	List<MSCC> getReportedMSCCs();

	void setGrantedMSCCs(List<MSCC> grantedMSCCs);

	void setReportedMSCCs(List<MSCC> reportedMSCCs);

	SubscriberRnCNonMonetaryBalance getCurrentRnCNonMonetaryBalance();

	void setCurrentRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance rnCBalance);

	SubscriberNonMonitoryBalance getCurrentNonMonetoryBalance();

    void setCurrentNonMonetoryBalance(SubscriberNonMonitoryBalance currentUsage);

	SubscriberMonetaryBalance getCurrentMonetaryBalance();

	void setCurrentMonetaryBalance(SubscriberMonetaryBalance monetaryBalance);

	void setSPRfetchTime(long sprFetchTime);

	long getSPRfetchTime();

	void setUsageFetchTime(long usageFetchTime);

	long getUsageFetchTime();

	void setSubscriptionFetchTime(long subscriptionFetchTime);

	long getSubscriptionsFetchTime();

	void setInterfaceQueueTime(long interfaceQueueTime);

	long getInterfaceQueueTime();

	void setInterfacePacketCreateTime(long interfacePacketCreateTime);

	long getInterfacePacketCreateTime();

	void setEngineQueueime(long engineQueueime);

	long getEngineQueueTime();

    void setPccProfileSelectionState(RatingGroupSelectionState pccProfileSelectionState);
	RatingGroupSelectionState getPccProfileSelectionState();

    void setPreviousMonetaryBalance(SubscriberMonetaryBalance subscriberOldMonetaryBalance);

	SubscriberMonetaryBalance getPreviousMonetaryBalance();

	SubscriberNonMonitoryBalance getPreviousNonMonetoryBalance();

    void setPreviousNonMonetoryBalance(SubscriberNonMonitoryBalance previousNonMonetoryBalance);

	SubscriberRnCNonMonetaryBalance getPreviousRnCNonMonetaryBalance();

	void setPreviousRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance previousRnCNonMonetaryBalance);

	void setQuotaReservation(QuotaReservation reservation);

    QuotaReservation getQuotaReservation();

	QuotaReservation getUnAccountedQuota();

    void setResumeTime(long resumeTime);

	void setSyCommunicationTime(long l);

	long getSyCommunicationTime();

	void setSyPacketQueueTime(long queueTime);

	long getSyPacketQueueTime();

	long getResumeTime();

	void setSessionLoadTime(long sessionLoadTime);

	long getSessionLoadTime();

	void addHandlerTime(String name, long executionTime);

    Map<String, Long> getHandlerNameToProcessingTime();

	void setUnAccountedQuota(QuotaReservation unAccountedReservation);

	SubscriberNonMonitoryBalance getAccountedNonMonetaryBalance();

	void setAccountedNonMonetaryBalance(SubscriberNonMonitoryBalance subscriberNonMonitoryBalance);

	SubscriberRnCNonMonetaryBalance getAccountedRnCNonMonetaryBalance();

	void setAccountedRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance);

	SubscriberMonetaryBalance getAccountedMonetaryBalance();

	void setAccountedMonetaryBalance(SubscriberMonetaryBalance subscriberMonetaryBalance);

	boolean isQuotaReservationChanged();

	void setQuotaReservationChanged(boolean isQuotaReservationChanged);

	ReportSummary getReportSummary();

	void setReportSummary(ReportSummary reportSummary);

	long getSubscriptionsReadTime();

	PCRFResponseImpl setSubscriptionsReadTime(long subscriptionsReadTime);

	long getUsageReadTime();

	PCRFResponseImpl setUsageReadTime(long usageReadTime);

	long getSPRReadTime();

	PCRFResponseImpl setSPRReadTime(long sprReadTime);

	void addDiagnosticInformation(String key, String value);

	Map<String, String> getDiagnosticInformation();

	SimpleDateFormat getChargingCDRDateFormat();
	void setChargingCDRDateFormat(SimpleDateFormat dateFormat);

}
