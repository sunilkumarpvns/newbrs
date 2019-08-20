package com.elitecore.elitesm.hibernate.digestconf;

import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.digestconf.DigestConfDataManager;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HDigestConfDataManager extends HBaseDataManager implements DigestConfDataManager {
	
	private static final String DIGEST_CONF_ID = "digestConfId";
	private static final String DIGEST_CONF_NAME = "name";
	private static final String MODULE = "HDigestConfDataManager";

	@Override
	public String create(Object obj) throws DataManagerException
	{
		DigestConfigInstanceData digestConfInstanceData = (DigestConfigInstanceData) obj;
		try{
			verifyDigestConfigInstanceDataName(digestConfInstanceData);
		
	        Session session=getSession();
	        session.clear();
	        
	        String auditId= UUIDGenerator.generate();
			
	        digestConfInstanceData.setAuditUId(auditId);
	        
			session.save(digestConfInstanceData);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ digestConfInstanceData.getName() +REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
            throw new DataManagerException(FAILED_TO_CREATE + digestConfInstanceData.getName() +REASON +hExp.getMessage() ,hExp);
        }catch(Exception exp){
        	Logger.logTrace(MODULE, exp);
            throw new DataManagerException(FAILED_TO_CREATE + digestConfInstanceData.getName() +REASON +exp.getMessage() ,exp);
        }
		return digestConfInstanceData.getName();
	}
	
	public List<DigestConfigInstanceData> getDigestConfigInstanceList() throws DataManagerException {
		List<DigestConfigInstanceData> digestConfigInstanceDataList = null;
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class);
			digestConfigInstanceDataList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return digestConfigInstanceDataList;
	}

	public PageList search(DigestConfigInstanceData digestConfigInstanceData,int requiredPageNo, Integer pageSize) throws DataManagerException {

		PageList pageList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class);
			if((digestConfigInstanceData.getName() != null && digestConfigInstanceData.getName().length()>0 )){
				String name="%"+digestConfigInstanceData.getName()+"%";
				criteria.add(Restrictions.ilike(DIGEST_CONF_NAME,name));
			}

			if(!("none".equalsIgnoreCase(digestConfigInstanceData.getDraftAAASipEnable()))){
				criteria.add(Restrictions.eq("draftAAASipEnable",digestConfigInstanceData.getDraftAAASipEnable()));
			}


			int totalItems = criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}
			List digestConfigList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(digestConfigList, requiredPageNo, totalPages ,totalItems);


		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void updateById(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData, String digestConfigId) throws DataManagerException {
		update(digestConfigInstanceData, staffData, DIGEST_CONF_ID,digestConfigId);
	}

	@Override
	public void updateByName(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData, String queryOrPathParam) throws DataManagerException {
		update(digestConfigInstanceData, staffData, DIGEST_CONF_NAME,queryOrPathParam);
	}
	
	private void update(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData,String propertyName,Object propertyValue) throws DataManagerException {
		String digestConfiguratonName = null;
		Session session = getSession();

		try{
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class).add(Restrictions.eq(propertyName,propertyValue));

			DigestConfigInstanceData instanceData = (DigestConfigInstanceData)criteria.uniqueResult();	

			if(instanceData == null){
				throw new InvalidValueException("Digest Configuration not found");
			}	
			digestConfiguratonName = instanceData.getName();
			JSONArray jsonArray=ObjectDiffer.diff(instanceData,digestConfigInstanceData);

			instanceData.setName(digestConfigInstanceData.getName());
			instanceData.setDescription(digestConfigInstanceData.getDescription());
			instanceData.setRealm(digestConfigInstanceData.getRealm());
			instanceData.setDefaultQoP(digestConfigInstanceData.getDefaultQoP());
			instanceData.setDefaultAlgo(digestConfigInstanceData.getDefaultAlgo());
			instanceData.setOpaque(digestConfigInstanceData.getOpaque());
			instanceData.setDefaultNonce(digestConfigInstanceData.getDefaultNonce());
			instanceData.setDefaultNonceLength(digestConfigInstanceData.getDefaultNonceLength());
			instanceData.setDraftAAASipEnable(digestConfigInstanceData.getDraftAAASipEnable());
			instanceData.setLastModifiedbyStaffid(digestConfigInstanceData.getLastModifiedbyStaffid());
			instanceData.setLastModifiedDate(digestConfigInstanceData.getLastModifiedDate());

			session.update(instanceData);
			session.flush();

			staffData.setAuditName(instanceData.getName());
			staffData.setAuditId(instanceData.getAuditUId());

			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DIGEST_CONFIGURATION);

		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+ digestConfiguratonName +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update "+digestConfiguratonName+", Reason: " + hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+digestConfiguratonName+", Reason: " + exp.getMessage(),exp);
		}
	}

	private void verifyDigestConfigInstanceDataName(DigestConfigInstanceData digestConfigInstanceData) throws DuplicateInstanceNameFoundException {
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(DigestConfigInstanceData.class);
		List list = criteria.add(Restrictions.eq(DIGEST_CONF_NAME,digestConfigInstanceData.getName())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException(digestConfigInstanceData.getName() +" name already exists");
		}
	}

	@Override
	public String getDigestConfigInstDataNameFormId(String digestConfId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class);
			criteria.add(Restrictions.eq(DIGEST_CONF_ID,digestConfId));
			criteria.setProjection(Projections.property(DIGEST_CONF_NAME));

			String name = (String) criteria.uniqueResult();
			return name;

		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to retrive Digest Configuration. Reason: "+ hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	

	@Override
	public DigestConfigInstanceData getDigestConfigInstDataByName(String digestConfigurationName)throws DataManagerException {
		return getDigestConfigInstDataByIdOrName(DIGEST_CONF_NAME,digestConfigurationName);
	}
	
	@Override
	public DigestConfigInstanceData getDigestConfigInstDataById(String digestConfId) throws DataManagerException {
		return getDigestConfigInstDataByIdOrName(DIGEST_CONF_ID,digestConfId);
	}
	
	private DigestConfigInstanceData getDigestConfigInstDataByIdOrName(String propertyName, Object propertyValue) throws DataManagerException{
		DigestConfigInstanceData digestConfigurationData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class).add(Restrictions.eq(propertyName,propertyValue));
			
			digestConfigurationData = (DigestConfigInstanceData) criteria.uniqueResult();
		
			if(digestConfigurationData == null){
				throw new InvalidValueException("Digest Configuration not found");
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Digest Configuration, Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Digest Configuration, Reason: " +exp.getMessage(), exp);
		}
		return digestConfigurationData;
	}

	@Override
	public String deleteByName(String digestConfigurationName)throws DataManagerException {
		return	delete(DIGEST_CONF_NAME,digestConfigurationName);
	}
	
	@Override
	public String deleteById(String digestConfId) throws DataManagerException {
		return	delete(DIGEST_CONF_ID,digestConfId);
	}
	
	private String delete(String propertyName, Object propertyValue) throws DataManagerException{
		String digestConfigurationName = ((DIGEST_CONF_NAME.equals(propertyName)) ? (String) propertyValue : "Digest Configuration");
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DigestConfigInstanceData.class).add(Restrictions.eq(propertyName,propertyValue));
			DigestConfigInstanceData digestConfigData = (DigestConfigInstanceData) criteria.uniqueResult();
			if(digestConfigData == null){
				throw new InvalidValueException("Digest Configuration not found");
			}
			session.delete(digestConfigData);
			return digestConfigData.getName();
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+ digestConfigurationName +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete "+digestConfigurationName+", Reason: " +  hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+digestConfigurationName+", Reason: " + exp.getMessage(), exp);
		}
	}
}
