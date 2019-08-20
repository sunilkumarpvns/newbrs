package com.elitecore.core.serverx.snmp.mib.os.data;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.snmp.mib.os.autogen.DskEntryMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumDskErrorFlag;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumLaErrorFlag;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumMemSwapError;
import com.elitecore.core.serverx.snmp.mib.os.autogen.LaEntryMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableDskTable;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableLaTable;
import com.elitecore.core.serverx.snmp.mib.os.extended.DskEntryMBeanImpl;
import com.elitecore.core.serverx.snmp.mib.os.extended.LaEntryMBeanImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.url.URLData;
import com.sun.management.snmp.SnmpPdu;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpVarBind;
import com.sun.management.snmp.SnmpVarBindList;
import com.sun.management.snmp.manager.SnmpPeer;
import com.sun.management.snmp.manager.SnmpRequest;
import com.sun.management.snmp.manager.SnmpRequestHandler;
import com.sun.management.snmp.manager.SnmpSession;

/**
 * This Class is fetches the Hardware Related statistics detail
 * like Memory Usage, Storage Detail, CPU Load Average Details
 * using UCD-SNMP-MIB of linux os.
 * 
 * @author sanjay.dhamelia
 */
public class SystemDetailProvider {

	protected static final String MODULE = "SYSTEM-DETAIL-PROVIDER";

	private static final int NO_SUCH_INSTANCE_RESPONSE_CODE = 4;
	
	@Nonnull private final URLData snmpUrl;
	@Nonnull private final TaskScheduler scheduler;

	private SnmpPeer snmpPeer;
	private SnmpSession snmpSession;

	private Map<Integer, LoadAveragetDetail> indexToLoadAverageEntry = new ConcurrentHashMap<Integer, SystemDetailProvider.LoadAveragetDetail>();
	private Map<Integer, DiskDetails> indexToDiskDetailEntry = new ConcurrentHashMap<Integer, SystemDetailProvider.DiskDetails>();
	
	// cached details
	private MemoryDetails memoryDetails = new MemoryDetails();
	private CPUDetails cpuDetails = new CPUDetails();
	
	
	// table for creating entries
	private volatile TableLaTable loadAverageTable = null;
	private volatile TableDskTable diskDetailTable = null;
	
	public SystemDetailProvider(@Nonnull URLData snmpUrl,@Nonnull TaskScheduler scheduler) {
		this.snmpUrl = Preconditions.checkNotNull(snmpUrl, "URL can not be null");
		this.scheduler = Preconditions.checkNotNull(scheduler, "Schedular can not be null");
	}

