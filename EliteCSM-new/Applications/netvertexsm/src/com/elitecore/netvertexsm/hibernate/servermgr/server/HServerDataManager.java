package com.elitecore.netvertexsm.hibernate.servermgr.server;


/**
 * @author dhavalraval
 */
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.UniqueResultExpectedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.VersionNotSupportedException;
import com.elitecore.netvertexsm.datamanager.radius.system.standardmaster.data.IStandardMasterData;
import com.elitecore.netvertexsm.datamanager.radius.system.standardmaster.data.IStandardMasterTypeData;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupRelationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerStartupConfigData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.server.NetServerDataManager;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.logger.Logger;


public class HServerDataManager extends HBaseDataManager implements NetServerDataManager {
    private static final String MODULE="HSERVER DATA MANAGER";
    /**
     * @author  dhavalraval
     * @param   netServerInstanceData create Method
     * @throws  DataManagerException
     * @purpose This method is generated to create NetServerInstanceData.
     */
    public INetServerInstanceData createServerInstance(INetServerInstanceData netServerInstanceData,String serverGroupId) throws DataManagerException{
        try {
            Session session = getSession();
            session.save(netServerInstanceData);
            ServerInstanceGroupRelationData serverInstanceGroupRelationData = new ServerInstanceGroupRelationData();
            serverInstanceGroupRelationData.setNetServerInstanceId(String.valueOf(netServerInstanceData.getNetServerId()));
            serverInstanceGroupRelationData.setServerInstanceGroupId(serverGroupId);
            
            
            Criteria criteria = session.createCriteria(ServerInstanceGroupRelationData.class);
            criteria.add(Restrictions.eq("serverInstanceGroupId", serverGroupId));
            if(Collectionz.isNullOrEmpty(criteria.list())){
            	serverInstanceGroupRelationData.setServerWeightage(CommonConstants.PRIMARY_INSTANCE);
            }else{
            	serverInstanceGroupRelationData.setServerWeightage(CommonConstants.SECONDARY_INSATNCE);
            }
            session.save(serverInstanceGroupRelationData);
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServerInstanceData; 
    }
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @purpose This method is generated to give list of NetServerConfigMapTypeData.
     * @throws  DataManagerException
     */
    public INetServerConfigMapTypeData getNetServerConfigMapTypeList(INetServerConfigMapTypeData netServerConfigMapTypeData) throws DataManagerException{
        List lstNetServerConfigMapTypeList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerConfigMapTypeData.class)
            .add(Restrictions.eq("netConfigMapTypeId",netServerConfigMapTypeData.getNetConfigMapTypeId()));
            return (INetServerConfigMapTypeData) criteria.uniqueResult();
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
     }
    
    /**
     * @author  dhavalraval
     * @param   netConfigurationInstanceData Create Method
     * @throws  DataManagerException
     * @purpose This method is generated to create NetConfigurationInstanceData.
     */
    public INetConfigurationInstanceData createNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException{
        try {
            Session session = getSession();
            session.save(netConfigurationInstanceData);
            session.flush();
        } catch (HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            exp.getMessage();
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationInstanceData;
    }
    
    /**
     * @author  dhavalraval 
     * @param   netServerInstanceConfMap Create Method. 
     * @throws  HibernateException.
     * @purpose This method is generated to create NetServerInstanceConfMapData.
     */
    public void createNetServerInstanceConfMap(INetServerInstanceConfMapData netServerInstanceConfMap) throws DataManagerException{
        try {
            Session session = getSession();
            session.save(netServerInstanceConfMap);
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    /**
     * @author  dhavalraval
     * @author  updated By - Kaushik Vira.
     * @param   netConfigurationValues Create Method.
     * @throws  DataManagerException.
     * @purpose This method is generated to create NetConfigurationValuesData.
     */
    public void createNetConfigurationValues(List<INetConfigurationValuesData> lstConfigParamValue) throws DataManagerException{
        try {
            Session session = getSession();
            for ( INetConfigurationValuesData netConfigurationValuesData : lstConfigParamValue ) {
                session.save(netConfigurationValuesData);  
            }
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @purpose This method is generated to give list of NetServerInstanceData.
     * @throws  DataManagerException
     */
    public List getNetServerInstanceList() throws DataManagerException{
        List lstNetServerInstanceList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("systemGenerated","N"));
            criteria.addOrder(Order.asc("netServerTypeId"));
            criteria.addOrder(Order.asc("netServerId"));
            lstNetServerInstanceList = criteria.list(); 
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServerInstanceList;
    }
    
    
    
    /**
     * @author  dhavalraval
     * @author  updated By - Kaushik Vira
     * @return  Returns List
     * @purpose This method is generated to give list of NetServiceInstanceData.
     * @throws  DataManagerException
     */
    public List<INetServiceInstanceData> getNetserviceInstanceList(long netServerInstanceId) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class)
            .add(Restrictions.eq("netServerId",netServerInstanceId))
            .add(Restrictions.eq("systemGenerated","N"));
            return criteria.list();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
  
    
    
    /**
     * @author  dhavalraval
     * @author  updated By -  kaushikvira
     * @param   netServerInstanceData
     * @throws  DataManagerException
     * @return  Returns List
     * @purpose This method is generated to give the list of NetServerInstance.
     */
    public INetServerInstanceData getNetServerInstance(long netServerInstanceId) throws DataManagerException{
        
        
        try {
            Session session = getSession();
//            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
//            .add(Restrictions.eq("systemGenerated","N"))
//            .add(Restrictions.eq("netServerId",netServierInstanceId));
            //INetServerInstanceData netServerInstanceData = (INetServerInstanceData)criteria.uniqueResult();
            
            Criteria criteria = session.createCriteria(NetServerInstanceData.class).add(Restrictions.eq("netServerId", netServerInstanceId));
            INetServerInstanceData netServerInstanceData = (INetServerInstanceData)criteria.uniqueResult();
            
            
            return netServerInstanceData;
        }catch(NonUniqueObjectException e) {
            throw new DataManagerException(e.getMessage(), e);
        }catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }

    /**
     * @author  dhavalraval
     * @param   netServiceInstanceData
     * @throws  DatatManagerException
     * @return  Returns List
     * @purpose This method is generated to give the list of NetServiceInstance.
     * 
     */
    public INetServiceInstanceData getNetServiceInstanceList(INetServiceInstanceData netServiceInstanceData) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class)
            .add(Restrictions.eq("systemGenerated","N"))
            .add(Restrictions.eq("netServiceId",netServiceInstanceData.getNetServiceId()));
            return (INetServiceInstanceData) criteria.uniqueResult();
        }catch(NonUniqueObjectException e) {
            throw new DataManagerException(e.getMessage(), e);
        }catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch(Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    

    /**
     * @author  dhavalraval
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServerTypeData.
     */
    public List getNetServerTypeList() throws DataManagerException {
        List serverTypeList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerTypeData.class)
            .add(Restrictions.eq("systemGenerated","N"));
            serverTypeList = criteria.list();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return serverTypeList;
    }
    
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @param   netServerTypeId
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServerConfigMapData.
     */
    public List<INetServerConfigMapData> getNetServerConfigMapList(String netServerTypeId) throws DataManagerException{
        
        List<INetServerConfigMapData> netServerConfigMapList = new ArrayList<INetServerConfigMapData>();
        Criteria criteria=null;
        INetServerTypeData netServerTypeData=null;
        try{
            Session session = getSession();
            
            criteria = session.createCriteria(NetServerTypeData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeId));
            
            netServerTypeData=(INetServerTypeData) criteria.uniqueResult();
           
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeData.getNetServerTypeId()))
            .add(Restrictions.eq("netServerVersion",netServerTypeData.getVersion()))
            .addOrder(Order.asc("netServerTypeId"));
            
