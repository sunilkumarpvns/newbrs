package com.elitecore.elitesm.util.exception;

import com.elitecore.elitesm.util.constants.CertificateRemarks;

public class CertificateValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	private CertificateRemarks remark;

	public CertificateValidationException(String message, CertificateRemarks remark) {
		super(message);    
		this.remark = remark;
	}

	public CertificateValidationException(String message, CertificateRemarks remark, Throwable cause) {
		super(message, cause);    
		this.remark = remark;
	}

	public CertificateValidationException(CertificateRemarks remark, Throwable cause) {
		super(cause);    
		this.remark = remark;
	}

	public CertificateRemarks getRemark() {
		return remark;
	}

}
