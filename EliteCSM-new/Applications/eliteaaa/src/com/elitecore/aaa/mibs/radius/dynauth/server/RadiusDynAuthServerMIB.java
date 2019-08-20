/**
 * RadiusDynAuthServerMIB implementation
 */
package com.elitecore.aaa.mibs.radius.dynauth.server;

import java.util.Map;

import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.TableRadiusDynAuthClientTable;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.dynauth.RadiusDynAuthServiceMIBCounters;
import com.elitecore.aaa.radius.service.dynauth.RadiusDynAuthServiceMIBCounters.RadiusDynAuthClientEntry;


public class RadiusDynAuthServerMIB {
	private static RadiusDynAuthServiceMIBCounters dynAuthMIBCounter = new RadiusDynAuthServiceMIBCounters(null);
	private static TableRadiusDynAuthClientTable dynAuthClientTable;
	
	public RadiusDynAuthServerMIB(RadiusDynAuthServiceMIBCounters radiusDynAuthServiceMIBCounters){
		dynAuthMIBCounter = radiusDynAuthServiceMIBCounters;		
	}
	
	public void init(){
		dynAuthMIBCounter.init();		
	}
	public static boolean getInitializedState() {
		return dynAuthMIBCounter.isInitialized;
	}
	
	public static boolean getOtherState() {
		return dynAuthMIBCounter.isOther;
	}
	public void setOtherState() {
		dynAuthMIBCounter.isOther = true;
	}
	public static boolean reInitialize(){		
		dynAuthMIBCounter.reInitialize();		
		return true;
	}
	
	public void listenUpdateRadiusDynAuthServResetTimeEvent() {
		dynAuthMIBCounter.radiusDynAuthServResetTime.set(System.currentTimeMillis());
	}
	
	public void listenRadiusDynAuthServTotalRequestsEvent() {	
		dynAuthMIBCounter.radiusDynAuthTotalRequests.incrementAndGet();
	}
	
	public  static long getRadiusDynAuthServTotalRequestsEvent() {	
		return dynAuthMIBCounter.radiusDynAuthTotalRequests.get();
	}
	
