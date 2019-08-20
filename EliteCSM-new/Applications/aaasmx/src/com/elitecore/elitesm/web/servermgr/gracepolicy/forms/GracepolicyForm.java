/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateGracepolicyForm.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.gracepolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class GracepolicyForm extends BaseWebForm{

    private java.lang.String gracePolicyId;
    private String gracePolicyName;
    private String gracePolicyValue;
    private String action;

    public java.lang.String getGracePolicyId(){
        return gracePolicyId;
    }

	public void setGracePolicyId(java.lang.String gracePolicyId) {
		this.gracePolicyId = gracePolicyId;
	}
    
	public String getGracePolicyName() {
		return gracePolicyName;
	}

	public void setGracePolicyName(String gracePolicyName) {
		this.gracePolicyName = gracePolicyName;
	}

	public String getGracePolicyValue() {
		return gracePolicyValue;
	}

	public void setGracePolicyValue(String gracePolicyValue) {
		this.gracePolicyValue = gracePolicyValue;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	

}
