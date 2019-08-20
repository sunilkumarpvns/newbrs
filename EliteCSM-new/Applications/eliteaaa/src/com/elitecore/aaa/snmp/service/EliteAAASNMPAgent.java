package com.elitecore.aaa.snmp.service;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.mibs.rm.charging.client.RMChargingClientMIB;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.CHARGING_SERVICE_CLIENT_MIBImpl;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.ChargingServerEntryMBeanImpl;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.TableChargingServerStatisticsTableImpl;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.CHARGING_SERVICE_CLIENT_MIB;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.TableChargingServerStatisticsTable;
import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.IP_POOL_SERVICE_CLIENT_MIBImpl;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.IpPoolServerEntryMBeanImpl;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.TableIpPoolServerStatisticsTableImpl;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IP_POOL_SERVICE_CLIENT_MIB;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.TableIpPoolServerStatisticsTable;
import com.elitecore.aaa.mibs.server.autogen.ELITEAAA_TRAP_MIBOidTable;
import com.elitecore.aaa.mibs.sm.client.RemoteSessionManagerClientMIB;
import com.elitecore.aaa.radius.sessionx.snmp.LOCAL_SESSION_MANAGER_MIBImpl;
import com.elitecore.aaa.radius.sessionx.snmp.LocalSessionManagerEntryMBeanImpl;
import com.elitecore.aaa.radius.sessionx.snmp.REMOTE_SESSION_MANAGER_MIBImpl;
import com.elitecore.aaa.radius.sessionx.snmp.RemoteSessionManagerEntryMBeanImpl;
import com.elitecore.aaa.radius.sessionx.snmp.TableLocalSessionManagerStatsTableImpl;
import com.elitecore.aaa.radius.sessionx.snmp.TableRemoteSessionManagerStatTableImpl;
import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.LOCAL_SESSION_MANAGER_MIB;
import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.TableLocalSessionManagerStatsTable;
import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.REMOTE_SESSION_MANAGER_MIB;
import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.TableRemoteSessionManagerStatTable;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableDskTable;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableLaTable;
import com.elitecore.core.serverx.snmp.mib.os.autogen.UCD_SNMP_MIB;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider;
import com.elitecore.core.serverx.snmp.mib.os.extended.TableDskTableImpl;
import com.elitecore.core.serverx.snmp.mib.os.extended.TableLaTableImpl;
import com.elitecore.core.serverx.snmp.mib.os.extended.UCD_SNMP_MIBImpl;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;

/**
 * @author sanjay.dhamelia
 * @author narendra.pathai
 */
public class EliteAAASNMPAgent extends EliteSnmpAgent{


	private static final String MODULE = "ELITEAAA-SNMP-AGENT";
	
	private AAAServerContext serverContext;

	private LOCAL_SESSION_MANAGER_MIB local_session_manager_mib;
	private REMOTE_SESSION_MANAGER_MIB remote_session_manager_mib;
	private IP_POOL_SERVICE_CLIENT_MIB ipPool_client_mib;
	private CHARGING_SERVICE_CLIENT_MIB charging_client_mib;
	
	private int localSMIndex = 1;
	private TableLocalSessionManagerStatsTable localSessionManagerTable;
	
	private final static String DEFAULT_SNMP_ADDRESS = "0.0.0.0";
	private final static int DEFAULT_SNMP_PORT = 1161;
	private final static int DEFAULT_HTTP_ADAPTOR_PORT = 8082;

	private final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	private Optional<SystemDetailProvider> systemDetailProvider = Optional.absent();
	
	/**
	 * Constructor Mainly used to Initialize the Dummy SnmpAgent 
	 * on ip:port = 0.0.0.0:161. Jira = ELITEAAA-2459
	 * @param serverContext
	 * @param enterpriseOid
	 */
	public EliteAAASNMPAgent(AAAServerContext serverContext,String enterpriseOid){
		this(serverContext, enterpriseOid, DEFAULT_SNMP_ADDRESS, DEFAULT_SNMP_PORT);
	}
	
