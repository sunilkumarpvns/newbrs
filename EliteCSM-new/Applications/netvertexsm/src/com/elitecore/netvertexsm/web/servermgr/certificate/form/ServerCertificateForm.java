package com.elitecore.netvertexsm.web.servermgr.certificate.form;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ServerCertificateForm extends BaseWebForm{
	
	private java.lang.Long serverCertificateId;
	private String serverCertificateName;    
	private FormFile publicCert;
	private FormFile privateKey;
	private String privateKeyPassword;
	private String privateKeyAlgorithm = PrivateKeyAlgo.RSA.name;
	private X509Certificate x509Certificate;
	private String actionName;	
	private String action;
	
	//Parameter for Server Certificate
	private long pageNumber;
	private long totalPages;
	private long totalRecords;	
	private Collection serverCertificateList;
	private List listServerCertificate;	

	//Parameter for CRL Certificate
	private long crlPageNumber;
	private long crlTotalPages;
	private long crlTotalRecords;	
	private Collection crlCertificateList;
	private List listCrlCertificate;
	
	

	public java.lang.Long getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(java.lang.Long serverCertificateId) {
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
}
