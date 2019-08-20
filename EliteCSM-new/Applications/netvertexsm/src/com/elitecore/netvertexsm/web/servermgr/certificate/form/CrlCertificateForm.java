package com.elitecore.netvertexsm.web.servermgr.certificate.form;

import java.util.Collection;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CrlCertificateForm extends BaseWebForm{
	
	private java.lang.Long crlCertificateId;
	private String crlCertificateName;    
	private FormFile crlCert;
	
	//Parameter for search	
	private long pageNumber;
	private long totalPages;
	private long totalRecords;	
	private Collection crlCertificateList;
	private List listCrlCertificate;
	
	
	public java.lang.Long getCrlCertificateId() {
		return crlCertificateId;
	}
	public void setCrlCertificateId(java.lang.Long crlCertificateId) {
		this.crlCertificateId = crlCertificateId;
	}
	public String getCrlCertificateName() {
		return crlCertificateName;
	}
	public void setCrlCertificateName(String crlCertificateName) {
		this.crlCertificateName = crlCertificateName;
	}
	public FormFile getCrlCert() {
		return crlCert;
	}
	public void setCrlCert(FormFile crlCert) {
		this.crlCert = crlCert;
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
	public Collection getCrlCertificateList() {
		return crlCertificateList;
	}
	public void setCrlCertificateList(Collection crlCertificateList) {
		this.crlCertificateList = crlCertificateList;
	}
	public List getListCrlCertificate() {
		return listCrlCertificate;
	}
	public void setListCrlCertificate(List listCrlCertificate) {
		this.listCrlCertificate = listCrlCertificate;
	}	
}
