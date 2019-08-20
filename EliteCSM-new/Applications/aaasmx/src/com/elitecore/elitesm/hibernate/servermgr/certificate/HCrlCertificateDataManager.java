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
import com.elitecore.elitesm.datamanager.servermgr.certificate.CrlCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HCrlCertificateDataManager extends HBaseDataManager implements CrlCertificateDataManager{

	private static final String CRL_CERTIFICATE_ID = "crlCertificateId";
	private static final String CRL_CERTIFICATE_NAME = "crlCertificateName";
	private static final String MODULE = "HCrlCertificateDataManager";
	
	@Override
	public PageList search(CrlCertificateData crlCertificateData) throws DataManagerException {
		PageList pageList=null;
		int pageNo;
		int pageSize;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(CrlCertificateData.class);

			if(crlCertificateData.getCrlCertificateName()!=null & crlCertificateData.getCrlCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike(CRL_CERTIFICATE_NAME, crlCertificateData.getCrlCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc(CRL_CERTIFICATE_ID));
			List list=criteria.list();
			int totalItems=list.size();	

			List<CrlCertificateData> crlCertificateList=criteria.list();
			
			pageList=new PageList(crlCertificateList,1,1,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Certificate Revocation List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Certificate Revocation List, Reason: " + e.getMessage(), e);
		}
		return pageList;
	}

	
	@Override
	public CrlCertificateData getCertificateRevocationById(String certificateRevocationId) throws DataManagerException {
		
		return getCertificateRevocation(CRL_CERTIFICATE_ID, certificateRevocationId);
		
	}

	@Override
	public CrlCertificateData getCertificateRevocationByName(String certificateRevocationName) throws DataManagerException {
	
		return getCertificateRevocation(CRL_CERTIFICATE_NAME, certificateRevocationName);
		
	}

	private CrlCertificateData getCertificateRevocation(String propertyName, Object value) throws DataManagerException {
		
		String certificateRevocationName = (CRL_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Certificate Revocation";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(CrlCertificateData.class).add(Restrictions.eq(propertyName, value));
			CrlCertificateData certificateRevocation = (CrlCertificateData) criteria.uniqueResult();
			
			if (certificateRevocation == null) {
				throw new InvalidValueException("Certificate Revocation not found");
			}
			
			certificateRevocationName = certificateRevocation.getCrlCertificateName();
			
			return certificateRevocation;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + certificateRevocationName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + certificateRevocationName + REASON + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		
		CrlCertificateData certificateRevocation = (CrlCertificateData)obj;
		String certificateRevocationName = certificateRevocation.getCrlCertificateName();
		
		try {
			
			Session session = getSession();
			session.clear();
			session.save(certificateRevocation);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + certificateRevocationName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + certificateRevocationName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + certificateRevocationName + REASON + e.getMessage(), e);
		}
		return certificateRevocationName;
	}


	@Override
	public void updateCertificateRevocationByName(CrlCertificateData certificateRevocation, String certificateRevocationName) throws DataManagerException {
		
		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(CrlCertificateData.class).add(Restrictions.eq(CRL_CERTIFICATE_NAME, certificateRevocationName));
			CrlCertificateData certificateRevocationData = (CrlCertificateData) criteria.uniqueResult();

			if (certificateRevocationData == null) {
				throw new InvalidValueException("Certificate Revocation not found");
			}
			
			certificateRevocationData.setCertificateFileName(certificateRevocation.getCertificateFileName());
			certificateRevocationData.setCrlCertificate(certificateRevocation.getCrlCertificate());
			certificateRevocationData.setModifiedByStaffId(certificateRevocation.getModifiedByStaffId());
			certificateRevocationData.setModifiedDate(certificateRevocation.getModifiedDate());
			
			session.update(certificateRevocationData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + certificateRevocationName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + certificateRevocationName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + certificateRevocationName + REASON + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteCertificateRevocationById(String certificateRevocationId) throws DataManagerException {
		
		return deleteCertificateRevocation(CRL_CERTIFICATE_ID, certificateRevocationId);

	}

	@Override
	public String deleteCertificateRevocationByName(String certificateRevocationName) throws DataManagerException {

		return deleteCertificateRevocation(CRL_CERTIFICATE_NAME, certificateRevocationName);

	}
	
	private String deleteCertificateRevocation(String propertyName, Object value) throws DataManagerException {
		
		String certificateRevocationName = (CRL_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Certificate Revocation";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(CrlCertificateData.class).add(Restrictions.eq(propertyName, value));
			CrlCertificateData certificateRevocation = (CrlCertificateData) criteria.uniqueResult();

			if (certificateRevocation == null) {
				throw new InvalidValueException("Certificate Revocation not found");
			}

			certificateRevocationName = certificateRevocation.getCrlCertificateName();
			
			session.delete(certificateRevocation);
			session.flush();

			return certificateRevocationName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + certificateRevocationName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + certificateRevocationName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + certificateRevocationName + REASON + e.getMessage(), e);
		} 

	}

	
}
