package com.elitecore.aaa.alert;

import java.io.File;
import java.util.*;

import com.elitecore.aaa.alert.conf.impl.BaseAlertConfigurable;
import com.elitecore.aaa.alert.listeners.SNMPAlertProcessor;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurationImpl;
import com.elitecore.aaa.core.config.AlertListnersDetail;
import com.elitecore.aaa.core.data.FileAlertConfiguration;
import com.elitecore.aaa.core.data.FileTypeAlerts;
import com.elitecore.aaa.core.data.SNMPTrapAlertConfiguration;
import com.elitecore.aaa.core.data.SNMPTraps;
import com.elitecore.aaa.core.data.SysLogAlertConfiguration;
import com.elitecore.aaa.core.data.SysLogAlerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertData;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.SystemAlertManager;
import com.elitecore.core.serverx.alert.listeners.AggregatingAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.FileAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SysLogAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertEnum;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertManager;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
/*
 * Here Listener and processor are same thing. The reason why the name of the listener is not changed to processor at some place
 * here is that in GUI we do not have to change its name to processor as the name "listener" exist for long time.
 */
public class AAASystemAlertManager extends SystemAlertManager implements IStackAlertManager{
	private static final String MODULE = "AAA-SYSTEM-ALERT-MANAGER";
	private BaseAlertConfigurable alertConfiguration;
	private Map<String,IAlertData> alertDetailMap;

	private static final int TIME_BASED_ROLLING_TYPE = 1;
	private static final int TIME_BASED_ROLLING_EVERY_DAY = 5;
	private static final boolean COMPRESS_ROLLED_UNIT = false;
	private static final int MAX_ROLLED_UNIT = 10;

	private static final String LOGS_FOLDER = "logs";
	private static final String DFLT_ALRT_FOLDER = "alertlog";
	private static final String DFLT_ALERT_FILE = "eliteaaa-server-alert.log";

	
	public AAASystemAlertManager(AAAServerContext context) {
		super(context);		
		this.alertDetailMap = new HashMap<String, IAlertData>();
	}
	
	//FIXME this is to be validated if it is working fine
	public void initSevice() throws InitializationFailedException{
		AAAServerConfiguration aaaServerConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration();
		//AAA server configuration can be null in case when configuration reading is failed
		if(aaaServerConfiguration != null){
			this.alertConfiguration = aaaServerConfiguration.getAAAAlertManagerConfiguration();
			AlertListnersDetail alertListnersDetail = ((AAAServerConfigurationImpl)((AAAServerContext)getServerContext()).getServerConfiguration()).getAlertListenersDetail();
			if(alertListnersDetail !=null){
				List<String> configuredAlertListener = alertListnersDetail.getAlertListnersList();
				if(configuredAlertListener != null){
					initFileProcessors(alertConfiguration.getFileTypeAlertsList(),configuredAlertListener);
					initAggregatingSNMPTrapProcessors(alertConfiguration.getSnmpTrapList(),configuredAlertListener);
					initSysLogProcessors(alertConfiguration.getSysLogAlertsList(),configuredAlertListener);
				}
			}
		}
		readDefaultAlertData();
	}

