package com.elitecore.netvertexsm.blmanager.locationconfig.region;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.RegionDataManager;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class RegionBLManager extends BaseBLManager{

	
	private RegionDataManager getRegionDataManager(IDataManagerSession session) {
		RegionDataManager regionDataManager = (RegionDataManager)DataManagerFactory.getInstance().getDataManager(RegionDataManager.class, session);
        return regionDataManager;
	}
	
	public void createRegionByList(List<RegionData> regionDataList,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager = getRegionDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (regionDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	regionDataManager.createRegionByList(regionDataList);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Location Master Instance. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }
	
	
	
	public void create(RegionData regionData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager = getRegionDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (regionDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	regionDataManager.create(regionData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Location Master Instance. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }

	public PageList search(RegionData regionData,int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager = getRegionDataManager(session);
       
        PageList pageList = null;
        if(regionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	pageList = regionDataManager.search(regionData, pageNo, pageSize);
        	return pageList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }       
	}
	
	public void delete(Long[] regionIds, IStaffData staffData,String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager=getRegionDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (regionDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	regionDataManager.delete(regionIds);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	
	
	public List<RegionData> getRegionDataList() throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager=getRegionDataManager(session);
        List<RegionData> regionDataList = null;
        if (regionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	regionDataList = regionDataManager.getRegionDataList();
        	return regionDataList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }

	
	}
	public RegionData getRegionData(long regionId) throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager=getRegionDataManager(session);
        RegionData region=null;
        if (regionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	region = regionDataManager.getRegionData(regionId);
        	return region;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }

	
	}

	public void update(RegionData regionData, IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        RegionDataManager regionDataManager = getRegionDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (regionDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	regionDataManager.update(regionData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Location Master Instance. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
		
	}
	
}