	public void listenRadiusDynAuthServTotalCOAAckEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalCOAAck.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServCoAAcks();
		}		
	}
	
	public void listenRadiusDynAuthServTotalDisConnectAckEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalDisConnectAck.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDisconAcks();
		}		
	}
	
	public void listenRadiusDynAuthServTotalCOANakEvent(String clientAddress) {
		dynAuthMIBCounter.radiusDynAuthServTotalCOANak.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServCoANaks();
		}	
	}
	public void listenRadiusDynAuthServTotalDisConnectNakEvent(String clientAddress) {
		dynAuthMIBCounter.radiusDynAuthServTotalDisConnectNak.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDisconNaks();
		}	
	}
	public void listenRadiusDynAuthServTotalCOARequestsEvent() {		
		dynAuthMIBCounter.radiusDynAuthServTotalCOARequests.incrementAndGet();
	}
	
	public void listenRadiusDynAuthServTotalDisconRequestsEvent() {		
		dynAuthMIBCounter.radiusDynAuthServTotalDisConnectRequests.incrementAndGet();
	}
	
	public void listenRadiusDynAuthServTotalCOARequestsEvent(String clientAddress) {
																	
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServCoARequests();
		}	
	}
	public void listenRadiusDynAuthServTotalDisconRequestsEvent(String clientAddress) {
		
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDisconRequests();
		}	
	}
	
	public void listenRadiusDynAuthServTotalBadAuthenticatorsCOARequestsEvent(String clientAddress) {	
		dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsCOARequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServCoABadAuthenticators();	
		}
	}
	
	public void listenRadiusDynAuthServTotalBadAuthenticatorsRequestsEvent(String clientAddress) {	
		dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsRequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServBadAuthenticators();	
		}
	}
	
	public void listenRadiusDynAuthServTotalBadAuthenticatorsDisConnectRequestsEvent(String clientAddress) {	
		dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsDisConnectRequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDisconBadAuthenticators();	
		}
	}
	
	public void listenRadiusDynAuthServTotalDupCOARequestsEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalDupCOARequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDupCoARequests();			
		}
	}
	
	public void listenRadiusDynAuthServTotalDupDisConnectEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalDupDisConnectRequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDupDisconRequests();			
		}
	}
	public void listenRadiusDynAuthServTotalInvalidRequestsEvent() {		
		dynAuthMIBCounter.radiusDynAuthServTotalInvalidRequests.incrementAndGet();
	}
	public void listenRadiusDynAuthServTotalClientCoAInvalidClientAddressesEvent() {		
		dynAuthMIBCounter.radiusDynAuthClientCoAInvalidClientAddresses.incrementAndGet();
	}
	public void listenRadiusDynAuthServTotalClientDisConnectInvalidClientAddressesEvent() {		
		dynAuthMIBCounter.radiusDynAuthClientDisConnectInvalidClientAddresses.incrementAndGet();
	}
	public void listenRadiusDynAuthServTotalMalformedCOARequestsEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalMalformedCOARequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServMalformedCoARequests();
		}		
	}
	
	public void listenRadiusDynAuthServTotalMalformedDisConnectRequestsEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalMalformedDisConnectRequests.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServMalformedDisconRequests();
		}		
	}
	public void listenRadiusDynAuthServTotalCOAPacketsDroppedEvent(String clientAddress) {	
		dynAuthMIBCounter.radiusDynAuthServTotalCOAPacketsDropped.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServCoAPacketsDropped();	
		}
	}
	public void listenRadiusDynAuthServTotalDisConnectPacketsDroppedEvent(String clientAddress) {	
		dynAuthMIBCounter.radiusDynAuthServTotalDisConnectPacketsDropped.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServDisconPacketsDropped();	
		}
	}
	public void listenRadiusDynAuthServTotalUnknownTypesEvent(String clientAddress) {		
		dynAuthMIBCounter.radiusDynAuthServTotalUnknownTypes.incrementAndGet();
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			dynAuthClientEntry.incrementDynAuthServUnknownTypes();
		}
	}
	public static long getRadiusDynAuthServResetTime() {
		return dynAuthMIBCounter.radiusDynAuthServResetTime.get();
	}
	
	public static long getRadiusDynAuthServTotalCOAAck() {
		return dynAuthMIBCounter.radiusDynAuthServTotalCOAAck.get();
	}
	public static long getRadiusDynAuthServTotalConnectAck() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDisConnectAck.get();
	}
	public static long getRadiusDynAuthServCoAAcks(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServCoAAcks();
		}
		return 0;
	}
	public static long getRadiusDynAuthServDisconAcks(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDisconAcks();
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalCOANak() {
		return dynAuthMIBCounter.radiusDynAuthServTotalCOANak.get();
	}
	public static long getRadiusDynAuthServTotalDisConnectNak() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDisConnectNak.get();
	}
	public static long getRadiusDynAuthServCoANaks(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServCoANaks();
		}
		return 0;
	}
	public static long getRadiusDynAuthServDisconNaks(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDisconNaks();
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalCOARequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalCOARequests.get();
	}
	public static long getRadiusDynAuthServTotalDisConnectRequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDisConnectRequests.get();
	}
	public static long getRadiusDynAuthServCoARequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServCoARequests();
		}
		return 0;
	}
	public static long getRadiusDynAuthServDisconRequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDisconRequests();
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalBadAuthenticatorsCOARequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsCOARequests.get();
	}
	public static long getRadiusDynAuthServTotalBadAuthenticatorsDisConnectRequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsDisConnectRequests.get();
	}
	public static long getRadiusDynAuthServCoABadAuthenticators(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServCoABadAuthenticators();
		}
		return 0;
	}
	public static long getRadiusDynAuthServDisconBadAuthenticators(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDisconBadAuthenticators();
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalDupCOARequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDupCOARequests.get();
	}
	public static long getRadiusDynAuthServTotalDupDisConnectRequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDupDisConnectRequests.get();
	}
	public static long getRadiusDynAuthServDupCoARequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDupCoARequests();
		}
		return 0;
	}
	public static long getRadiusDynAuthServDupDisconRequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDupDisconRequests();
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalInvalidRequests() {
		return dynAuthMIBCounter.radiusDynAuthServTotalInvalidRequests.get();
	}
	public static long getRadiusDynAuthServTotalMalformedCOARequests() {		
		return dynAuthMIBCounter.radiusDynAuthServTotalMalformedCOARequests.get();		
	}
	public static long getRadiusDynAuthServTotalMalformedDisConnectRequests() {		
		return dynAuthMIBCounter.radiusDynAuthServTotalMalformedDisConnectRequests.get();		
	}
	
	public static long getRadiusDynAuthServTotalBadAuthenticatorsRequests() {		
		return dynAuthMIBCounter.radiusDynAuthServTotalBadAuthenticatorsRequests.get();		
	}
	
	public  static long getRadiusDynAuthServMalformedCoARequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServMalformedCoARequests();
		}
		return 0;
	}
	public  static long getRadiusDynAuthServMalformedDisconRequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServMalformedDisconRequests();
		}
		return 0;
	}
	public static  long getRadiusDynAuthServTotalCOAPacketsDropped() {
		return dynAuthMIBCounter.radiusDynAuthServTotalCOAPacketsDropped.get();
	}
	public static  long getRadiusDynAuthServTotalDisConnectPacketsDropped() {
		return dynAuthMIBCounter.radiusDynAuthServTotalDisConnectPacketsDropped.get();
	}
	public static long getRadiusDynAuthServCoAPacketsDropped(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServCoAPacketsDropped();
			
		}
		return 0;
	}
	public static long getRadiusDynAuthServDisconPacketsDropped(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServDisconPacketsDropped();
		
		}
		return 0;
	}
	public static long getRadiusDynAuthServTotalUnknownTypes() {
		return dynAuthMIBCounter.radiusDynAuthServTotalUnknownTypes.get();
	}
	public static long getRadiusDynAuthServUnknownTypes(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
		if (dynAuthClientEntry != null){
			return dynAuthClientEntry.getDynAuthServUnknownTypes();
		}
		return 0;
	}
	
	public static Map<String, String> getClientMap(){
		return dynAuthMIBCounter.clientMap;
	}
	public static RadClientData getClientData(String strIp){
		return dynAuthMIBCounter.getClientData(strIp);
	}
	public static long getRadiusDynAuthServUpTime() {
		return dynAuthMIBCounter.radiusDynAuthServUpTime.get();
	}
	public static long getDynAuthClientDisconInvalidClientAddresses() {
        return dynAuthMIBCounter.radiusDynAuthClientDisConnectInvalidClientAddresses.get();
    }
	
	public static long getDynAuthClientCoAInvalidClientAddresses() {
        return dynAuthMIBCounter.radiusDynAuthClientCoAInvalidClientAddresses.get();
    }
	public static String getServerIdentifier() {
        return "Radius DynAuth Server";
    }
	public static long getRadiusDynAuthServDisconNakSessNoContext(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServDisconNakSessNoContext();
        }
        return 0;
    }
	public static long getRadiusDynAuthServDisconUserSessRemoved(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServDisconUserSessRemoved();
        }
        return 0;
    }
	public static long getRadiusDynAuthServCoAAuthOnlyRequests(String clientAddress) {
		RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServCoAAuthOnlyRequests();
        }
        return 0;
    }
	/**
     * Returns the InetAddressType of the client address
     * @param clientAddress   IP Address of the client
     * @return InetAddressType  -  The type of the InetAddress
     */
    public static int getRadiusDynAuthClientAddressType(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthClientAddressType();
        }
        return 0;
    }
    public static long getRadiusDynAuthServCoANakAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServCoANakAuthOnlyRequests();
        }
        return 0;
    }
    public static long getRadiusDynAuthServCoANakSessNoContext(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServCoANakSessNoContext();
        }
        return 0;
    }
    public static long getRadiusDynAuthServCoAUserSessChanged(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServCoANakSessNoContext();
        }
        return 0;
    }
    public static long getDynAuthServerCounterDiscontinuity(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServerCounterDiscontinuity();
        }
        return 0;
    }
    /**
     * 
     * @param clientAddress
     * @return counter No. of disconnect auth only requests
     */
    public static long getRadiusDynAuthServDisconAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServDisconAuthOnlyRequests();
        }
        return 0;
    }
    public static long getRadiusDynAuthServDisconNakAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            return dynAuthClientEntry.getDynAuthServDisconNakAuthOnlyRequests();
        }
        return 0;
    }
    public static void listenRadiusDynAuthServDisconNakSessNoContext(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
             dynAuthClientEntry.incrementDynAuthServDisconNakSessNoContext();
        }
   }
    public static void listenRadiusDynAuthServCoAAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
             dynAuthClientEntry.incrementDynAuthServCoAAuthOnlyRequests();
        }
    }
    public static void listenRadiusDynAuthServCoANakAuthOnlyRequests(String clientAddress) {
      	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
      	if (dynAuthClientEntry != null) {
            dynAuthClientEntry.incrementDynAuthServCoANakAuthOnlyRequests();
        }
    }
    public static void listenRadiusDynAuthServCoANakSessNoContext(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            dynAuthClientEntry.incrementDynAuthServCoANakSessNoContext();
        }
    }
    public static void listenRadiusDynAuthServCoAUserSessChanged(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            dynAuthClientEntry.incrementDynAuthServCoAUserSessChanged();
        }
    }
    public static void setDynAuthServerCounterDiscontinuity(String clientAddress, long counterDiscontinuity) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            dynAuthClientEntry.setDynAuthServUnknownTypes(counterDiscontinuity);
         }
     
    }
    public static void listenRadiusDynAuthServDisconAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
    	if (dynAuthClientEntry != null) {
            dynAuthClientEntry.incrementDynAuthServDisconAuthOnlyRequests();
         }
    }
    public static void listenRadiusDynAuthServDisconNakAuthOnlyRequests(String clientAddress) {
    	RadiusDynAuthClientEntry dynAuthClientEntry = dynAuthMIBCounter.dynAuthClientEntryMap.get(clientAddress);
        if (dynAuthClientEntry != null) {
            dynAuthClientEntry.incrementDynAuthServDisconNakAuthOnlyRequests();
        }
    }

    public static TableRadiusDynAuthClientTable getDynAuthClientTable() {
		return dynAuthClientTable;
	}
    
	public static void setDynAuthClientTable(TableRadiusDynAuthClientTable dynAuthClientTable) {
		RadiusDynAuthServerMIB.dynAuthClientTable = dynAuthClientTable;
	}
}        


