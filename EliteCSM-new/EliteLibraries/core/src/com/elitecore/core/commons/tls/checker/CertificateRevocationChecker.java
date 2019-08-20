package com.elitecore.core.commons.tls.checker;

import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import com.elitecore.core.commons.tls.checker.warning.TLSWarning;

public class CertificateRevocationChecker implements CertificateChecker {
	
	private boolean ignoreException;
	private List<X509CRL> crls;
	
	public CertificateRevocationChecker(boolean ignoreException, List<X509CRL> crls) {
		this.ignoreException = ignoreException;
		this.crls = crls;
	}

	@Override
	public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws CertificateException {
		for(X509Certificate certificate : certificates){
			if(crls == null || crls.isEmpty()){
				continue;
			}
			
			TLSWarning tlsWarning = new TLSWarning("Certificate Revocation");
			for(X509CRL crl : crls){
				if(!certificate.getIssuerDN().equals(crl.getIssuerDN())){
					continue;
				}
				
				if(crl.isRevoked(certificate)){
					if(ignoreException){
						tlsWarning.addCertificate(certificate);
					} else {
						throw new CertificateException("Certificate is revoked");
					}
					
				}
			
			}
		}
		
		return null;
	}

	
	
}
