/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.diameterpeer;

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
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.DiameterPeerDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterPeerBLManager extends BaseBLManager {
	
	/**
	 * @return Returns Data Manager instance for Diameterpeer data.
	 */
	public DiameterPeerDataManager getDiameterPeerDataManager(IDataManagerSession session) { 
		DiameterPeerDataManager diameterPeerDataManager = (DiameterPeerDataManager) DataManagerFactory.getInstance().getDataManager(DiameterPeerDataManager.class, session);
		return diameterPeerDataManager;
	}
	
	
	public List<DiameterPeerProfileData> getPeerProfileList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		List<DiameterPeerProfileData> lstDiameterPeerprofileList;

		if (diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			
			lstDiameterPeerprofileList = diameterPeerDataManager.getPeerProfileList();
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}

		return lstDiameterPeerprofileList; 
	}
	
	
	public List<DiameterPeerData> getDiameterPeerList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		List<DiameterPeerData> diameterPeersList =null;
		if (diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			diameterPeersList = diameterPeerDataManager.getDiameterPeerList();
			return diameterPeersList;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	

	public String getDiameterPeerNameById(String peerId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		if (diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		String peerName = null;
		
		try{
			peerName = diameterPeerDataManager.getDiameterPeerNameById(peerId);
			return peerName;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public String getDiameterPeerIdByName(String peerName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		if(diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			String peerId=null;
			
			peerId = diameterPeerDataManager.getDiameterPeerIdByName(peerName.trim());
			
			return peerId;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	
	public PageList search(DiameterPeerData diameterPeerData, Map infoMap, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);

		PageList lstDiameterPeerList;

		if (diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			lstDiameterPeerList = diameterPeerDataManager.search(diameterPeerData, infoMap);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_PEER);
			
			commit(session);

		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}

		return lstDiameterPeerList;
		
	}
	

	public DiameterPeerData getDiameterPeerById(String diameterPeerId) throws DataManagerException {
		
		return getDiameterPeer(diameterPeerId, BY_ID);
		
	}
	
	public DiameterPeerData getDiameterPeerByName(String diameterPeerName) throws DataManagerException {

		return getDiameterPeer(diameterPeerName.trim(), BY_NAME);
		
	}

	private DiameterPeerData getDiameterPeer(Object diameterPeerIdOrName, boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		if(diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			DiameterPeerData diameterPeer;
			
			if (isIdOrName) {
				diameterPeer = diameterPeerDataManager.getDiameterPeerById((String) diameterPeerIdOrName);
			} else {
				diameterPeer = diameterPeerDataManager.getDiameterPeerByName((String) diameterPeerIdOrName);
			}
			
			return diameterPeer;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}


	public void createDiameterPeer(DiameterPeerData diameterPeer, IStaffData staffData) throws DataManagerException {

		List<DiameterPeerData> diameterPeers = new ArrayList<DiameterPeerData>();
		diameterPeers.add(diameterPeer);
		createDiameterPeer(diameterPeers, staffData, "");
		
	}
	
	public Map<String, List<Status>> createDiameterPeer(List<DiameterPeerData> diameterPeers, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DiameterPeerDataManager.class, diameterPeers, staffData, ConfigConstant.CREATE_DIAMETER_PEER, partialSuccess);
	}

	
	public void updateDiameterPeerById(DiameterPeerData diameterPeer, IStaffData staffData) throws DataManagerException {
		
		updateDiameterPeer(diameterPeer, staffData, null);
		
	}

	public void updateDiameterPeerByName(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerName) throws DataManagerException {
		
		updateDiameterPeer(diameterPeer, staffData, diameterPeerName);
		
	}
	
	private void updateDiameterPeer(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		if(diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (diameterPeerName == null) {
				diameterPeerDataManager.updateDiameterPeerById(diameterPeer, staffData, diameterPeer.getPeerUUID());
			} else {
				diameterPeerDataManager.updateDiameterPeerByName(diameterPeer, staffData, diameterPeerName.trim());
			}

			commit(session);
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}


	public void deleteDiameterPeerById(List<String> diameterPeerIds, IStaffData staffData) throws DataManagerException {
		
		deleteDiameterPeer(diameterPeerIds, staffData, BY_ID);

	}
	
	public void deleteDiameterPeerByName(List<String> diameterPeerNames, IStaffData staffData) throws DataManagerException {
		
		deleteDiameterPeer(diameterPeerNames, staffData, BY_NAME);
		
	}

	private void deleteDiameterPeer(List<String> diameterPeerIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerDataManager diameterPeerDataManager = getDiameterPeerDataManager(session);
		
		if(diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(diameterPeerIdOrNames) == false) {

				int size = diameterPeerIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(diameterPeerIdOrNames.get(i)) == false) {

						String diameterPeerIdOrName = diameterPeerIdOrNames.get(i).trim();
						
						String diameterPeerName;

						if (isIdOrName) {
							diameterPeerName = diameterPeerDataManager.deleteDiameterPeerById(diameterPeerIdOrName);
						} else {
							diameterPeerName = diameterPeerDataManager.deleteDiameterPeerByName(diameterPeerIdOrName);
						}

						staffData.setAuditName(diameterPeerName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DIAMETER_PEER);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
}
