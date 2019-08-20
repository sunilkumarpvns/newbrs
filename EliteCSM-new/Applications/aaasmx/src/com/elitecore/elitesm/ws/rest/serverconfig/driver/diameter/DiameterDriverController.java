package com.elitecore.elitesm.ws.rest.serverconfig.driver.diameter;

import javax.ws.rs.Path;

@Path("/")
public class DiameterDriverController {

	@Path("/dbauth")
	public DiameterDBAuthDriverController getDiameterDBAuthDriverController() {
		return new DiameterDBAuthDriverController();
	}
	@Path("/ldapauth")
	public DiameterLDAPAuthDriverController getDiameterLDAPAuthDriverController() {
		return new DiameterLDAPAuthDriverController();
	}
	
	@Path("/httpauth")
	public DiameterHTTPAuthDriverController getDiameterHTTPAuthDriverController() {
		return new DiameterHTTPAuthDriverController();
	}
	
	@Path("/hssauth")
	public DiameterHSSAuthDriverController getDiameterHSSAuthDriverController() {
		return new DiameterHSSAuthDriverController();
	}
	
	@Path("/userfileauth")
	public DiameterUserFileAuthDriverController getDiameterUserFileAuthDriverController() {
		return new DiameterUserFileAuthDriverController();
	}
	
	@Path("/dbacct")
	public DiameterDBAcctDriverController getDiameterDBAcctDBAcctDriverController() {
		return new DiameterDBAcctDriverController();
	}
	
	@Path("/mapgwauth")
	public DiameterMapGWAuthDriverController getDiameterMapGWAuthDriverController() {
		return new DiameterMapGWAuthDriverController();
	}
	
	@Path("/classiccsv")
	public DiameterClassicCSVAcctDriverController getDiameterClassicCSVAcctDriverController() {
		return new DiameterClassicCSVAcctDriverController();
	}
}
