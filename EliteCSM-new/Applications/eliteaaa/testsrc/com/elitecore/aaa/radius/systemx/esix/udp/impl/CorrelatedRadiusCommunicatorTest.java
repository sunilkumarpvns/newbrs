package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class CorrelatedRadiusCommunicatorTest extends RadiusESIGroupTestSupport {

	private RadiusPacket radiusPacket;
	private CorrelatedRadiusCommunicator correlatedRadiusCommunicator;
	
	private DefaultExternalSystemData primaryEsi1Auth;
	private DefaultExternalSystemData primaryEsi1Acct;

	private UDPCommunicatorSpy primaryEsi1AuthCommunicator;
	private UDPCommunicatorSpy primaryEsi1AcctCommunicator;

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() throws InvalidURLException, InitializationFailedException {
		super.setUp();

		radiusPacket = new RadiusPacket();

		primaryEsi1Auth = createESI().setEsiName("primaryEsi1Auth").
				setUUID("primaryEsi1Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		primaryEsi1Acct = createESI().setEsiName("primaryEsi1Acct").
				setUUID("primaryEsi1Acct").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		primaryEsi1AuthCommunicator = getCommunicator(primaryEsi1Auth);
		primaryEsi1AcctCommunicator = getCommunicator(primaryEsi1Acct);

		correlatedRadiusCommunicator = new CorrelatedRadiusCommunicator(primaryEsi1AuthCommunicator.getCommunicator(), primaryEsi1AcctCommunicator.getCommunicator());
	}

	@Test
	public void authenticationRequestWillBeForwardedViaAuthCommunicator() {
		radiusPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);

		UDPRequest radUDPRequestImpl = createUdpRequest();
		correlatedRadiusCommunicator.handleUDPRequest(radUDPRequestImpl);

		primaryEsi1AuthCommunicator.verifyRequestReceipt();
	}

	@Test
	public void accountingRequestWillBeForwardedViaAcctCommunicator() {
		radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);

		UDPRequest radUDPRequestImpl = createUdpRequest();
		correlatedRadiusCommunicator.handleUDPRequest(radUDPRequestImpl);

		primaryEsi1AcctCommunicator.verifyRequestReceipt();
	}
	
	@Test
	public void isMarkedDeadIfAuthCommunicatorIsAliveButAcctCommunicatorIsDead() {
		primaryEsi1AuthCommunicator.markAlive();
		primaryEsi1AcctCommunicator.markDead();
		
		assertFalse(correlatedRadiusCommunicator.isAlive());
	}
	
	@Test
	public void isMarkedDeadIfAcctCommunicatorIsAliveButAuthCommunicatorIsDead() {
		primaryEsi1AuthCommunicator.markDead();
		primaryEsi1AcctCommunicator.markAlive();
		
		assertFalse(correlatedRadiusCommunicator.isAlive());
	}
	
	@Test
	public void isMarkedAliveIfBothCommunicatorsAreAlive() {
		primaryEsi1AuthCommunicator.markAlive();
		primaryEsi1AcctCommunicator.markAlive();
		
		assertTrue(correlatedRadiusCommunicator.isAlive());
	}
	
	@Test
	public void notifiesListenersOnComingBackAlive() {
		@SuppressWarnings("unchecked")
		ESIEventListener<ESCommunicator> mockListener = mock(ESIEventListener.class);
		
		correlatedRadiusCommunicator.addESIEventListener(mockListener);
	
		primaryEsi1AuthCommunicator.markDead();
		primaryEsi1AcctCommunicator.markDead();
		
		primaryEsi1AuthCommunicator.markAlive();
		primaryEsi1AcctCommunicator.markAlive();
		
		verify(mockListener).alive(correlatedRadiusCommunicator);
	}
	
	@Test
	public void willNotNotifiesToListenerOnMarkAliveIfGroupIsAlreadyAlive() {
		@SuppressWarnings("unchecked")
		ESIEventListener<ESCommunicator> mockListener = mock(ESIEventListener.class);
		
		correlatedRadiusCommunicator.addESIEventListener(mockListener);
		
		primaryEsi1AuthCommunicator.markAlive();

		verifyZeroInteractions(mockListener);
	}
	
	@Test
	public void notifiesListenersOnBeingDeadMarked() {
		@SuppressWarnings("unchecked")
		ESIEventListener<ESCommunicator> mockListener = mock(ESIEventListener.class);
		
		correlatedRadiusCommunicator.addESIEventListener(mockListener);
	
		primaryEsi1AuthCommunicator.markDead();
		
		verify(mockListener).dead(correlatedRadiusCommunicator);
	}
	
	@Test
	public void willNotNotifiesListenersOnMarkDeadIfGroupIsAlreadyDead() {
		@SuppressWarnings("unchecked")
		ESIEventListener<ESCommunicator> mockListener = mock(ESIEventListener.class);
		
		correlatedRadiusCommunicator.addESIEventListener(mockListener);
	
		primaryEsi1AuthCommunicator.markDead();
		
		verify(mockListener).dead(correlatedRadiusCommunicator);
		
		primaryEsi1AuthCommunicator.markDead();
		
		verify(mockListener, times(1)).dead(correlatedRadiusCommunicator);
	}
	
	@Test
	public void throwsWithIllegalArgumentExceptionIfPacketTypeIsNeitherOfAuthOrAcctType() {
		radiusPacket.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(equalTo("Cannot handle packet of type: " + RadiusConstants.COA_REQUEST_MESSAGE));
		
		correlatedRadiusCommunicator.handleUDPRequest(createUdpRequest());
	}

	@Test
	public void scanningThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.scan();
	}
	
	@Test
	public void initThrowsUnsupportedOpeartionException() throws InitializationFailedException {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.init();
	}
	
	@Test
	public void reInitThrowsUnsupportedOpeartionException() throws InitializationFailedException {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.reInit();
	}
	
	@Test
	public void shutDownThrowsUnsupportedOperationException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.shutdown();
	}
	
	@Test
	public void stopsThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.stop();
	}
	
	@Test
	public void getStatisticsThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.getStatistics();
	}
	
	@Test
	public void registerAlertListenerThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.registerAlertListener(new AlertListener() {
			
			@Override
			public void generateAlert(Events event, String message) {
				
			}
		});
	}
	
	@Test
	public void getMinLocalPortThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.getMinLocalPort();
	}
	
	@Test
	public void getTimeOutRequestCounterThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.getTimeOutRequestCounter();
	} 
	
	@Test
	public void getCommunicatorContextThrowsUnsupportedException() {
		exception.expect(UnsupportedOperationException.class);
		correlatedRadiusCommunicator.getCommunicatorContext();
	}
	
	@Test
	public void  getAuthCommunicatorReturnsAuthCommunicator() {
		assertEquals(primaryEsi1AuthCommunicator.getCommunicator(), correlatedRadiusCommunicator.getAuthCommunicator());
	}
	
	@Test
	public void  getAcctCommunicatorReturnsAcctCommunicator() {
		assertEquals(primaryEsi1AcctCommunicator.getCommunicator(), correlatedRadiusCommunicator.getAcctCommunicator());
	}
	
	private UDPRequest createUdpRequest() {
		UDPRequest radUDPRequestImpl = new RadUDPRequestImpl(radiusPacket.getBytes(), SHARED_SECRET);
		return radUDPRequestImpl;
	}
}