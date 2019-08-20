package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;
import java.io.StringWriter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class TranslatorTypeData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String translatorTypeId;
	private String name;
	private String value;
	private String translateTo;
	private String translateFrom;
	

	public String getTranslatorTypeId() {
		return translatorTypeId;
	}
	public void setTranslatorTypeId(String translatorTypeId) {
		this.translatorTypeId = translatorTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTranslateTo() {
		return translateTo;
	}
	public void setTranslateTo(String translateTo) {
		this.translateTo = translateTo;
	}
	public String getTranslateFrom() {
		return translateFrom;
	}
	public void setTranslateFrom(String translateFrom) {
		this.translateFrom = translateFrom;
	}
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getSimpleName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("translatorTypeId :" + translatorTypeId);
		writer.println("name :" + name);
		writer.println("value :" + value);
		writer.println("translateTo :" + translateTo);
		writer.println("translateFrom :" + translateFrom);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	public JSONObject toJson(){
		return new JSONObject()
			.accumulate("translatorTypeId", translatorTypeId)
			.accumulate("name", name)
			.accumulate("value", value)
			.accumulate("translateTo", translateTo)
			.accumulate("translateFrom", translateFrom);
	}

	public Object clone() throws CloneNotSupportedException {
		TranslatorTypeData translatorTypeData = new TranslatorTypeData();
		
		if(this.name != null)
			translatorTypeData.name = new String(this.name);
		if(this.value != null)
			translatorTypeData.value = new String(this.value);
		if(this.translateTo != null)
			translatorTypeData.translateTo = new String(this.translateTo);
		if(this.translateFrom != null)
			translatorTypeData.translateFrom = new String(this.translateFrom);

		return translatorTypeData;
		
	}
	
	
}
