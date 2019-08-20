package com.elitecore.elitesm.web.diameter.diameterepeergroup.form;

import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DiameterPeerGroupForm extends BaseWebForm {
	
	/**
	 * @author Nayana Rathod
	 */
	
	private static final long serialVersionUID = 1L;
	private String peerGroupId;
	private String peerGroupName;
	private String description;
	private String primaryPeerId;
	private String primaryPeerName;
	private String secondaryPeerId;
	private String secondaryPeerName;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private String auditUId;
	
	private List<DiameterPeerGroup> diameterPeerGroupList;
	private List<DiameterPeerData> diameterPeerDataList;
	private List<DiameterPeerRelationWithPeerGroup> peerList;
	
	private String stateful;
	private Long transactionTimeout=2000l;
	private String geoRedunduntGroup;
	private String geoRedunduntGroupName;
	
	public String getStateful() {
		return stateful;
	}

	public void setStateful(String statefull) {
		this.stateful = statefull;
	}

	public Long getTransactionTimeout() {
		return transactionTimeout;
	}

	public void setTransactionTimeout(Long transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}
	
	public List<DiameterPeerRelationWithPeerGroup> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<DiameterPeerRelationWithPeerGroup> peerList) {
		this.peerList = peerList;
	}
	public String getPeerGroupId() {
		return peerGroupId;
	}
	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
	public String getPeerGroupName() {
		return peerGroupName;
	}
	public void setPeerGroupName(String peerGroupName) {
		this.peerGroupName = peerGroupName;
	}
	public String getPrimaryPeerId() {
		return primaryPeerId;
	}
	public void setPrimaryPeerId(String primaryPeerId) {
		this.primaryPeerId = primaryPeerId;
	}
	public String getSecondaryPeerId() {
		return secondaryPeerId;
	}
	public void setSecondaryPeerId(String secondaryPeerId) {
		this.secondaryPeerId = secondaryPeerId;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public List<DiameterPeerGroup> getDiameterPeerGroupList() {
		return diameterPeerGroupList;
	}
	public void setDiameterPeerGroupList(List<DiameterPeerGroup> diameterPeerGroupList) {
		this.diameterPeerGroupList = diameterPeerGroupList;
	}
	public List<DiameterPeerData> getDiameterPeerDataList() {
		return diameterPeerDataList;
	}
	public void setDiameterPeerDataList(List<DiameterPeerData> diameterPeerDataList) {
		this.diameterPeerDataList = diameterPeerDataList;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrimaryPeerName() {
		return primaryPeerName;
	}
	public void setPrimaryPeerName(String primaryPeerName) {
		this.primaryPeerName = primaryPeerName;
	}
	public String getSecondaryPeerName() {
		return secondaryPeerName;
	}
	public void setSecondaryPeerName(String secondaryPeerName) {
		this.secondaryPeerName = secondaryPeerName;
	}

	public String getGeoRedunduntGroup() {
		return geoRedunduntGroup;
	}

	public void setGeoRedunduntGroup(String geoRedunduntGroup) {
		this.geoRedunduntGroup = geoRedunduntGroup;
	}

	public String getGeoRedunduntGroupName() {
		return geoRedunduntGroupName;
	}

	public void setGeoRedunduntGroupName(String geoRedunduntGroupName) {
		this.geoRedunduntGroupName = geoRedunduntGroupName;
	}
}