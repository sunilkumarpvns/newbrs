package com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient;

import java.sql.Timestamp;

public class RadiusDynaAuthClientData {
	
	private String instanceID;
	private Timestamp createTime;
	private Long radiusDynAuthClientIndex;
	private String radiusDynAuthClientAddressType;
	private String radiusDynAuthClientAddress;
	private Long ServDisconRequests;
	private Long ServDisconAuthOnlyRequests;
	private Long ServDupDisconRequests;
	private Long radiusDynAuthServDisconAcks;
	private Long radiusDynAuthServDisconNaks;
	private Long ServDisconNakAuthOnlyRequests;
	private Long ServDisconNakSessNoContext;
	private Long ServDisconUserSessRemoved;
	private Long ServMalformedDisconRequests;
	private Long ServDisconBadAuthenticators;
	private Long ServDisconPacketsDropped;
	private Long radiusDynAuthServCoARequests;
	private Long ServCoAAuthOnlyRequests;
	private Long ServDupCoARequests;
	private Long radiusDynAuthServCoAAcks;
	private Long ServCoANakAuthOnlyRequests;
	private Long ServCoANakSessNoContext;
	private Long ServCoAUserSessChanged;
	private Long ServMalformedCoARequests;
	private Long ServCoABadAuthenticators;
	private Long ServCoAPacketsDropped;
	private Long radiusDynAuthServUnknownTypes;
	private Long ServerCounterDiscontinuity;
	private Long radiusDynAuthServCoANaks;
	
