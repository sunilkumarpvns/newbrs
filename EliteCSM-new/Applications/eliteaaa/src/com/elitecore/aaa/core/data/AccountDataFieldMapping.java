package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class AccountDataFieldMapping{
	
	
	public AccountDataFieldMapping(){
		//required by JAXB
	}
	 public static String CUI = "CUI";
	 public static String GROUP_NAME = "GroupName";
	 public static String PASSWORD_CHECK = "PasswordCheck"; //NOSONAR - Reason: Credentials should not be hard-coded
	 public static String ENCRYPTION_TYPE = "EncryptionType";
	 public static String CUSTOMER_TYPE = "CustomerType";
	 public static String SERVICE_TYPE = "ServiceType";
	 public static String CALLING_STATION_ID = "Calling-Station-ID";
	 public static String CALLED_STATION_ID = "Called-Station-ID";
	 public static String MAX_SESSION_TIME = "Max Session Time";
	 public static String MAC_VALIDATION = "MACValidation";
	 public static String CUSTOMER_CHECK_ITEMS = "CustomerCheckItems";
	 public static String CUSTOMER_REJECT_ITEMS = "CustomerRejectItems";
	 public static String CUSTOMER_REPLY_ITEMS = "CustomerReplyItems";
	 public static String CUSTOMER_SERVICES="CustomerServices";
	 public static String LOGIN_POLICY = "LoginPolicy";
	 public static String ACCESS_POLICY = "AccessPolicy";
	 public static String AUTHORIZATION_POLICY = "AuthorizationPolicy";
	 public static String ADDITIONAL_POLICY = "AdditionalPolicy";
	 public static String IP_ALLOCATION = "IPAllocation";
	 public static String CREDIT_LIMIT = "CreditLimit";
	 public static String EXPIRY_DATE = "ExpiryDate";
	 public static String EMAIL="Email"; 
	 public static String CUSTOMER_STATUS = "CustomerStatus";
	 
	 public static String PARAM1 = "param1";
	 public static String PARAM2 = "param2";
	 public static String PARAM3 = "param3";
	 public static String PARAM4 = "param4";
	 public static String PARAM5 = "param5";
	 public static String HOTLINE_POLICY = "HotlinePolicy";
	 public static String GRACE_PERIOD = "GracePeriod";
	 public static String CALLBACK_ID = "Callback-Id";
	 
	 public static String USER_PASSWORD = "User-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	 public static String USER_NAME = "User-Name";
	 
	 public static String IMSI = "IMSI";
	 public static String MEID = "MEID";
	 public static String MSISDN  = "MSISDN";
	 public static String MDN  = "MDN";
	 public static String IMEI  = "IMEI";

	 public static String DEVICE_VENDOR = "DeviceVendor";
	 public static String DEVICE_NAME   = "DeviceName";
	 public static String DEVICE_PORT   = "DevicePort";
	 public static String GEO_LOCATION  = "GEOLocation";
	 public static String DEVICE_VLAN   = "DeviceVLAN";

	 public static final String DYNAMIC_CHECK_ITEMS = "DynamicCheckItems";


	 public static String FRAMED_IPV4_ADDRESS = "FRAMEDIPV4ADDRESS";
	 public static String FRAMED_IPV6_PREFIX = "FRAMEDIPV6PREFIX";
	 public static String FRAMED_POOL = "FRAMEDPOOL";
	 
	 public static String POLICY_GROUP = "Policy Group";
 	 
 	
	private ArrayList<String> fieldNameList = new ArrayList<String>();
	@XmlTransient 
	private Map<String,List<DataFieldMapping>> dataFieldMap = new HashMap<String,List<DataFieldMapping>>();	
	
	private List<DataFieldMappingImpl> fieldMappingdList = new ArrayList<DataFieldMappingImpl>();
	
	@XmlElement(name = "field-mapping")
	public List<DataFieldMappingImpl> getFieldMappingList() {
		return fieldMappingdList;
	}

	public void setFieldMappingList(List<DataFieldMappingImpl> fieldMappingdList) {
		this.fieldMappingdList = fieldMappingdList;
	}
	
	public List<DataFieldMapping> getFieldMapping(String logicalName){
		return dataFieldMap.get(logicalName);
	}
	@XmlTransient
	public Map<String,List<DataFieldMapping>> getFieldMapping(){
		
		return dataFieldMap;
	}
	
	public void setFieldMapping(Map<String,List<DataFieldMapping>> fieldMap){
		this.dataFieldMap = fieldMap;
		
		ArrayList<String> tempFieldList = new ArrayList<String>(); 
		
		for(Entry<String,List<DataFieldMapping>> entry: dataFieldMap.entrySet()){
			List<DataFieldMapping>  list= entry.getValue();
			int size = list.size();
			for(int i =0 ;i<size;i++){
				tempFieldList.add(list.get(i).getFieldName());
			}
		}
		this.fieldNameList = tempFieldList;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();				
		if(getFieldMapping(ENCRYPTION_TYPE) != null)
			out.println("    Encryption Type           = " + getFieldMapping(ENCRYPTION_TYPE));
		if(getFieldMapping(CUSTOMER_CHECK_ITEMS) != null)
			out.println("    Check-Items               = " + getFieldMapping(CUSTOMER_CHECK_ITEMS));
		if(getFieldMapping(CUSTOMER_REJECT_ITEMS) != null)
			out.println("    Reject Items              = " + getFieldMapping(CUSTOMER_REJECT_ITEMS));
		if(getFieldMapping(CUSTOMER_REPLY_ITEMS) != null)
			out.println("    Reply Items               = " + getFieldMapping(CUSTOMER_REPLY_ITEMS));
		if(getFieldMapping(AUTHORIZATION_POLICY) != null)
			out.println("    Authorization Policy      = " + getFieldMapping(AUTHORIZATION_POLICY));
		if(getFieldMapping(ADDITIONAL_POLICY) != null)
			out.println("    Additional Policy         = " + getFieldMapping(ADDITIONAL_POLICY));
		if(getFieldMapping(ACCESS_POLICY) != null)
			out.println("    Access Policy             = " + getFieldMapping(ACCESS_POLICY));
		if(getFieldMapping(LOGIN_POLICY) != null)
			out.println("    Concurrent Login Policy   = " + getFieldMapping(LOGIN_POLICY));
		if(getFieldMapping(IP_ALLOCATION) != null)
			out.println("    IP Pool Name              = " + getFieldMapping(IP_ALLOCATION));
		if(getFieldMapping(CUSTOMER_STATUS) != null)
			out.println("    Account Status            = " + getFieldMapping(CUSTOMER_STATUS));
		if(getFieldMapping(PARAM1) != null)
			out.println("    Param1                    = " + getFieldMapping(PARAM1));
		if(getFieldMapping(PARAM2) != null)
			out.println("    Param2                    = " + getFieldMapping(PARAM2));
		if(getFieldMapping(PARAM3) != null)
			out.println("    Param3                    = " + getFieldMapping(PARAM3));
		if(getFieldMapping(PARAM4) != null)
			out.println("    Param4                    = " + getFieldMapping(PARAM4));
		if(getFieldMapping(PARAM5) != null)
			out.println("    Param5                    = " + getFieldMapping(PARAM5));
		if(getFieldMapping(GROUP_NAME) != null)
			out.println("    Group Name                = " + getFieldMapping(GROUP_NAME));
		if(getFieldMapping(EMAIL) != null)
			out.println("    Email ID                  = " + getFieldMapping(EMAIL));
		if(getFieldMapping(CUSTOMER_SERVICES)  != null)
			out.println("    CustomerServices          = " + getFieldMapping(CUSTOMER_SERVICES));
		if(getFieldMapping(CUSTOMER_TYPE) != null)
			out.println("    Customer Type             = " + getFieldMapping(CUSTOMER_TYPE));
		if(getFieldMapping(USER_PASSWORD) != null)
			out.println("    Password                 = " + getFieldMapping(USER_PASSWORD));
		if(getFieldMapping(USER_NAME) != null)
			out.println("    UserName                  = " + getFieldMapping(USER_NAME));
		if(getFieldMapping(PASSWORD_CHECK) != null)
			out.println("    Password Check            = " + getFieldMapping(PASSWORD_CHECK));
		if(getFieldMapping(SERVICE_TYPE) != null)
			out.println("    Service Type              = " + getFieldMapping(SERVICE_TYPE));
		if(getFieldMapping(CALLING_STATION_ID) != null)
			out.println("    Calling Station ID        = " + getFieldMapping(CALLING_STATION_ID));
		if(getFieldMapping(CALLED_STATION_ID) != null)
			out.println("    Called Station ID         = " + getFieldMapping(CALLED_STATION_ID));
		if(getFieldMapping(CUI) != null)
			out.println("    CUI                       = " + getFieldMapping(CUI));
		if(getFieldMapping(HOTLINE_POLICY) != null)
			out.println("    Hotline Policy            = " + getFieldMapping(HOTLINE_POLICY));
		if(getFieldMapping(GRACE_PERIOD) != null)
			out.println("    Grace Period              = " + getFieldMapping(GRACE_PERIOD));
		if(getFieldMapping(CALLBACK_ID) != null)
			out.println("    Callback ID               = " + getFieldMapping(CALLBACK_ID));
		if(getFieldMapping(MAX_SESSION_TIME) != null)
			out.println("    Max Session Time          = " + getFieldMapping(MAX_SESSION_TIME));
		if(getFieldMapping(CREDIT_LIMIT) != null)
			out.println("    Credit Limit              = " + getFieldMapping(CREDIT_LIMIT));
		if(getFieldMapping(EXPIRY_DATE) != null)
			out.println("    Expiry Date               = " + getFieldMapping(EXPIRY_DATE));
		if(getFieldMapping(MAC_VALIDATION) != null)
			out.println("    MAC Validation            = " + getFieldMapping(MAC_VALIDATION));
		if( getFieldMapping(IMEI)!= null)
			out.println("    IMEI                      = " + getFieldMapping(IMEI));
		if(getFieldMapping(MDN) != null)
			out.println("    MDN                       = " + getFieldMapping(MDN));
		if(getFieldMapping(MSISDN) != null)
			out.println("    MSISDN                    = " + getFieldMapping(MSISDN));
		if(getFieldMapping(IMSI) != null)
			out.println("    IMSI                      = " + getFieldMapping(IMSI));
		if(getFieldMapping(MEID) != null)
			out.println("    MEID                      = " + getFieldMapping(MEID));
		if( getFieldMapping(DEVICE_VENDOR)!= null)
			out.println("    Device Vendor             = " + getFieldMapping(DEVICE_VENDOR));
		if(getFieldMapping(DEVICE_NAME) != null)
			out.println("    Device Name               = " + getFieldMapping(DEVICE_NAME));
		if(getFieldMapping(DEVICE_PORT) != null)
			out.println("    Device Port               = " + getFieldMapping(DEVICE_PORT));
		if(getFieldMapping(DEVICE_VLAN) != null)
			out.println("    Device VLAN               = " + getFieldMapping(DEVICE_VLAN));
		if(getFieldMapping(GEO_LOCATION) != null)
			out.println("    GEO Location              = " + getFieldMapping(GEO_LOCATION));
		
		if(getFieldMapping(FRAMED_IPV4_ADDRESS) != null)
			out.println("    Framed-IPV4-Address              = " + getFieldMapping(FRAMED_IPV4_ADDRESS));
		
		if(getFieldMapping(FRAMED_IPV6_PREFIX) != null)
			out.println("    Framed-IPV6-Prefix               = " + getFieldMapping(FRAMED_IPV6_PREFIX));
		
		if(getFieldMapping(FRAMED_POOL) != null)
			out.println("    Framed-Pool		              = " + getFieldMapping(FRAMED_POOL));
		
		if(getFieldMapping(POLICY_GROUP) != null)
			out.println("    Policy Group		              = " + getFieldMapping(POLICY_GROUP));
		
		out.close();
		return stringBuffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AccountDataFieldMapping))
			return false;
		
		return true;
	}
	
	public void addFieldMapping(String logicalName,String fieldName,String defaultValue,String valueMapping){
		List<DataFieldMapping> tempDataFieldMappingImpl = dataFieldMap.get(logicalName);;
		if(tempDataFieldMappingImpl==null){
			tempDataFieldMappingImpl = new ArrayList<DataFieldMapping>();
			dataFieldMap.put(logicalName, tempDataFieldMappingImpl);
		}
		this.fieldNameList.add(fieldName);
		tempDataFieldMappingImpl.add(new DataFieldMappingImpl(fieldName,defaultValue,valueMapping,logicalName));
	}
	public ArrayList<String> getFieldNameList() {
		return fieldNameList;
	}

}
