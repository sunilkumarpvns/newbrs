package com.elitecore.elitesm.ws.rest.serverconfig.driver.crestelrating;

import javax.ws.rs.Path;

@Path("/")
public class BaseDiameterDriverController {

	@Path("/ocsv2")
	public DiameterCrestelOCSv2DriverController getCrestelRatingOCSv2DriverController() {
		return new DiameterCrestelOCSv2DriverController();
	}
	
	@Path("/crestelrating")
	public DiameterRatingTranslationDriverController getCrestelDiameterRatingDriverController() {
		return new DiameterRatingTranslationDriverController();
	}
}
