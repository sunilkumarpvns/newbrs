/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   GracePolicyDataManager.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.servermgr.gracepolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;

public interface GracePolicyDataManager extends DataManager{

	List<GracepolicyData> getGracePolicyList() throws DataManagerException;

	GracepolicyData getGracePolicyById(String gracePolicyId) throws DataManagerException;
	GracepolicyData getGracePolicyByName(String gracePolicyName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	void updateGracePolicyById(GracepolicyData gracePolicy, String gracePolicyId, IStaffData staffData) throws DataManagerException;
	void updateGracePolicyByName(GracepolicyData gracePolicy, String gracePolicyName, IStaffData staffData) throws DataManagerException;

	String deleteGracePolicyById(String gracePolicyId, IStaffData staffData) throws DataManagerException;
	String deleteGracePolicyByName(String gracePolicyName, IStaffData staffData) throws DataManagerException;
	
}
