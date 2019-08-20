package com.elitecore.aaa.core.constant;

/**
 * 
 * Interface contains the ObjectName value for registration 
 * of SNMP MIB in JMX Mbean Server,for support 
 * of HTTPS Adator support in snmp MIB.
 * JIRA ELITEAAA-2527.
 * @author sanjay.dhamelia
 */
public interface SnmpAgentMBeanConstant {
	
	public final static String SNMP_AGENT = "SnmpAgent:type=";

	/***
	 * RADIUS-AUTH-MIB
	 */
	
	public final static String RAD_AUTH_MIB = SNMP_AGENT + "mib-2.radiusMIB.radiusAuthentication";
	/**
	 * constant contains the tree structure node for
	 * RADIUS-AUTH-SERVER-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String RAD_AUTH_SERVER_MIB = RAD_AUTH_MIB + ".radiusAuthServMIB.radiusAuthServMIBObjects.radiusAuthServ";
	public final static String RAD_AUTH_CLIENT_TABLE = RAD_AUTH_SERVER_MIB + ".radiusAuthClientTable.radiusAuthClientEntry,name=";

	/**
	 * constant contains the tree structure node for
	 * RADIUS-AUTH-CLIENT-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String RAD_AUTH_CLIENT_MIB = RAD_AUTH_MIB + ".radiusAuthClientMIB.radiusAuthClientMIBObjects.radiusAuthClient";
	public final static String RAD_AUTH_SERVER_TABLE = RAD_AUTH_CLIENT_MIB + ".radiusAuthServerTable.radiusAuthServerEntry,name=";
	
	/**
	 * RADIUS-ACCT-MIB
	 */
	public final static String RAD_ACCT_MIB = SNMP_AGENT + "mib-2.radiusMIB.radiusAccounting";
	/**
	 * constant contains the tree structure node for
	 * RADIUS-ACCT-SERVER-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String RAD_ACCT_SERVER_MIB = RAD_ACCT_MIB + ".radiusAccServMIB.radiusAccServMIBObjects.radiusAccServ";
	public final static String RAD_ACCT_CLIENT_TABLE = RAD_ACCT_SERVER_MIB + ".radiusAccClientTable.radiusAccClientEntry,name=";
	/**
	 * constant contains the tree structure node for
	 * RADIUS-ACCT-CLIENT-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String RAD_ACCT_CLIENT_MIB = RAD_ACCT_MIB + ".radiusAccClientMIB.radiusAccClientMIBObjects.radiusAccClient";
	public final static String RAD_ACCT_SERVER_TABLE = RAD_ACCT_CLIENT_MIB + ".radiusAcctServerTable.radiusAcctServerEntry,name=";
	
	/**
	 * RADIUS-DYNAUTH-MIB
	 */
	
	/**
	 * constant contains the tree structure node for
	 * RADIUS-DYNAUTH-SERVER-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String DYNAUTH_SERVER_MIB = SNMP_AGENT + "mib-2.radiusDynAuthServerMIB.radiusDynAuthServerMIBObjects";
	public final static String DYN_AUTH_CLIENT_TABLE = DYNAUTH_SERVER_MIB +  ".radiusDynAuthClientTable.radiusDynAuthClientEntry,name=";

	/**
	 * constant contains the tree structure node for
	 * RADIUS-DYNAUTH-CLIENT-MIB
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String DYNAUTH_CLIENT_MIB = SNMP_AGENT + "mib-2.radiusDynAuthClientMIB.radiusDynAuthClientMIBObjects";
	public final static String DYNA_AUTH_SERVER_TABLE = DYNAUTH_CLIENT_MIB + ".radiusDynAuthServerTable.radiusDynAuthServerEntry,name=";
	
	
	/**
	 * Custom MIB Implemenatation.
	 * i.e IP-POOL,Charging,SessionManager MIB
	 */
	public final static String ENTERPRISE_MIB = SNMP_AGENT + "private.elitecore.radius";

	/**
	 * IP-POOL MIB
	 */
	public final static String IP_POOL_SERVER_MIB = ENTERPRISE_MIB + ".ipPoolMIB.ipPoolServMIBObjects";
	/**
	 * constant contains the tree structure node for
	 * AAA Client Table of IP-POOL-SERVER-MIB,
	 * change the node value if node structure will change.
	 */
	public final static String IP_POOL_AAA_CLIENT_TABLE = IP_POOL_SERVER_MIB + ".ipPoolAAAClientStatisticsTable.ipPoolAAAClientEntry,name=";
	/**
	 * constant contains the tree structure node for
	 * NAS Client Table of IP-POOL-SERVER-MIB,
	 * change the node value if node structure will change.
	 */
	public final static String IP_POOL_NAS_CLIENT_TABLE = IP_POOL_SERVER_MIB + ".ipPoolNASClientStatisticsTable.ipPoolNASClientEntry,name=";
	
	/**
	 * constant contains the tree structure node as
	 * like in IP-POOL-SERVICE-CLIENT-MIB,
	 * change here if node structure will change.
	 */
	public final static String IP_POOL_CLIENT_MIB = ENTERPRISE_MIB + ".ipPoolMIB.ipPoolClientMIBObjects";
	public final static String IP_POOL_SERVER_TABLE = IP_POOL_CLIENT_MIB + ".ipPoolServerStatisticsTable.ipPoolServerEntry,name=";
	
	/**
	 * Charging-service MIB
	 */
	/**
	 * constant contains the tree structure node for
	 * CHARGING-SERVER-MIB,change the value of node
	 * if node structure will change.
	 */
	public final static String CHARGING_SERVER_MIB = ENTERPRISE_MIB + ".chargingMIB.chargingServMIBObjects";
	public final static String CHARGING_CLIENT_TABLE = CHARGING_SERVER_MIB + ".chargingClientStatsTable.chargingClientEntry,name=";
	
	/**
	 * constant contains the tree structure node for 
	 * CHARGING-CLIENT-MIB,change the value of node
	 * if node structure will change.
	 */
	public final static String CHARGING_CLIENT_MIB = ENTERPRISE_MIB + ".chargingMIB.chargingClientMIBObjects";
	public final static String CHARGING_SERVER_TABLE = CHARGING_CLIENT_MIB + ".chargingServerTable.chargingServerStatisticsTable.chargingServerEntry,name=";
	
	/**
	 * Session-Manager MIB
	 */
	public final static String SESSION_MGR_MIB = ENTERPRISE_MIB + ".sessionManagerMIB";

	/**
	 * constant contains the tree structure node for
	 * Local-Session-Manager MIB.
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String LOCAL_SM_MIB = SESSION_MGR_MIB + ".localSessionManagerMIBObjects";
	public final static String LOCAL_SM_TABLE = LOCAL_SM_MIB + ".localSessionManagerStatsTable.localSessionManagerEntry,name=";
	
	/**
	 * constant contains the tree structure node for
	 * Remote-Session-Manager MIB.
	 * change the value of node 
	 * if node structure will change.
	 */
	public final static String REMOTE_SM_MIB = SESSION_MGR_MIB + ".remoteSessionManagerMIBObjects";
	public final static String REMOTE_SM_TABLE = REMOTE_SM_MIB + ".remoteSessionManagerStatTable.remoteSessionManagerEntry,name=";
}
