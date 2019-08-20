package com.elitecore.nvsmx.ws.interceptor;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSAddAlternateIdEntry;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeBaseProductOfferEntry;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeImsPackageEntry;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSChangeImsPackageTable;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.NETVERTEX_PCRF_MIBImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.NetvertexWebServiceStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.TableWebServiceResultCodeWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.WebServiceResultCodeWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.ResetUsageWSMethodWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.ResetUsageWSResetBillingCycleResultCodeEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.ResetUsageWSResultCodeWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.ResetUsageWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.TableResetUsageWSMethodWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.TableResetUsageWSResetBillingCycleTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.TableResetUsageWSResultCodeWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionManagementWSMethodWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionManagementWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionWSGetSessionsByIPEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionWSGetSessionsBySubscriberIDEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionWSReauthSessionBySubscriberIDEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionWSReauthSessionsByCoreSessionIDEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionWSResultCodeWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSGetSessionsByIPTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSGetSessionsBySubscriberIDTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSMethodWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSReauthSessionBySubscriberIDTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSReauthSessionsByCoreSessionIDTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.TableSessionManagementWSResultCodeWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberProvisioningWSMethodWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberProvisioningWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSAddAlternateIdEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSAddSubscriberBulkEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSAddSubscriberEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSAddSubscriberProfileEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSAddSubscribersEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSChangeAlternateIdStatusEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSChangeBaseProductOfferEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSChangeImsPackageEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSDeleteAlternateIdEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSDeleteSubscriberProfileEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSDeleteSubscriberProfilesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSGetAlternateIdEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSGetSubscriberProfileByIDEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSListSubscriberProfilesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSMigrateSubscriberEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSPurgeAllSubscriberEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSPurgeSubscriberEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSPurgeSubscribersEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSRestoreSubscriberEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSRestoreSubscribersEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSResultCodeWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSUpdateAlternateIdEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberWSUpdateSubscriberProfileEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSAddAlternateIdTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSChangeAlternateIdStatusTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSChangeBaseProductOfferTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSChangeImsPackageTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSDeleteAlternateIdTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSGetAlternateIdTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSMethodWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSResultCodeWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberProvisioningWSUpdateAlternateIdTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSAddSubscriberBulkTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSAddSubscriberProfileTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSAddSubscriberTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSAddSubscribersTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSDeleteSubscriberProfileTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSDeleteSubscriberProfilesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSGetSubscriberProfileByIDTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSListSubscriberProfilesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSMigrateSubscriberTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSPurgeAllSubscriberTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSPurgeSubscriberTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSPurgeSubscribersTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSRestoreSubscriberTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSRestoreSubscribersTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.TableSubscriberWSUpdateSubscriberProfileTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionFnFGroupMembersEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSAddMonetaryBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSChangeBillDayEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSChangeDataAddOnSubscriptionEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSChangeRnCAddOnSubscriptionEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSGetBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSGetMonetaryBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSGetNonMonetaryBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSGetRnCBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListAddOnSubscriptionsEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListBoDPackagesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListDataPackagesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListPackagesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListRnCPackagesEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSListUsageMonitoringInformationEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSMethodWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSResultCodeWiseEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSSubscribeAddOnProductOfferEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSSubscribeBoDEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSSubscribeMonetaryRechargePlanEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSUpdateCreditLimitEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSUpdateMonetaryBalanceEntryImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionFnFGroupMembersTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSAddMonetaryBalanceTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSChangeBillDayTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSChangeDataAddOnSubscriptionTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSGetBalanceTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSGetMonetaryBalanceTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSGetNonMonetaryBalanceTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSGetRnCBalanceTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListAddOnSubscriptionsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListBoDPackagesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListDataPackagesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListPackagesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListRnCPackagesTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSListUsageMonitoringInformationTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSMethodWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSResultCodeWiseStatisticsTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSSubscribeAddOnProductOfferTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSSubscribeBoDTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSSubscribeMonetaryRechargePlanTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSUpdateCreditLimitTableImpl;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.TableSubscriptionWSUpdateMonetaryBalanceTableImpl;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import com.elitecore.nvsmx.ws.resetusage.ResetUsageWS;
import com.elitecore.nvsmx.ws.sessionmanagement.ISessionManagementWS;
import com.elitecore.nvsmx.ws.subscriberprovisioning.ISubscriberProvisioningWS;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.RESTSubscriberProvisioningWS;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.elitecore.nvsmx.ws.subscription.SubscriptionRestWS;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;
/**
 * @author kirpalsinh.raj
 *
 * This is a managing class which manage the web service counter and works as Interceptor to increments the counters based on request and responses.
 * It prepares following counters.
 *
 * 1) Global Counters : It include counters for all Web-Services all methods
 *
 * 2) Web-Service Counter : Web-Service module specific counters. i.e if SubscriptionWS counters for all its methods.
 * 	  method : incrementWebServiceResponseCounters(), incrementWebServiceRequestCounters()
 *
 * 3) Counters for Web-Service method: preparing counters for particular method of respective Web-Service module.
 * 	  method : incrementWebServiceMethodResponseCounters(), incrementWebServiceMethodRequestCounters()
 */

public class WebServiceStatisticsManager implements WebServiceInterceptor {

	private static final String HASH_SIGN = "#";

	private static final String MODULE = "WS-STATISTICS-MANAGER";

	public static final int STATS_OTHER 	= 1;
	public static final int STATS_RESET 	= 2;
	public static final int STATS_INITIALIZING = 3;
	public static final int STATS_RUNNING 		= 4;
	private int statisticsReset = STATS_OTHER;

	private long statisicsLastResetTime;
	private long webServiceUpTime;

	private WebServiceStatistics globalStatistics = new WebServiceStatistics();
	private ConcurrentHashMap<String,WebServiceStatistics> moduleAndMethodstatistics = new ConcurrentHashMap<String,WebServiceStatistics>();
	private static WebServiceStatisticsManager manager = new WebServiceStatisticsManager();

	private AtomicInteger resetUsageMethodIndex  	= new AtomicInteger(0);
	private AtomicInteger sessionMethodIndex 		= new AtomicInteger(0);
	private AtomicInteger subcriberMethodIndex  	= new AtomicInteger(0);
	private AtomicInteger subscriptionMethodIndex	= new AtomicInteger(0);

