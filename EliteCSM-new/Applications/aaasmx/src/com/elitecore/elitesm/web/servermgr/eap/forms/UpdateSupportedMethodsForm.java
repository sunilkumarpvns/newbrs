package com.elitecore.elitesm.web.servermgr.eap.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateSupportedMethodsForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String eapId;
	private String enabledAuthServiceMethod;
	private String action;
    
	private boolean tlsBool;
	private boolean ttlsBool;
	private boolean peapBool;
	private boolean simBool;
	private boolean akaBool;
	private boolean akaPrimeBool;
	private boolean md5Bool;
	private boolean mschapv2Bool;
	private boolean gtcBool;
    
	private long defaultNegiotationMethod;
	private String auditUId;

	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	public String getEnabledAuthServiceMethod() {
		return enabledAuthServiceMethod;
	}
	public void setEnabledAuthServiceMethod(String enabledAuthServiceMethod) {
		this.enabledAuthServiceMethod = enabledAuthServiceMethod;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isTlsBool() {
		return tlsBool;
	}
	public void setTlsBool(boolean tlsBool) {
		this.tlsBool = tlsBool;
	}
	public boolean isTtlsBool() {
		return ttlsBool;
	}
	public void setTtlsBool(boolean ttlsBool) {
		this.ttlsBool = ttlsBool;
	}
	public boolean isPeapBool() {
		return peapBool;
	}
	public void setPeapBool(boolean peapBool) {
		this.peapBool = peapBool;
	}
	public boolean isSimBool() {
		return simBool;
	}
	public void setSimBool(boolean simBool) {
		this.simBool = simBool;
	}
	public boolean isAkaBool() {
		return akaBool;
	}
	public void setAkaBool(boolean akaBool) {
		this.akaBool = akaBool;
	}
	public boolean isMd5Bool() {
		return md5Bool;
	}
	public void setMd5Bool(boolean md5Bool) {
		this.md5Bool = md5Bool;
	}
	public boolean isMschapv2Bool() {
		return mschapv2Bool;
	}
	public void setMschapv2Bool(boolean mschapv2Bool) {
		this.mschapv2Bool = mschapv2Bool;
	}
	public boolean isGtcBool() {
		return gtcBool;
	}
	public void setGtcBool(boolean gtcBool) {
		this.gtcBool = gtcBool;
	}
	public long getDefaultNegiotationMethod() {
		return defaultNegiotationMethod;
	}
	public void setDefaultNegiotationMethod(long defaultNegiotationMethod) {
		this.defaultNegiotationMethod = defaultNegiotationMethod;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public boolean isAkaPrimeBool() {
		return akaPrimeBool;
	}
	public void setAkaPrimeBool(boolean akaPrimeBool) {
		this.akaPrimeBool = akaPrimeBool;
	}	
	
    

}
