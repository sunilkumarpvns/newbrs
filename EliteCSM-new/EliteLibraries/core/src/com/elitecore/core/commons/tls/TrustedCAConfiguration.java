package com.elitecore.core.commons.tls;

import java.security.cert.X509Certificate;
import java.util.List;

public interface TrustedCAConfiguration {
	public List<X509Certificate> getCACertificates();
}
