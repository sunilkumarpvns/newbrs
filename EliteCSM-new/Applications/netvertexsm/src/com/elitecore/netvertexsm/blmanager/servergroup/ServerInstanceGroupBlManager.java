package com.elitecore.netvertexsm.blmanager.servergroup;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servergroup.ServerInstanceGroupDataManager;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupRelationData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm;

/**
 * Created by aditya on 11/5/16.
 */
public class ServerInstanceGroupBlManager extends BaseBLManager{

    private static ServerInstanceGroupBlManager serverInstanceGroupBlManager;

    private ServerInstanceGroupDataManager getServerInstanceGroupDataManager(IDataManagerSession session) {
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = (ServerInstanceGroupDataManager) DataManagerFactory.getInstance().getDataManager(ServerInstanceGroupDataManager.class, session);
        return serverInstanceGroupDataManager;
    }


    public static final ServerInstanceGroupBlManager getInstance(){
        if (serverInstanceGroupBlManager == null) {
            synchronized (ServerInstanceGroupBlManager.class) {
                if (serverInstanceGroupBlManager == null){
                    serverInstanceGroupBlManager = new ServerInstanceGroupBlManager();
                }
            }
        }
        return serverInstanceGroupBlManager;
    }


    public PageList search(ServerInstanceGroupData serverInstanceGroupData, int requiredPageNo,
                           Integer pageSize, IStaffData staffData,String staffBelongingGroups) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);

        if(serverInstanceGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

        try{
        	PageList pageList = serverInstanceGroupDataManager.search(serverInstanceGroupData, requiredPageNo, pageSize,staffBelongingGroups);
        	 return pageList;
        }catch(Exception e){
            throw new DataManagerException("Search Action failed. Reason:"+e.getMessage(),e);
        }finally{
            session.close();
        }
       
    }



    public void create(ServerInstanceGroupData serverInstanceGroupData,IStaffData staffData) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            serverInstanceGroupDataManager.create(serverInstanceGroupData);
            session.commit();
        } catch (DuplicateParameterFoundExcpetion exp) {
            session.rollback();
            throw new DuplicateParameterFoundExcpetion("Duplicate Network Detail. Reason: " + exp.getMessage());
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Create Action failed. Reason: " + exp.getMessage());
        } finally {
            session.close();
        }
    }


    public void update(ServerInstanceGroupData serverInstanceGroupData,IStaffData staffData) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            serverInstanceGroupDataManager.update(serverInstanceGroupData);
            session.commit();
        } catch (DuplicateParameterFoundExcpetion exp) {
            session.rollback();
            throw new DuplicateParameterFoundExcpetion("Duplicate Network Detail. Reason: " + exp.getMessage());
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Update Action failed. Reason: " + exp.getMessage());
        } finally {
            session.close();
        }
    }

    public void delete(String ids,IStaffData staffData) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            List<ServerInstanceGroupRelationData> serverInstanceGroupRelationDatas = getServerInstanceGroupRelationDatasBy(ids);
            List<Long> selectedNetserverIdList = new ArrayList<Long>();
            if(Collectionz.isNullOrEmpty(serverInstanceGroupRelationDatas)== false){
            	for(ServerInstanceGroupRelationData serverInstanceGroupRelationData : serverInstanceGroupRelationDatas){
            		selectedNetserverIdList.add(Long.valueOf(serverInstanceGroupRelationData.getNetServerInstanceId()));
            	}
            	netServerBLManager.deleteServer(selectedNetserverIdList);
            }
            serverInstanceGroupDataManager.delete(ids);
            session.commit();
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Delete Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }
    }

    public ServerInstanceGroupData getServerInstanceGroupData(String id) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            return serverInstanceGroupDataManager.getServerIntanceGroupData(id);
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }



    }
    
    @SuppressWarnings("unchecked")
	public List<ServerInstanceGroupRelationForm> getServerInstanceGroupRelationDatas() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            return serverInstanceGroupDataManager.getServerInstanceGroupRelationDatas();
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }



    }
    
    public List<ServerInstanceGroupRelationData> getServerInstanceGroupRelationDatasBy(String serverInstanceId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

        if (serverInstanceGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            return serverInstanceGroupDataManager.getServerInstanceGroupRelationDatasBy(serverInstanceId);
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }

    }
    
    /**
     * Manage operation related to hibernate session
     * @param serverInstanceId
     * @throws DataManagerException
     * @author Dhyani.Raval
     */
    public void swapInstances(String serverInstanceId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);

        if (serverInstanceGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            serverInstanceGroupDataManager.swapInstances(serverInstanceId);
            session.commit();
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }
    }
    
    /**
     * Manage order of the server groups
     * @param serverInstanceGroupId
     * @throws DataManagerException
     * @author Dhyani.Raval
     */
    public void manageOrder(String[] serverInstanceGroupId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);

        if (serverInstanceGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            serverInstanceGroupDataManager.manageOrder(serverInstanceGroupId);
            session.commit();
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }
    }
    
    /**
     * Return MaxOrder from the data manager
     * @return
     * @throws DataManagerException
     * @author Dhyani.Raval
     */
    public Integer getMaxOrder() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);
        Integer maxOrder = null;
        if (serverInstanceGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            maxOrder = serverInstanceGroupDataManager.getMaxOrder();
            session.commit();
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }
		return maxOrder;
    }

    
    /**
     * Fetch ServerInstanceGroupRelation by serverInstanceId
     * @param serverInstanceId
     * @return ServerInstanceGroupRelationData
     * @throws DataManagerException
     * @author Dhyani.Raval
     * 
     */
    public ServerInstanceGroupRelationData getServerInstanceRelationDatasBy(String serverInstanceId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ServerInstanceGroupDataManager serverInstanceGroupDataManager = getServerInstanceGroupDataManager(session);

        if (serverInstanceGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try {
            session.beginTransaction();
            return serverInstanceGroupDataManager.getServerInstanceRelationDatasBy(serverInstanceId);
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Action failed. Reason: "+exp.getMessage());
        }finally{
            session.close();
        }
    }
}