            INetServerVersionData netServerVersionData=(NetServerVersionData)criteria.uniqueResult();
            
            
            criteria = session.createCriteria(NetServerConfigMapData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeId))
            .addOrder(Order.asc("netConfigMapTypeId"));
            
            Iterator<INetServerConfigMapData> iter=criteria.list().iterator();
            while(iter.hasNext()){
                
                INetServerConfigMapData netServerConfigMapData = iter.next();
                
                criteria = session.createCriteria(NetConfigurationData.class)
                .add(Restrictions.eq("netConfigId",netServerConfigMapData.getNetConfigId()))
                .add(Restrictions.eq("configVersion",netServerVersionData.getConfigVersion()))
                .addOrder(Order.asc("netConfigId"));
                
                if(criteria.list().size()>0){
                    netServerConfigMapList.add(netServerConfigMapData);
                }       
            }
            
        } catch (HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServerConfigMapList;
    }
    
    /**
     * @author  Kaushik Vira
     * @return  Returns List
     * @param   netServerTypeId
     * @param   netServerVersion
     * @throws  DataManagerException
     * @purpose return ServerConfigMap list for server type based on supplied version.
     */
    public List<INetServerConfigMapData> getNetServerConfigMapList(String netServerTypeId,String netServerVersion) throws DataManagerException{
        
        List<INetServerConfigMapData> netServerConfigMapList = new ArrayList<INetServerConfigMapData>();
        Criteria criteria=null;
        String configVersionId =null;
        
        try {
            Session session = getSession();
            try {
                criteria = session.createCriteria(NetServerVersionData.class);
                criteria.add(Restrictions.eq("netServerTypeId", netServerTypeId));
                criteria.add(Restrictions.eq("netServerVersion",netServerVersion));
                configVersionId = ((NetServerVersionData) criteria.uniqueResult()).getConfigVersion();
            }
            catch(NonUniqueObjectException e) {
                throw new VersionNotSupportedException("No Configration Found for. netServerTypeId:='"+netServerTypeId+"',serverVersionId='"+netServerVersion+"'",e);
            }
            
            /*Based on configuration Version get All configuration*/
            criteria = session.createCriteria(NetConfigurationData.class);
            criteria.add(Restrictions.eq("configVersion", configVersionId));
            List<NetConfigurationData> lstConfigration = criteria.list();
            
            /*Make a list For filter*/
            List<String> lstConfigurationId = new ArrayList<String>();
            for ( NetConfigurationData netConfigurationData : lstConfigration ) {
                lstConfigurationId.add(netConfigurationData.getNetConfigId());
            }
            
            /* Get All the ServiceConfigMapData for Server Type*/
            criteria = session.createCriteria(NetServerConfigMapData.class);
            criteria.add(Restrictions.eq("netServerTypeId", netServerTypeId));
            List<NetServerConfigMapData> lstServerConfigMapData = criteria.list();
            
            /* Taking a subset.. So finally we get filtered ConfigId based on version. */
            for ( NetServerConfigMapData netServerConfigMapData : lstServerConfigMapData ) {
                if(lstConfigurationId.contains(netServerConfigMapData.getNetConfigId()))
                    netServerConfigMapList.add(netServerConfigMapData);
            }
        } catch (HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServerConfigMapList;
    }
    
    
    /**
     * @author  dhavalraval
     * @param   netConfigId
     * @throws  DataManagerException
     * @return  Returns netConfigMapTypeId
     * @purpose This method is generated to give netConfigMapTypeId of NetServerConfigMapData by using netConfigId
     */
    public String getNetServerConfigMapByConfigId(String strCofigId) throws DataManagerException{
        String strNetConfigMapTypeId = null;
        
        try {
            Session session = getSession();
            
            Criteria criteria = session.createCriteria(NetServerConfigMapData.class)
            .add(Restrictions.eq("netConfigId",strCofigId));
            
            INetServerConfigMapData netServerConfigMapData = (INetServerConfigMapData)criteria.uniqueResult();
            strNetConfigMapTypeId = netServerConfigMapData.getNetConfigMapTypeId();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return strNetConfigMapTypeId;
    }
    

    
    /**
     * @author  dhavalraval
     * @author  updated by - kaushikvira
     * @param   netServerInstanceId
     * @return  Returns List
     * @throws  DataManagerException 
     * @purpose This method is generated to give list of NetServerInstanceConfMap.
     */
    public List<INetServerInstanceConfMapData> getNetServerInstanceConfMap(long netServerInstanceId) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class)
            .add(Restrictions.eq("netServerId",netServerInstanceId));
            return criteria.list();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    

    

    
    
    /**
     * @author  dhavalraval
     * @author  updated By - kaushikvira
     * @return  NetConfigurationData class object
     * @param   configId
     * @throws  DataManagerException
     * @purpose This method is generated to get the parent-node (ROOT NODE) in NetConfigurationParameterData. 
     */
    public INetConfigurationData getRootParameterConfigurationData(String configId) throws DataManagerException{
        EliteAssert.notNull(configId,"ConfigId Must Be specified");
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationParameterData.class)
            .add(Restrictions.eq("configId",configId))
            .add(Restrictions.isNull("parentParameterId"));
            
            Set<INetConfigurationParameterData> stConfigParamSet = new HashSet<INetConfigurationParameterData>();
            stConfigParamSet.addAll(criteria.list());    		
            
            criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("netConfigId",configId));
            
            INetConfigurationData configurationData = (INetConfigurationData) criteria.uniqueResult();
            EliteAssert.notNull(configId,"Unable to Find Root Node for ConfigId :- " + configId +"Note :- Please Varify Minimal insert for this Configution.");
            configurationData.setNetConfigParameters(stConfigParamSet);
            return configurationData;
        }
        catch(NonUniqueObjectException e) {
            throw new DataManagerException("Unable to Find Root Node for ConfigId :- " + configId +"  Reason :-" +e.getMessage(), e);
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   netServerId
     * @throws  DataManagerException
     * @purpose This Method is generated to remove the netServerInstanceData
     */
    public void deleteNetServerInstance(long netServerId) throws DataManagerException{
        INetServerInstanceData netServerInstanceData = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("netServerId",netServerId))
            .add(Restrictions.eq("systemGenerated","N"));
            
            List lstNetServerInstance = criteria.list();
            
            if(lstNetServerInstance !=null &&  lstNetServerInstance.size()>0){
                netServerInstanceData = (INetServerInstanceData)lstNetServerInstance.get(0);
                session.delete(netServerInstanceData);
                session.flush();
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @author  Updated By - kaushik vira
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purpose Delete Configuration Instance.
     *          
     */
    public void deleteNetConfigurationInstance(long configInstanceId) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class)
            .add(Restrictions.eq("configInstanceId",configInstanceId));
            List<NetConfigurationInstanceData> lstNetConfigurationInstance = criteria.list();
            for ( NetConfigurationInstanceData netConfigurationInstanceData : lstNetConfigurationInstance ) {
                session.delete(netConfigurationInstanceData);
            }
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException("deleteNetConfigurationInstance Operation Failed. Reason :-" +hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException("deleteNetConfigurationInstance Operation Failed. Reason :-"+exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @author  Updated By - kaushik vira
     * @throws  DataManagerException
     * @purpose Remove all the Entry of the Configuration Parameter Value for Specified Configuration
     *          
     *        
     */
    public void deleteNetConfigurationValues(long configInstanceId) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class)
            .add(Restrictions.eq("configInstanceId",configInstanceId));
            List<NetConfigurationValuesData> lstNetConfigurationValues = criteria.list();
            for ( NetConfigurationValuesData netConfigurationValuesData : lstNetConfigurationValues ) {
                session.delete(netConfigurationValuesData);
            }
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException("deleteNetConfigurationValues Operation Failed. Reason :-" +hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException("deleteNetConfigurationValues Operation Failed. Reason :-" + exp.getMessage(), exp);
        }
    }
    
    
    /**
     * @author  Kaushik vira.
     * @param   netServerInstanceId - Identify Server Instance
     * @param   netConfigInstanceData - Identify Configuration Instance
     * @throws  DataManagerException
     * @purpose remove ServerInstancesConfigMap Entry of ServerIntance Specified  by INetConfigurationInstanceData.
     *     
     */        
    public void deleteNetServerInstanceConfMap(long configIntanceId) throws DataManagerException{
        EliteAssert.notNull(configIntanceId,"configIntanceId Must be Specified");
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class)
            .add(Restrictions.eq("configInstanceId",configIntanceId));
            session.delete(criteria.uniqueResult());
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException("deleteNetServerInstanceConfMap Operation Failed. Reason:-"+hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException("deleteNetServerInstanceConfMap Operation Failed. Reason:-"+exp.getMessage(), exp);
        }
    }
     

    
    /**
     * @author  dhavalraval
     * @param   netServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to update the basic details.
     * 
     */
    public void updateBasicDetail(INetServerInstanceData inetServerInstanceData,Timestamp statusChangeDate) throws DataManagerException{
        
        NetServerInstanceData netServerInstanceData = null;
        if(inetServerInstanceData == null)
            throw new InvalidArrguementsException("inetServerInstanceData Must Not be Null");
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class);
            netServerInstanceData = (NetServerInstanceData)criteria.add(Restrictions.eq("netServerId",inetServerInstanceData.getNetServerId()))
            .add(Restrictions.eq("systemGenerated","N"))
            .uniqueResult();
            
            netServerInstanceData.setName(inetServerInstanceData.getName());
            netServerInstanceData.setDescription(inetServerInstanceData.getDescription());
            netServerInstanceData.setJavaHome(inetServerInstanceData.getJavaHome());
            netServerInstanceData.setServerHome(inetServerInstanceData.getServerHome());
            netServerInstanceData.setStatusChangeDate(statusChangeDate);
            netServerInstanceData.setLastModifiedDate(statusChangeDate);
            
            
            session.update(netServerInstanceData);
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    /**
     * @author  dhavalraval
     * @param   netServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to change the AdminInterface.
     * 
     */
    public void changeAdminInterface(INetServerInstanceData inetServerInstanceData,Timestamp statusChangeDate) throws DataManagerException{
        Session session = getSession();
        NetServerInstanceData netServerInstanceData = null;
        if(inetServerInstanceData != null){
            try {
                Criteria criteria = session.createCriteria(NetServerInstanceData.class);
                netServerInstanceData = (NetServerInstanceData)criteria.add(Restrictions.eq("netServerId",inetServerInstanceData.getNetServerId()))
                .add(Restrictions.eq("systemGenerated","N"))
                .uniqueResult();
                
                netServerInstanceData.setAdminHost(inetServerInstanceData.getAdminHost());
                netServerInstanceData.setAdminPort(inetServerInstanceData.getAdminPort());
                netServerInstanceData.setLastModifiedDate(statusChangeDate);
                netServerInstanceData.setStatusChangeDate(statusChangeDate);
                
                session.update(netServerInstanceData);
                session.flush();
            } catch (HibernateException hExp) {
                throw new DataManagerException(hExp.getMessage(), hExp);
            } catch (Exception exp) {
                throw new DataManagerException(exp.getMessage(), exp);
            }
        }else{
            throw new DataManagerException();
        }
    }
    
    /**
     * @author  kaushikVira
     * @param   netServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to change the AdminInterface.
     * 
     */
    public void updateAdminDetails(INetServerInstanceData inetServerInstanceData,NetServerStartupConfigData iNetServerStartupConfigData,Timestamp statusChangeDate) throws DataManagerException{
        NetServerInstanceData netServerInstanceData = null;
        if(inetServerInstanceData == null || iNetServerStartupConfigData == null)
            throw new InvalidArrguementsException("inetServerInstanceData OR iNetServerStartupConfigData is null");
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class);
            iNetServerStartupConfigData.setNetServerId(inetServerInstanceData.getNetServerId());
            session.saveOrUpdate(iNetServerStartupConfigData);
            netServerInstanceData = (NetServerInstanceData)criteria.add(Restrictions.eq("netServerId",inetServerInstanceData.getNetServerId()))
            .add(Restrictions.eq("systemGenerated","N"))
            .uniqueResult();
            
