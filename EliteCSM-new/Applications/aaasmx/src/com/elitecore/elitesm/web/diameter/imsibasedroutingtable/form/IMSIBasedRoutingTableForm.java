package com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class IMSIBasedRoutingTableForm extends BaseWebForm {
	
	/**
	 * @author Nayana Rathod
	 */
	
	private static final long serialVersionUID = 1L;
	private String routingTableId;
	private String imsiBasedRoutingTableName;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private Collection imsiBasedRoutingConfList;
	private List listIMSIBasedRoutingTable;
	private String searchBy;
	private String searchValue;
	private String auditUId; 
	private String imsiIdentityAttributes="21067:65599.21067:65601";
	private List<DiameterPeerData> diameterPeerDataList;
	private Set<IMSIFieldMappingData> imsiFieldMappingSet;
	private FormFile fileUpload;
	
	public String getImsiBasedRoutingTableName() {
		return imsiBasedRoutingTableName;
	}
	public void setImsiBasedRoutingTableName(String imsiBasedRoutingTableName) {
		this.imsiBasedRoutingTableName = imsiBasedRoutingTableName;
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
	public Collection getImsiBasedRoutingConfList() {
		return imsiBasedRoutingConfList;
	}
	public void setImsiBasedRoutingConfList(Collection imsiBasedRoutingConfList) {
		this.imsiBasedRoutingConfList = imsiBasedRoutingConfList;
	}
	public List getListIMSIBasedRoutingTable() {
		return listIMSIBasedRoutingTable;
	}
	public void setListIMSIBasedRoutingTable(List listIMSIBasedRoutingTable) {
		this.listIMSIBasedRoutingTable = listIMSIBasedRoutingTable;
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
	
	public String getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getImsiIdentityAttributes() {
		return imsiIdentityAttributes;
	}
	public void setImsiIdentityAttributes(String imsiIdentityAttributes) {
		this.imsiIdentityAttributes = imsiIdentityAttributes;
	}
	
	@Override
	public String toString() {
		return "SearchIMSIBasedRoutingTableForm [routingTableId="
				+ routingTableId + ", imsiBasedRoutingTableName="
				+ imsiBasedRoutingTableName + ", pageNumber=" + pageNumber
				+ ", totalPages=" + totalPages + ", totalRecords="
				+ totalRecords + ", action=" + action
				+ ", imsiBasedRoutingConfList=" + imsiBasedRoutingConfList
				+ ", listIMSIBasedRoutingTable=" + listIMSIBasedRoutingTable
				+ ", searchBy=" + searchBy + ", searchValue=" + searchValue
				+ ", auditUId=" + auditUId + ", imsiIdentityAttributes="
				+ imsiIdentityAttributes + "]";
	}
	public List<DiameterPeerData> getDiameterPeerDataList() {
		return diameterPeerDataList;
	}
	public void setDiameterPeerDataList(List<DiameterPeerData> diameterPeerDataList) {
		this.diameterPeerDataList = diameterPeerDataList;
	}
	public Set<IMSIFieldMappingData> getImsiFieldMappingSet() {
		return imsiFieldMappingSet;
	}
	public void setImsiFieldMappingSet(Set<IMSIFieldMappingData> imsiFieldMappingSet) {
		this.imsiFieldMappingSet = imsiFieldMappingSet;
	}
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
}