/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ISsubscriberdbfieldmapData.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.wsconfig.data;

import com.elitecore.commons.base.Differentiable;

public interface IWSDBFieldMapData extends Differentiable{
	public String getDbfieldId();
	public void setDbfieldId(String dbfieldId);
	
	public String getWsConfigId();
	public void setWsConfigId(String wsConfigId);
	
	public String getKey();
	public void setKey(String key);
	
	public String getFieldName();
	public void setFieldName(String fieldName);

	public Integer getOrderNumber() ;
	public void setOrderNumber(Integer orderNumber);

}
