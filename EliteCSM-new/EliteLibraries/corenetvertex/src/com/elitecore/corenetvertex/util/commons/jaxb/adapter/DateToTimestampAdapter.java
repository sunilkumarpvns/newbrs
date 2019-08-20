package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateToTimestampAdapter extends XmlAdapter<Date, Timestamp> {
	
	public static final String MODULE = "DATE-TO-TIMESTAMP-ADAPTER";
	@Override
	public Date marshal(Timestamp timeStamp) throws Exception {
		if(timeStamp != null){
			return new Date(timeStamp.getTime());
		}
		return null;
	}

	@Override
	public Timestamp unmarshal(Date date) throws Exception {
			if(date != null){
				return new Timestamp(date.getTime());
			}
		return null;
			
	}

}
