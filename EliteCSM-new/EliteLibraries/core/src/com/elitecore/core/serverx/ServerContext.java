package com.elitecore.core.serverx;

import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.systemx.esix.TaskScheduler;


public interface ServerContext {

	public String getServerHome();
	
	public String getServerName();
	
	public String getServerVersion();
	
	public String getServerMajorVersion();
	
	public String getServerDescription();
	
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage);
	
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, Map<Alerts,Object> alertData);
	
	public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage);
	
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue);
	
	public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue);
	
	public boolean isLicenseValid(String key, String value);

    public long getTPSCounter();
    
  //  public void resetTPSCounter();
    
    public void incrementTPSCounter();
    
    /**
     * Returns scheduler which can be used to execute tasks, for single or
     * periodic execution.
     * @return a non-null scheduler
     */
    public TaskScheduler getTaskScheduler();
    
    @Nullable public String getServerInstanceId();
    
    public String getServerInstanceName();
    
    public boolean isServerStartedWithLastConf();
    
    public ExternalScriptsManager getExternalScriptsManager();

	public String getSVNRevision();

	public String getReleaseDate();
	
	public long getServerStartUpTime();
	
	public String getLocalHostName();
	
	public String getContact();
	
    public void registerCacheable(Cacheable cacheable);
    
    public void registerStopable(Stopable stopable);
    
    public void sendSnmpTrap(SystemAlert alert , SnmpAlertProcessor alertProcessor);
    
    public long addTotalResponseTime(long responseTimeInNano);
    
    public int addAndGetAverageRequestCount(int delta);
    
    public EliteSnmpAgent getSNMPAgent();
}
