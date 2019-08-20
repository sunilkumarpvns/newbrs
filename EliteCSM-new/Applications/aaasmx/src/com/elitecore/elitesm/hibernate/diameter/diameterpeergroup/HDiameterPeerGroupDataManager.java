/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.diameterpeergroup;

import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.DiameterPeerGroupDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * @author nayana.rathod
 *
 */
public class HDiameterPeerGroupDataManager extends HBaseDataManager implements DiameterPeerGroupDataManager{

	private static final String DIAMETER_PEER_GROUP = "Diameter Peer Group";
	private static final String PEER_GROUP_NOT_FOUND_MESSAGE = "Diameter Peer Group not found";
	private static final String PEER_GROUP_ID = "peerGroupId";
	private static final String PEER_GROUP_NAME = "peerGroupName";
	private static final String MODULE = HDiameterPeerGroupDataManager.class.getSimpleName();
	@Override
	public PageList searchDiameterPeerGroupData( DiameterPeerGroup diameterPeerGroup, int requiredPageNo, Integer pageSize) throws DataManagerException {
		List<DiameterPeerGroup> pluginList = null;
		PageList pageList = null;
		
		try{		
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class);
			String peerGroupName=diameterPeerGroup.getPeerGroupName();

			if((peerGroupName != null && !"".equals(peerGroupName))){
				peerGroupName = "%"+peerGroupName+"%";
				criteria.add(Restrictions.ilike(PEER_GROUP_NAME,peerGroupName));
			}

			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			pluginList = criteria.list();

			long totalPages = totalItems/pageSize;
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(pluginList, requiredPageNo, totalPages ,totalItems);

		} catch (HibernateException hbe) {
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException("Failed to retrive Diameter Peer Group list, Reason : " + hbe.getMessage(), hbe);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException("Failed to retrive Diameter Peer Group list, Reason : " + e.getMessage(), e);
		}
		return pageList;
	}

	@Override
	public String create(Object obj) throws DataManagerException {
		DiameterPeerGroup diameterPeerGroup = (DiameterPeerGroup) obj;
		try{
			Session session = getSession();
			session.clear();
			
			/* Fetch Next AUDIT IT */
			String auditId= UUIDGenerator.generate();
			
			diameterPeerGroup.setAuditUId(auditId);
			
			List<DiameterPeerRelationWithPeerGroup> peerList = diameterPeerGroup.getPeerList();
			
			session.save(diameterPeerGroup);
			
			String peerGroupId = diameterPeerGroup.getPeerGroupId();
			
			// iterate peer list and set peer group id
			int orderNumber = 1;
			for (DiameterPeerRelationWithPeerGroup peerDetail : peerList) {
				peerDetail.setPeerGroupId(peerGroupId);
				peerDetail.setOrderNumber(orderNumber++);
				session.save(peerDetail);
			}
			
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerGroup.getPeerGroupName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerGroup.getPeerGroupName() + REASON + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerGroup.getPeerGroupName() + REASON + exp.getMessage(), exp);
		}
		return diameterPeerGroup.getPeerGroupName();
	}
	
	
	@Override
	public List<DiameterPeerGroup> getDiameterPeerGroupList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class);
			List<DiameterPeerGroup> diameterPeerGroupList = (List<DiameterPeerGroup>)criteria.list();
			return diameterPeerGroupList;
		} catch (HibernateException hbe) {
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException("Failed to retrive Diameter Peer Group list, Reason: " +hbe.getMessage(),hbe);
		}
	}


	@Override
	public String deleteByName(String diameterPeerGroupName) throws DataManagerException {
		return delete(PEER_GROUP_NAME,diameterPeerGroupName);
	}

	@Override
	public String deleteById(String diameterPeerGroupId) throws DataManagerException {
		return delete(PEER_GROUP_ID,diameterPeerGroupId);
		
	}
	
	private String delete(String propertyName, Object value) throws DataManagerException {
		String diameterPeerGroupName = (PEER_GROUP_NAME.equals(propertyName)) ? (String) value : DIAMETER_PEER_GROUP;
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class).add(Restrictions.eq(propertyName,value));
			DiameterPeerGroup diameterPeerGroupData = (DiameterPeerGroup)criteria.uniqueResult();
			
			if (diameterPeerGroupData == null) {
				throw new InvalidValueException(PEER_GROUP_NOT_FOUND_MESSAGE);
			}
			
			diameterPeerGroupName = diameterPeerGroupData.getPeerGroupName();
			session.delete(diameterPeerGroupData);
			session.flush();
		
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException("Failed to delete " + diameterPeerGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException("Failed to delete " + diameterPeerGroupName + ", Reason: " + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException("Failed to delete " + diameterPeerGroupName + ", Reason: " + exp.getMessage(), exp);
		}
		return diameterPeerGroupName;
	}

	
	@Override
	public void updateById(DiameterPeerGroup diameterPeerGroup, IStaffData staffData) throws DataManagerException {
		update(diameterPeerGroup,staffData,PEER_GROUP_ID,diameterPeerGroup.getPeerGroupId());
		
	}

	@Override
	public void updateByName(DiameterPeerGroup diameterPeerGroup, IStaffData staffData, String diameterPeerGroupName) throws DataManagerException {
		update(diameterPeerGroup,staffData,PEER_GROUP_NAME,diameterPeerGroupName);
		
	}

	private void update(DiameterPeerGroup diameterPeerGroup, IStaffData staffData, String propertyName, Object value) throws DataManagerException {
		String diameterPeerGroupName = (PEER_GROUP_NAME.equals(propertyName)) ? (String) value : DIAMETER_PEER_GROUP;
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class).add(Restrictions.eq(propertyName,value));
			DiameterPeerGroup diameterPeerGroupData = (DiameterPeerGroup)criteria.uniqueResult();
			
			if (diameterPeerGroupData == null) {
				throw new InvalidValueException(PEER_GROUP_NOT_FOUND_MESSAGE);
			}
			
			diameterPeerGroupName = diameterPeerGroupData.getPeerGroupName();
			
			JSONArray jsonArray=ObjectDiffer.diff(diameterPeerGroupData,diameterPeerGroup);
			
			diameterPeerGroupData.setPeerGroupName(diameterPeerGroup.getPeerGroupName());
			diameterPeerGroupData.setDescription(diameterPeerGroup.getDescription());
			diameterPeerGroupData.setStateful(diameterPeerGroup.getStateful());
			diameterPeerGroupData.setTransactionTimeout(diameterPeerGroup.getTransactionTimeout());
			diameterPeerGroupData.setGeoRedunduntGroup(diameterPeerGroup.getGeoRedunduntGroup());
			session.update(diameterPeerGroupData);
			session.flush();

			//delete peer list
			List<DiameterPeerRelationWithPeerGroup> oldPeerList = diameterPeerGroupData.getPeerList();
			for (DiameterPeerRelationWithPeerGroup peerDetail : oldPeerList) {
				session.delete(peerDetail);
			}
			
			session.flush();
			//add new peer
			List<DiameterPeerRelationWithPeerGroup> peerList = diameterPeerGroup.getPeerList();
			String peerGroupId = diameterPeerGroupData.getPeerGroupId();
			int orderNumber = 1;
			for (DiameterPeerRelationWithPeerGroup peerDetail : peerList) {
				peerDetail.setPeerGroupId(peerGroupId);
				peerDetail.setOrderNumber(orderNumber++);
				session.save(peerDetail);
				session.flush();
			}
			//Audit Parameters
			staffData.setAuditName(diameterPeerGroupData.getPeerGroupName());
			staffData.setAuditId(diameterPeerGroupData.getAuditUId());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DIAMETER_PEER_GROUP);
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException("Failed to update " + diameterPeerGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException he){
			Logger.logTrace(MODULE, he);
			throw new DataManagerException("Failed to update " + diameterPeerGroupName + ", Reason: " + he.getMessage(), he);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException("Failed to update " + diameterPeerGroupName + ", Reason: " + exp.getMessage(), exp);
		}
	}
	

	@Override
	public DiameterPeerGroup getDiameterPeerGroupDataById(String diameterPeerGroupId) throws DataManagerException {
		return getDiameterPeerGroupDataByIdOrName(PEER_GROUP_ID,diameterPeerGroupId);
	}

	@Override
	public DiameterPeerGroup getDiameterPeerGroupDataByName(String diameterPeerGroupName) throws DataManagerException {
		return getDiameterPeerGroupDataByIdOrName(PEER_GROUP_NAME,diameterPeerGroupName);
	}

	private DiameterPeerGroup getDiameterPeerGroupDataByIdOrName(String propertyName, Object value) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerGroup diameterPeerGroupData = (DiameterPeerGroup) criteria.uniqueResult();
			
			if (diameterPeerGroupData == null) {
				throw new InvalidValueException(PEER_GROUP_NOT_FOUND_MESSAGE);
			}
			
			return diameterPeerGroupData;
		} catch (HibernateException e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException("Failed to retrive Diameter Peer Group, Reason : " + e.getMessage(), e);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException("Failed to retrive Diameter Peer Group, Reason : " + exp.getMessage(), exp);
		}
	}

	@Override
	public List<DiameterPeerGroup> getDiameterPeerGroupListExceptSelf(String peerGroupId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerGroup.class);
			List<DiameterPeerGroup> diameterPeerGroupList = (List<DiameterPeerGroup>)criteria.add(Restrictions.ne("peerGroupId", peerGroupId)).list();
			return diameterPeerGroupList;
		} catch (HibernateException hbe) {
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException("Failed to retrive Diameter Peer Group list, Reason: " +hbe.getMessage(),hbe);
		}
	}
}
