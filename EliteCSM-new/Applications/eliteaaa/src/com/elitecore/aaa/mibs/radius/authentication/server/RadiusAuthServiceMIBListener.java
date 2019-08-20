package com.elitecore.aaa.mibs.radius.authentication.server;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.mibs.radius.authentication.server.autogen.TableRadiusAuthClientTable;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.auth.RadiusAuthServiceMIBCounters;
import com.elitecore.aaa.radius.service.auth.RadiusAuthServiceMIBCounters.RadiusAuthClientEntry;

public class RadiusAuthServiceMIBListener {
	private static RadiusAuthServiceMIBCounters authServiceMIBCounters = new RadiusAuthServiceMIBCounters(null);
	private static TableRadiusAuthClientTable authClientTable;
	
	public RadiusAuthServiceMIBListener(RadiusAuthServiceMIBCounters radiusAuthServiceMIBCounters){
		authServiceMIBCounters = radiusAuthServiceMIBCounters;		
	}
	public void init(){
		authServiceMIBCounters.init();		
	}
	public static boolean getInitializedState() {
		return authServiceMIBCounters.isInitialized;
	}
	
	public static boolean getOtherState() {
		return authServiceMIBCounters.isOther;
	}
	
	public void setOtherState() {
		authServiceMIBCounters.isOther = true;
	}
	public static boolean reInitialize(){		
		authServiceMIBCounters.reInitialize();		
		return true;
	}		
	public void listenUpdateRadiusAuthServResetTimeEvent() {
		authServiceMIBCounters.radiusAuthServResetTime.set(System.currentTimeMillis());
	}
	
