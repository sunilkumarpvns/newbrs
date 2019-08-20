package com.elitecore.elitesm.ws.rest.adapter.concurrentloginpolicy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;

/**
 * 
 * Policy Type Adapter do conversion of Policy Type, Policy Type Name to Policy Type Id and vice versa. <br>
 * It takes Policy Type Name as input and give Policy Type Id as output in unmarshal. <br>
 * It takes Policy Type Id as input and give Policy Type Name as output in marshal. <br>
 * For invalid values it gives String invalid.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <policy-type>Group</policy-type>
 * 
 * }
 * 
 * than output is :
 * SMS0139
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class PolicyTypeAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {

		String policyType = null;

		if (ConcurrentLoginPolicyConstant.POLICY_TYPE_INDIVIDUAL_NAME.equalsIgnoreCase(value)) {
			policyType = ConcurrentLoginPolicyConstant.POLICY_TYPE_INDIVIDUAL_ID;
		} else if (ConcurrentLoginPolicyConstant.POLICY_TYPE_GROUP_NAME.equalsIgnoreCase(value)) {
			policyType = ConcurrentLoginPolicyConstant.POLICY_TYPE_GROUP_ID;
		} else {
			policyType = "invalid";
		}
		return policyType;

	}

    @Override
	public String marshal(String value) throws Exception {

		String policyType = null;

		if (ConcurrentLoginPolicyConstant.POLICY_TYPE_INDIVIDUAL_ID.equals(value)) {
			policyType = ConcurrentLoginPolicyConstant.POLICY_TYPE_INDIVIDUAL_NAME;
		} else if (ConcurrentLoginPolicyConstant.POLICY_TYPE_GROUP_ID.equals(value)) {
			policyType = ConcurrentLoginPolicyConstant.POLICY_TYPE_GROUP_NAME;
		} else {
			policyType = null;
		}
		return policyType;

	}
    
}
