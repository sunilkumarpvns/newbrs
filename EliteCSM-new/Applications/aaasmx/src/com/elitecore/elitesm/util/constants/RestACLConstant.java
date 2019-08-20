package com.elitecore.elitesm.util.constants;



public enum RestACLConstant {
	
	/* Database Datasource */
	GET_DATASOURCE(ConfigConstant.SEARCH_DATABASE_DATASOURCE),
	PUT_DATASOURCE(ConfigConstant.UPDATE_DATABASE_DATASOURCE),
	POST_DATASOURCE(ConfigConstant.CREATE_DATABASE_DATASOURCE),
	DELETE_DATASOURCE(ConfigConstant.DELETE_DATABASE_DATASOURCE),
	
	/* Driver */
	GET_DRIVER(ConfigConstant.SEARCH_DRIVER),
	POST_DRIVER(ConfigConstant.CREATE_DRIVER),
	PUT_DRIVER(ConfigConstant.UPDATE_DRIVER),
	DELETE_DRIVER(ConfigConstant.DELETE_DRIVER),
	
	/* Radius Policy */
	GET_RADIUSPOLICY(ConfigConstant.SEARCH_RADIUS_POLICY_ACTION),
	PUT_RADIUSPOLICY(ConfigConstant.UPDATE_RADIUS_POLICY_ACTION),
	POST_RADIUSPOLICY(ConfigConstant.CREATE_RADIUS_POLICY_ACTION),
	DELETE_RADIUSPOLICY(ConfigConstant.DELETE_RADIUS_POLICY_ACTION),
	
	/* Diameter Policy */
	GET_AUTHORIZATIONPOLICY(ConfigConstant.SEARCH_AUTHORIZATION_POLICY),
	PUT_AUTHORIZATIONPOLICY(ConfigConstant.UPDATE_AUTHORIZATION_POLICY_BASIC_DETAIL),
	POST_AUTHORIZATIONPOLICY(ConfigConstant.CREATE_AUTHORIZATION_POLICY),
	DELETE_AUTHORIZATIONPOLICY(ConfigConstant.DELETE_AUTHORIZATION_POLICY),
	
	/* Access Policy */
	GET_ACCESSPOLICY(ConfigConstant.SEARCH_ACCESS_POLICY_ACTION),
	PUT_ACCESSPOLICY(ConfigConstant.UPDATE_ACCESS_POLICY_ACTION),
	POST_ACCESSPOLICY(ConfigConstant.CREATE_ACCESS_POLICY_ACTION),
	DELETE_ACCESSPOLICY(ConfigConstant.DELETE_ACCESS_POLICY_ACTION),
	
	/* Grace Policy */
	GET_GRACEPOLICY(ConfigConstant.LIST_GRACE_POLICY),
	PUT_GRACEPOLICY(ConfigConstant.UPDATE_GRACE_POLICY),
	POST_GRACEPOLICY(ConfigConstant.CREATE_GRACE_POLICY),
	DELETE_GRACEPOLICY(ConfigConstant.DELETE_GRACE_POLICY),
	
	/* SSL Certificates */
	GET_SSLCERTIFICATES(ConfigConstant.SEARCH_SERVER_CERTIFICATE),
	PUT_SSLCERTIFICATES(ConfigConstant.UPDATE_SERVER_CERTIFICATE),
	POST_SSLCERTIFICATES(ConfigConstant.CREATE_SERVER_CERTIFICATE),
	DELETE_SSLCERTIFICATES(ConfigConstant.DELETE_SERVER_CERTIFICATE),
	
	/* Concurrent Login Policy */
	GET_CONCURRENTLOGINPOLICY(ConfigConstant.SEARCH_CONCURRENT_LOGIN_POLICY_ACTION),
	PUT_CONCURRENTLOGINPOLICY(ConfigConstant.UPDATE_CONCURRENT_LOGIN_POLICY_ACTION),
	POST_CONCURRENTLOGINPOLICY(ConfigConstant.CREATE_CONCURRENT_LOGIN_POLICY_ACTION),
	DELETE_CONCURRENTLOGINPOLICY(ConfigConstant.DELETE_CONCURRENT_LOGIN_POLICY_ACTION),
	
