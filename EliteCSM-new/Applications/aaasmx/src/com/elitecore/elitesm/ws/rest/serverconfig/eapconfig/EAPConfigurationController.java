package com.elitecore.elitesm.ws.rest.serverconfig.eapconfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPPeapConfiguration;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPSimAkaConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTTLSConfiguration;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.TLSCertificateValidation;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.VendorSpecificCertificateData;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class EAPConfigurationController {
	private static final String EAP_CONFIGURATION = "EAP Configuration";
	
	@GET
	public Response getEapConfigurationByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)@QueryParam(value = "name") String eapConfigName) throws DataManagerException {

		EAPConfigBLManager blManager = new EAPConfigBLManager();
		EAPConfigData eapConfigData = blManager.getEapConfigurationDataByName(eapConfigName);
		String enableAuthMethods = eapConfigData.getEnabledAuthMethods();
		List<String> enableAuthMethodList = Arrays.asList(enableAuthMethods.split(","));
		
		if(enableAuthMethodList.contains(EAPConfigConstant.TLS_STR)||enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)
				|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
			
			EAPTTLSConfiguration eapttlsConfiguration = new EAPTTLSConfiguration();
			EAPPeapConfiguration eapPeapConfiguration = new EAPPeapConfiguration();
			TLSCertificateValidation tlsCertificateValidation = new TLSCertificateValidation();
			EAPTLSConfigData eaptlsConfigData = eapConfigData.getEaptlsConfigData();
			if(eaptlsConfigData != null){
				//certificate validations
				tlsCertificateValidation.setExpiryDate((eaptlsConfigData.getExpiryDate() == null) ?"false":eaptlsConfigData.getExpiryDate());
				tlsCertificateValidation.setMacValidation((eaptlsConfigData.getMacValidation() == null) ?"false":eaptlsConfigData.getMacValidation());
				tlsCertificateValidation.setMissingClientCertificate((eaptlsConfigData.getMissingClientCertificate() == null) ?"false":eaptlsConfigData.getMissingClientCertificate());
				tlsCertificateValidation.setRevokedCertificate((eaptlsConfigData.getRevokedCertificate() == null) ?"false":eaptlsConfigData.getRevokedCertificate());
				eaptlsConfigData.setCertificateValidation(tlsCertificateValidation);
				
				//ttls configuration
				if(enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)){
					eapttlsConfiguration.setEapTtlsCertificateRequest((eapConfigData.getEapTtlsCertificateRequest() == null) ?"false":eapConfigData.getEapTtlsCertificateRequest());
					eapttlsConfiguration.setTtlsNegotiationMethod(eapConfigData.getTtlsNegotiationMethod());
					
					eaptlsConfigData.setTtlsConfiguration(eapttlsConfiguration);
				}
				
				//peap configuration
				if(enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
					eapPeapConfiguration.setEapPeapCertificateRequest((eapConfigData.getEapPeapCertificateRequest() == null) ?"false":eapConfigData.getEapPeapCertificateRequest());
					eapPeapConfiguration.setPeapNegotiationMethod(eapConfigData.getPeapNegotiationMethod());
					eapPeapConfiguration.setPeapVersion(eapConfigData.getPeapVersion());
					
					eaptlsConfigData.setPeapConfiguration(eapPeapConfiguration);;
				}
				
				eapConfigData.setEaptlsConfigData(eaptlsConfigData);
			}
			
			if(enableAuthMethodList.contains(EAPConfigConstant.SIM_STR) && eapConfigData.getSimConfigData() != null){
				if(Strings.isNullOrBlank(eapConfigData.getSimConfigData().getPseudonymGenMethod())){
					eapConfigData.getSimConfigData().setPseudonymGenMethod("-NONE-");
				}
				if(Strings.isNullOrBlank(eapConfigData.getSimConfigData().getFastReAuthGenMethod())){
					eapConfigData.getSimConfigData().setFastReAuthGenMethod("-NONE-");
				}
			}
			
			if(enableAuthMethodList.contains(EAPConfigConstant.AKA_STR) && eapConfigData.getAkaConfigData() != null){
				if(Strings.isNullOrBlank(eapConfigData.getAkaConfigData().getPseudonymGenMethod())){
					eapConfigData.getAkaConfigData().setPseudonymGenMethod("-NONE-");
				}
				if(Strings.isNullOrBlank(eapConfigData.getAkaConfigData().getFastReAuthGenMethod())){
					eapConfigData.getAkaConfigData().setFastReAuthGenMethod("-NONE-");
				}
			}
			
			if(enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR) && eapConfigData.getAkaPrimeConfigData() != null){
				if(Strings.isNullOrBlank(eapConfigData.getAkaPrimeConfigData().getPseudonymGenMethod())){
					eapConfigData.getAkaPrimeConfigData().setPseudonymGenMethod("-NONE-");
				}
				if(Strings.isNullOrBlank(eapConfigData.getAkaPrimeConfigData().getFastReAuthGenMethod())){
					eapConfigData.getAkaPrimeConfigData().setFastReAuthGenMethod("-NONE-");
				}
			}
		}
		
		return Response.ok(eapConfigData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getEapConfigurationByPathParam(@PathParam(value = "name") String eapConfigName) throws DataManagerException {
		return getEapConfigurationByQueryParam(eapConfigName);
	}
	
	@DELETE
	public Response	deleteEapConfigurationByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String eapConfigNames) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPConfigBLManager blManager = new EAPConfigBLManager();
		List<String> eapConfigNameslst = Arrays.asList(eapConfigNames.split(","));
		blManager.deleteByName(eapConfigNameslst, staffData);
		return Response.ok(RestUtitlity.getResponse("EAP Configuration(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String eapConfigNames) throws DataManagerException {
		return deleteEapConfigurationByQueryParam(eapConfigNames);
	}
	
	@POST
	public Response createEAPConfigs(@Valid EAPConfigData eapConfigData) throws DataManagerException{
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPConfigBLManager blManager = new EAPConfigBLManager();
		String enableAuthMethods = eapConfigData.getEnabledAuthMethods();
		eapConfigData.setCreatedByStaffId(staffData.getStaffId());
		eapConfigData.setCreateDate(new Timestamp(new Date().getTime()));
		
		List<String> enableAuthMethodList = Arrays.asList(enableAuthMethods.split(","));
		EAPTLSConfigData tlsConfigData = eapConfigData.getEaptlsConfigData();
		if(tlsConfigData != null){
			
			
			//following check for TLS,TTLS,PEAP any of this selected  than go to next page of TLS configuration
			if(enableAuthMethodList.contains(EAPConfigConstant.TLS_STR)||enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)
					|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
				
				//certificate validation
				TLSCertificateValidation certificateValidation = tlsConfigData.getCertificateValidation();
				
				if(certificateValidation != null){
					
					tlsConfigData.setExpiryDate((certificateValidation.getExpiryDate() == null) ?"false":certificateValidation.getExpiryDate().toLowerCase());
					tlsConfigData.setMacValidation((certificateValidation.getMacValidation() == null) ?"false":certificateValidation.getMacValidation().toLowerCase());
					tlsConfigData.setMissingClientCertificate((certificateValidation.getMissingClientCertificate() == null) ?"false":certificateValidation.getMissingClientCertificate().toLowerCase());
					tlsConfigData.setRevokedCertificate((certificateValidation.getRevokedCertificate() == null) ?"false":certificateValidation.getRevokedCertificate().toLowerCase());
				} 
				
				
				if (enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) == false){
					eapConfigData.setEapTtlsCertificateRequest("false");
				} else{
					EAPTTLSConfiguration eapttlsConfiguration = tlsConfigData.getTtlsConfiguration();
					if(eapttlsConfiguration != null){
						eapConfigData.setEapTtlsCertificateRequest(eapttlsConfiguration.getEapTtlsCertificateRequest());
						eapConfigData.setTtlsNegotiationMethod(eapttlsConfiguration.getTtlsNegotiationMethod());
					}
					
				}
				
				
				if (enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR) == false){
					eapConfigData.setEapPeapCertificateRequest("false");
				} else{
					EAPPeapConfiguration eapPeapConfiguration = tlsConfigData.getPeapConfiguration();
					if(eapPeapConfiguration != null){
						eapConfigData.setPeapVersion(eapPeapConfiguration.getPeapVersion());
						eapConfigData.setPeapNegotiationMethod(eapPeapConfiguration.getPeapNegotiationMethod());
						eapConfigData.setEapPeapCertificateRequest(eapPeapConfiguration.getEapPeapCertificateRequest());
					}
				}
				
				List<VendorSpecificCertificateData> vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateData>();
				List<VendorSpecificCertificateData> vendorSpecificCertificateDatas = tlsConfigData.getVendorSpecificList();
				
				if(Collectionz.isNullOrEmpty(vendorSpecificCertificateDatas) == false){
					for(VendorSpecificCertificateData vendorSpecificCertificateData :vendorSpecificCertificateDatas){
						VendorSpecificCertificateData vendorCertData =new VendorSpecificCertificateData();
						
						vendorCertData.setOui(vendorSpecificCertificateData.getOui());
						if(Strings.isNullOrBlank(vendorSpecificCertificateData.getServerCertificateIdForVSC())){
							vendorCertData.setServerCertificateIdForVSC(null);
						} else{
							vendorCertData.setServerCertificateIdForVSC(vendorSpecificCertificateData.getServerCertificateIdForVSC());
						}
						vendorSpecificCertificateList.add(vendorCertData);
					}
				}
				tlsConfigData.setVendorSpecificList(vendorSpecificCertificateList);
			}
			
		}
	
		if(eapConfigData.getAkaConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_STR)){
			eapConfigData.getAkaConfigData().setEapAuthType(EAPConfigConstant.AKA);
		}
		
		if(eapConfigData.getSimConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.SIM_STR)){
			eapConfigData.getSimConfigData().setEapAuthType(EAPConfigConstant.SIM);
		}
		
		if(eapConfigData.getAkaPrimeConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)){
			eapConfigData.getAkaPrimeConfigData().setEapAuthType(EAPConfigConstant.AKA_PRIME);
		}

		//set default configuration for eap configuration
		setDefaultConfiguration(enableAuthMethodList,eapConfigData,true);
		
		blManager.create(eapConfigData, staffData);
		return Response.ok(RestUtitlity.getResponse("EAP Configuration created successfully")).build();
	}
	
	
	@POST
	@Path("/bulk")
	public Response createEAPConfigs(@Valid ListWrapper<EAPConfigData> eapConfigDatas, @Context UriInfo uri) throws DataManagerException{
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPConfigBLManager blManager = new EAPConfigBLManager();
		List<EAPConfigData> eapConfigDataLst = eapConfigDatas.getList();
		
		for(EAPConfigData eapConfigData : eapConfigDataLst){
			String enableAuthMethods = eapConfigData.getEnabledAuthMethods();
			eapConfigData.setCreatedByStaffId(staffData.getStaffId());
			eapConfigData.setCreateDate(new Timestamp(new Date().getTime()));
			
			List<String> enableAuthMethodList = Arrays.asList(enableAuthMethods.split(","));
			EAPTLSConfigData tlsConfigData = eapConfigData.getEaptlsConfigData();
			if(tlsConfigData != null){
				
				
				//following check for TLS,TTLS,PEAP any of this selected  than go to next page of TLS configuration
				if(enableAuthMethodList.contains(EAPConfigConstant.TLS_STR)||enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)
						|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
					
					//certificate validation
					TLSCertificateValidation certificateValidation = tlsConfigData.getCertificateValidation();
					
					if(certificateValidation != null){
						
						tlsConfigData.setExpiryDate((certificateValidation.getExpiryDate() == null) ?"false":certificateValidation.getExpiryDate().toLowerCase());
						tlsConfigData.setMacValidation((certificateValidation.getMacValidation() == null) ?"false":certificateValidation.getMacValidation().toLowerCase());
						tlsConfigData.setMissingClientCertificate((certificateValidation.getMissingClientCertificate() == null) ?"false":certificateValidation.getMissingClientCertificate().toLowerCase());
						tlsConfigData.setRevokedCertificate((certificateValidation.getRevokedCertificate() == null) ?"false":certificateValidation.getRevokedCertificate().toLowerCase());
					} 
					
					
					if (enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) == false){
						eapConfigData.setEapTtlsCertificateRequest("false");
					} else{
						EAPTTLSConfiguration eapttlsConfiguration = tlsConfigData.getTtlsConfiguration();
						if(eapttlsConfiguration != null){
							eapConfigData.setEapTtlsCertificateRequest(eapttlsConfiguration.getEapTtlsCertificateRequest());
							eapConfigData.setTtlsNegotiationMethod(eapttlsConfiguration.getTtlsNegotiationMethod());
						}
						
					}
					
					
					if (enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR) == false){
						eapConfigData.setEapPeapCertificateRequest("false");
					} else{
						EAPPeapConfiguration eapPeapConfiguration = tlsConfigData.getPeapConfiguration();
						if(eapPeapConfiguration != null){
							eapConfigData.setPeapVersion(eapPeapConfiguration.getPeapVersion());
							eapConfigData.setPeapNegotiationMethod(eapPeapConfiguration.getPeapNegotiationMethod());
							eapConfigData.setEapPeapCertificateRequest(eapPeapConfiguration.getEapPeapCertificateRequest());
						}
					}
					
					List<VendorSpecificCertificateData> vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateData>();
					List<VendorSpecificCertificateData> vendorSpecificCertificateDatas = tlsConfigData.getVendorSpecificList();
					
					if(Collectionz.isNullOrEmpty(vendorSpecificCertificateDatas) == false){
						for(VendorSpecificCertificateData vendorSpecificCertificateData :vendorSpecificCertificateDatas){
							VendorSpecificCertificateData vendorCertData =new VendorSpecificCertificateData();
							
							vendorCertData.setOui(vendorSpecificCertificateData.getOui());
							if(Strings.isNullOrBlank(vendorSpecificCertificateData.getServerCertificateIdForVSC())){
								vendorCertData.setServerCertificateIdForVSC(null);
							} else{
								vendorCertData.setServerCertificateIdForVSC(vendorSpecificCertificateData.getServerCertificateIdForVSC());
							}
							vendorSpecificCertificateList.add(vendorCertData);
						}
					}
					tlsConfigData.setVendorSpecificList(vendorSpecificCertificateList);
				}
				
			}
		
			if(eapConfigData.getAkaConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_STR)){
				eapConfigData.getAkaConfigData().setEapAuthType(EAPConfigConstant.AKA);
			}
			
			if(eapConfigData.getSimConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.SIM_STR)){
				eapConfigData.getSimConfigData().setEapAuthType(EAPConfigConstant.SIM);
			}
			
			if(eapConfigData.getAkaPrimeConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)){
				eapConfigData.getAkaPrimeConfigData().setEapAuthType(EAPConfigConstant.AKA_PRIME);
			}

			//set default configuration for eap configuration
			setDefaultConfiguration(enableAuthMethodList,eapConfigData,true);
		}
				
		Map<String, List<Status>> responseMap = blManager.create(eapConfigDataLst, staffData, URLInfo.isPartialSuccess(uri));
		return Response.ok(RestUtitlity.getResponse(EAP_CONFIGURATION,"(s) created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid EAPConfigData eapConfigData,@PathParam(value="name")String eapConfigName)
			throws DataManagerException{
		return updateByQueryParam(eapConfigData,eapConfigName);
	}
	
	
	@PUT
	public Response updateByQueryParam(@Valid EAPConfigData eapConfigData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String eapConfigName)
			throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPConfigBLManager blManager = new EAPConfigBLManager();
		
		String enableAuthMethods = eapConfigData.getEnabledAuthMethods();
		eapConfigData.setCreatedByStaffId(staffData.getStaffId());
		eapConfigData.setCreateDate(new Timestamp(new Date().getTime()));
		
		List<String> enableAuthMethodList = Arrays.asList(enableAuthMethods.split(","));
		EAPTLSConfigData tlsConfigData = eapConfigData.getEaptlsConfigData();
		if(tlsConfigData != null){
			
			//following check for TLS,TTLS,PEAP any of this selected  than go to next page of TLS configuration
			if(enableAuthMethodList.contains(EAPConfigConstant.TLS_STR) || enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)
					|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){

				//certificate validation
				TLSCertificateValidation certificateValidation = tlsConfigData.getCertificateValidation();
				if(certificateValidation != null){
					
					tlsConfigData.setExpiryDate((certificateValidation.getExpiryDate() == null) ?"false":certificateValidation.getExpiryDate().toLowerCase());
					tlsConfigData.setMacValidation((certificateValidation.getMacValidation() == null) ?"false":certificateValidation.getMacValidation().toLowerCase());
					tlsConfigData.setMissingClientCertificate((certificateValidation.getMissingClientCertificate() == null) ?"false":certificateValidation.getMissingClientCertificate().toLowerCase());
					tlsConfigData.setRevokedCertificate((certificateValidation.getRevokedCertificate() == null) ?"false":certificateValidation.getRevokedCertificate().toLowerCase());
				}
				
				if (enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) == false){
					eapConfigData.setEapTtlsCertificateRequest("false");
				} else{
					EAPTTLSConfiguration eapttlsConfiguration = tlsConfigData.getTtlsConfiguration();
					if(eapttlsConfiguration != null){
						eapConfigData.setEapTtlsCertificateRequest(eapttlsConfiguration.getEapTtlsCertificateRequest());
						eapConfigData.setTtlsNegotiationMethod(eapttlsConfiguration.getTtlsNegotiationMethod());
					}
					
				}
				
				
				if (enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR) == false){
					eapConfigData.setEapPeapCertificateRequest("false");
				} else{
					EAPPeapConfiguration eapPeapConfiguration = tlsConfigData.getPeapConfiguration();
					if(eapPeapConfiguration != null){
						eapConfigData.setPeapVersion(eapPeapConfiguration.getPeapVersion());
						eapConfigData.setPeapNegotiationMethod(eapPeapConfiguration.getPeapNegotiationMethod());
						eapConfigData.setEapPeapCertificateRequest(eapPeapConfiguration.getEapPeapCertificateRequest());
					}
				}
				
				List<VendorSpecificCertificateData> vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateData>();
				List<VendorSpecificCertificateData> vendorSpecificCertificateDatas = tlsConfigData.getVendorSpecificList();
				
				if(Collectionz.isNullOrEmpty(vendorSpecificCertificateDatas) == false){
					for(VendorSpecificCertificateData vendorSpecificCertificateData :vendorSpecificCertificateDatas){
						VendorSpecificCertificateData vendorCertData =new VendorSpecificCertificateData();
						
						vendorCertData.setOui(vendorSpecificCertificateData.getOui());
						if(Strings.isNullOrBlank(vendorSpecificCertificateData.getServerCertificateIdForVSC())){
							vendorCertData.setServerCertificateIdForVSC(null);
						} else{
							vendorCertData.setServerCertificateIdForVSC(vendorSpecificCertificateData.getServerCertificateIdForVSC());
						}
						vendorSpecificCertificateList.add(vendorCertData);
					}
					tlsConfigData.setVendorSpecificList(vendorSpecificCertificateList);
				}
			}
			
		}
	
		if(eapConfigData.getAkaConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_STR)){
			eapConfigData.getAkaConfigData().setEapAuthType(EAPConfigConstant.AKA);
		}
		
		if(eapConfigData.getSimConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.SIM_STR)){
			eapConfigData.getSimConfigData().setEapAuthType(EAPConfigConstant.SIM);
		}
		
		if(eapConfigData.getAkaPrimeConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)){
			eapConfigData.getAkaPrimeConfigData().setEapAuthType(EAPConfigConstant.AKA_PRIME);
		}

		//set Default Configurations
		setDefaultConfiguration(enableAuthMethodList,eapConfigData,false);	
		blManager.updateEapConfigByName(eapConfigData, staffData, eapConfigName);
		return Response.ok(RestUtitlity.getResponse("EAP Configuration updated successfully")).build();
	}
	
	@GET
	@Path("/help/")
	public Response getEapConfigurationHelpContent() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.EAP_CONFIGURATION);
	}
	
	private void setDefaultConfiguration(List<String> enableAuthMethodList,
			EAPConfigData eapConfigData,Boolean isCreate) {
		// set default configuration
		if ((enableAuthMethodList.contains(EAPConfigConstant.TLS_STR)
				|| enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) || 
				enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR))
				&& eapConfigData.getEaptlsConfigData() == null) {
			EAPTLSConfigData defaultTLSConfigData = new EAPTLSConfigData();
			defaultTLSConfigData.setMinTlsVersion(EAPConfigConstant.TLSv_1);
			defaultTLSConfigData.setMaxTlsVersion(EAPConfigConstant.TLSv_1_2);
			defaultTLSConfigData.setServerCertificateId(null);
			defaultTLSConfigData.setCertificateRequest("false");
			defaultTLSConfigData.setDefaultCompressionMethod(null);
			defaultTLSConfigData.setSessionResumptionDuration(5000L);
			defaultTLSConfigData.setSessionResumptionLimit(2L);
			
			if(isCreate){
				//in create we provide this default configuration
				defaultTLSConfigData.setCertificateTypesList("1");
				//dynamic cipher suite
				ProtocolVersion minTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1);
				ProtocolVersion maxTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1_2);
				
				Set<CipherSuites> cipherSuiteSet = new LinkedHashSet<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTLSVer,maxTLSVer));
				
				StringBuilder cipherSuitesValue = new StringBuilder();
				
				for(CipherSuites cipherSuite : cipherSuiteSet){
					cipherSuitesValue.append(","+cipherSuite.code);
				}
				
				String cipherSuiteFinalValue = cipherSuitesValue.substring(1);
				defaultTLSConfigData.setCiphersuiteList(cipherSuiteFinalValue);
				defaultTLSConfigData.setExpiryDate("true");
				defaultTLSConfigData.setMacValidation("true");
				defaultTLSConfigData.setMissingClientCertificate("true");
				defaultTLSConfigData.setRevokedCertificate("true");
			}else{
				//in update this default configuration we provide
				defaultTLSConfigData.setCertificateTypesList("1");
				defaultTLSConfigData.setCiphersuiteList("10");
			}
			
			//if TTLs selected than add its configuration
			if (enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) == false){
				eapConfigData.setEapTtlsCertificateRequest("false");
			} else{
				eapConfigData.setEapTtlsCertificateRequest("false");
				eapConfigData.setTtlsNegotiationMethod(26);
			}
			
			
			if (enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR) == false){
				eapConfigData.setEapPeapCertificateRequest("false");
			} else{
				eapConfigData.setPeapVersion("0");
				eapConfigData.setPeapNegotiationMethod(26);
				eapConfigData.setEapPeapCertificateRequest("false");
			}
			
			eapConfigData.setEaptlsConfigData(defaultTLSConfigData);
		}

		// default SIM Configuration
		if (enableAuthMethodList.contains(EAPConfigConstant.SIM_STR)
				&& eapConfigData.getSimConfigData() == null) {
			EAPSimAkaConfigData eapSimConfigData = new EAPSimAkaConfigData();
			eapSimConfigData.setPseudonymGenMethod("BASE32");
			eapSimConfigData.setPseudonymHexenCoding("ENABLE");
			eapSimConfigData.setPseudonymPrefix("1999");
			eapSimConfigData.setPseudonymRootNAI("DISABLE");
			eapSimConfigData.setFastReAuthGenMethod("BASE32");
			eapSimConfigData.setFastReAuthHexenCoding("ENABLE");
			eapSimConfigData.setFastReAuthPrefix("1888");
			eapSimConfigData.setFastReAuthRootNAI("DISABLE");
			eapSimConfigData.setEapAuthType(EAPConfigConstant.SIM);

			eapConfigData.setSimConfigData(eapSimConfigData);
		}

		// default aka Configuaration
		if (enableAuthMethodList.contains(EAPConfigConstant.AKA_STR)
				&& eapConfigData.getAkaConfigData() == null) {
			EAPSimAkaConfigData eapAkaConfigData = new EAPSimAkaConfigData();
			eapAkaConfigData.setPseudonymGenMethod("BASE32");
			eapAkaConfigData.setPseudonymHexenCoding("ENABLE");
			eapAkaConfigData.setPseudonymPrefix("0999");
			eapAkaConfigData.setPseudonymRootNAI("DISABLE");
			eapAkaConfigData.setFastReAuthGenMethod("BASE32");
			eapAkaConfigData.setFastReAuthHexenCoding("ENABLE");
			eapAkaConfigData.setFastReAuthPrefix("0888");
			eapAkaConfigData.setFastReAuthRootNAI("DISABLE");
			eapAkaConfigData.setEapAuthType(EAPConfigConstant.AKA);

			eapConfigData.setAkaConfigData(eapAkaConfigData);
		}

		// default aka prime Configuaration
		if (enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)
				&& eapConfigData.getAkaPrimeConfigData() == null) {
			EAPSimAkaConfigData eapAkaPrimeConfigData = new EAPSimAkaConfigData();
			eapAkaPrimeConfigData.setPseudonymGenMethod("BASE32");
			eapAkaPrimeConfigData.setPseudonymHexenCoding("ENABLE");
			eapAkaPrimeConfigData.setPseudonymPrefix("6999");
			eapAkaPrimeConfigData.setPseudonymRootNAI("DISABLE");
			eapAkaPrimeConfigData.setFastReAuthGenMethod("BASE32");
			eapAkaPrimeConfigData.setFastReAuthHexenCoding("ENABLE");
			eapAkaPrimeConfigData.setFastReAuthPrefix("6888");
			eapAkaPrimeConfigData.setFastReAuthRootNAI("DISABLE");
			eapAkaPrimeConfigData.setEapAuthType(EAPConfigConstant.AKA_PRIME);

			eapConfigData.setAkaPrimeConfigData(eapAkaPrimeConfigData);
		}

	}
}
