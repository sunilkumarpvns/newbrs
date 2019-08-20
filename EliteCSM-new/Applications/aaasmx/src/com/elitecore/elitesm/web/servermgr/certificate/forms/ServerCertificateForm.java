package com.elitecore.elitesm.web.servermgr.certificate.forms;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ServerCertificateForm extends BaseWebForm{
	
	// SERVERCERTIFICATE FORM DETAILS PARAMETERS
	
	private String serverCertificateId;
	private String serverCertificateName;    
	private FormFile publicCert;
	private FormFile privateKey;
	private String privateKeyPassword;
	private String privateKeyAlgorithm = PrivateKeyAlgo.RSA.name;
	private X509Certificate x509Certificate;
	private String actionName;	
	private String action;
	
	//Parameter for search	
	private long pageNumber;
	private long totalPages;
	private long totalRecords;	
	private Collection serverCertificateList;
	private List listServerCertificate;	

	//TRUSTED CERTIFICATE DETAILS PARAMETERS
	
	private String trustedCertificateId;
	private String trustedCertificateName;    
	private FormFile trustedCert;
	
	//Parameter for search	
	private long trustedPageNumber;
	private long trustedTotalPages;
	private long trustedtotalRecords;	
	private Collection trustedCertificateList;
	private List listTrustedCertificate;
	
	
	//CRL PARAMETERS
	
	private String crlCertificateId;
	private String crlCertificateName;    
	private FormFile crlCert;
	
	//Parameter for search	
	private long crlPageNumber;
	private long crlTotalPages;
	private long crlTotalRecords;	
	private Collection crlCertificateList;
	private List listCrlCertificate;
	
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	public String getServerCertificateName() {		
		return serverCertificateName;
	}
	public void setServerCertificateName(String serverCertificateName) {		
		this.serverCertificateName = serverCertificateName;
	}
	public FormFile getPublicCert() {
		return publicCert;
	}
	public void setPublicCert(FormFile publicCert) {
		this.publicCert = publicCert;
	}
	public FormFile getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(FormFile privateKey) {
		this.privateKey = privateKey;
	}
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}
	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
	public String getPrivateKeyAlgorithm() {
		return privateKeyAlgorithm;
	}
	public void setPrivateKeyAlgorithm(String privateKeyAlgorithm) {
		this.privateKeyAlgorithm = privateKeyAlgorithm;
	}
	public X509Certificate getX509Certificate() {
		return x509Certificate;
	}
	public void setX509Certificate(X509Certificate x509Certificate) {
		this.x509Certificate = x509Certificate;
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
	public Collection getServerCertificateList() {
		return serverCertificateList;
	}
	public void setServerCertificateList(Collection serverCertificateList) {
		this.serverCertificateList = serverCertificateList;
	}
	public List getListServerCertificate() {
		return listServerCertificate;
	}
	public void setListServerCertificate(List listServerCertificate) {
		this.listServerCertificate = listServerCertificate;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public java.lang.String getTrustedCertificateId() {
		return trustedCertificateId;
	}
	public void setTrustedCertificateId(java.lang.String trustedCertificateId) {
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
	public long getTrustedPageNumber() {
		return trustedPageNumber;
	}
	public void setTrustedPageNumber(long trustedPageNumber) {
		this.trustedPageNumber = trustedPageNumber;
	}
	public long getTrustedTotalPages() {
		return trustedTotalPages;
	}
	public void setTrustedTotalPages(long trustedTotalPages) {
		this.trustedTotalPages = trustedTotalPages;
	}
	public long getTrustedtotalRecords() {
		return trustedtotalRecords;
	}
	public void setTrustedtotalRecords(long trustedtotalRecords) {
		this.trustedtotalRecords = trustedtotalRecords;
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
	public java.lang.String getCrlCertificateId() {
		return crlCertificateId;
	}
	public void setCrlCertificateId(java.lang.String crlCertificateId) {
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
	public long getCrlPageNumber() {
		return crlPageNumber;
	}
	public void setCrlPageNumber(long crlPageNumber) {
		this.crlPageNumber = crlPageNumber;
	}
	public long getCrlTotalPages() {
		return crlTotalPages;
	}
	public void setCrlTotalPages(long crlTotalPages) {
		this.crlTotalPages = crlTotalPages;
	}
	public long getCrlTotalRecords() {
		return crlTotalRecords;
	}
	public void setCrlTotalRecords(long crlTotalRecords) {
		this.crlTotalRecords = crlTotalRecords;
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
