/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscDigestconfForm.java                 		
 * ModualName digestconf    			      		
 * Created on 7 January, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.eap.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MiscEAPConfigForm extends BaseWebForm{

	private String name;
	private String tls;
	private String ttls;
	private String sim;
	private String aka;
	private String md5;
	private String mschapv2;
	private String gtc;
	private String akaPrime;
	// paging 
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	
	
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
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getAka() {
		return aka;
	}
	public void setAka(String aka) {
		this.aka = aka;
	}
	
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getMschapv2() {
		return mschapv2;
	}
	public void setMschapv2(String mschapv2) {
		this.mschapv2 = mschapv2;
	}
	public String getGtc() {
		return gtc;
	}
	public void setGtc(String gtc) {
		this.gtc = gtc;
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
	public String getAkaPrime() {
		return akaPrime;
	}
	public void setAkaPrime(String akaPrime) {
		this.akaPrime = akaPrime;
	}
}