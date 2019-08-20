/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.diameterpeer;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.DiameterPeerDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterPeerDataManager extends HBaseDataManager implements DiameterPeerDataManager{


	private static final String NAME = "name";
	private static final String PEER_ID = "peerUUID";
	private static final String MODULE = "HDiameterPeerDataManager";

	public PageList search(DiameterPeerData diameterPeerData,Map infoMap) throws DataManagerException {
		PageList pageList = null; 
        int pageNo;
        int pageSize;
        try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerData.class);
			
			if(diameterPeerData.getName() !=null && diameterPeerData.getName().trim().length()>0) {
				criteria.add(Restrictions.ilike(NAME,diameterPeerData.getName(),MatchMode.ANYWHERE));
			}
			
           if((diameterPeerData.getHostIdentity() != null && diameterPeerData.getHostIdentity().trim().length()>0 )){
        	   Criterion cr1 = Restrictions.ilike("hostIdentity",diameterPeerData.getHostIdentity(),MatchMode.ANYWHERE);
        	   Criterion cr2 = Restrictions.ilike("remoteAddress",diameterPeerData.getRemoteAddress(),MatchMode.ANYWHERE);
        	   Criterion orCondition =Restrictions.disjunction().add(cr1).add(cr2);
        	   criteria.add(orCondition);
			} 
           if(Strings.isNullOrBlank(diameterPeerData.getPeerProfileId()) == false && "0".equals(diameterPeerData.getPeerProfileId()) == false){
				criteria.add(Restrictions.eq("peerProfileId",diameterPeerData.getPeerProfileId()));
			}
            criteria.addOrder(Order.asc("hostIdentity"));
           
    		List list = criteria.list();
			int totalItems = list.size();
			criteria.setFirstResult(((pageNo-1) * pageSize));
		    criteria.setMaxResults(pageSize);
			
			List<DiameterPeerData> diameterPeerList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterPeerList, pageNo, totalPages ,totalItems);

		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	@Override
	public List<DiameterPeerData> getDiameterPeerList() throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerData.class);
			List<DiameterPeerData> diameterPeersList = (List<DiameterPeerData>)criteria.list();
			return diameterPeersList;
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer list, Reason: " + e.getMessage(), e);
		}
	}

	
	public List<DiameterPeerProfileData> getPeerProfileList() throws DataManagerException {
		
		List<DiameterPeerProfileData> lstDiameterPeerProfileList;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class);
			criteria.addOrder(Order.asc("profileName"));
			lstDiameterPeerProfileList = criteria.list();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter profile list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter profile list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return lstDiameterPeerProfileList;  
	}
	
	
	@Override
	public String getDiameterPeerNameById(String peerId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerData.class);
			DiameterPeerData diameterPeerData = (DiameterPeerData)criteria.add(Restrictions.eq(PEER_ID,peerId)).uniqueResult();
			if(diameterPeerData == null){
				throw new InvalidValueException("Diameter Peer not found");
			}
			return diameterPeerData.getName();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer name, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer name, Reason: " + e.getMessage(), e);
		}
	}

	
	@Override
	public String getDiameterPeerIdByName(String peerName) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerData.class).add(Restrictions.eq(NAME,peerName));
			DiameterPeerData diameterPeerData = (DiameterPeerData)criteria.uniqueResult();
			if(diameterPeerData == null){
				throw new InvalidValueException("Diameter Peer not found");
			}
			return diameterPeerData.getPeerUUID();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer id, Reason: " + he.getMessage(), he.getCause());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer id, Reason: " + e.getMessage(), e.getCause());
		}
	}
	

	@Override
	public DiameterPeerData getDiameterPeerById(String diameterPeerId) throws DataManagerException {
		
		return getDiameterPeer(PEER_ID, diameterPeerId);
		
	}

	@Override
	public DiameterPeerData getDiameterPeerByName(String diameterPeerName) throws DataManagerException {
		
		return getDiameterPeer(NAME, diameterPeerName);
		
	}

	private DiameterPeerData getDiameterPeer(String propertyName, Object value) throws DataManagerException {
		
		String diameterPeerName = (NAME.equals(propertyName)) ? (String) value : "Diameter Peer";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPeerData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerData diameterPeer = (DiameterPeerData) criteria.uniqueResult();
			
			if (diameterPeer == null) {
				throw new InvalidValueException("Diameter Peer not found");
			}
			
			diameterPeerName = diameterPeer.getName();
			
			return diameterPeer;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPeerName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPeerName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String create(Object obj) throws DataManagerException {
		
		DiameterPeerData diameterPeer = (DiameterPeerData) obj;
		String diameterPeerName = diameterPeer.getName();
		
		try {
			
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			
			Criteria criteria = session.createCriteria(DiameterPeerData.class).setProjection(Projections.max("peerId")); 
			
			Long  maxOrderNumber = (Long) criteria.uniqueResult();
	
			if( maxOrderNumber != null && maxOrderNumber > 0){
				diameterPeer.setPeerId(++maxOrderNumber);
			} else {
				diameterPeer.setPeerId(1L);
			}
			
			diameterPeer.setAuditUId(auditId);

			session.save(diameterPeer);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			String errorMessage;

			if (cve.getConstraintName().contains("UK1_MPEER")) {

				errorMessage = "Duplicate Host Identity found";

			} else {

				errorMessage = EliteExceptionUtils.extractConstraintName(cve.getSQLException());

			}
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerName + REASON + errorMessage, cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerName + REASON + e.getMessage(), e);
		}
		return diameterPeerName;
		
	}
	

	@Override
	public void updateDiameterPeerById(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerId) throws DataManagerException {
		
		updateDiameterPeer(diameterPeer, staffData, PEER_ID, diameterPeerId);
		
	}

	@Override
	public void updateDiameterPeerByName(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerName) throws DataManagerException {
		
		updateDiameterPeer(diameterPeer, staffData, NAME, diameterPeerName);
		
	}

	private void updateDiameterPeer(DiameterPeerData diameterPeer, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

		String diameterPeerName = (NAME.equals(propertyName)) ? (String) value : "Diameter Peer";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterPeerData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerData diameterPeerData = (DiameterPeerData) criteria.uniqueResult();

			if (diameterPeerData == null) {
				throw new InvalidValueException("Diameter Peer not found");
			}

			diameterPeerName = diameterPeerData.getName();

			JSONArray jsonArray = ObjectDiffer.diff(diameterPeerData, diameterPeer);

			diameterPeerData.setHostIdentity(diameterPeer.getHostIdentity());
			diameterPeerData.setName(diameterPeer.getName());
			diameterPeerData.setRealmName(diameterPeer.getRealmName());
			diameterPeerData.setRemoteAddress(diameterPeer.getRemoteAddress());
			diameterPeerData.setDiameterURIFormat(diameterPeer.getDiameterURIFormat());
			diameterPeerData.setLocalAddress(diameterPeer.getLocalAddress());
			diameterPeerData.setPeerProfileId(diameterPeer.getPeerProfileId());
			diameterPeerData.setLastModifiedDate(diameterPeer.getLastModifiedDate());
			diameterPeerData.setLastModifiedByStaffId(diameterPeer.getLastModifiedByStaffId());
			diameterPeerData.setRequestTimeout(diameterPeer.getRequestTimeout());
			diameterPeerData.setRetransmissionCount(diameterPeer.getRetransmissionCount());
			diameterPeerData.setSecondaryPeerName(diameterPeer.getSecondaryPeerName());

			session.update(diameterPeerData);
			session.flush();
			
			staffData.setAuditId(diameterPeerData.getAuditUId());
			staffData.setAuditName(diameterPeerData.getName());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DIAMETER_PEER);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();

			String errorMessage;

			if (cve.getConstraintName().contains("UK1_MPEER")) {

				errorMessage = "Duplicate Host Identity found";

			} else {

				errorMessage = EliteExceptionUtils.extractConstraintName(cve.getSQLException());

			}

			throw new DataManagerException("Failed to update " + diameterPeerName + ", Reason: " + errorMessage, cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPeerName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPeerName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteDiameterPeerById(String diameterPeerId) throws DataManagerException {
		
		return deleteDiameterPeer(PEER_ID, diameterPeerId);

	}
	
	@Override
	public String deleteDiameterPeerByName(String diameterPeerName) throws DataManagerException {

		return deleteDiameterPeer(NAME, diameterPeerName);

	}

	private String deleteDiameterPeer(String propertyName, Object value) throws DataManagerException {
		
		String diameterPeerName = (NAME.equals(propertyName)) ? (String) value : "Diameter Peer";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterPeerData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerData diameterPeer = (DiameterPeerData) criteria.uniqueResult();

			if (diameterPeer == null) {
				throw new InvalidValueException("Diameter Peer not found");
			}

			diameterPeerName = diameterPeer.getName();
			
			session.delete(diameterPeer);
			session.flush();

			return diameterPeerName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerName + ", Reason: " + e.getMessage(), e);
		} 

	}

	
}
