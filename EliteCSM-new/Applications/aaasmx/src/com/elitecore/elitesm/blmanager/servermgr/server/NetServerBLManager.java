package com.elitecore.elitesm.blmanager.servermgr.server;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.elitesm.blmanager.core.system.license.SMLicenseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.BaseServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.ValueOutOfRangeException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.VersionNotSupportedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerVersionData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStartupConfigData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewServerConfigOpFailedException;
import com.elitecore.elitesm.datamanager.servermgr.server.NetServerDataManager;
import com.elitecore.elitesm.datamanager.servermgr.service.NetServiceDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;
import com.elitecore.license.nfv.CentralizedLicenseCoordinator;

public class NetServerBLManager extends BaseServerBLManager {
    
    private static final String RESOURCE_MANAGER = "Resource Manager";
	private final static String MODULE = "NetServerBLManager";
    private final static String serviceName = "service-name";
    private final static String serviceId = "service-instance-id";
    private final static String ACTIVE_STATUS="CST01";
    private final static String ELITE_CSM_SERVER = "EliteCSM Server";
    
    /**
     * @author dhavalraval
     * @return Returns List
     * @throws DataManagerException
     * @purpose This method is generated to get the list of NetServerType
     */
    public List<NetServerTypeData> getNetServerTypeList( ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerTypeList();
        }
        catch (DataManagerException e) {
            throw e;
        }catch (Exception e) {
        	e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    /**
     * @author dhavalraval
     * @param netServerId
     * @return NetServerInstanceData object
     * @throws DataManagerException
     * @purpose This method is generated to get the object of
     *          INetServerInstanceData
     */
    public INetServerInstanceData getNetServerInstance( String netServerId ) throws DataManagerException {
        EliteAssert.greaterThanZero(netServerId, "netServerId Must be greater than zero.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            INetServerInstanceData netServerInstance = servermgrDataManager.getNetServerInstance(netServerId);
            
            NetServerStaffRelDetailData data = getInstanceStaffRelationDataByName(netServerInstance.getName());
            
            if(data != null ){
            	netServerInstance.setStaff(data.getStaffUser());
            }
			return netServerInstance;
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
    
    /**
     * @author dhavalraval
     * @param netConfigMapTypeId
     * @return NetServerConfigMapTypeData object
     * @throws DataManagerException
     * @purpose This method is generated to get the object of
     *          INetServerConfigMapTypeData
     */
    public INetServerConfigMapTypeData getNetServerConfigMapType( String netConfigMapTypeId ) throws DataManagerException {
        
        EliteAssert.notNull(netConfigMapTypeId, "netConfigMapTypeId Must be specified.");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            INetServerConfigMapTypeData netServerConfigMapTypeData = new NetServerConfigMapTypeData();
            netServerConfigMapTypeData.setNetConfigMapTypeId(netConfigMapTypeId);
            
            return servermgrDataManager.getNetServerConfigMapTypeList(netServerConfigMapTypeData);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        
    }
    
    /**
     * @author dhavalraval
     * @auther Updated by - Kaushik Vira
     * @param netServerInstanceData
     * @throws DataManagerException
     * @purpose This method is generated to Create the NetServerInstance
     */
	public INetServerInstanceData createServerInstance(INetServerInstanceData netServerInstanceData,
			IStaffData staffData) throws DataManagerException {

      EliteAssert.notNull(netServerInstanceData,"netServerInstanceData Must Specified");
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        
        if(servermgrDataManager == null){
        	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
        }
        
        try {
            
            session.beginTransaction();
            Logger.logDebug(MODULE, "--Start-- Crate New Server Instance for Server Type :- " + netServerInstanceData.getNetServerTypeId());
            INetServerInstanceData  serverInstanceData = servermgrDataManager.createServerInstance(netServerInstanceData);
            
            List<INetServerConfigMapData> lstNetServerConfigMapList = servermgrDataManager.getNetServerConfigMapList(serverInstanceData.getNetServerTypeId());
            for ( INetServerConfigMapData netServerConfigMapData : lstNetServerConfigMapList ) {
                createNetServerConfigurationInstance(netServerConfigMapData.getNetConfigId(), serverInstanceData.getNetServerId(), session);
            }
            
            generateServerInstanceIdentification(serverInstanceData.getNetServerId(), servermgrDataManager);
            
            // Audit parameters
            staffData.setAuditName(netServerInstanceData.getName());
            AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_SERVER_INSTANCE_ACTION);
            
            commit(session);
            Logger.logDebug(MODULE, "--END-- Crate New Server Instance Result:- Success");
            return serverInstanceData;
		} catch (DataManagerException hExp) {
			rollbackSession(session);
			Logger.logDebug(MODULE,"--END-- Crate New Server Instance Result:- Faild. Reason :-"+ hExp.getMessage());
			throw hExp;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			Logger.logDebug(MODULE,"--END-- Crate New Server Instance Result:- Faild. Reason :-"+ e.getMessage());
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
	public List<INetServiceInstanceData> getListOfServicesByServerId(String netServerId) throws DataManagerException{
		IDataManagerSession session = null;
        session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        
        if (servermgrDataManager == null) {
        	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
        }
        List<INetServiceInstanceData> servicesDataLst = null; 
        try{
        	servicesDataLst = servermgrDataManager.getNetserviceInstanceList(netServerId);
        	
        }catch(DataManagerException dbe){
        	throw dbe;
        }catch (Exception e) {
        	e.printStackTrace();
        	throw new DataManagerException(e.getMessage(), e);
		}
        
		return servicesDataLst;
	}
	
	
    public INetServerInstanceData updateServerInstanceByName( INetServerInstanceData netServerInstanceData,List<INetServiceInstanceData> lstServiceForUpdate,List<INetServiceInstanceData> lstServiceForDelete , String serverName, IStaffData staffData) throws DataManagerException {
    	  IDataManagerSession session = null;
          session = DataManagerSessionFactory.getInstance().getDataManagerSession();
          NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
          
          if (servermgrDataManager == null) {
          	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
          }
          
          INetServerInstanceData  serverInstanceData = null ;
          try {
              
              NetServiceBLManager serviceBLManager= new NetServiceBLManager();
              session.beginTransaction();
              
              serverInstanceData = servermgrDataManager.updateServerInstanceByName(netServerInstanceData, serverName, staffData);
              
              Set<String> servicesNames = getListOfServicesNames(getListOfServicesByServerId(serverInstanceData.getNetServerId()));
              
              TreeSet<String> servicesNameNotDeleted = new TreeSet<String>(); 
              if(Collectionz.isNullOrEmpty(lstServiceForDelete) == false){
            	  for (INetServiceInstanceData  netServiceInstanceData : lstServiceForDelete) {
            		  if(servicesNames.contains(netServiceInstanceData.getDisplayName())){
            			  netServiceInstanceData.setNetServerId(serverInstanceData.getNetServerId());
            			  serviceBLManager.deleteNetServiceByName(netServiceInstanceData.getDisplayName(), serverInstanceData.getNetServerId(),session);
            		  }else {
            			  servicesNameNotDeleted.add(netServiceInstanceData.getDisplayName());
            		  }
            	  }
            	  
            	  if(Collectionz.isNullOrEmpty(servicesNameNotDeleted) == false){
            		  String serviceNames = servicesNameNotDeleted.toString();
            		  
            		  String notConfiguredServices = serviceNames.substring(1, serviceNames.length()-1);
            		  throw new DataManagerException( "Failed to Remove Service(s), Reason : "+notConfiguredServices +" not configured in "+serverInstanceData.getName()+" Server");
            	  }
              }	

              servicesNames = getListOfServicesNames(getListOfServicesByServerId(serverInstanceData.getNetServerId()));
              if(Collectionz.isNullOrEmpty(lstServiceForUpdate) == false){
            	  for (INetServiceInstanceData  netServiceInstanceData : lstServiceForUpdate) {
            		  
            		  // if service not added than add else nothing to do
            		  if(servicesNames.contains(netServiceInstanceData.getDisplayName()) == false){
            			  netServiceInstanceData.setNetServerId(serverInstanceData.getNetServerId());
            			  serviceBLManager.createServiceInstance(netServiceInstanceData,session);
            		  }
            	  }
              }	
              
              
              // Audit parameters
              staffData.setAuditName(serverName);
              AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_SERVER_ACTION);
              
              commit(session);
              
		} catch (DataManagerException hExp) {
			rollbackSession(session);
			throw hExp;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        
       return serverInstanceData;
          
    }
    
    
    /**
     * @author mayurmistry
     * @param netServerInstanceData
     * @throws DataManagerException
     * @purpose This method is generated to Create the NetServerInstance
     */
    public INetServerInstanceData createServerInstance( INetServerInstanceData netServerInstanceData,List<INetServiceInstanceData> lstServiceInstanceData , IStaffData staffData) throws DataManagerException {
        EliteAssert.notNull(netServerInstanceData,"netServerInstanceData Must Specified");
        EliteAssert.notNull(lstServiceInstanceData,"lstServiceInstanceData Must Specified");
        IDataManagerSession session = null;
        session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        if (servermgrDataManager == null) {
        	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
        }
       
        try {
            
            NetServiceBLManager serviceBLManager= new NetServiceBLManager();
            session.beginTransaction();
            Logger.logDebug(MODULE, "--Start-- Create New Server Instance for Server Type :- " + netServerInstanceData.getNetServerTypeId());
            INetServerInstanceData  serverInstanceData = servermgrDataManager.createServerInstance(netServerInstanceData);
           
            List<INetServerConfigMapData> lstNetServerConfigMapList = servermgrDataManager.getNetServerConfigMapList(serverInstanceData.getNetServerTypeId());
            for ( INetServerConfigMapData netServerConfigMapData : lstNetServerConfigMapList ) {
                createNetServerConfigurationInstance(netServerConfigMapData.getNetConfigId(), serverInstanceData.getNetServerId(), session);
            }
            
            generateServerInstanceIdentification(serverInstanceData.getNetServerId(), servermgrDataManager);
            
            Logger.logDebug(MODULE, "--Start-- Created New Server Instance  :-"+netServerInstanceData);

            INetServerInstanceData netServerInstanceData1=servermgrDataManager.getNetServerInstance(netServerInstanceData.getNetServerId());
            Logger.logDebug(MODULE,"get server instance form the create server instance method : "+netServerInstanceData1);
            
            for (Iterator iterator = lstServiceInstanceData.iterator(); iterator.hasNext();) {
            	INetServiceInstanceData  netServiceInstanceData = (INetServiceInstanceData) iterator.next();
            	netServiceInstanceData.setNetServerId(serverInstanceData.getNetServerId());
            	serviceBLManager.createServiceInstance(netServiceInstanceData,session);
			}
            
            // Audit parameters
            staffData.setAuditName(netServerInstanceData.getName());
            AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_SERVER_INSTANCE_ACTION);
            
            commit(session);
            Logger.logDebug(MODULE, "--END-- Create New Server Instance Result:- Success.");
            return serverInstanceData;
        }catch (DataManagerException hExp) {
            rollbackSession(session);
            Logger.logDebug(MODULE, "--END-- Create New Server Instance Result:- Faild. Reason :-" + hExp.getMessage());
            throw hExp;
        }catch (Exception e) {
        	e.printStackTrace();
            rollbackSession(session);
            Logger.logDebug(MODULE, "--END-- Create New Server Instance Result:- Faild. Reason :-" + e.getMessage());
            throw new DataManagerException(e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    /**
     * @author vaseem 
     * @author updated By kaushikvira 
     * @param netServerId - Identify Server Instance 
     * @param NetServerDataManager - Session Object - This Method Can`t run with out parent Session. @purpose Generate Server
     * Identifier,It`s uses netserver Id as Server Identifier.
     */
    private void generateServerInstanceIdentification( String netServerId , NetServerDataManager servermgrDataManager ) throws DataManagerException {
        EliteAssert.greaterThanZero(netServerId, "netServerId");
        EliteAssert.notNull(servermgrDataManager, "servermgrDataManager Must be specified");
        String netServerCode = netServerId;      
        Logger.logDebug(MODULE, "Server Identifier : " + netServerCode + ", Len : "+ netServerCode.length());
        Logger.logDebug(MODULE, "Generated Net Server Code : " + netServerCode + ", Len : "+ netServerCode.length());
        
        servermgrDataManager.generateServerIdentificationInstance(netServerId, netServerCode);
        Logger.logDebug(MODULE, "Setting Server identification for ServerInstance:- " + netServerId + "-> Idnetification :-" + netServerId);
    }
    
    
    
    /**
     * @author dhavalraval
     * @return List
     * @throws DataManagerException
     * @purpose This method returns the list of all the records of
     *          NetServerInstance table.
     */
    public List<NetServerInstanceData> getNetServerInstanceList( ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerInstanceList();
        }catch (DataManagerException e) {
            throw e;
        }catch (Exception e) {
        	e.printStackTrace();
            throw new DataManagerException("getNetServerInstanceList Operation Failed. Reason:" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    public List<NetServerInstanceData> getAAAServerInstanceList( ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getAAAServerInstanceList();
        }catch (DataManagerException e) {
            throw e;
        }catch (Exception e) {
        	e.printStackTrace();
            throw new DataManagerException("getNetServerInstanceList Operation Failed. Reason:" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    
    
    public List<NetServerInstanceData> getNetServerInstanceListForLicense() throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerInstanceListForLicense();
        }catch (DataManagerException e) {
            throw e;
        }catch (Exception e) {
        	e.printStackTrace();
            throw new DataManagerException("getNetServerInstanceList Operation Failed. Reason:" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    /**
     * @author dhavalraval
     * @author Updated by - kaushik vira
     * @param lstServerInstanceId
     * @param commonStatusId
     * @throws DataManagerException
     * @purpose This method is generated to update the status (show/hide)
     */
    public void updateStatus( List<String> lstNetServerId , String commonStatusId ) throws DataManagerException {
        IDataManagerSession session = null;
        EliteAssert.notNull(lstNetServerId, "lstNetServerId Must be specified.");
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            session.beginTransaction();
            for ( String netServerId : lstNetServerId ) {
                servermgrDataManager.updateStatus(netServerId, commonStatusId, getSystemTimeStemp());
            }
            commit(session);
        }catch (DataManagerException e) {
            rollbackSession(session);
            throw e;
        }catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException("Update Status Operation Failed :" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    /**
     * @author dhavalraval
     * @param netServerInstanceData
     * @throws DataManagerException
     * @purpose This method is generated to update the BasicDetails of
     *          NetServerInstance.
     */
    public void updateBasicDetail( INetServerInstanceData netServerInstanceData ) throws DataManagerException {
        EliteAssert.notNull(netServerInstanceData, "netServerInstanceData Must Specify.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            session.beginTransaction();
            servermgrDataManager.updateBasicDetail(netServerInstanceData, getSystemTimeStemp());
            commit(session);
        }catch (DataManagerException hExp) {
            rollbackSession(session);
            throw hExp;
        }catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException("updateBasicDetail Operation Failed. Reason:-" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    /**
     * @author dhavalraval
     * @param netServerInstanceData
     * @throws DataManagerException
     * @purpose This method is generated to changeAdminInterface of
     *          NetServerInstace.
     */
    public void updateAdminInterface( INetServerInstanceData netServerInstanceData ) throws DataManagerException {
        EliteAssert.notNull(netServerInstanceData, "netServerInstanceData Must Specified.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            session.beginTransaction();
            servermgrDataManager.changeAdminInterface(netServerInstanceData, getSystemTimeStemp());
            commit(session);
        }catch (DataManagerException hExp) {
            rollbackSession(session);
            throw hExp;
        }catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException("Change Admin Interface Action Failed :" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik Vira
     * @param netServerInstanceData,iNetServerStartupConfigData
     * @throws DataManagerException
     * @purpose This method is generated to changeAdminInterfaceDetails of
     *          NetServerInstace.
     */
    public void updateAdminDetails( INetServerInstanceData inetServerInstanceData , NetServerStartupConfigData iNetServerStartupConfigData ) throws DataManagerException {
        
        EliteAssert.notNull(inetServerInstanceData, "inetServerInstanceData Must be specified.");
        EliteAssert.notNull(iNetServerStartupConfigData, "iNetServerStartupConfigData Must be specified.");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            validateCreateNetServerStartupConfig(iNetServerStartupConfigData);
            session.beginTransaction();
            inetServerInstanceData.setStartupConfig(iNetServerStartupConfigData);
            servermgrDataManager.updateAdminDetails(inetServerInstanceData, iNetServerStartupConfigData, getCurrentTimeStemp());
            commit(session);
        }catch (DataValidationException e) {
            rollbackSession(session);
            throw e;
        }catch (DataManagerException hExp) {
            rollbackSession(session);
            throw hExp;
        }catch (Exception hExp) {
            rollbackSession(session);
            hExp.printStackTrace();
            throw new DataManagerException("Change Admin Interface Action Failed :" + hExp.getMessage(), hExp);
        }finally {
            closeSession(session);
        }
        
    }
    
    public void deleteByName(List<String> serverInstanceNames,IStaffData staffData) throws DataManagerException {
    	List<String> serverInstanceIds = new ArrayList<String>();
    	String instanceName = "";
    	
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);

		if (servermgrDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
    	
    	try {
    		CentralizedLicenseCoordinator coordinator = new CentralizedLicenseCoordinator(new SMLicenseBLManager());
    		for(String serverInstanceName : serverInstanceNames){
    			
    			if(Strings.isNullOrBlank(serverInstanceName) == false){
    				
    				instanceName = serverInstanceName;
    				INetServerInstanceData netServerInstanceData = servermgrDataManager.getNetServerInstanceByName(serverInstanceName.trim());
    				
    				if(netServerInstanceData == null){
    					throw new InvalidValueException("Server Instance not found");
    				}
    				// add server id in list
    				serverInstanceIds.add(netServerInstanceData.getNetServerId());
    				
    				NetServerInstanceData serverInstanceData = null;
					serverInstanceData = (NetServerInstanceData) getNetServerInstanceByName(serverInstanceName);
					//deregistered the instance 
					coordinator.deregister(serverInstanceData.getName());
					
					String ip = serverInstanceData.getAdminHost();
					String port = String.valueOf(serverInstanceData .getAdminPort());
					Object[] objArgValues = {};
					String[] strArgTypes = {};
					String method = "deregisterLicense";
					try {
						//request to Eliteaaa for deregister the instance for license if it throws exception then continue to delete instance
						EliteUtility.doRemoteCommunication(MBeanConstants.LICENSE, method, ip, port, objArgValues, strArgTypes);
					} catch(Exception e) {
						Logger.logInfo(MODULE, "Server Communication failed. Connection can not established.");
					}
					//delete the staff user with instance in staff relation details
					deleteNetServerStaffRelDetailData(serverInstanceData.getName());
    			}
    		}
    		// delete server Instances
    		deleteServer(serverInstanceIds,staffData);
    		
    	} catch (InvalidValueException ive) {
    		ive.printStackTrace();
    		throw new DataManagerException(ive.getMessage(),ive);
    	} catch(DataManagerException dbe){
    		dbe.printStackTrace();
    		throw new DataManagerException("Failed to delete "+instanceName + ", Reason: "+ dbe.getMessage(),dbe);
    	} catch(Exception e){
    		e.printStackTrace();
    		throw new DataManagerException("Failed to delete "+instanceName + ", Reason: "+ e.getMessage(),e);
    	}
    }
    
    /**
     * @author dhavalraval
     * @param lstNetServerId
     * @throws DataManagerException
     * @purpose This method is generated to delete ServerInstance.
     */
    public void deleteServer( List<String> lstNetServerId , IStaffData staffData) throws DataManagerException {
        EliteAssert.notNull(lstNetServerId, "lstnetServerId Must be specified.");
        IDataManagerSession session = null;
        session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        
        if(servermgrDataManager == null){
        	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
        }
        
        try {
            session.beginTransaction();
            String instanceName = "";
            for ( String netServerInstanceId : lstNetServerId ) {
                 instanceName = deleteNetServerInstance(netServerInstanceId, session, servermgrDataManager);
                
                 //Audit paramters
                 staffData.setAuditName(instanceName);
                 AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_SERVER_INSTANCE_ACTION);
            }
            commit(session);
		} catch (DataManagerException hExp) {
			rollbackSession(session);
			throw hExp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
	private String deleteNetServerInstance( String netServerId , IDataManagerSession session , NetServerDataManager servermgrDataManager) throws DataManagerException {
        
        EliteAssert.notNull(session, "session Must be specified.");
        EliteAssert.greaterThanZero(netServerId, "netServerId");
        
        NetServiceBLManager netServiceBlManager = new NetServiceBLManager();
        
        Logger.logDebug(MODULE,"--start -  Delete net Server Instance :-  "+ netServerId);
        
        Logger.logDebug(MODULE,"Step 1 : Delete All Services Instance From Server.");
        List<INetServiceInstanceData> lstNetServiceInstanceList = servermgrDataManager.getNetserviceInstanceList(netServerId);
        for ( INetServiceInstanceData netServiceInstanceData : lstNetServiceInstanceList ) {
            netServiceBlManager.deleteNetService(netServiceInstanceData.getNetServiceId(), session);
        }
        
        Logger.logDebug(MODULE,"Step 2 : Delete All Server Configuration Instance From Server.");
        List<INetServerInstanceConfMapData> lstNetServerInstanceConfMap = servermgrDataManager.getNetServerInstanceConfMap(netServerId);
        for ( INetServerInstanceConfMapData netServerInstanceConfMapData : lstNetServerInstanceConfMap ) {
            deleteNetServerConfigurationInstance(netServerInstanceConfMapData.getConfigInstanceId(), session);
        }
       
        Logger.logDebug(MODULE,"Step 4 : Delete net Server Instance.");
        String deletedServerInstanceName = servermgrDataManager.deleteNetServerInstance(netServerId);
        
        Logger.logDebug(MODULE,"--End --  Delete net Server Instance :-  "+ netServerId);
        return deletedServerInstanceName;
    }
    
    public INetConfigurationInstanceData getConfigurationInstance( String configInstanceId ) throws DataManagerException {
        EliteAssert.greaterThanZero(configInstanceId,"configInstanceId");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            INetConfigurationInstanceData netConfigurationInstanceData = servermgrDataManager.getConfigurationInstance(configInstanceId);
            return netConfigurationInstanceData;
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        
    }
    
    
    
    public byte[] getServerConfigurationStream( String serverId , String configId ) throws DataManagerException {
        
        EliteAssert.notNull(configId);
        EliteAssert.greaterThanZero(serverId,"serverId");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            
            INetConfigurationData netConfigurationData = getRootParameterConfigurationData(configId);
            INetConfigurationInstanceData netConfigurationInstanceData = servermgrDataManager.getServerConfigurationInstanceData(serverId, configId);
            byte[] returnBytes = createOutputStream(netConfigurationData, netConfigurationInstanceData.getConfigInstanceId());
            
            return returnBytes;
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        
    }
    
    /**
     * @author dhavan
     * @param parameterId
     * @return
     * @throws DataManagerException
     */
    public INetConfigurationParameterData getNetConfigurationParameterData( String parameterId , String configId ) throws DataManagerException {
        
        EliteAssert.notNull(configId);
        EliteAssert.notNull(parameterId);
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
             
            return servermgrDataManager.getNetConfigurationParameterData(parameterId, configId);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Get Config Parameter Data failed :"
					+ e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
    /**
     * This method will save the updaeted node in the database. It will delete
     * remove existing configuration for particular instanceId and add the new
     * updated configuration.
     * 
     * @author dhavan
     * @param lstValueData
     * @param strConfigInstanceId
     * @throws DataManagerException
     */
    public void saveNetConfigurationValues( List lstValueData , String configInstanceId , String netServerId ) throws DataManagerException {
        
        EliteAssert.notNull(lstValueData, "lstValueData Must be Specified.");
        EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        EliteAssert.greaterThanZero(netServerId, "netServerId");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
             
            session.beginTransaction();
            servermgrDataManager.saveNetConfValuesData(lstValueData, configInstanceId);
            servermgrDataManager.changeIsConfigInSyncStaus(netServerId, BaseConstant.HIDE_STATUS_ID, new Timestamp(new Date().getTime()));
            
			
            commit(session);
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception exp) {
            rollbackSession(session);
            exp.printStackTrace();
            throw new DataManagerException("Update Status Action Failed :- " + exp.getMessage(), exp);
		} finally {
            closeSession(session);
        }
        
    }
    
    /**
     * @author dhavan This method will give list of Parameters and values based
     *         on configInstanceId
     * @param strConfigInstatnceId
     * @return
     */
    public INetConfigurationParameterData getConfigurationParameterValues( String configInstanceId ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            NetConfigurationParameterData netConfParamData = null;
            
            // Get Configid based on configinstatnceid
            INetConfigurationInstanceData netConfInstanceData = servermgrDataManager.getConfigurationInstance(configInstanceId);
            if (netConfInstanceData != null) {
                
                // Get Root Element by ConfigId
                INetConfigurationData netConfigurationData = servermgrDataManager.getRootParameterConfigurationData(netConfInstanceData.getConfigId());
                
                Set<INetConfigurationParameterData> setParameters = netConfigurationData.getNetConfigParameters();
                
                if (setParameters != null) {
                    Iterator itr = setParameters.iterator();
                    while (itr.hasNext()) {
                        netConfParamData = (NetConfigurationParameterData) itr.next();
                    }
                }
            }
            return netConfParamData;
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values");
            throw new DataManagerException("Update configuration Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
        
    }
    
    public String getNetConfigurationName( String configInstanceId ) throws DataManagerException {
    	EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            return servermgrDataManager.getNetConfigurationName(configInstanceId);
		} catch (DataManagerException exp) {
            throw exp;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values");
            throw new DataManagerException("Update configuration Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    public String getNetServerNameServiceConfig( String configInstanceId ) throws DataManagerException {
        
    	EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            return servermgrDataManager.getNetServerNameServiceConfig(configInstanceId);
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values");
            throw new DataManagerException("Update Basic Detail Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
        
    }
    
    // server name for server config
    public String getNetServerNameServerConfig( String configInstanceId ) throws DataManagerException {
    	EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerNameServerConfig(configInstanceId);
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values");
            throw new DataManagerException("Update Basic Detail Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    public List getServerConfigurationInstance( String serverInstanceId ) throws DataManagerException {
        EliteAssert.greaterThanZero(serverInstanceId, "serverInstanceId");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerConfigInstanceList(serverInstanceId);
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    public INetServerTypeData getNetServerType( String serverTypeId ) throws DataManagerException {
        EliteAssert.notNull(serverTypeId);
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerType(serverTypeId);
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author Dhaval Raval
     * @param netServerId
     * @return Returns List
     * @throws DataManagerException
     * @purpose This method is generated to get list of
     *          ServerInstanceConfMapData by using configInstanceId
     */
    public List getNetServerConfigInstanceList( String netServerId ) throws DataManagerException {
    	 EliteAssert.greaterThanZero(netServerId, "netServerId");
        
        IDataManagerSession session = null;
        List lstNetServerConfigInstanceList = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            lstNetServerConfigInstanceList = servermgrDataManager.getNetServerConfigInstanceList(netServerId);
            
            for ( int i = 0; i < lstNetServerConfigInstanceList.size(); i++ ) {
                NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData) lstNetServerConfigInstanceList.get(i);
                netConfigInstanceData.setNetConfiguration(servermgrDataManager.getNetConfigurationData(netConfigInstanceData.getConfigId()));
            }
           
            return lstNetServerConfigInstanceList;
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values ");
            throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
        
    }
    
    /**
     * @author Dhaval Raval
     * @param netServerId
     * @return Returns List
     * @throws DataManagerException
     * @purpose This method is generated to get list of
     *          ServerInstanceConfMapData by using configInstanceId
     */
    public List getNetServerConfigInstanceList( String netServerId , String netConfigMapTypeId ) throws DataManagerException {
        
    	EliteAssert.greaterThanZero(netServerId, "netServerId");
        EliteAssert.notNull(netConfigMapTypeId);
        
        IDataManagerSession session = null;
        
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            
            List lstNetServerConfigInstanceList = servermgrDataManager.getNetServerConfigInstanceList(netServerId, netConfigMapTypeId);
            
            for ( int i = 0; i < lstNetServerConfigInstanceList.size(); i++ ) {
                NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData) lstNetServerConfigInstanceList.get(i);
                netConfigInstanceData.setNetConfiguration(servermgrDataManager.getNetConfigurationData(netConfigInstanceData.getConfigId()));
            }
            return lstNetServerConfigInstanceList;
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values ");
            throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    
    public List getNetConfigParamStartupModeList( String startUpModeType ) throws DataManagerException {
        EliteAssert.notNull(startUpModeType);
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            if (servermgrDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            return servermgrDataManager.getNetConfigParamStartupModeList(startUpModeType);
		} catch (DataManagerException hExp) {
            throw hExp;
		} catch (Exception hExp) {
			hExp.printStackTrace();
            throw new DataManagerException("Update Server Configuration failed :" + hExp.getMessage(), hExp);
		} finally {
            closeSession(session);
        }
    }
    
    public void updateServerDetails( String netServerId , EliteNetServerData eliteNetServerData , String staffId , String isInSync ) throws DataManagerException {
        
    	EliteAssert.greaterThanZero(netServerId, "netServerId");
        EliteAssert.notNull(eliteNetServerData);
        
        IDataManagerSession session = null;
        
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getName());
            
            NetServiceDataManager netServiceDataManager = getNetServiceDataManager(session);
            EliteAssert.notNull(netServiceDataManager, "Data Manager implementation not found for " + NetServiceDataManager.class.getName());
            
            session.beginTransaction();
            
            INetServerInstanceData netServerInstanceData = getNetServerInstance(netServerId);
            
            /* Mark This Server now As Sync. */
            servermgrDataManager.changeIsConfigInSyncStaus(netServerId, isInSync, getSystemTimeStemp());
            
            cleanServerInstance(netServerInstanceData,session);
            
            Logger.logTrace(MODULE, "==========Transection Commited========");
            List serverConfigList = getServerConfigurationInstance(netServerInstanceData.getNetServerId());

            for ( int serverConfigIndex = 0; serverConfigIndex < eliteNetServerData.getNetConfigurationList().size(); serverConfigIndex++ ) {
                EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData) eliteNetServerData.getNetConfigurationList().get(serverConfigIndex);
                for ( int i = 0; i < serverConfigList.size(); i++ ) {
                    
                	
                    INetConfigurationInstanceData configInstanceData = (INetConfigurationInstanceData) serverConfigList.get(i);
                    INetConfigurationData configData = configInstanceData.getNetConfiguration();
                    
                    if (configData.getAlias().equalsIgnoreCase(eliteNetConfigurationData.getNetConfigurationKey())) {
                    	Logger.logDebug(MODULE, "overwriting server level configuration :- "+eliteNetConfigurationData.getNetConfigurationKey());
                    	
                        overwriteServerConfigurationValues(servermgrDataManager, eliteNetConfigurationData.getNetConfigurationData(), netServerId, configInstanceData.getConfigId());
                        break;
                    }
                }
            }
        
            if (eliteNetServerData.getNetServiceList() != null) {
                for ( int serviceListIndex = 0; serviceListIndex < eliteNetServerData.getNetServiceList().size(); serviceListIndex++ ) {
                    EliteNetServiceData eliteNetServiceData = (EliteNetServiceData) eliteNetServerData.getNetServiceList().get(serviceListIndex);
                    INetServiceInstanceData netServiceInstanceData = netServiceDataManager.getNetServiceInstance(netServerId, eliteNetServiceData.getNetServiceName(), eliteNetServiceData.getNetInstanceId());
                    
                    if (netServiceInstanceData == null) {
                        Date currDate = new Date();
                        
                        netServiceInstanceData = new NetServiceInstanceData();
                        INetServiceTypeData netServiceTypeData = netServiceDataManager.getNetServiceType(netServerInstanceData.getNetServerTypeId(), eliteNetServiceData.getNetServiceName());
                        if(netServiceTypeData!=null){
	                        netServiceInstanceData.setName(netServiceTypeData.getName());
	                        netServiceInstanceData.setDisplayName(netServiceTypeData.getName());
	                        netServiceInstanceData.setDescription(eliteNetServiceData.getDescription());
	                        netServiceInstanceData.setNetServerId(netServerId);
	                        netServiceInstanceData.setInstanceId(eliteNetServiceData.getNetInstanceId());
	                        netServiceInstanceData.setNetServiceTypeId(netServiceTypeData.getNetServiceTypeId());
	                        netServiceInstanceData.setSystemGenerated("N");
	                        netServiceInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
	                        netServiceInstanceData.setCreateDate(new Timestamp(currDate.getTime()));
	                        netServiceInstanceData.setCreatedByStaffId(staffId);
	                        
	                        /* Mark this Service as In Sync. */
	                        
	                        netServiceInstanceData.setIsInSync(isInSync);
	                        createServiceInstance(netServiceDataManager, netServiceInstanceData,session);
                        }
                    }
                    List serviceConfigList = netServiceDataManager.getNetServiceConfigInstanceList(netServiceInstanceData.getNetServiceId());
                    if (eliteNetServiceData.getNetConfigurationList() != null) {
                        for ( int serviceConfigIndex = 0; serviceConfigIndex < eliteNetServiceData.getNetConfigurationList().size(); serviceConfigIndex++ ) {
                            for ( int i = 0; i < serviceConfigList.size(); i++ ) {
                            	EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData) eliteNetServiceData.getNetConfigurationList().get(serviceConfigIndex);
                                INetConfigurationInstanceData configInstanceData = (INetConfigurationInstanceData) serviceConfigList.get(i);
                                INetConfigurationData configData = configInstanceData.getNetConfiguration();
                                	if (configData.getAlias().equalsIgnoreCase(eliteNetConfigurationData.getNetConfigurationKey())) {
                                		Logger.logDebug(MODULE, "overwriting service configuration :- "+eliteNetConfigurationData.getNetConfigurationKey());
                                		overwriteServiceConfigurationValues(netServiceDataManager, eliteNetConfigurationData.getNetConfigurationData(), netServiceInstanceData.getNetServiceId(), configInstanceData);
                                		break;
                                	}
                            }
                        }
                    }
                }
                
            }
            
            commit(session);
		} catch (DataManagerException e) {
            rollbackSession(session);
            throw e;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
            throw new DataManagerException("Update Basic Detail Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
        Logger.logInfo(MODULE,"Server Configuration updated Successfully. ");
    }
    
    private INetConfigurationData overwriteServerConfigurationValues( NetServerDataManager servermgrDataManager , byte[] sourceBytes , String serverId , String configId ) throws DataManagerException {
        
        EliteAssert.notNull(servermgrDataManager, "servermgrDataManager must be specified");
        EliteAssert.notNull(sourceBytes, "sourceBytes must be specified");
        EliteAssert.greaterThanZero(serverId, "serverId");
        EliteAssert.notNull(configId, "configId must be specified");
        
        INetConfigurationData netConfigurationData = new NetConfigurationData();
        try {
            ByteArrayInputStream iServiceStream = new ByteArrayInputStream(sourceBytes);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(iServiceStream);
            
            INetConfigurationInstanceData netConfigurationInstanceData = servermgrDataManager.getServerConfigurationInstanceData(serverId, configId);
            
            if (netConfigurationInstanceData != null) {
                
                netConfigurationData = getRootParameterConfigurationData(configId);
                
                xmlDoc.getDocumentElement().normalize();
                
                Iterator itrNetConfigParameters = netConfigurationData.getNetConfigParameters().iterator();
                
                if (itrNetConfigParameters.hasNext()) {
                    
                    INetConfigurationParameterData netConfigParamData = (INetConfigurationParameterData) itrNetConfigParameters.next();
                    NodeList lstDetailObjectList = xmlDoc.getElementsByTagName(netConfigParamData.getAlias());
                    
                    if (lstDetailObjectList.getLength() > 0) {
                        netConfigParamData = flushParameterValue(netConfigParamData);
                        netConfigParamData = generateDataObjectParseNode(netConfigParamData, lstDetailObjectList.item(0), netConfigurationInstanceData.getConfigInstanceId(), "1");
                    }
                    Set stConfigParamRootObject = new HashSet();
                    stConfigParamRootObject.add(netConfigParamData);
                    netConfigurationData.setNetConfigParameters(stConfigParamRootObject);
                    
                    List netConfigValues = new ArrayList();
                    netConfigValues = getRecursiveNetConfigurationParameterValues(netConfigParamData, netConfigurationInstanceData.getConfigInstanceId(), netConfigValues);
                    

                    servermgrDataManager.saveNetConfValuesData(netConfigValues, netConfigurationInstanceData.getConfigInstanceId());
                } else {
                    throw new DataManagerException("Update Basic Detail Failed : Root Parameter Not Found");
                }
            }
		} catch (DataManagerException e) {
            throw e;
		} catch (Throwable e) {
			e.printStackTrace();
            throw new DataManagerException("Update Basic Detail Failed " + e.getMessage(), e);
        }
        return netConfigurationData;
    }
    
    public INetConfigurationData overwriteServiceConfigurationValues( NetServiceDataManager servermgrDataManager , byte[] sourceBytes , String serviceId , INetConfigurationInstanceData netConfigurationInstanceData ) throws DataManagerException {
        
        EliteAssert.notNull(servermgrDataManager);
        EliteAssert.notNull(sourceBytes);
        EliteAssert.greaterThanZero(serviceId, "serviceId");
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData must be Specified");
        
        INetConfigurationData netConfigurationData = new NetConfigurationData();
        
        try {
            ByteArrayInputStream iServiceStream = new ByteArrayInputStream(sourceBytes);
            String configId = netConfigurationInstanceData.getConfigId();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(iServiceStream);
            
            netConfigurationData = getRootParameterConfigurationData(configId);
            
            xmlDoc.getDocumentElement().normalize();
            
            Iterator itrNetConfigParameters = netConfigurationData.getNetConfigParameters().iterator();
            
            if (itrNetConfigParameters.hasNext()) {
                INetConfigurationParameterData netConfigParamData = (INetConfigurationParameterData) itrNetConfigParameters.next();
                NodeList lstDetailObjectList = xmlDoc.getElementsByTagName(netConfigParamData.getAlias());
                if (lstDetailObjectList.getLength() > 0) {
                    netConfigParamData = flushParameterValue(netConfigParamData);
                    netConfigParamData = generateDataObjectParseNode(netConfigParamData, lstDetailObjectList.item(0), netConfigurationInstanceData.getConfigInstanceId(), "1");
                }
                Set stConfigParamRootObject = new HashSet();
                stConfigParamRootObject.add(netConfigParamData);
                netConfigurationData.setNetConfigParameters(stConfigParamRootObject);
                List netConfigValues = new ArrayList();
                netConfigValues = getRecursiveNetConfigurationParameterValues(netConfigParamData, netConfigurationInstanceData.getConfigInstanceId(), netConfigValues);
                

                servermgrDataManager.saveNetConfValuesData(netConfigValues, netConfigurationInstanceData.getConfigInstanceId());
            } else {
                throw new DataManagerException("Update Basic Detail Failed : Root Parameter Not Found");
            }
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException("Update Basic Detail Failed " + e.getMessage(), e);
        }
        return netConfigurationData;
    }
    
 
    public void createServiceInstance( NetServiceDataManager servermgrDataManager , INetServiceInstanceData netServiceInstanceData,IDataManagerSession session) throws DataManagerException {
        
        EliteAssert.notNull(servermgrDataManager);
        EliteAssert.notNull(netServiceInstanceData);
        
        try {
            netServiceInstanceData = servermgrDataManager.createServiceInstance(netServiceInstanceData);
            List lstNetServiceConfigMapList = servermgrDataManager.getNetServiceConfigMapList(netServiceInstanceData.getNetServiceTypeId(), netServiceInstanceData.getNetServerId());
            
            for ( int i = 0; i < lstNetServiceConfigMapList.size(); i++ ) {
                
                INetConfigurationInstanceData newNetConfigurationInstance = new NetConfigurationInstanceData();
                INetServiceInstanceConfMapData netServiceInstanceConfMap = new NetServiceInstanceConfMapData();
                INetConfigurationData netConfigurationData = new NetConfigurationData();
                
                String netConfigId = ((INetServiceConfigMapData) lstNetServiceConfigMapList.get(i)).getNetConfigId();
                
                newNetConfigurationInstance.setConfigId(netConfigId);
                INetConfigurationInstanceData netConfigurationInstance = servermgrDataManager.createNetConfigurationInstance(newNetConfigurationInstance);
                netServiceInstanceConfMap.setNetServiceId(netServiceInstanceData.getNetServiceId());
                netServiceInstanceConfMap.setConfigInstanceId(netConfigurationInstance.getConfigInstanceId());
                servermgrDataManager.createNetServiceInstanceConfMap(netServiceInstanceConfMap);
                
                netConfigurationData = servermgrDataManager.getRootParameterConfigurationData(netConfigurationInstance.getConfigId());
                
                Set netConfiguraionParameterSet = netConfigurationData.getNetConfigParameters();
                Iterator itrChildParamSet = netConfiguraionParameterSet.iterator();
                
                if (itrChildParamSet.hasNext()) {
                    INetConfigurationParameterData configParamData = (INetConfigurationParameterData) itrChildParamSet.next();
                    List lstConfigParamValue = new ArrayList();
                    parseInitCreateCurrentNode(configParamData, lstConfigParamValue, netConfigurationInstance, "1");
                    servermgrDataManager.createNetConfigurationValues(lstConfigParamValue);
                }
            }
		} catch (DataManagerException hExp) {
            throw hExp;
		} catch (Exception hExp) {
			hExp.printStackTrace();
            throw new DataManagerException("Create Action failed :" + hExp.getMessage(), hExp);
        }
    }
    
  
    
    private void cleanServerInstance(INetServerInstanceData netServerInstanceData,IDataManagerSession session ) throws DataManagerException {
        EliteAssert.notNull(netServerInstanceData, "netServerInstanceData must be Specified");
        EliteAssert.notNull(session, "servermgrDataManager must be Specified");
        EliteAssert.notNull(netServerInstanceData.getNetServerId(), "netServerInstanceData.getNetServerId() must be Specified");
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
              

        Logger.logTrace(MODULE, "Sub Services Delete Complete for Server : "+netServerInstanceData.getNetServerId());

        NetServiceBLManager netServiceBlmanager = new NetServiceBLManager();
        List<INetServiceInstanceData> netServiceInstanceList = servermgrDataManager.getNetserviceInstanceList(netServerInstanceData.getNetServerId());
        Logger.logTrace(MODULE, "netServiceInstanceList Size is "+netServiceInstanceList.size());
        for ( INetServiceInstanceData netServiceInstanceData2 : netServiceInstanceList ) {
            netServiceBlmanager.deleteNetService(netServiceInstanceData2.getNetServiceId(), session);
        }
    }
 
    public void createNetServerStartupConfig( INetServerStartupConfigData netServerStartupConfigData ) throws DataManagerException {
        
        EliteAssert.notNull(netServerStartupConfigData);
        
        IDataManagerSession session = null;
        try {
            
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager netServersStartupDataManager = getNetServerDataManager(session);
            
            if (netServersStartupDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            validateCreateNetServerStartupConfig(netServerStartupConfigData);
            session.beginTransaction();
            netServersStartupDataManager.createServerStartupConfigInstance(netServerStartupConfigData);
            commit(session);
		} catch (DataManagerException exp) {
            rollbackSession(session);
            throw new DataManagerException("Create NetServer Startup Config Action failed : " + exp.getMessage());
		} catch (Exception exp) {
            rollbackSession(session);
            exp.printStackTrace();
            throw new DataManagerException("Create NetServer Startup Config Action failed :" + exp.getMessage());
		} finally {
            closeSession(session);
        }
        
    }
    
    /**
     * @param netServerIntanceId
     * @return void
     * @author kaushikvira
     * @Purpose : if any new Configuration after creating instance of service
     *          that should be a add to existing Configuration instance. This
     *          method checks new Configuration Added In Template and creating
     *          new ConfigurationIntance In Existing ServerIntance at Server
     *          Level.
     */
    public void addNewServerConfiguration( String netServerInstanceId ) throws DataManagerException {
    	EliteAssert.greaterThanZero(netServerInstanceId, "netServerInstanceId");
        IDataManagerSession session = null;
        try {
            
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager netServerDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            session.beginTransaction();
            
            INetServerInstanceData netServerInstanceData = netServerDataManager.getNetServerInstance(netServerInstanceId);
            List netServerconfigInstanceMapList = netServerDataManager.getNetServerConfigInstanceList(netServerInstanceId);
            
            INetServerVersionData netServerVersionData = getConfigurationVersion(netServerInstanceData.getNetServerTypeId(), netServerInstanceData.getVersion());
            EliteAssert.notNull(netServerVersionData, "Unable to find Server Configuration Version");
            
            List netServerConfigMapList = netServerDataManager.getNetServerConfigMapByServerTypeId(netServerInstanceData.getNetServerTypeId(), netServerVersionData.getConfigVersion());
            
            /*
             * Comparing Existing Configuration Instances with Minimal Data And
             * Finding any New Configuration.
             */
            
            if (netServerConfigMapList.size() > netServerconfigInstanceMapList.size()) {
                Logger.logDebug(MODULE, netServerConfigMapList.size() - netServerconfigInstanceMapList.size() + "  New Configuration found for Server :-" + netServerInstanceId);
                INetConfigurationInstanceData tempNetServerConfigData = null;
                INetServerConfigMapData tempNetServerConfigMapData = null;
                Set tempHashSet = new HashSet();
                
                Iterator itNetServerConfigMapList = netServerconfigInstanceMapList.iterator();
                while (itNetServerConfigMapList.hasNext()) {
                    tempNetServerConfigData = (NetConfigurationInstanceData) itNetServerConfigMapList.next();
                    tempHashSet.add(tempNetServerConfigData.getConfigId());
                }
                
                itNetServerConfigMapList = netServerConfigMapList.iterator();
                while (itNetServerConfigMapList.hasNext()) {
                    tempNetServerConfigMapData = (NetServerConfigMapData) itNetServerConfigMapList.next();
                    if (!tempHashSet.contains(tempNetServerConfigMapData.getNetConfigId()))
                        createNetServerConfigurationInstance(tempNetServerConfigMapData.getNetConfigId(), netServerInstanceData.getNetServerId(), session);
                }
            }
            commit(session);
		} catch (DataManagerException e) {
            rollbackSession(session);
            throw e;
		} catch (Exception exp) {
            rollbackSession(session);
            exp.printStackTrace();
            throw new AddNewServerConfigOpFailedException("addNewServerConfiguration failed. Reason:" + exp.getMessage(), exp);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @param netServerIntanceId
     * @return void
     * @author kaushikvira
     * @Purpose : Return boolean for is in sync or not
     */
    public boolean isConfigInSync( String netServerInstanceId ) throws DataManagerException {
        
        EliteAssert.greaterThanZero(netServerInstanceId, "netServerIntanceId");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager netServerDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            INetServerInstanceData netServerInstanceData = netServerDataManager.getNetServerInstance(netServerInstanceId);
            
            if (!netServerInstanceData.isInSync())
                return false;
            
            for ( INetServiceInstanceData netServiceInstacneData : netServerDataManager.getNetserviceInstanceList(netServerInstanceId) ) {
                if (!netServiceInstacneData.isInSync())
                    return false;
            }
            return true;
            
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik Vira.
     * @return void
     * @purpose This method is Will mark server in sync. also mark all services
     *          of this server as sync.
     * @throws DataManagerException
     */
    public void markServerisInSync( String netServerInstanceId ) throws DataManagerException {
    	EliteAssert.greaterThanZero(netServerInstanceId, "netServerInstanceId");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            
            NetServerDataManager netServerDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getName());
            NetServiceDataManager netServiceDataManager = getNetServiceDataManager(session);
            EliteAssert.notNull(netServiceDataManager, "Data Manager implementation not found for " + NetServiceDataManager.class.getName());
            
            session.beginTransaction();
            netServerDataManager.changeIsConfigInSyncStaus(netServerInstanceId, BaseConstant.SHOW_STATUS_ID, getSystemTimeStemp());
            for ( INetServiceInstanceData netServiceInstanceData : netServerDataManager.getNetserviceInstanceList(netServerInstanceId) ) {
                netServiceDataManager.changeIsConfigInSyncStaus(netServiceInstanceData.getNetServiceId(), BaseConstant.SHOW_STATUS_ID, getSystemTimeStemp());
            }
            commit(session);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    public void validateCreateNetServerStartupConfig( INetServerStartupConfigData netServerStartupConfigData ) throws DataValidationException {
        
        // Common Status status
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getCommonStatusId()))
            throw new InvalidValueException("Invalid Common status Id");
        
        // Communication Port
        if (!EliteGenericValidator.isInRange(netServerStartupConfigData.getCommunicationPort(), 0L, 65536L))
            throw new ValueOutOfRangeException("Invalid Communication Port Id");
        
        // Failure Msg
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getFailureMsg()))
            throw new InvalidValueException("Invalid Failure Msg");
        
        // Login prompt
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getLoginPrompt()))
            throw new InvalidValueException("Invalid Login Prompt");
        
        // NetworkInfo Server Id
        if (!EliteGenericValidator.isGreaterThanZero(netServerStartupConfigData.getNetServerId()))
            throw new InvalidValueException("Invalid NetworkInfo Server Id");
        
        // NetworkInfo Password
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getPassword()))
            throw new InvalidValueException("Invalid Password");
        
        // NetworkInfo Password prompt
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getPasswordPrompt()))
            throw new InvalidValueException("Invalid passord Prompt");
        
        // NetworkInfo Protocol
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getProtocol()))
            throw new InvalidValueException("Invalid protocol");
        
        // NetworkInfo Shell
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getShell()))
            throw new InvalidValueException("Invalid Shell");
        
        // NetworkInfo Shell Prompt
        
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getShellPrompt()))
            throw new InvalidValueException("Invalid Shell Prompt");
        
        // NetworkInfo User Name
        if (EliteGenericValidator.isBlankOrNull(netServerStartupConfigData.getUserName()))
            throw new InvalidValueException("Invalid UserName");
        
    }
    
    /*
     * @author kaushikVira @param netServerTypeId - Identify Server Type @param
     * serverVersionId - identify Server Version @return configuration version
     * for the ServerType
     */
    public String resolveConfigurationVersion( String serverTypeId , String serverVersionId ) throws DataManagerException {
        EliteAssert.notNull(serverTypeId, "serverTypeId Must Specified");
        EliteAssert.notNull(serverVersionId, "serverVersionId Must Specified");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager netServerDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            return netServerDataManager.resolveConfigurationVersion(serverTypeId, serverVersionId);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
        }
    }
    
    /***************************************************************************
     * Start Server Upgrade
     **************************************************************************/
    
    /**
     * @author kaushik vira
     * @param netServerInstanceId - Identifies server instance
     * @param targetedServerVersion - Target Server Version
     * @return Upgrade server instance to targeted server version.
     */
    public void upgradeNetServer( String netServerInstanceId , String targetedServerVersion ) throws DataManagerException {
    	EliteAssert.greaterThanZero(netServerInstanceId, "netServerInstanceId");
        EliteAssert.notNull(targetedServerVersion, "Targeted Server Version Must be specified.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            session.beginTransaction();
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            /*
             * Getting netServerInstance Data it contains all the information
             * server instance.
             */
            INetServerInstanceData netServerIntanceData = getNetServerInstance(netServerInstanceId);
            Logger.logDebug(MODULE, "--Start--  UpgradeNetServer Started.");
            Logger.logDebug(MODULE, "netServerInstanceId :-" + netServerIntanceData.getNetServerId());
            Logger.logDebug(MODULE, "ServerTypeId :-" + netServerIntanceData.getNetServerType().getNetServerTypeId());
            Logger.logDebug(MODULE, "Targeted Version :-" + targetedServerVersion);
            
            
             INetServerVersionData netServerVersionData = serverDataManager.getCompatibleVersion(netServerIntanceData.getNetServerType().getNetServerTypeId(), targetedServerVersion);
             if(netServerVersionData == null)
                 throw new VersionNotSupportedException("No Supported Version is found.");
             
            /*
             * Getting Set of netConfigurationInstance Data For comparing
             * configuration Version. This Block will Collect Information about
             * Server instance version.
             */
            List<INetServerInstanceConfMapData> lstNetServerInstanceConfMapData = serverDataManager.getNetServerInstanceConfMap(netServerInstanceId);
            Set<INetConfigurationInstanceData> setNetConfigurationInstanceData = new HashSet<INetConfigurationInstanceData>();
            for ( INetServerInstanceConfMapData netServerInstanceConfMapData : lstNetServerInstanceConfMapData ) {
                setNetConfigurationInstanceData.add(serverDataManager.getConfigurationInstance(netServerInstanceConfMapData.getConfigInstanceId()));
            }
            
            Logger.logDebug(MODULE,"Step 1: Upgrade  Server Level");
            Logger.logDebug(MODULE,"Step 2: Upgrade  Service Level");
            Logger.logDebug(MODULE,"Step 3: Upgrade  Driver Level");
            
            
            Logger.logDebug(MODULE,"Step 1.1: Identify upgradation");
            
            /* temporary list used in next to next step */
            List<String> tempLstForContainsCheck = new ArrayList<String>();
            /* Comparing configuration */
            Map<INetConfigurationInstanceData, String> configurationComparisonMap = new HashMap<INetConfigurationInstanceData, String>();
            List<INetConfigurationInstanceData> lstRemovedConfiguration = new ArrayList<INetConfigurationInstanceData>();
            for ( INetConfigurationInstanceData netConfigurationInstanceData : setNetConfigurationInstanceData ) {
                INetConfigurationData netConfigurationData = getUpgradeVersionServerConfiguration(netConfigurationInstanceData.getConfigId(), netServerIntanceData.getNetServerTypeId(), targetedServerVersion);
                if (netConfigurationData == null) {
                    Logger.logDebug(MODULE, "Missing Configuration found : [ " + netConfigurationInstanceData.getConfigInstanceId() + ":" + netConfigurationInstanceData.getConfigId() + " ] -> [ " + netConfigurationInstanceData.getConfigInstanceId() + ":" + netConfigurationData + " ]");
                    lstRemovedConfiguration.add(netConfigurationInstanceData);
                } else {
                    Logger.logDebug(MODULE, "upgradation found : [ " + netConfigurationInstanceData.getConfigInstanceId() + ":" + netConfigurationInstanceData.getConfigId() + " ] -> [ " + netConfigurationInstanceData.getConfigInstanceId() + ":" + netConfigurationData.getNetConfigId() + " ]");
                    configurationComparisonMap.put(netConfigurationInstanceData, netConfigurationData.getNetConfigId());
                    tempLstForContainsCheck.add(netConfigurationData.getNetConfigId());
                }
            }
            
            /*
             * Getting List of ServerConfigMapData Data For comparing
             * configuration Version. This Block will Collect Information about
             * targeted version for Checking for any new configuration Added.
             */
            List<INetServerConfigMapData> lstTargetedNetServerConfigMapData = getNetServerConfigMapList(netServerIntanceData.getNetServerType().getNetServerTypeId(), targetedServerVersion);
            List<INetServerConfigMapData> lstnewAddedConfiguration = new ArrayList<INetServerConfigMapData>();
            for ( INetServerConfigMapData netServerConfigMapData : lstTargetedNetServerConfigMapData ) {
                if (!tempLstForContainsCheck.contains(netServerConfigMapData.getNetConfigId())) {
                    Logger.logDebug(MODULE, "New Configuration found : " + netServerConfigMapData.getNetConfigId());
                    lstnewAddedConfiguration.add(netServerConfigMapData);
                }
            }
            
            Logger.logDebug(MODULE,"Step 1.1.1: upgrade Configuration");
            for ( INetConfigurationInstanceData netConfigurationInstanceData : configurationComparisonMap.keySet() ) {
                upgradeNetServerConfiguration(netConfigurationInstanceData, configurationComparisonMap.get(netConfigurationInstanceData), session);
            }
            
            Logger.logDebug(MODULE,"Step 1.1.2: Create New Configuration");
            for(INetServerConfigMapData netServerConfigMapData : lstnewAddedConfiguration ) {
                createNetServerConfigurationInstance(netServerConfigMapData.getNetConfigId(),netServerInstanceId, session);
            }
            
            Logger.logDebug(MODULE,"Step 1.1.3: Delete Missing Configuration");
            for(INetConfigurationInstanceData netConfigurationInstanceData : lstRemovedConfiguration ) {
                deleteNetServerConfigurationInstance(netConfigurationInstanceData.getConfigInstanceId(), session);
            }
            
            
            NetServiceBLManager netServiceBlManager = new NetServiceBLManager();
            NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
            EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServiceDataManager.class.getSimpleName());
            
            List<NetServiceInstanceData>  lstNetServiceInstanceData = serviceDataManager.getNetserviceInstanceList(netServerInstanceId);
            for ( NetServiceInstanceData netServiceInstanceData : lstNetServiceInstanceData ) {
                netServiceBlManager.upgradeNetService(netServiceInstanceData.getNetServiceId(), netServerIntanceData, targetedServerVersion, session);
            }
            
            Logger.logDebug(MODULE,"Step 1.2 Update Server Instance Version " + netServerIntanceData.getVersion() +"->"+ targetedServerVersion);
            serverDataManager.updateServerVersion(netServerInstanceId, targetedServerVersion, getSystemTimeStemp());
            
            Logger.logDebug(MODULE,"Step 1.3 Mark Server unsync ");
            serverDataManager.changeIsConfigInSyncStaus(netServerInstanceId,BaseConstant.HIDE_STATUS_ID, getSystemTimeStemp());
            
            Logger.logDebug(MODULE, "-- End -- upgradeNetServer ServerInstanceId:" + netServerIntanceData.getNetServerId() + " Status : Successful");
            commit(session);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
        
    }
    
    /**
     * @author kaushik vira
     * @param  configTypeId - identify configuration Type.
     * @param  netServerType - Server Type
     * @param  netServerVersion - Server Version
     * @return Compatible Upgrade Version of ConfigurationData
     */
    public INetConfigurationData getUpgradeVersionServerConfiguration(String configTypeId,String netServerType,String netServerVersion) throws DataManagerException {
        EliteAssert.notNull(configTypeId,"configTypeId must be Specified.");
        EliteAssert.notNull(netServerType,"NetServer Type must be Specified.");
        EliteAssert.notNull(netServerVersion,"NetServer Version Must be specified.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(serverDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            INetConfigurationData updgradeServerConfiguration = getUpgradeConfigurationVersion(configTypeId, netServerType, netServerVersion, session);
            if(isServerLevelConfiguration(updgradeServerConfiguration,netServerType,netServerVersion, session))
                return updgradeServerConfiguration;
            return null;
            
		} catch (DataManagerException e) {
			throw e;
        }catch(Exception e) {
        	e.printStackTrace();
            throw new DataManagerException("getCompatibleUpgradeVersionOfConfigurationData Operation Faild. Reason :"+e.getMessage(),e);
        }finally {
            closeSession(session);
        } 
    }    
    
    private boolean isServerLevelConfiguration(INetConfigurationData netConfigurationData,String netServerType,String netServerVersion,IDataManagerSession session) throws DataManagerException{
        
        EliteAssert.notNull(session,"NetServer Type must be Specified.");
        if(netConfigurationData == null)
            return false;
        
        NetServerDataManager serverDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(serverDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        List<INetServerConfigMapData> netServerCofigMapData = serverDataManager.getNetServerConfigMapList(netServerType,netServerVersion);
        for ( INetServerConfigMapData netServerConfigMapData : netServerCofigMapData ) {
            if(netServerConfigMapData.getNetConfigId().equals(netConfigurationData.getNetConfigId()))
                return true;
        }
        return false;
    }
    
    
    public void generateInstanceId( INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException {
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData Must be specified");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            session.beginTransaction();
            
            List<INetConfigurationValuesData> lstNetConfigurationValuesData = generateInstanceId(netConfigurationInstanceData.getConfigInstanceId(),netConfigurationInstanceData.getConfigId(),session);
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            serverDataManager.saveNetConfValuesData(lstNetConfigurationValuesData, netConfigurationInstanceData.getConfigInstanceId());
            
            commit(session);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException("getConfigurationParameterDataList Operation Failed Reason:" + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik vira
     * @param INetConfigurationInstanceData - Identity ConfigurationInstance 
     * @return List<INetConfigurationParameterData> -
     * return list of configuration Parameter.
     */
    public List<INetConfigurationValuesData> getConfigurationParameterDataList( INetConfigurationInstanceData netConfigurationInstanceData ) throws DataManagerException {
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData Must be specified");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            return getConfigurationParameterDataList(netConfigurationInstanceData, session);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException("getConfigurationParameterDataList Operation Failed Reason:" + e.getMessage(), e);
        } finally {
        	closeSession(session);
        }
    }
    
    /**
     *@author kaushik vira
     *@param INetConfigurationInstanceData - Identity ConfigurationInstance 
     *@return List<INetConfigurationParameterData> - return list of configuration Parameter.
     */
    private List<INetConfigurationValuesData> getConfigurationParameterDataList( INetConfigurationInstanceData netConfigurationInstanceData , IDataManagerSession session ) throws DataManagerException {
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData Must be specified");
        EliteAssert.notNull(session, "session Must be specified, Can`t Run With out parent Session.");
        NetServerDataManager netServerDatamanager = getNetServerDataManager(session);
        EliteAssert.notNull(netServerDatamanager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        return netServerDatamanager.getConfigurationParameterValueDataList(netConfigurationInstanceData);
    }
    
    /**
     * @author kaushik vira
     * @param netServerTypeId - Identify NetworkInfo Server Type
     * @param targetedServerVersion - Target Server Version
     * @return List<INetServerConfigMapData> - all the ServerCofigMap Data for
     *         targetedServerVersion of netServerTypeId.
     */
    public List<INetServerConfigMapData> getNetServerConfigMapList( String netServerTypeId , String netServerVersion ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            return serverDataManager.getNetServerConfigMapList(netServerTypeId, netServerVersion);
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException("getNetServerConfigMapList Operation Faild. Reason :" + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik vira
     * @param netServerTypeId - identify ServerType.
     * @param netServerVersion - identify Serer Version
     * @return INetServerVersionData - netServerVerisonData Specified by
     *         netServerTypeId,netServerVersion
     */
    public INetServerVersionData getConfigurationVersion( String netServerTypeId , String netServerVersion ) throws DataManagerException {
        EliteAssert.notNull(netServerTypeId, "netServerTypeId Must be specified.");
        EliteAssert.notNull(netServerVersion, "netServerVersion Must be specified.");
        
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            return serverDataManager.getCompatibleVersion(netServerTypeId, netServerVersion);
		} catch (DuplicateEntityFoundException e) {
			throw e;
		} catch (DataManagerException e) {
			e.printStackTrace();
            throw new VersionNotSupportedException("Unable to Find Configuration version.Reason :" + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
            throw new VersionNotSupportedException("Unable to Find Configuration version.Reason :" + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik vira
     * @param configInstanceId - identify configuration Instance.
     * @return Delete server Configuration Instance.
     */
    public void deleteNetServerConfigurationInstance( String configInstanceId ) throws DataManagerException {
    	EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            session.beginTransaction();
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            deleteNetServerConfigurationInstance(configInstanceId, session);
            commit(session);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
            throw new DataManagerException("deleteNetServerConfiguration Operation Faild. Reason :" + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
    
    /**
     * @author kaushik vira
     * @param configInstanceId - identify configuration Instance.
     * @param netServerDataManager - netServerDataManager session.
     * @return Delete server Configuration Instance.
     */
    private void deleteNetServerConfigurationInstance( String configInstanceId , IDataManagerSession session ) throws DataManagerException {
    	EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        EliteAssert.notNull(session, "session must Specified.");
        
        NetServerDataManager netServerDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        Logger.logDebug(MODULE,"-- Start -- deleteNetServerConfigurationInstance :- "+ configInstanceId);
        
        /*
         * Steps : Delete ServerConfiguration. 1:- Delete All Entry from
         * TBLMNETCONFIGURATIONVALUES. 2:- Delete Entry form
         * TBLMNETSERVERINSTANCECONFMAP 3:- Delete Entry from
         * TBLMNETCONFIGURATIONINSTANCE.
         */
        
        Logger.logDebug(MODULE,"Step 1 : -Deleting TBLMNETCONFIGURATIONVALUES ");
        netServerDataManager.deleteNetConfigurationValues(configInstanceId);
        
        Logger.logDebug(MODULE,"Step 2 : -Deleting TBLMNETSERVERINSTANCECONFMAP");
        netServerDataManager.deleteNetServerInstanceConfMap(configInstanceId);
        
        Logger.logDebug(MODULE,"Step 3 : -Deleting TBLMNETCONFIGURATIONINSTANCE");
        netServerDataManager.deleteNetConfigurationInstance(configInstanceId);
        
        Logger.logDebug(MODULE,"-- End -- deleteNetServerConfigurationInstance :- "+ configInstanceId);
        
    }
    
    /**
     * @author kaushik vira
     * @param configTypeId - identify configuration Instance.
     * @param netServerDataManager - netServerDataManager session.
     * @return Create New Server Configuration.
     */
    private void createNetServerConfigurationInstance( String configTypeId , String netServerIntanceId , IDataManagerSession session ) throws DataManagerException {
        /* Adding new Configuration in Server Instance --start-- */
    	EliteAssert.greaterThanZero(netServerIntanceId, "netServerIntanceId");
        EliteAssert.notNull(session, "session must Specified.");
        EliteAssert.notNull(configTypeId, "configTypeId must Specified.");
        
        NetServerDataManager netServerDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(netServerDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        /*
         * Steps : Crate ServerConfiguration. 1:- Create Entry from
         * TBLMNETCONFIGURATIONINSTANCE. 2:- Create Entry form
         * TBLMNETSERVERINSTANCECONFMAP 3:- Create All Entry from
         * TBLMNETCONFIGURATIONVALUES.
         */
        
        Logger.logDebug(MODULE, "--START ADD-- New Configuration:- " + configTypeId + " for " + netServerIntanceId);
        
        /* Step 1: */
        INetConfigurationInstanceData netConfigurationInstance = new NetConfigurationInstanceData();
        netConfigurationInstance.setConfigId(configTypeId);
        netConfigurationInstance = netServerDataManager.createNetConfigurationInstance(netConfigurationInstance);
        Logger.logDebug(MODULE, "Step 1: - creating Configuration Instance: " + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServerInstanceId :" + netServerIntanceId);
        
        /* Step 2: */
        INetServerInstanceConfMapData netServerInstanceConfMap = new NetServerInstanceConfMapData();
        netServerInstanceConfMap.setNetServerId(netServerIntanceId);
        netServerInstanceConfMap.setConfigInstanceId(netConfigurationInstance.getConfigInstanceId());
        netServerDataManager.createNetServerInstanceConfMap(netServerInstanceConfMap);
        Logger.logDebug(MODULE, "Step 2: - creating Configuration Map Entry: " + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServerInstanceId :" + netServerIntanceId);
        
        /* Step 3: */
        INetConfigurationData netConfigurationData = new NetConfigurationData();
        /*
         * Every Root parameter has only one Root Tag - So only First parameter
         * Will be considered as root Tag.
         */
        netConfigurationData = netServerDataManager.getRootParameterConfigurationData(netConfigurationInstance.getConfigId());
        Iterator itrChildParamSet = netConfigurationData.getNetConfigParameters().iterator();
        if (itrChildParamSet.hasNext()) {
            INetConfigurationParameterData configParamData = (INetConfigurationParameterData) itrChildParamSet.next();
            List lstConfigParamValue = new ArrayList();
            lstConfigParamValue = parseInitCreateCurrentNode(configParamData, lstConfigParamValue, netConfigurationInstance, "1");
            netServerDataManager.createNetConfigurationValues(lstConfigParamValue);
        }
        Logger.logDebug(MODULE, "Step 3: -creating Param Values entry for configInstanceId :" + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServerInstanceId :" + netServerIntanceId);
        Logger.logDebug(MODULE, "--END ADD-- New Configuration:- " + configTypeId + " for " + netServerIntanceId);
        /* Adding new Configuration in Server Instance --End-- */
    }
    
    /***************************************************************************
     * END Server Upgrade
     **************************************************************************/
    

    public List<NetServerInstanceData> getNetServerInstanceListByTypeId(String netServerTypeId) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            return servermgrDataManager.getNetServerInstanceListByTypeId(netServerTypeId);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException("getNetServerInstanceList Operation Failed. Reason:" + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
    }
   
  public List getServerConfiguration(String netServerId) throws DataManagerException{
	 EliteAssert.notNull(netServerId);
    
     IDataManagerSession session = null;
    
     try {
        session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
        return servermgrDataManager.getNetServerConfiguration(netServerId);
     }catch (DataManagerException e) {
        throw e;
     }catch (Exception e) {
    	 e.printStackTrace();
        throw new DataManagerException(e.getMessage(), e);
     }finally {
        closeSession(session);
     }
 }

  /**
   * This method is used to get binded plugin-names with Server Instance .
   * @return set of plugin-names .
   * @throws DataManagerException
   */
  public Set<String> getBindedPluginNames() throws DataManagerException {
	  Set<String> pluginNames = new HashSet<String>();
	  try{
			List<NetServerInstanceData> netServerInstanceList = getNetServerInstanceList();
			BaseUpdateConfigurationAction baseUpdateConfigurationAction = new BaseUpdateConfigurationAction() {};
			if (netServerInstanceList != null && netServerInstanceList.isEmpty() == false) {
	        	 for(NetServerInstanceData serverInstanceData : netServerInstanceList){
	        		 String serverInstanceId = serverInstanceData.getNetServerId();
	        		 List configurationInstanceDatas = getServerConfigurationInstance(serverInstanceId);
	        		 if (configurationInstanceDatas != null && configurationInstanceDatas.isEmpty() == false) {
	        			 List<NetConfParameterValueBean> netConfParameterValueParameters= new ArrayList<NetConfParameterValueBean>();
	        			 int configurationInstanceDataLength = configurationInstanceDatas.size();
	        			 for(int i=0;i<configurationInstanceDataLength;i++){
	        					NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)configurationInstanceDatas.get(i);
	        					INetConfigurationParameterData netConfParamData2 = getConfigurationParameterValues(netConfigInstanceData.getConfigInstanceId());
	        					netConfParameterValueParameters = baseUpdateConfigurationAction.getRecursiveNetConfigurationParameterValues(netConfParamData2,netConfigInstanceData.getConfigInstanceId(),netConfParameterValueParameters);
	        					if (netConfParameterValueParameters != null){
 	        						for (NetConfParameterValueBean confParameterValueBean : netConfParameterValueParameters){
	 	        						if (confParameterValueBean.getAlias().equals("plugin-name") && confParameterValueBean.getValue() != null ){
	 	        							pluginNames.add(confParameterValueBean.getValue());
	 	        						}
	 	        					}
 	        					}
	        			 }
	        		 }
	        	 }
	        }
			return pluginNames;
		}catch (DataManagerException e) { 
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}
	  
  }
  
	public NetServerInstanceData getNetServerInstanceByName(String serverName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		NetServerInstanceData serverInstance = null;
		try {
			serverInstance = servermgrDataManager.getNetServerInstanceByName(serverName.trim());
			
			if(serverInstance == null){
				throw new InvalidValueException("Server Instance not found");
			}
			
		} catch (InvalidValueException ive) {
    		ive.printStackTrace();
    		throw new DataManagerException(ive.getMessage(),ive);
    	} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return serverInstance;
	}
  
	 public String getNetConfigurationIdByName( String configInstanceName ) throws DataManagerException {
	        IDataManagerSession session = null;
	        try {
	            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
	            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
	            return servermgrDataManager.getNetConfigurationIdByName(configInstanceName);
	        } catch (DataManagerException exp) {
	            throw exp;
	        }catch (Exception e) {
	        	e.printStackTrace();
	            Logger.logTrace(MODULE, "Problem while getting configuration parameters and values");
	            throw new DataManagerException("Update configuration Failed " + e.getMessage(), e);
	        }finally {
	            closeSession(session);
	        }
	    }

	public String getNetServerInstanceConfigId(String configInstanceIdString) throws DataManagerException {
		   IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            return servermgrDataManager.getNetServerInstanceConfigId(configInstanceIdString);
        } catch (DataManagerException exp) {
            throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Update configuration Failed " + e.getMessage(), e);
		} finally {
            closeSession(session);
        }
	}

	public String getNetServerTypeIdByName(String netServerTypeName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			String netServerId =  servermgrDataManager.getNetServerTypeIdByName(netServerTypeName);
			
			if( Strings.isNullOrBlank(netServerId) ){
				throw new DataManagerException("Invalid Server Type");
			}
			
			return netServerId;
			
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE, "Problem while fetching Net Server Type Id");
			throw new DataManagerException(e.getMessage(), e.getCause());
		} finally {
			closeSession(session);
		}
	}
	
	public boolean isEliteCSMServer(String serverTypeId) throws DataManagerException{
		String serverType = getNetServerTypeNameById(serverTypeId);
		return ELITE_CSM_SERVER.equals(serverType.trim()) ? true : false;
	}
	
	public boolean isRMManager(String serverTypeId) throws DataManagerException{
		String serverType = getNetServerTypeNameById(serverTypeId);
		return RESOURCE_MANAGER.equals(serverType.trim()) ? true : false;
		
	}
	
	public String getNetServerTypeNameById(String netServerTypeName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
			String netServerId =  servermgrDataManager.getNetServerTypeNameById(netServerTypeName);
			
			if( Strings.isNullOrBlank(netServerId) ){
				throw new DataManagerException("Invalid Server Type");
			}
			
			return netServerId;
			
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE, "Problem while fetching Net Server Type Id");
			throw new DataManagerException(e.getMessage(), e.getCause());
		} finally {
			closeSession(session);
		}
	}
	
	public String getNetServerIdByName(String serverTypeName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			String serverType =  servermgrDataManager.getNetServerIdByName(serverTypeName);

			return serverType;
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE, "Problem while fetching Net Server Name");
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public Set<String> getListOfServicesNames(List<INetServiceInstanceData> netServicesData){
		Set<String> servicesNames = new TreeSet<String>();
		
		if(Collectionz.isNullOrEmpty(netServicesData) == false){
			for(INetServiceInstanceData serviceInstanceData : netServicesData) {
				servicesNames.add(serviceInstanceData.getName());
			}
		}
		
		return servicesNames;
	}
	
	public List<String> getListOfServicesAliasNamesFromListOfServices(List<INetServiceInstanceData> netServicesDatas) throws DataManagerException{
		List<String> servicesTypeAliasNames = new ArrayList<String>();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
			if(Collectionz.isNullOrEmpty(netServicesDatas) == false){
				for(INetServiceInstanceData serviceInstanceData : netServicesDatas){
					servicesTypeAliasNames.add(servermgrDataManager.getServiceTypeAliasNameFromServiceTypeId(serviceInstanceData.getNetServiceTypeId()));
				}
			}

		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return servicesTypeAliasNames;
	}
	
	public List<String> getListOfServicesAliasNames() throws DataManagerException{
		List<String> servicesTypeAliasNames = new ArrayList<String>();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
				servicesTypeAliasNames.addAll(servermgrDataManager.getServiceTypeAliasNames());

		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return servicesTypeAliasNames;
	}
	
	public INetConfigurationInstanceData getConfigurationInstanceByConfigurationNameAndServerInstanceId(String serverIntanceId,final String configurationName) throws DataManagerException{
		INetConfigurationInstanceData configurationInstanceData = null;
		try {
			List<INetConfigurationInstanceData> configurationLst = getNetServerConfigInstanceList(serverIntanceId);
			
			for(INetConfigurationInstanceData netCofigurationInstanceData : configurationLst){
				String ConfigName = netCofigurationInstanceData.getNetConfiguration().getName();
				
				if(configurationName.equals(ConfigName.trim())){
					configurationInstanceData = netCofigurationInstanceData;
					break;
				}
				
			}
			
		} catch(DataManagerException dbe){
			throw dbe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to get Configuration Instance data by server instance id and configuration name , Reason: "+e.getMessage(),e);
		}
		
		return configurationInstanceData;
	}
	
	public void updateConfiguration(byte[] configurationData,String serverInstanceId,String configurationName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			INetConfigurationInstanceData clientConfigurationInstanceData = getConfigurationInstanceByConfigurationNameAndServerInstanceId(serverInstanceId,configurationName);
			INetConfigurationData clientConfigurationData = clientConfigurationInstanceData.getNetConfiguration();
			String configId = clientConfigurationData.getNetConfigId();
			
			session.beginTransaction();
			
			overwriteServerConfigurationValues(servermgrDataManager,configurationData, serverInstanceId, configId);
			
			commit(session);
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+configurationName+" configuration, Reason: "+exp.getMessage(),exp);
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException("Failed to update "+configurationName+" configuration, Reason: "+e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void createInstanceStaffRelationData(NetServerStaffRelDetailData data) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
		
		if(servermgrDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
            session.beginTransaction();
            servermgrDataManager.createInstanceStaffRelationData(data);
        	commit(session);
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateInstanceStaffRelationData( NetServerStaffRelDetailData data ) throws DataManagerException {
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager Implementation not found for " + NetServerDataManager.class.getSimpleName());
            
            session.beginTransaction();
            servermgrDataManager.updateInstanceStaffRelationData((data));
            commit(session);
        }catch (DataManagerException hExp) {
            rollbackSession(session);
            throw hExp;
        }catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException("updateBasicDetail Operation Failed. Reason:-" + e.getMessage(), e);
        }finally {
            closeSession(session);
        }
    }
	
	public NetServerStaffRelDetailData getInstanceStaffRelationDataByName(String name) throws DataManagerException {
		IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            return servermgrDataManager.getInstanceStaffRelationDataByName(name.trim());
		} catch (DataManagerException e) {
            throw e;
		} catch (Exception e) {
			e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
		} finally {
            closeSession(session);
        }
	}

	public void deleteNetServerStaffRelDetailData( String instanceName) throws DataManagerException {
        IDataManagerSession session = null;
        session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
        
        if(servermgrDataManager == null){
        	throw new DataManagerException("Data Manager Implementation not found for " + getClass().getName());
        }
        
        try {
            session.beginTransaction();
            servermgrDataManager.deleteStaffRelNetServerDetail(instanceName);
            commit(session);
		} catch (DataManagerException hExp) {
			rollbackSession(session);
			throw hExp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
	
}
