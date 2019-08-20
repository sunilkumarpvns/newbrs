package com.elitecore.netvertex.gateway.radius;

import java.util.List;

import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.scripts.RadiusGroovyScript;

public interface RadiusGatewayControllerContext {
	
	RadiusGateway getRadiusGateway(String ipAddress);
	
	List<RadiusGroovyScript> getRadiusGroovyScripts(String ipAddress);

	NetVertexServerContext getServerContext();

}
