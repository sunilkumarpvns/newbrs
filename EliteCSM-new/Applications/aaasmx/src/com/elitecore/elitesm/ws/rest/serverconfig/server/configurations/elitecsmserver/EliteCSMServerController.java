package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.clients.ClientsConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.dhcpkey.EliteCSMServerDHCPKeyConfigController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.DiameterStackConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.EliteAAAServerConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.systemmapping.EliteCSMServerSystemMappingController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.vsa.EliteCSMVSAInClassConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.wimax.EliteCSMServerWimaxConfigurationController;


@Path("/")
public class EliteCSMServerController {

	@Path("/clients")
	public ClientsConfigurationController getClientsConfigurationController(){
		return new ClientsConfigurationController();
	}
	
	@Path("/diameterstack")
	public DiameterStackConfigurationController getDiameterStackConfigurationController(){
		return new DiameterStackConfigurationController();
	}
	
	@Path("/eliteaaaserver")
	public EliteAAAServerConfigurationController getEliteAAServerConfigurationController(){
		return new EliteAAAServerConfigurationController();
	}
	
	@Path("/wimax")
	public EliteCSMServerWimaxConfigurationController getWimaxConfigurationController(){
		return new EliteCSMServerWimaxConfigurationController();
	}
	
	@Path("/dhcpkeys")
	public EliteCSMServerDHCPKeyConfigController getDHCPKeysConfigurationController(){
		return new EliteCSMServerDHCPKeyConfigController();
	}
	
	@Path("/vsa")
	public EliteCSMVSAInClassConfigurationController getVSAInClassConfigurationController(){
		return new EliteCSMVSAInClassConfigurationController();
	}
	
	@Path("/systemmapping")
	public EliteCSMServerSystemMappingController getSystemMappingConfigurationController(){
		return new EliteCSMServerSystemMappingController();
	}
}
