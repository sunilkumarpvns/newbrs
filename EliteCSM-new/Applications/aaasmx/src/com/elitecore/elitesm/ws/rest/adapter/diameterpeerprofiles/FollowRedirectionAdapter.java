package com.elitecore.elitesm.ws.rest.adapter.diameterpeerprofiles;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;

/**
 * 
 * Follow Redirection Adapter do conversion of Follow Redirection, Enabled to True and vice versa. <br>
 * It takes Enabled or Disabled as input and give True or False respectively as output in unmarshal. <br>
 * It takes True or False as input and give full Enabled or Disabled respectively as output in marshal. <br>
 * For invalid values it gives String invalid.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <follow-redirection>Enabled</follow-redirection>
 * 
 * }
 * 
 * than output is :
 * true
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class FollowRedirectionAdapter extends XmlAdapter<String, String> {

	@Override
    public String unmarshal(String value) throws Exception {
		
        String followRedirection = null;
        
        	if ("Enabled".equalsIgnoreCase(value)) {
        		followRedirection = "true";
        	} else if ("Disabled".equalsIgnoreCase(value)) {
        		followRedirection = "false";
        	} else {
        		followRedirection = "invalid";
        	}
        
        return followRedirection;
        
    }

    @Override
    public String marshal(String value) throws Exception {
    	
        String followRedirection = null;
        
        if ("true".equals(value)) {
            followRedirection = "Enabled";
        } else if ("false".equals(value)) {
            followRedirection = "Disabled";
        } else {
            followRedirection = null;
        }
        
        return followRedirection;
        
    }
    
}
