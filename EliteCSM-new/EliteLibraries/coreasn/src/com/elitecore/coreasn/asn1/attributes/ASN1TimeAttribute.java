package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ASN1TimeAttribute extends BaseASN1Attribute {

	private int integerValue;
	private int length;
	private byte bValue[];

	public ASN1TimeAttribute() {
	}

	public ASN1TimeAttribute(int i) {
		setValue(i);
	}

	public int readFrom(InputStream in) throws IOException {
		int bytesRead=0;
		
		bytesRead += readLength(in);
		
		int length = getLength();
		for (int counter=0; counter<length; counter++) {
			int l = in.read() & 0xFF;
			bytesRead++;
			integerValue |= (l << (8*((length-1)-counter)));
		}
		setValue(integerValue);
		return bytesRead;
	}


	public final void writeTo(OutputStream os) throws IOException {
		os.write((byte)SNMP_TIMETICKS);
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

}
