package com.elitecore.elitesm.ws.rest.serverconfig.driver.radius;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.driver.radius.RadiusDBAuthDriverController;

@Path("/")
public class RadiusDriverController {
	
	
	@Path("/userfileauth")
	public RadiusUserFileAuthDriverController getRadiusUserFileAuthDriverController() {
		return new RadiusUserFileAuthDriverController();
	}
	
	@Path("/dbauth")
	public RadiusDBAuthDriverController getRadiusDBAuthDriverController() {
		return new RadiusDBAuthDriverController();
	}
	
	@Path("/classiccsv")
	public RadiusClassicCSVAcctDriverController getRadiusClassicCSVAcctDriverController() {
		return new RadiusClassicCSVAcctDriverController();
	}
	
	@Path("/dbacct")
	public RadiusDBAcctDriverController getRadiusDBAcctDriverController() {
		return new RadiusDBAcctDriverController();
	}
	
	@Path("/mapgwauth")
	public RadiusMapGWAuthDriverController getRadiusMapGWAuthDriverController() {
		return new RadiusMapGWAuthDriverController();
	}
	
	@Path("/ldapauth")
	public RadiusLDAPDriverController getRadiusLdapDriverController() {
		return new RadiusLDAPDriverController();
	}
	
	@Path("/httpauth")
	public RadiusHTTPAuthDriverController getRadiusHTTPAuthDriverController() {
		return new RadiusHTTPAuthDriverController();
	}
	
	@Path("/webserviceauth")
	public RadiusWebServiceAuthDriverController getRadiusWebServiceAuthDriverController() {
		return new RadiusWebServiceAuthDriverController();
	}

	@Path("/hssauth")
	public RadiusHSSAuthDriverController getRadiusHSSAuthDriverController() {
		return new RadiusHSSAuthDriverController();
	}
	

}

