package com.elitecore.elitesm.ws.rest.digestconfiguration;

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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class DigestConfigurationController {

	private static final String DIGEST_CONFIGURATION = "Digest Configuration";
	private static final String MODULE = ConfigConstant.DIGEST_CONFIGURATION;
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String digestConfuguration) throws DataManagerException {

		DigestConfBLManager digestConfBLManager =new DigestConfBLManager();

		DigestConfigInstanceData digestConfigInstanceData = digestConfBLManager.getDigestConfigDataByName(digestConfuguration);
		
		return Response.ok(digestConfigInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String digestConfuguration) throws DataManagerException {
		return getByNameFromQuery(digestConfuguration);
	}
	
	@POST
	public Response create(@Valid DigestConfigInstanceData digestConfigurationData) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DigestConfBLManager digestConfBLManager =new DigestConfBLManager();
			
		digestConfigurationData.setCreatedbyStaffid(staffData.getStaffId());
		digestConfigurationData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			
		LogManager.getLogger().info(MODULE,"Digest Configuration created successfully");
		
		digestConfBLManager.create(digestConfigurationData,staffData);
		
		return Response.ok(RestUtitlity.getResponse("Digest Configuration created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DigestConfigInstanceData> digestConfigurationList, @Context UriInfo uri) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DigestConfBLManager digestConfBLManager =new DigestConfBLManager();
		List<DigestConfigInstanceData> digestConfigList = new ArrayList<DigestConfigInstanceData>();
		for (DigestConfigInstanceData digestConfigInstanceData : digestConfigurationList.getList()) {
			
			digestConfigInstanceData.setCreatedbyStaffid(staffData.getStaffId());
			digestConfigInstanceData.setLastModifiedbyStaffid(staffData.getStaffId());
			digestConfigInstanceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			digestConfigInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			
			digestConfigList.add(digestConfigInstanceData);
		}
		LogManager.getLogger().info(MODULE,"Digest Configuration(s) created successfully");
		
		Map<String, List<Status>> responseMap = digestConfBLManager.create(digestConfigList,staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIGEST_CONFIGURATION, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DigestConfigInstanceData digestConfigInstanceData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String byName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DigestConfBLManager digestConfBLManager =new DigestConfBLManager();
		digestConfigInstanceData.setCreatedbyStaffid(staffData.getStaffId());
		digestConfigInstanceData.setLastModifiedbyStaffid(staffData.getStaffId());
		digestConfigInstanceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		digestConfigInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));

		digestConfBLManager.updateDigestConfigurationByName(digestConfigInstanceData,staffData,byName);

		LogManager.getLogger().info(MODULE, digestConfigInstanceData.toString());
		return Response.ok(RestUtitlity.getResponse("Digest Configuration updated successfully")).build();
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid DigestConfigInstanceData digestConfigInstanceData,@PathParam(value="name") String byName) throws DataManagerException {
	return updateByQueryParam(digestConfigInstanceData, byName);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String digestConfuguration) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DigestConfBLManager digestConfBLManager =new DigestConfBLManager();
		
		List<String> digestConfigurationNames = Arrays.asList(digestConfuguration.split(","));
		
		digestConfBLManager.deleteByName(digestConfigurationNames,staffData);
		return Response.ok(RestUtitlity.getResponse("Digest Configuration(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String digestConfuguration) throws DataManagerException {
		return deleteByQueryParam(digestConfuguration);
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIGEST_CONFIGURATION);
	}
}
