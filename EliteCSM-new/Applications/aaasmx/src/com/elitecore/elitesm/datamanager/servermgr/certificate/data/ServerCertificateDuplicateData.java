package com.elitecore.elitesm.datamanager.servermgr.certificate.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlRootElement(name = "server-certificate-profile")
public class ServerCertificateDuplicateData {
	
	@NotEmpty(message = "Server Certificate Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 64, message = "Length of Server Certificate Name must not greater than 64.")
	private String serverCertificateName;
	
	@XmlElement(name = "name")
	public String getServerCertificateName() {
		return serverCertificateName;
	}

	public void setServerCertificateName(String serverCertificateName) {
		this.serverCertificateName = serverCertificateName;
	}
	
}
