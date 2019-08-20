package com.elitecore.elitesm.ws.rest.datasource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

@Path("/")
public class DatabaseDataSourceController {

	private static final String DATABASE_DATASOURCE = "Database Datasource";
	private static final String MODULE = ConfigConstant.DATABASE_DATASOURCE;
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String datasourceName) throws DataManagerException {

		DatabaseDSBLManager dataManager = new DatabaseDSBLManager();
		DatabaseDSData databaseDSData = (DatabaseDSData) dataManager.getDatabaseDSDataByName(datasourceName);
		
		databaseDSData.setPassword(null);

		return Response.ok(databaseDSData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String datasourceName) throws DataManagerException {
		return getByNameFromQuery(datasourceName);
	}
	
	@POST
	public Response create(@Valid DatabaseDSData databaseDSData) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DatabaseDSBLManager blManager = new DatabaseDSBLManager();
		
		databaseDSData.setCreatedByStaffId(staffData.getStaffId());
		databaseDSData.setLastmodifiedByStaffId(staffData.getStaffId());
		databaseDSData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		databaseDSData.setLastmodifiedDate(new Timestamp(System.currentTimeMillis()));
		
		databaseDSData.setPassword(blManager.encryptPassword(databaseDSData.getPassword()));
		
		blManager.create(databaseDSData,staffData);
		return Response.ok(RestUtitlity.getResponse("Database Datasource created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DatabaseDSData> datasources,@Context UriInfo uri) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DatabaseDSBLManager blManager = new DatabaseDSBLManager();

		List<DatabaseDSData> list = datasources.getList();
		List<IDatabaseDSData> iDatabaseDSDataList = new ArrayList<IDatabaseDSData>();
		
		for (DatabaseDSData databaseDSData : list) {
			
			databaseDSData.setCreatedByStaffId(staffData.getStaffId());
			databaseDSData.setLastmodifiedByStaffId(staffData.getStaffId());
			databaseDSData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			databaseDSData.setLastmodifiedDate(new Timestamp(System.currentTimeMillis()));
			
			databaseDSData.setPassword(blManager.encryptPassword(databaseDSData.getPassword()));
			
			iDatabaseDSDataList.add(databaseDSData);
		}
		
		Map<String, List<Status>> responseMap = blManager.create(iDatabaseDSDataList,staffData,URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DATABASE_DATASOURCE,"(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DatabaseDSData databaseDSData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam(value = "name") String datasourceName) 
			throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException{
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DatabaseDSBLManager blManager = new DatabaseDSBLManager();
		
		databaseDSData.setCreatedByStaffId(staffData.getStaffId());
		databaseDSData.setLastmodifiedByStaffId(staffData.getStaffId());
		databaseDSData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		databaseDSData.setLastmodifiedDate(new Timestamp(System.currentTimeMillis()));
		
		databaseDSData.setPassword(blManager.encryptPassword(databaseDSData.getPassword()));

		blManager.updateDatabaseDSDetailByName(databaseDSData, staffData, datasourceName);

		LogManager.getLogger().info(MODULE, databaseDSData.toString());
		return Response.ok(RestUtitlity.getResponse("Database Datasource updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DatabaseDSData databaseDSData,
			@PathParam(value = "name") String name) 
			throws DuplicateInstanceNameFoundException, DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		return updateByQueryParam(databaseDSData,name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String datasourceName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DatabaseDSBLManager dataManager = new DatabaseDSBLManager();

		List<String> datasourceNames = Arrays.asList(datasourceName.split(","));
		
		dataManager.deleteDatabaseDSDetailByName(datasourceNames, staffData);
		return Response.ok(RestUtitlity.getResponse("Database Datasource(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String datasourceName) throws DataManagerException {
		return deleteByQueryParam(datasourceName);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DATABASE_DATASOURCE);
	}
}