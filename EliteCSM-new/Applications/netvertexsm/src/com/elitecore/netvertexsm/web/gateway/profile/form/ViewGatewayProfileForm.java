package com.elitecore.netvertexsm.web.gateway.profile.form;

import org.apache.struts.action.ActionForm;

/**
 * @author Harsh Patel
 *
 */
public class ViewGatewayProfileForm extends ActionForm {

	private long profileId;
	private String commProtocolId;
	private int vendorId;
	private String firmware;
	private int maxThroughput;
	private int bufferBw;
	private String description;
	
	private String action;
	private String status;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	
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
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getCommProtocolId() {
		return commProtocolId;
	}
	public void setCommProtocolId(String commProtocolId) {
		this.commProtocolId = commProtocolId;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public int getMaxThroughput() {
		return maxThroughput;
	}
	public void setMaxThroughput(int maxThroughput) {
		this.maxThroughput = maxThroughput;
	}
	public int getBufferBw() {
		return bufferBw;
	}
	public void setBufferBw(int bufferBw) {
		this.bufferBw = bufferBw;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
