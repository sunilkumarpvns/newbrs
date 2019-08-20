package com.elitecore.core.commons.tls.checker.warning;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class TLSWarning {
	private String warning; 
	private List<X509Certificate> certificates;
	
	public TLSWarning(String warning){
		this.warning = warning;
		this.certificates = new ArrayList<X509Certificate>();
	}
	
	public void addCertificate(X509Certificate certificate){
		certificates.add(certificate);
	}
	
	public String getWarning(){
		return warning;
	}
	
	public List<X509Certificate> getCertificates(){
		return certificates;
	}
}