	private TableResetUsageWSMethodWiseStatisticsTableImpl 				resetUsageTableImpl;
	private TableSessionManagementWSMethodWiseStatisticsTableImpl 		sessionTableImpl;
	private TableSubscriberProvisioningWSMethodWiseStatisticsTableImpl 	subscriberTableImpl;
	private TableSubscriptionWSMethodWiseStatisticsTableImpl 			subscriptionTableImpl;

	private NetvertexWebServiceStatisticsProvider 		netvertexWSStatisticsProvider;
	private ResetUsageWSStatisticsProvider 				resetUsageWSStatisticsProvider;
	private SubscriberProvisioningWSStatisticsProvider 	subscriberWSStatisticsProvider;
	private SubscriptionWSStatisticsProvider 			subscriptionWSStatisticsProvider;
	private SessionManagementWSStatisticsProvider 		sessionWSStatisticsProvider;

	private TableWebServiceResultCodeWiseStatisticsTableImpl 				globalWSResultCodeWiseTableImpl;
	private TableSubscriberProvisioningWSResultCodeWiseStatisticsTableImpl 	subscriberWSResultCodeWiseTableImpl;
	private TableSubscriptionWSResultCodeWiseStatisticsTableImpl 			subscriptionWSResultCodeWiseTableImpl;
	private TableSessionManagementWSResultCodeWiseStatisticsTableImpl 		sessionWSResultCodeWiseTableImpl;
	private TableResetUsageWSResultCodeWiseStatisticsTableImpl 				resetUsageWSResultCodeWiseTableImpl;

	private NETVERTEX_PCRF_MIBImpl netvertexPCRFMib;
	private MBeanServer mBeanServer;

	private WebServiceStatisticsManager(){

		statisticsReset = STATS_INITIALIZING;
		webServiceUpTime = System.currentTimeMillis();

		netvertexWSStatisticsProvider 	 = new NetvertexWebServiceStatisticsProvider(globalStatistics);
		sessionWSStatisticsProvider 	 = new SessionManagementWSStatisticsProvider(new NullWSStatistics());
		subscriberWSStatisticsProvider 	 = new SubscriberProvisioningWSStatisticsProvider(new NullWSStatistics());
		subscriptionWSStatisticsProvider = new SubscriptionWSStatisticsProvider(new NullWSStatistics());
		resetUsageWSStatisticsProvider 	 = new ResetUsageWSStatisticsProvider(new NullWSStatistics());

		netvertexPCRFMib = new NETVERTEX_PCRF_MIBImpl( 	netvertexWSStatisticsProvider,
														resetUsageWSStatisticsProvider,
														sessionWSStatisticsProvider,
														subscriberWSStatisticsProvider,
														subscriptionWSStatisticsProvider );

		mBeanServer = ManagementFactory.getPlatformMBeanServer();

		try {
			netvertexPCRFMib.populate(mBeanServer, null);
		} catch (Exception e) {
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to polulate server with the mib. Reason: "+e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		sessionTableImpl 		= new TableSessionManagementWSMethodWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		subscriberTableImpl 	= new TableSubscriberProvisioningWSMethodWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		subscriptionTableImpl 	= new TableSubscriptionWSMethodWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		resetUsageTableImpl 	= new TableResetUsageWSMethodWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);

		globalWSResultCodeWiseTableImpl  		= new TableWebServiceResultCodeWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		resetUsageWSResultCodeWiseTableImpl 	= new TableResetUsageWSResultCodeWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		subscriberWSResultCodeWiseTableImpl 	= new TableSubscriberProvisioningWSResultCodeWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		subscriptionWSResultCodeWiseTableImpl 	= new TableSubscriptionWSResultCodeWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);
		sessionWSResultCodeWiseTableImpl 		= new TableSessionManagementWSResultCodeWiseStatisticsTableImpl(netvertexPCRFMib, mBeanServer);

 		addGlobalResultCodeEntriesInSNMPTable();

		EliteScheduler.getInstance().TASK_SCHEDULER.scheduleIntervalBasedTask(new TPMCalculator(globalStatistics,"GLOBAL"));
		statisticsReset = STATS_RUNNING;
	}

	private void addGlobalResultCodeEntriesInSNMPTable() {
		try{
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Adding Global ResultCode entries into SNMP Table.");
			}
			for(ResultCode resultCode : ResultCode.values()){
				WebServiceResultCodeWiseEntryImpl entry = new WebServiceResultCodeWiseEntryImpl(resultCode.code, globalStatistics);
				globalWSResultCodeWiseTableImpl.addEntry(entry);
			}
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Global ResultCode entries Successfully added into SNMP Table.");
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add Global ResultCode entries into SNMP Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	public NETVERTEX_PCRF_MIBImpl getNetvertex_PCRF_MIBImpl(){
		return netvertexPCRFMib;
	}

	public static WebServiceStatisticsManager getInstance(){
		return manager;
	}

	private WebServiceStatistics getWebServiceStatistics(String moduleOrMethodName){

			if( Strings.isNullOrBlank( moduleOrMethodName ) == true ){
				return null;
			}

			WebServiceStatistics  webServiceStatistics = moduleAndMethodstatistics.get(moduleOrMethodName);

			if( webServiceStatistics == null ){

				webServiceStatistics =  new WebServiceStatistics();
				WebServiceStatistics  previousWebServiceStatistics = moduleAndMethodstatistics.putIfAbsent(moduleOrMethodName, webServiceStatistics);

				if(previousWebServiceStatistics != null) {
					webServiceStatistics = previousWebServiceStatistics;

				} else {

					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Registering WebServiceStatistics with Scheduler for '"+moduleOrMethodName+"'");
					}
					EliteScheduler.getInstance().TASK_SCHEDULER.scheduleIntervalBasedTask(new TPMCalculator(webServiceStatistics,moduleOrMethodName));
					registerWebStatisticsWithSNMPProviders(moduleOrMethodName,webServiceStatistics);
				}
			}
		return webServiceStatistics;
	}

