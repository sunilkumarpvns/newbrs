package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.net.AddressResolver;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class AddressAttribute extends BaseRadiusAttribute {
	
	private static final long serialVersionUID = 1L;

	private static final String MODULE = "AddressAttribute";
	private Long netMask = null;

	@Nonnull
	private transient AddressResolver addressResolver;

	public AddressAttribute() { // NOSONAR
		this(AddressResolver.defaultAddressResolver());
	}
	
	AddressAttribute(AddressResolver addressResolver) {
		this.addressResolver = addressResolver;
		
	}
	
	public AddressAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
		this.addressResolver = AddressResolver.defaultAddressResolver();
	}

	public void setBytes(byte[] totalBytes) {
		if(totalBytes != null && totalBytes.length >= 4){
			ByteArrayInputStream in = new ByteArrayInputStream(totalBytes);
			try {
				readFrom(in);
			}catch(IOException ioExp){
				//It's not required becuase it read from always memory				
			}
		}
	}

	/**
	 * Sets the given value to this attribute. The value passed must be in "." or ":"
	 * separated form. This method parses the value using "." or ":" as a seperator
	 * of each address value.
	 * 
	 * @param The new value to be set for the attribute. 
	 */
	public void setStringValue(String value) {
	
		if (value != null) {
			
			String taglessValue = value;
			if(hasTag()){
				taglessValue = setTagAndGetTaglessValue(value);
			}
			
			if(taglessValue.indexOf("/") != -1){

				if(taglessValue.indexOf("/")== taglessValue.length()-1){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees from value(" + taglessValue + "). ");
					setValueBytes(new byte[4]);
					return;
				}else{
					String strSubNetMask = taglessValue.substring(taglessValue.indexOf('/')+1);
					try{
						short maskBits = Short.parseShort(strSubNetMask);
						if(maskBits > 32){
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid value for subnet mask: " + maskBits);
							setValueBytes(new byte[4]);
							return;
						}
						netMask = (long) ((1<<(32-maskBits)) -1);
						netMask = (~netMask) & 0x00000000FFFFFFFFL;
						taglessValue = taglessValue.substring(0,taglessValue.indexOf('/'));
					}catch(NumberFormatException ex){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees from value(" + taglessValue + "). " + ex.getMessage());
						setValueBytes(new byte[4]);
						return;
					}
				}
			}

			byte[] valueBytes = null;
			try { 					
				valueBytes = addressResolver.byName(taglessValue).getAddress();		
			} catch (Exception e){
				try {										
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees from value(" + taglessValue + "). " + e.getMessage());
					valueBytes = new byte[4];
				} catch (Exception exp) {										
					LogManager.getLogger().trace(MODULE, exp);					
				}
			}
			setValueBytes(valueBytes);
		}
	}

	/**
	 * Sets the given value to this attribute. The value passed must be in either in "."
	 * separated form for 1Pv4 address or ":" for IPv6. This method parses the value using "." or ":" as a seperator
	 * of each address value. The method is same as @see AddressAttribute#setStringValue(String).
	 * 
	 * @param The new value to be set for the attribute. 
	 */
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}

	/**
	 * Returns the value of address attribute in readable form. Depending on the the byte value , 
	 * It will return either a String Representation of IPv4 or Ipv6 Address.
	 * @return Returns string equivalent value.
	 */
	public String getStringValue(){
        String address = null;
        byte[] valueBytes = null;
		//return (getValueBytes()[0] & 0xff) + "." + (getValueBytes()[1] & 0xff) + "." +
    	//(getValueBytes()[2] & 0xff) + "." + (getValueBytes()[3] & 0xff);
        try {
        	valueBytes = getValueBytes();
        	if(valueBytes.length !=0)        		
        		address = addressResolver.byAddress(getValueBytes()).getHostAddress();
        	else
        		return null;
        } catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to interpret ip addrees." + e.getMessage());
					return "";        	
        }
		return address.toUpperCase();
	}

	/**
	 * Returns the value of address attribute in readable form. Each part of 
	 * the address will be seperated by "." or ":" in an uncompress form. This method is same as @see AddressAttribute#getStringValue().   
	 * 
	 * @return Returns string equivalent value.
	 */
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	/*
	 * author narendra.pathai 
	 * 
	 * (non-Javadoc)
	 * @see com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute#doPlus(java.lang.String)
	 * This method tries to find the integer equivalent of string value to be updated.
	 * First it tries to find the long equivalent by assuming it as plain long value
	 * but if there is number format exception which means that the value is not a plain long
	 * So it tries to find the long equivalent using IP address if value contains "."
	 * 
	 * Two formats are convertible here 1) Plain Long value 2) IP address format value
	 */
	@Override
	public void doPlus(String value){
		if(value == null)
			return;
		
		long longEquivalentValue = 0L;
		try{
			 longEquivalentValue = Long.parseLong(value);
		}catch(NumberFormatException ex){
			if(value.indexOf('.') != -1){
				try {
					longEquivalentValue = RadiusUtility.bytesToLong(addressResolver.byName(value).getAddress());
				} catch (UnknownHostException e) {
					throw new IllegalArgumentException("Cannot convert " + value + " to Integer value.");
				}
			}
		}
		
		long newValue = getLongValue() + longEquivalentValue; 
		byte[] bytes = new byte[4];
		for(int i = 3; i >= 0 ; i--){
			bytes[i] = (byte) (newValue & 0xFF);
			newValue = newValue >> 8;
		}
		setValueBytes(bytes);
	}

	@Override
	public boolean equals(Object obj){
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(!(obj instanceof AddressAttribute))
			return false;
		
		AddressAttribute radAttribute = (AddressAttribute)obj;
		
		if(netMask == null && radAttribute.getNetMask() == null){
			return super.equals(obj);
		}else{
			if(hasTag()){
				if(radAttribute.getTag() != 0){
					if(radAttribute.getTag() != getTag()){
						return false;
					}
				}
			}
			
			byte[] addressBytes = getValueBytes();
			Long thisAddress = RadiusUtility.bytesToLong(addressBytes);
			Long otherAddress = RadiusUtility.bytesToLong(radAttribute.getValueBytes());
			if(thisAddress != null && otherAddress != null){
				
				//if the netmasks are not same for both attributes
				if(netMask != null && radAttribute.getNetMask() != null)
					if(!netMask.equals(radAttribute.getNetMask()))
						return false;
				
				if(netMask == null){
					thisAddress = thisAddress & radAttribute.getNetMask();
					return thisAddress.equals(otherAddress);
				}else{
					otherAddress = otherAddress & netMask;
					return otherAddress.equals(thisAddress);
				}
			}
			return false;
		}
	}
	
	private Long getNetMask(){
		return netMask;
	}
}
