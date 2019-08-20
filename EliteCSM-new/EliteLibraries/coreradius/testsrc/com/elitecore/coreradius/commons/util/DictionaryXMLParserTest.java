package com.elitecore.coreradius.commons.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.StringAttribute;

public class DictionaryXMLParserTest {
	
		private Dictionary dictionary;
		
		@Before
		public void loadDictionary() {

			dictionary = new Dictionary();
			try{
			
				StringReader dictionaryReader = new StringReader(StaticDictionaryInput.STANDARD_DICTIONARY_XML);
				dictionary.load(dictionaryReader);
				
			}catch(Exception e){
				e.printStackTrace();
				fail("load dictionary failed for dictionary.Standard, reason: "+e.getMessage());
			}
			
			try {			
				StringReader dictionaryEliteReader = new StringReader(StaticDictionaryInput.ELITECORE_DICTIONARY_XML);
				dictionary.load(dictionaryEliteReader);
			}catch(Exception e){
				e.printStackTrace();
				fail("load dictionary failed for dictionary.Elitecore, reason: "+e.getMessage());
			}

//			Load Cisco Dictionary
//			try {
//				InputStream  dictionaryCiscoStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com\\elitecore\\test\\coreradius\\commons\\util\\dictionary.cisco");
//				Reader dictionaryCiscoReader = new InputStreamReader(dictionaryCiscoStream);
//				dictionary.load(dictionaryCiscoReader);
//			}catch(Exception e){
//				e.printStackTrace();
//				fail("load dictionary failed for dictionary.cisco, reason: "+e.getMessage());
//			}

			// Add code for more dictionary to be loaded.
		}

	    //-------------------------------------------------------------------------
		// Test getAttributeName method of Dictionary
		// Test cases:
		//   1. for each valid Attribute ID - type int
		//-------------------------------------------------------------------------
		@Test
		public void testGetAttributeNameByIntId(){
			try{
				//------------------------
				//getAttributeName
				//-------------------------
				assertEquals("User-Name",dictionary.getAttributeName(1));
				assertEquals("User-Password",dictionary.getAttributeName(2));
				assertEquals("CHAP-Password",dictionary.getAttributeName(3));
				assertEquals("NAS-IP-Address",dictionary.getAttributeName(4));
				assertEquals("NAS-Port",dictionary.getAttributeName(5));
				assertEquals("Service-Type",dictionary.getAttributeName(6));
				assertEquals("Framed-Protocol",dictionary.getAttributeName(7));
				assertEquals("Framed-IP-Address",dictionary.getAttributeName(8));
				assertEquals("Framed-IP-Netmask",dictionary.getAttributeName(9));
				assertEquals("Framed-Routing",dictionary.getAttributeName(10));
				assertEquals("Filter-Id",dictionary.getAttributeName(11));
				assertEquals("Framed-MTU",dictionary.getAttributeName(12));
				assertEquals("Framed-Compression",dictionary.getAttributeName(13));
				assertEquals("Login-IP-Host",dictionary.getAttributeName(14));
				assertEquals("Login-Service",dictionary.getAttributeName(15));
				assertEquals("Login-TCP-Port",dictionary.getAttributeName(16));
				assertEquals("Reply-Message",dictionary.getAttributeName(18));
				assertEquals("Callback-Number",dictionary.getAttributeName(19));
				assertEquals("Callback-Id",dictionary.getAttributeName(20));
				assertEquals("Framed-Route",dictionary.getAttributeName(22));
				assertEquals("Framed-IPX-Network",dictionary.getAttributeName(23));
				assertEquals("State",dictionary.getAttributeName(24));
				assertEquals("Class",dictionary.getAttributeName(25));
				assertEquals("Vendor-Specific",dictionary.getAttributeName(26));
				assertEquals("Session-Timeout",dictionary.getAttributeName(27));
				assertEquals("Idle-Timeout",dictionary.getAttributeName(28));
				assertEquals("Termination-Action",dictionary.getAttributeName(29));
				assertEquals("Called-Station-Id",dictionary.getAttributeName(30));
				assertEquals("Calling-Station-Id",dictionary.getAttributeName(31));
				assertEquals("NAS-Identifier",dictionary.getAttributeName(32));
				assertEquals("Proxy-State",dictionary.getAttributeName(33));
				assertEquals("Login-LAT-Service",dictionary.getAttributeName(34));
				assertEquals("Login-LAT-Node",dictionary.getAttributeName(35));
				assertEquals("Login-LAT-Group",dictionary.getAttributeName(36));
				assertEquals("Framed-AppleTalk-Link",dictionary.getAttributeName(37));
				assertEquals("Framed-AppleTalk-Network",dictionary.getAttributeName(38));
				assertEquals("Framed-AppleTalk-Zone",dictionary.getAttributeName(39));
				assertEquals("Acct-Status-Type",dictionary.getAttributeName(40));
				assertEquals("Acct-Delay-Time",dictionary.getAttributeName(41));
				assertEquals("Acct-Input-Octets",dictionary.getAttributeName(42));
				assertEquals("Acct-Output-Octets",dictionary.getAttributeName(43));
				assertEquals("Acct-Session-Id",dictionary.getAttributeName(44));
				assertEquals("Acct-Authentic",dictionary.getAttributeName(45));
				assertEquals("Acct-Session-Time",dictionary.getAttributeName(46));
				assertEquals("Acct-Input-Packets",dictionary.getAttributeName(47));
				assertEquals("Acct-Output-Packets",dictionary.getAttributeName(48));
				assertEquals("Acct-Terminate-Cause",dictionary.getAttributeName(49));
				assertEquals("Acct-Multi-Session-Id",dictionary.getAttributeName(50));
				assertEquals("Acct-Link-Count",dictionary.getAttributeName(51));
				assertEquals("Acct-Input-Gigawords",dictionary.getAttributeName(52));
				assertEquals("Acct-Output-Gigawords",dictionary.getAttributeName(53));
				
				//TODO: test after support for date Attribute 
				//assertEquals("Event-Timestamp",dictionary.getAttributeName(55)); 
				
				assertEquals("CHAP-Challenge",dictionary.getAttributeName(60));
				assertEquals("NAS-Port-Type",dictionary.getAttributeName(61));
				assertEquals("Port-Limit",dictionary.getAttributeName(62));
				assertEquals("Login-LAT-Port",dictionary.getAttributeName(63));
				assertEquals("Tunnel-Type",dictionary.getAttributeName(64));
				assertEquals("Tunnel-Medium-Type",dictionary.getAttributeName(65));
				assertEquals("Tunnel-Client-Endpoint",dictionary.getAttributeName(66));
				assertEquals("Tunnel-Server-Endpoint",dictionary.getAttributeName(67));
				assertEquals("Acct-Tunnel-Connection",dictionary.getAttributeName(68));
				assertEquals("Tunnel-Password",dictionary.getAttributeName(69));
				assertEquals("ARAP-Password",dictionary.getAttributeName(70));
				assertEquals("ARAP-Features",dictionary.getAttributeName(71));
				assertEquals("ARAP-Zone-Access",dictionary.getAttributeName(72));
				assertEquals("ARAP-Security",dictionary.getAttributeName(73));
				assertEquals("ARAP-Security-Data",dictionary.getAttributeName(74));
				assertEquals("Password-Retry",dictionary.getAttributeName(75));
				assertEquals("Prompt",dictionary.getAttributeName(76));
				assertEquals("Connect-Info",dictionary.getAttributeName(77));
				assertEquals("Configuration-Token",dictionary.getAttributeName(78));
				assertEquals("EAP-Message",dictionary.getAttributeName(79));
				assertEquals("Message-Authenticator",dictionary.getAttributeName(80));
				assertEquals("Tunnel-Private-Group-ID",dictionary.getAttributeName(81));
				assertEquals("Tunnel-Assignment-ID",dictionary.getAttributeName(82));
				assertEquals("Tunnel-Preference",dictionary.getAttributeName(83));
				assertEquals("ARAP-Challenge-Response",dictionary.getAttributeName(84));
				assertEquals("Acct-Interim-Interval",dictionary.getAttributeName(85));
				
				assertEquals("Acct-Tunnel-Packets-Lost",dictionary.getAttributeName(86));
				assertEquals("NAS-Port-Id",dictionary.getAttributeName(87));
				assertEquals("Framed-Pool",dictionary.getAttributeName(88));
				assertEquals("Tunnel-Client-Auth-ID",dictionary.getAttributeName(90));
				assertEquals("Tunnel-Server-Auth-ID",dictionary.getAttributeName(91));
				//TODO: test after support for its data type
				//assertEquals("NAS-IPv6-Address",dictionary.getAttributeName(95)); 	giving null value
				//assertEquals("Framed-Interface-Id",dictionary.getAttributeName(96));	giving null value
				assertEquals("Framed-IPv6-Prefix",dictionary.getAttributeName(97));
				//assertEquals("Login-IPv6-Host",dictionary.getAttributeName(98));		giving null value
				assertEquals("Framed-IPv6-Route",dictionary.getAttributeName(99));
				assertEquals("Framed-IPv6-Pool",dictionary.getAttributeName(100));
				
				
				assertEquals("Error-Cause",dictionary.getAttributeName(101));
//				assertEquals("Fall-Through",dictionary.getAttributeName(500));
//				assertEquals("Exec-Program",dictionary.getAttributeName(502));         
//				assertEquals("Exec-Program-Wait",dictionary.getAttributeName(503));         
//				assertEquals("Auth-Type",dictionary.getAttributeName(1000));
//				assertEquals("Menu",dictionary.getAttributeName(1001));
//				assertEquals("Termination-Menu",dictionary.getAttributeName(1002));
//				assertEquals("Prefix",dictionary.getAttributeName(1003));
//				assertEquals("Suffix",dictionary.getAttributeName(1004));
//				assertEquals("Group",dictionary.getAttributeName(1005));
//				assertEquals("Crypt-Password",dictionary.getAttributeName(1006));
//				assertEquals("Connect-Rate",dictionary.getAttributeName(1007));
//				assertEquals("Add-Prefix",dictionary.getAttributeName(1008));
//				assertEquals("Add-Suffix",dictionary.getAttributeName(1009));
//				//TODO: test after support for its data type
//				//assertEquals("Expiration",dictionary.getAttributeName(1010));			giving null value
//				assertEquals("Autz-Type",dictionary.getAttributeName(1011));
//				assertEquals("Acct-Type",dictionary.getAttributeName(1012));
//				assertEquals("Session-Type",dictionary.getAttributeName(1013));
//				assertEquals("Post-Auth-Type",dictionary.getAttributeName(1014));
//				assertEquals("Pre-Proxy-Type",dictionary.getAttributeName(1015));
//				assertEquals("Post-Proxy-Type",dictionary.getAttributeName(1016));
//				assertEquals("Pre-Acct-Type",dictionary.getAttributeName(1017));
//				
//				assertEquals("EAP-Type",dictionary.getAttributeName(1018));
//				assertEquals("EAP-TLS-Require-Client-Cert",dictionary.getAttributeName(1019));
//				assertEquals("EAP-Id",dictionary.getAttributeName(1020));
//				assertEquals("EAP-Code",dictionary.getAttributeName(1021));
//				assertEquals("EAP-MD5-Password",dictionary.getAttributeName(1022));
//				
//				assertEquals("User-Category",dictionary.getAttributeName(1029));
//				assertEquals("Group-Name",dictionary.getAttributeName(1030));
//				assertEquals("Huntgroup-Name",dictionary.getAttributeName(1031));
//				assertEquals("Simultaneous-Use",dictionary.getAttributeName(1034));
//				assertEquals("Strip-User-Name",dictionary.getAttributeName(1035));
//				assertEquals("Hint",dictionary.getAttributeName(1040));
//				assertEquals("Pam-Auth",dictionary.getAttributeName(1041));
//				assertEquals("Login-Time",dictionary.getAttributeName(1042));
//				assertEquals("Stripped-User-Name",dictionary.getAttributeName(1043));
//				assertEquals("Current-Time",dictionary.getAttributeName(1044));
//				assertEquals("Realm",dictionary.getAttributeName(1045));
//				assertEquals("No-Such-Attribute",dictionary.getAttributeName(1046));
//				assertEquals("Packet-Type",dictionary.getAttributeName(1047));
//				assertEquals("Proxy-To-Realm",dictionary.getAttributeName(1048));
//				assertEquals("Replicate-To-Realm",dictionary.getAttributeName(1049));
//				//TODO: test after support for its data type
//				//assertEquals("Acct-Session-Start-Time",dictionary.getAttributeName(1050));	giving null value
//				assertEquals("Acct-Unique-Session-Id",dictionary.getAttributeName(1051));
//				assertEquals("Client-IP-Address",dictionary.getAttributeName(1052));
//				assertEquals("Ldap-UserDn",dictionary.getAttributeName(1053));
//				assertEquals("NS-MTA-MD5-Password",dictionary.getAttributeName(1054));
//				assertEquals("SQL-User-Name",dictionary.getAttributeName(1055));
//				assertEquals("LM-Password",dictionary.getAttributeName(1057));
//				assertEquals("NT-Password",dictionary.getAttributeName(1058));
//				assertEquals("SMB-Account-CTRL",dictionary.getAttributeName(1059));
//				assertEquals("SMB-Account-CTRL-TEXT",dictionary.getAttributeName(1061));
//				assertEquals("User-Profile",dictionary.getAttributeName(1062));
//				assertEquals("Digest-Realm",dictionary.getAttributeName(1063));
//				assertEquals("Digest-Nonce",dictionary.getAttributeName(1064));
//				assertEquals("Digest-Method",dictionary.getAttributeName(1065));
//				assertEquals("Digest-URI",dictionary.getAttributeName(1066));
//				assertEquals("Digest-QOP",dictionary.getAttributeName(1067));
//				assertEquals("Digest-Algorithm",dictionary.getAttributeName(1068));
//				assertEquals("Digest-Body-Digest",dictionary.getAttributeName(1069));
//				assertEquals("Digest-CNonce",dictionary.getAttributeName(1070));
//				assertEquals("Digest-Nonce-Count",dictionary.getAttributeName(1071));
//				assertEquals("Digest-User-Name",dictionary.getAttributeName(1072));
//				assertEquals("Pool-Name",dictionary.getAttributeName(1073));
//				assertEquals("Ldap-Group",dictionary.getAttributeName(1074));
//				assertEquals("Module-Success-Message",dictionary.getAttributeName(1075));
//				assertEquals("Module-Failure-Message",dictionary.getAttributeName(1076));
//				
//				assertEquals("Rewrite-Rule",dictionary.getAttributeName(1078));
//				assertEquals("Sql-Group",dictionary.getAttributeName(1079));
//				assertEquals("Response-Packet-Type",dictionary.getAttributeName(1080));
//				assertEquals("Packet-Dst-Port",dictionary.getAttributeName(1081));
//				assertEquals("MS-CHAP-Use-NTLM-Auth",dictionary.getAttributeName(1082));
//				assertEquals("NTLM-User-Name",dictionary.getAttributeName(1083));
//				
//				assertEquals("EAP-Sim-Subtype",dictionary.getAttributeName(1200));
//				assertEquals("EAP-Sim-Rand1",dictionary.getAttributeName(1201));
//				assertEquals("EAP-Sim-Rand2",dictionary.getAttributeName(1202));
//				assertEquals("EAP-Sim-Rand3",dictionary.getAttributeName(1203));
//				assertEquals("EAP-Sim-SRES1",dictionary.getAttributeName(1204));
//				assertEquals("EAP-Sim-SRES2",dictionary.getAttributeName(1205));
//				assertEquals("EAP-Sim-SRES3",dictionary.getAttributeName(1206));
//				
				
				//-------------------------
				//getAttributeName
				//-------------------------
				
			}catch(Exception e){
				e.printStackTrace();
				fail(" failed, reason: "+e.getMessage());
			}
		}
		
