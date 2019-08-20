package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.clients.ClientsConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.diameterstack.RMDiameterStackConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.rmserver.EliteRMServerConfigurationController;


@Path("/")
public class RMServerController {
	
	@Path("/clients")
	public ClientsConfigurationController getClientsConfigurationController(){
		return new ClientsConfigurationController();
	}
	
	@Path("/diameterstack")
	public RMDiameterStackConfigurationController getDiameterStackConfigurationController(){
		return new RMDiameterStackConfigurationController();
	}
	
	@Path("/elitermserver")
	public EliteRMServerConfigurationController getDiameterStackConfgurationController(){
		return new EliteRMServerConfigurationController();
	}
}
