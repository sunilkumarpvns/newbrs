package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
/**
 * <p> This Communicator is combination of authentication and accounting communicator, 
 * so it can serve both Authentication and Accounting type of request.
 * </p>
 * 
 * <p> It will be mark alive if both internal communicators are live and marked as dead
 * if either of authentication or accounting communicator is dead. It also maintain ESIStatistics.
 * </p>   
 * 
 * <pre> 
 *  +----------------------------------------------------------------------------------------+
 *  |                                                                                        |
 *  |                                                    +--------------------+              |
 *  |                                                    |                    |              |
 *  |                                                    |       AUTH         |              |
 *  |                        /\                          |                    |              |
 *  |      request          /  \       Auth request      |    COMMUNICATOR    |              |
 *  |  --------------------/    \----------------------->|                    |              |
 *  |                      \    /                        |                    |              |
 *  |                       \  /                         +--------------------+              |
 *  |                        \/                                                              |
 *  |                         |                                                              |
 *  |                         |   Acct request           +--------------------+              |
 *  |                         |                          |         ACCT       |              |
 *  |                         |                          |                    |              |
 *  |                         |------------------------->|      COMMUNICATOR  |              |
 *  |                                                    |                    |              |
 *  |                                                    |                    |              |
 *  |                                                    +--------------------+              |
 *  |                                                                                        |
 *  +----------------------------------------------------------------------------------------+
 *  </pre>
 *  
 * @author soniya
 *
 */
public class CorrelatedRadiusCommunicator implements UDPCommunicator {
	
	private static final String MODULE = "CORRELATED-RADIUS-COMMUNICATOR";
	
	private UDPCommunicator authCommunicator;
	private UDPCommunicator acctCommunicator;
	private final List<ESIEventListener<ESCommunicator>> eventListeners = new CopyOnWriteArrayList<>();
	private volatile boolean isAlive = true;

	public CorrelatedRadiusCommunicator(UDPCommunicator authCommunicator, UDPCommunicator acctCommunicator) {
		this.authCommunicator = authCommunicator;
		this.acctCommunicator = acctCommunicator;
		EventListenerImpl listener = new EventListenerImpl();
		this.authCommunicator.addESIEventListener(listener);
		this.acctCommunicator.addESIEventListener(listener);
	}

	@Override
	public void reInit() throws InitializationFailedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init() throws InitializationFailedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	@Override
	public void scan() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
		eventListeners.add(eventListener);
	}

	@Override
	public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
		eventListeners.remove(eventListener);
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return authCommunicator.getName() + "-" + acctCommunicator.getName();
	}

	@Override
	public String getTypeName() {
		return "RADIUS";
	}

	@Override
	public ESIStatistics getStatistics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerAlertListener(AlertListener alertListener) {
		throw new UnsupportedOperationException();
	}

	private class EventListenerImpl implements ESIEventListener<ESCommunicator> {

		@Override
		public synchronized void alive(ESCommunicator esCommunicator) {
			
			if (isAlive) {
				return;
			}
			
			if (authCommunicator.isAlive() && acctCommunicator.isAlive()) {

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, getName() + " RADIUS type ESI is alive.");
				}

				isAlive = true;
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.alive(CorrelatedRadiusCommunicator.this);
				}
			}

		}

		@Override
		public synchronized void dead(ESCommunicator esCommunicator) {
		
			if (isAlive) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, getName() + " RADIUS type ESI is dead.");
				}

				isAlive = false;
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.dead(CorrelatedRadiusCommunicator.this);
				}

			}
		}
	}

	@Override
	public int getMinLocalPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTimeOutRequestCounter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UDPCommunicatorContext getCommunicatorContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void handleUDPRequest(UDPRequest request) {
		IRadiusPacket radiusPacket = ((RadUDPRequest) request).getRadiusPacket();

		if (radiusPacket.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Sending request to Auth ESI: " + authCommunicator.getName());
			}
			authCommunicator.handleUDPRequest(request);
		} else if (radiusPacket.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Sending request to Acct ESI: " + acctCommunicator.getName());
			}
			acctCommunicator.handleUDPRequest(request);
		} else {
			throw new IllegalArgumentException("Cannot handle packet of type: " + radiusPacket.getPacketType());
		}
	}

	public UDPCommunicator getAuthCommunicator() {
		return this.authCommunicator;
	}

	public UDPCommunicator getAcctCommunicator() {
		return this.acctCommunicator;
	}
}
