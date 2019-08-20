package com.elitecore.coreradius.commons.attributes.threegpp;

import java.io.ByteArrayOutputStream;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public abstract class GeographicLocationType {
	
	protected static final int DEFAULT_VALUE = 0;
	protected static final byte[] DEFAULT_VALUE_IN_BYTES = {0x0F,0x0F,0x0F};
	protected static final int RIGHT_DIGIT_MASK = 15;
	protected static final int LEFT_DIGIT_MASK = 240;
	private static final String TYPE = "type";
	
	/*
	 * As per the 3GPP standard when there is a hex value F in any of mnc or mcc bytes then
	 * it has to be ignored from the calulation of the mnc or mcc value.
	 */
	private static final int MCC_MNC_IGNORE_VALUE = 0x0F;
	
	public static final GeographicLocationType UNKNOWN = new GeographicLocationType() {
		@Override
		public byte[] getBytes() {
			return null;
		}
		@Override
		public void setBytes(byte[] valueBytes) {
			//NO NEED TO DO ANYTHING HERE
		}
		@Override
		public void setStringValue(String[] fieldTokens) {
			//NO NEED TO DO ANYTHING HERE
		}
		@Override
		public String getStringValue() {
			return "";
		}
		@Override
		public String getFieldValue(String fieldName) {
			return null;
		}
		@Override
		public String toString(){
			return "type=UNKNOWN";
		}
	};
	
	/**
	 * This is a factory method which creates the instance of one of geographic location types
	 * depending on the first byte of the valueBytes
	 * If the value bytes are <code>null</code> or unknown then this method returns UNKNOWN location type
	 * 
	 * @param valueBytes the value bytes that are to be parsed
	 * @return Returns the Geographic location type one of CGI,ECGI etc if known or UNKNOWN otherwise.
	 */
	public static GeographicLocationType getInstanceByBytes(byte[] valueBytes){
		if(valueBytes == null)
			return UNKNOWN;
		
		GeographicLocationType type = parseType(valueBytes[0]);
		type.setBytes(valueBytes);
		return type;
	}
	
	private static GeographicLocationType parseType(byte type){
		switch(type){
		case 0:
			return new CGILocationType();
		case 1:
			return new SAILocationType();
		case 2:
			return new RAILocationType();
		case (byte) 128:
			return new TAILocationType();
		case (byte) 129:
			return new ECGILocationType();
		case (byte) 130:
			return new TAIandECGILocationType();
		default:
			return UNKNOWN;
		}
	}
	
	/**
	 * This is a factory method which creates the instance of one of geographic location types
	 * depending on the type in string that is to be set.
	 * <p>If type is missing in the string then the UNKNOWN type is returned.</p>
	 * <p></p>
	 * If the String is <code>null</code> or does not contain type then this method returns UNKNOWN location type
	 * 
	 * @param String representation in <b>AVPair format</b> that are to be parsed. <b>e.g type=0,ci=1,mcc=999,mnc=999,lac=123</b>
	 * @return Returns the Geographic location type one of CGI,ECGI etc if known or UNKNOWN otherwise. If the string argument is null
	 * the UNKNOWN location type is returned.
	 */
	public static GeographicLocationType getInstanceByAVPair(String valueString){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		GeographicLocationType type = UNKNOWN;
		String tokens[] = valueString.split(AVPAIR_SEPERATOR);
		int index = findToken(tokens, TYPE);
		if(index != -1){
			type = parseType((byte)getValueFromKeyValuePair(tokens[index]));
			type.setStringValue(tokens);
		}
		return type;
	}
	
	protected static int findToken(String tokens[],String token){
		for(int i = 0; i < tokens.length ; i++){
			if(tokens[i].trim().toLowerCase().startsWith(token))
				return i;
		}
		return -1;
	}
	
	protected static int getValueFromKeyValuePair(String keyValuePair){
		if(keyValuePair == null)
			return -1;
		
		String[] tokens = keyValuePair.split("[=]");
		if(tokens.length != 2)
			return -1;
		
		try{
			return Integer.parseInt(tokens[1].trim());
		}catch (NumberFormatException ex) {
			return -1;
		}
	}
	
	protected static byte[] getValueFromKeyValuePairInBytes(String keyValuePair){
		if(keyValuePair == null)
			return new byte[]{};
		
		String[] tokens = keyValuePair.split("[=]");
		if(tokens.length != 2)
			return new byte[]{};
		
		try{
			byte[] valueBytes = {0x0F,0x0F,0x0F};
			char[] chars = tokens[1].trim().toCharArray();
			for(int i = chars.length - 1; i >= 0; i-- ){
				try{
					valueBytes[valueBytes.length - (i+1)] = (byte) Integer.parseInt(chars[i]+"");
				}catch(NumberFormatException ex){
					return new byte[]{};
				}
			}
			return valueBytes;
		}catch (NumberFormatException ex) {
			return new byte[]{};
		}
	}
	
	protected static String getStringValueForMNCandMCC(byte[] bytes){
		StringBuffer valueBuffer = new StringBuffer();
		for(int i = bytes.length - 1; i >= 0; i--){
			if(bytes[i] < MCC_MNC_IGNORE_VALUE){
				valueBuffer.append(bytes[i]);
			}
		}
		return valueBuffer.toString();
	}
	
	public abstract byte[] getBytes();
	public abstract void setBytes(byte[] valueBytes);
	public abstract void setStringValue(String[] fieldTokens);
	public abstract String getStringValue();
	public abstract String getFieldValue(String fieldName);
	
	
	protected static final byte[] getMCC(byte[] valueBuffer, int index){
		byte[] mccBytes = new byte[3];
		
		mccBytes[0] = (byte) (valueBuffer[index + 2] & 0xFF & RIGHT_DIGIT_MASK);
		
		mccBytes[1] = (byte) ((valueBuffer[index+1] & LEFT_DIGIT_MASK  & 0xFF) >> 4);
		
		mccBytes[2] = (byte) (valueBuffer[index+1] & RIGHT_DIGIT_MASK  & 0xFF);
		
		return mccBytes;
	}
	
	
	protected static final byte[] getMNC(byte[] valueBuffer, int index){
		byte[] mncBytes = new byte[3];
		
		mncBytes[0] = (byte) ((valueBuffer[index+2] & LEFT_DIGIT_MASK  & 0xFF) >> 4 );
		
		mncBytes[1] = (byte) ((valueBuffer[index+3] & LEFT_DIGIT_MASK  & 0xFF) >> 4);
		
		mncBytes[2] = (byte) (valueBuffer[index+3] & RIGHT_DIGIT_MASK  & 0xFF);

		return mncBytes;
	}
	
	protected static final byte[] getMNC(byte[] valueBuffer){
		return getMNC(valueBuffer,0);
	}
	
	protected static final byte[] getMCC(byte[] valueBuffer){
		return getMCC(valueBuffer,0);
	}
	
	protected static final int getTokenValue(String[] valueTokens , String token){
		int index = findToken(valueTokens,token);
		if(index == -1)
			return DEFAULT_VALUE;
		
		int returnVal = getValueFromKeyValuePair(valueTokens[index]);
		return returnVal != -1 ? returnVal : DEFAULT_VALUE;
	}
	
	protected static final byte[] getTokenValueInBytes(String[] valueTokens , String token){
		int index = findToken(valueTokens,token);
		if(index == -1)
			return new byte[]{0x0F,0x0F,0x0F};
		
		byte[] returnVal = getValueFromKeyValuePairInBytes(valueTokens[index]);
		return returnVal.length != 0 ? returnVal : DEFAULT_VALUE_IN_BYTES;
	}
	
	/*
	 * NOTE: Use this method only and only for MNC and MCC values
	 * 
	 * STEP 1 - This method first converts the integer value to its radix 10 equivalent bytes.
	 * E.g. if 12345 is input then this step will generate 5,4,3,2,1 as bytes
	 * 
	 * STEP 2 - Then as the length of MCC and MNC can be at the max 999 so if the bytes formed 
	 * by step 1 are greater than 3 it throws exception else then it normalizes the byte array to 
	 * be returned by padding 0s if the size of bytes in step 1 is less than 3
	 */
	//TODO check when the value is 0
	protected static final byte[] intToRadix10Bytes(int val){
		//STEP 1
		ByteArrayOutputStream stream = new ByteArrayOutputStream(3);
		do{
			stream.write(val % 10);
			val = val / 10;
		}while(val > 0);
		byte[] radix10Vals = stream.toByteArray();
		
		//STEP 2
		int len = radix10Vals.length;
		int offset = 3 - len;			//3 because the max value of MCC and MNC can be 999 so no of bytes can be at the max 3
		if(offset < 0)
			throw new IllegalArgumentException();
		byte[] finalBytes = {0x0F,0x0F,0x0F};
		for(int i = 0 ; i < len ; i++ ){
			finalBytes[i + offset] = radix10Vals[i];
		}
		return finalBytes;
	}
	
	
	public static void main(String args[]){
		String key = "key=123";
		byte[] val = getValueFromKeyValuePairInBytes(key);
		System.out.println(RadiusUtility.bytesToHex(val));
		System.out.println(RadiusUtility.bytesToHex(intToRadix10Bytes(123)));
}
}
