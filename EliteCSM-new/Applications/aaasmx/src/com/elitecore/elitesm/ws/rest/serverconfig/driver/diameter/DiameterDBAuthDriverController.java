package com.elitecore.elitesm.ws.rest.serverconfig.driver.diameter;

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
import java.util.Map.Entry;

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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterDBAuthDriverController {

	private static final String DIAMETER_DB_AUTH_DRIVER = "Diameter DB Auth Driver";
	private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name)
	throws DataManagerException {
		DriverBLManager driverBLManager = new DriverBLManager();

		DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(name);
		
		Set<DBAuthDriverData> dbAuthDriverDataSet = new HashSet<DBAuthDriverData>();
		
		DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		dbAuthDriverData.setCacheable(null);

		List<DBAuthFieldMapData> dbAuthDriverDataTemp = dbAuthDriverData.getDbFieldMapList();
		for(DBAuthFieldMapData data :dbAuthDriverDataTemp ) {
			data.setLogicalName(data.getNameValuePoolData().getName());
		}
		
		dbAuthDriverDataSet.add(dbAuthDriverData);
		
		driverInstanceData.setUserFileDetail(null);
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
		
		driverInstanceData.setDbdetail(dbAuthDriverDataSet);
		
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
		driverInstance.setDriverTypeId(DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER);
		driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
		
		DBAuthDriverData dbAuthDriverData = (DBAuthDriverData)driverInstance.getDbdetail().iterator().next();
		driverInstance.setCacheable(null);
		
		CreateDriverConfig driverConfig = new CreateDriverConfig();
		driverConfig.setDriverInstanceData(driverInstance);
		driverConfig.setDbAuthDriverData(dbAuthDriverData);
		
		String errorMsg = driverBlManager.createDBDriver(driverConfig,staffData);
		
		if(errorMsg.equals(ConfigConstant.DATABASE_CONNECTION_FAILED)) {
			return Response.ok(RestUtitlity.getResponse("Diameter DB Auth Driver created successfully.Database connection is not found.Subscriber Profile Management will not work properly")).build();
		} else if (errorMsg.equals(ConfigConstant.TABLE_DOES_NOT_EXIST)) {
			return Response.ok(RestUtitlity.getResponse("Diameter DB Auth Driver created successfully.Subscriber Profiler Table [ "+dbAuthDriverData.getTableName()+" ] is not found.Subscriber Profile Management will not work properly")).build();
		} else {
			return Response.ok(RestUtitlity.getResponse("Diameter DB Auth Driver created successfully")).build();	
		}
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceDataList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();

		List<DriverInstanceData> list = driverInstanceDataList.getList();
		List<CreateDriverConfig> driverConfigList = new ArrayList<CreateDriverConfig>();
		
		for(DriverInstanceData driverInstance : list){
			driverInstance.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstance.setDriverTypeId(DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER);
			driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
			
			DBAuthDriverData dbAuthDriverData = (DBAuthDriverData)driverInstance.getDbdetail().iterator().next();
			driverInstance.setCacheable(null);
			
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			driverConfig.setDriverInstanceData(driverInstance);
			driverConfig.setDbAuthDriverData(dbAuthDriverData);
			
			driverConfigList.add(driverConfig);
		}
		
		String errorMessage = "" ;
		Map<String, List<Status>> responseMap = driverBlManager.createDBDriver(driverConfigList,staffData, URLInfo.isPartialSuccess(uri));

		for (Entry<String, List<Status>> map : responseMap.entrySet()) {
			if(map.getKey().equalsIgnoreCase(ERROR_MESSAGE)){
				errorMessage = map.getValue().get(0).getMessage();
			}
		}
		if(!errorMessage.equals(ConfigConstant.SUCCESS)){
			return Response.ok(RestUtitlity.getResponse(DIAMETER_DB_AUTH_DRIVER,
					"(s) created successfully.Either database connection is not found or Subscriber Profiler Table is not found. Subscriber Profile Management will not work properly.",
					responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
		} else {
			return Response.ok(RestUtitlity.getResponse(DIAMETER_DB_AUTH_DRIVER, "(s) created successfully", responseMap,
					URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
		}
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String name) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String message = "";
		
		DriverBLManager driverBlManager = new DriverBLManager();

		DBAuthDriverData updateDBAuthDriverData = driverInstance.getDbdetail().iterator().next();
		
		DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByName(name);
		
		DBAuthDriverData dbAuthDriverData = driverBlManager.getDBDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		driverInstance.setCacheable(null);
		
		updateDBAuthDriverData.setDriverInstanceId(dbAuthDriverData.getDriverInstanceId());
		updateDBAuthDriverData.setDbAuthId(dbAuthDriverData.getDbAuthId());
		try{
			if(updateDBAuthDriverData.getDatabaseId() != dbAuthDriverData.getDatabaseId() || 
					updateDBAuthDriverData.getTableName().equalsIgnoreCase(dbAuthDriverData.getTableName()) == false){
				
				List<IDatasourceSchemaData> datasourceSchemaList = driverBlManager.getSchemaList(updateDBAuthDriverData);
				Set<IDatasourceSchemaData> datasourceSchemaSet = new HashSet<IDatasourceSchemaData>();
			
				datasourceSchemaSet.addAll(datasourceSchemaList);
	
				updateDBAuthDriverData.setDatasourceSchemaSet(datasourceSchemaSet);
	
				DatabaseSubscriberProfileBLManager profileBLManager = new DatabaseSubscriberProfileBLManager();
				profileBLManager.updateDatabaseSubscribeProfileSchema(updateDBAuthDriverData);
			}
		} catch(DatabaseConnectionException e){
			message = "Diameter DB Auth Driver Updated Successfully.Database connection is not found. Subscriber Profile Management will not work properly";
		} catch (TableDoesNotExistException te) {
			message = "Diameter DB Auth Driver Updated Successfully.Subscriber Profiler Table [ "+updateDBAuthDriverData.getTableName()+" ] is not found. Subscriber Profile Management will not work properly";
		}	
			driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverBlManager.updateDBAuthDriverByName(driverInstance, updateDBAuthDriverData, staffData, name);
			
			if(Strings.isNullOrBlank(message) == false) {
				return Response.ok(RestUtitlity.getResponse(message)).build();	
			}
		
		return Response.ok(RestUtitlity.getResponse("Diameter DB Auth Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData,@PathParam(value = "name") String name) 
			throws DuplicateInstanceNameFoundException, DataManagerException, JAXBException {
		return updateByQueryParam(driverInstanceData,name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData, driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("Diameter DB Auth Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_DB_AUTH);
	}
}
