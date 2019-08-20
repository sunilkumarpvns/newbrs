/**
 * 
 */
package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

/**
 * @author rakeshkachhadiya
 *
 */
public class SearchFileInformationForm extends BaseWebForm{

	private String deviceName;
	private String fileName;
	private String filePath;
	private String action;
	private List lstFiles;
	private String netServerId;
	private String[] select;
        private String radioChoice;
        private String errorCode = "0";        
        
        
	
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List getLstFiles() {
		return lstFiles;
	}

	public void setLstFiles(List lstFiles) {
	    this.lstFiles = lstFiles;
	}

	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public String[] getSelect() {
		return select;
	}

	public void setSelect(String[] select) {
		this.select = select;
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

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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

    
    public String getRadioChoice( ) {
        return radioChoice;
    }

    
    public void setRadioChoice( String radioChoice ) {
        this.radioChoice = radioChoice;
    }

    
    public String getErrorCode( ) {
        return errorCode;
    }

    
    public void setErrorCode( String errorCode ) {
        this.errorCode = errorCode;
    }
	
}
