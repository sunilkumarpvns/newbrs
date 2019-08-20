package com.elitecore.elitesm.ws.rest.serverconfig.sessionmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerESIServerData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class SessionManagerController {

	private static final String SESSION_MANAGERS = "Session Managers";
	private static final String MODULE = "SESSION_MANAGER";
	private static final Pattern NAME_PATTERN = Pattern.compile(RestValidationMessages.NAME_REGEX);

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String sessionManagerName) throws DataManagerException {

		SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();

		ISessionManagerInstanceData sessionManagerInstanceData = sessionManagerBLManager.getSessionManagerDataByName(sessionManagerName);

		SMConfigInstanceData smConfigInstanceDatas = sessionManagerInstanceData.getSmConfigInstanceData();
		List<SMDBFieldMapData> mandatoryMappingDataList = new ArrayList<SMDBFieldMapData>();
		List<SMDBFieldMapData> additionalMappingDataList = new ArrayList<SMDBFieldMapData>();
		
		smConfigInstanceDatas.setName(sessionManagerInstanceData.getName());
		smConfigInstanceDatas.setDescription(sessionManagerInstanceData.getDescription());

		for (SMDBFieldMapData smDBFieldMapData : smConfigInstanceDatas.getDbFieldMapDataList()) {
			if(Strings.isNullOrEmpty(smDBFieldMapData.getField()) == false){
				mandatoryMappingDataList.add(smDBFieldMapData);
			} else {
				additionalMappingDataList.add(smDBFieldMapData);
			}
		}
		smConfigInstanceDatas.setLstMandatoryFieldMapData(mandatoryMappingDataList);
		if(Collectionz.isNullOrEmpty(additionalMappingDataList) == false){
			smConfigInstanceDatas.setDbFieldMapDataList(null);
			smConfigInstanceDatas.setDbFieldMapDataList(additionalMappingDataList);
		}
		
		List<SMSessionCloserESIRelData> smSessionCloserESIRelDatas = smConfigInstanceDatas.getSmSessionCloserESIRelDataList();
		List<SMSessionCloserESIRelData> nasServerList = new ArrayList<SMSessionCloserESIRelData> ();
		List<SMSessionCloserESIRelData> acctServerList = new ArrayList<SMSessionCloserESIRelData> ();
		
		if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDatas) == false){
			for (SMSessionCloserESIRelData smSessionCloserESIRelData : smSessionCloserESIRelDatas) {
				if(smSessionCloserESIRelData.getExternalSystemData().getEsiTypeId() == 7){
					nasServerList.add(smSessionCloserESIRelData);
				} else if (smSessionCloserESIRelData.getExternalSystemData().getEsiTypeId() == 2){
					acctServerList.add(smSessionCloserESIRelData);
				}
			}
		}
		SessionManagerESIServerData esiServerData = new SessionManagerESIServerData();
		if(Collectionz.isNullOrEmpty(nasServerList) == false){
			esiServerData.setNasServerDataList(nasServerList);
		}
		
		if(Collectionz.isNullOrEmpty(acctServerList) == false){
			esiServerData.setAccountServerDataList(acctServerList);
		}
		
		if(Collectionz.isNullOrEmpty(esiServerData.getNasServerDataList()) == false){
			smConfigInstanceDatas.setSessionManagerESIServerData(esiServerData);
		}
		
		if(Collectionz.isNullOrEmpty(esiServerData.getAccountServerDataList()) == false){
			smConfigInstanceDatas.setSessionManagerESIServerData(esiServerData);
		}
		return Response.ok(smConfigInstanceDatas).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String sessionManagerName) throws DataManagerException {
		return getByNameFromQuery(sessionManagerName);
	}
	
	@POST
	public Response create(@Valid SMConfigInstanceData smConfigInstanceData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
		SessionManagerInstanceData sessionManagerInstanceData = new SessionManagerInstanceData();
		
		if(Strings.isNullOrEmpty(smConfigInstanceData.getName())){
			return Response.ok(RestUtitlity.getResponse("Session Manager Name must be specified",ResultCode.INPUT_PARAMETER_MISSING)).build();
		}
		
		if(!NAME_PATTERN.matcher(smConfigInstanceData.getName()).matches()){
			return Response.ok(RestUtitlity.getResponse("Session Manager Name must be valid",ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		sessionManagerInstanceData.setName(smConfigInstanceData.getName());
		sessionManagerInstanceData.setDescription(smConfigInstanceData.getDescription());
		sessionManagerInstanceData.setStatus("CST01");
		sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);

		sessionManagerBLManager.create(sessionManagerInstanceData,staffData);

		LogManager.getLogger().info(MODULE,"Session Manager created successfully");

		return Response.ok(RestUtitlity.getResponse("Session Manager created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<SMConfigInstanceData> smConfigInsDataList, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
		
		List<SMConfigInstanceData> sessionManagerConfDataList = smConfigInsDataList.getList();
		List<SessionManagerInstanceData> sessionManagerInstanceDataList = new ArrayList<SessionManagerInstanceData>();
		
		for (SMConfigInstanceData smConfigInstanceData : sessionManagerConfDataList) {
			SessionManagerInstanceData sessionManagerInstanceData = new SessionManagerInstanceData();
			
			if(Strings.isNullOrEmpty(smConfigInstanceData.getName())){
				return Response.ok(RestUtitlity.getResponse("Session Manager Name must be specified",ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			if(!NAME_PATTERN.matcher(smConfigInstanceData.getName()).matches()){
				return Response.ok(RestUtitlity.getResponse("Session Manager Name must be valid",ResultCode.INVALID_INPUT_PARAMETER)).build();
			}
			
			sessionManagerInstanceData.setName(smConfigInstanceData.getName());
			sessionManagerInstanceData.setDescription(smConfigInstanceData.getDescription());
			sessionManagerInstanceData.setStatus("CST01");
			sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);
			
			sessionManagerInstanceDataList.add(sessionManagerInstanceData);
		}

		Map<String, List<Status>> responseMap = sessionManagerBLManager.create(sessionManagerInstanceDataList,staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(SESSION_MANAGERS, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid SMConfigInstanceData smConfigInstanceData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String byName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
		ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
		SessionManagerInstanceData sessionManagerInstaceData = new SessionManagerInstanceData();
		ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData = null;
		
		if(Strings.isNullOrEmpty(smConfigInstanceData.getName())){
			return Response.ok(RestUtitlity.getResponse("Session Manager Name must be specified",ResultCode.INPUT_PARAMETER_MISSING)).build();
		}
		
		if(!NAME_PATTERN.matcher(smConfigInstanceData.getName()).matches()){
			return Response.ok(RestUtitlity.getResponse("Session Manager Name must be valid",ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		sessionManagerInstaceData.setName(smConfigInstanceData.getName());
		sessionManagerInstaceData.setDescription(smConfigInstanceData.getDescription());
		sessionManagerInstaceData.setStatus("CST01");
		sessionManagerInstaceData.setSmConfigInstanceData(smConfigInstanceData);
		
		List<SMSessionCloserESIRelData> esiDataList = sessionManagerInstaceData.getSmConfigInstanceData().getSmSessionCloserESIRelDataList();
		if(Collectionz.isNullOrEmpty(esiDataList) == false){
			for (SMSessionCloserESIRelData smSessionCloserESIRelData : esiDataList) {
				externalSystemInterfaceInstanceData = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataById(smSessionCloserESIRelData.getEsiInstanceId());
				smSessionCloserESIRelData.setExternalSystemData(externalSystemInterfaceInstanceData);
			}
		}
		sessionManagerBLManager.updateSessionManagerInstanceDataByName(sessionManagerInstaceData,staffData,byName);

		LogManager.getLogger().info(MODULE, sessionManagerInstaceData.toString());
		return Response.ok(RestUtitlity.getResponse("Session Manager updated successfully")).build();
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid SMConfigInstanceData smConfigInstanceData,@PathParam(value="name") String byName) throws DataManagerException {
		return updateByQueryParam(smConfigInstanceData, byName);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String sessionManagerName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
		
		List<String> sessionManagerNames = Arrays.asList(sessionManagerName.split(","));
		
		sessionManagerBLManager.deleteByName(sessionManagerNames,staffData);
		return Response.ok(RestUtitlity.getResponse("Session Managers deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String sessionManagerName) throws DataManagerException {
		return deleteByQueryParam(sessionManagerName);
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.SESSION_MANAGER);
	}
}
