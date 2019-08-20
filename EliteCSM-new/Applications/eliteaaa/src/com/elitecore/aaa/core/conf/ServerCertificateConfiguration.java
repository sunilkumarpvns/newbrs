package com.elitecore.aaa.core.conf;

import com.elitecore.core.commons.tls.ServerCertificateProfile;

public interface ServerCertificateConfiguration {
	
	ServerCertificateProfile getServerCertificateProfileByName(String name);
	ServerCertificateProfile getServerCertificateProfileById(String id);

}
