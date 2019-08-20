-- Auth Service Server MIB 

create table radiusAuthServ
(
instanceID 					VARCHAR(50),
createtime					TIMESTAMP,
radiusAuthServIdent   		VARCHAR(50),
radiusAuthServUpTime  		NUMERIC(10),
radiusAuthServResetTime  	NUMERIC(10),
radiusAuthServConfigReset  	VARCHAR(50),
TotalAccessRequests     NUMERIC(20),
TotalInvalidRequests    NUMERIC(20),
TotalDupAccessRequests  NUMERIC(20),
TotalAccessAccepts      NUMERIC(20),
TotalAccessRejects      NUMERIC(20),
TotalAccessChallenges   NUMERIC(20),
TotalMalformedAccessRequests   NUMERIC(20),
TotalBadAuthenticators  NUMERIC(20),
TotalPacketsDropped     NUMERIC(20),
TotalUnknownTypes       NUMERIC(20)
);

create table radiusAuthClientTable
(
instanceID 						VARCHAR(50),
createtime						TIMESTAMP,
radiusAuthClientIndex			NUMERIC(20),
radiusAuthClientAddress			VARCHAR(50),
radiusAuthClientID				VARCHAR(50),
radiusAuthServAccessRequests	NUMERIC(20),
DupAccessRequests				NUMERIC(20),
radiusAuthServAccessAccepts		NUMERIC(20),
radiusAuthServAccessRejects		NUMERIC(20),
radiusAuthServAccessChallenges	NUMERIC(20),
MalformedAccessRequests 		NUMERIC(20),
BadAuthenticators 			NUMERIC(20),
radiusAuthServPacketsDropped	NUMERIC(20),
radiusAuthServUnknownTypes   	NUMERIC(20)
);


-- Auth Service Client MIB

create table radiusAuthClient
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
InvalidServerAddresses NUMERIC(20),
radiusAuthClientIdentifier VARCHAR(50)
);

create table radiusAuthServerTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
radiusAuthServerIndex             NUMERIC(20),
authservername 					VARCHAR(50),
radiusAuthServerAddress           VARCHAR(50), 
ClientServerPortNUMERIC  		NUMERIC(20),
radiusAuthClientRoundTripTime   NUMERIC(10),
radiusAuthClientAccessRequests  NUMERIC(20),
ClientAccessRetransmissions  	NUMERIC(20),
radiusAuthClientAccessAccepts   NUMERIC(20),
radiusAuthClientAccessRejects   NUMERIC(20),
ClientAccessChallenges       	NUMERIC(20),
ClientMalformedAccessResponses  NUMERIC(20),
ClientBadAuthenticators         NUMERIC(20),
ClientPendingRequests   NUMERIC(20),
radiusAuthClientTimeouts        NUMERIC(20),
radiusAuthClientUnknownTypes    NUMERIC(20),
radiusAuthClientPacketsDropped  NUMERIC(20)
);



-- Accouting Service Server MIB
create table radiusAccServ
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
radiusAccServIdent            VARCHAR(50),
radiusAccServUpTime           NUMERIC(10),
radiusAccServResetTime        NUMERIC(10),
radiusAccServConfigReset      VARCHAR(50),
radiusAccServTotalRequests    NUMERIC(20),
TotalInvalidRequests   NUMERIC(20),
radiusAccServTotalDupRequests  NUMERIC(20),
radiusAccServTotalResponses    NUMERIC(20),
TotalMalformedRequests NUMERIC(20),
TotalBadAuthenticators NUMERIC(20),
TotalPacketsDropped    NUMERIC(20),
radiusAccServTotalNoRecords    NUMERIC(20),
radiusAccServTotalUnknownTypes NUMERIC(20)
);

create table radiusAccClientTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
radiusAccClientIndex         NUMERIC(20),
radiusAccClientAddress       VARCHAR(50),
radiusAccClientID            VARCHAR(50),
radiusAccServPacketsDropped  NUMERIC(20), 
radiusAccServRequests        NUMERIC(20),
radiusAccServDupRequests     NUMERIC(20),
radiusAccServResponses       NUMERIC(20),
radiusAccServBadAuthenticators NUMERIC(20),
radiusAccServMalformedRequests NUMERIC(20),
radiusAccServNoRecords         NUMERIC(20),
radiusAccServUnknownTypes      NUMERIC(20)
);


-- Accounting Service Client MIB

create table radiusAccClient
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
InvalidServerAddresses NUMERIC(20),
radiusAccClientIdentifier  VARCHAR(50)
); 

create table radiusAccServerTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
radiusAccServerIndex     NUMERIC(20),
acctservername 			VARCHAR(50),
radiusAccServerAddress   VARCHAR(50),
ClientServerPortNUMERIC   NUMERIC(20),
radiusAccClientRoundTripTime    NUMERIC(10),
radiusAccClientRequests         NUMERIC(20),
radiusAccClientRetransmissions  NUMERIC(20),
radiusAccClientResponses        NUMERIC(20),
ClientMalformedResponses NUMERIC(20),
ClientBadAuthenticators  NUMERIC(20),
radiusAccClientPendingRequests NUMERIC(20),
radiusAccClientTimeouts        NUMERIC(20),
radiusAccClientUnknownTypes    NUMERIC(20),
radiusAccClientPacketsDropped  NUMERIC(20)
);

