package com.elitecore.aaa.radius.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.Vendor;
import com.elitecore.aaa.radius.data.RadClientData;

public interface RadClientConfiguration {
	public String getAllClients();
	public RadClientData getClientData(String clientIp);
	public boolean isValidClient(String clientIP);
	public void removeClient(String ip);
	public String getClientVendorID(String strClientIP);
	public String getClientVendorType(String strClientIP);
	public boolean isPortalTypeClient(String strClientIP);
	public String getClientSharedSecret(String strClientIP, int requestType);
	public long getClientRequestExpiryTime(String strClientIP);
	public String getClientPolicie(String strClientIP);
	public List<Vendor> getSupportedVendorList(String strClientIP);
	public boolean isSupportedVendorId(String strClientIP,long vendorId);
	public boolean isSupportedVendorId(String strClientIP,String vendorId);
	public List<byte[]> getDNSList(String strClientIP);
	public String getHA_IPAddress(String strClientIP);
	public String getNasHotlinePolicy(String strClientIP);
	public List<String> getUserIdentitiesOfClient(String clientIp);
	public Map<String,List<byte[]>> getDNSIPListForClients();	
	public String getFramedPool(String clientIpAddress);
	public int getSupportedClientCount();
	public String getPrepaidStandard(String strClientIP);
	public List<String>getClientAddresses();
	public Map<String, ArrayList<HashMap<String, String>>> clientDetails();
}
