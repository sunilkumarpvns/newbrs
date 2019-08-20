package com.elitecore.aaa.radius.service.dynauth;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;

public class RadiusDynAuthServiceMIBCounters {
	
	public AtomicLong radiusDynAuthServUpTime;
	public AtomicLong radiusDynAuthServResetTime;
	
	public AtomicLong radiusDynAuthTotalRequests;
	public AtomicLong radiusDynAuthServTotalCOARequests;
	public AtomicLong radiusDynAuthServTotalDisConnectRequests;
	
	public AtomicLong radiusDynAuthClientDisConnectInvalidClientAddresses;
	public AtomicLong radiusDynAuthClientCoAInvalidClientAddresses;
	public AtomicLong radiusDynAuthServTotalInvalidRequests;
	
	
	
	public AtomicLong radiusDynAuthServTotalDupCOARequests;
	public AtomicLong radiusDynAuthServTotalDupDisConnectRequests;
	
	public AtomicLong radiusDynAuthServTotalCOANak;
	public AtomicLong radiusDynAuthServTotalCOAAck;
	
	public AtomicLong radiusDynAuthServTotalDisConnectNak;
	public AtomicLong radiusDynAuthServTotalDisConnectAck;
	
	public AtomicLong radiusDynAuthServTotalMalformedCOARequests;
	public AtomicLong radiusDynAuthServTotalMalformedDisConnectRequests;
	
	public AtomicLong radiusDynAuthServTotalBadAuthenticatorsRequests;
	public AtomicLong radiusDynAuthServTotalBadAuthenticatorsCOARequests;
	public AtomicLong radiusDynAuthServTotalBadAuthenticatorsDisConnectRequests;
	
	public AtomicLong radiusDynAuthServTotalCOAPacketsDropped;
	public AtomicLong radiusDynAuthServTotalDisConnectPacketsDropped;
	
	public AtomicLong radiusDynAuthServTotalUnknownTypes;
	
	
	public Map <String, RadiusDynAuthClientEntry> dynAuthClientEntryMap ;
	public Map <String, String>clientMap ;
	
	public boolean isInitialized ;
	public boolean isOther = true;
	private AAAServerContext serverContext;
	
	public RadiusDynAuthServiceMIBCounters(AAAServerContext serverContext){
		this.serverContext = serverContext;
		
		
		radiusDynAuthServUpTime = new AtomicLong(0);
		radiusDynAuthServResetTime = new AtomicLong(0);
		
		radiusDynAuthTotalRequests = new AtomicLong(0);
		radiusDynAuthServTotalCOARequests = new AtomicLong(0);
		radiusDynAuthServTotalDisConnectRequests = new AtomicLong(0);

		radiusDynAuthClientDisConnectInvalidClientAddresses = new AtomicLong(0);
		radiusDynAuthClientCoAInvalidClientAddresses = new AtomicLong(0);
		radiusDynAuthServTotalInvalidRequests = new AtomicLong(0);
		
		radiusDynAuthServTotalDupCOARequests = new AtomicLong(0);
		radiusDynAuthServTotalDupDisConnectRequests = new AtomicLong(0);
		
		radiusDynAuthServTotalCOANak = new AtomicLong(0);
		radiusDynAuthServTotalCOAAck = new AtomicLong(0);
		
		radiusDynAuthServTotalDisConnectNak = new AtomicLong(0);
		radiusDynAuthServTotalDisConnectAck = new AtomicLong(0);
		
		radiusDynAuthServTotalMalformedCOARequests = new AtomicLong(0);
		radiusDynAuthServTotalMalformedDisConnectRequests = new AtomicLong(0);
		
		radiusDynAuthServTotalBadAuthenticatorsRequests = new AtomicLong(0);
		radiusDynAuthServTotalBadAuthenticatorsCOARequests = new AtomicLong(0);
		radiusDynAuthServTotalBadAuthenticatorsDisConnectRequests = new AtomicLong(0);
		
		radiusDynAuthServTotalCOAPacketsDropped = new AtomicLong(0);
		radiusDynAuthServTotalDisConnectPacketsDropped = new AtomicLong(0);
		
		radiusDynAuthServTotalUnknownTypes = new AtomicLong(0);
		
		
		dynAuthClientEntryMap  = new HashMap<String, RadiusDynAuthClientEntry>();
		clientMap  = new HashMap<String, String>();
		
	}
	private List<String>getClientAdresses(){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();		
		if(clientConfiguration !=null){
			return clientConfiguration.getClientAddresses();
		}
		return new ArrayList<String>();
	}
	
