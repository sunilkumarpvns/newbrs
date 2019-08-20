package com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmippool;

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

public class RMIPPoolServiceController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get Resource Manager IPPool Service configuration, Reason : entered server name  \""+ serverName + "\" is a EliteCSM Server, it must be Resource Manager Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData rmIpPoolServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RM_IPPOOL_SERVICE);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(rmIpPoolServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(rmIpPoolServiceData.getNetServiceId(), configId);

		String rmIpPool = new String(data);
		StringReader strReaderchargingService = new StringReader(rmIpPool);

		RMIpPoolServiceData rmIpPoolData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(RMIpPoolServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		rmIpPoolData = (RMIpPoolServiceData) jaxbUnmarshaller.unmarshal(strReaderchargingService);
		
		ServiceRestUtility.getPluginName(rmIpPoolData.getPluginsDetails());
		
		rmIpPoolData.getLogger().setRollingType(ServiceRestUtility.getRollingType(rmIpPoolData.getLogger().getRollingType()));
		
		rmIpPoolData
		.getLogger()
		.getSysLogConfiguration()
		.setFacility(ServiceRestUtility.getSyslogFacility(rmIpPoolData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(rmIpPoolData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid RMIpPoolServiceData rmIpPoolServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		rmIpPoolServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(rmIpPoolServiceData.getLogger().getRollingType()));
		rmIpPoolServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(rmIpPoolServiceData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update Resource Manager IPPool Service configuration, Reason : entered server name  \""+ serverName + "\" is a EliteCSM Server, it must be Resource Manager Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData ipPoolData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.RM_IPPOOL_SERVICE);

		xmlData = ConfigUtil.serialize(RMIpPoolServiceData.class, rmIpPoolServiceData);

		netServiceBLManager.updateService(ipPoolData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Resource Manager IPPool Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid RMIpPoolServiceData rmIpPoolServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, rmIpPoolServiceData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RM_IPPOOL_SERVICE);
	}

}
