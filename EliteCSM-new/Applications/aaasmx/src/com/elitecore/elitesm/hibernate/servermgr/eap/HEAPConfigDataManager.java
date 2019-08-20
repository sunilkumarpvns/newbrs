
package com.elitecore.elitesm.hibernate.servermgr.eap;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.EAPConfigDataManager;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPSimAkaConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IEAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IVendorSpecificCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.VendorSpecificCertificateData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;


public class HEAPConfigDataManager extends HBaseDataManager implements EAPConfigDataManager {

	private static final String EAP_CONF_NAME = "name";
	private static final String EAP_AUTH_TYPE = "eapAuthType";
	private static final String EAPTLS_ID = "eaptlsId";
	private static final String EAP_CONFIG_ID = "eapId";
	private final static String MODULE = "HEAPConfigDataManager";

	@Override
	public String create(Object obj) throws DataManagerException {
		
		EAPConfigData eapConfigData = (EAPConfigData) obj;
		EliteAssert.notNull(eapConfigData,"EAP Configuration  must not be null.");
		try {
			// entry into tblmeapconfiguration
			Session session=getSession();
			session.clear();
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			eapConfigData.setAuditUId(auditId);
			
			//Save EAP Config data
			session.save(eapConfigData);
			session.flush();
			session.clear();
			
			List<String> enableAuthMethodList = Arrays.asList(eapConfigData.getEnabledAuthMethods().split(","));
			// entry into tblmeaptlsconfig data 
			if (eapConfigData.getEaptlsConfigData() != null && enableAuthMethodList.contains(EAPConfigConstant.TLS_STR) || enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) 
					|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR) ) {

				EAPTLSConfigData eaptlsConfigData = new EAPTLSConfigData();
				eaptlsConfigData = eapConfigData.getEaptlsConfigData();

				eaptlsConfigData.setEapId(eapConfigData.getEapId());
				session.save(eaptlsConfigData);
				session.flush();
				session.clear();
				// entry into tblmvendorspecificcertificate;

				List<VendorSpecificCertificateData> vendorspecificCertiList = eaptlsConfigData.getVendorSpecificList();

				if (Collectionz.isNullOrEmpty(vendorspecificCertiList) == false) {
					int vendorCertificateListSize = vendorspecificCertiList.size();
					Integer orderNo = 1;
					for(int index=0;index<vendorCertificateListSize;index++){
						
						IVendorSpecificCertificateData vendorSpecificCertificateData = vendorspecificCertiList.get(index);
						vendorSpecificCertificateData.setEaptlsId(eaptlsConfigData.getEaptlsId());
						vendorSpecificCertificateData.setOrderNumber(orderNo);
						session.save(vendorSpecificCertificateData);
						orderNo ++;
						
					}
				}

				session.flush();
				session.clear();
			}

			if (enableAuthMethodList.contains(EAPConfigConstant.SIM_STR) && eapConfigData.getSimConfigData() != null) {
				EAPSimAkaConfigData configData = eapConfigData.getSimConfigData();
				configData.setEapId(eapConfigData.getEapId());
				session.save(configData);
				session.flush();
				session.clear();
			}
			
			if (enableAuthMethodList.contains(EAPConfigConstant.AKA_STR) && eapConfigData.getAkaConfigData() != null) {
					EAPSimAkaConfigData configData = eapConfigData.getAkaConfigData();
					configData.setEapId(eapConfigData.getEapId());
					session.save(configData);
					session.flush();
					session.clear();
			}
			
