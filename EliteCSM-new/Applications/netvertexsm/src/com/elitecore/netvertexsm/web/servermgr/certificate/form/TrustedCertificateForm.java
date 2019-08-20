package com.elitecore.netvertexsm.web.servermgr.certificate.form;

import java.util.Collection;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class TrustedCertificateForm extends BaseWebForm{
	
	private java.lang.Long trustedCertificateId;
	private String trustedCertificateName;    
	private FormFile trustedCert;
	
	//Parameter for search	
	private long pageNumber;
	private long totalPages;
	private long totalRecords;	
	private Collection trustedCertificateList;
	private List listTrustedCertificate;
	
	public java.lang.Long getTrustedCertificateId() {
		return trustedCertificateId;
	}
	public void setTrustedCertificateId(java.lang.Long trustedCertificateId) {
		this.trustedCertificateId = trustedCertificateId;
	}
	public String getTrustedCertificateName() {
		return trustedCertificateName;
	}
	public void setTrustedCertificateName(String trustedCertificateName) {
		this.trustedCertificateName = trustedCertificateName;
	}
	public FormFile getTrustedCert() {
		return trustedCert;
	}
	public void setTrustedCert(FormFile trustedCert) {
		this.trustedCert = trustedCert;
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
	public Collection getTrustedCertificateList() {
		return trustedCertificateList;
	}
	public void setTrustedCertificateList(Collection trustedCertificateList) {
		this.trustedCertificateList = trustedCertificateList;
	}
	public List getListTrustedCertificate() {
		return listTrustedCertificate;
	}
	public void setListTrustedCertificate(List listTrustedCertificate) {
		this.listTrustedCertificate = listTrustedCertificate;
	}	
}
