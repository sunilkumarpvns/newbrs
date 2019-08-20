package com.elitecore.elitesm.ws.rest.dynauthservicepolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
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

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientDetailData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientsData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class DynAuthServicePolicyController {

	private static final String DYNAUTH_SERVICE_POLICIES = "Dynauth Service Policies ";

	@GET
	public Response getByQueryParamter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam("name") String name) throws DataManagerException {
		
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		DynAuthPolicyInstData data = policyBLManager.getDynAuthPolicyByName(name);
		return Response.ok(data).build();
	}

	@GET
	@Path("/{name}")
	public Response getByPathParameter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@PathParam("name") String name) throws DataManagerException {
		return getByQueryParamter(name);
	}

	@DELETE
	public Response deleteByQueryParameter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam("name") String name) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		policyBLManager.deleteDynAuthPolicyByName(Arrays.asList(name.split(",")), staffData);
		return Response.ok(RestUtitlity.getResponse("Dynauth Service Policy deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParameter(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@PathParam("name") String name) throws DataManagerException {
		return deleteByQueryParameter(name);
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DynAuthPolicyInstData> policies,@Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		
		for (DynAuthPolicyInstData policyData : policies.getList()) {
			setExternalSystemDataIn(policyData);
		}
		Map<String, List<Status>> responseMap = policyBLManager.createDynaAuthPolicy(policies.getList(), staffData, URLInfo.isPartialSuccess(uri));
		return Response.ok(RestUtitlity.getResponse(DYNAUTH_SERVICE_POLICIES,"created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid DynAuthPolicyInstData policy) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		policyBLManager.createDynAuthPolicy(policy, staffData);
		return Response.ok(RestUtitlity.getResponse("Dynauth Service Policy created successfully")).build();
	}
	
	@PUT
	public Response updateByQueryParameter(@Valid DynAuthPolicyInstData data, 
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
		
		setExternalSystemDataIn(data);

		policyBLManager.updateDynAuthPolicyByName(data, staffData, name);
		return Response.ok(RestUtitlity.getResponse("Dynauth Service Policy updated successfully")).build();
	}
	
	private void setExternalSystemDataIn(DynAuthPolicyInstData data) throws DataManagerException {
		Iterator<DynAuthNasClientsData> iterator = data.getDynAuthNasClientDataSet().iterator();
		ExternalSystemInterfaceBLManager esiBLmanager = new ExternalSystemInterfaceBLManager();
		
		while (iterator.hasNext()) {
			DynAuthNasClientsData clientData = iterator.next();
			Iterator<DynAuthNasClientDetailData> nasDetails = clientData.getDynaAuthNasClientDetailsData().iterator();
			
			while (nasDetails.hasNext()) {
				DynAuthNasClientDetailData nasDetail = nasDetails.next();
				ExternalSystemInterfaceInstanceData esiData = esiBLmanager.getExternalSystemInterfaceInstanceDataById(nasDetail.getEsiInstanceId());
				if (esiData != null) {
					nasDetail.setExternalSystemInstanceData(esiData);
				}
			}
		}
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParameter(DynAuthPolicyInstData data, 
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam("name") String name) throws DataManagerException {
		return updateByQueryParameter(data, name);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DYNAAUTH_SERVICE_POLICY);
	}
}