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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class DiameterUserFileAuthDriverController {
	private static final String DIAMETER_USER_FILE_AUTH_DRIVER = "Diameter User File Auth Driver";

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String name) throws DataManagerException{
		DriverBLManager driverBLManager = new DriverBLManager();
		
		DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(name);

		UserFileAuthDriverData userFileDriverData = driverBLManager.getUserFileDriverByDriverInstanceId(driverInstanceData.getDriverInstanceId());
		
		Set<UserFileAuthDriverData> userFileDriverDataSet = new HashSet<UserFileAuthDriverData>();
		
		userFileDriverDataSet.add(userFileDriverData);
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
		
		driverInstanceData.setUserFileDetail(userFileDriverDataSet);
		
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
		driverInstance.setDriverTypeId(DriverTypeConstants.DIAMETER_USERFILE_AUTH_DRIVER);
		
		driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
		
		UserFileAuthDriverData userFileAuthDriverData = (UserFileAuthDriverData)driverInstance.getUserFileDetail().iterator().next();
		
		CreateDriverConfig driverConfig = new CreateDriverConfig();
		driverConfig.setDriverInstanceData(driverInstance);
		driverConfig.setUserFileAuthDriverData(userFileAuthDriverData);

		
		driverBlManager.createUserFileAuthDriver(driverConfig, staffData);
		return Response.ok(RestUtitlity.getResponse("Diameter User File Auth Driver created successfully")).build();
	}
	
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DriverInstanceData> driverInstanceDataList,@Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<DriverInstanceData> list = driverInstanceDataList.getList();
		List<CreateDriverConfig> driverConfigList = new ArrayList<CreateDriverConfig>();
	
		for(DriverInstanceData driverInstance : list){
			
			driverInstance.setStatus(BaseConstant.SHOW_STATUS_ID);
			driverInstance.setDriverTypeId(DriverTypeConstants.DIAMETER_USERFILE_AUTH_DRIVER);
			
			driverInstance.setCreatedByStaffId(staffData.getStaffId());        	
			driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
			driverInstance.setCreateDate(new Timestamp(new Date().getTime()));
			
			UserFileAuthDriverData userFileAuthDriverData = (UserFileAuthDriverData)driverInstance.getUserFileDetail().iterator().next();
			
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			driverConfig.setDriverInstanceData(driverInstance);
			driverConfig.setUserFileAuthDriverData(userFileAuthDriverData);

			driverConfigList.add(driverConfig);
		}
		
		Map<String, List<Status>> responseMap = driverBlManager.createUserFileAuthDriver(driverConfigList, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_USER_FILE_AUTH_DRIVER,"(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DriverInstanceData driverInstance,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)  @QueryParam(value = "name") String name) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DriverBLManager driverBlManager = new DriverBLManager();
		
		UserFileAuthDriverData userFileAuthDriverData = (UserFileAuthDriverData) driverInstance.getUserFileDetail().iterator().next();
		
		driverInstance.setLastModifiedByStaffId(staffData.getStaffId());
		driverInstance.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		driverBlManager.updateUserFileAuthDriverByName(driverInstance, userFileAuthDriverData, staffData , name);

		return Response.ok(RestUtitlity.getResponse("Diameter User File Auth Driver updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DriverInstanceData driverInstanceData, 
			@PathParam(value = "name") String name) 
			throws DuplicateInstanceNameFoundException, DataManagerException, JAXBException {
		return updateByQueryParam(driverInstanceData,name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String driverInstanceName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DriverBLManager driverBlManager = new DriverBLManager();
		
		List<String> driverInstanceNameList = Arrays.asList(driverInstanceName.split(","));
		
		driverBlManager.deleteByName(driverInstanceNameList, staffData,driverInstanceNameList);
		return Response.ok(RestUtitlity.getResponse("Diameter User File Auth Driver(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteByQueryParam(name);
	}
	
	@GET	
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException{
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_USER_FILE_AUTH);
	}

	
}
