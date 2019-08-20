package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DictionaryData;
import com.google.gwt.user.client.ui.TreeItem;

public class DictionaryItem extends TreeItem {
	
	boolean isInitialized;
	List<AttributeItem> attributeItemList = new ArrayList<AttributeItem>();
	DictionaryData dictionaryData;
	
	

	
	public void initialized(){
		isInitialized = true;
		
	}
	
	public boolean isInitialized(){
		return isInitialized;
	}

	public List<AttributeItem> getAttributeItemList() {
		return attributeItemList;
	}

	public void setAttributeItemList(List<AttributeItem> attributeItemList) {
		this.attributeItemList = attributeItemList;
	}

	public DictionaryData getDictionaryData() {
		return dictionaryData;
	}

	public void setDictionaryData(DictionaryData dictionaryData) {
		this.dictionaryData = dictionaryData;
	}
  
	public void setName(String name){
		this.setHTML(name);
	}
	


}
