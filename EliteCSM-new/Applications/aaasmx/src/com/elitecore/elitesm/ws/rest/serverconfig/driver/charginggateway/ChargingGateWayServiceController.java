package com.elitecore.elitesm.ws.rest.serverconfig.driver.charginggateway;

import javax.ws.rs.Path;

@Path("/")
public class ChargingGateWayServiceController {
	@Path("/crestelcharging")
	public CrestelChargingDriverController getCrestelChargingDriverController() {
		return new CrestelChargingDriverController();
	}
	
	@Path("/crestelocsv2")
	public CrestelChargingOCSV2DriverController getCrestelChargingOCSV2DriverController() {
		return new CrestelChargingOCSV2DriverController();
	}
	
	@Path("/diametercharging")
	public DiameterChargingDriverController getDiameterChargingDriverController() {
		return new DiameterChargingDriverController();
	}
	
	@Path("/rmclassiccsvacct")
	public RMClassicCSVAcctDriverController getRMClassicCSVAcctDriverController() {
		return new RMClassicCSVAcctDriverController();
	}
}