netServerInstanceData.setAdminHost(inetServerInstanceData.getAdminHost());
netServerInstanceData.setAdminPort(inetServerInstanceData.getAdminPort());
netServerInstanceData.setLastModifiedDate(statusChangeDate);
netServerInstanceData.setStatusChangeDate(statusChangeDate);
session.update(netServerInstanceData);  
            session.flush();
        } catch (HibernateException hExp) {
        	
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
        	
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    
    /**
     * @author  dhavalraval
     * @param   netServerId
     * @throws  DataManagerException
     * @return  Returns List
     * @purpose This method is generated to give list of ServerInstanceConfMapData by using configInstanceId
     */
    public List<INetConfigurationInstanceData> getNetServerConfigInstanceList(long netServerId) throws DataManagerException{
        
        List<INetConfigurationInstanceData> lstNetServerConfig = new ArrayList<INetConfigurationInstanceData>();
        Criteria criteria=null;
        INetServerInstanceData netServerInstanceData=null;
        try {
            Session session = getSession();
            
            criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("netServerId",netServerId));
            
            netServerInstanceData=(NetServerInstanceData)criteria.uniqueResult();
            
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()))
            .add(Restrictions.eq("netServerVersion",netServerInstanceData.getVersion()))
            .addOrder(Order.asc("netServerTypeId"));
            
            INetServerVersionData netServerVersionData=(NetServerVersionData)criteria.uniqueResult();
            
            criteria = session.createCriteria(NetServerInstanceConfMapData.class)
            .add(Restrictions.eq("netServerId",netServerInstanceData.getNetServerId()))
            .addOrder(Order.asc("netServerId"));
            
            Iterator<INetServerInstanceConfMapData> iter= criteria.list().iterator();
            
            while(iter.hasNext()){
                
                INetServerInstanceConfMapData netServerInstanceConfMapData =iter.next();
                
                criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configInstanceId",netServerInstanceConfMapData.getConfigInstanceId()))
                .addOrder(Order.asc("configId"));
                
                INetConfigurationInstanceData netConfigurationInstanceData=(NetConfigurationInstanceData)criteria.uniqueResult();
                
                criteria = session.createCriteria(NetConfigurationData.class)
                .add(Restrictions.eq("netConfigId",netConfigurationInstanceData.getConfigId()))
                .add(Restrictions.eq("configVersion",netServerVersionData.getConfigVersion()))
                .addOrder(Order.asc("netConfigId"));
                
                List<NetConfigurationData> lstServerConfiguration= criteria.list();
                
                if(lstServerConfiguration.size()>0){
                    INetConfigurationData netConfigurationData=(NetConfigurationData)criteria.uniqueResult();
                    netConfigurationInstanceData.setNetConfiguration(netConfigurationData);
                    lstNetServerConfig.add(netConfigurationInstanceData);
                }
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServerConfig;
    }
    
    /*
     * @author  dhavalraval
     * @param   netServerId
     * @throws  DataManagerException
     * @return  Returns List
     * @purpose This method is generated to give list of ServerInstanceConfMapData by using configInstanceId
     */
    public List getNetServerConfigInstanceList(long netServerId,String netConfigMapTypeId) throws DataManagerException{
        List lstNetServerConfig = new ArrayList();
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class)
            .add(Restrictions.eq("netServerId",netServerId));
            
            List lstNetServerConfInst = criteria.list();
            
            for(int i=0;i<lstNetServerConfInst.size();i++){
                NetServerInstanceConfMapData confMapData = (NetServerInstanceConfMapData)lstNetServerConfInst.get(i);
                
                criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configInstanceId",confMapData.getConfigInstanceId()));
                
                NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)criteria.uniqueResult();
                
                if(netConfigInstanceData != null){
                    criteria = session.createCriteria(NetServerInstanceData.class)
                    .add(Restrictions.eq("netServerId",confMapData.getNetServerId()))
                    .add(Restrictions.eq("systemGenerated","N"));
                    
                    NetServerInstanceData netServerInstanceData = (NetServerInstanceData)criteria.uniqueResult();
                    
                    if(netServerInstanceData != null){
                        criteria = session.createCriteria(NetServerConfigMapData.class)
                        .add(Restrictions.eq("netConfigMapTypeId",netConfigMapTypeId))
                        .add(Restrictions.eq("netConfigId",netConfigInstanceData.getConfigId()))			    
                        .add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()));
                        
                        if(criteria.list() != null && criteria.list().size() > 0){
                            lstNetServerConfig.add(netConfigInstanceData);
                        }
                    }
                }
            }
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServerConfig;
    }
    
    
    
    public INetConfigurationInstanceData getConfigurationInstance(long configInstanceId) throws DataManagerException{
        
        INetConfigurationInstanceData configurationInstanceData = null;        
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class);
            criteria.add(Restrictions.eq("configInstanceId",configInstanceId));            
            configurationInstanceData = (INetConfigurationInstanceData)criteria.uniqueResult();
            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return configurationInstanceData;
    }
    
    
    public INetConfigurationInstanceData getServerConfigurationInstanceData(long netServerIntanceId, String configId) throws DataManagerException{
        
        INetConfigurationInstanceData configurationInstanceData = null;        
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class);//NetServerInstanceConfMap.class);
            criteria.add(Restrictions.eq("netServerId",netServerIntanceId));
            criteria.createAlias("netConfigurationInstance","netConfigInstanceData");    		
            criteria.add(Restrictions.eq("netConfigInstanceData.configId",configId));
            INetServerInstanceConfMapData netServerInstanceConfMap = (INetServerInstanceConfMapData) criteria.uniqueResult();
            if(netServerInstanceConfMap != null)  		
                configurationInstanceData = netServerInstanceConfMap.getNetConfigurationInstance();
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return configurationInstanceData;
        
    }
    

    
    
    /**
     *@author dhavan
     *@param parameterId
     *@return INetConfigurationParameterData
     * 
     */
    public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId)throws DataManagerException{
        INetConfigurationParameterData netConfigurationParameterData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationParameterData.class)
            .add(Restrictions.eq("parameterId",parameterId));
            
            netConfigurationParameterData = (INetConfigurationParameterData)criteria.uniqueResult();
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationParameterData;
    }
    
  
    /**
     * @author dhavan
     * @author updated By -kaushikvira
     * 
     */
    public void saveNetConfValuesData(List<INetConfigurationValuesData> lstValueData,long configInstanceId) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class)
            .add(Restrictions.eq("configInstanceId",configInstanceId));
            Logger.logTrace("----------", "All instances in NetConfigurationValuesData with strConfigInstanceId : "+configInstanceId);
            List<NetConfigurationValuesData> lstDelValueData = criteria.list();
            
            for ( NetConfigurationValuesData netConfigurationValuesData : lstDelValueData ) {
                session.delete(netConfigurationValuesData);
            }
            session.flush();
            
            for ( INetConfigurationValuesData netConfigurationValuesData2 : lstValueData ) {
                session.saveOrUpdate(netConfigurationValuesData2);
            }
            session.flush();
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    /**
     * @author dhavan
     * This method return the configuration name 
     * based on config instance id.
     * @param strConfigInstanceId
     * @return
     * @throws DataManagerException
     */
    public String getNetConfigurationName(long configInstanceId)throws DataManagerException {
        String strCnfName=null;
        String strNetConfigId = null;
        strNetConfigId = getNetConfigurationId(configInstanceId);
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("netConfigId",strNetConfigId));
            INetConfigurationData netConfigurationData =(INetConfigurationData)criteria.uniqueResult();
            strCnfName= netConfigurationData.getName();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return strCnfName;
    }
    
    /**
     * @author dhavan
     * 
     * This method return config id 
     * based on config instance id.
     * @param strConfigInstanceId
     * @return
     * @throws DataManagerException
     */
    private String getNetConfigurationId(long strConfigInstanceId)throws DataManagerException {
        String strConfigId=null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class)
            .add(Restrictions.eq("configInstanceId",strConfigInstanceId));
            INetConfigurationInstanceData netConfigurationInstanceData =(INetConfigurationInstanceData)criteria.uniqueResult();
            strConfigId= netConfigurationInstanceData.getConfigId();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return strConfigId ;
    }
    
    
    /**
     * This method will return NetServerName
     * based on strConfigInstanceId.
     * @param strConfigInstanceId
     * @return
     * @throws DataManagerException
     */
    public String getNetServerNameServiceConfig(long strConfigInstanceId) throws DataManagerException{
        long netServiceId; 
        long netServerId;
        String strNetServerName = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class)
            .add(Restrictions.eq("configInstanceId",strConfigInstanceId));
