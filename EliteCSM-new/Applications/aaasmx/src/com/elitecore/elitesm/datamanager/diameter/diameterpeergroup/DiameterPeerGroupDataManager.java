/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterPeerDataManager.java                 		
 * ModualName diameterpeer    			      		
 * Created on 13 march, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpeergroup;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;

public interface DiameterPeerGroupDataManager extends DataManager{
	
	public PageList searchDiameterPeerGroupData( DiameterPeerGroup diameterPeerGroupData, int requiredPageNo, Integer pageSize)throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;

	public List<DiameterPeerGroup> getDiameterPeerGroupList()throws DataManagerException;

	public String deleteByName(String diameterPeerGroupName) throws DataManagerException;

	public String deleteById(String diameterPeerGroupId) throws DataManagerException;

	public void updateById(DiameterPeerGroup diameterPeerGroup, IStaffData staffData) throws DataManagerException;

	public void updateByName(DiameterPeerGroup diameterPeerGroup, IStaffData staffData, String diameterPeerGroupName) throws DataManagerException;

	public DiameterPeerGroup getDiameterPeerGroupDataById(String diameterPeerGroupId) throws DataManagerException;

	public DiameterPeerGroup getDiameterPeerGroupDataByName(String diameterPeerGroupName) throws DataManagerException;

	public List<DiameterPeerGroup> getDiameterPeerGroupListExceptSelf(String peerGroupId) throws DataManagerException;


}
