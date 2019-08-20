package com.elitecore.elitesm.web.rm.ippool.forms;

import java.sql.Timestamp;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class IPPoolForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	public static final String NO_OF_IP_ADDRESS = "1";
	private String ipPoolId;
	private String name;
	private String activeStatus = "1";
	private String description;
	private String nasIPAddress;
	private String ipAddress;
	private String ruleSet;	
	private String additionalAttributes;
	private FormFile fileUpload;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private String status = "2";
	private List<IIPPoolData> ipPoolList;
	
	private String[] ipAddressRanges;
	private String[] rangeId;
	private String[] oldIPAddressRange;
	private String inputMode;
	private int totalNumber;
	private String startIPAddress;
	private String endIPAddress;
	private String createdByStaffId;
	private Timestamp statusChangedDate;
	private Timestamp createDate;
	private String auditUId;
	
	public String getIpPoolId() {
		return ipPoolId;
	}
	public void setIpPoolId(String ipPoolId) {
		this.ipPoolId = ipPoolId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public String getNasIPAddress() {
		return nasIPAddress;
	}
	public void setNasIPAddress(String nasIPAddress) {
		this.nasIPAddress = nasIPAddress;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getAdditionalAttributes() {
		return additionalAttributes;
	}
	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<IIPPoolData> getIpPoolList() {
		return ipPoolList;
	}
	public void setIpPoolList(List<IIPPoolData> ipPoolList) {
		this.ipPoolList = ipPoolList;
	}
	public String[] getIpAddressRanges() {
		return ipAddressRanges;
	}
	public void setIpAddressRanges(String[] ipAddressRanges) {
		this.ipAddressRanges = ipAddressRanges;
	}
	public String[] getOldIPAddressRange() {
		return oldIPAddressRange;
	}
	public void setOldIPAddressRange(String[] oldIPAddressRange) {
		this.oldIPAddressRange = oldIPAddressRange;
	}
	public String[] getRangeId() {
		return rangeId;
	}
	public void setRangeId(String[] rangeId) {
		this.rangeId = rangeId;
	}
	public String getInputMode() {
		return inputMode;
	}
	public void setInputMode(String inputMode) {
		this.inputMode = inputMode;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	public String getStartIPAddress() {
		return startIPAddress;
	}
	public void setStartIPAddress(String startIPAddress) {
		this.startIPAddress = startIPAddress;
	}
	public String getEndIPAddress() {
		return endIPAddress;
	}
	public void setEndIPAddress(String endIPAddress) {
		this.endIPAddress = endIPAddress;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getStatusChangedDate() {
		return statusChangedDate;
	}
	public void setStatusChangedDate(Timestamp statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return "IPPoolForm [ipPoolId=" + ipPoolId + ", name=" + name
				+ ", nasIPAddress=" + nasIPAddress + ", ipAddress=" + ipAddress
				+ ", action=" + action + "]";
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	
}
