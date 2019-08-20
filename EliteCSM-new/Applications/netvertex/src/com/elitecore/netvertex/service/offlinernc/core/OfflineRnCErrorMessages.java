package com.elitecore.netvertex.service.offlinernc.core;



public enum OfflineRnCErrorMessages {
	
	
	PARTNER_NOT_FOUND("PartnerNotFound"),
	NO_SERVICE_POLICY_SELECTED("NoServicePolicySelected"),
	RATE_NOT_FOUND("RateNotFound"),
	INVALID_EDR("InvalidEDR"), 
	PACKAGE_NOT_FOUND("PackageNotFound"), 
	RATE_CARD_GROUP_NOT_FOUND("RateCardGroupNotFound"), 
	ACCOUNT_NOT_FOUND("AccountNotFound"),
	EXCHANGE_RATE_NOT_FOUND("ExchangeRateNotFound"),
	PRODUCT_SPEC_NOT_FOUND("ProductSpecNotFound");
	
	public static final String MISSING_KEY = "MissingKey";
	public static final String INVALID_DATE_FORMAT = "InvalidDateFormat";
	
	private String message;
	
	private OfflineRnCErrorMessages(String errorMessage) {
		this.message = errorMessage;
	}
	
	public String message() {
		return message;
	}

}
