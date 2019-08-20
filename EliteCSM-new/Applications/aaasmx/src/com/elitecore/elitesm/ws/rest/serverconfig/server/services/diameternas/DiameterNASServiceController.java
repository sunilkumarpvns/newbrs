package com.elitecore.elitesm.ws.rest.serverconfig.server.services.diameternas;

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

public class DiameterNASServiceController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteDSC Diameter NAS Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData DiaNasServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_NAS);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(DiaNasServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(DiaNasServiceData.getNetServiceId(), configId);

		String radAuth = new String(data);
		StringReader strReaderRadAuth = new StringReader(radAuth);

		DiaNasServiceData diaNasServiveData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(DiaNasServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		diaNasServiveData = (DiaNasServiceData) jaxbUnmarshaller.unmarshal(strReaderRadAuth);

		diaNasServiveData.getLogger().setRollingType(ServiceRestUtility.getRollingType(diaNasServiveData.getLogger().getRollingType()));
		
		diaNasServiveData.setServicePolicies(ServiceRestUtility.getServicePolicy(diaNasServiveData.getServicePolicies()));
		
		diaNasServiveData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(diaNasServiveData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(diaNasServiveData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid DiaNasServiceData diamNasServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		diamNasServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(diamNasServiceData.getLogger().getRollingType()));
		diamNasServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(diamNasServiceData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteDSC Diameter NAS Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaNasData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_NAS);

		xmlData = ConfigUtil.serialize(DiaNasServiceData.class, diamNasServiceData);

		netServiceBLManager.updateService(diaNasData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Diameter NAS Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid DiaNasServiceData diaNasServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, diaNasServiceData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_NAS_SERVICE);
	}

}
