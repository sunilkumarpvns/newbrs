package com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InputParameterMissingException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.util.constants.AlertListenerConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class TrapListenerController {

	private static final String TRAP_LISTENER_CONFIGURATION = "Trap Listener configuration";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String trapListenerName) throws DataManagerException, JAXBException {

		AlertListenerData alertListenerData = getAlertListenerData(trapListenerName);
		AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		List<AlertTypeData> allAlertData = alertListenerBLManager.getAlertTypeDataList();
		getTrapListenerRelationalData(alertListenerData, allAlertData);

		return Response.ok(alertListenerData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		return getByNameFromQuery(serverName);
	}

	@POST
	public Response create(@Valid AlertListenerData alertListenerData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		alertListenerBLManager.getAlertTypeDataList();

		setAlertListenerData(alertListenerData);
		alertListenerData.setTypeId(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID);
		alertListenerBLManager.create(alertListenerData, staffData);

		return Response.ok(RestUtitlity.getResponse("Trap Listener configuration created successfully")).build();
	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<AlertListenerData> trapListenerDataList, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		List<AlertListenerData> list = trapListenerDataList.getList();
		List<AlertListenerData> alertListenerDataList = new  ArrayList<AlertListenerData>();

		for (AlertListenerData alertListenerData : list) {
			setAlertListenerData(alertListenerData);
			alertListenerDataList.add(alertListenerData);
			alertListenerData.setTypeId(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID);
		}
		Map<String, List<Status>> responseMap = alertListenerBLManager.create(alertListenerDataList, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse(TRAP_LISTENER_CONFIGURATION, "(s) created successfully",
				responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@PUT
	public Response updateByQueryParam(@Valid AlertListenerData alertListenerData , @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String alertListenerName)  throws DataManagerException, JAXBException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		AlertListenerBLManager alertListenerBLManager =  new AlertListenerBLManager();
		alertListenerData.setTypeId(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID);
		setAlertListenerData(alertListenerData);
		getAlertListenerData(alertListenerName);
		alertListenerBLManager.updateByName(alertListenerData, staffData, alertListenerName);

		return Response.ok(RestUtitlity.getResponse("Trap Listener configuration updated successfully")).build();

	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid AlertListenerData radAuthServiceData, @PathParam(value = "name") String serverName) throws DataManagerException, JAXBException {
		return updateByQueryParam(radAuthServiceData, serverName);
	}

	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String alertListenerName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AlertListenerBLManager alertListenerBLManager =  new AlertListenerBLManager();
		
		List<String> alertListenerNameList = Arrays.asList(alertListenerName.split(","));
		for (String listenerName: alertListenerNameList) {
			getAlertListenerData(listenerName);
		}
		alertListenerBLManager.deleteByName(alertListenerNameList, staffData);

		return Response.ok(RestUtitlity.getResponse("Trap Listener configuration(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String alertListenerName) throws DataManagerException {
		return deleteByQueryParam(alertListenerName);
	}

	private void getTrapListenerRelationalData(AlertListenerData alertListenerData, List<AlertTypeData> allAlertData) throws DataManagerException {
		
		alertListenerData.setAlertTrapListenerData((AlertTrapListenerData) alertListenerData.getAlertListener());
		Map<String,	String> mappingData = getIdToNameData(allAlertData);

		List<String> names = new LinkedList<String>();
		List<AlertListenerRelData> alertListenerRelDataSets = alertListenerData.getAlertListenerRelDataList();
		for (AlertListenerRelData relData : alertListenerRelDataSets) {
			
			if (AlertConfigurationConstant.Generics.getGenericName(relData.getTypeId()) != null) {
				names.add(AlertConfigurationConstant.Generics.getGenericName(relData.getTypeId()));
			} else {
				names.add(mappingData.get(relData.getTypeId())); 
			}
		}

		alertListenerData.setAlertList(names);
	}

	private Map<String, String> getIdToNameData(List<AlertTypeData> allAlertData) {
		Map<String,	String> mappingData = new LinkedHashMap<String, String>();
		for (AlertTypeData data : allAlertData) {
			if (AlertConfigurationConstant.LEAF_NODE.equals(data.getType()) && data.getName().equals(AlertConfigurationConstant.GENERIC) == false) {
				mappingData.put(data.getAlertTypeId(), data.getName());
			}
		}
		return mappingData;
	}
	
	private void setAlertListenerData(AlertListenerData alertListenerData) throws DataManagerException {
		AlertTrapListenerData trapListenerData = alertListenerData.getAlertTrapListenerData();
		try {
			if (alertListenerData.getAlertFileListenerData() != null || alertListenerData.getAlertSysLogAlertListenerData() !=null) {
				throw new DataValidationException();
			}
			if (trapListenerData == null) {
				throw new InputParameterMissingException();
			} else {
				alertListenerData.setAlertListener(trapListenerData);
			}

		} catch (DataValidationException dve) {
			throw new DataManagerException("Invalid Parameter given for Trap Listener Configuration.", dve);
		} catch (InputParameterMissingException ipm) {
			throw new DataManagerException("Insufficient basic paremerts for Trap Listener Configuration.", ipm);
		}
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.TRAP_LISTENER);
	}
	
	private AlertListenerData getAlertListenerData(String fileListenerName) throws DataManagerException {
		AlertListenerData alertListenerData = new AlertListenerData();
		AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		try {
			alertListenerData = alertListenerBLManager.getAlertListenerDetailDataByName(fileListenerName);
			if (AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equals(alertListenerData.getTypeId()) == false) {
				throw new InvalidValueException();
			}
		} catch (InvalidValueException ive) {
			throw new DataManagerException("Alert Listener configuration not found.", ive);
		} catch (DataManagerException dme) {
			throw dme;
		}
		return alertListenerData;
	}
}

	