	private void registerWebStatisticsWithSNMPProviders( String moduleOrMethodName, WebServiceStatistics webServiceStatistics) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Registering WebServiceStatistics of '"+moduleOrMethodName+"' with respective SNMP provider");
		}

		if( WebServiceModule.fromName(moduleOrMethodName) != null ){
			try{
				switch(WebServiceModule.fromName(moduleOrMethodName)){
					case SESSION_MANAGEMENT_WS:
						sessionWSStatisticsProvider.setWebServiceStatistics(webServiceStatistics);
						addSessionWSResultCodeWiseEntryIntoSNMPTable(webServiceStatistics);
						break;

					case SUBSCRIBER_PROVISIONING_WS:
						subscriberWSStatisticsProvider.setWebServiceStatistics(webServiceStatistics);
						addSubscriberWSResultCodeWiseEntryIntoSNMPTable(webServiceStatistics);
						break;

					case SUBSCRIPTION_WS:
						subscriptionWSStatisticsProvider.setWebServiceStatistics(webServiceStatistics);
						addSubscriptionWSResultCodeWiseEntryIntoSNMPTable(webServiceStatistics);
						break;

					case RESET_USAGE_WS:
						resetUsageWSStatisticsProvider.setWebServiceStatistics(webServiceStatistics);
						addResetUsageWSResultCodeWiseEntryIntoSNMPTable(webServiceStatistics);
						break;
					}
			}catch(Exception ex){
				if (getLogger().isErrorLogLevel()) {
					getLogger().error(MODULE, "Error while preparing SNMP provider for '"+moduleOrMethodName+"'. Reason: "+ex.getMessage());
					getLogger().trace(MODULE, ex);
				}
			}
		}else{
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Registering Method specific WebServiceStatistics of '"+moduleOrMethodName+"' with respective SNMP provider's Table");
				}

				try{

					if (moduleOrMethodName.contains(WebServiceModule.SESSION_MANAGEMENT_WS.getName()+HASH_SIGN)) {

						SessionManagementWSMethodWiseEntryImpl methodEntry = new SessionManagementWSMethodWiseEntryImpl(sessionMethodIndex.incrementAndGet(), moduleOrMethodName, webServiceStatistics);
						sessionTableImpl.addEntry(methodEntry);

						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Entry for: " + moduleOrMethodName + " added successfully in SNMP Session table." );
						}

						addSessionWSMethodWiseEntryIntoSNMPTable( moduleOrMethodName, webServiceStatistics);

					}else if (moduleOrMethodName.contains(WebServiceModule.SUBSCRIBER_PROVISIONING_WS.getName()+HASH_SIGN)) {

						SubscriberProvisioningWSMethodWiseEntryImpl methodEntry = new SubscriberProvisioningWSMethodWiseEntryImpl(subcriberMethodIndex.incrementAndGet(), moduleOrMethodName, webServiceStatistics);
						subscriberTableImpl.addEntry(methodEntry);

						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Entry for: " + moduleOrMethodName + " added successfully in SNMP Subscriber table." );
						}

						addSubscriberWSMethodWiseResultCodeEntryIntoSNMPTable(moduleOrMethodName, webServiceStatistics);

					}else if (moduleOrMethodName.contains(WebServiceModule.SUBSCRIPTION_WS.getName()+HASH_SIGN)) {

						SubscriptionWSMethodWiseEntryImpl methodEntry = new SubscriptionWSMethodWiseEntryImpl(subscriptionMethodIndex.incrementAndGet(), moduleOrMethodName, webServiceStatistics);
						subscriptionTableImpl.addEntry(methodEntry);

						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Entry for: " + moduleOrMethodName + " added successfully in SNMP Subscription table." );
						}

						addSubscriptionWSMethodWiseResultCodeEntryIntoSNMPTable(moduleOrMethodName, webServiceStatistics);

					}else if(moduleOrMethodName.contains(WebServiceModule.RESET_USAGE_WS.getName()+HASH_SIGN)){

						ResetUsageWSMethodWiseEntryImpl methodEntry =  new ResetUsageWSMethodWiseEntryImpl(resetUsageMethodIndex.incrementAndGet(), moduleOrMethodName, webServiceStatistics);
						resetUsageTableImpl.addEntry(methodEntry);

						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Entry for: " + moduleOrMethodName + " added successfully in SNMP ResetUsage table." );
						}

						String methodName = moduleOrMethodName.split(HASH_SIGN)[1];
						if(methodName.equalsIgnoreCase("wsResetBillingCycle")){
							addResetUsageWSResetBillingCycleResultCodeEntryIntoSNMPTable(webServiceStatistics);
						}

					}else{
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "No module or method matched. Invalid WebServiceStatistics of '"+moduleOrMethodName+"' found");
						}
					}
				}catch(Exception ex){
					if (getLogger().isErrorLogLevel()) {
						getLogger().error(MODULE, "Error while adding Method Entry '"+moduleOrMethodName+"' into its Table. Reason: "+ex.getMessage());
						getLogger().trace(MODULE, ex);
					}
				}
			}
	}

	private void addSessionWSMethodWiseEntryIntoSNMPTable(String moduleOrMethodName, WebServiceStatistics webServiceStatistics) {
		try{

			String methodName = moduleOrMethodName.split(HASH_SIGN)[1];

			if(methodName.equalsIgnoreCase(ISessionManagementWS.WS_GET_SESSION_BY_IP)){ //
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISessionManagementWS.WS_GET_SESSION_BY_IP+"' Entries in its SNMP table." );
				}

				TableSessionManagementWSGetSessionsByIPTableImpl table = new TableSessionManagementWSGetSessionsByIPTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SessionWSGetSessionsByIPEntryImpl entry = new SessionWSGetSessionsByIPEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SessionManagement-WS Method '"+ISessionManagementWS.WS_GET_SESSION_BY_IP+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISessionManagementWS.WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISessionManagementWS.WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY+"' Entries in its SNMP table." );
				}

				TableSessionManagementWSGetSessionsBySubscriberIDTableImpl table = new TableSessionManagementWSGetSessionsBySubscriberIDTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SessionWSGetSessionsBySubscriberIDEntryImpl entry = new SessionWSGetSessionsBySubscriberIDEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SessionManagement-WS Method '"+ISessionManagementWS.WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISessionManagementWS.WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISessionManagementWS.WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY+"' Entries in its SNMP table." );
				}

				TableSessionManagementWSReauthSessionBySubscriberIDTableImpl table = new TableSessionManagementWSReauthSessionBySubscriberIDTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SessionWSReauthSessionBySubscriberIDEntryImpl entry = new SessionWSReauthSessionBySubscriberIDEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SessionManagement-WS Method '"+ISessionManagementWS.WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISessionManagementWS.WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISessionManagementWS.WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID+"' Entries in its SNMP table." );
				}

				TableSessionManagementWSReauthSessionsByCoreSessionIDTableImpl table = new TableSessionManagementWSReauthSessionsByCoreSessionIDTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SessionWSReauthSessionsByCoreSessionIDEntryImpl entry = new SessionWSReauthSessionsByCoreSessionIDEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SessionManagement-WS Method '"+ISessionManagementWS.WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID+"' Entries added successfully in its SNMP table." );
				}

			}

		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add SessionManagement-WS 'Method wise ResultCode' Entries into SNMP Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addSessionWSResultCodeWiseEntryIntoSNMPTable(WebServiceStatistics webServiceStatistics) {
		try{
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Adding SessionManagement-WS 'ResultCode Wise' Entries in SNMP SessionManagement table." );
			}
			for(ResultCode resultCode : ResultCode.values()){
				SessionWSResultCodeWiseEntryImpl entry = new SessionWSResultCodeWiseEntryImpl(resultCode.code, webServiceStatistics);
				sessionWSResultCodeWiseTableImpl.addEntry(entry);
			}
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "SessionManagement-WS 'ResultCode Wise' Entries added successfully in SNMP SessionManagement table." );
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add SessionManagement-WS 'ResultCode wise' Entries into SNMP Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addSubscriberWSResultCodeWiseEntryIntoSNMPTable(WebServiceStatistics webServiceStatistics) {
		try{
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Adding SubscriberProvisioning-WS 'ResultCode Wise' Entries in SNMP's SubscriberProvisioning table." );
			}
			for(ResultCode resultCode : ResultCode.values()){
				SubscriberWSResultCodeWiseEntryImpl entry = new SubscriberWSResultCodeWiseEntryImpl(resultCode.code,webServiceStatistics);
				subscriberWSResultCodeWiseTableImpl.addEntry(entry);
			}
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS 'ResultCode Wise' Entries added successfully in SNMP's SubscriberProvisioning table." );
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add SubscriberProvisioning-WS 'ResultCode Wise' Entries into SNMP's SubscriberProvisioning Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addSubscriberWSMethodWiseResultCodeEntryIntoSNMPTable(String moduleAndMethodName, WebServiceStatistics webServiceStatistics) {
		try{

			String methodName = moduleAndMethodName.split(HASH_SIGN)[1];

			if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_PROFILE)){
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_PROFILE+"' Entries in its SNMP table." );
				}

				TableSubscriberWSAddSubscriberProfileTableImpl table = new TableSubscriberWSAddSubscriberProfileTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSAddSubscriberProfileEntryImpl entry = new SubscriberWSAddSubscriberProfileEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_PROFILE+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_UPDATE_SUBSCRIBER_PROFILE)){
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_UPDATE_SUBSCRIBER_PROFILE+"' Entries in its SNMP table." );
				}

				TableSubscriberWSUpdateSubscriberProfileTableImpl table = new TableSubscriberWSUpdateSubscriberProfileTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSUpdateSubscriberProfileEntryImpl entry = new SubscriberWSUpdateSubscriberProfileEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_UPDATE_SUBSCRIBER_PROFILE+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILE)){
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILE+"' Entries in its SNMP table." );
				}

				TableSubscriberWSDeleteSubscriberProfileTableImpl table = new TableSubscriberWSDeleteSubscriberProfileTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSDeleteSubscriberProfileEntryImpl entry = new SubscriberWSDeleteSubscriberProfileEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILE+"' Entries added successfully in its SNMP table." );
				}

			}  else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILES)){
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILES+"' Entries in its SNMP table." );
				}

				TableSubscriberWSDeleteSubscriberProfilesTableImpl table = new TableSubscriberWSDeleteSubscriberProfilesTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSDeleteSubscriberProfilesEntryImpl entry = new SubscriberWSDeleteSubscriberProfilesEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_DELETE_SUBSCRIBER_PROFILES+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_BULK)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_BULK+"' Entries in its SNMP table." );
				}

				TableSubscriberWSAddSubscriberBulkTableImpl table = new TableSubscriberWSAddSubscriberBulkTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSAddSubscriberBulkEntryImpl entry = new SubscriberWSAddSubscriberBulkEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER_BULK+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_GET_SUBSCRIBER_PROFILE_BY_ID)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_GET_SUBSCRIBER_PROFILE_BY_ID+"' Entries in its SNMP table." );
				}

				TableSubscriberWSGetSubscriberProfileByIDTableImpl table = new TableSubscriberWSGetSubscriberProfileByIDTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSGetSubscriberProfileByIDEntryImpl entry = new SubscriberWSGetSubscriberProfileByIDEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_GET_SUBSCRIBER_PROFILE_BY_ID+"' Entries added successfully in its SNMP table." );
				}

			}  else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_LIST_SUBSCRIBER_PROFILES)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_LIST_SUBSCRIBER_PROFILES+"' Entries in its SNMP table." );
				}

				TableSubscriberWSListSubscriberProfilesTableImpl table = new TableSubscriberWSListSubscriberProfilesTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSListSubscriberProfilesEntryImpl entry = new SubscriberWSListSubscriberProfilesEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_LIST_SUBSCRIBER_PROFILES+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBER)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBER+"' Entries in its SNMP table." );
				}

				TableSubscriberWSPurgeSubscriberTableImpl table = new TableSubscriberWSPurgeSubscriberTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSPurgeSubscriberEntryImpl entry = new SubscriberWSPurgeSubscriberEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBER+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBERS)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBERS+"' Entries in its SNMP table." );
				}

				TableSubscriberWSPurgeSubscribersTableImpl table = new TableSubscriberWSPurgeSubscribersTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSPurgeSubscribersEntryImpl entry = new SubscriberWSPurgeSubscribersEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_PURGE_SUBSCRIBERS+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_PURGE_ALL_SUBSCRIBER)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_PURGE_ALL_SUBSCRIBER+"' Entries in its SNMP table." );
				}

				TableSubscriberWSPurgeAllSubscriberTableImpl table = new TableSubscriberWSPurgeAllSubscriberTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSPurgeAllSubscriberEntryImpl entry = new SubscriberWSPurgeAllSubscriberEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_PURGE_ALL_SUBSCRIBER+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBER)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBER+"' Entries in its SNMP table." );
				}

				TableSubscriberWSRestoreSubscriberTableImpl table = new TableSubscriberWSRestoreSubscriberTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSRestoreSubscriberEntryImpl entry = new SubscriberWSRestoreSubscriberEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBER+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBERS)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBERS+"' Entries in its SNMP table." );
				}

				TableSubscriberWSRestoreSubscribersTableImpl table = new TableSubscriberWSRestoreSubscribersTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSRestoreSubscribersEntryImpl entry = new SubscriberWSRestoreSubscribersEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_RESTORE_SUBSCRIBERS+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_MIGRATE_SUBSCRIBER)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_MIGRATE_SUBSCRIBER+"' Entries in its SNMP table." );
				}

				TableSubscriberWSMigrateSubscriberTableImpl table = new TableSubscriberWSMigrateSubscriberTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSMigrateSubscriberEntryImpl entry = new SubscriberWSMigrateSubscriberEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_MIGRATE_SUBSCRIBER+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER+"' Entries in its SNMP table." );
				}

				TableSubscriberWSAddSubscriberTableImpl table = new TableSubscriberWSAddSubscriberTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSAddSubscriberEntryImpl entry = new SubscriberWSAddSubscriberEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBER+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_ADD_SUBSCRIBERS)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBERS+"' Entries in its SNMP table." );
				}

				TableSubscriberWSAddSubscribersTableImpl table = new TableSubscriberWSAddSubscribersTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSAddSubscribersEntryImpl entry = new SubscriberWSAddSubscribersEntryImpl(resultCode.code, webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBERS+"' Entries added successfully in its SNMP table." );
				}

			} else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_CHANGE_IMS_PACKAGE)){

                printDebugLogForPreprocess(ISubscriberProvisioningWS.WS_CHANGE_IMS_PACKAGE);

                TableSubscriberProvisioningWSChangeImsPackageTable table = new TableSubscriberProvisioningWSChangeImsPackageTableImpl(netvertexPCRFMib, mBeanServer);
                for(ResultCode resultCode : ResultCode.values()){
                    SubscriberWSChangeImsPackageEntry entry = new SubscriberWSChangeImsPackageEntryImpl(resultCode, webServiceStatistics);
                    table.addEntry(entry);
                }

                printDebugLogForPostprocess(ISubscriberProvisioningWS.WS_CHANGE_BASE_PRODUCT_OFFER);
            } else if(methodName.equalsIgnoreCase(ISubscriberProvisioningWS.WS_CHANGE_BASE_PRODUCT_OFFER)){

                printDebugLogForPreprocess(ISubscriberProvisioningWS.WS_CHANGE_BASE_PRODUCT_OFFER);

                TableSubscriberProvisioningWSChangeBaseProductOfferTableImpl table = new TableSubscriberProvisioningWSChangeBaseProductOfferTableImpl(netvertexPCRFMib, mBeanServer);
                for(ResultCode resultCode : ResultCode.values()){
                    SubscriberWSChangeBaseProductOfferEntry entry = new SubscriberWSChangeBaseProductOfferEntryImpl(resultCode, webServiceStatistics);
                    table.addEntry(entry);
                }

                printDebugLogForPostprocess(ISubscriberProvisioningWS.WS_CHANGE_BASE_PRODUCT_OFFER);
            } else if(methodName.equalsIgnoreCase(RESTSubscriberProvisioningWS.WS_ADD_ALTERNATEID)){

				printDebugLogForPreprocess(RESTSubscriberProvisioningWS.WS_ADD_ALTERNATEID);

				TableSubscriberProvisioningWSAddAlternateIdTableImpl table = new TableSubscriberProvisioningWSAddAlternateIdTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSAddAlternateIdEntry entry = new SubscriberWSAddAlternateIdEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(RESTSubscriberProvisioningWS.WS_ADD_ALTERNATEID);
			} else if(methodName.equalsIgnoreCase(RESTSubscriberProvisioningWS.WS_GET_ALTERNATE_ID)){

				printDebugLogForPreprocess(RESTSubscriberProvisioningWS.WS_GET_ALTERNATE_ID);

				TableSubscriberProvisioningWSGetAlternateIdTableImpl table = new TableSubscriberProvisioningWSGetAlternateIdTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSGetAlternateIdEntryImpl entry = new SubscriberWSGetAlternateIdEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(RESTSubscriberProvisioningWS.WS_GET_ALTERNATE_ID);
			} else if(methodName.equalsIgnoreCase(RESTSubscriberProvisioningWS.WS_CHANGE_ALTERNATEID_STATUS)){

				printDebugLogForPreprocess(RESTSubscriberProvisioningWS.WS_CHANGE_ALTERNATEID_STATUS);

				TableSubscriberProvisioningWSChangeAlternateIdStatusTableImpl table = new TableSubscriberProvisioningWSChangeAlternateIdStatusTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSChangeAlternateIdStatusEntryImpl entry = new SubscriberWSChangeAlternateIdStatusEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(RESTSubscriberProvisioningWS.WS_CHANGE_ALTERNATEID_STATUS);
			} else if(methodName.equalsIgnoreCase(RESTSubscriberProvisioningWS.WS_UPDATE_ALTERNATEID)){

				printDebugLogForPreprocess(RESTSubscriberProvisioningWS.WS_UPDATE_ALTERNATEID);

				TableSubscriberProvisioningWSUpdateAlternateIdTableImpl table = new TableSubscriberProvisioningWSUpdateAlternateIdTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSUpdateAlternateIdEntryImpl entry = new SubscriberWSUpdateAlternateIdEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(RESTSubscriberProvisioningWS.WS_UPDATE_ALTERNATEID);
			} else if(methodName.equalsIgnoreCase(RESTSubscriberProvisioningWS.WS_DELETE_ALTERNATE_ID)){

				printDebugLogForPreprocess(RESTSubscriberProvisioningWS.WS_DELETE_ALTERNATE_ID);

				TableSubscriberProvisioningWSDeleteAlternateIdTableImpl table = new TableSubscriberProvisioningWSDeleteAlternateIdTableImpl(netvertexPCRFMib, mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriberWSDeleteAlternateIdEntryImpl entry = new SubscriberWSDeleteAlternateIdEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(RESTSubscriberProvisioningWS.WS_DELETE_ALTERNATE_ID);
			} else {
				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '"+ISubscriberProvisioningWS.WS_ADD_SUBSCRIBERS+"' Not Found" );
				}
			}

		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add SubscriberProvisioning-WS 'Method wise ResultCode' Entries in its SNMP Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

    private void printDebugLogForPostprocess(String wsName) {
        if (getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "SubscriberProvisioning-WS method '" + wsName + "' Entries added successfully in its SNMP table.");
        }
    }

    private void printDebugLogForPreprocess(String wsName) {
        if (getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Adding '" + wsName + "' Entries in its SNMP table.");
        }
    }

    private void addSubscriptionWSMethodWiseResultCodeEntryIntoSNMPTable(String moduleOrMethodName, WebServiceStatistics webServiceStatistics) {
		try{

			 String methodName = moduleOrMethodName.split(HASH_SIGN)[1];
			 if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_GET_BALANCE)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriptionWS.WS_GET_BALANCE+"' Entries in its SNMP table." );
				}

				TableSubscriptionWSGetBalanceTableImpl table = new TableSubscriptionWSGetBalanceTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSGetBalanceEntryImpl entry = new SubscriptionWSGetBalanceEntryImpl( resultCode.code , webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Subscription-WS method '"+ISubscriptionWS.WS_GET_BALANCE+"' Entries added successfully in its SNMP table." );
				}

			}else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_PACKAGES)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriptionWS.WS_LIST_PACKAGES+"' Entries in its SNMP table." );
				}

				TableSubscriptionWSListPackagesTableImpl table = new TableSubscriptionWSListPackagesTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSListPackagesEntryImpl entry = new SubscriptionWSListPackagesEntryImpl( resultCode.code , webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Subscription-WS method '"+ISubscriptionWS.WS_LIST_PACKAGES+"' Entries added successfully in its SNMP table." );
				}
			}else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_DATA_PACKAGES)){

				 if (getLogger().isDebugLogLevel()) {
					 LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriptionWS.WS_LIST_DATA_PACKAGES+"' Entries in its SNMP table." );
				 }

				 TableSubscriptionWSListDataPackagesTableImpl table = new TableSubscriptionWSListDataPackagesTableImpl(netvertexPCRFMib,mBeanServer);
				 for(ResultCode resultCode : ResultCode.values()){
					 SubscriptionWSListDataPackagesEntryImpl entry = new SubscriptionWSListDataPackagesEntryImpl( resultCode , webServiceStatistics);
					 table.addEntry(entry);
				 }

				 if (getLogger().isDebugLogLevel()) {
					 LogManager.getLogger().debug(MODULE, "Subscription-WS method '"+ISubscriptionWS.WS_LIST_PACKAGES+"' Entries added successfully in its SNMP table." );
				 }
			 }else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_ADD_ON_SUBSCRIPTIONS)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriptionWS.WS_LIST_ADD_ON_SUBSCRIPTIONS+"' Entries in its SNMP table." );
				}

				TableSubscriptionWSListAddOnSubscriptionsTableImpl table = new TableSubscriptionWSListAddOnSubscriptionsTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSListAddOnSubscriptionsEntryImpl entry = new SubscriptionWSListAddOnSubscriptionsEntryImpl( resultCode.code , webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Subscription-WS method '"+ISubscriptionWS.WS_LIST_ADD_ON_SUBSCRIPTIONS+"' Entries added successfully in its SNMP table." );
				}
			}else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION)){

				printDebugLogForPreprocess(ISubscriptionWS.WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);

				TableSubscriptionWSChangeDataAddOnSubscriptionTableImpl table = new TableSubscriptionWSChangeDataAddOnSubscriptionTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSChangeDataAddOnSubscriptionEntryImpl entry = new SubscriptionWSChangeDataAddOnSubscriptionEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(ISubscriptionWS.WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);

			}else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_SUBSCRIBE_ADD_ON)){

				printDebugLogForPreprocess(ISubscriptionWS.WS_SUBSCRIBE_ADD_ON);

				TableSubscriptionWSSubscribeAddOnProductOfferTableImpl table = new TableSubscriptionWSSubscribeAddOnProductOfferTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSSubscribeAddOnProductOfferEntryImpl entry = new SubscriptionWSSubscribeAddOnProductOfferEntryImpl(resultCode, webServiceStatistics);
					table.addEntry(entry);
				}

				printDebugLogForPostprocess(ISubscriptionWS.WS_SUBSCRIBE_ADD_ON);

			}else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_USAGE_MONITORING_INFORMATION)){

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Adding '"+ISubscriptionWS.WS_LIST_USAGE_MONITORING_INFORMATION+"' Entries in its SNMP table." );
				}

				TableSubscriptionWSListUsageMonitoringInformationTableImpl table = new TableSubscriptionWSListUsageMonitoringInformationTableImpl(netvertexPCRFMib,mBeanServer);
				for(ResultCode resultCode : ResultCode.values()){
					SubscriptionWSListUsageMonitoringInformationEntryImpl entry = new SubscriptionWSListUsageMonitoringInformationEntryImpl( resultCode.code , webServiceStatistics);
					table.addEntry(entry);
				}

				if (getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Subscription-WS method '"+ISubscriptionWS.WS_LIST_USAGE_MONITORING_INFORMATION+"' Entries added successfully in its SNMP table." );
				}
			} else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_ADD_MONETARY_BALANCE)){

                 printDebugLogForPreprocess(ISubscriptionWS.WS_ADD_MONETARY_BALANCE);

                 TableSubscriptionWSAddMonetaryBalanceTableImpl table = new TableSubscriptionWSAddMonetaryBalanceTableImpl(netvertexPCRFMib,mBeanServer);
                 for(ResultCode resultCode : ResultCode.values()){
                     SubscriptionWSAddMonetaryBalanceEntryImpl entry = new SubscriptionWSAddMonetaryBalanceEntryImpl(resultCode , webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPreprocess(ISubscriptionWS.WS_ADD_MONETARY_BALANCE);
             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_GET_RNC_BALANCE)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_GET_RNC_BALANCE);

                 TableSubscriptionWSGetRnCBalanceTableImpl table = new TableSubscriptionWSGetRnCBalanceTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSGetRnCBalanceEntryImpl entry = new SubscriptionWSGetRnCBalanceEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_GET_RNC_BALANCE);

             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION);

                 TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl table = new TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSChangeRnCAddOnSubscriptionEntryImpl entry = new SubscriptionWSChangeRnCAddOnSubscriptionEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION);

             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_GET_MONETARY_BALANCE)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_GET_MONETARY_BALANCE);

                 TableSubscriptionWSGetMonetaryBalanceTableImpl table = new TableSubscriptionWSGetMonetaryBalanceTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSGetMonetaryBalanceEntryImpl entry = new SubscriptionWSGetMonetaryBalanceEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_GET_MONETARY_BALANCE);

             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_UPDATE_MONETARY_BALANCE)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_UPDATE_MONETARY_BALANCE);

                 TableSubscriptionWSUpdateMonetaryBalanceTableImpl table = new TableSubscriptionWSUpdateMonetaryBalanceTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSUpdateMonetaryBalanceEntryImpl entry = new SubscriptionWSUpdateMonetaryBalanceEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_UPDATE_MONETARY_BALANCE);
             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_GET_NONMONITORY_BALANCE)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_GET_NONMONITORY_BALANCE);

                 TableSubscriptionWSGetNonMonetaryBalanceTableImpl table = new TableSubscriptionWSGetNonMonetaryBalanceTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSGetNonMonetaryBalanceEntryImpl entry = new SubscriptionWSGetNonMonetaryBalanceEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_GET_NONMONITORY_BALANCE);
             } else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_RNC_PACKAGES)) {

                 printDebugLogForPreprocess(ISubscriptionWS.WS_LIST_RNC_PACKAGES);

                 TableSubscriptionWSListRnCPackagesTableImpl table = new TableSubscriptionWSListRnCPackagesTableImpl(netvertexPCRFMib, mBeanServer);
                 for (ResultCode resultCode : ResultCode.values()) {
                     SubscriptionWSListRnCPackagesEntryImpl entry = new SubscriptionWSListRnCPackagesEntryImpl(resultCode, webServiceStatistics);
                     table.addEntry(entry);
                 }

                 printDebugLogForPostprocess(ISubscriptionWS.WS_LIST_RNC_PACKAGES);
             }else if(methodName.equalsIgnoreCase(ISubscriptionWS.WS_UPDATE_CREDIT_LIMIT)) {

				 printDebugLogForPreprocess(ISubscriptionWS.WS_UPDATE_CREDIT_LIMIT);

				 TableSubscriptionWSUpdateCreditLimitTableImpl table = new TableSubscriptionWSUpdateCreditLimitTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionWSUpdateCreditLimitEntryImpl entry = new SubscriptionWSUpdateCreditLimitEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(ISubscriptionWS.WS_UPDATE_CREDIT_LIMIT);
             } else if (methodName.equalsIgnoreCase(ISubscriptionWS.WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN)) {
				printDebugLogForPreprocess(ISubscriptionWS.WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);

				 TableSubscriptionWSSubscribeMonetaryRechargePlanTableImpl table = new TableSubscriptionWSSubscribeMonetaryRechargePlanTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionWSSubscribeMonetaryRechargePlanEntryImpl entry = new SubscriptionWSSubscribeMonetaryRechargePlanEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(ISubscriptionWS.WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);
			 } else if (methodName.equalsIgnoreCase(ISubscriptionWS.WS_CHANGE_BILL_DAY)) {

				 printDebugLogForPreprocess(ISubscriptionWS.WS_CHANGE_BILL_DAY);

				 TableSubscriptionWSChangeBillDayTableImpl table = new TableSubscriptionWSChangeBillDayTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionWSChangeBillDayEntryImpl entry = new SubscriptionWSChangeBillDayEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(ISubscriptionWS.WS_CHANGE_BILL_DAY);
			 } else if (methodName.equalsIgnoreCase(ISubscriptionWS.WS_SUBSCRIBE_BOD)) {

				 printDebugLogForPreprocess(ISubscriptionWS.WS_SUBSCRIBE_BOD);

				 TableSubscriptionWSSubscribeBoDTableImpl table = new TableSubscriptionWSSubscribeBoDTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionWSSubscribeBoDEntryImpl entry = new SubscriptionWSSubscribeBoDEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(ISubscriptionWS.WS_SUBSCRIBE_BOD);
			 } else if (methodName.equalsIgnoreCase(ISubscriptionWS.WS_LIST_BOD_PACKAGES)) {

				 printDebugLogForPreprocess(ISubscriptionWS.WS_LIST_BOD_PACKAGES);

				 TableSubscriptionWSListBoDPackagesTableImpl table = new TableSubscriptionWSListBoDPackagesTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionWSListBoDPackagesEntryImpl entry = new SubscriptionWSListBoDPackagesEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(ISubscriptionWS.WS_LIST_BOD_PACKAGES);
			 } else if (methodName.equalsIgnoreCase(SubscriptionRestWS.WS_FNF_GROUP_MEMBERS)) {

				 printDebugLogForPreprocess(methodName);

				 TableSubscriptionFnFGroupMembersTableImpl table = new TableSubscriptionFnFGroupMembersTableImpl(netvertexPCRFMib, mBeanServer);
				 for (ResultCode resultCode : ResultCode.values()) {
					 SubscriptionFnFGroupMembersEntryImpl entry = new SubscriptionFnFGroupMembersEntryImpl(resultCode, webServiceStatistics);
					 table.addEntry(entry);
				 }

				 printDebugLogForPostprocess(SubscriptionRestWS.WS_FNF_GROUP_MEMBERS);
			 }

        }catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add Subscription-WS 'Method wise ResultCode' Entries into SNMP' Subscription Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addSubscriptionWSResultCodeWiseEntryIntoSNMPTable(WebServiceStatistics webServiceStatistics) {
		try{
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Adding Subscription-WS 'ResultCode wise' Entries in SNMP's Subscription Table." );
			}
			for(ResultCode resultCode : ResultCode.values()){
				SubscriptionWSResultCodeWiseEntryImpl entry = new SubscriptionWSResultCodeWiseEntryImpl(resultCode.code, webServiceStatistics);
				subscriptionWSResultCodeWiseTableImpl.addEntry(entry);
			}
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Subscription-WS 'ResultCode wise' Entries added successfully in SNMP's Subscription Table." );
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add Subscription-WS 'ResultCode Wise' Entries into SNMP's Subscription Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addResetUsageWSResetBillingCycleResultCodeEntryIntoSNMPTable(WebServiceStatistics webServiceStatistics) {
		try{
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Adding '"+ ResetUsageWS.WS_RESET_BILLING_CYCLE+"' Entries in its SNMP table." );
			}

			TableResetUsageWSResetBillingCycleTableImpl  wsResetBillingCycleTableImpl =  new TableResetUsageWSResetBillingCycleTableImpl(netvertexPCRFMib, mBeanServer);
			for(ResultCode resultCode : ResultCode.values()){
				ResetUsageWSResetBillingCycleResultCodeEntryImpl entry = new ResetUsageWSResetBillingCycleResultCodeEntryImpl(resultCode.code, webServiceStatistics);
				wsResetBillingCycleTableImpl.addEntry(entry);
			}

			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "ResetUsage-WS method '"+ResetUsageWS.WS_RESET_BILLING_CYCLE+"' Entries added successfully in its respective SNMP table." );
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add ResetUsage-WS method '"+ResetUsageWS.WS_RESET_BILLING_CYCLE+"' Entries in its respective SNMP table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	private void addResetUsageWSResultCodeWiseEntryIntoSNMPTable(WebServiceStatistics webServiceStatistics) {
		try{
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Adding ResetUsage-WS 'ResultCode Wise' entries in SNMP's ResetUsage Table." );
			}
			for(ResultCode resultCode : ResultCode.values()){
				ResetUsageWSResultCodeWiseEntryImpl entry = new ResetUsageWSResultCodeWiseEntryImpl(resultCode.code, webServiceStatistics);
				resetUsageWSResultCodeWiseTableImpl.addEntry(entry);
			}
			if (getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "ResetUsage-WS 'ResultCode Wise' added successfully in SNMP's ResetUsage Table." );
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Failed to add ResetUsage-WS 'ResultCode wise' Entries into SNMP's ResetUsage Table. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
	}

	public WebServiceStatistics getWSStatistics(String name){
		return moduleAndMethodstatistics.get(name.trim());
	}

	@Override
	public void requestReceived(WebServiceRequest request) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Request received for: '"+request.getWebServiceMethodName()+"'");
		}

		globalStatistics.incrementRequestCounters();
		incrementWebServiceRequestCounters(request);
		incrementWebServiceMethodRequestCounters(request);

	}

 	@Override
	public void responseReceived(WebServiceResponse response) {

 		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Response received for: '"+response.getWebServiceMethodName()+"'");
		}

 		globalStatistics.incrementResponseCounters(response.getResponseCode());
 		incrementWebServiceResponseCounters(response);
 		incrementWebServiceMethodResponseCounters(response);
 	}

	private void incrementWebServiceMethodResponseCounters(WebServiceResponse response) {

		StringBuilder moduleAndMethodName = new StringBuilder();
		moduleAndMethodName.append(response.getWebServiceName());
		moduleAndMethodName.append(HASH_SIGN);
		moduleAndMethodName.append(response.getWebServiceMethodName());

		WebServiceStatistics  statistics = getWebServiceStatistics(moduleAndMethodName.toString());
		if(Objects.nonNull(statistics)){
			statistics.incrementResponseCounters(response.getResponseCode());
		}

 	}

	private void incrementWebServiceResponseCounters(WebServiceResponse response) {

		WebServiceStatistics  statistics = getWebServiceStatistics(response.getWebServiceName());
		if(Objects.nonNull(statistics)){
			statistics.incrementResponseCounters(response.getResponseCode());
		}
	}

	private void incrementWebServiceMethodRequestCounters(WebServiceRequest request) {

		StringBuilder moduleAndMethodName = new StringBuilder();
		moduleAndMethodName.append(request.getWebServiceName());
		moduleAndMethodName.append(HASH_SIGN);
		moduleAndMethodName.append(request.getWebServiceMethodName());

		WebServiceStatistics  statistics = getWebServiceStatistics(moduleAndMethodName.toString());
		if(Objects.nonNull(statistics)){
			statistics.incrementRequestCounters();
		}
 	}

	private void incrementWebServiceRequestCounters(WebServiceRequest request) {

		WebServiceStatistics  statistics = getWebServiceStatistics(request.getWebServiceName());
		if(Objects.nonNull(statistics)){
			statistics.incrementRequestCounters();
		}
	}

	public Set<String> getWebServicesKeysCache(){
		return moduleAndMethodstatistics.keySet();
	}

	public WebServiceStatistics getWSGlobalStatistics(){
		return globalStatistics;
	}

	public synchronized String resetAllStatistics(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reset All Statistics");
		}

		try{
			statisticsReset = STATS_INITIALIZING;
			globalStatistics.resetStatistics();
			for(WebServiceStatistics  statistics : moduleAndMethodstatistics.values()){
				statistics.resetStatistics();
			}
			statisicsLastResetTime  = System.currentTimeMillis();
			statisticsReset = STATS_RUNNING;
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "All Statistics has been Reset Successfully");
			}
			return "SUCCESS";
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Error while Reseting All Statistics. Reason: "+ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
			statisticsReset = STATS_OTHER;
			return "FAILURE";
		}
	}

	public int getStatisticsReset() {
		return statisticsReset;
	}

	public long getWebServiceUpTime() {
		return webServiceUpTime;
	}

	public long getStatisicsLastResetTime() {
		return statisicsLastResetTime;
	}
}
