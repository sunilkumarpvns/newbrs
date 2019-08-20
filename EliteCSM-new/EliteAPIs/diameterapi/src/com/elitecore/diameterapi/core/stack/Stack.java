/**'
 * 
 */
package com.elitecore.diameterapi.core.stack;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertManager;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

/**
 * 
 * Responsibility of this is to handle 
 * Network Connector and Alert Generation
 * 
 * @author pulindani
 *
 */
public abstract class Stack {

	private static final String MODULE="STACK";
	
	protected Map<TransportProtocols, INetworkConnector> networkConnectors;
	
	private static IStackAlertManager alertManager;
	private SocketDetail socketDetail;
	
	public Stack() {
		this.networkConnectors = new EnumMap<>(TransportProtocols.class);
	}
	
	/**
	 * This will start Stack's Network Interface 
	 */
	protected boolean start(ConnectionFactory connectionFactory) {

		boolean allClosed = true;
		if(networkConnectors.isEmpty() == false) {
			for (INetworkConnector networkConnector : networkConnectors.values()) {
				boolean hasStarted = networkConnector.start(connectionFactory);
				allClosed &= ! hasStarted;
				if (hasStarted) {
					socketDetail = networkConnector.getBondSocketDetail();
				}
			}
			if (allClosed) {
				stop();
				return false;
			}
		}else {
			LogManager.getLogger().error(MODULE, "No Network Connector Specified");
			stop();
			return false;
		}
		return true;
	}
	
	public String getNetworkAddress() {
		if (socketDetail == null) {
			return null;
		}
		return socketDetail.getIPAddress();
	}
	
	public SocketDetail getSocketDetail() {
		return socketDetail;
	}
	
	public int getNetworkPort() {
		if (socketDetail == null) { 
			return 0;
		}
		return socketDetail.getPort();
	}
	
	public void addNetworkConnector(INetworkConnector networkConnector) {
		this.networkConnectors.put(networkConnector.getTransportProtocol(), networkConnector);
	}
	
	protected final INetworkConnector getNetworkConnector(TransportProtocols transportProtocol){
		return networkConnectors.get(transportProtocol);
	}
	
	/**
	 * 
	 * @author pulindani
	 *
	 */
	
	public void registerStackAlertManager(IStackAlertManager alertManager) {
		Stack.alertManager = alertManager;
	}
	
	protected boolean stop() {
		boolean hasStopped = true;
		for (INetworkConnector networkConnector : networkConnectors.values()) {
			hasStopped &= networkConnector.stop();
		}
		return hasStopped;
	}
	
	public static void generateAlert(StackAlertSeverity alertSeverity, DiameterStackAlerts alertEnum, String alertGenerator, String alertMessage) {
		if(Stack.alertManager == null) {
			getLogger().warn(MODULE, "Failed to generate alert: " + alertEnum + " : " + alertMessage + ". Reason: Alert Manager is not initialized");
		} else {
			Stack.alertManager.scheduleAlert(alertSeverity, alertEnum, alertGenerator, alertMessage);
		}
	}
	
	public static void generateAlert(StackAlertSeverity alertSeverity, DiameterStackAlerts alertEnum, String alertGenerator, String alertMessage, int alertIntValue, String alertStringValue) {
		if(Stack.alertManager == null) {
			getLogger().warn(MODULE, "Failed to generate alert: " + alertEnum + " : " + alertMessage + ". Reason: Alert Manager is not initialized");
		} else {
			Stack.alertManager.scheduleAlert(alertSeverity, alertEnum, alertGenerator, alertMessage, alertIntValue, alertStringValue);
		}
	}
	
	private static ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	public String getRemarks() {
		StringBuilder remark = new StringBuilder();
		HashSet<ServiceRemarks> set = new HashSet<>();
		for (INetworkConnector connector : networkConnectors.values()) {
			set.add(connector.getRemarks());
			if (connector.getRemarks() != null) {
				remark .append(", ")
					.append(connector.getTransportProtocol())
					.append(": ")
					.append(connector.getRemarks().remark);
			}
		}
		String out = null;
		if (set.size() == 1) {
			ServiceRemarks next = set.iterator().next();
			if (next != null) {
				out = next.remark;
			}
		} else {
			if (remark.length() > 0) {
				out = remark.substring(2);
			}
		}
		return out;
	}
}
