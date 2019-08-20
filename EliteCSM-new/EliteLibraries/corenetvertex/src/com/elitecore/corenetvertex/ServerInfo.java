package com.elitecore.corenetvertex;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.util.LeakyBucket;
import com.elitecore.corenetvertex.util.SystemPropertyReaders;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ServerInfo {
    private final String serverHome;
    private Timestamp serverStartUpTime;
    private String serverInstanceId;
    private String serverName;
    private String javaHome;
    private int jmxPort;
    private LeakyBucket<Timestamp> configurationReloadTime = new LeakyBucket<>(10);
    private Map<String, ServiceInfo> serviceDescription;
    private Map<String, GlobalListenersInfo> globalListeners;
    private String version;
    private Timestamp lastConfigurationReloadTime;

    public ServerInfo(String serverHome) {
        serverStartUpTime = new Timestamp(System.currentTimeMillis());
        this.serverHome = serverHome;
        javaHome = System.getProperty("java.home");
        jmxPort = new SystemPropertyReaders.NumberReaderBuilder("com.sun.management.jmxremote.port")
                .onFail( 0,"Unable to get system property com.sun.management.jmxremote.port.").build().read().intValue();

        serviceDescription = new HashMap<>();
        globalListeners = new HashMap<>();
    }

    @JsonIgnore
    public String getServerHome() {
        return serverHome;
    }

    public void setServerInstanceId(String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }

    public String getServerInstanceId() {
        return serverInstanceId;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

	@JsonIgnore
    public String getJavaHome(){ return javaHome; }

	@JsonIgnore
    public int getJmxPort() {
        return jmxPort;
    }

    public Timestamp getServerStartUpTime() {
        return serverStartUpTime;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverHome='" + serverHome + '\'' +
                ", serverInstanceId='" + serverInstanceId + '\'' +
                ", serverName='" + serverName + '\'' +
                ", javaHome='" + javaHome + '\'' +
                ", jmxPort=" + jmxPort +
                '}';
    }

    public void updateConfigurationReloadTime() {
        configurationReloadTime.add(new Timestamp(System.currentTimeMillis()));
    }

	@JsonIgnore
    public Map<String, ServiceInfo> getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(List<ServiceInfo> serviceInfoList) {
        for(ServiceInfo serviceInfo : serviceInfoList){
            this.serviceDescription.put(serviceInfo.getName(), serviceInfo);
        }
    }

	@JsonIgnore
    public Map<String, GlobalListenersInfo> getGlobalListeners() {
        return globalListeners;
    }

    public void setGlobalListeners(List<GlobalListenersInfo> globalListenersInfoList) {
        for(GlobalListenersInfo globalListenersInfo : globalListenersInfoList) {
            this.globalListeners.put(globalListenersInfo.getListenerName(), globalListenersInfo);
        }
    }

    public void setVersion(String version){
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getServerName() {
        return serverName;
    }

    @JsonIgnore
    public LeakyBucket<Timestamp> getConfigurationReloadTime(){
        return configurationReloadTime;
    }

	public void setLastConfigurationReloadTime() {

		this.lastConfigurationReloadTime = null;
		for (Timestamp timestamp : configurationReloadTime) {
			this.lastConfigurationReloadTime = timestamp;
		}
	}

	public Timestamp getLastConfigurationReloadTime() {
		return lastConfigurationReloadTime;
	}
}
