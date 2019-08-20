package com.elitecore.netvertex.service.offlinernc.core;

public enum OfflineRnCErrorCodes {

	PARTNER_NOT_FOUND("001"),
	NO_SERVICE_POLICY_SELECTED("002"),
	RATE_NOT_FOUND("003"),
	INVALID_EDR("004"), 
	PACKAGE_NOT_FOUND("005"), 
	RATE_CARD_GROUP_NOT_FOUND("006"), 
	ACCOUNT_NOT_FOUND("007"),
	EXCHANGE_RATE_NOT_FOUND("008"),
	PRODUCT_SPEC_NOT_FOUND("009");
	
	private String code;

	private OfflineRnCErrorCodes(String errorCode) {
		this.code = errorCode;
	}
	
	public String code() {
		return code;
	}
}
