package com.elitecore.netvertexsm.web.devicemgmt.form;

import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class DeviceManagementForm  extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private Long tacDetailId;
    private Long tac;
    private String brand;
    private String model;
    private String hardwareType;
    private String operatingSystem;
    private Integer year;
    private String additionalInfo;
    
    //Search Parameters
    private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String actionName;
	private List<TACDetailData> tacDetailDataList;
	private FormFile formFile;
	
	
	public Long getTacDetailId() {
		return tacDetailId;
	}
	public void setTacDetailId(Long tacDetailId) {
		this.tacDetailId = tacDetailId;
	}
	public Long getTac() {
		return tac;
	}
	public void setTac(Long tac) {
		this.tac = tac;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getHardwareType() {
		return hardwareType;
	}
	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
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
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public List<TACDetailData> getTacDetailDataList() {
		return tacDetailDataList;
	}
	public void setTacDetailDataList(List<TACDetailData> tacDetailDataList) {
		this.tacDetailDataList = tacDetailDataList;
	}
	public FormFile getFormFile() {
		return formFile;
	}
	public void setFormFile(FormFile formFile) {
		this.formFile = formFile;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = (additionalInfo != null ? additionalInfo.trim() : additionalInfo);
	}
    
}
