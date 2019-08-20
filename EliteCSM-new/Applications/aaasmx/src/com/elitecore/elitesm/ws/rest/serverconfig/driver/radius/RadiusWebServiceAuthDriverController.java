package com.elitecore.elitesm.ws.rest.serverconfig.driver.radius;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;
public class RadiusWebServiceAuthDriverController {
	private static final String RADIUS_WEB_SERVICE_AUTH_DRIVER = "Radius Web Service Auth Driver";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name)
	throws DataManagerException {
		DriverBLManager driverBLManager = new DriverBLManager();

		DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(name);
		
		WebServiceAuthDriverData webServiceAuthData = driverBLManager.getWebServiceDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		Set<WebServiceAuthDriverData> webServiceAuthDriverSet = new HashSet<WebServiceAuthDriverData>();
		
		List<WebMethodKeyMapRelData> webMethodKeyDataList = webServiceAuthData.getWebMethodKeyDataList();
		
		for(WebMethodKeyMapRelData data : webMethodKeyDataList) {
			data.setLogicalName(data.getNameValuePoolData().getName());
		}
		
		webServiceAuthDriverSet.add(webServiceAuthData);

		driverInstanceData.setUserFileDetail(null);
		driverInstanceData.setDbdetail(null);
		driverInstanceData.setCsvset(null);
		driverInstanceData.setMapGatewaySet(null);
		driverInstanceData.setDbacctset(null);
		driverInstanceData.setLdapdetail(null);
		driverInstanceData.setHttpAuthFieldMapSet(null);
		driverInstanceData.setHssDetail(null);
		driverInstanceData.setCrestelChargingSet(null);
		driverInstanceData.setCrestelRatingSet(null);
		driverInstanceData.setDiameterChargingDriverSet(null);
		driverInstanceData.setWebServiceAuthDriverSet(webServiceAuthDriverSet);
		
		return Response.ok(driverInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParam(@PathParam(value = "name") String driverName) throws DataManagerException{
		return getByQueryParam(driverName);
	}

	@POST
	public Response create(@Valid DriverInstanceData driverInstance) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
	
		driverInstance.setStatus(BaseConstant.SHOW_STATUS_ID);
		driverInstance.setDriverTypeId(DriverTypeConstants.RADIUS_AIRCEL_WEBSERVICE_AUTH_DRIVER);
		driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
		
		WebServiceAuthDriverData webServiceAuthDriverData = (WebServiceAuthDriverData)driverInstance.getWebServiceAuthDriverSet().iterator().next();
		
		ResultObject resultObject = new ResultObject();
		resultObject.setDriverInstance(driverInstance);
		resultObject.setWebServiceAuthDriverData(webServiceAuthDriverData);
		
		driverBlManager.createWebServiceAuthDriver(driverInstance, webServiceAuthDriverData, staffData);
		return Response.ok(RestUtitlity.getResponse("Radius Web Service Auth Driver created successfully")).build();
	}
	
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceDataList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();

		List<DriverInstanceData> list = driverInstanceDataList.getList();
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		
		for(DriverInstanceData driverInstance : list){
			driverInstance.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstance.setDriverTypeId(DriverTypeConstants.RADIUS_AIRCEL_WEBSERVICE_AUTH_DRIVER);
			driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
			
			WebServiceAuthDriverData webServiceAuthDriverData = (WebServiceAuthDriverData)driverInstance.getWebServiceAuthDriverSet().iterator().next();
			
			ResultObject resultObject = new ResultObject();
			resultObject.setDriverInstance(driverInstance);
			resultObject.setWebServiceAuthDriverData(webServiceAuthDriverData);
			resultObjectList.add(resultObject);
		}
		Map<String, List<Status>> responseMap = driverBlManager.createWebServiceAuthDriver(resultObjectList,staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(RADIUS_WEB_SERVICE_AUTH_DRIVER, "(s) created successfully",
				responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String name) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DriverBLManager driverBLManager = new DriverBLManager();

		WebServiceAuthDriverData updatedWebServiceAuthDriverData = (WebServiceAuthDriverData)driverInstance.getWebServiceAuthDriverSet().iterator().next();
		
		DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(name);
		WebServiceAuthDriverData webServiceAuthData = driverBLManager.getWebServiceDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		
		updatedWebServiceAuthDriverData.setDriverInstanceId(webServiceAuthData.getDriverInstanceId());
		updatedWebServiceAuthDriverData.setWsAuthDriverId(webServiceAuthData.getWsAuthDriverId());
		
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		driverBLManager.updateWebServiceAuthDriverByName(driverInstance, updatedWebServiceAuthDriverData, staffData, name);
		return Response.ok(RestUtitlity.getResponse("Radius Web Service Auth Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData,@PathParam(value = "name") String name) 
			throws  DataManagerException, JAXBException {
		return updateByQueryParam(driverInstanceData,name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData, driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("Radius Web Service Auth Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_WEB_AUTH);
	}


}
