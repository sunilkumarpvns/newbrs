package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mockito.Mockito;

import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponseImpl;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class UDPCommunicatorSpy {

	private static final String MODULE = "SPY-COMMUNICATOR";
	private DummyUDPCommunicator udpCommunicator;
	private DefaultExternalSystemData externalSystem;
	private UDPResponseListener listener;
	private UDPRequest lastSentRadiusRequest;

	public UDPCommunicatorSpy(DefaultExternalSystemData externalSystem) {
		this.externalSystem = externalSystem;
		udpCommunicator = spy(new DummyUDPCommunicator());
	}

	public UDPCommunicator getCommunicator() {
		return udpCommunicator;
	}

	public class DummyUDPCommunicator implements UDPCommunicator {

		private static final String MODULE = "DUMMY-ESI";
		private boolean alive =  true;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();

		public DummyUDPCommunicator() {
		}

		public boolean isAlive() {
			return alive;
		}


		@Override
		public int getMinLocalPort() {
			return 0;
		}

		@Override
		public int getTimeOutRequestCounter() {
			return 0;
		}

		@Override
		public UDPCommunicatorContext getCommunicatorContext() {
			return new UDPCommunicatorContext() {

				@Override
				public void updateAverageResponseTime(long responseTime) {

				}

				@Override
				public void responseReceived(UDPRequest udpRequest, UDPResponse udpResponse) {

				}

				@Override
				public void removeAttributes(UDPRequest udpRequest) {

				}

				@Override
				public void incrementTotalRequests() {

				}

				@Override
				public void incrementSuccessResponseCount() {

				}

				@Override
				public void incrementErrorResponseCount() {

				}

				@Override
				public void highResponseTimeReceived(UDPRequest udpRequest, UDPResponse udpResponse, int endToEndResponseTime) {

				}

				@Override
				public int getPort() {
					return externalSystem.getPort();
				}

				@Override
				public String getName() {
					return externalSystem.getName();
				}

				@Override
				public String getIPAddress() {
					return externalSystem.getStringIpAddress();
				}

				@Override
				public UDPExternalSystemData getExternalSystem() {
					return externalSystem;
				}
			};
		}

		@Override
		public void shutdown() {

		}

		@Override
		public void init() throws InitializationFailedException {
		}

		@Override
		public void scan() {

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

		}

		@Override
		public String getName() {
			return externalSystem.getName();
		}

		@Override
		public void registerAlertListener(AlertListener alertListener) {

		}

		@Override
		public void reInit() throws InitializationFailedException {

		}

		@Override
		public void handleUDPRequest(UDPRequest request) {
			if ( isAlive() == false) {
				request.getResponseListener().requestDropped(request, this);
			}

			lastSentRadiusRequest = request;
			listener = request.getResponseListener();
			LogManager.getLogger().info(MODULE, "Sending request to dummy ESI: " + getName());

		}



		public synchronized void markDead() {
			alive = false;
			for (ESIEventListener<ESCommunicator> evenListener: eventListeners) {
				evenListener.dead(this);
			}
		}

		public synchronized void markAlive() {
			alive = true;

			for (ESIEventListener<ESCommunicator> evenListener: eventListeners) {
				evenListener.alive(this);
			}
		}

		@Override
		public String getTypeName() {
			return null;
		}

		@Override
		public ESIStatistics getStatistics() {
			return null;
		}
	}

	public UDPCommunicatorSpy verifyRequestReceipt(int times) {
		verify(getCommunicator(), times(times)).handleUDPRequest(Mockito.any(UDPRequest.class));
		return this;
	}

	public void doesNotRespondWithinRequestTimeout() {
		LogManager.getLogger().info(MODULE, "Request is time out from communicator: " + udpCommunicator.getName());
		listener.requestTimeout(lastSentRadiusRequest, getCommunicator());
	}

	public UDPCommunicatorSpy sendsAccessAccept() {
		listener.responseReceived(lastSentRadiusRequest, dummyResponse());
		return this;
	}
	
	public UDPCommunicatorSpy sendsAccontingResponse() {
		listener.responseReceived(lastSentRadiusRequest, dummyAccountingResponse());
		return this;
	}

	private RadUDPResponseImpl dummyResponse() {
		RadiusPacket radiusPacket = new RadiusPacketBuilder().packetType(RadiusConstants.ACCESS_ACCEPT_MESSAGE).build();
		return new RadUDPResponseImpl(radiusPacket.getBytes(), "Dummy-Response");
	}
	
	private RadUDPResponseImpl dummyAccountingResponse() {
		RadiusPacket radiusPacket = new RadiusPacketBuilder().packetType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE).build();
		return new RadUDPResponseImpl(radiusPacket.getBytes(), "Dummy-Response");
	}

	public void markAlive() {
		udpCommunicator.markAlive();
	}

	public void markDead() {
		udpCommunicator.markDead();
	}

	public void verifyRequestNotReceived() {
		verify(getCommunicator(), times(0)).handleUDPRequest(Mockito.any(UDPRequest.class));
	}

	public UDPCommunicatorSpy verifyRequestReceipt() {
		verify(getCommunicator()).handleUDPRequest(Mockito.any(UDPRequest.class));
		return this;
	}

	public boolean isAlive() {
		return udpCommunicator.isAlive();
	}

}
