package com.elitecore.elitesm.ws.rest.serverconfig.server.services.dynauth;

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
public class DynamicAuthenticationServiceController {

	private static final String TWO = "2";
	private static final String ONE = "1";
	private static final String DELETE = "delete";
	private static final String UPDATE = "update";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteAAA Radius Dynamic Authentication Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
			
		INetServiceInstanceData radDynAuthServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_DYNAUTH);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(radDynAuthServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(radDynAuthServiceData.getNetServiceId(), configId);

		String radDynAuth = new String(data);
		StringReader strReaderRadDynAuth = new StringReader(radDynAuth);

		RadDynAuthServiceData radDynAuthData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(RadDynAuthServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		radDynAuthData = (RadDynAuthServiceData) jaxbUnmarshaller.unmarshal(strReaderRadDynAuth);

		ServiceRestUtility.getPluginName(radDynAuthData.getPluginsDetails());
		
		radDynAuthData.getLogger().setRollingType(ServiceRestUtility.getRollingType(radDynAuthData.getLogger().getRollingType()));
		
		radDynAuthData.setServicePolicies(ServiceRestUtility.getServicePolicy(radDynAuthData.getServicePolicies()));
		
		radDynAuthData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(radDynAuthData.getLogger().getSysLogConfiguration().getFacility()));

		getActionOnSuccessAndFailure(radDynAuthData);

		return Response.ok(radDynAuthData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		return getByNameFromQuery(serverName);
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid RadDynAuthServiceData radDynAuthServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;
		
		radDynAuthServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(radDynAuthServiceData.getLogger().getRollingType()));
		radDynAuthServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(radDynAuthServiceData.getLogger().getSysLogConfiguration().getFacility()));
		
		setActionOnSuccessAndFailure(radDynAuthServiceData);

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteAAA Radius Dynamic Authentication Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		INetServiceInstanceData radAuthData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_DYNAUTH);

		xmlData = ConfigUtil.serialize(RadDynAuthServiceData.class, radDynAuthServiceData);

		netServiceBLManager.updateService(radAuthData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Radius Dynamic Authentication Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid RadDynAuthServiceData radDynAuthServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, radDynAuthServiceData);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADDYNAAUTH_SERVICE);
	}


	private void getActionOnSuccessAndFailure(RadDynAuthServiceData radDynAuthData) throws DataManagerException {
		if (ONE.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnFailure())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnFailure(UPDATE);
		} else if (TWO.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnFailure())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnFailure(DELETE);
		} else  {
			throw new DataManagerException("Invalid Value of Action on Failure.");
		}

		if (ONE.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnSucess())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnSucess(UPDATE);
		} else if (TWO.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnSucess())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnSucess(DELETE);
		} else  {
			throw new DataManagerException("Invalid Value of Action on Success.");
		}

	}

	private void setActionOnSuccessAndFailure(RadDynAuthServiceData radDynAuthData) throws DataManagerException {
		if (UPDATE.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnFailure())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnFailure(TWO);
		} else if (DELETE.equals(radDynAuthData.getExternalDataBaseDetail().getActionOnFailure())) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnFailure(ONE);
		} else  {
			throw new DataManagerException("Invalid Value of Action on Failure.");
		}

		if (radDynAuthData.getExternalDataBaseDetail().getActionOnSucess().equals(UPDATE)) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnSucess(TWO);
		} else if (radDynAuthData.getExternalDataBaseDetail().getActionOnSucess().equals(DELETE)) {
			radDynAuthData.getExternalDataBaseDetail().setActionOnSucess(ONE);
		} else  {
			throw new DataManagerException("Invalid Value of Action on Success.");
		}

	}

}
