package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.google.gwt.user.client.ui.TreeItem;

public class AttributeItem extends TreeItem {
	
	boolean isInitialized;
	AttributeData attributeData;
	
	
	public AttributeItem(AttributeData attributeData){
		isInitialized= false;
		this.attributeData=attributeData;
		this.setHTML(attributeData.getName());
    }

	
	public AttributeItem() {
		attributeData=new AttributeData();
	}


	public void initialized(){
		isInitialized = true;
	}
	
	public boolean isInitialized(){
		return isInitialized;
	}


	public AttributeData getAttributeData() {
		return attributeData;
	}


	public void setAttributeData(AttributeData attributeData) {
		this.attributeData = attributeData;
	}

	public void setName(String name){
		this.setHTML(name);
	}
    
	
	
}
