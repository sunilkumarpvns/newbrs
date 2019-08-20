package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.AccessPolicyConstant;

/**
 * 
 * Default Access Adapter do conversion of status, full form to short form and vice versa. <br>
 * It takes full form status as input and give short form status as output in unmarshal. <br>
 * It takes short form status as input and give full form status as output in marshal. <br>
 * For invalid values it gives String invalid.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <defaut-access>allowed</defaut-access>
 * 
 * }
 * 
 * than output is :
 * A
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class DefaultAccessAdapter extends XmlAdapter<String, String> {

	@Override
    public String unmarshal(String value) throws Exception {
        String accessStatus = null;
        
        if (AccessPolicyConstant.ALLOWED.equalsIgnoreCase(value)) {
            accessStatus = AccessPolicyConstant.ALLOWED_VALUE;
        } else if (AccessPolicyConstant.DENIED.equalsIgnoreCase(value)) {
            accessStatus = AccessPolicyConstant.DENIED_VALUE;
        } else {
            accessStatus = "invalid";
        }
        return accessStatus;
    }

    @Override
    public String marshal(String value) throws Exception {
    	
        String accessStatus = null;
        
        if (AccessPolicyConstant.ALLOWED_VALUE.equals(value)) {
            accessStatus = AccessPolicyConstant.ALLOWED;
        } else if (AccessPolicyConstant.DENIED_VALUE.equals(value)) {
            accessStatus = AccessPolicyConstant.DENIED;
        } else {
            accessStatus = null;
        }
        return accessStatus;
    }
}
