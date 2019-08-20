/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDigestconfForm.java                 		
 * ModualName digestconf    			      		
 * Created on 7 January, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.digestconf.forms;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDigestConfForm extends BaseWebForm{

    private java.lang.Long digestConfId;
    private String name;
    private String description;
    private String realm;
    private String defaultQoP;
    private String defaultAlgo;
    private String opaque;
    private String defaultNonce;
    private java.lang.Integer defaultNonceLength;
    private String draftAAASipEnable;
    private String commonstatusid;
    private java.lang.Long lastModifiedbyStaffid;
    private java.lang.Long createdbyStaffid;
    private Timestamp lastModifiedDate;
    private Timestamp createDate;
    
    // parameter needed for digest config.
    private long pageNumber;
    private long totalPages;
	private long totalRecords;
	private String action;
    
    private List<DigestConfigInstanceData> digestConfigList; 


    public java.lang.Long getDigestConfId(){
        return digestConfId;
    }

	public void setDigestConfId(java.lang.Long digestConfId) {
		this.digestConfId = digestConfId;
	}


    public String getName(){
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}


    public String getDescription(){
        return description;
    }

	public void setDescription(String description) {
		this.description = description;
	}


    public String getRealm(){
        return realm;
    }

	public void setRealm(String realm) {
		this.realm = realm;
	}


    public String getDefaultQoP(){
        return defaultQoP;
    }

	public void setDefaultQoP(String defaultQoP) {
		this.defaultQoP = defaultQoP;
	}


    public String getDefaultAlgo(){
        return defaultAlgo;
    }

	public void setDefaultAlgo(String defaultAlgo) {
		this.defaultAlgo = defaultAlgo;
	}


    public String getOpaque(){
        return opaque;
    }

	public void setOpaque(String opaque) {
		this.opaque = opaque;
	}


    public String getDefaultNonce(){
        return defaultNonce;
    }

	public void setDefaultNonce(String defaultNonce) {
		this.defaultNonce = defaultNonce;
	}


    public java.lang.Integer getDefaultNonceLength(){
        return defaultNonceLength;
    }

	public void setDefaultNonceLength(java.lang.Integer defaultNonceLength) {
		this.defaultNonceLength = defaultNonceLength;
	}


    public String getDraftAAASipEnable(){
        return draftAAASipEnable;
    }

	public void setDraftAAASipEnable(String draftAAASipEnable) {
		this.draftAAASipEnable = draftAAASipEnable;
	}


    public String getCommonstatusid(){
        return commonstatusid;
    }

	public void setCommonstatusid(String commonstatusid) {
		this.commonstatusid = commonstatusid;
	}


    public java.lang.Long getLastModifiedbyStaffid(){
        return lastModifiedbyStaffid;
    }

	public void setLastModifiedbyStaffid(java.lang.Long lastModifiedbyStaffid) {
		this.lastModifiedbyStaffid = lastModifiedbyStaffid;
	}


    public java.lang.Long getCreatedbyStaffid(){
        return createdbyStaffid;
    }

	public void setCreatedbyStaffid(java.lang.Long createdbyStaffid) {
		this.createdbyStaffid = createdbyStaffid;
	}


    public Timestamp getLastModifiedDate(){
        return lastModifiedDate;
    }

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}


    public Timestamp getCreateDate(){
        return createDate;
    }

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
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

	public List<DigestConfigInstanceData> getDigestConfigList() {
		return digestConfigList;
	}

	public void setDigestConfigList(List<DigestConfigInstanceData> digestConfigList) {
		this.digestConfigList = digestConfigList;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	


}
