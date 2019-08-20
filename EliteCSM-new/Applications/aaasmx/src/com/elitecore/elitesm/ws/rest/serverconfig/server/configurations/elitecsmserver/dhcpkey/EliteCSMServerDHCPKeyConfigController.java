package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.dhcpkey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class EliteCSMServerDHCPKeyConfigController {
	
	public static final String DHCP_KEYS_CONFIGURATION = "DHCP Keys Config";
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName.trim());
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive DHCP Keys Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData wiMaxConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,DHCP_KEYS_CONFIGURATION);
		
		INetConfigurationData dbDHCPKeyConfigurationData = wiMaxConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = dbDHCPKeyConfigurationData.getNetConfigId();
		
		byte[] dhcpKeysConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String dhcpKeysData = new String(dhcpKeysConfigurationStream);
		StringReader dhcpKeysConfigDataStrReader =new StringReader(dhcpKeysData.trim());
		
		DHCPKeyConfigurationData dhcpKeysConfigurationData  = null;
		
		try {
			dhcpKeysConfigurationData=ConfigUtil.deserialize(dhcpKeysConfigDataStrReader, DHCPKeyConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive DHCP Keys configuration, Reason: "+e.getMessage(),e);
		}
		
		return Response.ok(dhcpKeysConfigurationData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateDHCPKeysConfigurationByQueryParam(@Valid DHCPKeyConfigurationData dhcpKeysConfigurationData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update DHCP Keys configuration, Reason: Server Instance not found", ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update DHCP Keys Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String dhcpKeysConfigSerializeData = null ;
		try {
			dhcpKeysConfigSerializeData = ConfigUtil.serialize(DHCPKeyConfigurationData.class, dhcpKeysConfigurationData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update DHCP Keys Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(dhcpKeysConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), DHCP_KEYS_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("DHCP Keys Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateDHCPKeysConfigurationByPathParam(@Valid DHCPKeyConfigurationData dhcpKeysConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException,JAXBException{
		return updateDHCPKeysConfigurationByQueryParam(dhcpKeysConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getDHCPKeysConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_DHCPKEYS_CONFIGURATION);
	}
}
