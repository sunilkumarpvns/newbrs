package com.elitecore.netvertex.service.offlinernc.prefix.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class PrefixConfiguration implements ToStringable {

	private String prefixCode;
	private String prefixName;
	private Integer countryCode;
	private Integer areaCode;
	
	public PrefixConfiguration() {

	}
	
	public PrefixConfiguration(String prefixCode, String prefixName, Integer countryCode, Integer areaCode) {
		this.prefixCode = prefixCode;
		this.prefixName = prefixName;
		this.countryCode = countryCode;
		this.areaCode = areaCode;
	}
	
	public String getPrefixCode() {
		return prefixCode;
	}
	public void setPrefixCode(String prefixCode) {
		this.prefixCode = prefixCode;
	}
	public String getPrefixName() {
		return prefixName;
	}
	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}
	public Integer getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}
	public Integer getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}
	
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
		
	}
	
	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.incrementIndentation();
		builder.append("PrefixName", prefixName);
		builder.append("PrefixCode", prefixCode);
		builder.append("ContryCode", countryCode);
		builder.append("AreaCode", areaCode);
		builder.decrementIndentation();
	}

	
}
