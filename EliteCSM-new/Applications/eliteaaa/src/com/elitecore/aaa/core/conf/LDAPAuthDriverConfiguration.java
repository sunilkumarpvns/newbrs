package com.elitecore.aaa.core.conf;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface LDAPAuthDriverConfiguration extends DriverConfiguration{
	
	@XmlEnum
	public enum LDAP_SEARCH_SCOPE{
		@XmlEnumValue(value = "SCOPE_BASE")
		SCOPE_BASE(0),
		@XmlEnumValue(value = "SCOPE_ONE")
		SCOPE_ONE(1),
		@XmlEnumValue(value = "SCOPE_SUB")
		SCOPE_SUB(2);
		
		public final int value;
		private static final Map<Integer,LDAP_SEARCH_SCOPE> map;
		public static final LDAP_SEARCH_SCOPE[] VALUES = values();
		static {
			map = new HashMap<Integer,LDAP_SEARCH_SCOPE>(14);
			for (LDAP_SEARCH_SCOPE type : VALUES) {
				map.put(type.value, type);
			}
		}	
		
		LDAP_SEARCH_SCOPE(int value){
			this.value = value;
		}
		
		public int getValue(){
			return this.value;
		}
		
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}
		
		public static LDAP_SEARCH_SCOPE get(int key){
			return map.get(key);
		}
	}
	
	public String getLDAPdriverid();
	
	public SimpleDateFormat[] getExpiryDatePatterns();

	public int getPasswordDecryptType();

	public long getQueryMaxExecTime();	
	
	public int getStatusCheckDuration();
	
	public long getMaxQueryTimeoutCount();
		
	public List<String> getUserIdentityAttributes();

	public AccountDataFieldMapping getAccountDataFieldMapping();

	public String getSearchFilter();
	
	public LDAP_SEARCH_SCOPE getSearchScope();
	public String getDSName() ;
}