--Dynauth Service Server MIB

create table radiusDynAuthServerScalars
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
DisconInvalidClientAddresses NUMERIC(20),
CoAInvalidClientAddresses    NUMERIC(20),
radiusDynAuthServerIdentifier VARCHAR(50) 
);

create table radiusDynAuthClientTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
radiusDynAuthClientIndex       NUMERIC(20),
radiusDynAuthClientAddressType VARCHAR(50),
radiusDynAuthClientAddress     VARCHAR(50),
ServDisconRequests            NUMERIC(20),
ServDisconAuthOnlyRequests    NUMERIC(20),
ServDupDisconRequests         NUMERIC(20),
radiusDynAuthServDisconAcks   NUMERIC(20),
radiusDynAuthServDisconNaks   NUMERIC(20),
ServDisconNakAuthOnlyRequests NUMERIC(20),
ServDisconNakSessNoContext    NUMERIC(20),
ServDisconUserSessRemoved     NUMERIC(20),
ServMalformedDisconRequests   NUMERIC(20),
ServDisconBadAuthenticators   NUMERIC(20),
ServDisconPacketsDropped      NUMERIC(20),
radiusDynAuthServCoARequests  NUMERIC(20),
ServCoAAuthOnlyRequests       NUMERIC(20),
ServDupCoARequests            NUMERIC(20),
radiusDynAuthServCoAAcks      NUMERIC(20),
radiusDynAuthServCoANaks      NUMERIC(20),
ServCoANakAuthOnlyRequests    NUMERIC(20),
ServCoANakSessNoContext       NUMERIC(20),
ServCoAUserSessChanged        NUMERIC(20),
ServMalformedCoARequests      NUMERIC(20),
ServCoABadAuthenticators      NUMERIC(20),
ServCoAPacketsDropped         NUMERIC(20),
radiusDynAuthServUnknownTypes NUMERIC(20),
ServerCounterDiscontinuity    NUMERIC(10)
);

--Dynauth Service Client MIB

create table radiusDynAuthClientScalars
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
DisconInvalidServerAddresses NUMERIC(20),
CoAInvalidServerAddresses    NUMERIC(20)
);

create table radiusDynAuthServerTable
(
instanceID   VARCHAR(50),
createtime TIMESTAMP,
radiusDynAuthServerIndex 			NUMERIC(20),
dynauthservername 					VARCHAR(50),
radiusDynAuthServerAddressType		VARCHAR(50), 
radiusDynAuthServerAddress 			VARCHAR(50),
ClientPortNUMERIC VARCHAR(50), 
radiusDynAuthServerID               VARCHAR(50),
ClientRoundTripTime    NUMERIC(10), 
ClientDisconRequests          NUMERIC(20),
ClientDisconAuthOnlyRequests  NUMERIC(20), 
ClientDisconRetransmissions   NUMERIC(20), 
radiusDynAuthClientDisconAcks  NUMERIC(20), 
radiusDynAuthClientDisconNaks  NUMERIC(20), 
ClientDisconNakAuthOnlyRequest NUMERIC(20), 
ClientDisconNakSessNoContext   NUMERIC(20), 
ClientMalformedDisconResponses NUMERIC(20),  
ClientDisconBadAuthenticators  NUMERIC(20), 
ClientDisconPendingRequests  NUMERIC(20), 
ClientDisconTimeouts		  NUMERIC(20), 
ClientDisconPacketsDropped   NUMERIC(20), 
radiusDynAuthClientCoARequests NUMERIC(20), 
ClientCoAAuthOnlyRequest     NUMERIC(20), 
ClientCoARetransmissions     NUMERIC(20),
radiusDynAuthClientCoAAcks   NUMERIC(20),
radiusDynAuthClientCoANaks   NUMERIC(20),
ClientCoANakAuthOnlyRequest  NUMERIC(20),
ClientCoANakSessNoContext    NUMERIC(20),
ClientMalformedCoAResponses  NUMERIC(20),
ClientCoABadAuthenticators   NUMERIC(20),
ClientCoAPendingRequests  	 NUMERIC(20),
radiusDynAuthClientCoATimeouts NUMERIC(20),
ClientCoAPacketsDropped  	NUMERIC(20),
ClientUnknownTypes  		NUMERIC(20),
ClientCounterDiscontinuity  NUMERIC(10)
);

-- Base Diameter MIB

create table dbpLocalStats
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
dbpLocalStatsTotalMessagesIn  NUMERIC(20),
dbpLocalStatsTotalMessagesOut NUMERIC(20),
dbpLocalStatsTotalUpTime  NUMERIC(10),
dbpLocalResetTime   NUMERIC(10),
dbpLocalConfigReset VARCHAR(50)
);

