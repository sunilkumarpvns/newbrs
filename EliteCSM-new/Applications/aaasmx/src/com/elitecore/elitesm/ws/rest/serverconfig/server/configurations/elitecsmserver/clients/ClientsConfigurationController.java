package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.clients;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;

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
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.clients.data.ClientsConfigurationData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;


public class ClientsConfigurationController {
	
	public final String CLIENTS_CONFIGURATION = "Clients" ;
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName.trim());
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive Clients configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData clientConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,CLIENTS_CONFIGURATION);
		
		INetConfigurationData clientConfigurationData = clientConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = clientConfigurationData.getNetConfigId();
		
		byte[] clientsConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String clientData = new String(clientsConfigurationStream);
		StringReader clientsDataStrReader =new StringReader(clientData.trim());
		
		ClientsConfigurationData clientsConfigurationData  = null;
		
		try {
			clientsConfigurationData=ConfigUtil.deserialize(clientsDataStrReader, ClientsConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive clients configuration, Reason: "+e.getMessage(),e);
		}
		
		return Response.ok(clientsConfigurationData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateClientsConfigurationByQueryParam(@Valid ClientsConfigurationData clientsConfigData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update "+CLIENTS_CONFIGURATION+" configuration, Reason: Server Instance not found", ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update clients configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String clientConfigSerializeData = null ;
		try {
			clientConfigSerializeData = ConfigUtil.serialize(ClientsConfigurationData.class, clientsConfigData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update clients configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(clientConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), CLIENTS_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("Clients Configuration updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateClientsConfigurationByPathParam(@Valid ClientsConfigurationData clientsConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, JAXBException{
		return updateClientsConfigurationByQueryParam(clientsConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getClientsConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_CLIENTS_CONFIGURATION);
	}
}
