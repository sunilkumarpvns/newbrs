package com.elitecore.diameterapi.diameter.common.routerx.agent;

import org.junit.runner.RunWith;

import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ProxyAgentWithFailoverActionTest extends RelayAgentWithFailoverActionTest {

	@Override
	protected RoutingActions getRoutingAction() {
		return RoutingActions.PROXY;
	}
}