	public String getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Long getRadiusDynAuthClientIndex() {
		return radiusDynAuthClientIndex;
	}
	public void setRadiusDynAuthClientIndex(Long radiusDynAuthClientIndex) {
		this.radiusDynAuthClientIndex = radiusDynAuthClientIndex;
	}
	public String getRadiusDynAuthClientAddressType() {
		return radiusDynAuthClientAddressType;
	}
	public void setRadiusDynAuthClientAddressType(
			String radiusDynAuthClientAddressType) {
		this.radiusDynAuthClientAddressType = radiusDynAuthClientAddressType;
	}
	public String getRadiusDynAuthClientAddress() {
		return radiusDynAuthClientAddress;
	}
	public void setRadiusDynAuthClientAddress(String radiusDynAuthClientAddress) {
		this.radiusDynAuthClientAddress = radiusDynAuthClientAddress;
	}
	public Long getServDisconRequests() {
		return ServDisconRequests;
	}
	public void setServDisconRequests(Long servDisconRequests) {
		ServDisconRequests = servDisconRequests;
	}
	public Long getServDisconAuthOnlyRequests() {
		return ServDisconAuthOnlyRequests;
	}
	public void setServDisconAuthOnlyRequests(Long servDisconAuthOnlyRequests) {
		ServDisconAuthOnlyRequests = servDisconAuthOnlyRequests;
	}
	public Long getServDupDisconRequests() {
		return ServDupDisconRequests;
	}
	public void setServDupDisconRequests(Long servDupDisconRequests) {
		ServDupDisconRequests = servDupDisconRequests;
	}
	public Long getRadiusDynAuthServDisconAcks() {
		return radiusDynAuthServDisconAcks;
	}
	public void setRadiusDynAuthServDisconAcks(Long radiusDynAuthServDisconAcks) {
		this.radiusDynAuthServDisconAcks = radiusDynAuthServDisconAcks;
	}
	public Long getRadiusDynAuthServDisconNaks() {
		return radiusDynAuthServDisconNaks;
	}
	public void setRadiusDynAuthServDisconNaks(Long radiusDynAuthServDisconNaks) {
		this.radiusDynAuthServDisconNaks = radiusDynAuthServDisconNaks;
	}
	public Long getServDisconNakAuthOnlyRequests() {
		return ServDisconNakAuthOnlyRequests;
	}
	public void setServDisconNakAuthOnlyRequests(Long servDisconNakAuthOnlyRequests) {
		ServDisconNakAuthOnlyRequests = servDisconNakAuthOnlyRequests;
	}
	public Long getServDisconNakSessNoContext() {
		return ServDisconNakSessNoContext;
	}
	public void setServDisconNakSessNoContext(Long servDisconNakSessNoContext) {
		ServDisconNakSessNoContext = servDisconNakSessNoContext;
	}
	public Long getServDisconUserSessRemoved() {
		return ServDisconUserSessRemoved;
	}
	public void setServDisconUserSessRemoved(Long servDisconUserSessRemoved) {
		ServDisconUserSessRemoved = servDisconUserSessRemoved;
	}
	public Long getServMalformedDisconRequests() {
		return ServMalformedDisconRequests;
	}
	public void setServMalformedDisconRequests(Long servMalformedDisconRequests) {
		ServMalformedDisconRequests = servMalformedDisconRequests;
	}
	public Long getServDisconBadAuthenticators() {
		return ServDisconBadAuthenticators;
	}
	public void setServDisconBadAuthenticators(Long servDisconBadAuthenticators) {
		ServDisconBadAuthenticators = servDisconBadAuthenticators;
	}
	public Long getServDisconPacketsDropped() {
		return ServDisconPacketsDropped;
	}
	public void setServDisconPacketsDropped(Long servDisconPacketsDropped) {
		ServDisconPacketsDropped = servDisconPacketsDropped;
	}
	public Long getRadiusDynAuthServCoARequests() {
		return radiusDynAuthServCoARequests;
	}
	public void setRadiusDynAuthServCoARequests(Long radiusDynAuthServCoARequests) {
		this.radiusDynAuthServCoARequests = radiusDynAuthServCoARequests;
	}
	public Long getServCoAAuthOnlyRequests() {
		return ServCoAAuthOnlyRequests;
	}
	public void setServCoAAuthOnlyRequests(Long servCoAAuthOnlyRequests) {
		ServCoAAuthOnlyRequests = servCoAAuthOnlyRequests;
	}
	public Long getServDupCoARequests() {
		return ServDupCoARequests;
	}
	public void setServDupCoARequests(Long servDupCoARequests) {
		ServDupCoARequests = servDupCoARequests;
	}
	public Long getRadiusDynAuthServCoAAcks() {
		return radiusDynAuthServCoAAcks;
	}
	public void setRadiusDynAuthServCoAAcks(Long radiusDynAuthServCoAAcks) {
		this.radiusDynAuthServCoAAcks = radiusDynAuthServCoAAcks;
	}
	public Long getServCoANakAuthOnlyRequests() {
		return ServCoANakAuthOnlyRequests;
	}
	public void setServCoANakAuthOnlyRequests(Long servCoANakAuthOnlyRequests) {
		ServCoANakAuthOnlyRequests = servCoANakAuthOnlyRequests;
	}
	public Long getServCoANakSessNoContext() {
		return ServCoANakSessNoContext;
	}
	public void setServCoANakSessNoContext(Long servCoANakSessNoContext) {
		ServCoANakSessNoContext = servCoANakSessNoContext;
	}
	public Long getServCoAUserSessChanged() {
		return ServCoAUserSessChanged;
	}
	public void setServCoAUserSessChanged(Long servCoAUserSessChanged) {
		ServCoAUserSessChanged = servCoAUserSessChanged;
	}
	public Long getServMalformedCoARequests() {
		return ServMalformedCoARequests;
	}
	public void setServMalformedCoARequests(Long servMalformedCoARequests) {
		ServMalformedCoARequests = servMalformedCoARequests;
	}
	public Long getServCoABadAuthenticators() {
		return ServCoABadAuthenticators;
	}
	public void setServCoABadAuthenticators(Long servCoABadAuthenticators) {
		ServCoABadAuthenticators = servCoABadAuthenticators;
	}
	public Long getServCoAPacketsDropped() {
		return ServCoAPacketsDropped;
	}
	public void setServCoAPacketsDropped(Long servCoAPacketsDropped) {
		ServCoAPacketsDropped = servCoAPacketsDropped;
	}
	public Long getRadiusDynAuthServUnknownTypes() {
		return radiusDynAuthServUnknownTypes;
	}
	public void setRadiusDynAuthServUnknownTypes(Long radiusDynAuthServUnknownTypes) {
		this.radiusDynAuthServUnknownTypes = radiusDynAuthServUnknownTypes;
	}
	public Long getServerCounterDiscontinuity() {
		return ServerCounterDiscontinuity;
	}
	public void setServerCounterDiscontinuity(Long serverCounterDiscontinuity) {
		ServerCounterDiscontinuity = serverCounterDiscontinuity;
	}
	public Long getRadiusDynAuthServCoANaks() {
		return radiusDynAuthServCoANaks;
	}
	public void setRadiusDynAuthServCoANaks(Long radiusDynAuthServCoANaks) {
		this.radiusDynAuthServCoANaks = radiusDynAuthServCoANaks;
	}
}

 