package com.elitecore.diameterapi.diameter.translator.data.impl;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;

public class CopyPacketTranslatorPolicyDataImpl implements
		CopyPacketTranslatorPolicyData {

	private String copyPacketMapConfId;
	private String name;
	private String fromTranslatorId;
	private String toTranslatorId;
	private List<CopyPacketTranslationConfigDataImpl> translationConfigDataList;
	private String script;
	private List<DummyResponseDetail> dummyResponseList;

	public CopyPacketTranslatorPolicyDataImpl() {
		// For JAXB
	}
	@Override
	@XmlElement(name = "copy-packet-mapping-id" , type = String.class)
	public String getCopyPacketMapConfId() {
		return copyPacketMapConfId;
	}

	@Override
	@XmlElement(name = "name" , type = String.class)
	public String getName() {
		return name;
	}

	@Override
	@XmlElement(name = "from-interpreter-id" , type = String.class)
	public String getFromTranslatorId() {
		return fromTranslatorId;
	}

	@Override
	@XmlElement(name = "to-interpreter-id" , type = String.class)
	public String getToTranslatorId() {
		return toTranslatorId;
	}

	@Override
	@XmlElementWrapper(name ="copy-packet-mappings")
	@XmlElement(name = "copy-packet-mapping")
	public List<CopyPacketTranslationConfigDataImpl> getTranslationConfigDataList() {
		return translationConfigDataList;
	}
	
	@XmlElementWrapper(name = "dummy-responses")
	@XmlElement(name = "dummy-response")
	public List<DummyResponseDetail> getDummyResponseList() {
		return dummyResponseList;
	}

	@Override
	@XmlElement(name ="script",type =String.class)
	public String getScript() {
		return script;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFromTranslatorId(String fromTranslatorId) {
		this.fromTranslatorId = fromTranslatorId;
	}

	public void setToTranslatorId(String toTranslatorId) {
		this.toTranslatorId = toTranslatorId;
	}

	public void setTranslationConfigDataList(
			List<CopyPacketTranslationConfigDataImpl> translationConfigDataList) {
		this.translationConfigDataList = translationConfigDataList;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setDummyResponseList(List<DummyResponseDetail> dummyResponseList) {
		this.dummyResponseList = dummyResponseList;
	}
	
	public void setCopyPacketMapConfId(String copyPacketMapConfId) {
		this.copyPacketMapConfId = copyPacketMapConfId;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.println("Copy Packet Translation Policy Configuration: " + getName());
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "ID", getCopyPacketMapConfId()));
		out.println(format("%-30s: %s", "From Translator ID", getFromTranslatorId()));
		out.println(format("%-30s: %s", "To Translator ID", getToTranslatorId()));
		out.println(format("%-30s: %s", "Script", getScript() != null ? getScript() : ""));
		if(!Collectionz.isNullOrEmpty(translationConfigDataList)){
			for(int i = 0 ; i < translationConfigDataList.size() ; i++ ){
				out.println(repeat("-", 70));
				out.print(translationConfigDataList.get(i));
			}
		}
		out.println(repeat("-", 70));
		out.println("Dummy Mappings: ");
		if(!Collectionz.isNullOrEmpty(dummyResponseList)){
			for(int i = 0 ; i < dummyResponseList.size() ; i++ ){
				out.println(repeat("-", 70));
				out.print(dummyResponseList.get(i));
			}
		}
		out.close();
		return stringBuffer.toString();
	}

}