	public EliteAAASNMPAgent(AAAServerContext serverContext, String enterpriseOid , String ipAddress , int port) {
		super(serverContext, new SnmpOid(enterpriseOid), ipAddress, port);
		this.serverContext = serverContext;
	}

	@Override
	public void init() throws InitializationFailedException {
		super.init();
		initSystemDetailProvider();
		initializeAndRegisterMIBs();
		registerSnmpTrapTable();
	}
	
	/**
	 * Initialize System Detail provider which fetches
	 * the OS Details including memory,storage,loadAverage
	 * using SNMP Protocol.
	 * 
	 * The OS for which statistics needs to be fetch is provided using 
	 * miscellaneous configuration parameter - os.snmp.address.
	 * 
	 * <param name="os.snmp.address" value="ipAddress:port"/>
	 *  
	 * if it is not configured then UCD_SNMP_MIB will not be initialize. 
	 */
	private void initSystemDetailProvider() throws InitializationFailedException {
		String address = System.getProperty(MiscellaneousConfigurable.OS_SNMP_ADDRESS);
		
		if (Strings.isNullOrBlank(address)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "System Detail Provider will not start. Reason: "+
						MiscellaneousConfigurable.OS_SNMP_ADDRESS +" is not configured.");
			}
			return;
		}
		
		try {
			
			URLData urlData = URLParser.parse(address);
			
			SystemDetailProvider osDetailProvider = new SystemDetailProvider(urlData, serverContext.getTaskScheduler());
			osDetailProvider.start();
			
			systemDetailProvider = Optional.of(osDetailProvider);
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "System Detail Provider started successfully " +
						"using address: " + urlData.getHost() + ":" + urlData.getPort());
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to start System Detail Provider. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace("Failed to start System Detail Provider.", e);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (systemDetailProvider.isPresent()) {
			systemDetailProvider.get().stop();
		}
	}
	
	private void initializeAndRegisterMIBs(){
		
		initAndRegisterIPPoolClientMIB();
		
		initAndRegisterRMChargingClientMIB();
		
		initAndRegisterRemoteSMMIB();
		
		initAndRegisterLocalSMMIB();
		
		initAndRegisterUCDSnmpMIB();
	}

	/**
	 * Initialize and Register IP-Pool Server MIB.
	 */
	private void initAndRegisterIPPoolClientMIB() {
		try {
			ipPool_client_mib = new IP_POOL_SERVICE_CLIENT_MIBImpl();
			ipPool_client_mib.populate(mbeanServer, null);

			TableIpPoolServerStatisticsTable ippoolServerTable = new TableIpPoolServerStatisticsTableImpl(ipPool_client_mib,mbeanServer);

			Map<String, String> ipPoolServerNameToAddressMap = RMIPPoolClientMIB.getIpPoolServerNameToAddressMap();

			int ipPoolServerIndex = 1;
			String ipPoolEsiName = null;
			
			try{
				for (Entry<String, String> ipPoolServerEntrySet : ipPoolServerNameToAddressMap.entrySet()) {
					
					ipPoolEsiName = ipPoolServerEntrySet.getKey();
					IpPoolServerEntryMBeanImpl ipPoolServerEntry = new IpPoolServerEntryMBeanImpl(ipPoolServerIndex, ipPoolEsiName, ipPoolServerEntrySet.getValue());
					ippoolServerTable.addEntry(ipPoolServerEntry,new ObjectName(ipPoolServerEntry.getObjectName()));

					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Entry For  IP-Pool Server: " + ipPoolEsiName + " successfully added into the IP-Pool Server table at Index: " + ipPoolServerIndex );
					}
					ipPoolServerIndex++;
				}
				
			}catch(SnmpStatusException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For IP-Pool Server: "+ipPoolEsiName+" in IP-Pool Server table. Reason: " + ex.getMessage());
				}
				LogManager.getLogger().trace(MODULE, ex);
			}catch(MalformedObjectNameException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For IP-Pool Server: "+ipPoolEsiName+" in IP-Pool Server table. Reason: " + ex.getMessage());
				}
				LogManager.getLogger().trace(MODULE, ex);
			}
			
			super.registerMib(ipPool_client_mib);
			
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to initialize IP-Pool Client MIB, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}
 
	/**
	 * Initialize and Register Charging Server MIB.
	 */
	private void initAndRegisterRMChargingClientMIB() {
		try {
			charging_client_mib = new CHARGING_SERVICE_CLIENT_MIBImpl();
			charging_client_mib.populate(mbeanServer, null);

			String chargingEsiName = null;
			try{

				TableChargingServerStatisticsTable chargingServerTable = new TableChargingServerStatisticsTableImpl(charging_client_mib,mbeanServer);
				int chargingServerIndex = 1;
				Map<String, String> chargingServerNameToAddressMap = RMChargingClientMIB.getChargingServerNameToAddressMap();

				for (Entry<String, String> chargingGw : chargingServerNameToAddressMap.entrySet()) {
					chargingEsiName = chargingGw.getKey();
					
					ChargingServerEntryMBeanImpl chargingServerEntry = new ChargingServerEntryMBeanImpl(chargingServerIndex, chargingEsiName, chargingGw.getValue());
					chargingServerTable.addEntry(chargingServerEntry,new ObjectName(chargingServerEntry.getObjectName()));
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Entry For Charging Server: " + chargingEsiName + " successfully added into the Charging Server table at Index: " + chargingServerIndex );
					}
					chargingServerIndex++;
				}
			}catch(SnmpStatusException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For Charging Server: "+chargingEsiName+" in Charging Server table. Reason: " + ex.getMessage());
				}
				LogManager.getLogger().trace(MODULE, ex);			
			}catch (MalformedObjectNameException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For Charging Server: "+chargingEsiName+" in Charging Server table. Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);			

			}
		
			super.registerMib(charging_client_mib);

		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to initialize RM Charging Client MIB, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}

	private void initAndRegisterLocalSMMIB() {
		/**
		 * Initialize and Register the Local-Session-Manager-MIB.
		 */
		try{
			this.local_session_manager_mib = new LOCAL_SESSION_MANAGER_MIBImpl();
			local_session_manager_mib.populate(mbeanServer, null);
			localSessionManagerTable = new TableLocalSessionManagerStatsTableImpl(local_session_manager_mib,mbeanServer);
			serverContext.registerSnmpMib(local_session_manager_mib);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to initialize Session Manager MIB, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}
	
	private void initAndRegisterRemoteSMMIB() {
		/**
		 * Initialize and Register the Remote-Session-Manager-MIB.
		 */
		try{
			this.remote_session_manager_mib = new REMOTE_SESSION_MANAGER_MIBImpl();
			remote_session_manager_mib.populate(mbeanServer, null);
			
			String remoteSMName = null;

			try {
				
				int remoteSMIndex = 1;
				Map<String, String> remoteSMNameToAddressMap = RemoteSessionManagerClientMIB.getRemoteSmESINameToAddressMap();
				TableRemoteSessionManagerStatTable remoteSessionManagerTable = new TableRemoteSessionManagerStatTableImpl(remote_session_manager_mib,mbeanServer);

				for (Entry<String, String> remoteSM : remoteSMNameToAddressMap.entrySet()) {
					remoteSMName = remoteSM.getKey();
					
					RemoteSessionManagerEntryMBeanImpl remoteSMEntry = new RemoteSessionManagerEntryMBeanImpl(remoteSMIndex, remoteSM.getKey(), remoteSM.getValue());
					remoteSessionManagerTable.addEntry(remoteSMEntry,new ObjectName(remoteSMEntry.getObjectName()));
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Entry For Remote SM: " + remoteSMName + " successfully added into the Remote SM table at Index: " + remoteSMIndex );
					}
					remoteSMIndex++;
				}
					
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For Remote SM: " + remoteSMName + " in the Remote SM table, Reason: " + e.getMessage() );
				}
				LogManager.getLogger().trace(e);
			}catch(MalformedObjectNameException e){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry For Remote SM: " + remoteSMName + " in the Remote SM table, Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(e);
			}
		
			super.registerMib(remote_session_manager_mib);

		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to initialize Session Manager MIB, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}
	
	protected void registerSnmpTrapTable(){
		super.registetSnmpTrapTable(new ELITEAAA_TRAP_MIBOidTable());
	}
	
	public void expose(LocalSessionManagerEntryMBeanImpl localSessionManagerEntry){

		if(RUNNING.equals(getState())){
			String localSMName = null;
			try {
				localSMName = localSessionManagerEntry.getSmName();
				localSessionManagerTable.addEntry(localSessionManagerEntry,new ObjectName(localSessionManagerEntry.getObjectName()));
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Entry For Session Manager: " + localSMName + " successfully added into the Local Session Manager table at Index: " + localSessionManagerEntry.getSmIndex());
				}
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Local Session Manager: "+ localSMName+" in Local Session Manager table. Reason: "+e.getMessage());
				}
				LogManager.getLogger().trace(e);
			}catch (MalformedObjectNameException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Local Session Manager: "+ localSMName+" in Local Session Manager table. Reason: "+e.getMessage());
				}
				LogManager.getLogger().trace(e);
			}
		}
	}

	/**
	 * Maintain the index for LocalSessionManagerEntry.
	 */
	public final int incrementAndGetLocalSMIndex() {
		return localSMIndex++;
	}
	
	/**
	 * Get the Http Adaptor port
	 * from system property 
	 * java.websnmp.port
	 * if it is null default port 8082
	 * will be returned.
	 * @return httpAdpatorPort
	 */
	public int getHttpAdaptorPort() {
		String httpAdaptorPortStr = System.getProperty(CommonConstants.WEBSNMP_PORT);
		
		if(httpAdaptorPortStr == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Port is not specified for HTTP Adaptor, So taking default port: "+DEFAULT_HTTP_ADAPTOR_PORT);
			}
			return DEFAULT_HTTP_ADAPTOR_PORT;
		}
		
		return Numbers.parseInt(httpAdaptorPortStr, DEFAULT_HTTP_ADAPTOR_PORT); 
	}
	
	/**
	 * Initialize and Register the UCD_SNMP_MIB which is used to expose the 
	 * statistics of OS.
	 * 
	 * Need to set the table from init as MIB reference is required for creation
	 * of snmp table.
	 */
	private void initAndRegisterUCDSnmpMIB() {
		
		if(systemDetailProvider.isPresent()) {
		
			SystemDetailProvider osDetailProvider = systemDetailProvider.get();
			UCD_SNMP_MIB ucd_snmp_mib = new UCD_SNMP_MIBImpl(osDetailProvider);
			try {
				ucd_snmp_mib.populate(mbeanServer, null);

				TableLaTable loadAverageTable = new TableLaTableImpl(ucd_snmp_mib,mbeanServer);
				osDetailProvider.setLoadAverageTable(loadAverageTable);

				TableDskTable dskTable = new TableDskTableImpl(ucd_snmp_mib,mbeanServer);
				osDetailProvider.setDskTable(dskTable);
				serverContext.registerSnmpMib(ucd_snmp_mib);

			} catch (Exception e) {
				if(LogManager.getLogger().isErrorLogLevel()){
					LogManager.getLogger().error(MODULE, "Failed to initialize UCD SNMP MIB, Reason: "+e.getMessage());
				}
				LogManager.getLogger().trace(e);
			}
		}
	}
}