	public void init(){
		List<String>clientAddressList = getClientAdresses();
		if (!isInitialized){
			isOther = false;
			
			radiusDynAuthServUpTime.set(System.currentTimeMillis());
			radiusDynAuthServResetTime.set(System.currentTimeMillis());
			
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				ListIterator<String> iterator = clientAddressList.listIterator();
				int clientIndexCounter = 0;
				while(iterator.hasNext()){
					clientIndexCounter++;
					String clientAddress = String.valueOf(iterator.next());
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						dynAuthClientEntryMap.put(clientAddress, new RadiusDynAuthClientEntry(clientIndexCounter, clientAddress));
					}
				}
			}
			isInitialized = true;
		}
	}
	
	
	public void reset(){
		
		isInitialized = false;
		
		radiusDynAuthServUpTime.set(0);
		radiusDynAuthServResetTime.set(0);
		
		radiusDynAuthTotalRequests.set(0);
		radiusDynAuthServTotalCOARequests.set(0);
		radiusDynAuthServTotalDisConnectRequests.set(0);
		
		radiusDynAuthClientCoAInvalidClientAddresses.set(0);
		radiusDynAuthServTotalInvalidRequests.set(0);
		radiusDynAuthClientDisConnectInvalidClientAddresses.set(0);
		
		radiusDynAuthServTotalDupCOARequests.set(0);
		radiusDynAuthServTotalDupDisConnectRequests.set(0);
		
		radiusDynAuthServTotalCOANak.set(0);
		radiusDynAuthServTotalCOAAck.set(0);
		
		radiusDynAuthServTotalDisConnectNak.set(0);
		radiusDynAuthServTotalDisConnectAck.set(0);
		
		radiusDynAuthServTotalMalformedCOARequests.set(0);
		radiusDynAuthServTotalMalformedDisConnectRequests.set(0);
		
		radiusDynAuthServTotalBadAuthenticatorsRequests.set(0);
		radiusDynAuthServTotalBadAuthenticatorsCOARequests.set(0);
		radiusDynAuthServTotalBadAuthenticatorsDisConnectRequests.set(0);
		
		radiusDynAuthServTotalCOAPacketsDropped.set(0);
		radiusDynAuthServTotalDisConnectPacketsDropped.set(0);
		
		radiusDynAuthServTotalUnknownTypes.set(0);
				
		clientMap = new HashMap<String, String>();
		dynAuthClientEntryMap = new HashMap<String, RadiusDynAuthClientEntry>();		
	}
	
	public void reInitialize(){
		List<String> clientAddressList = getClientAdresses();
		Map <String, RadiusDynAuthClientEntry> localDynAuthClientEntryMap = new HashMap<String, RadiusDynAuthClientEntry>();
		Map <String, String>localClientMap = new HashMap<String, String>();
		radiusDynAuthServResetTime.set(System.currentTimeMillis());
		
		RadiusDynAuthClientEntry dynAuthClientEntry;
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			ListIterator<String> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 0;
			while(iterator.hasNext()){
				clientIndexCounter++;					
				String clientAddress = String.valueOf(iterator.next());
				localClientMap.put(String.valueOf(clientIndexCounter), clientAddress);
				dynAuthClientEntry = (RadiusDynAuthClientEntry)dynAuthClientEntryMap.get(clientAddress);
				if(dynAuthClientEntry == null){
					localDynAuthClientEntryMap.put(clientAddress, new RadiusDynAuthClientEntry(clientIndexCounter, clientAddress));
				}else{
					localDynAuthClientEntryMap.put(clientAddress, dynAuthClientEntry);
				}
			}
			dynAuthClientEntryMap = localDynAuthClientEntryMap;
			clientMap = localClientMap;
		}
		isInitialized = true;
	}
	
	public RadClientData getClientData(String strIp){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		RadClientData clientData = null;
		if(clientConfiguration != null){
			clientData = clientConfiguration.getClientData(strIp);
		}
		return clientData;
	}
	
    public static class RadiusDynAuthClientEntry {

        private AtomicInteger dynAuthClientIndex = new AtomicInteger();
        private AtomicInteger dynAuthClientAddressType = new AtomicInteger();
        private String dynAuthClientAddress;
        private AtomicLong dynAuthServDisconRequests = new AtomicLong();
        private AtomicLong dynAuthServDisconAuthOnlyRequests = new AtomicLong();
        private AtomicLong dynAuthServDupDisconRequests = new AtomicLong();
        private AtomicLong dynAuthServDisconAcks = new AtomicLong();
        private AtomicLong dynAuthServDisconNaks = new AtomicLong();
        private AtomicLong dynAuthServDisconNakAuthOnlyRequests = new AtomicLong();
        private AtomicLong dynAuthServDisconNakSessNoContext = new AtomicLong();
        private AtomicLong dynAuthServDisconUserSessRemoved = new AtomicLong();
        private AtomicLong dynAuthServMalformedDisconRequests = new AtomicLong();
        private AtomicLong dynAuthServDisconBadAuthenticators = new AtomicLong();
        private AtomicLong dynAuthServDisconPacketsDropped = new AtomicLong();
        private AtomicLong dynAuthServCoARequests = new AtomicLong();
        private AtomicLong dynAuthServCoAAuthOnlyRequests = new AtomicLong();
        private AtomicLong dynAuthServDupCoARequests = new AtomicLong();
        private AtomicLong dynAuthServCoAAcks = new AtomicLong();
        private AtomicLong dynAuthServCoANaks = new AtomicLong();
        private AtomicLong dynAuthServCoANakAuthOnlyRequests = new AtomicLong();
        private AtomicLong dynAuthServCoANakSessNoContext = new AtomicLong();
        private AtomicLong dynAuthServCoAUserSessChanged = new AtomicLong();
        private AtomicLong dynAuthServMalformedCoARequests = new AtomicLong();
        private AtomicLong dynAuthServCoABadAuthenticators = new AtomicLong();
        private AtomicLong dynAuthServBadAuthenticators = new AtomicLong();
        private AtomicLong dynAuthServCoAPacketsDropped = new AtomicLong();
        private AtomicLong dynAuthServUnknownTypes = new AtomicLong();
        private AtomicLong dynAuthServerCounterDiscontinuity = new AtomicLong();

        public RadiusDynAuthClientEntry(int dynAuthClientIndex, String dynAuthClientAddress) {
            setDynAuthClientIndex(dynAuthClientIndex);
            setDynAuthClientAddress(dynAuthClientAddress);
            
        }

        public String getDynAuthClientAddress() {
            return dynAuthClientAddress;
        }

        public void setDynAuthClientAddress(String dynAuthClientAddress) {

            this.dynAuthClientAddress = dynAuthClientAddress;

            // Set type of InetAddress
            try {
                InetAddress inetAddress = InetAddress.getByName(dynAuthClientAddress);

                if (inetAddress instanceof Inet4Address) {
                    this.dynAuthClientAddressType.set(InetAddressType.IPv4);
                } else if (inetAddress instanceof Inet6Address) {
                    this.dynAuthClientAddressType.set(InetAddressType.IPv6);
                }
            } catch (UnknownHostException ex) {
                try {
                    new URL(dynAuthClientAddress);
                    this.dynAuthClientAddressType.set(InetAddressType.DNS);
                } catch (MalformedURLException exception) {
                    this.dynAuthClientAddressType.set(InetAddressType.UNKNOWN);
                }
            }
        }

        public int getDynAuthClientAddressType() {
            return dynAuthClientAddressType.get();
        }

        public int getDynAuthClientIndex() {
            return dynAuthClientIndex.get();
        }

        public void setDynAuthClientIndex(int dynAuthClientIndex) {
            this.dynAuthClientIndex.set(dynAuthClientIndex);
        }

        public long getDynAuthServCoAAcks() {
            return dynAuthServCoAAcks.get();
        }

        public void incrementDynAuthServCoAAcks() {
            dynAuthServCoAAcks.incrementAndGet();
        }

        public long getDynAuthServCoAAuthOnlyRequests() {
            return dynAuthServCoAAuthOnlyRequests.get();
        }

        public void incrementDynAuthServCoAAuthOnlyRequests() {
            dynAuthServCoAAuthOnlyRequests.incrementAndGet();
        }

        public long getDynAuthServCoABadAuthenticators() {
            return dynAuthServCoABadAuthenticators.get();
        }
        public long getDynAuthServBadAuthenticators() {
            return dynAuthServBadAuthenticators.get();
        }

        public void incrementDynAuthServCoABadAuthenticators() {
            dynAuthServCoABadAuthenticators.incrementAndGet();
        }
        public void incrementDynAuthServBadAuthenticators() {
            dynAuthServBadAuthenticators.incrementAndGet();
        }

        public long getDynAuthServCoANakAuthOnlyRequests() {
            return dynAuthServCoANakAuthOnlyRequests.get();
        }

        public void incrementDynAuthServCoANakAuthOnlyRequests() {
            dynAuthServCoANakAuthOnlyRequests.incrementAndGet();
        }

        public long getDynAuthServCoANakSessNoContext() {
            return dynAuthServCoANakSessNoContext.get();
        }

        public void incrementDynAuthServCoANakSessNoContext() {
            dynAuthServCoANakSessNoContext.incrementAndGet();
        }

        public long getDynAuthServCoANaks() {
            return dynAuthServCoANaks.get();
        }

        public void incrementDynAuthServCoANaks() {
            dynAuthServCoANaks.incrementAndGet();
        }

        public long getDynAuthServCoAPacketsDropped() {
            return dynAuthServCoAPacketsDropped.get();
        }

        public void incrementDynAuthServCoAPacketsDropped() {
            dynAuthServCoAPacketsDropped.incrementAndGet();
        }

        public long getDynAuthServCoARequests() {
            return dynAuthServCoARequests.get();
        }

        public void incrementDynAuthServCoARequests() {
            dynAuthServCoARequests.incrementAndGet();
        }

        public long getDynAuthServCoAUserSessChanged() {
            return dynAuthServCoAUserSessChanged.get();
        }

        public void incrementDynAuthServCoAUserSessChanged() {
            dynAuthServCoAUserSessChanged.incrementAndGet();
        }

        public long getDynAuthServDisconAcks() {
            return dynAuthServDisconAcks.get();
        }

        public void incrementDynAuthServDisconAcks() {
            dynAuthServDisconAcks.incrementAndGet();
        }

        public long getDynAuthServDisconAuthOnlyRequests() {
            return dynAuthServDisconAuthOnlyRequests.get();
        }

        public void incrementDynAuthServDisconAuthOnlyRequests() {
            dynAuthServDisconAuthOnlyRequests.incrementAndGet();
        }

        public long getDynAuthServDisconBadAuthenticators() {
            return dynAuthServDisconBadAuthenticators.get();
        }

        public void incrementDynAuthServDisconBadAuthenticators() {
            dynAuthServDisconBadAuthenticators.incrementAndGet();
        }

        public long getDynAuthServDisconNakAuthOnlyRequests() {
            return dynAuthServDisconNakAuthOnlyRequests.get();
        }

        public void incrementDynAuthServDisconNakAuthOnlyRequests() {
            dynAuthServDisconNakAuthOnlyRequests.incrementAndGet();
        }

        public long getDynAuthServDisconNakSessNoContext() {
            return dynAuthServDisconNakSessNoContext.get();
        }

        public void incrementDynAuthServDisconNakSessNoContext() {
            dynAuthServDisconNakSessNoContext.incrementAndGet();
        }

        public long getDynAuthServDisconNaks() {
            return dynAuthServDisconNaks.get();
        }

        public void incrementDynAuthServDisconNaks() {
            dynAuthServDisconNaks.incrementAndGet();
        }

        public long getDynAuthServDisconPacketsDropped() {
            return dynAuthServDisconPacketsDropped.get();
        }

        public void incrementDynAuthServDisconPacketsDropped() {
            dynAuthServDisconPacketsDropped.incrementAndGet();
        }

        public long getDynAuthServDisconRequests() {
            return dynAuthServDisconRequests.get();
        }

        public void incrementDynAuthServDisconRequests() {
            dynAuthServDisconRequests.incrementAndGet();
        }

        public long getDynAuthServDisconUserSessRemoved() {
            return dynAuthServDisconUserSessRemoved.get();
        }

        public void incrementDynAuthServDisconUserSessRemoved() {
            dynAuthServDisconUserSessRemoved.incrementAndGet();
        }

        public long getDynAuthServDupCoARequests() {
            return dynAuthServDupCoARequests.get();
        }

        public void incrementDynAuthServDupCoARequests() {
            dynAuthServDupCoARequests.incrementAndGet();
        }

        public long getDynAuthServDupDisconRequests() {
            return dynAuthServDupDisconRequests.get();
        }

        public void incrementDynAuthServDupDisconRequests() {
            dynAuthServDupDisconRequests.incrementAndGet();
        }

        public long getDynAuthServMalformedCoARequests() {
            return dynAuthServMalformedCoARequests.get();
        }

        public void incrementDynAuthServMalformedCoARequests() {
            dynAuthServMalformedCoARequests.incrementAndGet();
        }

        public long getDynAuthServMalformedDisconRequests() {
            return dynAuthServMalformedDisconRequests.get();
        }

        public void incrementDynAuthServMalformedDisconRequests() {
            dynAuthServMalformedDisconRequests.incrementAndGet();
        }

        public long getDynAuthServUnknownTypes() {
            return dynAuthServUnknownTypes.get();
        }

        public void incrementDynAuthServUnknownTypes() {
            dynAuthServUnknownTypes.incrementAndGet();
        }

        public long getDynAuthServerCounterDiscontinuity() {
            return dynAuthServerCounterDiscontinuity.get();
        }

        public void incrementDynAuthServerCounterDiscontinuity() {
            dynAuthServerCounterDiscontinuity.incrementAndGet();
        }

        /**
        SYNTAX       INTEGER {
        unknown(0), ipv4(1),ipv6(2),ipv4z(3),ipv6z(4), dns(16)
        }
         */
        private static class InetAddressType {

            private static final int UNKNOWN = 0;
            private static final int IPv4 = 1;
            private static final int IPv6 = 2;
            private static final int IPv4Z = 3;
            private static final int IPv6Z = 4;
            private static final int DNS = 16;
        }

        public void setDynAuthServUnknownTypes(long counterDiscontinuity) {
            this.dynAuthServerCounterDiscontinuity.set(counterDiscontinuity);
        }
     
    }
	
	

}
