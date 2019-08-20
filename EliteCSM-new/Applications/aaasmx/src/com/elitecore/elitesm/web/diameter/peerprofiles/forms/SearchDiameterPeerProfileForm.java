/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDiameterPeerProfileForm.java                 		
 * ModualName PeerProfiles    			      		
 * Created on 5 March, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDiameterPeerProfileForm extends BaseWebForm{

	private String  profileName;	
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String actionType;
	private Collection diameterPeerProfileList;
	private List listDiameterPeerProfile;
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
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
	
	public Collection getDiameterPeerProfileList() {
		return diameterPeerProfileList;
	}
	public void setDiameterPeerProfileList(Collection diameterPeerProfileList) {
		this.diameterPeerProfileList = diameterPeerProfileList;
	}
	public List getListDiameterPeerProfile() {
		return listDiameterPeerProfile;
	}
	public void setListDiameterPeerProfile(List listDiameterPeerProfile) {
		this.listDiameterPeerProfile = listDiameterPeerProfile;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
}