//            Logger.logInfo(" ::: criteria here ::: " ,criteria);
            INetServiceInstanceConfMapData netServiceInstanceConfMapData =(INetServiceInstanceConfMapData)criteria.uniqueResult();
//            Logger.logInfo(" ::: netServiceInstanceConfMapData here ::: " ,netServiceInstanceConfMapData);
            netServiceId = netServiceInstanceConfMapData.getNetServiceId();
            Logger.logInfo(MODULE," ::: strNetServiceId here ::: " +netServiceId);
            
            criteria = session.createCriteria(NetServiceInstanceData.class)
            .add(Restrictions.eq("netServiceId",netServiceId))
            .add(Restrictions.eq("systemGenerated","N"));

//            Logger.logInfo(" ::: criteria here ::: " ,criteria);
            
            INetServiceInstanceData netServiceInstanceData =(INetServiceInstanceData)criteria.uniqueResult();
//            Logger.logInfo(" ::: netServiceInstanceData ::: " , netServiceInstanceData);
            netServerId = netServiceInstanceData.getNetServerId();
            Logger.logInfo(MODULE," ::: strNetServerId ::: " + netServerId);
            strNetServerName = getNetServerName(netServerId);
            Logger.logInfo(" ::: strNetServerName ::: " , strNetServerName);
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        
        return strNetServerName;
    }

    
    
    private String getNetServerName(long netServerId)throws DataManagerException{
        String strNetServerName = null;
        Session session = getSession();
        Criteria criteria = session.createCriteria(NetServerInstanceData.class)
        .add(Restrictions.eq("netServerId",netServerId))
        .add(Restrictions.eq("systemGenerated","N"));
        INetServerInstanceData netServerInstanceData =(INetServerInstanceData)criteria.uniqueResult();
        strNetServerName = netServerInstanceData.getName();
        return strNetServerName;
    }
    
    
    public INetConfigurationData getNetConfigurationData(String configId) throws DataManagerException{
        INetConfigurationData netConfigurationData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("netConfigId",configId));
            netConfigurationData = (INetConfigurationData)criteria.uniqueResult();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationData;
    }
    
    
    /*
     * @author vaseem
     * @author updated By kaushikvira
     * @param netServerId - Identify Server Instance
     * @param NetServerDataManager - Session Object - This Method Can`t run with out parent Session.
     * @purpose Generate Server Identifier,It`s uses netserver Id as Server Identifier.
     */
    public void generateServerIdentificationInstance(long netServerId,String netServerIdentifier) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("systemGenerated","N"));
            NetServerInstanceData  netServerInstanceData = (NetServerInstanceData) criteria.add(Restrictions.eq("netServerId",netServerId)).uniqueResult();
            netServerInstanceData.setNetServerCode(netServerIdentifier);
            session.update(netServerInstanceData);
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    //ServerConfig
    public String getNetServerNameServerConfig(long configInstanceId)throws DataManagerException {
       long netServerId;
        String strNetServerName = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class)
            .add(Restrictions.eq("configInstanceId",configInstanceId));
            INetServerInstanceConfMapData netServerInstanceConfMapData =(INetServerInstanceConfMapData)criteria.uniqueResult();
            netServerId = netServerInstanceConfMapData.getNetServerId();
            strNetServerName = getNetServerName(netServerId);
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        
        return strNetServerName;
    }
    
    
    
    public INetServerTypeData getNetServerType(String serverTypeId)throws DataManagerException{
        INetServerTypeData netServerTypeData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetServerTypeData.class)
            .add(Restrictions.eq("netServerTypeId",serverTypeId))
            .add(Restrictions.eq("systemGenerated","N"));
            
            netServerTypeData = (INetServerTypeData)criteria.uniqueResult();
        } catch (HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            exp.getMessage();
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServerTypeData;
        
    }
    
    public INetServiceTypeData getNetServiceType(String serviceTypeId)throws DataManagerException{
        INetServiceTypeData netServiceTypeData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetServiceTypeData.class)
            .add(Restrictions.eq("netServiceTypeId",serviceTypeId))
            .add(Restrictions.eq("systemGenerated","N"));
            
            netServiceTypeData = (INetServiceTypeData)criteria.uniqueResult();
        } catch (HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            exp.getMessage();
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServiceTypeData;
        
    }
    

    
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServerInstanceData serverInstanceData, String paramName) throws DataManagerException{
        INetConfigurationParameterData netConfigurationParameterData = null;
        INetConfigurationData netConfigurationData=null;
        
        try {
            Session session = getSession();
            INetServerVersionData netServerVersionData=getCompatibleVersion(serverInstanceData.getNetServerId());
            Criteria criteria = session.createCriteria(NetServerConfigMapData.class);
            criteria.add(Restrictions.eq("netServerTypeId",serverInstanceData.getNetServerTypeId()));
            
            List<NetServerConfigMapData> lstConfiguration = criteria.list();
            for(int i=0;i<lstConfiguration.size();i++){
                
                NetServerConfigMapData netServerConfigMapData = lstConfiguration.get(i);
                
                criteria = session.createCriteria(NetConfigurationData.class);
                criteria.add(Restrictions.eq("netConfigId",netServerConfigMapData.getNetConfigId()));
                criteria.add(Restrictions.eq("configVersion",netServerVersionData.getConfigVersion()));
                
                netConfigurationData=(NetConfigurationData) criteria.uniqueResult();
                
                if(netConfigurationData != null){
                    
                    criteria = session.createCriteria(NetConfigurationParameterData.class);
                    criteria.add(Restrictions.eq("configId",netConfigurationData.getNetConfigId()));
                    criteria.add(Restrictions.eq("alias",paramName));
                    netConfigurationParameterData = (INetConfigurationParameterData) criteria.uniqueResult();
                    
                    if(netConfigurationParameterData != null){
                        break;
                    }
                }
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return netConfigurationParameterData;
    }
    
    public List<INetConfigurationParameterData> getNetConfigurationParameterDataList(INetServerInstanceData netServerInstanceData, String paramName) throws DataManagerException{

    	
    	List<INetConfigurationParameterData> configurationParameterDataList = null;
         try {
             Session session = getSession();
             
             /* Getting ServerVersion Instance to get server version */
            
             /* based on Server Type and Version Get Configuration Version*/
             String configVersionId = null;
             Criteria criteria = null;
             try {
                 criteria = session.createCriteria(NetServerVersionData.class);
                 criteria.add(Restrictions.eq("netServerTypeId", netServerInstanceData.getNetServerTypeId()));
                 criteria.add(Restrictions.eq("netServerVersion",netServerInstanceData.getVersion()));
                 configVersionId = ((NetServerVersionData) criteria.uniqueResult()).getConfigVersion();
             }
             catch(NonUniqueObjectException e) {
                 throw new VersionNotSupportedException("No Configration Found for. netServerTypeId:='"+netServerInstanceData.getNetServerTypeId()+"',serverVersionId='"+netServerInstanceData.getVersion()+"'",e);
             }
           
             /*Based on configuration Version get All configuration*/
             criteria = session.createCriteria(NetConfigurationData.class);
             criteria.add(Restrictions.eq("configVersion", configVersionId));
             List<NetConfigurationData> lstConfigration = criteria.list();
           
             /*Make a list For filter*/
             List<String> lstConfigurationId = new ArrayList<String>();
             for ( NetConfigurationData netConfigurationData : lstConfigration ) {
                 lstConfigurationId.add(netConfigurationData.getNetConfigId());
             }
           
             /* Get All the ServerConfigMap for Service Type*/
             criteria = session.createCriteria(NetServerConfigMapData.class);
             criteria.add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()));
             List<NetServerConfigMapData> lstServerConfigMapData = criteria.list();
           
             /* Taking a subset.. So finally we get filtered ConfigId based on version. */
             List<String> lstServerConfigMapWithVersionFilter = new ArrayList<String>();
             for ( NetServerConfigMapData netServerConfigMapData : lstServerConfigMapData ) {
                 if(lstConfigurationId.contains(netServerConfigMapData.getNetConfigId()))
                	 lstServerConfigMapWithVersionFilter.add(netServerConfigMapData.getNetConfigId());
             }
             
             for ( String configId : lstServerConfigMapWithVersionFilter ) {
                 criteria = session.createCriteria(NetConfigurationParameterData.class);
                 criteria.add(Restrictions.eq("configId", configId));
                 criteria.add(Restrictions.eq("alias", paramName));
                 
                 configurationParameterDataList = (List<INetConfigurationParameterData>) criteria.list();
                 
                 if (configurationParameterDataList != null && configurationParameterDataList.size()>0) {
                     break;
                 }
             }
         }
         catch(DataManagerException e) {
             throw e;
         }
         catch (HibernateException hExp) {
             throw new DataManagerException(hExp.getMessage(), hExp);
         }
         catch (Exception exp) {
             throw new DataManagerException(exp.getMessage(), exp);
         }
         
         return configurationParameterDataList;
    }
    
    
    
    public List getNetConfigParameterValueList(long configInstanceId, String parameterId)throws DataManagerException{
        Session session = getSession();
        List lstConfigValue = null;
        
        try {
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class);
            criteria.add(Restrictions.eq("configInstanceId",configInstanceId));
            criteria.add(Restrictions.eq("parameterId",parameterId));
            lstConfigValue = criteria.list();			
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return lstConfigValue;
    }
    
    
    public void addNetConfValuesData(List lstValueData) throws DataManagerException {
        try {
            Session session = getSession();
            for(int i=0 ; i < lstValueData.size(); i++){
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData)lstValueData.get(i);
                session.save(netConfValuesData);
            }
            session.flush();
        } catch (HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            exp.printStackTrace();
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public void deleteNetConfValuesData(List lstValueData) throws DataManagerException {
        try {
            Session session = getSession();
            for(int i=0 ; i < lstValueData.size(); i++){
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData)lstValueData.get(i);
                session.delete(netConfValuesData);
            }
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServiceInstanceData serviceInstanceData, String paramName) throws DataManagerException{
        INetConfigurationParameterData netConfigurationParameterData = null;
        Session session = getSession();
        try {
            
            /* Getting ServerVersion Instance to get server version */
            Criteria  criteria = session.createCriteria(NetServerInstanceData.class);
            criteria.add(Restrictions.eq("netServerId",serviceInstanceData.getNetServerId()));
            NetServerInstanceData netServerInstanceData = (NetServerInstanceData) criteria.uniqueResult();
            
            /* based on Server Type and Version Get Configuration Version*/
            String configVersionId = null;
            try {
                criteria = session.createCriteria(NetServerVersionData.class);
                criteria.add(Restrictions.eq("netServerTypeId", netServerInstanceData.getNetServerTypeId()));
                criteria.add(Restrictions.eq("netServerVersion",netServerInstanceData.getVersion()));
                configVersionId = ((NetServerVersionData) criteria.uniqueResult()).getConfigVersion();
            }
            catch(NonUniqueObjectException e) {
                throw new VersionNotSupportedException("No Configration Found for. netServerTypeId:='"+netServerInstanceData.getNetServerTypeId()+"',serverVersionId='"+netServerInstanceData.getVersion()+"'",e);
            }
            
            /*Based on configuration Version get All configuration*/
            criteria = session.createCriteria(NetConfigurationData.class);
            criteria.add(Restrictions.eq("configVersion", configVersionId));
            List<NetConfigurationData> lstConfigration = criteria.list();
            
            /*Make a list For filter*/
            List<String> lstConfigurationId = new ArrayList<String>();
            for ( NetConfigurationData netConfigurationData : lstConfigration ) {
                lstConfigurationId.add(netConfigurationData.getNetConfigId());
            }
            
            /* Get All the ServiceConfigMapData for Service Type*/
            criteria = session.createCriteria(NetServiceConfigMapData.class);
            criteria.add(Restrictions.eq("netServiceTypeId",serviceInstanceData.getNetServiceTypeId()));
            List<NetServiceConfigMapData> lstServiceConfigMapData = criteria.list();
            
            /* Taking a subset.. So finally we get filtered ConfigId based on version. */
            List<String> lstServiceConfigMapWithVersionFilter = new ArrayList<String>();
            for ( NetServiceConfigMapData netServiceConfigMapData : lstServiceConfigMapData ) {
                if(lstConfigurationId.contains(netServiceConfigMapData.getNetConfigId()))
                    lstServiceConfigMapWithVersionFilter.add(netServiceConfigMapData.getNetConfigId());
            }
            
            for ( String configId : lstServiceConfigMapWithVersionFilter ) {
                criteria = session.createCriteria(NetConfigurationParameterData.class);
                criteria.add(Restrictions.eq("configId",configId));
                criteria.add(Restrictions.eq("alias",paramName));
                netConfigurationParameterData = (INetConfigurationParameterData)criteria.uniqueResult();
                if(netConfigurationParameterData != null){
                    break;
                }
                
            }
            
        }catch(DataManagerException e) {
            throw e;
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return netConfigurationParameterData;
    }
    
    /**
     * @author  Vaseem
     * @return  Returns List
     * @purpose This method is generated to give list of tblmStandardMaster.
     * @throws  DataManagerException
     */
    public List getNetConfigParamStartupModeList(String startUpModeType) throws DataManagerException{
        List lstNetConfigParamStartupModeList = null;
        
        try {
            Session session = getSession();
            
            Criteria criteria = session.createCriteria(IStandardMasterTypeData.class)
            	.add(Restrictions.eq("alias",startUpModeType))
            	.add(Restrictions.eq("systemGenerated","N"));
            IStandardMasterTypeData standardMasterTypeData = (IStandardMasterTypeData) criteria.uniqueResult();
            
            criteria = session.createCriteria(IStandardMasterData.class)
            	.add(Restrictions.eq("masterTypeId",standardMasterTypeData.getMasterTypeId()))
            	.add(Restrictions.eq("systemGenerated","N"))
            	.addOrder(Order.asc("masterId"));
            
            lstNetConfigParamStartupModeList = criteria.list();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetConfigParamStartupModeList;
    }
    
    /**
     * @author  kaushik
     * @return  INetServerStartupConfigData
     * @purpose This method is create ServerStartupConfigInstance.
     * @throws  DataManagerException
     */
    public INetServerStartupConfigData createServerStartupConfigInstance(INetServerStartupConfigData netServerStartupConfigData) throws DataManagerException{
        try {
            Session session = getSession();
            session.saveOrUpdate(netServerStartupConfigData);
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServerStartupConfigData; 
    }
    
    
    /**
     * @author  kaushik
     * @return  netServerConfigMapList
     * @purpose This method is give netServerConfigMapList based on netServerTypeId.
     * @throws  DataManagerException
     */
    public List getNetServerConfigMapByServerTypeId( String netServerTypeId,String configVersion ) throws DataManagerException {
        
        List<NetServerConfigMapData> netServerConfigMapList = null;
        List<NetConfigurationData> netServerConfigList = null;
        List<NetServerConfigMapData> resultList = new ArrayList<NetServerConfigMapData>();
        try {
            Session session = getSession();
            Criteria criteria1 = session.createCriteria(NetServerConfigMapData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeId));
            netServerConfigMapList =  criteria1.list();
            
            Criteria criteria2 = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("configVersion",configVersion));
            
            netServerConfigList = criteria2.list();
            
            List<String> tempCompList = new ArrayList<String>();
            for ( NetConfigurationData netConfigurationData : netServerConfigList ) {
                tempCompList.add(netConfigurationData.getNetConfigId());
            }
            
            for ( NetServerConfigMapData netServerConfigMapData : netServerConfigMapList ) {
                if(tempCompList.contains(netServerConfigMapData.getNetConfigId()))       
                    resultList.add(netServerConfigMapData);
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return resultList;
    }
    
    /**
     * @author  kaushik Vira.
     * @return  void
     * @purpose This method is Change Config sync status for server..
     * @throws  DataManagerException
     */
    public void changeIsConfigInSyncStaus(long netServerId,String syncStatusType,Timestamp statusChangeDate) throws DataManagerException {
        Session session = getSession();
        NetServerInstanceData netServerInstanceData = null;
        try {
            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("systemGenerated","N"));
            netServerInstanceData = (NetServerInstanceData)criteria.add(Restrictions.eq("netServerId",netServerId)).uniqueResult();
            netServerInstanceData.setIsInSync(syncStatusType);
            netServerInstanceData.setLastModifiedDate(statusChangeDate);
            session.update(netServerInstanceData);
            session.flush();
            netServerInstanceData = null;
            criteria = null;
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(),hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public INetServerVersionData getCompatibleVersion(long netServerIntanceId)throws DataManagerException{
        Criteria criteria=null;
        INetServerInstanceData netServerInstanceData=null;
        try{
            Session session = getSession();
            netServerInstanceData= getNetServerInstanceData(netServerIntanceId);
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()))
            .add(Restrictions.eq("netServerVersion",netServerInstanceData.getVersion()));
            return (NetServerVersionData) criteria.uniqueResult();
        }catch(NonUniqueObjectException e){
            throw new DuplicateEntityFoundException("Multiple Configration Version Found.  Reason:" +e.getMessage(),e);
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(),hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public INetServerVersionData getCompatibleVersion(String netServerTypeId,String netServerVersion)throws DataManagerException{
        Criteria criteria=null;
        try{
            Session session = getSession();
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeId))
            .add(Restrictions.eq("netServerVersion",netServerVersion));
            NetServerVersionData netServerVersionData = (NetServerVersionData) criteria.uniqueResult();
            return netServerVersionData;
        }catch(NonUniqueObjectException e){
            throw new DuplicateEntityFoundException("Multiple Configration Version Found.  Reason:" +e.getMessage(),e);
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(),hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    private INetServerInstanceData getNetServerInstanceData(long netServerId)throws DataManagerException{
        Criteria criteria=null;
        try{
            Session session = getSession();
            criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("netServerId",netServerId));
            return (NetServerInstanceData) criteria.uniqueResult();
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    public List getNewVersionServerConfigurationInstance(String compatibleVersion)throws DataManagerException{
        List lstNetServerConfig = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("configVersion",compatibleVersion))
            .addOrder(Order.asc("netConfigId"));
            
            List lstServerConf = criteria.list();
            
            for(int i=0;i<lstServerConf.size();i++){
                NetConfigurationData confData = (NetConfigurationData) lstServerConf.get(i);
                criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configId",confData.getNetConfigId()))
                .addOrder(Order.asc("configInstanceId"));
                
                lstNetServerConfig = criteria.list();
                
            }
            return lstNetServerConfig;
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public boolean getServerConfigurationInstanceStatus(String compatibleVersion)throws DataManagerException{
        boolean status=false;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("configVersion",compatibleVersion))
            .addOrder(Order.asc("netConfigId"));
            
            List lstServerConf = criteria.list();
            
            for(int i=0;i<lstServerConf.size();i++){
                
                NetConfigurationData confData = (NetConfigurationData)lstServerConf.get(i);
                
                criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configId",confData.getNetConfigId()))
                .addOrder(Order.asc("configInstanceId"));
                
                List lstServerInstanceConf=criteria.list();
                
                if(lstServerInstanceConf != null){
                    status=true;
                }
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return status;
    }
    public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId,String configId) throws DataManagerException{
        
        INetConfigurationParameterData netConfigurationParameterData=null;
        try {
            
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationParameterData.class)
            .add(Restrictions.eq("parameterId",parameterId))
            .add(Restrictions.eq("configId",configId));
            
            netConfigurationParameterData=(NetConfigurationParameterData)criteria.uniqueResult();                            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationParameterData;
    }
    
    /*
     * @author kaushikVira
     * @param netServerTypeId - Identify Server Type
     * @param serverVersionId - identify Server Version
     * @return configuration version for the ServerType
     */
    
    public String resolveConfigurationVersion( String netServerTypeId,String serverVersionId) throws DataManagerException {
        NetServerVersionData resultObject = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId",netServerTypeId))
            .add(Restrictions.eq("netServerVersion",serverVersionId));
            resultObject = (NetServerVersionData) criteria.uniqueResult();
            return resultObject.getConfigVersion();
        }
        catch(NonUniqueObjectException e) {
            throw new VersionNotSupportedException("No Configration Found for. netServerTypeId:='"+netServerTypeId+"',serverVersionId='"+serverVersionId+"'",e);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    /*
     * @author kashikvira
     * @param  alias - Configuration Alias.
     * @param  configVersion - identify Configuration Version.
     */
    
    public INetConfigurationData getCompatibleUpgradeVersionOfConfigrationData(String alias,String configVersion) throws DataManagerException{
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class)
            .add(Restrictions.eq("alias",alias))
            .add(Restrictions.eq("configVersion",configVersion));
            return(INetConfigurationData) criteria.uniqueResult();
        }
        catch(NonUniqueObjectException e) {
            throw new UniqueResultExpectedException("Unique CompatibleUpgradeVersionOfConfigration Expected for alias :"+alias+",configVersion :"+configVersion,e);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    /*
     * @author kaushik vira
     * @param INetConfigurationInstanceData - Identity ConfigurationInstance
     * @param 
     * @return List<INetConfigurationValuesData> - return list of configuration Parameter orderBy instance id.
     */
    public List<INetConfigurationValuesData> getConfigurationParameterValueDataList( INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetConfigurationValuesData.class)
            .add(Restrictions.eq("configInstanceId",netConfigurationInstanceData.getConfigInstanceId()))
            .addOrder(Order.asc("instanceId"));
            return criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    /*
     * @author kaushik vira
     * @param configId - Identity Configuration
     * @return Map<String,String> - return parameter map<parameterId,ParentParameterid> for specified configId.
     */
    public Map<String, INetConfigurationParameterData> getConfigurationParameterMap( String configId ) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetConfigurationParameterData.class)
            .add(Restrictions.eq("configId",configId));
            Map<String,INetConfigurationParameterData> resultMap = new HashMap<String, INetConfigurationParameterData>();
            List<INetConfigurationParameterData> lstNetConfigurationParameterData = criteria.list();
            for ( INetConfigurationParameterData netConfigurationParameterData : lstNetConfigurationParameterData ) {
                INetConfigurationParameterData tempNetConfigurationParameterData  = new NetConfigurationParameterData();
                tempNetConfigurationParameterData.setAlias(netConfigurationParameterData.getAlias());
                tempNetConfigurationParameterData.setDefaultValue(netConfigurationParameterData.getDefaultValue());
                tempNetConfigurationParameterData.setParentParameterId(netConfigurationParameterData.getParentParameterId());
                resultMap.put(netConfigurationParameterData.getParameterId(),tempNetConfigurationParameterData);
            }
            return resultMap;
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    public void upadateNetServerConfigurationInstance( INetConfigurationInstanceData netConfigurationInstanceData ) throws DataManagerException {
        Session session = getSession();
        try {
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class)
            .add(Restrictions.eq("configInstanceId",netConfigurationInstanceData.getConfigInstanceId()));
            
            INetConfigurationInstanceData resultnetConfigurationInstanceData = (NetConfigurationInstanceData) criteria.uniqueResult();
            resultnetConfigurationInstanceData.setConfigId(netConfigurationInstanceData.getConfigId());
            session.update(resultnetConfigurationInstanceData);
            session.flush();
        }catch(NonUniqueObjectException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    
    public INetServerInstanceData updateServerVersion(long netServerInstanceId,String ServerVersion,Timestamp statusChangeDate) throws DataManagerException{
        Session session = getSession();
        try {
            Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            .add(Restrictions.eq("systemGenerated","N"))
            .add(Restrictions.eq("netServerId",netServerInstanceId));
            
            NetServerInstanceData  netServerInstanceData = (NetServerInstanceData) criteria.uniqueResult();
            
            netServerInstanceData.setVersion(ServerVersion);
            netServerInstanceData.setLastModifiedDate(statusChangeDate);
            session.update(netServerInstanceData);
            
            session.flush();
            return netServerInstanceData;
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public List getNetServerInstanceListByTypeId(String netServerTypeId) throws DataManagerException{
        List lstNetServerInstanceListByType = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class);
            
            criteria.add(Restrictions.eq("systemGenerated","N"));
            criteria.add(Restrictions.eq("netServerTypeId",netServerTypeId));
            criteria.addOrder(Order.asc("netServerTypeId"));
            criteria.addOrder(Order.asc("netServerId"));
            
            lstNetServerInstanceListByType = criteria.list(); 
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServerInstanceListByType;
    }
    
    public INetServerInstanceData getNetServerInstanceServerConfig(long configInstanceId) throws DataManagerException {
    	  long netServerId ;
          INetServerInstanceData netServerInstanceData = null;
          try {
              Session session = getSession();
              Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
              INetServerInstanceConfMapData netServerInstanceConfMapData = (INetServerInstanceConfMapData) criteria.uniqueResult();
              netServerId = netServerInstanceConfMapData.getNetServerId();
              
              criteria = session.createCriteria(NetServerInstanceData.class).add(Restrictions.eq("netServerId", netServerId));
              netServerInstanceData = (INetServerInstanceData) criteria.uniqueResult();
              
          }
          catch (HibernateException hExp) {
              throw new DataManagerException(hExp.getMessage(), hExp);
          }
          catch (Exception exp) {
              throw new DataManagerException(exp.getMessage(), exp);
          }
          
          return netServerInstanceData;
    }



//    public INetConfigurationData getNetConfigurationDataByInstance(String configInstanceId) throws DataManagerException{
//    	
//     	try {
//    		Session session = getSession();
//    		Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
//    		NetConfigurationInstanceData  netConfigurationInstanceData =(NetConfigurationInstanceData) criteria.uniqueResult();
//    		criteria = session.createCriteria(INetConfigurationData.class).add(Restrictions.eq("netConfigId", netConfigurationInstanceData.getConfigId())).add(Restrictions.eq("systemGenerated", "N"));
//    		
//    		return (INetConfigurationData) criteria.uniqueResult();
//    	}
//    	catch (HibernateException hExp) {
//    		throw new DataManagerException(hExp.getMessage(), hExp);
//    	}
//    	catch (Exception exp) {
//    		throw new DataManagerException(exp.getMessage(), exp);
//    	}
//    }
     
        
    public List<INetConfigurationInstanceData> getNetServerConfiguration(Long netServerId) throws DataManagerException{

    	List<INetConfigurationInstanceData> lstNetServerConfig = new ArrayList<INetConfigurationInstanceData>();
    	Criteria criteria=null;
    	INetServerInstanceData netServerInstanceData=null;
    	try {
    		Session session = getSession();

    		criteria = session.createCriteria(NetServerInstanceData.class)
    		.add(Restrictions.eq("netServerId",netServerId));

    		netServerInstanceData=(NetServerInstanceData)criteria.uniqueResult();

    		criteria = session.createCriteria(NetServerVersionData.class)
    		.add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()))
    		.add(Restrictions.eq("netServerVersion",netServerInstanceData.getVersion()))
    		.addOrder(Order.asc("netServerTypeId"));

    		INetServerVersionData netServerVersionData=(NetServerVersionData)criteria.uniqueResult();


    		criteria = session.createCriteria(NetServerConfigMapData.class)
    		.add(Restrictions.eq("netConfigMapTypeId","S"))
    		.add(Restrictions.eq("netServerTypeId",netServerInstanceData.getNetServerTypeId()));

    		INetServerConfigMapData confInstanceData =(NetServerConfigMapData)criteria.uniqueResult();


    		criteria = session.createCriteria(NetConfigurationInstanceData.class)
    		.add(Restrictions.eq("configId",confInstanceData.getNetConfigId()));

    		List<INetConfigurationInstanceData> conList = criteria.list();

    		Long[] configInstanceIdList = new Long[conList.size()];

    		for (int i = 0; i < configInstanceIdList.length; i++) {
    			configInstanceIdList[i]=conList.get(i).getConfigInstanceId();

    		}

    		criteria = session.createCriteria(NetServerInstanceConfMapData.class)
    		.add(Restrictions.eq("netServerId",netServerId)).add(Restrictions.in("configInstanceId", configInstanceIdList));



    		Iterator<INetServerInstanceConfMapData> iter= criteria.list().iterator();

    		while(iter.hasNext()){

    			INetServerInstanceConfMapData netServerInstanceConfMapData =iter.next();

    			criteria = session.createCriteria(NetConfigurationInstanceData.class)
    			.add(Restrictions.eq("configInstanceId",netServerInstanceConfMapData.getConfigInstanceId()))
    			.addOrder(Order.asc("configId"));

    			INetConfigurationInstanceData netConfigurationInstanceData=(NetConfigurationInstanceData)criteria.uniqueResult();

    			criteria = session.createCriteria(NetConfigurationData.class)
    			.add(Restrictions.eq("netConfigId",netConfigurationInstanceData.getConfigId()))
    			.add(Restrictions.eq("configVersion",netServerVersionData.getConfigVersion()))
    			.addOrder(Order.asc("netConfigId"));

    			List<NetConfigurationData> lstServerConfiguration= criteria.list();

    			if(lstServerConfiguration.size()>0){
    				INetConfigurationData netConfigurationData=(NetConfigurationData)criteria.uniqueResult();
    				netConfigurationInstanceData.setNetConfiguration(netConfigurationData);
    				lstNetServerConfig.add(netConfigurationInstanceData);
    			}
    		}

    	} catch (HibernateException hExp) {
    		throw new DataManagerException(hExp.getMessage(), hExp);
    	} catch (Exception exp){
    		throw new DataManagerException(exp.getMessage(), exp);
    	}
    	return lstNetServerConfig;
    }

    public void deleteServerInsatnceGroupRelations(long netServerId) throws DataManagerException{
        try {
        	
            Session session = getSession();
            Criteria criteria = session.createCriteria(ServerInstanceGroupRelationData.class)
            .add(Restrictions.eq("netServerInstanceId",String.valueOf(netServerId)));
            
            List<ServerInstanceGroupRelationData> serverInstanceGroupRelations = criteria.list();
            
            if(Collectionz.isNullOrEmpty(serverInstanceGroupRelations)== false){
            	ServerInstanceGroupRelationData serverInstanceGroupRelationData = (ServerInstanceGroupRelationData) serverInstanceGroupRelations.get(0);
                session.delete(serverInstanceGroupRelationData);
                session.flush();
            }
            
        } catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        } catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
}  

