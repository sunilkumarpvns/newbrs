
package com.elitecore.aaa.util.mbean;

import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class AAASupportedRFCController extends BaseMBeanController implements AAASupportedRFCControllerMBean{

	@Override
	public String getName() {
		return MBeanConstants.SUPPORTED_RFC;
	}

	@Override
	public Map<String, String> readSupportedRFCs() {
		
		Map<String, String> supportedRFCList =new LinkedHashMap<String, String>();
		
		supportedRFCList.put("RFC 1157", "A Simple Network Management Protocol (SNMP) *");
		supportedRFCList.put("RFC 1901", "Introduction to Community-based SNMPv2 *");
		supportedRFCList.put("RFC 1902", "Structure of Management Information for Version 2 of the SNMPv2 *");
		supportedRFCList.put("RFC 1903", "Textual Conventions for Version 2 of theSNMPv2 *");
		supportedRFCList.put("RFC 1904", "Conformance Statements for Version 2 of the SNMPv2 *");
		supportedRFCList.put("RFC 1905", "Protocol Operations for Version 2 of the SNMPv2 *");
		
		supportedRFCList.put("RFC 2246", "The TLS Protocol Version 1.0");
		supportedRFCList.put("RFC 2254", "The String Representation of LDAP Search Filters");
		supportedRFCList.put("RFC 2433", "Microsoft PPP CHAP Extensions");
		supportedRFCList.put("RFC 2548", "Microsoft Vendor-specific RADIUS Attributes");
		supportedRFCList.put("RFC 2607", "Proxy Chaining and Policy Implementation in Roaming");
		supportedRFCList.put("RFC 2716", "PPP EAP TLS Authentication Protocol");
		supportedRFCList.put("RFC 2759", "Microsoft PPP CHAP Extensions, Version 2");
		supportedRFCList.put("RFC 2809", "Implementation of L2TP Compulsory Tunneling via RADIUS");
		supportedRFCList.put("RFC 2865", "Remote Authentication Dial In User Service (RADIUS)");
		supportedRFCList.put("RFC 2866", "RADIUS Accounting");
		supportedRFCList.put("RFC 2867", "RADIUS Accounting Modifications for Tunnel Protocol Support");
		supportedRFCList.put("RFC 2868", "RADIUS Attributes for Tunnel Protocol Support");
		supportedRFCList.put("RFC 2869", "RADIUS Extensions");
		supportedRFCList.put("RFC 2882", "Network Access Servers Requirements: Extended RADIUS practices");
		supportedRFCList.put("RFC 2903", "Generic AAA Architecture");
		supportedRFCList.put("RFC 2904", "AAA Authorization Framework");
		supportedRFCList.put("RFC 2905", "AAA Authorization Application Examples");
		supportedRFCList.put("RFC 2906", "AAA Authorization Requirements");
		
		supportedRFCList.put("RFC 3141", "CDMA2000 Wireless Data Requirements for AAA");
		supportedRFCList.put("RFC 3162", "RADIUS and IPv6");
		supportedRFCList.put("RFC 3377", "Lightweight Directory Access Protocol (v3): Technical Specification *");
		supportedRFCList.put("RFC 3575", "IANA considerations for RADIUS");
		supportedRFCList.put("RFC 3576", "Dynamic Authorization Extensions to Remote Authentication Dial In User Service (RADIUS)");
		supportedRFCList.put("RFC 3579", "RADIUS Support For EAP");
		supportedRFCList.put("RFC 3580", "IEEE 802.1x Remote Authentication Dial In User Service (RADIUS) Usage Guidelines");
		supportedRFCList.put("RFC 3588", "Diameter Base Protocol");
		supportedRFCList.put("RFC 3748", "Extensible Authentication Protocol (EAP)");
		
		supportedRFCList.put("RFC 4005", "Diameter Network Access Server Application");
		supportedRFCList.put("RFC 4006", "Diameter Credit-Control Application");
		supportedRFCList.put("RFC 4017", "Extensible Authentication Protocol (EAP) Method Requirements for Wireless LANs");
		supportedRFCList.put("RFC 4072", "Diameter Extensible Authentication Protocol (EAP) Application");
		
		supportedRFCList.put("RFC 4137", "EAP State Machine for the architecture");
		supportedRFCList.put("RFC 4186", "EAP Method for Global System for Mobile Communications (GSM) Subscriber Identity Modules (EAP-SIM)");
		supportedRFCList.put("RFC 4187", "EAP Method for 3rd Generation Authentication and Key Agreement (EAP-AKA)");
		supportedRFCList.put("RFC 4282", "The Network Access Identifier");
		supportedRFCList.put("RFC 4325", "Internet X.509 Public Key Infrastructure Authority Information Access Certificate Revocation List (CRL) Extension (Updates 3280)");
		supportedRFCList.put("RFC 4346", "The Transport Layer Security (TLS) Protocol Version 1.1");
		supportedRFCList.put("RFC 4372", "Chargeable User Identity");
		supportedRFCList.put("RFC 4492", "Elliptic Curve Cryptography (ECC) Cipher Suites for Transport Layer Security (TLS)");
		supportedRFCList.put("RFC 4507", "Transport Layer Security (TLS) Session Resumption without Server-Side State");
		supportedRFCList.put("RFC 4630", "DirectoryString Processing in the Internet X.509 Public Key Infrastructure Certificate and Certificate Revocation List (CRL) Profile (Updates 3280)");
		supportedRFCList.put("RFC 4648", "The Base16, Base32, and Base64 Data Encodings");
		supportedRFCList.put("RFC 4668", "RADIUS Authentication Client MIB for IPv6 (Updates 2618)");
		supportedRFCList.put("RFC 4669", "RADIUS Authentication Server MIB for IPv6 (Updates 2619)");
		supportedRFCList.put("RFC 4670", "RADIUS Accounting Client MIB for IPv6 (Updates 2620)");
		supportedRFCList.put("RFC 4671", "RADIUS Accounting Server MIB for IPv6 (Updates 2621)");
		supportedRFCList.put("RFC 4672", "RADIUS Dynamic Authorization Client MIB");
		supportedRFCList.put("RFC 4673", "RADIUS Dynamic Authorization Server MIB");
		supportedRFCList.put("RFC 4675", "RADIUS Attributes for Virtual LAN and Priority Support");
		supportedRFCList.put("RFC 4679", "DSL Forum Vendor-Specific RADIUS Attributes");
		supportedRFCList.put("RFC 4681", "TLS User Mapping Extension");
		supportedRFCList.put("RFC 4818", "Radius Delegated-IPv6-Prefix Attribute");
		supportedRFCList.put("RFC 4849", "RADIUS Filter Rule Attribute");
		
		supportedRFCList.put("RFC 5054", "Using the Secure Remote Password (SRP) Protocol for TLS Authentication");
		supportedRFCList.put("RFC 5080", "RADIUS Implementation Issues and Suggested Fixes");
		supportedRFCList.put("RFC 5090", "RADIUS Extension for Digest Authentication");	
		supportedRFCList.put("RFC 5216", "The EAP-TLS Authentication Protocol");
		supportedRFCList.put("RFC 5246", "The Transport Layer Security (TLS) Protocol Version 1.2 (Updates 4492)");
		supportedRFCList.put("RFC 5281", "EAP Tunneled Transport Layer Security Authenticated Protocol Version 0 (EAP-TTLSv0)");
		supportedRFCList.put("RFC 5448", "Improved Extensible Authentication Protocol Method for 3rd Generation Authentication and Key Agreement (EAP-AKA')");
		supportedRFCList.put("RFC 5607", "RADIUS Authorization for NAS Management");
		supportedRFCList.put("RFC 5608", "RADIUS Usage for SNMP Transport Models");
		supportedRFCList.put("RFC 5729", "Clarifications on the Routing of Diameter Requests Based on the Username and the Realm");
		supportedRFCList.put("RFC 5746", "Transport Layer Security (TLS) Renegotiation Indication Extension");
		supportedRFCList.put("RFC 5878", "Transport Layer Security (TLS) Authorization Extensions");
		supportedRFCList.put("RFC 5997", "Use of Status-Server Packets in the Remote Authentication Dial In User Service (RADIUS) Protocol");
		
		supportedRFCList.put("RFC 6065", "Using Authentication, Authorization, and Accounting Services to Dynamically Provision View-Based Access Control Model User-to-Group Mappings");
		supportedRFCList.put("RFC 6066", "Transport Layer Security (TLS) Extensions: Extension Definitions");
		supportedRFCList.put("RFC 6091", "Using OpenPGP Keys for Transport Layer Security (TLS) Authentication");
		supportedRFCList.put("RFC 6158", "RADIUS Design Guidelines");
		supportedRFCList.put("RFC 6455", "The WebSocket Protocol");
		supportedRFCList.put("RFC 6519", "RADIUS Extensions for Dual-Stack Lite");
		supportedRFCList.put("RFC 6818", "Internet X.509 Public Key Infrastructure Certificate and Certificate Revocation List (CRL) Profile (Updates 3280,5280)");
		
		supportedRFCList.put("3GPP", "A.S0008-C IS 835C IOS-CDMA2000");
		supportedRFCList.put("3GPP Standard", "32299-860");
		supportedRFCList.put("ETSI", "TS129-061-V8.1.0 UMTS LTE;  Interworking between PLMN supporting packet based services and PDN");
		supportedRFCList.put("Draft-ietf-pppext-eap-ttls-0.1.txt", "EAP Tunneled TLS Authenticated Protocol");
		supportedRFCList.put("Draft-lior-RADIUS-prepaid-extensions-14.txt", "Prepaid Extensions to Remote Authentication Dial-In User Service (RADIUS)");
		supportedRFCList.put("Draft-Sterman-aaa-sip-01.txt", "RADIUS Extension for Digest Authentication");
		supportedRFCList.put("Draft-kamath-pppext-eap-mschapv2-02.txt", "Microsoft EAP CHAP Extensions");
		supportedRFCList.put("Draft-kamath-pppext-peapv0-00.txt", "Microsoft's PEAP");
		supportedRFCList.put("Wimax NWG 1.3", "WiMax NWG 1.3 R3 Specification for RADIUS");

		return supportedRFCList;
	}

}
