package com.elitecore.elitesm.ws.rest.accesspolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.radius.policies.accesspolicy.AccessPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class AccessPolicyController {

	private static final String ACCESS_POLICY_POLICIES = "Access Policy/Policies ";
	private static final int YEAR = 1700;
	private static final int MONTH = 1;
	private static final int DATE = 1;
	private static final int SECOND = 0;
	private static final String MODULE = ConfigConstant.ACCESS_POLICY;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String accessPolicyName) throws DataManagerException {

		AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();
		IAccessPolicyData accessPolicy = accessPolicyBLManager.getAccessPolicyByName(accessPolicyName,ConfigManager.chekForCaseSensitivity());
		
		if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
			
			List accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();
			
			Calendar calendar = new GregorianCalendar();
			
			int startHour = 0;
			int startMinute = 0;
			int stopHour = 0;
			int stopMinute = 0;
			
			Iterator iteratorAccessPolicyDetail = accessPolicyDetails.iterator();
			while (iteratorAccessPolicyDetail.hasNext()) {
				
				AccessPolicyDetailData accessPolicyDetail = (AccessPolicyDetailData) iteratorAccessPolicyDetail.next();
				
				if (accessPolicyDetail.getStartTime() != null) {
					
					calendar.setTime(accessPolicyDetail.getStartTime());
					startHour = calendar.get(Calendar.HOUR_OF_DAY);
					startMinute = calendar.get(Calendar.MINUTE);
					
					accessPolicyDetail.setStartHour(String.valueOf(startHour));
					accessPolicyDetail.setStartMinute(String.valueOf(startMinute));
					
				}
				
				if (accessPolicyDetail.getStopTime() != null) {
					
					calendar.setTime(accessPolicyDetail.getStopTime());
					stopHour = calendar.get(Calendar.HOUR_OF_DAY);
					stopMinute = calendar.get(Calendar.MINUTE);
					
					accessPolicyDetail.setStopHour(String.valueOf(stopHour));
					accessPolicyDetail.setStopMinute(String.valueOf(stopMinute));
					
				}
				
			}
			
		}
		accessPolicy.setName(accessPolicyName);
		return Response.ok(accessPolicy).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String accessPolicyName) throws DataManagerException {
		return getByNameFromQuery(accessPolicyName);
	}

	@POST
	public Response create(@Valid AccessPolicyData accessPolicy) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();

		if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
			
			List<AccessPolicyDetailData> accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();
			
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			
			int size = accessPolicyDetails.size();
			for ( int i = 0; i < size; i++ ) {
				
				AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) accessPolicyDetails.get(i);
	                
				int startHour = Integer.parseInt(accessPolicyDetailData.getStartHour());
				int startMinute = Integer.parseInt(accessPolicyDetailData.getStartMinute());
				gregorianCalendar.set(YEAR, MONTH, DATE, startHour, startMinute, SECOND);
				accessPolicyDetailData.setStartTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
				
				int stopHour = Integer.parseInt(accessPolicyDetailData.getStopHour());
				int stopMinute = Integer.parseInt(accessPolicyDetailData.getStopMinute());
				gregorianCalendar.set(YEAR, MONTH, DATE, stopHour, stopMinute, SECOND);
				accessPolicyDetailData.setStopTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
	                
			}
			
		}

		accessPolicyBLManager.createAccessPolicy(accessPolicy, staffData, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Access Policy created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<AccessPolicyData> accessPolicyDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();

		List<AccessPolicyData> accessPolicies = accessPolicyDatas.getList();
		
		for (Iterator<AccessPolicyData> iterator = accessPolicies.iterator(); iterator.hasNext();) {
			
			AccessPolicyData accessPolicy = iterator.next();
			
			if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
				
				List<AccessPolicyDetailData> accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();
				
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				
				int size = accessPolicyDetails.size();
				for ( int i = 0; i < size; i++ ) {
					
                    AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) accessPolicyDetails.get(i);
                    
					int startHour = Integer.parseInt(accessPolicyDetailData.getStartHour());
					int startMinute = Integer.parseInt(accessPolicyDetailData.getStartMinute());
					gregorianCalendar.set(YEAR, MONTH, DATE, startHour, startMinute, SECOND);
					accessPolicyDetailData.setStartTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
					
					int stopHour = Integer.parseInt(accessPolicyDetailData.getStopHour());
					int stopMinute = Integer.parseInt(accessPolicyDetailData.getStopMinute());
					gregorianCalendar.set(YEAR, MONTH, DATE, stopHour, stopMinute, SECOND);
					accessPolicyDetailData.setStopTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
                    
                }
				
			}

		}

		Map<String, List<Status>> responseMap = accessPolicyBLManager.createAccessPolicy(accessPolicies, staffData, URLInfo.isPartialSuccess(uri), ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse(ACCESS_POLICY_POLICIES, "created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();

	}

	@PUT
	public Response updateByQueryParam(@Valid AccessPolicyData accessPolicy, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String accessPolicyName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();
		
		if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
			
			List<AccessPolicyDetailData> accessPolicyDetails =  accessPolicy.getAccessPolicyDetailDataList();
			
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			
			int size = accessPolicyDetails.size();
			for ( int i = 0; i < size; i++ ) {
				
                AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) accessPolicyDetails.get(i);
                
                int startHour = Integer.parseInt(accessPolicyDetailData.getStartHour());
				int startMinute = Integer.parseInt(accessPolicyDetailData.getStartMinute());
				gregorianCalendar.set(YEAR, MONTH, DATE, startHour, startMinute, SECOND);
				accessPolicyDetailData.setStartTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
				
				int stopHour = Integer.parseInt(accessPolicyDetailData.getStopHour());
				int stopMinute = Integer.parseInt(accessPolicyDetailData.getStopMinute());
				gregorianCalendar.set(YEAR, MONTH, DATE, stopHour, stopMinute, SECOND);
				accessPolicyDetailData.setStopTime(new Timestamp(gregorianCalendar.getTimeInMillis()));
                
            }
			
		}
		
		accessPolicyBLManager.updateAccessPolicyByName(accessPolicy, staffData, accessPolicyName, ConfigManager.chekForCaseSensitivity());

		LogManager.getLogger().info(MODULE, accessPolicy.toString());

		return Response.ok(RestUtitlity.getResponse("Access Policy updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid AccessPolicyData accessPolicy, @PathParam(value = "name") String accessPolicyName) throws DataManagerException {
		return updateByQueryParam(accessPolicy, accessPolicyName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String accessPolicyName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();

		List<String> accessPolicyNames = Arrays.asList(accessPolicyName.split(","));

		accessPolicyBLManager.deleteAccessPolicyByName(accessPolicyNames, staffData, ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse("Access Policy/Policies deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String accessPolicyName) throws DataManagerException {
		return deleteByQueryParam(accessPolicyName);
	}

	@GET
	@Path("/help")
	public Response getAccessPolicyHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ACCESS_POLICY);
	}

}
