package com.elitecore.netvertexsm.util.constants;
/**
 * Possible error for certificate and private key.
 * @author shreeji.patel
 *
 */
public enum CertificateRemarks {
	//CERTIFICATE REMARK
	INVALID_CERTIFICATE("Invalid Certificate"),
	
	//PRIVATE REMARK
	INVALID_PRIVATE_KEY("Invalid PrivateKey"),
	
	//VALIDATE REMARK
	CERTIFICATE_KEY_MISMATCH("Certificate and Key mismatch")
	;

	public final String remark;
	
	private CertificateRemarks(String remark) {
		this.remark = remark;
	}
}
