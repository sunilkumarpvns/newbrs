package com.elitecore.elitesm.blmanager.servermgr.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermanager.plugins.PluginDataManager;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.IPluginServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.UniversalPluginConfigurationImpl;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginDetails;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class PluginBLManager extends BaseBLManager{
	
	private static final String GROOVY_PLUGIN = "GROOVY_PLUGIN";
	private static final String UNIVERSAL_PLUGIN = "UNIVERSAL_PLUGIN";
	private static final String FAILURE = "FAILURE";
	private static final String SUCCESS = "SUCCESS";
	private static final String MODULE = "PluginBLManager";

	public void createUniversalPlugin(CreatePluginConfig createPluginConfigs, IStaffData staffData) throws TableDoesNotExistException, DatabaseConnectionException, DataManagerException{
		List<CreatePluginConfig> pluginConfigs = new ArrayList<CreatePluginConfig>();
		pluginConfigs.add(createPluginConfigs);
		createUniversalPlugin(pluginConfigs, staffData, "false");

	}

	public Map<String, List<Status>> createUniversalPlugin(List<CreatePluginConfig> createPluginConfigs, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertPluginRecords(createPluginConfigs, staffData, partialSuccess, UNIVERSAL_PLUGIN);
	}

	public List<IPluginServiceTypeData> getListOfAllPluginsServiceData() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<IPluginServiceTypeData> pluginServiceList = null;
		try {
			session.beginTransaction();
			pluginServiceList = pluginDataManager.getListOfAllPluginsServiceData();
		} catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginServiceList;
	}

	private PluginDataManager getPluginDataManager(IDataManagerSession session) {
		PluginDataManager pluginDataManager = (PluginDataManager)DataManagerFactory.getInstance().getDataManager(PluginDataManager.class, session);
		return pluginDataManager;	
	}

	public PageList search(PluginInstData pluginInstanceData, int requiredPageNo, Integer pageSize ,IStaffData staffData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		PageList pluginList = null;
		try{				
			pluginList = pluginDataManager.search(pluginInstanceData,requiredPageNo,pageSize);
			staffData.setAuditName(pluginInstanceData.getName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_PLUGIN);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginList;
	}

	public void deletePluginById(List<String> pluginToDelete, IStaffData staffData) throws DataManagerException {
		delete(pluginToDelete, staffData, BY_ID);
	}

	public void deletePluginByName(List<String> pluginToDelete, IStaffData staffData) throws DataManagerException {
		delete(pluginToDelete, staffData, BY_NAME);
	}

	public void delete(List<String> pluginInstanceIdOrName, IStaffData staffData, boolean deleteByIdOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			List<String> pluginList = new ArrayList<String>();
			for (String pluginToDelete : pluginInstanceIdOrName) {
				if (deleteByIdOrName) {
					pluginList.add(pluginToDelete);
				} else {
					PluginInstData pluginInstData = pluginDataManager.getPluginInstDataByName(pluginToDelete.trim());
					pluginList.add(pluginInstData.getPluginInstanceId());
				}
			}
			
			if (Collectionz.isNullOrEmpty(pluginList) == false) {
				
				List<String> pluginNames = pluginDataManager.delete(pluginList);
				if(Collectionz.isNullOrEmpty(pluginNames) == false){
					
					for(String strDriverName : pluginNames){
						staffData.setAuditName(strDriverName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_PLUGIN);
					}
				}
			}
			
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public PluginInstData getPluginInstanceData(String pluginInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		PluginInstData pluginInstanceData = null;

		try{
			pluginInstanceData = pluginDataManager.getPluginInstanceData(pluginInstanceId);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginInstanceData;
	}

	public PluginInstData getListOfPluginInstanceByPluginInstanceId( String pluginInstanceId ) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		PluginInstData pluginInstData = null;
		try{				
			pluginInstData = pluginDataManager.getPluginInstanceByPluginInstanceId(pluginInstanceId);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginInstData;
	}

	public UniversalPluginData getUniversalAuthPluginDataByPluginInstanceName( String pluginInstanceName ) throws DataManagerException {
		return getUniversalAuthPluginData(pluginInstanceName.trim() , BY_NAME);
	}

	public UniversalPluginData getUniversalAuthPluginDataByPluginInstanceId( String pluginInstanceId ) throws DataManagerException {
		return getUniversalAuthPluginData(pluginInstanceId , BY_ID);
	}

	public UniversalPluginData getUniversalAuthPluginData(Object pluginIdOrName, boolean getByIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{	
			if (getByIdOrName) {
				return pluginDataManager.getUniversalPluginDataById((String)pluginIdOrName);
			} else {
				return pluginDataManager.getUniversalPluginDataByName((String)pluginIdOrName);
			}
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<PluginInstData> getAuthPluginList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<PluginInstData> pluginInstDataList = null;
		try{				
			pluginInstDataList = pluginDataManager.getAuthPluginList();
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginInstDataList;
	}

	public List<PluginInstData> getAcctPluginList()  throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<PluginInstData> pluginInstDataList = null;
		try{				
			pluginInstDataList = pluginDataManager.getAcctPluginList();
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginInstDataList;
	}

	public GroovyPluginFile getGroovyPluginFileByName(String groovyFileName, String pluginInstanceId)  throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			GroovyPluginFile groovyPluginFile = pluginDataManager.getGroovyPluginFileByName(groovyFileName.trim(), pluginInstanceId);
			return groovyPluginFile;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateGroovyFile(GroovyPluginFile groovyPluginFileNewObject,IStaffData staffData, PluginInstData pluginInstData)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			pluginDataManager.updateGroovyFile(groovyPluginFileNewObject, staffData, pluginInstData);
			commit(session);
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void createTransactionLogger(List<CreatePluginConfig> createPluginConfigs, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();

			for (CreatePluginConfig createPluginConfig : createPluginConfigs) {

				pluginDataManager.createTransactionLogger(createPluginConfig);

				staffData.setAuditName(createPluginConfig.getPluginInstData().getName());
				AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_PLUGIN);
			}
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void createTransactionLogger(CreatePluginConfig pluginConfig,IStaffData staffData) throws DataManagerException {
		List<CreatePluginConfig> pluginConfigs = new ArrayList<CreatePluginConfig>();
		pluginConfigs.add(pluginConfig);
		createTransactionLogger(pluginConfigs, staffData);
	}

	public void updateTransactionLoggerByName(PluginInstData pluginInstanceData, TransactionLoggerData traLoggerData, IStaffData staffData, String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();

			if(name == null){
				pluginDataManager.updateTransactionLoggerPluginByID(pluginInstanceData, traLoggerData,staffData,pluginInstanceData.getPluginInstanceId());
			}else{
				pluginDataManager.updateTransactionLoggerPluginByName(pluginInstanceData, traLoggerData,staffData,name.trim());
			}
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateTransactionLoggerByID(PluginInstData pluginInstanceData, TransactionLoggerData traLoggerData, IStaffData staffData) throws DataManagerException {
		updateTransactionLoggerByName(pluginInstanceData,traLoggerData, staffData, null);
	}

	public void updatePluginStatus(List<String> asList, String status) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			PluginDataManager pluginDataManager = getPluginDataManager(session);

			if(pluginDataManager==null){
				throw new DataManagerException("Data Manager Not Found for " + getClass().getName());
			}

			session.beginTransaction();
			pluginDataManager.updateStatus(asList, status);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public boolean isTransactionLoggerEnabled(String pluginTypeId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			boolean isTransactionLoggerEnabled = pluginDataManager.isTransactionLoggerEnabled(pluginTypeId);
			return isTransactionLoggerEnabled;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<PluginInstData> getListOfPluginInstData(List<String> pluginInstanceIds) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			List pluginInstDataList = pluginDataManager.getListOfPluginInstData(pluginInstanceIds);
			return pluginInstDataList;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<PluginInstData> getDiameterPluginList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<PluginInstData> pluginInstDataList = null;
		try{				
			pluginInstDataList = pluginDataManager.getDiameterPluginList();
			return pluginInstDataList;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<PluginInstData> getPluginInstanceDataList(String servicePolicyName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<PluginInstData> pluginInstDataList = null;
		try{				
			pluginInstDataList = pluginDataManager.getPluginInstanceDataList(servicePolicyName);
			return pluginInstDataList;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void createQuotaMgtPlugin(CreatePluginConfig pluginConfig,IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();

			pluginDataManager.createQuotaMgtPlugin(pluginConfig);
			staffData.setAuditName(pluginConfig.getPluginInstData().getName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_PLUGIN);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public QuotaMgtPluginData getQuotaMgtPluginDataByPluginInstanceId(String pluginInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{			
			QuotaMgtPluginData quotaMgtPluginData = pluginDataManager.getQuotaMgtPluginDataByPluginInstanceId(pluginInstanceId);
			return quotaMgtPluginData;
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateQuotaMgtPlugin(PluginInstData pluginInstanceData,
			QuotaMgtPluginData quotaMgtPluginData, IStaffData staffData,
			String updateActionAlias,
			QuotaManagementPluginConfiguration quotaMgtPluginOldObj,
			QuotaManagementPluginConfiguration quotaMgtPluginNewObj)
					throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();

			pluginDataManager.updateQuotaMgtPlugin(pluginInstanceData,quotaMgtPluginData, staffData, updateActionAlias,quotaMgtPluginOldObj, quotaMgtPluginNewObj);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void createUserStatPostAuthPlugin(CreatePluginConfig pluginConfig,IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();

			pluginDataManager.createUserStatPostAuth(pluginConfig);
			staffData.setAuditName(pluginConfig.getPluginInstData().getName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_PLUGIN);
			commit(session);
		} catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public UserStatPostAuthPluginData getUserStatPostAuthPluginDataByPluginInstanceId(String pluginInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{			
			UserStatPostAuthPluginData userStatPostAuthPluginData = pluginDataManager.getUserStatPostAuthPluginDataByPluginInstanceId(pluginInstanceId);
			return userStatPostAuthPluginData;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateUserStatPostAuthPlugin(PluginInstData pluginInstanceData,UserStatPostAuthPluginData userStatPostAuthPluginData, IStaffData staffData,String updateActionAlias,UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginOldObj,UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginNewObj)throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();

			pluginDataManager.updateUserStatPostAuthPlugin(pluginInstanceData, userStatPostAuthPluginData, staffData, updateActionAlias, userStatisticPostAuthPluginOldObj, userStatisticPostAuthPluginNewObj);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * This is used to check whether given plugin is binded with any NAS service policy.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInNASPolicy(String pluginName) throws DataManagerException{
		Set<String> pluginNameInNASPolicy=new HashSet<String>();
		pluginNameInNASPolicy.addAll(new DiameterNASPolicyBLManager().getBindedPluginNames());

		if (pluginNameInNASPolicy != null && pluginNameInNASPolicy.isEmpty() == false) {
			if(pluginNameInNASPolicy.contains(pluginName)){
				return "NAS Policy";
			}
		}
		return "";
	}

	/**
	 * This is used to check whether given plugin is binded with any EAP service policy.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInEAPPolicy(String pluginName)throws DataManagerException{
		Set<String> pluginNameInEAPPolicy=new HashSet<String>();
		pluginNameInEAPPolicy.addAll(new EAPPolicyBLManager().getBindedPluginNames());

		if (pluginNameInEAPPolicy != null && pluginNameInEAPPolicy.isEmpty() == false) {
			if(pluginNameInEAPPolicy.contains(pluginName)){
				return "EAP Policy";
			}
		}
		return "";
	}

	/**
	 * This is used to check whether given plugin is binded with any CC service policy.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInCCPolicy(String pluginName)throws DataManagerException{
		Set<String> pluginNameInCCPolicy=new HashSet<String>();
		pluginNameInCCPolicy.addAll(new CreditControlPolicyBLManager().getBindedPluginNames());
		if (pluginNameInCCPolicy != null && pluginNameInCCPolicy.isEmpty() == false) {
			if(pluginNameInCCPolicy.contains(pluginName)){
				return "Credit Control Policy";
			}
		}
		return "";
	}

	/**
	 * This is used to check whether given plugin is binded with any TGPP service policy.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInTGPPPolicy(String pluginName) throws DataManagerException{
		Set<String> pluginNameInTgppPolicy=new HashSet<String>();
		pluginNameInTgppPolicy.addAll(new TGPPAAAPolicyBLManager().getBindedPluginNames("plugin-name"));
		if (pluginNameInTgppPolicy != null && pluginNameInTgppPolicy.isEmpty() == false) {
			if(pluginNameInTgppPolicy.contains(pluginName)){
				return "3GPP AAA Policy";
			}
		}
		return "";
	}

	/**
	 * This is used to check whether given plugin is binded with any Radius service policy.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInRadiusPolicy(String pluginName) throws DataManagerException{
		Set<String> pluginNameInRadiusPolicy = new HashSet<String>();
		pluginNameInRadiusPolicy.addAll(new ServicePolicyBLManager().getBindedPluginNames("plugin-name"));
		if (pluginNameInRadiusPolicy != null && pluginNameInRadiusPolicy.isEmpty() == false) {
			if(pluginNameInRadiusPolicy.contains(pluginName)){
				return "Radius Service Policy";
			}
		}
		return "";
	}

	/**
	 * This is used to check whether given plugin is binded with Server Instance.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInServerInstance(String pluginName) throws DataManagerException{
		Set<String> pluginNameInServerInstance = new HashSet<String>();
		pluginNameInServerInstance.addAll(new NetServerBLManager().getBindedPluginNames());
		if (pluginNameInServerInstance != null && pluginNameInServerInstance.isEmpty() == false) {
			if(pluginNameInServerInstance.contains(pluginName)){
				return "Server Instance";
			}
		}
		return "";
	}
	/**
	 * This is used to check whether given plugin is binded with Configured services.
	 * @param pluginName contains plugin-name to be check
	 * @return Where plugin is binded 
	 * @throws DataManagerException 
	 */
	public String checkPluginBindInServerInstanceServices(String pluginName) throws DataManagerException{
		Set<String> pluginNameInServerInstanceServices = new HashSet<String>();
		pluginNameInServerInstanceServices.addAll(new NetServiceBLManager().getBindedPluginNames());
		if (pluginNameInServerInstanceServices != null && pluginNameInServerInstanceServices.isEmpty() == false) {
			if(pluginNameInServerInstanceServices.contains(pluginName)){
				return "Configured Services";
			}
		}
		return "";
	}
	/**
	 * This method used convert set of source where plugin is binded in usable string message to be display user.
	 * @param pluginBinded source places where plugin is binded
	 * @return formated string
	 */
	public String getFormatedStringForPlugin(Set<String> pluginBinded){
		StringBuffer formatedString = new StringBuffer();
		if(pluginBinded.isEmpty() == false || pluginBinded != null ){
			for(String bindPlace : pluginBinded){
				if(bindPlace != null  && bindPlace.trim().isEmpty() == false){
					formatedString.append(bindPlace+", ");
				}
			}
		}
		String formatedPlugin = formatedString.toString();
		if (formatedPlugin.isEmpty() == false){
			return formatedString.substring(0, formatedPlugin.length()-2);
		}else{
			return "";
		}
	}

	public TransactionLoggerData getTransactionLoggerPluginDataByName( String pluginInstanceName ) throws DataManagerException {
		return getTransactionLoggerData(pluginInstanceName.trim() , BY_NAME);
	}

	public TransactionLoggerData getTransactionLoggerPluginDataByID( String pluginInstanceId ) throws DataManagerException {
		return getTransactionLoggerData(pluginInstanceId , BY_ID);
	}

	public TransactionLoggerData getTransactionLoggerData(Object pluginIdOrName, boolean getByIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{	
			if (getByIdOrName) {
				return pluginDataManager.getTransactionLoggerPluginDataById((String)pluginIdOrName);
			} else {
				return pluginDataManager.getTransactionLoggerPluginDataByName((String)pluginIdOrName);
			}
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public PluginInstData getPluginInstDataByName(String pluginName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		PluginInstData pluginInstData = null;

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{	
			pluginInstData = pluginDataManager.getPluginInstDataByName(pluginName.trim());
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return pluginInstData;
	}

	public void createGroovyPlugin(CreatePluginConfig pluginConfig,IStaffData staffData) throws DataManagerException {
		List<CreatePluginConfig> pluginConfigs = new ArrayList<CreatePluginConfig>();
		pluginConfigs.add(pluginConfig);
		createGroovyPluginByName(pluginConfigs,staffData, "false");
	}
	public Map<String, List<Status>> createGroovyPluginByName(List<CreatePluginConfig> createPluginConfigs, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertPluginRecords(createPluginConfigs, staffData, partialSuccess, GROOVY_PLUGIN);
	}

	public void update(PluginInstData pluginInstanceData,GroovyPluginData groovyPluginData, IStaffData staffData,String actionAlias) throws DataManagerException {
		updateGroovyPluginByName(pluginInstanceData,groovyPluginData, staffData, null);
	}

	public void updateGroovyPluginByName(PluginInstData pluginInstDatas,GroovyPluginData pluginData,IStaffData staffData,String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			if(name == null){
				pluginDataManager.updateGroovyPluginByID(pluginInstDatas, pluginData,staffData,pluginInstDatas.getPluginInstanceId());
			}else{
				pluginDataManager.updateGroovyPluginByName(pluginInstDatas, pluginData,staffData,name.trim());
			}
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public GroovyPluginData getGroovyPluginDataByName( String pluginInstanceName ) throws DataManagerException {
		return getGroovyPluginData(pluginInstanceName.trim() , BY_NAME);
	}

	public GroovyPluginData getGroovyPluginDataByPluginInstanceId( String pluginInstanceId ) throws DataManagerException {
		return getGroovyPluginData(pluginInstanceId , BY_ID);
	}

	private GroovyPluginData getGroovyPluginData(Object pluginIdOrName, boolean getByIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{	
			if (getByIdOrName) {
				return pluginDataManager.getGroovyPluginDataById((String)pluginIdOrName);
			} else {
				return pluginDataManager.getGroovyPluginDataByName((String)pluginIdOrName);
			}
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateDiameterUniversalPlugin(PluginInstData pluginInstData, UniversalPluginData universalPluginData, IStaffData staffData, DiameterUniversalPluginDetails oldObject, DiameterUniversalPluginDetails newObject) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			pluginDataManager.update(pluginInstData, universalPluginData,staffData,ConfigConstant.UPDATE_PLUGIN, oldObject, newObject);
			commit(session);
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

	}

	public void updateRadiusUniversalPlugin(PluginInstData pluginInstData, UniversalPluginData universalPluginData, IStaffData staffData, UniversalPluginConfigurationImpl oldObject, UniversalPluginConfigurationImpl newObject) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);

		if (pluginDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			pluginDataManager.update(pluginInstData, universalPluginData, staffData, oldObject, newObject);
			commit(session);
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action Failed .",de);
		} finally {
			closeSession(session);
		}
	}
	
	private Map<String, List<Status>> insertPluginRecords(List<?> records, IStaffData staffData, String partialSuccess, String pluginType) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PluginDataManager pluginDataManager = getPluginDataManager(session);
		
		ArrayList<Status> successRecordList = new ArrayList<Status>();
		ArrayList<Status> failureRecordList = new ArrayList<Status>();

		Map<String, List<Status>> responseMap  = new HashMap<String,List<Status>>();
		try{
			if(Collectionz.isNullOrEmpty(records) == false){

				for (Iterator<?> iterator = records.iterator(); iterator.hasNext();) {
					session.beginTransaction();
					Object object =  iterator.next();
					try {
						String name = "";
						if(UNIVERSAL_PLUGIN.equalsIgnoreCase(pluginType)){
							name = pluginDataManager.createUniversalPlugin((CreatePluginConfig) object);
						} else if(GROOVY_PLUGIN.equalsIgnoreCase(pluginType)){
							name = pluginDataManager.createGroovyPlugin((CreatePluginConfig) object);
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_PLUGIN);

						if(partialSuccess.equalsIgnoreCase("true")){
							commit(session);
								successRecordList.add(new Status(ResultCode.SUCCESS.responseCode, name + " created successfully"));
						}
					} catch (DataManagerException e) {
						if(partialSuccess.equalsIgnoreCase("true")){

							failureRecordList.add(new Status(RestUtitlity.getResultCode(e), e.getMessage()));
							rollbackSession(session);
						} else {
							rollbackSession(session);
							throw e;
						}
					}
				}
				responseMap.put(SUCCESS, successRecordList);
				responseMap.put(FAILURE, failureRecordList);
				if(partialSuccess.equalsIgnoreCase("false") || partialSuccess.isEmpty()){
					commit(session);
				}
			}
		}catch(DataManagerException exp){
			Logger.logTrace(MODULE, exp);
			throw exp;
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException("failed to perform bulk operation, Reason: "+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return responseMap;
	}
}