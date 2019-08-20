/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpolicy;

import java.sql.Timestamp;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;

public interface DiameterPolicyDataManager extends DataManager{
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public PageList search(DiameterPolicyData diameterPolicyData, int pageNo, int pageSize) throws DataManagerException;
	
	public String updateStatus(String diameterPolicyId,String commonStatus, Timestamp statusChangeDate) throws DataManagerException;
	
	public DiameterPolicyData getDiameterPolicyDataById(String diameterPolicyId) throws DataManagerException;
	
	public DiameterPolicyData getDiameterPolicyDataByName(String searchVal) throws DataManagerException;

	public String deleteById(String parseLong) throws DataManagerException;

	public String deleteByName(String radiusPolicyIdOrName) throws DataManagerException;

	public void updateByName(DiameterPolicyData diameterPolicyData, IStaffData staffData, String trim) throws DataManagerException;

	public void updateById(DiameterPolicyData diameterPolicyData, IStaffData staffData, String diameterPolicyId) throws DataManagerException;
}
