/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile;

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
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.DiameterPeerProfileDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterPeerProfileBLManager extends BaseBLManager {

	/**
	 * @return Returns Data Manager instance for Diameterpolicy data.
	 */
	public DiameterPeerProfileDataManager getDiameterPeerProfileDataManager(IDataManagerSession session) { 
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = (DiameterPeerProfileDataManager) DataManagerFactory.getInstance().getDataManager(DiameterPeerProfileDataManager.class, session);
		return diameterPeerProfileDataManager;
	}
	
	public List<ServerCertificateData> getListOfServerCertificate() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerProfileDataManager diameterPeerDataManager = getDiameterPeerProfileDataManager(session);
		
		List<ServerCertificateData> lstServerCertificateData;

		if (diameterPeerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			
			lstServerCertificateData = diameterPeerDataManager.getServerCertificateDataList();
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}

		return lstServerCertificateData; 
	}
    
	public String getPeerProfileIdByPeerProfileName(String peerProfileName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = getDiameterPeerProfileDataManager(session);
		
		if(diameterPeerProfileDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			String peerProfileId = diameterPeerProfileDataManager.getPeerProfileIdByPeerProfileName(peerProfileName.trim());
			
			return peerProfileId;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
	public PageList searchDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfile, Map infoMap, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = getDiameterPeerProfileDataManager(session);

		PageList lstDiameterPeerProfileList;

		if (diameterPeerProfileDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			lstDiameterPeerProfileList = diameterPeerProfileDataManager.searchDiameterPeerProfile(diameterPeerProfile, infoMap);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_PEER_PROFILE);
			
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

		return lstDiameterPeerProfileList;
		
	}
	

	public DiameterPeerProfileData getDiameterPeerProfileById(String diameterPeerProfileId) throws DataManagerException {
		
		return getDiameterPeerProfile(diameterPeerProfileId, BY_ID);
		
	}
	
	public DiameterPeerProfileData getDiameterPeerProfileByName(String diameterPeerProfileName) throws DataManagerException {
		
		return getDiameterPeerProfile(diameterPeerProfileName.trim(), BY_NAME);
		
	}

	private DiameterPeerProfileData getDiameterPeerProfile(Object diameterPeerProfileIdOrName, boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = getDiameterPeerProfileDataManager(session);

		if (diameterPeerProfileDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			DiameterPeerProfileData diameterPeerProfile;
			
			if (isIdOrName) {
				diameterPeerProfile = diameterPeerProfileDataManager.getDiameterPeerProfileById((String) diameterPeerProfileIdOrName);
			} else {
				diameterPeerProfile = diameterPeerProfileDataManager.getDiameterPeerProfileByName((String) diameterPeerProfileIdOrName);
			}
			
			return diameterPeerProfile;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData) throws DataManagerException {

		List<DiameterPeerProfileData> diameterPeerProfiles = new ArrayList<DiameterPeerProfileData>();
		diameterPeerProfiles.add(diameterPeerProfile);
		createDiameterPeerProfile(diameterPeerProfiles, staffData, "");
		
	}
	
	public Map<String, List<Status>> createDiameterPeerProfile(List<DiameterPeerProfileData> diameterPeerProfiles, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DiameterPeerProfileDataManager.class, diameterPeerProfiles, staffData, ConfigConstant.CREATE_DIAMETER_PEER_PROFILE, partialSuccess);
	}

	
	public void updateDiameterPeerProfileById(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData) throws DataManagerException {

		updateDiameterPeerProfile(diameterPeerProfile, staffData, null);

	}

	public void updateDiameterPeerProfileByName(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileName) throws DataManagerException {
		
		updateDiameterPeerProfile(diameterPeerProfile, staffData, diameterPeerProfileName);
		
	}
	
	private void updateDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = getDiameterPeerProfileDataManager(session);

		if (diameterPeerProfileDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (diameterPeerProfileName == null) {
				diameterPeerProfileDataManager.updateDiameterPeerProfileById(diameterPeerProfile, staffData, diameterPeerProfile.getPeerProfileId());
			} else {
				diameterPeerProfileDataManager.updateDiameterPeerProfileByName(diameterPeerProfile, staffData, diameterPeerProfileName.trim());
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
	
	
	public void deleteDiameterPeerProfileById(List<String> diameterPeerProfileIds, IStaffData staffData) throws DataManagerException {
		
		deleteDiameterPeerProfile(diameterPeerProfileIds, staffData, BY_ID);

	}
	
	public void deleteDiameterPeerProfileByName(List<String> diameterPeerProfileNames, IStaffData staffData) throws DataManagerException {
		
		deleteDiameterPeerProfile(diameterPeerProfileNames, staffData, BY_NAME);
		
	}

	private void deleteDiameterPeerProfile(List<String> diameterPeerProfileIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPeerProfileDataManager diameterPeerProfileDataManager = getDiameterPeerProfileDataManager(session);

		if (diameterPeerProfileDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(diameterPeerProfileIdOrNames) == false) {

				int size = diameterPeerProfileIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(diameterPeerProfileIdOrNames.get(i)) == false) {

						String diameterPeerProfileIdOrName = diameterPeerProfileIdOrNames.get(i).trim();
						
						String diameterPeerProfileName;

						if (isIdOrName) {
							diameterPeerProfileName = diameterPeerProfileDataManager.deleteDiameterPeerProfileById(diameterPeerProfileIdOrName);
						} else {
							diameterPeerProfileName = diameterPeerProfileDataManager.deleteDiameterPeerProfileByName(diameterPeerProfileIdOrName);
						}

						staffData.setAuditName(diameterPeerProfileName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DIAMETER_PEER_PROFILE);

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
