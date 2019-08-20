package com.elitecore.aaa.radius.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.commons.base.Optional;

public interface RadESConfiguration {

	public static enum RadESTypeConstants{
		RAD_AUTH_PROXY(1,"RAD_AUTH_PROXY"),
		RAD_ACCT_PROXY(2,"RAD_ACCT_PROXY"),
		IP_POOL_SERVER(3,"IP_POOL_SERVER"),
		PREPAID_SERVER(4,"PREPAID_SERVER"),
		SESSION_MANAGER(5,"SESSION_MANAGER"),
		CHARGING_GATEWAY(6,"CHARGING_GATEWAY"),
		NAS(7,"NAS"),
		DYNAMIC_NAS(8,"DYNAMIC_NAS"),
		GENERIC_ESI_TYPE(0,"GENERIC_ESI_TYPE");
		
		final public int type;
		final public String name;
		private static Map<Integer,RadESTypeConstants> map;
		public static final RadESTypeConstants[] VALUES = values();
		
		static{
			map = new HashMap<Integer, RadESConfiguration.RadESTypeConstants>(8);
			for(RadESTypeConstants value: VALUES){
				map.put(value.type, value);
			}
		}
		
		RadESTypeConstants(int type, String name){
			this.name = name;
			this.type = type;
		}
		
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}
		
		public static RadESTypeConstants get(int key){
			return map.get(key);
		}
		public static String getName(int value){
			return map.get(value).name;
		}
		
		public int getType(){
			return this.type;
		}
	}
	
	
	public Optional<DefaultExternalSystemData> getESDataByName(String esName);
	public Optional<DefaultExternalSystemData> getESData(String esID);
	public List<DefaultExternalSystemData> getESListByType(int typeID);
	public Map<String, DefaultExternalSystemData> getAllESI();
}
