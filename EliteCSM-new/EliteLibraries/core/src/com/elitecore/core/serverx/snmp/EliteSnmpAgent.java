package com.elitecore.core.serverx.snmp;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.snmp.mib.mib2.extended.RFC1213_MIBImpl;
import com.elitecore.core.serverx.snmp.mib.mib2.extended.SnmpImpl;
import com.elitecore.core.serverx.snmp.mib.mib2.extended.SystemImpl;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.sun.management.comm.SnmpV3AdaptorServer;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpOidRecord;
import com.sun.management.snmp.SnmpOidTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpVarBind;
import com.sun.management.snmp.SnmpVarBindList;
import com.sun.management.snmp.IPAcl.JdmkAcl;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.manager.SnmpSession;

public class EliteSnmpAgent {


	private static String MODULE = "SNMP-AGENT";
	private static final String UNIVERSAL_IP = "0.0.0.0";

	private final int Generic_EnterpriseSpecific = 6;
	private final int Specific = 0;

	private SnmpOid enterpriseOid;
	private int port;
	private String ipAddress;
	private ServerContext serverContext;
	private ServiceRemarks serviceRemarks;
	private SnmpV3AdaptorServer snmpAdaptor;
	private SnmpSession snmpSession;
	private ArrayList<SnmpOidTable> snmpTrapTables;
	private Date startDate;
	private SnmpImpl snmp;
	
	private final String ALERT_SEVERITY = "alertSeverity";
	private final String SERVER_INSTANCE_ID = "serverInstanceId";
	
	/**
	 * Possible SNMP Agent status.
	 */
	
	public final static String STARTUP_IN_PROGRESS = "Startup In Progress";
	public final static String RUNNING = "Running";
	public final static String NOT_STARTED = "Not Started";
	public final static String SHUTDOWN_IN_PROGRESS = "Shutdown In Progress";
	public final static String STOPPED = "Stopped";

	private String currentState = NOT_STARTED;
	
	public EliteSnmpAgent(ServerContext serverContext, SnmpOid enterpriseOid , String ipAddress , int port){
		this.serverContext = serverContext;
		this.enterpriseOid = enterpriseOid;
		this.ipAddress = ipAddress;
		this.port = port;
		this.snmpTrapTables = new ArrayList<SnmpOidTable>(1);		
	}
	
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing SNMP Agent");

		currentState = STARTUP_IN_PROGRESS;
		try {

			InetAddress snmpInetAddress = null;
			try {
				snmpInetAddress = InetAddress.getByName(ipAddress);
				validatePort(snmpInetAddress);
			} catch (UnknownHostException e) {
				snmpInetAddress = InetAddress.getByName(UNIVERSAL_IP);
				LogManager.getLogger().info(MODULE, "Problem in binding configured address: "+ this.ipAddress + 
						", so SNMP Agent will start using universal ip address: " + UNIVERSAL_IP);
				validatePort(snmpInetAddress);
				serviceRemarks = ServiceRemarks.STARTED_ON_UNIVERSAL_IP;
			} 
			
			this.ipAddress = snmpInetAddress.getHostAddress();

			// If file not found then authorization is disabled

			JdmkAcl jdmkAcl = new JdmkAcl("EliteJdmkAcl", 
					serverContext.getServerHome()+ File.separator + "system" + File.separator + "jdmk.acl");
			snmpAdaptor = new SnmpV3AdaptorServer(jdmkAcl, port, snmpInetAddress);

			snmpAdaptor.enableSnmpV1V2SetRequest();
			snmp = new SnmpImpl(snmpAdaptor);
			
			snmpAdaptor.start();
			
			int count = 0;
			boolean isStarted = false;
			while(count<100){
				
				if(snmpAdaptor.getState() != SnmpV3AdaptorServer.ONLINE){
					Thread.sleep(10);
				}else{
					isStarted = true;
					break;
				}
				count++;
			}
			
			if (!isStarted) {
				throw new ServiceInitializationException("Cannot bind IP-Port: " + ipAddress + ":" + port, ServiceRemarks.PROBLEM_BINDING_IP_PORT);
			}
			
			try {
				snmpSession = new SnmpSession("EliteSNMPSession");
			} catch (Exception e) {
				throw new ServiceInitializationException("SNMP Agent failed to create SNMP Session to send inform requests. Reason: " 
						+ e.getMessage() , ServiceRemarks.UNKNOWN_PROBLEM, e);
			}
			
			startDate = new Date();
			currentState = RUNNING;
			
		} catch(ServiceInitializationException e) {
			currentState = NOT_STARTED;
			serviceRemarks = e.getRemark();
			throw new InitializationFailedException(e);
		} catch (Exception e) {
			currentState = NOT_STARTED;
			serviceRemarks = ServiceRemarks.UNKNOWN_PROBLEM;
			throw new InitializationFailedException(e);
		}

