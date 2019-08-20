package com.elitecore.elitesm.ws.rest.serverconfig.server.services.diametereap;

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

public class DiameterEAPServiceController {

	@GET
	public Response getByNameByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to get EliteDSC Diameter EAP Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaEapServiceData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_EAP);

		String configId = netServiceBLManager.getConfigIdByServiceTypeId(diaEapServiceData.getNetServiceTypeId());
		byte[] data = netServiceBLManager.getServiceConfigurationStream(diaEapServiceData.getNetServiceId(), configId);

		String diaEap = new String(data);
		StringReader strReaderRadAuth = new StringReader(diaEap);

		DiaEapServiceData diaEapServiveData = null;

		JAXBContext jaxbContext = null;
		Unmarshaller jaxbUnmarshaller = null;

		jaxbContext = JAXBContext.newInstance(DiaEapServiceData.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		diaEapServiveData = (DiaEapServiceData) jaxbUnmarshaller.unmarshal(strReaderRadAuth);
	
		diaEapServiveData.getLogger().setRollingType(ServiceRestUtility.getRollingType(diaEapServiveData.getLogger().getRollingType()));
		
		diaEapServiveData.setServicePolicies(ServiceRestUtility.getServicePolicy(diaEapServiveData.getServicePolicies()));
		diaEapServiveData
				.getLogger()
				.getSysLogConfiguration()
				.setFacility(ServiceRestUtility.getSyslogFacility(diaEapServiveData.getLogger().getSysLogConfiguration().getFacility()));

		return Response.ok(diaEapServiveData).build();


	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String serverName, @Valid DiaEapServiceData diaEapServiceData) 
			throws DataManagerException, JAXBException {

		String xmlData = null;

		diaEapServiceData.getLogger().setRollingType(ServiceRestUtility.setRollingType(diaEapServiceData.getLogger().getRollingType()));
		diaEapServiceData.getLogger().getSysLogConfiguration().setFacility(ServiceRestUtility.setSyslogFacility(diaEapServiceData.getLogger().getSysLogConfiguration().getFacility()));

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager =  new NetServiceBLManager();

		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);

		if (netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false) {
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteDSC Diameter EAP Service configuration, Reason : entered server name  \""+ serverName + "\" is a Resource Manager Server, it must be EliteAAA Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		INetServiceInstanceData diaEapData = netServiceBLManager.getNetServiceInstanceByServiceName(netServerInstanceData.getNetServerId(), ServiceRestUtility.DIAMETER_EAP);

		xmlData = ConfigUtil.serialize(DiaEapServiceData.class, diaEapServiceData);

		netServiceBLManager.updateService(diaEapData.getNetServiceId(), xmlData.getBytes());

		return Response.ok(RestUtitlity.getResponse("Diameter EAP Service updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String serverName, @Valid DiaEapServiceData diaEapServiceData) throws DataManagerException, JAXBException {
		return updateByQueryParam(serverName, diaEapServiceData);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_EAP_SERVICE);
	}

}
