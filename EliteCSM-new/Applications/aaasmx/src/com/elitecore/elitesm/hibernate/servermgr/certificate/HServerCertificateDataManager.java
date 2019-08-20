package com.elitecore.elitesm.hibernate.servermgr.certificate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.ServerCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HServerCertificateDataManager extends HBaseDataManager implements ServerCertificateDataManager{
	
	private static final String REASON = ", Reason: ";
	private static final String FAILED_TO_CREATE = "Failed to create ";
	private static final String SERVER_CERTIFICATE_ID = "serverCertificateId";
	private static final String SERVER_CERTIFICATE_NAME = "serverCertificateName";

	private static final String MODULE = "SERVER-CERTI-DM";
	

	@Override
	public PageList search(ServerCertificateData serverCertificateData) throws DataManagerException {
		PageList pageList=null;
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(ServerCertificateData.class);

			if(serverCertificateData.getServerCertificateName()!=null & serverCertificateData.getServerCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike(SERVER_CERTIFICATE_NAME, serverCertificateData.getServerCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc(SERVER_CERTIFICATE_ID));
			List list=criteria.list();
			int totalItems=list.size();	


			List<ServerCertificateData> serverCertificateList=criteria.list();

			Logger.logDebug(MODULE, "Search Successfully....");
			pageList=new PageList(serverCertificateList,1,1,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Server Certificate List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Server Certificate List, Reason: " + e.getMessage(), e);
		}
		return pageList;
	}

	
	@Override
	public ServerCertificateData getServerCertificateById(String serverCertificateId) throws DataManagerException {
		
		return getServerCertificate(SERVER_CERTIFICATE_ID, serverCertificateId);
		
	}

	@Override
	public ServerCertificateData getServerCertificateByName(String serverCertificateName) throws DataManagerException {
	
		return getServerCertificate(SERVER_CERTIFICATE_NAME, serverCertificateName);
		
	}

	private ServerCertificateData getServerCertificate(String propertyName, Object value) throws DataManagerException {
		
		String serverCertificateName = (SERVER_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Server Certificate";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(ServerCertificateData.class).add(Restrictions.eq(propertyName, value));
			ServerCertificateData serverCertificate = (ServerCertificateData) criteria.uniqueResult();
			
			if (serverCertificate == null) {
				throw new InvalidValueException("Server Certificate not found");
			}
			
			serverCertificateName = serverCertificate.getServerCertificateName();
			
			return serverCertificate;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + serverCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + serverCertificateName + REASON + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		
		ServerCertificateData serverCertificate = (ServerCertificateData) obj;
		String serverCertificateName = serverCertificate.getServerCertificateName();
		
		try {
			
			Session session = getSession();
			session.clear();
			
			session.save(serverCertificate);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + serverCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + serverCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + serverCertificateName + REASON + e.getMessage(), e);
		}
		return serverCertificateName;
	}
	

	@Override
	public void updateServerCertificateByName(ServerCertificateData serverCertificate, String serverCertificateName, String certificateType) throws DataManagerException {
		
		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ServerCertificateData.class).add(Restrictions.eq(SERVER_CERTIFICATE_NAME, serverCertificateName));
			ServerCertificateData serverCertificateData = (ServerCertificateData) criteria.uniqueResult();

			if (serverCertificateData == null) {
				throw new InvalidValueException("Server Certificate not found");
			}
			
			if (certificateType.equals("publicCertificate") == true) {
				
				serverCertificateData.setCertificateFileName(serverCertificate.getCertificateFileName());
				serverCertificateData.setCertificate(serverCertificate.getCertificate());
				
			} else if (certificateType.equals("privateKey") == true) {
				
				serverCertificateData.setPrivateKeyFileName(serverCertificate.getPrivateKeyFileName());
				serverCertificateData.setPrivateKey(serverCertificate.getPrivateKey());
				
			}

			serverCertificateData.setModifiedByStaffId(serverCertificate.getModifiedByStaffId());
			serverCertificateData.setModifiedDate(serverCertificate.getModifiedDate());
			
			session.update(serverCertificateData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + serverCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + serverCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + serverCertificateName + REASON + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteServerCertificateById(String serverCertificateId) throws DataManagerException {
		
		return deleteServerCertificate(SERVER_CERTIFICATE_ID, serverCertificateId);

	}

	@Override
	public String deleteServerCertificateByName(String serverCertificateName) throws DataManagerException {

		return deleteServerCertificate(SERVER_CERTIFICATE_NAME, serverCertificateName);

	}
	
	private String deleteServerCertificate(String propertyName, Object value) throws DataManagerException {
		
		String serverCertificateName = (SERVER_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Server Certificate";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ServerCertificateData.class).add(Restrictions.eq(propertyName, value));
			ServerCertificateData serverCertificate = (ServerCertificateData) criteria.uniqueResult();

			if (serverCertificate == null) {
				throw new InvalidValueException("Server Certificate not found");
			}

			serverCertificateName = serverCertificate.getServerCertificateName();
			
			session.delete(serverCertificate);
			session.flush();

			return serverCertificateName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + serverCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + serverCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + serverCertificateName + REASON + e.getMessage(), e);
		} 

	}

	
}
