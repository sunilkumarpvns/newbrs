package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlRootElement(name = "parameter")
@XmlType(propOrder = {"checkExpression", "mappingExpression", "defaultValue", "valueMapping"})
public class TranslationMappingInstDetailData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	
	private String detailId;
	private String checkExpression;
	private String mappingExpression;
	private String defaultValue;
	private String valueMapping;
	private String defaultMapping;
	private String mappingTypeId;
	private String translationMapConfigId;
	private Long orderNumber;
	
	@XmlTransient
	public String getDetailId() {
		return detailId;
	}
	
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@XmlElement(name = "check-expression")
	public String getCheckExpression() {
		return checkExpression;
	}
	
	public void setCheckExpression(String checkExpression) {
		this.checkExpression = checkExpression;
	}
	
	@XmlElement(name = "mapping-expression")
	public String getMappingExpression() {
		return mappingExpression;
	}
	
	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}
	
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlTransient
	public String getMappingTypeId() {
		return mappingTypeId;
	}
	public void setMappingTypeId(String mappingTypeId) {
		this.mappingTypeId = mappingTypeId;
	}
	
	@XmlTransient
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	@XmlElement(name = "value-mapping")
	public String getValueMapping() {
		return valueMapping;
	}
	
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	@XmlTransient
	public String getDefaultMapping() {
		return defaultMapping;
	}
	
	public void setDefaultMapping(String defaultMapping) {
		this.defaultMapping = defaultMapping;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ TranslationMappingInstDetailData --------------");
		writer.println("detailId 	     		 :"+detailId);
		writer.println("checkExpression          :"+checkExpression);           
		writer.println("mappingExpression        :"+mappingExpression);
		writer.println("defaultMapping           :"+defaultMapping);
		writer.println("defaultValue          	 :"+defaultValue);       
		writer.println("valueMapping  			 :"+valueMapping);
		writer.println("mappingTypeId  			 :"+mappingTypeId);
		writer.println("translationMapConfigId   :"+translationMapConfigId);
		writer.println("------------------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
	
	public JSONObject toJson(){
		JSONObject object = new JSONObject();
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Mapping Expression", mappingExpression);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);
		
		if(checkExpression!=null){
			object.put(checkExpression, innerObject);
		}
		return object;
	}
}

