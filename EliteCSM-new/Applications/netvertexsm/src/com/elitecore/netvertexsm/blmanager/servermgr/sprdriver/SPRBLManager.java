package com.elitecore.netvertexsm.blmanager.servermgr.sprdriver;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.SPRDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class SPRBLManager extends BaseBLManager {

	private SPRDataManager getSPRDataManager(IDataManagerSession session) {
		SPRDataManager sprDriverDataManager = (SPRDataManager) DataManagerFactory.getInstance().getDataManager(SPRDataManager.class, session);
		return sprDriverDataManager; 
	}

	public void create(SPRData sprData, IStaffData staffData, String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPRDataManager sprDataManager = getSPRDataManager(session);

		checkSPRDataManager(sprDataManager);

		try{
			session.beginTransaction();
			sprDataManager.create(sprData);
			session.commit();
			
		}catch(DuplicateParameterFoundExcpetion dpe){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
		    session.close();
		}
	}
	public void update(SPRData sprData, IStaffData staffData, String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPRDataManager sprDataManager = getSPRDataManager(session);

		checkSPRDataManager(sprDataManager);

		try{
			session.beginTransaction();
			sprDataManager.update(sprData);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
		    session.close();
		}			
	}


	public PageList search(SPRData sprData, int pageNo, int pageSize) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPRDataManager sprDataManager = getSPRDataManager(session);
		PageList sprList = null;		

		checkSPRDataManager(sprDataManager);

		try{
			session.beginTransaction();
			sprList = sprDataManager.search(sprData, pageNo, pageSize);		
			return sprList;
		}catch(Exception e){
			session.rollback();
			e.printStackTrace();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
		    session.close();
		}

	}

	public List<SPRData> getSPRDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPRDataManager sprDataManager = getSPRDataManager(session);	
		List<SPRData> sprList = null;

		checkSPRDataManager(sprDataManager);

		try{        	
			session.beginTransaction();	        
			sprList = sprDataManager.getSPRDataList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
		    session.close();
		}		
		return sprList;
	}


	public SPRData getSPRData(String sprId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPRDataManager sprDataManager = getSPRDataManager(session);	
		SPRData sprData = null;

		checkSPRDataManager(sprDataManager);

		try{        	
			session.beginTransaction();	        
			sprData = sprDataManager.getSPRData(sprId);                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
		    session.close();
		}		
		return sprData;
	}

	public void delete(String[] sprIdList,IStaffData staffData, String actionAlias ) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SPRDataManager sprDataManager = getSPRDataManager(session);

		checkSPRDataManager(sprDataManager);

		try{
			session.beginTransaction();
			sprDataManager.delete(sprIdList);
			session.commit();		
		}catch(ConstraintViolationException de){
			session.rollback();
			throw new ConstraintViolationException(de.getMessage(),de.getSQLException(),de.getConstraintName());
		}catch(Exception de){
			session.rollback();
			throw new DataManagerException("Action Failed .",de.getMessage());
		}finally{
		    session.close();
		}
	} 

	private void checkSPRDataManager(SPRDataManager sprDataManager) {
		Preconditions.checkNotNull(sprDataManager,"Data Manager implementation not found for " + getClass().getName());
	}
}
