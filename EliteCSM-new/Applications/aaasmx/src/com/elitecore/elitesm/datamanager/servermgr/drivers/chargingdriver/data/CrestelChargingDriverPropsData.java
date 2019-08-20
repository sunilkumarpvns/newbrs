package com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlRootElement(name = "jndi-property")
@XmlType(propOrder = {"name", "value"})
public class CrestelChargingDriverPropsData extends BaseData implements Serializable,Differentiable{
	private static final long serialVersionUID = 1L;
	private String propertyId;
	@NotEmpty(message = "Property must be specified")
	private String name;
	@NotEmpty(message = "Value must be specified")
	private String value;
	private String crestelChargingDriverId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getPropertyId() {
		return propertyId;
	}
	
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	@XmlElement(name = "property")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public String getCrestelChargingDriverId() {
		return crestelChargingDriverId;
	}

	public void setCrestelChargingDriverId(String crestelChargingDriverId) {
		this.crestelChargingDriverId = crestelChargingDriverId;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		if(name != null){
			object.put(name, 
					new JSONObject().accumulate("Value", value)
					);
		}
		
		return object;
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
