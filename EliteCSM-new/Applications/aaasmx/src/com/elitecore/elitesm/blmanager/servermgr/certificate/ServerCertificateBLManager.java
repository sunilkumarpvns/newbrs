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
import com.elitecore.elitesm.datamanager.servermgr.certificate.ServerCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class ServerCertificateBLManager extends BaseBLManager{

	
	public PageList search(ServerCertificateData serverCertificateData, IStaffData staffData) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServerCertificateDataManager serverCertificateDataManager=getServerCertificateDataManager(session);
		
		if(serverCertificateDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			session.beginTransaction();
			PageList lstServerCertificateList = serverCertificateDataManager.search(serverCertificateData);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_SERVER_CERTIFICATE);
			commit(session);
			return lstServerCertificateList;
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


	public ServerCertificateData view(String serverCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		ServerCertificateDataManager serverCertificateDataManager = getServerCertificateDataManager(session);
		
		if(serverCertificateDataManager==null){
			throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
		}

		try{
			ServerCertificateData serverCertificateData = serverCertificateDataManager.getServerCertificateById(serverCertificateId);
			return serverCertificateData;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	
	public ServerCertificateDataManager getServerCertificateDataManager(IDataManagerSession session) {
		ServerCertificateDataManager serverCertificateDatamanager=(ServerCertificateDataManager)DataManagerFactory.getInstance().getDataManager(ServerCertificateDataManager.class, session);
		return serverCertificateDatamanager;
	}	
	
	
	public ServerCertificateData getServerCertificateById(String serverCertificateId) throws DataManagerException {
		
		return getServerCertificate(serverCertificateId, BY_ID);
		
	}
	
	public ServerCertificateData getServerCertificateByName(String serverCertificateName) throws DataManagerException {
		
		return getServerCertificate(serverCertificateName.trim(), BY_NAME);
		
	}
	
	private ServerCertificateData getServerCertificate(Object serverCertificateIdOrName, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServerCertificateDataManager serverCertificateDataManager = getServerCertificateDataManager(session);
		
		if (serverCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			
			ServerCertificateData serverCertificate;

			if (isIdOrName) {
				serverCertificate = serverCertificateDataManager.getServerCertificateById((String) serverCertificateIdOrName);
			} else {
				serverCertificate = serverCertificateDataManager.getServerCertificateByName((String) serverCertificateIdOrName);
			}
			
			return serverCertificate;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createServerCertificate(ServerCertificateData serverCertificate, IStaffData staffData) throws DataManagerException {
		
		List<ServerCertificateData> ServerCertificates = new ArrayList<ServerCertificateData>();
		ServerCertificates.add(serverCertificate);
		createServerCertificate(ServerCertificates, staffData, "");
		
	}

	public Map<String, List<Status>> createServerCertificate(List<ServerCertificateData> ServerCertificates, IStaffData staffData, String isPartialSuccess) throws DataManagerException {
		
		return insertRecords(ServerCertificateDataManager.class, ServerCertificates, staffData,
				ConfigConstant.CREATE_SERVER_CERTIFICATE, isPartialSuccess);
	}


	public void updateServerCertificateByName(ServerCertificateData serverCertificate, IStaffData staffData, String serverCertificateName, String certificateType) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServerCertificateDataManager serverCertificateDataManager = getServerCertificateDataManager(session);
		
		if (serverCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			serverCertificateDataManager.updateServerCertificateByName(serverCertificate, serverCertificateName.trim(), certificateType);
			
			staffData.setAuditName(serverCertificateName.trim());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_SERVER_CERTIFICATE);

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

	
	public void deleteServerCertificateById(List<String> serverCertificateIds, IStaffData staffData) throws DataManagerException {
		
		deleteServerCertificate(serverCertificateIds, staffData, BY_ID);

	}
	
	public void deleteServerCertificateByName(List<String> serverCertificateNames, IStaffData staffData) throws DataManagerException {
		
		deleteServerCertificate(serverCertificateNames, staffData, BY_NAME);
		
	}
	
	private void deleteServerCertificate(List<String> serverCertificateIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServerCertificateDataManager serverCertificateDataManager = getServerCertificateDataManager(session);
		
		if (serverCertificateDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {

			if (Collectionz.isNullOrEmpty(serverCertificateIdOrNames) == false) {
				
				session.beginTransaction();

				int size = serverCertificateIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(serverCertificateIdOrNames.get(i)) == false) {

						String serverCertificateIdOrName = serverCertificateIdOrNames.get(i).trim();
						
						String serverCertificateName;

						if (isIdOrName) {
							serverCertificateName = serverCertificateDataManager.deleteServerCertificateById(serverCertificateIdOrName);
						} else {
							serverCertificateName = serverCertificateDataManager.deleteServerCertificateByName(serverCertificateIdOrName);
						}

						staffData.setAuditName(serverCertificateName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_SERVER_CERTIFICATE);

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
