package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

import com.elitecore.commons.logging.LogManager;

public class EUI64Attribute extends BaseRadiusAttribute {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "EUI64 Attribute";

	public EUI64Attribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}


	/**
	 * Sets the given value to this attribute. The value passed must be in ":"
	 * separated form. This method parses the value using ":" as a seperator
	 * of each Address value.
	 * This is as per rfc 3162 IPv6/Radius rfc.
	 * @param The new value to be set for the attribute. 
	 */
	
    public void setStringValue(String value) {

    	if(value != null) {
    		String taglessValue = value;
    		if(hasTag()){
	    		taglessValue = setTagAndGetTaglessValue(value);
    		}
    		char[] srcb = taglessValue.toCharArray();
    		byte[] valueBytes = new byte[8];
    		int val=0;
    		int j=0;
    		boolean hasmore = false;

        //No Validation is done for the given Framed Interface Id . It is assumed to be a valid Framed-Interface-Id.

    		for(int i=0 ; i < srcb.length; i++) {
                int chval = Character.digit(srcb[i], 16);

                if(chval != -1){
                        val <<= 4;
                        val |= chval;
                        if (val > 0xffff)  // To Check if the value is not more than ffff 
                                return;
                        hasmore = true;
                        continue;
                }else if(srcb[i] != ':'){
                        setValueBytes(new byte[8]);
                        return;
                } else {
                        valueBytes[j++] = (byte) ((val >> 8) & 0xff);
                        valueBytes[j++] = (byte) (val & 0xff);
                        hasmore = false;
                        val = 0;
                }
    		}
    		if(hasmore) {
                if ((j + 2) > 16) {
                        setValueBytes(new byte[8]);
                }
                valueBytes[j++] = (byte) ((val >> 8) & 0xff);
                valueBytes[j++] = (byte) (val & 0xff);
    		}
        setValueBytes(valueBytes);
    	}
    }


	/**
	 * Sets the given value to this attribute. The value passed must be in ":"
	 * separated form. This method parses the value using ":" as a seperator
	 * of each address value. The method is same as @see InterfaceAttribute#setStringValue(String).
	 * 
	 * @param The new value to be set for the attribute. 
	 */
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}

	/**
	 * Returns the value of address attribute in readable form. Each part of 
	 * the address will be separated by ":"  
	 * 
	 * @return Returns string equivalent value.
	 */
	
	
    public String getStringValue() {

        StringBuffer sbuf = new StringBuffer();
        StringBuffer hex = new StringBuffer();
        int temp = 0;
        int next =0;
        
		/**
		 * Converting the Value of Bytes in the Byte Array into a HexaDecimal Value and 
		 * seperated by ":". No compression is done .
		 */

        for(int i=0;i<8;i++) {
        	temp = getValueBytes()[i++] & 0xff;
        	next = getValueBytes()[i] & 0xff;
        	
           	hex.delete(0, hex.length());
        	hex.append(Integer.toHexString(temp));

        	if(temp !=0) {
        		sbuf.append(hex);
               	hex.delete(0, hex.length());
                if(next <16) {
                	hex.append('0');
                }
            	hex.append(Integer.toHexString(next));
        	} else{
               	hex.delete(0, hex.length());
        		hex.append(Integer.toHexString(next));
        	}

        	if(i<7 ) {
        		sbuf.append(hex).append(':');
        	} else {
        		sbuf.append(hex);
        	}
        }
        return sbuf.toString().toUpperCase();
    }
	
    /**
	 * Returns the value of Address attribute in readable form. Each part of 
	 * the address will be seperated by ":". This method is same as @see InterfaceAttribute#getStringValue().   
	 * 
	 * @return Returns string equivalent value.
	 */
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}

	@Override
	public void doPlus(String value) {
		LogManager.getLogger().warn(MODULE, "Plus operation is not supported if EUI64 type value");
	}
}
