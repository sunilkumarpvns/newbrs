package com.elitecore.netvertexsm.web.gateway.profile.form;

import java.util.Collection;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;




public class MiscProfileForm extends BaseWebForm{
	
	private String  name;
	private boolean show;
	private boolean hide;
	private boolean all; 
	private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private String action;
	private String status;
	private List<GatewayProfileData> listSearchProfile; 
	private List profileList;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean getAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public boolean getHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	
	
	public int getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public boolean getShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<GatewayProfileData> getListSearchProfile() {
		return listSearchProfile;
	}
	public void setListSearchProfile(List<GatewayProfileData> listSearchProfile) {
		this.listSearchProfile = listSearchProfile;
	}
	public List getProfileList() {
		return profileList;
	}
	public void setProfileList(List profileList) {
		this.profileList = profileList;
	}
		
}

