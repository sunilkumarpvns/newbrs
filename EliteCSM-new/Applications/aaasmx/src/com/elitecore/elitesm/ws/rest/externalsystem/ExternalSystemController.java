package com.elitecore.elitesm.ws.rest.externalsystem;

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

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class ExternalSystemController {
	private static final String EXTERNAL_SYSTEM_INTERFACE = "External System Interface";
	private static final String MODULE = ConfigConstant.EXTERNAL_SYSTEM;
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)@QueryParam(value = "name") String externalSystemName) throws DataManagerException {

		ExternalSystemInterfaceBLManager blManager = new ExternalSystemInterfaceBLManager();
		ExternalSystemInterfaceInstanceData esiInstanceData = (ExternalSystemInterfaceInstanceData) blManager.getExternalSystemInterfaceInstanceDataByName(externalSystemName.trim());
		return Response.ok(esiInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String externalSystemName) throws DataManagerException {
		return getByNameFromQuery(externalSystemName);
	}

	@POST
	public Response create(@Valid ExternalSystemInterfaceInstanceData externalSystem) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ExternalSystemInterfaceBLManager externalSystemBLManager = new ExternalSystemInterfaceBLManager();
		
		if ((externalSystem.getEsiTypeId() == 1 || externalSystem.getEsiTypeId() == 2) == false){
			externalSystem.setRealmNames(null);
		}
		
		if(externalSystem.getStatusCheckMethod() == null || externalSystem.getStatusCheckMethod() == 1){
			externalSystem.setStatusCheckMethod(1L);
			externalSystem.setPacketBytes(null);
		}
		
		externalSystemBLManager.createESIInstance(externalSystem, staffData);
		return Response.ok(RestUtitlity.getResponse("External System Interface created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<ExternalSystemInterfaceInstanceData> externalSystems, @Context UriInfo uri) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ExternalSystemInterfaceBLManager externalSystemBLManager = new ExternalSystemInterfaceBLManager();
		List<ExternalSystemInterfaceInstanceData> esiDataList = externalSystems.getList();
		for (ExternalSystemInterfaceInstanceData esiData : esiDataList){
			
			if ((esiData.getEsiTypeId() == 1 || esiData.getEsiTypeId() == 2) == false){
				esiData.setRealmNames(null);
			}
			
			if(esiData.getStatusCheckMethod() == null || esiData.getStatusCheckMethod() == 1){
				esiData.setStatusCheckMethod(1L);
				esiData.setPacketBytes(null);
			}
		}
		Map<String, List<Status>> responseMap = externalSystemBLManager.createESIInstance(esiDataList, staffData, URLInfo.isPartialSuccess(uri));
		return Response.ok(RestUtitlity.getResponse(EXTERNAL_SYSTEM_INTERFACE,"(s) created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String externalSystemName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ExternalSystemInterfaceBLManager blManager = new ExternalSystemInterfaceBLManager();
		List<String> esiInstanceNames = Arrays.asList(externalSystemName.split(","));
		blManager.deleteByInstanceName(esiInstanceNames, staffData);
		return Response.ok(RestUtitlity.getResponse("External System Interface(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String externalSystemName) throws JAXBException, DataManagerException {
		return deleteByQueryParam(externalSystemName);
	}
	
	@PUT
	public Response update(@Valid ExternalSystemInterfaceInstanceData esiInstanceData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String esiName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ExternalSystemInterfaceBLManager blManager = new ExternalSystemInterfaceBLManager();
		
		esiInstanceData.setStatus("Y");
		if ((esiInstanceData.getEsiTypeId() == 1 || esiInstanceData.getEsiTypeId() == 2) == false){
			esiInstanceData.setRealmNames(null);
		}
		
		if(esiInstanceData.getStatusCheckMethod() == null || esiInstanceData.getStatusCheckMethod() == 1){
			esiInstanceData.setStatusCheckMethod(1L);
			esiInstanceData.setPacketBytes(null);
		}
		blManager.updateByInstanceName(esiInstanceData, staffData,esiName.trim());
		return Response.ok(RestUtitlity.getResponse("External System Interface updated successfully")).build();
	}
	
	@PUT 
	@Path("/{name}")
	public Response updateByPathParam(@Valid ExternalSystemInterfaceInstanceData esiInstanceData,@PathParam(value="name") String esiName) throws DataManagerException{
		return update(esiInstanceData, esiName);
	}
	
	@GET
	@Path("/help/")
	public Response getExternalSystemInterfaceHelp() throws IOException{
		return RestUtitlity.getHelp(RestHelpConstant.ESI);
	}
}