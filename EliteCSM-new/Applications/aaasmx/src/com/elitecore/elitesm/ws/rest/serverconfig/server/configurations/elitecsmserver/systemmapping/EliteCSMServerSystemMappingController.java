package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.systemmapping;

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
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.systemmapping.data.SystemMappingConfigurationData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class EliteCSMServerSystemMappingController {
	public static final String SYSTEM_MAPPING_CONFIGURATION = "System Mapping";
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName.trim());
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive System Mapping Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData systemMappingConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,SYSTEM_MAPPING_CONFIGURATION);
		
		INetConfigurationData systemMappingConfigurationData = systemMappingConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = systemMappingConfigurationData.getNetConfigId();
		
		byte[] systemMappingConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String systemMappingData = new String(systemMappingConfigurationStream);
		StringReader systemMappingConfigDataStrReader =new StringReader(systemMappingData.trim());
		
		SystemMappingConfigurationData sysMappingConfigurationData  = null;
		
		try {
			sysMappingConfigurationData=ConfigUtil.deserialize(systemMappingConfigDataStrReader, SystemMappingConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive System Mapping configuration, Reason: "+e.getMessage(),e);
		}
		
		return Response.ok(sysMappingConfigurationData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateSystemMappingConfigurationByQueryParam(@Valid SystemMappingConfigurationData systemMappingConfigurationData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update System Mapping configuration, Reason: Server Instance not found", ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update System Mapping Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String systemMappingConfigSerializeData = null ;
		try {
			systemMappingConfigSerializeData = ConfigUtil.serialize(SystemMappingConfigurationData.class, systemMappingConfigurationData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update System Mapping Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(systemMappingConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), SYSTEM_MAPPING_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("System Mapping Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateSystemMappingConfigurationByPathParam(@Valid SystemMappingConfigurationData systemMappingConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException,JAXBException{
		return updateSystemMappingConfigurationByQueryParam(systemMappingConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getSystemMappingConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_SYSTEM_MAPPING_CONFIGURATION);
	}
}
