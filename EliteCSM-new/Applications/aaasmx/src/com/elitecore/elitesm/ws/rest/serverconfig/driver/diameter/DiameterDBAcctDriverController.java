package com.elitecore.elitesm.ws.rest.serverconfig.driver.diameter;

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
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.aaa.util.constants.RadiusDBAcctDriverConstant;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterDBAcctDriverController {

	private static final String DIAMETER_DB_ACCT_DRIVER = "Diameter DB Acct Driver";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name)
	throws DataManagerException {
		DriverBLManager driverBlManager = new DriverBLManager();

		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		DBAcctDriverData dbAcctDriver = driverBlManager.getDbAcctDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		Set<DBAcctDriverData> dbAcctDriverDataSet = new HashSet<DBAcctDriverData>();
		dbAcctDriver.setStoreAllCdr(null);
		dbAcctDriverDataSet.add(dbAcctDriver);

		driverInstanceData.setUserFileDetail(null);
		driverInstanceData.setCsvset(null);
		driverInstanceData.setDbdetail(null);
		driverInstanceData.setMapGatewaySet(null);
		driverInstanceData.setHssDetail(null);
		driverInstanceData.setLdapdetail(null);
		driverInstanceData.setDetaillocalset(null);
		driverInstanceData.setCrestelRatingSet(null);
		driverInstanceData.setCrestelChargingSet(null);
		driverInstanceData.setDiameterChargingDriverSet(null);
		driverInstanceData.setWebServiceAuthDriverSet(null);
		driverInstanceData.setHttpAuthFieldMapSet(null);
		
		driverInstanceData.setDbacctset(dbAcctDriverDataSet);
		
		return Response.ok(driverInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParam(@PathParam(value = "name") String driverName) throws DataManagerException{
		return getByQueryParam(driverName);
	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceDataList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<DriverInstanceData> list = driverInstanceDataList.getList();
		List<ResultObject> resultObjectList = new java.util.ArrayList<ResultObject>();
		
		for(DriverInstanceData driverInstanceData : list){
			driverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstanceData.setDriverTypeId(DriverTypeConstants.DIAMETER_DB_ACCT_DRIVER);
			driverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
			
			DBAcctDriverData dbAcctDriverData = (DBAcctDriverData) driverInstanceData.getDbacctset().iterator().next();
			dbAcctDriverData.setInterimTablename(RadiusDBAcctDriverConstant.DIAMETER_TABLE_NAME);
			dbAcctDriverData.setInterimCdrIdSeqName(RadiusDBAcctDriverConstant.DIAMETER_SEQUENCE_NAME);
			
			ResultObject resultObject = new ResultObject();
			resultObject.setDbAcctDriverData(dbAcctDriverData);
			resultObject.setDriverInstance(driverInstanceData);
			resultObjectList.add(resultObject);
		}
		
		Map<String, List<Status>> responseMap = driverBlManager.createDBAcctDriver(resultObjectList,  staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_DB_ACCT_DRIVER, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid DriverInstanceData driverInstanceData) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		driverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
		driverInstanceData.setDriverTypeId(DriverTypeConstants.DIAMETER_DB_ACCT_DRIVER);
		driverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
		driverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		driverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
		
		DBAcctDriverData dbAcctDriverData = (DBAcctDriverData) driverInstanceData.getDbacctset().iterator().next();
		dbAcctDriverData.setInterimTablename(RadiusDBAcctDriverConstant.DIAMETER_TABLE_NAME);
		dbAcctDriverData.setInterimCdrIdSeqName(RadiusDBAcctDriverConstant.DIAMETER_SEQUENCE_NAME);
		
		driverBlManager.createDBAcctDriver(dbAcctDriverData, driverInstanceData, staffData);
		return Response.ok(RestUtitlity.getResponse("Diameter DB Acct Driver created successfully")).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String name) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DriverBLManager driverBlManager = new DriverBLManager();

		DBAcctDriverData updatedDBAcctDriver  = driverInstance.getDbacctset().iterator().next(); 
		updatedDBAcctDriver.setInterimTablename(RadiusDBAcctDriverConstant.DIAMETER_TABLE_NAME);
		updatedDBAcctDriver.setInterimCdrIdSeqName(RadiusDBAcctDriverConstant.DIAMETER_SEQUENCE_NAME);
		
		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		DBAcctDriverData dbAcctDriverData = driverBlManager.getDbAcctDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		
		updatedDBAcctDriver.setOpenDbAcctId(dbAcctDriverData.getOpenDbAcctId());
		updatedDBAcctDriver.setDriverInstanceId(dbAcctDriverData.getDriverInstanceId());
		
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		try{
			driverBlManager.updateDBAcctDriverByName(driverInstance, updatedDBAcctDriver, staffData, name);
		}catch (DatabaseConnectionException dc) {
			return Response.ok(RestUtitlity.getResponse("Diameter DB Acct Driver Updated Successfully. Database connection is not found")).build();
		}
		return Response.ok(RestUtitlity.getResponse("Diameter DB Acct Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData,@PathParam(value = "name") String name) 
			throws DuplicateInstanceNameFoundException, DataManagerException, JAXBException {
		return updateByQueryParam(driverInstanceData, name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData, driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("Diameter DB Acct Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_DB_ACCT);
	}
	

	
}