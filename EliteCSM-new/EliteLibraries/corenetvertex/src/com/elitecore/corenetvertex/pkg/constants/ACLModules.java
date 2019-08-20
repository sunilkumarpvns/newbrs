package com.elitecore.corenetvertex.pkg.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public enum ACLModules {

	PRODUCTOFFER("Product Offer" ,null,Component.PD,"productoffer/product-offer",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    DATAPKG	("DataPackage",	null,Component.PD,"Pkg",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
    QUOTAPROFILE("QuotaProfile",DATAPKG,Component.PD,"QuotaProfile;SyQuotaProfile;RncProfile",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    QOSPROFILE("QosProfile",DATAPKG,Component.PD,"QosProfile;TopupQosProfile;TopupQosMultiplier;",ACLAction.CREATE,ACLAction.DELETE,ACLAction.MANAGEORDER,ACLAction.UPDATE),
    PCCRULE	 ("PCCRule",QOSPROFILE,Component.PD,"PccRule",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    NOTIFICATION("Notification", DATAPKG,Component.PD,"UsageNotification",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    
    NOTIFICATIONTEMPLATE("NotificationTemplate", null ,Component.PD,"NotificationTemplate",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    
    SUBSCRIBER	("SubscriberProfileRepository",null,Component.PD,null,ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE,ACLAction.CREATE_SUBSCRIBER,ACLAction.VIEW_SUBSCRIBER,
    		ACLAction.DELETE_SUBSCRIBER,ACLAction.UPDATE_SUBSCRIBER,ACLAction.PURGE_SUBSCRIBER,
    		ACLAction.RESTORE_SUBSCRIBER, ACLAction.SUBSCRIBE_ADDON, ACLAction.UNSUBSCRIBE_ADDON,
    		ACLAction.VIEW_DELETED_SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION),

	SUBSCRIBERSESSION("SubscriberSession", null, Component.PD,"SubscriberSession", ACLAction.REAUTH, ACLAction.DELETE, ACLAction.DISCONNECT, ACLAction.SEARCH),

    IMSPKG("IMSPackage",null,Component.PD,"IMSPkg",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
    IMSPKGSERVICE("IMSPackageService",IMSPKG,Component.PD,"IMSPkgService",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    /**Service Type ACL configurations
	 */
	//SERVICETYPE("ServiceType",	null,"ServiceType",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
	GLOBALPCCRULE("GlobalPccRule",	null,Component.PD,"GlobalPccRule",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
	GLOBALRATECARD("Global Monetary Rate Card",	null,Component.PD,"globalratecard/global-rate-card",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	EMERGENCYPKG("EmergencyPackage",null,Component.PD,"EmergencyPkg",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.MANAGEORDER,ACLAction.UPDATE),
	EMERGENCYQOSPROFILE("EmergencyQosProfile",EMERGENCYPKG,Component.PD,"EmergencyPkgQos",ACLAction.CREATE,ACLAction.DELETE,ACLAction.MANAGEORDER,ACLAction.UPDATE),
	EMERGENCYPCCRULE("EmergencyPCCRule",EMERGENCYQOSPROFILE,Component.PD,"EmergencyPccRule",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	RATINGGROUP("RatingGroup",	null,Component.PD,"ratinggroup/rating-group",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
	DATASERVICETYPE("DataServiceType",	null,Component.PD,"dataservicetype/data-service-type",ACLAction.CREATE,ACLAction.DELETE,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),

	PROMOTIONALPKG("PromotionalPackage",null,Component.PD,"PromotionalPkg",ACLAction.CREATE,ACLAction.DELETE,ACLAction.MANAGEORDER,ACLAction.IMPORT,ACLAction.EXPORT,ACLAction.UPDATE),
	PROMOTIONALQUOTAPROFILE("PromotionalQuotaProfile",PROMOTIONALPKG,Component.PD,"PromotionalQuotaProfile;PromotionalSyQuotaProfile",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	PROMOTIONALQOSPROFILE("PromotionalQosProfile",PROMOTIONALPKG,Component.PD,"PromotionalQosProfile",ACLAction.CREATE,ACLAction.DELETE,ACLAction.MANAGEORDER,ACLAction.UPDATE),
	PROMOTIONALNOTIFICATION("PromotionalNotification", PROMOTIONALPKG,Component.PD,"PromotionalUsageNotification",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	PROMOTIONALPCCRULE("PromotionalPCCRule",PROMOTIONALQOSPROFILE,Component.PD,"PromotionalPCCRule",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),

	DATATOPUP("Data Top-Up" ,null,Component.PD,"datatopup/data-topup",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	BODPACKAGE("BoD Package" ,null,Component.PD,"bodpackage/bod-package",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	BODQOSMULTIPLIER("BoD QoS Multiplier" ,BODPACKAGE,Component.PD,"bodqosmultiplier/bod-qos-multiplier",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	TOPUPNOTIFICATION("Top-Up Notification" ,DATATOPUP,Component.PD,"topupnotification/topup-notification",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	MONETARYRECHARGEPLAN("Monetary Recharge Plan",null,Component.PD,"monetaryrechargeplan/monetary-recharge-plan",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),

	CHARGINGRULEBASENAME("ChargingRuleBaseName",null,Component.PD,"ChargingRuleBaseName",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE,ACLAction.IMPORT,ACLAction.EXPORT),
	//SM Modules
	GROUP("Group",null,Component.SM,"group/group",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	STAFF("Staff",null,Component.SM,"staff/staff",ACLAction.CREATE,ACLAction.UPDATE),
	ROLE("Role",null,Component.SM ,"role/role" ,ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	DEVICE("Device",null,Component.SM,"device/device",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	DATABASE("Database Datasource",null,Component.SM,"database/database",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	LDAP("LDAP Datasource",null,Component.SM,"ldap/ldap",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PASSWORDPOLICYCONFIG("Password Policy Configuration",null,Component.SM,"passwordpolicyconfig/password-policy-config",ACLAction.UPDATE),
	GATEWAY("Gateway",null,Component.SM,"gateway/diameter-gateway;gateway/radius-gateway",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	FILELOCATION("File Location",null,Component.SM,"filelocation/file-location",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	FILEMAPPING("File Mapping",null,Component.SM,"filemapping/file-mapping",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PREFIX("Prefix",null,Component.SM,"prefix/prefix",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	LRN("LRN", null, Component.SM, "lrn/lrn", ACLAction.CREATE, ACLAction.DELETE, ACLAction.UPDATE),
	SERVERINSTANCE("ServerInstance",null,Component.SM,"serverinstance/server-instance",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SESSIONCONFIGURATION("Session Configuration",null,Component.SM,"sessionconfiguration/session-configuration",ACLAction.UPDATE),
	SLICECONFIGURATION("Slice Configuration",null,Component.PD,"sliceconfig/slice-config",ACLAction.UPDATE),
	GATEWAYPROFILE("Gateway Profile",null,Component.SM,"gatewayprofile/diameter-gateway-profile;gatewayprofile/radius-gateway-profile",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),

	SERVER_GROUP("Server Group",null,Component.SM,"servergroup/server-group",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SERVER_INSTANCE("Server Instance",null,Component.SM,"serverinstance/server-instance",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SERVER_INSTANCE_INTEGRATION("Server Instance Integration",null,Component.SM,"serverinstanceregistration/server-instance-registration",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SERVER_INSTANCE_LIVE_DETAIL("Server Instance Live Detail",null,Component.SM,"serverinstancelivedetail/server-instance-live-detail"),
	SERVER_PROFILE("ServerProfile",null,Component.SM,"serverprofile/server-profile",ACLAction.UPDATE),
	NOTIFICATIONAGENTS("Notification Agents",null,Component.SM,"notificationagents/email-agent;notificationagents/sms-agent",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	NETWORK("Network",null,Component.SM,"network/network",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	MCCMNCGROUP("MCC-MNC Group",null,Component.SM,"mccmncgroup/mcc-mnc-group",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
	ROUTINGTABLE("RoutingTable",null,Component.SM,"routingtable/routing-table",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),

	SPINTERFACE("Sp Interface",null,Component.SM,"spinterface/db-sp-interface;spinterface/ldap-sp-interface",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SPR("SPR",null,Component.SM,"spr/spr",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	DDF("DDF Configuration",null,Component.SM,"ddf/ddf",ACLAction.UPDATE),
	LICENSE("License",null,Component.SM,"license/license",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	DRIVER("Driver",null,Component.SM,"driver/csv-driver;driver/db-cdr-driver",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	ALERT("ALERT Configuration",null,Component.SM,"alert/file-alert;alert/trap-alert",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
    SYSTEM_PARAMETER("SystemParameter",null,Component.SM,"systemparameter/system-parameter;systemparameter/offline-system-parameter",ACLAction.UPDATE),
	REGION("Region",null,Component.SM,"region/region",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	CITY("City",null,Component.SM,"city/city",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	AREA("Area",null,Component.SM,"area/area",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PACKETMAPPING("Packet Mapping",null,Component.SM,"packetmapping/diameter-packet-mapping;packetmapping/radius-packet-mapping",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PCCRULEMAPPING("PCCRule Mapping",null,Component.SM,"pccrulemapping/diameter-pcc-rule-mapping;pccrulemapping/radius-pcc-rule-mapping",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PCC_SERVICE_POLICY("PCC Service Policy",null, Component.SM, "pccservicepolicy/pcc-service-policy",ACLAction.CREATE, ACLAction.UPDATE, ACLAction.DELETE),
	LDAP_AUTH_CONFIGURATION("LDAP Auth Configuration",null,Component.SM,"ldapauthconf/ldap-auth-configuration",ACLAction.UPDATE),
	GEOGRAPHY("Geography",null,Component.SM,"geography/geography",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),

	//Partner RnC Modules
	LOB("Lob",null,Component.PD,"lob/lob",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	SERVICE("Service",null,Component.PD,"service/service",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	REVENUE_DETAIL("Revenue Detail",null,Component.PD,"revenuedetail/revenue-detail",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	CURRENCY("Currency", null, Component.PD, "currency/currency", ACLAction.CREATE, ACLAction.UPDATE, ACLAction.DELETE),
	OFFLINE_RNC("Offline RnC",null,Component.SM,"serverprofile/offline-rnc",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PARTNERGROUP("Partner Group",null,Component.PD,"partnergroup/partner-group",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	CALENDER("Calendar",null,Component.PD,"calender/calender",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PARTNER("Partner",null,Component.PD,"partner/partner",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	ACCOUNT("Account",null,Component.PD,"account/account",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	PREFIXES("Prefixes",null,Component.PD,"prefixes/prefixes",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	GUIDING("Guiding",null,Component.PD,"guiding/guiding",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	RNCPACKAGE("RnC Package",null,Component.PD,"rncpackage/rnc-package",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	RATECARDGROUP("Rate Card Group",RNCPACKAGE,Component.PD,"ratecardgroup/rate-card-group",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	RATECARD("Rate Card",null,Component.PD,"ratecard/rate-card",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	MONETARYRATECARD("Monetary Rate Card",RNCPACKAGE,Component.PD,"monetaryratecard/monetary-rate-card",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	RNCNOTIFICATION("RnC Notification",RNCPACKAGE,Component.PD,"rncnotification/rnc-notification",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE),
	RNCPRODUCTSPEC("Product Specification" ,null,Component.PD,"rncproductspec/rnc-product-spec",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE,ACLAction.IMPORT,ACLAction.EXPORT),
	DATARATECARD("Data Rate Card",DATAPKG,Component.PD,"dataratecard/data-rate-card",ACLAction.CREATE,ACLAction.DELETE,ACLAction.UPDATE),
    NONMONETARYRATECARD("NonMonetary Rate Card",RNCPACKAGE,Component.PD,"nonmonetaryratecard/non-monetary-rate-card",ACLAction.CREATE,ACLAction.UPDATE,ACLAction.DELETE);
    
	
    private final String displayLabel;
	private final ACLAction[] actions;
	private final ACLModules parentModule;
	private final String[] actionURLs;
	private final Component component;
	
	protected static  final Map<ACLModules,List<ACLModules>> moduleActionMap = new ConcurrentSkipListMap<>();
	protected static final Map<String,ACLModules> moduleMap = new HashMap<>();

	private static final List<ACLModules> rootModules = Collectionz.newArrayList();
	private static final Map<String,ACLModules> actionURLMap= Maps.newHashMap();
	static {
		
		regenrateMap();
		generateList();
	}
	private ACLModules(String label,ACLModules parentModule,Component component,String actionURL,ACLAction... actions){
		this.displayLabel = label;
		this.actions = actions;
		this.parentModule = parentModule;
		this.component = component;
		if(Strings.isNullOrBlank(actionURL) == false){
			this.actionURLs= actionURL.split(";");
		}else{
			this.actionURLs = new String[1];
		}
	}
	private static void regenrateMap() {
		for (ACLModules obj : values()) {
			getKeyValuePairs(obj);
			moduleMap.put(obj.name(),obj);
			for(String actionURL : obj.actionURLs){
				actionURLMap.put(actionURL, obj);
			}
			
		}
		
	}
	
	private static void generateList() {
		for (ACLModules obj : values()) {
			if(obj.parentModule==null){
				rootModules.add(obj);
			}
		} 
	}
	/**
	 * This method is used to define map that contains the Module Name as key and its list of children as value.
	 * @param module define the ACLModule
	 */
	private static void getKeyValuePairs(ACLModules module) {
			if(module.parentModule == null){
				moduleActionMap.put(module, new ArrayList<ACLModules>());
			}else{
				
				if(moduleActionMap.containsKey(module.parentModule)){
						moduleActionMap.put(module, new ArrayList<ACLModules>());
						moduleActionMap.get(module.parentModule).add(module);
					
				}
			}
		
	}
    	

    /**
     * This method will generate json array that contains parent module wise json string/data.
     * The json data defines the parent-child relationship of the Modules and its actions
     * @return
     */
	public static JsonArray getJsonDataFromMap(Component component){
		regenrateMap();
		JsonArray finalArray = new JsonArray();
		for (Map.Entry<ACLModules, List<ACLModules>> entry : moduleActionMap.entrySet()) {
			if((component == null || entry.getKey().component.equals(component)) && moduleActionMap.containsKey(entry.getKey())) {
					JsonArray jsonData = getJSONData(entry.getKey());
					finalArray.add(jsonData);
			}

		}
		return finalArray;
	}
	public static JsonArray getJsonDataFromMap(){
		return getJsonDataFromMap(null);
	}
    /**
     * This method is used to maintain parent-child relationship between module and sub modules and define relational data as Json.
     * @param key
     * @return
     */
	public static JsonArray getJSONData(ACLModules key) {
		JsonArray parentJsonArray = new JsonArray();
			JsonArray childJsonArray = new JsonArray();
			setActionsToJSONArray(key, childJsonArray);
			JsonObject parentData = new JsonObject();
			parentData.add(key.displayLabel, childJsonArray);
			parentJsonArray.add(parentData);
		return parentJsonArray;
	}
	/**
	 * This method will iterate list of actions specified with the ModuleAction enum and convert them to the JsonObject.
	 * @param key defines the Module Name
	 * @param jsonArray defines the Module wise actions as Json String
	 */
    private static void setActionsToJSONArray(ACLModules key,JsonArray jsonArray) {
		ACLAction[] actions = key.actions;
		if(actions.length > 0){
			for (int i = 0; i < actions.length; i++) {
				JsonObject obj = new JsonObject();
				if(actions[i].getVal().equalsIgnoreCase(ACLAction.UPDATE.getVal())){
					setJsonPropertyForUpdate(key, actions, i, obj);
					
				}else{
					obj.addProperty(actions[i].name(), actions[i].getVal());
				}
				jsonArray.add(obj);
			}
		}
		
	}

	private static void setJsonPropertyForUpdate(ACLModules key, ACLAction[] actions, int i, JsonObject obj) {
		JsonArray updateArray = new JsonArray();
		List<ACLModules> childrenList = moduleActionMap.get(key);
		if (Collectionz.isNullOrEmpty(childrenList) == false) {
            JsonObject updateDetail = new JsonObject();
            updateDetail.addProperty(ACLAction.UPDATE_BASIC_DETAIL.name(), ACLAction.UPDATE_BASIC_DETAIL.getVal());
            updateArray.add(updateDetail);
            for (ACLModules modules : childrenList) {
                if (moduleActionMap.containsKey(modules)) {
                    updateArray.add(getJSONData(modules));
                    moduleActionMap.remove(modules);
                }
            }
            obj.add(actions[i].getVal(), updateArray);
        }else{
            obj.addProperty(actions[i].name(), actions[i].getVal());
        }
	}

	public String getDisplayLabel() {
        return displayLabel;
    }

    public ACLAction[] getActions(){
	return actions;
    }
    
    
    
    public ACLModules getParentModule(){
    	return parentModule;
    }

	public static List<ACLModules> getModuleActionByName(String moduleName) {
		return moduleActionMap.get(moduleName);
	}
	
	public static ACLModules getModuleByName(String moduleName) {
		return moduleMap.get(moduleName);
	}
	
	public static ACLModules fromVal(String moduleName){
		for(ACLModules modules : values()){
			if(modules.getDisplayLabel().equalsIgnoreCase(moduleName)){
			return modules;
			}
		}
		return null;
	}
	
	public static ACLModules fromModuleName(String actionUrl){
		return actionURLMap.get(actionUrl);
	}
	
	public static String getVal(String module){
		for(ACLModules modules : values()){
			if(modules.name().equalsIgnoreCase(module)){
			return modules.getDisplayLabel();
			}
		}
		return null;
	}
	public static List<ACLModules> getRootmodules() {
		return rootModules;
	}
	public String[] getActionURL() {
		return actionURLs;
	}

	public Component getComponent() {
		return component;
	}
}

