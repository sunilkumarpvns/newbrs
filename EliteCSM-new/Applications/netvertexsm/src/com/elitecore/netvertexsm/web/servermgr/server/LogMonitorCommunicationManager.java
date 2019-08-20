package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.HashMap;
import java.util.List;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

public class LogMonitorCommunicationManager {

	private static final String MODULE          = LogMonitorCommunicationManager.class.getSimpleName();
	private IRemoteCommunicationManager remoteCommunicationManager = null;

	public HashMap<String, List<LogMonitorInfo>> getLogMonitors(String adminHost, int adminPort,
			String netServerCode) throws Exception {
		
		Logger.logInfo(MODULE, "Enter getLogMonitors() method of " + getClass().getName());
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		remoteCommunicationManager.init(adminHost, adminPort, netServerCode, true);
		HashMap<String, List<LogMonitorInfo>> logMonitorsMap = null;
		try {
			logMonitorsMap = (HashMap<String, List<LogMonitorInfo>>) remoteCommunicationManager.execute(MBeanConstants.LOG_MONITOR_INFO, "listRegisteredLogMonitors", null ,null);
			if(logMonitorsMap!=null){
				Logger.logInfo(MODULE,"Total LogMonitor : "+logMonitorsMap.size());
			}else{
				Logger.logInfo(MODULE,"Empty LogMonitor : "+logMonitorsMap);
			}
		}catch (Exception e) {
			Logger.logError(MODULE, "Invalid License Data found  " + getClass().getName());
			Logger.logTrace(MODULE, e);
		}
		return logMonitorsMap;
	}
	
	public boolean addLogMonitors(String adminHost, int adminPort,
			String netServerCode , LogMonitorData logMonitorData) throws Exception {
		Logger.logInfo(MODULE, "Enter addLogMonitors() method of " + getClass().getName());
		
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		remoteCommunicationManager.init(adminHost, adminPort, netServerCode, true);
		
		String monitorType = logMonitorData.getLogMonitorType();
		String condition = logMonitorData.getExpression();
		Long durationMinutes = logMonitorData.getDuration();
	
		Object[] objArgValues = {monitorType,condition,durationMinutes};
		String[] strArgTypes = {monitorType.getClass().getName() ,	condition.getClass().getName(),	durationMinutes.getClass().getName()};
		boolean result = false;
		try {
			result = (Boolean) remoteCommunicationManager.execute(MBeanConstants.LOG_MONITOR_INFO,
					"addLogMonitor", objArgValues , strArgTypes);
		}catch (Exception e) {
			Logger.logError(MODULE, "Invalid License Data found  " + getClass().getName());
			Logger.logTrace(MODULE, e);
		}
		return result;
	}
	
	public boolean deleteLogMonitor(String adminHost, int adminPort, String netServerCode , LogMonitorData logMonitorData) throws Exception {
		Logger.logInfo(MODULE, "Enter deleteLogMonitor() method of " + getClass().getName());
		
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		remoteCommunicationManager.init(adminHost, adminPort, netServerCode, true);
		
		String monitorType = logMonitorData.getLogMonitorType();
		String condition = logMonitorData.getExpression();
	
		Object[] objArgValues = {monitorType,condition};
		String[] strArgTypes = {monitorType.getClass().getName() ,	condition.getClass().getName()};
		boolean result = false;
		try {
			result = (Boolean) remoteCommunicationManager.execute(MBeanConstants.LOG_MONITOR_INFO,"clearLogMonitor", objArgValues , strArgTypes);
		}catch (Exception e) {
			Logger.logError(MODULE, "Invalid License Data found  " + getClass().getName());
			Logger.logTrace(MODULE, e);
		}
		return result;
	}	
	
	public boolean deleteAllLogMonitors(String adminHost, int adminPort, String netServerCode) throws Exception {
		Logger.logInfo(MODULE, "Enter deleteAllLogMonitors() method of " + getClass().getName());
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		remoteCommunicationManager.init(adminHost, adminPort, netServerCode, true);
		
		try {
			remoteCommunicationManager.execute(MBeanConstants.LOG_MONITOR_INFO,"clearAllLogMonitors", null , null);
			return true;
		}catch (Exception e) {
			Logger.logError(MODULE, "Invalid License Data found  " + getClass().getName());
			Logger.logTrace(MODULE, e);
			return true;
		}
	}		
}