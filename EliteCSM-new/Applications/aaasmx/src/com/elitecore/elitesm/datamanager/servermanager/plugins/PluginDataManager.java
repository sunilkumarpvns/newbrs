package com.elitecore.elitesm.datamanager.servermanager.plugins;

import java.util.List;

import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.IPluginServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.UniversalPluginConfigurationImpl;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginDetails;

/**
 * @author nayana.rathod
 *
 */

public interface PluginDataManager extends DataManager{
	
	public PageList search(PluginInstData pluginInstanceData, int requiredPageNo, Integer pageSize) throws DataManagerException;
	public List<IPluginServiceTypeData> getListOfAllPluginsServiceData() throws DataManagerException;
	public String createUniversalPlugin(CreatePluginConfig pluginConfig)throws DataManagerException;
	public PluginInstData getPluginInstanceData(String pluginInstanceId)throws DataManagerException;
	public PluginInstData getPluginInstanceByPluginInstanceId(String pluginInstanceId)throws DataManagerException;
	public void update(PluginInstData pluginInstData, UniversalPluginData universalPluginData, IStaffData staffData, String actionAlias, DiameterUniversalPluginDetails oldObject, DiameterUniversalPluginDetails newObject)throws DataManagerException;
	public List<PluginInstData> getAuthPluginList()throws DataManagerException;
	public List<PluginInstData> getAcctPluginList()throws DataManagerException;
	public String createGroovyPlugin(CreatePluginConfig pluginConfig)throws DataManagerException;
	public GroovyPluginFile getGroovyPluginFileByName(String groovyFileName, String pluginInstanceId)throws DataManagerException;
	public void createTransactionLogger(CreatePluginConfig pluginConfig)throws DataManagerException;
	public void updateTransactionLoggerPluginByName(PluginInstData pluginInstanceData, TransactionLoggerData traLoggerData, IStaffData staffData, String idOrName)throws DataManagerException;
	public void updateStatus(List<String> asList, String status)throws DataManagerException;
	public boolean isTransactionLoggerEnabled(String pluginType)throws DataManagerException;
	public List getListOfPluginInstData(List<String> pluginInstanceIds)throws DataManagerException;
	public List<PluginInstData> getDiameterPluginList()throws DataManagerException;
	public List<PluginInstData> getPluginInstanceDataList(String servicePolicyName)throws DataManagerException;
	
	public void createQuotaMgtPlugin(CreatePluginConfig pluginConfig)throws DataManagerException;
	public QuotaMgtPluginData getQuotaMgtPluginDataByPluginInstanceId(String pluginInstanceId)throws DataManagerException;
	public void updateQuotaMgtPlugin(PluginInstData pluginInstanceData, QuotaMgtPluginData quotaMgtPluginData, IStaffData staffData, String updateActionAlias, QuotaManagementPluginConfiguration quotaMgtPluginOldObj, QuotaManagementPluginConfiguration quotaMgtPluginNewObj)throws DataManagerException;
	
	/**
	 * This Function Creates New UserStatisticPostAuth Plugin 
	 * @param pluginConfig contain Plugin Configuration
	 * @throws DataManagerException
	 */
	public void createUserStatPostAuth(CreatePluginConfig pluginConfig)throws DataManagerException;
	
	/**
	 * This Function Is Retriving Data From Database For UserStatisticPostAuth Plugin
	 * @param pluginInstanceId contain ID Of Plugin
	 * @return userStatPostAuthPluginData UI Plugin Data
	 * @throws DataManagerException
	 */
	public UserStatPostAuthPluginData getUserStatPostAuthPluginDataByPluginInstanceId(String pluginInstanceId)throws DataManagerException;
	
	/**
	 *  This Function Is Used To Update UserStatisticPostAuth Plugin Data 
	 * @param pluginInstanceData Contain All Plugin Data
	 * @param userStatPostAuthPluginData Plugin Data
	 * @param staffData contain staff information
	 * @param updateActionAlias contain Plugin Action
	 * @param userStatisticPostAuthPluginOldObj contain Old Value of Plugin
	 * @param userStatisticPostAuthPluginNewObj contain New Value of Plugin
 	 * @throws DataManagerException
	 */
	public void updateUserStatPostAuthPlugin(PluginInstData pluginInstanceData, UserStatPostAuthPluginData userStatPostAuthPluginData, IStaffData staffData, String updateActionAlias, UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginOldObj, UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginNewObj)throws DataManagerException;
	public UniversalPluginData getUniversalPluginDataById(String pluginIdOrName) throws DataManagerException; 
	public UniversalPluginData getUniversalPluginDataByName(String pluginIdOrName) throws DataManagerException;
	public void update(PluginInstData pluginInstData,UniversalPluginData universalPluginData,IStaffData staffData,
			UniversalPluginConfigurationImpl oldObject,UniversalPluginConfigurationImpl newObject) throws DataManagerException;
	public TransactionLoggerData getTransactionLoggerPluginDataById(String pluginIdOrName) throws DataManagerException;
	public TransactionLoggerData getTransactionLoggerPluginDataByName(String pluginIdOrName) throws DataManagerException;
	public void updateTransactionLoggerPluginByID(PluginInstData pluginInstanceData,TransactionLoggerData traLoggerData, IStaffData staffData, String pluginInstaceID) throws DataManagerException;
	PluginInstData getPluginInstDataByName(String pluginName) throws DataManagerException;
	public void updateGroovyPluginByID(PluginInstData pluginInstDatas,GroovyPluginData pluginData, IStaffData staffData,String pluginInstanceId) throws DataManagerException;
	public void updateGroovyPluginByName(PluginInstData pluginInstDatas,GroovyPluginData pluginData, IStaffData staffData, String idOrName) throws DataManagerException;
	public GroovyPluginData getGroovyPluginDataByName(String name) throws DataManagerException;
	public GroovyPluginData getGroovyPluginDataById(String pluginIdOrName) throws DataManagerException;
	void updateGroovyFile(GroovyPluginFile groovyPluginFileNewObject,
			IStaffData staffData, PluginInstData pluginInstData)
			throws DataManagerException;
	public List<String> delete(List<String> pluginList) throws DataManagerException;
}
