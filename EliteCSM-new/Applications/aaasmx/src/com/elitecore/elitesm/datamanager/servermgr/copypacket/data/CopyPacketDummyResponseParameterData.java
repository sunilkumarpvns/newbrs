package com.elitecore.elitesm.datamanager.servermgr.copypacket.data;

import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlType(propOrder = {"outField", "value"})
public class CopyPacketDummyResponseParameterData extends BaseData implements Serializable , Differentiable {
	private static final long serialVersionUID = 1L;
	private String dummyResponseParameterId;
	private String outField;
	private String value;
	private String copyPacketTransConfId;
	private Integer orderNumber ;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	@XmlTransient
	public String getDummyResponseParameterId() {
		return dummyResponseParameterId;
	}

	public void setDummyResponseParameterId(String dummyResponseParameterId) {
		this.dummyResponseParameterId = dummyResponseParameterId;
	}
	
	@XmlElement(name = "out-field")
	@NotEmpty(message = "Out Field of Dummy Response Parameter must be specified.")
	public String getOutField() {
		return outField;
	}
	public void setOutField(String outField) {
		this.outField = outField;
	}
	
	@XmlElement(name = "value")
	@NotEmpty(message = "Value of Dummy Response Parameter must be specified.")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public String getCopyPacketTransConfId() {
		return copyPacketTransConfId;
	}
	public void setCopyPacketTransConfId(String copyPacketTransConfId) {
		this.copyPacketTransConfId = copyPacketTransConfId;
	}
	
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new  IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getSimpleName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("DummyResponseParameterId :" + dummyResponseParameterId);
		writer.println("OutField :" + outField);
		writer.println("Value :" + value);
		writer.println("copyPacketTransConfigId :" + copyPacketTransConfId);
		
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
