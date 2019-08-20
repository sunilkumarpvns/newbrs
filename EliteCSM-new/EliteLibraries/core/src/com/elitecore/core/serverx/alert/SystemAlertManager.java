package com.elitecore.core.serverx.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;


public abstract class SystemAlertManager {
	private static final String MODULE = "SYS-ALRT-MNGR";	
	private ServerContext serverContext;
	private boolean isServiceStarted ;
	private List<SystemAlert> unserveredAlertQueue;
	
	public SystemAlertManager(ServerContext context) {
		this.serverContext = context;
		unserveredAlertQueue = new ArrayList<SystemAlert>();
	}
	
	public synchronized void startService() {
		if (!isServiceStarted) {			
			isServiceStarted = true;
			generateAllUnservedAlert();
			this.serverContext.getTaskScheduler().scheduleIntervalBasedTask(new AlertDailyStatisticsUpdater(getAlertDataMap()));
		}
	}
	
	private void generateAllUnservedAlert(){
		for(SystemAlert alert : unserveredAlertQueue){
			scheduleAlert(alert);
		}
		unserveredAlertQueue.clear();
	}
	
	public void scheduleAlert(IAlertEnum alert, String alertGeneratorIdentity, String severity,String alertMessage) {
		try {
			scheduleAlert(new SystemAlert(alert, alertGeneratorIdentity, severity, alertMessage));
		}catch(Exception e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	public void scheduleAlert(IAlertEnum alert, String alertGeneratorIdentity, String severity,String alertMessage, int alertIntValue, String alertStringValue) {
		try {
			scheduleAlert(new SystemAlert(alert, alertGeneratorIdentity, severity, alertMessage, alertIntValue, alertStringValue));
		}catch(Exception e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	public void scheduleAlert(IAlertEnum alert, String alertGeneratorIdentity, String severity,String alertMessage,
			Map<com.elitecore.core.serverx.alert.Alerts,Object> alertData) {
		try {
			scheduleAlert(new SystemAlert(alert, alertGeneratorIdentity, severity, alertMessage, alertData));
		}catch(Exception e) {
			LogManager.getLogger().trace(e);
		}
	}	

	public void scheduleAlert(SystemAlert alert){
		try{
			if(isServiceStarted){
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new SystemAlertExecutor(alert));				
			}else{
				unserveredAlertQueue.add(alert);
			}
		}catch(RejectedExecutionException e){
			LogManager.getLogger().error(MODULE, "Alert could not be executed. reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}				
	}
	
	public void generateAlert(String severity,IAlertEnum alert, String alertGeneratorIdentity, String alertMessage) {
		try {
			handleSystemAlert(new SystemAlert(alert, alertGeneratorIdentity, severity, alertMessage));
		}catch(Exception e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	public void generateAlert(String severity,IAlertEnum alert, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue) {
		try {
			handleSystemAlert(new SystemAlert(alert, alertGeneratorIdentity, severity, alertMessage, alertIntValue, alertStringValue));
		}catch(Exception e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	private void handleSystemAlert(SystemAlert alert){
		IAlertData alertData = getAlertData(alert.getAlert().id());
		if(alertData != null){				
			alertData.handleSystemAlert(alert);
		}	
	}

	public synchronized void stopService() {
		if (isServiceStarted) {			
			isServiceStarted = false;			
		}
	}
	
	protected abstract IAlertData getAlertData(String alertId);

	protected abstract Map<String,IAlertData> getAlertDataMap();
	
	public final ServerContext getServerContext() {
		return this.serverContext;
	}
	
	public class SystemAlertExecutor extends BaseSingleExecutionAsyncTask{
		private SystemAlert alert;
		public SystemAlertExecutor(SystemAlert alert) {
			this.alert = alert;
		}
		@Override
		public void execute(AsyncTaskContext context) {
			handleSystemAlert(alert);
		}
	}

}
