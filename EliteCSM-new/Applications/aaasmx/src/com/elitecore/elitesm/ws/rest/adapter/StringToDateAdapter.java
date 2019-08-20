package com.elitecore.elitesm.ws.rest.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringToDateAdapter extends XmlAdapter<String, Date>{

	@Override
	public String marshal(Date date) throws Exception {
		return String.valueOf(date);
	}

	@Override
	public Date unmarshal(String string) throws Exception {
	    Date date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").parse(string);  
		return date;
	}

}
