/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;

public interface DiameterPeerProfileDataManager extends DataManager{
	
	public PageList searchDiameterPeerProfile(DiameterPeerProfileData diameterPeerProfile, Map infoMap) throws DataManagerException;
	
	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException ;

	public String getPeerProfileIdByPeerProfileName(String peerProfileName) throws DataManagerException;

	public DiameterPeerProfileData getDiameterPeerProfileById(String diameterPeerProfileId) throws DataManagerException;

	public DiameterPeerProfileData getDiameterPeerProfileByName(String diameterPeerProfileName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;

	public void updateDiameterPeerProfileById(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileId) throws DataManagerException;

	public void updateDiameterPeerProfileByName(DiameterPeerProfileData diameterPeerProfile, IStaffData staffData, String diameterPeerProfileName) throws DataManagerException;

	public String deleteDiameterPeerProfileById(String diameterPeerProfileId) throws DataManagerException;

	public String deleteDiameterPeerProfileByName(String diameterPeerProfileName) throws DataManagerException;
	
}