create table dbpPerPeerInfoTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
dbpPerPeerInfoState            VARCHAR(50),
dbpPerPeerInfoStateDuration    NUMERIC(10),
dbpPerPeerInfoLastDiscCause    VARCHAR(50),
WhoInitDisconnect  			   VARCHAR(50),
dbpPerPeerStatsDWCurrentStatus VARCHAR(50),
StatsTimeoutConnAtmpts 	 NUMERIC(20),
dbpPerPeerStatsASRsIn    NUMERIC(20),
dbpPerPeerStatsASRsOut   NUMERIC(20),
dbpPerPeerStatsASAsIn    NUMERIC(20),
dbpPerPeerStatsASAsOut   NUMERIC(20),
dbpPerPeerStatsACRsIn    NUMERIC(20),
dbpPerPeerStatsACRsOut   NUMERIC(20),
dbpPerPeerStatsACAsIn    NUMERIC(20),
dbpPerPeerStatsACAsOut   NUMERIC(20),
dbpPerPeerStatsCERsIn    NUMERIC(20),
dbpPerPeerStatsCERsOut   NUMERIC(20),
dbpPerPeerStatsCEAsIn    NUMERIC(20),
dbpPerPeerStatsCEAsOut   NUMERIC(20),
dbpPerPeerStatsDWRsIn    NUMERIC(20),
dbpPerPeerStatsDWRsOut   NUMERIC(20),
dbpPerPeerStatsDWAsIn    NUMERIC(20),
dbpPerPeerStatsDWAsOut   NUMERIC(20),
dbpPerPeerStatsDPRsIn    NUMERIC(20),
dbpPerPeerStatsDPRsOut   NUMERIC(20),
dbpPerPeerStatsDPAsIn    NUMERIC(20),
dbpPerPeerStatsDPAsOut   NUMERIC(20),
dbpPerPeerStatsRARsIn    NUMERIC(20),
dbpPerPeerStatsRARsOut   NUMERIC(20),
dbpPerPeerStatsRAAsIn    NUMERIC(20),
dbpPerPeerStatsRAAsOut   NUMERIC(20),
dbpPerPeerStatsSTRsIn    NUMERIC(20),
dbpPerPeerStatsSTRsOut   NUMERIC(20),
dbpPerPeerStatsSTAsIn    NUMERIC(20),
dbpPerPeerStatsSTAsOut   NUMERIC(20),
dbpPerPeerInfoDWReqTimer  NUMERIC(10),
dbpPerPeerStatsRedirectEvents   NUMERIC(20),
dbpPerPeerStatsAccDupRequests   NUMERIC(20),
dbpPerPeerStatsMalformedReqsts  NUMERIC(20),
dbpPerPeerStatsAccsNotRecorded  NUMERIC(20),
dbpPerPeerStatsAccRetrans       NUMERIC(20),
dbpPerPeerStatsTotalRetrans     NUMERIC(20),
AccPendReqstsOut       NUMERIC(20),
AccReqstsDropped       NUMERIC(20),
HByHDropMessages       NUMERIC(20),
dbpPerPeerStatsEToEDupMessages  NUMERIC(20),
dbpPerPeerStatsUnknownTypes     NUMERIC(20),
dbpPerPeerStatsProtocolErrors    NUMERIC(20),
TransientFailures      NUMERIC(20),
PermanentFailures      NUMERIC(20),
dbpPerPeerStatsTransportDown     NUMERIC(20),
dbpPeerIdentity                 VARCHAR(50)
);

create table dbpRealmMessageRouteTables
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
dbpRealmMessageRouteIndex	NUMERIC(20),
dbpRealmMessageRouteRealm	VARCHAR(50),
dbpRealmMessageRouteApp		NUMERIC(20),
dbpRealmMessageRouteType	VARCHAR(50),
dbpRealmMessageRouteAction	VARCHAR(50),
dbpRealmMessageRouteACRsIn	NUMERIC(20),
dbpRealmMessageRouteACRsOut	NUMERIC(20),
dbpRealmMessageRouteACAsIn	NUMERIC(20),
dbpRealmMessageRouteACAsOut	NUMERIC(20),
dbpRealmMessageRouteRARsIn 	NUMERIC(20),
dbpRealmMessageRouteRARsOut NUMERIC(20),
dbpRealmMessageRouteRAAsIn 	NUMERIC(20),
dbpRealmMessageRouteRAAsOut NUMERIC(20),
dbpRealmMessageRouteSTRsIn 	NUMERIC(20),
dbpRealmMessageRouteSTRsOut NUMERIC(20),
dbpRealmMessageRouteSTAsIn 	NUMERIC(20),
dbpRealmMessageRouteSTAsOut NUMERIC(20),
dbpRealmMessageRouteASRsIn 	NUMERIC(20),
dbpRealmMessageRouteASRsOut NUMERIC(20),
dbpRealmMessageRouteASAsIn 	NUMERIC(20),
dbpRealmMessageRouteASAsOut NUMERIC(20),
dbpRealmMessageRouteAccRetrans 	  NUMERIC(20),
AccDupReqsts      NUMERIC(20),
PendReqstsOut     NUMERIC(20),
ReqstsDrop 	      NUMERIC(20)
);

