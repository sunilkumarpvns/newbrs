package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.vsa;

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
import com.sun.org.apache.bcel.internal.generic.RETURN;

public class EliteCSMVSAInClassConfigurationController {
	public static final String VSA_IN_CLASSATTRIBUTE = "VSA in Class Attribute";
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName.trim());
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive VSA In Class Attribute Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData vsaInClassConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,VSA_IN_CLASSATTRIBUTE);
		
		INetConfigurationData dbVSAInClassConfigurationData = vsaInClassConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = dbVSAInClassConfigurationData.getNetConfigId();
		
		byte[] vsaInClassConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String vsaInClassData = new String(vsaInClassConfigurationStream);
		
		StringReader vsaInClassConfigDataStrReader = new StringReader(vsaInClassData.trim());
		
		VSAInClassConfigurationData vsaInClassConfigurationData  = null;
		
		try {
			vsaInClassConfigurationData=ConfigUtil.deserialize(vsaInClassConfigDataStrReader, VSAInClassConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive VSA In Class Attribute configuration, Reason: "+e.getMessage(),e);
		}
		
		return Response.ok(vsaInClassConfigurationData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateVSAInClassConfigurationByQueryParam(@Valid VSAInClassConfigurationData vsaInClassConfigurationData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update VSA In Class Attribute Configuration, Reason: Server Instance not found", ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update VSA In Class Attribute Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String vsaInClassConfigSerializeData = null ;
		try {
			vsaInClassConfigSerializeData = ConfigUtil.serialize(VSAInClassConfigurationData.class, vsaInClassConfigurationData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update VSA In Class Attribute Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(vsaInClassConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), VSA_IN_CLASSATTRIBUTE);
		
		return Response.ok(RestUtitlity.getResponse("VSA In Class Attribute Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateVSAInClassConfigurationByPathParam(@Valid VSAInClassConfigurationData vsaInClassConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException,JAXBException{
		return updateVSAInClassConfigurationByQueryParam(vsaInClassConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getDHCPKeysConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_VSA_CONFIGURATION);
	}

}
