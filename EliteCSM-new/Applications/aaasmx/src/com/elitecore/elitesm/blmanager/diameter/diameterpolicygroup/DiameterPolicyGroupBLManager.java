package com.elitecore.elitesm.blmanager.diameter.diameterpolicygroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.DiameterPolicyGroupDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterPolicyGroupBLManager extends BaseBLManager {

	private DiameterPolicyGroup getDiameterPolicyGroupData(Object diameterPolicyGroupIdOrName, boolean isIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyGroupDataManager diameterPolicyGroupDataManager = getDiameterPolicyGroupDataManager(session);
		
		if (diameterPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			DiameterPolicyGroup diameterPolicyGroup;
			
			if (isIdOrName) {
				diameterPolicyGroup = diameterPolicyGroupDataManager.getDiameterPolicyGroupDataById((String)diameterPolicyGroupIdOrName);
			} else {
				diameterPolicyGroup = diameterPolicyGroupDataManager.getDiameterPolicyGroupDataByName((String)diameterPolicyGroupIdOrName);
			}
			
			return diameterPolicyGroup;
			
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	public DiameterPolicyGroup getDiameterPolicyGroupDataById(String diameterPolicyGroupId) throws DataManagerException {
		return getDiameterPolicyGroupData(diameterPolicyGroupId, BY_ID);
		
	}
	
	public DiameterPolicyGroup getDiameterPolicyGroupDataByName(String diameterPolicyGroupName) throws DataManagerException {
		return getDiameterPolicyGroupData(diameterPolicyGroupName.trim(), BY_NAME);
		
	}
	
	
	public PageList search(DiameterPolicyGroup diameterPolicyGroup, int pageNo, int pageSize, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyGroupDataManager diameterPolicyGroupDataManager = getDiameterPolicyGroupDataManager(session);
		
		PageList lstDiameterPolicyList;

		if (diameterPolicyGroupDataManager == null ) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();	
			lstDiameterPolicyList = diameterPolicyGroupDataManager.search(diameterPolicyGroup, pageNo, pageSize);
			
			AuditUtility.doAuditing(session,staffData, ConfigConstant.SEARCH_DIAMETER_POLICY_GROUP);
			
			commit(session);
			
			return lstDiameterPolicyList; 
		} catch ( DataManagerException exception ){
			rollbackSession(session);
			throw exception;
		} catch(Exception e){
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	
	private void deleteDiameterPolicyGroup(List<String> diameterPeerGroupIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyGroupDataManager diameterPolicyGroupDataManager = getDiameterPolicyGroupDataManager(session);
		
		if (diameterPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			session.beginTransaction();
			
			String diameterPolicyGroupName;
			
			if (Collectionz.isNullOrEmpty(diameterPeerGroupIdOrNames) == false) {
				int size = diameterPeerGroupIdOrNames.size();
				for (int i = 0; i < size; i++) {
					if (Strings.isNullOrBlank(diameterPeerGroupIdOrNames.get(i)) == false) {
						String diameterPeerGroupIdOrName = diameterPeerGroupIdOrNames.get(i).trim();
						if (isIdOrName) {
							diameterPolicyGroupName = diameterPolicyGroupDataManager.deleteDiameterPolicyGroupById(diameterPeerGroupIdOrName);
						} else {
							diameterPolicyGroupName = diameterPolicyGroupDataManager.deleteDiameterPolicyGroupByName(diameterPeerGroupIdOrName);
						}
						staffData.setAuditName(diameterPolicyGroupName);
						AuditUtility.doAuditing(session,staffData,ConfigConstant.DELETE_DIAMETER_POLICY_GROUP);
					}
				}
				commit(session);
			}
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}	
	
	public void deleteDiameterPolicyGroupById(List<String> diameterPeerGroupIds, IStaffData staffData) throws DataManagerException {
		deleteDiameterPolicyGroup(diameterPeerGroupIds, staffData, BY_ID);
	}
	
	public void deleteDiameterPolicyGroupByName(List<String> diameterPeerGroupNames, IStaffData staffData) throws DataManagerException {
		deleteDiameterPolicyGroup(diameterPeerGroupNames, staffData, BY_NAME);
	}	
	
	
	public void createDiameterPolicyGroup(DiameterPolicyGroup diameterPolicyGroupData, IStaffData staffData) throws DataManagerException {
		List<DiameterPolicyGroup> diameterPolicyGroupDatas = new ArrayList<DiameterPolicyGroup>();
		diameterPolicyGroupDatas.add(diameterPolicyGroupData);
		createDiameterPolicyGroup(diameterPolicyGroupDatas, staffData, "");
	}
	
	public Map<String, List<Status>> createDiameterPolicyGroup(List<DiameterPolicyGroup> diameterPolicyGroupDatas, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DiameterPolicyGroupDataManager.class, diameterPolicyGroupDatas, staffData, ConfigConstant.CREATE_DIAMETER_POLICY_GROUP, partialSuccess);
	}
	
	
	public DiameterPolicyGroupDataManager getDiameterPolicyGroupDataManager(IDataManagerSession session) {
		DiameterPolicyGroupDataManager diameterPolicyDataManager = (DiameterPolicyGroupDataManager)DataManagerFactory.getInstance().getDataManager(DiameterPolicyGroupDataManager.class,session);
		return diameterPolicyDataManager; 
	}
	
	
	public void updateDiameterPolicyGroupByName(DiameterPolicyGroup diameterPolicyGroupData, IStaffData staffData, String diameterPolicyGroupName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyGroupDataManager diameterPolicyGroupDataManager = getDiameterPolicyGroupDataManager(session);

		if (diameterPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			
			if (diameterPolicyGroupName == null) {
				diameterPolicyGroupDataManager.updateDiameterPolicyGroupById(diameterPolicyGroupData, staffData, diameterPolicyGroupData.getPolicyId());
			} else {
				diameterPolicyGroupDataManager.updateDiameterPolicyGroupByName(diameterPolicyGroupData, staffData, diameterPolicyGroupName.trim());
			}

			commit(session);
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception exp) {
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}

	}
	
	public void updateDiameterPolicyGroupById(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData) throws DataManagerException {
		updateDiameterPolicyGroupByName(diameterPolicyGroup, staffData, null);		
	}
	
	
	public void verifyDiameterPolicyName(String policyId, String policyName)throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyGroupDataManager diameterPolicyGroupDataManager = getDiameterPolicyGroupDataManager(session);
		boolean isPolicyName;

		if (diameterPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		isPolicyName = diameterPolicyGroupDataManager.verifyDiameterPolicyGroupName(policyId, policyName);
		if(isPolicyName){
			throw new DuplicateRadiusPolicyNameException("Duplicate Radius Policy Name Exception");
		}
		closeSession(session);
	}

	
}
