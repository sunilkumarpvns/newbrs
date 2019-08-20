package com.elitecore.elitesm.ws.rest.serverconfig.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

@Path("/")
public class ServerController {

	private static final String ARGU_TYPE_STRING = "java.lang.String";
	private static final String JAVA_HOME = "javaHome";
	private static final String SERVER_HOME = "serverHome";
	private static final String MODULE = ConfigConstant.SERVER;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String serverName) throws DataManagerException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverName);
		
		//set the staff user from staffrelation table to instace.
		NetServerStaffRelDetailData instanceStaffRelationData = netServerBLManager.getInstanceStaffRelationDataByName(serverName);
		netServerInstanceData.setStaff(instanceStaffRelationData.getStaffUser());
		
		Set<String> servicesNames = netServerBLManager.getListOfServicesNames(netServerBLManager.getListOfServicesByServerId(netServerInstanceData.getNetServerId()));
		
		if(Collectionz.isNullOrEmpty(servicesNames) == false){
			
			List<String> services = new ArrayList<String>(servicesNames);
			
			NetServiceData servicesData= new NetServiceData();
			servicesData.setServicesNames(services);
			
			netServerInstanceData.setServicesListForView(servicesData);
		}
		
		return Response.ok(netServerInstanceData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String name) throws DataManagerException {
		return getByNameFromQuery(name);
	}
	
	@POST
	public Response create(@Valid NetServerInstanceData serverInstanceData)
			throws DataManagerException,CommunicationException, NoSuchEncryptionException, EncryptionFailedException, UnidentifiedServerInstanceException, ConnectException, DecryptionNotSupportedException, DecryptionFailedException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager serviceBLManager = new NetServiceBLManager();
			
		IRemoteCommunicationManager remoteCommunicationManager = null;
		
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		
		Object[] objArgValues = {};
		String[] strArgTypes = {};
		remoteCommunicationManager.init(serverInstanceData.getAdminHost(), serverInstanceData.getAdminPort(), null, false);
		String serverHome = (String) remoteCommunicationManager.execute(
				MBeanConstants.CONFIGURATION, SERVER_HOME, objArgValues, strArgTypes);
		String javaHome = (String) remoteCommunicationManager.execute(
				MBeanConstants.CONFIGURATION, JAVA_HOME, objArgValues, strArgTypes);
		
		
		INetServerTypeData serverTypeData = netServerBLManager.getNetServerType(serverInstanceData.getNetServerTypeId());
		
		serverInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
		serverInstanceData.setNetServerCode(serverInstanceData.getNetServerTypeId());
		serverInstanceData.setVersion(serverTypeData.getVersion());
		serverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
		serverInstanceData.setCreatedByStaffId(staffData.getStaffId());
		serverInstanceData.setServerHome(serverHome);
		serverInstanceData.setJavaHome(javaHome);
		serverInstanceData.setIsInSync(BaseConstant.HIDE_STATUS_ID);
		serverInstanceData.setSystemGenerated(BaseConstant.NOT_A_SYSTEMRECORD);
		
		NetServiceData servicesData = new NetServiceData();
		servicesData = serverInstanceData.getServicesListForAdd();
		
		List<String> configuredServices = new ArrayList<String>();
		if(servicesData != null){
			configuredServices =  servicesData.getServicesNames();
		}
		
		if(Collectionz.isNullOrEmpty(configuredServices) == false){
			List<INetServiceInstanceData> serviceInstaceDataList = new ArrayList<INetServiceInstanceData>();

			for( String serviceName : configuredServices){
				if(Strings.isNullOrBlank(serviceName) == false) {
						
					INetServiceTypeData serviceTypeData = serviceBLManager.getNetServiceType(serviceName);
					INetServiceInstanceData instanceData = getServiceInstanceData(serviceTypeData, staffData.getStaffId());
					serviceInstaceDataList.add(instanceData);
				}	
			}
			serverInstanceData = (NetServerInstanceData) netServerBLManager.createServerInstance(serverInstanceData,serviceInstaceDataList,staffData);
		}else{
			serverInstanceData = (NetServerInstanceData) netServerBLManager.createServerInstance(serverInstanceData,staffData);
		}
		
		NetServerStaffRelDetailData data = new NetServerStaffRelDetailData();
		
		data.setName(serverInstanceData.getName());
		data.setStaffUser(serverInstanceData.getStaff());
		netServerBLManager.createInstanceStaffRelationData(data);
		
		String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
		String netServerName = PasswordEncryption.getInstance().crypt(serverInstanceData.getName(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
		
		Object[] argValues = { netServerCode, netServerName };
		String[] argTypes = { ARGU_TYPE_STRING, ARGU_TYPE_STRING };
		remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,  "writeServerInstanceDetails", argValues, argTypes);
		
		StaffBLManager staffBLManager = new StaffBLManager();
       	String username = data.getStaffUser();
		StaffData staffDetailData = (StaffData) staffBLManager.getStaffDataByUserName(username); 
        String password = PasswordEncryption.getInstance().decrypt(staffDetailData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
        Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
        AuthenticationDetails authenticationDetails = (AuthenticationDetails) obj;
        
        Object[] wsArgValues = {username, password, authenticationDetails.getLocalAddress(), authenticationDetails.getLocalPort(),authenticationDetails.getContextPath()};
        String[] wsArgTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.Integer","java.lang.String"};
        remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeWebServiceDetail",wsArgValues,wsArgTypes);
        remoteCommunicationManager.close();

		
		LogManager.getLogger().info(MODULE, serverInstanceData.toString());

		return Response.ok(RestUtitlity.getResponse(serverInstanceData.getName() +" server created successfully")).build();

	}
	
	private INetServiceInstanceData getServiceInstanceData(INetServiceTypeData serviceTypeData, String staffId) {
	
		INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
		
		netServiceInstanceData.setCreatedByStaffId(staffId);
		netServiceInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
		netServiceInstanceData.setNetServiceTypeId(serviceTypeData.getNetServiceTypeId());
		netServiceInstanceData.setName(serviceTypeData.getName());
		netServiceInstanceData.setDisplayName(serviceTypeData.getName());
		netServiceInstanceData.setDescription(serviceTypeData.getDescription());
		netServiceInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
		netServiceInstanceData.setInstanceId("000");
		netServiceInstanceData.setIsInSync(BaseConstant.HIDE_STATUS_ID);
		netServiceInstanceData.setSystemGenerated(BaseConstant.NOT_A_SYSTEMRECORD);
		netServiceInstanceData.setLastModifiedDate(null);
		netServiceInstanceData.setLastModifiedByStaffId(null);
		netServiceInstanceData.setStatusChangeDate(null);
		netServiceInstanceData.setLastSyncDate(null);
		netServiceInstanceData.setLastSuccessSynDate(null);
		netServiceInstanceData.setLastSyncStatus(null);

		return netServiceInstanceData;
	}

	@DELETE
	public Response	deleteServerInstanceByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceNames) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		List<String> serverInstanceNameslst = Arrays.asList(serverInstanceNames.split(","));
		netServerBLManager.deleteByName(serverInstanceNameslst, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Server Instance(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteServerInstanceByPathParam(@PathParam(value = "name") String serverInstanceName) throws DataManagerException {
		return deleteServerInstanceByQueryParam(serverInstanceName);
	}
	
	@PUT
	public Response updateByQueryParam(@Valid NetServerInstanceData serverInstanceData,@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, DecryptionNotSupportedException, DecryptionFailedException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager serviceBLManager = new NetServiceBLManager();
		
		IRemoteCommunicationManager remoteCommunicationManager = null;
		
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		
		Object[] objArgValues = {};
		String[] strArgTypes = {};
		remoteCommunicationManager.init(serverInstanceData.getAdminHost(), serverInstanceData.getAdminPort(), null, false);
		String serverHome = (String) remoteCommunicationManager.execute(
				MBeanConstants.CONFIGURATION, SERVER_HOME, objArgValues, strArgTypes);
		String javaHome = (String) remoteCommunicationManager.execute(
				MBeanConstants.CONFIGURATION, JAVA_HOME, objArgValues, strArgTypes);
		
		serverInstanceData.setServerHome(serverHome);
		serverInstanceData.setJavaHome(javaHome);
		
		String staffName = serverInstanceData.getStaff();
		NetServiceData servicesDataForUpdate = new NetServiceData();
		NetServiceData servicesDataForDelete = new NetServiceData();
		
		servicesDataForUpdate = serverInstanceData.getServicesListForAdd();
		servicesDataForDelete = serverInstanceData.getServiceListForDelete();
		
		String instanceName = serverInstanceData.getName();
		
		List<String> configuredServicesForDelete = new ArrayList<String>();
		List<String> configuredServicesForUpdate = new ArrayList<String>();
		if(servicesDataForUpdate != null){
			 configuredServicesForUpdate =  servicesDataForUpdate.getServicesNames();
		}
		
		if(servicesDataForDelete != null){
			configuredServicesForDelete = servicesDataForDelete.getServicesNames();
		}
		
		if(Collectionz.isNullOrEmpty(configuredServicesForUpdate) == false || Collectionz.isNullOrEmpty(configuredServicesForDelete) == false){
			
			List<INetServiceInstanceData> serviceInstaceDataListForUpdate = new ArrayList<INetServiceInstanceData>();
			if(Collectionz.isNullOrEmpty(configuredServicesForUpdate) == false){
				//for update services list
				for(String serviceName : configuredServicesForUpdate){
					if(Strings.isNullOrBlank(serviceName) == false) {
					
						INetServiceTypeData serviceTypeData = serviceBLManager.getNetServiceType(serviceName);
						INetServiceInstanceData instanceData = getServiceInstanceData(serviceTypeData, staffData.getStaffId());
						serviceInstaceDataListForUpdate.add(instanceData);
					}
					
				}
			}
			
			//delete services
			List<INetServiceInstanceData> serviceInstaceDataListForDelete = new ArrayList<INetServiceInstanceData>();
			if(Collectionz.isNullOrEmpty(configuredServicesForDelete) == false){
				for(String serviceName : configuredServicesForDelete){
					if(Strings.isNullOrBlank(serviceName) == false) {
						INetServiceTypeData serviceTypeData = serviceBLManager.getNetServiceType(serviceName);
						INetServiceInstanceData instanceData = getServiceInstanceData(serviceTypeData, staffData.getStaffId());
						serviceInstaceDataListForDelete.add(instanceData);
					}
					
				}
			}
			serverInstanceData = (NetServerInstanceData) netServerBLManager.updateServerInstanceByName(serverInstanceData,serviceInstaceDataListForUpdate,serviceInstaceDataListForDelete,serverInstanceName,staffData);
		}else{
			serverInstanceData = (NetServerInstanceData) netServerBLManager.updateServerInstanceByName(serverInstanceData,null,null,serverInstanceName,staffData);
		}
		
		String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
		String netServerName = PasswordEncryption.getInstance().crypt(serverInstanceData.getName(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
		
		Object[] argValues = { netServerCode, netServerName };
		String[] argTypes = { ARGU_TYPE_STRING, ARGU_TYPE_STRING };
		remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,  "writeServerInstanceDetails", argValues, argTypes);
		
		
		//For update the staff relation detail 
		NetServerStaffRelDetailData data = netServerBLManager.getInstanceStaffRelationDataByName(instanceName);
		data.setName(instanceName);
        data.setStaffUser(staffName);
        netServerBLManager.updateInstanceStaffRelationData(data);
         
     	StaffBLManager staffBLManager = new StaffBLManager();
    	String username = data.getStaffUser();
		StaffData staffDetailData = (StaffData) staffBLManager.getStaffDataByUserName(username); 
		String password = PasswordEncryption.getInstance().decrypt(staffDetailData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
         
		Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
        AuthenticationDetails authenticationDetails = (AuthenticationDetails) obj;
		
         Object[] wsArgValues = {username, password, authenticationDetails.getLocalAddress(), authenticationDetails.getLocalPort(),authenticationDetails.getContextPath()};
         String[] wsArgTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.Integer","java.lang.String"};
         remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeWebServiceDetail",wsArgValues,wsArgTypes);
         remoteCommunicationManager.close();
		
		return Response.ok(RestUtitlity.getResponse("Server Instance updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid NetServerInstanceData serverInstanceData,@PathParam(value="name")String serverInstanceName)
			throws DataManagerException, ConnectException, NoSuchEncryptionException, EncryptionFailedException, DecryptionNotSupportedException, DecryptionFailedException{
		return updateByQueryParam(serverInstanceData,serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getServerInstanceHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.SERVER_INSTANCE);
	}
}
