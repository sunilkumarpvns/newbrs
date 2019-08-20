/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMForm.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.reports.userstat.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchUserStatisticsForm extends BaseWebForm{

    private String userIdentity;
    
    private long pageNumber;
	private int  multiFactor;
	private long totalPages;
	private long totalRecords;
	
	private List  userStatisticsList;
	private String nameCount;
	
	private String action;
	private String status;
	
   

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}


	public int getMultiFactor() {
		return multiFactor;
	}

	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
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


	public List getUserStatisticsList() {
		return userStatisticsList;
	}

	public void setUserStatisticsList(List userStatisticsList) {
		this.userStatisticsList =userStatisticsList;
	}

	public String getNameCount() {
		return nameCount;
	}

	public void setNameCount(String nameCount) {
		this.nameCount = nameCount;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

}