	/* Trusted Client Profile(Radius Client Profile) */
	GET_CLIENTPROFILE(ConfigConstant.SEARCH_CLIENT_PROFILE),
	PUT_CLIENTPROFILE(ConfigConstant.UPDATE_CLIENT_PROFILE),
	POST_CLIENTPROFILE(ConfigConstant.CREATE_CLIENT_PROFILE),
	DELETE_CLIENTPROFILE(ConfigConstant.DELETE_CLIENT_PROFILE),
	
	/* Diameter Peer Profiles */
	GET_DIAMETERPEERPROFILES(ConfigConstant.SEARCH_DIAMETER_PEER_PROFILE),
	PUT_DIAMETERPEERPROFILES(ConfigConstant.UPDATE_DIAMETER_PEER_PROFILE),
	POST_DIAMETERPEERPROFILES(ConfigConstant.CREATE_DIAMETER_PEER_PROFILE),
	DELETE_DIAMETERPEERPROFILES(ConfigConstant.DELETE_DIAMETER_PEER_PROFILE),
	
	/* Server */
	POST_SERVER(ConfigConstant.CREATE_SERVER_INSTANCE_ACTION),
	GET_SERVER(ConfigConstant.SEARCH_SERVER_INSTANCE_ACTION),
	DELETE_SERVER(ConfigConstant.DELETE_SERVER_INSTANCE_ACTION),
	PUT_SERVER(ConfigConstant.UPDATE_SERVER_ACTION),
	
	/* Clients Configuration */
	GET_CLIENTSCONFIGURATION(ConfigConstant.VIEW_CONFIGURATION_ACTION),
	POST_CLIENTSCONFIGURATION(ConfigConstant.UPDATE_CONFIGURATION_ACTION),
	
	/* External System */
	GET_ESI(ConfigConstant.SEARCH_EXTERNAL_SYSTEM),
	PUT_ESI(ConfigConstant.UPDATE_EXTERNAL_SYSTEM),
	POST_ESI(ConfigConstant.CREATE_EXTERNAL_SYSTEM), 
	DELETE_ESI(ConfigConstant.DELETE_EXTERNAL_SYSTEM),
	
	/* LDAP Datasource */
	GET_LDAPDATASOURCE(ConfigConstant.SEARCH_LDAP_DATASOURCE),
	PUT_LDAPDATASOURCE(ConfigConstant.UPDATE_LDAP_DATASOURCE),
	POST_LDAPDATASOURCE(ConfigConstant.CREATE_LDAP_DATASOURCE),
	DELETE_LDAPDATASOURCE(ConfigConstant.DELETE_LDAP_DATASOURCE),
	
	/* Digest Configuration */
	GET_DIGESTCONFIGURATION(ConfigConstant.SEARCH_DIGEST_CONFIGURATION),
	PUT_DIGESTCONFIGURATION(ConfigConstant.UPDATE_DIGEST_CONFIGURATION),
	POST_DIGESTCONFIGURATION(ConfigConstant.CREATE_DIGEST_CONFIGURATION),
	DELETE_DIGESTCONFIGURATION(ConfigConstant.DELETE_DIGEST_CONFIGURATION),
	
	/* Diameter Peers */
	GET_DIAMETERPEERS(ConfigConstant.SEARCH_DIAMETER_PEER),
	PUT_DIAMETERPEERS(ConfigConstant.UPDATE_DIAMETER_PEER),
	POST_DIAMETERPEERS(ConfigConstant.CREATE_DIAMETER_PEER),
	DELETE_DIAMETERPEERS(ConfigConstant.DELETE_DIAMETER_PEER),
	
	/* Diameter Concurrecny */
	GET_DIAMETERCONCURRENCY(ConfigConstant.SEARCH_DIAMETER_CONCURRENCY),
	PUT_DIAMETERCONCURRENCY(ConfigConstant.UPDATE_DIAMETER_CONCURRENCY),
	POST_DIAMETERCONCURRENCY(ConfigConstant.CREATE_DIAMETER_CONCURRENCY),
	DELETE_DIAMETERCONCURRENCY(ConfigConstant.DELETE_DIAMETER_CONCURRENCY),
	
	/* Diameter Peer Group */
	GET_DIAMETERPEERGROUP(ConfigConstant.SEARCH_DIAMETER_PEER_GROUP),
	PUT_DIAMETERPEERGROUP(ConfigConstant.UPDATE_DIAMETER_PEER_GROUP),
	POST_DIAMETERPEERGROUP(ConfigConstant.CREATE_DIAMETER_PEER_GROUP),
	DELETE_DIAMETERPEERGROUP(ConfigConstant.DELETE_DIAMETER_PEER_GROUP),
	
