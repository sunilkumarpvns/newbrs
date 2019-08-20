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
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.ServerCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class HServerCertificateDataManager extends HBaseDataManager implements ServerCertificateDataManager{

	private static final String MODULE = HServerCertificateDataManager.class.getSimpleName();

	@Override
	public void create(ServerCertificateData serverCertificateData)	throws DataManagerException {
		try{			
			Session session=getSession();
			session.save(serverCertificateData);
			session.flush();
			Logger.logDebug(MODULE, "Server Certificate created Successfully");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public PageList search(ServerCertificateData serverCertificateData,Map infoMap) throws DataManagerException {
		PageList pageList=null;
		try{			
			int pageNo=(Integer)infoMap.get("pageNo");
			int pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(ServerCertificateData.class);

			if(serverCertificateData.getServerCertificateName()!=null & serverCertificateData.getServerCertificateName().trim().length()>0){
				criteria.add(Restrictions.ilike("serverCertificateName", serverCertificateData.getServerCertificateName().trim(),MatchMode.ANYWHERE));
			}		
			criteria.addOrder(Order.asc("serverCertificateId"));
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<ServerCertificateData> serverCertificateList=criteria.list();
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			pageList=new PageList(serverCertificateList,pageNo,totalPages,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void update(ServerCertificateData serverCertificateData) throws DataManagerException {
		try{
			Session session=getSession();
			verifyServerCertificateNameforUpdate(serverCertificateData);
			Criteria criteria=session.createCriteria(ServerCertificateData.class);

			ServerCertificateData data=(ServerCertificateData)criteria.add(Restrictions.eq("serverCertificateId", serverCertificateData.getServerCertificateId())).uniqueResult();
			data.setServerCertificateName(serverCertificateData.getServerCertificateName());
			data.setCertificate(serverCertificateData.getCertificate());
			data.setPrivateKey(serverCertificateData.getPrivateKey());
			data.setPrivateKeyPassword(serverCertificateData.getPrivateKeyPassword());
			data.setPrivateKeyAlgorithm(serverCertificateData.getPrivateKeyAlgorithm());
			data.setModifiedByStaffId(serverCertificateData.getModifiedByStaffId());
			data.setModifiedDate(serverCertificateData.getModifiedDate());
			data.setCertificateFileName(serverCertificateData.getCertificateFileName());

			session.update(data);
			session.flush();
			Logger.logDebug(MODULE, "Server Certificate Updated Successfully");
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public void delete(List<Long> lstServerCertificateId) throws DataManagerException {

		List<ServerCertificateData> serverCertificateList =null;
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(ServerCertificateData.class);
			criteria.add(Restrictions.in("serverCertificateId", lstServerCertificateId));
			serverCertificateList=criteria.list();
			if(serverCertificateList!=null && serverCertificateList.size()>0){
				for(int i=0 ; i<lstServerCertificateId.size() ;i++){
					ServerCertificateData serverCertificateData=(ServerCertificateData)serverCertificateList.get(i);
					session.delete(serverCertificateData);
					session.flush();				
				}
				Logger.logDebug(MODULE, "Server Certificate Deleted Successfully.");
			}			
		}catch(ConstraintViolationException cve){
			throw cve;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}		
	}	

	private void verifyServerCertificateNameforUpdate(ServerCertificateData serverCertificateData) throws DuplicateInstanceNameFoundException {
		Criteria criteria=null;
		Session session=getSession();
		criteria=session.createCriteria(ServerCertificateData.class);
		criteria.add(Restrictions.eq("serverCertificateName", serverCertificateData.getServerCertificateName())).add(Restrictions.ne("serverCertificateId", serverCertificateData.getServerCertificateId()));
		List<ServerCertificateData> list=criteria.list();
		if(list==null || list.isEmpty()){
			return;			
		}else{
			throw new DuplicateInstanceNameFoundException("Diameter Peer Profile Name Is Duplicated.");
		}
	}

	@Override
	public ServerCertificateData getServerCertificateDataByServerCertificateId(Long serverCertificateId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			ServerCertificateData serverCertificateData = (ServerCertificateData)criteria.add(Restrictions.eq("serverCertificateId",serverCertificateId)).uniqueResult();
			return serverCertificateData;
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}	
}
