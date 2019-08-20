package com.elitecore.elitesm.util.eapconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;

public class EAPConfigUtils {
	public static String convertDefaultNegotiationMethodToLabel(Long defaultNegotiationMethod) {
		
		   String strdefaultNegotiationMethod="";
		    
		   
		   if(defaultNegotiationMethod == EAPConfigConstant.TLS){
			   strdefaultNegotiationMethod="TLS";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.TTLS){
			   strdefaultNegotiationMethod="TTLS";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.SIM){
			   strdefaultNegotiationMethod="SIM";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.AKA){
			   strdefaultNegotiationMethod="AKA";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.MD5_CHALLENGE){
			   strdefaultNegotiationMethod="MD5-CHALLENGE";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.MSCHAPv2){
			   strdefaultNegotiationMethod="MS-CHAPv2";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.PEAP){
			   strdefaultNegotiationMethod="PEAP";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.GTC){
			   strdefaultNegotiationMethod="GTC";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.AKA_PRIME){
			   strdefaultNegotiationMethod="AKA'";
		   }
		   
		  return strdefaultNegotiationMethod; 
		   
	}

	public static String[] convertEnableAuthMethodToLabel(String enabledAuthMethods) {

		//Enabled Auth Methods..
		String strEnabledAuthMethods = enabledAuthMethods;
		String strLabelEnabledAuthMethods="";
		String[] resultArray = new String[10];
		if(strEnabledAuthMethods != null && strEnabledAuthMethods.length()>0){
			String[] enabledAuthMethodsArray = strEnabledAuthMethods.split(",");


			for(int i=0;i<enabledAuthMethodsArray.length;i++){

				if(strLabelEnabledAuthMethods.length()>0){

					if(EAPConfigConstant.TLS_STR.equals(enabledAuthMethodsArray[i])){ 
						strLabelEnabledAuthMethods +=",TLS";
						resultArray[1]=EAPConfigConstant.TLS_STR;
					}else if(EAPConfigConstant.TTLS_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",TTLS";
						resultArray[2]=EAPConfigConstant.TTLS_STR;
					}else if(EAPConfigConstant.PEAP_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",PEAP";
						resultArray[3]=EAPConfigConstant.PEAP_STR;
					}else if(EAPConfigConstant.SIM_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",SIM";
						resultArray[4]=EAPConfigConstant.SIM_STR;
					}else if(EAPConfigConstant.AKA_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",AKA";
						resultArray[5]=EAPConfigConstant.AKA_STR;
					}else if(EAPConfigConstant.GTC_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",GTC";
						resultArray[6]=EAPConfigConstant.GTC_STR;
					}else if(EAPConfigConstant.MSCHAPv2_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",MS-CHAPv2";
						resultArray[7]=EAPConfigConstant.MSCHAPv2_STR;
					}else if(EAPConfigConstant.MD5_CHALLENGE_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",MD5";
						resultArray[8]=EAPConfigConstant.MD5_CHALLENGE_STR;
					}else if(EAPConfigConstant.AKA_PRIME_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +=",AKA'";
						resultArray[9]=EAPConfigConstant.AKA_PRIME_STR;
					} else{
						strLabelEnabledAuthMethods = "";
					}


				}else{


					if(EAPConfigConstant.TLS_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="TLS";
						resultArray[1]=EAPConfigConstant.TLS_STR;
					}else if(EAPConfigConstant.TTLS_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods += "TTLS";
						resultArray[2]=EAPConfigConstant.TTLS_STR;
					}else if(EAPConfigConstant.PEAP_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods += "PEAP";
						resultArray[3]=EAPConfigConstant.PEAP_STR;
					}else if(EAPConfigConstant.SIM_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="SIM";
						resultArray[4]=EAPConfigConstant.SIM_STR;
					}else if(EAPConfigConstant.AKA_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods+="AKA";
						resultArray[5]=EAPConfigConstant.AKA_STR;
					}else if(EAPConfigConstant.GTC_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="GTC";
						resultArray[6]=EAPConfigConstant.GTC_STR;
					}else if(EAPConfigConstant.MSCHAPv2_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="MS-CHAPv2";
						resultArray[7]=EAPConfigConstant.MSCHAPv2_STR;
					}else if(EAPConfigConstant.MD5_CHALLENGE_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="MD5";
						resultArray[8]=EAPConfigConstant.MD5_CHALLENGE_STR;
					}else if(EAPConfigConstant.AKA_PRIME_STR.equals(enabledAuthMethodsArray[i])){
						strLabelEnabledAuthMethods +="AKA'";
						resultArray[9]=EAPConfigConstant.AKA_PRIME_STR;
					} else{
						strLabelEnabledAuthMethods = "";
					}


				}

			}

		}

		System.out.println("Enabled Auth Method :"+strLabelEnabledAuthMethods.toString());
		resultArray[0]=strLabelEnabledAuthMethods.toString();

		return resultArray;
	}
	
