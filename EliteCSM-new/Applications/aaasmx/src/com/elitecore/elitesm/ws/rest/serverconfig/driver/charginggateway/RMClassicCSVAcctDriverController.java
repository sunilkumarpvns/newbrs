package com.elitecore.elitesm.ws.rest.serverconfig.driver.charginggateway;

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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAttrRelationData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVStripPattRelData;
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
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class RMClassicCSVAcctDriverController {
	private static final String RM_CLASSIC_CSV_ACCT_DRIVER = "RM Classic CSV Acct Driver";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name)
	throws DataManagerException {
		DriverBLManager driverBLManager = new DriverBLManager();

		DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(name);
		
		ClassicCSVAcctDriverData classicCSVAcctDriverData = driverBLManager.getClassicCsvDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		Set<ClassicCSVAcctDriverData> classicCSVAcctDriverDataSet = new HashSet<ClassicCSVAcctDriverData>();
		classicCSVAcctDriverData.setPassword(null);
		classicCSVAcctDriverDataSet.add(classicCSVAcctDriverData);

		driverInstanceData.setDbdetail(null);
		driverInstanceData.setDbacctset(null);
		driverInstanceData.setCsvset(null);
		driverInstanceData.setMapGatewaySet(null);
		driverInstanceData.setHssDetail(null);
		driverInstanceData.setLdapdetail(null);
		driverInstanceData.setDetaillocalset(null);
		driverInstanceData.setWebServiceAuthDriverSet(null);
		driverInstanceData.setHttpAuthFieldMapSet(null);
		driverInstanceData.setUserFileDetail(null);
		driverInstanceData.setCrestelRatingSet(null);
		driverInstanceData.setCrestelChargingSet(null);
		driverInstanceData.setDiameterChargingDriverSet(null);
		
		driverInstanceData.setCsvset(classicCSVAcctDriverDataSet);
		
		return Response.ok(driverInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParam(@PathParam(value = "name") String driverName) throws DataManagerException{
		return getByQueryParam(driverName);
	}
	
	@POST
	public Response create(@Valid DriverInstanceData driverInstanceData) throws DataManagerException,
		   NoSuchEncryptionException, EncryptionFailedException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		ResultObject resultObject = new ResultObject();
		
			driverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstanceData.setDriverTypeId(DriverTypeConstants.RM_CLASSICCSV_ACCT_DRIVER);
			driverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
			
			ClassicCSVAcctDriverData classicCSVAcctDriverData = (ClassicCSVAcctDriverData) driverInstanceData.getCsvset().iterator().next();
			
			resultObject.setClassicCSVAcctDriverData(classicCSVAcctDriverData);
			resultObject.setDriverInstance(driverInstanceData);
			
			String encryptedPassword = PasswordEncryption.getInstance().crypt(classicCSVAcctDriverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			classicCSVAcctDriverData.setPassword(encryptedPassword);
			
		driverBlManager.createClassicCSVAcctDriver(classicCSVAcctDriverData, driverInstanceData, staffData);
		return Response.ok(RestUtitlity.getResponse("RM Classic CSV Acct Driver created successfully")).build();
	}
		
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceDataList,@Context UriInfo uri) throws DataManagerException,
		   NoSuchEncryptionException, EncryptionFailedException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<DriverInstanceData> list = driverInstanceDataList.getList();
		List<ResultObject> resultObjectList = new java.util.ArrayList<ResultObject>();
		
		for(DriverInstanceData driverInstanceData : list){
			driverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstanceData.setDriverTypeId(DriverTypeConstants.RM_CLASSICCSV_ACCT_DRIVER);
			driverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
			
			ClassicCSVAcctDriverData classicCSVAcctDriverData = (ClassicCSVAcctDriverData) driverInstanceData.getCsvset().iterator().next();
			
			ResultObject resultObject = new ResultObject();
			resultObject.setClassicCSVAcctDriverData(classicCSVAcctDriverData);
			resultObject.setDriverInstance(driverInstanceData);
			resultObjectList.add(resultObject);
			
			String encryptedPassword = PasswordEncryption.getInstance().crypt(classicCSVAcctDriverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			classicCSVAcctDriverData.setPassword(encryptedPassword);
			
		}
		Map<String, List<Status>> responseMap = driverBlManager.createClassicCSVAcctDriver(resultObjectList,  staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(RM_CLASSIC_CSV_ACCT_DRIVER, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String name) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DriverBLManager driverBlManager = new DriverBLManager();

		ClassicCSVAcctDriverData updatedClassicCSVAcctDriverData = driverInstance.getCsvset().iterator().next(); 
		
		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		ClassicCSVAcctDriverData classicCSVAcctDriverData = driverBlManager.getClassicCsvDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		
		if(updatedClassicCSVAcctDriverData.getCsvAttrRelList() != null){
			List<ClassicCSVAttrRelationData> mappingList = new ArrayList<ClassicCSVAttrRelationData>(updatedClassicCSVAcctDriverData.getCsvAttrRelList());
			updatedClassicCSVAcctDriverData.setMappingList(mappingList);
		}
		if(updatedClassicCSVAcctDriverData.getCsvPattRelList() != null){
			List<ClassicCSVStripPattRelData> stripMappingList = new ArrayList<ClassicCSVStripPattRelData>(updatedClassicCSVAcctDriverData.getCsvPattRelList());
			updatedClassicCSVAcctDriverData.setStripMappingList(stripMappingList);
		}
		
		updatedClassicCSVAcctDriverData.setDriverInstanceId(classicCSVAcctDriverData.getDriverInstanceId());
		updatedClassicCSVAcctDriverData.setClassicCsvId(classicCSVAcctDriverData.getClassicCsvId());
		
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		driverBlManager.updateRadiusClassicCSVAcctDriverByName(driverInstance, updatedClassicCSVAcctDriverData, staffData, name);
		return Response.ok(RestUtitlity.getResponse("RM Classic CSV Acct Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData,@PathParam(value = "name") String name)  throws DataManagerException {
		return updateByQueryParam(driverInstanceData,name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData, driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("RM Classic CSV Acct Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.RM_CLASSICCSV_ACCT_DRIVER);
	}

}
