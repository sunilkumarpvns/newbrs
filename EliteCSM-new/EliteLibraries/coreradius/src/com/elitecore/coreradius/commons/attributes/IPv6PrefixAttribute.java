package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

/**
 * @author rudraduttjoshi
 *
 */
public class IPv6PrefixAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "IPv6PrefixAttribute";
	public IPv6PrefixAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public IPv6PrefixAttribute() {
	}

	/**
	 * Sets the given value to this attribute. The value passed must be in ":"
	 * separated form. This method parses the value using ":" as a seperator
	 * of each Address value.
	 * This is as per rfc 3162 IPv6/Radius rfc.
	 * @param The new value to be set for the attribute. 
	 */

	public void setStringValue(String value){
		if(value != null) {
			String taglessValue = value;
			if(hasTag()){
				taglessValue = setTagAndGetTaglessValue(value);
			}
			byte[] valueBytes = new byte[18];
			byte[] temp = new byte[16];
			String ipv6Address = null;
			int j=0;
			int k =0;
			int prefixSize=64;

			try {
				StringTokenizer stk = new StringTokenizer(taglessValue,"/");
				String[] toks = new String[stk.countTokens()];
				while(stk.hasMoreTokens()){
					toks[k]=stk.nextToken();
					k++;
				}

				/**
				 *  If No Length Value is Specified in the prefix Attribute , Default Value 64 will be used according to RFC 3162.
				 * 	The Syntax of the Framed-IPv6-Prefix Attribute is "ipv6-Address/length".
				 * 	example: fedc:ba98:7654:3210:fedc:ba98:7654:3210/128.
				 */
				if(k > 1){
					ipv6Address = toks[0];
					prefixSize=Integer.parseInt(toks[1]);
					valueBytes[j++]=(byte) 0;
					if(prefixSize <= 128){
						valueBytes[j++]=(byte) prefixSize;
					}else{
						valueBytes[j++]=(byte) 64;
					}
				} else {
					ipv6Address  =  taglessValue;
					valueBytes[j++]=(byte) 0;
					valueBytes[j++]=(byte) 64;
				}

				if(ipv6Address!=null && ipv6Address.length()>0){
					temp =Inet6Address.getByName(ipv6Address).getAddress();
					int byteSize=prefixSize/8;
					for(int i=0;i<byteSize;i++) {
						valueBytes[j++]=temp[i];
					}
					int remainingBits = prefixSize - byteSize*8;
					if(remainingBits >= 4){
						valueBytes[j]  = (byte) (temp[byteSize] & 0xf0);
					}
				}
			} catch(Exception e) {				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to interpret ipv6 Prefix addrees from value(" + taglessValue + "). " + e.getMessage());
				valueBytes = new byte[2]; // Prefix  have atleast length 4 ( type , length , reserve , prefix-length) so I have set it to 2 bytes value
			}
			setValueBytes(valueBytes);
		}
	}

	/**
	 * Sets the given value to this attribute. The value passed must be in ":"
	 * separated form. This method parses the value using ":" as a seperator
	 * of each address value. The method is same as @see PrefixAttribute#setStringValue(String).
	 * 
	 * @param The new value to be set for the attribute. 
	 */
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}

	/**
	 * Returns the value of address attribute in readable form. Each part of 
	 * the address will be separated by ":"  
	 * @return Returns string equivalent value.
	 */
	public String getStringValue() {
		String address = null;
		int reserved = 0;
		int prefix_length = 0;
		byte[] srcBytesValue = getValueBytes();
		byte[] valueBytes = new byte[16];

		// The first byte is for The Reserved field and the second byte is for Prefix Length field.
		// The rest of the bytes are for the IPv6 Address.
		//reserved field is kept for future use.

		reserved = srcBytesValue[0] & 0xff;
		prefix_length = srcBytesValue[1] & 0xff;
		System.arraycopy(srcBytesValue, 2, valueBytes, 0, srcBytesValue.length-2);
		try {
			address = Inet6Address.getByAddress(valueBytes).getHostAddress();
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to interpret ipv6 Prefix addrees." + e.getMessage());
			return "";        	
		}
		return address+"/"+prefix_length;
	}
	/**
	 * Returns the value of Address attribute in readable form. Each part of 
	 * the address will be seperated by ":". This method is same as @see PrefixAttribute#getStringValue().   
	 * @return Returns string equivalent value.
	 */
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	@Override
	public void doPlus(String value) {
		LogManager.getLogger().warn(MODULE, "Plus operation is not supported if IPv6 Address");
	}
}
