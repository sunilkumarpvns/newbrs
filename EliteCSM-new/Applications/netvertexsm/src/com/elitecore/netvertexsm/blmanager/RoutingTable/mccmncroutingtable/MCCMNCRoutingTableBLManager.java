package com.elitecore.netvertexsm.blmanager.RoutingTable.mccmncroutingtable;

import java.util.List;


import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.MCCMNCRoutingTableDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class MCCMNCRoutingTableBLManager extends BaseBLManager {
	
	
	
	/**This method id used to search list of  all Routing Entries 
	 * on the basis of Routing Entry name
	 * @param routingTableDataData
	 * @param requiredPageNo
	 * @param pageSize
	 * @param staffData
	 * @param actionAlias
	 * @return
	 * @throws DataManagerException
	 */
	public PageList search(RoutingTableData routingTableData, int requiredPageNo,
			Integer pageSize, IStaffData staffData, String actionAlias) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        MCCMNCRoutingTableDataManager routingTableDataDataManager = getDataDataManager(session);
        PageList routingTableDataDataList;
        
        if(routingTableDataDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	routingTableDataDataList = routingTableDataDataManager.search(routingTableData, requiredPageNo, pageSize);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }
        return routingTableDataDataList;
   }

	/**This method is used to create Routing Entry Data
	 * @param routingTableDataData
	 * @param staffData
	 * @param actionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void create(RoutingTableData routingTableData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCRoutingTableDataManager routingTableDataDatamanager = getDataDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (routingTableDataDatamanager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataDatamanager.create(routingTableData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Routing Id Detail. : "+exp.getMessage(),exp);
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
    }
	
	/**This method is used to get RoutingTableData List
	 * @return List of RoutingTableData
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public List<RoutingTableData> getRoutingTableDataList() throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCRoutingTableDataManager routingTableDataDataManager = getDataDataManager(session);
        List<RoutingTableData> routingTableDataDataList = null;
        if (routingTableDataDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataDataList = routingTableDataDataManager.getRoutingTableDataList();
        	return routingTableDataDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	
	}

	/**This method is used to delete Routing Entry Data 
	 * @param routingTableDataIDS(selected from search page)
	 * @param staffData
	 * @param deleteActionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void delete(Long[] routingTableDataIDS, IStaffData staffData,String deleteActionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCRoutingTableDataManager routingTableDataDataManager=getDataDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (routingTableDataDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataDataManager.delete(routingTableDataIDS);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, deleteActionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}

	/**This method is used to search Routing Entry Data on provided Routing Entry ID
	 * @param routingTableDataId
	 * @return MCCMNCGroup Data
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public RoutingTableData getRoutingTableData(long routingTableDataId)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
	    MCCMNCRoutingTableDataManager routingTableDataDataManager=getDataDataManager(session);
	    RoutingTableData routingTableDataData = null;
        if (routingTableDataDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataData =routingTableDataDataManager.getRoutingTableData(routingTableDataId);
        	return routingTableDataData;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	
	/**This method is used to update Routing Entry Data 
	 * @param routingTableDataData
	 * @param staffData
	 * @param updateActionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void update(RoutingTableData routingTableDataData, IStaffData staffData,
			String updateActionAlias)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		MCCMNCRoutingTableDataManager routingTableDataDatamanager = getDataDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (routingTableDataDatamanager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataDatamanager.update(routingTableDataData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, updateActionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate MCC-MNC Codes Detail. : "+exp.getMessage(),exp);
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
		
	
	/**This method is used to fetch Routing Entry Gateway Relation Data of the provided Routing Entry ID
	 * @param routingTableDataID
	 * @return List of RoutingTableGatewayRelData
	 * @throws DataManagerException
	 */
	public List<RoutingTableGatewayRelData> getRoutingGateWayRelList(Long routingTableDataID) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		MCCMNCRoutingTableDataManager routingTableDataDataManager=getDataDataManager(session);
		List<RoutingTableGatewayRelData> routingTableGatewayRelDataList = null;
		if (routingTableDataDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			routingTableGatewayRelDataList =routingTableDataDataManager.getRoutingTableGatewayRelList(routingTableDataID);
			return routingTableGatewayRelDataList;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
        	session.close();
        }
	}
		

	public void changeRoutingEntryOrder(String[] routingEntryIds,IStaffData staffData,
			String changeOrderActionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		MCCMNCRoutingTableDataManager routingTableDataDataManager=getDataDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if (routingTableDataDataManager == null||systemAuditDataManager==null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			routingTableDataDataManager.changeRoutingEntryOrder(routingEntryIds);
			systemAuditDataManager.updateTbltSystemAudit(staffData, changeOrderActionAlias);
			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
        	session.close();
        }
	}
	
	
	public RoutingTableData getRoutingTableDataByMCCMNCGroup(long mccmncGroupId)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
	    MCCMNCRoutingTableDataManager routingTableDataDataManager=getDataDataManager(session);
	    RoutingTableData routingTableDataData = null;
        if (routingTableDataDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	routingTableDataData =routingTableDataDataManager.getRoutingTableByMCCMNCGroup(mccmncGroupId);
        	return routingTableDataData;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	
	
	
       
		private MCCMNCRoutingTableDataManager getDataDataManager(IDataManagerSession session) {
			MCCMNCRoutingTableDataManager routingTableDataDatamanager = (MCCMNCRoutingTableDataManager)DataManagerFactory.getInstance().getDataManager(MCCMNCRoutingTableDataManager.class, session);
            return routingTableDataDatamanager;
    	}	
}