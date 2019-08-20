package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.rmserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.ServiceDetailsData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.rmserver.data.RMServerConfigurationData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.PasswordEncryption;

public class EliteRMServerConfigurationController {
	public final String ELITE_RM_SERVER_CONFIGURATION = "Elite RM Server" ;
	public EAPConfigBLManager eapBLManager = new EAPConfigBLManager();

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if(netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive EliteRM Server configuration, Reason : entered server name's type is EliteCSM Server",ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData eliteAAAServerConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,ELITE_RM_SERVER_CONFIGURATION);
		
		INetConfigurationData eliteRMServerConfigurationData = eliteAAAServerConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = eliteRMServerConfigurationData.getNetConfigId();
		
		byte[] eliteRmServerConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String eliteRMServerConfigDataStr = new String(eliteRmServerConfigurationStream);
		StringReader eliteAAAServerDataStrReader =new StringReader(eliteRMServerConfigDataStr.trim());
		
		RMServerConfigurationData rmServerConfigData  = null;
		
		try {
			rmServerConfigData=ConfigUtil.deserialize(eliteAAAServerDataStrReader, RMServerConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive EliteRM Server configuration, Reason: "+e.getMessage(),e);
		}
		
		String rollingTypeId = rmServerConfigData.getLoggerDetail().getRollingType();
		String rollingTypeName = "";
		if (RestValidationMessages.TIME_BASED_ROLLING_TYPE.equals(rollingTypeId)) {
			rollingTypeName = RestValidationMessages.TIME_BASED;
		} else if (RestValidationMessages.SIZE_BASED_ROLLING_TYPE.equals(rollingTypeId)) {
			rollingTypeName = RestValidationMessages.SIZE_BASED;
		}
		rmServerConfigData.getLoggerDetail().setRollingType(rollingTypeName);
		
		String facilityStr = rmServerConfigData.getLoggerDetail().getSysLogConfiguration().getFacility();
		if (Strings.isNullOrBlank(facilityStr)) {
			rmServerConfigData.getLoggerDetail().getSysLogConfiguration().setFacility(RestValidationMessages.NONE_WITH_HYPHEN);
		}

		List<String> alertListenerConfigNames = rmServerConfigData.getAlertListnersDetail().getAlertListnersList();
		List<String> configuratioNames = new ArrayList<String>();
		for(String alertListenerName : alertListenerConfigNames){
			if(Strings.isNullOrBlank(alertListenerName) == false){
				configuratioNames.add(alertListenerName);
			}else{
				configuratioNames.add(RestValidationMessages.NONE_WITH_HYPHEN);
			}
		}
		rmServerConfigData.getAlertListnersDetail().setAlertListnersList(configuratioNames);
		
		String databaseDSName = rmServerConfigData.getKpiServiceConfiguration().getDsName();
		if(Strings.isNullOrBlank(databaseDSName)){
			rmServerConfigData.getKpiServiceConfiguration().setDsName(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		rmServerConfigData.getAaaDatasourceDetail().setPassword(null);
		
		return Response.ok(rmServerConfigData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateEliteRMServerConfigurationByQueryParam(@Valid RMServerConfigurationData rmServerConfigData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update "+ELITE_RM_SERVER_CONFIGURATION+" configuration, Reason: Server Instance not found",ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteRM Server configuration, Reason : entered server name's type is EliteCSM Server", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		List<String> configuredServicesNames = netServerBLManager.getListOfServicesAliasNamesFromListOfServices(netServerBLManager.getListOfServicesByServerId(netServerInstanceData.getNetServerId()));
		Set<String> servicesNames = new TreeSet<String>();
		
		List<ServiceDetailsData> serviceDetailsDatas = rmServerConfigData.getServiceTypes().getConfiguredServiceList();
		if(Collectionz.isNullOrEmpty(serviceDetailsDatas) == false){
			for(ServiceDetailsData serviceDetailsData:serviceDetailsDatas){
				servicesNames.add(serviceDetailsData.getServiceId());
			}
			
		}
		int configuredServicesSize = configuredServicesNames.size();
		int updateServicesSize = servicesNames.size();
		
		boolean isChangedServicesList = true;
		if(configuredServicesSize != updateServicesSize){
			isChangedServicesList = false;
		}
		
		if(isChangedServicesList){
			for(String serviceName : servicesNames){
				if(configuredServicesNames.contains(serviceName) == false){
					isChangedServicesList = false;
					break;
				}
			}
		}
		if(isChangedServicesList == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to add or remove services from this configuration, you can only enable or disable configured services", ResultCode.OPERATION_NOT_SUPPORTED)).build();
		}
		
		if(rmServerConfigData.getAaaDatasourceDetail() != null){
			String plainPassword = rmServerConfigData.getAaaDatasourceDetail().getPassword();
			String encryptedPassword = "";
			try {
				encryptedPassword = PasswordEncryption.getInstance().crypt(plainPassword, PasswordEncryption.ELITE_PASSWORD_CRYPT);
			} catch (Exception e) {
				throw new DataManagerException("Failed to update EliteRM Server configuration , Reason: "+e.getMessage());
			}
			rmServerConfigData.getAaaDatasourceDetail().setPassword(encryptedPassword);
		}
		
		if(rmServerConfigData.getLoggerDetail() != null && rmServerConfigData.getLoggerDetail().getSysLogConfiguration() != null){
			String facilityName = rmServerConfigData.getLoggerDetail().getSysLogConfiguration().getFacility();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facilityName)){
				rmServerConfigData.getLoggerDetail().getSysLogConfiguration().setFacility("");
			}
		}
		
		if(rmServerConfigData.getKpiServiceConfiguration() != null){
			String datasourceName = rmServerConfigData.getKpiServiceConfiguration().getDsName();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(datasourceName)){
				rmServerConfigData.getKpiServiceConfiguration().setDsName("");
			}
		}
		
		String aaaServerConfigSerializeData = null ;
		try {
			aaaServerConfigSerializeData = ConfigUtil.serialize(RMServerConfigurationData.class, rmServerConfigData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update EliteRM Server Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(aaaServerConfigSerializeData.getBytes(),netServerInstanceData.getNetServerId(), ELITE_RM_SERVER_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("EliteRM Server Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateEliteRMServerConfigurationByPathParam(@Valid RMServerConfigurationData rmServerConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, JAXBException{
		return updateEliteRMServerConfigurationByQueryParam(rmServerConfigData,serverInstanceName);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITERM_CONFIGURATION);
	}
}
