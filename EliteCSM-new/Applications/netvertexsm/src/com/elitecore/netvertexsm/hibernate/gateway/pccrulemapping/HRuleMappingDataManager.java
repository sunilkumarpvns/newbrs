package com.elitecore.netvertexsm.hibernate.gateway.pccrulemapping;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.RuleMappingDataManager;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HRuleMappingDataManager  extends HBaseDataManager implements RuleMappingDataManager {

	@Override
	public PageList search(RuleMappingData ruleMappingData, int requiredPageNo,
			Integer pageSize) throws DataManagerException {
		PageList pageList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RuleMappingData.class).addOrder(Order.asc("name"));
			if(ruleMappingData.getName() != null && ruleMappingData.getName().length() > 0){
				criteria.add(Restrictions.ilike("name", ruleMappingData.getName(),MatchMode.ANYWHERE));
			}
			int totalItems = criteria.list().size();
			criteria.setFirstResult(((requiredPageNo - 1) * pageSize));
			if(pageSize > 0){
				criteria.setMaxResults(pageSize);
			}
			List groupDataList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0){
				totalPages -= 1;
			}
			pageList = new PageList(groupDataList, requiredPageNo, totalPages, totalItems);
		}catch(HibernateException hExe){
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void create(RuleMappingData ruleMappingData)throws DataManagerException {
		try{
			Session session = getSession();
			session.save(ruleMappingData);
			session.flush();
			for(PCCRuleMappingData mappingData : ruleMappingData.getPccRuleMappingList()) {
				mappingData.setRuleMappingId(ruleMappingData.getRuleMappingId());
				session.save(mappingData);
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
	public void delete(Long[] ruleMappingIDS) throws DataManagerException {
		try{
			Session session = getSession();			
			for(Long ruleMappingID:ruleMappingIDS){
				deletePCCRuleMapData(ruleMappingID);
				RuleMappingData ruleMappingData = (RuleMappingData) session.load(RuleMappingData.class, ruleMappingID);
				session.delete(ruleMappingData);
			}
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }	
		
	}
	
	public void deletePCCRuleMapData(Long profileId)throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCCRuleMappingData.class);
			criteria.add(Restrictions.eq("ruleMappingId", profileId));
			List<PCCRuleMappingData> pccRuleMapList = criteria.list();
			
			for(PCCRuleMappingData pccRuleMapData : pccRuleMapList){
				pccRuleMapData = (PCCRuleMappingData) session.load(PCCRuleMappingData.class, pccRuleMapData.getPccRuleMapId());
				session.delete(pccRuleMapData);
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
	public void update(RuleMappingData ruleMappingData)	throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RuleMappingData.class).add(Restrictions.eq("ruleMappingId",ruleMappingData.getRuleMappingId()));
			RuleMappingData ruleMapData = (RuleMappingData)criteria.uniqueResult();
			
			deletePCCRuleMapData(ruleMappingData.getRuleMappingId());	
			
			for(PCCRuleMappingData mappingData : ruleMappingData.getPccRuleMappingList()) {
				mappingData.setRuleMappingId(ruleMappingData.getRuleMappingId());
				session.save(mappingData);
			}
			
			ruleMapData.setRuleMappingId(ruleMappingData.getRuleMappingId());
			ruleMapData.setName(ruleMappingData.getName());
			ruleMapData.setDescription(ruleMappingData.getDescription());
			session.update(ruleMapData);
			session.flush();
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public RuleMappingData getRuleMappingData(long ruleMappingId)throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(RuleMappingData.class);
		RuleMappingData ruleMappingData=null;
		if(ruleMappingId > 0) 
			criteria.add(Restrictions.eq("ruleMappingId", ruleMappingId));
		ruleMappingData = (RuleMappingData) criteria.uniqueResult();
		
		Criteria criteriaPCCRUleMapping = session.createCriteria(PCCRuleMappingData.class);
		if(ruleMappingId > 0) 
			criteriaPCCRUleMapping.add(Restrictions.eq("ruleMappingId", ruleMappingId)).addOrder(Order.asc("pccRuleMapId"));
		ruleMappingData.setPccRuleMappingList(criteriaPCCRUleMapping.list());
		return ruleMappingData;
	}

	@Override
	public List<RuleMappingData> getRoutingTableDataList()
			throws DataManagerException {
		List<RuleMappingData> ruleMappingList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RuleMappingData.class).addOrder(Order.asc("name"));
			ruleMappingList = criteria.list();
		}catch(HibernateException hExe){
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return ruleMappingList;
	}

}
