package com.elitecore.diameterapi.core.common.transport;

import org.apache.logging.log4j.ThreadContext;

import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;

import java.util.Map;

public class PacketProcessImpl implements PacketProcess {
	private Packet packet;
	private NetworkConnectionHandler connectionHandler;
	private DiameterStack stack;
	private final Map<String, String> callerDiagnosticsMap;

	public PacketProcessImpl(Packet packet, NetworkConnectionHandler connectionHandler, DiameterStack stack,
							 Map<String, String> callerDiagnosticsMap){
		this.packet = packet;
		this.connectionHandler = connectionHandler;
		this.stack = stack;
		this.callerDiagnosticsMap = callerDiagnosticsMap;
	}

	@Override
	public void preSubmit() {
		//No packet processing required
	}

	@Override
	public void postSubmit() {
		// This operation will clear the MDC information of the thread that created this PacketProcess instance
		clearMDCInformation();
	}

	/**
	 * Copy caller thread diagnostics to current thread
	 */
	private void copyMDCInformation() {
		ThreadContext.putAll(callerDiagnosticsMap);
	}

	@Override
	public void run() {
		try {
			copyMDCInformation();
			stack.handleReceivedMessage(packet, connectionHandler);
		} finally {
			// This operation will clear the MDC information of the worker thread that processed this PacketProcess instance
			clearMDCInformation();
		}
	}
	
	private void clearMDCInformation() {
		ThreadContext.clearAll();
	}

	@Override
	public Packet getPacket(){
		return packet;
	}
	
	@Override
	public NetworkConnectionHandler getConnectionHandler(){
		return connectionHandler;
	}

}
