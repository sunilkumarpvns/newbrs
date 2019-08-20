package com.elitecore.core.commons.tls.checker;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import com.elitecore.core.commons.tls.checker.warning.TLSWarning;

public class CertifcateTrustedCertificateChecker implements CertificateChecker {

	private boolean ignoreException;
	private List<X509Certificate> trustedCertificates;
	
	public CertifcateTrustedCertificateChecker(boolean ignoreException, List<X509Certificate> certificates) {
		this.ignoreException = ignoreException;
		this.trustedCertificates = certificates;
	}

	@Override
	public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws CertificateException {
		TLSWarning tlsWarning = new TLSWarning("Unknown CA");
		
		for(X509Certificate certificate : certificates){
			for(X509Certificate trustedCert : trustedCertificates){
				if(!certificate.getIssuerDN().equals(trustedCert.getSubjectDN())){ continue;}

				try{
					certificate.verify(trustedCert.getPublicKey());
					return null;
				}catch (Exception e) {
					tlsWarning.addCertificate(certificate);
					continue;
				}
			
			}
		}
		
		if(ignoreException){
			return tlsWarning;
		} else {
			throw new CertificateException("Unknown CA.");
		}
		
	}

}
