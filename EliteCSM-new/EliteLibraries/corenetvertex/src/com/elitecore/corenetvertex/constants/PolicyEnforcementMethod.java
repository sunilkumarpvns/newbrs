package com.elitecore.corenetvertex.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PolicyEnforcementMethod {

	COA("COA", true, false, false, false),
	ACCESS_ACCEPT("Access-Accept",true, false, false, false),

	RAR("RAR" , false, false, false , true),
	ASR("ASR", false, false, false, true),

	Cisco_SCE_API("Cisco SCE API" , true, false, false, false),
	
	Cisco_SCE("Cisco SCE" , false, true, false, false),
	STANDARD("Standard" , false, true, false, false),
	
	NONE("None" , true, false, false, false);

	private String val;
	private boolean isRadiusMethod;
	private boolean isDiameterMethod;
	private boolean isCISCOMethod;
	private boolean isDiameterSCEMethod;

	private static final Map<String , PolicyEnforcementMethod> objectMap;
	private static final ArrayList<PolicyEnforcementMethod> radiusMethods;
	private static final ArrayList<PolicyEnforcementMethod> diameterMethods;
	private static final ArrayList<PolicyEnforcementMethod> ciscoSCEMethods;
	private static final ArrayList<PolicyEnforcementMethod> diameterSCEMethods;

	static {
		objectMap = new HashMap<String,PolicyEnforcementMethod>(8,1);
		radiusMethods = new ArrayList<PolicyEnforcementMethod>();
		diameterMethods = new ArrayList<PolicyEnforcementMethod>();
		ciscoSCEMethods = new ArrayList<PolicyEnforcementMethod>();
		diameterSCEMethods = new ArrayList<PolicyEnforcementMethod>();

		for (PolicyEnforcementMethod method : values()){

			objectMap.put(method.val, method);

			if(method.isRadiusMethod){
				radiusMethods.add(method);
			}

			if(method.isDiameterMethod){
				diameterMethods.add(method);
			}

			if(method.isCISCOMethod){
				ciscoSCEMethods.add(method);
			}
			
			if(method.isDiameterSCEMethod){
				diameterSCEMethods.add(method);
			}
		}
		radiusMethods.trimToSize();
		diameterMethods.trimToSize();
		ciscoSCEMethods.trimToSize();
		diameterSCEMethods.trimToSize();
	}

	public static PolicyEnforcementMethod fromName(String method){
		return objectMap.get(method);
	}

	public static List<PolicyEnforcementMethod> getEnforcementMethods(GatewayTypeConstant gateway){
		switch(gateway){
		case RADIUS :
			return radiusMethods;
		case DIAMETER :
			return diameterMethods;
		case CISCOSCE : 
			return ciscoSCEMethods;
	}
		return null;
	}

	private PolicyEnforcementMethod(String val,boolean radius,boolean diameter,boolean ciscoSCE,boolean DiameterSCEMethod){
		this.isRadiusMethod = radius;
		this.isDiameterMethod = diameter;
		this.isCISCOMethod = ciscoSCE;
		this.isDiameterSCEMethod = DiameterSCEMethod;
		this.val = val;
	}

	public String getVal(){
		return val;
	}

	public boolean isRadiusMethod() {
		return isRadiusMethod;
	}

	public boolean isDiameterMethod() {
		return isDiameterMethod;
	}

	public boolean isCISCOMethod() {
		return isCISCOMethod;
	}

	public boolean isDiameterSCEMethod() {
		return isDiameterSCEMethod;
	}

	public static ArrayList<PolicyEnforcementMethod> getRadiusMethods() {
		return radiusMethods;
	}

	public static ArrayList<PolicyEnforcementMethod> getDiameterMethods() {
		return diameterMethods;
	}

	public static ArrayList<PolicyEnforcementMethod> getCiscosceMethods() {
		return ciscoSCEMethods;
	}

	public static ArrayList<PolicyEnforcementMethod> getDiameterSCEMethods() {
		return diameterSCEMethods;
	}
}