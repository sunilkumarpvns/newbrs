package com.elitecore.elitesm.web.digestconf.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDigestConfForm extends BaseWebForm {
	
	
	private static final long serialVersionUID = 1L;

	private long digestConfId;
	private String name;
	private String description;
	private String  realm="elitecore.com";              
	private String defaultQoP="auth";                 
	private String defaultAlgo="MD5";                
	private String opaque="elitecore";                     
	private String defaultNonce= "digest";               
	private int defaultNonceLength= 4;      
	private String draftAAASipEnable="true";
	
	
	
	
	
	
	public long getDigestConfId() {
		return digestConfId;
	}
	public void setDigestConfId(long digestConfId) {
		this.digestConfId = digestConfId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getDefaultQoP() {
		return defaultQoP;
	}
	public void setDefaultQoP(String defaultQoP) {
		this.defaultQoP = defaultQoP;
	}
	public String getDefaultAlgo() {
		return defaultAlgo;
	}
	public void setDefaultAlgo(String defaultAlgo) {
		this.defaultAlgo = defaultAlgo;
	}
	public String getOpaque() {
		return opaque;
	}
	public void setOpaque(String opaque) {
		this.opaque = opaque;
	}
	public String getDefaultNonce() {
		return defaultNonce;
	}
	public void setDefaultNonce(String defaultNonce) {
		this.defaultNonce = defaultNonce;
	}
	public int getDefaultNonceLength() {
		return defaultNonceLength;
	}
	public void setDefaultNonceLength(int defaultNonceLength) {
		this.defaultNonceLength = defaultNonceLength;
	}
	public String getDraftAAASipEnable() {
		return draftAAASipEnable;
	}
	public void setDraftAAASipEnable(String draftAAASipEnable) {
		this.draftAAASipEnable = draftAAASipEnable;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	
	
	
	

}
