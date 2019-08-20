package com.elitecore.elitesm.hibernate.rm.ippool;

import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.ippool.IPPoolDataManager;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolStatus;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HIPPoolDataManager extends HBaseDataManager implements IPPoolDataManager {

    private static final String IP_POOL_NAME = "name";
	private static final String IP_POOL_ID = "ipPoolId";
	private static final String MODULE = "IPPoolDataManager";

	@Override
	public List<IIPPoolData> getAllList() throws DataManagerException {
        List<IIPPoolData> ipPoolList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(IPPoolData.class);
            ipPoolList = criteria.list();

        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }
        return ipPoolList;
    } 

    @Override
    public List<IIPPoolData> getList() throws DataManagerException {
        List<IIPPoolData> ipPoolList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(IPPoolData.class);
    		criteria.add(Restrictions.eq("commonStatusId","CST01"));            
    		ipPoolList = criteria.list();

        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }
        return ipPoolList;
    } 

    @Override
    public List<IIPPoolData> getList(IIPPoolData iPPoolData) throws DataManagerException {
        List<IIPPoolData> ippoolList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(IPPoolData.class);

            if(iPPoolData != null){
        		//criteria.add(Restrictions.eq("commonStatusId","CST01"));
            	
        		if(Strings.isNullOrEmpty(iPPoolData.getIpPoolId()) == false){
//            		criteria.add(Restrictions.eq("iPPoolId",iPPoolData.getIpPoolId()));
        			criteria.add(Restrictions.eq(IP_POOL_ID,iPPoolData.getIpPoolId()));
            	}
            	
            	if(iPPoolData.getName() != null){
            		criteria.add(Restrictions.eq(IP_POOL_NAME,iPPoolData.getName()));
            	}

            	if(iPPoolData.getCommonStatusId() != null){
            		criteria.add(Restrictions.eq("commonStatusId",iPPoolData.getCommonStatusId()));
            	}

            	if(iPPoolData.getCreatedByStaffId() != null){
            		criteria.add(Restrictions.eq("createdByStaffId",iPPoolData.getCreatedByStaffId()));
            	}
            	
             }
            
            ippoolList = criteria.list();
        }catch(HibernateException hExp){
        	hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	exp.printStackTrace();
        	throw new DataManagerException(exp.getMessage(), exp);
        }
        return ippoolList;
    }
    
    @Override
    public List<IPPoolData> getIPPoolDetailList(IIPPoolDetailData iipPoolDetailData) throws DataManagerException{
    	 List<IPPoolData> ippoolList = null;
    	 try{
    		 Session session = getSession();
    		 Criteria criteria = session.createCriteria(IPPoolData.class);
    		 criteria.createAlias("ipPoolDetail","ipPoolDetailData");
    		 if(iipPoolDetailData != null){
    			 if( Strings.isNullOrEmpty(iipPoolDetailData.getIpPoolId()) == false){
    				 criteria.add(Restrictions.ne(IP_POOL_ID,iipPoolDetailData.getIpPoolId()));
    			 }	 
    			if(iipPoolDetailData.getIpAddress() !=null) {
    				 criteria.add(Restrictions.eq("ipPoolDetailData.ipAddress",iipPoolDetailData.getIpAddress()));
    			}
    			if(iipPoolDetailData.getNasIPAddress() != null) {
    				criteria.add(Restrictions.eq("nasIPAddress",iipPoolDetailData.getNasIPAddress()));
    			}
    		 }
    		 criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    		 ippoolList = criteria.list();
    	 }catch(HibernateException hExp){
    		 throw new DataManagerException(hExp.getMessage(), hExp);
    	 }catch(Exception exp){
    		 throw new DataManagerException(exp.getMessage(), exp);
    	 }
    	 return ippoolList;
    }
    
    
    @Override
    public PageList search(IIPPoolData iPPoolData, int pageNo,int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(IPPoolData.class);

			if ((iPPoolData.getName() != null && iPPoolData.getName().length() > 0)) {
					criteria.add(Restrictions.ilike(IP_POOL_NAME, iPPoolData.getName()));
			}

			if ((iPPoolData.getCommonStatusId() != null && iPPoolData.getCommonStatusId().length() > 0)) {
				criteria.add(Restrictions.ilike("commonStatusId", iPPoolData.getCommonStatusId()));
		    }

			if ((iPPoolData.getNasIPAddress() != null && iPPoolData.getNasIPAddress().length() > 0)) {
				criteria.add(Restrictions.ilike("nasIPAddress", iPPoolData.getNasIPAddress()));
			}

			int totalItems = criteria.list().size();
				criteria.setFirstResult((pageNo - 1) * pageSize);
			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}

			List<IPPoolData> ippoolList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0)
			totalPages-=1;
			
			pageList = new PageList(ippoolList, pageNo, totalPages,totalItems);

		}catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
    }
    
    
    @Override
    public void updateStatus(String ipPoolId, String commonStatus, Timestamp statusChangeDate,IStaffData staffData,String actionAlias) throws DataManagerException{
    	Session session = getSession();			
    	IPPoolData ipPoolData = null;
    	try{
    		Criteria criteria = session.createCriteria(IPPoolData.class);
    		ipPoolData = (IPPoolData)criteria.add(Restrictions.eq(IP_POOL_ID,ipPoolId))
    		.uniqueResult();
    		
    		IPPoolData ipData=new IPPoolData();
    		ipData.setIpPoolId(ipPoolId);
    		ipData.setCommonStatusId(commonStatus);
    		ipData.setStatusChangedDate(statusChangeDate);
    		
    		IPPoolStatus ipPoolStatusOldObj = new IPPoolStatus();
    		ipPoolStatusOldObj.setStatus(ipPoolData.getCommonStatusId());
    		
    		IPPoolStatus ipPoopStatusNewObj = new IPPoolStatus();
    		ipPoopStatusNewObj.setStatus(commonStatus);
    		
    		JSONArray jsonArray=ObjectDiffer.diff(ipPoolStatusOldObj,ipPoopStatusNewObj);    		
    		
    		ipPoolData.setCommonStatusId(commonStatus);
    		ipPoolData.setStatusChangedDate(statusChangeDate);
    		ipPoolData.setLastModifiedDate(statusChangeDate);
    		session.merge(ipPoolData);
    		session.flush();
    		
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
    	}catch(HibernateException hExp){
    		throw new DataManagerException(hExp.getMessage(), hExp);
    	}catch(Exception exp){
    		throw new DataManagerException(exp.getMessage(), exp);
    	}
    }
    
    
    @Override
	public String create(Object obj) throws DataManagerException {
    	IIPPoolData iPPoolData =  (IIPPoolData) obj;
		try {
			Session session = getSession();
			session.clear();

			// fetch next audit id
			String auditId= UUIDGenerator.generate();

			iPPoolData.setAuditUId(auditId);
			iPPoolData.setCheckValidate(true);
			session.save(iPPoolData);
			
			session.flush();
			session.clear();

			insertIPPoolDetails(iPPoolData, CREATE_IPPOOL_DETAIL);

		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + iPPoolData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE + iPPoolData.getName() + REASON + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + iPPoolData.getName() + REASON + exp.getMessage());
		}
		return iPPoolData.getName();
	}
    
    @Override
	public void update(IIPPoolData iPPoolData,IStaffData staffData,String actionAlias) throws DataManagerException{
    	Session session = getSession();
		try{
			IPPoolData oldIPPoolData = null;
			oldIPPoolData = (IPPoolData) session.get(IPPoolData.class, iPPoolData.getIpPoolId());
			
			iPPoolData.setCheckValidate(true);
			JSONArray jsonArray=ObjectDiffer.diff(oldIPPoolData,(IPPoolData)iPPoolData);
			
			oldIPPoolData.setAdditionalAttributes(iPPoolData.getAdditionalAttributes());
			oldIPPoolData.setCommonStatusId(iPPoolData.getCommonStatusId());
			oldIPPoolData.setEditable(iPPoolData.getEditable());
			oldIPPoolData.setName(iPPoolData.getName());
			oldIPPoolData.setNasIPAddress(iPPoolData.getNasIPAddress());
			
			session.merge(oldIPPoolData);
			session.flush();
			
			staffData.setAuditId(oldIPPoolData.getAuditUId());
			staffData.setAuditName(oldIPPoolData.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
    
    @Override
   	public void updateByName(IIPPoolData iPPoolData,IStaffData staffData,String actionAlias) throws DataManagerException{
       	Session session = getSession();
   		try{
   			
   			Criteria criteria = session.createCriteria(IPPoolData.class);
   			IPPoolData oldIPPoolData = (IPPoolData)criteria.add(Restrictions.eq(IP_POOL_ID,iPPoolData.getIpPoolId()))
    		.uniqueResult();
    		
   			iPPoolData.setCheckValidate(true);
   			JSONArray jsonArray=ObjectDiffer.diff(oldIPPoolData,(IPPoolData)iPPoolData);
   			
   			criteria = session.createCriteria(IPPoolDetailData.class);
   			List<IPPoolDetailData> ipPoolDetailDataList = (List<IPPoolDetailData>)criteria.add(Restrictions.eq(IP_POOL_ID,iPPoolData.getIpPoolId()))
   		    		.list();
   			deleteObjectList(ipPoolDetailDataList, session);
   			
   			oldIPPoolData.setAdditionalAttributes(iPPoolData.getAdditionalAttributes());
   			oldIPPoolData.setCommonStatusId(iPPoolData.getCommonStatusId());
   			oldIPPoolData.setEditable(iPPoolData.getEditable());
   			oldIPPoolData.setName(iPPoolData.getName());
   			oldIPPoolData.setNasIPAddress(iPPoolData.getNasIPAddress());
   			oldIPPoolData.setIpPoolDetail(iPPoolData.getIpPoolDetail());
   			session.update(oldIPPoolData);
   			session.flush();
   			
   			staffData.setAuditId(oldIPPoolData.getAuditUId());
   			staffData.setAuditName(oldIPPoolData.getName());
   			
   			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
   		}catch(HibernateException hExp){
   			hExp.printStackTrace();
   			throw new DataManagerException(hExp.getMessage(), hExp);
   		}catch(Exception exp){
   			throw new DataManagerException(exp.getMessage(), exp);
   		}
   	}
    
    @Override
	public void insertIPPoolDetails(IIPPoolData iPPoolData, long serialNumber)
			throws DataManagerException {
		try {
			if (!iPPoolData.getIpPoolDetail().isEmpty()) {
				Session session = getSession();
				if (serialNumber == UPDATE_IPPOOL_DETAIL) {
					Criteria criteria = session.createCriteria(IPPoolDetailData.class);
					criteria.add(Restrictions.eq(IP_POOL_ID, iPPoolData.getIpPoolId()));
					Long maxCount = (Long) criteria.setProjection(Projections.max("serialNumber")).uniqueResult();
					serialNumber = (maxCount != null ? maxCount : 0);
				}
				for (IIPPoolDetailData ipPoolDetailData : iPPoolData.getIpPoolDetail()) {
					ipPoolDetailData.setIpPoolId(iPPoolData.getIpPoolId());
					ipPoolDetailData.setSerialNumber(++serialNumber);
					session.save(ipPoolDetailData);
					if (serialNumber % 20 == 0) {
						session.flush();
						session.clear();
					}
				}
			}
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage());
		}
	}
    
    @Override
	public String deleteById(String ipPoolId) throws DataManagerException {
		return delete(IP_POOL_ID, ipPoolId);
	}

	@Override
	public String deleteByName(String ipPoolName) throws DataManagerException {
		return delete(IP_POOL_NAME, ipPoolName);
	}
	
	private String delete(String propertyName, Object value) throws DataManagerException {
		String ipPoolName = ((IP_POOL_NAME.equals(propertyName)) ? (String) value : "IP Pool");
		IPPoolData ipPoolData = null;
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(IPPoolData.class);
			criteria.add(Restrictions.eq(propertyName,value));
			ipPoolData = (IPPoolData) criteria.uniqueResult();
			
			
			//	IPPoolData ipPoolData = (IPPoolData) session.get(IPPoolData.class, propertyName);

				if(ipPoolData == null){
					throw new InvalidValueException("IP Pool Instance not Found");
				}

				/*delete child records*/
				deleteIPPoolDetailById(ipPoolData.getIpPoolId());
				/*delete IPPool instance*/
				session.delete(ipPoolData);
				session.flush();
			}catch (ConstraintViolationException cve){
				cve.printStackTrace();
				throw new DataManagerException("Failed to delete " + ipPoolName + ", Reason: " 
						+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			}catch(HibernateException hExp){
				hExp.printStackTrace();
				throw new DataManagerException("Failed to delete " + ipPoolName + ", Reason: " + hExp.getMessage(), hExp);
			}catch(Exception exp){
				exp.printStackTrace();
				throw new DataManagerException("Failed to delete " + ipPoolName + ", Reason: " + exp.getMessage(), exp);
			}
			return ipPoolName;
	}
    	
	@Override
    public void delete(String ipPoolId) throws DataManagerException{
    	try {
    		Session session = getSession();
			if (Strings.isNullOrEmpty(ipPoolId) == false) {
    			IPPoolData ipPoolData = (IPPoolData) session.get(IPPoolData.class, Long.valueOf(ipPoolId));
    			if(ipPoolData != null){
    				/*delete child records*/
    				deleteIPPoolDetailById(ipPoolId);
    				/*delete IPPool instance*/
		            session.delete(ipPoolData);
		        	session.flush();
    			}else{
            		throw new InvalidValueException("IP Pool Instance not Found");
           		}
       		}else{
        		throw new DataManagerException("Invalid IP Pool Id");
       		}

    	}catch (HibernateException hExp){
    		throw new DataManagerException(hExp.getMessage(), hExp);
    	}catch(Exception exp){
    		throw new DataManagerException(exp.getMessage(), exp);
    	}
    }
    
    @Override
    public void deleteIPPoolDetailById(String ipPoolId) throws DataManagerException{
    	deleteIPPoolDetail(ipPoolId, null, null);
    }
    
    @Override
    public void deleteIPPoolDetailByIPAddress(String ipPoolId, String ipAddress) throws DataManagerException{
    	deleteIPPoolDetail(ipPoolId, ipAddress, null);
    }
    
    @Override
    public void deleteIPPoolDetailByRange(String ipPoolId, String ipAddressRangeId) throws DataManagerException{
    	deleteIPPoolDetail(ipPoolId, null, ipAddressRangeId);
    }
    
    
    public void deleteIPPoolDetail(String ipPoolId, String ipAddress, String ipAddressRangeId) throws DataManagerException{
    	try {
    		StringBuilder deleteQryBuilder = new StringBuilder(32);
    		deleteQryBuilder.append("delete ")
    						.append(IPPoolDetailData.class.getSimpleName())
    						.append(" where ipPoolId =:ipPoolId");
    		if(ipAddress != null && !"".equals(ipAddress)){
    			deleteQryBuilder.append(" and ipAddress =:ipAddress");
    		}
    		if(ipAddressRangeId != null && !"".equals(ipAddressRangeId)){
    			deleteQryBuilder.append(" and ipAddressRangeId =:ipAddressRangeId");
    		}
    		Logger.logDebug(MODULE, "Delete HQL qry:"+deleteQryBuilder);
    		
    		Query delQuery = getSession().createQuery(deleteQryBuilder.toString());
    		delQuery.setParameter(IP_POOL_ID, ipPoolId);
    		if(ipAddress != null && !"".equals(ipAddress)){
    			delQuery.setParameter("ipAddress", ipAddress);
    		}
    		if(ipAddressRangeId != null && !"".equals(ipAddressRangeId)){
    			delQuery.setParameter("ipAddressRangeId", ipAddressRangeId);
    		}
    		int result = delQuery.executeUpdate();
    		Logger.logDebug(MODULE, result+ "Records Deleted successfully");
    	}catch (HibernateException hExp) {
    		hExp.printStackTrace();
    		throw new DataManagerException(hExp.getMessage(),hExp);
    	}catch(Exception exp){
    		throw new DataManagerException(exp.getMessage(), exp);
    	}
    }

	@Override
	public Integer getIPPoolCount(String ipAdress,String nasIPAddress, String ipPoolId) throws DataManagerException {
		try{
			Session  session = getSession();
			Criteria criteria = session.createCriteria(IPPoolData.class);
			criteria.createAlias("ipPoolDetail","ipPoolDetailData");
			criteria.add(Restrictions.eq("ipPoolDetailData.ipAddress", ipAdress));
			if(nasIPAddress != null) {
				criteria.add(Restrictions.eq("nasIPAddress", nasIPAddress));
			}
			if(ipPoolId != null) {
				criteria.add(Restrictions.ne(IP_POOL_ID, ipPoolId));
			}
			Long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
			return count.intValue();
		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(), e);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
				
		
	}

	@Override
	public List<IIPPoolDetailData> getIPPoolDetailList(IIPPoolDetailData ipPoolDetailData,int pageNo, int pageSize) throws DataManagerException{
		try{
			Session  session = getSession();
			Criteria criteria = session.createCriteria(IPPoolDetailData.class);
			if( Strings.isNullOrEmpty(ipPoolDetailData.getIpPoolId()) == false ) {
				criteria.add(Restrictions.eq(IP_POOL_ID, ipPoolDetailData.getIpPoolId()));
			}
			criteria.setFirstResult((pageNo - 1) * pageSize);
			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			if(ipPoolDetailData.getIpAddress() != null && ipPoolDetailData.getIpAddress().length()>0){
    			criteria.add(Restrictions.ilike("ipAddress",ipPoolDetailData.getIpAddress(),MatchMode.ANYWHERE));
    		}
			criteria.addOrder(Order.asc("serialNumber"));
			return criteria.list();
		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(), e);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<Object[]> getDistinctIPPoolDetailByRangeList(IIPPoolDetailData ipPoolDetailData) throws DataManagerException{
		try{
			Session  session = getSession();
			Criteria criteria = session.createCriteria(IPPoolDetailData.class);
			if( Strings.isNullOrEmpty(ipPoolDetailData.getIpPoolId()) == false ) {
				criteria.add(Restrictions.eq(IP_POOL_ID, ipPoolDetailData.getIpPoolId()));
			}
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.distinct(Projections.property("ipAddressRange")));
			projectionList.add(Projections.property("ipAddressRangeId"));
			criteria.setProjection(projectionList);
			criteria.addOrder(Order.asc("ipAddressRangeId"));
			return criteria.list();
		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(), e);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}
	
	@Override
	public Long getIPPoolDetailTotalCount(IIPPoolDetailData ipPoolDetailData) throws DataManagerException{
		try{
			Session  session = getSession();
			Criteria criteria = session.createCriteria(IPPoolDetailData.class);
			if( Strings.isNullOrEmpty(ipPoolDetailData.getIpPoolId()) == false) {
				criteria.add(Restrictions.eq(IP_POOL_ID, ipPoolDetailData.getIpPoolId()));
			}
			if(ipPoolDetailData.getIpAddress() != null && ipPoolDetailData.getIpAddress().length()>0){
    			criteria.add(Restrictions.ilike("ipAddress",ipPoolDetailData.getIpAddress(),MatchMode.ANYWHERE));
    		}
			
			Long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
			return count;
		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(), e);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}

	@Override
	public IPPoolData getIPPoolById(String ipPoolId) throws DataManagerException {
    	return getIPPool(IP_POOL_ID,ipPoolId);
	}
	
	@Override
	public IPPoolData getIPPoolByName(String ipPoolName) throws DataManagerException {
		return getIPPool(IP_POOL_NAME,ipPoolName);
	}
	
	private IPPoolData getIPPool(String propertyName,Object propertyValue) throws DataManagerException {
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(IPPoolData.class);
			IPPoolData ipPoolData = (IPPoolData)criteria.add(Restrictions.eq(propertyName,propertyValue)).uniqueResult();
			
			if(ipPoolData == null){
				throw new InvalidValueException("IPPool Data not found");
			}
			
			return ipPoolData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
}
