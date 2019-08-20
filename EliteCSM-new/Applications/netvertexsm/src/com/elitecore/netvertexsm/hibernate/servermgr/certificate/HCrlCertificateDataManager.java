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
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.CrlCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class HCrlCertificateDataManager extends HBaseDataManager implements CrlCertificateDataManager{

	private static final String MODULE = "CRL-CERTI-DM";
	
	@Override
	public PageList search(CrlCertificateData crlCertificateData, Map infoMap) throws DataManagerException {
		PageList pageList=null;
		int pageNo;
		int pageSize;
		try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(CrlCertificateData.class);

			if(crlCertificateData.getCrlCertificateName()!=null & crlCertificateData.getCrlCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike("crlCertificateName", crlCertificateData.getCrlCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc("crlCertificateId"));
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<CrlCertificateData> crlCertificateList=criteria.list();
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			pageList=new PageList(crlCertificateList,pageNo,totalPages,totalItems);
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void create(CrlCertificateData crlCertificateData) throws DataManagerException {
		try{
			Session session=getSession();
			/*for(CrlCertificateData data : crlCertificateDatas){*/
				session.save(crlCertificateData);	
			/*}			*/
			session.flush();
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	@Override
	public void update(CrlCertificateData crlCertificateData) throws DataManagerException {
		try{
			Session session=getSession();
			
			/*for(CrlCertificateData data: crlCertificateData){*/
				System.out.println("name : "+crlCertificateData.getCrlCertificateName());
				System.out.println("Last Modified Staff Id : "+crlCertificateData.getModifiedByStaffId());
				System.out.println("Last Modified date : "+crlCertificateData.getModifiedDate());
				verifyCrlCertificateNameforUpdate(crlCertificateData);
				Criteria criteria=session.createCriteria(CrlCertificateData.class);

				CrlCertificateData crlCertiData=(CrlCertificateData)criteria.add(Restrictions.eq("crlCertificateId", crlCertificateData.getCrlCertificateId())).uniqueResult();
				crlCertiData.setCrlCertificateName(crlCertificateData.getCrlCertificateName());
				crlCertiData.setCrlCertificate(crlCertificateData.getCrlCertificate());			
				crlCertiData.setModifiedByStaffId(crlCertificateData.getModifiedByStaffId());
				crlCertiData.setModifiedDate(crlCertificateData.getModifiedDate());
				session.update(crlCertiData);	
			/*}			*/
			session.flush();
			Logger.logDebug(MODULE, "Record Updated Successfully.");
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public void deleteAll(List<Long> lstCrlCertificateId) throws DataManagerException {
		
		List<CrlCertificateData> crlCertificateList =null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(CrlCertificateData.class);
			criteria.add(Restrictions.in("crlCertificateId", lstCrlCertificateId));
			crlCertificateList=criteria.list();
			if(crlCertificateList!=null && crlCertificateList.size()>0){
				for(int i=0 ; i<lstCrlCertificateId.size() ;i++){
					CrlCertificateData crlCertificateData=(CrlCertificateData)crlCertificateList.get(i);
					session.delete(crlCertificateData);					
					session.flush();	
				}
				Logger.logDebug(MODULE, "Record Deleted Successfully.");
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
	public CrlCertificateData getCrlCertificateDataByCrlCertificateId(Long crlCertificateId) throws DataManagerException {
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(CrlCertificateData.class);
			CrlCertificateData crlCertificateData=(CrlCertificateData) criteria.add(Restrictions.eq("crlCertificateId", crlCertificateId)).uniqueResult();
			return crlCertificateData;			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}
	}	
	
	private void verifyCrlCertificateNameforUpdate(CrlCertificateData crlCertificateData) throws DuplicateInstanceNameFoundException {
		Criteria criteria=null;
		Session session=getSession();
		criteria=session.createCriteria(CrlCertificateData.class);
		criteria.add(Restrictions.eq("crlCertificateName", crlCertificateData.getCrlCertificateName())).add(Restrictions.ne("crlCertificateId", crlCertificateData.getCrlCertificateId()));
		List<CrlCertificateData> list=criteria.list();
		if(list==null || list.isEmpty()){
			return;			
		}else{
			throw new DuplicateInstanceNameFoundException("CRL Certificate Name Is Duplicated.");
		}
	}
}
