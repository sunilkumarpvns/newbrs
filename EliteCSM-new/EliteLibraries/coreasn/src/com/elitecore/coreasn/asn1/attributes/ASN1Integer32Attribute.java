package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ASN1Integer32Attribute extends BaseASN1Attribute {

	private int integerValue=0;
	private int length;
	private byte bValue[];

	public ASN1Integer32Attribute() {
	}

	public ASN1Integer32Attribute(int i) {
		setValue(i);
	}

	public int readFrom(InputStream in) throws IOException {
		int bytesRead=0;
		
		bytesRead += readLength(in);
		
		int length = getLength();
		for (int counter=0; counter<length; counter++) {
			int tempInt = in.read() & 0xFF;
			bytesRead++;
			//integerValue = (integerValue << 8) | l;
			integerValue |= (tempInt << (8*((length-1)-counter)));
		}
		setValue(integerValue);
		return bytesRead;
	}


	public final void writeTo(OutputStream os) throws IOException {
		os.write((byte)(ASN_UNIVERSAL|ASN_PRIMITIVE | ASN_INTEGER));
		if(getCalculatedLength() > 127) {
			os.write(getLongLengthBytes(getCalculatedLength()));
		}else {
			os.write((byte)(getCalculatedLength() & 0xFF));
		}
		for(int cnt = 4-length;cnt<4;cnt++) {
			os.write(bValue[cnt]);
		}
	}
	
	public int getIntValue() {
		return integerValue;
	}

	public String getStringValue() {
		return String.valueOf(integerValue);
	}

	public void setValue(int value) {
		bValue = intToByteArray(value);
		this.integerValue = value;
	}

	public int getCalculatedLength() {
		return length;
	}
	
	private byte[] intToByteArray (final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		length = byteNum;
		byte[] byteArray = new byte[4];
		
		for (int n = 0; n < byteNum; n++){
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		}
		return (byteArray);
	}
	
	public String toString() {
		return String.valueOf("In Integer : " + integerValue);
	}
}
