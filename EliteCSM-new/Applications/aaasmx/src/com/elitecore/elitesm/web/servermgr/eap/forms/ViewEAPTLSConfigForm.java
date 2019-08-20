/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscDigestconfForm.java                 		
 * ModualName digestconf    			      		
 * Created on 7 January, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.eap.forms;

import java.sql.Timestamp;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewEAPTLSConfigForm extends BaseWebForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long eapId;
	private String name;
	private String description;
	private long defaultNegiotationMethod=4;
	private String treatInvalidPacketAsFatal="true";
	private String notificationSuccess="false";
	private String notificationFailure="false";
	private long maxEapPacketSize=1024;
	private long sessionCleanupInterval=300;
	private long sessionDurationForCleanup=300;
	private long sessionTimeout=3000;
	private String eapTtlsCertificateRequest="false";
	private long createdByStaffId;
	private Timestamp createDate;
	private long lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Set<EAPTLSConfigData> eapTlsConfigSet;
	/*private int tls;
	private int ttls;
	private int peap;
	private int aka;
	private int sim;
	private int md5;
	private int mschapv2;
	private int gtc;*/
	
	private String tls;
	private String ttls;
	private String peap;
	private String aka;
	private String sim;
	private String md5;
	private String mschapv2;
	private String gtc;
	private String enabledAuthServiceMethod;
	private String type;
	
	public long getEapId() {
		return eapId;
	}
	public void setEapId(long eapId) {
		this.eapId = eapId;
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
	public long getDefaultNegiotationMethod() {
		return defaultNegiotationMethod;
	}
	public void setDefaultNegiotationMethod(long defaultNegiotationMethod) {
		this.defaultNegiotationMethod = defaultNegiotationMethod;
	}
	public String getTreatInvalidPacketAsFatal() {
		return treatInvalidPacketAsFatal;
	}
	public void setTreatInvalidPacketAsFatal(String treatInvalidPacketAsFatal) {
		this.treatInvalidPacketAsFatal = treatInvalidPacketAsFatal;
	}
	public String getNotificationSuccess() {
		return notificationSuccess;
	}
	public void setNotificationSuccess(String notificationSuccess) {
		this.notificationSuccess = notificationSuccess;
	}
	public String getNotificationFailure() {
		return notificationFailure;
	}
	public void setNotificationFailure(String notificationFailure) {
		this.notificationFailure = notificationFailure;
	}
	public long getMaxEapPacketSize() {
		return maxEapPacketSize;
	}
	public void setMaxEapPacketSize(long maxEapPacketSize) {
		this.maxEapPacketSize = maxEapPacketSize;
	}
	public long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}
	public void setSessionCleanupInterval(long sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}
	public long getSessionDurationForCleanup() {
		return sessionDurationForCleanup;
	}
	public void setSessionDurationForCleanup(long sessionDurationForCleanup) {
		this.sessionDurationForCleanup = sessionDurationForCleanup;
	}
	public long getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	public String getEapTtlsCertificateRequest() {
		return eapTtlsCertificateRequest;
	}
	public void setEapTtlsCertificateRequest(String eapTtlsCertificateRequest) {
		this.eapTtlsCertificateRequest = eapTtlsCertificateRequest;
	}
	public long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Set<EAPTLSConfigData> getEapTlsConfigSet() {
		return eapTlsConfigSet;
	}
	public void setEapTlsConfigSet(Set<EAPTLSConfigData> eapTlsConfigSet) {
		this.eapTlsConfigSet = eapTlsConfigSet;
	}
	public String getTls() {
		return tls;
	}
	public void setTls(String tls) {
		this.tls = tls;
	}
	public String getTtls() {
		return ttls;
	}
	public void setTtls(String ttls) {
		this.ttls = ttls;
	}
	public String getPeap() {
		return peap;
	}
	public void setPeap(String peap) {
		this.peap = peap;
	}
	public String getAka() {
		return aka;
	}
	public void setAka(String aka) {
		this.aka = aka;
	}
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getMschapv2() {
		return mschapv2;
	}
	public void setMschapv2(String mschapv2) {
		this.mschapv2 = mschapv2;
	}
	public String getGtc() {
		return gtc;
	}
	public void setGtc(String gtc) {
		this.gtc = gtc;
	}
	public String getEnabledAuthServiceMethod() {
		return enabledAuthServiceMethod;
	}
	public void setEnabledAuthServiceMethod(String enabledAuthServiceMethod) {
		this.enabledAuthServiceMethod = enabledAuthServiceMethod;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
