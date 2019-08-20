package com.elitecore.corenetvertex.constants;

public enum AccountingEffect {

	CR("CR"),
	DR("DR")
	;
	
	private String value;
	
	private AccountingEffect(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}