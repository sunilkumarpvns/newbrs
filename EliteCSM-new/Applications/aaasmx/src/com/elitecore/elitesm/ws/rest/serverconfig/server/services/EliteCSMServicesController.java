package com.elitecore.elitesm.ws.rest.serverconfig.server.services;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametercc.DiameterCCServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametereap.DiameterEAPServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.diameternas.DiameterNASServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametertgppserver.DiameterTGPPServerController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.dynauth.DynamicAuthenticationServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.radacct.AccountingServiceController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.radauth.AuthenticationServiceController;

@Path("/")
public class EliteCSMServicesController {
	
	@Path("/radauth")
	public AuthenticationServiceController getAuthenticationServiceController() {
		return new AuthenticationServiceController();
	}
	
	@Path("/radacct")
	public AccountingServiceController getAccountingServiceController() {
		return new AccountingServiceController();
	}
	
	@Path("/raddynauth")
	public DynamicAuthenticationServiceController getDynamicAuthenticationServiceController() {
		return new DynamicAuthenticationServiceController();
		
	}
	
	@Path ("/diameternas")
	public DiameterNASServiceController getDiameterNASServiceController() {
		return new DiameterNASServiceController();
	}
	
	@Path("/diametercc")
	public DiameterCCServiceController getDiameterCCServiceController() {
		return new DiameterCCServiceController();
	}
	
	@Path("/diametereap")
	public DiameterEAPServiceController getDiameterEAPServiceController() {
		return new DiameterEAPServiceController();
	}
	
	@Path("/diametertgppserver")
	public DiameterTGPPServerController getDiameterTGPPServerController() {
		return new DiameterTGPPServerController();
	}

}
