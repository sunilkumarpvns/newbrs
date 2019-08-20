package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

public abstract class BaseField implements TGPPField{
	
	protected static final int RIGHT_DIGIT_MASK = 15;
	protected static final int LEFT_DIGIT_MASK = 240;
	
	/*
	 * As per the 3GPP standard when there is a hex value F in any of mnc or mcc bytes then
	 * it has to be ignored from the calulation of the mnc or mcc value.
	 */
	private static final int MCC_MNC_IGNORE_VALUE = 0x0F;
	
	
	protected int getMCC(byte[] valueBuffer,int index){		
		int mcc = 0;
		int multiplier = 1;
		
		int mccbyte = valueBuffer[index + 2] & 0xFF & RIGHT_DIGIT_MASK;
		if(mccbyte < MCC_MNC_IGNORE_VALUE){
			mcc = mccbyte;
			multiplier *= 10;
		}
		
		mccbyte = ((valueBuffer[index+1] & LEFT_DIGIT_MASK  & 0xFF) >> 4);
		if(mccbyte < MCC_MNC_IGNORE_VALUE){
			mcc += mccbyte * multiplier;
			multiplier *= 10; 
		}
		
		mccbyte = (valueBuffer[index+1] & RIGHT_DIGIT_MASK  & 0xFF);
		if(mccbyte < MCC_MNC_IGNORE_VALUE){
			mcc += mccbyte * multiplier;
		}
		
		return mcc;
	}
	
	protected int getMNC(byte[] valueBuffer,int index){
		
		int mnc = 0;
		int multiplier = 1;
		
		int mncByte = ((valueBuffer[index+2] & LEFT_DIGIT_MASK  & 0xFF) >> 4 );
		if(mncByte < MCC_MNC_IGNORE_VALUE){
			mnc = mncByte;
			multiplier *= 10;
		}
		
		mncByte = ((valueBuffer[index+3] & LEFT_DIGIT_MASK  & 0xFF) >> 4);
		if(mncByte < MCC_MNC_IGNORE_VALUE){
			mnc += mncByte * multiplier;
			multiplier *= 10;
		}
		
		mncByte = (valueBuffer[index+3] & RIGHT_DIGIT_MASK  & 0xFF);
		if(mncByte < MCC_MNC_IGNORE_VALUE){
			mnc += mncByte * multiplier;
		}

		return mnc;
	}

	protected int getMNC(byte[] valueBuffer){
		return getMNC(valueBuffer,0);
	}
	
	protected int getMCC(byte[] valueBuffer){
		return getMCC(valueBuffer,0);
	}
}
