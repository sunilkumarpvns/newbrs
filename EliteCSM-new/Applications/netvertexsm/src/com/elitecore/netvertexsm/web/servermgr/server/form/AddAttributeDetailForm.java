package com.elitecore.netvertexsm.web.servermgr.server.form;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class AddAttributeDetailForm extends BaseWebForm{

	String action;	
	List listDictionaryId=new ArrayList();
	List listDictionary=new ArrayList();
	
	public List getListDictionary(){
		return listDictionary;
	}
	public void setListDictionary(List listDictionary){
		this.listDictionary=listDictionary;
	}
	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
	public List getListDictionaryId(){
		return listDictionaryId;
	}
	
	public void setListDictionaryId(List listDictionaryId){
		this.listDictionaryId=listDictionaryId;
	}
}