		LogManager.getLogger().info(MODULE, "SNMP Agent started successfully");
	}

	private void validatePort(InetAddress snmpInetAddress)
			throws InitializationFailedException {
		// This socket is created to check weather Port is already is in use or not
		DatagramSocket socket;
		try{
			socket = new DatagramSocket(port, snmpInetAddress);
			socket.close();
		}catch (Exception e) {
			serviceRemarks = ServiceRemarks.PROBLEM_BINDING_IP_PORT;
			throw new InitializationFailedException(e);
		}
	}
	
	public void sendTrap(SystemAlert alert,SnmpAlertProcessor snmpAlertProcessor) {

		SnmpOid oid;
		SnmpOidRecord oidRecord = resolveByName(alert.getAlert().getName());
		if(oidRecord == null){
			oid = new SnmpOid(alert.getAlert().oid());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Sending Trap Alert: " + alert.getAlert().getName() + " with provided specific Oid: "+alert.getAlert().oid()+" to "
						+ snmpAlertProcessor.getName() + "Reason: Alert Name not found in Oid Table");
			}
		}else{
			oid = new SnmpOid(oidRecord.getOid());
		}

		SnmpVarBind varBind = new SnmpVarBind(oid, new SnmpString(alert.getDescription()));

		SnmpVarBindList varBindList = new SnmpVarBindList(5);
		varBindList.addVarBind(varBind);

		if(snmpAlertProcessor.isAdvanceTrap()) {
			addAdvancedTrapDetails(alert, varBindList);
		}

		TrapVersion trapVersion = snmpAlertProcessor.getVersion();
		try {
			switch (trapVersion) {
			case V1:
				sendSNMPV1Trap(alert, snmpAlertProcessor, varBindList);
				break;
			case V2c:
				sendSNMPV2Trap(alert, snmpAlertProcessor, oid, varBindList);
				break;
			default:
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Unable to send SNMP Alert: " + alert.getAlert().getName() + 
							"  to SNMP listener: " + snmpAlertProcessor.getName() + " Reason: Trap Version not specified");
				}
				break;
			}

		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while sending SNMP Alert to snmp listener " + snmpAlertProcessor.getName() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Error while sending SNMP Alert to snmp listener " + snmpAlertProcessor.getName() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void sendSNMPV2Trap(SystemAlert alert, SnmpAlertProcessor snmpAlertProcessor, SnmpOid oid,
			SnmpVarBindList varBindList) throws IOException, SnmpStatusException {
		SnmpOidRecord oidRecord;
		SnmpVarBind varBind;
		if(snmpAlertProcessor.getSnmpRequestType() == SnmpRequestType.TRAP){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Sending SNMP Trap: " + alert.getAlert().getName() + 
						" to SNMP listener: " + snmpAlertProcessor.getName());
			}

			if (alert.getAlertDataMap() != null && alert.getAlertDataMap().size() > 0) {
				SnmpOid alertDataOid;
				for(Entry<Alerts, Object > entry :alert.getAlertDataMap().entrySet()) {
					oidRecord = resolveByName(entry.getKey().name());
					if(oidRecord == null){
						alertDataOid = new SnmpOid(entry.getKey().oid());
					}else{
						alertDataOid = new SnmpOid(oidRecord.getOid());
					}
					varBind = new SnmpVarBind(alertDataOid, new SnmpString(entry.getValue().toString()));
					varBindList.addVarBind(varBind);
				}
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Sending Trap Alert: " + alert.getAlert().getName() + " with provided specific Oid: "+alert.getAlert().oid()+" to "
						+ snmpAlertProcessor.getName() + "Reason: Alert Name not found in Oid Table");
			}
			// SnmpTimeticks will be ( Current Time -  Adaptor Startup time )
			snmpAdaptor.snmpV2Trap(snmpAlertProcessor.getSnmpPeer(), oid, varBindList, null);

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "SNMP Trap: " + alert.getAlert().getName() + 
						" successfully sent to SNMP listener: " + snmpAlertProcessor.getName());
			}

		}else if(snmpAlertProcessor.getSnmpRequestType() == SnmpRequestType.INFORM){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Sending SNMP Inform: " + alert.getAlert().getName() + 
						" to SNMP listener: " + snmpAlertProcessor.getName());
			}

			//session.destroySession();
			snmpSession.snmpInformRequest(snmpAlertProcessor.getSnmpPeer(), null, oid, varBindList);

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "SNMP Inform: " + alert.getAlert().getName() + 
						" successfully sent to SNMP listener: " + snmpAlertProcessor.getName());
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "SNMP Alert: " + alert.getAlert().getName() + 
						" sending skipped. Reason: Invalid Request type: " + snmpAlertProcessor.getSnmpRequestType() + 
						" configured in SNMP listener: " + snmpAlertProcessor.getName());
			}
		}
	}

	private void sendSNMPV1Trap(SystemAlert alert, SnmpAlertProcessor snmpAlertProcessor, SnmpVarBindList varBindList)
			throws IOException, SnmpStatusException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Sending SNMP Trap: " + alert.getAlert().getName() + 
					" to SNMP listener: " + snmpAlertProcessor.getName());
		}
		// agentAddr will be local address 
		// If the local host cannot be determined, it takes 0.0.0.0
		// SnmpTimeticks will be ( Current Time -  Adaptor Startup time )
		snmpAdaptor.snmpV1Trap(snmpAlertProcessor.getSnmpPeer(), null, enterpriseOid, Generic_EnterpriseSpecific, Specific , varBindList, null);

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "SNMP Trap: " + alert.getAlert().getName() + 
					" successfully sent to SNMP listener: " + snmpAlertProcessor.getName());
		}
	}

	/**
	 * This method will add the snmp variables like alert severity, serverInstanceId
	 * in generated alerts.
	 * Application specific snmp variable can be added into generated trap
	 * by implementing this method. 
	 * 
	 * @param alert represents the alert generated from the system
	 * @param varBindList represents the number of snmp variables need to send in alerts
	 */
	protected void addAdvancedTrapDetails(SystemAlert alert, SnmpVarBindList varBindList) {
		addAlertSeverity(alert, varBindList);
		addServerInstanceId(alert, varBindList);
	}

	private void addAlertSeverity(SystemAlert alert, SnmpVarBindList varBindList) {
		SnmpOidRecord serverityRecord = resolveByName(ALERT_SEVERITY);
		if (serverityRecord == null) {
			LogManager.getLogger().error(MODULE, "Alert Severity not added in Alert: " + alert.getAlert().getName() 
					+ ". Reason: Alert Severity not found in Oid Table");
		} else {
			SnmpOid serverityOid = new SnmpOid(serverityRecord.getOid());
			SnmpVarBind serverityVarBind = new SnmpVarBind(serverityOid, new SnmpString(alert.getSeverity()));
			varBindList.addVarBind(serverityVarBind);
		}
	}

	private void addServerInstanceId(SystemAlert alert, SnmpVarBindList varBindList) {
		SnmpOidRecord serverInstanceIdOidRecord = resolveByName(SERVER_INSTANCE_ID);
		
		if (serverInstanceIdOidRecord == null) {
			LogManager.getLogger().error(MODULE, "Server Instance Id not added in alert: "+ alert.getAlert().getName() 
					+ ". Reason: Server Instance Id not found in oid table");
		} else {
			SnmpOid serverInstanceIdOid = new SnmpOid(serverInstanceIdOidRecord.getOid());
			SnmpVarBind serverInstanceIdVarBind = new SnmpVarBind(serverInstanceIdOid, 
					new SnmpString(serverContext.getServerInstanceName()));
			varBindList.addVarBind(serverInstanceIdVarBind);
		}
	}

	public void registerMib(SnmpMib snmpMib){
		if(snmpMib == null){
			LogManager.getLogger().error(MODULE, "Can't register SNMP Mib to Elite SNMP Agent. Reason: " 
					+ "Snmp Mib is null");
			return;
		}
		snmpAdaptor.addMib(snmpMib);
		LogManager.getLogger().info(MODULE,"SNMP MIB: " + snmpMib.getMibName() + " registered Successfully");
	}

	public void registetSnmpTrapTable(SnmpOidTable snmpOidTable){

		if(snmpOidTable == null){
			LogManager.getLogger().error(MODULE, "Can't register SNMP Oid Table to Elite SNMP Agent. Reason: " 
					+ "Snmp Oid Table is null");
			return;
		}
		snmpTrapTables.add(snmpOidTable);
	}

	private SnmpOidRecord resolveByName(String alertName) {
		SnmpOidRecord oidRecord = null;
		for (SnmpOidTable oidTable : snmpTrapTables) {
			try {
				return oidTable.resolveVarName(alertName);
			} catch (SnmpStatusException e) {
				// SnmpStatusException will occur only in case when alert Name can't be Resolved.
				// Log is Already there when method return NULL.
			}
		}
		return oidRecord;
	}
	
	public void stop(){
		currentState= SHUTDOWN_IN_PROGRESS;
		
		/*
		 * Sleeping the current thread for completion of tasks of SNMP Agent
		 * (e.g. sending inform request to SNMP Server and wait for its response)*/
			try {
				
				LogManager.getLogger().info(MODULE, "Waiting 1sec. for SNMP Agent to complete its task before stop SNMP Agent");
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		
			LogManager.getLogger().info(MODULE, "Stopping SNMP Agent");
			
		if(snmpAdaptor != null) {
			snmpAdaptor.stop();
		}
		
		if(snmpSession != null) {	
			snmpSession.destroySession();
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Snmp Agent Stopped Successfully");
		}
		currentState= STOPPED;
	}

	public void loadRFC1213Mib(String sysDescr, String sysObjId, long sysUpTime,
			String sysContact, String sysName, String sysLocation) {
		try{
			SystemImpl system= new SystemImpl(sysDescr , sysObjId, sysUpTime, sysContact, sysName, sysLocation);
			RFC1213_MIBImpl rfc1213_MIB = new RFC1213_MIBImpl(snmp, system);
			rfc1213_MIB.init();
			registerMib(rfc1213_MIB);
		}catch (IllegalAccessException e) {
			LogManager.getLogger().error(MODULE, "Unable to load RFC1213 Mib. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	public Date getStartDate(){		
		return startDate;
	}
		
	public int getPort() {
		if(snmpAdaptor!=null){
			return snmpAdaptor.getPort();
		}
		return port;
	}
	
	public String getIPAddress() {
		return ipAddress;
	}
	
	public String getState(){
		return currentState;
	}
	
	public String getRemarks() {
		if(serviceRemarks != null){
			return serviceRemarks.remark;
		}
		return "";
	}
	
	public SnmpImpl getSnmpGroup() {
		return snmp;
	}
}