	/* Diameter Policy Group */
	GET_DIAMETERPOLICYGROUP(ConfigConstant.SEARCH_DIAMETER_POLICY_GROUP),
	PUT_DIAMETERPOLICYGROUP(ConfigConstant.UPDATE_DIAMETER_POLICY_GROUP),
	POST_DIAMETERPOLICYGROUP(ConfigConstant.CREATE_DIAMETER_POLICY_GROUP),
	DELETE_DIAMETERPOLICYGROUP(ConfigConstant.DELETE_DIAMETER_POLICY_GROUP),
	
	/* Radius Policy Group */
	GET_RADIUSPOLICYGROUP(ConfigConstant.SEARCH_RADIUS_POLICY_GROUP),
	PUT_RADIUSPOLICYGROUP(ConfigConstant.UPDATE_RADIUS_POLICY_GROUP),
	POST_RADIUSPOLICYGROUP(ConfigConstant.CREATE_RADIUS_POLICY_GROUP),
	DELETE_RADIUSPOLICYGROUP(ConfigConstant.DELETE_RADIUS_POLICY_GROUP),
	
	/* Radius ESI Group */
	GET_RADIUSESIGROUP(ConfigConstant.SEARCH_RADIUS_ESI_GROUP),
	PUT_RADIUSESIGROUP(ConfigConstant.UPDATE_RADIUS_ESI_GROUP),
	POST_RADIUSESIGROUP(ConfigConstant.CREATE_RADIUS_ESI_GROUP),
	DELETE_RADIUSESIGROUP(ConfigConstant.DELETE_RADIUS_ESI_GROUP),
	
	/* Priority Table */
	GET_PRIORITYTABLE(ConfigConstant.SEARCH_PRIORITY_TABLE),
	PUT_PRIORITYTABLE(ConfigConstant.UPDATE_PRIORITY_TABLE),
	POST_PRIORITYTABLE(ConfigConstant.UPDATE_PRIORITY_TABLE),
	
	/* Diameter Routing Table */
	GET_DIAMETERROUTINGTABLE(ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE),
	PUT_DIAMETERROUTINGTABLE(ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE),
	POST_DIAMETERROUTINGTABLE(ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE),
	DELETE_DIAMETERROUTINGTABLE(ConfigConstant.DELETE_DIAMETER_ROUTING_TABLE),
	
	/*EAP Configuration*/
	GET_EAPCONFIG(ConfigConstant.SEARCH_EAP_CONFIGURATION),
	PUT_EAPCONFIG(ConfigConstant.UPDATE_EAP_CONFIGURATION),
	POST_EAPCONFIG(ConfigConstant.CREATE_EAP_CONFIGURATION),
	DELETE_EAPCONFIG(ConfigConstant.DELETE_EAP_CONFIGURATION),
	
	/* Translation Mapping Configuration */
	GET_TRANSLATIONMAPPING(ConfigConstant.SEARCH_TRANSLATION_MAPPING_CONFIG),
	PUT_TRANSLATIONMAPPING(ConfigConstant.UPDATE_TRANSLATION_MAPPING_CONFIG),
	POST_TRANSLATIONMAPPING(ConfigConstant.CREATE_TRANSLATION_MAPPING_CONFIG),
	DELETE_TRANSLATIONMAPPING(ConfigConstant.DELETE_TRANSLATION_MAPPING_CONFIG),
	
	/* Credit Control Service Policy */
	GET_CCSERVICEPOLICY(ConfigConstant.SEARCH_CREDIT_CONTROL_SERVICE_POLICY),
	POST_CCSERVICEPOLICY(ConfigConstant.CREATE_CREDIT_CONTROL_SERVICE_POLICY),
	PUT_CCSERVICEPOLICY(ConfigConstant.UPDATE_CREDIT_CONTROL_SERVICE_POLICY),
	DELETE_CCSERVICEPOLICY(ConfigConstant.DELETE_CREDIT_CONTROL_SERVICE_POLICY),

