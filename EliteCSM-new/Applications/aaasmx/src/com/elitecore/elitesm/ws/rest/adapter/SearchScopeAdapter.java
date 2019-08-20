package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * Contains this code for the field <b>Search Scope</b> of <b>LDAP Driver</b>for converting respective value to their 
 * provided number in case of communication via REST API.
 * @author Tejas.P.Shah
 *
 */
public class SearchScopeAdapter  extends XmlAdapter<String, String>{

	/*
	 * When Search Scope value comes by REST Request at that time it will convert them into
	 * their respective number
	 * */
	@Override
	public String unmarshal(String searchScopeValue) throws Exception {
		if(searchScopeValue.equalsIgnoreCase("SCOPE_BASE") ){
			return "0";
		}else if(searchScopeValue.equalsIgnoreCase("SCOPE_ONE")){
			return "1";
		}else if(searchScopeValue.equalsIgnoreCase("SCOPE_SUB")){
			return "2";
		}
		return "";
	}

	/*
	 * When Search Scope value fetch from DB it will marshall them into required value
	 * */
	@Override
	public String marshal(String searchScopeValue) throws Exception {
		if(searchScopeValue.equalsIgnoreCase("0") ){
			return "SCOPE_BASE";
		}else if(searchScopeValue.equalsIgnoreCase("1")){
			return "SCOPE_ONE";
		}else if(searchScopeValue.equalsIgnoreCase("2")){
			return "SCOPE_SUB";
		}
		return  searchScopeValue;
	}
}