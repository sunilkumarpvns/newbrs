package com.elitecore.diameterapi.diameter.common.routerx.agent;

import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public class ProxyAgentWithoutFailoverConfigurationTest extends RelayAgentWithoutFailoverConfigurationTest {

	@Override
	protected RoutingActions getRoutingAction() {
		return RoutingActions.PROXY;
	}
}
