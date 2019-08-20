package com.elitecore.netvertexsm.util.exception;

import com.elitecore.netvertexsm.util.constants.CertificateRemarks;

public class InvalidPrivateKeyException extends Exception {

	private static final long serialVersionUID = 1L;

	private CertificateRemarks remark;

	public InvalidPrivateKeyException(String message, CertificateRemarks remark) {
		super(message);    
		this.remark = remark;
	}

	public InvalidPrivateKeyException(String message, CertificateRemarks remark, Throwable cause) {
		super(message, cause);    
		this.remark = remark;
	}

	public InvalidPrivateKeyException(CertificateRemarks remark, Throwable cause) {
		super(cause);    
		this.remark = remark;
	}

	public CertificateRemarks getRemark() {
		return remark;
	}

}
