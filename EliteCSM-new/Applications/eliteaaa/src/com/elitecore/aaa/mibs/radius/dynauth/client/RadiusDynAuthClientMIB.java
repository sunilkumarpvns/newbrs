package com.elitecore.aaa.mibs.radius.dynauth.client;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.TableRadiusDynAuthServerTable;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

/**
 *
 * @author raghug
 */
public class RadiusDynAuthClientMIB {

    private static HashMap<String, DynAuthServerEntry> dynAuthServerEntryMap = new HashMap<String, DynAuthServerEntry>();
    private static LinkedHashMap<String, String> serverMap;
    
    private static AtomicLong dynAuthClientCoAInvalidServerAddresses;
    private static AtomicLong dynAuthClientInvalidServerAddresses;
    
    private AAAServerContext serverContext;


    private static boolean isInitialized;
	private static TableRadiusDynAuthServerTable dynAuthServerTable;
    
    public RadiusDynAuthClientMIB(AAAServerContext serverContext) {
    	this.serverContext = serverContext;
    	this.dynAuthClientCoAInvalidServerAddresses = new AtomicLong(0);
    	this.dynAuthClientInvalidServerAddresses = new AtomicLong(0);
	}
    
    public  void init() {
        if (!isInitialized) {

            serverMap = new LinkedHashMap<String, String>();

            List<DefaultExternalSystemData> esiList  = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESConfiguration.RadESTypeConstants.NAS.type) ;
			int serverIndexCounter = 0;
			if(esiList!=null){
				int size = esiList.size();
				for(int i=0;i<size;i++){
					DefaultExternalSystemData externalSystem = esiList.get(i);
					if(externalSystem!=null){
						String serverAddress = String.valueOf(externalSystem.getIPAddress().getHostAddress());
						if (serverAddress != null && serverAddress.trim().length() > 0) {
							serverIndexCounter++;
							serverAddress = serverAddress.trim();
							serverMap.put(String.valueOf(serverIndexCounter), externalSystem.getName());
							dynAuthServerEntryMap.put(externalSystem.getName(), new DynAuthServerEntry(serverIndexCounter, serverAddress,externalSystem.getPort(),externalSystem.getName()));
						}
					}	
				}
			}
            isInitialized = true;
        }
    }

    public String getClientIdentifier() {
        return "EliteRadius DynAuth Client";
    }

    public static HashMap<String, String> getServerMap() {
        return serverMap;
    }

    /**
     *
     * @param serverAddress - IP Address of the server
     * @return serverIndex  - Index of the server in the server list.
     */
    public static long getRadiusDynAuthServerIndex(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthServerIndex();
        }

        return 0;
    }

    /**
     * Returns the InetAddressType of the server address
     * @param serverAddress   IP Address of the server
     * @return InetAddressType  -  The type of the InetAddress
     */
    public static int getRadiusDynAuthServerAddressType(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthServerAddressType();
        }
        return 0;
    }

    /**
     * Returns the InetAddress of the server
     * @param  serverAddress   IP Address of the server
     * @return serverAddress  -  The InetAddress of the string
     */
    public static String getRadiusDynAuthServerAddress(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthServerAddress();
        }
        return null;
    }

    /**
     *  Returns the portno
     * @param  serverAddress   IP Address of the server
     * @return serverClientPortNumber  -  The port no of the server client
     */
    public static long getRadiusDynAuthServerClientPortNo(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthServerClientPortNumber();
        }
        return 0;
    }

    /**
     *  Returns the serverID of the given server
     * @param  serverAddress   IP Address of the server
     * @return serverID  -  The id of the server
     */
    public static String getRadiusDynAuthServerID(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthServerID();
        }
        return null;
    }

    /**
     *  Sets the round trip time of the client
     * @param serverAddress
     * @param roundTripTime
     */
    public static void setRadiusDynAuthClientRoundTripTime(String serverAddress, long roundTripTime) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.setDynAuthClientRoundTripTime(roundTripTime);
        }
    }

    /**
     *  Returns the client round trip time
     * @param  serverAddress   IP Address of the server
     * @return clientRounfTripTime  -  The round trip time
     */
    public static long getRadiusDynAuthClientRoundTripTime(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientRoundTripTime();
        }
        return 0;
    }

    /**
     *  Returns the total no. of disconnect requests sent to the server
     * @param  serverAddress IP Address of the server
     * @return disconRequests - Total no. of disconnect requests.
     */
    public static long getRadiusDynAuthClientDisconRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconRequests();
        }
        return 0;
    }


    /**
     *  Returns the total no. of Authorize only disconnect requests sent to the server
     * @param  serverAddress   IP Address of the server
     * @return authOnlyDisconRequests  - Total no. of Authorize only disconnect requests.
     */
    public static long getRadiusDynAuthClientDisconAuthOnlyRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconAuthOnlyRequests();
        }
        return 0;
    }
    
    


    /**
     * Returns the total no. disconnect requests retransmitted
     * @param  serverAddress   IP Address of the server
     * @return disconRetransmissions  - Total no. disconnect requests retransmitted.
     */
    public static long getRadiusDynAuthClientDisconRetransmissions(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconRetransmissions();
        }
        return 0;
    }

    /**
     * Increments the no. disconnect requests retransmitted
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientDisconRetransmissions(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconRetransmissions();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconTimeouts(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconTimeouts();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoATimeouts(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientCoATimeouts();
        }
    }



    /**
     * Returns the total no. disconnect Acks received
     * @param  serverAddress   IP Address of the server
     * @return disconAcks  - Total no. disconnect Acks received.
     */
    public static long getRadiusDynAuthClientDisconAcks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconAcks();
        }
        return 0;
    }

   
     /**
     * Returns the total no. disconnect Naks received
     * @param  serverAddress   IP Address of the server
     * @return disconNaks  - Total no. disconnect Naks received.
     */
    public static long getRadiusDynAuthClientDisconNaks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconNaks();
        }
        return 0;
    }

    
    
     /**
     * Returns the total no. disconnect Naks Auth Only received
     * @param  serverAddress   IP Address of the server
     * @return disconNakAuthOnly  - Total no. disconnect Auth Only Naks received.
     */
    public static long getRadiusDynAuthClientDisconNakAuthOnlyRequest(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconNakAuthOnlyRequest();
        }
        return 0;
    }

    /**
     * Increments the no. disconnect Naks Auth Only received
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientDisconNakAuthOnlyRequest(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
             dynAuthServerEntry.incrementDynAuthClientDisconNakAuthOnlyRequest();
        }
    }
    
    
    public static void incrementRadiusDynAuthClientCoARequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
             dynAuthServerEntry.incrementDynAuthClientCoARequests();
        }
    }     
    
    
     /**
     * Returns the total no. disconnect NakSessNoContext
     * @param  serverAddress   IP Address of the server
     * @return disconNakSessNoContext  - Total no. disconnect NakSessNoContext received.
     */
    public static long getRadiusDynAuthClientDisconNakSessNoContext(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconNakSessNoContext();
        }
        return 0;
    }

    /**
     * Increments the no. of NakSessNoContext
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientDisconNakSessNoContext(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconNakSessNoContext();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconNaks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconNaks();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconPacketsDropped(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconPacketsDropped();
        }
    }
    public static void incrementRadiusDynAuthClientCoAPacketsDropped(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientCoAPacketsDropped();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconPendingRequests();
        }
    }
    
    public static void decrementRadiusDynAuthClientDisconPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.decrementDynAuthClientDisconPendingRequests();
        }
    }
    
    public static void decrementRadiusDynAuthClientCoAPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.decrementDynAuthClientCoAPendingRequests();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconRequests();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconAuthOnlyRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
               dynAuthServerEntry.incrementDynAuthClientDisconAuthOnlyRequests();
        }
    }
    
      /**
     * Returns the total no. of malformed disconnect responses
     * @param  serverAddress   IP Address of the server
     * @return malformedResponses - Total no.of malformed disconnect responses
     */
    public static long getRadiusDynAuthClientMalformedDisconResponses(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientMalformedDisconResponses();
        }
        return 0;
    }

    /**
     * Increments the  no. of malformed disconnect responses
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientMalformedDisconResponses(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            dynAuthServerEntry.incrementDynAuthClientMalformedDisconResponses();
        }
    }

    /**
     * Returns the total no. of disconnect requests with bad authenticator
     * @param  serverAddress   IP Address of the server
     * @return badAuthenticatorRequests - Total no.of disconnect requests with bad authenticator
     */

    public static long getRadiusDynAuthClientDisconBadAuthenticators(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconBadAuthenticators();
        }
        return 0;
    }

    /**
     * Increments the  total no. of disconnect requests with bad authenticator
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientDisconBadAuthenticators(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            dynAuthServerEntry.incrementDynAuthClientDisconBadAuthenticators();
        }
    }     

    /**
     * Returns the total no. of pending disconnect requests
     * @param  serverAddress   IP Address of the server
     * @return pendingRequests - Total no.of pending disconnect requests
     */

    public static long getRadiusDynAuthClientDisconPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconPendingRequests();
        }
        return 0;
    }

    
    
   /**
     * Returns the total no. of disconnect timeouts
     * @param  serverAddress   IP Address of the server
     * @return timeoutCount - Total no.of disconnect timeouts
     */

    public static long getRadiusDynAuthClientDisconTimeouts(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconTimeouts();
        }
        return 0;
    }

  
   /**
     * Returns the total no. of dropped disconnect packets
     * @param  serverAddress   IP Address of the server
     * @return droppedCount - Total no.of dropped disconnect packets
     */

    public static long getRadiusDynAuthClientDisconPacketsDropped(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientDisconPacketsDropped();
        }
        return 0;
    }

    
    /**
     *  Returns the total no. of CoA requests sent to the server
     * @param  serverAddress   IP Address of the server
     * @return CoARequests  -  Total no. of CoA requests.
     */
    public static long getRadiusDynAuthClientCoARequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoARequests();
        }
        return 0;
    }

   

    /**
     *  Returns the total no. of Authorize only CoA requests sent to the server
     * @param  serverAddress   IP Address of the server
     * @return authOnlyDisconRequests  - Total no. of Authorize only CoA requests.
     */
    public static long getRadiusDynAuthClientCoAAuthOnlyRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoAAuthOnlyRequest();
        }
        return 0;
    }

   

      /**
     * Returns the total no. CoA requests retransmitted
     * @param  serverAddress   IP Address of the server
     * @return disconRetransmissions  - Total no. CoA requests retransmitted.
     */
    public static long getRadiusDynAuthClientCoARetransmissions(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoARetransmissions();
        }
        return 0;
    }

    /**
     * Increments the no. CoA requests retransmitted
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientCoARetransmissions(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoARetransmissions();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoAPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoAPendingRequests();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoANaks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoANaks();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoAAcks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoAAcks();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoANakAuthOnlyRequest(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoANakAuthOnlyRequest();
        }
    }


    /**
     * Returns the total no. CoA Acks received
     * @param  serverAddress   IP Address of the server
     * @return ACKs received  - Total no. CoA Acks received.
     */
    public static long getRadiusDynAuthClientCoAAcks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoAAcks();
        }
        return 0;
    }

   
     /**
     * Returns the total no. of CoA NAKs received
     * @param  serverAddress   IP Address of the server
     * @return CoANAKs  - Total no. CoA Naks received.
     */
    public static long getRadiusDynAuthClientCoANaks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoANaks();
        }
        return 0;
    }

   
     /**
     * Returns the total no. CoA Naks Auth Only received
     * @param  serverAddress   IP Address of the server
     * @return CoANakAuthOnly  - Total no. CoA Auth Only Naks received.
     */
    public static long getRadiusDynAuthClientCoANakAuthOnlyRequest(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoANakAuthOnlyRequest();
        }
        return 0;
    }


     /**
     * Returns the total no. of CoA NakSessNoContext
     * @param  serverAddress   IP Address of the server
     * @return CoANakSessNoContext  - Total no. of CoA NakSessNoContext received.
     */
    public static long getRadiusDynAuthClientCoANakSessNoContext(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoANakSessNoContext();
        }
        return 0;
    }

    /**
     * Increments the no. of CoA NakSessNoContext
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientCoANakSessNoContext(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            dynAuthServerEntry.incrementDynAuthClientCoANakSessNoContext();
        }
    }


      /**
     * Returns the total no. of malformed CoA responses
     * @param  serverAddress   IP Address of the server
     * @return malformedResponses - Total no.of malformed CoA responses
     */
    public static long getRadiusDynAuthClientMalformedCoAResponses(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientMalformedCoAResponses();
        }
        return 0;
    }

    /**
     * Increments the  no. of malformed CoA responses
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientMalformedCoAResponses(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientMalformedCoAResponses();
        }
    }

       /**
     * Returns the total no. of CoA requests with bad authenticator
     * @param  serverAddress   IP Address of the server
     * @return badAuthenticatorRequests - Total no.of CoA requests with bad authenticator
     */

    public static long getRadiusDynAuthClientCoABadAuthenticators(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoABadAuthenticators();
        }
        return 0;
    }

    /**
     * Increments the  total no. of CoA requests with bad authenticator
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientCoABadAuthenticators(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoABadAuthenticators();
        }
    }
    
    public static void incrementRadiusDynAuthClientCoAAuthOnlyRequest(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCoAAuthOnlyRequest();
        }
    }

    /**
     * Returns the total no. of pending CoA requests
     * @param  serverAddress   IP Address of the server
     * @return pendingRequests - Total no.of pending CoA requests
     */

    public static long getRadiusDynAuthClientCoAPendingRequests(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoAPendingRequests();
        }
        return 0;
    }

   /**
     * Returns the total no. of CoA timeouts
     * @param  serverAddress   IP Address of the server
     * @return timeoutCount - Total no.of CoA timeouts
     */

    public static long getRadiusDynAuthClientCoATimeouts(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoATimeouts();
        }
        return 0;
    }


   /**
     * Returns the total no. of dropped CoA packets
     * @param  serverAddress   IP Address of the server
     * @return droppedCount - Total no.of dropped CoA packets
     */

    public static long getRadiusDynAuthClientCoAPacketsDropped(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCoAPacketsDropped();
        }
        return 0;
    }

   

   /**
     * Returns the total no. of unknown requests received
     * @param  serverAddress   IP Address of the server
     * @return unknownCount - Total no.of unknown requests
     */

    public static long getRadiusDynAuthClientUnknownTypes(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientUnknownTypes();
        }
        return 0;
    }

    /**
     * Increments the  total no. of unknown requests received
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientUnknownTypes(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientUnknownTypes();
        }
    }
    
    /**
     * Returns the value of counter discontinuity
     * @param  serverAddress   IP Address of the server
     * @return counterDiscontinuity - Total no.of counter discontinuity
     */

    public static long getRadiusDynAuthClientCounterDiscontinuity(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
            return dynAuthServerEntry.getDynAuthClientCounterDiscontinuity();
        }
        return 0;
    }

    /**
     * Increments the value of counter discontinuity
     * @param serverAddress
     */
    public static void incrementRadiusDynAuthClientCounterDiscontinuity(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientCounterDiscontinuity();
        }
    }
    
    public static void incrementRadiusDynAuthClientDisconAcks(String serverAddress) {
        DynAuthServerEntry dynAuthServerEntry = dynAuthServerEntryMap.get(serverAddress);
        if (dynAuthServerEntry != null) {
        	dynAuthServerEntry.incrementDynAuthClientDisconAcks();
        }
    }

   
    public static long getDynAuthClientCoAInvalidServerAddresses() {
        return dynAuthClientCoAInvalidServerAddresses.get();
    }
    
    public static long getDynAuthClientDisconInvalidServerAddresses() {
        return dynAuthClientInvalidServerAddresses.get();
    }
    
    public static void incrementDynAuthClientCoAInvalidServerAddresses() {
         dynAuthClientCoAInvalidServerAddresses.incrementAndGet();
    }
    
    public static void incrementDynAuthDisconClientInvalidServerAddresses() {
        dynAuthClientInvalidServerAddresses.incrementAndGet();
     
    }



    // Server Entry
    private static class DynAuthServerEntry {

        private AtomicLong dynAuthServerIndex = new AtomicLong();
        private AtomicInteger dynAuthServerAddressType = new AtomicInteger();
        private String dynAuthServerAddress;
        private AtomicLong dynAuthServerClientPortNumber = new AtomicLong();
        private String dynAuthServerID;
        private AtomicLong dynAuthClientRoundTripTime = new AtomicLong();
        private AtomicLong dynAuthClientDisconRequests = new AtomicLong();
        private AtomicLong dynAuthClientDisconAuthOnlyRequests = new AtomicLong();
        private AtomicLong dynAuthClientDisconRetransmissions = new AtomicLong();
        
        private AtomicLong dynAuthClientDisconAcks = new AtomicLong();
        private AtomicLong dynAuthClientDisconNaks = new AtomicLong();
        private AtomicLong dynAuthClientDisconNakAuthOnlyRequest = new AtomicLong();
        private AtomicLong dynAuthClientDisconNakSessNoContext = new AtomicLong();
        private AtomicLong dynAuthClientMalformedDisconResponses = new AtomicLong();
        private AtomicLong dynAuthClientDisconBadAuthenticators = new AtomicLong();
        private AtomicLong dynAuthClientDisconPendingRequests = new AtomicLong();
        
        private AtomicLong dynAuthClientDisconTimeouts = new AtomicLong();
        
        private AtomicLong dynAuthClientDisconPacketsDropped = new AtomicLong();
        
        private AtomicLong dynAuthClientCoARequests = new AtomicLong();
        private AtomicLong dynAuthClientCoAAuthOnlyRequest = new AtomicLong();
        
        private AtomicLong dynAuthClientCoARetransmissions = new AtomicLong();
        private AtomicLong dynAuthClientCoAAcks = new AtomicLong();
        private AtomicLong dynAuthClientCoANaks = new AtomicLong();
        
        private AtomicLong dynAuthClientCoANakAuthOnlyRequest = new AtomicLong();
        private AtomicLong dynAuthClientCoANakSessNoContext = new AtomicLong();
        private AtomicLong dynAuthClientMalformedCoAResponses = new AtomicLong();
        private AtomicLong dynAuthClientCoABadAuthenticators = new AtomicLong();
        private AtomicLong dynAuthClientCoAPendingRequests = new AtomicLong();
        private AtomicLong dynAuthClientCoATimeouts = new AtomicLong();
        private AtomicLong dynAuthClientCoAPacketsDropped = new AtomicLong();
        
        private AtomicLong dynAuthClientUnknownTypes = new AtomicLong();
        
        private AtomicLong dynAuthClientCounterDiscontinuity = new AtomicLong();
        private String name;
        


        public DynAuthServerEntry(int dynAuthServerIndex, String dynAuthServerAddress,long dynAuthServerClientPortNumber,String name) {
            this.dynAuthServerIndex.set(dynAuthServerIndex);
            this.dynAuthServerID = dynAuthServerAddress + dynAuthServerIndex;
            this.dynAuthServerAddress = dynAuthServerAddress;
            this.dynAuthServerClientPortNumber.set(dynAuthServerClientPortNumber);
            this.name = name;
            setDynAuthServerAddress(dynAuthServerAddress);
        }

        public long getDynAuthClientCoAAcks() {
            return dynAuthClientCoAAcks.get();
        }

        public void incrementDynAuthClientCoAAcks() {
            dynAuthClientCoAAcks.incrementAndGet();
        }

        public long getDynAuthClientCoAAuthOnlyRequest() {
            return dynAuthClientCoAAuthOnlyRequest.get();
        }

        public void incrementDynAuthClientCoAAuthOnlyRequest() {
            dynAuthClientCoAAuthOnlyRequest.incrementAndGet();
        }

        public long getDynAuthClientCoABadAuthenticators() {
            return dynAuthClientCoABadAuthenticators.get();
        }

        public void incrementDynAuthClientCoABadAuthenticators() {
            dynAuthClientCoABadAuthenticators.incrementAndGet();
        }

        public long getDynAuthClientCoANakAuthOnlyRequest() {
            return dynAuthClientCoANakAuthOnlyRequest.get();
        }

        public void incrementDynAuthClientCoANakAuthOnlyRequest() {
            dynAuthClientCoANakAuthOnlyRequest.incrementAndGet();
        }

        public long getDynAuthClientCoANakSessNoContext() {
            return dynAuthClientCoANakSessNoContext.get();
        }

        public void incrementDynAuthClientCoANakSessNoContext() {
            dynAuthClientCoANakSessNoContext.incrementAndGet();
        }

        public long getDynAuthClientCoANaks() {
            return dynAuthClientCoANaks.get();
        }

        public void incrementDynAuthClientCoANaks() {
            dynAuthClientCoANaks.incrementAndGet();
        }

        public long getDynAuthClientCoAPacketsDropped() {
            return dynAuthClientCoAPacketsDropped.get();
        }

        public void incrementDynAuthClientCoAPacketsDropped() {
            this.dynAuthClientCoAPacketsDropped.incrementAndGet();
        }

        public long getDynAuthClientCoAPendingRequests() {
            return dynAuthClientCoAPendingRequests.get();
        }

        public void incrementDynAuthClientCoAPendingRequests() {
            this.dynAuthClientCoAPendingRequests.incrementAndGet();
        }

        public void decrementDynAuthClientCoAPendingRequests() {
        	if (dynAuthClientCoAPendingRequests.get() > 0 ) {
        		dynAuthClientCoAPendingRequests.decrementAndGet();
            }
        }

        public long getDynAuthClientCoARequests() {
            return dynAuthClientCoARequests.get();
        }

        public void incrementDynAuthClientCoARequests() {
            this.dynAuthClientCoARequests.incrementAndGet();
        }

        public long getDynAuthClientCoARetransmissions() {
            return dynAuthClientCoARetransmissions.get();
        }

        public void incrementDynAuthClientCoARetransmissions() {
            this.dynAuthClientCoARetransmissions.incrementAndGet();
        }

        public long getDynAuthClientCoATimeouts() {
            return dynAuthClientCoATimeouts.get();
        }

        public void incrementDynAuthClientCoATimeouts() {
            this.dynAuthClientCoATimeouts.incrementAndGet();
        }

        public long getDynAuthClientCounterDiscontinuity() {
            return dynAuthClientCounterDiscontinuity.get();
        }

        public void incrementDynAuthClientCounterDiscontinuity() {
            this.dynAuthClientCounterDiscontinuity.incrementAndGet();
        }

        public long getDynAuthClientDisconAcks() {
            return dynAuthClientDisconAcks.get();
        }

        public void incrementDynAuthClientDisconAcks() {
            dynAuthClientDisconAcks.incrementAndGet();
        }

        public long getDynAuthClientDisconAuthOnlyRequests() {
            return dynAuthClientDisconAuthOnlyRequests.get();
        }

        public void incrementDynAuthClientDisconAuthOnlyRequests() {
            dynAuthClientDisconAuthOnlyRequests.incrementAndGet();
        }

        public long getDynAuthClientDisconBadAuthenticators() {
            return dynAuthClientDisconBadAuthenticators.get();
        }

        public void incrementDynAuthClientDisconBadAuthenticators() {
            dynAuthClientDisconBadAuthenticators.incrementAndGet();
        }

        public long getDynAuthClientDisconNakAuthOnlyRequest() {
            return dynAuthClientDisconNakAuthOnlyRequest.get();
        }

        public void incrementDynAuthClientDisconNakAuthOnlyRequest() {
            dynAuthClientDisconNakAuthOnlyRequest.incrementAndGet();
        }

        public long getDynAuthClientDisconNakSessNoContext() {
            return dynAuthClientDisconNakSessNoContext.get();
        }

        public void incrementDynAuthClientDisconNakSessNoContext() {
            dynAuthClientDisconNakSessNoContext.incrementAndGet();
        }

        public long getDynAuthClientDisconNaks() {
            return dynAuthClientDisconNaks.get();
        }

        public void incrementDynAuthClientDisconNaks() {
            dynAuthClientDisconNaks.incrementAndGet();
        }

        public long getDynAuthClientDisconPacketsDropped() {
            return dynAuthClientDisconPacketsDropped.get();
        }

        public void incrementDynAuthClientDisconPacketsDropped() {
            dynAuthClientDisconPacketsDropped.incrementAndGet();
        }

        public long getDynAuthClientDisconPendingRequests() {
            return dynAuthClientDisconPendingRequests.get();
        }

        public void incrementDynAuthClientDisconPendingRequests() {
            dynAuthClientDisconPendingRequests.incrementAndGet();
        }

        private void decrementDynAuthClientDisconPendingRequests() {
        	if(dynAuthClientDisconPendingRequests.get() > 0) {
        		dynAuthClientDisconPendingRequests.decrementAndGet();
        }
        }

        public long getDynAuthClientDisconRequests() {
            return dynAuthClientDisconRequests.get();
        }

        public void incrementDynAuthClientDisconRequests() {
            dynAuthClientDisconRequests.incrementAndGet();
        }

        public long getDynAuthClientDisconRetransmissions() {
            return dynAuthClientDisconRetransmissions.get();
        }

        public void incrementDynAuthClientDisconRetransmissions() {
            dynAuthClientDisconRetransmissions.incrementAndGet();
        }

        public long getDynAuthClientDisconTimeouts() {
            return dynAuthClientDisconTimeouts.get();
        }

        public void incrementDynAuthClientDisconTimeouts() {
            dynAuthClientDisconTimeouts.incrementAndGet();
        }

        public long getDynAuthClientMalformedCoAResponses() {
            return dynAuthClientMalformedCoAResponses.get();
        }

        public void incrementDynAuthClientMalformedCoAResponses() {
            dynAuthClientMalformedCoAResponses.incrementAndGet();
        }

        public long getDynAuthClientMalformedDisconResponses() {
            return dynAuthClientMalformedDisconResponses.get();
        }

        public void incrementDynAuthClientMalformedDisconResponses() {
            dynAuthClientMalformedDisconResponses.incrementAndGet();
        }

        public long getDynAuthClientRoundTripTime() {
            return dynAuthClientRoundTripTime.get();
        }

        public void setDynAuthClientRoundTripTime(long dynAuthClientRoundTripTime) {
            this.dynAuthClientRoundTripTime.set(dynAuthClientRoundTripTime);
        }

        public long getDynAuthClientUnknownTypes() {
            return dynAuthClientUnknownTypes.get();
        }

        public void incrementDynAuthClientUnknownTypes() {
            this.dynAuthClientUnknownTypes.incrementAndGet();
        }

        public String getDynAuthServerAddress() {
            return dynAuthServerAddress;
        }

        public void setDynAuthServerAddress(String dynAuthServerAddress) {
            this.dynAuthServerAddress = dynAuthServerAddress;

            try {
                InetAddress inetAddress = InetAddress.getByName(dynAuthServerAddress);
                if (inetAddress instanceof Inet4Address) {
                    setDynAuthServerAddressType(InetAddressType.IPv4);
                } else if (inetAddress instanceof Inet6Address) {
                    setDynAuthServerAddressType(InetAddressType.IPv6);
                }
            } catch (UnknownHostException ex) {
                try {
                    new URL(dynAuthServerAddress);
                    setDynAuthServerAddressType(InetAddressType.IPv6);
                } catch (MalformedURLException exception) {
                    setDynAuthServerAddressType(InetAddressType.UNKNOWN);
                }
            }
        }

        public int getDynAuthServerAddressType() {
            return dynAuthServerAddressType.get();
        }

        public void setDynAuthServerAddressType(int dynAuthServerAddressType) {
            if (dynAuthServerAddressType == InetAddressType.IPv4 ||
                    dynAuthServerAddressType == InetAddressType.IPv6 ||
                    dynAuthServerAddressType == InetAddressType.IPv4Z ||
                    dynAuthServerAddressType == InetAddressType.IPv6Z ||
                    dynAuthServerAddressType == InetAddressType.DNS) {
                this.dynAuthServerAddressType.set(dynAuthServerAddressType);
            } else {
                this.dynAuthServerAddressType.set(InetAddressType.UNKNOWN);
            }
        }

        public long getDynAuthServerClientPortNumber() {
            return dynAuthServerClientPortNumber.get();
        }

        public void setDynAuthServerClientPortNumber(long dynAuthServerClientPortNumber) {
            this.dynAuthServerClientPortNumber.set(dynAuthServerClientPortNumber);
        }

        public String getDynAuthServerID() {
            return dynAuthServerID;
        }

        public long getDynAuthServerIndex() {
            return dynAuthServerIndex.get();
        }

        public void setDynAuthServerIndex(long dynAuthServerIndex) {
            this.dynAuthServerIndex.set(dynAuthServerIndex);
        }

		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			responseBuilder.append("\n    Request Summary Of ESI : "+name);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nIP                                        :" + this.dynAuthServerAddress);
			responseBuilder.append("\nPort                                      :" + this.dynAuthServerClientPortNumber);
			responseBuilder.append("\nRound Trip Time                           :" + this.dynAuthClientRoundTripTime);
			responseBuilder.append("\nDisconnect Requests                       :" + this.dynAuthClientDisconRequests);
			responseBuilder.append("\nDisconnect Auth-Only Requests             :" + this.dynAuthClientDisconAuthOnlyRequests);
			responseBuilder.append("\nDisconnect Retransmissions Requests       :" + this.dynAuthClientDisconRetransmissions);
			responseBuilder.append("\nDisconnect Acks                           :" + this.dynAuthClientDisconAcks);
			responseBuilder.append("\nDisconnect Naks                           :" + this.dynAuthClientDisconNaks);
			responseBuilder.append("\nDisconnect Nak AuthOnly Request           :" + this.dynAuthClientDisconNakAuthOnlyRequest);
			responseBuilder.append("\nDisconnect Nak Session Not Exist Request  :" + this.dynAuthClientDisconNakSessNoContext);
			responseBuilder.append("\nMalformed Disconnect Responses            :" + this.dynAuthClientMalformedDisconResponses);
			responseBuilder.append("\nBadAuthenticators Disconnect Responses    :" + this.dynAuthClientDisconBadAuthenticators);
			responseBuilder.append("\nDisconnect Pending Requests               :" + this.dynAuthClientDisconPendingRequests);
			responseBuilder.append("\nDisconnect Timeouts                       :" + this.dynAuthClientDisconTimeouts);
			responseBuilder.append("\nDisconnect Packets Dropped                :" + this.dynAuthClientDisconPacketsDropped);
			responseBuilder.append("\nCoA Requests                              :" + this.dynAuthClientCoARequests);
			responseBuilder.append("\nCoA Auth-Only Request                     :" + this.dynAuthClientCoAAuthOnlyRequest);
			responseBuilder.append("\nCoA Retransmissions                       :" + this.dynAuthClientCoARetransmissions);
			responseBuilder.append("\nCoA Acks                                  :" + this.dynAuthClientCoAAcks);
			responseBuilder.append("\nCoA Naks                                  :" + this.dynAuthClientCoANaks);
			responseBuilder.append("\nCoA Nak Auth-Only Request                 :" + this.dynAuthClientCoANakAuthOnlyRequest);
			responseBuilder.append("\nCoA Nak Session Not Exist Request         :" + this.dynAuthClientCoANakSessNoContext);
			responseBuilder.append("\nMalformed CoA Responses                   :" + this.dynAuthClientMalformedCoAResponses);
			responseBuilder.append("\nCoA BadAuthenticators                     :" + this.dynAuthClientCoABadAuthenticators);
			responseBuilder.append("\nCoA Pending Requests                      :" + this.dynAuthClientCoAPendingRequests);
			responseBuilder.append("\nCoA Timeouts                              :" + this.dynAuthClientCoATimeouts);
			responseBuilder.append("\nCoA PacketsDropped                        :" + this.dynAuthClientCoAPacketsDropped);
			responseBuilder.append("\nUnknown Types                             :" + this.dynAuthClientUnknownTypes);
			responseBuilder.append("\n----------------------------------------------------------------");
			return responseBuilder.toString();
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


    }
    /*
    RadiusDynAuthServerEntry ::= SEQUENCE {
    radiusDynAuthServerIndex                     Integer32,
    radiusDynAuthServerAddressType               InetAddressType,
    radiusDynAuthServerAddress                   InetAddress,
    radiusDynAuthServerClientPortNumber          InetPortNumber,
    radiusDynAuthServerID                        SnmpAdminString,
    radiusDynAuthClientRoundTripTime             TimeTicks,
    radiusDynAuthClientDisconRequests            Counter32,
    radiusDynAuthClientDisconAuthOnlyRequests    Counter32,
    radiusDynAuthClientDisconRetransmissions     Counter32,
    radiusDynAuthClientDisconAcks                Counter32,
    radiusDynAuthClientDisconNaks                Counter32,
    radiusDynAuthClientDisconNakAuthOnlyRequest  Counter32,
    radiusDynAuthClientDisconNakSessNoContext    Counter32,
    radiusDynAuthClientMalformedDisconResponses  Counter32,
    radiusDynAuthClientDisconBadAuthenticators   Counter32,
    radiusDynAuthClientDisconPendingRequests     Gauge32,
    radiusDynAuthClientDisconTimeouts            Counter32,
    radiusDynAuthClientDisconPacketsDropped      Counter32,
    radiusDynAuthClientCoARequests               Counter32,
    radiusDynAuthClientCoAAuthOnlyRequest        Counter32,
    radiusDynAuthClientCoARetransmissions        Counter32,
    radiusDynAuthClientCoAAcks                   Counter32,
    radiusDynAuthClientCoANaks                   Counter32,
    radiusDynAuthClientCoANakAuthOnlyRequest     Counter32,
    radiusDynAuthClientCoANakSessNoContext       Counter32,
    radiusDynAuthClientMalformedCoAResponses     Counter32,
    radiusDynAuthClientCoABadAuthenticators      Counter32,
    radiusDynAuthClientCoAPendingRequests        Gauge32,
    radiusDynAuthClientCoATimeouts               Counter32,
    radiusDynAuthClientCoAPacketsDropped         Counter32,
    radiusDynAuthClientUnknownTypes              Counter32,
    radiusDynAuthClientCounterDiscontinuity      TimeTicks
    }
    
     */
    

	public static String getSummary(String esiName) {
		if(dynAuthServerEntryMap!=null && dynAuthServerEntryMap.get(esiName)!=null){
			return dynAuthServerEntryMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}

	public static TableRadiusDynAuthServerTable getDynAuthServerTable() {
		return dynAuthServerTable;
	}
	
	public static void setDynAuthServerTable(TableRadiusDynAuthServerTable dynAuthServerTable) {
		RadiusDynAuthClientMIB.dynAuthServerTable = dynAuthServerTable;
	}
}
