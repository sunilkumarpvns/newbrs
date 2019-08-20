package com.elitecore.elitesm.blmanager.servermgr.service;


import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.BaseServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewDriverConfigOpFailedException;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewServiceConfigOpFailedException;
import com.elitecore.elitesm.datamanager.servermgr.server.NetServerDataManager;
import com.elitecore.elitesm.datamanager.servermgr.service.NetServiceDataManager;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.EliteServerAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;


public class NetServiceBLManager extends BaseServerBLManager {

	private final static String MODULE="NetServiceBLManager";

	private final static String ACTIVE_STATUS="CST01";

	private final static String service = "service";
	private final static String serviceList = "service-list";		
	//private final static String serviceName = "service-name";		
	//private final static String serviceId = "service-instance-id";		
	private final static String serviceId = "service-id";
	//private final static String serviceId = "service-name";
	//private final static String serviceInstanceName = "service-instance-name"; 


	/**
	 * @author  dhavalraval
	 * @return  Returns List
	 * @throws  DataManagerException
	 * @purpose This method is generated to get the list of NetServiceType
	 */
	public List getNetServiceTypeList() throws DataManagerException{
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

			return servermgrDataManager.getNetServiceTypeList();
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public INetServiceConfigMapTypeData getNetServiceConfigMapType(String netConfigMapTypeId) throws DataManagerException{
		EliteAssert.notNull(netConfigMapTypeId,"netConfigMapTypeId must Specified" );
		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
			INetServiceConfigMapTypeData netServiceConfigMapTypeData = new NetServiceConfigMapTypeData();
			netServiceConfigMapTypeData.setNetConfigMapTypeId(netConfigMapTypeId);

			return servermgrDataManager.getNetServiceConfigMapTypeList(netServiceConfigMapTypeData);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}



	/**
	 * @author  dhavalraval
	 * @param   netServerTypeId
	 * @return  Returns List
	 * @throws  DataManagerException
	 * @purpose This method is generated to get the list of NetServiceType
	 */
	public List getNetServiceTypeList(String netServerTypeId) throws DataManagerException{
		EliteAssert.notNull(netServerTypeId);
		IDataManagerSession session = null;
		try {
			session =   DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementaion not found for "+getClass().getName());

			return servermgrDataManager.getNetServiceTypeList(netServerTypeId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}


	/**
	 * @author kaushik vira
	 */
	public List<INetServiceConfigMapData> getNetServiceConfigMapList(String netServiceTypeId ,String netServerTypeId , String netServerVersion ) throws DataManagerException {
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager serverDataManager = getNetServiceDataManager(session);
			return serverDataManager.getNetServiceConfigMapList(netServiceTypeId, netServerTypeId, netServerVersion);
		}
		catch (DataManagerException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DataManagerException("getNetServerConfigMapList Operation Faild. Reason :" + e.getMessage(), e);
		}
		finally {
			closeSession(session);
		}
	}



	/**
	 * @author  dhavalraval
	 * @param   netServerId
	 * @return  NetServerInstanceData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to get the object of INetServerInstanceData
	 */
	public INetServerInstanceData getNetServerInstance(String netServerId) throws DataManagerException{
		EliteAssert.greaterThanZero(netServerId,"netServerId must be greater than zero.");
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
			EliteAssert.notNull(servermgrDataManager,"Data Manager implementation not found for "+NetServerDataManager.class.getSimpleName());            
			return servermgrDataManager.getNetServerInstance(netServerId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	/** @author  mayurmistry
	 * @param   netServerId
	 * @return  NetServerInstanceData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to get the object of INetServerInstanceData
	 */
	public INetServerInstanceData getNetServerInstance(String netServerId, IDataManagerSession session) throws DataManagerException{
		EliteAssert.greaterThanZero(netServerId,"netServerId must be greater than zero.");
	
		try {
		
			NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
			EliteAssert.notNull(servermgrDataManager,"Data Manager implementation not found for "+NetServerDataManager.class.getSimpleName());            
			return servermgrDataManager.getNetServerInstance(netServerId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	/**
	 * @author  dhavalraval
	 * @author  updated By - kaushikvira 
	 * @param   netServiceId
	 * @return  NetServiceInstanceData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to get the object of INetServiceInstanceData
	 */
	public INetServiceInstanceData getNetServiceInstance(String netServiceId) throws DataManagerException{
		EliteAssert.greaterThanZero(netServiceId,"netServiceId must be greater than zero.");
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
			EliteAssert.notNull(serviceDataManager,"Data Manager implementation not found for "+NetServiceDataManager.class.getSimpleName());            

			INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
			netServiceInstanceData.setNetServiceId(netServiceId);
			return serviceDataManager.getNetServiceInstance(netServiceInstanceData);
		} catch(DataManagerException e){
			throw e;
		} catch(Exception e) {
			throw new DataManagerException("getNetServiceInstance Operation failed. Reason:-"+e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}



	/**
	 * @author  dhavalraval
	 * @param   netServiceInstanceData
	 * @throws  DataManagerException
	 * @purpose This method is generated to Create the NetServiceInstance
	 */
	public void createServiceInstance(INetServiceInstanceData netServiceInstanceData) throws DataManagerException{
		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);

			EliteAssert.notNull(serviceDataManager,"Data Manager Implementation not found for "+NetServiceDataManager.class.getSimpleName());

			session.beginTransaction();
			
			INetServiceInstanceData serviceInstanceData = serviceDataManager.createServiceInstance(netServiceInstanceData);
			List<INetServiceConfigMapData> lstNetServiceConfigMapList = serviceDataManager.getNetServiceConfigMapList(netServiceInstanceData.getNetServiceTypeId(),netServiceInstanceData.getNetServerId());

			for ( INetServiceConfigMapData netServiceConfigMapData : lstNetServiceConfigMapList ) {
				createNetServiceConfigurationInstance(netServiceConfigMapData.getNetConfigId(), serviceInstanceData.getNetServiceId(),session);
			}

			addNetServiceInstanceServerConfiguration(serviceDataManager,serviceInstanceData,session);
			removeEmptyNetServiceInstanceServerConfiguration(serviceDataManager,serviceInstanceData.getNetServerId(),session);

			commit(session);

		}  catch(DataManagerException e){
			e.printStackTrace();
			rollbackSession(session);
			throw e;
		}
		catch(Exception e) {
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	  public void createServiceInstance(INetServiceInstanceData netServiceInstanceData,IDataManagerSession session) throws DataManagerException{
	    	
			NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);

			EliteAssert.notNull(serviceDataManager,"Data Manager Implementation not found for "+NetServiceDataManager.class.getSimpleName());

			INetServiceInstanceData serviceInstanceData = serviceDataManager.createServiceInstance(netServiceInstanceData);

			List<INetServiceConfigMapData> lstNetServiceConfigMapList = serviceDataManager.getNetServiceConfigMapList(netServiceInstanceData.getNetServiceTypeId(),netServiceInstanceData.getNetServerId());

			for ( INetServiceConfigMapData netServiceConfigMapData : lstNetServiceConfigMapList ) {
				createNetServiceConfigurationInstance(netServiceConfigMapData.getNetConfigId(), serviceInstanceData.getNetServiceId(),session);
			}
			
			
			addNetServiceInstanceServerConfiguration(serviceDataManager,serviceInstanceData,session);
			removeEmptyNetServiceInstanceServerConfiguration(serviceDataManager,serviceInstanceData.getNetServerId(),session);
			
	    }
	/*
	 * @author kaushikVira
	 * @Return it give System CurrentTime TimeStemp
	 */
	protected Timestamp getCurrentTimeStemp(){
		return new Timestamp(new Date().getTime());
	}

	private String getNextServiceInstanceId(String instanceId){

		String nextInstanceId = "";
		int index = Integer.valueOf(instanceId).intValue();
		index++;
		nextInstanceId = String.valueOf(index);
		for(int i=nextInstanceId.length();i<3;i++)
			nextInstanceId = "0"+nextInstanceId;

		return nextInstanceId;
	}

	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method  returns the list of all the records of NetServiceInstance table.
	 */
	public List getNetServiceInstanceList() throws DataManagerException{
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);


			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementaion not found for "+getClass().getName());

			return servermgrDataManager.getNetServiceInstanceList();
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}

	}


	/**
	 * @author  dhavalraval
	 * @param   netServerId
	 * @return  Returns List
	 * @throws  DataManagerException
	 * @purpose This method returns the List of all the records of NetServerInstance table.
	 */
	public List getNetServiceInstanceList(String netServerId) throws DataManagerException{
	
		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

			return servermgrDataManager.getNetserviceInstanceList(netServerId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}



	/**
	 * @author  dhavalraval
	 * @param   netServerInstanceData
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the BasicDetails of NetServerInstance.
	 */
	public void updateBasicDetail(INetServiceInstanceData netServiceInstanceData) throws DataManagerException{
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servicemgrDataManager = getNetServiceDataManager(session);
			Timestamp currentDate = new Timestamp((new Date()).getTime());

			if(servicemgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

			session.beginTransaction();
			servicemgrDataManager.updateBasicDetail(netServiceInstanceData,currentDate);
			commit(session);
		} catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}
		catch(Exception e) {
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}


	/**
	 * @author  dhavalraval
	 * @param   lstnetServiceId
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete ServiceInstance.
	 */
	public void deleteService(List<String> lstnetServiceId,String netserverId) throws DataManagerException{

		EliteAssert.notNull(lstnetServiceId,"lstnetServiceId  must be secify");
		EliteAssert.notNull(netserverId,"netserverId  must be secify");

		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();

			NetServerDataManager netServerDataManager =  getNetServerDataManager(session);
			EliteAssert.notNull(netServerDataManager,"Data Manager implementation not found for "+NetServerDataManager.class.getName());

			session.beginTransaction();
			/* Changeing Server sync status - unsync */
			netServerDataManager.changeIsConfigInSyncStaus(netserverId,BaseConstant.HIDE_STATUS_ID,new Timestamp(new Date().getTime()));


			for ( String netServiceId : lstnetServiceId ) {
				deleteNetService(netServiceId,session);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}
		catch(Exception e) {
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void deleteNetService(String netServiceId,IDataManagerSession session) throws DataManagerException{

		EliteAssert.greaterThanZero(netServiceId,"netServiceId");
		EliteAssert.notNull(session,"Session Must Specified");

		NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
		EliteAssert.notNull(serviceDataManager,"Data Managet impl not found for " +NetServiceDataManager.class.getSimpleName());

		Logger.logDebug(MODULE,"-- Start -- Deleting Net Serivce Intance :-" + netServiceId );
		
		Logger.logDebug(MODULE,"Step 1 :- Delete Serivce Configuration from server-config");
		deleteNetServiceInstanceServerConfiguration(serviceDataManager,netServiceId);

		Logger.logDebug(MODULE,"Step 2:- Delete All Service Config Intance");
		List<INetServiceInstanceConfMapData>  lstNetServiceInstanceConfMap = serviceDataManager.getNetServiceInstanceConfMap(netServiceId);
		for ( INetServiceInstanceConfMapData netServiceInstanceConfMapData : lstNetServiceInstanceConfMap ) {
			deleteNetServiceConfigurationInstance(netServiceInstanceConfMapData.getConfigInstanceId(), session);
		}  
		
		Logger.logDebug(MODULE,"Step 3 :- Delete Net Service Instance");
		serviceDataManager.deleteNetServiceInstance(netServiceId);

		Logger.logDebug(MODULE,"-- END -- Deleting Net Serivce Intance :-" + netServiceId );
	}


	public void deleteNetServiceByName(String ServiceName, String netServerInstanceId, IDataManagerSession session) throws DataManagerException{
		NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
		if(serviceDataManager == null){
			throw new DataManagerException("Data Manager implemented not found for " +getClass().getName());
		}
		try{
			
			NetServiceInstanceData netServiceInstanceData = serviceDataManager.getServiceDetailByServiceNameAndServerInstanceId(ServiceName, netServerInstanceId);
			String netServiceId = netServiceInstanceData.getNetServiceId();
			
			Logger.logDebug(MODULE,"Step 1 :- Delete Serivce Configuration from server-config");
			deleteNetServiceInstanceServerConfiguration(serviceDataManager,netServiceId);

			Logger.logDebug(MODULE,"Step 2:- Delete All Service Config Intance");
			List<INetServiceInstanceConfMapData>  lstNetServiceInstanceConfMap = serviceDataManager.getNetServiceInstanceConfMap(netServiceId);
			for ( INetServiceInstanceConfMapData netServiceInstanceConfMapData : lstNetServiceInstanceConfMap ) {
				deleteNetServiceConfigurationInstance(netServiceInstanceConfMapData.getConfigInstanceId(), session);
			}  
			
			Logger.logDebug(MODULE,"Step 3 :- Delete Net Service Instance");
			serviceDataManager.deleteNetServiceInstance(netServiceId);

			Logger.logDebug(MODULE,"-- END -- Deleting Net Serivce Intance :-" + netServiceId );
			
		}catch(DataManagerException dbe){
			throw dbe;
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete Service By Name : "+e.getMessage(),e);
		}
		
		
		
	}
	


	public INetConfigurationInstanceData getConfigurationInstance(String configInstanceId) throws DataManagerException {

		EliteAssert.greaterThanZero(configInstanceId,"configInstanceId");

		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getConfigurationInstance(configInstanceId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	@Override
	public INetConfigurationData getRootParameterConfigurationData(String configId) throws DataManagerException {
		EliteAssert.notNull(configId);
		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getRootParameterConfigurationData(configId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}



	public byte[] getServiceConfigurationStream(String serviceId, String configId) throws DataManagerException {
		EliteAssert.notNull(configId);
		EliteAssert.greaterThanZero(serviceId,"serviceId");
		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			INetConfigurationData netConfigurationData = getRootParameterConfigurationData(configId);
			INetConfigurationInstanceData netConfigurationInstanceData = servermgrDataManager.getServiceConfigurationInstanceData(serviceId, configId);
			return createOutputStream(netConfigurationData,netConfigurationInstanceData.getConfigInstanceId());
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		} 
	}


	

	/**
	 * @author dhavan
	 * @param parameterId
	 * @return
	 * @throws DataManagerException
	 */
	public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId,String configId)throws DataManagerException {

		EliteAssert.notNull(parameterId);
		EliteAssert.notNull(configId);

		IDataManagerSession session = null;
		try {
			session =   DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getNetConfigurationParameterData(parameterId,configId);
		}
		catch(DataManagerException e){
			throw e;
		}
		catch(Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		} 

	}

	/**
	 * This method will save the updaeted node in the database.
	 * It will delete remove existing configuration for particular
	 *  instanceId and add the new updated configuration.
	 *  @author dhavan
	 *  
	 * @param lstValueData
	 * @param strConfigInstanceId
	 * @throws DataManagerException
	 */
	public void saveNetConfigurationValues(List<INetConfigurationValuesData> lstValueData,String configInstanceId,String serviceInstanceId)throws DataManagerException {

		EliteAssert.notNull(lstValueData,"lstValueData Must be Specified.");
		EliteAssert.greaterThanZero(configInstanceId,"configInstanceId");
		EliteAssert.greaterThanZero(serviceInstanceId,"serviceInstanceId");

		IDataManagerSession session =null;
		try{

			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
			EliteAssert.notNull(serviceDataManager,"Data Manager implementation not found for " +NetServiceDataManager.class.getSimpleName());
			session.beginTransaction();
			serviceDataManager.saveNetConfValuesData(lstValueData,configInstanceId);
			serviceDataManager.changeIsConfigInSyncStaus(serviceInstanceId,BaseConstant.HIDE_STATUS_ID, getSystemTimeStemp());
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}
		catch(Exception exp){
			rollbackSession(session);
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}


	}
	
	/**
	 * @author dhavan
	 * This method will give list of Parameters and values based on configInstanceId
	 * @param strConfigInstatnceId
	 * @return
	 */
	public INetConfigurationParameterData getConfigurationParameterValues(String configInstanceId)throws DataManagerException{

		EliteAssert.greaterThanZero(configInstanceId,"configInstatnceId");
		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
			NetConfigurationParameterData netConfParamData=null;

			//Get Configid based on configInstanceId
			INetConfigurationInstanceData  netConfInstanceData=servermgrDataManager.getConfigurationInstance(configInstanceId);
			if(netConfInstanceData !=null){
				String strConfigId = netConfInstanceData.getConfigId();
				Logger.logTrace(MODULE,"strConfigId :"+ strConfigId);

				// Get Root Element by ConfigId 
				INetConfigurationData netConfigurationData = servermgrDataManager.getRootParameterConfigurationData(strConfigId);

				Set setParameters= netConfigurationData.getNetConfigParameters();

				if(setParameters!=null){
					Iterator itr = setParameters.iterator();
					while(itr.hasNext()){
						netConfParamData = (NetConfigurationParameterData)itr.next();
					}
				}
			}

			return netConfParamData;
		}
		catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}


	}

	public String getNetConfigurationName(String configInstanceId)throws DataManagerException{
		EliteAssert.greaterThanZero(configInstanceId,"configInstatnceId");

		IDataManagerSession session =null;
		try{
			session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
			return servermgrDataManager.getNetConfigurationName(configInstanceId);
		}catch(DataManagerException exp){
			Logger.logTrace(MODULE,"Problem while getting configuration parameters and values");
			throw exp;
		}
		catch(Exception exp){
			Logger.logTrace(MODULE,"Problem while getting configuration parameters and values");
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}
	}

	public String getNetServiceNameServiceConfig(String configInstanceId)throws DataManagerException{
		EliteAssert.greaterThanZero(configInstanceId,"configInstatnceId");

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
			return servermgrDataManager.getNetServiceNameServiceConfig(configInstanceId);
		}catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}
	}


	public List getServiceConfigurationInstance(String serviceInstanceId)throws DataManagerException{
		EliteAssert.greaterThanZero(serviceInstanceId,"serviceInstanceId");

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getNetServiceConfigInstanceList(serviceInstanceId);
		}
		catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}

	}


	public INetServiceTypeData getNetServiceType(String serviceTypeId)throws DataManagerException{
		EliteAssert.notNull(serviceTypeId);

		IDataManagerSession session =null;
		try{
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			return servermgrDataManager.getNetServiceType(serviceTypeId);
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public INetConfigurationParameterData getNetConfigParameterData(INetServiceInstanceData netServiceInstanceData,String alias) throws DataManagerException{
		EliteAssert.notNull(netServiceInstanceData);
		EliteAssert.notNull(alias);

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getNetConfigurationParameterData(netServiceInstanceData,alias);
		}
		catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}
	}


	/****************************************************************************************************/

	/**
	 * @author  Dhaval Raval
	 * @param   netServiceId
	 * @return  Returns List
	 * @throws  DataManagerException
	 * @purpose This method is generatd to get list of ServiceInstanceConfMapData by using configInstanceId
	 */
	public List getNetServiceConfigInstanceList(String netServiceId)throws DataManagerException {
		EliteAssert.greaterThanZero(netServiceId,"netServiceId");

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			List lstNetServiceConfigInstanceList = null;

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager Implementation not found for "+getClass().getName());


			lstNetServiceConfigInstanceList = servermgrDataManager.getNetServiceConfigInstanceList(netServiceId);

			for(int i=0;i<lstNetServiceConfigInstanceList.size();i++){
				NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstNetServiceConfigInstanceList.get(i); 
				netConfigInstanceData.setNetConfiguration(servermgrDataManager.getNetConfigurationData(netConfigInstanceData.getConfigId()));
			}
			return lstNetServiceConfigInstanceList;
		}  catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}

	}

	/**
	 * @author  Dhaval Raval
	 * @param   netServiceId
	 * @return  Returns List
	 * @throws  DataManagerException
	 * @purpose This method is generatd to get list of ServiceInstanceConfMapData by using configInstanceId
	 */
	public List getNetServiceConfigInstanceList(String netServiceId,String netConfigMapTypeId) throws DataManagerException{
		EliteAssert.notNull(netConfigMapTypeId);
		EliteAssert.greaterThanZero(netServiceId,"netServiceId");

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			List lstNetServiceConfigInstanceList = null;

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager Implementation not found for "+getClass().getName());
			lstNetServiceConfigInstanceList = servermgrDataManager.getNetServiceConfigInstanceList(netServiceId,netConfigMapTypeId);

			for(int i=0;i<lstNetServiceConfigInstanceList.size();i++){
				NetConfigurationInstanceData netConfigurationInstanceData = (NetConfigurationInstanceData)lstNetServiceConfigInstanceList.get(i);
				netConfigurationInstanceData.setNetConfiguration(servermgrDataManager.getNetConfigurationData(netConfigurationInstanceData.getConfigId()));
			}
			return lstNetServiceConfigInstanceList;

		}catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}

	}


	/**
	 * @author  vaseem
	 * @param   netServiceInstanceData
	 * @throws  DataManagerException
	 * @purpose This method is generated to add Service Instance to existing list in Server Configuration
	 */

	private void addNetServiceInstanceServerConfiguration(NetServiceDataManager servermgrDataManager, INetServiceInstanceData netServiceInstanceData,IDataManagerSession session) throws DataManagerException{
		EliteAssert.notNull(servermgrDataManager,"servermgrDataManager Must Specified");
		EliteAssert.notNull(netServiceInstanceData,"netServiceInstanceData Must Specified");

		try {

			INetServerInstanceData serverInstanceData = getNetServerInstance(netServiceInstanceData.getNetServerId(),session);
			INetServiceTypeData netServiceTypeData = servermgrDataManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());

			if(netServiceTypeData != null){

				INetConfigurationParameterData netConfigurationParameterData = servermgrDataManager.getNetConfigurationParameterData(serverInstanceData,service);
				INetConfigurationParameterData netListConfigurationParameterData = servermgrDataManager.getNetConfigurationParameterData(serverInstanceData,serviceList);				
				if(netConfigurationParameterData != null){

					HashMap map = new HashMap();
					map.put(service,"");
					//map.put(serviceName,netServiceTypeData.getAlias());				
					map.put(serviceId,netServiceTypeData.getAlias());
					//map.put(serviceInstanceName,netServiceInstanceData.getName());
					Logger.logInfo(MODULE, " netServiceInstanceData.getName()..."+netServiceInstanceData.getName());
					INetConfigurationInstanceData netConfigurationInstance = servermgrDataManager.getServerConfigurationInstanceData(netServiceInstanceData.getNetServerId(), netConfigurationParameterData.getConfigId());

					List instanceList = servermgrDataManager.getNetConfigParameterValueList(netConfigurationInstance.getConfigInstanceId(),netConfigurationParameterData.getParameterId());
					List lstServiceList = servermgrDataManager.getNetConfigParameterValueList(netConfigurationInstance.getConfigInstanceId(),netListConfigurationParameterData.getParameterId());

					if(lstServiceList == null || !(lstServiceList.size() > 0)){
						throw new DataManagerException("Update Server Configuration failed");
					}

					String parentInstance = ((INetConfigurationValuesData)lstServiceList.get(0)).getInstanceId();
					String instanceId = getNextConfigurationValueInstance(instanceList,parentInstance);
					List lstNewParameters = new ArrayList();
					lstNewParameters = getRecursiveNewNetConfParameterData(netConfigurationParameterData,lstNewParameters,netConfigurationInstance.getConfigInstanceId(),instanceId,map);
					servermgrDataManager.addNetConfValuesData(lstNewParameters);
				}else{
					throw new DataManagerException("Update Server Configuration failed");				
				}
			}else{
				throw new DataManagerException("Update Server Configuration failed");				
			}


		}catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}

	}

	private List getRecursiveNewNetConfParameterData(INetConfigurationParameterData netConfParamData,List lstParameters, String configInstanceId, String instanceId, HashMap map){

		INetConfigurationValuesData netConfigurationValuesData = new NetConfigurationValuesData();
		netConfigurationValuesData.setConfigInstanceId(configInstanceId);
		netConfigurationValuesData.setInstanceId(instanceId);
		netConfigurationValuesData.setParameterId(netConfParamData.getParameterId());
		netConfigurationValuesData.setConfigId(netConfParamData.getConfigId());


		String value = "";
		if(map.get(netConfParamData.getAlias()) != null){
			value = map.get(netConfParamData.getAlias()).toString();

		}else{
			value = netConfParamData.getDefaultValue();

		}
		netConfigurationValuesData.setValue(value);

		lstParameters.add(netConfigurationValuesData);

		Iterator itrNetConfigParameters = netConfParamData.getNetConfigChildParameters().iterator();
		while(itrNetConfigParameters.hasNext()){
			INetConfigurationParameterData netConfigParamChildData = (INetConfigurationParameterData)itrNetConfigParameters.next();
			lstParameters = getRecursiveNewNetConfParameterData(netConfigParamChildData ,lstParameters,configInstanceId,instanceId+"."+netConfigParamChildData.getSerialNo(),map);
		}
		return lstParameters;
	}


	private String getNextConfigurationValueInstance(List configurationValueList, String parentInstance){

		String maxInstance = "";
		Collections.sort(configurationValueList);
		if(configurationValueList.size() > 0){
			INetConfigurationValuesData netConfigValuesData = (INetConfigurationValuesData)configurationValueList.get(configurationValueList.size()-1);
			StringTokenizer token = new StringTokenizer(netConfigValuesData.getInstanceId(),".");
			String lastToken = "";
			while(token.hasMoreTokens())lastToken = token.nextToken();

			maxInstance = netConfigValuesData.getInstanceId().substring(0,netConfigValuesData.getInstanceId().lastIndexOf("."));
			maxInstance = maxInstance+"."+(Integer.parseInt(lastToken)+1);
		}else{
			maxInstance = parentInstance+".1";
		}
		Logger.logInfo(MODULE, " maxInstance : "+maxInstance);
		return maxInstance;
	}




	private void removeEmptyNetServiceInstanceServerConfiguration(NetServiceDataManager serviceDataManager, String netServerId,IDataManagerSession session) throws DataManagerException{
		EliteAssert.notNull(serviceDataManager);
		EliteAssert.greaterThanZero(netServerId);

		try {
			INetServerInstanceData serverInstanceData = getNetServerInstance(netServerId,session);

			INetConfigurationParameterData netConfigurationParameterData = serviceDataManager.getNetConfigurationParameterData(serverInstanceData,service);
			INetConfigurationParameterData netConfigurationParameterDataName = serviceDataManager.getNetConfigurationParameterData(serverInstanceData,serviceId);				
			if(netConfigurationParameterData != null && netConfigurationParameterDataName != null){
				INetConfigurationInstanceData netConfigurationInstance = serviceDataManager.getServerConfigurationInstanceData(netServerId, netConfigurationParameterData.getConfigId());
				List serviceInstanceList = serviceDataManager.getNetConfigParameterValueList(netConfigurationInstance.getConfigInstanceId(),netConfigurationParameterData.getParameterId());
				List serviceNameInstanceList = serviceDataManager.getNetConfigParameterValueList(netConfigurationInstance.getConfigInstanceId(),netConfigurationParameterDataName.getParameterId());					
				List deleteServiceList = new ArrayList();
				for(int i=0;i<serviceInstanceList.size();i++){
					INetConfigurationValuesData serviceValue = (INetConfigurationValuesData)serviceInstanceList.get(i);

					for(int j=0;j<serviceNameInstanceList.size();j++){
						INetConfigurationValuesData serviceNameValue = (INetConfigurationValuesData)serviceNameInstanceList.get(j);
						if(serviceNameValue.getInstanceId().trim().startsWith(serviceValue.getInstanceId().trim())
								&& serviceNameValue.getInstanceId().trim().substring(serviceValue.getInstanceId().trim().length(),serviceNameValue.getInstanceId().trim().length()).trim().startsWith(".")		
						){
							if(serviceNameValue.getValue() == null || serviceNameValue.getValue().equalsIgnoreCase("")){
								deleteServiceList.add(serviceValue);
							}
						}
					}
				}

				List lstNewParameters = new ArrayList();
				lstNewParameters = getRecursiveNetConfigurationParameterValues(netConfigurationParameterData,netConfigurationInstance.getConfigInstanceId(),lstNewParameters,deleteServiceList,session);
				lstNewParameters.addAll(deleteServiceList);
				serviceDataManager.removeNetConfValuesData(lstNewParameters);
			}
		} catch (DataManagerException hExp) {
			throw hExp;
		}
		catch (Exception hExp) {
			throw new DataManagerException("Create Action failed :"+hExp.getMessage(),hExp);
		}
	}

	private List getRecursiveNetConfigurationParameterValues( INetConfigurationParameterData netConfParamData , String configInstanceId , List lstParameters , List parentInstanceList ) {
		Set stConfigInstanceValues = new HashSet();

		Iterator itrConfigParamValues = netConfParamData.getNetConfigParamValues().iterator();
		if (itrConfigParamValues != null && itrConfigParamValues.hasNext()) {
			while (itrConfigParamValues.hasNext()) {
				INetConfigurationValuesData netConfigParamValues = (INetConfigurationValuesData) itrConfigParamValues.next();
				if (netConfigParamValues.getConfigInstanceId().equals(configInstanceId)) {
					for ( int i = 0; i < parentInstanceList.size(); i++ ) {
						INetConfigurationValuesData netConfigParentParamValues = (INetConfigurationValuesData) parentInstanceList.get(i);
						if (netConfigParamValues.getInstanceId().startsWith(netConfigParentParamValues.getInstanceId()) && netConfigParamValues.getInstanceId().substring(netConfigParentParamValues.getInstanceId().length(), netConfigParamValues.getInstanceId().length()).startsWith(".")) {
							lstParameters.add(netConfigParamValues);
						}
					}
				}

			}
			//netConfParamData.setNetConfigParamValues(stConfigInstanceValues);
		}

		if(netConfParamData.getNetConfigChildParameters() != null ){
			Iterator itrConfigChildParams = netConfParamData.getNetConfigChildParameters().iterator();
			while(itrConfigChildParams.hasNext()){
				NetConfigurationParameterData netChildConfParamData = (NetConfigurationParameterData)itrConfigChildParams.next();
				lstParameters = getRecursiveNetConfigurationParameterValues(netChildConfParamData,configInstanceId,lstParameters,parentInstanceList);
			}
		}
		return lstParameters;
	}	
	private List getRecursiveNetConfigurationParameterValues( INetConfigurationParameterData netConfParamData , String configInstanceId , List lstParameters , List parentInstanceList ,IDataManagerSession session)  throws DataManagerException{
		
		NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
		
		
		List lstConfigParamValues = serviceDataManager.getNetConfigParameterValueList(configInstanceId, netConfParamData.getParameterId());
	
		Iterator itrConfigParamValues = lstConfigParamValues.iterator();
		if (itrConfigParamValues != null && itrConfigParamValues.hasNext()) {
			while (itrConfigParamValues.hasNext()) {
				INetConfigurationValuesData netConfigParamValues = (INetConfigurationValuesData) itrConfigParamValues.next();

				if (netConfigParamValues.getConfigInstanceId().equals(configInstanceId)) {
					for ( int i = 0; i < parentInstanceList.size(); i++ ) {
						INetConfigurationValuesData netConfigParentParamValues = (INetConfigurationValuesData) parentInstanceList.get(i);
						if (netConfigParamValues.getInstanceId().startsWith(netConfigParentParamValues.getInstanceId()) && netConfigParamValues.getInstanceId().substring(netConfigParentParamValues.getInstanceId().length(), netConfigParamValues.getInstanceId().length()).startsWith(".")) {
							lstParameters.add(netConfigParamValues);
						}
					}
				}

			}
			//netConfParamData.setNetConfigParamValues(stConfigInstanceValues);
		}

		if(netConfParamData.getNetConfigChildParameters() != null ){
			Iterator itrConfigChildParams = netConfParamData.getNetConfigChildParameters().iterator();
			while(itrConfigChildParams.hasNext()){
				NetConfigurationParameterData netChildConfParamData = (NetConfigurationParameterData)itrConfigChildParams.next();

				lstParameters = getRecursiveNetConfigurationParameterValues(netChildConfParamData,configInstanceId,lstParameters,parentInstanceList,session);
			}
		}
		return lstParameters;
	}	

	public INetServiceTypeData getNetServiceType(String serverTypeId, String netServiceName)throws DataManagerException{
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getNetServiceType(serverTypeId, netServiceName);
		}
		catch (DataManagerException hExp) {
			throw hExp;
		}
		catch (Exception hExp) {
			throw new DataManagerException("Create Action failed :"+hExp.getMessage(),hExp);
		}finally {
			closeSession(session);
		}
	}


	public void updateServiceDetails(String netServiceId, EliteNetServiceData eliteNetServiceData, String staffId,String isInSync)throws DataManagerException{

		EliteAssert.greaterThanZero(netServiceId,"netServiceId");
		EliteAssert.notNull(eliteNetServiceData);
		EliteAssert.notNull(staffId,"staffId");
		EliteAssert.notNull(isInSync);

		IDataManagerSession session = null;
		try {
			session =  DataManagerSessionFactory.getInstance().getDataManagerSession();

			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if (servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			session.beginTransaction();
			INetServiceInstanceData netServiceInstanceData = getNetServiceInstance(netServiceId);
			servermgrDataManager.changeIsConfigInSyncStaus(netServiceId,isInSync,new Timestamp(new Date().getTime()));


			List serverConfigList = getServiceConfigurationInstance(netServiceInstanceData.getNetServiceId());
			if(eliteNetServiceData.getNetConfigurationList() != null){
				for(int serviceConfigIndex=0;serviceConfigIndex<eliteNetServiceData.getNetConfigurationList().size();serviceConfigIndex++){
					EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)eliteNetServiceData.getNetConfigurationList().get(serviceConfigIndex);
					for(int i=0;i<serverConfigList.size();i++){
						INetConfigurationInstanceData configInstanceData = (INetConfigurationInstanceData)serverConfigList.get(i);
						INetConfigurationData configData = configInstanceData.getNetConfiguration();

						if(configData.getAlias().equalsIgnoreCase(eliteNetConfigurationData.getNetConfigurationKey())){
							overwriteServiceConfigurationValues(servermgrDataManager, eliteNetConfigurationData.getNetConfigurationData(),netServiceId, configInstanceData);
							break;
						}
					}
				}
			}else{
				//TODO : Remove Service Configuration
			}


			commit(session);
		}
		catch (DataManagerException hExp) {
			rollbackSession(session);
			throw hExp;
		}
		catch (Exception hExp) {
			rollbackSession(session);
			throw new DataManagerException("Create Action failed :"+hExp.getMessage(),hExp);
		}finally {
			closeSession(session);
		}


	}
 
	
	public INetConfigurationData overwriteServiceConfigurationValues(NetServiceDataManager servermgrDataManager, byte[] sourceBytes, String serviceId, INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException {

		EliteAssert.notNull(netConfigurationInstanceData,"netConfigurationInstanceData Must be specified.");
		EliteAssert.notNull(sourceBytes);
		EliteAssert.notNull(netConfigurationInstanceData);

		INetConfigurationData netConfigurationData = new NetConfigurationData();
		try{
			ByteArrayInputStream iServiceStream = new ByteArrayInputStream(sourceBytes);
			String configId = netConfigurationInstanceData.getConfigId();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();                    
			DocumentBuilder db = dbf.newDocumentBuilder();                    
			Document xmlDoc =  db.parse(iServiceStream);

			netConfigurationData = getRootParameterConfigurationData(configId);

			xmlDoc.getDocumentElement ().normalize ();

			Iterator itrNetConfigParameters = netConfigurationData.getNetConfigParameters().iterator();

			if(itrNetConfigParameters.hasNext()){
				INetConfigurationParameterData netConfigParamData = (INetConfigurationParameterData)itrNetConfigParameters.next();
				NodeList lstDetailObjectList = xmlDoc.getElementsByTagName(netConfigParamData.getAlias());
				if(lstDetailObjectList.getLength() > 0){
					netConfigParamData = flushParameterValue(netConfigParamData);				
					netConfigParamData = generateDataObjectParseNode(netConfigParamData,lstDetailObjectList.item(0),netConfigurationInstanceData.getConfigInstanceId(),"1");
				}
				Set stConfigParamRootObject = new HashSet();
				stConfigParamRootObject.add(netConfigParamData);
				netConfigurationData.setNetConfigParameters(stConfigParamRootObject);
				List netConfigValues = new ArrayList();
				netConfigValues = getRecursiveNetConfigurationParameterValues(netConfigParamData,"0",netConfigValues);

				servermgrDataManager.saveNetConfValuesData(netConfigValues,netConfigurationInstanceData.getConfigInstanceId());
			}else{
				throw new DataManagerException("Update Basic Detail Failed : Root Parameter Not Found");				
			}
		}
		catch(DataManagerException e) {
			throw e;
		}
		catch(Throwable e){
			throw new DataManagerException("Update Basic Detail Failed "+e.getMessage(),e);    	
		}
		return netConfigurationData;
	}



	/**
	 *@param IDataManagerSession,netServerIntanceId
	 *@return void
	 *@author kaushikvira
	 *@Purpose : if any new configuration after creating instance of Service that should be a add to existing configuration instance.
	 *            This method checks new Configuration Added In Template and Creating new ConfigurationIntance In Existing ServiceIntance at Service/driver Level
	 */ 
	public void addNewServiceConfiguration(String netserviceIntanceId) throws DataManagerException {
		IDataManagerSession session = null;
		EliteAssert.greaterThanZero(netserviceIntanceId,"netserviceIntanceId");
		try { 

			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager netServiceDataManager = getNetServiceDataManager(session);
			EliteAssert.notNull(netServiceDataManager,"Data Manager implementation not found for " + NetServiceBLManager.class.getSimpleName());

			session.beginTransaction();

			INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
			netServiceInstanceData.setNetServiceId(netserviceIntanceId);
			netServiceInstanceData = netServiceDataManager.getNetServiceInstance(netServiceInstanceData);
			//netServiceInstanceData = netServiceDataManager.getNetServiceInstanceData(netserviceIntanceId);
			Logger.logInfo(MODULE, " ::: netServiceInstanceData ::: "+netServiceInstanceData);
			List netServiceconfigInstanceMapList =  netServiceDataManager.getNetServiceConfigInstanceList(netserviceIntanceId);

			/*Function Call Changed For Version Mgmt. - kaushik  */
			List netServiceConfigMapList = netServiceDataManager.getNetServiceConfigMapList(netServiceInstanceData.getNetServiceTypeId(),netServiceInstanceData.getNetServerId());
			EliteServerAssert.notNull(netServiceConfigMapList);

			if (netServiceConfigMapList.size() > netServiceconfigInstanceMapList.size()) {
				Logger.logDebug(MODULE,netServiceConfigMapList.size() - netServiceconfigInstanceMapList.size() + "  New Configuration found for serviceInstanceId :-" + netserviceIntanceId);
				INetConfigurationInstanceData tempNetServiceConfigData = null;
				INetServiceConfigMapData tempNetSericeConfigMapData = null;

				Set tempHashSet = new HashSet();
				Iterator itNetServerConfigMapList = netServiceconfigInstanceMapList.iterator();
				while (itNetServerConfigMapList.hasNext()) {
					tempNetServiceConfigData = (NetConfigurationInstanceData) itNetServerConfigMapList.next();
					tempHashSet.add(tempNetServiceConfigData.getConfigId());
				}

				itNetServerConfigMapList = netServiceConfigMapList.iterator();
				while (itNetServerConfigMapList.hasNext()) {
					tempNetSericeConfigMapData = (NetServiceConfigMapData) itNetServerConfigMapList.next();

					if (!tempHashSet.contains(tempNetSericeConfigMapData.getNetConfigId())){
						createNetServiceConfigurationInstance(tempNetSericeConfigMapData.getNetConfigId(), netserviceIntanceId, session);
					}
				}
			}

			commit(session);
		}
		catch(AddNewDriverConfigOpFailedException e){
			//e.printStackTrace();
			rollbackSession(session);
			throw e;
		}
		catch (Exception  exp) {
			//exp.printStackTrace();
			rollbackSession(session);
			throw new AddNewServiceConfigOpFailedException("Not able to Add new  in Existing ServiceIntance. Reason :" + exp.getMessage(),exp);
		}
		finally{
			closeSession(session);
		}
	}


	/**
	 * @author  kaushik Vira.
	 * @return  void
	 * @purpose This method is Will mark server in sync. also mark all services of this server as sync.
	 * @throws  DataManagerException
	 */
	public void markServiceisInSync(String netServiceIntanceId) throws DataManagerException {
		EliteAssert.greaterThanZero(netServiceIntanceId,"netServiceIntanceId");


		IDataManagerSession session = null;

		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager netServiceDataManager = getNetServiceDataManager(session);

			if (netServiceDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + NetServiceDataManager.class.getName());


			session.beginTransaction();
			netServiceDataManager.changeIsConfigInSyncStaus(netServiceIntanceId,BaseConstant.SHOW_STATUS_ID,new Timestamp(new Date().getTime()));
			commit(session);
		}
		catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}
		catch(Exception e) {
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}
		finally  {
			closeSession(session);
		}
	}



	public void upgradeNetService( String netServiceInstanceId,INetServerInstanceData netServerIntanceData, String targetedServerVersion,IDataManagerSession session ) throws DataManagerException {
		EliteAssert.greaterThanZero(netServiceInstanceId, "netServiceInstanceId");
		EliteAssert.notNull(targetedServerVersion, "Targeted Server Version Must be specified.");
		EliteAssert.notNull(session, "session Must be specified.");
		try {
			NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
			EliteAssert.notNull(serviceDataManager, "Data Manager implementation not found for " + NetServiceDataManager.class.getSimpleName());

			/*
			 * Getting netServerInstance Data it contains all the information service instance.
			 */
			INetServiceInstanceData netSerivceIntanceData = getNetServiceInstance(netServiceInstanceId);

			Logger.logDebug(MODULE, "--Start--  Upgrade Net Service Started.");
			Logger.logDebug(MODULE, "netServiceInstanceId :-" + netSerivceIntanceData.getNetServiceId());
			Logger.logDebug(MODULE, "ServiceTypeId :-" + netSerivceIntanceData.getNetServiceTypeId());
			Logger.logDebug(MODULE, "Targeted Version :-" + targetedServerVersion);

			/*
			 * Getting Set of netConfigurationInstance Data For comparing
			 * configuration Version. This Block will Collect Information about
			 * Service instance version.
			 */

			List<INetServiceInstanceConfMapData> lstNetServiceInstanceConfMapData = serviceDataManager.getNetServiceInstanceConfMap(netServiceInstanceId);

			Set<INetConfigurationInstanceData> setNetConfigurationInstanceData = new HashSet<INetConfigurationInstanceData>();
			for ( INetServiceInstanceConfMapData netServiceInstanceConfMapData : lstNetServiceInstanceConfMapData ) {
				setNetConfigurationInstanceData.add(netServiceInstanceConfMapData.getNetConfigurationInstance());  
			}

			Logger.logDebug(MODULE,"Step 1.1: Identify upgradation");

			/* temporary list used in next to next step */

			List<String> tempLstForContainsCheck = new ArrayList<String>();

			/* Comparing configuration */
			Map<INetConfigurationInstanceData, String> configurationComparisonMap = new HashMap<INetConfigurationInstanceData, String>();
			List<INetConfigurationInstanceData> lstRemovedConfiguration = new ArrayList<INetConfigurationInstanceData>();

			for ( INetConfigurationInstanceData netConfigurationInstanceData : setNetConfigurationInstanceData ) {

				INetConfigurationData netConfigurationData = getUpgradeVersionOfServiceConfiguration(netConfigurationInstanceData.getConfigId(),netSerivceIntanceData.getNetServiceTypeId() , netServerIntanceData.getNetServerTypeId(), targetedServerVersion);
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
			List<INetServiceConfigMapData> lstTargetedNetServiceConfigMapData = getNetServiceConfigMapList(netSerivceIntanceData.getNetServiceTypeId(),netServerIntanceData.getNetServerTypeId(),targetedServerVersion);
			List<INetServiceConfigMapData> lstnewAddedConfiguration = new ArrayList<INetServiceConfigMapData>();
			for ( INetServiceConfigMapData netServerConfigMapData : lstTargetedNetServiceConfigMapData ) {
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
			for(INetServiceConfigMapData netServerConfigMapData : lstnewAddedConfiguration ) {
				createNetServiceConfigurationInstance(netServerConfigMapData.getNetConfigId(),netServiceInstanceId, session);
			}

			Logger.logDebug(MODULE,"Step 1.1.3: Delete Missing Configuration");
			for(INetConfigurationInstanceData netConfigurationInstanceData : lstRemovedConfiguration ) {
				deleteNetServiceConfigurationInstance(netConfigurationInstanceData.getConfigInstanceId(), session);
			}



			Logger.logDebug(MODULE, "-- End --  Upgrade NetService ServerInstanceId:" + netServerIntanceData.getNetServerId() + " Status : Successful");

		}
		catch(DataManagerException e) {
			throw new DataManagerException(e);
		}
		catch (Exception e) {
			throw new DataManagerException(e);
		}
	}

	/**
	 * @author kaushik vira
	 * @param  configTypeId - identify configuration Type.
	 * @param  netServerType - Server Type
	 * @param  netServerVersion - Server Version
	 * @return Compatible Upgrade Version of ConfigurationData
	 */
	public INetConfigurationData getUpgradeVersionOfServiceConfiguration(String configTypeId,String netServiceType,String netServerType,String netServerVersion) throws DataManagerException {

		EliteAssert.notNull(configTypeId,"configTypeId must be Specified.");
		EliteAssert.notNull(configTypeId,"netServiceType must be Specified.");
		EliteAssert.notNull(netServerType,"NetServer Type must be Specified.");
		EliteAssert.notNull(netServerVersion,"NetServer Version Must be specified.");

		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServerDataManager serverDataManager = getNetServerDataManager(session);
			EliteAssert.notNull(serverDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());

			INetConfigurationData updgradeServiceConfiguration = getUpgradeConfigurationVersion(configTypeId, netServerType, netServerVersion, session);

			System.out.println("~~~Service Config Upgrade"+updgradeServiceConfiguration.getNetConfigId());

			if(isServiceLevelConfiguration(updgradeServiceConfiguration, netServiceType,netServerType,netServerVersion,session))
				return updgradeServiceConfiguration;
			return null;

		}
		catch(DataManagerException e) {
			throw e;
		}catch(Exception e) {
			throw new DataManagerException("getCompatibleUpgradeVersionOfConfigurationData Operation Faild. Reason :"+e.getMessage(),e);
		}finally {
			closeSession(session);
		} 
	}    

	private boolean isServiceLevelConfiguration(INetConfigurationData netConfigurationData,String netServiceTypeId,String netServerTypeId,String upgradeVersion,IDataManagerSession session) throws DataManagerException{

		EliteAssert.notNull(session,"session  must be Specified.");
		EliteAssert.notNull(netServiceTypeId,"netServiceTypeId  must be Specified.");
		EliteAssert.notNull(netServerTypeId,"netServerTypeId  must be Specified.");
		EliteAssert.notNull(upgradeVersion,"upgradeVersion  must be Specified.");

		if(netConfigurationData == null)
			return false;

		NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);
		EliteAssert.notNull(serviceDataManager,"Data Manager implementation not found for " + NetServiceDataManager.class.getSimpleName());

		List<INetServiceConfigMapData> netServiceCofigMapData = serviceDataManager.getNetServiceConfigMapList(netServiceTypeId,netServerTypeId,upgradeVersion);
		for ( INetServiceConfigMapData netServerConfigMapData : netServiceCofigMapData ) {
			if(netServerConfigMapData.getNetConfigId().equals(netConfigurationData.getNetConfigId()))
				return true;
		}
		return false;
	}


	/**
	 * @author kaushik vira
	 * @param netServerTypeId - Identify  Server Type
	 * @param targetedServerVersion - Target Server Version
	 * @return List<INetServerConfigMapData> - all the ServerCofigMap Data for
	 *         targetedServerVersion of netServerTypeId.
	 */
	public List<INetServiceConfigMapData> getNetServiceConfigMapList( String netServerTypeId , String netServerId ) throws DataManagerException {
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager serverDataManager = getNetServiceDataManager(session);
			return serverDataManager.getNetServiceConfigMapList(netServerTypeId, netServerId);
		}
		catch (DataManagerException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DataManagerException("getNetServerConfigMapList Operation Faild. Reason :" + e.getMessage(), e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @author kaushik vira
	 */
	private void createNetServiceConfigurationInstance( String configTypeId , String netServiceInstanceId , IDataManagerSession session ) throws DataManagerException {

		Logger.logDebug(MODULE, "--START ADD--New Configuration:- " + configTypeId +" for " + netServiceInstanceId);

		/* --start-- Adding new Configuration in Service Instance  */
		EliteAssert.notNull(configTypeId, "configInstanceId must Specified.");
		EliteAssert.notNull(session, "session must Specified.");

		NetServiceDataManager netserviceDataManager = getNetServiceDataManager(session);
		EliteAssert.notNull(netserviceDataManager, "Data Manager implementation not found for " + NetServiceDataManager.class.getSimpleName());

		/*
		 * Steps : Create ServerConfiguration.
		 * 1:- Create Entry from  TBLMNETCONFIGURATIONINSTANCE.
		 * 2:- Create Entry form  TBLMNETSERVICEINSTANCECONFMAP
		 * 3:- Create All Entry from  TBLMNETCONFIGURATIONVALUES.
		 */

		Logger.logDebug(MODULE, "--START ADD-- New Configuration:- " + configTypeId + " for " + netServiceInstanceId);

		/* Step 1: */
		INetConfigurationInstanceData newNetConfigurationInstance = new NetConfigurationInstanceData();
		newNetConfigurationInstance.setConfigId(configTypeId);
		INetConfigurationInstanceData netConfigurationInstance = netserviceDataManager.createNetConfigurationInstance(newNetConfigurationInstance);
		Logger.logDebug(MODULE, "Step 1: - Createing Configuration Instance: " + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServiceInstanceId :" + netServiceInstanceId);



		/* Step 2: */
		INetServiceInstanceConfMapData netServiceInstanceConfMap= new NetServiceInstanceConfMapData();
		netServiceInstanceConfMap.setNetServiceId(netServiceInstanceId);
		netServiceInstanceConfMap.setConfigInstanceId(netConfigurationInstance.getConfigInstanceId());
		netserviceDataManager.createNetServiceInstanceConfMap(netServiceInstanceConfMap);
		Logger.logDebug(MODULE, "Step 2: - Createing Configuration Map Entry: " + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServiceInstanceId :" + netServiceInstanceId);

		/* Step 3: */
		/*
		 * Every Root parameter has only one Root Tag - So only First parameter Will be considered as root Tag.
		 */

		INetConfigurationData   netConfigurationData = netserviceDataManager.getRootParameterConfigurationData(netConfigurationInstance.getConfigId());
		Iterator itrChildParamSet = netConfigurationData.getNetConfigParameters().iterator();
		if(itrChildParamSet.hasNext()){
			INetConfigurationParameterData configParamData = (INetConfigurationParameterData)itrChildParamSet.next();
			List lstConfigParamValue = new ArrayList();
			lstConfigParamValue = parseInitCreateCurrentNode(configParamData,lstConfigParamValue,netConfigurationInstance,"1");
			netserviceDataManager.createNetConfigurationValues(lstConfigParamValue);
		}
		Logger.logDebug(MODULE, "Step 3: -Createing Param Values entry for configInstanceId :" + netConfigurationInstance.getConfigInstanceId() + " of type :" + netConfigurationInstance.getConfigId() + " In ServiceInstanceId :" + netServiceInstanceId);

		Logger.logDebug(MODULE, "--END ADD-- New Configuration:- " + configTypeId + " for " + netServiceInstanceId);
		/* Adding new Configuration in Service Instance --End-- */

		/* --END--  Adding new Configuration in Service Instance  */
	}

	/* @author kaushik vira
	 */
	private void deleteNetServiceConfigurationInstance( String configInstanceId , IDataManagerSession session ) throws DataManagerException {

		EliteAssert.notNull(configInstanceId, "configInstanceId must Specified.");
		EliteAssert.notNull(session, "session must Specified.");


		NetServiceDataManager netSericeDataManager = getNetServiceDataManager(session);
		EliteAssert.notNull(netSericeDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());

		Logger.logDebug(MODULE,"-- Start -- deleting NetService Configuration Instance :- "+ configInstanceId);

		/*
		 * Steps : Delete ServerConfiguration.
		 * 1:- Delete All Entry from  TBLMNETCONFIGURATIONVALUES.
		 * 2:- Delete Entry from      TBLMNETSERVICEINSTANCECONFMAP. 
		 * 3:- Delete Entry from      TBLMNETCONFIGURATIONINSTANCE.
		 */

		Logger.logDebug(MODULE,"Step 1 : - Deleting TBLMNETCONFIGURATIONVALUES ");
		netSericeDataManager.deleteNetConfigurationValues(configInstanceId);

		Logger.logDebug(MODULE,"Step 2 : - Deleting TBLMNETSERVERINSTANCECONFMAP");
		netSericeDataManager.deleteNetServiceInstanceConfMap(configInstanceId);

		Logger.logDebug(MODULE,"Step 3 : - Deleting TBLMNETCONFIGURATIONINSTANCE");
		netSericeDataManager.deleteNetConfigurationInstance(configInstanceId);

		Logger.logDebug(MODULE,"-- End -- deleting NetService Configuration Instance  :- "+ configInstanceId);
	}


	private void deleteNetServiceInstanceServerConfiguration(NetServiceDataManager servermgrDataManager,String netServiceId)throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServiceDataManager localServerManager = getNetServiceDataManager(session);
		
		try {
			INetServiceInstanceData netServiceInstanceData = getNetServiceInstance(netServiceId);
			INetServerInstanceData serverInstanceData = getNetServerInstance(netServiceInstanceData.getNetServerId());

			INetServiceTypeData netServiceTypeData = localServerManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
			EliteAssert.notNull(netServiceTypeData,"netServiceTypeData must not be null");

			INetConfigurationParameterData netServiceConfigurationParameterData = localServerManager.getNetConfigurationParameterData(serverInstanceData,service);
			EliteAssert.notNull(netServiceConfigurationParameterData,"netServiceConfigurationParameterData must not be null");

			INetConfigurationInstanceData netConfigurationInstance = localServerManager.getServerConfigurationInstanceData(netServiceInstanceData.getNetServerId(), netServiceConfigurationParameterData.getConfigId());
			
			List netConfigValues = getServerConfigServiceValues(localServerManager, netServiceConfigurationParameterData,netServiceInstanceData, netConfigurationInstance);
			servermgrDataManager.deleteNetConfValuesData(netConfigValues);
		} catch (DataManagerException e) {
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	private List getServerConfigServiceValues(NetServiceDataManager servermgrDataManager,INetConfigurationParameterData netServiceConfigurationParameterData,INetServiceInstanceData netServiceInstanceData, INetConfigurationInstanceData netConfigurationInstance)throws DataManagerException{

		List finalValueList = new ArrayList();
		List finalValueServiceList = new ArrayList();           
		List serviceNameValueList = new ArrayList();            
		List serviceIdValueList = new ArrayList();              
		try {
			INetServerInstanceData serverInstanceData = getNetServerInstance(netServiceInstanceData.getNetServerId());
				      	
			INetServiceTypeData netServiceTypeData = servermgrDataManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());   
			INetConfigurationParameterData netServiceInstanceIdConfigParamData = servermgrDataManager.getNetConfigurationParameterData(serverInstanceData,serviceId);

			Iterator itrNetServiceParam = netServiceConfigurationParameterData.getNetConfigChildParameters().iterator();
			while(itrNetServiceParam.hasNext()){
				INetConfigurationParameterData netConfigParamData = (INetConfigurationParameterData)itrNetServiceParam.next();
				
				
				//Checking Service Id
				if(netConfigParamData.getParameterId().equalsIgnoreCase(netServiceInstanceIdConfigParamData.getParameterId())){
					Iterator itrNetServiceIdValues = netConfigParamData.getNetConfigParamValues().iterator();
					while(itrNetServiceIdValues.hasNext()){
						INetConfigurationValuesData netConfigServiceIdValuesData = (INetConfigurationValuesData)itrNetServiceIdValues.next(); 
						if(netConfigServiceIdValuesData.getValue() != null){
							if(netConfigServiceIdValuesData.getValue().trim().equalsIgnoreCase(netServiceTypeData.getAlias()) &&
									netConfigServiceIdValuesData.getConfigInstanceId()==netConfigurationInstance.getConfigInstanceId()                                                             
							){
								//System.out.println("Values Added : "+netConfigServiceIdValuesData.getValue()+"  |  Instance Id : "+netConfigServiceIdValuesData.getInstanceId());
								serviceIdValueList.add(netConfigServiceIdValuesData);                                                   
							}
						}

					}
				}	

			}

			String paramInstanceIdService = null;
			
				for(int j=0;j<serviceIdValueList.size();j++){
					INetConfigurationValuesData netConfigServiceIdValuesData = (INetConfigurationValuesData)serviceIdValueList.get(j);

					if(netConfigServiceIdValuesData.getInstanceId().substring(0,netConfigServiceIdValuesData.getInstanceId().lastIndexOf("."))
							.equalsIgnoreCase(netConfigServiceIdValuesData.getInstanceId().substring(0,netConfigServiceIdValuesData.getInstanceId().lastIndexOf(".")))){

						paramInstanceIdService = netConfigServiceIdValuesData.getInstanceId().substring(0,netConfigServiceIdValuesData.getInstanceId().lastIndexOf("."));
						Iterator itr = netServiceConfigurationParameterData.getNetConfigParamValues().iterator();
						while(itr.hasNext()){
							INetConfigurationValuesData netConfigServiceValuesData = (INetConfigurationValuesData)itr.next();
							if(netConfigServiceValuesData.getInstanceId().equalsIgnoreCase(paramInstanceIdService) &&
									netConfigServiceValuesData.getConfigInstanceId()==netConfigurationInstance.getConfigInstanceId()
							){
								finalValueServiceList.add(netConfigServiceValuesData);
								break;
							}
						}
					}
				}
			
			finalValueList.addAll(finalValueServiceList);
			Iterator childIterator = netServiceConfigurationParameterData.getNetConfigChildParameters().iterator();
			while(childIterator.hasNext()){
				INetConfigurationParameterData childConfigParamData = (INetConfigurationParameterData)childIterator.next();
				Iterator childValuesIterator = childConfigParamData.getNetConfigParamValues().iterator();

				while(childValuesIterator.hasNext()){
					INetConfigurationValuesData childValuesData = (INetConfigurationValuesData)childValuesIterator.next();
					for(int i=0;i<finalValueServiceList.size();i++){
						INetConfigurationValuesData serviceConfigValueData = (INetConfigurationValuesData)finalValueServiceList.get(i);
						if(serviceConfigValueData.getConfigInstanceId()==childValuesData.getConfigInstanceId()){
							if(childValuesData.getInstanceId().startsWith(serviceConfigValueData.getInstanceId())
									&& childValuesData.getInstanceId().substring(serviceConfigValueData.getInstanceId().length(),childValuesData.getInstanceId().length()).startsWith(".")          
							){
								finalValueList.add(childValuesData);
							}
						}
					}
				}
			}


		} catch (DataManagerException hExp) {
			throw hExp;
		}
		return finalValueList;
	}

	public List<INetConfigurationParameterData> getNetConfigParameterDataList( INetServiceInstanceData netServiceInstanceData, String alias) throws DataManagerException {
		EliteAssert.notNull(netServiceInstanceData);
		EliteAssert.notNull(alias);

		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);

			if(servermgrDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			return servermgrDataManager.getNetConfigParameterDataList(netServiceInstanceData,alias);
		}
		catch(DataManagerException exp){
			throw exp;
		}
		catch(Exception exp){
			throw new DataManagerException("Update Status Action Failed : "+exp.getMessage());
		}
		finally {
			closeSession(session);
		}
	}
	
	/**
	 * This method is used to get binded plugin-names with Configured services in server instance .
	 * @return set of plugin-names .
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames() throws DataManagerException {
		  Set<String> pluginNames = new HashSet<String>();
		  NetServerBLManager netServerBLManager = new NetServerBLManager();
		  BaseUpdateConfigurationAction baseUpdateConfigurationAction = new BaseUpdateConfigurationAction() {};
		  try{
				List<NetServerInstanceData> netServerInstanceList = netServerBLManager.getNetServerInstanceList();
				if (netServerInstanceList != null && netServerInstanceList.isEmpty() == false) {
		        	 for(NetServerInstanceData serverInstanceData : netServerInstanceList){
		        		 String serverInstanceId = serverInstanceData.getNetServerId();
		        		 List<NetServiceInstanceData> serviceInstanceList = (List<NetServiceInstanceData>)getNetServiceInstanceList(serverInstanceId);
		        		 if (serviceInstanceList != null && serviceInstanceList.isEmpty() == false) {
		        			 for(NetServiceInstanceData serviceInstanceData : serviceInstanceList){
		        				 String serviceInstanceId = serviceInstanceData.getNetServiceId();
		        				 List<NetConfigurationInstanceData> configurationInstanceDatas = getNetServiceConfigInstanceList(serviceInstanceId);
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
		        	 }
				}	 
				return pluginNames;
			}catch (DataManagerException e) { 
				throw new DataManagerException("Action failed :"+e.getMessage(),e);
			}
	}

	public String getNetServiceTypeIdByName(String serviceName)  throws DataManagerException {
		IDataManagerSession session =null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
			return servermgrDataManager.getNetServiceTypeIdByName(serviceName);
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}finally {
			closeSession(session);
		}
	}  
	
	public void updateService(String netServiceId, byte[] sourceBytes)throws DataManagerException {

		EliteAssert.greaterThanZero(netServiceId,"netServiceId");

		IDataManagerSession session = null;
		session =  DataManagerSessionFactory.getInstance().getDataManagerSession();

		NetServiceDataManager serviceDataManager = getNetServiceDataManager(session);

		if (serviceDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		session.beginTransaction();
		try {
			INetServiceInstanceData netServiceInstanceData = getNetServiceInstance(netServiceId);
			List serviceInstanceList = getServiceConfigurationInstance(netServiceInstanceData.getNetServiceId());

			int noOfService = serviceInstanceList.size();
			for (int i=0; i<noOfService; i++) {
				INetConfigurationInstanceData configInstanceData = (INetConfigurationInstanceData)serviceInstanceList.get(i);
				overwriteServiceConfigurationValues(serviceDataManager, sourceBytes, netServiceId, configInstanceData);
			}
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}

	}
	
	public INetServiceInstanceData getNetServiceInstanceByServiceName(String netServerId, String serviceName) throws DataManagerException {

		IDataManagerSession session = null;
		INetServiceInstanceData serviceInstanceData = null;
		session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
		if(servermgrDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try {
			List<INetServiceInstanceData> netServiceInstanceList = servermgrDataManager.getNetserviceInstanceList(netServerId);

			for (INetServiceInstanceData serviceInstanceDataValue : netServiceInstanceList )  {
				if (serviceInstanceDataValue.getDisplayName().equals(serviceName)) {
					serviceInstanceData = serviceInstanceDataValue;
					break;
				}
			}

			if (serviceInstanceData == null) {
				throw new InvalidValueException("Requested Service is not configured");
			}
		} catch(InvalidValueException ive) {
			ive.printStackTrace();
			throw new DataManagerException(ive.getMessage(), ive);
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return serviceInstanceData;
	}
	
	public String getConfigIdByServiceTypeId(String ServiceTypeId) throws DataManagerException {

		IDataManagerSession session = null;
		String serviceConfigId = null;
		session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		NetServiceDataManager servermgrDataManager = getNetServiceDataManager(session);
		if(servermgrDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try {
			serviceConfigId = servermgrDataManager.getConfigIdByServiceTypeId(ServiceTypeId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
		return serviceConfigId;
	}
}
