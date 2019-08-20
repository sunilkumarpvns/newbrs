package com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametercc;

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

public class DiameterCCServiceController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteDSC Diameter CC Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaCcServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_CC);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(diaCcServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(diaCcServiceData.getNetServiceId(), configId);

		String radAuth = new String(data);
		StringReader strReaderRadAuth = new StringReader(radAuth);

		DiaCCServiceData diaCcServiveData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(DiaCCServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		diaCcServiveData = (DiaCCServiceData) jaxbUnmarshaller.unmarshal(strReaderRadAuth);
		
		diaCcServiveData.getLogger().setRollingType(ServiceRestUtility.getRollingType(diaCcServiveData.getLogger().getRollingType()));
		
		diaCcServiveData.setServicePolicies(ServiceRestUtility.getServicePolicy(diaCcServiveData.getServicePolicies()));
		
		diaCcServiveData
				.getLogger()
				.getSysLogConfiguration()
				.setFacility(ServiceRestUtility.getSyslogFacility(diaCcServiveData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(diaCcServiveData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid DiaCCServiceData diaCcServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		diaCcServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(diaCcServiceData.getLogger().getRollingType()));
		diaCcServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(diaCcServiceData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteDSC Diameter CC Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaCcData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_CC);

		xmlData = ConfigUtil.serialize(DiaCCServiceData.class, diaCcServiceData);

		netServiceBLManager.updateService(diaCcData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Diameter CC Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid DiaCCServiceData diaCcServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, diaCcServiceData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_CC_SERVICE);
	}

}
