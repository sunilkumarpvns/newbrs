package com.elitecore.aaa.diameter.conf.impl;

import static com.elitecore.commons.base.Strings.repeat;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
public class DiameterWebServiceConfigurationDetail {
	
	private ReAuthTranslationMappingDetail reAuthTranslationMappingDetail;
	private AbortSessionTranslationMappingDetail abortSessionTranslationMappingDetail;
	private GenericTranslationMappingDetail genericTranslationMappingDetail;

	public DiameterWebServiceConfigurationDetail() {
		this.reAuthTranslationMappingDetail = new ReAuthTranslationMappingDetail();
		this.abortSessionTranslationMappingDetail = new AbortSessionTranslationMappingDetail();
		this.genericTranslationMappingDetail = new GenericTranslationMappingDetail();
	}

	@XmlElement(name="diameter-re-auth")
	public ReAuthTranslationMappingDetail getReAuthTranslationMappingDetail() {
		return reAuthTranslationMappingDetail;
	}

	public void setReAuthTranslationMappingDetail(
			ReAuthTranslationMappingDetail reAuthTranslationMappingDetail) {
		this.reAuthTranslationMappingDetail = reAuthTranslationMappingDetail;
	}
	@XmlElement(name="diameter-abort-session")
	public AbortSessionTranslationMappingDetail getAbortSessionTranslationMappingDetail() {
		return abortSessionTranslationMappingDetail;
	}

	public void setAbortSessionTranslationMappingDetail(
			AbortSessionTranslationMappingDetail abortSessionTranslationMappingDetail) {
		this.abortSessionTranslationMappingDetail = abortSessionTranslationMappingDetail;
	}

	@XmlElement(name="diameter-generic-request")
	public GenericTranslationMappingDetail getGenericTranslationMappingDetail() {
		return genericTranslationMappingDetail;
	}

	public void setGenericTranslationMappingDetail(
			GenericTranslationMappingDetail genericTranslationMappingDetail) {
		this.genericTranslationMappingDetail = genericTranslationMappingDetail;
	}

	@Override
	public String toString() {
	
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.print(getReAuthTranslationMappingDetail());
		out.print(getAbortSessionTranslationMappingDetail());
		out.print(getGenericTranslationMappingDetail());
		out.close();
		return stringBuffer.toString();
	}
	
}









