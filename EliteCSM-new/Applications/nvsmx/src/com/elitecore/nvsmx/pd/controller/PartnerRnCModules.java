package com.elitecore.nvsmx.pd.controller;

/**
 * Used to display list of Partner RnC modules
 * @author Gaurav.Mishra
 */
public enum PartnerRnCModules {

	RATE_CARD("Rate Card","com.elitecore.corenetvertex.pd.ratecard.RateCardData","pd/ratecard/rate-card"),
	RATE_CARD_GROUP("Rate Card Group","com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData","pd/ratecardgroup/rate-card-group"),
	ACCOUNT("Account","com.elitecore.corenetvertex.pd.account.AccountData","pd/account/account"),
	;

	private String val;
	private String entityName;
	private String modulePath;

	PartnerRnCModules(String val,String entityName, String modulepath){
		this.val = val;
		this.entityName = entityName;
		this.modulePath = modulepath;
	}

	public String getVal() {
		return val;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public String getModulePath() {
		return modulePath;
	}
}
