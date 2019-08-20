package com.elitecore.elitesm.web.diameter.subscriberroutingtable.form;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.IMSIMappingData;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.MSISDNMappingData;

public class SubscriberRoutingTableForm {

	/* Searching Information */
	private String subscriberDetails;

	/* IMSI based Routing table attributes */
	private Long imsiBasedroutingTableId;
	private String imsiBasedRoutingTableName;
	private long imsiPageNumber;
	private long imsiTotalPages;
	private long imsiTotalRecords;
	private Collection imsiBasedRoutingConfList;
	private List listIMSIBasedRoutingTable;
	private String imsiIdentityAttributes;
	private List<DiameterPeerData> diameterPeerDataList;
	private Set<IMSIFieldMappingData> imsiFieldMappingSet;
	private String imsiSearchAction;
	
	/* MSISDN based Routing table attributes */
	private String msisdnSearchAction;
	private Long msisdnBasedroutingTableId;
	private String msisdnBasedRoutingTableName;
	private long msisdnPageNumber;
	private long msisdnTotalPages;
	private long msisdnTotalRecords;
	private Collection msisdnBasedRoutingConfList;
	private List listMSISDNBasedRoutingTable;
	private String msisdnIdentityAttributes;
	private Set<MSISDNFieldMappingData> msisdnFieldMappingSet;

	/* Search IMSI Criteria */
	private List<IMSIMappingData> imsiMappingDataList;
	private long imsiMappingPageNumber;
	private long imsiMappingTotalPages;
	private long imsiMappingTotalRecords;
	
	/*Search MSISDN Criteria*/
	private long msisdnMappingPageNumber;
	private long msisdnMappingTotalPages;
	private long msisdnMappingTotalRecords;
	private List<MSISDNMappingData> msisdnMappingDataList;
	
	/* Other properties */
	private boolean searchRange = false;
	private boolean searchImsi = false;
	private boolean searchMsisdn = false;
	private Long auditUId; 
	private String action;
	
