package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import com.google.gwt.user.client.ui.Button;

public class RemoveButton extends Button{

	private int index;
	public RemoveButton(int index,String name){
		super(name);
		this.index=index;
	}
	public int getIndex(){
		return index;
	}
	
}
