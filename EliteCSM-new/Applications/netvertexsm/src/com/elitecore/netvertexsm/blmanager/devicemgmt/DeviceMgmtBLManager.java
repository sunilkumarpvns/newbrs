package com.elitecore.netvertexsm.blmanager.devicemgmt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.core.util.csv.CSVData;
import com.elitecore.core.util.csv.EliteCSVUtility;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.MessageData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.devicemgmt.DeviceMgmtDataManager;
import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class DeviceMgmtBLManager extends BaseBLManager{
	
	private static DeviceMgmtBLManager deviceManagementBLMaanger;
	private DeviceMgmtDataManager getDeviceMgmtDataManager(IDataManagerSession session) {
		DeviceMgmtDataManager deviceMgmtDataManager = (DeviceMgmtDataManager)DataManagerFactory.getInstance().getDataManager(DeviceMgmtDataManager.class, session);
        return deviceMgmtDataManager;
	}
	
	public static final DeviceMgmtBLManager getInstance(){
        if (deviceManagementBLMaanger == null) {
            synchronized (DeviceMgmtBLManager.class) {
                if (deviceManagementBLMaanger == null){
                	deviceManagementBLMaanger = new DeviceMgmtBLManager();
                }
            }
        }
        
        return deviceManagementBLMaanger;
    }
	
	public void create(TACDetailData tacDetailData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (deviceMgmtDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	deviceMgmtDataManager.create(tacDetailData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate TAC Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
    }

	
	public void update(TACDetailData tacDetailData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (deviceMgmtDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	deviceMgmtDataManager.update(tacDetailData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate TAC Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
    }
	
	public void delete(Long[] tacDetailIds,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (deviceMgmtDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	deviceMgmtDataManager.delete(tacDetailIds);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
        
    }
	public PageList search(TACDetailData tacDetailData,int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        
        PageList pageList = null;
        if (deviceMgmtDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	pageList = deviceMgmtDataManager.search(tacDetailData,pageNo,pageSize);
        	return pageList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
       
	}
	public TACDetailData getTACDetailData(Long tacDetailId,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        TACDetailData tacDetailData = null;
        if (deviceMgmtDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	tacDetailData = deviceMgmtDataManager.getTACDetailData(tacDetailId);
        	return tacDetailData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	public TACDetailData getTACDetailData(Long tacDetailId) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
        TACDetailData tacDetailData = null;
        if (deviceMgmtDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	tacDetailData = deviceMgmtDataManager.getTACDetailData(tacDetailId);
        	return tacDetailData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	
	public  List<MessageData> uploadCSV(FormFile csvfile,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion, IOException, SQLException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<MessageData> messages;
		try{
		CSVData csvData = EliteCSVUtility.parseCSVExt(csvfile.getInputStream(), ",");
		session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
		
		messages= deviceMgmtDataManager.uploadCSV(csvData);
		session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
    	SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
    	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
    	session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e);
		}finally{
				session.close();
			}
		return messages;
	}
	
	public List<TACDetailData> getTACDetailData() throws DataManagerException,
			DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
		if (deviceMgmtDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			return deviceMgmtDataManager.getTACDetails();
			
		} catch (DataManagerException exp) {
			throw new DataManagerException("Action failed : " + exp.getMessage());
		} finally {
			session.close();
		}
	}

	public List<TACDetailData> getTacDetails(Long[] tacDetailIds)
			throws DataManagerException, DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DeviceMgmtDataManager deviceMgmtDataManager = getDeviceMgmtDataManager(session);
		List<TACDetailData> tacDetailDatas = null;
		if (deviceMgmtDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			session.beginTransaction();
			tacDetailDatas = deviceMgmtDataManager.getTacDetails(tacDetailIds);
			session.commit();
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage());
		} finally {
			session.close();
		}
		return tacDetailDatas;

	}

}
