package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;

public class TrueFalseAdapter extends XmlAdapter<String, String>{

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String YES = "Y";
	private static final String NO = "N";

	@Override
    public String unmarshal(String value) throws Exception {
        String enable = null;
        
        if (TRUE.equalsIgnoreCase(value)) {
            enable = YES;
        } else if (FALSE.equalsIgnoreCase(value)) {
            enable = NO;
        } else {
            enable = TranslationMappingConfigConstants.INVALID_VALUE;
        }
        return enable;
    }

    @Override
    public String marshal(String value) throws Exception {
    	
        String enable = null;
        
        if (YES.equalsIgnoreCase(value)) {
            enable = TRUE;
        } else if (NO.equalsIgnoreCase(value)) {
            enable = FALSE;
        } else {
            enable = TranslationMappingConfigConstants.INVALID_VALUE;
        }
        return enable;
    }
}