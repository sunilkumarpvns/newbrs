package com.elitecore.elitesm.datamanager.radius.radtest.data;

import net.sf.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class RadiusTestParamData extends BaseData implements IRadiusTestParamData,Differentiable{
    
    private String ntRadParamId;
    private String ntradId;
    private String name;
    private String value;
    private Integer orderNumber;
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    
    
    public String getNtRadParamId() {
		return ntRadParamId;
	}

	public void setNtRadParamId(String ntRadParamId) {
		this.ntRadParamId = ntRadParamId;
	}

	public String getNtradId() {
		return ntradId;
	}

	public void setNtradId(String ntradId) {
		this.ntradId = ntradId;
	}

	public String getValue( ) {
        return value;
    }
    
    public void setValue( String value ) {
        this.value = value;
    }

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Parameter Name", name);
		object.put("Parameter Value", value);
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
