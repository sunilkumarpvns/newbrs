package com.elitecore.elitesm.ws.rest.serverconfig.driver.charginggateway;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class CrestelChargingDriverController {

	private static final String CRESTEL_CHARGING_DRIVER = "Crestel Charging Driver";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name)
	throws DataManagerException {
		
		DriverBLManager driverBlManager = new DriverBLManager();

		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		CrestelChargingDriverData crestleChargingDriverData = driverBlManager.getCrestelChargingDriverData(driverInstanceData.getDriverInstanceId());
		Set<CrestelChargingDriverData> crestelChargingDriverDataSet = new HashSet<CrestelChargingDriverData>();
		crestelChargingDriverDataSet.add(crestleChargingDriverData);
		driverInstanceData.setDbdetail(null);
		driverInstanceData.setDbacctset(null);
		driverInstanceData.setCsvset(null);
		driverInstanceData.setMapGatewaySet(null);
		driverInstanceData.setHssDetail(null);
		driverInstanceData.setLdapdetail(null);
		driverInstanceData.setDetaillocalset(null);
		driverInstanceData.setCrestelRatingSet(null);
		driverInstanceData.setCrestelChargingSet(null);
		driverInstanceData.setDiameterChargingDriverSet(null);
		driverInstanceData.setWebServiceAuthDriverSet(null);
		driverInstanceData.setHttpAuthFieldMapSet(null);
		driverInstanceData.setUserFileDetail(null);
		driverInstanceData.setCrestelRatingSet(null);
		driverInstanceData.setCrestelChargingSet(crestelChargingDriverDataSet);
		
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
		driverInstance.setDriverTypeId(DriverTypeConstants.CRESTEL_CHARGING_DRIVER);
		driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
		
		CrestelChargingDriverData crestelChargingDriverData = (CrestelChargingDriverData)driverInstance.getCrestelChargingSet().iterator().next();

		driverBlManager.createCrestelChargingDriver(crestelChargingDriverData, driverInstance, staffData);
		return Response.ok(RestUtitlity.getResponse("Crestel Charging Driver created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<DriverInstanceData> list = driverInstanceList.getList();
		List<ResultObject> resultObjectList = new java.util.ArrayList<ResultObject>();
		
		for(DriverInstanceData driverInstance : list){
			
			driverInstance.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstance.setDriverTypeId(DriverTypeConstants.CRESTEL_CHARGING_DRIVER);
			driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
			
			CrestelChargingDriverData crestelChargingDriverData = (CrestelChargingDriverData)driverInstance.getCrestelChargingSet().iterator().next();
			
			ResultObject resultObject = new ResultObject();
			resultObject.setCrestelChargingDriverData(crestelChargingDriverData);
			resultObject.setDriverInstance(driverInstance);
			resultObjectList.add(resultObject);
		}

		Map<String, List<Status>> responseMap = driverBlManager.createCrestelChargingDriver(resultObjectList, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(CRESTEL_CHARGING_DRIVER, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String name) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DriverBLManager driverBlManager = new DriverBLManager();

		CrestelChargingDriverData updatedCrestelChargingDriverData = (CrestelChargingDriverData)driverInstance.getCrestelChargingSet().iterator().next(); 
		
		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		CrestelChargingDriverData crestleChargingDriverData = driverBlManager.getCrestelChargingDriverData(driverInstanceData.getDriverInstanceId());
		
		updatedCrestelChargingDriverData.setDriverInstanceId(crestleChargingDriverData.getDriverInstanceId());
		updatedCrestelChargingDriverData.setCrestelChargingDriverId(crestleChargingDriverData.getCrestelChargingDriverId());
		
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		driverBlManager.updateCrestelChargingDriverDataByName(driverInstance, updatedCrestelChargingDriverData, staffData, name);
		
		return Response.ok(RestUtitlity.getResponse("Crestel Charging Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData,@PathParam(value = "name") String name) throws DataManagerException {
		return updateByQueryParam(driverInstanceData,name);
	}

	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData, driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("Crestel Charging Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.CRESTEL_CHARGING);
	}
}
