package com.elitecore.elitesm.ws.rest.concurrentloginpolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
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
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.util.ConcurrentLoginPolicyUtility;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class ConcurrentLoginPolicyController {

	private static final String CONCURRENT_LOGIN = "Concurrent Login";
	private static final int UNLIMITED = -1;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String concurrentLoginPolicyName) throws DataManagerException {

		ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
		ConcurrentLoginPolicyData concurrentLoginPolicy = concurrentLoginPolicyBLManager.getConcurrentLoginPolicyByName(concurrentLoginPolicyName, ConfigManager.chekForCaseSensitivity());
		
		if (concurrentLoginPolicy.getLogin() == UNLIMITED) {
			concurrentLoginPolicy.setLoginLimit("Unlimited");
		} else {
			concurrentLoginPolicy.setLoginLimit("Limited");
		}
		
		concurrentLoginPolicy.setLstConcurrentLoginPolicyDetails(concurrentLoginPolicy.getConcurrentLoginPolicyDetail());
		
		if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails()) == false) {
			List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails();

			for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : concurrentLoginPolicyDetails) {
				concurrentLoginPolicyDetail.setAttributeValue(ConcurrentLoginPolicyUtility.getAttributeNameFromAttributeId(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()));
				if (concurrentLoginPolicyDetail.getLogin() == UNLIMITED) {
					concurrentLoginPolicyDetail.setLoginLimit("Unlimited");
				} else {
					concurrentLoginPolicyDetail.setLoginLimit("Limited");
				}
			}
		}
		concurrentLoginPolicy.setName(concurrentLoginPolicyName);
		return Response.ok(concurrentLoginPolicy).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String concurrentLoginPolicyName) throws DataManagerException {
		return getByNameFromQuery(concurrentLoginPolicyName);
	}

	@POST
	public Response create(@Valid ConcurrentLoginPolicyData concurrentLoginPolicy) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
		
		Timestamp timestamp = new Timestamp(new Date().getTime());
		
		concurrentLoginPolicy.setSystemGenerated("N");
		concurrentLoginPolicy.setCreateDate(timestamp);
		concurrentLoginPolicy.setCreatedByStaffId(staffData.getStaffId());
		concurrentLoginPolicy.setLastModifiedDate(timestamp);
		concurrentLoginPolicy.setLastModifiedByStaffId(staffData.getStaffId());
        concurrentLoginPolicy.setStatusChangeDate(timestamp);
        
        if (concurrentLoginPolicy.getLoginLimit().equalsIgnoreCase("Unlimited")) {
        	concurrentLoginPolicy.setLogin(UNLIMITED);
		} 
        
        if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails()) == false) {
        	concurrentLoginPolicy.setConcurrentLoginPolicyDetail(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails());
        	concurrentLoginPolicy.setLstConcurrentLoginPolicyDetails(null);
			List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getConcurrentLoginPolicyDetail();
			
			int size = concurrentLoginPolicyDetails.size();
			for ( int i = 0; i < size; i++ ) {
				ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail = (ConcurrentLoginPolicyDetailData) concurrentLoginPolicyDetails.get(i);
				concurrentLoginPolicyDetail.setSerialNumber(i + 1);
				if (ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()).equals("attribute_list_not_found") == false) {
					concurrentLoginPolicyDetail.setAttributeValue(ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()));
				}
				if (concurrentLoginPolicyDetail.getLoginLimit().equalsIgnoreCase("Unlimited")) {
					concurrentLoginPolicyDetail.setLogin(UNLIMITED);
				}
            }
		}
			
		concurrentLoginPolicyBLManager.createConcurrentLoginPolicy(concurrentLoginPolicy, staffData, ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse("Concurrent Login Policy created successfully")).build();

	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<ConcurrentLoginPolicyData> concurrentLoginPolicyDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
		
		List<ConcurrentLoginPolicyData> concurrentLoginPolicies = concurrentLoginPolicyDatas.getList();
		
		for (ConcurrentLoginPolicyData concurrentLoginPolicy : concurrentLoginPolicies) {
			
			Timestamp timestamp = new Timestamp(new Date().getTime());
			
			concurrentLoginPolicy.setSystemGenerated("N");
			concurrentLoginPolicy.setCreateDate(timestamp);
			concurrentLoginPolicy.setCreatedByStaffId(staffData.getStaffId());
			concurrentLoginPolicy.setLastModifiedDate(timestamp);
			concurrentLoginPolicy.setLastModifiedByStaffId(staffData.getStaffId());
            concurrentLoginPolicy.setStatusChangeDate(timestamp);
            
            if (concurrentLoginPolicy.getLoginLimit().equalsIgnoreCase("Unlimited")) {
            	concurrentLoginPolicy.setLogin(UNLIMITED);
			} 
            
            if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails()) == false) {
            	concurrentLoginPolicy.setConcurrentLoginPolicyDetail(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails());
            	concurrentLoginPolicy.setLstConcurrentLoginPolicyDetails(null);
				List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getConcurrentLoginPolicyDetail();
				
				int size = concurrentLoginPolicyDetails.size();
				for ( int i = 0; i < size; i++ ) {
					ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail = (ConcurrentLoginPolicyDetailData) concurrentLoginPolicyDetails.get(i);
					concurrentLoginPolicyDetail.setSerialNumber(i + 1);
					if (ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()).equals("attribute_list_not_found") == false) {
						concurrentLoginPolicyDetail.setAttributeValue(ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()));
					}
					if (concurrentLoginPolicyDetail.getLoginLimit().equalsIgnoreCase("Unlimited")) {
						concurrentLoginPolicyDetail.setLogin(UNLIMITED);
					}
                }
			}
			
		}

		Map<String, List<Status>> responseMap = concurrentLoginPolicyBLManager.createConcurrentLoginPolicy(concurrentLoginPolicies, staffData, URLInfo.isPartialSuccess(uri),ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse(CONCURRENT_LOGIN, " Policy/Policies created successfully",
				responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();

	}

	@PUT
	public Response updateByQueryParam(@Valid ConcurrentLoginPolicyData concurrentLoginPolicy, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String concurrentLoginPolicyName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
		
		concurrentLoginPolicy.setLastModifiedDate(new Timestamp(new Date().getTime()));
		concurrentLoginPolicy.setLastModifiedByStaffId(staffData.getStaffId());
		
		if (concurrentLoginPolicy.getLoginLimit().equalsIgnoreCase("Unlimited")) {
        	concurrentLoginPolicy.setLogin(UNLIMITED);
		} 
		
		if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails()) == false) {
			concurrentLoginPolicy.setConcurrentLoginPolicyDetail(concurrentLoginPolicy.getLstConcurrentLoginPolicyDetails());
        	concurrentLoginPolicy.setLstConcurrentLoginPolicyDetails(null);
			List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getConcurrentLoginPolicyDetail();
			
			int size = concurrentLoginPolicyDetails.size();
			for ( int i = 0; i < size; i++ ) {
				ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail = (ConcurrentLoginPolicyDetailData) concurrentLoginPolicyDetails.get(i);
				concurrentLoginPolicyDetail.setSerialNumber(i + 1);
				if (ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()).equals("attribute_list_not_found") == false) {
					concurrentLoginPolicyDetail.setAttributeValue(ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicy.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()));
				}
				if (concurrentLoginPolicyDetail.getLoginLimit().equalsIgnoreCase("Unlimited")) {
					concurrentLoginPolicyDetail.setLogin(UNLIMITED);
				}
            }
		}
		
		concurrentLoginPolicyBLManager.updateConcurrentLoginPolicyByName(concurrentLoginPolicy, staffData, concurrentLoginPolicyName, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Concurrent Login Policy updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid ConcurrentLoginPolicyData concurrentLoginPolicy, @PathParam(value = "name") String concurrentLoginPolicyName) throws DataManagerException {
		return updateByQueryParam(concurrentLoginPolicy, concurrentLoginPolicyName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String concurrentLoginPolicyNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();

		List<String> listConcurrentLoginPolicyName = Arrays.asList(concurrentLoginPolicyNames.split(","));

		concurrentLoginPolicyBLManager.deleteConcurrentLoginPolicyByName(listConcurrentLoginPolicyName, staffData, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Concurrent Login Policy/Policies deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String concurrentLoginPolicyNames) throws DataManagerException {
		return deleteByQueryParam(concurrentLoginPolicyNames);
	}

	@GET
	@Path("/help")
	public Response getConcurrentLoginPolicyHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.CONCURRENT_LOGIN_POLICY);
	}
	

}
