package com.elitecore.elitesm.blmanager.servicepolicy.rm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.CGPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class CGPolicyBLManager extends BaseBLManager {
	
	public CGPolicyDataManager getCGPolicyDataManager(IDataManagerSession session){
		CGPolicyDataManager cgPolicyDataManager = (CGPolicyDataManager)DataManagerFactory.getInstance().getDataManager(CGPolicyDataManager.class, session);
		return cgPolicyDataManager;
	}

	public void create(CGPolicyData cgPolicyData, IStaffData staffData) throws DataManagerException {
		List<CGPolicyData> policies = new ArrayList<CGPolicyData>();
		policies.add(cgPolicyData);
		create(policies, staffData, "false");
	}

	public Map<String, List<Status>> create(List<CGPolicyData> policyData, IStaffData staffData, String partialSuccess) throws DataManagerException {

		DriverBLManager blManager = new DriverBLManager();

		for (CGPolicyData data : policyData) {
			for(CGPolicyDriverRelationData driver : data.getDriverList()){
				String driverName = blManager.getDriverNameById(driver.getDriverInstanceId());
				driver.setDriverName(driverName);
			}
		}
		return insertRecords(CGPolicyDataManager.class, policyData, staffData, ConfigConstant.CREATE_CG_POLICY, partialSuccess);
	}
	
	public void delete(String[] policyIds, IStaffData staffData) throws DataManagerException {
		delete(policyIds, staffData, BY_ID);
	}
	
	public void deleteByName(String[] policiesName, IStaffData staffData) throws DataManagerException {
		delete(policiesName, staffData, BY_NAME);
	}

	private void delete(String[] policiesToBeDelete, IStaffData staffData, boolean deleteByIDorName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);

		if(cgPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			
			for (String policyToDelete : policiesToBeDelete) {
				if (Strings.isNullOrBlank(policyToDelete) == false) {
					String policy = policyToDelete.trim();
					String deletedPolicy;
					if (deleteByIDorName) {
						deletedPolicy = cgPolicyDataManager.deleteById(policy);
					} else {
						deletedPolicy = cgPolicyDataManager.deleteByName(policy);
					}
					staffData.setAuditName(deletedPolicy);
					AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CREDIT_CONTROL_SERVICE_POLICY);
				}
			}
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	public PageList searchCGPolicy(CGPolicyData cgPolicyData ,int pageNo, Integer pageSize) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);
		if(cgPolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				     
			PageList pageList = cgPolicyDataManager.searchCGPolicy(cgPolicyData, pageNo, pageSize);
			return pageList;
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void updateCGPolicyStatus(List<String> cgPolicyIds,String status) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);
		if(cgPolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
			session.beginTransaction();
			cgPolicyDataManager.updateStatus(cgPolicyIds, status);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void updateById(CGPolicyData policyData,IStaffData staffData) throws DataManagerException {
		update(policyData, staffData, null);
	}

	public void update(CGPolicyData policyData,IStaffData staffData, String policyToUpdate) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         

		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);
		if (cgPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			if (policyToUpdate == null) {
				DriverBLManager blManager = new DriverBLManager();
				
				for(CGPolicyDriverRelationData driver : policyData.getDriverList()){
					String driverName = blManager.getDriverNameById(driver.getDriverInstanceId());
					driver.setDriverName(driverName);
				}
				
				cgPolicyDataManager.updateById(policyData, staffData, policyData.getPolicyId());
			} else {
				cgPolicyDataManager.updateByName(policyData, staffData, policyToUpdate);
			}
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
		
	}

	public CGPolicyData getPolicyDataByPolicyId(String cgPolicyId) throws DataManagerException {
		return getCGPolicy(cgPolicyId, BY_ID);
	}
	
	public CGPolicyData getPolicyDataByPolicyName(String policyName) throws DataManagerException {
		CGPolicyData cgPolicy = getCGPolicy(policyName, BY_NAME);
		convertPolicyData(cgPolicy);
		return cgPolicy;
	}
	
	private void convertPolicyData(CGPolicyData cgPolicy) {
		List<CGPolicyDriverRelationData> primaryDrivers = cgPolicy.getDriverList();
		DriverBLManager blManager = new DriverBLManager();
		for (CGPolicyDriverRelationData primaryDriver : primaryDrivers) {
			String driverId = primaryDriver.getDriverInstanceId();
			try {
				String driverName = blManager.getDriverNameById(driverId);
				primaryDriver.setDriverName(driverName);
			} catch (DataManagerException e) {
				e.printStackTrace();
			}
		}
	}

	private CGPolicyData getCGPolicy(String policyToGet, boolean getByNameOrId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);

		if(cgPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			if (getByNameOrId) {
				return cgPolicyDataManager.getCGPolicyDataById((String)policyToGet);
			} else {
				return cgPolicyDataManager.getCGPolicyDataByName((String)policyToGet);
			}
		} catch(DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void changePolicyOrder(String[] order,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);

		if(cgPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			cgPolicyDataManager.changePolicyOrder(order);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public List<CGPolicyData> getCGPolicies() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CGPolicyDataManager cgPolicyDataManager = getCGPolicyDataManager(session);

		if(cgPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			List<CGPolicyData> policyList = cgPolicyDataManager.getCGPolicies();
			commit(session);
			return policyList;
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
}
