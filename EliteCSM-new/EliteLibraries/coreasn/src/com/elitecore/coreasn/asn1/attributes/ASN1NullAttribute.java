package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ASN1NullAttribute extends BaseASN1Attribute{
	
	int tagValue = ASN_NULL;
	int length=0;
	int integerValue=0;
	//private static final String MODULE= "ASN1NullAttribute - SNMP";
	
	public ASN1NullAttribute() {
	}

	public int readFrom(InputStream in) throws IOException {
		readLength(in);
		length = getLength();
		return 1;
	}


	public final void writeTo(OutputStream os) throws IOException {
		os.write((byte) (ASN_UNIVERSAL | ASN_PRIVATE | ASN_NULL));
		os.write((byte)getCalculatedLength());
	}


	public int getIntValue() {
		return integerValue;
	}

	public String getStringValue() {
		return String.valueOf(integerValue);
	}

	public void setValue(int value) {
		this.integerValue = value;
	}
	
	public int getCalculatedLength() {
		return 0;
	}
	
	public String toString() {
		return "Variable Type : NULL";
	}
}
