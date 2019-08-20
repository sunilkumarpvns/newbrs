package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;


/**
 * Implementation to handle text type attribute of Radius packet.
 *  
 *    RFC : 
 *    1-253 octets containing UTF-8 encoded 10646 [7]
 *    characters.  Text of length zero (0) MUST NOT be sent;
 *    omit the entire attribute instead.
 *    
 * @author Elitecore Tehcnologies Ltd.
 *
 */
public class TextAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	public TextAttribute() {
	}

    public TextAttribute(AttributeId attributeDetail) {
    	super(attributeDetail);
	}

    /**
     * Sets value of this attribute to the given String value.
     */
    public void setStringValue(String value) {
        if (value != null){
        	String taglessValue = value;
        	if(hasTag()){
	        	taglessValue = setTagAndGetTaglessValue(value);
        	}
        	if(taglessValue.startsWith("0x") || taglessValue.startsWith("0X")){
    			setValueBytes(RadiusUtility.getBytesFromHexValue(taglessValue));
            } else{
            	try{
            		setValueBytes(taglessValue.getBytes(CommonConstants.UTF8));
            	}catch(UnsupportedEncodingException e){
            		setValueBytes(taglessValue.getBytes());
            	}
    		}
        }
    }

    /**
     * Sets value of this attribute to the given String value using given 
     * character set.
     */
    public void setStringValue(String value, String charsetName) 
                throws UnsupportedEncodingException {
        if (value != null)
            setValueBytes(value.getBytes(charsetName));
    }

    /**
     * @return Returns value of this attribute in String format. 
     */
    public String getStringValue() {
    	byte data[] = getValueBytes();
		return RadiusUtility.bytesToHex(data);
    	//return new String(getValueBytes());
    }

    /**
     * @return Returns value of this attribute in String format, using the 
     * given character set.
     */
    public String getStringValue(String charsetName) 
                throws UnsupportedEncodingException {
        return new String(getValueBytes(),charsetName);
    }

    public void doPlus(String value){
    	String hexValue = value;
    	if(!value.startsWith("0x") && !value.startsWith("0X")){
    		hexValue = RadiusUtility.bytesToHex(value.getBytes());
        }
    	hexValue = hexValue.substring(2);
    	setStringValue(getStringValue() + hexValue);
    }
    
    public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		byte valueByte[] = getValueBytes();
		if(getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD){
			byte[] saltBytes = new byte[2];
			byte[] keyBytes = new byte[valueByte.length-2];
			if(valueByte != null && valueByte.length > 2){
				System.arraycopy(valueByte, 0, saltBytes, 0, saltBytes.length);
				System.arraycopy(valueByte, 2, keyBytes, 0, valueByte.length-2);
			}
			strBuilder.append("Salt = ");
			strBuilder.append(RadiusUtility.bytesToHex(saltBytes));
			strBuilder.append(" Value = ");
			strBuilder.append(RadiusUtility.bytesToHex(keyBytes));	
		}else{
			strBuilder.append(RadiusUtility.bytesToHex(valueByte));
		}
		return " = " + strBuilder.toString();
	}
}
