package com.elitecore.netvertexsm.hibernate.servicepolicy;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.ServicePolicyDataManager;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;

public class HServicePolicyDataManager extends HBaseDataManager implements ServicePolicyDataManager {

	/**
	 * This Method is generated to create PCRF Service Policy.
	 * @author Manjil Purohit
	 * @param pcrfServicePolicyData
	 * @throws DataManagerException
	 */
	public void create(PCRFServicePolicyData pcrfServicePolicyData)	throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<PCRFServicePolicyData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				PCRFServicePolicyData data = list.get(0);
				pcrfServicePolicyData.setOrderNumber(data.getOrderNumber()+1);
			}
			
			session.save(pcrfServicePolicyData);
			session.flush();
			
			if(pcrfServicePolicyData.getPcrfPolicyCDRDriverRelDataList()!=null) {
				for(int i=0; i<pcrfServicePolicyData.getPcrfPolicyCDRDriverRelDataList().size(); i++) {
					pcrfServicePolicyData.getPcrfPolicyCDRDriverRelDataList().get(i).setPcrfPolicyId(pcrfServicePolicyData.getPcrfPolicyId());
					session.save(pcrfServicePolicyData.getPcrfPolicyCDRDriverRelDataList().get(i));
				}
			}

			if(pcrfServicePolicyData.getPcrfServicePolicySyGatewayRelDataList()!=null) {
				for(int i=0; i<pcrfServicePolicyData.getPcrfServicePolicySyGatewayRelDataList().size(); i++) {
					pcrfServicePolicyData.getPcrfServicePolicySyGatewayRelDataList().get(i).setPcrfPolicyId(pcrfServicePolicyData.getPcrfPolicyId());
					session.save(pcrfServicePolicyData.getPcrfServicePolicySyGatewayRelDataList().get(i));
				}
			}
		
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	/**
	 * This Method is generated to create PCRF Service Policy.
	 * @author Manjil Purohit
	 * @param pcrfServicePolicyData
	 * @throws DataManagerException
	 */
	public PageList search(PCRFServicePolicyData pcrfServicePolicyData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class).addOrder(Order.asc("orderNumber"));			
			
			if(pcrfServicePolicyData.getName()!=null && pcrfServicePolicyData.getName().length()>0) {
				criteria.add(Restrictions.ilike("name", pcrfServicePolicyData.getName(), MatchMode.ANYWHERE));
			}
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List<PCRFServicePolicyData> servicePolicyList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(servicePolicyList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return pageList;
	}
	
	public List searchServiceServicePolicy() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
			
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public void changeServicePolicyOrder(String[] order) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class);
			List<PCRFServicePolicyData> policyList = criteria.add(Restrictions.eq("status", "CST01")).list();
			if(order != null){
				for(int i=0;i<order.length;i++){
					String name = order[i];
					for(int j=0;j<policyList.size();j++){
						PCRFServicePolicyData tempPolicyData = policyList.get(j);
						if(tempPolicyData.getName().equals(name)){
							tempPolicyData.setOrderNumber(i+1);
							session.update(tempPolicyData);
							break;
						}
					}
				}
			}
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public void update(PCRFServicePolicyData pcrfServicePolicyData, long pcrfPolicyId) throws DataManagerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(List ldapSPInterfaceIds) throws DataManagerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PCRFServicePolicyData> getPCRFServicePolicyList() throws DataManagerException {
		// TODO Auto-generated method stub
		List<PCRFServicePolicyData> pcrfServicePolicyDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class);
			pcrfServicePolicyDataList = criteria.list();
		} catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pcrfServicePolicyDataList;		
	}

	/**
	 * This method will return the unique result provided by criteria.
	 * @author Manjil Purohit
	 * @param pcrfPolicyData
	 * @return pcrfPolicyData
	 */
	public PCRFServicePolicyData getPCRFServicePolicyData(PCRFServicePolicyData pcrfPolicyData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(PCRFServicePolicyData.class);
		
		if(pcrfPolicyData.getPcrfPolicyId()>0) {
			criteria.add(Restrictions.eq("pcrfPolicyId", pcrfPolicyData.getPcrfPolicyId()));
		}
		return (PCRFServicePolicyData) criteria.uniqueResult();
	}
	
	public DriverInstanceData getDriverInstanceData(long driverInstanceId) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(DriverInstanceData.class);
		
		if(driverInstanceId>0) {
			criteria.add(Restrictions.eq("driverInstanceId", driverInstanceId));
		}
		return (DriverInstanceData) criteria.uniqueResult();
	}
	
	public List<PCRFPolicyCDRDriverRelData> getPCRFPolicyCDRDriverList(long pcrfID) throws DataManagerException {
		List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFPolicyCDRDriverRelData.class).addOrder(Order.asc("weightage"));
			if(pcrfID>0) {
				criteria.add(Restrictions.eq("pcrfPolicyId", pcrfID));
			}	
			pcrfPolicyCDRDriverRelDataList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return pcrfPolicyCDRDriverRelDataList;
	}

	public List<PCRFServicePolicySyGatewayRelData> getPCRFPolicySyGatewayList(long pcrfID) throws DataManagerException {
		List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList = new LinkedList<PCRFServicePolicySyGatewayRelData>();
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicySyGatewayRelData.class).addOrder(Order.asc("orderNo"));
			if(pcrfID>0) {
				criteria.add(Restrictions.eq("pcrfPolicyId", pcrfID));
			}
			pcrfServicePolicySyGatewayRelDataList = (List<PCRFServicePolicySyGatewayRelData>)criteria.list();
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return pcrfServicePolicySyGatewayRelDataList;
	}	
	@Override
	public void update(PCRFServicePolicyData pcrfPolicyData) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCRFServicePolicyData.class);
			criteria.add(Restrictions.eq("pcrfPolicyId", pcrfPolicyData.getPcrfPolicyId()));
			
			delete(pcrfPolicyData);
			
	    	List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList = pcrfPolicyData.getPcrfPolicyCDRDriverRelDataList();
	    	for(PCRFPolicyCDRDriverRelData pcrfPolicyCDRDriverRelData : pcrfPolicyCDRDriverRelDataList) {
	    		pcrfPolicyCDRDriverRelData.setPcrfPolicyId(pcrfPolicyData.getPcrfPolicyId());
	    		session.save(pcrfPolicyCDRDriverRelData);
	    	}

	    	List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList  = pcrfPolicyData.getPcrfServicePolicySyGatewayRelDataList();
	    	for(PCRFServicePolicySyGatewayRelData pcrfServicePolicySyGatewayRelData : pcrfServicePolicySyGatewayRelDataList) {
	    		pcrfServicePolicySyGatewayRelData.setPcrfPolicyId(pcrfPolicyData.getPcrfPolicyId());
	    		session.save(pcrfServicePolicySyGatewayRelData);
	    	}
	    	
			PCRFServicePolicyData pcrfServicePolicyData = (PCRFServicePolicyData)criteria.uniqueResult();
			pcrfServicePolicyData.setName(pcrfPolicyData.getName());
			pcrfServicePolicyData.setDescription(pcrfPolicyData.getDescription());
			pcrfServicePolicyData.setRuleset(pcrfPolicyData.getRuleset());
			pcrfServicePolicyData.setAction(pcrfPolicyData.getAction());
			pcrfServicePolicyData.setSessionMgrEnabled(pcrfPolicyData.getSessionMgrEnabled());
			pcrfServicePolicyData.setUnknownUserAction(pcrfPolicyData.getUnknownUserAction());
			pcrfServicePolicyData.setPkgId(pcrfPolicyData.getPkgId());
			pcrfServicePolicyData.setStatus(pcrfPolicyData.getStatus());
			pcrfServicePolicyData.setSyMode(pcrfPolicyData.getSyMode());
			pcrfServicePolicyData.setIdentityAttribute(pcrfPolicyData.getIdentityAttribute());
			setUpdateAuditDetail(pcrfServicePolicyData);
			
			session.update(pcrfServicePolicyData);			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	public void delete(PCRFServicePolicyData pcrfPolicyData)throws DataManagerException {
		try{
			
			Session session = getSession();
			
			SQLQuery sqlQuery = session.createSQLQuery("delete from TBLMPCRFPOLICYCDRDRIVERREL where PCRFPOLICYID = " + pcrfPolicyData.getPcrfPolicyId());
			sqlQuery.executeUpdate();

			sqlQuery = session.createSQLQuery("delete from TBLMPCRFPOLICYGATEWAYREL where PCRFPOLICYID = " + pcrfPolicyData.getPcrfPolicyId());
			sqlQuery.executeUpdate();

		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}
	/*public void deletePCRFDriverData(Long pcrfPolicyId)throws DataManagerException {
		List<PCRFSPDriver> pcrfDriverList = null;
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(PCRFSPDriver.class);
			criteria.add(Restrictions.eq("pcrfPolicyId", pcrfPolicyId));
			
			pcrfDriverList = criteria.list();	
			
			for(PCRFSPDriver pcrfDriverData:pcrfDriverList){
				session.delete(pcrfDriverData);
			}
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}*/
	
	public void delete(List<Long> pcrfPolicyIdList, String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();			
			for(Long pcrfPolicyId:pcrfPolicyIdList){
				PCRFServicePolicyData pcrfPolicyData = (PCRFServicePolicyData) session.load(PCRFServicePolicyData.class, pcrfPolicyId);
				delete(pcrfPolicyData);
				session.delete(pcrfPolicyData);
			}			
			session.flush();
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	public void updateStatus(List<String> pcrfPolicyIds,String status) throws DataManagerException {
		Long pcrfPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;

		for(int i=0;i<pcrfPolicyIds.size();i++){
			pcrfPolicyId = Long.parseLong(pcrfPolicyIds.get(i));
			criteria = session.createCriteria(PCRFServicePolicyData.class);
			PCRFServicePolicyData pcrfPolicyInstData = (PCRFServicePolicyData)criteria.add(Restrictions.eq("pcrfPolicyId",pcrfPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(pcrfPolicyInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					int orderNumber = pcrfPolicyInstData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(PCRFServicePolicyData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Integer(orderNumber))); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(PCRFServicePolicyData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<PCRFServicePolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							pcrfPolicyInstData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			pcrfPolicyInstData.setStatus(status);			
			session.update(pcrfPolicyInstData);			
			session.flush();
		}
	}
}