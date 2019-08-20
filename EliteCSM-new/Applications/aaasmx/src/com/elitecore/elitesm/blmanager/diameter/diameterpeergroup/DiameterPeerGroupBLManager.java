/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.diameterpeergroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.DiameterPeerGroupDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;

/**
 * @author nayana.rathod
 *
 */
public class DiameterPeerGroupBLManager extends BaseBLManager {
	
	private static final String DATA_MANAGER_EXCEPTION_MESSAGE = "Data Manager implementation not found for ";
	private static final String MODULE = DiameterPeerGroupBLManager.class.getSimpleName();

	public DiameterPeerGroupDataManager getDiameterPeerGroupDataManager(IDataManagerSession session) { 
		return (DiameterPeerGroupDataManager) DataManagerFactory.getInstance().getDataManager(DiameterPeerGroupDataManager.class, session);
	}

	
	/**
	 * @param diameterPeerGroupIdOrNames
	 * @param staffData
	 * @param isIdOrName
	 * @throws DataManagerException
	 */
	private void deleteDiameterPeerGroup(List<String> diameterPeerGroupIdOrNames, IStaffData staffData,boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);
		
		String diameterPeerGroupName = null;
		
		if (diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}
		
		try {
			
			session.beginTransaction();
			
			if (Collectionz.isNullOrEmpty(diameterPeerGroupIdOrNames) == false) {
				int size = diameterPeerGroupIdOrNames.size();
				for (int i = 0; i < size; i++) {
					if (Strings.isNullOrBlank(diameterPeerGroupIdOrNames.get(i)) == false) {
						String diameterPeerGroupIdOrName = diameterPeerGroupIdOrNames.get(i).trim();
						if (isIdOrName) {
							diameterPeerGroupName = diameterPeerGroupDataManager.deleteById(diameterPeerGroupIdOrName);
						} else {
							diameterPeerGroupName = diameterPeerGroupDataManager.deleteByName(diameterPeerGroupIdOrName);
						}
						staffData.setAuditName(diameterPeerGroupName);
						AuditUtility.doAuditing(session,staffData,ConfigConstant.DELETE_DIAMETER_PEER_GROUP);
					}
				}
				commit(session);
			}
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void deleteDiameterPeerGroupById(List<String> diameterPeerGroupIds, IStaffData staffData) throws DataManagerException {
		deleteDiameterPeerGroup(diameterPeerGroupIds, staffData, BY_ID);
	}
	
	public void deleteDiameterPeerGroupByName(List<String> diameterPeerGroupNames, IStaffData staffData) throws DataManagerException {
		deleteDiameterPeerGroup(diameterPeerGroupNames, staffData, BY_NAME);
	}


	/**
	 * @param diameterPeerGroupData
	 * @param requiredPageNo
	 * @param pageSize
	 * @param staffData 
	 * @return
	 * @throws DataManagerException
	 */
	public PageList searchDiameterPeerGroupData(DiameterPeerGroup diameterPeerGroupData, int requiredPageNo, Integer pageSize, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);

		if (diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}

		try{	
			session.beginTransaction();
			
			PageList pluginList = diameterPeerGroupDataManager.searchDiameterPeerGroupData(diameterPeerGroupData,requiredPageNo,pageSize);	
			AuditUtility.doAuditing(session,staffData, ConfigConstant.SEARCH_DIAMETER_PEER_GROUP);

			commit(session);
			
			return pluginList;
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception de) {
			rollbackSession(session);
			Logger.logTrace(MODULE, de);
			throw new DataManagerException(de.getMessage(), de);
		} finally {
			closeSession(session);
		}
	}
	
	
	public void createDiameterPeerGroup(DiameterPeerGroup diameterPeerGroupData, IStaffData staffData) throws DataManagerException {
		List<DiameterPeerGroup> diameterPeerGroupDataList = new ArrayList<DiameterPeerGroup>();
		diameterPeerGroupDataList.add(diameterPeerGroupData);
		createDiameterPeerGroup(diameterPeerGroupDataList, staffData, "");
	}
	
