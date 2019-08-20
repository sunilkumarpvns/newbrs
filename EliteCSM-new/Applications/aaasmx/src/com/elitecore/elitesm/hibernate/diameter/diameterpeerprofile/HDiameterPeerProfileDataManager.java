/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.diameterpeerprofile;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.DiameterPeerProfileDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterPeerProfileDataManager extends HBaseDataManager implements DiameterPeerProfileDataManager{

	private static final String PEER_PROFILE_ID = "peerProfileId";
	private static final String PROFILE_NAME = "profileName";
	private static final String MODULE = "HDiameterPeerProfileDataManager";

	public PageList searchDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfileData,Map infoMap) throws DataManagerException {
		PageList pageList = null;
        int pageNo;
        int pageSize;
		try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
		    Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class);
			
           if((diameterPeerProfileData.getProfileName() != null && diameterPeerProfileData.getProfileName().trim().length()>0 )){
				criteria.add(Restrictions.ilike(PROFILE_NAME,diameterPeerProfileData.getProfileName(),MatchMode.ANYWHERE));
			} 
           criteria.addOrder(Order.asc(PROFILE_NAME)); 
           
    		List list = criteria.list();
			int totalItems = list.size();
			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);
			List<DiameterPeerProfileData> diameterPeerProfileList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterPeerProfileList, pageNo, totalPages ,totalItems);

		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer profile list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter peer profile list, Reason: " + e.getMessage(), e);
		}Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
	
		return pageList;
	}
	
	@Override
	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException {
		List<ServerCertificateData> lstServerCertificateData;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			criteria.addOrder(Order.asc("serverCertificateId"));
			lstServerCertificateData = criteria.list();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive server certificate list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive server certificate list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return lstServerCertificateData;  
	}
	
	@Override
	public String getPeerProfileIdByPeerProfileName(String peerProfileName) throws DataManagerException {
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class).add(Restrictions.eq(PROFILE_NAME, peerProfileName));
			DiameterPeerProfileData diameterPeerProfileData = (DiameterPeerProfileData) criteria.uniqueResult();
			
			if (diameterPeerProfileData == null) {
				throw new InvalidValueException("Diameter Peer Profile not found");
			}
			
			return diameterPeerProfileData.getPeerProfileId();
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Peer Profile Id " + peerProfileName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Peer Profile Id " + peerProfileName + ", Reason: " + e.getMessage(), e);
		}	
		
		
	}
	
	
	@Override
	public DiameterPeerProfileData getDiameterPeerProfileById(String DiameterPeerProfileId) throws DataManagerException {
		
		return getDiameterPeerProfile(PEER_PROFILE_ID, DiameterPeerProfileId);
		
	}

	@Override
	public DiameterPeerProfileData getDiameterPeerProfileByName(String DiameterPeerProfileName) throws DataManagerException {
		
		return getDiameterPeerProfile(PROFILE_NAME, DiameterPeerProfileName);
		
	}

	private DiameterPeerProfileData getDiameterPeerProfile(String propertyName, Object value) throws DataManagerException {
		
		String diameterPeerProfileName = (PROFILE_NAME.equals(propertyName)) ? (String) value : "Diameter Peer Profile";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerProfileData diameterPeerProfile = (DiameterPeerProfileData) criteria.uniqueResult();
			
			if (diameterPeerProfile == null) {
				throw new InvalidValueException("Diameter Peer Profile not found");
			}
			
			diameterPeerProfileName = diameterPeerProfile.getProfileName();
			
			return diameterPeerProfile;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPeerProfileName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPeerProfileName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String create(Object obj) throws DataManagerException {
		
		DiameterPeerProfileData diameterPeerProfile = (DiameterPeerProfileData) obj;
		String diameterPeerProfileName = diameterPeerProfile.getProfileName();
		
		try {
			
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();

			diameterPeerProfile.setAuditUId(auditId);

			session.save(diameterPeerProfile);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerProfileName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerProfileName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPeerProfileName + REASON + e.getMessage(), e);
		}
		return diameterPeerProfileName;
		
	}
	

	@Override
	public void updateDiameterPeerProfileById(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileId) throws DataManagerException {
		
		updateDiameterPeerProfile(diameterPeerProfile, staffData, PEER_PROFILE_ID, diameterPeerProfileId);
		
	}

	@Override
	public void updateDiameterPeerProfileByName(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileName) throws DataManagerException {
		
		updateDiameterPeerProfile(diameterPeerProfile, staffData, PROFILE_NAME, diameterPeerProfileName);
		
	}

	private void updateDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

		String diameterPeerProfileName = (PROFILE_NAME.equals(propertyName)) ? (String) value : "Diameter Peer Profile";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerProfileData diameterPeerProfileData = (DiameterPeerProfileData) criteria.uniqueResult();

			if (diameterPeerProfileData == null) {
				throw new InvalidValueException("Diameter Peer Profile not found");
			}

			diameterPeerProfileName = diameterPeerProfileData.getProfileName();

			JSONArray jsonArray = ObjectDiffer.diff(diameterPeerProfileData, diameterPeerProfile);
			
			diameterPeerProfileData.setProfileName(diameterPeerProfile.getProfileName());
			diameterPeerProfileData.setDescription(diameterPeerProfile.getDescription());
			diameterPeerProfileData.setExclusiveAuthAppIds(diameterPeerProfile.getExclusiveAuthAppIds());
			diameterPeerProfileData.setExclusiveAcctAppIds(diameterPeerProfile.getExclusiveAcctAppIds());
			diameterPeerProfileData.setTransportProtocol(diameterPeerProfile.getTransportProtocol());
			diameterPeerProfileData.setSocketReceiveBufferSize(diameterPeerProfile.getSocketReceiveBufferSize());
			diameterPeerProfileData.setRedirectHostAvpFormat(diameterPeerProfile.getRedirectHostAvpFormat());
			diameterPeerProfileData.setSocketSendBufferSize(diameterPeerProfile.getSocketSendBufferSize());
			diameterPeerProfileData.setTcpNagleAlgorithm(diameterPeerProfile.getTcpNagleAlgorithm());
			diameterPeerProfileData.setDwrDuration(diameterPeerProfile.getDwrDuration());
			diameterPeerProfileData.setInitConnectionDuration(diameterPeerProfile.getInitConnectionDuration());
			diameterPeerProfileData.setRetryCount(diameterPeerProfile.getRetryCount());
			diameterPeerProfileData.setSessionCleanUpCER(diameterPeerProfile.getSessionCleanUpCER());
			diameterPeerProfileData.setSessionCleanUpDPR(diameterPeerProfile.getSessionCleanUpDPR());
			diameterPeerProfileData.setCerAvps(diameterPeerProfile.getCerAvps());
			diameterPeerProfileData.setDprAvps(diameterPeerProfile.getDprAvps());
			diameterPeerProfileData.setDwrAvps(diameterPeerProfile.getDwrAvps());
			diameterPeerProfileData.setSendDPRCloseEvent(diameterPeerProfile.getSendDPRCloseEvent());
			diameterPeerProfileData.setLastModifiedDate(diameterPeerProfile.getLastModifiedDate());
			diameterPeerProfileData.setLastModifiedByStaffId(diameterPeerProfile.getLastModifiedByStaffId());
			diameterPeerProfileData.setFollowRedirection(diameterPeerProfile.getFollowRedirection());
			diameterPeerProfileData.setHotlinePolicy(diameterPeerProfile.getHotlinePolicy());
			diameterPeerProfileData.setMinTlsVersion(diameterPeerProfile.getMinTlsVersion());
			diameterPeerProfileData.setMaxTlsVersion(diameterPeerProfile.getMaxTlsVersion());
			diameterPeerProfileData.setServerCertificateId(diameterPeerProfile.getServerCertificateId());
			diameterPeerProfileData.setClientCertificateRequest(diameterPeerProfile.getClientCertificateRequest());
			diameterPeerProfileData.setCiphersuiteList(diameterPeerProfile.getCiphersuiteList());
			diameterPeerProfileData.setValidateCertificateExpiry(diameterPeerProfile.getValidateCertificateExpiry());
			diameterPeerProfileData.setValidateCertificateRevocation(diameterPeerProfile.getValidateCertificateRevocation());
			diameterPeerProfileData.setAllowCertificateCA(diameterPeerProfile.getAllowCertificateCA());
			diameterPeerProfileData.setValidateHost(diameterPeerProfile.getValidateHost());
			diameterPeerProfileData.setServerCertificateName(diameterPeerProfile.getServerCertificateName());
			diameterPeerProfileData.setSecurityStandard(diameterPeerProfile.getSecurityStandard());
			diameterPeerProfileData.setDhcpIPAddress(diameterPeerProfile.getDhcpIPAddress());
			diameterPeerProfileData.setHaIPAddress(diameterPeerProfile.getHaIPAddress());

			session.update(diameterPeerProfileData);
			session.flush();
			
			staffData.setAuditId(diameterPeerProfileData.getAuditUId());
			staffData.setAuditName(diameterPeerProfileData.getProfileName());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DIAMETER_PEER_PROFILE);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPeerProfileName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPeerProfileName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPeerProfileName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteDiameterPeerProfileById(String diameterPeerProfileId) throws DataManagerException {
		
		return deleteDiameterPeerProfile(PEER_PROFILE_ID, diameterPeerProfileId);

	}
	
	@Override
	public String deleteDiameterPeerProfileByName(String diameterPeerProfileName) throws DataManagerException {

		return deleteDiameterPeerProfile(PROFILE_NAME, diameterPeerProfileName);

	}

	private String deleteDiameterPeerProfile(String propertyName, Object value) throws DataManagerException {
		
		String diameterPeerProfileName = (PROFILE_NAME.equals(propertyName)) ? (String) value : "Diameter Peer Profile";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterPeerProfileData.class).add(Restrictions.eq(propertyName, value));
			DiameterPeerProfileData diameterPeerProfile = (DiameterPeerProfileData) criteria.uniqueResult();

			if (diameterPeerProfile == null) {
				throw new InvalidValueException("Diameter Peer Profile not found");
			}

			diameterPeerProfileName = diameterPeerProfile.getProfileName();
			
			session.delete(diameterPeerProfile);
			session.flush();

			return diameterPeerProfileName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerProfileName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerProfileName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPeerProfileName + ", Reason: " + e.getMessage(), e);
		} 

	}

}
