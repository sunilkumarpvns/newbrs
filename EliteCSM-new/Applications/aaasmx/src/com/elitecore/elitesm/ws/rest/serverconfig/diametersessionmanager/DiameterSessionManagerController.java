package com.elitecore.elitesm.ws.rest.serverconfig.diametersessionmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterSessionManagerController {

	private static final String DIAMETER_SESSION_MANAGERS = "Diameter Session Managers";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterSessionManagerName) throws DataManagerException {
		DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
		return Response.ok(diameterSessionManagerBLManager.getDiameterSessionManagerDataByName(diameterSessionManagerName)).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterSessionManagerName) throws DataManagerException {
		return getByNameFromQuery(diameterSessionManagerName);
	}
	
	@POST
	public Response create(@Valid DiameterSessionManagerData diameterSessionManagerData) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
		
		diameterSessionManagerBLManager.create(diameterSessionManagerData, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Session Manager created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterSessionManagerData> diameterSessionManagerDataList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
		
		Map<String, List<Status>> responseMap = diameterSessionManagerBLManager.create(diameterSessionManagerDataList.getList(), staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_SESSION_MANAGERS, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DiameterSessionManagerData diameterSessionManagerData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String byName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
		
		diameterSessionManagerBLManager.updataDiameterSessionManager(diameterSessionManagerData, staffData, byName);
		return Response.ok(RestUtitlity.getResponse("Diameter Session Manager updated successfully")).build();
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid DiameterSessionManagerData diameterSessionManagerData,@PathParam(value="name") String byName) throws DataManagerException {
		return updateByQueryParam(diameterSessionManagerData, byName);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterSessionManagerData) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
		
		List<String> diameterSessionManagerNames = Arrays.asList(diameterSessionManagerData.split(","));
		
		diameterSessionManagerBLManager.deleteDiameterSessionManagerByName(diameterSessionManagerNames,staffData);
		return Response.ok(RestUtitlity.getResponse("Diameter Session Managers deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String sessionManagerName) throws DataManagerException {
		return deleteByQueryParam(sessionManagerName);
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_SESSION_MANAGER);
	}
}
