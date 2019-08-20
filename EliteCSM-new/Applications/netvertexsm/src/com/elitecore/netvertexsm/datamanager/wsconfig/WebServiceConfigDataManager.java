/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   WebServiceConfigDataManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.datamanager.wsconfig;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;

public interface WebServiceConfigDataManager extends DataManager{

	public void updateSubscriberConfiguration(WSConfigData subscriberDBConfigData) throws DataManagerException;
	public WSConfigData getSubscriberConfiguration() throws DataManagerException;
	public WSConfigData getSessionMgmtConfiguration() throws DataManagerException;
	
}