-- Diameter CC MIB

create table dccaPerPeerStatsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
dccaPerPeerStatsCCRIn       NUMERIC(20),
dccaPerPeerStatsCCROut      NUMERIC(20),
dccaPerPeerStatsCCRDropped  NUMERIC(20),
dccaPerPeerStatsCCAIn       NUMERIC(20),
dccaPerPeerStatsCCAOut      NUMERIC(20),
dccaPerPeerStatsCCADropped  NUMERIC(20),
dccaPerPeerStatsRARIn       NUMERIC(20),
dccaPerPeerStatsRARDropped  NUMERIC(20),
dccaPerPeerStatsRAAOut      NUMERIC(20),
dccaPerPeerStatsRAADropped  NUMERIC(20),
dccaPerPeerStatsSTROut      NUMERIC(20),
dccaPerPeerStatsSTRDropped  NUMERIC(20),
dccaPerPeerStatsSTAIn       NUMERIC(20),
dccaPerPeerStatsSTADropped  NUMERIC(20),
dccaPerPeerStatsAAROut      NUMERIC(20),
dccaPerPeerStatsAARDropped  NUMERIC(20),
dccaPerPeerStatsAAAIn       NUMERIC(20),
dccaPerPeerStatsAAADropped  NUMERIC(20),
dccaPerPeerStatsASRIn       NUMERIC(20),
dccaPerPeerStatsASRDropped  NUMERIC(20),
dccaPerPeerStatsASAOut      NUMERIC(20),
dccaPerPeerStatsASADropped  NUMERIC(20),
dccaPeerIdentity            VARCHAR(50)
);

--- RM Charging Service MIB

create table chargingServMIBObjects
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
chargingServUpTime		NUMERIC(10),
chargingServiceReset	VARCHAR(50),
chargingServResetTime	NUMERIC(10),
chargingServTotalRequests  NUMERIC(20),
chargingServTotalResponses NUMERIC(20),
TotalInvalidRequests  NUMERIC(20),
chargingServTotalDupRequests	  	NUMERIC(20),	
TotalMalformedRequests	NUMERIC(20),
TotalBadAuthenticators	NUMERIC(20),
TotalPacketsDropped		NUMERIC(20),
chargingServTotalNoRecords		NUMERIC(20),
chargingServTotalUnknownTypes	NUMERIC(20),
TotalAccessRequest 	NUMERIC(20),
chargingServTotalAccessAccept	NUMERIC(20),
chargingServTotalAccessReject	NUMERIC(20),
chargingServTotalAcctRequest	NUMERIC(20),
chargingServTotalAcctResponse	NUMERIC(20),
TotalAcctStartRequest	NUMERIC(20),
TotalAcctStopRequest	NUMERIC(20),
TotalAcctUpdateRequest	NUMERIC(20)
);

create table chargingClientStatsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
chargingClientIndex  NUMERIC(20),
chargingClientID	 VARCHAR(50),
chargingClientAddress	VARCHAR(50),
chargingRequests		NUMERIC(20),
chargingResponses		NUMERIC(20),
chargingInvalidRequests	NUMERIC(20),
chargingDupRequests		NUMERIC(20),
chargingMalformedRequests	NUMERIC(20),
chargingBadAuthenticators	NUMERIC(20),
chargingPacketsDropped	NUMERIC(20),
chargingNoRecords		NUMERIC(20),
chargingUnknownTypes	NUMERIC(20),
chargingAccessRequest	NUMERIC(20),
chargingAccessAccept	NUMERIC(20),
chargingAccessReject	NUMERIC(20),
chargingAcctRequest		NUMERIC(20),
chargingAcctResponse	  NUMERIC(20),
chargingAcctStartRequest  NUMERIC(20),	
chargingAcctStopRequest	  NUMERIC(20), 	
chargingAcctUpdateRequest NUMERIC(20)	
);

---Charging Client MIB

create table chargingServerStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
chargingServerIndex  	NUMERIC(20),
chargingServerName		VARCHAR(50),
chargingServerAddress 	VARCHAR(50),
chargingRequests		NUMERIC(20),
chargingResponses		NUMERIC(20),
chargingPacketsDropped	NUMERIC(20),
chargingUnknownTypes	NUMERIC(20),
chargingRequestTimeout	NUMERIC(20),
chargingAccessRequest	NUMERIC(20),
chargingAccessAccept	NUMERIC(20),
chargingAccessReject	NUMERIC(20),
chargingAcctRequest		NUMERIC(20),
chargingAcctResponse	NUMERIC(20),
chargingAcctStartRequest	NUMERIC(20),
chargingAcctStopRequest		NUMERIC(20),
chargingAcctUpdateRequest	NUMERIC(20)
);

