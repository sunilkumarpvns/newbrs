package com.elitecore.elitesm.ws.rest.serverconfig.server.services.radacct;

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
public class AccountingServiceController {

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteAAA Radius Accounting Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		INetServiceInstanceData radAcctServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_ACCT);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(radAcctServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(radAcctServiceData.getNetServiceId(), configId);

		String radAcct = new String(data);
		StringReader strReaderRadAcct = new StringReader(radAcct);

		RadAcctServiceData radAcctData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(RadAcctServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		radAcctData = (RadAcctServiceData) jaxbUnmarshaller.unmarshal(strReaderRadAcct);

		ServiceRestUtility.getPluginName(radAcctData.getPluginsDetails());
		
		radAcctData.getLogger().setRollingType(ServiceRestUtility.getRollingType(radAcctData.getLogger().getRollingType()));
		
		radAcctData.setServicePolicies(ServiceRestUtility.getServicePolicy(radAcctData.getServicePolicies()));
		
		radAcctData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(radAcctData.getLogger().getSysLogConfiguration().getFacility()));


		return Response.ok(radAcctData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		return getByNameFromQuery(serverName);
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid RadAcctServiceData radAcctServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;
		
		radAcctServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(radAcctServiceData.getLogger().getRollingType()));
		radAcctServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(radAcctServiceData.getLogger().getSysLogConfiguration().getFacility()));
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();
		
		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteAAA Radius Accounting Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server.", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		INetServiceInstanceData radAcctData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RAD_ACCT);

		xmlData = ConfigUtil.serialize(RadAcctServiceData.class, radAcctServiceData);
		
		netServiceBLManager.updateService(radAcctData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Radius Accounting Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid RadAcctServiceData radAcctServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, radAcctServiceData);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADACCT_SERVICE);
	}

}