	private void initSysLogProcessors(SysLogAlerts sysLogAlertsList,List<String> configuredAlertListener) {
		if(sysLogAlertsList!=null){
			List<SysLogAlertConfiguration> sysLogListenersConfList = sysLogAlertsList.getSysLogAlertConfigurationsList();
			
			if(sysLogListenersConfList!=null){
				int numOFConf = sysLogListenersConfList.size();
				SysLogAlertConfiguration sysLogAlertConfiguration;
				for(int i=0;i<numOFConf;i++){
					sysLogAlertConfiguration = sysLogListenersConfList.get(i);
					if(sysLogAlertConfiguration!=null){
						if(configuredAlertListener.contains(sysLogAlertConfiguration.getLitenerName())){
							try {
								SystemAlertProcessor alertProcessor = generateAlertProcessorForSysLog(sysLogAlertConfiguration);
								alertProcessor.init(); 
								readAlertData(sysLogAlertConfiguration.getEnabeldId(),alertProcessor);
							}catch(InitializationFailedException e) {
								LogManager.getLogger().trace(MODULE, e);					
								if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
									LogManager.getLogger().error(MODULE, "Error while initializing syslog alert processor reason: " + e.getMessage());
								}
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
									LogManager.getLogger().warn(MODULE, "Alerts will not be generated in syslog alert processor, Reason: " + e.getMessage());
								}
							}
						}
					}
				}
			}
		}
		
	}

	private SystemAlertProcessor generateAlertProcessorForSysLog(SysLogAlertConfiguration sysLogAlertConfiguration) {
		SystemAlertProcessor sysLogAlertProcessor = new SysLogAlertProcessor(getServerContext(),sysLogAlertConfiguration.getListenerId(),
																	sysLogAlertConfiguration.getLitenerName(),sysLogAlertConfiguration.getSyslogHost(),
																	sysLogAlertConfiguration.getFacility());
		if(!sysLogAlertConfiguration.getIsRepeatedMessageReduction()) {
			return sysLogAlertProcessor;
		}

		return generateAggregatingAlertProcessor(sysLogAlertProcessor);
	}

	private void initAggregatingSNMPTrapProcessors(SNMPTraps snmpTypeAlerts,List<String> configuredAlertListener) {

		if(snmpTypeAlerts!=null){
			List<SNMPTrapAlertConfiguration> snmpTrapListenersConfList = snmpTypeAlerts.getSnmpTrapAlertConfigurationsList();
			
			if(snmpTrapListenersConfList!=null){
				int numOFConf = snmpTrapListenersConfList.size();
				SNMPTrapAlertConfiguration snmpAlertConfiguration;
				for(int i=0;i<numOFConf;i++){
					snmpAlertConfiguration = snmpTrapListenersConfList.get(i);
					if(snmpAlertConfiguration!=null){
						if(configuredAlertListener.contains(snmpAlertConfiguration.getName())){
							try {
								SystemAlertProcessor alertProcessor = generateAlertProcessorForSNMP(snmpAlertConfiguration);
								alertProcessor.init();
								readAlertData(snmpAlertConfiguration.getEnabledId(),alertProcessor);
							}catch(InitializationFailedException e) {
								LogManager.getLogger().trace(MODULE, e);					
								if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
									LogManager.getLogger().error(MODULE, "Error while initializing snmp alert processor reason: " + e.getMessage());
								}
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
									LogManager.getLogger().warn(MODULE, "Alerts will not be generated in SNMP alert processor, Reason: " + e.getMessage());
								}
							}
						}
					}
				}
			}
		}
	}

	private SystemAlertProcessor generateAlertProcessorForSNMP(SNMPTrapAlertConfiguration snmpAlertConfiguration) throws InitializationFailedException {
		SNMPAlertProcessor snmpAlertProcessor = new SNMPAlertProcessor(getServerContext(), snmpAlertConfiguration.getName(), 
				snmpAlertConfiguration.getListenerId(), snmpAlertConfiguration.getIpAddress(),
				snmpAlertConfiguration.getPort(),snmpAlertConfiguration.getTrapVersion(),
				snmpAlertConfiguration.getCommunity(),snmpAlertConfiguration.getIsAdvanceTrap());

		if(!snmpAlertConfiguration.getIsRepeatedMessageReduction()) {
			return snmpAlertProcessor;
		}

		return generateAggregatingAlertProcessor(snmpAlertProcessor);
	}

	private void initFileProcessors(FileTypeAlerts fileTypeAlerts,List<String> configuredAlertListener) {
		if(fileTypeAlerts!=null){
			List<FileAlertConfiguration> fileListenersConfList = fileTypeAlerts.getFileAlertConfigurations();
			
			if(fileListenersConfList!=null){
				int numOFConf = fileListenersConfList.size();
				FileAlertConfiguration fileAlertConfiguration;
				for(int i=0;i<numOFConf;i++){
					fileAlertConfiguration = fileListenersConfList.get(i);
					if(fileAlertConfiguration!=null){
						if(configuredAlertListener.contains(fileAlertConfiguration.getName())){
							SystemAlertProcessor alertProcessor = generateAlertProcessorForFile(fileAlertConfiguration);
							try {
								alertProcessor.init(); 
								readAlertData(fileAlertConfiguration.getEnabledId(),alertProcessor);
							}catch(InitializationFailedException e) {
								LogManager.getLogger().trace(MODULE, e);					
								if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
									LogManager.getLogger().error(MODULE, "Error while initializing file alert processor reason: " + e.getMessage());
								}
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
									LogManager.getLogger().warn(MODULE, "Alerts will not be generated in file alert processor, Reason: " + e.getMessage());
								}
							}
						}
					}
				}
			}
		}
	}

	private SystemAlertProcessor generateAlertProcessorForFile(FileAlertConfiguration fileAlertConfiguration) {
		SystemAlertProcessor fileAlertProcessor = new FileAlertProcessor(getServerContext(),fileAlertConfiguration.getListenerId(), fileAlertConfiguration.getFileName(),
																fileAlertConfiguration.getRollingType(),fileAlertConfiguration.getRollingUnit(),
																fileAlertConfiguration.getMaxRollingUnit(),fileAlertConfiguration.getIsCompRollingUnit());
		if(!fileAlertConfiguration.getIsRepeatedMessageReduction()) {
			return fileAlertProcessor;
		}

		return generateAggregatingAlertProcessor(fileAlertProcessor);
	}

	private void readAlertData(List<String> supportedAlertList ,SystemAlertProcessor systemAlertProcessor) {
		if(supportedAlertList!=null){
			int numOfAlert = supportedAlertList.size();
			for(int i=0;i<numOfAlert;i++){
				readAlertData(supportedAlertList.get(i), systemAlertProcessor);
			}
		}
	}

	@Override
	public IAlertData getAlertData(String alertId) {
		if(alertId!=null){
			return alertDetailMap.get(alertId);
		}
		return null;
	}

	@Override
	public void scheduleAlert(StackAlertSeverity severity,IStackAlertEnum alert, String alertGeneratorIdentity,	String alertMessage) {
		
		DiameterAAAAlertRelation aaaAlertRelation = DiameterAAAAlertRelation.fromAlertId(alert.id());
		if (aaaAlertRelation == null) {
			LogManager.getLogger().warn(MODULE, "Alert relation not defined for Id: " + alert.id());
			return;
		}
		Alerts aaaAlert = Alerts.fromAlertId(aaaAlertRelation.aaaDiameterAlertId);
		if (aaaAlert == null) {
			LogManager.getLogger().warn(MODULE,  "Actual alert not defined for Id: " + alert.id());
			return;
		}
		super.scheduleAlert(aaaAlert, alertGeneratorIdentity, severity.name(),alertMessage);
		
	}
	
	@Override
	public void scheduleAlert(StackAlertSeverity severity,
			IStackAlertEnum alert, String alertGeneratorIdentity,
			String alertMessage, int alertIntValue, String alertStringValue) {
		
		DiameterAAAAlertRelation aaaAlertRelation = DiameterAAAAlertRelation.fromAlertId(alert.id());
		if (aaaAlertRelation == null) {
			LogManager.getLogger().warn(MODULE, "Alert relation not defined for Id: " + alert.id());
			return;
		}
		Alerts aaaAlert = Alerts.fromAlertId(aaaAlertRelation.aaaDiameterAlertId);
		if (aaaAlert == null) {
			LogManager.getLogger().warn(MODULE,  "Actual alert not defined for Id: " + alert.id());
			return;
		}
		super.scheduleAlert(aaaAlert, alertGeneratorIdentity, severity.name(),alertMessage, alertIntValue, alertStringValue);
		
	}
	
	private void readAlertData(String enabledAlert, SystemAlertProcessor systemAlertProcessor){
		Alerts alerts = Alerts.fromAlertId(enabledAlert);
		if(alerts == null){
			LogManager.getLogger().warn(MODULE, "Alert Id : " + enabledAlert +" is invalid");
			return;
		}
		AlertData alertData = (AlertData)alertDetailMap.get(alerts.alertId);
		if(alertData == null) {
			alertData = new AlertData();
			alertData.setAlertId(alerts.alertId);
			alertDetailMap.put(alerts.alertId, alertData);
		}
		alertData.addAlertListener(systemAlertProcessor);
	}
	private void readDefaultAlertData() {
		Alerts alerts[] = Alerts.VALUES;
		try{
			FileAlertProcessor defaultFileAlertProcessor = new FileAlertProcessor(getServerContext(), UUID.randomUUID().toString(), getDefaultAlertFile(),
					TIME_BASED_ROLLING_TYPE,TIME_BASED_ROLLING_EVERY_DAY,MAX_ROLLED_UNIT,COMPRESS_ROLLED_UNIT);

			defaultFileAlertProcessor.init();

			for(int cnt = 0; cnt < alerts.length; cnt++) {
				Alerts alert = alerts[cnt];
				readAlertData(alert.alertId, defaultFileAlertProcessor);
			}
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Default Listener Initialization Failed,Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}
	private String getDefaultAlertFile() {
		return getServerContext().getServerHome() + File.separator + LOGS_FOLDER + File.separator + DFLT_ALRT_FOLDER + File.separator + DFLT_ALERT_FILE;
   	}
	
	private SystemAlertProcessor generateAggregatingAlertProcessor(SystemAlertProcessor alertProcessor) {
		List<IAlertEnum> alertList = new ArrayList<IAlertEnum>(Arrays.asList(Alerts.values()));
		AggregatingAlertProcessor aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, getServerContext().getTaskScheduler(), alertList);
		return aggregatingAlertProcessor;
	}

	@Override
	protected Map<String, IAlertData> getAlertDataMap() {
		return alertDetailMap;
	}

}