--- IP-POOL-SERVICE MIB

create table ipPoolServMIBObjects
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ipPoolServiceUpTime NUMERIC(20),
ipPoolServiceReset	VARCHAR(50),
ipPoolServiceResetTime NUMERIC(20),
ipPoolServTotalRequest	NUMERIC(20),
ipPoolServTotalResponses	NUMERIC(20), 	
ipPoolServTotalRequestDropped	NUMERIC(20),
TotalDuplicateRequest	NUMERIC(20),
ipPoolServTotalUnknownPacket	NUMERIC(20),
ipPoolServTotalInvalidRequest	NUMERIC(20),
TotalDiscoverRequest	NUMERIC(20),
ipPoolServTotalOfferResponse	NUMERIC(20),
TotalDeclineResponse	NUMERIC(20),
TotalAllocationRequest	NUMERIC(20),
ipPoolServTotalReleaseRequest	NUMERIC(20),
ipPoolServTotalUpdateRequest	NUMERIC(20)
);

create table ipPoolAAAClientStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ipPoolAAAClientIndex	NUMERIC(20),
aaaID			VARCHAR(50),
aaaIPAddress	VARCHAR(50),
aaaIPAddressRequest	NUMERIC(20),
aaaIPAddressResponses	NUMERIC(20),
aaaIPAddressRequestDropped	  NUMERIC(20),
aaaIPAddressDuplicateRequest  NUMERIC(20),
aaaIPAddressUnknownPacket	NUMERIC(20),
aaaIPAddressInvalidRequest	NUMERIC(20),
aaaIPAddressDiscoverRequest	NUMERIC(20),
aaaIPAddressOfferResponse	NUMERIC(20),
aaaIPAddressDeclineResponse	NUMERIC(20),
aaaIPAddressAllocationRequest	NUMERIC(20),
aaaIPAddressReleaseRequest	NUMERIC(20),
aaaIPAddressUpdateRequest	NUMERIC(20)
);

create table ipPoolNASClientStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ipPoolNASClientIndex NUMERIC(20),
nasID			VARCHAR(50),	
nasIPAddressRequest		NUMERIC(20),
nasIPAddressResponses	NUMERIC(20),
nasIPAddressRequestDropped	NUMERIC(20),
nasIPAddressUnknownPacket	NUMERIC(20),
nasIPAddressInvalidRequest	NUMERIC(20),
nasIPAddressDiscoverRequest	NUMERIC(20),
nasIPAddressOfferResponse	NUMERIC(20),
nasIPAddressDeclineResponse	NUMERIC(20),
nasIPAddressAllocationRequest	NUMERIC(20),
nasIPAddressReleaseRequest	NUMERIC(20),
nasIPAddressUpdateRequest	NUMERIC(20)
);

--- IP-POOL-CLIENT MIB

create table ipPoolClientMIBObjects
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ipPoolTotalRequest		NUMERIC(20),
ipPoolTotalResponses	NUMERIC(20),
ipPoolTotalRequestTimeOut 	NUMERIC(20),
TotalRequestRetransmission	NUMERIC(20),
ipPoolTotalDiscoverRequest	NUMERIC(20),
ipPoolTotalOfferResponse	NUMERIC(20),
ipPoolTotalDeclineResponse	NUMERIC(20),
ipPoolTotalAllocationRequest	NUMERIC(20),
ipPoolTotalAllocationResponse	NUMERIC(20),
ipPoolTotalReleaseRequest	NUMERIC(20),
ipPoolTotalUpdateRequest	NUMERIC(20)
);

create table ipPoolServerStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ipPoolServerIndex	NUMERIC(20),   	
ipPoolServerName	VARCHAR(50),
ipPoolServerAddress	VARCHAR(50),
ipPoolServRequest	NUMERIC(20),
ipPoolServResponses	NUMERIC(20),
ipPoolServRequestTimeOut	NUMERIC(20),
ServRequestRetransmission	NUMERIC(20),
ipPoolServDiscoverRequest	NUMERIC(20),
ipPoolServOfferResponse		NUMERIC(20),
ipPoolServDeclineResponse	NUMERIC(20),
ipPoolServAllocationRequest	NUMERIC(20),
ipPoolServAllocationResponse	NUMERIC(20),
ipPoolServReleaseRequest	NUMERIC(20),
ipPoolServUpdateRequest	NUMERIC(20)
);

--- LOCAL-SESSION-MANAGER-MIB ---

