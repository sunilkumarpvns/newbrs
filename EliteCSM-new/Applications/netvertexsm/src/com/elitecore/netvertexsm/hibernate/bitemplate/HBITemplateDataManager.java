package com.elitecore.netvertexsm.hibernate.bitemplate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.bitemplate.BITemplateDataManager;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HBITemplateDataManager extends HBaseDataManager implements BITemplateDataManager {

	@Override
	public PageList search(BITemplateData templateData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BITemplateData.class);			

			if(templateData.getName()!=null) 
				criteria.add(Restrictions.ilike("name", templateData.getName(), MatchMode.ANYWHERE));

			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if(pageSize > 0) 
				criteria.setMaxResults(pageSize);
			List biTemplateList = criteria.list();

			//	Collections.sort(quotaMgrList);
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(biTemplateList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public List<BITemplateData> getBITemplateList() throws DataManagerException {
		List<BITemplateData> biTemplateList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BITemplateData.class);
			biTemplateList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return biTemplateList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteBITemplate(List<Long> biTemplateIDList, String actionAlias) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			for(Long id : biTemplateIDList){
				Criteria relCriteria = session.createCriteria(BISubKeyData.class);
				relCriteria.add(Restrictions.eq("biTemplateId", id));
				for(BISubKeyData biRelData : (List<BISubKeyData>) relCriteria.list()){
					BISubKeyData relData = (BISubKeyData) session.load(BISubKeyData.class, biRelData.getId());
					session.delete(relData);
					session.flush();
				}
				BITemplateData biData = (BITemplateData) session.load(BITemplateData.class, id);	
				session.delete(biData);
				session.flush();
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public void create(BITemplateData biTemplateData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			session.save(biTemplateData);
			session.flush();
			for(BISubKeyData relData : biTemplateData.getBiSubKeyList()) {
				relData.setBiTemplateId(biTemplateData.getId());
				session.save(relData);
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public BITemplateData getBITemplateData(BITemplateData biTemplateData) throws DataManagerException {
		Criteria criteria;
		try {
			Session session = getSession();
			criteria = session.createCriteria(BITemplateData.class);
			if(biTemplateData.getId() > 0) 
				criteria.add(Restrictions.eq("id", biTemplateData.getId()));
			return (BITemplateData) criteria.list().get(0);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BISubKeyData> getBISubKeyList(Long biTemplateId) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISubKeyData.class);
			criteria.add(Restrictions.eq("biTemplateId", biTemplateId));
			return (List<BISubKeyData>) criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
	}

	@Override
	public void updateBITemplate(BITemplateData biTemplateData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();			
						
			Criteria criteria = session.createCriteria(BISubKeyData.class);
			criteria.add(Restrictions.eq("biTemplateId", biTemplateData.getId()));
			for(BISubKeyData biRelData : (List<BISubKeyData>) criteria.list()){
				BISubKeyData relData = (BISubKeyData) session.load(BISubKeyData.class, biRelData.getId());
				session.delete(relData);
				session.flush();
			}
			for(BISubKeyData relData : biTemplateData.getBiSubKeyList()) {
				relData.setBiTemplateId(biTemplateData.getId());
				session.save(relData);
			}
			

			criteria = session.createCriteria(BITemplateData.class);
			criteria.add(Restrictions.eq("id", biTemplateData.getId()));
			BITemplateData biTempData = (BITemplateData)criteria.uniqueResult();
			
			biTempData.setId(biTemplateData.getId());
			biTempData.setName(biTemplateData.getName());
			biTempData.setDescription(biTemplateData.getDescription());
			biTempData.setKey(biTemplateData.getKey());
			setUpdateAuditDetail(biTempData);
			session.update(biTempData);
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public void uploadFile(List<BISubKeyData> subKeyList) throws DataManagerException {
		try{
			Session session = getSession();			
			for(BISubKeyData data : subKeyList){
					session.saveOrUpdate(data);								
			}
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

}
