package com.elitecore.diameterapi.diameter.common.routerx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.routerx.agent.RoutingEntryDataImpl;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NewDiameterRouterTest extends DiameterTestSupport {
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	private DiameterRouter router;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void ensuresThatAServerInitiatedRequestForNewSessionIsNotHandledByRouterAndIsProcessedLocally() throws RoutingFailedException {
		router = new DiameterRouter(getStackContext(), createANonLocalRoutingEntryData());
		router.init();
		RoutingActions routingAction = router.processDiameterRequest(createRAR(getPeerOperation(ROUTING_PEER[1]).getDiameterPeer().getPeerData()),
				getSession());
		
		assertEquals(RoutingActions.LOCAL, routingAction);
	}

	public class WhenMisconfiguredRoutingEntryIsSelected {
		
		@Before
		public void givenAMisconfiguredRoutingEntry() throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
			Mockito.doThrow(DriverNotFoundException.class).when(getStackContext()).getDiameterCDRDriver("driver_name");
			List<RoutingEntryData> rounEntryDatas = createRoutingEntryDataForProxyActionWithfailureArgument();
			router = new DiameterRouter(getStackContext(), rounEntryDatas);
			router.init();
		}
		
		@Test
		public void throwsRoutingFailedExceptionWithUnableToComplyResultCode() {
			try {
				router.processDiameterRequest(createCCUpdateRequestFrom(getPeerOperation(ROUTING_PEER[1]).getDiameterPeer().getPeerData()),
						getSession());
				fail("Routing should fail in case of invalid routing entry configuration");
			} catch (RoutingFailedException e) {
				assertEquals(ResultCode.DIAMETER_UNABLE_TO_COMPLY, e.getResultCode());
			}
		}
		
		@Test
		public void requestIsNotProxied() {
			try {
				router.processDiameterRequest(createCCUpdateRequestFrom(getPeerOperation(ROUTING_PEER[1]).getDiameterPeer().getPeerData()),
						getSession());
				fail("Routing should fail in case of invalid routing entry configuration");
			} catch (RoutingFailedException e) {
				getPeerOperation(ROUTING_PEER[1]).verifyRequestNotReceived();
			}
		}
	}
	
	private List<RoutingEntryData> createANonLocalRoutingEntryData() {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setRoutingAction(RoutingActions.PROXY.routingAction);
		data.setDestRealm(ROUTING_PEER[2]);
		data.setRoutingName("Proxy");
		PeerGroupImpl group = new PeerGroupImpl();
		PeerInfoImpl info = new PeerInfoImpl();
		info.setPeerName(ROUTING_PEER[0]);
		info.setLoadFactor(1);
		group.getPeerList().add(info);
		info = new PeerInfoImpl();
		info.setPeerName(ROUND_ROBIN_PEER[0]);
		info.setLoadFactor(1);
		group.getPeerList().add(info);
		info = new PeerInfoImpl();
		info.setPeerName(SECONDARY_PEER[0]);
		info.setLoadFactor(0);
		group.getPeerList().add(info);
		data.setPeerGroupList(Arrays.asList(group));
		
		return Arrays.<RoutingEntryData>asList(data);
	}
	
	private List<RoutingEntryData> createRoutingEntryDataForProxyActionWithfailureArgument() {

		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setRoutingAction(RoutingActions.PROXY.routingAction);
		data.setDestRealm(ROUTING_PEER[2]);
		data.setRoutingName("Proxy");
		PeerGroupImpl group = new PeerGroupImpl();
		PeerInfoImpl info = new PeerInfoImpl();
		info.setPeerName(ROUTING_PEER[0]);
		info.setLoadFactor(1);
		group.getPeerList().add(info);
		data.setPeerGroupList(Arrays.asList(group));

		DiameterFailoverConfigurationImpl failoverConfigurationImpl = new DiameterFailoverConfigurationImpl();
		failoverConfigurationImpl.setAction(DiameterFailureConstants.RECORD.failureAction);
		failoverConfigurationImpl.setErrorCodes("3000");
		failoverConfigurationImpl.setFailoverArguments("driver_name");
		data.setFailoverDataList(Arrays.asList(failoverConfigurationImpl));
		return Arrays.<RoutingEntryData>asList(data);
	}
}