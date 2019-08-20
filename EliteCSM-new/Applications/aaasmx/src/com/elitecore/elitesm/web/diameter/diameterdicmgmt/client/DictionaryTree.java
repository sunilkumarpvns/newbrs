package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.Iterator;
import java.util.List;

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
		dictionaryItem.setHTML(dictionaryItem.getDictionaryData().getApplicationName());
		this.addItem(dictionaryItem);
		List<AttributeItem> attribuList = dictionaryItem.getAttributeItemList();

		for (Iterator iterator = attribuList.iterator(); iterator.hasNext();) 
		{
			AttributeItem attributeItem = (AttributeItem) iterator.next();
			dictionaryItem.addItem(attributeItem);
		}

	}


}
