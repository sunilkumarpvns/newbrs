package com.elitecore.netvertex.core.tls;

import com.elitecore.core.commons.tls.ServerCertificateProfile;

public interface ServerCertificateConfiguration {

	ServerCertificateProfile getServerCertificateProfileByName(String name);
	ServerCertificateProfile getServerCertificateProfileById(Long id);

}
