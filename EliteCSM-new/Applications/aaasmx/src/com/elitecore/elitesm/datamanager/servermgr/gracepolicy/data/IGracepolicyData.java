/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IGracepolicyData.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data;
   
public interface IGracepolicyData{

    public java.lang.String getGracePolicyId();
	public void setGracePolicyId(java.lang.String gracePolicyId);

    public String getName();
	public void setName(String name);

    public String getValue();
	public void setValue(String value);

}
