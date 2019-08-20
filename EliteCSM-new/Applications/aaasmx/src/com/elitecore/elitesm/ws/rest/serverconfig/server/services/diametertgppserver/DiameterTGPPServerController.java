package com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametertgppserver;

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

public class DiameterTGPPServerController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteDSC Diameter TGPP Server configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaTgppServerData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_TGPP);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(diaTgppServerData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(diaTgppServerData.getNetServiceId(), configId);

		String diaTgpp = new String(data);
		StringReader strReaderRadAuth = new StringReader(diaTgpp);

		DiaTGPPServerData diaTgppData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(DiaTGPPServerData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		diaTgppData = (DiaTGPPServerData) jaxbUnmarshaller.unmarshal(strReaderRadAuth);
		
		diaTgppData.getLogger().setRollingType(ServiceRestUtility.getRollingType(diaTgppData.getLogger().getRollingType()));
		
		diaTgppData.setServicePolicies(ServiceRestUtility.getServicePolicy(diaTgppData.getServicePolicies()));
		
		diaTgppData
				.getLogger()
				.getSysLogConfiguration()
				.setFacility(ServiceRestUtility.getSyslogFacility(diaTgppData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(diaTgppData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid DiaTGPPServerData diaTgppServerData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		diaTgppServerData.getLogger().setRollingType(ServiceRestUtility.setRollingType(diaTgppServerData.getLogger().getRollingType()));
		diaTgppServerData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(diaTgppServerData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteDSC Diameter TGPP Server configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaTgppData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_TGPP);

		xmlData = ConfigUtil.serialize(DiaTGPPServerData.class, diaTgppServerData);

		netServiceBLManager.updateService(diaTgppData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Diameter TGPP Server updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid DiaTGPPServerData diaTgppServerData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, diaTgppServerData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_TGPP_SERVER);
	}

}