		//-------------------------------------------------------------------------
		// Test getAttributeName method of Dictionary
		// Test cases:
		//   1. for each valid Attribute ID - type String
		//-------------------------------------------------------------------------
		@Test
		public void testGetAttributeNameByStrId(){

			try{
				//------------------------
				//getAttributeName
				//-------------------------
				assertEquals("User-Name",dictionary.getAttributeName("1"));
				assertEquals("User-Password",dictionary.getAttributeName("2"));
				assertEquals("CHAP-Password",dictionary.getAttributeName("3"));
				assertEquals("NAS-IP-Address",dictionary.getAttributeName("4"));
				assertEquals("NAS-Port",dictionary.getAttributeName("5"));
				assertEquals("Service-Type",dictionary.getAttributeName("6"));
				assertEquals("Framed-Protocol",dictionary.getAttributeName("7"));
				assertEquals("Framed-IP-Address",dictionary.getAttributeName("8"));
				assertEquals("Framed-IP-Netmask",dictionary.getAttributeName("9"));
				assertEquals("Framed-Routing",dictionary.getAttributeName("10"));
				assertEquals("Filter-Id",dictionary.getAttributeName("11"));
				assertEquals("Framed-MTU",dictionary.getAttributeName("12"));
				assertEquals("Framed-Compression",dictionary.getAttributeName("13"));
				assertEquals("Login-IP-Host",dictionary.getAttributeName("14"));
				assertEquals("Login-Service",dictionary.getAttributeName("15"));
				assertEquals("Login-TCP-Port",dictionary.getAttributeName("16"));
				assertEquals("Reply-Message",dictionary.getAttributeName("18"));
				assertEquals("Callback-Number",dictionary.getAttributeName("19"));
				assertEquals("Callback-Id",dictionary.getAttributeName("20"));
				assertEquals("Framed-Route",dictionary.getAttributeName("22"));
				assertEquals("Framed-IPX-Network",dictionary.getAttributeName("23"));
				assertEquals("State",dictionary.getAttributeName("24"));
				assertEquals("Class",dictionary.getAttributeName("25"));
				assertEquals("Vendor-Specific",dictionary.getAttributeName("26"));
				assertEquals("Session-Timeout",dictionary.getAttributeName("27"));
				assertEquals("Idle-Timeout",dictionary.getAttributeName("28"));
				assertEquals("Termination-Action",dictionary.getAttributeName("29"));
				assertEquals("Called-Station-Id",dictionary.getAttributeName("30"));
				assertEquals("Calling-Station-Id",dictionary.getAttributeName("31"));
				assertEquals("NAS-Identifier",dictionary.getAttributeName("32"));
				assertEquals("Proxy-State",dictionary.getAttributeName("33"));
				assertEquals("Login-LAT-Service",dictionary.getAttributeName("34"));
				assertEquals("Login-LAT-Node",dictionary.getAttributeName("35"));
				assertEquals("Login-LAT-Group",dictionary.getAttributeName("36"));
				assertEquals("Framed-AppleTalk-Link",dictionary.getAttributeName("37"));
				assertEquals("Framed-AppleTalk-Network",dictionary.getAttributeName("38"));
				assertEquals("Framed-AppleTalk-Zone",dictionary.getAttributeName("39"));
				assertEquals("Acct-Status-Type",dictionary.getAttributeName("40"));
				assertEquals("Acct-Delay-Time",dictionary.getAttributeName("41"));
				assertEquals("Acct-Input-Octets",dictionary.getAttributeName("42"));
				assertEquals("Acct-Output-Octets",dictionary.getAttributeName("43"));
				assertEquals("Acct-Session-Id",dictionary.getAttributeName("44"));
				assertEquals("Acct-Authentic",dictionary.getAttributeName("45"));
				assertEquals("Acct-Session-Time",dictionary.getAttributeName("46"));
				assertEquals("Acct-Input-Packets",dictionary.getAttributeName("47"));
				assertEquals("Acct-Output-Packets",dictionary.getAttributeName("48"));
				assertEquals("Acct-Terminate-Cause",dictionary.getAttributeName("49"));
				assertEquals("Acct-Multi-Session-Id",dictionary.getAttributeName("50"));
				assertEquals("Acct-Link-Count",dictionary.getAttributeName("51"));
				assertEquals("Acct-Input-Gigawords",dictionary.getAttributeName("52"));
				assertEquals("Acct-Output-Gigawords",dictionary.getAttributeName("53"));
				//TODO: test after support for date Attribute
				//assertEquals("Event-Timestamp",dictionary.getAttributeName("55")); 
				
				assertEquals("CHAP-Challenge",dictionary.getAttributeName("60"));
				assertEquals("NAS-Port-Type",dictionary.getAttributeName("61"));
				assertEquals("Port-Limit",dictionary.getAttributeName("62"));
				assertEquals("Login-LAT-Port",dictionary.getAttributeName("63"));
				assertEquals("Tunnel-Type",dictionary.getAttributeName("64"));
				assertEquals("Tunnel-Medium-Type",dictionary.getAttributeName("65"));
				assertEquals("Tunnel-Client-Endpoint",dictionary.getAttributeName("66"));
				assertEquals("Tunnel-Server-Endpoint",dictionary.getAttributeName("67"));
				assertEquals("Acct-Tunnel-Connection",dictionary.getAttributeName("68"));
				assertEquals("Tunnel-Password",dictionary.getAttributeName("69"));
				assertEquals("ARAP-Password",dictionary.getAttributeName("70"));
				assertEquals("ARAP-Features",dictionary.getAttributeName("71"));
				assertEquals("ARAP-Zone-Access",dictionary.getAttributeName("72"));
				assertEquals("ARAP-Security",dictionary.getAttributeName("73"));
				assertEquals("ARAP-Security-Data",dictionary.getAttributeName("74"));
				assertEquals("Password-Retry",dictionary.getAttributeName("75"));
				assertEquals("Prompt",dictionary.getAttributeName("76"));
				assertEquals("Connect-Info",dictionary.getAttributeName("77"));
				assertEquals("Configuration-Token",dictionary.getAttributeName("78"));
				assertEquals("EAP-Message",dictionary.getAttributeName("79"));
				assertEquals("Message-Authenticator",dictionary.getAttributeName("80"));
				assertEquals("Tunnel-Private-Group-ID",dictionary.getAttributeName("81"));
				assertEquals("Tunnel-Assignment-ID",dictionary.getAttributeName("82"));
				assertEquals("Tunnel-Preference",dictionary.getAttributeName("83"));
				assertEquals("ARAP-Challenge-Response",dictionary.getAttributeName("84"));
				assertEquals("Acct-Interim-Interval",dictionary.getAttributeName("85"));
				
				assertEquals("Acct-Tunnel-Packets-Lost",dictionary.getAttributeName("86"));
				assertEquals("NAS-Port-Id",dictionary.getAttributeName("87"));
				assertEquals("Framed-Pool",dictionary.getAttributeName("88"));
				assertEquals("Tunnel-Client-Auth-ID",dictionary.getAttributeName("90"));
				assertEquals("Tunnel-Server-Auth-ID",dictionary.getAttributeName("91"));
				////TODO: test after support for its data type
				//assertEquals("NAS-IPv6-Address",dictionary.getAttributeName("95")); 	giving null value
				//assertEquals("Framed-Interface-Id",dictionary.getAttributeName("96"));	giving null value
				assertEquals("Framed-IPv6-Prefix",dictionary.getAttributeName("97"));
				//assertEquals("Login-IPv6-Host",dictionary.getAttributeName("98"));		giving null value
				assertEquals("Framed-IPv6-Route",dictionary.getAttributeName("99"));
				assertEquals("Framed-IPv6-Pool",dictionary.getAttributeName("100"));
				
				
				assertEquals("Error-Cause",dictionary.getAttributeName("101"));
//				assertEquals("Fall-Through",dictionary.getAttributeName("500"));
//				assertEquals("Exec-Program",dictionary.getAttributeName("502"));         
//				assertEquals("Exec-Program-Wait",dictionary.getAttributeName("503"));         
//				assertEquals("Auth-Type",dictionary.getAttributeName("1000"));
//				assertEquals("Menu",dictionary.getAttributeName("1001"));
//				assertEquals("Termination-Menu",dictionary.getAttributeName("1002"));
//				assertEquals("Prefix",dictionary.getAttributeName("1003"));
//				assertEquals("Suffix",dictionary.getAttributeName("1004"));
//				assertEquals("Group",dictionary.getAttributeName("1005"));
//				assertEquals("Crypt-Password",dictionary.getAttributeName("1006"));
//				assertEquals("Connect-Rate",dictionary.getAttributeName("1007"));
//				assertEquals("Add-Prefix",dictionary.getAttributeName("1008"));
//				assertEquals("Add-Suffix",dictionary.getAttributeName("1009"));
//				////TODO: test after support for its data type
//				//assertEquals("Expiration",dictionary.getAttributeName("1010"));			giving null value
//				assertEquals("Autz-Type",dictionary.getAttributeName("1011"));
//				assertEquals("Acct-Type",dictionary.getAttributeName("1012"));
//				assertEquals("Session-Type",dictionary.getAttributeName("1013"));
//				assertEquals("Post-Auth-Type",dictionary.getAttributeName("1014"));
//				assertEquals("Pre-Proxy-Type",dictionary.getAttributeName("1015"));
//				assertEquals("Post-Proxy-Type",dictionary.getAttributeName("1016"));
//				assertEquals("Pre-Acct-Type",dictionary.getAttributeName("1017"));
//				
//				assertEquals("EAP-Type",dictionary.getAttributeName("1018"));
//				assertEquals("EAP-TLS-Require-Client-Cert",dictionary.getAttributeName("1019"));
//				assertEquals("EAP-Id",dictionary.getAttributeName("1020"));
//				assertEquals("EAP-Code",dictionary.getAttributeName("1021"));
//				assertEquals("EAP-MD5-Password",dictionary.getAttributeName("1022"));
//				
//				assertEquals("User-Category",dictionary.getAttributeName("1029"));
//				assertEquals("Group-Name",dictionary.getAttributeName("1030"));
//				assertEquals("Huntgroup-Name",dictionary.getAttributeName("1031"));
//				assertEquals("Simultaneous-Use",dictionary.getAttributeName("1034"));
//				assertEquals("Strip-User-Name",dictionary.getAttributeName("1035"));
//				assertEquals("Hint",dictionary.getAttributeName("1040"));
//				assertEquals("Pam-Auth",dictionary.getAttributeName("1041"));
//				assertEquals("Login-Time",dictionary.getAttributeName("1042"));
//				assertEquals("Stripped-User-Name",dictionary.getAttributeName("1043"));
//				assertEquals("Current-Time",dictionary.getAttributeName("1044"));
//				assertEquals("Realm",dictionary.getAttributeName("1045"));
//				assertEquals("No-Such-Attribute",dictionary.getAttributeName("1046"));
//				assertEquals("Packet-Type",dictionary.getAttributeName("1047"));
//				assertEquals("Proxy-To-Realm",dictionary.getAttributeName("1048"));
//				assertEquals("Replicate-To-Realm",dictionary.getAttributeName("1049"));
//				////TODO: test after support for its data type
//				//assertEquals("Acct-Session-Start-Time",dictionary.getAttributeName("1050"));	giving null value
//				assertEquals("Acct-Unique-Session-Id",dictionary.getAttributeName("1051"));
//				assertEquals("Client-IP-Address",dictionary.getAttributeName("1052"));
//				assertEquals("Ldap-UserDn",dictionary.getAttributeName("1053"));
//				assertEquals("NS-MTA-MD5-Password",dictionary.getAttributeName("1054"));
//				assertEquals("SQL-User-Name",dictionary.getAttributeName("1055"));
//				assertEquals("LM-Password",dictionary.getAttributeName("1057"));
//				assertEquals("NT-Password",dictionary.getAttributeName("1058"));
//				assertEquals("SMB-Account-CTRL",dictionary.getAttributeName("1059"));
//				assertEquals("SMB-Account-CTRL-TEXT",dictionary.getAttributeName("1061"));
//				assertEquals("User-Profile",dictionary.getAttributeName("1062"));
//				assertEquals("Digest-Realm",dictionary.getAttributeName("1063"));
//				assertEquals("Digest-Nonce",dictionary.getAttributeName("1064"));
//				assertEquals("Digest-Method",dictionary.getAttributeName("1065"));
//				assertEquals("Digest-URI",dictionary.getAttributeName("1066"));
//				assertEquals("Digest-QOP",dictionary.getAttributeName("1067"));
//				assertEquals("Digest-Algorithm",dictionary.getAttributeName("1068"));
//				assertEquals("Digest-Body-Digest",dictionary.getAttributeName("1069"));
//				assertEquals("Digest-CNonce",dictionary.getAttributeName("1070"));
//				assertEquals("Digest-Nonce-Count",dictionary.getAttributeName("1071"));
//				assertEquals("Digest-User-Name",dictionary.getAttributeName("1072"));
//				assertEquals("Pool-Name",dictionary.getAttributeName("1073"));
//				assertEquals("Ldap-Group",dictionary.getAttributeName("1074"));
//				assertEquals("Module-Success-Message",dictionary.getAttributeName("1075"));
//				assertEquals("Module-Failure-Message",dictionary.getAttributeName("1076"));
//				
//				assertEquals("Rewrite-Rule",dictionary.getAttributeName("1078"));
//				assertEquals("Sql-Group",dictionary.getAttributeName("1079"));
//				assertEquals("Response-Packet-Type",dictionary.getAttributeName("1080"));
//				assertEquals("Packet-Dst-Port",dictionary.getAttributeName("1081"));
//				assertEquals("MS-CHAP-Use-NTLM-Auth",dictionary.getAttributeName("1082"));
//				assertEquals("NTLM-User-Name",dictionary.getAttributeName("1083"));
//				
//				assertEquals("EAP-Sim-Subtype",dictionary.getAttributeName("1200"));
//				assertEquals("EAP-Sim-Rand1",dictionary.getAttributeName("1201"));
//				assertEquals("EAP-Sim-Rand2",dictionary.getAttributeName("1202"));
//				assertEquals("EAP-Sim-Rand3",dictionary.getAttributeName("1203"));
//				assertEquals("EAP-Sim-SRES1",dictionary.getAttributeName("1204"));
//				assertEquals("EAP-Sim-SRES2",dictionary.getAttributeName("1205"));
//				assertEquals("EAP-Sim-SRES3",dictionary.getAttributeName("1206"));
//				
				
				//-------------------------
				//getAttributeName
				//-------------------------
				
			}catch(Exception e){
				e.printStackTrace();
				fail(" failed, reason: "+e.getMessage());
			}
			
		}
		
//		-------------------------------------------------------------------------
		// Test getAttributeID method of Dictionary
		// Test cases:
		//   1. for each valid Attribute Name - type String
		//-------------------------------------------------------------------------
		@Test
		public void testGetAttributeID(){
			
			try {
				assertEquals("1",dictionary.getAttributeID("User-Name"));
				assertEquals("2",dictionary.getAttributeID("User-Password"));
				assertEquals("3",dictionary.getAttributeID("CHAP-Password"));
				assertEquals("4",dictionary.getAttributeID("NAS-IP-Address"));
				assertEquals("5",dictionary.getAttributeID("NAS-Port"));
				assertEquals("6",dictionary.getAttributeID("Service-Type"));
				assertEquals("7",dictionary.getAttributeID("Framed-Protocol"));
				assertEquals("8",dictionary.getAttributeID("Framed-IP-Address"));
				assertEquals("9",dictionary.getAttributeID("Framed-IP-Netmask"));
				assertEquals("10",dictionary.getAttributeID("Framed-Routing"));
				assertEquals("11",dictionary.getAttributeID("Filter-Id"));
				assertEquals("12",dictionary.getAttributeID("Framed-MTU"));
				assertEquals("13",dictionary.getAttributeID("Framed-Compression"));
				assertEquals("14",dictionary.getAttributeID("Login-IP-Host"));
				assertEquals("15",dictionary.getAttributeID("Login-Service"));
				assertEquals("16",dictionary.getAttributeID("Login-TCP-Port"));
				assertEquals("18",dictionary.getAttributeID("Reply-Message"));
				assertEquals("19",dictionary.getAttributeID("Callback-Number"));
				assertEquals("20",dictionary.getAttributeID("Callback-Id"));
				assertEquals("22",dictionary.getAttributeID("Framed-Route"));
				assertEquals("23",dictionary.getAttributeID("Framed-IPX-Network"));
				assertEquals("24",dictionary.getAttributeID("State"));
				assertEquals("25",dictionary.getAttributeID("Class"));
				assertEquals("26",dictionary.getAttributeID("Vendor-Specific"));
				assertEquals("27",dictionary.getAttributeID("Session-Timeout"));
				assertEquals("28",dictionary.getAttributeID("Idle-Timeout"));
				assertEquals("29",dictionary.getAttributeID("Termination-Action"));
				assertEquals("30",dictionary.getAttributeID("Called-Station-Id"));
				assertEquals("31",dictionary.getAttributeID("Calling-Station-Id"));
				assertEquals("32",dictionary.getAttributeID("NAS-Identifier"));
				assertEquals("33",dictionary.getAttributeID("Proxy-State"));
				assertEquals("34",dictionary.getAttributeID("Login-LAT-Service"));
				assertEquals("35",dictionary.getAttributeID("Login-LAT-Node"));
				assertEquals("36",dictionary.getAttributeID("Login-LAT-Group"));
				assertEquals("37",dictionary.getAttributeID("Framed-AppleTalk-Link"));
				assertEquals("38",dictionary.getAttributeID("Framed-AppleTalk-Network"));
				assertEquals("39",dictionary.getAttributeID("Framed-AppleTalk-Zone"));
				assertEquals("40",dictionary.getAttributeID("Acct-Status-Type"));
				assertEquals("41",dictionary.getAttributeID("Acct-Delay-Time"));
				assertEquals("42",dictionary.getAttributeID("Acct-Input-Octets"));
				assertEquals("43",dictionary.getAttributeID("Acct-Output-Octets"));
				assertEquals("44",dictionary.getAttributeID("Acct-Session-Id"));
				assertEquals("45",dictionary.getAttributeID("Acct-Authentic"));
				assertEquals("46",dictionary.getAttributeID("Acct-Session-Time"));
				assertEquals("47",dictionary.getAttributeID("Acct-Input-Packets"));
				assertEquals("48",dictionary.getAttributeID("Acct-Output-Packets"));
				assertEquals("49",dictionary.getAttributeID("Acct-Terminate-Cause"));
				assertEquals("50",dictionary.getAttributeID("Acct-Multi-Session-Id"));
				assertEquals("51",dictionary.getAttributeID("Acct-Link-Count"));
				assertEquals("52",dictionary.getAttributeID("Acct-Input-Gigawords"));
				assertEquals("53",dictionary.getAttributeID("Acct-Output-Gigawords"));
				
				//TODO: test after support for date Attribute
				//assertEquals("55",dictionary.getAttributeID("Event-Timestamp"));
				
				assertEquals("60",dictionary.getAttributeID("CHAP-Challenge"));
				assertEquals("61",dictionary.getAttributeID("NAS-Port-Type"));
				assertEquals("62",dictionary.getAttributeID("Port-Limit"));
				assertEquals("63",dictionary.getAttributeID("Login-LAT-Port"));
				assertEquals("64",dictionary.getAttributeID("Tunnel-Type"));
				assertEquals("65",dictionary.getAttributeID("Tunnel-Medium-Type"));
				assertEquals("66",dictionary.getAttributeID("Tunnel-Client-Endpoint"));
				assertEquals("67",dictionary.getAttributeID("Tunnel-Server-Endpoint"));
				assertEquals("68",dictionary.getAttributeID("Acct-Tunnel-Connection"));
				assertEquals("69",dictionary.getAttributeID("Tunnel-Password"));
				assertEquals("70",dictionary.getAttributeID("ARAP-Password"));
				assertEquals("71",dictionary.getAttributeID("ARAP-Features"));
				assertEquals("72",dictionary.getAttributeID("ARAP-Zone-Access"));
				assertEquals("73",dictionary.getAttributeID("ARAP-Security"));
				assertEquals("74",dictionary.getAttributeID("ARAP-Security-Data"));
				assertEquals("75",dictionary.getAttributeID("Password-Retry"));
				assertEquals("76",dictionary.getAttributeID("Prompt"));
				assertEquals("77",dictionary.getAttributeID("Connect-Info"));
				assertEquals("78",dictionary.getAttributeID("Configuration-Token"));
				assertEquals("79",dictionary.getAttributeID("EAP-Message"));
				assertEquals("80",dictionary.getAttributeID("Message-Authenticator"));
				assertEquals("81",dictionary.getAttributeID("Tunnel-Private-Group-ID"));
				assertEquals("82",dictionary.getAttributeID("Tunnel-Assignment-ID"));
				assertEquals("83",dictionary.getAttributeID("Tunnel-Preference"));
				assertEquals("84",dictionary.getAttributeID("ARAP-Challenge-Response"));
				assertEquals("85",dictionary.getAttributeID("Acct-Interim-Interval"));
				
				assertEquals("86",dictionary.getAttributeID("Acct-Tunnel-Packets-Lost"));
				assertEquals("87",dictionary.getAttributeID("NAS-Port-Id"));
				assertEquals("88",dictionary.getAttributeID("Framed-Pool"));
				assertEquals("90",dictionary.getAttributeID("Tunnel-Client-Auth-ID"));
				assertEquals("91",dictionary.getAttributeID("Tunnel-Server-Auth-ID"));
				////TODO: test after support for its data type
				//assertEquals("95",dictionary.getAttributeID("NAS-IPv6-Address"));
				//assertEquals("96",dictionary.getAttributeID("Framed-Interface-Id"));
				assertEquals("97",dictionary.getAttributeID("Framed-IPv6-Prefix"));
				//assertEquals("98",dictionary.getAttributeID("Login-IPv6-Host"));
				assertEquals("99",dictionary.getAttributeID("Framed-IPv6-Route"));
				assertEquals("100",dictionary.getAttributeID("Framed-IPv6-Pool"));
				
				
				assertEquals("101",dictionary.getAttributeID("Error-Cause"));
//				assertEquals("500",dictionary.getAttributeID("Fall-Through"));
//				assertEquals("502",dictionary.getAttributeID("Exec-Program"));
//				assertEquals("503",dictionary.getAttributeID("Exec-Program-Wait"));
//				assertEquals("1000",dictionary.getAttributeID("Auth-Type"));
//				assertEquals("1001",dictionary.getAttributeID("Menu"));
//				assertEquals("1002",dictionary.getAttributeID("Termination-Menu"));
//				assertEquals("1003",dictionary.getAttributeID("Prefix"));
//				assertEquals("1004",dictionary.getAttributeID("Suffix"));
//				assertEquals("1005",dictionary.getAttributeID("Group"));
//				assertEquals("1006",dictionary.getAttributeID("Crypt-Password"));
//				assertEquals("1007",dictionary.getAttributeID("Connect-Rate"));
//				assertEquals("1008",dictionary.getAttributeID("Add-Prefix"));
//				assertEquals("1009",dictionary.getAttributeID("Add-Suffix"));
//				//TODO: test after support for its data type
//				//assertEquals("1010",dictionary.getAttributeID("Expiration"));
//				assertEquals("1011",dictionary.getAttributeID("Autz-Type"));
//				assertEquals("1012",dictionary.getAttributeID("Acct-Type"));
//				assertEquals("1013",dictionary.getAttributeID("Session-Type"));
//				assertEquals("1014",dictionary.getAttributeID("Post-Auth-Type"));
//				assertEquals("1015",dictionary.getAttributeID("Pre-Proxy-Type"));
//				assertEquals("1016",dictionary.getAttributeID("Post-Proxy-Type"));
//				assertEquals("1017",dictionary.getAttributeID("Pre-Acct-Type"));
//				
//				assertEquals("1018",dictionary.getAttributeID("EAP-Type"));
//				assertEquals("1019",dictionary.getAttributeID("EAP-TLS-Require-Client-Cert"));
//				assertEquals("1020",dictionary.getAttributeID("EAP-Id"));
//				assertEquals("1021",dictionary.getAttributeID("EAP-Code"));
//				assertEquals("1022",dictionary.getAttributeID("EAP-MD5-Password"));
//				
//				assertEquals("1029",dictionary.getAttributeID("User-Category"));
//				assertEquals("1030",dictionary.getAttributeID("Group-Name"));
//				assertEquals("1031",dictionary.getAttributeID("Huntgroup-Name"));
//				assertEquals("1034",dictionary.getAttributeID("Simultaneous-Use"));
//				assertEquals("1035",dictionary.getAttributeID("Strip-User-Name"));
//				assertEquals("1040",dictionary.getAttributeID("Hint"));
//				assertEquals("1041",dictionary.getAttributeID("Pam-Auth"));
//				assertEquals("1042",dictionary.getAttributeID("Login-Time"));
//				assertEquals("1043",dictionary.getAttributeID("Stripped-User-Name"));
//				assertEquals("1044",dictionary.getAttributeID("Current-Time"));
//				assertEquals("1045",dictionary.getAttributeID("Realm"));
//				assertEquals("1046",dictionary.getAttributeID("No-Such-Attribute"));
//				assertEquals("1047",dictionary.getAttributeID("Packet-Type"));
//				assertEquals("1048",dictionary.getAttributeID("Proxy-To-Realm"));
//				assertEquals("1049",dictionary.getAttributeID("Replicate-To-Realm"));
//				////TODO: test after support for its data type
//				//assertEquals("1050",dictionary.getAttributeID("Acct-Session-Start-Time"));
//				assertEquals("1051",dictionary.getAttributeID("Acct-Unique-Session-Id"));
//				assertEquals("1052",dictionary.getAttributeID("Client-IP-Address"));
//				assertEquals("1053",dictionary.getAttributeID("Ldap-UserDn"));
//				assertEquals("1054",dictionary.getAttributeID("NS-MTA-MD5-Password"));
//				assertEquals("1055",dictionary.getAttributeID("SQL-User-Name"));
//				assertEquals("1057",dictionary.getAttributeID("LM-Password"));
//				assertEquals("1058",dictionary.getAttributeID("NT-Password"));
//				assertEquals("1059",dictionary.getAttributeID("SMB-Account-CTRL"));
//				assertEquals("1061",dictionary.getAttributeID("SMB-Account-CTRL-TEXT"));
//				assertEquals("1062",dictionary.getAttributeID("User-Profile"));
//				assertEquals("1063",dictionary.getAttributeID("Digest-Realm"));
//				assertEquals("1064",dictionary.getAttributeID("Digest-Nonce"));
//				assertEquals("1065",dictionary.getAttributeID("Digest-Method"));
//				assertEquals("1066",dictionary.getAttributeID("Digest-URI"));
//				assertEquals("1067",dictionary.getAttributeID("Digest-QOP"));
//				assertEquals("1068",dictionary.getAttributeID("Digest-Algorithm"));
//				assertEquals("1069",dictionary.getAttributeID("Digest-Body-Digest"));
//				assertEquals("1070",dictionary.getAttributeID("Digest-CNonce"));
//				assertEquals("1071",dictionary.getAttributeID("Digest-Nonce-Count"));
//				assertEquals("1072",dictionary.getAttributeID("Digest-User-Name"));
//				assertEquals("1073",dictionary.getAttributeID("Pool-Name"));
//				assertEquals("1074",dictionary.getAttributeID("Ldap-Group"));
//				assertEquals("1075",dictionary.getAttributeID("Module-Success-Message"));
//				assertEquals("1076",dictionary.getAttributeID("Module-Failure-Message"));
//				
//				assertEquals("1078",dictionary.getAttributeID("Rewrite-Rule"));
//				assertEquals("1079",dictionary.getAttributeID("Sql-Group"));
//				assertEquals("1080",dictionary.getAttributeID("Response-Packet-Type"));
//				assertEquals("1081",dictionary.getAttributeID("Packet-Dst-Port"));
//				assertEquals("1082",dictionary.getAttributeID("MS-CHAP-Use-NTLM-Auth"));
//				assertEquals("1083",dictionary.getAttributeID("NTLM-User-Name"));
//				
//				assertEquals("1200",dictionary.getAttributeID("EAP-Sim-Subtype"));
//				assertEquals("1201",dictionary.getAttributeID("EAP-Sim-Rand1"));
//				assertEquals("1202",dictionary.getAttributeID("EAP-Sim-Rand2"));
//				assertEquals("1203",dictionary.getAttributeID("EAP-Sim-Rand3"));
//				assertEquals("1204",dictionary.getAttributeID("EAP-Sim-SRES1"));
//				assertEquals("1205",dictionary.getAttributeID("EAP-Sim-SRES2"));
//				assertEquals("1206",dictionary.getAttributeID("EAP-Sim-SRES3"));


				
			} catch (Exception e) {
				e.printStackTrace();
				fail(" failed, reason: "+e.getMessage());
			}
			
		}
		
