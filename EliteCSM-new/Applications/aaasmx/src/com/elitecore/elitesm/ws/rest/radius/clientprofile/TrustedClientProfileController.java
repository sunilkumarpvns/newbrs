package com.elitecore.elitesm.ws.rest.radius.clientprofile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ProfileSuppVendorRelData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class TrustedClientProfileController {

	private static final String TRUSTED_CLIENT = "Trusted Client";
	private static final String MODULE = ConfigConstant.TRUSTED_CLIENT_PROFILE;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String clientProfileName)
			throws DataManagerException {

		ClientProfileBLManager blManager = new ClientProfileBLManager();
		RadiusClientProfileData clientProfileData = (RadiusClientProfileData) blManager.getClientProfileDataByName(clientProfileName.trim());
		return Response.ok(clientProfileData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPathParam(@PathParam(value = "name") String clientProfileName)
			throws DataManagerException {
		return getByNameFromQuery(clientProfileName);
	}

	@POST
	public Response createTrustedClient(@Valid RadiusClientProfileData clientProfileData) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		clientProfileData.setCreatedByStaffId(staffData.getStaffId());
		clientProfileData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		clientProfileData.setLastModifiedByStaffId(staffData.getStaffId());
		clientProfileData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		clientProfileData.setSystemgenerated("N");
		
		List<ProfileSuppVendorRelData> supportedVendorList = new ArrayList<ProfileSuppVendorRelData>();
		if(Collectionz.isNullOrEmpty(clientProfileData.getSupportedVendorLst())== false){
			List<VendorData> vendorDatas = clientProfileData.getSupportedVendorLst();
			
			for (VendorData vendorData : vendorDatas) {
				ProfileSuppVendorRelData data = new ProfileSuppVendorRelData();
				if(vendorData != null){
					data.setVendorInstanceId(clientProfileBLManager.getVendorIdFromName(vendorData.getVendorName()));
					supportedVendorList.add(data);
				}
			}
			
			clientProfileData.setSupportedVendorList(supportedVendorList);
		}
		clientProfileBLManager.create(clientProfileData, staffData);
		return Response.ok(RestUtitlity.getResponse("Trusted Client Profile created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response createTrustedClient(@Valid ListWrapper<RadiusClientProfileData> clientProfileLst, @Context UriInfo uri) throws DataManagerException {
		
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		List<RadiusClientProfileData> clientProfileDatas = clientProfileLst.getList();
		
		for (RadiusClientProfileData clientProfileData : clientProfileDatas) {
			
			clientProfileData.setCreatedByStaffId(staffData.getStaffId());
			clientProfileData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			clientProfileData.setLastModifiedByStaffId(staffData.getStaffId());
			clientProfileData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			clientProfileData.setSystemgenerated("N");
			
			List<ProfileSuppVendorRelData> supportedVendorList = new ArrayList<ProfileSuppVendorRelData>();
			if(Collectionz.isNullOrEmpty(clientProfileData.getSupportedVendorLst())== false){
				List<VendorData> vendorDatas = clientProfileData.getSupportedVendorLst();
				
				for (VendorData vendorData : vendorDatas) {
					ProfileSuppVendorRelData data = new ProfileSuppVendorRelData();
					if(vendorData != null){
						data.setVendorInstanceId(clientProfileBLManager.getVendorIdFromName(vendorData.getVendorName()));
						supportedVendorList.add(data);
					}
				}
				
				clientProfileData.setSupportedVendorList(supportedVendorList);
			}
		}
		Map<String, List<Status>> responseMap = clientProfileBLManager.create(clientProfileDatas, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(TRUSTED_CLIENT, "Profile(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String clientProfileName)
			throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		List<String> clientProfileNames = Arrays.asList(clientProfileName.split(","));
		clientProfileBLManager.deleteByName(clientProfileNames, staffData);
		return Response.ok(RestUtitlity.getResponse("Trusted Client Profile(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String clientProfileName)
			throws DataManagerException {
		return deleteByQueryParam(clientProfileName);
	}

	@PUT
	public Response updateByQueryParam(@Valid RadiusClientProfileData clientProfileData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String clientProfileName)
			throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		List<ProfileSuppVendorRelData> supportedVendorList = new ArrayList<ProfileSuppVendorRelData>();
		if(Collectionz.isNullOrEmpty(clientProfileData.getSupportedVendorLst())== false){
			List<VendorData> vendorDatas = clientProfileData.getSupportedVendorLst();
			for (VendorData vendorData : vendorDatas) {
				ProfileSuppVendorRelData data = new ProfileSuppVendorRelData();
				if(vendorData != null){
					data.setVendorInstanceId(clientProfileBLManager.getVendorIdFromName(vendorData.getVendorName()));
					supportedVendorList.add(data);
				}
			}
			clientProfileData.setSupportedVendorList(supportedVendorList);
		}
		clientProfileBLManager.updateRadiusClientProfileDataByName(clientProfileData, staffData,clientProfileName.trim());
		LogManager.getLogger().info(MODULE, clientProfileData.toString());
		
		return Response.ok(RestUtitlity.getResponse("Trusted Client Profile updated successfully")).build();
	}

	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid RadiusClientProfileData clientProfileData,@PathParam(value = "name") String clientProfileName)
			throws DataManagerException {
		return updateByQueryParam(clientProfileData, clientProfileName);
	}

	@GET
	@Path("/help/")
	public Response getTrustedClientProfileHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.TRUSTED_CLIENT_PROFILE);
	}

}
