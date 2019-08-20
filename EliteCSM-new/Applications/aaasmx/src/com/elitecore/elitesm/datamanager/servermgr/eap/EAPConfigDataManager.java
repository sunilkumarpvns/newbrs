package com.elitecore.elitesm.datamanager.servermgr.eap;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPSimAkaConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IEAPConfigData;

public interface EAPConfigDataManager extends DataManager{

	@Override
	public String create(Object object) throws DataManagerException;

	public void defaultEAPConfigCreate(IEAPConfigData eapConfigData) throws DataManagerException;

	public List<EAPConfigData> getEAPConfigList() throws DataManagerException;
	
	/**
	 * This is used to get EAP configuration by it's id
	 * @param eapConfigId id of EAP configuration
	 * @return EAPConfigData
	 * @throws DataManagerException
	 */
	public EAPConfigData getEAPConfigDataById(String eapConfigId) throws DataManagerException;
	
	/**
	 * This is used to get EAP configuration by it's name
	 * @param eapConfigName name of EAP configuration
	 * @return EAPConfigData
	 * @throws DataManagerException
	 */
	public EAPConfigData getEAPConfigDataByName(String eapConfigName) throws DataManagerException;

	/**
	 * this delete EAP configuration by it's configuration id
	 * @param eapId configuration id
	 * @return name of EAPConfiguration which will deleted
	 * @throws DataManagerException
	 */
	public String deleteById(String eapId) throws DataManagerException;
	
	/**
	 * this delete EAP configuration by it's configuration name
	 * @param eapConfigName configuration id
	 * @return name of EAPConfiguration which will deleted
	 * @throws DataManagerException
	 */
	public String deleteByName(String eapConfigName) throws DataManagerException;

	public PageList search(EAPConfigData eapConfigData,String[] enableAuthMethodarry, int requiredPageNo, Integer pageSize) throws DataManagerException;

	public void updateEapBasicDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias)throws DataManagerException;

	public void updateEapTLSDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public void updateEapGsmDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public void updateSupportedAuthMethods(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException;

	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException;

	public EAPSimAkaConfigData getEapGsmDetails(EAPConfigData eapConfigData)throws DataManagerException;
	
	public String getEapNameFromId(String eapId)throws DataManagerException; 
	
	public String getServerCertificateIdFromName(String serverCertificateName) throws DataManagerException;
	
	public String getServerCertificateNameFromId(String serverCertificateId) throws DataManagerException;
	
	/**
	 * this update EAP configuration by it's name
	 * @param eapConfigData EAP configuration data to be update
	 * @param staffData user information who make upadte operation
	 * @param eapConfigName EAP configuration name 
	 * @param actionAlias 
	 * @throws DataManagerException
	 */
	public void updateEapConfigDataByName(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias,String eapConfigName)throws DataManagerException;
	
}
