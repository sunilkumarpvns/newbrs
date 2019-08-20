package com.elitecore.diameterapi.diameter.translator.data.impl;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.diameterapi.diameter.translator.data.CopyPacketMappingData;
import com.elitecore.diameterapi.diameter.translator.operations.PacketOperations;

public class CopyPacketMappingDataImpl implements CopyPacketMappingData {

	private PacketOperations operation;
	private String checkExpression;
	private String destinationExpression;
	private String sourceExpression;
	private String defaultValue;
	private String valueMapping;
	
	public CopyPacketMappingDataImpl() {
		// For JAXB
	}
	
	@Override
	@XmlElement(name = "operation", type = PacketOperations.class)
	public PacketOperations getOperation() {
		return operation;
	}

	@Override
	@XmlElement(name = "check-expression", type = String.class)
	public String getCheckExpression() {
		return checkExpression;
	}

	@Override
	@XmlElement(name = "destination-expression", type = String.class)
	public String getDestinationExpression() {
		return destinationExpression;
	}

	@Override
	@XmlElement(name = "source-expression", type = String.class)
	public String getSourceExpression() {
		return sourceExpression;
	}

	@Override
	@XmlElement(name = "default-value",type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	@XmlElement(name = "value-mapping",type = String.class)
	public String getValueMapping() {
		return valueMapping;
	}

	public void setOperation(PacketOperations packetOperation) {
		this.operation = packetOperation;
	}

	public void setCheckExpression(String checkExpression) {
		this.checkExpression = checkExpression;
	}

	public void setDestinationExpression(String destinationExpression) {
		this.destinationExpression = destinationExpression;
	}

	public void setSourceExpression(String sourceExpression) {
		this.sourceExpression = sourceExpression;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Operation", 
				getOperation() != null ? getOperation().operation : ""));
		out.println(format("%-30s: %s", "Check Expression", 
				getCheckExpression() != null ? getCheckExpression() : ""));
		out.println(format("%-30s: %s", "Destination Expression", 
				getDestinationExpression()));
		out.println(format("%-30s: %s", "Source Expression", 
				getSourceExpression() != null ? getSourceExpression() : ""));
		out.println(format("%-30s: %s", "Default Value", 
				getDefaultValue() != null ? getDefaultValue() : ""));
		out.println(format("%-30s: %s", "Value Mapping", 
				getValueMapping() != null ? getValueMapping() : ""));
		out.close();
		return stringBuffer.toString();

	}

}
