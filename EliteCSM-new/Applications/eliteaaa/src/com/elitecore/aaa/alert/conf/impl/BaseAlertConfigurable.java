package com.elitecore.aaa.alert.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.config.AlertListnersDetail;
import com.elitecore.aaa.core.data.FileAlertConfiguration;
import com.elitecore.aaa.core.data.FileTypeAlerts;
import com.elitecore.aaa.core.data.SNMPTrapAlertConfiguration;
import com.elitecore.aaa.core.data.SNMPTraps;
import com.elitecore.aaa.core.data.SysLogAlertConfiguration;
import com.elitecore.aaa.core.data.SysLogAlerts;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.serverx.alert.listeners.BaseAlertProcessor;
import com.elitecore.core.util.logger.AlertRollingFileLogger;
import com.elitecore.core.util.logger.EliteRollingFileLogger;


public abstract class BaseAlertConfigurable extends Configurable{
	
	private SNMPTraps snmpTraps;
	private SysLogAlerts sysLogAlerts;
	private FileTypeAlerts fileTypeAlerts;
	private static final String MODULE = "AAA_ALERT_CONFIGURABLE";
	
	public BaseAlertConfigurable(){
		//required By Jaxb.
		snmpTraps = new SNMPTraps();
		sysLogAlerts = new SysLogAlerts();
		fileTypeAlerts = new FileTypeAlerts();
	}
	
	@XmlElement(name = "file-listners")
	public FileTypeAlerts getFileTypeAlertsList() {
		return fileTypeAlerts;
	}

	public void setFileTypeAlertsList(FileTypeAlerts fileTypeAlerts) {
		this.fileTypeAlerts = fileTypeAlerts;
	}
	@XmlElement(name = "trap-listners")
	public SNMPTraps getSnmpTrapList() {
		return snmpTraps;
	}

	public void setSnmpTrapList(SNMPTraps snmpTraps) {
		this.snmpTraps = snmpTraps;
	}

	@XmlElement(name = "system-listners")
	public SysLogAlerts getSysLogAlertsList() {
		return sysLogAlerts;
	}

	public void setSysLogAlertsList(SysLogAlerts sysLogAlerts) {
		this.sysLogAlerts = sysLogAlerts;
	}
	
