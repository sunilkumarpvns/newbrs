package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class AcctPolicyInstData extends BaseData implements java.io.Serializable,Differentiable {

	// Fields
	private static final long serialVersionUID = 1L;
	private Long acctPolicyId;
	private String name;
	private String description;
	private String status;
	private Long orderNumber;
	private String ruleSet;
	private String responseAttributes;
	
	private String cuiAttribute;
	private String multipleUserIdentity;
	private String acctMethod;
	private String validateAcctPacket;
	private String acctPolicyIdEsiTypeId;
	
	private List<AcctPolicyAdditionalDriverRelData>  additionalDriverList;
	
	private List<AcctPolicyMainDriverRelData> mainDriverList;
	
	private AcctPolicyRMParamsData prepaidRMParamsData;
	private AcctPolicyRMParamsData ipPoolRMParamsData;
	private AcctPolicyRMParamsData chargingGatewayRMParamsData;
	
	private List<AcctPolicyExternalSystemRelData> proxyServerRelList;
	private List<AcctPolicyExternalSystemRelData> ipPoolServerRelList;
	private List<AcctPolicyExternalSystemRelData> prepaidServerRelList;
	private List<AcctPolicyExternalSystemRelData> chargingGatewayServerRelList;
	
	private List<AcctPolicyBroadcastESIRelData> broadcastingServerRelList;
	
	
	private AcctPolicySMRelData acctPolicySMRelData;
	private Set<DriverInstanceData> additionalDrivers;
	
	
	private String prePlugins;
	private String postPlugins;
	
	private String proxyTranslationMapConfigId;
	private String proxyScript;
	private String driverScript;
	private String auditUId;

	// Constructors

	/** default constructor */
	public AcctPolicyInstData() {
	}



	// Property accessors

	
	
	public Long getAcctPolicyId() {
		return this.acctPolicyId;
	}

	public String getMultipleUserIdentity() {
		return multipleUserIdentity;
	}

	public void setMultipleUserIdentity(String multipleUserIdentity) {
		this.multipleUserIdentity = multipleUserIdentity;
	}



	public void setAcctPolicyId(Long acctPolicyId) {
		this.acctPolicyId = acctPolicyId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRuleSet() {
		return this.ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public String getCuiAttribute() {
		return cuiAttribute;
	}



	public void setCuiAttribute(String cuiAttribute) {
		this.cuiAttribute = cuiAttribute;
	}



	public String getResponseAttributes() {
		return responseAttributes;
	}



	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}



	public String getAcctMethod() {
		return acctMethod;
	}


	public Set getAdditionalDrivers() {
		return additionalDrivers;
	}



	public void setAdditionalDrivers(Set additionalDrivers) {
		this.additionalDrivers = additionalDrivers;
	}


	public void setAcctMethod(String acctMethod) {
		this.acctMethod = acctMethod;
	}



	public String getValidateAcctPacket() {
		return this.validateAcctPacket;
	}

	public void setValidateAcctPacket(String validateAcctPacket) {
		this.validateAcctPacket = validateAcctPacket;
	}

	public List<AcctPolicyMainDriverRelData> getMainDriverList() {
		return mainDriverList;
	}



	public void setMainDriverList(List<AcctPolicyMainDriverRelData> mainDriverList) {
		this.mainDriverList = mainDriverList;
	}




	public AcctPolicySMRelData getAcctPolicySMRelData() {
		return acctPolicySMRelData;
	}



	public void setAcctPolicySMRelData(AcctPolicySMRelData acctPolicySMRelData) {
		this.acctPolicySMRelData = acctPolicySMRelData;
	}



    
	public List<AcctPolicyExternalSystemRelData> getProxyServerRelList() {
		return proxyServerRelList;
	}



	public void setProxyServerRelList(
			List<AcctPolicyExternalSystemRelData> proxyServerRelList) {
		this.proxyServerRelList = proxyServerRelList;
	}



	public List<AcctPolicyExternalSystemRelData> getIpPoolServerRelList() {
		return ipPoolServerRelList;
	}



	public void setIpPoolServerRelList(
			List<AcctPolicyExternalSystemRelData> ipPoolServerRelList) {
		this.ipPoolServerRelList = ipPoolServerRelList;
	}



	public List<AcctPolicyExternalSystemRelData> getPrepaidServerRelList() {
		return prepaidServerRelList;
	}



	public void setPrepaidServerRelList(
			List<AcctPolicyExternalSystemRelData> prepaidServerRelList) {
		this.prepaidServerRelList = prepaidServerRelList;
	}



	public List<AcctPolicyExternalSystemRelData> getChargingGatewayServerRelList() {
		return chargingGatewayServerRelList;
	}



	public void setChargingGatewayServerRelList(
			List<AcctPolicyExternalSystemRelData> chargingGatewayServerRelList) {
		this.chargingGatewayServerRelList = chargingGatewayServerRelList;
	}

	public AcctPolicyRMParamsData getPrepaidRMParamsData() {
		return prepaidRMParamsData;
	}



	public void setPrepaidRMParamsData(AcctPolicyRMParamsData prepaidRMParamsData) {
		this.prepaidRMParamsData = prepaidRMParamsData;
	}



	public AcctPolicyRMParamsData getIpPoolRMParamsData() {
		return ipPoolRMParamsData;
	}



	public void setIpPoolRMParamsData(AcctPolicyRMParamsData ipPoolRMParamsData) {
		this.ipPoolRMParamsData = ipPoolRMParamsData;
	}



	public AcctPolicyRMParamsData getChargingGatewayRMParamsData() {
		return chargingGatewayRMParamsData;
	}



	public void setChargingGatewayRMParamsData(
			AcctPolicyRMParamsData chargingGatewayRMParamsData) {
		this.chargingGatewayRMParamsData = chargingGatewayRMParamsData;
	}



	public List<AcctPolicyBroadcastESIRelData> getBroadcastingServerRelList() {
		return broadcastingServerRelList;
	}



	public void setBroadcastingServerRelList(
			List<AcctPolicyBroadcastESIRelData> broadcastingServerRelList) {
		this.broadcastingServerRelList = broadcastingServerRelList;
	}



	public List<AcctPolicyAdditionalDriverRelData> getAdditionalDriverList() {
		return additionalDriverList;
	}


	public void setAdditionalDriverList(
			List<AcctPolicyAdditionalDriverRelData> additionalDriverList) {
		this.additionalDriverList = additionalDriverList;
	}
	public String getPrePlugins() {
		return prePlugins;
	}
	public void setPrePlugins(String prePlugins) {
		this.prePlugins = prePlugins;
	}
	public String getPostPlugins() {
		return postPlugins;
	}

	public void setPostPlugins(String postPlugins) {
		this.postPlugins = postPlugins;
	}
	
	public String getProxyTranslationMapConfigId() {
		return proxyTranslationMapConfigId;
	}
	public void setProxyTranslationMapConfigId(String proxyTranslationMapConfigId) {
		this.proxyTranslationMapConfigId = proxyTranslationMapConfigId;
	}
	public String getProxyScript() {
		return proxyScript;
	}
	public void setProxyScript(String proxyScript) {
		this.proxyScript = proxyScript;
	}



	public String getDriverScript() {
		return driverScript;
	}



	public void setDriverScript(String driverScript) {
		this.driverScript = driverScript;
	}

	
    @Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("Acct Policy Id :" + acctPolicyId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Status :" + status);
		writer.println("Order Number :" + orderNumber);
		writer.println("Rule Set :" + ruleSet);
		writer.println("Response Attributes :" + responseAttributes);
		writer.println("Cui Attribute :" + cuiAttribute);
		writer.println("Multiple User Identity :" + multipleUserIdentity);
		writer.println("Acct Method :" + acctMethod);
		writer.println("Validate Acct Packet :" + validateAcctPacket);
		writer.println("Additional Driver List :" + additionalDriverList);
		writer.println("Main Driver List :" + mainDriverList);
		writer.println("Prepaid RMParams Data :" + prepaidRMParamsData);
		writer.println("Ip Pool RMParams Data :" + ipPoolRMParamsData);
		writer.println("Charging Gateway RMParams Data :" + chargingGatewayRMParamsData);
		writer.println("Proxy Server RelList :" + proxyServerRelList);
		writer.println("Ip Pool Server RelList :" + ipPoolServerRelList);
		writer.println("Prepaid Server RelList :" + prepaidServerRelList);
		writer.println("Charging Gateway Server RelList :" + chargingGatewayServerRelList);
		writer.println("Broadcasting Server RelList :" + broadcastingServerRelList);
		writer.println("Acct Policy SMRelData :" + acctPolicySMRelData);
		writer.println("Additional Drivers :" + additionalDrivers);
		writer.println("Pre Plugins :" + prePlugins);
		writer.println("Post Plugins :" + postPlugins);
		writer.println("Proxy Translation Map Config Id :" + proxyTranslationMapConfigId);
		writer.println("Proxy Script :" + proxyScript);
		writer.println("Driver Script :" + driverScript);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}



	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Active", status);
		object.put("Description", description);
		object.put("Policy Selection Rule", ruleSet);
		object.put("Validate Packet", validateAcctPacket);
		object.put("User Identity Attribute", multipleUserIdentity);
		object.put("CUI Attribute", cuiAttribute);
//		object.put("Session Manager", )
		object.put("Response Attributes", responseAttributes);
		
		//Accounting Method
		object.put("Accounting", getAccountingMethod());

		//IP Pool Communication Parameters
		if(ipPoolRMParamsData!=null){
			object.put("IP Pool Communication Parameters", getIPPoolCommunicationParameters());
		}

		//Prepaid Communication Parameters 
		if(prepaidRMParamsData!=null){
			object.put("Prepaid Communication Parameters ", getPrepaidCommunicationParameters());
		}

		//Charging Gateway Communication Parameters 
		if(chargingGatewayRMParamsData!=null){
			object.put("Charging Gateway Communication Parameters ", getChargingGatewayCommunicationParameters());
		}
		
		//Additional Processing
		object.put("Additional Processing", getAdditionalProcessing());
		
		//Plugins
		object.put("Pre-plugins", prePlugins);
		object.put("Post-plugins", postPlugins);
		return object;
	}

	private Object getAccountingMethod() {
		JSONObject object = new JSONObject();
		object.put("Accounting Method", acctMethod);
		if(mainDriverList!=null){
			JSONArray array = new JSONArray();
			for (AcctPolicyMainDriverRelData element : mainDriverList) {
				if(element.getWeightage()!=null && element.getDriverData()!=null && element.getDriverData().getName()!=null){
					array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Main Driver Group", array);
		}
		object.put("Driver Script", driverScript);
		if(proxyServerRelList!=null){
			JSONArray array = new JSONArray();
			for (AcctPolicyExternalSystemRelData element : proxyServerRelList) {
				if(element.getWeightage()!=null && element.getExternalSystemData()!=null && element.getExternalSystemData().getName()!=null){
					array.add(element.getExternalSystemData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Proxy Server", array);
		}
		object.put("Translation Mapping", proxyTranslationMapConfigId);
		object.put("Script", proxyScript);
		return object;
	}
	
	private JSONObject getIPPoolCommunicationParameters(){
		JSONObject object = new JSONObject();
//		object.put("IP Pool Communication", ipPoolRMParamsData.ge);
		object.put("Accept On Timeout", ipPoolRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", ipPoolRMParamsData.getRuleSet());
		if(ipPoolServerRelList!=null){
			JSONArray array = new JSONArray();
			for (AcctPolicyExternalSystemRelData element : ipPoolServerRelList) {
				if(element.getWeightage() != null && element.getExternalSystemData()!= null && element.getExternalSystemData().getName()!=null){
					array.add(element.getExternalSystemData().getName() + "-W-" + element.getWeightage().intValue());
				}
			}
			object.put("IP Pool Server", array);
		}
		if(ipPoolRMParamsData.getTranslationMappingConfData()!=null && ipPoolRMParamsData.getTranslationMappingConfData().getName()!=null){
			object.put("Translation Mapping", ipPoolRMParamsData.getTranslationMappingConfData().getName());
		}
		object.put("Script", ipPoolRMParamsData.getScript());
		return object;
	}
	
	private JSONObject getPrepaidCommunicationParameters(){
		JSONObject object = new JSONObject();
//		object.put("Prepaid Communication", ipPoolRMParamsData.ge);
		object.put("Accept On Timeout()", prepaidRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", prepaidRMParamsData.getRuleSet());
		
		if(prepaidServerRelList!=null){
			JSONArray array = new JSONArray();
			for (AcctPolicyExternalSystemRelData element : prepaidServerRelList) {
				if(element.getWeightage() != null && element.getExternalSystemData()!= null && element.getExternalSystemData().getName()!=null){
					array.add(element.getExternalSystemData().getName() + "-W-" + element.getWeightage().intValue());
				}
			}
			object.put("Prepaid Server", array);
		}
		if(prepaidRMParamsData.getTranslationMappingConfData()!=null && prepaidRMParamsData.getTranslationMappingConfData().getName()!=null){
			object.put("Translation Mapping", prepaidRMParamsData.getTranslationMappingConfData().getName());
		}
		object.put("Script", prepaidRMParamsData.getScript());
		return object;
	}
	
	private JSONObject getChargingGatewayCommunicationParameters() {
		JSONObject object = new JSONObject();
//		object.put("Charging Gateway Communication", );
		object.put("Accept On Timeout", chargingGatewayRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", chargingGatewayRMParamsData.getRuleSet());
		if(chargingGatewayServerRelList!=null){
			JSONArray array = new JSONArray();
			for (AcctPolicyExternalSystemRelData element : chargingGatewayServerRelList) {
				if(element.getWeightage() != null && element.getExternalSystemData()!= null && element.getExternalSystemData().getName()!=null){
					array.add(element.getExternalSystemData().getName() + "-W-" + element.getWeightage().intValue());
				}
			}object.put("Charging Gateway Server", array);
		}
		if(chargingGatewayRMParamsData.getTranslationMappingConfData()!=null && chargingGatewayRMParamsData.getTranslationMappingConfData().getName()!=null){
			object.put("Translation Mapping", chargingGatewayRMParamsData.getTranslationMappingConfData().getName());
		}
		object.put("Script", chargingGatewayRMParamsData.getScript());
		return object;
	}
	
	private JSONObject getAdditionalProcessing(){
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		if(additionalDrivers!=null){
			for (DriverInstanceData element : additionalDrivers) {
				if(element.getName()!=null){
					array.add(element.getName());
				}
			}
			object.put("Additional Driver", array);
		}
		if(broadcastingServerRelList!=null){
			JSONObject fields = new JSONObject();
			for (AcctPolicyBroadcastESIRelData element : broadcastingServerRelList) {
				fields.putAll(element.toJson());
			}
			object.put("Broadcasting Server", fields);
		}
		return object;
	}



	public String getAuditUId() {
		return auditUId;
	}



	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}



	public String getAcctPolicyIdEsiTypeId() {
		return acctPolicyIdEsiTypeId;
	}



	public void setAcctPolicyIdEsiTypeId(String acctPolicyIdEsiTypeId) {
		this.acctPolicyIdEsiTypeId = acctPolicyIdEsiTypeId;
	}
	
}