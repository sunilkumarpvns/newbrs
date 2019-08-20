package com.elitecore.netvertexsm.web.gateway.attrmapping.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;

public class PacketMappingForm extends ActionForm {
	private long packetMapId;
	private String name;
	private String description;
	private String commProtocol;
	private String packetType;
	private String type;
	private int orderNumber;
	
	private String status;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	
	private List<PacketMappingData> listSearchPacketMapping;
	private List<AttributeMappingData> attributeMappings;
	private PacketMappingData packetMappingData;
		
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public PacketMappingData getPacketMappingData() {
		return packetMappingData;
	}
	public void setPacketMappingData(PacketMappingData packetMappingData) {
		this.packetMappingData = packetMappingData;
	}
	public long getPacketMapId() {
		return packetMapId;
	}
	public void setPacketMapId(long packetMapId) {
		this.packetMapId = packetMapId;
	}
	public List<AttributeMappingData> getAttributeMappings() {
		return attributeMappings;
	}
	public void setAttributeMappings(List<AttributeMappingData> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}
	public List<PacketMappingData> getListSearchPacketMapping() {
		return listSearchPacketMapping;
	}
	public void setListSearchPacketMapping(
			List<PacketMappingData> listSearchPacketMapping) {
		this.listSearchPacketMapping = listSearchPacketMapping;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCommProtocol() {
		return commProtocol;
	}
	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}
	public String getPacketType() {
		return packetType;
	}
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
