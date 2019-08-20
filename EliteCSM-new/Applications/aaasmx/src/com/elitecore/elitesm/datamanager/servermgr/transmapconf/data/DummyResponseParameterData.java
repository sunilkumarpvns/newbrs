package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlRootElement(name = "dummy-response-parameter")
@XmlType(propOrder = {"outField", "value"})
public class DummyResponseParameterData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String parameterId;
	private String outField;
	private String value;
	private String translationMapConfigId;
	private Integer orderNumber;
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	@XmlTransient
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	
	@XmlElement(name = "out-field")
	public String getOutField() {
		return outField;
	}
	public void setOutField(String outField) {
		this.outField = outField;
	}
	
	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new  IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getSimpleName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("parameterId :" + parameterId);
		writer.println("outField :" + outField);
		writer.println("value :" + value);
		writer.println("translationMapConfigId :" + translationMapConfigId);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}
	
	@Override
	public JSONObject toJson(){
		 JSONObject object=new JSONObject();
		 if(outField!=null){
			 object.put(outField, 
					 new JSONObject().accumulate("Value", value)
					 );
		 }
		 return object;
	}
}

