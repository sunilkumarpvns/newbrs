/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDiameterPeerProfileForm.java                 		
 * ModualName PeerProfiles    			      		
 * Created on 5 March, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDiameterPeerForm extends BaseWebForm{

	private String hostIdentity;	
	private String name;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String actionType;
	private Collection diameterPeerList;
	private List<DiameterPeerData> listDiameterPeer;
	private List<DiameterPeerProfileData> peerProfileList;
	private String peerProfileId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHostIdentity() {
		return hostIdentity;
	}
	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public Collection getDiameterPeerList() {
		return diameterPeerList;
	}
	public void setDiameterPeerList(Collection diameterPeerList) {
		this.diameterPeerList = diameterPeerList;
	}
	public List<DiameterPeerData> getListDiameterPeer() {
		return listDiameterPeer;
	}
	public void setListDiameterPeer(List<DiameterPeerData> listDiameterPeer) {
		this.listDiameterPeer = listDiameterPeer;
	}
	public List<DiameterPeerProfileData> getPeerProfileList() {
		return peerProfileList;
	}
	public void setPeerProfileList(List<DiameterPeerProfileData> peerProfileList) {
		this.peerProfileList = peerProfileList;
	}
	public String getPeerProfileId() {
		return peerProfileId;
	}
	public void setPeerProfileId(String peerProfileId) {
		this.peerProfileId = peerProfileId;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
		
}
