package com.elitecore.netvertexsm.web.tableorder;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;

public class TableOrderData {
	private static TableOrderData tableOrderdata;
	private Map<String, InstanceClassDetail> classDetails;
	
	public final static String DIAMETERROUTING = "DIAMETERROUTING";
	public final static String RADIUSAUTHPOLICY = "RADIUSAUTHPOLICY";
	public final static String RADIUSACCTPOLICY = "RADIUSACCTPOLICY";
	public final static String DYNAUTHPOLICYINST = "DYNAUTHPOLICYINST";
	public final static String NASPOLICYINST = "NASPOLICYINST";
	public final static String CREDITCONTROLPOLICYINST = "CREDITCONTROLPOLICYINST";
	public final static String EAPPOLICYINST = "EAPPOLICYINST";
	public final static String RMCGPOLICYINST = "RMCGPOLICYINST" ; 
	
	
	public static TableOrderData getInstance(){
		if(tableOrderdata == null){
			tableOrderdata = new TableOrderData();
		}
		return tableOrderdata;
	}
	
	private TableOrderData(){
		init();
	};
	
	public void init(){
		classDetails = new HashMap<String, InstanceClassDetail>();
/*		classDetails.put(DIAMETERROUTING, new InstanceClassDetail(ConfigConstant.MANAGE_DIAMETER_ROUTING_TABLE_ORDER, DiameterRoutingConfData.class));
		classDetails.put(RADIUSAUTHPOLICY, new InstanceClassDetail(ConfigConstant.MANAGE_AUTH_POLICY_ORDER, AuthPolicyInstData.class));
		classDetails.put(RADIUSACCTPOLICY, new InstanceClassDetail(ConfigConstant.MANAGE_ACCT_POLICY_ORDER, AcctPolicyInstData.class));
		classDetails.put(DYNAUTHPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_DYNAUTH_POLICY_ORDER, DynAuthPolicyInstData.class));
		classDetails.put(NASPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_NAS_SERVICE_POLICY_ORDER, NASPolicyInstData.class));
		classDetails.put(CREDITCONTROLPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_CREDIT_CONTROL_SERVICE_POLICY_ORDER, CreditControlPolicyData.class));
		classDetails.put(EAPPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_DIAMETER_EAP_POLICY_ORDER, EAPPolicyData.class));
		classDetails.put(RMCGPOLICYINST,new InstanceClassDetail(ConfigConstant.MANAGE_CG_POLICY_ORDER, CGPolicyData.class));
*/	}
	
	public Class<? extends TableOrder> getClassFromName(String className){
		return classDetails.get(className).getInstanceClass();
	}
	
	public String getActionAlias(String className){
		return classDetails.get(className).getAction_alias();
	}
	
	private class InstanceClassDetail{
		String action_alias;
		Class<? extends TableOrder> instanceClass;
		
		public InstanceClassDetail(String action_alias,Class<? extends TableOrder> instanceClass) {
			this.action_alias = action_alias;
			this.instanceClass = instanceClass;
		}

		public Class<? extends TableOrder> getInstanceClass() {
			return instanceClass;
		}

		public String getAction_alias() {
			return action_alias;
		}

	}
}
