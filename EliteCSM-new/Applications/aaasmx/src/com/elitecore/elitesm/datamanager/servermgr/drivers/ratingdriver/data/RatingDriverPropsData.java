package com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlType(propOrder = { "name", "value" })
public class RatingDriverPropsData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String propertyId;
	@NotEmpty(message = "Property be must specified")
	private String name;
	@NotEmpty(message = "Value must be specified")
	private String value;
	private String crestelRatingDriverId;
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
	public String getCrestelRatingDriverId() {
		return crestelRatingDriverId;
	}
	public void setCrestelRatingDriverId(String crestelRatingDriverId) {
		this.crestelRatingDriverId = crestelRatingDriverId;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		if(name!=null){
			object.put(name,
					new JSONObject().accumulate("Value", value)
					);
		}
		return object;
	}

}