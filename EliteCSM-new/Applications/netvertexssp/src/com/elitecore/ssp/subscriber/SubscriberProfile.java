
package com.elitecore.ssp.subscriber;

import java.io.Serializable;
import java.util.Map;
 
public class SubscriberProfile implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final   String area =  "AREA";
    private final   String arpu = "ARPU";
    private final   String billingDate = "BILLINGDATE";
    private final   String  birthDate = "BIRTHDATE";
    
    private final   String cadre = "CADRE";
    private final   String city = "CITY";
  
    private final   String company = "COMPANY";
    private final   String country = "COUNTRY";
    private final   String cui = "CUI";
    private final   String customerType = "CUSTOMERTYPE";
    private final   String department = "DEPARTMENT";
    private final   String deviceType = "DEVICETYPE";
    
    private final   String email = "EMAIL";
    private final   String  encryptionType = "ENCRYPTIONTYPE"; 
    private final   String esn = "ESN";
    private final   String eui64 = "EUI64";
    private final   String  expiryDate = "EXPIRYDATE";
    private final   String fap = "FAP";  
    private final   String groupName = "GROUPNAME";
    private final   String imei = "IMEI";
    private final   String imsi = "IMSI";
    private final   String mac = "MAC";
    private final   String meid =  "MEID";
    private final   String modifiedEui64 = "MODIFIED_EUI64";
    private final   String msisdn = "MSISDN";
    private    String param1 = "PARAM1";
    private    String param2 = "PARAM2" ;
    private    String param3 = "PARAM3" ;
    private    String param4 = "PARAM4";
    private    String param5 = "PARAM5";
    private    String param6= "PARAM6";
    private    String param7 = "PARAM7";
    private    String param8 = "PARAM8";
    private    String param9 = "PARAM9";
    private    String param10 = "PARAM10";
    private    String param11= "PARAM11";
    private    String param12 = "PARAM12";
    private final   String parentID =  "PARENTID";
    private final   String password ="PASSWORD";
    private final   String phone =  "PHONE";
    private final   String role =  "ROLE";
    private final   String sipURL =  "SIPURL";
    private final   String status = "STATUS";
    private final   String subscriberID  = "SUBSCRIBERIDENTITY";
    private final   String subscriberPackage = "SUBSCRIBERPACKAGE" ;
    private final   String subscriberStatus = "SUBSCRIBERSTATUS" ;
    private final   String userName =   "USERNAME";
    private final   String zone = "ZONE";
    private Map<String, String> subscriberProfileMap;
    
    public SubscriberProfile(Map<String, String> subscriberProfileMap){
    	this.subscriberProfileMap = subscriberProfileMap;
    }
    
    
    
    @Override
	public String toString() {
		return "SubscriberProfile [subscriberID of the  suubscriber is =" + subscriberProfileMap.get(subscriberID) + "]";
	}



	public String getArea() {
    	return subscriberProfileMap.get(area);        
    }

    public String getArpu() {
    	 return subscriberProfileMap.get(arpu);
    }

    public String getBillingDate() {
    
    	return subscriberProfileMap.get(billingDate);
    }
 
    public String getBirthDate() {
        return subscriberProfileMap.get(billingDate);
    }

    public String getCadre() {
        return subscriberProfileMap.get(cadre);
    }

    public String getCity() {
        return subscriberProfileMap.get(city);
    }

    public String getCompany() {
        return subscriberProfileMap.get(company);
    }

 
    public String getCountry() {
        return subscriberProfileMap.get(country);
    }

 
    public String getCui() {
        return subscriberProfileMap.get(cui);
    }

    public String getCustomerType() {
        return subscriberProfileMap.get(customerType);
    }

    public String getDepartment() {
        return subscriberProfileMap.get(department);
    }

    public String getDeviceType() {
        return subscriberProfileMap.get(deviceType);
    }

    public String getEmail() {
        return subscriberProfileMap.get(email);
    }

    public String getEncryptionType() {
        return subscriberProfileMap.get(encryptionType);
    }

    public String getEsn() {
        return subscriberProfileMap.get(esn);
    }

    public String getEui64() {
        return subscriberProfileMap.get(eui64);
    }
 
    public String getExpiryDate() {
        return subscriberProfileMap.get(expiryDate);
    }

    public String getFap() {
        return subscriberProfileMap.get(fap);
    }

    public String getGroupName() {
    	return subscriberProfileMap.get(groupName);        
    }
 
    public String getImei() {
        return subscriberProfileMap.get(imei);
    }

    public String getImsi() {
        return subscriberProfileMap.get(imsi);
    }

    public String getMac() {
        return subscriberProfileMap.get(mac);
    }
 
    public String getMeid() {
        return subscriberProfileMap.get(meid);
    }

    public String getModifiedEui64() {
        return subscriberProfileMap.get(modifiedEui64);
    }

    public String getMsisdn() {
        return subscriberProfileMap.get(msisdn);
    }

    public String getParam1() {
    	return subscriberProfileMap.get("PARAM1");  
    }

    public String getParam2() {
    	return subscriberProfileMap.get("PARAM2");
    }

    public String getParam3() {
    	return subscriberProfileMap.get("PARAM3");
    }

    public String getParam4() {
    	return subscriberProfileMap.get("PARAM4");
    }

    public String getParam5() {
    	return subscriberProfileMap.get("PARAM5");
    }
 
 
    public String getParentID() {
    	return subscriberProfileMap.get(parentID);
    }

    public String getPassword() {
    	return subscriberProfileMap.get(password);        
    }

    public String getPhone() {
        return subscriberProfileMap.get(phone);
    }

    public String getRole() {
        return subscriberProfileMap.get(role);
    }

 
    public String getSipURL() {
        return subscriberProfileMap.get(sipURL);
    }

    public String getStatus() {
    	return subscriberProfileMap.get(status);
    }

    public String getSubscriberID() {
    	return subscriberProfileMap.get(subscriberID);
    }
 
    public String getSubscriberPackage() {
    	return subscriberProfileMap.get(subscriberPackage);        
    }
 
    public String getSubscriberStatus() {
    	return subscriberProfileMap.get(subscriberStatus);        
    }

    public String getUserName() {
    	return subscriberProfileMap.get(userName);        
    }
 
    public String getZone() {
    	return subscriberProfileMap.get(zone);
        
    }
    
    public String getParameter(String key){
    	return subscriberProfileMap.get(key);
    }

	public void setParam1(String param1) {
		this.subscriberProfileMap.put("PARAM1",param1);
	}

	public void setParam2(String param2) {
		this.subscriberProfileMap.put("PARAM2",param2);
	}

	public void setParam3(String param3) {
		this.subscriberProfileMap.put("PARAM3",param3);
	}

	public void setParam4(String param4) {
		this.subscriberProfileMap.put("PARAM4",param4);
	}

	public void setParam5(String param5) {
		this.subscriberProfileMap.put("PARAM5",param5);
	}

	public String getParam6() {
		return subscriberProfileMap.get("PARAM6");
	}

	public void setParam6(String param6) {
		this.subscriberProfileMap.put("PARAM6",param6);
	}

	public String getParam7() {
		return subscriberProfileMap.get("PARAM7");
	}

	public void setParam7(String param7) {
		this.subscriberProfileMap.put("PARAM7",param7);
	}

	public String getParam8() {
		return subscriberProfileMap.get("PARAM8");
	}

	public void setParam8(String param8) {
	     subscriberProfileMap.put(this.param8,param8);
	}

	public String getParam9() {
		return subscriberProfileMap.get("PARAM9");
	}

	public void setParam9(String param9) {
		this.subscriberProfileMap.put("PARAM9",param9);
	}

	public String getParam10() {
		return subscriberProfileMap.get("PARAM10");
	}

	public void setParam10(String param10) {
		this.subscriberProfileMap.put("PARAM10",param10);
	}

	public String getParam11() {
		return subscriberProfileMap.get("PARAM11");
	}

	public void setParam11(String param11) {
		this.subscriberProfileMap.put("PARAM11",param11);
	}

	public String getParam12() {
		return subscriberProfileMap.get("PARAM12");
	}

	public void setParam12(String param12) {
		this.subscriberProfileMap.put("PARAM12",param12);
	}
}
