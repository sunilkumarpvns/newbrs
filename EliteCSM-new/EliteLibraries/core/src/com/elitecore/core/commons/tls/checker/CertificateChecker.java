package com.elitecore.core.commons.tls.checker;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import com.elitecore.core.commons.tls.checker.warning.TLSWarning;

public interface CertificateChecker{
	public TLSWarning checkCertificatePath(X509Certificate[] certificate) throws GeneralSecurityException;
	
}
