package com.elitecore.diameterapi.diameter.translator.data.impl;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslationConfigurationData;

public class CopyPacketTranslationConfigDataImpl implements CopyPacketTranslationConfigurationData {

	private String mappingName;
	private String inExpression;
	private List<CopyPacketMappingDataImpl> requestMappingDataList;
	private List<CopyPacketMappingDataImpl> responseMappingDataList;
	private boolean dummyMappingEnabled;

	@Override
	@XmlElement(name ="mapping-name",type=String.class)
	public String getMappingName() {
		return mappingName;
	}

	@Override
	@XmlElement(name = "in-expression", type = String.class)
	public String getInExpression() {
		return inExpression;
	}

	@Override
	@XmlElementWrapper(name = "request-mappings")
	@XmlElement(name = "request-mapping")
	public List<CopyPacketMappingDataImpl> getRequestMappingDataList() {
		return requestMappingDataList;
	}

	@Override
	@XmlElementWrapper(name = "response-mappings")
	@XmlElement(name = "response-mapping")
	public List<CopyPacketMappingDataImpl> getResponseMappingDataList() {
		return responseMappingDataList;
	}
	
	@Override
	@XmlElement(name = "dummy-mapping-enabled", type = boolean.class)
	public boolean isDummyMappingEnabled() {
		return dummyMappingEnabled;
	}
	
	public void setDummyMappingEnabled(boolean dummyMappingEnabled) {
		this.dummyMappingEnabled = dummyMappingEnabled;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public void setInExpression(String inExpression) {
		this.inExpression = inExpression;
	}

	public void setRequestMappingDataList(
			List<CopyPacketMappingDataImpl> requestMappingDataList) {
		this.requestMappingDataList = requestMappingDataList;
	}

	public void setResponseMappingDataList(
			List<CopyPacketMappingDataImpl> responseMappingDataList) {
		this.responseMappingDataList = responseMappingDataList;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println(padStart("Copy Packet Mapping Configuration: " + getMappingName(), 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getMappingName()));
		out.println(format("%-30s: %s", "In Expression", getInExpression() != null ? getInExpression() : ""));
		out.println(format("%-30s: %s", "Dummy Response", isDummyMappingEnabled()));
		out.println(repeat("-", 70));
		out.println("Request Parameters: ");
		if(!Collectionz.isNullOrEmpty(requestMappingDataList)){
			for(int i = 0 ; i < requestMappingDataList.size() ; i++ ){
				out.print(requestMappingDataList.get(i));
			}
		}
		out.println(repeat("-", 70));
		out.println("Response Parameters: ");
		if(!Collectionz.isNullOrEmpty(responseMappingDataList)){
			for(int i = 0 ; i < responseMappingDataList.size() ; i++ ){
				out.print(responseMappingDataList.get(i));
			}
		}
		out.close();
		return stringBuffer.toString();
	}
}

