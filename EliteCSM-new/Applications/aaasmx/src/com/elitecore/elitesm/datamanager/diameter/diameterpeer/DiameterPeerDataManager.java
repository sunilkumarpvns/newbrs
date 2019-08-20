/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterPeerDataManager.java                 		
 * ModualName diameterpeer    			      		
 * Created on 13 march, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpeer;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;

public interface DiameterPeerDataManager extends DataManager{
	
	public PageList search(DiameterPeerData diameterPeerData,Map infoMap) throws DataManagerException;
	
	public List<DiameterPeerData> getDiameterPeerList() throws DataManagerException;

	public List<DiameterPeerProfileData> getPeerProfileList() throws DataManagerException;
	
	public String getDiameterPeerNameById(String peerId)throws DataManagerException;

	public String getDiameterPeerIdByName(String peerName) throws DataManagerException;

	public DiameterPeerData getDiameterPeerById(String diameterPeerId) throws DataManagerException;

	public DiameterPeerData getDiameterPeerByName(String diameterPeerName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;

	public void updateDiameterPeerById(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerId) throws DataManagerException;

	public void updateDiameterPeerByName(DiameterPeerData diameterPeer, IStaffData staffData, String diameterPeerName) throws DataManagerException;

	public String deleteDiameterPeerById(String diameterPeerId) throws DataManagerException;

	public String deleteDiameterPeerByName(String diameterPeerName) throws DataManagerException;

}
