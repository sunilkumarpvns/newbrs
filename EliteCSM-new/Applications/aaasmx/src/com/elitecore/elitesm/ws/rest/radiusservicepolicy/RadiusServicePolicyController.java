package com.elitecore.elitesm.ws.rest.radiusservicepolicy;

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
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthResponseBehaviors;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;


@Path("/")
public class RadiusServicePolicyController {

	private static final String RADIUS_SERVICE_POLICIES = "Radius Service Policies";

	@GET
	public Response getByQueryParameter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String name) throws DataManagerException {
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		RadServicePolicyData policyData = policyBLManager.getRadiusServicePolicyByName(name);
		
		try {
			String policyString = new String(policyData.getRadiusPolicyXml());
			RadiusServicePolicyData data = (RadiusServicePolicyData)ConfigUtil.deserialize(policyString, RadiusServicePolicyData.class);
			return Response.ok(data).build();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParameter(@PathParam("name") String name) throws DataManagerException {
		return getByQueryParameter(name);
	}																																	 
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<RadiusServicePolicyData> data, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		
		Map<String, List<Status>> responseMap = policyBLManager.createRadiusServicePolicy(data.getList(), staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(RADIUS_SERVICE_POLICIES, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid RadiusServicePolicyData data) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		if(data.getDefaultAuthResponseBehavior().equals(AuthResponseBehaviors.HOTLINE.name()) == false) {
				data.setHotlinePolicy(null);
		}
		policyBLManager.createRadiusServicePolicy(data, staffData);
		return Response.ok(RestUtitlity.getResponse("Radius Service Policy created successfully")).build();
	}

	@PUT
	public Response updateByQueryParameter(@Valid RadiusServicePolicyData data, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String name) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		
		if(data.getDefaultAuthResponseBehavior().equals(AuthResponseBehaviors.HOTLINE.name()) == false) {
			data.setHotlinePolicy(null);
		}
		policyBLManager.updateRadiusServicePolicyByName(data, name, staffData);
		return Response.ok(RestUtitlity.getResponse("Radius Service Policy updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParameter(@Valid RadiusServicePolicyData data, @PathParam("name") String name) throws DataManagerException { 
		return updateByQueryParameter(data, name);
	}
	
	@DELETE
	public Response deleteByQueryParameter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String name) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		policyBLManager.deleteRadiusServicePolicyByName(Arrays.asList(name.split(",")), staffData);
		return Response.ok(RestUtitlity.getResponse("Radius Service Policy/Policies deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByPathParameter(@PathParam("name") String name) throws DataManagerException {
		return deleteByQueryParameter(name);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp("radiusservicepolicy");
	}
}
