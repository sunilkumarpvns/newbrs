package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.radius.systemx.esix.udp.RadESIEventListener;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;

public class RadESIEventListenerImpl implements RadESIEventListener {
	private final static String MODULE = "RAD-ESI-EVNT-LISTNR";
	private ServerContext serverContext;
	public RadESIEventListenerImpl(ServerContext serverContext){
		this.serverContext = serverContext;		
	}
	@Override
	public void alive(UDPCommunicator esCommunicator) {
		serverContext.generateSystemAlert(AlertSeverity.CLEAR,Alerts.RADIUSALIVE, MODULE,
				esCommunicator.getCommunicatorContext().getIPAddress()+":"+ esCommunicator.getCommunicatorContext().getPort() 
					+ " Marked Alive.", 0, esCommunicator.getCommunicatorContext().getIPAddress()+":"
					+ esCommunicator.getCommunicatorContext().getPort());
	}

	@Override
	public void dead(UDPCommunicator esCommunicator) {
		serverContext.generateSystemAlert(AlertSeverity.CRITICAL,Alerts.RADIUSDEAD, MODULE, 
				esCommunicator.getCommunicatorContext().getIPAddress()+":"+ esCommunicator.getCommunicatorContext().getPort() 
					+ " Marked Dead.", 0, esCommunicator.getCommunicatorContext().getIPAddress()+":"
							+ esCommunicator.getCommunicatorContext().getPort());
	}

}