	public String getSubscriberDetails() {
		return subscriberDetails;
	}
	public void setSubscriberDetails(String subscriberDetails) {
		this.subscriberDetails = subscriberDetails;
	}
	public Long getImsiBasedroutingTableId() {
		return imsiBasedroutingTableId;
	}
	public void setImsiBasedroutingTableId(Long imsiBasedroutingTableId) {
		this.imsiBasedroutingTableId = imsiBasedroutingTableId;
	}
	public String getImsiBasedRoutingTableName() {
		return imsiBasedRoutingTableName;
	}
	public void setImsiBasedRoutingTableName(String imsiBasedRoutingTableName) {
		this.imsiBasedRoutingTableName = imsiBasedRoutingTableName;
	}
	public long getImsiPageNumber() {
		return imsiPageNumber;
	}
	public void setImsiPageNumber(long imsiPageNumber) {
		this.imsiPageNumber = imsiPageNumber;
	}
	public long getImsiTotalPages() {
		return imsiTotalPages;
	}
	public void setImsiTotalPages(long imsiTotalPages) {
		this.imsiTotalPages = imsiTotalPages;
	}
	public long getImsiTotalRecords() {
		return imsiTotalRecords;
	}
	public void setImsiTotalRecords(long imsiTotalRecords) {
		this.imsiTotalRecords = imsiTotalRecords;
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
	public String getImsiIdentityAttributes() {
		return imsiIdentityAttributes;
	}
	public void setImsiIdentityAttributes(String imsiIdentityAttributes) {
		this.imsiIdentityAttributes = imsiIdentityAttributes;
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
	public Long getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(Long auditUId) {
		this.auditUId = auditUId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getImsiSearchAction() {
		return imsiSearchAction;
	}
	public void setImsiSearchAction(String imsiSearchAction) {
		this.imsiSearchAction = imsiSearchAction;
	}
	public String getMsisdnSearchAction() {
		return msisdnSearchAction;
	}
	public void setMsisdnSearchAction(String msisdnSearchAction) {
		this.msisdnSearchAction = msisdnSearchAction;
	}
	public Long getMsisdnBasedroutingTableId() {
		return msisdnBasedroutingTableId;
	}
	public void setMsisdnBasedroutingTableId(Long msisdnBasedroutingTableId) {
		this.msisdnBasedroutingTableId = msisdnBasedroutingTableId;
	}
	public String getMsisdnBasedRoutingTableName() {
		return msisdnBasedRoutingTableName;
	}
	public void setMsisdnBasedRoutingTableName(String msisdnBasedRoutingTableName) {
		this.msisdnBasedRoutingTableName = msisdnBasedRoutingTableName;
	}
	public long getMsisdnPageNumber() {
		return msisdnPageNumber;
	}
	public void setMsisdnPageNumber(long msisdnPageNumber) {
		this.msisdnPageNumber = msisdnPageNumber;
	}
	public long getMsisdnTotalPages() {
		return msisdnTotalPages;
	}
	public void setMsisdnTotalPages(long msisdnTotalPages) {
		this.msisdnTotalPages = msisdnTotalPages;
	}
	public long getMsisdnTotalRecords() {
		return msisdnTotalRecords;
	}
	public void setMsisdnTotalRecords(long msisdnTotalRecords) {
		this.msisdnTotalRecords = msisdnTotalRecords;
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
	public String getMsisdnIdentityAttributes() {
		return msisdnIdentityAttributes;
	}
	public void setMsisdnIdentityAttributes(String msisdnIdentityAttributes) {
		this.msisdnIdentityAttributes = msisdnIdentityAttributes;
	}
	public Set<MSISDNFieldMappingData> getMsisdnFieldMappingSet() {
		return msisdnFieldMappingSet;
	}
	public void setMsisdnFieldMappingSet(
			Set<MSISDNFieldMappingData> msisdnFieldMappingSet) {
		this.msisdnFieldMappingSet = msisdnFieldMappingSet;
	}
	public boolean isSearchRange() {
		return searchRange;
	}
	public void setSearchRange(boolean searchRange) {
		this.searchRange = searchRange;
	}
	public boolean isSearchMsisdn() {
		return searchMsisdn;
	}
	public void setSearchMsisdn(boolean searchMsisdn) {
		this.searchMsisdn = searchMsisdn;
	}
	public boolean isSearchImsi() {
		return searchImsi;
	}
	public void setSearchImsi(boolean searchImsi) {
		this.searchImsi = searchImsi;
	}
	public List<MSISDNMappingData> getMsisdnMappingDataList() {
		return msisdnMappingDataList;
	}
	public void setMsisdnMappingDataList(List<MSISDNMappingData> msisdnMappingDataList) {
		this.msisdnMappingDataList = msisdnMappingDataList;
	}
	public List<IMSIMappingData> getImsiMappingDataList() {
		return imsiMappingDataList;
	}
	public void setImsiMappingDataList(List<IMSIMappingData> imsiMappingDataList) {
		this.imsiMappingDataList = imsiMappingDataList;
	}
	public long getImsiMappingPageNumber() {
		return imsiMappingPageNumber;
	}
	public void setImsiMappingPageNumber(long imsiMappingPageNumber) {
		this.imsiMappingPageNumber = imsiMappingPageNumber;
	}
	public long getImsiMappingTotalPages() {
		return imsiMappingTotalPages;
	}
	public void setImsiMappingTotalPages(long imsiMappingTotalPages) {
		this.imsiMappingTotalPages = imsiMappingTotalPages;
	}
	public long getImsiMappingTotalRecords() {
		return imsiMappingTotalRecords;
	}
	public void setImsiMappingTotalRecords(long imsiMappingTotalRecords) {
		this.imsiMappingTotalRecords = imsiMappingTotalRecords;
	}
	public long getMsisdnMappingPageNumber() {
		return msisdnMappingPageNumber;
	}
	public void setMsisdnMappingPageNumber(long msisdnMappingPageNumber) {
		this.msisdnMappingPageNumber = msisdnMappingPageNumber;
	}
	public long getMsisdnMappingTotalPages() {
		return msisdnMappingTotalPages;
	}
	public void setMsisdnMappingTotalPages(long msisdnMappingTotalPages) {
		this.msisdnMappingTotalPages = msisdnMappingTotalPages;
	}
	public long getMsisdnMappingTotalRecords() {
		return msisdnMappingTotalRecords;
	}
	public void setMsisdnMappingTotalRecords(long msisdnMappingTotalRecords) {
		this.msisdnMappingTotalRecords = msisdnMappingTotalRecords;
	}
}
