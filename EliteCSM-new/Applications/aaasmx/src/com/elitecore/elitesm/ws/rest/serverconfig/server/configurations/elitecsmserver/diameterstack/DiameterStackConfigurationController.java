package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data.DiameterStackConfigurationData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class DiameterStackConfigurationController {
	
	public final String DIAMETER_STACK_CONFIGURATION = "Diameter Stack" ;
	
	public DiameterSessionManagerBLManager  diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
	public EAPConfigBLManager eapBLManager = new EAPConfigBLManager();
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to retrive Diameter stack configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		String serverInstanceId = netServerInstanceData.getNetServerId();
		
		INetConfigurationInstanceData diameterStackConfigurationInstanceData = netServerBLManager.getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,DIAMETER_STACK_CONFIGURATION);
		
		INetConfigurationData diameterStackConfigurationData = diameterStackConfigurationInstanceData.getNetConfiguration();
		String configInstanceIdStr = diameterStackConfigurationData.getNetConfigId();
		
		byte[] diameterStackConfigurationStream = netServerBLManager.getServerConfigurationStream(serverInstanceId,configInstanceIdStr);
		
		String diameterStackConfigDataStr = new String(diameterStackConfigurationStream);
		StringReader diameterStackDataStrReader = new StringReader(diameterStackConfigDataStr.trim());
		
		DiameterStackConfigurationData diameterStackConfigData  = null;
		
		try {
			diameterStackConfigData=ConfigUtil.deserialize(diameterStackDataStrReader, DiameterStackConfigurationData.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Stack configuration, Reason: "+e.getMessage(),e);
		}
		
		String sessionMangerId  = diameterStackConfigData.getSessionManagerId();
		if(Strings.isNullOrBlank(sessionMangerId) == false){
			
			DiameterSessionManagerData sessionMnagerData = diameterSessionManagerBLManager.getDiameterSessionManagerDataById(sessionMangerId);
			if(sessionMnagerData != null){
				diameterStackConfigData.setSessionManagerId(sessionMnagerData.getName());
			}else{
				diameterStackConfigData.setSessionManagerId(RestValidationMessages.NONE_WITH_HYPHEN);
			}
			
		}else{
			diameterStackConfigData.setSessionManagerId(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		if(Strings.isNullOrBlank(diameterStackConfigData.getRoutingTableName())){
			diameterStackConfigData.setRoutingTableName(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		if(Strings.isNullOrBlank(diameterStackConfigData.getSecurityStandard())){
			diameterStackConfigData.setSecurityStandard(RestValidationMessages.NONE);
		}
		
		String rollingTypeId = diameterStackConfigData.getServiceLoggerDetail().getRollingType();
		String rollingTypeName ="";
		if("1".equals(rollingTypeId)){
			rollingTypeName = RestValidationMessages.TIME_BASED;
		}else if("2".equals(rollingTypeId)){
			rollingTypeName = RestValidationMessages.SIZE_BASED;
		}
		diameterStackConfigData.getServiceLoggerDetail().setRollingType(rollingTypeName);
		
		String certificateId = diameterStackConfigData.getSecurityParameters().getServerCertificateId();
		String cerfificateName = "";
		if(Strings.isNullOrBlank(certificateId) == false){
			cerfificateName = eapBLManager.getServerCertificateNameFromId(certificateId);
			if(Strings.isNullOrBlank(cerfificateName)){
				cerfificateName = RestValidationMessages.NONE;
			}
			diameterStackConfigData.getSecurityParameters().setServerCertificateId(cerfificateName);
		}else{
			diameterStackConfigData.getSecurityParameters().setServerCertificateId(RestValidationMessages.NONE);
		}
		
		String cipherSuitesIds = diameterStackConfigData.getSecurityParameters().getEnabledCiphersuites();
		String cipherSuiteNames = "";
		if(Strings.isNullOrBlank(cipherSuitesIds) == false){
			cipherSuiteNames = EAPConfigUtils.convertCipherSuiteCodeToCipherSuiteName(cipherSuitesIds);
		}
		diameterStackConfigData.getSecurityParameters().setEnabledCiphersuites(cipherSuiteNames);
		
		String facilityStr = diameterStackConfigData.getServiceLoggerDetail().getSysLogConfiguration().getFacility();
		if(Strings.isNullOrBlank(facilityStr)){
			diameterStackConfigData.getServiceLoggerDetail().getSysLogConfiguration().setFacility(RestValidationMessages.NONE_WITH_HYPHEN);
		}
		
		return Response.ok(diameterStackConfigData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException, JAXBException {
		return getByNameFromQuery(name);
	}
	
	@PUT
	public Response updateDiameterStackConfigurationByQueryParam(@Valid DiameterStackConfigurationData diameterStackConfigData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, JAXBException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData;
		
		try{
			netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		}catch(DataManagerException dbe){
			return Response.ok(RestUtitlity.getResponse("Failed to update "+DIAMETER_STACK_CONFIGURATION+" configuration, Reason: Server Instance not found",ResultCode.NOT_FOUND)).build();
		}
		
		if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
			return Response.ok(RestUtitlity.getResponse("You are not allow to update diameter stack configuration, Reason : Entered server name's type is Resource Manager", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		if(RestValidationMessages.NONE.equalsIgnoreCase(diameterStackConfigData.getSessionManagerId())){
			diameterStackConfigData.setSessionManagerId("");
		}
		
		if (RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(diameterStackConfigData.getRoutingTableName())) {
			diameterStackConfigData.setRoutingTableName("");
		}
		
		String sessionManagerName = diameterStackConfigData.getSessionManagerId();
		String sessionManagerId = "";
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(sessionManagerName.trim()) == false){
			try{
				DiameterSessionManagerData sessionManagerData = diameterSessionManagerBLManager.getDiameterSessionManagerDataByName(sessionManagerName);
				sessionManagerId = String.valueOf(sessionManagerData.getSessionManagerId());
			}catch(Exception e){
				sessionManagerId = "";
		    }
			diameterStackConfigData.setSessionManagerId(sessionManagerId);
		}else{
			diameterStackConfigData.setSessionManagerId("");
		}
		
		if(diameterStackConfigData.getSecurityParameters() != null){
			String certificateName = diameterStackConfigData.getSecurityParameters().getServerCertificateId();
			if(RestValidationMessages.NONE.equalsIgnoreCase(certificateName) == false){
				String cerfificateId;
				try{
					cerfificateId = eapBLManager.getServerCertificateIdFromName(certificateName);
					diameterStackConfigData.getSecurityParameters().setServerCertificateId(cerfificateId);;
				}catch(Exception e){
					cerfificateId = "";
				}
			}else{
				diameterStackConfigData.getSecurityParameters().setServerCertificateId("");;
			}
			
			String enabledCiphersuites = diameterStackConfigData.getSecurityParameters().getEnabledCiphersuites();
			String cipherSuiteIds = "";
			if(Strings.isNullOrBlank(enabledCiphersuites) == false){
				cipherSuiteIds = EAPConfigUtils.convertCipherSuitesNamesToCipherSuiteCodes(enabledCiphersuites);
			}
			diameterStackConfigData.getSecurityParameters().setEnabledCiphersuites(cipherSuiteIds);
		}
		
		if(diameterStackConfigData.getServiceLoggerDetail() != null){
			if(diameterStackConfigData.getServiceLoggerDetail().getSysLogConfiguration() != null){
				String facility = diameterStackConfigData.getServiceLoggerDetail().getSysLogConfiguration().getFacility();
				if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facility)){
					diameterStackConfigData.getServiceLoggerDetail().getSysLogConfiguration().setFacility("");
				}
			}
		}
		
		if(diameterStackConfigData.getDiameterWebServiceConfigurationDetail() != null){
			String reAuthTransalationMappingName = diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getReAuthTranslationMappingDetail().getTranslationMapping();
			String abortSessionMappingName = diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getAbortSessionTranslationMappingDetail().getTranslationMapping();
			String genericRequestMappingName = diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getGenericTranslationMappingDetail().getTranslationMapping();
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(reAuthTransalationMappingName)){
				diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getReAuthTranslationMappingDetail().setTranslationMapping("");
			}
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(abortSessionMappingName)){
				diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getAbortSessionTranslationMappingDetail().setTranslationMapping("");
			}
			if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(genericRequestMappingName)){
				diameterStackConfigData.getDiameterWebServiceConfigurationDetail().getGenericTranslationMappingDetail().setTranslationMapping("");
			}
			
		}
				
		String diameterStackConfigSerializeData = null ;
		try {
			diameterStackConfigSerializeData = ConfigUtil.serialize(DiameterStackConfigurationData.class, diameterStackConfigData);
			
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Stack configuration, Reason: "+e.getMessage(),e);
		}
		
		netServerBLManager.updateConfiguration(diameterStackConfigSerializeData.getBytes(), netServerInstanceData.getNetServerId(), DIAMETER_STACK_CONFIGURATION);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Stack configuration updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateDiameterStackConfigurationByPathParam(@Valid DiameterStackConfigurationData diameterStackConfigData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, JAXBException{
		return updateDiameterStackConfigurationByQueryParam(diameterStackConfigData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getDiameterStackConfigurationHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECSMSERVER_DIAMETER_STACK_CONFIGURATION);
	}
}
