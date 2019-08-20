package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ASN1OctetStringAttribute extends BaseASN1Attribute {

	private byte[] stringValue;
	//private static final String MODULE = "ASN1OctetString - SNMP"; 

	public ASN1OctetStringAttribute(){
		stringValue = new byte[0];
	}

	public ASN1OctetStringAttribute(String value){
		try{
			stringValue = value.getBytes("UTF-8");
		}catch(UnsupportedEncodingException e){
			stringValue = value.getBytes();
		}
	}

	public int readFrom(InputStream in) throws IOException {
		//Logger.logDebug(MODULE,"Reading Data");
		int bytesRead=0;
		bytesRead += readLength(in);

		stringValue = new byte[getLength()];
		in.read(stringValue);
		bytesRead += getLength();
		return bytesRead;
	}

	public int getIntValue() {
		return 0;
	}

	public int getCalculatedLength() {
		int length=0;
		length += stringValue.length;
		return length;
	}
	public String getStringValue() {
		try{
			return new String(stringValue,"UTF-8");
		}catch(UnsupportedEncodingException e){
			return new String(stringValue);
		}
	}

	public void writeTo(OutputStream os) throws java.io.IOException {
		//Logger.logTrace(MODULE,"Writing Data");
		os.write((byte)(ASN_UNIVERSAL | ASN_PRIMITIVE | ASN_OCTET_STRING));
		if(getCalculatedLength() > 127) {
			os.write(getLongLengthBytes(getCalculatedLength()));
		}else {
			os.write((byte)(getCalculatedLength() & 0xFF));
		}
		os.write(stringValue);
	}

	public void setStringValue(String str) {
		try{
			stringValue = str.getBytes("UTF-8");
		}catch(UnsupportedEncodingException e){
			stringValue = str.getBytes();
		}
	}

}