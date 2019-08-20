package com.elitecore.aaa.ws.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.elitecore.aaa.ws.config.AAARestServer;
import com.elitecore.aaa.ws.controller.session.diameter.DiameterSessionController;
import com.elitecore.aaa.ws.controller.session.radius.RadiusSessionController;


@Path("/")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
public class AAARestController {
	

	@Path("v1/diameter/session")
	public DiameterSessionController getDiameterSessionController() {
		return new DiameterSessionController();
	}
	
	@Path("v1/radius/session")
	public RadiusSessionController getRadiusSessionController() {
		return new RadiusSessionController(AAARestServer.getServerContext());
	}

}