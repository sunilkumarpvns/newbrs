package com.elitecore.nvsmx.policydesigner;

/**
 * Used to display list of policy designer modules
 * @author Dhyani.Raval
 *
 */
public enum PolicyDesignerModules {

	USAGE_QUOTA_PROFILE("Quota Profile","/view/policydesigner/quota/QuotaProfile"),
	SY_QUOTA_PROFILE("Sy Quota Profile","/view/policydesigner/syquota/SyQuotaProfile"),
	QOS_PROFILE("QoS Profile","/view/policydesigner/qos/QosProfile"),
	PCC_RULE("PCC Rule","/view/policydesigner/pccrule/PCCRuleData"),
	;

	private String val;
	
	private String jspUrl;


	PolicyDesignerModules(String val,String jspUrl){
		this.val = val;
		this.jspUrl = jspUrl;
	}

	public String getVal() {
		return val;
	}
	
	public String getJspUrl() {
		return jspUrl;
	}

}