	/* Radius Service Policy */
	GET_RADIUSSERVICEPOLICY(ConfigConstant.SEARCH_RADIUS_SERVICE_POLICY),
	POST_RADIUSSERVICEPOLICY(ConfigConstant.CREATE_RADIUS_SERVICE_POLICY),
	PUT_RADIUSSERVICEPOLICY(ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_BASIC_DETAILS),
	DELETE_RADIUSSERVICEPOLICY(ConfigConstant.DELETE_RADIUS_SERVICE_POLICY),
	
	/* Tgpp Service Policy */
	GET_TGPPSERVICEPOLICY(ConfigConstant.SEARCH_TGPP_AAA_SERVICE_POLICY),
	POST_TGPPSERVICEPOLICY(ConfigConstant.CREATE_TGPP_AAA_SERVICE_POLICY),
	PUT_TGPPSERVICEPOLICY(ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY),
	DELETE_TGPPSERVICEPOLICY(ConfigConstant.DELETE_TGPP_AAA_SERVICE_POLICY),

	/* Dynauth Service Policy */
	GET_DYNAUTHSERVICEPOLICY(ConfigConstant.SEARCH_DYNAUTH_POLICY),
	POST_DYNAUTHSERVICEPOLICY(ConfigConstant.CREATE_DYNAUTH_POLICY),
	PUT_DYNAUTHSERVICEPOLICY(ConfigConstant.UPDATE_DYNAUTH_POLICY_BASIC_DETAIL),
	DELETE_DYNAUTHSERVICEPOLICY(ConfigConstant.DELETE_DYNAUTH_POLICY),
	
	/* EAP Service Policy */
	GET_EAPSERVICEPOLICY(ConfigConstant.SEARCH_DIAMETER_EAP_POLICY),
	POST_EAPSERVICEPOLICY(ConfigConstant.CREATE_DIAMETER_EAP_POLICY),
	PUT_EAPSERVICEPOLICY(ConfigConstant.UPDATE_DIAMETER_EAP_POLICY),
	DELETE_EAPSERVICEPOLICY(ConfigConstant.DELETE_DIAMETER_EAP_POLICY),
	
	/* Dynauth Service Policy */
	GET_NASSERVICEPOLICY(ConfigConstant.SEARCH_NAS_SERVICE_POLICY),
	POST_NASSERVICEPOLICY(ConfigConstant.CREATE_NAS_SERVICE_POLICY),
	PUT_NASSERVICEPOLICY(ConfigConstant.UPDATE_NAS_SERVICE_POLICY),
	DELETE_NASSERVICEPOLICY(ConfigConstant.DELETE_NAS_SERVICE_POLICY),
	
	/* Charging Service Policy */
	GET_CHARGINGSERVICEPOLICY(ConfigConstant.SEARCH_CG_POLICY),
	POST_CHARGINGSERVICEPOLICY(ConfigConstant.CREATE_CG_POLICY),
	PUT_CHARGINGSERVICEPOLICY(ConfigConstant.UPDATE_CG_POLICY),
	DELETE_CHARGINGSERVICEPOLICY(ConfigConstant.DELETE_CG_POLICY),
	
	/* IP Pool */
	GET_IPPOOL(ConfigConstant.VIEW_IP_POOL_ACTION),
	PUT_IPPOOL(ConfigConstant.UPDATE_IP_POOL_ACTION),
	POST_IPPOOL(ConfigConstant.CREATE_IP_POOL_ACTION),
	DELETE_IPPOOL(ConfigConstant.DELETE_IP_POOL_ACTION),
	
	/* Radius Session Manager */
	GET_SESSIONMANAGER(ConfigConstant.SEARCH_SESSION_MANAGER),
	PUT_SESSIONMANAGER(ConfigConstant.UPDATE_SESSION_MANAGER),
	POST_SESSIONMANAGER(ConfigConstant.CREATE_SESSION_MANAGER),
	DELETE_SESSIONMANAGER(ConfigConstant.DELETE_SESSION_MANAGER),
	
	/*Copy Packet*/
	GET_COPYPACKET(ConfigConstant.SEARCH_COPY_PACKET_TRANSLATION_MAPPING_CONFIG),
	PUT_COPYPACKET(ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG),
	POST_COPYPACKET(ConfigConstant.CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG),
	DELETE_COPYPACKET(ConfigConstant.DELETE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG),
	
