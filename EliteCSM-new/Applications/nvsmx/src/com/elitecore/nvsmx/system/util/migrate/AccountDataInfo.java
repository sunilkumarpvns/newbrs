package com.elitecore.nvsmx.system.util.migrate;

import java.util.Map;

/**
 * Class represents subscriber profile of NetVertex 6.4.8
 * 
 * @author Chetan.Sankhala
 */
public class AccountDataInfo {

	private Map<String, String> valueByColumnName;
	
	public AccountDataInfo(Map<String, String> valueByColumnNameMap) {
		this.valueByColumnName = valueByColumnNameMap;
	}

	public Map<String, String> getValueByColumnName() {
		return valueByColumnName;
	}

	public void setValueByColumnName(Map<String, String> valueByColumnName) {
		this.valueByColumnName = valueByColumnName;
	}
}
