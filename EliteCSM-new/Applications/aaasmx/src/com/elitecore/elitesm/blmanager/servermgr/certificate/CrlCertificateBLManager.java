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
import com.elitecore.elitesm.datamanager.servermgr.certificate.CrlCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class CrlCertificateBLManager extends BaseBLManager{

	public PageList search(CrlCertificateData crlCertificateData, IStaffData staffData) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		CrlCertificateDataManager crlCertificateDataManager=getCrlCertificateDataManager(session);
		
		if(crlCertificateDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			PageList lstCrlCertificateList=crlCertificateDataManager.search(crlCertificateData);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_CERTIFICATE_REVOCATION_LIST);
			commit(session);
			return lstCrlCertificateList;
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


	public CrlCertificateData view(String crlCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CrlCertificateDataManager crlCertificateDataManager = getCrlCertificateDataManager(session);
		
		if(crlCertificateDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());			
		}

		try{
			CrlCertificateData crlCertificateData = crlCertificateDataManager.getCertificateRevocationById(crlCertificateId);
			return crlCertificateData;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public CrlCertificateDataManager getCrlCertificateDataManager(IDataManagerSession session) {
		CrlCertificateDataManager crlCertificateDataManager=(CrlCertificateDataManager)DataManagerFactory.getInstance().getDataManager(CrlCertificateDataManager.class, session);
		return crlCertificateDataManager;
	}	

	
	public CrlCertificateData getCertificateRevocationById(String certificateRevocationId) throws DataManagerException {
		
		return getCertificateRevocation(certificateRevocationId, BY_ID);
		
	}
	
	public CrlCertificateData getCertificateRevocationByName(String certificateRevocationName) throws DataManagerException {
		
		return getCertificateRevocation(certificateRevocationName.trim(), BY_NAME);
		
	}
	
	private CrlCertificateData getCertificateRevocation(Object certificateRevocationIdOrName, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CrlCertificateDataManager certificateRevocationDataManager = getCrlCertificateDataManager(session);
		
		if (certificateRevocationDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			CrlCertificateData certificateRevocation;

			if (isIdOrName) {
				certificateRevocation = certificateRevocationDataManager.getCertificateRevocationById((String) certificateRevocationIdOrName);
			} else {
				certificateRevocation = certificateRevocationDataManager.getCertificateRevocationByName((String) certificateRevocationIdOrName);
			}
			
			return certificateRevocation;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createCertificateRevocation(CrlCertificateData certificateRevocation, IStaffData staffData) throws DataManagerException {
		
		List<CrlCertificateData> CertificateRevocations = new ArrayList<CrlCertificateData>();
		CertificateRevocations.add(certificateRevocation);
		createCertificateRevocation(CertificateRevocations, staffData, "");
		
	}

	public Map<String, List<Status>> createCertificateRevocation(List<CrlCertificateData> CertificateRevocations, IStaffData staffData, String partialSuccess) throws DataManagerException {
		
		return insertRecords(CrlCertificateDataManager.class, CertificateRevocations, staffData, ConfigConstant.CREATE_CERTIFICATE_REVOCATION_LIST, partialSuccess);
	}


	public void updateCertificateRevocationByName(CrlCertificateData certificateRevocation, IStaffData staffData, String certificateRevocationName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CrlCertificateDataManager certificateRevocationDataManager = getCrlCertificateDataManager(session);
		
		if (certificateRevocationDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			certificateRevocationDataManager.updateCertificateRevocationByName(certificateRevocation, certificateRevocationName.trim());
			
			staffData.setAuditName(certificateRevocationName.trim());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_CERTIFICATE_REVOCATION_LIST);

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

	
	public void deleteCertificateRevocationById(List<String> certificateRevocationIds, IStaffData staffData) throws DataManagerException {
		
		deleteCertificateRevocation(certificateRevocationIds, staffData, BY_ID);

	}
	
	public void deleteCertificateRevocationByName(List<String> certificateRevocationNames, IStaffData staffData) throws DataManagerException {
		
		deleteCertificateRevocation(certificateRevocationNames, staffData, BY_NAME);
		
	}
	
	private void deleteCertificateRevocation(List<String> certificateRevocationIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CrlCertificateDataManager certificateRevocationDataManager = getCrlCertificateDataManager(session);
		
		if (certificateRevocationDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {

			if (Collectionz.isNullOrEmpty(certificateRevocationIdOrNames) == false) {
				
				session.beginTransaction();

				int size = certificateRevocationIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(certificateRevocationIdOrNames.get(i)) == false) {

						String certificateRevocationIdOrName = certificateRevocationIdOrNames.get(i).trim();
						
						String certificateRevocationName;

						if (isIdOrName) {
							certificateRevocationName = certificateRevocationDataManager.deleteCertificateRevocationById(certificateRevocationIdOrName);
						} else {
							certificateRevocationName = certificateRevocationDataManager.deleteCertificateRevocationByName(certificateRevocationIdOrName);
						}

						staffData.setAuditName(certificateRevocationName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CERTIFICATE_REVOCATION_LIST);

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
