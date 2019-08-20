package com.elitecore.elitesm.ws.rest.ldapdatasource;

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
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
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
public class LDAPDataSourceController {

private static final String LDAP_DATASOURCE = "LDAP Datasource";
private static final String MODULE = ConfigConstant.LDAP_DATASOURCE;

	@GET
	public Response getByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String ldapDatasource) throws DataManagerException {

		LDAPDatasourceBLManager ldapDataSourceBLManager = new LDAPDatasourceBLManager();
		
		LDAPDatasourceData databaseDSData = (LDAPDatasourceData) ldapDataSourceBLManager.getLDAPDatabaseDataByName(ldapDatasource);
		
		List<LDAPBaseDnDetailData> listOfBaseDnDetail = ldapDataSourceBLManager.getLDAPBaseBnDetailByLdapId(databaseDSData.getLdapDsId());
		
		databaseDSData.setSearchDnDetailList(listOfBaseDnDetail);
		
		databaseDSData.setPassword(null);
		
		return Response.ok(databaseDSData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParam(@PathParam(value = "name") String ldapDatasourceName) throws DataManagerException {
		return getByQueryParam(ldapDatasourceName);
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<LDAPDatasourceData> ldapDatasourceDataList, @Context UriInfo uri) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
		List<LDAPDatasourceData> ldapDatasourceDatas = new ArrayList<LDAPDatasourceData>();
		List<LDAPDatasourceData> ldapDSList = ldapDatasourceDataList.getList();
		
		for (LDAPDatasourceData ldapDatasourceData : ldapDSList) {
			
			ldapDatasourceData.setCreatedByStaffId(staffData.getStaffId());
			ldapDatasourceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			
			ldapDatasourceData.setLdapBaseDnDetail(null);
			
			ldapDatasourceData.setPassword(ldapDatasourceBLManager.encryptPassword(ldapDatasourceData.getPassword()));
			ldapDatasourceDatas.add(ldapDatasourceData);
		}
		
		Map<String, List<Status>> responseMap = ldapDatasourceBLManager.create(ldapDatasourceDatas,staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(LDAP_DATASOURCE, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid LDAPDatasourceData ldapDatasourceData) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

		ldapDatasourceData.setCreatedByStaffId(staffData.getStaffId());
		ldapDatasourceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		ldapDatasourceData.setLdapBaseDnDetail(null);
		
		ldapDatasourceData.setPassword(ldapDatasourceBLManager.encryptPassword(ldapDatasourceData.getPassword()));

		ldapDatasourceBLManager.create(ldapDatasourceData,staffData);

		LogManager.getLogger().info(MODULE, "LDAP Datasource created successfully");

		return Response.ok(RestUtitlity.getResponse("LDAP Datasource created successfully")).build();
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String ldapDSName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

		List<String> datasourceNames = Arrays.asList(ldapDSName.split(","));
		
		ldapDatasourceBLManager.deleteByName(datasourceNames, staffData);
		return Response.ok(RestUtitlity.getResponse("LDAP Datasource(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String ldapDSName) throws DataManagerException {
		return deleteByQueryParam(ldapDSName);
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateByPathParam(@Valid LDAPDatasourceData ldapDatasourceData, @PathParam(value="name") String ldapDSName) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		return updateByQueryParam(ldapDatasourceData, ldapDSName);
	}
	
	@PUT
	public Response updateByQueryParam(@Valid LDAPDatasourceData ldapDatasource,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value="name") String ldapDSName) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

		ldapDatasource.setPassword(ldapDatasourceBLManager.encryptPassword(ldapDatasource.getPassword()));

		ldapDatasource.setLastModifiedByStaffId(staffData.getStaffId());
		ldapDatasource.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));

		ldapDatasourceBLManager.updateLDAPDatasourceDataByName(ldapDatasource,staffData,ldapDSName);
		LogManager.getLogger().info(MODULE, ldapDatasource.toString());
		
		return Response.ok(RestUtitlity.getResponse("LDAP Datasource updated successfully")).build();
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.LDAP_DATASOURCE);
	}
}
