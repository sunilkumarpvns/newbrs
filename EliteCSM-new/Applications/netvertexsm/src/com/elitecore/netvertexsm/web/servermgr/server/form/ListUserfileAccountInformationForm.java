package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.Map;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ListUserfileAccountInformationForm extends BaseWebForm{
	
	String selectedFileName;
	String action;
	Long netserverid;
	
	private int start;
	private int end;
	private int previousPage;
	private int numerOfRecordsPerPage; 
	private int totalNumberOfRecord; 
	private int lastPage;
	private int totalNoOfPage;
	private int nextPage=0;
	private int pageNo=0;
	private int totalRow=0;
	private int totalField=0;
	Map userAccountMap;

	public String getSelectedFileName() {
		return selectedFileName;
	}

	public void setSelectedFileName(String selectedFileName) {
		this.selectedFileName = selectedFileName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getNumerOfRecordsPerPage() {
		return numerOfRecordsPerPage;
	}

	public void setNumerOfRecordsPerPage(int numerOfRecordsPerPage) {
		this.numerOfRecordsPerPage = numerOfRecordsPerPage;
	}

	public int getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getTotalField() {
		return totalField;
	}

	public void setTotalField(int totalField) {
		this.totalField = totalField;
	}

	public int getTotalNoOfPage() {
		return totalNoOfPage;
	}

	public void setTotalNoOfPage(int totalNoOfPage) {
		this.totalNoOfPage = totalNoOfPage;
	}

	public int getTotalNumberOfRecord() {
		return totalNumberOfRecord;
	}

	public void setTotalNumberOfRecord(int totalNumberOfRecord) {
		this.totalNumberOfRecord = totalNumberOfRecord;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public Map getUserAccountMap() {
		return userAccountMap;
	}

	public void setUserAccountMap(Map userAccountMap) {
		this.userAccountMap = userAccountMap;
	}

	public Long getNetserverid() {
		return netserverid;
	}

	public void setNetserverid(Long netserverid) {
		this.netserverid = netserverid;
	}	

}
