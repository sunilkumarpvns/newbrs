package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.AttributeData;
import com.google.gwt.user.client.ui.Tree;

public class DictionaryTree extends Tree {



	public DictionaryTree(DictionaryItem dictionaryItem) {
		buildTree(dictionaryItem);
		

	}
	public void setDictionary(DictionaryItem dictionaryItem){
		this.clear();
		buildTree(dictionaryItem);
		dictionaryItem.setState(true);
		
	}

	private void buildTree(DictionaryItem dictionaryItem) {
		this.clear();
		dictionaryItem.setHTML(dictionaryItem.getDictionaryData().getName());
		this.addItem(dictionaryItem);
		List<AttributeItem> attribuList = dictionaryItem.getAttributeItemList();

		for (Iterator iterator = attribuList.iterator(); iterator.hasNext();) 
		{
			
			AttributeItem attributeItem = (AttributeItem) iterator.next();
			attributeItem = setAttributeNodeToTree(attributeItem);
			dictionaryItem.addItem(attributeItem);
		}
		

	}
	private AttributeItem setAttributeNodeToTree(AttributeItem attributeItem) {
		 
		  Collection<AttributeData> childAttributeList = attributeItem.getAttributeData().getChildAttributeList();
		  if(childAttributeList != null && !childAttributeList.isEmpty() ){
			 
			  for (Iterator iterator = childAttributeList.iterator(); iterator.hasNext();) {
	   
				 AttributeData attributeData = (AttributeData) iterator.next();
				 AttributeItem childAttributeItem = new AttributeItem(attributeData);
				 childAttributeItem = setAttributeNodeToTree(childAttributeItem); 
				 attributeItem.addItem(childAttributeItem);
				 
			}
			 
		  }
		  
		  return attributeItem; 
		
	}






}
