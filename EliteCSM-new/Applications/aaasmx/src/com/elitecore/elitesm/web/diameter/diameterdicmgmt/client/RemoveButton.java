package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Button;

public class RemoveButton extends Button{

	private int index;
	private String type;
	private Object object;
	public RemoveButton(int index,String name,String type,Object obj){
		super(name);
		this.index=index;
		this.type=type;
		this.object=obj;
		Log.info("RemoveButton called");
	}
	public int getIndex(){
		return index;
	}
	
	public String getType(){
		return type;
	}
	
	public Object getObject(){
		return object;
	}
	
	
}
