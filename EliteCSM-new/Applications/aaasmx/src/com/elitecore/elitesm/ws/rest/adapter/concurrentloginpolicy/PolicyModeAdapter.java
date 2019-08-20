package com.elitecore.elitesm.ws.rest.adapter.concurrentloginpolicy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;

/**
 * 
 * Policy Mode Adapter do conversion of Policy Mode, Policy Mode Name to Policy Mode Id and vice versa. <br>
 * It takes Policy Mode Name as input and give Policy Mode Id as output in unmarshal. <br>
 * It takes Policy Mode Id as input and give Policy Mode Name as output in marshal. <br>
 * For invalid values it gives String invalid.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <policy-mode>General</policy-mode>
 * 
 * }
 * 
 * than output is :
 * SMS0149
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class PolicyModeAdapter extends XmlAdapter<String, String> {

	@Override
    public String unmarshal(String value) throws Exception {
		
        String policyMode = null;
        
        if (ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_NAME.equalsIgnoreCase(value)) {
            policyMode = ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_ID;
        } else if (ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_NAME.equalsIgnoreCase(value)) {
            policyMode = ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_ID;
        } else {
            policyMode = "invalid";
        }
        return policyMode;
        
    }

    @Override
    public String marshal(String value) throws Exception {
    	
        String policyMode = null;
        
        if (ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_ID.equals(value)) {
            policyMode = ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_NAME;
        } else if (ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_ID.equals(value)) {
            policyMode = ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_NAME;
        } else {
            policyMode = null;
        }
        return policyMode;
        
    }
    
}
