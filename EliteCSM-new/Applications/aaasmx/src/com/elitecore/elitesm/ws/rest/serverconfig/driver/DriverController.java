package com.elitecore.elitesm.ws.rest.serverconfig.driver;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.driver.charginggateway.ChargingGateWayServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.driver.crestelrating.BaseDiameterDriverController;
import com.elitecore.elitesm.ws.rest.serverconfig.driver.diameter.DiameterDriverController;
import com.elitecore.elitesm.ws.rest.serverconfig.driver.radius.RadiusDriverController;

@Path("/")
public class DriverController {
	@Path("/radius")
	public RadiusDriverController getRadiusDriverController() {
		return new RadiusDriverController();
	}
	
	@Path("/diameter")
	public DiameterDriverController getDiameterDriverController() {
		return new DiameterDriverController();
	}
	
	@Path("/charginggateway")
	public ChargingGateWayServiceController getChargingGateWayService() {
		return new ChargingGateWayServiceController();
	}
	
	@Path("/basediameter")
	public BaseDiameterDriverController getBaseDiameterDriverController() {
		return new BaseDiameterDriverController();
	}
	
}
