package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;



public class WiMAXIPAddressAttribute extends BaseRadiusWiMAXAttribute{
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "WiMAXIPAddressAttribute";
	
	public WiMAXIPAddressAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public void setStringValue(String value) {
		if (value != null) {
			byte[] valueBytes = null;
			try { 
				valueBytes = InetAddress.getByName(value).getAddress();
			} catch (Exception e){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees from value(" + value + "). " + e.getMessage());
			}
	        setValueBytes(valueBytes);
		}
	}
	
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}
	
	public String getStringValue(){
        String address = null;
		//return (getValueBytes()[0] & 0xff) + "." + (getValueBytes()[1] & 0xff) + "." +
    	//(getValueBytes()[2] & 0xff) + "." + (getValueBytes()[3] & 0xff);
        try {
        	address = InetAddress.getByAddress(getValueBytes()).getHostAddress();
        } catch (Exception e) {
        	if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees from input String value." + e.getMessage());
        	return null;
        }
		return address.toUpperCase();
	}
	
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	@Override
	public void doPlus(String value){
		setStringValue(value);
	}
}
