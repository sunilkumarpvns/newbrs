package com.elitecore.diameterapi.diameter.common.routerx.agent;

import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public class ProxyAgentWithDropActionTest extends RelayAgentWithDropActionTest {

	@Override
	protected RoutingActions getRoutingAction() {
		return RoutingActions.PROXY;
	}
}
