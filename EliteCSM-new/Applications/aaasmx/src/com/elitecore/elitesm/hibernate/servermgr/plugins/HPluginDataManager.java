package com.elitecore.elitesm.hibernate.servermgr.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermanager.plugins.PluginDataManager;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.IPluginServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.FormatMappingData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.UniversalPluginConfigurationImpl;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginDetails;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HPluginDataManager extends HBaseDataManager implements PluginDataManager{
	
	private static final String PLUGIN_INSTANCE_ID = "pluginInstanceId";
	private static final String PLUGIN_INSTANCE_NAME = "name";
	private static final String MODULE = "HPluginDataManager";
	
	@Override
	public String createUniversalPlugin(CreatePluginConfig pluginConfig) throws DataManagerException {
		
		PluginInstData pluginInstData = pluginConfig.getPluginInstData();
		try{						
			Session session = getSession();
			session.clear();

			UniversalPluginData universalPluginData = pluginConfig.getUniversalPluginData();			

			String auditId= UUIDGenerator.generate();
			
			pluginInstData.setAuditUId(auditId);
			
			session.save(pluginInstData);			
			session.flush();
			session.clear();

			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			universalPluginData.setPluginInstanceId(pluginInstanceId);
			session.save(universalPluginData);
			session.flush();
			session.clear();
			
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() +REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() +REASON+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() +REASON+ exp.getMessage(),exp);
		}
		return pluginInstData.getName();
	}
	
	public PageList search(PluginInstData pluginInstanceData, int requiredPageNo, Integer pageSize) throws DataManagerException {

		List<PluginInstData> pluginList = null;
		PageList pageList = null;
		try{		
			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginInstData.class);
			String name=pluginInstanceData.getName();
			String selectedPlugin=pluginInstanceData.getPluginTypeId();

			if((name != null && !"".equals(name))){
				name = "%"+name+"%";
				criteria.add(Restrictions.ilike("name",name));
			}

			if(Strings.isNullOrBlank(selectedPlugin) == false){
				criteria.add(Restrictions.eq("pluginTypeId",selectedPlugin));
			}
			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			pluginList = criteria.list();

			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			for(int i =0;i<pluginList.size();i++){

				PluginInstData tempData = pluginList.get(i);
				tempData.setPluginTypeName(tempData.getPluginTypesData().getName());

			}
			pageList = new PageList(pluginList, requiredPageNo, totalPages ,totalItems);

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return pageList;
	}

	@Override
	public PluginInstData getPluginInstanceData(String pluginInstanceId) throws DataManagerException {
		PluginInstData pluginInstanceData;
		try {
			Session session = getSession();

			pluginInstanceData = (PluginInstData) session.createCriteria(PluginInstData.class).add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceId)).uniqueResult();
			if(pluginInstanceData == null){
				throw new InvalidValueException("Plugin Data not found");
			}
		} catch (HibernateException hbe) {
			throw new DataManagerException(hbe.getMessage(), hbe);
		} catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		return pluginInstanceData;
	}
	
	@Override
	public PluginInstData getPluginInstanceByPluginInstanceId( String pluginInstanceId ) throws DataManagerException {
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginInstData.class);
			PluginInstData pluginInstData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginInstanceId)).uniqueResult();
			if(pluginInstData == null){
				throw new DataManagerException("Plugin Data not found");
			}
			return pluginInstData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public void update(PluginInstData pluginInstData, UniversalPluginData universalPluginData, IStaffData staffData, String actionAlias, DiameterUniversalPluginDetails oldObject, DiameterUniversalPluginDetails newObject) throws DataManagerException {
		try{
			Session session = getSession();
			Set<UniversalPluginData> universalAuthPluginSet=new HashSet<UniversalPluginData>();
			
			Criteria criteria = session.createCriteria(PluginInstData.class);

			PluginInstData tempPluginInstanceData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, universalPluginData.getPluginInstanceId())).uniqueResult();
			if(tempPluginInstanceData == null){
				throw new InvalidValueException("Plugin Data not found");
			}
			universalAuthPluginSet.add(universalPluginData);
			pluginInstData.setUniversalPluginDetails(universalAuthPluginSet);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject,newObject);
			
			criteria = session.createCriteria(UniversalPluginData.class);
			UniversalPluginData tempUnivesalAuthPluginData = (UniversalPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, universalPluginData.getPluginInstanceId())).uniqueResult();
			if(tempUnivesalAuthPluginData == null){
				throw new InvalidValueException("Universal Plugin Data not found");
			}
			// in Universal Auth Plugin 
			tempUnivesalAuthPluginData.setPluginData(universalPluginData.getPluginData());
			tempUnivesalAuthPluginData.setPluginInstanceId(tempUnivesalAuthPluginData.getPluginInstanceId());

			session.update(tempUnivesalAuthPluginData);

			// for plugin instance 
			tempPluginInstanceData.setName(pluginInstData.getName());
			tempPluginInstanceData.setDescription(pluginInstData.getDescription());
			tempPluginInstanceData.setLastModifiedByStaffId(pluginInstData.getLastModifiedByStaffId());
			tempPluginInstanceData.setLastModifiedDate(pluginInstData.getLastModifiedDate());
			tempPluginInstanceData.setAuditUId(pluginInstData.getAuditUId());
			tempPluginInstanceData.setStatus(pluginInstData.getStatus());
			
			session.update(tempPluginInstanceData);
			session.flush();

			Logger.getLogger().info(MODULE, "JSON Array is : "+ jsonArray.toString());
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Universal Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Universal Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Universal Plugin , Reason: " + exp.getMessage(), exp);
		}
	}

	@Override
	public List<IPluginServiceTypeData> getListOfAllPluginsServiceData() throws DataManagerException {
		try{	
			
			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginServiceTypeData.class);
			List pluginServiceDataList = criteria.list();
			return pluginServiceDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public List<PluginInstData> getAuthPluginList() throws DataManagerException {
		try{	
			
			Session session = getSession();
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.USER_STATISTIC_POST_AUTH_PLUGIN));
			
			Criteria criteria = session.createCriteria(PluginInstData.class).add(disjunction).add(Restrictions.eq("status", BaseConstant.SHOW_STATUS_ID));
			List pluginInstDataList = criteria.list();
			return pluginInstDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public List<PluginInstData> getAcctPluginList() throws DataManagerException {
		try{	
			
			Session session = getSession();
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN));
			
			Criteria criteria = session.createCriteria(PluginInstData.class).add(disjunction).add(Restrictions.eq("status", BaseConstant.SHOW_STATUS_ID));
			List pluginInstDataList = criteria.list();
			return pluginInstDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}	
	}

	@Override
	public String createGroovyPlugin(CreatePluginConfig pluginConfig) throws DataManagerException {
		PluginInstData pluginInstData =  pluginConfig.getPluginInstData();
		try{						
			Session session = getSession();
			session.clear();

			GroovyPluginData groovyPluginData = pluginConfig.getGroovyPluginData();			

			String auditId= UUIDGenerator.generate();
			
			pluginInstData.setAuditUId(auditId);
			
			session.save(pluginInstData);			

			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			groovyPluginData.setPluginInstanceId(pluginInstanceId);
			session.save(groovyPluginData);

			String pluginId = groovyPluginData.getPluginId();
			Set<GroovyPluginFile> tempSet = groovyPluginData.getGroovyPluginFileSet();
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(tempSet) == false ){
				for (GroovyPluginFile groovyPluginFile :  tempSet ){
					groovyPluginFile.setPluginId(pluginId);
					groovyPluginFile.setOrderNumber(orderNumber);
					session.save(groovyPluginFile);
					orderNumber++;
				}
			}
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() +REASON+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ pluginInstData.getName() +REASON+ exp.getMessage(),exp);
		}
		return pluginInstData.getName();
	}

	@Override
	public GroovyPluginFile getGroovyPluginFileByName(String groovyFileName, String pluginId) throws DataManagerException {
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(GroovyPluginFile.class);
			GroovyPluginFile groovyPluginFile = (GroovyPluginFile)criteria.add(Restrictions.eq("pluginId",pluginId)).add(Restrictions.eq("groovyFileName",groovyFileName)).uniqueResult();
			if(groovyPluginFile == null){
				throw new DataManagerException("Plugin Data not found");
			}
			return groovyPluginFile;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	
	@Override
	public void updateGroovyFile(GroovyPluginFile groovyPluginFileNewObject, IStaffData staffData,PluginInstData pluginInstData) throws DataManagerException {
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(GroovyPluginFile.class);
			
			criteria = session.createCriteria(GroovyPluginFile.class);
			GroovyPluginFile groovyPluginFileData = (GroovyPluginFile)criteria.add(Restrictions.eq("groovyFileId", groovyPluginFileNewObject.getGroovyFileId())).uniqueResult();
			if(groovyPluginFileData == null){
				throw new InvalidValueException("Groovy Plugin Data not found");
			}
			JSONArray jsonArray=ObjectDiffer.diff(groovyPluginFileData,groovyPluginFileNewObject);

			groovyPluginFileData.setGroovyFile(groovyPluginFileNewObject.getGroovyFile());
			groovyPluginFileData.setLastUpdatedTime(groovyPluginFileNewObject.getLastUpdatedTime());
			session.update(groovyPluginFileData);
			session.flush();
			
			staffData.setAuditId(pluginInstData.getAuditUId());
			staffData.setAuditName(pluginInstData.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_PLUGIN);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin File, Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin File, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin File, Reason: " + exp.getMessage(), exp);
		}
	}

	@Override
	public void createTransactionLogger(CreatePluginConfig pluginConfig)throws DataManagerException {
		PluginInstData pluginInstData = null;
		try{						
			Session session = getSession();
			pluginInstData = pluginConfig.getPluginInstData();

			TransactionLoggerData transactionLoggerData = pluginConfig.getTransactionLoggerData();

			String auditId= UUIDGenerator.generate();
			
			pluginInstData.setAuditUId(auditId);
			
			session.save(pluginInstData);			

			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			transactionLoggerData.setPluginInstanceId(pluginInstanceId);
			session.save(transactionLoggerData);

			String pluginId = transactionLoggerData.getPluginId();
			Set<FormatMappingData> tempSet = transactionLoggerData.getFormatMappingDataSet();
			
			int orderNumber = 1;
			for (FormatMappingData formatMappingData :  tempSet ){
				formatMappingData.setPluginId(pluginId);
				formatMappingData.setOrderNumber(orderNumber);
				session.save(formatMappingData);
				orderNumber++;
			}
			session.flush();
		
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ exp.getMessage(),exp);
		}
	}

	@Override
	public void updateStatus(List<String> pluginInstanceIds, String status) throws DataManagerException {
		String pluginInstanceId = null;
		Session session = getSession();
		Criteria criteria = null;

		for(int i=0;i<pluginInstanceIds.size();i++){
			pluginInstanceId = pluginInstanceIds.get(i);
			criteria = session.createCriteria(PluginInstData.class);
			PluginInstData pluginInstData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginInstanceId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(pluginInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					Criteria newCriteria = session.createCriteria(PluginInstData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")); 					
					List pluginInstanceDataList = newCriteria.list();
					if(pluginInstanceDataList != null && pluginInstanceDataList.size() >0){
						criteria = session.createCriteria(PluginInstData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
					}
				}				
			}
			pluginInstData.setStatus(status);			
			session.update(pluginInstData);			
			session.flush();
		}
	}

	@Override
	public boolean isTransactionLoggerEnabled(String pluginTypeId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginInstData.class);
			List<PluginInstData> pluginInstanceList = criteria.add(Restrictions.eq("pluginTypeId",pluginTypeId)).list();
			if(pluginInstanceList != null && pluginInstanceList.size() > 0){
				return true;
			}
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return false;
	}

	@Override
	public List<PluginInstData> getListOfPluginInstData(List<String> pluginInstanceIds)
			throws DataManagerException {
			
		try{
			List<PluginInstData> pluginInstanceDataList = new ArrayList<PluginInstData>();
			
			Session session = getSession();
			Criteria criteria = null;
			String pluginInstanceId;
			
			if( Collectionz.isNullOrEmpty(pluginInstanceIds) == false){
				for(int i=0;i<pluginInstanceIds.size();i++){
					pluginInstanceId = pluginInstanceIds.get(i);
					criteria = session.createCriteria(PluginInstData.class);
					PluginInstData tempData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginInstanceId)).uniqueResult();
					pluginInstanceDataList.add(tempData);
				}		
			}else{
				criteria = session.createCriteria(PluginInstData.class);
				criteria = session.createCriteria(PluginInstData.class);
				pluginInstanceDataList = criteria.list();
			}

			return pluginInstanceDataList;
		}catch(org.hibernate.exception.ConstraintViolationException cve){
			throw new org.hibernate.exception.ConstraintViolationException(cve.getMessage(),cve.getSQLException() ,cve.getConstraintName());
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public List<PluginInstData> getDiameterPluginList() throws DataManagerException {
		try{	
			
			Session session = getSession();
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.DIAMETER_GROOVY_PLUGIN));
			disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER));
			
			Criteria criteria = session.createCriteria(PluginInstData.class).add(disjunction).add(Restrictions.eq("status", BaseConstant.SHOW_STATUS_ID));
			List pluginInstDataList = criteria.list();
			return pluginInstDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public List<PluginInstData> getPluginInstanceDataList(String servicePolicyName) throws DataManagerException {
		try{	
			
			Session session = getSession();
			Disjunction disjunction = Restrictions.disjunction();
			
			if (servicePolicyName != null && servicePolicyName.isEmpty() == false) {
			
				if (servicePolicyName.equals("RAD-AUTH")) {
				
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.USER_STATISTIC_POST_AUTH_PLUGIN));
					
				} else if (servicePolicyName.equals("RAD-ACCT")) {
				
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN));
				
				} else if (servicePolicyName.equals("RAD-DYNAUTH")) {
					
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
				
				} else if (servicePolicyName.equals("DIAMETER-EAP") || servicePolicyName.equals("DIAMETER-NAS")
						|| servicePolicyName.equals("DIAMETER-CC") || servicePolicyName.equals("DIAMETER-STACK") || servicePolicyName.equals("3GPP AAA SERVER")) {
				
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.DIAMETER_GROOVY_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER));
				
				} else if (servicePolicyName.equals("RM-CHARGING-SERVICE") || servicePolicyName.equals("GTP-PRIME-SERVICE")
						|| servicePolicyName.equals("RM-PREPAID-CHARGING") || servicePolicyName.equals("IPPOOL-SERVICE")
						|| servicePolicyName.equals("IPPOOL-SERVICE") || servicePolicyName.equals("RM_CONCURRENT_LOGIN")) {
					
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_GROOVY_PLUGIN));
					disjunction.add(Restrictions.eq("pluginTypeId", PluginTypesConstants.RADIUS_TRANSACTION_LOGGER));
				} 
			}
			
			Criteria criteria = session.createCriteria(PluginInstData.class).add(disjunction).add(Restrictions.eq("status", BaseConstant.SHOW_STATUS_ID));
			List pluginInstDataList = criteria.list();
			return pluginInstDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public void createQuotaMgtPlugin(CreatePluginConfig pluginConfig) throws DataManagerException {
		PluginInstData pluginInstData = null;
		try{						
			Session session = getSession();
			pluginInstData = pluginConfig.getPluginInstData();

			QuotaMgtPluginData quotaMgtPluginData = pluginConfig.getQuotaMgtPluginData();

			String auditId= UUIDGenerator.generate();
			
			pluginInstData.setAuditUId(auditId);
			
			session.save(pluginInstData);			
			session.flush();

			String pluginInstanceId = pluginInstData.getPluginInstanceId();
			quotaMgtPluginData.setPluginInstanceId(pluginInstanceId);
			session.save(quotaMgtPluginData);
			session.flush();
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ exp.getMessage(),exp);
		}
	}

	@Override
	public QuotaMgtPluginData getQuotaMgtPluginDataByPluginInstanceId( String pluginInstanceId ) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(QuotaMgtPluginData.class);
			QuotaMgtPluginData quotaMgtPluginData = (QuotaMgtPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginInstanceId)).uniqueResult();
			if(quotaMgtPluginData == null){
				throw new InvalidValueException("Quota Management Plugin data not found");
			}
			return quotaMgtPluginData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);

		}
	}

	@Override
	public void updateQuotaMgtPlugin(PluginInstData pluginInstanceData,
			QuotaMgtPluginData quotaMgtPluginData, IStaffData staffData,
			String actionAlias,
			QuotaManagementPluginConfiguration quotaMgtPluginOldObj,
			QuotaManagementPluginConfiguration quotaMgtPluginNewObj)
			throws DataManagerException {

		try{
			Session session = getSession();
			Set<QuotaMgtPluginData> quotaMgtPluginDataSet = new HashSet<QuotaMgtPluginData>();
			
			Criteria criteria = session.createCriteria(PluginInstData.class);

			PluginInstData tempPluginInstanceData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceData.getPluginInstanceId())).uniqueResult();
			if(tempPluginInstanceData == null){
				throw new InvalidValueException("Plugin data not found");
			}
			quotaMgtPluginDataSet.add(quotaMgtPluginData);
			pluginInstanceData.setQuotaMgtPluginDataSet(quotaMgtPluginDataSet);
			JSONArray jsonArray=ObjectDiffer.diff(quotaMgtPluginOldObj,quotaMgtPluginNewObj);
			
			if(tempPluginInstanceData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				tempPluginInstanceData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			criteria = session.createCriteria(QuotaMgtPluginData.class);
			QuotaMgtPluginData quotaMgtPlugin = (QuotaMgtPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceData.getPluginInstanceId())).uniqueResult();
			if(quotaMgtPlugin == null){
				throw new InvalidValueException("Quota Management Plugin data not found");
			}
			quotaMgtPlugin.setPluginData(quotaMgtPluginData.getPluginData());
			quotaMgtPlugin.setPluginInstanceId(quotaMgtPluginData.getPluginInstanceId());
			session.update(quotaMgtPlugin);

			// for plugin instance 
			tempPluginInstanceData.setName(pluginInstanceData.getName());
			tempPluginInstanceData.setDescription(pluginInstanceData.getDescription());
			tempPluginInstanceData.setLastModifiedByStaffId(pluginInstanceData.getLastModifiedByStaffId());
			tempPluginInstanceData.setLastModifiedDate(pluginInstanceData.getLastModifiedDate());
			tempPluginInstanceData.setAuditUId(pluginInstanceData.getAuditUId());
			tempPluginInstanceData.setStatus(pluginInstanceData.getStatus());
			
			session.update(tempPluginInstanceData);
			session.flush();

			Logger.getLogger().info(MODULE, "JSON Array is : "+ jsonArray.toString());
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Quota Management Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Quota Management Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Quota Management Plugin , Reason: " + exp.getMessage(), exp);
		}
	}

	@Override
	public void createUserStatPostAuth(CreatePluginConfig pluginConfig)throws DataManagerException {
		PluginInstData pluginInstData = null;
		try {
			Session session = getSession();
			pluginInstData = pluginConfig.getPluginInstData();

			UserStatPostAuthPluginData userStatisticsData = pluginConfig.getUserStatPostAuthPluginData();
			String auditId= UUIDGenerator.generate();
			pluginInstData.setAuditUId(auditId);
			session.save(pluginInstData);
			session.flush();

			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			userStatisticsData.setPluginInstanceId(pluginInstanceId);
			session.save(userStatisticsData);
			session.flush();
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to create "+ pluginInstData.getName() +", Reason: "+ exp.getMessage(),exp);
		}
	}

	@Override
	public UserStatPostAuthPluginData getUserStatPostAuthPluginDataByPluginInstanceId(String pluginInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(UserStatPostAuthPluginData.class);
			UserStatPostAuthPluginData userStatPostAuthPluginData = (UserStatPostAuthPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginInstanceId)).uniqueResult();
			if(userStatPostAuthPluginData == null){
				throw new InvalidValueException("User Statistic Post Auth Plugin data not found");
			}
			return userStatPostAuthPluginData;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public void updateUserStatPostAuthPlugin(PluginInstData pluginInstanceData,UserStatPostAuthPluginData userStatPostAuthPluginData,IStaffData staffData, String updateActionAlias,UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginOldObj,UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginNewObj)throws DataManagerException {
		
		try{
				Session session = getSession();
				Criteria criteria = session.createCriteria(PluginInstData.class);
				PluginInstData tempPluginInstanceData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceData.getPluginInstanceId())).uniqueResult();
				if(tempPluginInstanceData == null){
					throw new InvalidValueException("Plugin data not found");
				}
				JSONArray jsonArray=ObjectDiffer.diff(userStatisticPostAuthPluginOldObj,userStatisticPostAuthPluginNewObj);
			
				if(tempPluginInstanceData.getAuditUId() == null){
					String auditId= UUIDGenerator.generate();
					tempPluginInstanceData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
			}
			
			for (int j = 0; j < jsonArray.size(); j++) {
				JSONObject handlerObject = jsonArray.getJSONObject(j);
				Object valuesArray = handlerObject.get("values");
				
				if(valuesArray != null){
					JSONArray handlerArray = (JSONArray) valuesArray;
					
					int handlerArrayLen = handlerArray.size();
					for (int k = 0; k < handlerArrayLen; k++) {
						JSONObject rec = handlerArray.getJSONObject(k);
						
						String fieldVal = rec.getString("Field");
						if(fieldVal.equals("PacketType")){
							
							JSONObject newObj=createJSONObjectFromPacketType(rec);
							handlerArray.set(k, newObj);
						}
					}
				}
			}
			criteria = session.createCriteria(UserStatPostAuthPluginData.class);
			UserStatPostAuthPluginData usrStatPostAuthPluginData = (UserStatPostAuthPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceData.getPluginInstanceId())).uniqueResult();
			if(usrStatPostAuthPluginData == null){
				throw new InvalidValueException("User Statistic Post Auth Plugin data not found");
			}
			usrStatPostAuthPluginData.setPluginData(userStatPostAuthPluginData.getPluginData());
			usrStatPostAuthPluginData.setPluginInstanceId(usrStatPostAuthPluginData.getPluginInstanceId());
			session.update(usrStatPostAuthPluginData);

			// for plugin instance 
			tempPluginInstanceData.setName(pluginInstanceData.getName());
			tempPluginInstanceData.setDescription(pluginInstanceData.getDescription());
			tempPluginInstanceData.setLastModifiedByStaffId(pluginInstanceData.getLastModifiedByStaffId());
			tempPluginInstanceData.setLastModifiedDate(pluginInstanceData.getLastModifiedDate());
			tempPluginInstanceData.setAuditUId(pluginInstanceData.getAuditUId());
			tempPluginInstanceData.setStatus(pluginInstanceData.getStatus());
			
			session.update(tempPluginInstanceData);
			session.flush();

			Logger.getLogger().info(MODULE, "JSON Array is : "+ jsonArray.toString());
			
			doAuditingJson(jsonArray.toString(),staffData,updateActionAlias);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update User Statastic Post Auth Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update User Statastic Post Auth Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update User Statastic Post Auth Plugin , Reason: " + exp.getMessage(), exp);
		}
	}
	/**
	 * This method return new JSONObject from old that contains the Id
	 * @param oldJsonObject JSONObject containing Id
	 * @return JSONObject with updated name
	 * @throws NumberFormatException
	 */
	private JSONObject createJSONObjectFromPacketType(JSONObject oldJsonObject) throws NumberFormatException{
		String oldId = oldJsonObject.getString("OldValue");
		String newId = oldJsonObject.getString("NewValue");
		
		String oldName = getCodeFromPacketType(oldId);
		String newName = getCodeFromPacketType(newId);
		
		JSONObject newJsonObj=new JSONObject();
		newJsonObj.put("Field", oldJsonObject.getString("Field"));
		newJsonObj.put("OldValue", oldName);
		newJsonObj.put("NewValue", newName);
		
		return newJsonObj;
	}
	/**
	 * This function get name of  Packet Type, 
	 * @return name of packet type
	 * @throws NumberFormatException
	 */
	private String getCodeFromPacketType(String id) throws NumberFormatException{
		String name = null;
		if(id.equals("0")){
			name = "Request Packet";
		} else if(id.equals("1")){
			name = "Response Packet";
		}
		else
		{
			name = "-";
		}
		return name;
	}

	@Override
	public UniversalPluginData getUniversalPluginDataById(String pluginIdOrName)throws DataManagerException {
		return getUniversalAuthPluginData(PLUGIN_INSTANCE_ID, pluginIdOrName);
	}

	@Override
	public UniversalPluginData getUniversalPluginDataByName(String pluginIdOrName) throws DataManagerException {
	    return getUniversalAuthPluginData(PLUGIN_INSTANCE_NAME , pluginIdOrName);
	}
	
	private UniversalPluginData getUniversalAuthPluginData(String propertyName, Object value) throws DataManagerException {
		String pluginName = (PLUGIN_INSTANCE_NAME.equals(propertyName)) ? (String)value : "Universal Plugin Data";
		try{
			Session session = getSession();
			Criteria pluginDetailCriteria = session.createCriteria(PluginInstData.class);
			PluginInstData pluginData = (PluginInstData) pluginDetailCriteria.add(Restrictions.eq(propertyName, value)).uniqueResult();

			if (pluginData == null) {
				throw new InvalidValueException("Plugin Data not found");
			}

			Criteria criteria = session.createCriteria(UniversalPluginData.class);
			UniversalPluginData universalPluginData = (UniversalPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginData.getPluginInstanceId())).uniqueResult();

			if (universalPluginData == null) {
				throw new InvalidValueException("Universal Plugin Data not found");
			}
			return universalPluginData;
		}catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		}
	}

	@Override
	public List<String> delete(List<String> pluginList) throws DataManagerException {
		String pluginName = "Plugin Instance";
		
		try{
			
			List<String> deletedPluginLst = new ArrayList<String>();
			Session session = getSession();
			
			MultiIdentifierLoadAccess<PluginInstData> multiLoadAccess = session.byMultipleIds(PluginInstData.class);
			List<PluginInstData> pluginInstDataLst = multiLoadAccess.multiLoad(pluginList);
			
			if(Collectionz.isNullOrEmpty(pluginInstDataLst) == false){
				for(PluginInstData pluginInstance : pluginInstDataLst){
					if(pluginInstance != null){
						pluginName = pluginInstance.getName();
						session.delete(pluginInstance);
						session.flush();
						deletedPluginLst.add(pluginName);
					}
				}
			}
		
			return deletedPluginLst;
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+ pluginName +", Reason: "
			+EliteExceptionUtils.extractConstraintName(cve.getSQLException()),cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete "+ pluginName +", Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to delete "+ pluginName +", Reason: "+e.getMessage(),e);
		}
	}
	
	
	@Override
	public void update(PluginInstData pluginInstData,UniversalPluginData universalPluginData,
			IStaffData staffData,
			UniversalPluginConfigurationImpl oldObject,UniversalPluginConfigurationImpl newObject) throws DataManagerException {
		try{
			Session session = getSession();
			Set<UniversalPluginData> universalAuthPluginSet = new HashSet<UniversalPluginData>();
			
			Criteria criteria = session.createCriteria(PluginInstData.class);

			PluginInstData tempPluginInstanceData = (PluginInstData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, universalPluginData.getPluginInstanceId())).uniqueResult();
			
			if(tempPluginInstanceData == null){
				throw new InvalidValueException("Plugin Data Not Found");
			}
			
			universalAuthPluginSet.add(universalPluginData);
			pluginInstData.setUniversalPluginDetails(universalAuthPluginSet);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject,newObject);
			
			criteria = session.createCriteria(UniversalPluginData.class);
			UniversalPluginData tempUnivesalAuthPluginData = (UniversalPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, universalPluginData.getPluginInstanceId())).uniqueResult();
			if(tempUnivesalAuthPluginData == null){
				throw new InvalidValueException("Universal Plugin Data Not Found");
			}

			// in Universal Auth Plugin 
			tempUnivesalAuthPluginData.setPluginData(universalPluginData.getPluginData());

			session.update(tempUnivesalAuthPluginData);

			System.out.println(new String(universalPluginData.getPluginData()));
			
			// for plugin instance 
			tempPluginInstanceData.setName(pluginInstData.getName());
			tempPluginInstanceData.setDescription(pluginInstData.getDescription());
			tempPluginInstanceData.setLastModifiedByStaffId(pluginInstData.getLastModifiedByStaffId());
			tempPluginInstanceData.setLastModifiedDate(pluginInstData.getLastModifiedDate());
			tempPluginInstanceData.setAuditUId(pluginInstData.getAuditUId());
			tempPluginInstanceData.setStatus(pluginInstData.getStatus());
			
			session.update(tempPluginInstanceData);
			session.flush();

			Logger.getLogger().info(MODULE, "JSON Array is : "+ jsonArray.toString());
			
			staffData.setAuditName(tempPluginInstanceData.getName());
			staffData.setAuditId(tempPluginInstanceData.getAuditUId());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_PLUGIN);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Universal Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Universal Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Universal Plugin , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public TransactionLoggerData getTransactionLoggerPluginDataById(String pluginIdOrName)throws DataManagerException {
		return getTransactionLoggerPluginData(PLUGIN_INSTANCE_ID, pluginIdOrName);
	}

	@Override
	public TransactionLoggerData getTransactionLoggerPluginDataByName(String pluginIdOrName) throws DataManagerException {
	    return getTransactionLoggerPluginData(PLUGIN_INSTANCE_NAME , pluginIdOrName);
	}
	
	
	private TransactionLoggerData getTransactionLoggerPluginData(String propertyName, Object value) throws DataManagerException{
		String pluginName = (PLUGIN_INSTANCE_NAME.equals(propertyName)) ? (String)value : "Transaction Logger Plugin Data";
		TransactionLoggerData transactionLoggerData = null;
		try {
			Session session = getSession();
			Criteria pluginDetailCriteria = session.createCriteria(PluginInstData.class);
		    PluginInstData pluginData = (PluginInstData) pluginDetailCriteria.add(Restrictions.eq(propertyName, value)).uniqueResult();
			
			if (pluginData == null) {
				throw new InvalidValueException("Plugin Data not found");
			}
			
			Criteria criteria = session.createCriteria(TransactionLoggerData.class);
		    transactionLoggerData = (TransactionLoggerData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginData.getPluginInstanceId())).uniqueResult();
			
		   if(transactionLoggerData == null){
			   throw new InvalidValueException("Transaction Logger Plugin Data not found");
		   }
		} catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		}
		return transactionLoggerData;
	}

	@Override
	public void updateTransactionLoggerPluginByName(PluginInstData pluginInstanceData,TransactionLoggerData traLoggerData, IStaffData staffData,String name)
			throws DataManagerException {
		updateTransactionLoggerPlugin(pluginInstanceData, traLoggerData, staffData,PLUGIN_INSTANCE_NAME, name);
	}

	@Override
	public void updateTransactionLoggerPluginByID(PluginInstData pluginInstanceData,TransactionLoggerData traLoggerData, IStaffData staffData,
			String pluginInstaceID) throws DataManagerException {
		updateTransactionLoggerPlugin(pluginInstanceData, traLoggerData, staffData, PLUGIN_INSTANCE_ID,pluginInstaceID);
		
	}
	
	private void updateTransactionLoggerPlugin(PluginInstData pluginInstanceData, TransactionLoggerData traLoggerData, IStaffData staffData,String propertyName,Object propertyValue) throws DataManagerException {
		try{
			PluginInstData pluginInstData = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginInstData.class);
			pluginInstData = (PluginInstData)criteria.add(Restrictions.eq(propertyName,propertyValue)).uniqueResult();
			
			if(pluginInstData == null){
				throw new InvalidValueException("Plugin Data not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(pluginInstData,pluginInstanceData);
			
			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			criteria = session.createCriteria(TransactionLoggerData.class);

			TransactionLoggerData transactionLoggerDataObj = (TransactionLoggerData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceId)).uniqueResult();

			if(transactionLoggerDataObj == null){
				throw new InvalidValueException("Transaction Logger Plugin Data not found");
			}
			String pluginId = transactionLoggerDataObj.getPluginId();

			criteria = session.createCriteria(FormatMappingData.class);
			List<FormatMappingData> formatMappingDataList = criteria.add(Restrictions.eq("pluginId",pluginId)).list();
			if(Collectionz.isNullOrEmpty(formatMappingDataList) == false){
				for (FormatMappingData formatMappingData : formatMappingDataList) {
					session.delete(formatMappingData);
					session.flush();
				}
			}

			Set<FormatMappingData> formatMappingDataSet = traLoggerData.getFormatMappingDataSet();

			int orderNumber = 1;
			for( FormatMappingData formatMappingData : formatMappingDataSet ){
				formatMappingData.setPluginId(pluginId);
				formatMappingData.setOrderNumber(orderNumber);
				formatMappingDataSet.add(formatMappingData);
				session.save(formatMappingData);
				session.flush();
				orderNumber++;
			}
		
			// Save Data's for Groovy Plugin 
			transactionLoggerDataObj.setPluginInstanceId(pluginInstanceId);
			transactionLoggerDataObj.setLogFile(traLoggerData.getLogFile());
			transactionLoggerDataObj.setRange(traLoggerData.getRange());
			transactionLoggerDataObj.setPattern(traLoggerData.getPattern());
			transactionLoggerDataObj.setGlobalization(traLoggerData.getGlobalization());
			transactionLoggerDataObj.setTimeBoundry(traLoggerData.getTimeBoundry());
			session.update(transactionLoggerDataObj);
			session.flush();

			pluginInstData.setName(pluginInstanceData.getName());
			pluginInstData.setDescription(pluginInstanceData.getDescription());
			pluginInstData.setLastModifiedByStaffId(pluginInstanceData.getLastModifiedByStaffId());
			pluginInstData.setLastModifiedDate(pluginInstanceData.getLastModifiedDate());
			pluginInstData.setStatus(pluginInstanceData.getStatus());
			
			session.update(pluginInstData);
			session.flush();
			
			staffData.setAuditName(pluginInstData.getName());
			staffData.setAuditId(pluginInstData.getAuditUId());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_PLUGIN);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Transaction Logger Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Transaction Logger Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Transaction Logger Plugin , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public PluginInstData getPluginInstDataByName(String pluginName) throws DataManagerException{
		PluginInstData pluginData = null;
		try {
			Session session = getSession();
			Criteria pluginDetailCriteria = session.createCriteria(PluginInstData.class);
		    pluginData = (PluginInstData) pluginDetailCriteria.add(Restrictions.eq(PLUGIN_INSTANCE_NAME, pluginName)).uniqueResult();
			
		    if(pluginData == null){
		    	throw new InvalidValueException("Plugin Data not found");
		    }
		} catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive , Reason: " + exp.getMessage(), exp);
		}
		return pluginData;
	}
	
	@Override
	public void updateGroovyPluginByID(PluginInstData pluginInstDatas,GroovyPluginData groovyPluginData, IStaffData staffData,String pluginInstanceId) throws DataManagerException {
		 updateGroovyPlugin(pluginInstDatas, groovyPluginData, staffData, PLUGIN_INSTANCE_ID, pluginInstanceId);
	}

	@Override
	public void updateGroovyPluginByName(PluginInstData pluginInstDatas,GroovyPluginData groovyPluginData, IStaffData staffData, String name) throws DataManagerException {
		updateGroovyPlugin(pluginInstDatas, groovyPluginData, staffData, PLUGIN_INSTANCE_NAME, name);
	}
	
	private void updateGroovyPlugin(PluginInstData pluginInstanceData, GroovyPluginData groovyPluginData, IStaffData staffData,String propertyName,Object propertyValue) throws DataManagerException{
		try{
			PluginInstData pluginInstData = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(PluginInstData.class);
			pluginInstData = (PluginInstData)criteria.add(Restrictions.eq(propertyName,propertyValue)).uniqueResult();
			
			if(pluginInstData == null){
				throw new InvalidValueException("Plugin Data not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(pluginInstData,pluginInstanceData);
			
			String pluginInstanceId = pluginInstData.getPluginInstanceId();

			criteria = session.createCriteria(GroovyPluginData.class);

			GroovyPluginData groovyPluginDataObj = (GroovyPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID, pluginInstanceId)).uniqueResult();

			if(groovyPluginDataObj == null){
				throw new InvalidValueException("Groovy Plugin Data not found");
			}
			
			String pluginId = groovyPluginDataObj.getPluginId();

			criteria = session.createCriteria(GroovyPluginFile.class);
			List<GroovyPluginFile> groovyPluginFileList = criteria.add(Restrictions.eq("pluginId",pluginId)).list();
			
			if(Collectionz.isNullOrEmpty(groovyPluginFileList) == false){
				for (GroovyPluginFile groovyPluginFile : groovyPluginFileList) {
					session.delete(groovyPluginFile);
					session.flush();
				}
			}

			Set<GroovyPluginFile> groovyPluginFileSet = groovyPluginData.getGroovyPluginFileSet();
			if(Collectionz.isNullOrEmpty(groovyPluginFileSet) == false){
				int orderNumber = 1;
				for( GroovyPluginFile groovyPluginFile : groovyPluginFileSet ){
					groovyPluginFile.setPluginId(pluginId);
					groovyPluginFile.setOrderNumber(orderNumber);
					groovyPluginFileSet.add(groovyPluginFile);
					session.save(groovyPluginFile);
					session.flush();
					orderNumber++;
				}
			}
		
			// Save Data's for Groovy Plugin 
			groovyPluginDataObj.setPluginInstanceId(pluginInstanceId);
			session.update(groovyPluginDataObj);
			session.flush();

			pluginInstData.setName(pluginInstanceData.getName());
			pluginInstData.setDescription(pluginInstanceData.getDescription());
			pluginInstData.setLastModifiedByStaffId(pluginInstanceData.getLastModifiedByStaffId());
			pluginInstData.setLastModifiedDate(pluginInstanceData.getLastModifiedDate());
			pluginInstData.setStatus(pluginInstanceData.getStatus());
			
			session.update(pluginInstData);
			session.flush();
			
			staffData.setAuditId(pluginInstanceData.getAuditUId());
			staffData.setAuditName(pluginInstanceData.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_PLUGIN);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Groovy Plugin , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public GroovyPluginData getGroovyPluginDataByName(String name) throws DataManagerException {
		return getGroovyPluginData(PLUGIN_INSTANCE_NAME, name);
	}

	@Override
	public GroovyPluginData getGroovyPluginDataById(String pluginIdOrName) throws DataManagerException {
		return getGroovyPluginData(PLUGIN_INSTANCE_ID, pluginIdOrName);
	}
	
	private GroovyPluginData getGroovyPluginData(String propertyName, Object proprtyValue) throws DataManagerException{
		String pluginName = (PLUGIN_INSTANCE_NAME.equals(propertyName)) ? (String)proprtyValue : "Groovy Plugin Data";
		try{
			Session session = getSession();
			Criteria pluginDetailCriteria = session.createCriteria(PluginInstData.class);
			PluginInstData pluginData = (PluginInstData) pluginDetailCriteria.add(Restrictions.eq(propertyName,proprtyValue)).uniqueResult();

			if (pluginData == null) {
				throw new InvalidValueException("Plugin Data not found");
			}

			Criteria criteria = session.createCriteria(GroovyPluginData.class);
			GroovyPluginData groovyPluginData = (GroovyPluginData)criteria.add(Restrictions.eq(PLUGIN_INSTANCE_ID,pluginData.getPluginInstanceId())).uniqueResult();

			if (groovyPluginData == null) {
				throw new InvalidValueException("Groovy Plugin Data not found");
			}
			return groovyPluginData;
		}catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + pluginName +", Reason: " + exp.getMessage(), exp);
		}
	}
}
