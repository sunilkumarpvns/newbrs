package com.elitecore.elitesm.ws.rest.diameterpeerprofiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.CertificateValidationCheckBox;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterPeerProfilesController {

	private static final String DIAMETER_PEER_PROFILE = "Diameter Peer Profile";
	private static final String MODULE = ConfigConstant.DIAMETER_PEER_PROFILE;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerProfileName) throws DataManagerException {

		DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
		DiameterPeerProfileData diameterPeerProfile = diameterPeerProfileBLManager.getDiameterPeerProfileByName(diameterPeerProfileName);
		
		CertificateValidationCheckBox certificateValidationCheckBox = new CertificateValidationCheckBox();
		
		certificateValidationCheckBox.setExpiryDate(diameterPeerProfile.getValidateCertificateExpiry());
		certificateValidationCheckBox.setUnknownCA(diameterPeerProfile.getAllowCertificateCA());
		certificateValidationCheckBox.setRevokedCertificate(diameterPeerProfile.getValidateCertificateRevocation());
		certificateValidationCheckBox.setUnknownHost(diameterPeerProfile.getValidateHost());
		
		diameterPeerProfile.setCertificateValidationCheckBox(certificateValidationCheckBox);
		
		String serverCertificateId = diameterPeerProfile.getServerCertificateId();
		
		if (serverCertificateId == null) {
			diameterPeerProfile.setServerCertificateName("NONE");
		} else {
			EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			try {
				diameterPeerProfile.setServerCertificateName(eapConfigBLManager.getServerCertificateNameFromId(serverCertificateId));
			} catch (DataManagerException e) {
				e.printStackTrace();
				diameterPeerProfile.setServerCertificateName("NONE");
			}
		}

		return Response.ok(diameterPeerProfile).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterPeerProfileName) throws DataManagerException {
		return getByNameFromQuery(diameterPeerProfileName);
	}

	@POST
	public Response create(@Valid DiameterPeerProfileData diameterPeerProfile) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
		
		diameterPeerProfile.setCreateDate(new Timestamp(new Date().getTime()));
		diameterPeerProfile.setCreatedByStaffId(staffData.getStaffId());
		diameterPeerProfile.setLastModifiedDate(new Timestamp(new Date().getTime()));
		diameterPeerProfile.setLastModifiedByStaffId(staffData.getStaffId());
		
		CertificateValidationCheckBox certificateValidationCheckBox = diameterPeerProfile.getCertificateValidationCheckBox();
		
		if (certificateValidationCheckBox != null) {
			
			diameterPeerProfile.setValidateCertificateExpiry(certificateValidationCheckBox.getExpiryDate());
			diameterPeerProfile.setAllowCertificateCA(certificateValidationCheckBox.getUnknownCA());
			diameterPeerProfile.setValidateCertificateRevocation(certificateValidationCheckBox.getRevokedCertificate());
			diameterPeerProfile.setValidateHost(certificateValidationCheckBox.getUnknownHost());
			
		}
			
		diameterPeerProfileBLManager.createDiameterPeerProfile(diameterPeerProfile, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Peer Profile created successfully")).build();

	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterPeerProfileData> diameterPeerProfileDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
		
		List<DiameterPeerProfileData> diameterPeerProfiles = diameterPeerProfileDatas.getList();
		
		for (DiameterPeerProfileData diameterPeerProfile : diameterPeerProfiles) {
			
			diameterPeerProfile.setCreateDate(new Timestamp(new Date().getTime()));
			diameterPeerProfile.setCreatedByStaffId(staffData.getStaffId());
			diameterPeerProfile.setLastModifiedDate(new Timestamp(new Date().getTime()));
			diameterPeerProfile.setLastModifiedByStaffId(staffData.getStaffId());
			
			CertificateValidationCheckBox certificateValidationCheckBox = diameterPeerProfile.getCertificateValidationCheckBox();
			
			if (certificateValidationCheckBox != null) {
				
				diameterPeerProfile.setValidateCertificateExpiry(certificateValidationCheckBox.getExpiryDate());
				diameterPeerProfile.setAllowCertificateCA(certificateValidationCheckBox.getUnknownCA());
				diameterPeerProfile.setValidateCertificateRevocation(certificateValidationCheckBox.getRevokedCertificate());
				diameterPeerProfile.setValidateHost(certificateValidationCheckBox.getUnknownHost());
				
			}
			
		}

		Map<String, List<Status>> responseMap = diameterPeerProfileBLManager.createDiameterPeerProfile(diameterPeerProfiles, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_PEER_PROFILE, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@PUT
	public Response updateByQueryParam(@Valid DiameterPeerProfileData diameterPeerProfile, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerProfileName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
		
		diameterPeerProfile.setLastModifiedDate(new Timestamp(new Date().getTime()));
		diameterPeerProfile.setLastModifiedByStaffId(staffData.getStaffId());
		
		CertificateValidationCheckBox certificateValidationCheckBox = diameterPeerProfile.getCertificateValidationCheckBox();
		
		if (certificateValidationCheckBox != null) {
			
			diameterPeerProfile.setValidateCertificateExpiry(certificateValidationCheckBox.getExpiryDate());
			diameterPeerProfile.setAllowCertificateCA(certificateValidationCheckBox.getUnknownCA());
			diameterPeerProfile.setValidateCertificateRevocation(certificateValidationCheckBox.getRevokedCertificate());
			diameterPeerProfile.setValidateHost(certificateValidationCheckBox.getUnknownHost());
			
		}
			
		diameterPeerProfileBLManager.updateDiameterPeerProfileByName(diameterPeerProfile, staffData, diameterPeerProfileName);

		LogManager.getLogger().info(MODULE, diameterPeerProfile.toString());

		return Response.ok(RestUtitlity.getResponse("Diameter Peer Profile updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DiameterPeerProfileData diameterPeerProfile, @PathParam(value = "name") String diameterPeerProfileName) throws DataManagerException {
		return updateByQueryParam(diameterPeerProfile, diameterPeerProfileName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerProfileNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();

		List<String> listDiameterPeerProfileName = Arrays.asList(diameterPeerProfileNames.split(","));

		diameterPeerProfileBLManager.deleteDiameterPeerProfileByName(listDiameterPeerProfileName, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Peer Profile(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String diameterPeerProfileNames) throws DataManagerException {
		return deleteByQueryParam(diameterPeerProfileNames);
	}

	@GET
	@Path("/help")
	public Response getDiameterPeerProfilesHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_PEER_PROFILES);
	}
	

}
