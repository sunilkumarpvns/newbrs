/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   WebServiceConfigDataManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.wsconfig;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSDBFieldMapData;

public interface WebServiceConfigDataManager extends DataManager{

	public void updateSubscriberConfiguration(IWSConfigData subscriberDBConfigData, IStaffData staffData) throws DataManagerException;
	public IWSConfigData getSubscriberConfiguration() throws DataManagerException;
	public List<IWSDBFieldMapData> getSubscriberDBFeildMapList(Integer dbConfigId)  throws DataManagerException;
	
	public IWSConfigData getSessionMgmtConfiguration() throws DataManagerException;
	public void updateSessionMgmtConfiguration(IWSConfigData sessionMgmtConfigData, IStaffData staffData) throws DataManagerException;
	
}