	public void listenRadiusAuthServTotalAccessAcceptsEvent(String clientAddress) {		
		authServiceMIBCounters.radiusAuthServTotalAccessAccepts.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServAccessAccepts();
		}		
	}
	
	
	
	public void listenRadiusAuthServTotalAccessChallengesEvent(String clientAddress) {
		authServiceMIBCounters.radiusAuthServTotalAccessChallenges.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServAccessChallenges();
		}
	}
	public void listenRadiusAuthServTotalAccessRejectsEvent(String clientAddress) {
		authServiceMIBCounters.radiusAuthServTotalAccessRejects.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServAccessRejects();
		}	
	}
	public void listenRadiusAuthServTotalAccessRequestsEvent() {		
		authServiceMIBCounters.radiusAuthServTotalAccessRequests.incrementAndGet();
	}

	public void listenRadiusAuthServTotalAccessRequestsEvent(String clientAddress) {
	
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServAccessRequests();
		}	
	}
	
	public void listenRadiusAuthServTotalBadAuthenticatorsEvent(String clientAddress) {	
		authServiceMIBCounters.radiusAuthServTotalBadAuthenticators.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServBadAuthenticators();	
		}
	}
	public void listenRadiusAuthServTotalDupAccessRequestsEvent(String clientAddress) {		
		authServiceMIBCounters.radiusAuthServTotalDupAccessRequests.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServDupAccessRequests();			
		}
	}
	public void listenRadiusAuthServTotalInvalidRequestsEvent() {		
		authServiceMIBCounters.radiusAuthServTotalInvalidRequests.incrementAndGet();
	}
	public void listenRadiusAuthServTotalMalformedAccessRequestsEvent(String clientAddress) {		
		authServiceMIBCounters.radiusAuthServTotalMalformedAccessRequests.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServMalformedAccessRequests();
		}		
	}
	public void listenRadiusAuthServTotalPacketsDroppedEvent(String clientAddress) {	
		authServiceMIBCounters.radiusAuthServTotalPacketsDropped.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServPacketsDropped();	
		}
	}
	public void listenRadiusAuthServTotalUnknownTypesEvent(String clientAddress) {		
		authServiceMIBCounters.radiusAuthServTotalUnknownTypes.incrementAndGet();
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			authClientEntry.incrementRadiusAuthServUnknownTypes();
		}
	}
	
	
	public static long getRadiusAuthServResetTime() {
		return authServiceMIBCounters.radiusAuthServResetTime.get();
	}

	public static long getRadiusAuthServTotalAccessAccepts() {
		return authServiceMIBCounters.radiusAuthServTotalAccessAccepts.get();
	}

	public static long getRadiusAuthServTotalAccessAccepts(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServAccessAccepts();
		}
		return 0;
	}
	
	public static long getRadiusAuthServTotalAccessChallenges() {
		return authServiceMIBCounters.radiusAuthServTotalAccessChallenges.get();
	}

	public static long getRadiusAuthServTotalAccessChallenges(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServAccessChallenges();
		}
		return 0;
	}
		
	public HashMap getRadiusServTotaleAccessRejectReason(String clientAddress) {
		RadiusAuthClientEntry radiusAuthClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		return radiusAuthClientEntry.getRadiusAuthServAccessRejectsReasons();
	}

	public static long getRadiusAuthServTotalAccessRejects() {
		return authServiceMIBCounters.radiusAuthServTotalAccessRejects.get();
	}

	public static long getRadiusAuthServTotalAccessRejects(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServAccessRejects();
		}
		return 0;
	}
	public static long getRadiusAuthServTotalAccessRequests() {
		return authServiceMIBCounters.radiusAuthServTotalAccessRequests.get();
	}
	public static long getRadiusAuthServTotalAccessRequests(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServAccessRequests();
		}
		return 0;
	}	
	public static long getRadiusAuthServTotalBadAuthenticators() {
		return authServiceMIBCounters.radiusAuthServTotalBadAuthenticators.get();
	}
	public static long getRadiusAuthServTotalBadAuthenticators(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServBadAuthenticators();
		}
		return 0;
	}
	

	public static long getRadiusAuthServTotalDupAccessRequests() {
		return authServiceMIBCounters.radiusAuthServTotalDupAccessRequests.get();
	}

	public static long getRadiusAuthServTotalDupAccessRequests(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServDupAccessRequests();
		}
		return 0;
	}
	
	public static long getRadiusAuthServTotalInvalidRequests() {
		return authServiceMIBCounters.radiusAuthServTotalInvalidRequests.get();
	}

	

	public  static long getRadiusAuthServTotalMalformedAccessRequests() {		
		return authServiceMIBCounters.radiusAuthServTotalMalformedAccessRequests.get();		
	}

	public  static long getRadiusAuthServTotalMalformedAccessRequests(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServMalformedAccessRequests();
		}
		return 0;
	}


	public static  long getRadiusAuthServTotalPacketsDropped() {
		return authServiceMIBCounters.radiusAuthServTotalPacketsDropped.get();
	}

	public static long getRadiusAuthServTotalPacketsDropped(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServPacketsDropped();
		}
		return 0;
	}

	
	public static long getRadiusAuthServTotalUnknownTypes() {
		return authServiceMIBCounters.radiusAuthServTotalUnknownTypes.get();
	}

	public static long getRadiusAuthServTotalUnknownTypes(String clientAddress) {
		RadiusAuthClientEntry authClientEntry = (RadiusAuthClientEntry)authServiceMIBCounters.authClientEntryMap.get(clientAddress);
		if (authClientEntry != null){
			return authClientEntry.getRadiusAuthServUnknownTypes();
		}
		return 0;
	}
	public static Map<String, String> getClientMap(){
		return authServiceMIBCounters.clientMap;
	}
	public static RadClientData getClientData(String strIp){
		return authServiceMIBCounters.getClientData(strIp);
	}
	public static long getRadiusAuthServUpTime() {
		return authServiceMIBCounters.radiusAuthServUpTime.get();
	}

	public static TableRadiusAuthClientTable getAuthClientTable() {
		return authClientTable;
	}

	public static void setAuthClientTable(TableRadiusAuthClientTable authClientTable) {
		RadiusAuthServiceMIBListener.authClientTable = authClientTable;
	}
		
}
