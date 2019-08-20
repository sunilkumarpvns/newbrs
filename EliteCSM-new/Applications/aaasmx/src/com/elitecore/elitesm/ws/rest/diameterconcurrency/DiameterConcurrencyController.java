package com.elitecore.elitesm.ws.rest.diameterconcurrency;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class DiameterConcurrencyController {

	private static final String DIAMETER_CONCURRENCIES = "Diameter Concurrencies";
	private static final String MODULE = ConfigConstant.DIAMETER_CONCURRENCY;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterConcurrencyName) throws DataManagerException {

		DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
		DiameterConcurrencyData diameterConcurrencyData = diameterConcurrencyBLManager.getDiameterConcurrencyDataByName(diameterConcurrencyName);
		
		List<DiameterConcurrencyFieldMapping> diameterConcurrencyDataList = diameterConcurrencyData.getDiameterConcurrencyFieldMappingList();
		List<DiameterConcurrencyFieldMapping> mandatoryMappingList = new ArrayList<DiameterConcurrencyFieldMapping>();
		List<DiameterConcurrencyFieldMapping> additionalMappingList = new ArrayList<DiameterConcurrencyFieldMapping>();
		
		for (DiameterConcurrencyFieldMapping diameterConcurrencyDatas : diameterConcurrencyDataList) {
			
			if (Strings.isNullOrEmpty(diameterConcurrencyDatas.getLogicalField()) == false) {
				mandatoryMappingList.add(diameterConcurrencyDatas);
			} else {
				additionalMappingList.add(diameterConcurrencyDatas);
			}
		}
		diameterConcurrencyData.setDiameterConcurrencyMandatoryFieldMappingsList(mandatoryMappingList);
		if(Collectionz.isNullOrEmpty(additionalMappingList) == false){
			diameterConcurrencyData.setDiameterConcurrencyAdditonalFieldMappingList(additionalMappingList);
		}
		return Response.ok(diameterConcurrencyData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterConcurrencyName) throws DataManagerException {
		return getByNameFromQuery(diameterConcurrencyName);
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterConcurrencyData> diameterConcurrencyDataList, @Context UriInfo uri) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
		
		for (DiameterConcurrencyData diameterConcurrencyData : diameterConcurrencyDataList.getList()) {
			
			diameterConcurrencyData.setDiameterConcurrencyFieldMappingList(getDiameterFieldMapping(diameterConcurrencyData));
			
			LogManager.getLogger().info(MODULE, diameterConcurrencyData.toString());
		}
		
		Map<String, List<Status>> responseMap = diameterConcurrencyBLManager.create(diameterConcurrencyDataList.getList(),staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_CONCURRENCIES, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid DiameterConcurrencyData diameterConcurrencyData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();

		diameterConcurrencyData.setDiameterConcurrencyFieldMappingList(getDiameterFieldMapping(diameterConcurrencyData));

		LogManager.getLogger().info(MODULE, diameterConcurrencyData.toString());

		diameterConcurrencyBLManager.create(diameterConcurrencyData,staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Concurrency created successfully")).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DiameterConcurrencyData diameterConcurrencyData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String byName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();

		diameterConcurrencyData.setDiameterConcurrencyFieldMappingList(getDiameterFieldMapping(diameterConcurrencyData));
		
		diameterConcurrencyBLManager.updateDiameterConcurrencyByName(diameterConcurrencyData,staffData,byName);

		LogManager.getLogger().info(MODULE, diameterConcurrencyData.toString());
		return Response.ok(RestUtitlity.getResponse("Diameter Concurrency updated successfully")).build();
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid DiameterConcurrencyData diameterConcurrencyData,@PathParam(value="name") String byName) throws DataManagerException {
		return updateByQueryParam(diameterConcurrencyData, byName);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String diameterConcurrencyName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();

		List<String> diameterConcurrencyNames = Arrays.asList(diameterConcurrencyName.split(","));
		diameterConcurrencyBLManager.deleteByName(diameterConcurrencyNames, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Concurrencies deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String diameterConcurrencyName) throws DataManagerException {
		return deleteByQueryParam(diameterConcurrencyName);
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp("diameterConcurrency");
	}
	
	/**
	 * This method is used to combine two saperate list to one list.
	 * @param diameterConcurrencyData
	 * @return list
	 */
	private List<DiameterConcurrencyFieldMapping> getDiameterFieldMapping(DiameterConcurrencyData diameterConcurrencyData){

		List<DiameterConcurrencyFieldMapping> diaConMappingList = new ArrayList<DiameterConcurrencyFieldMapping>();
		for (DiameterConcurrencyFieldMapping diameterConcurrencyFieldMapping : diameterConcurrencyData.getDiameterConcurrencyMandatoryFieldMappingsList()) {
			diaConMappingList.add(diameterConcurrencyFieldMapping);
		}

		List<DiameterConcurrencyFieldMapping> additionalDBFieldMapping = diameterConcurrencyData.getDiameterConcurrencyAdditonalFieldMappingList(); 
		if (Collectionz.isNullOrEmpty(additionalDBFieldMapping) == false) {

			for (DiameterConcurrencyFieldMapping diameterConcurrencyFieldMapping : additionalDBFieldMapping) {
				diaConMappingList.add(diameterConcurrencyFieldMapping);
			}
		}
		return diaConMappingList;
	}
}