	/**
	 * Snmp peer is created to sent request,using URLData contains the IP:PORT.
	 * Snmp Session is created for creation of snmp request.
	 * All the parameters detail were fetch using schedular so on regular
	 * interval every information of Hardware details were fetch.
	 * @throws Exception
	 */
	public void start() throws Exception {
		snmpPeer = new SnmpPeer(snmpUrl.getHost(), snmpUrl.getPort());
		snmpSession = new SnmpSession("Snmp Session For System Details", snmpPeer);
		
		scheduler.scheduleIntervalBasedTask(new BaseIntervalBasedTask() {
			
			@Override
			public long getInterval() {
				return 60;
			}
			
			@Override
			public long getInitialDelay() {
				return 60;
			}
			
			@Override
			public TimeUnit getTimeUnit() {
				return TimeUnit.SECONDS;
			}
			
			@Override
			public void execute(AsyncTaskContext context) {
				try {
					doRequest();
				} catch (SnmpStatusException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		});
	}

	public TableLaTable getLoadAverageTable() {
		return loadAverageTable;
	}
	
	public void setLoadAverageTable(TableLaTable loadAverageTable) {
		this.loadAverageTable = loadAverageTable;
	}
	
	public TableDskTable getDskTable() {
		return diskDetailTable;
	}
	
	public void setDskTable(TableDskTable dskTable) {
		this.diskDetailTable = dskTable;
	}
	
	/**
	 * Need to destroy the SnmpSession on server shutdown.
	 */
	public void stop() {
		snmpSession.destroySession();
	}
	
	/**
	 * This method generates the SNMP Request for getting 
	 * the statistics value of the OS Parameter.
	 * @throws SnmpStatusException
	 */
	private void doRequest() throws SnmpStatusException {
		doMemoryDetailRequest();
		doCPUDetailRequest();
		doLoadAverageRequest();
		doDiskDetailRequest();
	}

	private void doMemoryDetailRequest() throws SnmpStatusException {
		
		SnmpVarBindList snmpVarBindList = new SnmpVarBindList("Variable Bind List for Memory Details");
		snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.4");
		
		/**
		 * Performs a single SNMP getbulk request on the variable binding list. Uses the default peer. 
		 * Parameters:
		 * Asych Handler - The callback that is invoked when a request is complete.
		 * variable bind list - A list of SnmpVarBind objects.
		 * nonRepeat - Number of variable bindings to get one time.
		 * maxRepeat - Number of repetitions for the variable bindings to get in one time.
		 * Throws:
		 * SnmpStatusException - An error occurred during the operation.
		 * 
		 * 
		 * Here total 18 nodes are defined in memory details so need to fetch data for 18 nodes
		 * so maxRepeat=18
		 */
		snmpSession.snmpGetBulkRequest(new SnmpRequestHandler() {
			
			@Override
			public void processSnmpPollTimeout(SnmpRequest request) {
				if (request.getRequestStatus() == SnmpRequest.stTimeout) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Request for Memory statistics details was timedout: "+
								"\n\tSnmp Request : "+SnmpPdu.pduTypeToString(request.getCommand())+
								"\n\tRequested Oid: .1.3.6.1.4.1.2021.4"+
								"\n\tError ID     : "+request.getErrorStatus()+
								"\n\tError Cause  : "+SnmpRequest.snmpErrorToString(request.getErrorStatus()));
					}
				}
			}
			
			@Override
			public void processSnmpPollData(SnmpRequest request, int errorStatus, int errorIndex,
					SnmpVarBindList variableBindList) {
				
				SnmpVarBindList responseVarBindList = request.getResponseVarBindList();
				Enumeration varBindList = responseVarBindList.getVarBindList();
				while (varBindList.hasMoreElements()) {
					SnmpVarBind nextElement = (SnmpVarBind) varBindList.nextElement();
					String oid = nextElement.getOid().toString();
					if (oid.equals("1.3.6.1.4.1.2021.4.1.0")) {
						memoryDetails.setMemIndex(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.2.0")) {
						memoryDetails.setMemErrorName(nextElement.getSnmpStringValue().toString());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.3.0")) {
						 memoryDetails.setMemTotalSwap(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.4.0")) {
						 memoryDetails.setMemAvailSwap(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.5.0")) {
						 memoryDetails.setMemTotalReal(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.6.0")) {
						 memoryDetails.setMemAvailReal(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.7.0")) {
						 memoryDetails.setMemTotalSwapTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.8.0")) {
						 memoryDetails.setMemAvailSwapTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.9.0")) {
						 memoryDetails.setMemTotalRealTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.10.0")) {
						 memoryDetails.setMemAvailRealTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.11.0")) {
						 memoryDetails.setMemTotalFree(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.12.0")) {
						 memoryDetails.setMemMinimumSwap(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.13.0")) {
						 memoryDetails.setMemShared(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.14.0")) {
						 memoryDetails.setMemBuffer(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.15.0")) {
						 memoryDetails.setMemCached(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.16.0")) {
						 memoryDetails.setMemUsedSwapTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.17.0")) {
						 memoryDetails.setMemUsedRealTXT(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.4.100.0")) {
						 memoryDetails.setMemSwapError(new EnumMemSwapError(nextElement.getSnmpIntValue().intValue()));
					} else if (oid.equals("1.3.6.1.4.1.2021.4.101.0")) {
						memoryDetails.setMemSwapErrorMsg(nextElement.getSnmpStringValue().toString());
					}
				}
			}
			
			@Override
			public void processSnmpInternalError(SnmpRequest request, String errorMessage) {
				if(request.getRequestStatus() == SnmpRequest.stInternalError) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Error occured while processing request for Memory statistics: "+
							"\n\tSnmp Request : "+SnmpPdu.pduTypeToString(request.getCommand())+
							"\n\tRequested Oid: .1.3.6.1.4.1.2021.4"+
							"\n\tError ID     : "+request.getErrorStatus()+
							"\n\tError Cause  : "+SnmpRequest.snmpErrorToString(request.getErrorStatus()));
					}
				}
			}
		}, snmpVarBindList,0,18);
	}
	
	private void doCPUDetailRequest() throws SnmpStatusException {
		
		SnmpVarBindList snmpVarBindList = new SnmpVarBindList("Variable Bind List for CPU Details");
		snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.11");
		
		/**
		 * Performs a single SNMP getbulk request on the variable binding list. Uses the default peer. 
		 * Parameters:
		 * Asych Handler - The callback that is invoked when a request is complete.
		 * variable bind list - A list of SnmpVarBind objects.
		 * nonRepeat - Number of variable bindings to get one time.
		 * maxRepeat - Number of repetitions for the variable bindings to get in one time.
		 * Throws:
		 * SnmpStatusException - An error occurred during the operation.
		 * 
		 * 
		 * Here total 28 nodes are defined in memory details so need to fetch data for 28 nodes
		 * so maxRepeat=28
		 */
		snmpSession.snmpGetBulkRequest(new SnmpRequestHandler() {
			
			@Override
			public void processSnmpPollTimeout(SnmpRequest request) {
				if (request.getRequestStatus() == SnmpRequest.stTimeout) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Request for CPU statistics was timedout: "+
								"\n\tSnmp Request : "+SnmpPdu.pduTypeToString(request.getCommand())+
								"\n\tRequested Oid: .1.3.6.1.4.1.2021.11"+
								"\n\tError ID     : "+request.getErrorStatus()+
								"\n\tError Cause  : "+SnmpRequest.snmpErrorToString(request.getErrorStatus()));
					}
				}
			}
			
			@Override
			public void processSnmpPollData(SnmpRequest request, int errorStatus, int errorIndex,
					SnmpVarBindList variableBindList) {
				
				SnmpVarBindList responseVarBindList = request.getResponseVarBindList();
				Enumeration varBindList = responseVarBindList.getVarBindList();
				while (varBindList.hasMoreElements()) {
					SnmpVarBind nextElement = (SnmpVarBind) varBindList.nextElement();
					String oid = nextElement.getOid().toString();
					if (oid.equals("1.3.6.1.4.1.2021.11.1.0")) {
						cpuDetails.setSsIndex(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.2.0")) {
						cpuDetails.setSsErrorName(nextElement.getSnmpStringValue().toString());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.3.0")) {
						 cpuDetails.setSsSwapIn(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.4.0")) {
						 cpuDetails.setSsSwapOut(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.5.0")) {
						 cpuDetails.setSsIOSent(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.6.0")) {
						 cpuDetails.setSsIOReceive(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.7.0")) {
						 cpuDetails.setSsSysInterrupts(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.8.0")) {
						 cpuDetails.setSsSysContext(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.9.0")) {
						 cpuDetails.setSsCpuUser(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.10.0")) {
						 cpuDetails.setSsCpuSystem(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.11.0")) {
						 cpuDetails.setSsCpuIdle(nextElement.getSnmpIntValue().intValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.50.0")) {
						 cpuDetails.setSsCpuRawUser(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.51.0")) {
						 cpuDetails.setSsCpuRawNice(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.52.0")) {
						 cpuDetails.setSsCpuRawSystem(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.53.0")) {
						 cpuDetails.setSsCpuRawIdle(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.54.0")) {
						 cpuDetails.setSsCpuRawWait(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.55.0")) {
						 cpuDetails.setSsCpuRawKernel(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.56.0")) {
						 cpuDetails.setSsCpuRawInterrupt(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.57.0")) {
						 cpuDetails.setSsIORawSent(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.58.0")) {
						 cpuDetails.setSsIORawReceived(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.59.0")) {
						 cpuDetails.setSsRawInterrupts(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.60.0")) {
						 cpuDetails.setSsRawContexts(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.61.0")) {
						 cpuDetails.setSsCpuRawSoftIRQ(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.62.0")) {
						 cpuDetails.setSsRawSwapIn(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.63.0")) {
						 cpuDetails.setSsRawSwapOut(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.64.0")) {
						 cpuDetails.setSsCpuRawSteal(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.65.0")) {
						 cpuDetails.setSsCpuRawGuest(nextElement.getSnmpCounterValue().longValue());
					} else if (oid.equals("1.3.6.1.4.1.2021.11.66.0")) {
						cpuDetails.setSsCpuRawGuestNice(nextElement.getSnmpCounterValue().longValue());
					}
				}
			}
			
			@Override
			public void processSnmpInternalError(SnmpRequest request, String errorMessage) {
				if(request.getRequestStatus() == SnmpRequest.stInternalError) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Error occured while processing request for CPU statistics: "+
							"\n\tSnmp Request : "+SnmpPdu.pduTypeToString(request.getCommand())+
							"\n\tRequested Oid: 1.3.6.1.4.1.2021.11"+
							"\n\tError ID     : "+request.getErrorStatus()+
							"\n\tError Cause  : "+SnmpRequest.snmpErrorToString(request.getErrorStatus()));
					}
				}
			}
		}, snmpVarBindList,0,28);
		
	}
	
	private void doLoadAverageRequest() throws SnmpStatusException {

		int indexValue = 1;
		while (true) {
			SnmpVarBindList snmpVarBindList = new SnmpVarBindList("Variable Bind List for System Load Average");
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.1."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.2."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.3."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.4."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.5."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.6."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.100."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.10.1.101."+indexValue);

			SnmpRequest snmpGetRequest = snmpSession.snmpGetRequest(null, snmpVarBindList);
			snmpGetRequest.waitForCompletion(1000);
			SnmpVarBindList responseVarBindList = snmpGetRequest.getResponseVarBindList();

			SnmpVarBind varBindAt = responseVarBindList.getVarBindAt(0);
			
			if(varBindAt.getValueStatus() == NO_SUCH_INSTANCE_RESPONSE_CODE){
				break;
			}
			
			parseResponseAndCreateEntryForLoadAverage(snmpGetRequest.getResponseVarBindList(), indexValue);
			responseVarBindList.clear();
			indexValue++;
		}		
	}
	
	/**
	 * Parse the SNMP response and set the respective counters
	 * of Load Average Detail by matching their OID Values. 
	 * @param responseVarBindList - list contains the response variables
	 * @param indexValue - represent index value of entry.
	 * @throws SnmpStatusException
	 */
	private void parseResponseAndCreateEntryForLoadAverage(SnmpVarBindList responseVarBindList, int indexValue) throws SnmpStatusException {
		
		LoadAveragetDetail loadAveragetDetail = new LoadAveragetDetail();
		Enumeration varBindList = responseVarBindList.elements();
		while(varBindList.hasMoreElements()) {
			SnmpVarBind nextElement = (SnmpVarBind) varBindList.nextElement();
			String oid = nextElement.getOid().toString();
			String oidWithoutIndexValue = oid.substring(0, oid.lastIndexOf('.'));
			
			if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.1")) {
				loadAveragetDetail.setLaIndex(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.2")) {
				 loadAveragetDetail.setLaNames(nextElement.getSnmpStringValue().toString());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.3")) {
				 loadAveragetDetail.setLaLoad(nextElement.getSnmpStringValue().toString());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.4")) {
				 loadAveragetDetail.setLaConfig(nextElement.getSnmpStringValue().toString());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.5")) {
				 loadAveragetDetail.setLaLoadInt(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.6")) {
				 loadAveragetDetail.setLaLoadFloat(nextElement.getSnmpOpaqueValue().toByte());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.100")) {
				 loadAveragetDetail.setLaErrorFlag(new EnumLaErrorFlag(nextElement.getSnmpIntValue().intValue()));
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.10.1.101")) {
				loadAveragetDetail.setLaErrMessage(nextElement.getSnmpStringValue().toString());
			}
		}
		
		if(indexToLoadAverageEntry.get(indexValue) == null) {
			indexToLoadAverageEntry.put(indexValue, loadAveragetDetail);
			LaEntryMBean loadAveragetEntry = new LaEntryMBeanImpl(loadAveragetDetail);
			try {
				loadAverageTable.addEntry(loadAveragetEntry);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Successfully added entry for load average detail: "+loadAveragetDetail.getLaNames());
				}
			} catch (SnmpStatusException e) { 
				getLogger().trace(MODULE, e);
				throw new SnmpStatusException("Fail to add entry for load average detail: "+loadAveragetDetail.getLaNames());
			}
		} else {
			/**
			 * In SNMP Table Entries where created using Index value = 1 (i.e. Entry have index of value 1).
			 * But Index was start from 0 on SNMP table. So need to take entry using indexValue-1.
			 */
			LaEntryMBeanImpl entry = (LaEntryMBeanImpl) loadAverageTable.getEntry(indexValue-1);
			loadAverageTable.removeEntry(entry);
			indexToLoadAverageEntry.put(indexValue, loadAveragetDetail);
			LaEntryMBean loadAveragetEntry = new LaEntryMBeanImpl(loadAveragetDetail);
			try {
				loadAverageTable.addEntry(loadAveragetEntry);
			} catch (SnmpStatusException e) { 
				ignoreTrace(e);
				throw new SnmpStatusException("Fail to update entry for load average detail: "+loadAveragetDetail.getLaNames());
			}
		}
	}
	
	private void doDiskDetailRequest() throws SnmpStatusException {
		
		int indexValue = 1;

		while (true) {
			SnmpVarBindList snmpVarBindList = new SnmpVarBindList("Variable Bind List for Disk Details");
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.1."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.2."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.3."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.4."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.5."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.6."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.7."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.8."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.9."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.10."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.11."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.12."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.13."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.14."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.15."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.16."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.100."+indexValue);
			snmpVarBindList.addVarBind(".1.3.6.1.4.1.2021.9.1.101."+indexValue);

			SnmpRequest snmpGetRequest = snmpSession.snmpGetRequest(null, snmpVarBindList);
			snmpGetRequest.waitForCompletion(1000);
			SnmpVarBindList responseVarBindList = snmpGetRequest.getResponseVarBindList();

			SnmpVarBind varBindAt = responseVarBindList.getVarBindAt(0);

			if(varBindAt.getValueStatus() == NO_SUCH_INSTANCE_RESPONSE_CODE){
				break;
			}

			parseResponseAndCreateEntryForDiskDetail(snmpGetRequest.getResponseVarBindList(), indexValue);
			responseVarBindList.clear();
			indexValue++;
		}		
	}

	/**
	 * Parse the SNMP response and set the respective counters
	 * of Disk Details by matching their OID Values. 
	 * @param responseVarBindList - list contains the response variables
	 * @param indexValue - represent index value of entry.
	 * @throws SnmpStatusException
	 */
	private void parseResponseAndCreateEntryForDiskDetail(SnmpVarBindList responseVarBindList, int indexValue) throws SnmpStatusException {
		
		DiskDetails diskDetails = new DiskDetails();
		Enumeration varBindList = responseVarBindList.elements();
		while(varBindList.hasMoreElements()) {
			SnmpVarBind nextElement = (SnmpVarBind) varBindList.nextElement();
			String oid = nextElement.getOid().toString();
			String oidWithoutIndexValue = oid.substring(0, oid.lastIndexOf('.'));

			if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.1")) {
				diskDetails.setDskIndex(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.2")) {
				diskDetails.setDskPath(nextElement.getSnmpStringValue().toString());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.3")) {
				diskDetails.setDskDevice(nextElement.getSnmpStringValue().toString());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.4")) {
				 diskDetails.setDskMinimum(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.5")) {
				 diskDetails.setDskMinPercent(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.6")) {
				 diskDetails.setDskTotal(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.7")) {
				 diskDetails.setDskAvail(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.8")) {
				 diskDetails.setDskUsed(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.9")) {
				 diskDetails.setDskPercent(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.10")) {
				 diskDetails.setDskPercentNode(nextElement.getSnmpIntValue().intValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.11")) {
				 diskDetails.setDskTotalLow(nextElement.getSnmpGaugeValue().longValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.12")) {
				 diskDetails.setDskTotalHigh(nextElement.getSnmpGaugeValue().longValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.13")) {
				 diskDetails.setDskAvailLow(nextElement.getSnmpGaugeValue().longValue());
			} else if(oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.14")) {
				 diskDetails.setDskAvailHigh(nextElement.getSnmpGaugeValue().longValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.15")) {
				 diskDetails.setDskUsedLow(nextElement.getSnmpGaugeValue().longValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.16")) {
				 diskDetails.setDskUsedHigh(nextElement.getSnmpGaugeValue().longValue());
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.100")) {
				 diskDetails.setDskErrorFlag(new EnumDskErrorFlag(nextElement.getSnmpIntValue().intValue()));
			} else if (oidWithoutIndexValue.equals("1.3.6.1.4.1.2021.9.1.101")) {
				diskDetails.setDskErrorMsg(nextElement.getSnmpStringValue().toString());
			}
		}

		if(indexToDiskDetailEntry.get(indexValue) == null) {
			indexToDiskDetailEntry.put(indexValue, diskDetails);
			DskEntryMBean diskDetailEntry = new DskEntryMBeanImpl(diskDetails);
			try {
				diskDetailTable.addEntry(diskDetailEntry);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Successfully added entry for Disk Statistics detail: "+diskDetails.getDskDevice());
				}
			} catch (SnmpStatusException e) { 
				ignoreTrace(e);
				throw new SnmpStatusException("Fail to add entry for Disk Statistics detail: "+diskDetails.getDskDevice());
			}
		} else {
			/**
			 * In SNMP Table Entries where created using Index value = 1 (i.e. Entry have index of value 1).
			 * But Index was start from 0 on SNMP table. So need to take entry using indexValue-1.
			 */
			DskEntryMBeanImpl entry = (DskEntryMBeanImpl) diskDetailTable.getEntry(indexValue-1);
			diskDetailTable.removeEntry(entry);
			indexToDiskDetailEntry.put(indexValue, diskDetails);
			DskEntryMBeanImpl diskDetailEntry = new DskEntryMBeanImpl(diskDetails);
			try {
				diskDetailTable.addEntry(diskDetailEntry);
			} catch (SnmpStatusException e) { 
				ignoreTrace(e);
				throw new SnmpStatusException("Fail to update entry for Disk Statistics detail: "+diskDetails.getDskDevice());
			}
		}
	}

	public MemoryDetails getMemoryDetails() {
		return memoryDetails;
	}
	
	public CPUDetails getCpuDetails() {
		return cpuDetails;
	}
	
	/**
	 * Memory Statics Details contains the list of 
	 * counters value.
	 */
	public static class MemoryDetails {
		/**
	     * Variable for storing the value of "MemSwapErrorMsg".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.101".
	     * 
	     * Bogus Name. This should always return the string 'swap'.
	     * Define in MIB.
	     */
	    private String MemSwapErrorMsg = new String();

	    /**
	     * Variable for storing the value of "MemSwapError".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.100".
	     */
	    private EnumMemSwapError MemSwapError = new EnumMemSwapError();

	    /**
	     * Variable for storing the value of "MemUsedRealTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.17".
	     */
	    private Integer MemUsedRealTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemUsedSwapTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.16".
	     */
	    private Integer MemUsedSwapTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemCached".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.15".
	     */
	    private Integer MemCached = new Integer(0);

	    /**
	     * Variable for storing the value of "MemBuffer".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.14".
	     */
	    private Integer MemBuffer = new Integer(0);

	    /**
	     * Variable for storing the value of "MemShared".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.13".
	     */
	    private Integer MemShared = new Integer(0);

	    /**
	     * Variable for storing the value of "MemMinimumSwap".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.12".
	     */
	    private Integer MemMinimumSwap = new Integer(0);

	    /**
	     * Variable for storing the value of "MemTotalFree".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.11".
	     */
	    private Integer MemTotalFree = new Integer(0);

	    /**
	     * Variable for storing the value of "MemAvailRealTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.10".
	     */
	    private Integer MemAvailRealTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemTotalRealTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.9".
	     */
	    private Integer MemTotalRealTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemAvailSwapTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.8".
	     */
	    private Integer MemAvailSwapTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemTotalSwapTXT".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.7".
	     */
	    private Integer MemTotalSwapTXT = new Integer(0);

	    /**
	     * Variable for storing the value of "MemAvailReal".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.6".
	     */
	    private Integer MemAvailReal = new Integer(0);

	    /**
	     * Variable for storing the value of "MemTotalReal".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.5".
	     */
	    private Integer MemTotalReal = new Integer(0);

	    /**
	     * Variable for storing the value of "MemAvailSwap".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.4".
	     */
	    private Integer MemAvailSwap = new Integer(0);

	    /**
	     * Variable for storing the value of "MemTotalSwap".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.3".
	     */
	    private Integer MemTotalSwap = new Integer(0);

	    /**
	     * Variable for storing the value of "MemErrorName".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.2".
	     */
	    private String MemErrorName = new String("swap");

	    /**
	     * Variable for storing the value of "MemIndex".
	     * The variable is identified by: "1.3.6.1.4.1.2021.4.1".
	     * 
	     * Bogus Index.  This should always return the integer 0.
	     * Defined in MIB
	     */
	    private Integer MemIndex = new Integer(0);
	    
	    public String getMemSwapErrorMsg() {
			return MemSwapErrorMsg;
		}

		public void setMemSwapErrorMsg(String memSwapErrorMsg) {
			MemSwapErrorMsg = memSwapErrorMsg;
		}

		public EnumMemSwapError getMemSwapError() {
			return MemSwapError;
		}

		public void setMemSwapError(EnumMemSwapError memSwapError) {
			MemSwapError = memSwapError;
		}

		public Integer getMemUsedRealTXT() {
			return MemUsedRealTXT;
		}

		public void setMemUsedRealTXT(Integer memUsedRealTXT) {
			MemUsedRealTXT = memUsedRealTXT;
		}

		public Integer getMemUsedSwapTXT() {
			return MemUsedSwapTXT;
		}

		public void setMemUsedSwapTXT(Integer memUsedSwapTXT) {
			MemUsedSwapTXT = memUsedSwapTXT;
		}

		public Integer getMemCached() {
			return MemCached;
		}

		public void setMemCached(Integer memCached) {
			MemCached = memCached;
		}

		public Integer getMemBuffer() {
			return MemBuffer;
		}

		public void setMemBuffer(Integer memBuffer) {
			MemBuffer = memBuffer;
		}

		public Integer getMemShared() {
			return MemShared;
		}

		public void setMemShared(Integer memShared) {
			MemShared = memShared;
		}

		public Integer getMemMinimumSwap() {
			return MemMinimumSwap;
		}

		public void setMemMinimumSwap(Integer memMinimumSwap) {
			MemMinimumSwap = memMinimumSwap;
		}

		public Integer getMemTotalFree() {
			return MemTotalFree;
		}

		public void setMemTotalFree(Integer memTotalFree) {
			MemTotalFree = memTotalFree;
		}

		public Integer getMemAvailRealTXT() {
			return MemAvailRealTXT;
		}

		public void setMemAvailRealTXT(Integer memAvailRealTXT) {
			MemAvailRealTXT = memAvailRealTXT;
		}

		public Integer getMemTotalRealTXT() {
			return MemTotalRealTXT;
		}

		public void setMemTotalRealTXT(Integer memTotalRealTXT) {
			MemTotalRealTXT = memTotalRealTXT;
		}

		public Integer getMemAvailSwapTXT() {
			return MemAvailSwapTXT;
		}

		public void setMemAvailSwapTXT(Integer memAvailSwapTXT) {
			MemAvailSwapTXT = memAvailSwapTXT;
		}

		public Integer getMemTotalSwapTXT() {
			return MemTotalSwapTXT;
		}

		public void setMemTotalSwapTXT(Integer memTotalSwapTXT) {
			MemTotalSwapTXT = memTotalSwapTXT;
		}

		public Integer getMemAvailReal() {
			return MemAvailReal;
		}

		public void setMemAvailReal(Integer memAvailReal) {
			MemAvailReal = memAvailReal;
		}

		public Integer getMemTotalReal() {
			return MemTotalReal;
		}

		public void setMemTotalReal(Integer memTotalReal) {
			MemTotalReal = memTotalReal;
		}

		public Integer getMemAvailSwap() {
			return MemAvailSwap;
		}

		public void setMemAvailSwap(Integer memAvailSwap) {
			MemAvailSwap = memAvailSwap;
		}

		public Integer getMemTotalSwap() {
			return MemTotalSwap;
		}

		public void setMemTotalSwap(Integer memTotalSwap) {
			MemTotalSwap = memTotalSwap;
		}

		public String getMemErrorName() {
			return MemErrorName;
		}

		public void setMemErrorName(String memErrorName) {
			MemErrorName = memErrorName;
		}

		public Integer getMemIndex() {
			return MemIndex;
		}

		public void setMemIndex(Integer memIndex) {
			MemIndex = memIndex;
		}

		
	}

	/**
	 * Disk Statics Details contains the list of 
	 * counters value.
	 */
	public static class DiskDetails {
		
		 /**
	     * Variable for storing the value of "DskErrorMsg".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.101".
	     */
	    private String DskErrorMsg = new String();

	    /**
	     * Variable for storing the value of "DskErrorFlag".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.100".
	     */
	    private EnumDskErrorFlag DskErrorFlag = new EnumDskErrorFlag();

	    /**
	     * Variable for storing the value of "DskUsedHigh".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.16".
	     */
	    private Long DskUsedHigh = new Long(0);

	    /**
	     * Variable for storing the value of "DskUsedLow".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.15".
	     */
	    private Long DskUsedLow = new Long(0);

	    /**
	     * Variable for storing the value of "DskAvailHigh".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.14".
	     */
	    private Long DskAvailHigh = new Long(0);

	    /**
	     * Variable for storing the value of "DskAvailLow".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.13".
	     */
	    private Long DskAvailLow = new Long(0);

	    /**
	     * Variable for storing the value of "DskTotalHigh".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.12".
	     */
	    private Long DskTotalHigh = new Long(0);

	    /**
	     * Variable for storing the value of "DskTotalLow".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.11".
	     */
	    private Long DskTotalLow = new Long(0);

	    /**
	     * Variable for storing the value of "DskPercentNode".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.10".
	     */
	    private Integer DskPercentNode = new Integer(0);

	    /**
	     * Variable for storing the value of "DskPercent".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.9".
	     */
	    private Integer DskPercent = new Integer(0);

	    /**
	     * Variable for storing the value of "DskUsed".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.8".
	     */
	    private Integer DskUsed = new Integer(0);

	    /**
	     * Variable for storing the value of "DskAvail".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.7".
	     */
	    private Integer DskAvail = new Integer(0);

	    /**
	     * Variable for storing the value of "DskTotal".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.6".
	     */
	    private Integer DskTotal = new Integer(0);

	    /**
	     * Variable for storing the value of "DskMinPercent".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.5".
	     */
	    private Integer DskMinPercent = new Integer(0);

	    /**
	     * Variable for storing the value of "DskMinimum".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.4".
	     */
	    private Integer DskMinimum = new Integer(0);

	    /**
	     * Variable for storing the value of "DskDevice".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.3".
	     */
	    private String DskDevice = new String();

	    /**
	     * Variable for storing the value of "DskPath".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.2".
	     */
	    private String DskPath = new String();

	    /**
	     * Variable for storing the value of "DskIndex".
	     * The variable is identified by: "1.3.6.1.4.1.2021.9.1.1".
	     */
	    private Integer DskIndex = new Integer(1);

		public String getDskErrorMsg() {
			return DskErrorMsg;
		}

		public void setDskErrorMsg(String dskErrorMsg) {
			DskErrorMsg = dskErrorMsg;
		}

		public EnumDskErrorFlag getDskErrorFlag() {
			return DskErrorFlag;
		}

		public void setDskErrorFlag(EnumDskErrorFlag dskErrorFlag) {
			DskErrorFlag = dskErrorFlag;
		}

		public Long getDskUsedHigh() {
			return DskUsedHigh;
		}

		public void setDskUsedHigh(Long dskUsedHigh) {
			DskUsedHigh = dskUsedHigh;
		}

		public Long getDskUsedLow() {
			return DskUsedLow;
		}

		public void setDskUsedLow(Long dskUsedLow) {
			DskUsedLow = dskUsedLow;
		}

		public Long getDskAvailHigh() {
			return DskAvailHigh;
		}

		public void setDskAvailHigh(Long dskAvailHigh) {
			DskAvailHigh = dskAvailHigh;
		}

		public Long getDskAvailLow() {
			return DskAvailLow;
		}

		public void setDskAvailLow(Long dskAvailLow) {
			DskAvailLow = dskAvailLow;
		}

		public Long getDskTotalHigh() {
			return DskTotalHigh;
		}

		public void setDskTotalHigh(Long dskTotalHigh) {
			DskTotalHigh = dskTotalHigh;
		}

		public Long getDskTotalLow() {
			return DskTotalLow;
		}

		public void setDskTotalLow(Long dskTotalLow) {
			DskTotalLow = dskTotalLow;
		}

		public Integer getDskPercentNode() {
			return DskPercentNode;
		}

		public void setDskPercentNode(Integer dskPercentNode) {
			DskPercentNode = dskPercentNode;
		}

		public Integer getDskPercent() {
			return DskPercent;
		}

		public void setDskPercent(Integer dskPercent) {
			DskPercent = dskPercent;
		}

		public Integer getDskUsed() {
			return DskUsed;
		}

		public void setDskUsed(Integer dskUsed) {
			DskUsed = dskUsed;
		}

		public Integer getDskAvail() {
			return DskAvail;
		}

		public void setDskAvail(Integer dskAvail) {
			DskAvail = dskAvail;
		}

		public Integer getDskTotal() {
			return DskTotal;
		}

		public void setDskTotal(Integer dskTotal) {
			DskTotal = dskTotal;
		}

		public Integer getDskMinPercent() {
			return DskMinPercent;
		}

		public void setDskMinPercent(Integer dskMinPercent) {
			DskMinPercent = dskMinPercent;
		}

		public Integer getDskMinimum() {
			return DskMinimum;
		}

		public void setDskMinimum(Integer dskMinimum) {
			DskMinimum = dskMinimum;
		}

		public String getDskDevice() {
			return DskDevice;
		}

		public void setDskDevice(String dskDevice) {
			DskDevice = dskDevice;
		}

		public String getDskPath() {
			return DskPath;
		}

		public void setDskPath(String dskPath) {
			DskPath = dskPath;
		}

		public Integer getDskIndex() {
			return DskIndex;
		}

		public void setDskIndex(Integer dskIndex) {
			DskIndex = dskIndex;
		}
	}
	
	/**
	 * CPU Statics Details contains the list of 
	 * counters value.
	 */
	public static class CPUDetails {

		/**
	     * Variable for storing the value of "SsCpuRawUser".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.50".
	     */
	    private Long SsCpuRawUser = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawGuestNice".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.66".
	     */
	    private Long SsCpuRawGuestNice = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawGuest".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.65".
	     */
	    private Long SsCpuRawGuest = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawSteal".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.64".
	     */
	    private Long SsCpuRawSteal = new Long(0);

	    /**
	     * Variable for storing the value of "SsRawSwapOut".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.63".
	     */
	    private Long SsRawSwapOut = new Long(0);

	    /**
	     * Variable for storing the value of "SsRawSwapIn".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.62".
	     */
	    private Long SsRawSwapIn = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawSoftIRQ".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.61".
	     */
	    private Long SsCpuRawSoftIRQ = new Long(0);

	    /**
	     * Variable for storing the value of "SsRawContexts".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.60".
	     */
	    private Long SsRawContexts = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuIdle".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.11".
	     */
	    private Integer SsCpuIdle = new Integer(0);

	    /**
	     * Variable for storing the value of "SsCpuSystem".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.10".
	     */
	    private Integer SsCpuSystem = new Integer(0);

	    /**
	     * Variable for storing the value of "SsCpuUser".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.9".
	     */
	    private Integer SsCpuUser = new Integer(0);

	    /**
	     * Variable for storing the value of "SsSysContext".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.8".
	     */
	    private Integer SsSysContext = new Integer(0);

	    /**
	     * Variable for storing the value of "SsSysInterrupts".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.7".
	     */
	    private Integer SsSysInterrupts = new Integer(0);

	    /**
	     * Variable for storing the value of "SsRawInterrupts".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.59".
	     */
	    private Long SsRawInterrupts = new Long(0);

	    /**
	     * Variable for storing the value of "SsIORawReceived".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.58".
	     */
	    private Long SsIORawReceived = new Long(0);

	    /**
	     * Variable for storing the value of "SsIOReceive".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.6".
	     */
	    private Integer SsIOReceive = new Integer(0);

	    /**
	     * Variable for storing the value of "SsIORawSent".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.57".
	     */
	    private Long SsIORawSent = new Long(0);

	    /**
	     * Variable for storing the value of "SsIOSent".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.5".
	     */
	    private Integer SsIOSent = new Integer(0);

	    /**
	     * Variable for storing the value of "SsSwapOut".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.4".
	     */
	    private Integer SsSwapOut = new Integer(0);

	    /**
	     * Variable for storing the value of "SsCpuRawInterrupt".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.56".
	     */
	    private Long SsCpuRawInterrupt = new Long(0);

	    /**
	     * Variable for storing the value of "SsSwapIn".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.3".
	     */
	    private Integer SsSwapIn = new Integer(0);

	    /**
	     * Variable for storing the value of "SsCpuRawKernel".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.55".
	     */
	    private Long SsCpuRawKernel = new Long(0);

	    /**
	     * Variable for storing the value of "SsErrorName".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.2".
	     * default value = systemStats defined in MIB.
	     */
	    
	    private String SsErrorName = new String("systemStats");

	    /**
	     * Variable for storing the value of "SsCpuRawWait".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.54".
	     */
	    private Long SsCpuRawWait = new Long(0);

	    /**
	     * Variable for storing the value of "SsIndex".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.1".
	     * 
	     * Bogus Index.  This should always return the integer 1.
	     * default value of index is Defined in MIB.
	     */
	    private Integer SsIndex = new Integer(1);

	    /**
	     * Variable for storing the value of "SsCpuRawIdle".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.53".
	     */
	    private Long SsCpuRawIdle = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawSystem".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.52".
	     */
	    private Long SsCpuRawSystem = new Long(0);

	    /**
	     * Variable for storing the value of "SsCpuRawNice".
	     * The variable is identified by: "1.3.6.1.4.1.2021.11.51".
	     */
	    private Long SsCpuRawNice = new Long(0);

		public Long getSsCpuRawUser() {
			return SsCpuRawUser;
		}

		public void setSsCpuRawUser(Long ssCpuRawUser) {
			SsCpuRawUser = ssCpuRawUser;
		}

		public Long getSsCpuRawGuestNice() {
			return SsCpuRawGuestNice;
		}

		public void setSsCpuRawGuestNice(Long ssCpuRawGuestNice) {
			SsCpuRawGuestNice = ssCpuRawGuestNice;
		}

		public Long getSsCpuRawGuest() {
			return SsCpuRawGuest;
		}

		public void setSsCpuRawGuest(Long ssCpuRawGuest) {
			SsCpuRawGuest = ssCpuRawGuest;
		}

		public Long getSsCpuRawSteal() {
			return SsCpuRawSteal;
		}

		public void setSsCpuRawSteal(Long ssCpuRawSteal) {
			SsCpuRawSteal = ssCpuRawSteal;
		}

		public Long getSsRawSwapOut() {
			return SsRawSwapOut;
		}

		public void setSsRawSwapOut(Long ssRawSwapOut) {
			SsRawSwapOut = ssRawSwapOut;
		}

		public Long getSsRawSwapIn() {
			return SsRawSwapIn;
		}

		public void setSsRawSwapIn(Long ssRawSwapIn) {
			SsRawSwapIn = ssRawSwapIn;
		}

		public Long getSsCpuRawSoftIRQ() {
			return SsCpuRawSoftIRQ;
		}

		public void setSsCpuRawSoftIRQ(Long ssCpuRawSoftIRQ) {
			SsCpuRawSoftIRQ = ssCpuRawSoftIRQ;
		}

		public Long getSsRawContexts() {
			return SsRawContexts;
		}

		public void setSsRawContexts(Long ssRawContexts) {
			SsRawContexts = ssRawContexts;
		}

		public Integer getSsCpuIdle() {
			return SsCpuIdle;
		}

		public void setSsCpuIdle(Integer ssCpuIdle) {
			SsCpuIdle = ssCpuIdle;
		}

		public Integer getSsCpuSystem() {
			return SsCpuSystem;
		}

		public void setSsCpuSystem(Integer ssCpuSystem) {
			SsCpuSystem = ssCpuSystem;
		}

		public Integer getSsCpuUser() {
			return SsCpuUser;
		}

		public void setSsCpuUser(Integer ssCpuUser) {
			SsCpuUser = ssCpuUser;
		}

		public Integer getSsSysContext() {
			return SsSysContext;
		}

		public void setSsSysContext(Integer ssSysContext) {
			SsSysContext = ssSysContext;
		}

		public Integer getSsSysInterrupts() {
			return SsSysInterrupts;
		}

		public void setSsSysInterrupts(Integer ssSysInterrupts) {
			SsSysInterrupts = ssSysInterrupts;
		}

		public Long getSsRawInterrupts() {
			return SsRawInterrupts;
		}

		public void setSsRawInterrupts(Long ssRawInterrupts) {
			SsRawInterrupts = ssRawInterrupts;
		}

		public Long getSsIORawReceived() {
			return SsIORawReceived;
		}

		public void setSsIORawReceived(Long ssIORawReceived) {
			SsIORawReceived = ssIORawReceived;
		}

		public Integer getSsIOReceive() {
			return SsIOReceive;
		}

		public void setSsIOReceive(Integer ssIOReceive) {
			SsIOReceive = ssIOReceive;
		}

		public Long getSsIORawSent() {
			return SsIORawSent;
		}

		public void setSsIORawSent(Long ssIORawSent) {
			SsIORawSent = ssIORawSent;
		}

		public Integer getSsIOSent() {
			return SsIOSent;
		}

		public void setSsIOSent(Integer ssIOSent) {
			SsIOSent = ssIOSent;
		}

		public Integer getSsSwapOut() {
			return SsSwapOut;
		}

		public void setSsSwapOut(Integer ssSwapOut) {
			SsSwapOut = ssSwapOut;
		}

		public Long getSsCpuRawInterrupt() {
			return SsCpuRawInterrupt;
		}

		public void setSsCpuRawInterrupt(Long ssCpuRawInterrupt) {
			SsCpuRawInterrupt = ssCpuRawInterrupt;
		}

		public Integer getSsSwapIn() {
			return SsSwapIn;
		}

		public void setSsSwapIn(Integer ssSwapIn) {
			SsSwapIn = ssSwapIn;
		}

		public Long getSsCpuRawKernel() {
			return SsCpuRawKernel;
		}

		public void setSsCpuRawKernel(Long ssCpuRawKernel) {
			SsCpuRawKernel = ssCpuRawKernel;
		}

		public String getSsErrorName() {
			return SsErrorName;
		}

		public void setSsErrorName(String ssErrorName) {
			SsErrorName = ssErrorName;
		}

		public Long getSsCpuRawWait() {
			return SsCpuRawWait;
		}

		public void setSsCpuRawWait(Long ssCpuRawWait) {
			SsCpuRawWait = ssCpuRawWait;
		}

		public Integer getSsIndex() {
			return SsIndex;
		}

		public void setSsIndex(Integer ssIndex) {
			SsIndex = ssIndex;
		}

		public Long getSsCpuRawIdle() {
			return SsCpuRawIdle;
		}

		public void setSsCpuRawIdle(Long ssCpuRawIdle) {
			SsCpuRawIdle = ssCpuRawIdle;
		}

		public Long getSsCpuRawSystem() {
			return SsCpuRawSystem;
		}

		public void setSsCpuRawSystem(Long ssCpuRawSystem) {
			SsCpuRawSystem = ssCpuRawSystem;
		}

		public Long getSsCpuRawNice() {
			return SsCpuRawNice;
		}

		public void setSsCpuRawNice(Long ssCpuRawNice) {
			SsCpuRawNice = ssCpuRawNice;
		}
	}
	
	/**
	 * Load Average Statics Details contains the list of 
	 * counters value.
	 */
	public static class LoadAveragetDetail {
		
		 /**
	     * Variable for storing the value of "LaLoadFloat".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.6".
	     * In the SNMP MIB, this is defined as a fixed length string of size 7.
	     */
	    private Byte[] LaLoadFloat = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

	    /**
	     * Variable for storing the value of "LaLoadInt".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.5".
	     */
	    private Integer LaLoadInt = new Integer(0);

	    /**
	     * Variable for storing the value of "LaConfig".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.4".
	     */
	    private String LaConfig = new String();

	    /**
	     * Variable for storing the value of "LaLoad".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.3".
	     */
	    private String LaLoad = new String();

	    /**
	     * Variable for storing the value of "LaErrMessage".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.101".
	     */
	    private String LaErrMessage = new String();

	    /**
	     * Variable for storing the value of "LaNames".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.2".
	     */
	    private String LaNames = new String();

	    /**
	     * Variable for storing the value of "LaIndex".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.1".
	     */
	    private Integer LaIndex = new Integer(0);

	    /**
	     * Variable for storing the value of "LaErrorFlag".
	     * The variable is identified by: "1.3.6.1.4.1.2021.10.1.100".
	     */
	    private EnumLaErrorFlag LaErrorFlag = new EnumLaErrorFlag();

	    
		public Byte[] getLaLoadFloat() {
			return LaLoadFloat;
		}

		public void setLaLoadFloat(Byte[] laLoadFloat) {
			LaLoadFloat = laLoadFloat;
		}

		public Integer getLaLoadInt() {
			return LaLoadInt;
		}

		public void setLaLoadInt(Integer laLoadInt) {
			LaLoadInt = laLoadInt;
		}

		public String getLaConfig() {
			return LaConfig;
		}

		public void setLaConfig(String laConfig) {
			LaConfig = laConfig;
		}

		public String getLaLoad() {
			return LaLoad;
		}

		public void setLaLoad(String laLoad) {
			LaLoad = laLoad;
		}

		public String getLaErrMessage() {
			return LaErrMessage;
		}

		public void setLaErrMessage(String laErrMessage) {
			LaErrMessage = laErrMessage;
		}

		public String getLaNames() {
			return LaNames;
		}

		public void setLaNames(String laNames) {
			LaNames = laNames;
		}

		public Integer getLaIndex() {
			return LaIndex;
		}

		public void setLaIndex(Integer laIndex) {
			LaIndex = laIndex;
		}

		public EnumLaErrorFlag getLaErrorFlag() {
			return LaErrorFlag;
		}

		public void setLaErrorFlag(EnumLaErrorFlag laErrorFlag) {
			LaErrorFlag = laErrorFlag;
		}
	}
}
