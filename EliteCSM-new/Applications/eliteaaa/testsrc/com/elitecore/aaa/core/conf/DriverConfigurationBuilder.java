package com.elitecore.aaa.core.conf;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.data.AttributeRelationBuilder;
import com.elitecore.aaa.core.data.AttributesRelation;

public class DriverConfigurationBuilder {

	/**
	 * 
	 */
	private ClassicCSVAcctDriverConfigurationImpl driverConfiguration;

	public DriverConfigurationBuilder(ClassicCSVAcctDriverConfigurationImpl baseDriverConfiguration) {
		this.driverConfiguration = baseDriverConfiguration;
	}

	public DriverConfigurationBuilder withMapping(AttributeRelationBuilder attributesRelation) {
		driverConfiguration.getAttributesRelationList().add(
				new AttributesRelation(attributesRelation.getAttributeId(), 
						attributesRelation.getDefaultValue(), 
						attributesRelation.isbUseDictionaryValue(), 
						attributesRelation.getHeader(), 
						attributesRelation.getAttributeList()));
		return this;
	}
	
	public ClassicCSVAcctDriverConfigurationImpl getDriverConfiguration() {
		return this.driverConfiguration;
	}

	public DriverConfigurationBuilder  enclosingCharacter(String enclosingCharacter) {
		driverConfiguration.getCdrDetails().setEnclosingChar(enclosingCharacter);
		return this;
	}
	
	public DriverConfigurationBuilder prefixedCdrTimeStamp() {
		driverConfiguration.getCdrDetails().setCdrTimeStampPosition("Prefix");
		return this;
	}
	
	public DriverConfigurationBuilder suffixedCdrTimeStamp() {
		driverConfiguration.getCdrDetails().setCdrTimeStampPosition("Suffix");
		return this;
	}
	
	public DriverConfigurationBuilder withCdrTimeStampHeader(String cdrTimeStampHeader) {
		driverConfiguration.getCdrDetails().setCdrTimeStampHeader(cdrTimeStampHeader);
		return this;
	}

	public DriverConfigurationBuilder havingDelimiter (String delimiter) {
		driverConfiguration.getCdrDetails().setDelimeter(delimiter);
		return this;
	}
}