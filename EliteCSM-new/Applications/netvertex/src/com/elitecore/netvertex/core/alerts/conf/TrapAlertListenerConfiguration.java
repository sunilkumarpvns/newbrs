package com.elitecore.netvertex.core.alerts.conf;

import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.List;

public class TrapAlertListenerConfiguration implements ToStringable{

	private final String community;
	private final String listenerId;
	private final int port;
	private final String serverIp;
	private final TrapVersion trapVersion;
	private final boolean isAdvanceTrap;
	private final String listenerName;

	private final SnmpRequestType snmpRequestType;
	private final int requestTimeout;
	private final int retryCount;
	private final List<AlertsConfiguration> alertConfigurations;

	public TrapAlertListenerConfiguration(String listenerId,
										  String community,
										  int port,
										  String serverIp,
										  TrapVersion trapVersion,
										  boolean isAdvanceTrap,
										  String listenerName,
										  SnmpRequestType snmpRequestType,
										  int requestTimeout,
										  int retryCount,
										  List<AlertsConfiguration> alertConfigurations) {
		this.community = community;
		this.listenerId = listenerId;
		this.port = port;
		this.serverIp = serverIp;
		this.trapVersion = trapVersion;
		this.isAdvanceTrap = isAdvanceTrap;
		this.listenerName = listenerName;
		this.snmpRequestType = snmpRequestType;
		this.requestTimeout = requestTimeout;
		this.retryCount = retryCount;
		this.alertConfigurations = alertConfigurations;
	}



	public String getName() {
		return listenerName;
	}

	public String getCommunity() {
		return community;
	}


	public String getListenerId() {
		return listenerId;
	}

	public int getPort() {
		return port;
	}

	public String getServerIp() {
		return serverIp;
	}

	public TrapVersion getTrapVersion() {
		return trapVersion;
	}

	public boolean isAdvanceTrap() {
		return isAdvanceTrap;
	}


	public SnmpRequestType getSnmpRequestType() {
		return snmpRequestType;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public List<AlertsConfiguration> getAlertConfigurations() {
		return alertConfigurations;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.appendHeading("Trap Alert Listener(" + listenerName + ") (" + listenerId + ")");
		builder.incrementIndentation();
		builder.append("Server Address", serverIp+":"+port);
		builder.append("Community", community);
		builder.append("Trap Version", trapVersion);
		builder.append("Advanced Trap", Boolean.toString(isAdvanceTrap).toUpperCase());
		builder.append("Request Type", snmpRequestType);
		if(snmpRequestType == SnmpRequestType.INFORM){
			builder.append("Timeout", requestTimeout);
			builder.append("Retry Count", retryCount);
		}
		builder.incrementIndentation();
		builder.appendChildObject("Alerts", alertConfigurations);
		builder.decrementIndentation();
		builder.decrementIndentation();
	}
}
