package com.elitecore.diameterapi.core.translator.policy.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.translator.policy.data.TranslationDetail;

@XmlType(propOrder = {})
public class TranslationDetailImpl implements TranslationDetail{

	private String inRequestType="";
	private String outRequestType="";
	private List<MappingDataImpl>requestMappingDataList;
	private List<MappingDataImpl>responseMappingDataList;
	private boolean isDummyResponse=false;
	private String mappingName="";
	
	public TranslationDetailImpl(){
		requestMappingDataList=new ArrayList<MappingDataImpl>();
		responseMappingDataList=new ArrayList<MappingDataImpl>();
	}

	@XmlElement(name = "in-request-type", type = String.class)
	public String getInRequestType() {
		return inRequestType;
	}
	
	public void setInRequestType(String inRequestType) {
		this.inRequestType = inRequestType;
	}
	
	@XmlElement(name = "out-request-type", type = String.class)
	public String getOutRequestType() {
		return outRequestType;
	}

	public void setOutRequestType(String outRequestType) {
		this.outRequestType = outRequestType;
	}

	@XmlElementWrapper(name = "request-mappings")
	@XmlElement(name = "request-mapping")
	public List<MappingDataImpl> getRequestMappingDataList() {
		return requestMappingDataList;
	}
	
	public void setRequestMappingDataList(List<MappingDataImpl> requestMappingDataList) {
		this.requestMappingDataList = requestMappingDataList;
	}
	
	@XmlElementWrapper(name = "response-mappings")
	@XmlElement(name = "response-mapping")
	public List<MappingDataImpl> getResponseMappingDataList() {
		return responseMappingDataList;
	}

	public void setResponseMappingDataList(List<MappingDataImpl> responseMappingDataList) {
		this.responseMappingDataList = responseMappingDataList;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    -- Translation Detail Configuration -- ");
		out.println("    In Request Type  		= " + inRequestType);
		out.println("    Out Request Type 		= " + outRequestType);
		out.println("    Dummy Response Enabled = " + getIsDummyResponse());
		out.println();
		out.println("     Request Mapping:");
		final int requestMappingDataSize=requestMappingDataList.size();
		for (int i=0; i< requestMappingDataSize; i++){
                out.println( requestMappingDataList.get(i) );
        }
		out.println("     Response Mapping:");
		final int responseMappingDataSize=responseMappingDataList.size();
		for (int i=0; i< responseMappingDataSize; i++){
                out.println( responseMappingDataList.get(i) );
        }
		out.close();
		return stringBuffer.toString();
	}

	@XmlElement(name = "dummy-response-enabled", type = boolean.class)
	public boolean getIsDummyResponse() {
		return isDummyResponse;
	}
	public void setIsDummyResponse(boolean isDummyResponse){
		this.isDummyResponse = isDummyResponse;
	}
		
	@XmlElement(name ="mapping-name",type=String.class)
	public String getMappingName() {
		return mappingName;
}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
}
