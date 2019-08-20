package com.elitecore.elitesm.util.constants;

public class EAPConfigConstant extends BaseConstant{
	public static final String MODULE_NAME = "EAP Config Constant";

	public static final int MD5_CHALLENGE =4;
	public static final String MD5_CHALLENGE_STR ="004";
	public static final int OTP_CHALLENGE =5;
	public static final String OTP_CHALLENGE_STR ="005";
	public static final int GTC =6;
	public static final String GTC_STR ="006";
	public static final int TLS =13;
	public static final String TLS_STR ="013";
	public static final int SIM =18;
	public static final String SIM_STR ="018";
	public static final int TTLS =21;
	public static final String TTLS_STR ="021";
	public static final int AKA_PRIME = 50;
	public static final String AKA_PRIME_STR = "050";
	public static final int AKA =23;
	public static final String AKA_STR ="023";
	public static final int PEAP =25;
	public static final String PEAP_STR ="025";
	public static final int MSCHAPv2 =26;
	public static final String MSCHAPv2_STR ="026";
	
	public static final String MD5_CHALLAGNE_VALUE = "MD5" ;
	public static final String GTC_VALUE = "GTC" ;
	public static final String TLS_VALUE = "TLS" ;
	public static final String SIM_VALUE = "SIM" ;
	public static final String TTLS_VALUE = "TTLS" ;
	public static final String AKA_VALUE = "AKA" ;
	public static final String AKA_PRIME_VALUE = "AKA'" ;
	public static final String PEAP_VALUE = "PEAP" ; 
	public static final String MSCHAPv2_VALUE = "MS-CHAPv2";

	
	//TTLS Negotiation Method
	public static final String EAP_MD5_STR="EAP-MD5";
	public static final String EAP_GTC_STR = "EAP-GTC";
	public static final String EAP_MsCHAPv2_STR = "EAP-MsCHAPv2";
	
	public static final Integer EAP_MD5 = 4;
	public static final Integer EAP_GTC = 6;
	public static final Integer EAP_MsCHAPv2 = 26;
	
	//Certificate type
	public static final String RSA_STR = "RSA" ;
	public static final String RSA_DH_STR = "RSA-DH";
	public static final String DSS_STR = "DSS";
	public static final String DSS_DH_STR = "DSS-DH";
	
	public static final Integer RSA = 1;
	public static final Integer RSA_DH = 3;
	public static final Integer DSS = 2;
	public static final Integer DSS_DH = 4;
	
	//TLS Version
	public static final String TLSv_1 = "TLSv1";
	public static final String TLSv_1_1 = "TLSv1.1";
	public static final String TLSv_1_2 = "TLSv1.2";
	

}
