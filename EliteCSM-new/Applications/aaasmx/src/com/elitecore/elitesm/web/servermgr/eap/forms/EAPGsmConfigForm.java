package com.elitecore.elitesm.web.servermgr.eap.forms;

import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class EAPGsmConfigForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private String action;
	// EAP Sim Parameter
	private String simPseudonymGenMethod;
	private String simPseudonymHexenCoding;
	private String simPseudonymPrefix;
	private String simPseudonymRootNAI;
	private String simPseudonymAAAIndentityInRootNAI;
	private String simFastReauthGenMethod;
	private String simFastReauthHexenCoding;
	private String simFastReauthPrefix;
	private String simFastReauthRootNAI;
	private String simFastReauthAAAIndentityInRootNAI;
	
	//EAP AKA Parameter
	private String akaPseudonymGenMethod;
	private String akaPseudonymHexenCoding;
	private String akaPseudonymPrefix;
	private String akaPseudonymRootNAI;
	private String akaPseudonymAAAIndentityInRootNAI;
	private String akaFastReauthGenMethod;
	private String akaFastReauthHexenCoding;
	private String akaFastReauthPrefix;
	private String akaFastReauthRootNAI;
	private String akaFastReauthAAAIndentityInRootNAI;
	
	
	//EAP AKA' Parameter
	private String akaPrimePseudonymGenMethod;
	private String akaPrimePseudonymHexenCoding;
	private String akaPrimePseudonymPrefix;
	private String akaPrimePseudonymRootNAI;
	private String akaPrimePseudonymAAAIndentityInRootNAI;
	private String akaPrimeFastReauthGenMethod;
	private String akaPrimeFastReauthHexenCoding;
	private String akaPrimeFastReauthPrefix;
	private String akaPrimeFastReauthRootNAI;
	private String akaPrimeFastReauthAAAIndentityInRootNAI;
	
	// For View Form
	private String eapId;
	
	// For Update Functionality 
	private String simConfigId;
	private String akaConfigId;
	private String akaPrimeConfigId;
	private String auditUId;
	private String name;
	
	private Map<String,String> defaultGenMethod;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSimPseudonymGenMethod() {
		return simPseudonymGenMethod;
	}
	public void setSimPseudonymGenMethod(String simPseudonymGenMethod) {
		this.simPseudonymGenMethod = simPseudonymGenMethod;
	}
	public String getSimPseudonymHexenCoding() {
		return simPseudonymHexenCoding;
	}
	public void setSimPseudonymHexenCoding(String simPseudonymHexenCoding) {
		this.simPseudonymHexenCoding = simPseudonymHexenCoding;
	}
	public String getSimPseudonymPrefix() {
		return simPseudonymPrefix;
	}
	public void setSimPseudonymPrefix(String simPseudonymPrefix) {
		this.simPseudonymPrefix = simPseudonymPrefix;
	}
	public String getSimFastReauthGenMethod() {
		return simFastReauthGenMethod;
	}
	public void setSimFastReauthGenMethod(String simFastReauthGenMethod) {
		this.simFastReauthGenMethod = simFastReauthGenMethod;
	}
	public String getSimFastReauthHexenCoding() {
		return simFastReauthHexenCoding;
	}
	public void setSimFastReauthHexenCoding(String simFastReauthHexenCoding) {
		this.simFastReauthHexenCoding = simFastReauthHexenCoding;
	}
	public String getSimFastReauthPrefix() {
		return simFastReauthPrefix;
	}
	public void setSimFastReauthPrefix(String simFastReauthPrefix) {
		this.simFastReauthPrefix = simFastReauthPrefix;
	}
	public String getAkaPseudonymGenMethod() {
		return akaPseudonymGenMethod;
	}
	public void setAkaPseudonymGenMethod(String akaPseudonymGenMethod) {
		this.akaPseudonymGenMethod = akaPseudonymGenMethod;
	}
	public String getAkaPseudonymHexenCoding() {
		return akaPseudonymHexenCoding;
	}
	public void setAkaPseudonymHexenCoding(String akaPseudonymHexenCoding) {
		this.akaPseudonymHexenCoding = akaPseudonymHexenCoding;
	}
	public String getAkaPseudonymPrefix() {
		return akaPseudonymPrefix;
	}
	public void setAkaPseudonymPrefix(String akaPseudonymPrefix) {
		this.akaPseudonymPrefix = akaPseudonymPrefix;
	}
	public String getAkaFastReauthGenMethod() {
		return akaFastReauthGenMethod;
	}
	public void setAkaFastReauthGenMethod(String akaFastReauthGenMethod) {
		this.akaFastReauthGenMethod = akaFastReauthGenMethod;
	}
	public String getAkaFastReauthHexenCoding() {
		return akaFastReauthHexenCoding;
	}
	public void setAkaFastReauthHexenCoding(String akaFastReauthHexenCoding) {
		this.akaFastReauthHexenCoding = akaFastReauthHexenCoding;
	}
	public String getAkaFastReauthPrefix() {
		return akaFastReauthPrefix;
	}
	public void setAkaFastReauthPrefix(String akaFastReauthPrefix) {
		this.akaFastReauthPrefix = akaFastReauthPrefix;
	}
	public Map<String, String> getDefaultGenMethod() {
		return defaultGenMethod;
	}
	public void setDefaultGenMethod(Map<String, String> defaultGenMethod) {
		this.defaultGenMethod = defaultGenMethod;
	}
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	public String getSimConfigId() {
		return simConfigId;
	}
	public void setSimConfigId(String simConfigId) {
		this.simConfigId = simConfigId;
	}
	public String getAkaConfigId() {
		return akaConfigId;
	}
	public void setAkaConfigId(String akaConfigId) {
		this.akaConfigId = akaConfigId;
	}
	
	public void setDefaultGenMethodMap(){
		Map<String,String> defaultGenMethodMap = new LinkedHashMap<String, String>();
		defaultGenMethodMap.put("BASE16", "Base16");
		defaultGenMethodMap.put("BASE32", "Base32");
		defaultGenMethodMap.put("BASE64", "Base64");
		defaultGenMethodMap.put("ELITECRYPT", "ELITECRYPT");
		defaultGenMethodMap.put("BASIC_ALPHA_1", "BASIC ALPHA 1");
		setDefaultGenMethod(defaultGenMethodMap);
	}
	
	public void setDefaultValueToForm(){
		setSimPseudonymPrefix("1999");
		setSimFastReauthPrefix("1888");
		setSimPseudonymGenMethod("BASE32");
		setSimFastReauthGenMethod("BASE32");
		setSimPseudonymRootNAI("DISABLE");
		setSimFastReauthRootNAI("DISABLE");
		
		
		
		setAkaPseudonymPrefix("0999");
		setAkaFastReauthPrefix("0888");
		setAkaPseudonymGenMethod("BASE32");
		setAkaFastReauthGenMethod("BASE32");
		setAkaPseudonymRootNAI("DISABLE");
		setAkaFastReauthRootNAI("DISABLE");
		
		
		setAkaPrimePseudonymPrefix("6999"); 
		setAkaPrimeFastReauthPrefix("6888"); 
		setAkaPrimePseudonymGenMethod("BASE32");
		setAkaPrimeFastReauthGenMethod("BASE32");
		setAkaPrimePseudonymRootNAI("DISABLE");
		setAkaPrimeFastReauthRootNAI("DISABLE");

		setDefaultGenMethodMap();
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAkaPrimePseudonymGenMethod() {
		return akaPrimePseudonymGenMethod;
	}
	public void setAkaPrimePseudonymGenMethod(String akaPrimePseudonymGenMethod) {
		this.akaPrimePseudonymGenMethod = akaPrimePseudonymGenMethod;
	}
	public String getAkaPrimePseudonymHexenCoding() {
		return akaPrimePseudonymHexenCoding;
	}
	public void setAkaPrimePseudonymHexenCoding(String akaPrimePseudonymHexenCoding) {
		this.akaPrimePseudonymHexenCoding = akaPrimePseudonymHexenCoding;
	}
	public String getAkaPrimePseudonymPrefix() {
		return akaPrimePseudonymPrefix;
	}
	public void setAkaPrimePseudonymPrefix(String akaPrimePseudonymPrefix) {
		this.akaPrimePseudonymPrefix = akaPrimePseudonymPrefix;
	}
	public String getAkaPrimeFastReauthGenMethod() {
		return akaPrimeFastReauthGenMethod;
	}
	public void setAkaPrimeFastReauthGenMethod(String akaPrimeFastReauthGenMethod) {
		this.akaPrimeFastReauthGenMethod = akaPrimeFastReauthGenMethod;
	}
	public String getAkaPrimeFastReauthHexenCoding() {
		return akaPrimeFastReauthHexenCoding;
	}
	public void setAkaPrimeFastReauthHexenCoding(
			String akaPrimeFastReauthHexenCoding) {
		this.akaPrimeFastReauthHexenCoding = akaPrimeFastReauthHexenCoding;
	}
	public String getAkaPrimeFastReauthPrefix() {
		return akaPrimeFastReauthPrefix;
	}
	public void setAkaPrimeFastReauthPrefix(String akaPrimeFastReauthPrefix) {
		this.akaPrimeFastReauthPrefix = akaPrimeFastReauthPrefix;
	}
	public String getAkaPrimeConfigId() {
		return akaPrimeConfigId;
	}
	public void setAkaPrimeConfigId(String akaPrimeConfigId) {
		this.akaPrimeConfigId = akaPrimeConfigId;
	}
	
	//Introducing new fields
	public String getSimFastReauthRootNAI() {
		return simFastReauthRootNAI;
	}
	public void setSimFastReauthRootNAI(String simFastReauthRootNAI) {
		this.simFastReauthRootNAI = simFastReauthRootNAI;
	}
	public String getSimFastReauthAAAIndentityInRootNAI() {
		return simFastReauthAAAIndentityInRootNAI;
	}
	public void setSimFastReauthAAAIndentityInRootNAI(
			String simFastReauthAAAIndentityInRootNAI) {
		this.simFastReauthAAAIndentityInRootNAI = simFastReauthAAAIndentityInRootNAI;
	}
	public String getAkaPseudonymRootNAI() {
		return akaPseudonymRootNAI;
	}
	public void setAkaPseudonymRootNAI(String akaPseudonymRootNAI) {
		this.akaPseudonymRootNAI = akaPseudonymRootNAI;
	}
	public String getAkaPseudonymAAAIndentityInRootNAI() {
		return akaPseudonymAAAIndentityInRootNAI;
	}
	public void setAkaPseudonymAAAIndentityInRootNAI(
			String akaPseudonymAAAIndentityInRootNAI) {
		this.akaPseudonymAAAIndentityInRootNAI = akaPseudonymAAAIndentityInRootNAI;
	}
	public String getAkaFastReauthRootNAI() {
		return akaFastReauthRootNAI;
	}
	public void setAkaFastReauthRootNAI(String akaFastReauthRootNAI) {
		this.akaFastReauthRootNAI = akaFastReauthRootNAI;
	}
	public String getAkaFastReauthAAAIndentityInRootNAI() {
		return akaFastReauthAAAIndentityInRootNAI;
	}
	public void setAkaFastReauthAAAIndentityInRootNAI(
			String akaFastReauthAAAIndentityInRootNAI) {
		this.akaFastReauthAAAIndentityInRootNAI = akaFastReauthAAAIndentityInRootNAI;
	}
	public String getAkaPrimePseudonymRootNAI() {
		return akaPrimePseudonymRootNAI;
	}
	public void setAkaPrimePseudonymRootNAI(String akaPrimePseudonymRootNAI) {
		this.akaPrimePseudonymRootNAI = akaPrimePseudonymRootNAI;
	}
	public String getAkaPrimePseudonymAAAIndentityInRootNAI() {
		return akaPrimePseudonymAAAIndentityInRootNAI;
	}
	public void setAkaPrimePseudonymAAAIndentityInRootNAI(
			String akaPrimePseudonymAAAIndentityInRootNAI) {
		this.akaPrimePseudonymAAAIndentityInRootNAI = akaPrimePseudonymAAAIndentityInRootNAI;
	}
	public String getAkaPrimeFastReauthRootNAI() {
		return akaPrimeFastReauthRootNAI;
	}
	public void setAkaPrimeFastReauthRootNAI(String akaPrimeFastReauthRootNAI) {
		this.akaPrimeFastReauthRootNAI = akaPrimeFastReauthRootNAI;
	}
	public String getAkaPrimeFastReauthAAAIndentityInRootNAI() {
		return akaPrimeFastReauthAAAIndentityInRootNAI;
	}
	public void setAkaPrimeFastReauthAAAIndentityInRootNAI(
			String akaPrimeFastReauthAAAIndentityInRootNAI) {
		this.akaPrimeFastReauthAAAIndentityInRootNAI = akaPrimeFastReauthAAAIndentityInRootNAI;
	}
	public String getSimPseudonymRootNAI() {
		return simPseudonymRootNAI;
	}
	public void setSimPseudonymRootNAI(String simPseudonymRootNAI) {
		this.simPseudonymRootNAI = simPseudonymRootNAI;
	}
	public String getSimPseudonymAAAIndentityInRootNAI() {
		return simPseudonymAAAIndentityInRootNAI;
	}
	public void setSimPseudonymAAAIndentityInRootNAI(
			String simPseudonymAAAIndentityInRootNAI) {
		this.simPseudonymAAAIndentityInRootNAI = simPseudonymAAAIndentityInRootNAI;
	}
	
}
