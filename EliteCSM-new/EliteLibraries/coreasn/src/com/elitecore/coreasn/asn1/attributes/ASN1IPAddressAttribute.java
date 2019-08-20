package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ASN1IPAddressAttribute extends BaseASN1Attribute {

	private byte[] stringValue;
	private static final String MODULE = "ASN1IPAddress - SNMP"; 

	public ASN1IPAddressAttribute(){
		stringValue = new byte[0];
	}

	public ASN1IPAddressAttribute(String value){
		try{
			stringValue = value.getBytes("UTF-8");
		}catch(UnsupportedEncodingException e){
			stringValue = value.getBytes();
		}
	}

	public int readFrom(InputStream in) throws IOException {
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
		os.write((byte)SNMP_IPADDRESS);
		if(getCalculatedLength() > 127) {
			os.write(getLongLengthBytes(getCalculatedLength()));
		}else {
			os.write((byte)(getCalculatedLength() & 0xFF));
		}
		os.write(stringValue);
	}

	public void setStringValue(String str) {
//		stringValue = str.getBytes();
		try {
			stringValue = InetAddress.getByName(str).getAddress();
			
		} catch (UnknownHostException e) {
		}
	}

}