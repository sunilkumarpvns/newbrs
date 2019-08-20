package com.elitecore.diameterapi.core.translator.policy.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;


@XmlType(propOrder = {})
public class TranslatorPolicyDataImpl implements TranslatorPolicyData {
	
	private String transMapConfId="";
	private String name;
	private String toInterpreterId="";
	private String fromInterpreterId="";
	private String baseTranslationMappingId;
	
	private boolean isDummyResponse=false;
	private Map<String, String> dummyResponseMap;
	private List <TranslationDetailImpl> translationDetailList;
	private List<DummyResponseDetail> dummyResponseList;
	
	private String script;
	

	public TranslatorPolicyDataImpl(){
		this.translationDetailList=new ArrayList<TranslationDetailImpl>();		
		this.dummyResponseMap=new HashMap<String,String>();
		this.dummyResponseList = new ArrayList<DummyResponseDetail>();
	}
	
	@XmlElementWrapper(name = "dummy-responses")
	@XmlElement(name = "dummy-response")
	public List<DummyResponseDetail> getDummyResposeList() {
		return dummyResponseList;
	}

	public void setDummyResposeList(List<DummyResponseDetail> dummyResponseList) {
		this.dummyResponseList = dummyResponseList;
	}

	@XmlElementWrapper(name ="mappings")
	@XmlElement(name = "mapping")
	public List<TranslationDetailImpl> getTranslationDetailList() {
		return translationDetailList;
	}

	public void setTranslationDetailList(List <TranslationDetailImpl> translationDetailList) {
		if(translationDetailList != null)
			this.translationDetailList =translationDetailList;
	}

	@XmlElement(name = "from-interpreter-id" , type = String.class)
	public String getFromTranslatorId() {
		return fromInterpreterId;
	}
	
	public void setToTranslatorId(String toInterpreterId) {
		this.toInterpreterId = toInterpreterId;
	}


	@XmlElement(name = "to-interpreter-id" , type = String.class)
	public String getToTranslatorId() {
		return toInterpreterId;
	}

	public void setFromTranslatorId(String fromInterpreterId) {
		this.fromInterpreterId = fromInterpreterId;
	}
	
	public void setTransMapConfId(String transMapConfId){
		this.transMapConfId=transMapConfId;
	}
	
	public void setName(String name){
		this.name=name;
	}
	@XmlElement(name = "translation-mapping-id" , type = String.class)
	public String getTransMapConfId(){
		return this.transMapConfId;
	}
	
	@XmlElement(name = "name" , type = String.class)
	public String getName(){
		return this.name;
	}
	
	@XmlElement(name = "dummy-response-enable" , type = boolean.class,defaultValue ="false")
	public boolean getIsDummyResponse() {
		return isDummyResponse;
	}


	public void setIsDummyResponse(boolean isDummyResponse) {
		this.isDummyResponse = isDummyResponse;
	}

	@Override
	@XmlTransient
	public Map<String, String> getDummyResponseMap() {
		return dummyResponseMap;
	}


	public void setDummyResponseMap(HashMap<String, String> dummyResponseMap) {
		if(dummyResponseMap != null)
			this.dummyResponseMap = dummyResponseMap;
	}
	
	@XmlElement(name = "basetranslation-mapping-id" , type = String.class)
	public String getBaseTranslationMappingId() {
		return baseTranslationMappingId;
	}

	public void setBaseTranslationMappingId(String baseTranslationMappingId) {
		this.baseTranslationMappingId = baseTranslationMappingId;
	}

	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("    ");
		out.println("  -- Translator Policy Data  Configuration -- ");
		out.println("  Id                     = " + transMapConfId);
		out.println("  Name                   = " + name);
		out.println("  To InterpreterId       = " + toInterpreterId);
		out.println("  From InterpreterId     = " + fromInterpreterId);
		out.println("  Dummy Response Enabled = " + getIsDummyResponse());
		out.println("  Dummy Response Data    = " + getDummyResponseMap());
		out.println("  Base Translation ID    = " + baseTranslationMappingId);
		final int translationDetailSize=translationDetailList.size();
		for (int i=0; i< translationDetailSize; i++){
                out.println( translationDetailList.get(i) );
        }
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}

	@XmlElement(name ="script",type =String.class)
	public String getScript() {
		return this.script;
	}
	
	public void setScript(String script){
		this.script = script;
	}
}