	/*Radius Universal Plugin*/
	GET_UNIVERSALPLUGIN(ConfigConstant.SEARCH_PLUGIN),
	PUT_UNIVERSALPLUGIN(ConfigConstant.UPDATE_PLUGIN),
	POST_UNIVERSALPLUGIN(ConfigConstant.CREATE_PLUGIN),
	DELETE_UNIVERSALPLUGIN(ConfigConstant.DELETE_PLUGIN),
	
	/*Transaction Logger Plugin*/
	GET_TRANSACTIONLOGGERPLUGIN(ConfigConstant.SEARCH_PLUGIN),
	PUT_TRANSACTIONLOGGERPLUGIN(ConfigConstant.UPDATE_PLUGIN),
	POST_TRANSACTIONLOGGERPLUGIN(ConfigConstant.CREATE_PLUGIN),
	DELETE_TRANSACTIONLOGGERPLUGIN(ConfigConstant.DELETE_PLUGIN),
	
	/*Groovy Plugin*/
	GET_GROOVYPLUGIN(ConfigConstant.SEARCH_PLUGIN),
	PUT_GROOVYPLUGIN(ConfigConstant.UPDATE_PLUGIN),
	POST_GROOVYPLUGIN(ConfigConstant.CREATE_PLUGIN),
	DELETE_GROOVYPLUGIN(ConfigConstant.DELETE_PLUGIN),
	
	GET_ALERTCONFIGURATION(ConfigConstant.SEARCH_ALERT_LISTENER),
	PUT_ALERTCONFIGURATION(ConfigConstant.UPDATE_ALERT_LISTENER),
	POST_ALERTCONFIGURATION(ConfigConstant.CREATE_ALERT_LISTENER),
	DELETE_ALERTCONFIGURATION(ConfigConstant.DELETE_ALERT_LISTENER),
	
	/*Diameter Session Manager*/
	GET_DIAMETERSESSIONMANAGER(ConfigConstant.SEARCH_DIAMETER_SESSION_MANAGER),
	PUT_DIAMETERSESSIONMANAGER(ConfigConstant.UPDATE_DIAMETER_SESSION_MANAGER),
	POST_DIAMETERSESSIONMANAGER(ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER),
	DELETE_DIAMETERSESSIONMANAGER(ConfigConstant.DELETE_DIAMETER_SESSION_MANAGER),
	
	/*Default Configuration*/
	POST_DEFAULTCONFIGURATION(ConfigConstant.SYSTEM_STARTUP_SETUP),
	GET_DEFAULTCONFIGURATION(ConfigConstant.SYSTEM_STARTUP_SETUP),
	
	/** NOTE : Subscriber web-service's add operation would perform through PUT operation where as 
	    update operation performs through POST operation   */
	
	/* Subscriber Profile */
	GET_SUBSCRIBER(ConfigConstant.SEARCH_SUBSCRIBE_PROFILE),
	PUT_SUBSCRIBER(ConfigConstant.ADD_SUBSCRIBE_PROFILE),    
	DELETE_SUBSCRIBER(ConfigConstant.DELETE_SUBSCRIBE_PROFILE),
	POST_SUBSCRIBER(ConfigConstant.UPDATE_SUBSCRIBE_PROFILE),

	/*Session Search*/
	GET_SESSION(ConfigConstant.SEARCH_ACTIVE_SESSION),
	
	/* Script */
	GET_SCRIPT(ConfigConstant.SEARCH_SUBSCRIBE_PROFILE),
	PUT_SCRIPT(ConfigConstant.ADD_SUBSCRIBE_PROFILE),    
	DELETE_SCRIPT(ConfigConstant.DELETE_SUBSCRIBE_PROFILE),
	POST_SCRIPT(ConfigConstant.UPDATE_SUBSCRIBE_PROFILE),

	/* Correlated Radius */
	GET_CORRELATEDRADIUS(ConfigConstant.SEARCH_CORRELATED_RADIUS),
	PUT_CORRELATEDRADIUS(ConfigConstant.UPDATE_CORRELATED_RADIUS),
	DELETE_CORRELATEDRADIUS(ConfigConstant.DELETE_CORRELATED_RADIUS),
	POST_CORRELATEDRADIUS(ConfigConstant.CREATE_CORRELATED_RADIUS),
	;
	
	private String configConstantString;

	private RestACLConstant(String configConstantString){
		this.configConstantString = configConstantString;
	}
	public String getConfigConstantString() {
		return configConstantString;
	}
}
