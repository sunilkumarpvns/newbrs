package com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable.form;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MSISDNBasedRoutingTableForm extends BaseWebForm {
	
	/**
	 * @author Nayana Rathod
	 */
	
	private static final long serialVersionUID = 1L;
	private String routingTableId;
	private String msisdnBasedRoutingTableName;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private Collection msisdnBasedRoutingConfList;
	private List listMSISDNBasedRoutingTable;
	private String searchBy;
	private String searchValue;
	private String auditUId; 
	private String msisdnIdentityAttributes="21067:65599.21067:65600";
	private List<DiameterPeerData> diameterPeerDataList;
	private Set<MSISDNFieldMappingData> msisdnFieldMappingSet;
	private FormFile fileUpload;
	private Long msisdnLength=10L;
	private String mcc;
	
	public String getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getMsisdnBasedRoutingTableName() {
		return msisdnBasedRoutingTableName;
	}
	public void setMsisdnBasedRoutingTableName(String msisdnBasedRoutingTableName) {
		this.msisdnBasedRoutingTableName = msisdnBasedRoutingTableName;
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
	public Collection getMsisdnBasedRoutingConfList() {
		return msisdnBasedRoutingConfList;
	}
	public void setMsisdnBasedRoutingConfList(Collection msisdnBasedRoutingConfList) {
		this.msisdnBasedRoutingConfList = msisdnBasedRoutingConfList;
	}
	public List getListMSISDNBasedRoutingTable() {
		return listMSISDNBasedRoutingTable;
	}
	public void setListMSISDNBasedRoutingTable(List listMSISDNBasedRoutingTable) {
		this.listMSISDNBasedRoutingTable = listMSISDNBasedRoutingTable;
	}
	public String getSearchBy() {
		return searchBy;
	}
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getMsisdnIdentityAttributes() {
		return msisdnIdentityAttributes;
	}
	public void setMsisdnIdentityAttributes(String msisdnIdentityAttributes) {
		this.msisdnIdentityAttributes = msisdnIdentityAttributes;
	}
	public List<DiameterPeerData> getDiameterPeerDataList() {
		return diameterPeerDataList;
	}
	public void setDiameterPeerDataList(List<DiameterPeerData> diameterPeerDataList) {
		this.diameterPeerDataList = diameterPeerDataList;
	}
	public Set<MSISDNFieldMappingData> getMsisdnFieldMappingSet() {
		return msisdnFieldMappingSet;
	}
	public void setMsisdnFieldMappingSet(
			Set<MSISDNFieldMappingData> msisdnFieldMappingSet) {
		this.msisdnFieldMappingSet = msisdnFieldMappingSet;
	}
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public Long getMsisdnLength() {
		return msisdnLength;
	}
	public void setMsisdnLength(Long msisdnLength) {
		this.msisdnLength = msisdnLength;
	}
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	
	@Override
	public String toString() {
		return "MSISDNBasedRoutingTableForm [routingTableId=" + routingTableId
				+ ", msisdnBasedRoutingTableName="
				+ msisdnBasedRoutingTableName + ", pageNumber=" + pageNumber
				+ ", totalPages=" + totalPages + ", totalRecords="
				+ totalRecords + ", action=" + action
				+ ", msisdnBasedRoutingConfList=" + msisdnBasedRoutingConfList
				+ ", listMSISDNBasedRoutingTable="
				+ listMSISDNBasedRoutingTable + ", searchBy=" + searchBy
				+ ", searchValue=" + searchValue + ", auditUId=" + auditUId
				+ ", msisdnIdentityAttributes=" + msisdnIdentityAttributes
				+ ", diameterPeerDataList=" + diameterPeerDataList
				+ ", msisdnFieldMappingSet=" + msisdnFieldMappingSet
				+ ", fileUpload=" + fileUpload + ", msisdnLength="
				+ msisdnLength + ", mcc=" + mcc + "]";
	}
}