	public static String convertEnableAuthMethodToLabelString(String enabledAuthMethods) {
		return convertEnableAuthMethodToLabel(enabledAuthMethods)[0];
	}
	
	/**
	 * This method convert Enable Auth method to it's Type id
	 * @param enableAuthMethods name of valid Enable Auth methods(comma seperated also allow) 
	 * @param isNegotiationMethod specify whether it is Negotiation method 's Auth type
	 * @return list of Auth method type 
	 */
	public static String convertEnableAuthMethodToAuthTypeId(String enableAuthMethods,boolean isNegotiationMethod){
		String strEnabledAuthMethods = enableAuthMethods.trim();
		StringBuffer strLabelEnabledAuthMethods=new StringBuffer();
		String[] resultArray = new String[10];
		if(Strings.isNullOrBlank(strEnabledAuthMethods) == false){
			int portion = strEnabledAuthMethods.split(",").length-1;
			if(portion>0){
				
				String[] enabledAuthMethodsArray = strEnabledAuthMethods.split(",");
				
				if(enabledAuthMethodsArray != null){
					for(String enableAuthMethod : enabledAuthMethodsArray){
						if(Strings.isNullOrBlank(enableAuthMethod)==false){
							if(EAPConfigConstant.MD5_CHALLAGNE_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.MD5_CHALLENGE_STR +",");
							} else if(EAPConfigConstant.GTC_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.GTC_STR +",");
							} else if(EAPConfigConstant.TLS_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.TLS_STR +",");
							}else if(EAPConfigConstant.TTLS_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.TTLS_STR +",");
							}else if(EAPConfigConstant.PEAP_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.PEAP_STR +",");
							}else if(EAPConfigConstant.SIM_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.SIM_STR +",");
							}else if(EAPConfigConstant.AKA_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.AKA_STR +",");
							}else if(EAPConfigConstant.AKA_PRIME_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.AKA_PRIME_STR +",");
							}else if(EAPConfigConstant.MSCHAPv2_VALUE.equals(enableAuthMethod)){
								strLabelEnabledAuthMethods.append(EAPConfigConstant.MSCHAPv2_STR +",");
							}else {
								strLabelEnabledAuthMethods.append(enableAuthMethod+",");
							}
						}
					}
				}
			} else {
				//if single EAP auth method 
				if(isNegotiationMethod && "MD5-CHALLENGE".equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.MD5_CHALLENGE_STR +",");
				}else if(EAPConfigConstant.MD5_CHALLAGNE_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.MD5_CHALLENGE_STR +",");
				} else if(EAPConfigConstant.GTC_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.GTC_STR +",");
				} else if(EAPConfigConstant.TLS_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.TLS_STR +",");
				}else if(EAPConfigConstant.TTLS_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.TTLS_STR +",");
				}else if(EAPConfigConstant.PEAP_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.PEAP_STR +",");
				}else if(EAPConfigConstant.SIM_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.SIM_STR +",");
				}else if(EAPConfigConstant.AKA_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.AKA_STR +",");
				}else if(EAPConfigConstant.AKA_PRIME_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.AKA_PRIME_STR +",");
				}else if(EAPConfigConstant.MSCHAPv2_VALUE.equals(strEnabledAuthMethods)){
					strLabelEnabledAuthMethods.append(EAPConfigConstant.MSCHAPv2_STR +",");
				} else{
					strLabelEnabledAuthMethods.append(strEnabledAuthMethods+",");
				}
			}
		}
		if(Strings.isNullOrBlank(strLabelEnabledAuthMethods.toString().trim()) == false){
			String enableMethodTypeIds = strLabelEnabledAuthMethods.deleteCharAt(strLabelEnabledAuthMethods.length()-1).toString();
			return enableMethodTypeIds;
		}
		return null;
	}
	
	/**
	 * Converts EAP TTLS negotiation method id to name
	 * @param ttlsNegotiationMethod Id of TTLS negotiation method
	 * @return TTLS negotiation method name
	 */
	public static String convertEapTTLSNegotiationMethodToString(Integer ttlsNegotiationMethod){
		String negotiationMethod = "";
		if(ttlsNegotiationMethod == EAPConfigConstant.EAP_MD5){
			negotiationMethod = EAPConfigConstant.EAP_MD5_STR;
		} else if(ttlsNegotiationMethod == EAPConfigConstant.EAP_GTC){
			negotiationMethod = EAPConfigConstant.EAP_GTC_STR;
		} else if(ttlsNegotiationMethod == EAPConfigConstant.EAP_MsCHAPv2){
			negotiationMethod = EAPConfigConstant.EAP_MsCHAPv2_STR;
		}
		return negotiationMethod;
	}
	
	/**
	 * Converts EAP TTLS negotiation method name to id
	 * @param ttlsNegotiationMethodName valid ttls negotiation method name
	 * @return TTLS negotiation method id
	 */
	public static Integer convertEapTTLSNegotiationMethodStringToId(String ttlsNegotiationMethodName){
		Integer negotiationMethodId = null ;
		if(EAPConfigConstant.EAP_MD5_STR.equalsIgnoreCase(ttlsNegotiationMethodName)){
			negotiationMethodId = EAPConfigConstant.EAP_MD5;
		} else if(EAPConfigConstant.EAP_GTC_STR.equalsIgnoreCase(ttlsNegotiationMethodName)){
			negotiationMethodId = EAPConfigConstant.EAP_GTC;
		} else if( EAPConfigConstant.EAP_MsCHAPv2_STR.equalsIgnoreCase(ttlsNegotiationMethodName)){
			negotiationMethodId = EAPConfigConstant.EAP_MsCHAPv2;
		}
		return negotiationMethodId;
	}
	
	public static String convertEapCertificateTypeNameToTypeId(String certificateTypeNames){
		StringBuffer certificateTypeIds = new StringBuffer();
		if(certificateTypeNames != null){
			List<String> certificateTypeLst = Arrays.asList(certificateTypeNames.trim().split(","));
			for(String certificateType : certificateTypeLst){
				if(Strings.isNullOrBlank(certificateType) == false){
					if(certificateType.equals(EAPConfigConstant.RSA_STR)){
						certificateTypeIds.append(EAPConfigConstant.RSA+",");
					}else if(certificateType.equals(EAPConfigConstant.RSA_DH_STR)){
						certificateTypeIds.append(EAPConfigConstant.RSA_DH+",");
					}else if(certificateType.equals(EAPConfigConstant.DSS_STR)){
						certificateTypeIds.append(EAPConfigConstant.DSS+",");
					}else if(certificateType.equals(EAPConfigConstant.DSS_DH_STR)){
						certificateTypeIds.append(EAPConfigConstant.DSS_DH+",");
					} else {
						certificateTypeIds.append(certificateType+",");
					}
				}
			}
		}
		String commaSepratedCertificateTypeIds = certificateTypeIds.substring(0,certificateTypeIds.length()-1);
		return commaSepratedCertificateTypeIds;
	}
	
	public static String getCertificateTypeName(String certificateType){
		String certificateTypeName = null;
		if(Strings.isNullOrBlank(certificateType) == false){
			if(certificateType.equals(EAPConfigConstant.RSA.toString())){
				certificateTypeName = EAPConfigConstant.RSA_STR;
			}else if(certificateType.equals(EAPConfigConstant.RSA_DH.toString())){
				certificateTypeName= (EAPConfigConstant.RSA_DH_STR+",");
			}else if(certificateType.equals(EAPConfigConstant.DSS.toString())){
				certificateTypeName=(EAPConfigConstant.DSS_STR+",");
			}else if(certificateType.equals(EAPConfigConstant.DSS_DH.toString())){
				certificateTypeName=(EAPConfigConstant.DSS_DH_STR+",");
			} else {
				certificateTypeName = null;
			}
		}
		return certificateTypeName;
	}
	public static String convertEapCertificateTypeIdToTypeName(String certificateTypeIds){
		StringBuffer certificateTypeNames = new StringBuffer();
		if(certificateTypeNames != null){
			List<String> certificateTypeLst = Arrays.asList(certificateTypeIds.split(","));
			for(String certificateType : certificateTypeLst){
				if(Strings.isNullOrBlank(certificateType) == false){
					if(certificateType.equals(""+EAPConfigConstant.RSA)){
						certificateTypeNames.append(EAPConfigConstant.RSA_STR+",");
					}else if(certificateType.equals(""+EAPConfigConstant.RSA_DH)){
						certificateTypeNames.append(EAPConfigConstant.RSA_DH_STR+",");
					}else if(certificateType.equals(""+EAPConfigConstant.DSS)){
						certificateTypeNames.append(EAPConfigConstant.DSS_STR+",");
					}else if(certificateType.equals(""+EAPConfigConstant.DSS_DH)){
						certificateTypeNames.append(EAPConfigConstant.DSS_DH_STR+",");
					}
				}
			}
		}
		String commaSepratedCertificateTypeNames = certificateTypeNames.substring(0,certificateTypeNames.length()-1);
		return commaSepratedCertificateTypeNames;
	}
	
	public static String convertCipherSuitesNamesToCipherSuiteCodes(String cipherSuiteNames){
		Map<String,Integer> cipherSuiteDetails = getCipherSuiteDetailMap();
		List<String> cipherSuiteList;
		StringBuffer cipherSuiteIds =new StringBuffer();
		if(Strings.isNullOrBlank(cipherSuiteNames) == false){
			cipherSuiteList = Arrays.asList(cipherSuiteNames.split(","));
			
			for(String cipherSuiteName : cipherSuiteList){
				if(Strings.isNullOrBlank(cipherSuiteName)== false){
					if(cipherSuiteDetails.containsKey(cipherSuiteName.trim())){
						cipherSuiteIds.append(cipherSuiteDetails.get(cipherSuiteName.trim())+",");
					}else{
						cipherSuiteIds.append(cipherSuiteName.trim()+",");
					}
				}
			}
		}
		String finalCipherSuites = cipherSuiteIds.substring(0,cipherSuiteIds.length()-1);
		return finalCipherSuites;
	}
	
	public static String convertCipherSuiteCodeToCipherSuiteName(String cipherSuiteCodes){
		Collection<CipherSuites> cipherSuites= Arrays.asList(CipherSuites.values());
		List<String> cipherSuiteList;
		StringBuffer cipherSuiteNames =new StringBuffer();
		if(Strings.isNullOrBlank(cipherSuiteCodes) == false){
			cipherSuiteList = Arrays.asList(cipherSuiteCodes.split(","));
			
			for(String cipherSuiteCode : cipherSuiteList){
				if(Strings.isNullOrBlank(cipherSuiteCode)== false){
					for(CipherSuites cipherSuit:cipherSuites){
						
						if((cipherSuit.code == Integer.parseInt(cipherSuiteCode))){
							cipherSuiteNames.append(cipherSuit.name()+",");
						}
						
					}
				}
			}
			
		}
		String finalCipherSuites = cipherSuiteNames.substring(0,cipherSuiteNames.length()-1);
		return finalCipherSuites;
	}
	
	public static Map<String,Integer> getCipherSuiteDetailMap(){
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		HashMap<String,Integer> cipherSuiteDetails = new HashMap<String,Integer>();
		if(Collectionz.isNullOrEmpty(cipherSuites) == false){
			for(CipherSuites cipherSuit:cipherSuites){
				cipherSuiteDetails.put(cipherSuit.name(),cipherSuit.code);
			}
		}
		return cipherSuiteDetails;
	}
	
	public static Integer getCipherSuiteKeyByName(String value){
		
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		if(Collectionz.isNullOrEmpty(cipherSuites) == false){
			for(CipherSuites cipherSuit:cipherSuites){
				if(cipherSuit.name().equals(value)){
					return cipherSuit.code;
				}
			}
		}
		return 0;
	}
	
	public static String getCipherSuitesNameByKey(Integer cipherSuiteCode){
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		if(Collectionz.isNullOrEmpty(cipherSuites) == false){
			for(CipherSuites cipherSuit:cipherSuites){
				if(cipherSuit.code == cipherSuiteCode){
					return cipherSuit.name();
				}
			}
		}
		return "";
	}
	
	/**
	 * used to check whether Maximum TLS version is greater than or equal to Minimum TLS version
	 * @param minTlsVersion Minimum TLS version
	 * @param maxTlsVersion Maximum TLS version
	 * @return true/false
	 */
	public static boolean isMaxTLSVersionGreaterThanOrEqualToMinTLSVersion(String minTlsVersion, String maxTlsVersion){
		Map<String, Integer> tlsVersions = new TreeMap<String, Integer>();
		tlsVersions.put("TLSv1", 1);
		tlsVersions.put("TLSv1.1", 2);
		tlsVersions.put("TLSv1.2", 3);
		boolean isMaxTlsVersionGreaterThanOrEqualToMinTlsVersion = false;
		Integer maxTls = tlsVersions.get(maxTlsVersion);
		Integer minTls = tlsVersions.get(minTlsVersion);
		
		if(maxTls != null && minTls != null){
			
			if(maxTls >= minTls){
				isMaxTlsVersionGreaterThanOrEqualToMinTlsVersion = true;
			}
			
		} 
		
		return isMaxTlsVersionGreaterThanOrEqualToMinTlsVersion;
	}
	
	/**
	 * get unsupported Cipher suites list from given list of Cipher Suites
	 * @param minTlsVersionValue minimum TLS Version
	 * @param maxTlsVersionValue maximum TLS Version
	 * @param cipherSuiteStr list of Cipher Suites
	 * @return Set of Invalid Cipher suite 
	 */
	public static TreeSet<String> getTLSVersionSpecificUnsupportedCipherSuiteList(String minTlsVersionValue, String maxTlsVersionValue, String cipherSuiteStr){
		TLSVersion minTlsVersion = TLSVersion.fromVersion(minTlsVersionValue);
		TLSVersion maxTlsVersion = TLSVersion.fromVersion(maxTlsVersionValue);
		List<CipherSuites> cipherSuitesList=new ArrayList<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTlsVersion,maxTlsVersion));
		return getInvalidCipherSuites(cipherSuiteStr, getValidCipherSuiteCodes(cipherSuitesList));
	}
	
	private static TreeSet<String> getInvalidCipherSuites(String cipherSuitesStr,List<String> ValidCipherSuiteCodes){
		TreeSet<String> invalidCipherSuite = new TreeSet<String>();
		List<String> cipherSuiteLst = Arrays.asList(cipherSuitesStr.split(","));
		
		if(Collectionz.isNullOrEmpty(cipherSuiteLst) == false){
			for(String cipherSuite : cipherSuiteLst){
				if(EliteUtility.isNumeric(cipherSuite.trim())){
					if(Strings.isNullOrBlank(cipherSuite) == false){
						if(ValidCipherSuiteCodes.contains(cipherSuite.trim()) == false){
							//get cipher suite name from code
							String cipherSuiteName = getCipherSuitesNameByKey(Integer.parseInt(cipherSuite));
							invalidCipherSuite.add(cipherSuiteName);
						}
					}
				}else{
					invalidCipherSuite.add(cipherSuite.trim());
				}
			}
		}
		return invalidCipherSuite;
	}
	
	/**
	 * this is used to get Valid cipher suites codes
	 * @param cipherSuites List of Valid cipher suites
	 * @return list of cipher suite codes
	 */
	public static List<String> getValidCipherSuiteCodes(List<CipherSuites> cipherSuites){
		List<String> validCipherSuitesCodesLst = new LinkedList<String>();
		if(Collectionz.isNullOrEmpty(cipherSuites)== false){
			for(CipherSuites cipherSuite : cipherSuites){
				validCipherSuitesCodesLst.add(String.valueOf(cipherSuite.code));
			}
		}
		return validCipherSuitesCodesLst;
	}
		
}
