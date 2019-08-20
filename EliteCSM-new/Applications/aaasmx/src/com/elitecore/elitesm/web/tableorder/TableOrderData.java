package com.elitecore.elitesm.web.tableorder;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.tableorder.TableOrder;

public class TableOrderData {
	private static TableOrderData tableOrderdata;
	private Map<String, InstanceClassDetail> classDetails;
	
	public final static String DIAMETERROUTING = "DIAMETERROUTING";
	public final static String RADIUSSERVICEPOLICY = "RADIUSSERVICEPOLICY";
	public final static String DYNAUTHPOLICYINST = "DYNAUTHPOLICYINST";
	public final static String NASPOLICYINST = "NASPOLICYINST";
	public final static String CREDITCONTROLPOLICYINST = "CREDITCONTROLPOLICYINST";
	public final static String EAPPOLICYINST = "EAPPOLICYINST";
	public final static String RMCGPOLICYINST = "RMCGPOLICYINST" ; 
	public final static String MANAGEDASHBOARD="MANAGEDASHBOARD";
	public final static String TGPPAAAPOLICYINST = "TGPPAAAPOLICYINST";
	
	
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
		System.out.println("Enter in table data");
		classDetails = new HashMap<String, InstanceClassDetail>();
		classDetails.put(DIAMETERROUTING, new InstanceClassDetail(ConfigConstant.MANAGE_DIAMETER_ROUTING_TABLE_ORDER, DiameterRoutingConfData.class));
		classDetails.put(RADIUSSERVICEPOLICY, new InstanceClassDetail(ConfigConstant.MANAGE_RADIUS_SERVICE_POLICY_ORDER, RadServicePolicyData.class));
		classDetails.put(DYNAUTHPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_DYNAUTH_POLICY_ORDER, DynAuthPolicyInstData.class));
		classDetails.put(NASPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_NAS_SERVICE_POLICY_ORDER, NASPolicyInstData.class));
		classDetails.put(CREDITCONTROLPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_CREDIT_CONTROL_SERVICE_POLICY_ORDER, CreditControlPolicyData.class));
		classDetails.put(EAPPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_DIAMETER_EAP_POLICY_ORDER, EAPPolicyData.class));
		classDetails.put(RMCGPOLICYINST,new InstanceClassDetail(ConfigConstant.MANAGE_CG_POLICY_ORDER, CGPolicyData.class));
		classDetails.put(MANAGEDASHBOARD,new InstanceClassDetail(ConfigConstant.MANAGE_DASHBOARD_ACTION, DashboardUserRelData.class));
		classDetails.put(TGPPAAAPOLICYINST, new InstanceClassDetail(ConfigConstant.MANAGE_TGPP_AAA_SERVICE_POLICY_ORDER, TGPPAAAPolicyData.class));
	}
	
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
