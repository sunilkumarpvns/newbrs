package com.elitecore.coreeap.fsm;

public class EAPFailureReasonConstants {
	public static final String USER_NOT_FOUND = "User not found";
	public static final String INVALID_PASSWORD = "Invalid Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_CHAP_PASSWORD = "Invalid CHAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_MSCHAPv2_PASSWORD = "Invalid MSCHAPv2-Password";	//NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_EAPMD5_PASSWORD = "Invalid EAP-MD5 Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_SIM_PASSWORD = "Invalid EAP-SIM Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String TLS_NEGOTIATION_FAILED = "TLS Negotiation Failed";
	public static final String EAP_METHOD_NOT_SUPPORTED = "EAP method not supported/enabled";
	public static final String INVALID_MSCHAP_PASSWORD = "Invalid MSCHAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String SIM_FAILURE = "EAP-SIM Failure";
	public static final String INVALID_AKA_PASSWORD = "Invalid EAP-AKA Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String AKA_FAILURE = "EAP-AKA Failure";
	public static final String INVALID_EAPGTC_PASSWORD = "Invalid EAP-GTC Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String AKA_PRIME_FAILURE = "EAP-AKA-PRIME Failure";
	public static final String INVALID_AKA_PRIME_PASSWORD = "Invalid EAP-AKA-PRIME Password"; //NOSONAR - Reason: Credentials should not be hard-coded
}
