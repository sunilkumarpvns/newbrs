package com.elitecore.netvertexsm.blmanager.RoutingTable.mccmncgroup;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.MCCMNCGroupDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class MCCMNCGroupBLManager extends BaseBLManager{

	
	
	
	/**This method id used to search page list of  all MCCMNCGroupData
	 * or the searched MCCMNC Group on the basis of passed Group Name
	 * @param mccmncGroupData
	 * @param requiredPageNo
	 * @param pageSize
	 * @param staffData
	 * @param actionAlias
	 * @return
	 * @throws DataManagerException
	 */
	public PageList search(MCCMNCGroupData mccmncGroupData, int requiredPageNo,
			Integer pageSize, IStaffData staffData, String actionAlias) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        MCCMNCGroupDataManager mccmncGroupDataManager = getmccmncGroupDataManager(session);
       
        
        PageList mccmncGroupDataList;
        
        if(mccmncGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	mccmncGroupDataList = mccmncGroupDataManager.search(mccmncGroupData, requiredPageNo, pageSize);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }
        return mccmncGroupDataList;
   }

	/**This method is used to create MCCMNC Group Data
	 * @param mccmncGroupData
	 * @param staffData
	 * @param actionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void create(MCCMNCGroupData mccmncGroupData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCGroupDataManager mccmncGroupDatamanager = getmccmncGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (mccmncGroupDatamanager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	mccmncGroupDatamanager.create(mccmncGroupData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
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
	
	/**This method is used to MCCMNCGroupData List
	 * @return List of MCCMNCGroupData
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public List<MCCMNCGroupData> getMCCMNCGroupDataList() throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCGroupDataManager mccmncGroupDataManager = getmccmncGroupDataManager(session);
        List<MCCMNCGroupData> mccmncGroupDataList = null;
        if (mccmncGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	mccmncGroupDataList = mccmncGroupDataManager.getmccmncGroupDataList();
        	return mccmncGroupDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	
	}

	/**This method is used to delete MCCMNCGroup Data 
	 * @param mccmncGroupIDS(selected from search page)
	 * @param staffData
	 * @param deleteActionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void delete(Long[] mccmncGroupIDS, IStaffData staffData,String deleteActionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        MCCMNCGroupDataManager mccmncGroupDataManager=getmccmncGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (mccmncGroupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	mccmncGroupDataManager.delete(mccmncGroupIDS);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, deleteActionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}

	/**This method is used to search MCCMNCGroup Data on provided MCCMNCGroup ID
	 * @param mccmncGroupId
	 * @return MCCMNCGroup Data
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public MCCMNCGroupData getMCCMNCGroupData(long mccmncGroupId)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
	    MCCMNCGroupDataManager mccmncGroupDataManager=getmccmncGroupDataManager(session);
        MCCMNCGroupData mccmncGroupData = null;
        if (mccmncGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	mccmncGroupData =mccmncGroupDataManager.getMCCMNCGroupData(mccmncGroupId);
        	return mccmncGroupData;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}

	/**This method is used to search MCCMNCGroup Data on provided MCCMNCGroup ID
	 * @param mccmncGroupId
	 * @return MCCMNCGroup Data
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public MCCMNCGroupData getMCCMNCGroupData(long mccmncGroupId,IStaffData staffData,String actionAlias)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
	    MCCMNCGroupDataManager mccmncGroupDataManager=getmccmncGroupDataManager(session);
	   
	    MCCMNCGroupData mccmncGroupData = null;
        if (mccmncGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
               	
        	mccmncGroupData =mccmncGroupDataManager.getMCCMNCGroupData(mccmncGroupId);
        	return mccmncGroupData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	
	
	/**This method is used to update MCCMNCGroup Data 
	 * @param mccmncGroupData
	 * @param staffData
	 * @param updateActionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void update(MCCMNCGroupData mccmncGroupData, IStaffData staffData,
			String updateActionAlias)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		MCCMNCGroupDataManager mccmncGroupDatamanager = getmccmncGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (mccmncGroupDatamanager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	mccmncGroupDatamanager.update(mccmncGroupData);
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
		
	
	/**This method is used to fetch MCMNCCode Group Relation Data of the passes MCCMNCGruop ID
	 * @param mccmncGroupID
	 * @return List of MCCMNCCodeGroupRelData
	 * @throws DataManagerException
	 */
	public List<MCCMNCCodeGroupRelData> getMCCMNCCodeGroupRelDataList(Long mccmncGroupID) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		MCCMNCGroupDataManager mccmncGroupDataManager=getmccmncGroupDataManager(session);
		List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = null;
		if (mccmncGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			mccmncCodeGroupRelDataList =mccmncGroupDataManager.getMCCMNCodeGroupRelDataList(mccmncGroupID);
			return mccmncCodeGroupRelDataList;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
        	session.close();
        }
	}
		
    public MCCMNCGroupData getMCCMNCGroupByNetwork(MCCMNCCodeGroupRelData mccmncCodeGroupRelData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
	    MCCMNCGroupDataManager mccmncGroupDataManager=getmccmncGroupDataManager(session);
        MCCMNCGroupData mccmncGroupData = null;
        if (mccmncGroupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	mccmncGroupData =mccmncGroupDataManager.getMCCMNCGroupByNetwork(mccmncCodeGroupRelData);
        	return mccmncGroupData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
    	
    }
	
	
       
		private MCCMNCGroupDataManager getmccmncGroupDataManager(IDataManagerSession session) {
    		MCCMNCGroupDataManager mccmncGroupDatamanager = (MCCMNCGroupDataManager)DataManagerFactory.getInstance().getDataManager(MCCMNCGroupDataManager.class, session);
            return mccmncGroupDatamanager;
    	}	
	}