
package com.elitecore.netvertexsm.hibernate.servermgr.service;

/**
 * @author dhavalraval
 */
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.VersionNotSupportedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.service.NetServiceDataManager;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.logger.Logger;


public class HServiceDataManager extends HBaseDataManager implements NetServiceDataManager {
    
    private final static String ENTITY_NAME = "HServiceDataManager";
    
    /**
     * @author  dhavalraval
     * @param   netConfigurationInstanceData Create Method
     * @throws  DataManagerException
     * @purpose This method is generated to create NetConfigurationInstanceData.
     */
    public INetConfigurationInstanceData createNetConfigurationInstance( INetConfigurationInstanceData netConfigurationInstanceData ) throws DataManagerException {
        try {
            Session session = getSession();
            session.save(netConfigurationInstanceData);
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationInstanceData;
    }
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @purpose This method is generated to give list of NetServerConfigMapTypeData.
     * @throws  DataManagerException 
     */
    public INetServiceConfigMapTypeData getNetServiceConfigMapTypeList( INetServiceConfigMapTypeData netServiceConfigMapTypeData ) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceConfigMapTypeData.class)
            .add(Restrictions.eq("netConfigMapTypeId", netServiceConfigMapTypeData.getNetConfigMapTypeId()));
            return (INetServiceConfigMapTypeData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   netConfigurationValues Create Method.
     * @throws  DataManagerException.
     * @purpose This method is generated to create NetConfigurationValuesData.
     */
    public void createNetConfigurationValues( List lstConfigParamValue ) throws DataManagerException {
        INetConfigurationValuesData netConfigurationValues = null;
        try {
            Session session = getSession();
            for ( int i = 0; i < lstConfigParamValue.size(); i++ ) {
                netConfigurationValues = (INetConfigurationValuesData) lstConfigParamValue.get(i);
                session.save(netConfigurationValues);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @purpose This method is generated to give list of NetServiceInstanceData.
     * @throws  DataManagerException
     */
    public List<INetServiceConfigMapData> getNetServiceInstanceList( ) throws DataManagerException {
        List<INetServiceConfigMapData> lstNetServiceInstanceList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class);
            lstNetServiceInstanceList = criteria.list();
        }
        catch (HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            exp.getMessage();
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServiceInstanceList;
    }
    
    /**
     * @author  dhavalraval
     * @author  updated By - kaushikvira
     * @return  Returns List
     * @purpose This method is generated to give list of NetServiceInstanceData.
     * @throws  DataManagerException
     */
    public List<NetServiceInstanceData> getNetserviceInstanceList( long netServerId ) throws DataManagerException {
        
        EliteAssert.notNull(netServerId,"netServerId must be specified.");
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class)
            .add(Restrictions.eq("netServerId", netServerId))
            .add(Restrictions.eq("systemGenerated", "N"))
            .addOrder(Order.asc("netServiceTypeId"));
            return criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
 
   
    
    /**
     * @author  dhavalraval
     * @author  updated By kaushikvira
     * @param   netServiceInstanceData
     * @throws  DatatManagerException
     * @return  Returns List
     * @purpose This method is generated to give the list of NetServiceInstance.
     * 
     */
    public INetServiceInstanceData getNetServiceInstance( INetServiceInstanceData netServiceInstanceData ) throws DataManagerException {
        EliteAssert.notNull(netServiceInstanceData,"netServiceInstanceData Must Specified");
        EliteAssert.notNull(netServiceInstanceData.getNetServiceId(),"netServiceInstanceData.getNetServiceId() Must Specified");
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class)
            .add(Restrictions.eq("systemGenerated", "N"))
            .add(Restrictions.eq("netServiceId", netServiceInstanceData.getNetServiceId()));
            
            return (INetServiceInstanceData) criteria.uniqueResult();
        }
        catch(NonUniqueObjectException e) {
            throw new DataManagerException(e.getMessage(), e);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
 
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServiceTypeData.
     */
    public List<INetServiceConfigMapData> getNetServiceTypeList( ) throws DataManagerException {
        List<INetServiceConfigMapData> serviceTypeList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceTypeData.class)
            //          .addOrder(Order.asc("name"));
            .addOrder(Order.asc("netServiceTypeId")).add(Restrictions.eq("systemGenerated", "N"));
            
            serviceTypeList = criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return serviceTypeList;
    }
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServiceTypeData.
     */
    public List<INetServiceConfigMapData> getNetServiceTypeList( String netServerTypeId ) throws DataManagerException {
        List<INetServiceConfigMapData> serviceTypeList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceTypeData.class)
                .add(Restrictions.eq("netServerTypeId", netServerTypeId))
                .add(Restrictions.eq("systemGenerated", "N"));
            
            serviceTypeList = criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return serviceTypeList;
    }
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @return  Returns List
     * @throws  DataManagerException 
     * @purpose This method is generated to give list of NetServiceInstanceConfMap.
     */
    public List<INetServiceInstanceConfMapData> getNetServiceInstanceConfMap( long netServiceId ) throws DataManagerException {
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class)
            .add(Restrictions.eq("netServiceId", netServiceId));
            
            return criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    

    
    /**
     * @author  dhavalraval
     * @return  NetConfigurationData class object
     * @param   configId
     * @throws  DataManagerException
     * @purpose This method is generated to get the parent-node (ROOT NODE) in NetConfigurationParameterData. 
     */
    public INetConfigurationData getRootParameterConfigurationData( String configId ) throws DataManagerException {
        INetConfigurationData configurationData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationParameterData.class).add(Restrictions.eq("configId", configId)).add(Restrictions.isNull("parentParameterId"));
            
            Set<INetConfigurationParameterData> stConfigParamSet = new HashSet<INetConfigurationParameterData>();
            stConfigParamSet.addAll(criteria.list());
            
            criteria = session.createCriteria(NetConfigurationData.class).add(Restrictions.eq("netConfigId", configId));
            
            configurationData = (INetConfigurationData) criteria.uniqueResult();
            configurationData.setNetConfigParameters(stConfigParamSet);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return configurationData;
    }
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @throws  DataManagerException
     * @purpose This method is generated to remove the netServiceInstanceData.
     */
    public void deleteNetServiceInstance( long netServiceId ) throws DataManagerException {
        INetServiceInstanceData netServiceInstanceData = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId)).add(Restrictions.eq("systemGenerated", "N"));
            
            List<INetServiceConfigMapData> lstNetServiceInstance = criteria.list();
            
            if (lstNetServiceInstance != null && lstNetServiceInstance.size() > 0) {
                netServiceInstanceData = (INetServiceInstanceData) lstNetServiceInstance.get(0);
                session.delete(netServiceInstanceData);
                session.flush();
            }
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    

    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purpose This method is generated to remove the netConfigurationInstance.
     */
    public void deleteNetConfigurationInstance( long configInstanceId ) throws DataManagerException {
        INetConfigurationInstanceData netConfigurationInstanceData = null;
        try {
            Session session = getSession();
            
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configInstanceId", configInstanceId));
            
            List<NetConfigurationInstanceData> lstNetConfigurationInstance = criteria.list();
            
            for ( int i = 0; i < lstNetConfigurationInstance.size(); i++ ) {
                netConfigurationInstanceData = lstNetConfigurationInstance.get(i);
                session.delete(netConfigurationInstanceData);
            }
            
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purose  This method is generated to remove the netConfigurationValues.
     */
    public void deleteNetConfigurationValues( long configInstanceId ) throws DataManagerException {
        INetConfigurationValuesData netConfigurationValuesData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class)
             .add(Restrictions.eq("configInstanceId", configInstanceId));
            
            List<INetConfigurationValuesData> lstNetConfigurationValues = criteria.list();
            
            for ( int i = 0; i < lstNetConfigurationValues.size(); i++ ) {
                netConfigurationValuesData = lstNetConfigurationValues.get(i);
                session.delete(netConfigurationValuesData);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author Kaushik vira.
     * @param   netServiceId
     * @throws  DataManagerException
     * @purpose This method is generated to remove the netServiceInstanceConfMap.
     */
    public void deleteNetServiceInstanceConfMap( long configIntanceId ) throws DataManagerException {
        EliteAssert.notNull(configIntanceId,"netConfigInstanceData Must be Specified");
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class)
            .add(Restrictions.eq("configInstanceId",configIntanceId));
            session.delete(criteria.uniqueResult());
            session.flush();
        } catch (HibernateException hExp) {
            throw new DataManagerException("deleteNetServiceInstanceConfMap Operation Failed. Reason:-"+hExp.getMessage(), hExp);
        } catch (Exception exp){
            throw new DataManagerException("deleteNetServiceInstanceConfMap Operation Failed. Reason:-"+exp.getMessage(), exp);
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
    public void updateBasicDetail( INetServiceInstanceData inetServiceInstanceData , Timestamp statusChangeDate ) throws DataManagerException {
        Session session = getSession();
        NetServiceInstanceData netServiceInstanceData = null;
        if (inetServiceInstanceData == null) 
             throw new InvalidArrguementsException("inetServiceInstanceData Must not be Null");
        
            try {
                Criteria criteria = session.createCriteria(NetServiceInstanceData.class);
                netServiceInstanceData = (NetServiceInstanceData) criteria.add(Restrictions.eq("netServiceId", inetServiceInstanceData.getNetServiceId())).uniqueResult();
                
                netServiceInstanceData.setName(inetServiceInstanceData.getName());
                netServiceInstanceData.setDescription(inetServiceInstanceData.getDescription());
                netServiceInstanceData.setStatusChangeDate(statusChangeDate);
                netServiceInstanceData.setLastModifiedDate(statusChangeDate);
                
                session.update(netServiceInstanceData);
                session.flush();
            }
            catch (HibernateException hExp) {
                throw new DataManagerException(hExp.getMessage(), hExp);
            }
            catch (Exception exp) {
                throw new DataManagerException(exp.getMessage(), exp);
            }
    }
    
    /**
     * @author  dhavalraval
     * @param   netServiceInstanceData object
     * @throws  DataManagerException
     * @purpose This method is generated to create NetServiceInstanceData.
     */
    public INetServiceInstanceData createServiceInstance( INetServiceInstanceData netServiceInstanceData ) throws DataManagerException {
        try {
            Session session = getSession();
            session.save(netServiceInstanceData);
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServiceInstanceData;
    }
    
    /**
     * @author  dhavalraval
     * @author  updated by - kaushikvira
     * @return  Returns List
     * @param   netServerTypeId
     * @param   netServerId
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServerConfigMapData.
     */
    public List<INetServiceConfigMapData> getNetServiceConfigMapList( String netServiceTypeId , long netServerId ) throws DataManagerException {
        
        Criteria criteria = null;
        List<INetServiceConfigMapData> netServiceConfigMapList = new ArrayList<INetServiceConfigMapData>();
        try {
            Session session = getSession();
            INetServerVersionData netServerVersionData = getCompatibleVersion(netServerId);
            criteria = session.createCriteria(NetServiceConfigMapData.class)
                .add(Restrictions.eq("netServiceTypeId", netServiceTypeId));
            
            List<INetServiceConfigMapData> listNetServiceConfigMapData = criteria.list();
            
            for ( INetServiceConfigMapData netServiceConfigMapData : listNetServiceConfigMapData ) {
                
                criteria = session.createCriteria(NetConfigurationData.class)
                .add(Restrictions.eq("netConfigId", netServiceConfigMapData.getNetConfigId()))
                .add(Restrictions.eq("configVersion", netServerVersionData.getConfigVersion()))
                .addOrder(Order.asc("netConfigId"));
                
                if (criteria.list().size() > 0) {
                    netServiceConfigMapList.add(netServiceConfigMapData);
                }
            }
        }
        catch (DataManagerException e) {
            throw new DataManagerException(e.getMessage(), e);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServiceConfigMapList;
    }
    
    
    /**
     * @author  kaushik Vira 
     */
    public List<INetServiceConfigMapData> getNetServiceConfigMapList( String netServiceTypeId,String netServerTypeId,String serverVersion) throws DataManagerException {
        Criteria criteria = null;
        List<INetServiceConfigMapData> netServiceConfigMapList = new ArrayList<INetServiceConfigMapData>();
        try {
            Session session = getSession();
            INetServerVersionData netServerVersionData = getCompatibleVersion(netServerTypeId,serverVersion);
            
            criteria = session.createCriteria(NetServiceConfigMapData.class)
            .add(Restrictions.eq("netServiceTypeId", netServiceTypeId));
            
            List<INetServiceConfigMapData> listNetServiceConfigMapData = criteria.list();
            
            for ( INetServiceConfigMapData netServiceConfigMapData : listNetServiceConfigMapData ) {
                
                criteria = session.createCriteria(NetConfigurationData.class)
                .add(Restrictions.eq("netConfigId", netServiceConfigMapData.getNetConfigId()))
                .add(Restrictions.eq("configVersion", netServerVersionData.getConfigVersion()))
                .addOrder(Order.asc("netConfigId"));
                
                if (criteria.list().size() > 0) {
                    netServiceConfigMapList.add(netServiceConfigMapData);
                }
            }
        }
        catch (DataManagerException e) {
            throw new DataManagerException(e.getMessage(), e);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServiceConfigMapList;
    }
    
    /**
     * @author  dhavalraval 
     * @param   netServiceInstanceConfMap  
     * @throws  HibernateException.
     * @purpose This method is generated to create NetServiceInstanceConfMapData.
     */
    public void createNetServiceInstanceConfMap( INetServiceInstanceConfMapData netServiceInstanceConfMap ) throws DataManagerException {
        try {
            Session session = getSession();
            session.save(netServiceInstanceConfMap);
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @throws  DataManagerException
     * @return  Returns List
     * @purpose This method is generated to give list of ServiceInstnaceConfMapData by using configInstanceId
     */
    public List<NetConfigurationInstanceData> getNetServiceConfigInstanceList( long netServiceId ) throws DataManagerException {
        
        List<NetConfigurationInstanceData> lstNetServiceConfig = new ArrayList<NetConfigurationInstanceData>();
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class)
            .add(Restrictions.eq("netServiceId", netServiceId));
            List<NetServiceInstanceConfMapData> lstNetServiceConfInst = criteria.list();
            
            for ( int i = 0; i < lstNetServiceConfInst.size(); i++ ) {
                NetServiceInstanceConfMapData confMapData = lstNetServiceConfInst.get(i);
                criteria = session.createCriteria(NetConfigurationInstanceData.class)
                .add(Restrictions.eq("configInstanceId", confMapData.getConfigInstanceId()));
                
                NetConfigurationInstanceData netConfigurationInstanceData = (NetConfigurationInstanceData) criteria.uniqueResult();
                
                if (netConfigurationInstanceData != null) {
                    criteria = session.createCriteria(NetConfigurationData.class)
                    .add(Restrictions.eq("netConfigId", netConfigurationInstanceData.getConfigId()));
                    netConfigurationInstanceData.setNetConfiguration((INetConfigurationData) criteria.uniqueResult());
                }
                lstNetServiceConfig.add(netConfigurationInstanceData);
            }
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServiceConfig;
    }
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @throws  DataManagerException
     * @return  Returns List
     * @purpose This method is generated to give list of ServiceInstnaceConfMapData by using configInstanceId
     */
    public List<NetConfigurationInstanceData> getNetServiceConfigInstanceList( long netServiceId , String netConfigMapTypeId ) throws DataManagerException {
        List<NetConfigurationInstanceData> lstNetServiceConfig = new ArrayList<NetConfigurationInstanceData>();
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class).add(Restrictions.eq("netServiceId", netServiceId));
            
            List<INetServiceConfigMapData> lstNetServiceConfInst = criteria.list();
            
            for ( int i = 0; i < lstNetServiceConfInst.size(); i++ ) {
                NetServiceInstanceConfMapData netServiceInstanceConfMapData = (NetServiceInstanceConfMapData) lstNetServiceConfInst.get(i);
                
                criteria = session.createCriteria(NetConfigurationInstanceData.class).add(Restrictions.eq("configInstanceId", netServiceInstanceConfMapData.getConfigInstanceId()));
                
                NetConfigurationInstanceData netConfigurationInstanceData = (NetConfigurationInstanceData) criteria.uniqueResult();
                
                if (netConfigurationInstanceData != null) {
                    criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceInstanceConfMapData.getNetServiceId())).add(Restrictions.eq("systemGenerated", "N"));
                    
                    NetServiceInstanceData netServiceInstanceData = (NetServiceInstanceData) criteria.uniqueResult();
                    
                    if (netServiceInstanceData != null) {
                        criteria = session.createCriteria(NetServiceConfigMapData.class).add(Restrictions.eq("netConfigMapTypeId", netConfigMapTypeId)).add(Restrictions.eq("netConfigId", netConfigurationInstanceData.getConfigId())).add(Restrictions.eq("netServiceTypeId", netServiceInstanceData.getNetServiceTypeId()));
                        
                        if (criteria.list() != null && criteria.list().size() > 0) {
                            lstNetServiceConfig.add(netConfigurationInstanceData);
                        }
                    }
                }
            }
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return lstNetServiceConfig;
    }
    

    
    public INetConfigurationInstanceData getConfigurationInstance( long configInstanceId ) throws DataManagerException {
        
        INetConfigurationInstanceData configurationInstanceData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class);
            criteria.add(Restrictions.eq("configInstanceId", configInstanceId));
            configurationInstanceData = (INetConfigurationInstanceData) criteria.uniqueResult();
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return configurationInstanceData;
    }
    
    public INetConfigurationInstanceData getServerConfigurationInstanceData( long serverId , String configId ) throws DataManagerException {
        
        INetConfigurationInstanceData configurationInstanceData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceConfMapData.class);//NetServerInstanceConfMap.class);
            criteria.add(Restrictions.eq("netServerId", serverId));
            criteria.createAlias("netConfigurationInstance", "netConfigInstanceData");
            criteria.add(Restrictions.eq("netConfigInstanceData.configId", configId));
            INetServerInstanceConfMapData netServerInstanceConfMap = (INetServerInstanceConfMapData) criteria.uniqueResult();
          
            if (netServerInstanceConfMap != null){
            	Criteria criteria1 = session.createCriteria(NetConfigurationInstanceData.class).add(Restrictions.eq("configInstanceId", netServerInstanceConfMap.getConfigInstanceId()));
                configurationInstanceData = (INetConfigurationInstanceData)criteria1.uniqueResult();
            }
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return configurationInstanceData;
        
    }
    
    public INetConfigurationInstanceData getServiceConfigurationInstanceData( long serviceId , String configId ) throws DataManagerException {
        
        INetConfigurationInstanceData configurationInstanceData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class);//NetServiceInstanceConfMap.class);
            criteria.add(Restrictions.eq("netServiceId", serviceId));
            criteria.createAlias("netConfigurationInstance", "netConfigInstanceData");
            criteria.add(Restrictions.eq("netConfigInstanceData.configId", configId));
            
            INetServiceInstanceConfMapData netServiceInstanceConfMapData = (INetServiceInstanceConfMapData) criteria.uniqueResult();
            
            if (netServiceInstanceConfMapData != null)
                configurationInstanceData = netServiceInstanceConfMapData.getNetConfigurationInstance();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
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
    public INetConfigurationParameterData getNetConfigurationParameterData( String parameterId , String configId ) throws DataManagerException {
        
        INetConfigurationParameterData netConfigurationParameterData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationParameterData.class).add(Restrictions.eq("parameterId", parameterId)).add(Restrictions.eq("configId", configId));
            
            netConfigurationParameterData = (INetConfigurationParameterData) criteria.uniqueResult();
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationParameterData;
    }
    
    /**
     * @author dhavan
     * 
     */
    public void saveNetConfValuesData( List lstValueData , long configInstanceId ) throws DataManagerException {
        try {
            Session session = getSession();
            session.flush();
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
            List<INetServiceConfigMapData> lstDelValueData = criteria.list();
            
            for ( int i = 0; i < lstDelValueData.size(); i++ ) {
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData) lstDelValueData.get(i);
                session.delete(netConfValuesData);
            }
           session.flush();
            for ( int i = 0; i < lstValueData.size(); i++ ) {
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData) lstValueData.get(i);
                session.save(netConfValuesData);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
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
    public String getNetConfigurationName( long configInstanceId ) throws DataManagerException {
        String strCnfName = null;
        String strNetConfigId = null;
        strNetConfigId = getNetConfigurationId(configInstanceId);
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class).add(Restrictions.eq("netConfigId", strNetConfigId));
            INetConfigurationData netConfigurationData = (INetConfigurationData) criteria.uniqueResult();
            strCnfName = netConfigurationData.getName();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
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
    private String getNetConfigurationId( long configInstanceId ) throws DataManagerException {
        String strConfigId = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationInstanceData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
            INetConfigurationInstanceData netConfigurationInstanceData = (INetConfigurationInstanceData) criteria.uniqueResult();
            strConfigId = netConfigurationInstanceData.getConfigId();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return strConfigId;
    }
    
    /**
     * @author dhavan
     * This method return the service name
     * based on config instance id.
     * 
     * @param strConfigInstanceId
     * @return
     * @throws DataManagerException
     */
    public String getNetServiceNameServiceConfig( long configInstanceId ) throws DataManagerException {
        String strNetServiceName = null;
        long netServiceId;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
            INetServiceInstanceConfMapData netServiceInstanceConfMapData = (INetServiceInstanceConfMapData) criteria.uniqueResult();
            netServiceId = netServiceInstanceConfMapData.getNetServiceId();
            strNetServiceName = getNetServiceName(netServiceId);
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return strNetServiceName;
    }
    
    private String getNetServiceName( long netServiceId ) throws DataManagerException {
        String strNetServiceName = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId));
            INetServiceInstanceData netServiceInstanceData = (INetServiceInstanceData) criteria.uniqueResult();
            strNetServiceName = netServiceInstanceData.getDisplayName();
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return strNetServiceName;
    }
    
  
    private String getNetServerName( long netServerId ) throws DataManagerException {
        String strNetServerName = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServerInstanceData.class).add(Restrictions.eq("netServerId", netServerId)).add(Restrictions.eq("systemGenerated", "N"));
            INetServerInstanceData netServerInstanceData = (INetServerInstanceData) criteria.uniqueResult();
            strNetServerName = netServerInstanceData.getName();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }    
        return strNetServerName;
    }
    
    public INetConfigurationData getNetConfigurationData( String configId ) throws DataManagerException {
        INetConfigurationData netConfigurationData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationData.class).add(Restrictions.eq("netConfigId", configId));
            netConfigurationData = (INetConfigurationData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netConfigurationData;
    }
    
   
    
    
   
   
    public INetServiceTypeData getNetServiceType( String serviceTypeId ) throws DataManagerException {
        INetServiceTypeData netServiceTypeData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetServiceTypeData.class).add(Restrictions.eq("netServiceTypeId", serviceTypeId)).add(Restrictions.eq("systemGenerated", "N"));
            
            netServiceTypeData = (INetServiceTypeData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return netServiceTypeData;
        
    }
    
    
    
    public String getMaxServiceInstance( String netServiceTypeId , long netServerId ) throws DataManagerException {
        String instanceId = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(INetServiceInstanceData.class).add(Restrictions.eq("netServiceTypeId", netServiceTypeId)).add(Restrictions.eq("netServerId", netServerId)).add(Restrictions.eq("systemGenerated", "N")).addOrder(Order.desc("instanceId"));
            
            List<INetServiceConfigMapData> serviceInstanceList = criteria.list();
            
            if (serviceInstanceList.size() > 0) {
                INetServiceInstanceData netServiceTypeData = (INetServiceInstanceData) serviceInstanceList.get(0);
                instanceId = netServiceTypeData.getInstanceId();
            } else {
                instanceId = null;
            }
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return instanceId;
        
    }
    
    public void updateServiceInstanceId( long netServiceId , String instanceId ) throws DataManagerException {
        Session session = getSession();
        try {
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId)).add(Restrictions.eq("systemGenerated", "N"));
            
            NetServiceInstanceData netServiceInstanceData = (NetServiceInstanceData) criteria.uniqueResult();
            netServiceInstanceData.setInstanceId(instanceId);
            session.update(netServiceInstanceData);
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
  
    public INetConfigurationParameterData getNetConfigurationParameterData( INetServerInstanceData serverInstanceData , String paramName ) throws DataManagerException {
        INetConfigurationParameterData netConfigurationParameterData = null;
        try {
            Session session = getSession();
            
            Criteria  criteria;
            /* based on Server Type and Version Get Configuration Version*/
            String configVersionId = null;
            try {
                criteria = session.createCriteria(NetServerVersionData.class);
                criteria.add(Restrictions.eq("netServerTypeId", serverInstanceData.getNetServerTypeId()));
                criteria.add(Restrictions.eq("netServerVersion",serverInstanceData.getVersion()));
                configVersionId = ((NetServerVersionData) criteria.uniqueResult()).getConfigVersion();
            }
            catch(NonUniqueObjectException e) {
                throw new VersionNotSupportedException("No Configration Found for. netServerTypeId:='"+serverInstanceData.getNetServerTypeId()+"',serverVersionId='"+serverInstanceData.getVersion()+"'",e);
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
            
            /* Get All the ServerConfigMapData for Server Type*/
            criteria = session.createCriteria(NetServerConfigMapData.class);
            criteria.add(Restrictions.eq("netServerTypeId", serverInstanceData.getNetServerTypeId()));
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
                netConfigurationParameterData = (INetConfigurationParameterData) criteria.uniqueResult();
                if (netConfigurationParameterData != null) {
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
        return netConfigurationParameterData;
    }
    
    public List<INetConfigurationValuesData> getNetConfigParameterValueList( long configInstanceId , String parameterId ) throws DataManagerException {
        List<INetConfigurationValuesData> lstConfigValue = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetConfigurationValuesData.class);
            criteria.add(Restrictions.eq("configInstanceId", configInstanceId));
            criteria.add(Restrictions.eq("parameterId", parameterId));
            lstConfigValue = criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return lstConfigValue;
    }
    
    public void addNetConfValuesData( List lstValueData ) throws DataManagerException {
        try {
            Session session = getSession();
            for ( int i = 0; i < lstValueData.size(); i++ ) {
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData) lstValueData.get(i);
                session.save(netConfValuesData);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public void deleteNetConfValuesData( List lstValueData ) throws DataManagerException {
        try {
            Session session = getSession();
            for ( int i = 0; i < lstValueData.size(); i++ ) {
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData) lstValueData.get(i);
                session.delete(netConfValuesData);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    public INetConfigurationParameterData getNetConfigurationParameterData( INetServiceInstanceData serviceInstanceData , String paramName ) throws DataManagerException {
        INetConfigurationParameterData netConfigurationParameterData = null;
        try {
            Session session = getSession();
            
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
                criteria.add(Restrictions.eq("configId", configId));
                criteria.add(Restrictions.eq("alias", paramName));
                netConfigurationParameterData = (INetConfigurationParameterData) criteria.uniqueResult();
                if (netConfigurationParameterData != null) {
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
        
        return netConfigurationParameterData;
    }
    
    public INetServiceInstanceData getNetServiceInstanceServiceConfig( long configInstanceId ) throws DataManagerException {
        long  netServiceId;
        INetServiceInstanceData netServiceInstanceData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceConfMapData.class).add(Restrictions.eq("configInstanceId", configInstanceId));
            INetServiceInstanceConfMapData netServiceInstanceConfMapData = (INetServiceInstanceConfMapData) criteria.uniqueResult();
            netServiceId = netServiceInstanceConfMapData.getNetServiceId();
            
            criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId));
            netServiceInstanceData = (INetServiceInstanceData) criteria.uniqueResult();
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return netServiceInstanceData;
    }
    
    public void removeNetConfValuesData( List lstValueData ) throws DataManagerException {
        try {
            Session session = getSession();
            for ( int i = 0; i < lstValueData.size(); i++ ) {
                INetConfigurationValuesData netConfValuesData = (INetConfigurationValuesData) lstValueData.get(i);
                session.delete(netConfValuesData);
            }
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    
    public INetServiceInstanceData getNetServiceInstance(long netServerId,String netServiceName,String instanceId) throws DataManagerException{
    	NetServiceInstanceData netServiceInstanceData=null;
    	try {
    		Session session = getSession();
    		Criteria criteria = session.createCriteria(NetServiceTypeData.class).add(Restrictions.eq("alias",netServiceName)).add(Restrictions.eq("systemGenerated","N"));
    		NetServiceTypeData netServiceTypeData = (NetServiceTypeData)criteria.uniqueResult();
    		
    		Criteria criteriaNet = session.createCriteria(NetServiceInstanceData.class)
    		.add(Restrictions.eq("netServiceTypeId", netServiceTypeData.getNetServiceTypeId()))
    		.add(Restrictions.eq("systemGenerated","N"));
    		//.add(Restrictions.eq("netServerId", netServerId));
    		List<NetServiceInstanceData> list = criteriaNet.list();
    		Logger.logDebug("HServiceDataManager", "list :"+list);
    		Logger.logDebug("HServiceDataManager", "list :"+list);
    		//netServiceInstanceData = (NetServiceInstanceData)criteriaNet.uniqueResult();
    		//String id = netServiceInstanceData.getDescription();
    	}catch(HibernateException hExp){
    		throw new DataManagerException(hExp.getMessage(),hExp);
    	}catch(Exception exp){
    		throw new DataManagerException(exp.getMessage(),exp);
    	}
    return netServiceInstanceData;
    }
    
    public INetServiceTypeData getNetServiceType( String serverTypeId , String netServiceName ) throws DataManagerException {
        INetServiceTypeData netServiceTypeData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceTypeData.class).add(Restrictions.eq("alias", netServiceName)).add(Restrictions.eq("netServerTypeId", serverTypeId)).add(Restrictions.eq("systemGenerated", "N"));
            netServiceTypeData = (NetServiceTypeData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
        return netServiceTypeData;
    }
    
    public void changeIsConfigInSyncStaus( long netServiceId , String syncStatusType , Timestamp statusChangeDate ) throws DataManagerException {
        NetServiceInstanceData netServiceInstanceData = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId)).add(Restrictions.eq("systemGenerated", "N"));
            netServiceInstanceData = (NetServiceInstanceData) criteria.uniqueResult();
            netServiceInstanceData.setIsInSync(syncStatusType);
            netServiceInstanceData.setLastModifiedDate(statusChangeDate);
            session.update(netServiceInstanceData);
            session.flush();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
        
    }
    
    
    private INetServerVersionData getCompatibleVersion( String netServerTypeId,String netServerVersion ) throws DataManagerException {
        Criteria criteria = null;
        try {
            Session session = getSession();
            
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId", netServerTypeId))
            .add(Restrictions.eq("netServerVersion", netServerVersion))
            .addOrder(Order.asc("netServerTypeId"));
            
            return (NetServerVersionData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    private INetServerVersionData getCompatibleVersion( long netServerInstanceId ) throws DataManagerException {
        
        Criteria criteria = null;
        INetServerInstanceData netServerInstanceData = null;
        
        try {
            Session session = getSession();
            netServerInstanceData = getNetServerInstanceData(netServerInstanceId);
            
            criteria = session.createCriteria(NetServerVersionData.class)
            .add(Restrictions.eq("netServerTypeId", netServerInstanceData.getNetServerTypeId()))
            .add(Restrictions.eq("netServerVersion", netServerInstanceData.getVersion()))
            .addOrder(Order.asc("netServerTypeId"));
            
            return (NetServerVersionData) criteria.uniqueResult();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    private INetServerInstanceData getNetServerInstanceData( long netServerId ) throws DataManagerException {
        Criteria criteria = null;
        try {
            Session session = getSession();
            criteria = session.createCriteria(NetServerInstanceData.class).add(Restrictions.eq("netServerId", netServerId));
            return (NetServerInstanceData) criteria.uniqueResult();
            
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }

	public INetServiceInstanceData getNetServiceInstanceData(long netServiceId)
			throws DataManagerException {
		 Criteria criteria = null;
	        try {
	            Session session = getSession();
	            criteria = session.createCriteria(NetServiceInstanceData.class).add(Restrictions.eq("netServiceId", netServiceId));
	            return (NetServiceInstanceData) criteria.uniqueResult();
	            
	        }
	        catch (HibernateException hExp) {
	            throw new DataManagerException(hExp.getMessage(), hExp);
	        }
	        catch (Exception exp) {
	            throw new DataManagerException(exp.getMessage(), exp);
	        }
		
	}

}
