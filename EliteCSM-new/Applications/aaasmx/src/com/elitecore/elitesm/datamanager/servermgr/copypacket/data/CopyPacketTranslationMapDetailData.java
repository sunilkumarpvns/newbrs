package com.elitecore.elitesm.datamanager.servermgr.copypacket.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlRootElement(name = "mapping-detail")
@XmlType(propOrder = {"operation", "checkExpression", "destinationExpression", "sourceExpression", "defaultValue", "valueMapping"})
public class CopyPacketTranslationMapDetailData extends BaseData implements Serializable,Differentiable {
	
	private static final long serialVersionUID = 1L;
	private String copyPacketMapDetailId;
	private String copyPacketMappingId;
	private Long orderNumber;
	@Pattern(regexp = "ADD|UPDATE|UPGRADE|DEL|MOVE", message ="Operation can be ADD, UPDATE, UPGRADE, DEL or MOVE only.")
	@NotEmpty(message = "Operation must be specified. It can be ADD, UPDATE, UPGRADE, DEL or MOVE.")
	private String operation;
	private String checkExpression;
	@NotEmpty(message = "Destination Expression must be specified.")
	private String destinationExpression;
	private String sourceExpression;
	private String defaultValue;
	private String valueMapping;
	private String mappingTypeId;
	
	@XmlTransient
	public String getCopyPacketMapDetailId() {
		return copyPacketMapDetailId;
	}
	public void setCopyPacketMapDetailId(String copyPacketMapDetailId) {
		this.copyPacketMapDetailId = copyPacketMapDetailId;
	}
	
	@XmlTransient
	public String getCopyPacketMappingId() {
		return copyPacketMappingId;
	}
	public void setCopyPacketMappingId(String copyPacketMappingId) {
		this.copyPacketMappingId = copyPacketMappingId;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNo) {
		this.orderNumber = orderNo;
	}
	
	@XmlElement(name = "operation")
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@XmlElement(name = "check-expression")
	public String getCheckExpression() {
		return checkExpression;
	}
	public void setCheckExpression(String checkExpression) {
		this.checkExpression = checkExpression;
	}
	
	@XmlElement(name= "destination-expression")
	public String getDestinationExpression() {
		return destinationExpression;
	}
	public void setDestinationExpression(String destinationExpression) {
		this.destinationExpression = destinationExpression;
	}
	
	@XmlElement(name = "source-expression")
	public String getSourceExpression() {
		return sourceExpression;
	}
	public void setSourceExpression(String sourceExpression) {
		this.sourceExpression = sourceExpression;
	}
	
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlElement(name = "value-mapping")
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	@XmlTransient
	public String getMappingTypeId() {
		return mappingTypeId;
	}
	public void setMappingTypeId(String mappingTypeId) {
		this.mappingTypeId = mappingTypeId;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ CopyPacketMappingInstDetailData --------------");
		writer.println("CopyPacketMapID 	     		 :"+copyPacketMapDetailId);
		writer.println("Operation 	     		 :"+operation);
		writer.println("checkExpression          :"+checkExpression);           
		writer.println("destinationExpression        :"+destinationExpression);
		writer.println("SourceExpression           :"+sourceExpression);
		writer.println("defaultValue          	 :"+defaultValue);       
		writer.println("valueMapping  			 :"+valueMapping);
		writer.println("mappingTypeId  			 :"+mappingTypeId);
		writer.println("CopyPacketMapInstanceId   :"+copyPacketMappingId);
		writer.println("------------------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
	@Override
	public JSONObject toJson(){
		JSONObject object = new JSONObject();
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Check Expression", checkExpression);
		innerObject.put("Operation", operation);
		innerObject.put("Destination Expression", destinationExpression);
		innerObject.put("Source Expression", sourceExpression);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);
		//Order Number is a unique filed so taken for auditing.
		if(orderNumber!=null){
			object.put(orderNumber, innerObject);
		}
		return object;
	}

}