	@DBRead
	public void readAlertConfiguration() throws Exception {
		
		AlertListnersDetail alertListenersDetail = getAlertListnersDetail();
		List<String> alertListeners = alertListenersDetail.getAlertListnersList();
		
		Connection connection = null;
		PreparedStatement psForAlertListener = null;
		ResultSet rsForAlertListener = null;

		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			FileTypeAlerts fileTypeAlerts = new FileTypeAlerts();
			SNMPTraps snmpTraps = new SNMPTraps();
			SysLogAlerts sysLogAlerts = new SysLogAlerts();
			if(alertListeners!=null){
				int numOFListenerConfigured = alertListeners.size();
				String alertListenerName;
				
				List<FileAlertConfiguration> fileAlertListnerList = new ArrayList<FileAlertConfiguration>();
				List<SNMPTrapAlertConfiguration> snmpTrapAlertListnerList = new ArrayList<SNMPTrapAlertConfiguration>();
				List<SysLogAlertConfiguration> sysLogAlertListnerList = new ArrayList<SysLogAlertConfiguration>();
				
				
				for(int i=0;i<numOFListenerConfigured;i++){
					alertListenerName = alertListeners.get(i);
					
					psForAlertListener = connection.prepareStatement(getQueryForListenerDetail(alertListenerName));
					if(psForAlertListener != null){
						rsForAlertListener = psForAlertListener.executeQuery();

						if(rsForAlertListener.next()) {
							String alertTypeId = rsForAlertListener.getString("TYPEID");
							String alertIntanceId = rsForAlertListener.getString("INSTANCEID");

							if(BaseAlertProcessor.FILE_ALERT_PROCESSOR_ID.equalsIgnoreCase(alertTypeId)){
								FileAlertConfiguration fileAlertConfiguration = readFileAlertListenerConf(alertIntanceId,alertListenerName);
								if(fileAlertConfiguration!=null && fileAlertConfiguration.getEnabledId().size() >0)
									fileAlertListnerList.add(fileAlertConfiguration);
							}else if (BaseAlertProcessor.TRAP_ALERT_PROCESSOR_ID.equalsIgnoreCase(alertTypeId)) {
								SNMPTrapAlertConfiguration snmpTrapAlertConfiguration = readSNMPTrapAlertListenerConf(alertIntanceId,alertListenerName);
								if(snmpTrapAlertConfiguration!=null && snmpTrapAlertConfiguration.getEnabledId().size() >0)
									snmpTrapAlertListnerList.add(snmpTrapAlertConfiguration);
							}else if (BaseAlertProcessor.SYSLOG_ALERT_PROCESSOR_ID.equalsIgnoreCase(alertTypeId)) {
								SysLogAlertConfiguration sysLogAlertConfiguration = readSyslogAlertListenerConf(alertIntanceId,alertListenerName);
								if(sysLogAlertConfiguration!=null && sysLogAlertConfiguration.getEnabeldId().size() >0)
									sysLogAlertListnerList.add(sysLogAlertConfiguration);
							}
						}
					}}
				fileTypeAlerts.setFileAlertConfigurations(fileAlertListnerList);
				snmpTraps.setSnmpTrapAlertConfigurationsList(snmpTrapAlertListnerList);
				sysLogAlerts.setSysLogAlertConfigurationsList(sysLogAlertListnerList);
				
				this.fileTypeAlerts = fileTypeAlerts;
				this.snmpTraps = snmpTraps;
				this.sysLogAlerts = sysLogAlerts;
				
			}
		}finally{
			DBUtility.closeQuietly(rsForAlertListener);
			DBUtility.closeQuietly(psForAlertListener);
			DBUtility.closeQuietly(connection);
		}

	}

	@DBReload
	public void reloadAlertConfiguration(){
		
	}
	
	private SysLogAlertConfiguration readSyslogAlertListenerConf(String alertIntanceId, String name) throws Exception{


		SysLogAlertConfiguration sysLogAlertConfiguration = null;
		Connection connection = null;
		
		PreparedStatement psForFileListener = null;
		ResultSet rsForSysLogListener = null;
		
		PreparedStatement psForAlertListener = null;
		ResultSet rsForAlertListener = null;
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			psForFileListener = connection.prepareStatement(getQueryForSysLogListener(alertIntanceId));
			if(psForFileListener == null){
				throw new SQLException();
			}
			
			rsForSysLogListener = psForFileListener.executeQuery();
			String hostIp;
			
			while(rsForSysLogListener.next()) {
				
				hostIp = null;
				
				if( rsForSysLogListener.getString("ADDRESS")!=null &&  rsForSysLogListener.getString("ADDRESS").trim().length()>0){
					hostIp =  rsForSysLogListener.getString("ADDRESS");
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "ADDRESS Parameter for File Alert Configuration : "+name+" is not defined , so this Alert Listener is not added to valid listener list");
				}
				if(hostIp!=null){
					sysLogAlertConfiguration = new SysLogAlertConfiguration();
					sysLogAlertConfiguration.setLitenerName(name);
					sysLogAlertConfiguration.setListenerId(rsForSysLogListener.getString("SYSLOGINSTANCEID"));
					sysLogAlertConfiguration.setFacility(rsForSysLogListener.getString("FACILITY"));
					
					sysLogAlertConfiguration.setIsRepeatedMessageReduction(Boolean.parseBoolean(rsForSysLogListener.getString("REPEATEDMESSAGEREDUCTION")));
					
					PreparedStatement psForEnableAlertList = null;
					ResultSet rsForEnableAlertList = null;
					
					try{
						psForEnableAlertList = connection.prepareStatement(getQueryForEnableAlertList(alertIntanceId));
						if(psForEnableAlertList == null){
							throw new SQLException("Prepared Statement is null for the System Log Alerts");
						}
						rsForEnableAlertList = psForEnableAlertList.executeQuery();
						String enableAlert = null;
						List<String> supportedAlertList = new ArrayList<String>();
						while(rsForEnableAlertList.next()){
							enableAlert = rsForEnableAlertList.getString("ALERTTYPEID");
							supportedAlertList.add(enableAlert);
						}
						sysLogAlertConfiguration.setEnabeldId(supportedAlertList);
						
					}finally{
						DBUtility.closeQuietly(rsForEnableAlertList);
						DBUtility.closeQuietly(psForEnableAlertList);
					}
				}
			}
		}finally{
			DBUtility.closeQuietly(rsForAlertListener);
			DBUtility.closeQuietly(rsForSysLogListener);
			DBUtility.closeQuietly(psForAlertListener);
			DBUtility.closeQuietly(psForFileListener);
			DBUtility.closeQuietly(connection);
		}
		return sysLogAlertConfiguration;
	}

	private SNMPTrapAlertConfiguration readSNMPTrapAlertListenerConf(String instanceId, String alertListenerName) throws Exception{

		Connection connection = null;

		SNMPTrapAlertConfiguration trapAlertConfiguration = null;

		PreparedStatement psForTrapListener = null;
		ResultSet rsForTrapListener = null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			psForTrapListener = connection.prepareStatement(getQueryForTrapListener(instanceId));
			if(psForTrapListener !=null){
				rsForTrapListener = psForTrapListener.executeQuery();

				String trapServerIPAddress ;
				int trapVersion ;
				String community;

				while(rsForTrapListener.next()){
					trapAlertConfiguration = new SNMPTrapAlertConfiguration();

					trapServerIPAddress="127.0.0.1:162";
					trapVersion =1;
					community = "public";

					trapAlertConfiguration.setListenerId(rsForTrapListener.getString("TRAPLISTNERID"));
					trapAlertConfiguration.setName(alertListenerName);

					if(rsForTrapListener.getString("TRAPSERVER")!=null && rsForTrapListener.getString("TRAPSERVER").trim().length()>0){
						trapServerIPAddress = rsForTrapListener.getString("TRAPSERVER").trim();

					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Trap Server Parameter for Trap Alert Configuration : "+alertListenerName+" is not defined , using default value");

					}
					trapAlertConfiguration.setTrapServerAddress(trapServerIPAddress);

					if(rsForTrapListener.getString("TRAPVERSION")!=null && rsForTrapListener.getString("TRAPVERSION").trim().length()>0){
						trapVersion = Numbers.parseInt(rsForTrapListener.getString("TRAPVERSION").trim(), trapVersion);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Trap Version Parameter for Trap Alert Configuration : "+alertListenerName+" is not defined , using default value");
					}
					trapAlertConfiguration.setTrapVersion(trapVersion);

					if(rsForTrapListener.getString("COMMUNITY")!=null && rsForTrapListener.getString("COMMUNITY").trim().length()>0){
						community = rsForTrapListener.getString("COMMUNITY");
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Community Parameter for Trap Alert Configuration : "+alertListenerName+" is not defined , using default value");

					}
					trapAlertConfiguration.setCommunity(community);

					trapAlertConfiguration.setIsAdvanceTrap(Boolean.parseBoolean(rsForTrapListener.getString("ADVANCETRAP")));
					
					trapAlertConfiguration.setIsRepeatedMessageReduction(Boolean.parseBoolean(rsForTrapListener.getString("REPEATEDMESSAGEREDUCTION")));
					
					PreparedStatement psForEnableAlertList = null;
					ResultSet rsForEnableAlertList = null;				
					try{
						psForEnableAlertList = connection.prepareStatement(getQueryForEnableAlertList(instanceId));
						rsForEnableAlertList = psForEnableAlertList.executeQuery();
						String enableAlert = null;
						List<String> supportedAlertList = new ArrayList<String>();
						while(rsForEnableAlertList.next()){
							enableAlert = rsForEnableAlertList.getString("ALERTTYPEID");
							supportedAlertList.add(enableAlert);
						}
						trapAlertConfiguration.setEnabledId(supportedAlertList);

					}finally{
						DBUtility.closeQuietly(rsForEnableAlertList);
						DBUtility.closeQuietly(psForEnableAlertList);
					}

				}
			}
		}finally{
			DBUtility.closeQuietly(rsForTrapListener);
			DBUtility.closeQuietly(psForTrapListener);
			DBUtility.closeQuietly(connection);
		}
		return trapAlertConfiguration;
	}

	private FileAlertConfiguration readFileAlertListenerConf(String instanceId,String alertListenerName) throws Exception{

		
		Connection connection = null;
		
		PreparedStatement psForFileListener = null;
		ResultSet rsForFileListener = null;
		FileAlertConfiguration fileAlertConfiguration = null;
		
		PreparedStatement psForAlertListener = null;
		ResultSet rsForAlertListener = null;
		

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			psForFileListener = connection.prepareStatement(getQueryForFileListener(instanceId));
			if(psForFileListener != null){
				
			
			rsForFileListener = psForFileListener.executeQuery();
			
			String fileName;
			int rollingType;
			int rollingUnit;
			int maxRollingUnit;
			boolean compRollingUnit;
			
			while(rsForFileListener.next()) {
				fileAlertConfiguration = new FileAlertConfiguration();
				
				fileName = "elite-aaa-alert.log";
				rollingType = EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE;
				rollingUnit = EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY;
				maxRollingUnit = 10;
				compRollingUnit = false;
				
				fileAlertConfiguration.setListenerId(rsForFileListener.getString("FILELISTNERID"));
				fileAlertConfiguration.setName(alertListenerName);
				
				if( rsForFileListener.getString("FILENAME")!=null &&  rsForFileListener.getString("FILENAME").trim().length()>0){
					fileName =  rsForFileListener.getString("FILENAME");
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "File Name Parameter for File Alert Configuration : "+alertListenerName+" is not defined , using default value");
				}
				fileAlertConfiguration.setFileName(fileName);
				
				
				if(rsForFileListener.getString("ROLLINGTYPE")!=null && rsForFileListener.getString("ROLLINGTYPE").trim().length()>0){
					rollingType = Numbers.parseInt(rsForFileListener.getString("ROLLINGTYPE"),rollingType);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Rolling Type Parameter for File Alert Configuration : "+alertListenerName+" is not defined , using default value");
				}
				fileAlertConfiguration.setRollingType(rollingType);
				
				if(rsForFileListener.getString("ROLLINGUNIT")!=null && rsForFileListener.getString("ROLLINGUNIT").trim().length()>0){
					rollingUnit = Numbers.parseInt(rsForFileListener.getString("ROLLINGUNIT"),rollingUnit);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Rolling Unit Parameter for File Alert Configuration : "+alertListenerName+" is not defined , using default value");
				}
				
				fileAlertConfiguration.setRollingUnit(rollingUnit);
				
				if(rsForFileListener.getString("MAXROLLINGUNIT")!=null && rsForFileListener.getString("MAXROLLINGUNIT").trim().length()>0){
					maxRollingUnit = Numbers.parseInt(rsForFileListener.getString("MAXROLLINGUNIT"),maxRollingUnit);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Maximum Rolling Unit Parameter for File Alert Configuration : "+alertListenerName+" is not defined , using default value");
				}
				fileAlertConfiguration.setMaxRollingUnit(maxRollingUnit);
				
				compRollingUnit = Boolean.parseBoolean(rsForFileListener.getString("COMPROLLINGUNIT"));;
				fileAlertConfiguration.setIsCompRollingUnit(compRollingUnit);
				
				fileAlertConfiguration.setIsRepeatedMessageReduction(Boolean.parseBoolean(rsForFileListener.getString("REPEATEDMESSAGEREDUCTION")));
				
				PreparedStatement psForEnableAlertList = null;
				ResultSet rsForEnableAlertList = null;
				
				try{
					psForEnableAlertList = connection.prepareStatement(getQueryForEnableAlertList(instanceId));
					rsForEnableAlertList = psForEnableAlertList.executeQuery();
					List<String> supportedAlerts = new ArrayList<String>();
					while(rsForEnableAlertList.next()){
						String enableAlert = rsForEnableAlertList.getString("ALERTTYPEID");
						supportedAlerts.add(enableAlert);
					}
					fileAlertConfiguration.setEnabledId(supportedAlerts);
					
				}finally{
					DBUtility.closeQuietly(rsForEnableAlertList);
					DBUtility.closeQuietly(psForEnableAlertList);
				}
				
			}}
		}finally{
			DBUtility.closeQuietly(rsForAlertListener);
			DBUtility.closeQuietly(rsForFileListener);
			DBUtility.closeQuietly(psForAlertListener);
			DBUtility.closeQuietly(psForFileListener);
			DBUtility.closeQuietly(connection);
		}
		return fileAlertConfiguration;
	}

	@PostRead
	public void postReadProcessing() {
		if(this.snmpTraps!=null){
			List<SNMPTrapAlertConfiguration> configurationList = this.snmpTraps.getSnmpTrapAlertConfigurationsList();
			if(configurationList!=null){
				int numOFConf = configurationList.size();
				SNMPTrapAlertConfiguration snmpTrapAlertConfiguration;
				for(int i=0;i<numOFConf;i++){
					snmpTrapAlertConfiguration = configurationList.get(i);
					if(snmpTrapAlertConfiguration!=null){
						String tempServerURN = snmpTrapAlertConfiguration.getTrapServerAddress();
						String trapServerIPAddress="127.0.0.1";
						int trapServerPort =162;
						if(tempServerURN!=null){
							if (tempServerURN.indexOf(':') != -1) {
								trapServerIPAddress = tempServerURN.substring(0, tempServerURN.lastIndexOf(':'));
						    	if (tempServerURN.length() > tempServerURN.lastIndexOf(':')) {
						    		String port = tempServerURN.substring((tempServerURN.lastIndexOf(':') + 1));
						    		trapServerPort = Numbers.parseInt(port.trim(),trapServerPort);
						    	}
						    }
						}
						snmpTrapAlertConfiguration.setPort(trapServerPort);
						snmpTrapAlertConfiguration.setIpAddress(trapServerIPAddress);
					}
				}
			}
		}
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	private String getQueryForListenerDetail(String alertListenerName) {
		return "SELECT * FROM TBLMALERTINSTANCE WHERE NAME='" + alertListenerName +"'";
	}
	private String getQueryForFileListener(String instanceId) {
		return "SELECT * FROM TBLMFILELISTENER WHERE INSTANCEID='" + instanceId + "'";
	}

	private String getQueryForTrapListener(String instanceId) {
		return "SELECT * FROM TBLMTRAPLISTENER WHERE INSTANCEID='" + instanceId + "'";
	}

	private String getQueryForSysLogListener(String instanceId) {
		return "SELECT * FROM TBLMSYSLOGLISTENER WHERE INSTANCEID='" + instanceId + "'";
	}

	private String getQueryForEnableAlertList(String instanceId){
		return "SELECT * FROM TBLMALERTLISTENERREL WHERE INSTANCEID='" + instanceId + "'";
	}
	
	public abstract AlertListnersDetail getAlertListnersDetail();
	
}
