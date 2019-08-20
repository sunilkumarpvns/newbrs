package com.elitecore.aaa.core.drivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.MAPGWAuthDriver.UlticomResponseListener;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.data.sim.PDPContext;


public class AuthGatewayProfileRequestor {
	private static final String MODULE = "MAPGW-NATIVE";
	private UlticomResponseListener responseListener;
	private Map<String,String> profileFieldMap;
	private int requestorID = 0;
	public static String VLR_CAMEL_SUB_INFO = "CamelSubscriptionInfo";
	public static String SERVICE_PROFILE = "ServiceProfile";
	private List<PDPContext> pdpContexts;
	
	public native int requestProfile(String identityString, long requestTimeout, int requestorID, int driverID);
	
	public AuthGatewayProfileRequestor(int id) {
		this.profileFieldMap = new HashMap<String,String>();
		this.requestorID = id;
		
		// Assuming there would not be more than 7 PDPContexts. 
		// Right now In Ulticom's MAPGateway.h file, maximum PDPContexts that could be handled is 7 i.e. ulcm_PDP_Context's array size
		// So when that capacity is increased/decreased, capacity in below list should also be increased/decreased. 
		this.pdpContexts = new ArrayList<PDPContext>(7);
	}
	
	/**
	 * native implementation will receive the Account Data,after receiving the Account Data native implementation will invoke OnSuccess() method.
	 */
	public void onSuccess(){
		responseListener.onSuccess(profileFieldMap, pdpContexts);
		close();
	}
	/**
	 * If native implementation will not receive any Account Data from Authgateway, then native implementation will invoke OnFailure() method with failure reason.
	 */			
	public void onFailure(String reason){
		responseListener.onFailure(reason);
	}
	
	/**
	 * If native implementation detects timeout from HLR, it will invoke onTimeout() method 
	 */			
	public void onTimeout(){
		responseListener.onTimeout();
	}
	
	public void registerListener(UlticomResponseListener listener){
		responseListener = listener;
	}
	
	public void deregisterListener(){
		responseListener = null;
	}
	public void close(){
		profileFieldMap = new HashMap<String,String>();
		pdpContexts = new ArrayList<PDPContext>(7);
		deregisterListener();
	}
	public void setMsisdn(String msisdn){
		profileFieldMap.put(AccountDataFieldMapping.MSISDN, msisdn);
	}
	
	public void setIMSI(String imsi){
		profileFieldMap.put(AccountDataFieldMapping.IMSI, imsi);
	}
	public void setVlrSubscriptionInfoPresent(String vlrSubscriptionInfoPresent){
		profileFieldMap.put(VLR_CAMEL_SUB_INFO, vlrSubscriptionInfoPresent);
	}
	public void setParam1(String param1){
		profileFieldMap.put(AccountDataFieldMapping.PARAM1, param1);
	}
	public void setParam2(String param2){
		profileFieldMap.put(AccountDataFieldMapping.PARAM2, param2);
	}
	public void setParam3(String param3){
		profileFieldMap.put(AccountDataFieldMapping.PARAM3, param3);
	}
	public void setParam4(String param4){
		profileFieldMap.put(AccountDataFieldMapping.PARAM4, param4);
	}
	public void setParam5(String param5){
		profileFieldMap.put(AccountDataFieldMapping.PARAM5, param5);
	}
	public void setServiceProfile(String serviceProfile){
		profileFieldMap.put(SERVICE_PROFILE, serviceProfile);
	}
	
	public void addPDPContext(PDPContext context) {
		pdpContexts.add(context);
	}
	
	public void setCustomerStatus(String status){
		if (status.equalsIgnoreCase("A")){
			profileFieldMap.put(AccountDataFieldMapping.CUSTOMER_STATUS, "A");
		} else {
			profileFieldMap.put(AccountDataFieldMapping.CUSTOMER_STATUS, "I");
		}
	}
	
	public void log(String strMessage){
		LogManager.getLogger().trace(MODULE, strMessage);
	}
	
	public boolean isFree(){
		if (responseListener==null)
			return true;
		return false;
	}
	public int getRequestorID(){
		return this.requestorID;
	}
	
}
