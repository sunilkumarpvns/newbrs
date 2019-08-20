/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDigestconfForm.java                 		
 * ModualName digestconf    			      		
 * Created on 7 January, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.eap.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchEAPConfigForm extends BaseWebForm{

    
	private static final long serialVersionUID = 1L;

	private long eapId;
	
	// search criteria parameter

	private String name;

	private String tls="0";
	private String ttls="0";
	private String aka="0";
	private String akaPrime="0";
	private String peap="0";
	private String sim="0";
	private String md5="0";
    private String gtc="0";
	private String mschapv2="0";
	
    // parameter needed for digest config.
    private long pageNumber;
    private long totalPages;
	private long totalRecords;
	private String action;
    
    private List<EAPConfigData> eapConfigList;
    

	public long getEapId() {
		return eapId;
	}

	public void setEapId(long eapId) {
		this.eapId = eapId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
 
	
	
	public String getTls() {
		return tls;
	}

	public void setTls(String tls) {
		this.tls = tls;
	}

	public String getTtls() {
		return ttls;
	}

	public void setTtls(String ttls) {
		this.ttls = ttls;
	}

	public String getPeap() {
		return peap;
	}

	public void setPeap(String peap) {
		this.peap = peap;
	}

	public String getAka() {
		return aka;
	}

	public void setAka(String aka) {
		this.aka = aka;
	}

	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getGtc() {
		return gtc;
	}

	public void setGtc(String gtc) {
		this.gtc = gtc;
	}

	public String getMschapv2() {
		return mschapv2;
	}

	public void setMschapv2(String mschapv2) {
		this.mschapv2 = mschapv2;
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

	public List<EAPConfigData> getEapConfigList() {
		return eapConfigList;
	}

	public void setEapConfigList(List<EAPConfigData> eapConfigList) {
		this.eapConfigList = eapConfigList;
	}

	public String getAkaPrime() {
		return akaPrime;
	}

	public void setAkaPrime(String akaPrime) {
		this.akaPrime = akaPrime;
	} 
    
 

}