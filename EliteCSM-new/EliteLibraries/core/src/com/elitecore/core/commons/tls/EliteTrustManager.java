package com.elitecore.core.commons.tls;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import com.elitecore.core.commons.tls.checker.CertifcateTrustedCertificateChecker;
import com.elitecore.core.commons.tls.checker.CertificateRevocationChecker;
import com.elitecore.core.commons.tls.checker.CertificateSubjectCnChecker;
import com.elitecore.core.commons.tls.checker.CertificateValidationChecker;
import com.elitecore.core.commons.tls.checker.warning.TLSWarning;

public class EliteTrustManager implements X509TrustManager {

	private EliteTrustManagerParams trustManagerParams;
	private List<TLSWarning> tlsWarnings;

	private CertifcateTrustedCertificateChecker certifcateTrustedCertificateChecker;
	private CertificateRevocationChecker certificateRevocationChecker;
	private CertificateValidationChecker certificateValidationChecker;
	private CertificateSubjectCnChecker certificateSubjectCnChecker;
	
	private boolean certificateExpiryVarified = false;
	private boolean certificateRevocationVarified = false;
	private boolean certificateCAVarified = false;
	private boolean certificateSubjectCNVarified = false;
	
	public EliteTrustManager(EliteTrustManagerParams eliteTrustManagerParams) {
		this.tlsWarnings = new ArrayList<TLSWarning>();
		this.trustManagerParams = eliteTrustManagerParams;
		this.certifcateTrustedCertificateChecker = new CertifcateTrustedCertificateChecker(!trustManagerParams.isValidateCertificateCA(), 
																							trustManagerParams.getTrustedCertificates());
		this.certificateRevocationChecker = new CertificateRevocationChecker(!trustManagerParams.isValidateCertificateRevocation(),
																				trustManagerParams.getCRLs());
		
		this.certificateValidationChecker = new CertificateValidationChecker(!trustManagerParams.isValidateCertificateExpiry());
	}
	
	
	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {
		try{
			reset();
			if(arg0 == null || arg0.length == 0){
				if(trustManagerParams.isValidateCertificateCA()){
					tlsWarnings.add(new TLSWarning("Client Cetificate Authentication"));
					return;
				} else {
					throw new CertificateException("No certificate found");
				}
				
			}
			checkIfValid(arg0);
		} catch (GeneralSecurityException e) {
			throw new CertificateException(e);
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {
		try {
			reset();
			if(arg0 == null || arg0.length == 0){
				throw new CertificateException("No certificate found");
			}
			checkIfValid(arg0);
		} catch (GeneralSecurityException e) {
			throw new CertificateException(e);
		}
	}
	
	private void reset(){
		tlsWarnings.clear();
		certificateExpiryVarified = false;
		certificateRevocationVarified = false;
		certificateCAVarified = false;
		certificateSubjectCNVarified = false;
	}
	

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return trustManagerParams.getTrustedCertificates().toArray(new X509Certificate[trustManagerParams.getTrustedCertificates().size()]);
	}
	
	
	private void checkIfValid(X509Certificate [] x509Certificates) throws GeneralSecurityException{
		
		TLSWarning tlsWarning = certifcateTrustedCertificateChecker.checkCertificatePath(x509Certificates);
		if(tlsWarning != null){
			tlsWarnings.add(tlsWarning);
		}
		
		certificateCAVarified = true;
		
		tlsWarning = certificateRevocationChecker.checkCertificatePath(x509Certificates);
		if(tlsWarning != null){
			tlsWarnings.add(tlsWarning);
		}
		certificateRevocationVarified = true;
		
		
		tlsWarning = certificateValidationChecker.checkCertificatePath(x509Certificates);
		if(tlsWarning != null){
			tlsWarnings.add(tlsWarning);
		}
		certificateExpiryVarified = true;
		
		
		if(certificateSubjectCnChecker != null){
			tlsWarning = certificateSubjectCnChecker.checkCertificatePath(x509Certificates);
			if(tlsWarning != null){
				tlsWarnings.add(tlsWarning);
			}
			certificateSubjectCNVarified = true;
		}
		
	}
	

	public void setCertifcateTrustedCertificateChecker(CertifcateTrustedCertificateChecker certifcateTrustedCertificateChecker) { this.certifcateTrustedCertificateChecker = certifcateTrustedCertificateChecker;}

	public void setCertificateRevocationChecker(CertificateRevocationChecker certificateRevocationChecker) { this.certificateRevocationChecker = certificateRevocationChecker;}

	public void setCertificateValidationChecker(CertificateValidationChecker certificateValidationChecker) { this.certificateValidationChecker = certificateValidationChecker; }

	public void setCertificateSubjectCnChecker(CertificateSubjectCnChecker certificateSubjectCnChecker) { this.certificateSubjectCnChecker = certificateSubjectCnChecker;}
	
	public void setValidateCertificateSubjectCN(boolean validateCertificateSubjectCN) {this.certificateSubjectCNVarified = validateCertificateSubjectCN; }
	
	public boolean isCertificateExpiryVarified() {return certificateExpiryVarified;}

	public boolean isCertificateRevocationVarified() {return certificateRevocationVarified;}

	public boolean isCertificateCAVarified() {return certificateCAVarified; }

	public boolean isCertificateSubjectCNVarified() { return certificateSubjectCNVarified; }
	
	public List<TLSWarning> getTlsWarnings() { return tlsWarnings; }
	
	
}