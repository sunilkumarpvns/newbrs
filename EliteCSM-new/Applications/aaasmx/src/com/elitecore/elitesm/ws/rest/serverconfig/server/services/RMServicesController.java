package com.elitecore.elitesm.ws.rest.serverconfig.server.services;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmcharging.RMChargingServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmippool.RMIPPoolServiceController;

@Path("/")
public class RMServicesController {
	
	@Path("/rmcharging")
	public RMChargingServiceController getRMChargingServiceController() {
		return new RMChargingServiceController();
	}
	
	@Path("/rmippool")
	public RMIPPoolServiceController getRMIPPoolServiceController() {
		return new RMIPPoolServiceController();
	}

}
