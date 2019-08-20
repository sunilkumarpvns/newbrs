package com.elitecore.elitesm.blmanager.servermgr.certificate;

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
import com.elitecore.elitesm.datamanager.servermgr.certificate.TrustedCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class TrustedCertificateBLManager extends BaseBLManager{

	public PageList search(TrustedCertificateData trustedCertificateData, IStaffData staffData) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		TrustedCertificateDataManager trustedCertificateDataManager=getTrustedCertificateDataManager(session);
		
		if(trustedCertificateDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			PageList lstTrustedCertificateList=trustedCertificateDataManager.search(trustedCertificateData);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_TRUSTED_CERTIFICATE);
			commit(session);
			return lstTrustedCertificateList;
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


	public TrustedCertificateData view(String trustedCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);
		
		if(trustedCertificateDataManager==null){
			throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
		}

		try{
			TrustedCertificateData trustedCertificateData = trustedCertificateDataManager.getTrustedCertificateById(trustedCertificateId);
			return trustedCertificateData;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public TrustedCertificateDataManager getTrustedCertificateDataManager(IDataManagerSession session) {
		TrustedCertificateDataManager trustedCertificateDataManager=(TrustedCertificateDataManager)DataManagerFactory.getInstance().getDataManager(TrustedCertificateDataManager.class, session);
		return trustedCertificateDataManager;
	}

	
	public TrustedCertificateData getTrustedCertificateById(String trustedCertificateId) throws DataManagerException {
		
		return getTrustedCertificate(trustedCertificateId, BY_ID);
		
	}
	
	public TrustedCertificateData getTrustedCertificateByName(String trustedCertificateName) throws DataManagerException {
		
		return getTrustedCertificate(trustedCertificateName.trim(), BY_NAME);
		
	}
	
	private TrustedCertificateData getTrustedCertificate(Object trustedCertificateIdOrName, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);
		
		if (trustedCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			TrustedCertificateData trustedCertificate;

			if (isIdOrName) {
				trustedCertificate = trustedCertificateDataManager.getTrustedCertificateById((String) trustedCertificateIdOrName);
			} else {
				trustedCertificate = trustedCertificateDataManager.getTrustedCertificateByName((String) trustedCertificateIdOrName);
			}
			
			return trustedCertificate;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createTrustedCertificate(TrustedCertificateData trustedCertificate, IStaffData staffData) throws DataManagerException {
		
		List<TrustedCertificateData> TrustedCertificates = new ArrayList<TrustedCertificateData>();
		TrustedCertificates.add(trustedCertificate);
		createTrustedCertificate(TrustedCertificates, staffData, "");
		
	}

	public Map<String, List<Status>> createTrustedCertificate(List<TrustedCertificateData> TrustedCertificates, IStaffData staffData, String isPartialSuccess) throws DataManagerException {
		
		return insertRecords(TrustedCertificateDataManager.class, TrustedCertificates, staffData, ConfigConstant.CREATE_TRUSTED_CERTIFICATE, isPartialSuccess);
	}


	public void updateTrustedCertificateByName(TrustedCertificateData trustedCertificate, IStaffData staffData, String trustedCertificateName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);
		
		if (trustedCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			trustedCertificateDataManager.updateTrustedCertificateByName(trustedCertificate, trustedCertificateName.trim());
			
			staffData.setAuditName(trustedCertificateName.trim());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_TRUSTED_CERTIFICATE);

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

	
	public void deleteTrustedCertificateById(List<String> trustedCertificateIds, IStaffData staffData) throws DataManagerException {
		
		deleteTrustedCertificate(trustedCertificateIds, staffData, BY_ID);

	}
	
	public void deleteTrustedCertificateByName(List<String> trustedCertificateNames, IStaffData staffData) throws DataManagerException {
		
		deleteTrustedCertificate(trustedCertificateNames, staffData, BY_NAME);
		
	}
	
	private void deleteTrustedCertificate(List<String> trustedCertificateIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);
		
		if (trustedCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {

			if (Collectionz.isNullOrEmpty(trustedCertificateIdOrNames) == false) {
				
				session.beginTransaction();

				int size = trustedCertificateIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(trustedCertificateIdOrNames.get(i)) == false) {

						String trustedCertificateIdOrName = trustedCertificateIdOrNames.get(i).trim();
						
						String trustedCertificateName;

						if (isIdOrName) {
							trustedCertificateName = trustedCertificateDataManager.deleteTrustedCertificateById(trustedCertificateIdOrName);
						} else {
							trustedCertificateName = trustedCertificateDataManager.deleteTrustedCertificateByName(trustedCertificateIdOrName);
						}

						staffData.setAuditName(trustedCertificateName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_TRUSTED_CERTIFICATE);

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
