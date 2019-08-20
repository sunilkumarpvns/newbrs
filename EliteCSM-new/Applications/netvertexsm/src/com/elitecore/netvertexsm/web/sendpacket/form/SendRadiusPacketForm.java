package com.elitecore.netvertexsm.web.sendpacket.form;

import java.io.File;
import java.util.List;

import org.apache.struts.action.ActionForm;

public class SendRadiusPacketForm extends ActionForm {
	/**
	 * @author ishani.bhatt
	 * this  class will get the packet configuration from GUI and pass it to PacketConfiguration object
	 */
	private static final long serialVersionUID = 1L;
	private Long packetId;
	private String name;
	private String ipAddress;
	private Long port;
	private Long timeOut;
	private String packetData;
	public String action;
	public String secretKey;
	
	public long totalPages;
	public long totalRecords;
	public long pageNumber;
	public List<File> lstSendPacketData;
	public String responseData;
	
	public Long getPacketId() {
		return packetId;
	}
	public void setPacketId(Long packetId) {
		this.packetId = packetId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public Long getPort() {
		return port;
	}
	public void setPort(Long port) {
		this.port = port;
	}
	public Long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
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
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getPacketData() {
		return packetData;
	}
	public void setPacketData(String packetData) {
		this.packetData = packetData;
	}
	public List<File> getLstSendPacketData() {
		return lstSendPacketData;
	}
	public void setLstSendPacketData(List<File> lstSendPacketData) {
		this.lstSendPacketData = lstSendPacketData;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secret) {
		this.secretKey = secret;
	}
	public String getResponseData() {
		return responseData;
	}
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	
}
