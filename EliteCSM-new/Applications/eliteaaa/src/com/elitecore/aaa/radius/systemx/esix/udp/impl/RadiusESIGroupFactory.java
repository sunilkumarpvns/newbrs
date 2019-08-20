package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.RedundancyMode;
import com.elitecore.core.commons.InitializationFailedException;

public class RadiusESIGroupFactory {

	private static final String MODULE = "RADIUS-ESI-GROUP-FACTORY";

	private AAAServerContext serverContext;
	private Map<String, RadiusEsiGroup> radiusESIGroup = new HashMap<>();

	public RadiusEsiGroup getOrCreateGroupInstance(AAAServerContext serverContext, RadiusEsiGroupData radiusESIGroupData) throws InitializationFailedException {
		this.serverContext = serverContext;

		RadiusEsiGroup esiGroup = null;

		if (radiusESIGroup.get(radiusESIGroupData.getName()) != null) {
			esiGroup = radiusESIGroup.get(radiusESIGroupData.getName());
		} else {
			esiGroup = createGroup(radiusESIGroupData);
			esiGroup.init();
			radiusESIGroup.put(radiusESIGroupData.getName(), esiGroup);
		}

		return esiGroup;
	}

	private RadiusEsiGroup createGroup(RadiusEsiGroupData esiGroupData) {
		RadiusEsiGroup radiusEsiGroup;

		if (RedundancyMode.NM.redundancyModeName.equalsIgnoreCase(esiGroupData.getRedundancyMode())) {
			radiusEsiGroup = new NPlusMRedundancyGroup(serverContext, esiGroupData);
		} else {
			radiusEsiGroup = new ActivePassiveRedundancyGroup(serverContext, esiGroupData);
		}
		return radiusEsiGroup;
	}

}
