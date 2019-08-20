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
import com.elitecore.elitesm.datamanager.servermgr.certificate.TrustedCertificateDataManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HTrustedCertificateDataManager extends HBaseDataManager implements TrustedCertificateDataManager{
	
	private static final String MODULE = "HTrustedCertificateDataManager";
	private static final String REASON = ", Reason: ";
	private static final String FAILED_TO_CREATE = "Failed to create ";
	private static final String TRUSTED_CERTIFICATE_ID = "trustedCertificateId";
	private static final String TRUSTED_CERTIFICATE_NAME = "trustedCertificateName";
	

	@Override
	public PageList search(TrustedCertificateData trustedCertificateData) throws DataManagerException {
		PageList pageList=null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);

			if(trustedCertificateData.getTrustedCertificateName()!=null & trustedCertificateData.getTrustedCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike(TRUSTED_CERTIFICATE_NAME, trustedCertificateData.getTrustedCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc(TRUSTED_CERTIFICATE_ID));
			List list=criteria.list();
			int totalItems=list.size();	

			List<TrustedCertificateData> trustedCertificateList=criteria.list();
			
			pageList=new PageList(trustedCertificateList,1,1,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Trusted Certificate List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Trusted Certificate List, Reason: " + e.getMessage(), e);
		}
		return pageList;
	}

	
	@Override
	public TrustedCertificateData getTrustedCertificateById(String trustedCertificateId) throws DataManagerException {
		
		return getTrustedCertificate(TRUSTED_CERTIFICATE_ID, trustedCertificateId);
		
	}

	@Override
	public TrustedCertificateData getTrustedCertificateByName(String trustedCertificateName) throws DataManagerException {
	
		return getTrustedCertificate(TRUSTED_CERTIFICATE_NAME, trustedCertificateName);
		
	}

	private TrustedCertificateData getTrustedCertificate(String propertyName, Object value) throws DataManagerException {
		
		String trustedCertificateName = (TRUSTED_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Trusted Certificate";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(TrustedCertificateData.class).add(Restrictions.eq(propertyName, value));
			TrustedCertificateData trustedCertificate = (TrustedCertificateData) criteria.uniqueResult();
			
			if (trustedCertificate == null) {
				throw new InvalidValueException("Trusted Certificate not found");
			}
			
			trustedCertificateName = trustedCertificate.getTrustedCertificateName();
			
			return trustedCertificate;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + trustedCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + trustedCertificateName + REASON + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		TrustedCertificateData trustedCertificate = (TrustedCertificateData) obj;
		String trustedCertificateName = trustedCertificate.getTrustedCertificateName();
		
		try {
			
			Session session = getSession();
			session.clear();
			session.save(trustedCertificate);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + trustedCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + trustedCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + trustedCertificateName + REASON + e.getMessage(), e);
		}
		return trustedCertificateName;
	}


	@Override
	public void updateTrustedCertificateByName(TrustedCertificateData trustedCertificate, String trustedCertificateName) throws DataManagerException {
		
		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(TrustedCertificateData.class).add(Restrictions.eq(TRUSTED_CERTIFICATE_NAME, trustedCertificateName));
			TrustedCertificateData trustedCertificateData = (TrustedCertificateData) criteria.uniqueResult();

			if (trustedCertificateData == null) {
				throw new InvalidValueException("Trusted Certificate not found");
			}
			
			trustedCertificateData.setCertificateFileName(trustedCertificate.getCertificateFileName());
			trustedCertificateData.setTrustedCertificate(trustedCertificate.getTrustedCertificate());
			trustedCertificateData.setModifiedByStaffId(trustedCertificate.getModifiedByStaffId());
			trustedCertificateData.setModifiedDate(trustedCertificate.getModifiedDate());
			
			session.update(trustedCertificateData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + trustedCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + trustedCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + trustedCertificateName + REASON + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteTrustedCertificateById(String trustedCertificateId) throws DataManagerException {
		
		return deleteTrustedCertificate(TRUSTED_CERTIFICATE_ID, trustedCertificateId);

	}

	@Override
	public String deleteTrustedCertificateByName(String trustedCertificateName) throws DataManagerException {

		return deleteTrustedCertificate(TRUSTED_CERTIFICATE_NAME, trustedCertificateName);

	}
	
	private String deleteTrustedCertificate(String propertyName, Object value) throws DataManagerException {
		
		String trustedCertificateName = (TRUSTED_CERTIFICATE_NAME.equals(propertyName)) ? (String) value : "Trusted Certificate";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(TrustedCertificateData.class).add(Restrictions.eq(propertyName, value));
			TrustedCertificateData trustedCertificate = (TrustedCertificateData) criteria.uniqueResult();

			if (trustedCertificate == null) {
				throw new InvalidValueException("Trusted Certificate not found");
			}

			trustedCertificateName = trustedCertificate.getTrustedCertificateName();
			
			session.delete(trustedCertificate);
			session.flush();

			return trustedCertificateName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + trustedCertificateName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + trustedCertificateName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + trustedCertificateName + REASON + e.getMessage(), e);
		} 

	}

	
}
