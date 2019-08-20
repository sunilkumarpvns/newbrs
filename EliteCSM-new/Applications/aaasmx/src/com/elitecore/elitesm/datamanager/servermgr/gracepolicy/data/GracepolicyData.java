/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   GracepolicyData.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
   
@XmlRootElement(name = "grace-policy")
@XmlType(propOrder = { "name", "value" })
public class GracepolicyData extends BaseData implements IGracepolicyData,Differentiable{

    private java.lang.String gracePolicyId;
    
    @NotEmpty(message = "Grace Policy Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 25, message = "Length of Grace Policy Name must not greater than 25.")
    private String name;
    
    @NotEmpty(message = "Grace Policy Value must be specified.")
	@Pattern(regexp = "(\\d+([,]\\d+)*)*", message = "Grace Policy value must be comma seperated numeric value.")
    @Length(max = 25, message = "Length of Grace Policy Value must not greater than 25.")
    private String value;

    @XmlTransient
    public java.lang.String getGracePolicyId(){
        return gracePolicyId;
    }

	public void setGracePolicyId(java.lang.String gracePolicyId) {
		this.gracePolicyId = gracePolicyId;
	}

	@XmlElement(name = "name")
    public String getName(){
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "value")
    public String getValue(){
        return value;
    }

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public JSONObject toJson() {
		JSONObject outterObject = new JSONObject();
		JSONObject innerObject = new JSONObject();
		
		innerObject.put("Value", value);
		if(name != null) {
			outterObject.put(name,innerObject);
		}
		return outterObject;
	}

}