create table localSessionManagerStatsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
smIndex	NUMERIC(20),
smName	VARCHAR(50),
smTotalSessionCreationFailure	NUMERIC(20),
smTotalSessionUpdationFailure	NUMERIC(20),
smTotalSessionDeletionFailure	NUMERIC(20),
smTotalActiveSession	NUMERIC(20),
smTotalInActiveSession	NUMERIC(20),
smTotalOverridedSession	NUMERIC(20),
smTotalTimedOutSession	NUMERIC(20),
smStatsResetTime	NUMERIC(10),
smTotalRequestRx	NUMERIC(20),
smTotalAccessRequestRx	NUMERIC(20),
smTotalAccessRejectTx	NUMERIC(20),
smTotalAcctRequestRx	NUMERIC(20),
smTotalAcctStartRequestRx	NUMERIC(20),
smTotalAcctStopRequestRx	NUMERIC(20),
smTotalAcctUpdateRequestRx	NUMERIC(20),
smTotalAcctStopRequestTx	NUMERIC(20),
smTotalAcctStopResponseRx	NUMERIC(20),
TotalAcctStopRequestTimeouts	NUMERIC(20),
TotalAcctStopResponseDropped	NUMERIC(20),
smTotalDisconnectRequest	NUMERIC(20),
smTotalDisconnectAck	NUMERIC(20),
smTotalDisconnectNAck	NUMERIC(20),
DisconnectRequestTimeouts	NUMERIC(20),
DisconnectResponseDropped	NUMERIC(20),
NAckResidualSessCtxRemoved	NUMERIC(20),
NAckInvalidEAPPacket	NUMERIC(20),
NAckUnsupporteAttribute	NUMERIC(20),
NAckMissingAttribute	NUMERIC(20),
NAckNASIdentificationMismatch	NUMERIC(20),
NAckInvalidRequest		NUMERIC(20),
NAckUnsupportedService	NUMERIC(20),
NAckUnsupportedExtension	NUMERIC(20),
NAckAdministrativelyProhibit	NUMERIC(20),
NAckRequestNotRoutable	NUMERIC(20),
NAckSessionCtxNotFound	NUMERIC(20),
NAckSessionCtxNotRemovable	NUMERIC(20),
NAckOtherProxyProcessingError	NUMERIC(20),
NAckResourcesUnavailable	NUMERIC(20),
NAckRequestInitiated	NUMERIC(20)
);

--- REMOTE-SESSION-MANAGER-MIB ---

create table remoteSessionManagerStatTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
remoteSMName VARCHAR(50),
remoteSMIPAddress	VARCHAR(50),
remoteSMRequestRx	NUMERIC(20),
remoteSMResponsesTx	NUMERIC(20),
remoteSMRequestDropped	NUMERIC(20),
remoteSMUnknownRequestType	NUMERIC(20),
remoteSMTimeoutRequest	NUMERIC(20),
remoteSMAccessRequestRx	NUMERIC(20),
remoteSMAccessAcceptTx	NUMERIC(20),
remoteSMAccessRejectTx	NUMERIC(20),
remoteSMAcctRequestRx	NUMERIC(20),
remoteSMAcctResponseTx	NUMERIC(20),
remoteSMAcctStartRequestRx	NUMERIC(20),
remoteSMAcctUpdateRequestRx	NUMERIC(20),
remoteSMAcctStopRequestRx	NUMERIC(20)
);

---JVM MIB

---Thread Statistics

create table jvmThreading
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
jvmThreadCount 	NUMERIC(20),
jvmThreadDaemonCount	NUMERIC(20),
jvmThreadPeakCount		NUMERIC(20),
jvmThreadTotalStartedCount	NUMERIC(20)
);


create table jvmThreadInstanceTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
jvmThreadInstIndex VARCHAR(50),
jvmThreadInstId	NUMERIC(20),
jvmThreadInstState	VARCHAR(50),
jvmThreadInstBlockCount		NUMERIC(20),
jvmThreadInstBlockTimeMs	NUMERIC(20),
jvmThreadInstWaitCount	NUMERIC(20),
jvmThreadInstWaitTimeMs	NUMERIC(20),
jvmThreadInstCpuTimeNs	NUMERIC(20),
jvmThreadInstName		VARCHAR(50),
jvmThreadInstLockName	VARCHAR(100),
jvmThreadInstLockOwnerPtr VARCHAR(50),
jvmThreadLockedMonitor  VARCHAR(1023),
jvmThreadLockInfo  VARCHAR(1023),
JVMTHREADINSTCPUTIMENS_MINVAL NUMERIC(20,0),
JVMTHREADINSTCPUTIMENS_MAXVAL NUMERIC(20,0)
);

create table jvmMemory
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
jvmMemoryPendingFinalCount	NUMERIC(20), 
jvmMemoryHeapInitSize	NUMERIC(20),
jvmMemoryHeapUsed		NUMERIC(20),
jvmMemoryHeapCommitted	NUMERIC(20),
jvmMemoryHeapMaxSize	NUMERIC(20),
jvmMemoryNonHeapInitSize	NUMERIC(20),
jvmMemoryNonHeapUsed	NUMERIC(20),
jvmMemoryNonHeapCommitted	NUMERIC(20),
jvmMemoryNonHeapMaxSize	NUMERIC(20),
JVMMEMORYHEAPUSED_MINVAL NUMERIC(20,0),
JVMMEMORYHEAPUSED_MAXVAL NUMERIC(20,0),
JVMMEMORYNONHEAPUSED_MINVAL NUMERIC(20,0),
JVMMEMORYNONHEAPUSED_MAXVAL NUMERIC(20,0)
);

