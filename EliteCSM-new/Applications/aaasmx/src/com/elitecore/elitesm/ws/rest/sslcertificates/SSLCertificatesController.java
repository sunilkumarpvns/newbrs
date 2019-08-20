package com.elitecore.elitesm.ws.rest.sslcertificates;

import javax.ws.rs.Path;

import com.elitecore.elitesm.ws.rest.sslcertificates.certificaterevocation.CertificateRevocationController;
import com.elitecore.elitesm.ws.rest.sslcertificates.servercertificateprofile.ServerCertificateProfileController;
import com.elitecore.elitesm.ws.rest.sslcertificates.trustedcertificate.TrustedCertificateController;

public class SSLCertificatesController {
	
	@Path("/servercertificateprofile")
	public ServerCertificateProfileController getServerCertificateProfileController(){
		return new ServerCertificateProfileController();
	}
	
	@Path("/trustedcertificate")
	public TrustedCertificateController getTrustedCertificateController(){
		return new TrustedCertificateController();
	}
	
	@Path("/certificaterevocation")
	public CertificateRevocationController getCertificateRevocationController(){
		return new CertificateRevocationController();
	}

}
