package com.elitecore.coreradius.commons.attributes.cisco;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.TextAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */

public class CiscoCommandCodeAttribute extends TextAttribute{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CODE_KEY = "code";
	private static final String VALUE_KEY = "value";
	private static final int UNINITIALIZED = -1;

	private int code = UNINITIALIZED;
	private String value = null;

	public CiscoCommandCodeAttribute(){
		//this attribute type will always remain avpair type 
		bAvpair = true;
	}

	public CiscoCommandCodeAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
		bAvpair = true;
	}

	/*while setting the string value the spaces in code are ignored as it is an integer value, but in case of value key
	 *the spaces or blanks MUST NOT be trimmed as they have some meaning for CISCO
	 */
	@Override
	public void setStringValue(String value){
		Integer code = null;
		byte[] valueKeyBytes = new byte[0];

		String avpairSeparator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.CISCO_VENDOR_ID);
		StringTokenizer tk = new StringTokenizer(value,avpairSeparator);
		int tokenCount = tk.countTokens();

		if(tokenCount < 1 || tokenCount > 2){
			throw new IllegalArgumentException("Invalid value: " + value + ". At least one key i.e. code was expected, got " + tokenCount + " key/s. Supported keys are " + CODE_KEY + " & " + VALUE_KEY);
		}

		String tokenVal;
		String key;

		while(tk.hasMoreTokens()){
			tokenVal = tk.nextToken(); 
			key = extractKey(tokenVal);
			if(CODE_KEY.equals(key)){
				tokenVal = extractValueOfKey(CODE_KEY, tokenVal);
				try{
					code =  Integer.parseInt(tokenVal.trim());
				}catch(NumberFormatException ex){
					throw new IllegalArgumentException("Invalid value: " + value + ". Value for code key is not integer.");
				}
			}else if(VALUE_KEY.equals(key)){
				valueKeyBytes = extractValueOfKey(VALUE_KEY, tokenVal).getBytes();
			}else{
				throw new IllegalArgumentException("Invalid value: " + value + ". Key : " + key + " not supported. Keys supported are: code and value");
			}
		}

		if(code != UNINITIALIZED){
			if(tokenCount == 2 && valueKeyBytes.length == 0){
				throw new IllegalArgumentException("Invalid value: " + value + ". value key not found");
			}

			byte[] finalBytes = new byte[valueKeyBytes.length + 1];
			finalBytes[0] = (byte) code.intValue();
			System.arraycopy(valueKeyBytes, 0, finalBytes, 1, valueKeyBytes.length);

			setValueBytes(finalBytes);
		}else{
			throw new IllegalArgumentException("Invalid value: " + value + ". code key not found");
		}
	}


	private String extractKey(String value){
		int indexOfEquals = value.indexOf("=");
		if(indexOfEquals == -1 || indexOfEquals == value.length() - 1){
			throw new IllegalArgumentException("Invalid value : " + value);
		}
		return value.substring(0,indexOfEquals).trim();
	}

	private String extractValueOfKey(String token, String value){
		return value.substring(value.indexOf("=") + 1);
	}

	@Override
	public void setValueBytes(byte[] valueBytes) {
		if(valueBytes != null && valueBytes.length > 0){
			this.code = (int)valueBytes[0];
			byte[] valueKeyBytes = Arrays.copyOfRange(valueBytes, 1, valueBytes.length);
			this.value = new String(valueKeyBytes);
			super.setValueBytes(valueBytes);
		}else{
			super.setValueBytes(new byte[0]);
		}
	}

	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int read = super.readFrom(sourceStream);
		byte[] valueBytes = getValueBytes();
		if(valueBytes.length > 0){
			this.code = (int)valueBytes[0];
			this.value = new String(Arrays.copyOfRange(valueBytes, 1, valueBytes.length));
		}
		return read;
	}

	@Override
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		int read = super.readLengthOnwardsFrom(sourceStream);
		byte[] valueBytes = getValueBytes();
		if(valueBytes.length > 0){
			this.code = (int)valueBytes[0];
			this.value = new String(Arrays.copyOfRange(valueBytes, 1, valueBytes.length));
		}
		return read;
	}

	@Override
	public String getStringValue() {
		String avpairSeparator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.CISCO_VENDOR_ID);
		return CODE_KEY + "=" + code + avpairSeparator + VALUE_KEY + "=" + value;
	}

	@Override
	public String getKeyValue(String key) {
		if(CODE_KEY.equals(key)){
			return code + "";
		}else if(VALUE_KEY.equals(key)){
			return (value != null ? this.value : "");
		}
		return "";
	}

	@Override
	public String toString() {
		String avpairSeparator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.CISCO_VENDOR_ID);
		return " = " + CODE_KEY + "=" + code + avpairSeparator + VALUE_KEY + "=" + value;
	}

}
