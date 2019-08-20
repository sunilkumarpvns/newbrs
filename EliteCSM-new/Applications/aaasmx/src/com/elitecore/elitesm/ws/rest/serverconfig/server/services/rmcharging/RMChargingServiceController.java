package com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmcharging;

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

public class RMChargingServiceController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get Resource Manager Charging Service configuration, Reason : entered server name  \""+ serverName + "\" is a EliteCSM Server, it must be Resource Manager Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData rmChargingServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RM_CHARGING_SERVICE);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(rmChargingServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(rmChargingServiceData.getNetServiceId(), configId);

		String rmCharging = new String(data);
		StringReader strReaderchargingService = new StringReader(rmCharging);

		RMChargingServiceData rmChargingData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(RMChargingServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		rmChargingData = (RMChargingServiceData) jaxbUnmarshaller.unmarshal(strReaderchargingService);
		
		ServiceRestUtility.getPluginName(rmChargingData.getPluginsDetails());
		
		rmChargingData.getLogger().setRollingType(ServiceRestUtility.getRollingType(rmChargingData.getLogger().getRollingType()));
		
		rmChargingData.setServicePolicies(ServiceRestUtility.getServicePolicy(rmChargingData.getServicePolicies()));
		
		rmChargingData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(rmChargingData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(rmChargingData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid RMChargingServiceData rmChargingServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		rmChargingServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(rmChargingServiceData.getLogger().getRollingType()));
		rmChargingServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(rmChargingServiceData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update Resource Manager Charging Service configuration, Reason : entered server name  \""+ serverName + "\" is a EliteCSM Server, it must be Resource Manager Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaNasData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RM_CHARGING_SERVICE);

		xmlData = ConfigUtil.serialize(RMChargingServiceData.class, rmChargingServiceData);

		netServiceBLManager.updateService(diaNasData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Resource Manager Charging Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid RMChargingServiceData rmChargingServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, rmChargingServiceData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RM_CHARGING_SERVICE);
	}

}
