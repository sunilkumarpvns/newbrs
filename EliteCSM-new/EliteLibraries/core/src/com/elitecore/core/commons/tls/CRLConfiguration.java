package com.elitecore.core.commons.tls;

import java.security.cert.X509CRL;
import java.util.List;

public interface CRLConfiguration {
	
	public List<X509CRL> getCRLs();
	
	public boolean getOCSPEnabled();
	
	public String getOCSPURL();
	
}
