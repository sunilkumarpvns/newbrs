/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateGracepolicyForm.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.gracepolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class InitGracepolicyForm extends BaseWebForm{

    private java.lang.Long gracePolicyId;
    private String gracePolicyName;
    private String gracePolicyValue;
    


    public java.lang.Long getGracePolicyId(){
        return gracePolicyId;
    }

	public void setGracePolicyId(java.lang.Long gracePolicyId) {
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


    

}
