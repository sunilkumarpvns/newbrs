package com.elitecore.elitesm.ws.rest.serverconfig.server.services.radauth;

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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.ServiceRestUtility;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@Path("/")
public class AuthenticationServiceController {

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteAAA Radius Authentication Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		INetServiceInstanceData radAuthServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_AUTH);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(radAuthServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(radAuthServiceData.getNetServiceId(), configId);

		String radAuth = new String(data);
		StringReader strReaderRadAuth = new StringReader(radAuth);

		RadAuthServiceData radAuthData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(RadAuthServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		radAuthData = (RadAuthServiceData) jaxbUnmarshaller.unmarshal(strReaderRadAuth);
		
		ServiceRestUtility.getPluginName(radAuthData.getPluginsDetails());
		
		radAuthData.getLogger().setRollingType(ServiceRestUtility.getRollingType(radAuthData.getLogger().getRollingType()));
		
		radAuthData.setServicePolicies(ServiceRestUtility.getServicePolicy(radAuthData.getServicePolicies()));
		
		radAuthData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(radAuthData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(radAuthData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		return getByNameFromQuery(serverName);
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid RadAuthServiceData radAuthServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;
		
		radAuthServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(radAuthServiceData.getLogger().getRollingType()));
		radAuthServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(radAuthServiceData.getLogger().getSysLogConfiguration().getFacility()));
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();
		
		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteAAA Radius Authentication Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		INetServiceInstanceData radAuthData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_AUTH);

		xmlData = ConfigUtil.serialize(RadAuthServiceData.class, radAuthServiceData);
		
		netServiceBLManager.updateService(radAuthData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Radius Authentication Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid RadAuthServiceData radAuthServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, radAuthServiceData);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADAUTH_SERVICE);
	}

}