		//-------------------------------------------------------------------------
		// Test getAttributeIntValue method of Dictionary
		// Test cases:
		//   1. for each valid Attribute  Name  - type String
		//		           and Attribute Value Name - type String   
		//-------------------------------------------------------------------------
		@Test
		public void testGetAttributeIntValueByStrAttrName(){
			try {
				
				//	User Types
				assertEquals(1,dictionary.getKeyFromValue("Service-Type","Login-User"));
				assertEquals(2,dictionary.getKeyFromValue("Service-Type","Framed-User"));
				assertEquals(3,dictionary.getKeyFromValue("Service-Type","Callback-Login-User"));
				assertEquals(4,dictionary.getKeyFromValue("Service-Type","Callback-Framed-User"));
				assertEquals(5,dictionary.getKeyFromValue("Service-Type","Outbound-User"));
				assertEquals(6,dictionary.getKeyFromValue("Service-Type","Administrative-User"));
				assertEquals(7,dictionary.getKeyFromValue("Service-Type","NAS-Prompt-User"));
				assertEquals(8,dictionary.getKeyFromValue("Service-Type","Authenticate-Only"));
				assertEquals(9,dictionary.getKeyFromValue("Service-Type","Callback-NAS-Prompt"));
				assertEquals(10,dictionary.getKeyFromValue("Service-Type","Call-Check"));
				assertEquals(11,dictionary.getKeyFromValue("Service-Type","Callback-Administrative"));
				assertEquals(12,dictionary.getKeyFromValue("Service-Type","Voice"));
				assertEquals(13,dictionary.getKeyFromValue("Service-Type","Fax"));
				assertEquals(14,dictionary.getKeyFromValue("Service-Type","Modem-Relay"));
				assertEquals(15,dictionary.getKeyFromValue("Service-Type","IAPP-Register"));
				assertEquals(16,dictionary.getKeyFromValue("Service-Type","IAPP-AP-Check"));
				assertEquals(17,dictionary.getKeyFromValue("Service-Type","Authorize-Only"));
				
				//Framed Protocols
				assertEquals(1,dictionary.getKeyFromValue("Framed-Protocol","PPP"));
				assertEquals(2,dictionary.getKeyFromValue("Framed-Protocol","SLIP"));
				assertEquals(3,dictionary.getKeyFromValue("Framed-Protocol","ARAP"));
				assertEquals(4,dictionary.getKeyFromValue("Framed-Protocol","Gandalf-SLML"));
				assertEquals(5,dictionary.getKeyFromValue("Framed-Protocol","Xylogics-IPX-SLIP"));
				assertEquals(6,dictionary.getKeyFromValue("Framed-Protocol","X.75-Synchronous"));
				assertEquals(7,dictionary.getKeyFromValue("Framed-Protocol","GPRS-PDP-Context"));
				
				
				//Framed Routing Values
				assertEquals(0,dictionary.getKeyFromValue("Framed-Routing","None"));
				assertEquals(1,dictionary.getKeyFromValue("Framed-Routing","Broadcast"));
				assertEquals(2,dictionary.getKeyFromValue("Framed-Routing","Listen"));
				assertEquals(3,dictionary.getKeyFromValue("Framed-Routing","Broadcast-Listen"));
				
				
				//Framed Compression Types
				assertEquals(0,dictionary.getKeyFromValue("Framed-Compression","None"));
				assertEquals(1,dictionary.getKeyFromValue("Framed-Compression","Van-Jacobson-TCP-IP"));
				assertEquals(2,dictionary.getKeyFromValue("Framed-Compression","IPX-Header-Compression"));
				assertEquals(3,dictionary.getKeyFromValue("Framed-Compression","Stac-LZS"));

				//Login Services
				assertEquals(0,dictionary.getKeyFromValue("Login-Service","Telnet"));
				assertEquals(1,dictionary.getKeyFromValue("Login-Service","Rlogin"));
				assertEquals(2,dictionary.getKeyFromValue("Login-Service","TCP-Clear"));
				assertEquals(3,dictionary.getKeyFromValue("Login-Service","PortMaster"));
				assertEquals(4,dictionary.getKeyFromValue("Login-Service","LAT"));
				assertEquals(5,dictionary.getKeyFromValue("Login-Service","X25-PAD"));
				assertEquals(6,dictionary.getKeyFromValue("Login-Service","X25-T3POS"));
				assertEquals(7,dictionary.getKeyFromValue("Login-Service","TCP-Clear-Quiet"));

				//Login-TCP-Port
				assertEquals(23,dictionary.getKeyFromValue("Login-TCP-Port","Telnet"));
				assertEquals(513,dictionary.getKeyFromValue("Login-TCP-Port","Rlogin"));
				assertEquals(514,dictionary.getKeyFromValue("Login-TCP-Port","Rsh"));
				
				//Status Types
				assertEquals(1,dictionary.getKeyFromValue("Acct-Status-Type","Start"));
				assertEquals(2,dictionary.getKeyFromValue("Acct-Status-Type","Stop"));
				assertEquals(3,dictionary.getKeyFromValue("Acct-Status-Type","Interim-Update"));
		//		assertEquals(3,dictionary.getKeyFromValue("Acct-Status-Type","Alive"));
				assertEquals(7,dictionary.getKeyFromValue("Acct-Status-Type","Accounting-On"));
				assertEquals(8,dictionary.getKeyFromValue("Acct-Status-Type","Accounting-Off"));
				assertEquals(9,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Start"));
				assertEquals(10,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Stop"));
				assertEquals(11,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Reject"));
				assertEquals(12,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Link-Start"));
				assertEquals(13,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Link-Stop"));
				assertEquals(14,dictionary.getKeyFromValue("Acct-Status-Type","Tunnel-Link-Reject"));
				assertEquals(15,dictionary.getKeyFromValue("Acct-Status-Type","Failed"));
				
				//Authentication Types
				assertEquals(1,dictionary.getKeyFromValue("Acct-Authentic","RADIUS"));
				assertEquals(2,dictionary.getKeyFromValue("Acct-Authentic","Local"));
				assertEquals(3,dictionary.getKeyFromValue("Acct-Authentic","Remote"));
				assertEquals(4,dictionary.getKeyFromValue("Acct-Authentic","Diameter"));
				
				//Termination Options
				assertEquals(0,dictionary.getKeyFromValue("Termination-Action","Default"));
				assertEquals(1,dictionary.getKeyFromValue("Termination-Action","RADIUS-Request"));
				
				//NAS Port Types
				assertEquals(0,dictionary.getKeyFromValue("NAS-Port-Type","Async"));
				assertEquals(1,dictionary.getKeyFromValue("NAS-Port-Type","Sync"));
				assertEquals(2,dictionary.getKeyFromValue("NAS-Port-Type","ISDN"));
				assertEquals(3,dictionary.getKeyFromValue("NAS-Port-Type","ISDN-V120"));
				assertEquals(4,dictionary.getKeyFromValue("NAS-Port-Type","ISDN-V110"));
				assertEquals(5,dictionary.getKeyFromValue("NAS-Port-Type","Virtual"));
				assertEquals(6,dictionary.getKeyFromValue("NAS-Port-Type","PIAFS"));
				assertEquals(7,dictionary.getKeyFromValue("NAS-Port-Type","HDLC-Clear-Channel"));
				assertEquals(8,dictionary.getKeyFromValue("NAS-Port-Type","X.25"));
				assertEquals(9,dictionary.getKeyFromValue("NAS-Port-Type","X.75"));
				assertEquals(10,dictionary.getKeyFromValue("NAS-Port-Type","G.3-Fax"));
				assertEquals(11,dictionary.getKeyFromValue("NAS-Port-Type","SDSL"));
				assertEquals(12,dictionary.getKeyFromValue("NAS-Port-Type","ADSL-CAP"));
				assertEquals(13,dictionary.getKeyFromValue("NAS-Port-Type","ADSL-DMT"));
				assertEquals(14,dictionary.getKeyFromValue("NAS-Port-Type","IDSL"));
				assertEquals(15,dictionary.getKeyFromValue("NAS-Port-Type","Ethernet"));
				assertEquals(16,dictionary.getKeyFromValue("NAS-Port-Type","xDSL"));
				assertEquals(17,dictionary.getKeyFromValue("NAS-Port-Type","Cable"));
				assertEquals(18,dictionary.getKeyFromValue("NAS-Port-Type","Wireless-Other"));
				assertEquals(19,dictionary.getKeyFromValue("NAS-Port-Type","Wireless-802.11"));
				assertEquals(20,dictionary.getKeyFromValue("NAS-Port-Type","Token-Ring"));
				assertEquals(21,dictionary.getKeyFromValue("NAS-Port-Type","FDDI"));
				assertEquals(22,dictionary.getKeyFromValue("NAS-Port-Type","Wireless-CDMA2000"));
				assertEquals(23,dictionary.getKeyFromValue("NAS-Port-Type","Wireless-UMTS"));
				assertEquals(24,dictionary.getKeyFromValue("NAS-Port-Type","Wireless-1X-EV"));
				assertEquals(25,dictionary.getKeyFromValue("NAS-Port-Type","IAPP"));
				assertEquals(26,dictionary.getKeyFromValue("NAS-Port-Type","FTTP"));
				
				
				//Acct Terminate Causes
				assertEquals(1,dictionary.getKeyFromValue("Acct-Terminate-Cause","User-Request"));
				assertEquals(2,dictionary.getKeyFromValue("Acct-Terminate-Cause","Lost-Carrier"));
				assertEquals(3,dictionary.getKeyFromValue("Acct-Terminate-Cause","Lost-Service"));
				assertEquals(4,dictionary.getKeyFromValue("Acct-Terminate-Cause","Idle-Timeout"));
				assertEquals(5,dictionary.getKeyFromValue("Acct-Terminate-Cause","Session-Timeout"));
				assertEquals(6,dictionary.getKeyFromValue("Acct-Terminate-Cause","Admin-Reset"));
				assertEquals(7,dictionary.getKeyFromValue("Acct-Terminate-Cause","Admin-Reboot"));
				assertEquals(8,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Error"));
				assertEquals(9,dictionary.getKeyFromValue("Acct-Terminate-Cause","NAS-Error"));
				assertEquals(10,dictionary.getKeyFromValue("Acct-Terminate-Cause","NAS-Request"));
				assertEquals(11,dictionary.getKeyFromValue("Acct-Terminate-Cause","NAS-Reboot"));
				assertEquals(12,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Unneeded"));
				assertEquals(13,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Preempted"));
				assertEquals(14,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Suspended"));
				assertEquals(15,dictionary.getKeyFromValue("Acct-Terminate-Cause","Service-Unavailable"));
				assertEquals(16,dictionary.getKeyFromValue("Acct-Terminate-Cause","Callback"));
				assertEquals(17,dictionary.getKeyFromValue("Acct-Terminate-Cause","User-Error"));
				assertEquals(18,dictionary.getKeyFromValue("Acct-Terminate-Cause","Host-Request"));
				assertEquals(19,dictionary.getKeyFromValue("Acct-Terminate-Cause","Supplicant-Restart"));
				assertEquals(20,dictionary.getKeyFromValue("Acct-Terminate-Cause","Reauthentication-Failure"));
				assertEquals(21,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Reinit"));
				assertEquals(22,dictionary.getKeyFromValue("Acct-Terminate-Cause","Port-Disabled"));


				assertEquals(3,dictionary.getKeyFromValue("Tunnel-Type","L2TP"));
				assertEquals(1,dictionary.getKeyFromValue("Tunnel-Medium-Type","IP"));
				assertEquals(1,dictionary.getKeyFromValue("Tunnel-Type","PPTP"));
				assertEquals(2,dictionary.getKeyFromValue("Tunnel-Type","L2F"));
				assertEquals(3,dictionary.getKeyFromValue("Tunnel-Type","L2TP"));
				assertEquals(4,dictionary.getKeyFromValue("Tunnel-Type","ATMP"));
				assertEquals(5,dictionary.getKeyFromValue("Tunnel-Type","VTP"));
				assertEquals(6,dictionary.getKeyFromValue("Tunnel-Type","AH"));
				assertEquals(7,dictionary.getKeyFromValue("Tunnel-Type","IP-IP"));
				assertEquals(8,dictionary.getKeyFromValue("Tunnel-Type","MIN-IP-IP"));
				assertEquals(9,dictionary.getKeyFromValue("Tunnel-Type","ESP"));
				assertEquals(10,dictionary.getKeyFromValue("Tunnel-Type","GRE"));
				assertEquals(11,dictionary.getKeyFromValue("Tunnel-Type","DVS"));
				assertEquals(12,dictionary.getKeyFromValue("Tunnel-Type","IP-in-IP-Tunneling"));
				assertEquals(13,dictionary.getKeyFromValue("Tunnel-Type","VLAN"));

				assertEquals(0,dictionary.getKeyFromValue("Prompt","No-Echo"));
				assertEquals(1,dictionary.getKeyFromValue("Prompt","Echo"));
				
				//Error causes
				assertEquals(201,dictionary.getKeyFromValue("Error-Cause","Residual-Context-Removed"));
				assertEquals(202,dictionary.getKeyFromValue("Error-Cause","Invalid-EAP-Packet"));
				assertEquals(401,dictionary.getKeyFromValue("Error-Cause","Unsupported-Attribute"));
				assertEquals(402,dictionary.getKeyFromValue("Error-Cause","Missing-Attribute"));
				assertEquals(403,dictionary.getKeyFromValue("Error-Cause","NAS-Identification-Mismatch"));
				assertEquals(404,dictionary.getKeyFromValue("Error-Cause","Invalid-Request"));
				assertEquals(405,dictionary.getKeyFromValue("Error-Cause","Unsupported-Service"));
				assertEquals(406,dictionary.getKeyFromValue("Error-Cause","Unsupported-Extension"));
//				assertEquals(501,dictionary.getKeyFromValue("Error-Cause","Administratively-Prohibited"));
//				assertEquals(502,dictionary.getKeyFromValue("Error-Cause","Proxy-Request-Not-Routable"));
//				assertEquals(503,dictionary.getKeyFromValue("Error-Cause","Session-Context-Not-Found"));
//				assertEquals(504,dictionary.getKeyFromValue("Error-Cause","Session-Context-Not-Removable"));
//				assertEquals(505,dictionary.getKeyFromValue("Error-Cause","Proxy-Processing-Error"));
//				assertEquals(506,dictionary.getKeyFromValue("Error-Cause","Resources-Unavailable"));
//				assertEquals(507,dictionary.getKeyFromValue("Error-Cause","Request-Initiated"));


//				assertEquals(0,dictionary.getKeyFromValue("Auth-Type","Local"));
//				assertEquals(1,dictionary.getKeyFromValue("Auth-Type","System"));
//				assertEquals(2,dictionary.getKeyFromValue("Auth-Type","SecurID"));
//				assertEquals(3,dictionary.getKeyFromValue("Auth-Type","Crypt-Local"));
//				assertEquals(4,dictionary.getKeyFromValue("Auth-Type","Reject"));
//				assertEquals(5,dictionary.getKeyFromValue("Auth-Type","ActivCard"));
//				assertEquals(6,dictionary.getKeyFromValue("Auth-Type","EAP"));
//				assertEquals(7,dictionary.getKeyFromValue("Auth-Type","ARAP"));

//				assertEquals(254,dictionary.getKeyFromValue("Auth-Type","Accept"));
//				assertEquals(1024,dictionary.getKeyFromValue("Auth-Type","PAP"));
//				assertEquals(1025,dictionary.getKeyFromValue("Auth-Type","CHAP"));
//				assertEquals(1026,dictionary.getKeyFromValue("Auth-Type","LDAP"));
//				assertEquals(1027,dictionary.getKeyFromValue("Auth-Type","PAM"));
//				assertEquals(1028,dictionary.getKeyFromValue("Auth-Type","MS-CHAP"));
//				assertEquals(1029,dictionary.getKeyFromValue("Auth-Type","Kerberos"));
//				assertEquals(1030,dictionary.getKeyFromValue("Auth-Type","CRAM"));
//				assertEquals(1031,dictionary.getKeyFromValue("Auth-Type","NS-MTA-MD5"));
//				assertEquals(1033,dictionary.getKeyFromValue("Auth-Type","SMB"));

//				assertEquals(0,dictionary.getKeyFromValue("Autz-Type","Local"));
//				assertEquals(0,dictionary.getKeyFromValue("Acct-Type","Local"));
//				assertEquals(0,dictionary.getKeyFromValue("Session-Type","Local"));
//				assertEquals(0,dictionary.getKeyFromValue("Post-Auth-Type","Local"));
//
//				assertEquals(0,dictionary.getKeyFromValue("Fall-Through","No"));
//				assertEquals(1,dictionary.getKeyFromValue("Fall-Through","Yes"));

//				assertEquals(1,dictionary.getKeyFromValue("Packet-Type","Access-Request"));
//				assertEquals(2,dictionary.getKeyFromValue("Packet-Type","Access-Accept"));
//				assertEquals(3,dictionary.getKeyFromValue("Packet-Type","Access-Reject"));
//				assertEquals(4,dictionary.getKeyFromValue("Packet-Type","Accounting-Request"));
//				assertEquals(5,dictionary.getKeyFromValue("Packet-Type","Accounting-Response"));
//				assertEquals(6,dictionary.getKeyFromValue("Packet-Type","Accounting-Status"));
//				assertEquals(7,dictionary.getKeyFromValue("Packet-Type","Password-Request"));
//				assertEquals(8,dictionary.getKeyFromValue("Packet-Type","Password-Accept"));
//				assertEquals(9,dictionary.getKeyFromValue("Packet-Type","Password-Reject"));
//				assertEquals(10,dictionary.getKeyFromValue("Packet-Type","Accounting-Message"));
//				assertEquals(11,dictionary.getKeyFromValue("Packet-Type","Access-Challenge"));
//				assertEquals(12,dictionary.getKeyFromValue("Packet-Type","Status-Server"));
//				assertEquals(13,dictionary.getKeyFromValue("Packet-Type","Status-Client"));
//				assertEquals(40,dictionary.getKeyFromValue("Packet-Type","Disconnect-Request"));
//				assertEquals(41,dictionary.getKeyFromValue("Packet-Type","Disconnect-ACK"));    
//				assertEquals(42,dictionary.getKeyFromValue("Packet-Type","Disconnect-NAK"));    
//				assertEquals(43,dictionary.getKeyFromValue("Packet-Type","CoA-Request"));       
//				assertEquals(44,dictionary.getKeyFromValue("Packet-Type","CoA-ACK"));           
//				assertEquals(45,dictionary.getKeyFromValue("Packet-Type","CoA-NAK"));
//				assertEquals(50,dictionary.getKeyFromValue("Packet-Type","IP-Address-Allocate"));
//				assertEquals(51,dictionary.getKeyFromValue("Packet-Type","IP-Address-Release"));
//				
//				assertEquals(1,dictionary.getKeyFromValue("Response-Packet-Type","Access-Request"));
//				assertEquals(2,dictionary.getKeyFromValue("Response-Packet-Type","Access-Accept"));
//				assertEquals(3,dictionary.getKeyFromValue("Response-Packet-Type","Access-Reject"));
//				assertEquals(4,dictionary.getKeyFromValue("Response-Packet-Type","Accounting-Request"));
//				assertEquals(5,dictionary.getKeyFromValue("Response-Packet-Type","Accounting-Response"));
//				assertEquals(6,dictionary.getKeyFromValue("Response-Packet-Type","Accounting-Status"));
//				assertEquals(7,dictionary.getKeyFromValue("Response-Packet-Type","Password-Request"));
//				assertEquals(8,dictionary.getKeyFromValue("Response-Packet-Type","Password-Accept"));
//				assertEquals(9,dictionary.getKeyFromValue("Response-Packet-Type","Password-Reject"));
//				assertEquals(10,dictionary.getKeyFromValue("Response-Packet-Type","Accounting-Message"));
//				assertEquals(11,dictionary.getKeyFromValue("Response-Packet-Type","Access-Challenge"));
//				assertEquals(12,dictionary.getKeyFromValue("Response-Packet-Type","Status-Server"));
//				assertEquals(13,dictionary.getKeyFromValue("Response-Packet-Type","Status-Client"));
//
//				assertEquals(0,dictionary.getKeyFromValue("EAP-Type","None"));
//				assertEquals(1,dictionary.getKeyFromValue("EAP-Type","Identity"));
//				assertEquals(2,dictionary.getKeyFromValue("EAP-Type","Notification"));
//				assertEquals(3,dictionary.getKeyFromValue("EAP-Type","NAK"));
//				assertEquals(4,dictionary.getKeyFromValue("EAP-Type","MD5-Challenge"));
//				assertEquals(5,dictionary.getKeyFromValue("EAP-Type","One-Time-Password"));
//				assertEquals(6,dictionary.getKeyFromValue("EAP-Type","Generic-Token-Card"));
//				assertEquals(9,dictionary.getKeyFromValue("EAP-Type","RSA-Public-Key"));
//				assertEquals(10,dictionary.getKeyFromValue("EAP-Type","DSS-Unilateral"));
//				assertEquals(11,dictionary.getKeyFromValue("EAP-Type","KEA"));
//				assertEquals(12,dictionary.getKeyFromValue("EAP-Type","KEA-Validate"));
//				assertEquals(11,dictionary.getKeyFromValue("EAP-Type","KEA"));
//				assertEquals(13,dictionary.getKeyFromValue("EAP-Type","EAP-TLS"));
//				assertEquals(14,dictionary.getKeyFromValue("EAP-Type","Defender-Token"));
//				assertEquals(15,dictionary.getKeyFromValue("EAP-Type","RSA-SecurID-EAP"));
//				assertEquals(16,dictionary.getKeyFromValue("EAP-Type","Arcot-Systems-EAP"));
//				assertEquals(17,dictionary.getKeyFromValue("EAP-Type","Cisco-LEAP"));
//				assertEquals(18,dictionary.getKeyFromValue("EAP-Type","Nokia-IP-Smart-Card"));
//				assertEquals(18,dictionary.getKeyFromValue("EAP-Type","SIM"));
//				assertEquals(19,dictionary.getKeyFromValue("EAP-Type","SRP-SHA1-Part-1"));
//				assertEquals(20,dictionary.getKeyFromValue("EAP-Type","SRP-SHA1-Part-2"));
//				assertEquals(21,dictionary.getKeyFromValue("EAP-Type","EAP-TTLS"));
//				assertEquals(22,dictionary.getKeyFromValue("EAP-Type","Remote-Access-Service"));
//				assertEquals(23,dictionary.getKeyFromValue("EAP-Type","UMTS"));
//				assertEquals(24,dictionary.getKeyFromValue("EAP-Type","EAP-3Com-Wireless"));
//				assertEquals(25,dictionary.getKeyFromValue("EAP-Type","PEAP"));
//				assertEquals(26,dictionary.getKeyFromValue("EAP-Type","MS-EAP-Authentication"));
//				assertEquals(27,dictionary.getKeyFromValue("EAP-Type","MAKE"));
//				assertEquals(28,dictionary.getKeyFromValue("EAP-Type","CRYPTOCard"));
//				assertEquals(29,dictionary.getKeyFromValue("EAP-Type","EAP-MSCHAP-V2"));
//				assertEquals(30,dictionary.getKeyFromValue("EAP-Type","DynamID"));
//				assertEquals(31,dictionary.getKeyFromValue("EAP-Type","Rob-EAP"));
//				assertEquals(32,dictionary.getKeyFromValue("EAP-Type","SecurID-EAP"));
//				assertEquals(33,dictionary.getKeyFromValue("EAP-Type","MS-Authentication-TLV"));
//				assertEquals(34,dictionary.getKeyFromValue("EAP-Type","SentriNET"));
//				assertEquals(35,dictionary.getKeyFromValue("EAP-Type","EAP-Actiontec-Wireless"));
//				assertEquals(36,dictionary.getKeyFromValue("EAP-Type","Cogent-Biomentric-EAP"));
//				assertEquals(37,dictionary.getKeyFromValue("EAP-Type","AirFortress-EAP"));
//				assertEquals(38,dictionary.getKeyFromValue("EAP-Type","EAP-HTTP-Digest"));
//				assertEquals(39,dictionary.getKeyFromValue("EAP-Type","SecuriSuite-EAP"));
//				assertEquals(40,dictionary.getKeyFromValue("EAP-Type","DeviceConnect-EAP"));
//				assertEquals(41,dictionary.getKeyFromValue("EAP-Type","EAP-SPEKE"));
//				assertEquals(42,dictionary.getKeyFromValue("EAP-Type","EAP-MOBAC"));
//				assertEquals(26,dictionary.getKeyFromValue("EAP-Type","Microsoft-MS-CHAPv2"));
//				assertEquals(29,dictionary.getKeyFromValue("EAP-Type","Cisco-MS-CHAPv2"));
//				assertEquals(26,dictionary.getKeyFromValue("EAP-Type","MS-CHAP-V2"));
//
//				assertEquals(0,dictionary.getKeyFromValue("EAP-TLS-Require-Client-Cert","No"));
//				assertEquals(1,dictionary.getKeyFromValue("EAP-TLS-Require-Client-Cert","Yes"));
//
//				assertEquals(1,dictionary.getKeyFromValue("EAP-Code","Request"));
//				assertEquals(2,dictionary.getKeyFromValue("EAP-Code","Response"));
//				assertEquals(3,dictionary.getKeyFromValue("EAP-Code","Success"));
//				assertEquals(4,dictionary.getKeyFromValue("EAP-Code","Failure"));


				
				
			} catch (Exception e) {
				e.printStackTrace();
				fail("fail reason: "+e.getMessage());
			}
		}
		
		//-------------------------------------------------------------------------
		// Test getKeyFromValue method of Dictionary
		// Test cases:
		//   1. for each valid iAttId : Attribute Id - type int
		//		       and strValueName : Value Name - type String
		//-------------------------------------------------------------------------
		@Test
		public void testGetAttributeIntValueByIntAttrId(){
			
			try {

				
				//	User Types
				assertEquals(1,dictionary.getKeyFromValue(6,"Login-User"));
				assertEquals(2,dictionary.getKeyFromValue(6,"Framed-User"));
				assertEquals(3,dictionary.getKeyFromValue(6,"Callback-Login-User"));
				assertEquals(4,dictionary.getKeyFromValue(6,"Callback-Framed-User"));
				assertEquals(5,dictionary.getKeyFromValue(6,"Outbound-User"));
				assertEquals(6,dictionary.getKeyFromValue(6,"Administrative-User"));
				assertEquals(7,dictionary.getKeyFromValue(6,"NAS-Prompt-User"));
				assertEquals(8,dictionary.getKeyFromValue(6,"Authenticate-Only"));
				assertEquals(9,dictionary.getKeyFromValue(6,"Callback-NAS-Prompt"));
				assertEquals(10,dictionary.getKeyFromValue(6,"Call-Check"));
				assertEquals(11,dictionary.getKeyFromValue(6,"Callback-Administrative"));
				assertEquals(12,dictionary.getKeyFromValue(6,"Voice"));
				assertEquals(13,dictionary.getKeyFromValue(6,"Fax"));
				assertEquals(14,dictionary.getKeyFromValue(6,"Modem-Relay"));
				assertEquals(15,dictionary.getKeyFromValue(6,"IAPP-Register"));
				assertEquals(16,dictionary.getKeyFromValue(6,"IAPP-AP-Check"));
				assertEquals(17,dictionary.getKeyFromValue(6,"Authorize-Only"));
				
				//Framed Protocols
				assertEquals(1,dictionary.getKeyFromValue(7,"PPP"));
				assertEquals(2,dictionary.getKeyFromValue(7,"SLIP"));
				assertEquals(3,dictionary.getKeyFromValue(7,"ARAP"));
				assertEquals(4,dictionary.getKeyFromValue(7,"Gandalf-SLML"));
				assertEquals(5,dictionary.getKeyFromValue(7,"Xylogics-IPX-SLIP"));
				assertEquals(6,dictionary.getKeyFromValue(7,"X.75-Synchronous"));
				assertEquals(7,dictionary.getKeyFromValue(7,"GPRS-PDP-Context"));
				
				
				//Framed Routing Values
				assertEquals(0,dictionary.getKeyFromValue(10,"None"));
				assertEquals(1,dictionary.getKeyFromValue(10,"Broadcast"));
				assertEquals(2,dictionary.getKeyFromValue(10,"Listen"));
				assertEquals(3,dictionary.getKeyFromValue(10,"Broadcast-Listen"));
				
				
				//Framed Compression Types
				assertEquals(0,dictionary.getKeyFromValue(13,"None"));
				assertEquals(1,dictionary.getKeyFromValue(13,"Van-Jacobson-TCP-IP"));
				assertEquals(2,dictionary.getKeyFromValue(13,"IPX-Header-Compression"));
				assertEquals(3,dictionary.getKeyFromValue(13,"Stac-LZS"));

				//Login Services
				assertEquals(0,dictionary.getKeyFromValue(15,"Telnet"));
				assertEquals(1,dictionary.getKeyFromValue(15,"Rlogin"));
				assertEquals(2,dictionary.getKeyFromValue(15,"TCP-Clear"));
				assertEquals(3,dictionary.getKeyFromValue(15,"PortMaster"));
				assertEquals(4,dictionary.getKeyFromValue(15,"LAT"));
				assertEquals(5,dictionary.getKeyFromValue(15,"X25-PAD"));
				assertEquals(6,dictionary.getKeyFromValue(15,"X25-T3POS"));
				assertEquals(7,dictionary.getKeyFromValue(15,"TCP-Clear-Quiet"));

				//Login-TCP-Port
				assertEquals(23,dictionary.getKeyFromValue(16,"Telnet"));
				assertEquals(513,dictionary.getKeyFromValue(16,"Rlogin"));
				assertEquals(514,dictionary.getKeyFromValue(16,"Rsh"));
				
				//Status Types
				assertEquals(1,dictionary.getKeyFromValue(40,"Start"));
				assertEquals(2,dictionary.getKeyFromValue(40,"Stop"));
				assertEquals(3,dictionary.getKeyFromValue(40,"Interim-Update"));
//				assertEquals(3,dictionary.getKeyFromValue(40,"Alive"));
				assertEquals(7,dictionary.getKeyFromValue(40,"Accounting-On"));
				assertEquals(8,dictionary.getKeyFromValue(40,"Accounting-Off"));
				assertEquals(9,dictionary.getKeyFromValue(40,"Tunnel-Start"));
				assertEquals(10,dictionary.getKeyFromValue(40,"Tunnel-Stop"));
				assertEquals(11,dictionary.getKeyFromValue(40,"Tunnel-Reject"));
				assertEquals(12,dictionary.getKeyFromValue(40,"Tunnel-Link-Start"));
				assertEquals(13,dictionary.getKeyFromValue(40,"Tunnel-Link-Stop"));
				assertEquals(14,dictionary.getKeyFromValue(40,"Tunnel-Link-Reject"));
				assertEquals(15,dictionary.getKeyFromValue(40,"Failed"));
				
				//Authentication Types
				assertEquals(1,dictionary.getKeyFromValue(45,"RADIUS"));
				assertEquals(2,dictionary.getKeyFromValue(45,"Local"));
				assertEquals(3,dictionary.getKeyFromValue(45,"Remote"));
				assertEquals(4,dictionary.getKeyFromValue(45,"Diameter"));
				
				//Termination Options
				assertEquals(0,dictionary.getKeyFromValue(29,"Default"));
				assertEquals(1,dictionary.getKeyFromValue(29,"RADIUS-Request"));
				
				//NAS Port Types
				assertEquals(0,dictionary.getKeyFromValue(61,"Async"));
				assertEquals(1,dictionary.getKeyFromValue(61,"Sync"));
				assertEquals(2,dictionary.getKeyFromValue(61,"ISDN"));
				assertEquals(3,dictionary.getKeyFromValue(61,"ISDN-V120"));
				assertEquals(4,dictionary.getKeyFromValue(61,"ISDN-V110"));
				assertEquals(5,dictionary.getKeyFromValue(61,"Virtual"));
				assertEquals(6,dictionary.getKeyFromValue(61,"PIAFS"));
				assertEquals(7,dictionary.getKeyFromValue(61,"HDLC-Clear-Channel"));
				assertEquals(8,dictionary.getKeyFromValue(61,"X.25"));
				assertEquals(9,dictionary.getKeyFromValue(61,"X.75"));
				assertEquals(10,dictionary.getKeyFromValue(61,"G.3-Fax"));
				assertEquals(11,dictionary.getKeyFromValue(61,"SDSL"));
				assertEquals(12,dictionary.getKeyFromValue(61,"ADSL-CAP"));
				assertEquals(13,dictionary.getKeyFromValue(61,"ADSL-DMT"));
				assertEquals(14,dictionary.getKeyFromValue(61,"IDSL"));
				assertEquals(15,dictionary.getKeyFromValue(61,"Ethernet"));
				assertEquals(16,dictionary.getKeyFromValue(61,"xDSL"));
				assertEquals(17,dictionary.getKeyFromValue(61,"Cable"));
				assertEquals(18,dictionary.getKeyFromValue(61,"Wireless-Other"));
				assertEquals(19,dictionary.getKeyFromValue(61,"Wireless-802.11"));
				assertEquals(20,dictionary.getKeyFromValue(61,"Token-Ring"));
				assertEquals(21,dictionary.getKeyFromValue(61,"FDDI"));
				assertEquals(22,dictionary.getKeyFromValue(61,"Wireless-CDMA2000"));
				assertEquals(23,dictionary.getKeyFromValue(61,"Wireless-UMTS"));
				assertEquals(24,dictionary.getKeyFromValue(61,"Wireless-1X-EV"));
				assertEquals(25,dictionary.getKeyFromValue(61,"IAPP"));
				assertEquals(26,dictionary.getKeyFromValue(61,"FTTP"));
				
				
				//Acct Terminate Causes
				assertEquals(1,dictionary.getKeyFromValue(49,"User-Request"));
				assertEquals(2,dictionary.getKeyFromValue(49,"Lost-Carrier"));
				assertEquals(3,dictionary.getKeyFromValue(49,"Lost-Service"));
				assertEquals(4,dictionary.getKeyFromValue(49,"Idle-Timeout"));
				assertEquals(5,dictionary.getKeyFromValue(49,"Session-Timeout"));
				assertEquals(6,dictionary.getKeyFromValue(49,"Admin-Reset"));
				assertEquals(7,dictionary.getKeyFromValue(49,"Admin-Reboot"));
				assertEquals(8,dictionary.getKeyFromValue(49,"Port-Error"));
				assertEquals(9,dictionary.getKeyFromValue(49,"NAS-Error"));
				assertEquals(10,dictionary.getKeyFromValue(49,"NAS-Request"));
				assertEquals(11,dictionary.getKeyFromValue(49,"NAS-Reboot"));
				assertEquals(12,dictionary.getKeyFromValue(49,"Port-Unneeded"));
				assertEquals(13,dictionary.getKeyFromValue(49,"Port-Preempted"));
				assertEquals(14,dictionary.getKeyFromValue(49,"Port-Suspended"));
				assertEquals(15,dictionary.getKeyFromValue(49,"Service-Unavailable"));
				assertEquals(16,dictionary.getKeyFromValue(49,"Callback"));
				assertEquals(17,dictionary.getKeyFromValue(49,"User-Error"));
				assertEquals(18,dictionary.getKeyFromValue(49,"Host-Request"));
				assertEquals(19,dictionary.getKeyFromValue(49,"Supplicant-Restart"));
				assertEquals(20,dictionary.getKeyFromValue(49,"Reauthentication-Failure"));
				assertEquals(21,dictionary.getKeyFromValue(49,"Port-Reinit"));
				assertEquals(22,dictionary.getKeyFromValue(49,"Port-Disabled"));


				assertEquals(3,dictionary.getKeyFromValue(64,"L2TP"));
				assertEquals(1,dictionary.getKeyFromValue(65,"IP"));
				assertEquals(1,dictionary.getKeyFromValue(64,"PPTP"));
				assertEquals(2,dictionary.getKeyFromValue(64,"L2F"));
				assertEquals(3,dictionary.getKeyFromValue(64,"L2TP"));
				assertEquals(4,dictionary.getKeyFromValue(64,"ATMP"));
				assertEquals(5,dictionary.getKeyFromValue(64,"VTP"));
				assertEquals(6,dictionary.getKeyFromValue(64,"AH"));
				assertEquals(7,dictionary.getKeyFromValue(64,"IP-IP"));
				assertEquals(8,dictionary.getKeyFromValue(64,"MIN-IP-IP"));
				assertEquals(9,dictionary.getKeyFromValue(64,"ESP"));
				assertEquals(10,dictionary.getKeyFromValue(64,"GRE"));
				assertEquals(11,dictionary.getKeyFromValue(64,"DVS"));
				assertEquals(12,dictionary.getKeyFromValue(64,"IP-in-IP-Tunneling"));
				assertEquals(13,dictionary.getKeyFromValue(64,"VLAN"));

				assertEquals(0,dictionary.getKeyFromValue(76,"No-Echo"));
				assertEquals(1,dictionary.getKeyFromValue(76,"Echo"));
				
				//Error causes
				assertEquals(201,dictionary.getKeyFromValue(101,"Residual-Context-Removed"));
				assertEquals(202,dictionary.getKeyFromValue(101,"Invalid-EAP-Packet"));
//				assertEquals(401,dictionary.getKeyFromValue(101,"Unsupported-Attribute"));
//				assertEquals(402,dictionary.getKeyFromValue(101,"Missing-Attribute"));
//				assertEquals(403,dictionary.getKeyFromValue(101,"NAS-Identification-Mismatch"));
//				assertEquals(404,dictionary.getKeyFromValue(101,"Invalid-Request"));
//				assertEquals(405,dictionary.getKeyFromValue(101,"Unsupported-Service"));
//				assertEquals(406,dictionary.getKeyFromValue(101,"Unsupported-Extension"));
//				assertEquals(501,dictionary.getKeyFromValue(101,"Administratively-Prohibited"));
//				assertEquals(502,dictionary.getKeyFromValue(101,"Proxy-Request-Not-Routable"));
//				assertEquals(503,dictionary.getKeyFromValue(101,"Session-Context-Not-Found"));
//				assertEquals(504,dictionary.getKeyFromValue(101,"Session-Context-Not-Removable"));
//				assertEquals(505,dictionary.getKeyFromValue(101,"Proxy-Processing-Error"));
//				assertEquals(506,dictionary.getKeyFromValue(101,"Resources-Unavailable"));
//				assertEquals(507,dictionary.getKeyFromValue(101,"Request-Initiated"));


//				assertEquals(0,dictionary.getKeyFromValue(1000,"Local"));
//				assertEquals(1,dictionary.getKeyFromValue(1000,"System"));
//				assertEquals(2,dictionary.getKeyFromValue(1000,"SecurID"));
//				assertEquals(3,dictionary.getKeyFromValue(1000,"Crypt-Local"));
//				assertEquals(4,dictionary.getKeyFromValue(1000,"Reject"));
//				assertEquals(5,dictionary.getKeyFromValue(1000,"ActivCard"));
//				assertEquals(6,dictionary.getKeyFromValue(1000,"EAP"));
//				assertEquals(7,dictionary.getKeyFromValue(1000,"ARAP"));
//
//				assertEquals(254,dictionary.getKeyFromValue(1000,"Accept"));
//				assertEquals(1024,dictionary.getKeyFromValue(1000,"PAP"));
//				assertEquals(1025,dictionary.getKeyFromValue(1000,"CHAP"));
//				assertEquals(1026,dictionary.getKeyFromValue(1000,"LDAP"));
//				assertEquals(1027,dictionary.getKeyFromValue(1000,"PAM"));
//				assertEquals(1028,dictionary.getKeyFromValue(1000,"MS-CHAP"));
//				assertEquals(1029,dictionary.getKeyFromValue(1000,"Kerberos"));
//				assertEquals(1030,dictionary.getKeyFromValue(1000,"CRAM"));
//				assertEquals(1031,dictionary.getKeyFromValue(1000,"NS-MTA-MD5"));
//				assertEquals(1033,dictionary.getKeyFromValue(1000,"SMB"));

//				assertEquals(0,dictionary.getKeyFromValue(1011,"Local"));
//				assertEquals(0,dictionary.getKeyFromValue(1012,"Local"));
//				assertEquals(0,dictionary.getKeyFromValue(1013,"Local"));
//				assertEquals(0,dictionary.getKeyFromValue(1014,"Local"));
//
//				assertEquals(0,dictionary.getKeyFromValue(500,"No"));
//				assertEquals(1,dictionary.getKeyFromValue(500,"Yes"));
//
//				assertEquals(1,dictionary.getKeyFromValue(1047,"Access-Request"));
//				assertEquals(2,dictionary.getKeyFromValue(1047,"Access-Accept"));
//				assertEquals(3,dictionary.getKeyFromValue(1047,"Access-Reject"));
//				assertEquals(4,dictionary.getKeyFromValue(1047,"Accounting-Request"));
//				assertEquals(5,dictionary.getKeyFromValue(1047,"Accounting-Response"));
//				assertEquals(6,dictionary.getKeyFromValue(1047,"Accounting-Status"));
//				assertEquals(7,dictionary.getKeyFromValue(1047,"Password-Request"));
//				assertEquals(8,dictionary.getKeyFromValue(1047,"Password-Accept"));
//				assertEquals(9,dictionary.getKeyFromValue(1047,"Password-Reject"));
//				assertEquals(10,dictionary.getKeyFromValue(1047,"Accounting-Message"));
//				assertEquals(11,dictionary.getKeyFromValue(1047,"Access-Challenge"));
//				assertEquals(12,dictionary.getKeyFromValue(1047,"Status-Server"));
//				assertEquals(13,dictionary.getKeyFromValue(1047,"Status-Client"));
//				assertEquals(40,dictionary.getKeyFromValue(1047,"Disconnect-Request"));
//				assertEquals(41,dictionary.getKeyFromValue(1047,"Disconnect-ACK"));    
//				assertEquals(42,dictionary.getKeyFromValue(1047,"Disconnect-NAK"));    
//				assertEquals(43,dictionary.getKeyFromValue(1047,"CoA-Request"));       
//				assertEquals(44,dictionary.getKeyFromValue(1047,"CoA-ACK"));           
//				assertEquals(45,dictionary.getKeyFromValue(1047,"CoA-NAK"));
//				assertEquals(50,dictionary.getKeyFromValue(1047,"IP-Address-Allocate"));
//				assertEquals(51,dictionary.getKeyFromValue(1047,"IP-Address-Release"));
//				
//				assertEquals(1,dictionary.getKeyFromValue(1080,"Access-Request"));
//				assertEquals(2,dictionary.getKeyFromValue(1080,"Access-Accept"));
//				assertEquals(3,dictionary.getKeyFromValue(1080,"Access-Reject"));
//				assertEquals(4,dictionary.getKeyFromValue(1080,"Accounting-Request"));
//				assertEquals(5,dictionary.getKeyFromValue(1080,"Accounting-Response"));
//				assertEquals(6,dictionary.getKeyFromValue(1080,"Accounting-Status"));
//				assertEquals(7,dictionary.getKeyFromValue(1080,"Password-Request"));
//				assertEquals(8,dictionary.getKeyFromValue(1080,"Password-Accept"));
//				assertEquals(9,dictionary.getKeyFromValue(1080,"Password-Reject"));
//				assertEquals(10,dictionary.getKeyFromValue(1080,"Accounting-Message"));
//				assertEquals(11,dictionary.getKeyFromValue(1080,"Access-Challenge"));
//				assertEquals(12,dictionary.getKeyFromValue(1080,"Status-Server"));
//				assertEquals(13,dictionary.getKeyFromValue(1080,"Status-Client"));
//
//				assertEquals(0,dictionary.getKeyFromValue(1018,"None"));
//				assertEquals(1,dictionary.getKeyFromValue(1018,"Identity"));
//				assertEquals(2,dictionary.getKeyFromValue(1018,"Notification"));
//				assertEquals(3,dictionary.getKeyFromValue(1018,"NAK"));
//				assertEquals(4,dictionary.getKeyFromValue(1018,"MD5-Challenge"));
//				assertEquals(5,dictionary.getKeyFromValue(1018,"One-Time-Password"));
//				assertEquals(6,dictionary.getKeyFromValue(1018,"Generic-Token-Card"));
//				assertEquals(9,dictionary.getKeyFromValue(1018,"RSA-Public-Key"));
//				assertEquals(10,dictionary.getKeyFromValue(1018,"DSS-Unilateral"));
//				assertEquals(11,dictionary.getKeyFromValue(1018,"KEA"));
//				assertEquals(12,dictionary.getKeyFromValue(1018,"KEA-Validate"));
//				assertEquals(11,dictionary.getKeyFromValue(1018,"KEA"));
//				assertEquals(13,dictionary.getKeyFromValue(1018,"EAP-TLS"));
//				assertEquals(14,dictionary.getKeyFromValue(1018,"Defender-Token"));
//				assertEquals(15,dictionary.getKeyFromValue(1018,"RSA-SecurID-EAP"));
//				assertEquals(16,dictionary.getKeyFromValue(1018,"Arcot-Systems-EAP"));
//				assertEquals(17,dictionary.getKeyFromValue(1018,"Cisco-LEAP"));
//				assertEquals(18,dictionary.getKeyFromValue(1018,"Nokia-IP-Smart-Card"));
//				assertEquals(18,dictionary.getKeyFromValue(1018,"SIM"));
//				assertEquals(19,dictionary.getKeyFromValue(1018,"SRP-SHA1-Part-1"));
//				assertEquals(20,dictionary.getKeyFromValue(1018,"SRP-SHA1-Part-2"));
//				assertEquals(21,dictionary.getKeyFromValue(1018,"EAP-TTLS"));
//				assertEquals(22,dictionary.getKeyFromValue(1018,"Remote-Access-Service"));
//				assertEquals(23,dictionary.getKeyFromValue(1018,"UMTS"));
//				assertEquals(24,dictionary.getKeyFromValue(1018,"EAP-3Com-Wireless"));
//				assertEquals(25,dictionary.getKeyFromValue(1018,"PEAP"));
//				assertEquals(26,dictionary.getKeyFromValue(1018,"MS-EAP-Authentication"));
//				assertEquals(27,dictionary.getKeyFromValue(1018,"MAKE"));
//				assertEquals(28,dictionary.getKeyFromValue(1018,"CRYPTOCard"));
//				assertEquals(29,dictionary.getKeyFromValue(1018,"EAP-MSCHAP-V2"));
//				assertEquals(30,dictionary.getKeyFromValue(1018,"DynamID"));
//				assertEquals(31,dictionary.getKeyFromValue(1018,"Rob-EAP"));
//				assertEquals(32,dictionary.getKeyFromValue(1018,"SecurID-EAP"));
//				assertEquals(33,dictionary.getKeyFromValue(1018,"MS-Authentication-TLV"));
//				assertEquals(34,dictionary.getKeyFromValue(1018,"SentriNET"));
//				assertEquals(35,dictionary.getKeyFromValue(1018,"EAP-Actiontec-Wireless"));
//				assertEquals(36,dictionary.getKeyFromValue(1018,"Cogent-Biomentric-EAP"));
//				assertEquals(37,dictionary.getKeyFromValue(1018,"AirFortress-EAP"));
//				assertEquals(38,dictionary.getKeyFromValue(1018,"EAP-HTTP-Digest"));
//				assertEquals(39,dictionary.getKeyFromValue(1018,"SecuriSuite-EAP"));
//				assertEquals(40,dictionary.getKeyFromValue(1018,"DeviceConnect-EAP"));
//				assertEquals(41,dictionary.getKeyFromValue(1018,"EAP-SPEKE"));
//				assertEquals(42,dictionary.getKeyFromValue(1018,"EAP-MOBAC"));
//				assertEquals(26,dictionary.getKeyFromValue(1018,"Microsoft-MS-CHAPv2"));
//				assertEquals(29,dictionary.getKeyFromValue(1018,"Cisco-MS-CHAPv2"));
//				assertEquals(26,dictionary.getKeyFromValue(1018,"MS-CHAP-V2"));
//
//				assertEquals(0,dictionary.getKeyFromValue(1019,"No"));
//				assertEquals(1,dictionary.getKeyFromValue(1019,"Yes"));
//
//				assertEquals(1,dictionary.getKeyFromValue(1021,"Request"));
//				assertEquals(2,dictionary.getKeyFromValue(1021,"Response"));
//				assertEquals(3,dictionary.getKeyFromValue(1021,"Success"));
//				assertEquals(4,dictionary.getKeyFromValue(1021,"Failure"));
//				
			} catch (Exception e) {
				e.printStackTrace();
				fail("fail reason: "+e.getMessage());
				
			}
		}
		
		
		@Test
		public void testgetVendorAttributeName(){
			try{
				System.out.println("VSA name"+dictionary.getAttributeName(21067,0));
				assertEquals("24Online-AVPair", dictionary.getAttributeName(21067,0));
				assertEquals("Tekelec-AVPair",dictionary.getAttributeName(21067,1) );
				assertEquals("Cyberoam-AVPair", dictionary.getAttributeName(21067,2));
				assertEquals("IP-Pool-AVPair", dictionary.getAttributeName(21067,3));
				assertEquals("Rating-AVPair", dictionary.getAttributeName(21067,4));
				
				
			}catch (Exception e) {
				fail("fail reason :"+e.getMessage());
			}
			
			
		}
		
		@Test
		public void testgetVendorAttribute(){
			try{
				IRadiusAttribute tempAttribute = dictionary.getAttribute(21067,1);
				assertEquals(true, tempAttribute instanceof StringAttribute);
			
				tempAttribute.setVendorID(21067);
				assertEquals(21067,tempAttribute.getVendorID());
										
			}catch (Exception e) {
				fail("fail reason :"+e.getMessage());
			}
			
			
		}
		
		@Test
		public void testgetVendorAttributeID(){
			
			try{
							
//				assertEquals("0", dictionary.getVendorAttributeID("elitecore","24Online-AVPair"));
//				assertEquals("1", dictionary.getVendorAttributeID("elitecore","Tekelec-AVPair") );
//				assertEquals("2", dictionary.getVendorAttributeID("elitecore","Cyberoam-AVPair"));
//				assertEquals("3", dictionary.getVendorAttributeID("elitecore","IP-Pool-AVPair"));
//				assertEquals("4", dictionary.getVendorAttributeID("elitecore","Rating-AVPair"));
				
				
			}catch (Exception e) {
				fail("fail reason :"+e.getMessage());
			}
			
		}
		
/*
		public static void	testgetVendorAttributeStringValue(){
			
			try{
				Dictionary dictionary = dictionary;
							
				System.out.println(" check name " +dictionary.getVendorAttributeStringValue(21067,5,"1"));
				assertEquals("CustomerType=Prepaid", dictionary.getVendorAttributeStringValue(21067,5,"1"));
				assertEquals("Status=Success", dictionary.getVendorAttributeStringValue(21067,5,"2") );
				assertEquals("Status=Failure", dictionary.getVendorAttributeStringValue(21067,5,"3"));
				
				
				
			}catch (Exception e) {
				fail("fail reason :"+e.getMessage());
			}
		}


		
		
		
		public static void testgetVendorAttributeIntValue(){
			try{
				Dictionary dictionary = dictionary;
					
				System.out.println("check attribute int value " +dictionary.getVendorAttributeIntValue(21067,5,"CustomerType=Prepaid"));
				assertEquals(1,dictionary.getVendorAttributeIntValue(21067,5,"CustomerType=Prepaid"));
				assertEquals(2,dictionary.getVendorAttributeIntValue(21067,5,"Status=Success"));
				assertEquals(3,dictionary.getVendorAttributeIntValue(21067,5,"Status=Failure"));
				
			}catch (Exception e) {
				fail("fail reason :"+e.getMessage());
			}
			

		}
		
		*/
}
