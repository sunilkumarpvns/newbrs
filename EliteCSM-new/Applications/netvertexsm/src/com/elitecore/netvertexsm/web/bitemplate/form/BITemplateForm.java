package com.elitecore.netvertexsm.web.bitemplate.form;

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;

public class BITemplateForm extends ActionForm {
	private long biId;
	private String name;
	private String description;
	private String bikey;
//	private long id;
//	private String key;
//	private String value;
	private Long templateId;
	private List<BISubKeyData> subKeyList;
	private FormFile csvFile;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private List<BITemplateData> listSearchTemplate;
	
	
	public FormFile getCsvFile() {
		return csvFile;
	}
	public void setCsvFile(FormFile csvFile) {
		this.csvFile = csvFile;
	}
	public List<BISubKeyData> getSubKeyList() {
		return subKeyList;
	}
	public void setSubKeyList(List<BISubKeyData> subKeyList) {
		this.subKeyList = subKeyList;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBikey() {
		return bikey;
	}
	public void setBikey(String bikey) {
		this.bikey = bikey;
	}
	public List<BITemplateData> getListSearchTemplate() {
		return listSearchTemplate;
	}
	public void setListSearchTemplate(List<BITemplateData> listSearchTemplate) {
		this.listSearchTemplate = listSearchTemplate;
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
	public long getBiId() {
		return biId;
	}
	public void setBiId(long biId) {
		this.biId = biId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
}