create table jvmMemManagerTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
jvmMemManagerIndex	NUMERIC(20),
jvmMemManagerName	VARCHAR(50),
jvmMemManagerState	VARCHAR(50)
);

create table jvmMemPoolTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
jvmMemPoolIndex	NUMERIC(20),
jvmMemPoolName	VARCHAR(50),
jvmMemPoolType	VARCHAR(50),
jvmMemPoolState	VARCHAR(50),
JvmMemPoolPeakReset NUMERIC(20),
jvmMemPoolInitSize	NUMERIC(20),	
jvmMemPoolUsed	NUMERIC(20),
jvmMemPoolCommitted	NUMERIC(20),
jvmMemPoolMaxSize	NUMERIC(20),
jvmMemPoolPeakUsed	NUMERIC(20),
jvmMemPoolPeakCommitted	NUMERIC(20),
jvmMemPoolPeakMaxSize	NUMERIC(20),
jvmMemPoolCollectUsed	NUMERIC(20),
jvmMemPoolCollectCommitted	NUMERIC(20),
jvmMemPoolCollectMaxSize	NUMERIC(20),
JvmMemPoolThreshold 	NUMERIC(20),
jvmMemPoolThreshdCount	NUMERIC(20),
jvmMemPoolThreshdSupport	VARCHAR(50),
JvmMemPoolCollectThreshold NUMERIC(20),
jvmMemPoolCollectThreshdCount	NUMERIC(20),
MemPoolCollectThreshdSupport	VARCHAR(50),
jvmSystemLoadAverage NUMERIC(7,2),
JVMSYSTEMLOADAVERAGE_MINVAL NUMERIC(20,0),
JVMSYSTEMLOADAVERAGE_MAXVAL NUMERIC(20,0)
);

create table memory
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
memIndex 	NUMERIC(20),
memErrorName	VARCHAR(50),
memTotalSwap	NUMERIC(20),	
memAvailSwap	NUMERIC(20),
memTotalReal	NUMERIC(20),
memAvailReal	NUMERIC(20),
memTotalSwapTXT	NUMERIC(20),
memAvailSwapTXT	NUMERIC(20),
memTotalRealTXT	NUMERIC(20),
memAvailRealTXT	NUMERIC(20),
memTotalFree	NUMERIC(20),
memMinimumSwap	NUMERIC(20),
memShared	NUMERIC(20),
memBuffer	NUMERIC(20),
memCached	NUMERIC(20),
memUsedSwapTXT	NUMERIC(20),
memUsedRealTXT	NUMERIC(20),
memSwapError	VARCHAR(50),
memSwapErrorMsg	VARCHAR(200)
);

create table systemStats
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
ssIndex	NUMERIC(20),	
ssErrorName	VARCHAR(50),
ssSwapIn	NUMERIC(20),
ssSwapOut	NUMERIC(20),
ssIOSent	NUMERIC(20),
ssIOReceive	NUMERIC(20),
ssSysInterrupts	NUMERIC(20),
ssSysContext	NUMERIC(20),
ssCpuUser	NUMERIC(20),
ssCpuSystem	NUMERIC(20),
ssCpuIdle	NUMERIC(20),
ssCpuRawUser	NUMERIC(20),
ssCpuRawNice	NUMERIC(20),
ssCpuRawSystem	NUMERIC(20),
ssCpuRawIdle	NUMERIC(20),
ssCpuRawWait	NUMERIC(20),
ssCpuRawKernel	NUMERIC(20),
ssCpuRawInterrupt	NUMERIC(20),
ssIORawSent	NUMERIC(20),
ssIORawReceived	NUMERIC(20),
ssRawInterrupts	NUMERIC(20),
ssRawContexts	NUMERIC(20),
ssCpuRawSoftIRQ	NUMERIC(20),
ssRawSwapIn	NUMERIC(20),
ssRawSwapOut	NUMERIC(20),
ssCpuRawSteal	NUMERIC(20),
ssCpuRawGuest	NUMERIC(20),
ssCpuRawGuestNice	NUMERIC(20)
);

create table laTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
laIndex	NUMERIC(20),	
laNames	VARCHAR(50),
laLoad	VARCHAR(50),
laConfig	VARCHAR(50),
laLoadInt	NUMERIC(20),
laLoadFloat	NUMERIC(7,2),
laErrorFlag	VARCHAR(50),
laErrMessage	VARCHAR(200)
);