	public Map<String, List<Status>> createDiameterPeerGroup(List<DiameterPeerGroup> diameterPeerGroupDatas, IStaffData staffData, String partialSuccess) throws DataManagerException {

		if (Collectionz.isNullOrEmpty(diameterPeerGroupDatas) == false) {

			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();

			for (DiameterPeerGroup diameterPeerGroupData : diameterPeerGroupDatas) {

				for(DiameterPeerRelationWithPeerGroup peerDetail: diameterPeerGroupData.getPeerList()) {
					peerDetail.setPeerId(diameterPeerBLManager.getDiameterPeerIdByName(peerDetail.getPeerName()));
				}
				diameterPeerGroupData.setPeerList(diameterPeerGroupData.getPeerList());
			}
		}
		return insertRecords(DiameterPeerGroupDataManager.class, diameterPeerGroupDatas, staffData, ConfigConstant.CREATE_DIAMETER_PEER_GROUP, partialSuccess);
	}


	/**
	 * @param diameterPeerGroupIdOrName
	 * @param isIdOrName
	 * @return
	 * @throws DataManagerException
	 */
	private DiameterPeerGroup getDiameterPeerGroupData(Object diameterPeerGroupIdOrName, boolean isIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);
		
		if(diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}
		
		try{
			DiameterPeerGroup diameterPeerGroup;
			
			if (isIdOrName) {
				diameterPeerGroup = diameterPeerGroupDataManager.getDiameterPeerGroupDataById((String)diameterPeerGroupIdOrName);
			} else {
				diameterPeerGroup = diameterPeerGroupDataManager.getDiameterPeerGroupDataByName((String)diameterPeerGroupIdOrName);
			}
			
			return diameterPeerGroup;
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public DiameterPeerGroup getDiameterPeerGroupByName(String diameterPeerGroupName) throws DataManagerException {
		return getDiameterPeerGroupData(diameterPeerGroupName.trim(), BY_NAME);
	}
	
	public DiameterPeerGroup getDiameterPeerGroupById(String diameterPeerGroupId) throws DataManagerException {
		return getDiameterPeerGroupData(diameterPeerGroupId, BY_ID);
	}
	
	
	public void updateDiameterPeerGroupByName(DiameterPeerGroup diameterPeerGroup, IStaffData staffData, String diameterPeerGroupName ) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);

		if (diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}

		try {

			session.beginTransaction();
			
			if (diameterPeerGroupName == null) {
				diameterPeerGroupDataManager.updateById(diameterPeerGroup, staffData);
			} else {
				diameterPeerGroupDataManager.updateByName(diameterPeerGroup, staffData, diameterPeerGroupName.trim());
			}
			
			commit(session);
			
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	public void updateDiameterPeerGroupById(DiameterPeerGroup diameterPeerGroup, IStaffData staffData) throws DataManagerException {
		updateDiameterPeerGroupByName(diameterPeerGroup, staffData, null);
	}


	public List<DiameterPeerGroup> getDiameterPeerGroupList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);

		List<DiameterPeerGroup> diameterPeerGroupDataList = null ;
		if (diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}

		try{				
		
			diameterPeerGroupDataList = diameterPeerGroupDataManager.getDiameterPeerGroupList();	
			return diameterPeerGroupDataList;
			
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception de) {
			Logger.logTrace(MODULE, de);
			throw new DataManagerException(de.getMessage(), de);
		} finally {
			closeSession(session);
		}
	}
	
	
	public String getDiameterPeerGroupNameFromId(String peerDroupId) throws DataManagerException{
		DiameterPeerGroup diameterPeerGroup = getDiameterPeerGroupById(peerDroupId);
		return diameterPeerGroup.getPeerGroupName();
	}


	public List<DiameterPeerGroup> getDiameterPeerGroupListExceptSelf(String peerGroupId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerGroupDataManager diameterPeerGroupDataManager = getDiameterPeerGroupDataManager(session);

		List<DiameterPeerGroup> diameterPeerGroupDataList = null ;
		if (diameterPeerGroupDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_EXCEPTION_MESSAGE + getClass().getName());
		}

		try{				
		
			diameterPeerGroupDataList = diameterPeerGroupDataManager.getDiameterPeerGroupListExceptSelf(peerGroupId);	
			return diameterPeerGroupDataList;
			
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception de) {
			Logger.logTrace(MODULE, de);
			throw new DataManagerException(de.getMessage(), de);
		} finally {
			closeSession(session);
		}
	}

}
