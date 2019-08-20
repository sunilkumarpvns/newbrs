package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.BaseConstant;

public class StatusAdapter extends XmlAdapter<String, String>{

	private static final String EMPTY = "";
	private static final String ACTIVE = "ACTIVE";
	private static final String INACTIVE = "INACTIVE";

	@Override
    public String unmarshal(String value) throws Exception {
        String status = null;
        
        if (ACTIVE.equalsIgnoreCase(value)) {
            status = BaseConstant.SHOW_STATUS_ID;
        } else if (INACTIVE.equalsIgnoreCase(value)) {
            status = BaseConstant.HIDE_STATUS_ID;
        } else if(EMPTY.equalsIgnoreCase(value)){
        	status = EMPTY;
        } else {
            status = "invalid";
        }
        return status;
    }

    @Override
    public String marshal(String value) throws Exception {
    	
        String status = null;
        
        if (BaseConstant.SHOW_STATUS_ID.equals(value)) {
            status = ACTIVE;
        } else if (BaseConstant.HIDE_STATUS_ID.equals(value)) {
            status = INACTIVE;
        } else {
            status = null;
        }
        return status;
    }
}