package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicator;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadiusExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.scanner.IcmpScanner;
import com.elitecore.aaa.radius.systemx.esix.udp.scanner.NullScanner;
import com.elitecore.aaa.radius.systemx.esix.udp.scanner.RadStatusScanner;
import com.elitecore.aaa.radius.systemx.esix.udp.scanner.StatusScanner;
import com.elitecore.aaa.radius.systemx.esix.udp.scanner.UDPScanner;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorImpl;

public abstract class RadUDPCommunicatorImpl extends UDPCommunicatorImpl implements RadUDPCommunicator,ReInitializable {
	
	private static final String MODULE = "RAD-UDP-COMM";
	private final ServerContext serverContext;
	private StatusScanner scanner;

	public RadUDPCommunicatorImpl(final ServerContext serverContext, RadiusExternalSystemData externalSystem) {
		this(serverContext.getTaskScheduler(), externalSystem, serverContext);
	}

	private RadUDPCommunicatorImpl(TaskScheduler scheduler, RadiusExternalSystemData externalSystem, ServerContext serverContext){
		super(scheduler, externalSystem);
		this.serverContext = serverContext;
	}
	
	public RadUDPCommunicatorImpl(final ServiceContext serviceContext, RadiusExternalSystemData externalSystem) {
		this(serviceContext.getServerContext().getTaskScheduler(),
				externalSystem, serviceContext.getServerContext());
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		initStatusScanner();
	}
	
	private void initStatusScanner() {
		if(getExternalSystem().getIsAlwaysAlive()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No scanning will be performed for ES: " + getExternalSystem().getName() + 
						"[" + getExternalSystem().getIPAddress().getHostAddress() + ":" + 
						getExternalSystem().getPort() + "], Reason: ES is marked Always Alive.");
			}
			return;
		}
		
		StatusCheckMethod statusCheckMethod = ((RadiusExternalSystemData)externalSystemData).getStatusCheckMethod();
		
		createScanner(statusCheckMethod);
		
		try {
			scanner.init();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().trace(MODULE, e);
			createNullScanner(e.getLocalizedMessage());
		}
	}

	private void createScanner(StatusCheckMethod statusCheckMethod) {
		
		if(statusCheckMethod == null) {
			createNullScanner("No Scanner Type is configured for ESI: " + externalSystemData.getName());
			return;
		}
		
		switch (statusCheckMethod) {
		case ICMP_REQUEST:
			scanner = new IcmpScanner(RadUDPCommunicatorImpl.this);
			break;
		case PACKET_BYTES:
			scanner = new UDPScanner(RadUDPCommunicatorImpl.this, ((RadiusExternalSystemData)externalSystemData).getScannerPacket());
			break;
		case RADIUS_PACKET:
			scanner = new RadStatusScanner(RadUDPCommunicatorImpl.this, ((RadiusExternalSystemData)externalSystemData).getScannerPacket());
			break;
		default:
			createNullScanner("Unknown Scanner Type: " + statusCheckMethod + " is configured");
		}
	}

	private void createNullScanner(String reason) {
		
		scanner = NullScanner.INSTANCE;
		
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "No scanning will be performed for ES: " + getExternalSystem().getName() + 
					"[" + getExternalSystem().getIPAddress().getHostAddress() + ":" + 
					getExternalSystem().getPort() + "], Reason: " + reason);
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		super.reInit();
		this.externalSystemData = ((AAAServerContext)serverContext).getServerConfiguration()
		.getRadESConfiguration().getESData(externalSystemData.getUUID()).get();
	}

	@Override
	public void handleRadiusRequest(RadUDPRequest radUDPRequest) {
		handleUDPRequest(radUDPRequest);
	}
	
	@Override
	public void scan() {
		if (getExternalSystem().getIsAlwaysAlive()){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "ES: " + getExternalSystem().getName() + "(" + getExternalSystem().getIPAddress().getHostAddress() + ":" + 
						getExternalSystem().getPort() + ") is being treated as always alive. So no scanning will be performed for this ES.");
			}
			markAlive();
			return;
		}
		
		scanner.scan();
	}
	
	public void shutdown() {
		super.shutdown();
	}
	
	protected ServerContext getServerContext() {
		return this.serverContext;
	}

	@Override
	protected void actionOnHighResponseTime(UDPRequest udpRequest, UDPResponse udpResponse, int endToEndResponseTime) {
		getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.RADIUS_ESI_HIGH_RESPONSE_TIME, MODULE, "High Response Time "+endToEndResponseTime+" ms from " + getExternalSystem().getName() + " ("+ getExternalSystem().getIPAddress() +":" + getExternalSystem().getPort() + ") " + " Radius ESI", endToEndResponseTime, getExternalSystem().getName());
	}
	
	abstract protected CommunicationHandler createCommunicationHandler();
}