create table dskTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
dskIndex	NUMERIC(20),
dskPath		VARCHAR(50),
dskDevice	VARCHAR(50),
dskMinimum	NUMERIC(20),
dskMinPercent	NUMERIC(20),
dskTotal	NUMERIC(20),
dskAvail	NUMERIC(20),
dskUsed		NUMERIC(20),
dskPercent	NUMERIC(20),
dskPercentNode	NUMERIC(20),
dskTotalLow	NUMERIC(20),
dskTotalHigh	NUMERIC(20),
dskAvailLow	NUMERIC(20),
dskAvailHigh	NUMERIC(20),
dskUsedLow	NUMERIC(20),
dskUsedHigh	NUMERIC(20),
dskErrorFlag	VARCHAR(50),
dskErrorMsg	VARCHAR(200)
);

--- CUSTOM DIAMETER MIB

create table diameterStack
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
stackIdentity VARCHAR(50),
stackURI	VARCHAR(50),
stackRealm	VARCHAR(50),
stackUpTime	NUMERIC(10),
stackResetTime NUMERIC(10),
stackConfigReset VARCHAR(50),
stackIPAddress VARCHAR(50),
stackListeningPort NUMERIC(10)
);

create table stackStatistics
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
totalRequestRx NUMERIC(20),
totalRequestTx NUMERIC(20),
totalAnswerRx NUMERIC(20),
totalAnswerTx NUMERIC(20),
totalAnswerDr NUMERIC(20),
totalRequestDr NUMERIC(20),
totalRequestRetransmitted NUMERIC(20),
totalUnknownHbHAnswerDropped NUMERIC(20),
totalDuplicateRequest NUMERIC(20),
totalDuplicateEtEAnswer NUMERIC(20),
totalMalformedPacketRx NUMERIC(20),
totalRequestTimeOut NUMERIC(20),
totalRequestPn NUMERIC(20)
);

create table appStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
applicationID NUMERIC(20),
appStatsRequestRx NUMERIC(20),
appStatsAnswerTx NUMERIC(20),
appStatsRequestTx NUMERIC(20),
appStatsAnswerRx NUMERIC(20),
appStatsRequestRetransmitted NUMERIC(20),
appStatsRequestTimeOut NUMERIC(20),
appStatsRequestDr NUMERIC(20),
appStatsAnswerDr NUMERIC(20),
UnknownHbHAnswerDropped NUMERIC(20),
appStatsDuplicateRequest NUMERIC(20),
appStatsDuplicateEtEAnswer NUMERIC(20),
appStatsMalformedPacketRx NUMERIC(20),
appStatsRequestPn NUMERIC(20)
);

create table peerInfoTable 
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
peerIndex NUMERIC(20),
peerIdentity VARCHAR(50),
peerTransportProtocol VARCHAR(50),
peerSecurity	VARCHAR(50),
connectionInitByPeer VARCHAR(50)
);

create table peerIpAddrTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
peerIndex NUMERIC(20),
peerIpAddressIndex NUMERIC(20),
peerRemoteIpAddress VARCHAR(50),
peerLocalIpAddress VARCHAR(50),
peerStatus VARCHAR(50),
reconnectionCount NUMERIC(10)
);

create table appWiseStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
peerIndex NUMERIC(20),
applicationID NUMERIC(20),
appwRequestRx NUMERIC(20),
appwAnswerTx NUMERIC(20),
appwRequestTx NUMERIC(20),
appwAnswerRx NUMERIC(20),
appwRequestRetransmitted NUMERIC(20),
appwRequestTimeOut NUMERIC(20),
appwRequestDr NUMERIC(20),
appwAnswerDr NUMERIC(20),
appwUnknownHbHAnswerDropped NUMERIC(20),
appwDuplicateRequest NUMERIC(20),
appwDuplicateEtEAnswer NUMERIC(20),
appwMalformedPacketRx NUMERIC(20),
appwRequestPn NUMERIC(20)
);

create table commandCodeStatisticsTable
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
peerIndex NUMERIC(20),
applicationID NUMERIC(20),
commandCode NUMERIC(20),
commandCodeName VARCHAR(50),
ccwRequestRx NUMERIC(20),
ccwAnswerTx NUMERIC(20),
ccwRequestTx NUMERIC(20),
ccwAnswerRx NUMERIC(20),
ccwRequestRetransmitted NUMERIC(20),
ccwRequestTimeOut NUMERIC(20),
ccwRequestDr NUMERIC(20),
ccwAnswerDr NUMERIC(20),
ccwUnknownHbHAnswerDropped NUMERIC(20),
ccwDuplicateRequest NUMERIC(20),
ccwDuplicateEtEAnswer NUMERIC(20),
ccwMalformedPacketRx NUMERIC(20),
ccwRequestPn NUMERIC(20)
);

create table appPeerResultCodeStats
(
instanceID VARCHAR(50),
createtime TIMESTAMP,
peerIndex NUMERIC(20),
applicationID NUMERIC(20),
resultCode NUMERIC(20),
applicationName VARCHAR(50),
peerIdentity VARCHAR(50),
rcwTx NUMERIC(20),
rcwRx NUMERIC(20)
);

COMMIT;
