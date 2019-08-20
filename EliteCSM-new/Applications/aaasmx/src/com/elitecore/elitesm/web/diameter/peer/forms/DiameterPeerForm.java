/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateDiameterpolicyForm.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DiameterPeerForm extends BaseWebForm{
	
		private String peerUUID;
	    private Long peerId;
	    private String hostIdentity;
	    private String realmName;
	    private String remoteAddress;
	    private String diameterURIFormat="${aaa}${FQDN}${port};transport=tcp;protocol=diameter"; 
		private String peerProfileId;
	    private List<DiameterPeerProfileData> peerProfileList;
	    private String localAddress;
	    private String name;
	    private java.lang.Long requestTimeout=3000L;
	    private java.lang.Long retransmissionCount=0L;
	    private String auditUId;
	    private String secondaryPeerName;
	    private List<DiameterPeerData> secondaryPeerList;
	    
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Long getPeerId() {
			return peerId;
		}
		public void setPeerId(Long peerId) {
			this.peerId = peerId;
		}
		public String getHostIdentity() {
			return hostIdentity;
		}
		public void setHostIdentity(String hostIdentity) {
			this.hostIdentity = hostIdentity;
		}
		public String getRealmName() {
			return realmName;
		}
		public void setRealmName(String realmName) {
			this.realmName = realmName;
		}
		public String getRemoteAddress() {
			return remoteAddress;
		}
		public void setRemoteAddress(String remoteAddress) {
			this.remoteAddress = remoteAddress;
		}
		public String getPeerProfileId() {
			return peerProfileId;
		}
		public void setPeerProfileId(String peerProfileId) {
			this.peerProfileId = peerProfileId;
		}
		public List<DiameterPeerProfileData> getPeerProfileList() {
			return peerProfileList;
		}
		public void setPeerProfileList(List<DiameterPeerProfileData> peerProfileList) {
			this.peerProfileList = peerProfileList;
		}
		public String getLocalAddress() {
			return localAddress;
		}
		public void setLocalAddress(String localAddress) {
			this.localAddress = localAddress;
		}
		public String getDiameterURIFormat() {
			return diameterURIFormat;
		}
		public void setDiameterURIFormat(String diameterURIFormat) {
			this.diameterURIFormat = diameterURIFormat;
		}
		public java.lang.Long getRequestTimeout() {
			return requestTimeout;
		}
		public void setRequestTimeout(java.lang.Long requestTimeout) {
			this.requestTimeout = requestTimeout;
		}
		public java.lang.Long getRetransmissionCount() {
			return retransmissionCount;
		}
		public void setRetransmissionCount(java.lang.Long retransmissionCount) {
			this.retransmissionCount = retransmissionCount;
		}
		public String getAuditUId() {
			return auditUId;
		}
		public void setAuditUId(String auditUId) {
			this.auditUId = auditUId;
		}
		public String getSecondaryPeerName() {
			return secondaryPeerName;
		}
		public void setSecondaryPeerName(String secondaryPeerName) {
			this.secondaryPeerName = secondaryPeerName;
		}
		public List<DiameterPeerData> getSecondaryPeerList() {
			return secondaryPeerList;
		}
		public void setSecondaryPeerList(List<DiameterPeerData> secondaryPeerList) {
			this.secondaryPeerList = secondaryPeerList;
		}
		public String getPeerUUID() {
			return peerUUID;
		}
		public void setPeerUUID(String peerUUID) {
			this.peerUUID = peerUUID;
		}
}
