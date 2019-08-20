package com.elitecore.diameterapi.diameter.common.packet.avps.derived;

import java.net.InetAddress;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpOctetString;

public class AvpIPv4Address extends AvpOctetString{
	
	private String MODULE = "IPv4AdressAVP";
	public AvpIPv4Address(int intAVPCode, int intVendorId, byte flag,String strAvpId, String strAVPEncryption) {
		super(intAVPCode, intVendorId, flag, strAvpId, strAVPEncryption);
		// TODO Auto-generated constructor stub
	}
	
	public void setStringValue(String strAddress) {
		
		if (strAddress != null) {			
			byte[] valueBytes = null;
			try { 					
				valueBytes = InetAddress.getByName(strAddress).getAddress();		
			} catch (Exception e){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to interpret IP Addrees from value(" + strAddress + "). Reason: " + e.getMessage());
				valueBytes = new byte[4];
			}
	        setValueBytes(valueBytes);
		}	
	}
	
	public String getStringValue() {		
		String address = null;
        byte[] valueBytes = null;
		try {
        	valueBytes = getValueBytes();
        	if(valueBytes.length !=0)        		
        		address = InetAddress.getByAddress(getValueBytes()).getHostAddress();        		
        	else
        		return null;
        } catch (Exception e) {	
        	if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees." + e.getMessage());
			return null;        	
        }
        return address;
	}
}