			if (enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR) && eapConfigData.getAkaPrimeConfigData() != null) {
				EAPSimAkaConfigData configData = eapConfigData.getAkaPrimeConfigData();
				configData.setEapId(eapConfigData.getEapId());
				session.save(configData);
				session.flush();
				session.clear();
			}
			
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE +eapConfigData.getName()+ REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE +eapConfigData.getName()+ REASON +hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE +eapConfigData.getName()+ REASON + exp.getMessage(),exp);
		}
		return eapConfigData.getName();

	}


	public void defaultEAPConfigCreate(IEAPConfigData eapConfigData) throws DataManagerException {
		
		EliteAssert.notNull(eapConfigData,"EAP Configuration  must not be null.");
		try{
			Session session=getSession();
			session.save(eapConfigData);
			session.flush();

		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create Default EAP Configuration, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create Default EAP Configuration, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to create Default EAP Configuration, Reason: "+exp.getMessage(),exp);
		}

	}

	public List<EAPConfigData> getEAPConfigList() throws DataManagerException {
		
		List<EAPConfigData> eapConfigList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPConfigData.class);
			eapConfigList = criteria.list();

		}catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of EAP Configurations, Reason: "+hExp.getMessage(), hExp);
		}

		return eapConfigList;
	}

	@Override
	public EAPConfigData getEAPConfigDataByName(String eapConfigName)
			throws DataManagerException {
		return getEAPConfigData(EAP_CONF_NAME, eapConfigName);
	}
	
	@Override
	public EAPConfigData getEAPConfigDataById(String eapId)throws DataManagerException {
		return getEAPConfigData(EAP_CONFIG_ID, eapId);
	}

	private EAPConfigData getEAPConfigData(String eapProperty,Object value) throws DataManagerException {
		EAPConfigData eapConfigData=null;

		try {

			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPConfigData.class).add(Restrictions.eq(eapProperty, value));
			eapConfigData = (EAPConfigData)criteria.uniqueResult();

			if (eapConfigData == null) {
				throw new InvalidValueException("EAP Configuration not found");
			}
			
			eapConfigData = getEAPConfigDetailWithAllChildConfiguration(eapConfigData, session);
			
		}catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP Configuration, Reason: "+hExp.getMessage(), hExp);
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP Configuration, Reason: "+e.getMessage(), e);
		}
		
		return eapConfigData;
	}
	
	private EAPConfigData getEAPConfigDetailWithAllChildConfiguration(EAPConfigData eapConfigData, Session session){
		
		try {
			//Get TLSConfiugration data based on eapId
			Criteria tlsCriteria  = session.createCriteria(EAPTLSConfigData.class);
			tlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId()));
			EAPTLSConfigData eapTlsConfigData =(EAPTLSConfigData)tlsCriteria.uniqueResult();	

			if(eapTlsConfigData != null){

				//Get vendor specific certificate data
				Criteria vendorSpecificCriteria = session.createCriteria(VendorSpecificCertificateData.class);
				vendorSpecificCriteria.add(Restrictions.eq(EAPTLS_ID,eapTlsConfigData.getEaptlsId()));
				List<VendorSpecificCertificateData> vendorSpecificCertList = vendorSpecificCriteria.list();

				eapTlsConfigData.setVendorSpecificList(vendorSpecificCertList);
				eapConfigData.setEaptlsConfigData(eapTlsConfigData);
			}
			
			/* get EAP Sim Config Data */
			Criteria simCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
			simCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.SIM));
			List<EAPSimAkaConfigData> simConfigList = simCriteria.list();
			EAPSimAkaConfigData simConfigData = null;
			
			if(Collectionz.isNullOrEmpty(simConfigList) == false){
				simConfigData =	simConfigList.get(0);	
				int simConfigLstSize = simConfigList.size();
				if(simConfigLstSize>1){
					//if more than one SIM configuration found for same EAP configuration than keep only one 
					session.beginTransaction();
					for (int i = 1; i < simConfigLstSize; i++) {
						session.delete(simConfigList.get(i));	
					}
					session.getTransaction().commit();
				}
			}
			eapConfigData.setSimConfigData(simConfigData);
			
			/* get EAP AKA Config Data */
			Criteria akaCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
			akaCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA));
			List<EAPSimAkaConfigData> akaConfigList = akaCriteria.list();
			EAPSimAkaConfigData akaConfigData = null;
			
			if(Collectionz.isNullOrEmpty(akaConfigList) == false){
				akaConfigData =	akaConfigList.get(0);
				//if more than one AKA configuration found for same EAP configuration than keep only one
				int akaConfigLstSize = akaConfigList.size();
				if(akaConfigLstSize>1){
					session.beginTransaction();
					for (int i = 1; i < akaConfigLstSize; i++) {
						session.delete(akaConfigList.get(i));	
					}
					session.getTransaction().commit();
				}
			}
			
			eapConfigData.setAkaConfigData(akaConfigData);
			
			/* get EAP AKA Prime Config Data */
			Criteria akaPrimeCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
			akaPrimeCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA_PRIME));
			List<EAPSimAkaConfigData> akaPrimeConfigList = akaPrimeCriteria.list();
			EAPSimAkaConfigData akaPrimeConfigData = null;
			
			if(Collectionz.isNullOrEmpty(akaPrimeConfigList) == false){
				akaPrimeConfigData = akaPrimeConfigList.get(0);
				//if more than one AKA PRIME configuration found for same EAP configuration than keep only one
				int akaPrimeConfigLstSize = akaPrimeConfigList.size();
				if(akaPrimeConfigLstSize>1){
					session.beginTransaction();
					for (int i = 1; i < akaPrimeConfigLstSize; i++) {
						session.delete(akaPrimeConfigList.get(i));	
					}
					session.getTransaction().commit();
				}
			}
			
			eapConfigData.setAkaPrimeConfigData(akaPrimeConfigData);

		} catch (HibernateException hbe) {
			throw hbe;
		}
		
		return eapConfigData;
	}
	
	@Override
	public String deleteByName(String eapConfigName) throws DataManagerException {
		return delete(EAP_CONF_NAME, eapConfigName);
	}
	
	@Override
	public String deleteById(String eapId) throws DataManagerException {
		return delete(EAP_CONFIG_ID, eapId);
	}

	private String delete(String propertyName, Object value) throws DataManagerException{
		String eapConfigName = (EAP_CONF_NAME.equals(propertyName)) ? (String) value : "EAP Configuration";
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPConfigData.class);
			criteria.add(Restrictions.eq(propertyName,value));

			EAPConfigData eapConfigData = (EAPConfigData)criteria.uniqueResult();

			if (eapConfigData == null) {
				throw new InvalidValueException("EAP Configuration not found");
			}
			
			String eapConfigId = eapConfigData.getEapId();
			
			//Get TLS configuration based on eapId
			Criteria tlsCriteria =session.createCriteria(EAPTLSConfigData.class);
			tlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigId)); 
			EAPTLSConfigData eaptlsConfigdata=(EAPTLSConfigData)tlsCriteria.uniqueResult();

			if (eaptlsConfigdata != null) {

				Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
				vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,eaptlsConfigdata.getEaptlsId()));
				List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();
				
				//Delete vendor specific certificate data for corresponding TLS configuration 
				for (VendorSpecificCertificateData vendorSpecificCertificateData : oldVendorSpecificCertList) {
					session.delete(vendorSpecificCertificateData);
				}
				session.flush();
				session.delete(eaptlsConfigdata);
			}
			
			Criteria akaSimCriteria =session.createCriteria(EAPSimAkaConfigData.class);
			akaSimCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigId)); 
			List list = (List)akaSimCriteria.list();
			//Delete all SIM,AKA,AKA' configuration
			deleteObjectList(list, session);
			
			//delete EAP configuration
			session.delete(eapConfigData);
			
			eapConfigName = eapConfigData.getName();
			return eapConfigName;
		}catch (ConstraintViolationException ce) {
			ce.printStackTrace();
			throw new DataManagerException("Failed to delete "+eapConfigName+", Reason: " + EliteExceptionUtils.extractConstraintName(ce.getSQLException()), ce);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete "+eapConfigName+", Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+eapConfigName+", Reason: "+exp.getMessage(), exp);
		}
	}

	public PageList search(EAPConfigData eapConfigData,	String[] enableAuthMethodarry, int requiredPageNo, Integer pageSize) throws DataManagerException {

		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria nameCriteria = session.createCriteria(EAPConfigData.class);

			if((eapConfigData.getName() != null && eapConfigData.getName().length()>0 )){
				String name="%"+eapConfigData.getName()+"%";
				nameCriteria.add(Restrictions.ilike(EAP_CONF_NAME,name));
			}
			List<EAPConfigData> eapConfigList=null;

			Disjunction disjunction =Restrictions.disjunction();
			for(int i=0;i<enableAuthMethodarry.length;i++){
				if(!("0".equalsIgnoreCase(enableAuthMethodarry[i]))){
					disjunction.add(Restrictions.ilike("enabledAuthMethods","%"+enableAuthMethodarry[i]+"%"));
				}
			}

			nameCriteria.add(disjunction);

			int totalItems = nameCriteria.list().size();
			nameCriteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				nameCriteria.setMaxResults(pageSize);
			}
			eapConfigList = nameCriteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(eapConfigList, requiredPageNo, totalPages ,totalItems);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of EAP Configuration during pagging, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of EAP Configuration during pagging, Reason: "+exp.getMessage(), exp);
		}
		
		return pageList;

	}


	public void updateEapBasicDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException{

		Session session = getSession();
		EAPConfigData configData = null;

		try {
			if (eapConfigData != null) {
				EliteAssert.notNull(eapConfigData,"EAP Configuration Data must not be null.");

				Criteria criteria = session.createCriteria(EAPConfigData.class);
				criteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId()));

				configData=(EAPConfigData)criteria.uniqueResult();
				
				if (configData != null) {
					configData = getEAPConfigDetailWithAllChildConfiguration(configData, session);
				}
				
				JSONArray jsonArray=ObjectDiffer.diff(configData,eapConfigData);
				
				configData.setName(eapConfigData.getName());
				configData.setDescription(eapConfigData.getDescription());
				configData.setSessionCleanupInterval(eapConfigData.getSessionCleanupInterval());
				configData.setSessionDurationForCleanup(eapConfigData.getSessionDurationForCleanup());
				configData.setSessionTimeout(eapConfigData.getSessionTimeout());
				configData.setMskRevalidationTime(eapConfigData.getMskRevalidationTime());
				configData.setTreatInvalidPacketAsFatal(eapConfigData.getTreatInvalidPacketAsFatal());
				configData.setNotificationSuccess(eapConfigData.getNotificationSuccess());
				configData.setNotificationFailure(eapConfigData.getNotificationFailure());
				configData.setMaxEapPacketSize(eapConfigData.getMaxEapPacketSize());
				configData.setLastModifiedByStaffId(eapConfigData.getLastModifiedByStaffId());
				configData.setLastModifiedDate(eapConfigData.getLastModifiedDate());
				
				if (configData.getAuditUId() == null) {
					String auditId= UUIDGenerator.generate();
					configData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				
				session.update(configData);

				doAuditingJson(jsonArray.toString(),staffData,actionAlias);

			}
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of EAP Configuration, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of EAP Configuration, Reason: " +hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of EAP Configuration, Reason: " +exp.getMessage(), exp);
		}
	
	}


	public void updateEapTLSDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException {

		Session session = getSession();
		EAPConfigData configData = null;
		
		try{
			if (eapConfigData != null) {
				
				EliteAssert.notNull(eapConfigData,"EAP Configuration Data must not be null.");
				// update in eapconfigdata
				Criteria criteria = session.createCriteria(EAPConfigData.class);
				criteria.add(Restrictions.eq(EAP_CONFIG_ID, eapConfigData.getEapId()));
				configData=(EAPConfigData)criteria.uniqueResult();
				
				if (configData != null) {
					configData = getEAPConfigDetailWithAllChildConfiguration(configData, session);
				}
				
				JSONArray jsonArray=ObjectDiffer.diff(configData,eapConfigData);
				
				configData.setLastModifiedDate(eapConfigData.getLastModifiedDate());
				configData.setLastModifiedByStaffId(eapConfigData.getLastModifiedByStaffId());
				Logger.logDebug("module",eapConfigData.getEapTtlsCertificateRequest());
				configData.setEapTtlsCertificateRequest(eapConfigData.getEapTtlsCertificateRequest());
				configData.setEapPeapCertificateRequest(eapConfigData.getEapPeapCertificateRequest());
				configData.setPeapVersion(eapConfigData.getPeapVersion());
				configData.setPeapNegotiationMethod(eapConfigData.getPeapNegotiationMethod());
				configData.setTtlsNegotiationMethod(eapConfigData.getTtlsNegotiationMethod());
				
				
				if(configData.getAuditUId() == null){
					String auditId= UUIDGenerator.generate();
					configData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				//eapConfigData.getEaptlsConfigData().setVendorSpecificSet(null);
				session.update(configData);

				//update or Save EAP TLS/TTLS Configuration...

				Criteria ttlsCriteria=session.createCriteria(EAPTLSConfigData.class);
				ttlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId()));
				EAPTLSConfigData eaptlsConfigData = (EAPTLSConfigData)ttlsCriteria.uniqueResult();
				
				EAPTLSConfigData updateData=eapConfigData.getEaptlsConfigData();
				if(eaptlsConfigData != null){

					eaptlsConfigData.setEapId(updateData.getEapId());
					//eaptlsConfigData.setEaptlsId(updateData.getEaptlsId());
					eaptlsConfigData.setMinTlsVersion(updateData.getMinTlsVersion());
					eaptlsConfigData.setMaxTlsVersion(updateData.getMaxTlsVersion());
					eaptlsConfigData.setCertificateRequest(updateData.getCertificateRequest());
					eaptlsConfigData.setServerCertificateId(updateData.getServerCertificateId());
					eaptlsConfigData.setSessionResumptionLimit(updateData.getSessionResumptionLimit());
					eaptlsConfigData.setSessionResumptionDuration(updateData.getSessionResumptionDuration());
					eaptlsConfigData.setDefaultCompressionMethod(updateData.getDefaultCompressionMethod());
					eaptlsConfigData.setCertificateTypesList(updateData.getCertificateTypesList());
					eaptlsConfigData.setCiphersuiteList(updateData.getCiphersuiteList());
					eaptlsConfigData.setExpiryDate(updateData.getExpiryDate());
					eaptlsConfigData.setRevokedCertificate(updateData.getRevokedCertificate());
					eaptlsConfigData.setMissingClientCertificate(updateData.getMissingClientCertificate());
					eaptlsConfigData.setMacValidation(updateData.getMacValidation());
					//eaptlsConfigData.setVendorSpecificSet(updateData.getVendorSpecificSet());
					session.update(eaptlsConfigData);
					session.flush();

					// update vendor specific cert data
					Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
					vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,eaptlsConfigData.getEaptlsId()));
					List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();

					for (Iterator iterator = oldVendorSpecificCertList.iterator(); iterator.hasNext();) {

						VendorSpecificCertificateData vendorSpecificCertificateData = (VendorSpecificCertificateData) iterator.next();
						session.delete(vendorSpecificCertificateData);

					}
					session.flush();

					List<VendorSpecificCertificateData> newVendorSpecificCertList = updateData.getVendorSpecificList();
					List<VendorSpecificCertificateData> newVendorSpecificCertSet = new ArrayList<VendorSpecificCertificateData>();
					
					Integer orderNo = 1;
					for (Iterator iterator = newVendorSpecificCertList.iterator(); iterator.hasNext();) {

						VendorSpecificCertificateData vendorSpecificCertificateData = (VendorSpecificCertificateData) iterator.next();
						vendorSpecificCertificateData.setEaptlsId(updateData.getEaptlsId());
						vendorSpecificCertificateData.setOrderNumber(orderNo);
						newVendorSpecificCertSet.add(vendorSpecificCertificateData);
						session.save(vendorSpecificCertificateData);
						session.flush();
						orderNo ++;
					}
					eaptlsConfigData.setVendorSpecificList(newVendorSpecificCertSet);
					configData.setEaptlsConfigData(eaptlsConfigData);
				}else{

					session.save(updateData);
					session.flush();

					// entry into tblmvendorspecificcertificate;

					List<VendorSpecificCertificateData> vendorspecificCertiList = updateData.getVendorSpecificList();
					List<VendorSpecificCertificateData> newVendorSpecificCertList=new ArrayList<VendorSpecificCertificateData>();
					
					Integer orderNo = 1 ;
					for(int index=0;index<vendorspecificCertiList.size();index++){

						IVendorSpecificCertificateData vendorSpecificCertificateData = vendorspecificCertiList.get(index);
						VendorSpecificCertificateData vendorData=vendorspecificCertiList.get(index);
						vendorSpecificCertificateData.setEaptlsId(updateData.getEaptlsId());
						vendorData.setEaptlsId(updateData.getEaptlsId());
						vendorData.setOrderNumber(orderNo);
						vendorSpecificCertificateData.setOrderNumber(orderNo);
						newVendorSpecificCertList.add(vendorData);
						session.save(vendorSpecificCertificateData);
						orderNo ++;

					}
					updateData.setVendorSpecificList(newVendorSpecificCertList);
					session.flush();

					configData.setEaptlsConfigData(updateData);
				}

				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update EAP TLS details, Reason: " +hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update EAP TLS details, Reason: " +exp.getMessage(),exp);
		}
		
	}
	
	
	public void updateEapGsmDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException{
		Session session = getSession();
		try{
			if(eapConfigData.getSimConfigData()!=null){
				EAPSimAkaConfigData eapConfData= (EAPSimAkaConfigData) session.get(EAPSimAkaConfigData.class, eapConfigData.getSimConfigData().getConfigId());
				// Update EAP Sim Config Details
				if(eapConfigData.getSimConfigData() != null){
					EAPSimAkaConfigData configData = eapConfigData.getSimConfigData();

					JSONArray jsonArray=ObjectDiffer.diff(eapConfData, eapConfigData.getSimConfigData());

					configData.setEapId(eapConfigData.getEapId());
					session.merge(configData);
					session.flush();
					if(jsonArray.size()!=0){
						doAuditingJson(jsonArray.toString(),staffData,actionAlias);
					}
				}
			}
			if(eapConfigData.getAkaConfigData()!=null){
				EAPSimAkaConfigData akaConfData= (EAPSimAkaConfigData) session.get(EAPSimAkaConfigData.class, eapConfigData.getAkaConfigData().getConfigId());

				// Update EAP AKA Config Details
				if(eapConfigData.getAkaConfigData() != null){
					EAPSimAkaConfigData configData = eapConfigData.getAkaConfigData();

					JSONArray jsonArray=ObjectDiffer.diff(akaConfData, eapConfigData.getAkaConfigData());

					configData.setEapId(eapConfigData.getEapId());
					session.merge(configData);
					session.flush();
					if(jsonArray.size()!=0){
						doAuditingJson(jsonArray.toString(),staffData,actionAlias);
					}
				}
			}
			if(eapConfigData.getAkaPrimeConfigData()!= null){
				EAPSimAkaConfigData akaPrimeConfData= (EAPSimAkaConfigData) session.get(EAPSimAkaConfigData.class, eapConfigData.getAkaPrimeConfigData().getConfigId());
				// Update EAP AKA' Config Details
				if(eapConfigData.getAkaPrimeConfigData() != null){
					EAPSimAkaConfigData configData = eapConfigData.getAkaPrimeConfigData();

					JSONArray jsonArray=ObjectDiffer.diff(akaPrimeConfData, eapConfigData.getAkaPrimeConfigData());

					configData.setEapId(eapConfigData.getEapId());
					session.merge(configData);
					session.flush();
					if(jsonArray.size()!=0){
						doAuditingJson(jsonArray.toString(),staffData,actionAlias);
					}
					
				}
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update EAP GSM Details, Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update EAP GSM Details, Reason: "+exp.getMessage(),exp);
		}
	}
	
	
	public void updateSupportedAuthMethods(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException {
		   
		Session session = getSession();
		EAPConfigData configData = null;
		
		try{
			if(eapConfigData != null){
				EliteAssert.notNull(eapConfigData,"EAP Configuration Data must not be null.");

				Criteria criteria = session.createCriteria(EAPConfigData.class);
				criteria.add(Restrictions.eq(EAP_CONFIG_ID,eapConfigData.getEapId()));
				configData=(EAPConfigData)criteria.uniqueResult();
				

				if(configData != null){
					configData = getEAPConfigDetailWithAllChildConfiguration(configData, session);
				}

				JSONArray jsonArray=ObjectDiffer.diff(configData, eapConfigData);
				
				configData.setDefaultNegiotationMethod(eapConfigData.getDefaultNegiotationMethod());
				configData.setLastModifiedByStaffId(eapConfigData.getLastModifiedByStaffId());
				configData.setLastModifiedDate(eapConfigData.getLastModifiedDate());
				String oldEnabledAuthMethods =configData.getEnabledAuthMethods();
				String[] oldEnabledMethodsArray=getOldEnableMethods(oldEnabledAuthMethods);
				String[] newEnabledMethodsArray=getNewEnabledMethods(eapConfigData);
				
				if(configData.getAuditUId() == null){
					String auditId= UUIDGenerator.generate();
					configData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				
				/*
				 * check for TLS/TTLS configuration..
				 */
				  
                  if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TTLS_STR)
                		  &&!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TLS_STR)
                		  &&!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.PEAP_STR)
                		  
                		  && (checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TLS_STR)
                				  ||checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TTLS_STR)
                						  ||checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.PEAP_STR))){
                	  
					// create new eap tls/ttls with default value..
					EAPTLSConfigData defaultTLSConfigData= new EAPTLSConfigData();
					defaultTLSConfigData.setEapId(configData.getEapId());
					defaultTLSConfigData.setMinTlsVersion("TLSv1");
					defaultTLSConfigData.setMaxTlsVersion("TLSv1");
					defaultTLSConfigData.setServerCertificateId(null);
					defaultTLSConfigData.setCertificateRequest("false");
					defaultTLSConfigData.setDefaultCompressionMethod(null);
					defaultTLSConfigData.setSessionResumptionDuration(5000L);
					defaultTLSConfigData.setSessionResumptionLimit(2L);
					defaultTLSConfigData.setCertificateTypesList("1");
					defaultTLSConfigData.setCiphersuiteList("10");

					session.save(defaultTLSConfigData);
					session.flush();
					configData.setEapTtlsCertificateRequest("false");
                 
                  }else if((checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TLS_STR) 
                		  || checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TTLS_STR)
                		  || checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.PEAP_STR))
                		  
                		  && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TLS_STR)
                		  && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TTLS_STR)
                		  && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.PEAP_STR)){
                	  
					Criteria tlsCriteria = session.createCriteria(EAPTLSConfigData.class);
					tlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,configData.getEapId()));
					EAPTLSConfigData tlsConfigData=(EAPTLSConfigData)tlsCriteria.uniqueResult();

					if(tlsConfigData != null){

						Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
						vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,tlsConfigData.getEaptlsId()));
						List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();
						for (Iterator iterator = oldVendorSpecificCertList.iterator(); iterator.hasNext();) {
							VendorSpecificCertificateData vendorSpecificCertificateData = (VendorSpecificCertificateData) iterator.next();
							session.delete(vendorSpecificCertificateData);
							session.flush();
						}
						
						session.delete(tlsConfigData);
						session.flush();
						configData.setEapTtlsCertificateRequest("false");
					} 
					
					
				}
                // Sim Configuration
                if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.SIM_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.SIM_STR)){
				   // create SIM with default
					EAPSimAkaConfigData eapSimConfigData = null;
					Criteria simCriteria = session.createCriteria(EAPSimAkaConfigData.class).add(Restrictions.eq(EAP_CONFIG_ID, configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.SIM));
					List<EAPSimAkaConfigData>  simList = simCriteria.list();
					boolean isSave= true;
					if(simList!=null && !simList.isEmpty()){
						eapSimConfigData = simList.get(0);
						isSave = false;
					}else{
						eapSimConfigData = new EAPSimAkaConfigData();
					}
					
					eapSimConfigData.setPseudonymGenMethod("BASE32");
					eapSimConfigData.setPseudonymHexenCoding("ENABLE");
					eapSimConfigData.setPseudonymPrefix("1999");
					eapSimConfigData.setPseudonymRootNAI("DISABLE");
					eapSimConfigData.setFastReAuthGenMethod("BASE32");
					eapSimConfigData.setFastReAuthHexenCoding("ENABLE");
					eapSimConfigData.setFastReAuthPrefix("1888");
					eapSimConfigData.setFastReAuthRootNAI("DISABLE");
					eapSimConfigData.setEapAuthType(EAPConfigConstant.SIM);
					eapSimConfigData.setEapId(configData.getEapId());
					if(isSave){
						session.save(eapSimConfigData);	
					}else{
						session.update(eapSimConfigData);
					}
					session.flush();
				}else if(checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.SIM_STR) && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.SIM_STR)){
				   // delete SIM config	
					Criteria simCriteria = session.createCriteria(EAPSimAkaConfigData.class);
					simCriteria.add(Restrictions.eq(EAP_CONFIG_ID,configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE,EAPConfigConstant.SIM));
					List list = simCriteria.list();
					deleteObjectList(list, session);

				}
                // Aka Configuration
                if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.AKA_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.AKA_STR)){
				   // create AKA with default config
					EAPSimAkaConfigData eapAkaConfigData = null;
					Criteria akaCriteria = session.createCriteria(EAPSimAkaConfigData.class).add(Restrictions.eq(EAP_CONFIG_ID, configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA));
					List<EAPSimAkaConfigData>  akaList = akaCriteria.list();
					boolean isSave= true;
					if(akaList!=null && !akaList.isEmpty()){
						eapAkaConfigData = akaList.get(0);
						isSave = false;
					}else{
						eapAkaConfigData = new EAPSimAkaConfigData();
					}
					eapAkaConfigData.setPseudonymGenMethod("BASE32");
					eapAkaConfigData.setPseudonymHexenCoding("ENABLE");
					eapAkaConfigData.setPseudonymPrefix("0999");
					eapAkaConfigData.setPseudonymRootNAI("DISABLE");
					eapAkaConfigData.setFastReAuthGenMethod("BASE32");
					eapAkaConfigData.setFastReAuthHexenCoding("ENABLE");
					eapAkaConfigData.setFastReAuthPrefix("0888");
					eapAkaConfigData.setFastReAuthRootNAI("DISABLE");
					eapAkaConfigData.setEapAuthType(EAPConfigConstant.AKA);
					eapAkaConfigData.setEapId(configData.getEapId());
					if(isSave){
						session.save(eapAkaConfigData);
					}else{
						session.update(eapAkaConfigData);	
					}
					session.flush();
					
				}else if(checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.AKA_STR) && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.AKA_STR)){
				   // delete AKA config	
					Criteria akaCriteria = session.createCriteria(EAPSimAkaConfigData.class);
					akaCriteria.add(Restrictions.eq(EAP_CONFIG_ID,configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE,EAPConfigConstant.AKA));
					List list = akaCriteria.list();
					deleteObjectList(list, session);
					
				}
                
                if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.AKA_PRIME_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.AKA_PRIME_STR)){
				   // create AKA' with default config
					EAPSimAkaConfigData eapAkaPrimeConfigData = null;
					Criteria akaPrimeCriteria = session.createCriteria(EAPSimAkaConfigData.class).add(Restrictions.eq(EAP_CONFIG_ID, configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA_PRIME));
					List<EAPSimAkaConfigData>  akaPrimeList = akaPrimeCriteria.list();
					boolean isSave= true;
					if(akaPrimeList!=null && !akaPrimeList.isEmpty()){
						eapAkaPrimeConfigData = akaPrimeList.get(0);
						isSave = false;
					}else{
						eapAkaPrimeConfigData = new EAPSimAkaConfigData();
					}
					eapAkaPrimeConfigData.setPseudonymGenMethod("BASE32");
					eapAkaPrimeConfigData.setPseudonymHexenCoding("ENABLE");
					eapAkaPrimeConfigData.setPseudonymPrefix("6999");
					eapAkaPrimeConfigData.setPseudonymRootNAI("DISABLE");
					eapAkaPrimeConfigData.setFastReAuthGenMethod("BASE32");
					eapAkaPrimeConfigData.setFastReAuthHexenCoding("ENABLE");
					eapAkaPrimeConfigData.setFastReAuthPrefix("6888");
					eapAkaPrimeConfigData.setFastReAuthRootNAI("DISABLE");
					eapAkaPrimeConfigData.setEapAuthType(EAPConfigConstant.AKA_PRIME);
					eapAkaPrimeConfigData.setEapId(configData.getEapId());
					if(isSave){
						session.save(eapAkaPrimeConfigData);
					}else{
						session.update(eapAkaPrimeConfigData);	
					}
					session.flush();
					
				}else if(checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.AKA_PRIME_STR) && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.AKA_PRIME_STR)){
				   // delete AKA' config	
					Criteria akaPrimeCriteria = session.createCriteria(EAPSimAkaConfigData.class);
					akaPrimeCriteria.add(Restrictions.eq(EAP_CONFIG_ID,configData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE,EAPConfigConstant.AKA_PRIME));
					List list = akaPrimeCriteria.list();
					deleteObjectList(list, session);
					
				}
                
                if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.PEAP_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.PEAP_STR)){
					configData.setEapPeapCertificateRequest("false");
					
				}else if(checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.PEAP_STR) && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.PEAP_STR)){
					configData.setEapPeapCertificateRequest("false");
				}
                
                if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TTLS_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TTLS_STR)){

                	configData.setEapTtlsCertificateRequest("false");
				}else if(!checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TLS_STR) && checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TLS_STR)){

					configData.setEapTtlsCertificateRequest("false");
				}else if(checkedExistForMethod(oldEnabledMethodsArray,EAPConfigConstant.TTLS_STR) && !checkedExistForMethod(newEnabledMethodsArray,EAPConfigConstant.TTLS_STR)){
					
					configData.setEapTtlsCertificateRequest("false");
				}
				
				configData.setEnabledAuthMethods(eapConfigData.getEnabledAuthMethods());
				session.update(configData);
				session.flush();
				
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
				
			}
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Supported Auth Methods, Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Supported Auth Methods, Reason: "+exp.getMessage(),exp);
		}
		
	}

	private String[] getOldEnableMethods(String enabledAuthMethods) {
		String[] enabledMethods = null;
		if (enabledAuthMethods != null) {
			enabledMethods = enabledAuthMethods.split(",");
		}
		return enabledMethods;
	}

	private String[] getNewEnabledMethods(EAPConfigData eapConfigData) {
		List<String> list = eapConfigData.getCheckedEnabledMethodsArray();
		String[] enabledMethods = null;
		if (list != null && !list.isEmpty()) {
			enabledMethods = new String[list.size()];
			for (int j = 0; j < enabledMethods.length; j++) {
				enabledMethods[j] = list.get(j);
			}
		}
		return enabledMethods;
	}

	private boolean checkedExistForMethod(String[] methods, String method) {
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				if (methods[i] != null && methods[i].equals(method)) {
					return true;
				}
			}
		}
		return false;
	}		

	@Override
	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException {
		List<ServerCertificateData> lstServerCertificateData;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			criteria.addOrder(Order.asc("serverCertificateId"));
			lstServerCertificateData = criteria.list();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
		    throw new DataManagerException("Failed to retrive list of Server Certificates, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of Server Certificates, Reason: "+exp.getMessage(), exp);
		}
				
		return lstServerCertificateData;  
	}

	@Override
	public EAPSimAkaConfigData getEapGsmDetails(EAPConfigData eapConfigData) throws DataManagerException {
		
		Session session = getSession();
		try{
			
			EAPSimAkaConfigData eapConfData = null;	
			if(eapConfigData.getSimConfigData()!=null){
				
				Criteria criteria = session.createCriteria(EAPSimAkaConfigData.class);
				criteria.add(Restrictions.eq(EAP_CONFIG_ID, eapConfigData.getEapId())).add(Restrictions.eq("configId",eapConfigData.getSimConfigData().getConfigId()));
				eapConfData=(EAPSimAkaConfigData)criteria.uniqueResult();
				session.flush();
			}
			
			return eapConfData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP GSM Details, Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP GSM Details, Reason: "+exp.getMessage(),exp);
		}
		
	}

	@Override
	public String getEapNameFromId(String eapId) throws DataManagerException {
		
		Session session = getSession();
		String eapConfig;
		try{
			Criteria criteria = session.createCriteria(EAPConfigData.class).add(Restrictions.eq(EAP_CONFIG_ID, eapId));
			criteria.setProjection(Projections.property(EAP_CONF_NAME));
			
			if(criteria.uniqueResult() == null){
				return "";
			}
			
			eapConfig=criteria.uniqueResult().toString();
			
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP Configuration name from EAP Configration id, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive EAP Configuration name from EAP Configration id, Reason: "+e.getMessage(),e);
		}

		return eapConfig;
	}


	@Override
	public String getServerCertificateIdFromName(String serverCertificateName)
			throws DataManagerException {
		String serverCertificateId;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			ServerCertificateData certificateData = (ServerCertificateData)criteria.add(Restrictions.eq("serverCertificateName",serverCertificateName)).uniqueResult();
			
			if(certificateData == null){
				throw new InvalidValueException("No matching Server Certificate name found for: "+serverCertificateName);
			}
			
			serverCertificateId = certificateData.getServerCertificateId();
		}catch(DataManagerException dbe){
			dbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Server Certificate id from Server Certificate Name, Reason: "+dbe.getMessage(), dbe);
		}catch (HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Server Certificate id from Server Certificate Name, Reason: "+hbe.getMessage(), hbe);
		}
		
		return serverCertificateId;
	}

	@Override
	public String getServerCertificateNameFromId(String serverCertificateId)
			throws DataManagerException {
		String serverCertificateName;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			ServerCertificateData certificateData = (ServerCertificateData)criteria.add(Restrictions.eq("serverCertificateId",serverCertificateId)).uniqueResult();
			serverCertificateName = certificateData.getServerCertificateName();
		}catch (HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Server Certificate Name from Server Certificate Id, Reason: "+hbe.getMessage(), hbe);
		}
		
		return serverCertificateName;
	}


	@Override
	public void updateEapConfigDataByName(EAPConfigData eapConfigData,IStaffData staffData, String actionAlias, String eapConfigName)
			throws DataManagerException {
		//get session
		Session session = getSession();
		EAPConfigData dbEapConfigData = null;
		try{
			if(eapConfigData != null){
				EliteAssert.notNull(eapConfigData,"EAP Configuration Data must not be null.");
				List<String> enableAuthMethodList = Arrays.asList(eapConfigData.getEnabledAuthMethods().split(","));
				//get eap Config by name from url
				Criteria criteria = session.createCriteria(EAPConfigData.class);
				dbEapConfigData=(EAPConfigData)criteria.add(Restrictions.eq(EAP_CONF_NAME,eapConfigName)).uniqueResult();
				
				if(dbEapConfigData == null){
					throw new InvalidValueException("EAP Configuration not found");
				}
				
				dbEapConfigData = getEAPConfigDetailWithAllChildConfiguration(dbEapConfigData, session);
				
				JSONArray jsonArray=ObjectDiffer.diff(dbEapConfigData,eapConfigData);
				
				//tls configuration update
				Criteria tlsCriteria  = session.createCriteria(EAPTLSConfigData.class);
				tlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId()));
				EAPTLSConfigData dbEapTlsConfigData =(EAPTLSConfigData)tlsCriteria.uniqueResult();	
				
				if(dbEapTlsConfigData != null ){
					Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
					vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,dbEapTlsConfigData.getEaptlsId()));
					List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();
					dbEapTlsConfigData.setVendorSpecificList(oldVendorSpecificCertList);
					dbEapConfigData.setEaptlsConfigData(dbEapTlsConfigData);
				}
				
				//delete SIM configuration
				Criteria simCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
				simCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.SIM));
				List<EAPSimAkaConfigData> simConfigList = simCriteria.list();
				EAPSimAkaConfigData simConfigData = null;
				
				if(Collectionz.isNullOrEmpty(simConfigList)==false){
					simConfigData = simConfigList.get(0);
					session.beginTransaction();
					for (EAPSimAkaConfigData simConfig : simConfigList) {
						session.delete(simConfig);
					}
					session.flush();
					Logger.logDebug(MODULE, "Deleted SIM configuration during update");
				}
				
				dbEapConfigData.setSimConfigData(simConfigData);
				
				
				//delete AKA configuration
				Criteria akaCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
				akaCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA));
				List<EAPSimAkaConfigData> akaConfigList = akaCriteria.list();
				EAPSimAkaConfigData akaConfigData = null;
				
				if(Collectionz.isNullOrEmpty(akaConfigList) == false){
					akaConfigData =	akaConfigList.get(0);	
					session.beginTransaction();
					for (EAPSimAkaConfigData akaConfig : akaConfigList) {
						session.delete(akaConfig);
					}
					session.flush();
					Logger.logDebug(MODULE, "Deleted AKA configuration during update");
				}
				
				dbEapConfigData.setAkaConfigData(akaConfigData);
				
				//delete AKA PRIME configuration
				Criteria akaPrimeCriteria  = session.createCriteria(EAPSimAkaConfigData.class);
				akaPrimeCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId())).add(Restrictions.eq(EAP_AUTH_TYPE, EAPConfigConstant.AKA_PRIME));
				List<EAPSimAkaConfigData> akaPrimeConfigList = akaPrimeCriteria.list();
				EAPSimAkaConfigData akaPrimeConfigData = null;
				
				if(Collectionz.isNullOrEmpty(akaPrimeConfigList) == false){
					akaPrimeConfigData = akaPrimeConfigList.get(0);
					session.beginTransaction();
					for (EAPSimAkaConfigData akaPrimeConfig : akaPrimeConfigList) {
						session.delete(akaPrimeConfig);
					}
					session.flush();
					Logger.logDebug(MODULE, "Deleted AKA prime configuration during update");
				}
				
				dbEapConfigData.setAkaPrimeConfigData(akaPrimeConfigData);
				
				if(enableAuthMethodList.contains(EAPConfigConstant.TLS_STR) || enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR) 
						|| enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
					
					Criteria ttlsCriteria=session.createCriteria(EAPTLSConfigData.class);
					ttlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId()));
					EAPTLSConfigData dbEaptlsConfigData = (EAPTLSConfigData)ttlsCriteria.uniqueResult();
					
					EAPTLSConfigData newTlsConfigData = eapConfigData.getEaptlsConfigData();
					if(dbEaptlsConfigData != null){

						dbEaptlsConfigData.setMinTlsVersion(newTlsConfigData.getMinTlsVersion());
						dbEaptlsConfigData.setMaxTlsVersion(newTlsConfigData.getMaxTlsVersion());
						dbEaptlsConfigData.setCertificateRequest(newTlsConfigData.getCertificateRequest());
						dbEaptlsConfigData.setServerCertificateId(newTlsConfigData.getServerCertificateId());
						dbEaptlsConfigData.setSessionResumptionLimit(newTlsConfigData.getSessionResumptionLimit());
						dbEaptlsConfigData.setSessionResumptionDuration(newTlsConfigData.getSessionResumptionDuration());
						dbEaptlsConfigData.setDefaultCompressionMethod(newTlsConfigData.getDefaultCompressionMethod());
						dbEaptlsConfigData.setCertificateTypesList(newTlsConfigData.getCertificateTypesList());
						dbEaptlsConfigData.setCiphersuiteList(newTlsConfigData.getCiphersuiteList());
						
						//PEAP parameters based on enable auth method
						if(enableAuthMethodList.contains(EAPConfigConstant.PEAP_STR)){
							dbEapConfigData.setPeapNegotiationMethod(eapConfigData.getPeapNegotiationMethod());
							dbEapConfigData.setPeapVersion(eapConfigData.getPeapVersion());
							dbEapConfigData.setEapPeapCertificateRequest(eapConfigData.getEapPeapCertificateRequest());
							
						}else {
							dbEapConfigData.setPeapNegotiationMethod(26);
							dbEapConfigData.setPeapVersion("0");
							dbEapConfigData.setEapPeapCertificateRequest("false");
						}
						
						//TTLS parameters based on enable auth method
						if(enableAuthMethodList.contains(EAPConfigConstant.TTLS_STR)){
							dbEapConfigData.setTtlsNegotiationMethod(eapConfigData.getTtlsNegotiationMethod());
							dbEapConfigData.setEapTtlsCertificateRequest(eapConfigData.getEapTtlsCertificateRequest());
						}else {
							dbEapConfigData.setTtlsNegotiationMethod(26);
							dbEapConfigData.setEapTtlsCertificateRequest("false");
						}
						
						
						dbEaptlsConfigData.setExpiryDate(newTlsConfigData.getExpiryDate());
						dbEaptlsConfigData.setRevokedCertificate(newTlsConfigData.getRevokedCertificate());
						dbEaptlsConfigData.setMissingClientCertificate(newTlsConfigData.getMissingClientCertificate());
						dbEaptlsConfigData.setMacValidation(newTlsConfigData.getMacValidation());
						session.update(dbEaptlsConfigData);
						session.flush();
						
						Logger.logDebug(MODULE, "TLS updated without Vendor Specific certificate  congiuration");
						
						// update vendor specific certificate data
						Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
						vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,dbEaptlsConfigData.getEaptlsId()));
						List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();
						
						//delete older vendor certificate data
						for (VendorSpecificCertificateData vendorSpecificCertificateData : oldVendorSpecificCertList) {
							session.delete(vendorSpecificCertificateData);
						}
						session.flush();
						Logger.logDebug(MODULE, "Deleted vendor specific configuration");

						List<VendorSpecificCertificateData> newVendorSpecificCertList = newTlsConfigData.getVendorSpecificList();
						List<VendorSpecificCertificateData> newVendorSpecificCertDataList = new ArrayList<VendorSpecificCertificateData>();
						
						if(Collectionz.isNullOrEmpty(newVendorSpecificCertList) == false){
							
							//entry into tblmvendorspecificcertificate
							Integer orderNo = 1;
							for (VendorSpecificCertificateData vendorSpecificCertificateData : newVendorSpecificCertList) {
								vendorSpecificCertificateData.setEaptlsId(dbEaptlsConfigData.getEaptlsId());
								vendorSpecificCertificateData.setOrderNumber(orderNo);
								newVendorSpecificCertDataList.add(vendorSpecificCertificateData);
								session.save(vendorSpecificCertificateData);
								orderNo++;
							}
						}
						session.flush();
						
						dbEaptlsConfigData.setVendorSpecificList(newVendorSpecificCertDataList);
						dbEapConfigData.setEaptlsConfigData(dbEaptlsConfigData);
					}else{

						newTlsConfigData.setEapId(dbEapConfigData.getEapId());
						session.save(newTlsConfigData);
						session.flush();

						// entry into tblmvendorspecificcertificate
						List<VendorSpecificCertificateData> vendorspecificCertiList = newTlsConfigData.getVendorSpecificList();
						List<VendorSpecificCertificateData> newVendorSpecificCertList=new ArrayList<VendorSpecificCertificateData>();
						if(Collectionz.isNullOrEmpty(vendorspecificCertiList) == false){
							Integer orderNo = 1;
							for(VendorSpecificCertificateData vendorSpecificCertificateData : vendorspecificCertiList){
								vendorSpecificCertificateData.setEaptlsId(newTlsConfigData.getEaptlsId());
								vendorSpecificCertificateData.setOrderNumber(orderNo);
								newVendorSpecificCertList.add(vendorSpecificCertificateData);
								session.save(vendorSpecificCertificateData);
								orderNo++;
							}
						}
						
						newTlsConfigData.setVendorSpecificList(newVendorSpecificCertList);
						session.flush();

						dbEapConfigData.setEaptlsConfigData(newTlsConfigData);
					}

				} else {
					// if non of TLS ,TTLS,PEAP selected than delete previous all TLS Based Configuration 
					Criteria ttlsCriteria=session.createCriteria(EAPTLSConfigData.class);
					ttlsCriteria.add(Restrictions.eq(EAP_CONFIG_ID,dbEapConfigData.getEapId()));
					EAPTLSConfigData dbEaptlsConfigData = (EAPTLSConfigData)ttlsCriteria.uniqueResult();
					
					if(dbEaptlsConfigData != null){
						// update vendor specific certificate data
						Criteria vendorSpeciCriteria = session.createCriteria(VendorSpecificCertificateData.class);
						vendorSpeciCriteria.add(Restrictions.eq(EAPTLS_ID,dbEaptlsConfigData.getEaptlsId()));
						List<VendorSpecificCertificateData> oldVendorSpecificCertList = vendorSpeciCriteria.list();
						
						//delete older vendor certificate data
						for (VendorSpecificCertificateData vendorSpecificCertificateData : oldVendorSpecificCertList) {
							session.delete(vendorSpecificCertificateData);
						}
						session.flush();
						
						//delete TLS Configuration
						session.delete(dbEaptlsConfigData);
						
						session.flush();
					}
				}
				
				if(enableAuthMethodList.contains(EAPConfigConstant.SIM_STR) && eapConfigData.getSimConfigData() != null){
					EAPSimAkaConfigData configData = eapConfigData.getSimConfigData();
					configData.setEapId(dbEapConfigData.getEapId());
					session.save(configData);
					session.flush();
					dbEapConfigData.setSimConfigData(configData);
					Logger.logInfo(MODULE, "updated SIM configuration");
				}
				
				if(enableAuthMethodList.contains(EAPConfigConstant.AKA_STR) && eapConfigData.getAkaConfigData() != null){
						EAPSimAkaConfigData configData = eapConfigData.getAkaConfigData();
						configData.setEapId(dbEapConfigData.getEapId());
						session.save(configData);
						session.flush();
						dbEapConfigData.setAkaConfigData(configData);
						Logger.logDebug(MODULE, "updated AKA configuration");
				}
				if(enableAuthMethodList.contains(EAPConfigConstant.AKA_PRIME_STR) && eapConfigData.getAkaPrimeConfigData() != null){
					EAPSimAkaConfigData configData = eapConfigData.getAkaPrimeConfigData();
					configData.setEapId(dbEapConfigData.getEapId());
					session.save(configData);
					session.flush();
					dbEapConfigData.setAkaPrimeConfigData(configData);
					Logger.logInfo(MODULE, "updated AKA prime configuration");
				}
				//basic details
				dbEapConfigData.setName(eapConfigData.getName());
				dbEapConfigData.setEnabledAuthMethods(eapConfigData.getEnabledAuthMethods());
				dbEapConfigData.setDescription(eapConfigData.getDescription());
				dbEapConfigData.setDefaultNegiotationMethod(eapConfigData.getDefaultNegiotationMethod());
				dbEapConfigData.setSessionCleanupInterval(eapConfigData.getSessionCleanupInterval());
				dbEapConfigData.setSessionDurationForCleanup(eapConfigData.getSessionDurationForCleanup());
				dbEapConfigData.setSessionTimeout(eapConfigData.getSessionTimeout());
				dbEapConfigData.setMskRevalidationTime(eapConfigData.getMskRevalidationTime());
				dbEapConfigData.setTreatInvalidPacketAsFatal(eapConfigData.getTreatInvalidPacketAsFatal());
				dbEapConfigData.setNotificationSuccess(eapConfigData.getNotificationSuccess());
				dbEapConfigData.setNotificationFailure(eapConfigData.getNotificationFailure());
				dbEapConfigData.setMaxEapPacketSize(eapConfigData.getMaxEapPacketSize());
				//last modified details
				dbEapConfigData.setLastModifiedByStaffId(staffData.getStaffId());
				dbEapConfigData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
				
				//JSONArray jsonArray = ObjectDiffer.diff(dbEapConfigData,eapConfigData);
				session.update(dbEapConfigData);
				session.flush();
				Logger.logInfo(MODULE, "updated eap config basic details");
				
				
				//audit parameter
				staffData.setAuditName(dbEapConfigData.getName());
				staffData.setAuditId(dbEapConfigData.getAuditUId());
				Logger.logInfo(MODULE, "setted audit parameter");
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update EAP Configuration, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update EAP Configuration, Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to update EAP Configuration, Reason: "+exp.getMessage(),exp);
		}
		
	}
	
}