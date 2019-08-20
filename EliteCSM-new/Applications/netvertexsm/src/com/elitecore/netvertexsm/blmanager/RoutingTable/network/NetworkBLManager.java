package com.elitecore.netvertexsm.blmanager.RoutingTable.network;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.NetworkDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;


public class NetworkBLManager extends BaseBLManager{
	
	private NetworkDataManager getNetworkDataManager(IDataManagerSession session) {
		NetworkDataManager networkDataManager = (NetworkDataManager)DataManagerFactory.getInstance().getDataManager(NetworkDataManager.class, session);
        return networkDataManager;
	}
	
	public void create(NetworkData networkData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	networkDataManager.create(networkData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Network Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }

	
	public void update(NetworkData networkData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if(networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	networkDataManager.update(networkData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Network Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }
	
	public void delete(Long[] networkIDs,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	networkDataManager.delete(networkIDs);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
        
    }
	public PageList search(NetworkData networkData,int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
       
        PageList pageList = null;
        if(networkDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	       	
        	pageList = networkDataManager.search(networkData,pageNo,pageSize);
        	return pageList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }       
	}
	public NetworkData getNetworkDetailData(Long mccMNCCodesId,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
       
        NetworkData networkData = null;
        if(networkDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	       	
        	networkData = networkDataManager.getNetworkDetailData(mccMNCCodesId);
        	return networkData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	public List<CountryData> getCountryDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        List<CountryData> countryDataList = null;
        if(networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	countryDataList = networkDataManager.getCountryDataList();
        	session.commit();
        	return countryDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	public List<OperatorData> getOperatorDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        List<OperatorData> operatorDataList = null;
        if(networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	operatorDataList = networkDataManager.getOperatorDataList();
        	session.commit();
        	return operatorDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	public List<BrandData> getBrandDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        List<BrandData> brandDataList = null;
        if(networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	brandDataList = networkDataManager.getBrandDataList();
        	session.commit();
        	return brandDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	public List<BrandOperatorRelData> getBrandOperatorDataRelList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager networkDataManager = getNetworkDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        List<BrandOperatorRelData> operatorDataList = null;
        if(networkDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	operatorDataList = networkDataManager.getBrandOperatorDataRelList();
        	session.commit();
        	return operatorDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }
	}
	public NetworkData getNetworkDetailData(Long networkID) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        NetworkDataManager mccmncMgmtDatamanager = getNetworkDataManager(session);
        NetworkData mccmncCodesData = null;
        if (mccmncMgmtDatamanager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	mccmncCodesData = mccmncMgmtDatamanager.getNetworkDetailData(networkID);
        	return mccmncCodesData;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	
	
	public List<NetworkData> getNetworkDataList(String sqlQuery) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		NetworkDataManager networkDataManager = getNetworkDataManager(session);
		List<NetworkData> mccmncCodesData = null;
		if (networkDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();        	
			mccmncCodesData = networkDataManager.getNetworkDataList(sqlQuery);
			session.close();
			return mccmncCodesData;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
        	session.close();
        }

	}
	public List<NetworkData> getNetworkDataList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		NetworkDataManager networkDataManager = getNetworkDataManager(session);
		List<NetworkData> networkData = null;
		if (networkDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();        	
			networkData = networkDataManager.getNetworkDataList();
			return networkData;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
        	session.close();
        }

	}
	
}