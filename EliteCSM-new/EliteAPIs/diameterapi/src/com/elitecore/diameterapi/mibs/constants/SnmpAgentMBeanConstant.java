package com.elitecore.diameterapi.mibs.constants;

public interface SnmpAgentMBeanConstant {

	/**
	 * Incorporate SNMP Agent into HTTP_ADAPTOR
	 */
	public final static String SNMP_AGENT = "SnmpAgent:type=";
	
	/**
	 * Base Diameter MIB
	 */
	public final static String BASE_DIAMETER_MIB = SNMP_AGENT +"mib-2.diameterBaseProtocolMIB.diameterBaseObjects";
	
	public final static String PEER_TABLE = BASE_DIAMETER_MIB + ".dbpPeerCfgs.dbpPeerTable.dbpPeerEntry,name=";
	public final static String PEER_IP_ADDR_TABLE = BASE_DIAMETER_MIB + ".dbpPeerCfgs.dbpPeerIpAddrTable.dbpPeerIpAddrEntry,name=";
	public final static String REALM_MSG_ROUTE_TABLE = BASE_DIAMETER_MIB + ".dbpRealmStats.dbpRealmMessageRouteTable.dbpRealmMessageRouteEntry,name=";
	public final static String PEER_INFO_TABLE = BASE_DIAMETER_MIB + ".dbpPeerStats.dbpPeerInfo.dbpPerPeerInfoTable.dbpPerPeerInfoEntry,name=";
	public final static String PEER_VENDOR_TABLE = BASE_DIAMETER_MIB + ".dbpPeerCfgs.dbpPeerVendorTable.dbpPeerVendorEntry,name=";
	public final static String APP_ADV_FROM_PEER_TABLE = BASE_DIAMETER_MIB + ".dbpPeerCfgs.dbpAppAdvFromPeerTable.dbpAppAdvFromPeerEntry,name=";
	public final static String LOCAL_APP_TABLE = BASE_DIAMETER_MIB + ".dbpLocalCfgs.dbpLocalApplTable.dbpLocalApplEntry,name=";
	public final static String APP_ADV_TO_PEER_TABLE = BASE_DIAMETER_MIB + ".dbpLocalCfgs.dbpAppAdvToPeerTable.dbpAppAdvToPeerEntry,name=";
	/**
	 * Diameter CC MIB
	 */
	public final static String DIAMETER_CC_MIB = SNMP_AGENT +"mib-2.diameterCCAMIB.diameterCcAppMIB.diameterCcAppObjects";
	
	public final static String CCA_HOST_IPADDR_TABLE = DIAMETER_CC_MIB + ".dccaHostCfgs.dccaHostIpAddrTable.dccaHostIpAddrEntry,name=";
	public final static String CCA_PEER_TABLE = DIAMETER_CC_MIB + ".dccaPeerCfgs.dccaPeerTable.dccaPeerEntry,name=";
	public final static String CCA_PEER_VENDOR_TABLE = DIAMETER_CC_MIB + ".dccaPeerCfgs.dccaPeerVendorTable.dccaPeerVendorEntry,name=";
	public final static String CCA_PEER_STAT_TABLE = DIAMETER_CC_MIB + ".dccaPeerStats.dccaPerPeerStatsTable.dccaPerPeerStatsEntry,name=";
	
	
	/**
	 * Diameter Stack MIB
	 * 
	 */
	
	public final static String DIAMETER_STACK_MIB = SNMP_AGENT +"enterprises.elitecore.diameterstack";
	public final static String DIAMETER_STACK_INFO = DIAMETER_STACK_MIB +",name=";
	
	public final static String DIAMETER_STACK_STATISTICS = DIAMETER_STACK_MIB +".stackStatistics,name=";
	
	
	public final static String DIAMETER_STACK_APP_STATISTICS = DIAMETER_STACK_MIB +".appStatistics,name=";
	public final static String DIAMETER_STACK_APP_STATISTICS_TABLE = DIAMETER_STACK_MIB +".appStatistics.appStatisticsTable.applicationStatisticsEntry,name=";

	public static final String DIAMETER_STACK_PEER_STATISTICS = DIAMETER_STACK_MIB +".peerStatistics,name=";
	public static final String DIAMETER_STACK_PEER_INFO_TABLE = DIAMETER_STACK_MIB +".peerStatistics.peerInfoTable.peerInfoEntry,name=";

	public static final String DIAMETER_STACK_PEER_CONN_STATISTICS = DIAMETER_STACK_MIB +".peerStatistics.connectionStatistics,name=";
	public static final String DIAMETER_STACK_PEER_CONN_TABLE = DIAMETER_STACK_MIB +".peerStatistics.connectionStatistics.peerIpAddrTable.peerIpAddrEntry,name=";
	
	public static final String DIAMETER_STACK_PEER_APP_WISE_STATISTICS = DIAMETER_STACK_MIB +".peerStatistics.applicationWiseStatistics,name=";
	public static final String DIAMETER_STACK_PEER_APP_WISE_TABLE = DIAMETER_STACK_MIB +".peerStatistics.appWiseStatisticsTable.appWiseStatisticsEntry,name=";

	public static final String DIAMETER_STACK_PEER_APP_CC_WISE_STATISTICS = DIAMETER_STACK_MIB +".peerStatistics.applicationWiseStatistics.commandCodeWiseStatistics,name=";
	public static final String DIAMETER_STACK_PEER_APP_CC_WISE_TABLE = DIAMETER_STACK_MIB +".peerStatistics.applicationWiseStatistics.commandCodeWiseStatistics.commandCodeStatisticsTable.commandCodeStatisticsEntry,name=";
	
	public static final String DIAMETER_STACK_PEER_APP_RESULT_CODE_WISE_STATISTICS = DIAMETER_STACK_MIB +".peerStatistics.applicationWiseStatistics.resultCodeWiseStatistics,name=";
	public static final String DIAMETER_STACK_PEER_APP_RESULT_CODE_WISE_TABLE = DIAMETER_STACK_MIB +".peerStatistics.applicationWiseStatistics.resultCodeStatisticsTable.resultCodeStatisticsEntry,name=";
	
	
}
