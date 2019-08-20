package com.elitecore.test.dependecy.diameter.packet.avps.basic;

import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.NegativeValueException;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class AvpUnsigned64  extends BaseDiameterAVP{
	
	public AvpUnsigned64(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	public void setInteger(long lData) {
		byte[] valueBuffer;
		ByteBuffer bData;
		/*
		 * Changes done by SUBHASH PUNANI
		 * Added one exceptions for checking of negative value
		 * 
		 */
		if(lData<0)
			throw new NegativeValueException("Negative Value Found in " + getClass().getName());
		bData = ByteBuffer.allocate(8);
		bData.putLong(lData);
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	
	private void setInteger(BigInteger data) {
		byte[] valueBuffer = data.toByteArray();
		if(valueBuffer[0] != 0){
			throw new NumberFormatException("Value: " + data.toString() + " out of Range.");
		}
		byte[] temp = new byte[8];
		System.arraycopy(valueBuffer, valueBuffer.length-8, temp, 0, temp.length);
		this.setValueBytes(temp);
	}
	
	public long getInteger() {
		byte []valueBuffer;
		long data;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(8);
		valueBuffer = this.getValueBytes();
		if(valueBuffer.length>8)
			bData.put(valueBuffer, 0, 8);
		else {
			bData.put(valueBuffer);
		}
		data = bData.getLong(0);
		return data;
	}
	
	private BigInteger getBigInteger(){
		byte temp [] = new byte[9];
		//Adding extra Sign byte
		System.arraycopy(getValueBytes(), 0, temp, 1, 8);
		return new BigInteger(temp);
	}
	
	public String getStringValue() {
		long value = getInteger(); 
		if(value < 0){
			return getBigInteger().toString();
		}else{
			return String.valueOf(value);
		}
	}
	
	public void setStringValue(String data){
		try{
			setInteger(Long.parseLong(data));
		}catch(NumberFormatException e){
			setInteger(new BigInteger(data));
		}
	}
	
	@Override
	public void doPlus(String value) {
		if(value == null || value.trim().length() == 0){
			return;
		}
		long avpValue = getInteger();
		//AVP value is Big
		if(avpValue < 0){
			BigInteger bigValue;
			try{
				bigValue = new BigInteger(value);
			}catch(NumberFormatException e){
				//Here it is String Representation --> Getting Value form Dictionary
				long dictionaryValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
				if(dictionaryValue >= 0){
					//Value Available
					bigValue = BigInteger.valueOf(dictionaryValue);
				}else{
					throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned64.", e);
				}
			}
			bigValue = getBigInteger().add(bigValue);
			if(bigValue.compareTo(BigInteger.ZERO) == -1 )
				throw new  IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range");
			setInteger(bigValue);
			return;
		}
		
		//AVP value is Small;
		long lValue;
		try{
			lValue = Long.parseLong(value);
			long result = avpValue + lValue;
			if(result < 0){
				//Sum getting Off Range --> Trying to add as Big Integer.
				BigInteger bigValue = getBigInteger().add(new BigInteger(value));
				if(bigValue.compareTo(BigInteger.ZERO) == -1)
					throw new  IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range");
				setInteger(bigValue);
			}else{
				setInteger(result);
			}
		}catch(NumberFormatException e){
			//NFE --> Reason 1: String Representation --> Getting Value form Dictionary
			lValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
			if(lValue < 0){
				//Value not Available --> NFE caused because of Big value --> Taking Big Integer
				BigInteger bigValue;
				try{
					bigValue = new BigInteger(value);
				}catch(NumberFormatException numberFormatException){
					//Big Integer giving NFE --> Reason Invalid String value arrived.
					throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned64.", numberFormatException);
				}
				bigValue = getBigInteger().add(bigValue);
				if(bigValue.compareTo(BigInteger.ZERO) == -1)
					throw new  IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range");
				setInteger(bigValue);
				return;
			}
			//Value from Dictionary Available
			long result = avpValue + lValue;
			if(result < 0){
				//Sum getting Off Range --> Trying to add as Big Integer.
				BigInteger bigValue = getBigInteger().add(new BigInteger(value));
				if(bigValue.compareTo(BigInteger.ZERO) == -1)
					throw new  IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range");
				setInteger(bigValue);
			}else{
				setInteger(result);
			}
		}
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		try{
			String strValue = (String)obj;
			if(strValue!=null){
				if(strValue.equals("*"))
					return true;
				long iValue = 0;
				try{
					iValue = Long.parseLong(strValue);
				}catch(NumberFormatException nfe){
					return false;
				}
				long iAttributeValue = getInteger();
				return iValue== iAttributeValue;
			}
			return false;
		}catch(ClassCastException cce){
			return false;
		}
	}
	*/
	
}
