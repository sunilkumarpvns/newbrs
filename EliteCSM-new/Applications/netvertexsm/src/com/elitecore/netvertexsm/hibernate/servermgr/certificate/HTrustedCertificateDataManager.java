package com.elitecore.netvertexsm.hibernate.servermgr.certificate;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.TrustedCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class HTrustedCertificateDataManager extends HBaseDataManager implements TrustedCertificateDataManager{
	private static final String MODULE = "TRUST-CERTI-DM";
	@Override
	public void create(TrustedCertificateData trustedCertificateData) throws DataManagerException {
		try{
			Session session=getSession();
			session.save(trustedCertificateData);
			session.flush();
			Logger.logDebug(MODULE, "Trusted certificate Created Successfully");
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public PageList search(TrustedCertificateData trustedCertificateData, Map infoMap) throws DataManagerException {
		PageList pageList=null;
		int pageNo;
		int pageSize;
		try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);

			if(trustedCertificateData.getTrustedCertificateName()!=null & trustedCertificateData.getTrustedCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike("trustedCertificateName", trustedCertificateData.getTrustedCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc("trustedCertificateId"));
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<TrustedCertificateData> trustedCertificateList=criteria.list();
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			pageList=new PageList(trustedCertificateList,pageNo,totalPages,totalItems);
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void update(TrustedCertificateData trustedCertificateData) throws DataManagerException {
		try{
			Session session=getSession();
			verifyTrustedCertificateNameforUpdate(trustedCertificateData);
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);

			TrustedCertificateData data=(TrustedCertificateData)criteria.add(Restrictions.eq("trustedCertificateId", trustedCertificateData.getTrustedCertificateId())).uniqueResult();
			data.setTrustedCertificateName(trustedCertificateData.getTrustedCertificateName());
			data.setTrustedCertificate(trustedCertificateData.getTrustedCertificate());			
			data.setModifiedByStaffId(trustedCertificateData.getModifiedByStaffId());
			data.setModifiedDate(trustedCertificateData.getModifiedDate());
			data.setCertificateFileName(trustedCertificateData.getCertificateFileName());

			session.update(data);
			session.flush();
			Logger.logDebug(MODULE, "Trusted certificate Updated Successfully");
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public void delete(Long trustedCertificateId) throws DataManagerException {
		
		List<TrustedCertificateData> trustedCertificateList =null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);
			criteria.add(Restrictions.eq("trustedCertificateId", trustedCertificateId));
			trustedCertificateList=criteria.list();
			if(trustedCertificateList!=null && trustedCertificateList.size()>0){
				TrustedCertificateData trustedCertificateData=(TrustedCertificateData)trustedCertificateList.get(0);
				session.delete(trustedCertificateData);
				session.flush();				
				Logger.logDebug(MODULE, "Trusted certificate deleted Successfully");
			}
		}catch(ConstraintViolationException cve){
			throw cve;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}		
	}

	@Override
	public TrustedCertificateData getTrustedCertificateDataByTrustedCertificateId( Long trustedCertificateId) throws DataManagerException {
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);
			TrustedCertificateData trustedCertificateData=(TrustedCertificateData) criteria.add(Restrictions.eq("trustedCertificateId", trustedCertificateId)).uniqueResult();
			return trustedCertificateData;			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}
	}	

	private void verifyTrustedCertificateNameforUpdate(TrustedCertificateData trustedCertificateData) throws DuplicateInstanceNameFoundException {
		Criteria criteria=null;
		Session session=getSession();
		criteria=session.createCriteria(TrustedCertificateData.class);
		criteria.add(Restrictions.eq("trustedCertificateName", trustedCertificateData.getTrustedCertificateName())).add(Restrictions.ne("trustedCertificateId", trustedCertificateData.getTrustedCertificateId()));
		List<TrustedCertificateData> list=criteria.list();
		if(list==null || list.isEmpty()){
			return;			
		}else{
			throw new DuplicateInstanceNameFoundException("Found duplicate Trusted Certificate Name");
		}
	}

	@Override
	public void deleteAll(List<Long> lstTrustedCertificateId) throws DataManagerException {
		
		List<TrustedCertificateData> trustedCertificateList =null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(TrustedCertificateData.class);
			criteria.add(Restrictions.in("trustedCertificateId", lstTrustedCertificateId));
			trustedCertificateList=criteria.list();
			if(trustedCertificateList!=null && trustedCertificateList.size()>0){
				for(int i=0 ; i<lstTrustedCertificateId.size() ;i++){
					TrustedCertificateData trustedCertificateData=(TrustedCertificateData)trustedCertificateList.get(i);
					session.delete(trustedCertificateData);					
					session.flush();	
				}
				Logger.logDebug(MODULE, "Trusted certificates deleted Successfully");
			}			
		}catch(ConstraintViolationException cve){
			throw cve;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}		
	}
}
