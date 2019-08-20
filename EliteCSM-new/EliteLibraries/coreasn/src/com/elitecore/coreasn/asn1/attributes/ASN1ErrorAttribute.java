package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ASN1ErrorAttribute extends BaseASN1Attribute{

	private int tagValue;
	private IASN1Attribute variable;
	
	//private static final String MODULE = "ASN1ErrorAttribute - SNMP";

	public ASN1ErrorAttribute() {
	}

	public ASN1ErrorAttribute(int tag) {
		setTag(tag);
	}

	private void setTag(int tag) {
		this.tagValue = tag;
		//bTagValue = intToByteArray(tag);
	}
	
	public int getTagValue() {
		return tagValue;
	}
	
	private byte[] intToByteArray (final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		//length = byteNum;
		byte[] byteArray = new byte[4];
		
		for (int n = 0; n < byteNum; n++){
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		}
		return (byteArray);
	}

	public int readFrom(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeTo(OutputStream os) throws IOException {
		if(tagValue == SNMP_END_OF_MIBVIEW){
			byte ans[] = intToByteArray(tagValue);
			os.write((byte)(ans[3] & 0xFF));
		}else if(tagValue == SNMP_NO_SUCH_OBJECT){
			byte ans[] = intToByteArray(tagValue);
			os.write((byte)(ans[3] & 0xFF));
		}else if(tagValue == SNMP_NO_SUCH_INSTANCE){
			byte ans[] = intToByteArray(tagValue);
			os.write((byte)(ans[3] & 0xFF));
		}	
			if(getCalculatedLength() > 127) {
				os.write(getLongLengthBytes(getCalculatedLength()));
			}else {
				os.write((byte)(getCalculatedLength() & 0xFF));
			}
			
	}

	
	public int getCalculatedLength() {
		return 0;
	}


	public void setVariable(IASN1Attribute attribute) {
		variable = attribute;
	}

	public IASN1Attribute getVariable() {
		return variable;
	}

	public int getIntValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
