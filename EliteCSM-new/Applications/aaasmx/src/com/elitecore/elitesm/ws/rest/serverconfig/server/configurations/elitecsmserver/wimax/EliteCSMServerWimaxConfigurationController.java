package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.wimax;

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
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class EliteCSMServerWimaxConfigurationController {
	private static final String FLOW_BASED_ACCOUNTING = "Flow-based accounting";
	private static final String IP_SESSION_BASED_ACCOUNTING = "IP-session-based accounting";
	private static final String REQUIRED = "Required";
	private static final String NOT_REQUIRED = "Not Required";
	public static final String WIMAX_CONFIGURATION = "WiMAX Config";
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName.trim());
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive Wimax Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData wiMaxConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,WIMAX_CONFIGURATION);
		
		INetConfigurationData dbWiMaxConfigurationData = wiMaxConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = dbWiMaxConfigurationData.getNetConfigId();
		
		byte[] wiMaxConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String wiMaxData = new String(wiMaxConfigurationStream);
		StringReader wiMaxConfigDataStrReader =new StringReader(wiMaxData.trim());
		
		WimaxConfigurationData wiMaxConfigurationData  = null;
		
		try {
			wiMaxConfigurationData=ConfigUtil.deserialize(wiMaxConfigDataStrReader, WimaxConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive WiMax configuration, Reason: "+e.getMessage(),e);
		}
		
		String accountingCapabilities = wiMaxConfigurationData.getAccountingCapabilities();
		if("1".equals(accountingCapabilities)){
			wiMaxConfigurationData.setAccountingCapabilities(IP_SESSION_BASED_ACCOUNTING);
		}else if("2".equals(accountingCapabilities)){
			wiMaxConfigurationData.setAccountingCapabilities(FLOW_BASED_ACCOUNTING);
		}
		
		String nofiicationCapabilities = wiMaxConfigurationData.getIdleModeNotificationCapabilities();
		if("0".equals(nofiicationCapabilities)){
			wiMaxConfigurationData.setIdleModeNotificationCapabilities(NOT_REQUIRED);
		}else if("1".equals(nofiicationCapabilities)){
			wiMaxConfigurationData.setIdleModeNotificationCapabilities(REQUIRED);
		}
		
		return Response.ok(wiMaxConfigurationData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateWimaxConfigurationByQueryParam(@Valid WimaxConfigurationData wiMaxConfigurationData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update WiMax configuration, Reason: Server Instance not found", ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update WiMax Configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String accountingCapabilities = wiMaxConfigurationData.getAccountingCapabilities();
		if(IP_SESSION_BASED_ACCOUNTING.equalsIgnoreCase(accountingCapabilities)){
			wiMaxConfigurationData.setAccountingCapabilities("1");
		}else if(FLOW_BASED_ACCOUNTING.equalsIgnoreCase(accountingCapabilities)){
			wiMaxConfigurationData.setAccountingCapabilities("2");
		}
		
		String noficaitionCapabilities = wiMaxConfigurationData.getIdleModeNotificationCapabilities();
		if(REQUIRED.equalsIgnoreCase(noficaitionCapabilities)){
			wiMaxConfigurationData.setIdleModeNotificationCapabilities("1");
		}else if(NOT_REQUIRED.equalsIgnoreCase(noficaitionCapabilities)){
			wiMaxConfigurationData.setIdleModeNotificationCapabilities("0");
		}
		
		
		String wiMaxConfigSerializeData = null ;
		try {
			wiMaxConfigSerializeData = ConfigUtil.serialize(WimaxConfigurationData.class, wiMaxConfigurationData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update WiMax Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(wiMaxConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), WIMAX_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("WiMax Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateWimaxConfigurationByPathParam(@Valid WimaxConfigurationData wimaxConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, JAXBException{
		return updateWimaxConfigurationByQueryParam(wimaxConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getWiMaxConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_WIMAX_COFIGURATION);
	}

}
