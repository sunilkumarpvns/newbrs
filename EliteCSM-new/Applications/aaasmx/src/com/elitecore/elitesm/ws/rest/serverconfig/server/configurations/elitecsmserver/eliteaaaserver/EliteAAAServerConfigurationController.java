package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
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
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.AAAServerConfigurationData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.ServiceDetailsData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class EliteAAAServerConfigurationController {
	public final String ELITE_AAA_SERVER_CONFIGURATION = "EliteAAAServer" ;
	public EAPConfigBLManager eapBLManager = new EAPConfigBLManager();

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive EliteAAA Server configuration, Reason : entered server name's type is Resource Manager",ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData eliteAAAServerConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,ELITE_AAA_SERVER_CONFIGURATION);
		
		INetConfigurationData eliteAAAServerConfigurationData = eliteAAAServerConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = eliteAAAServerConfigurationData.getNetConfigId();
		
		byte[] eliteAAAServerConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String eliteAAAServerConfigDataStr = new String(eliteAAAServerConfigurationStream);
		StringReader eliteAAAServerDataStrReader =new StringReader(eliteAAAServerConfigDataStr.trim());
		
		AAAServerConfigurationData aaaServerConfigData  = null;
		
		try {
			aaaServerConfigData=ConfigUtil.deserialize(eliteAAAServerDataStrReader, AAAServerConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive EliteAAA Server configuration, Reason: "+e.getMessage(),e);
		}
		
		String rollingTypeId = aaaServerConfigData.getLoggerDetail().getRollingType();
		String rollingTypeName = "";
		if ("1".equals(rollingTypeId)) {
			rollingTypeName = RestValidationMessages.TIME_BASED;
		} else if ("2".equals(rollingTypeId)) {
			rollingTypeName = RestValidationMessages.SIZE_BASED;
		}
		aaaServerConfigData.getLoggerDetail().setRollingType(rollingTypeName);
		
		String facilityStr = aaaServerConfigData.getLoggerDetail().getSysLogConfiguration().getFacility();
		if (Strings.isNullOrBlank(facilityStr)) {
			aaaServerConfigData.getLoggerDetail().getSysLogConfiguration().setFacility(RestValidationMessages.NONE_WITH_HYPHEN);
		}

		List<String> alertListenerConfigNames = aaaServerConfigData.getAlertListnersDetail().getAlertListnersList();
		List<String> configuratioNames = new ArrayList<String>();
		for(String alertListenerName : alertListenerConfigNames){
			if(Strings.isNullOrBlank(alertListenerName) == false){
				configuratioNames.add(alertListenerName);
			}else{
				configuratioNames.add(RestValidationMessages.NONE_WITH_HYPHEN);
			}
		}
		aaaServerConfigData.getAlertListnersDetail().setAlertListnersList(configuratioNames);
		
		String databaseDSName = aaaServerConfigData.getKpiServiceConfiguration().getDsName();
		if(Strings.isNullOrBlank(databaseDSName)){
			aaaServerConfigData.getKpiServiceConfiguration().setDsName(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		String certificateId = aaaServerConfigData.getWebServiceConfiguration().getServerCertificateProfileId();
		if(Strings.isNullOrBlank(certificateId)==false){
			String certificateName = "";
				certificateName = eapBLManager.getServerCertificateNameFromId(certificateId);
				if(Strings.isNullOrBlank(certificateName)){
					certificateName = RestValidationMessages.NONE_WITH_HYPHEN;
				}
			
			aaaServerConfigData.getWebServiceConfiguration().setServerCertificateProfileId(certificateName);
		}else{
			aaaServerConfigData.getWebServiceConfiguration().setServerCertificateProfileId(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		aaaServerConfigData.getAaaDatasourceDetail().setPassword(null);
		
		return Response.ok(aaaServerConfigData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateEliteAAAServerConfigurationByQueryParam(@Valid AAAServerConfigurationData aaaServerConfigData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		
		NetServerInstanceData netServerInstanceData;
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update "+ELITE_AAA_SERVER_CONFIGURATION+" configuration, Reason: Server Instance not found",ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update EliteAAA Server configuration, Reason : entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		List<String> configuredServicesNames = netServerBLManager.getListOfServicesAliasNamesFromListOfServices(netServerBLManager.getListOfServicesByServerId(netServerInstanceData.getNetServerId()));
		Set<String> servicesNames = new TreeSet<String>();
		
		List<ServiceDetailsData> serviceDetailsDatas = aaaServerConfigData.getServiceTypes().getConfiguredServiceList();
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
		
		if(aaaServerConfigData.getAaaDatasourceDetail() != null){
			String plainPassword = aaaServerConfigData.getAaaDatasourceDetail().getPassword();
			String encryptedPassword = "";
			try {
				encryptedPassword = PasswordEncryption.getInstance().crypt(plainPassword, PasswordEncryption.ELITE_PASSWORD_CRYPT);
			} catch (Exception e) {
				throw new DataManagerException("Failed to update EliteAAA Server configuration , Reason: "+e.getMessage());
			}
			aaaServerConfigData.getAaaDatasourceDetail().setPassword(encryptedPassword);
		}
		
		if(aaaServerConfigData.getWebServiceConfiguration() != null){
			String certificateProfileName = aaaServerConfigData.getWebServiceConfiguration().getServerCertificateProfileId();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(certificateProfileName) == false){
				try{
					String certificateId = eapBLManager.getServerCertificateIdFromName(certificateProfileName);
					aaaServerConfigData.getWebServiceConfiguration().setServerCertificateProfileId(String.valueOf(certificateId));
				}catch(Exception e){
					throw new DataManagerException("Failed to update EliteAAA Server configuration , Reason: "+e.getMessage());	
				}
			}else{
				aaaServerConfigData.getWebServiceConfiguration().setServerCertificateProfileId("");
			}
				
		}
		
		if(aaaServerConfigData.getLoggerDetail() != null && aaaServerConfigData.getLoggerDetail().getSysLogConfiguration() != null){
			String facilityName = aaaServerConfigData.getLoggerDetail().getSysLogConfiguration().getFacility();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facilityName)){
				aaaServerConfigData.getLoggerDetail().getSysLogConfiguration().setFacility("");
			}
		}
		
		if(aaaServerConfigData.getKpiServiceConfiguration() != null){
			String datasourceName = aaaServerConfigData.getKpiServiceConfiguration().getDsName();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(datasourceName)){
				aaaServerConfigData.getKpiServiceConfiguration().setDsName("");
			}
		}
		
		String aaaServerConfigSerializeData = null ;
		try {
			aaaServerConfigSerializeData = ConfigUtil.serialize(AAAServerConfigurationData.class, aaaServerConfigData);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update EliteAAA Server Configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(aaaServerConfigSerializeData.getBytes(),netServerInstanceData.getNetServerId(), ELITE_AAA_SERVER_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("EliteAAA Server Configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateEliteAAAServerConfigurationByPathParam(@Valid AAAServerConfigurationData aaaServerConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, JAXBException{
		return updateEliteAAAServerConfigurationByQueryParam(aaaServerConfigData,serverInstanceName);
	}

	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_ELITEAAA_CONFIGURATION);
	}
}
