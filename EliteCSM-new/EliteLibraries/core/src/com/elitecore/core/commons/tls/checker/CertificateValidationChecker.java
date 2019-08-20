package com.elitecore.core.commons.tls.checker;

import com.elitecore.core.commons.tls.checker.warning.TLSWarning;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

public class CertificateValidationChecker implements CertificateChecker{
	
	private boolean ignoreException;
	
	public CertificateValidationChecker(boolean ignoreException) {
		this.ignoreException = ignoreException;
	}
	
	@Override
	public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws CertificateExpiredException, CertificateNotYetValidException {
		TLSWarning tlsWarning = null;
		for(X509Certificate certificate : certificates){
			try{
				certificate.checkValidity();
			}catch(CertificateExpiredException ex) {
				if(ignoreException){
					if(tlsWarning == null){
						tlsWarning = new TLSWarning("CertificateExpiry");
					}
					
					tlsWarning.addCertificate(certificate);
					
				}
			} catch(CertificateNotYetValidException ex){
				if(tlsWarning == null){
					tlsWarning = new TLSWarning("CertificateNotYetValid");
				}
				tlsWarning.addCertificate(certificate);
			}
		}
		return tlsWarning;
	}
	
	
}