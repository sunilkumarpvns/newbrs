package com.elitecore.coreasn.asn1;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseASN1TLV {

	public static final int BIT_10000000 = 0x80;

	public static final int BIT_01111111 = 0x7F;
	
	public static final int BIT_11000000 = 0xC0;
	
	public static final int BIT_00100000 = 0x20;
	
	public static final int BIT_00011111 = 0x1F;

	/**
	 * All Tag Type As per ASN.1 Standard
	 */
	

	public static final byte ASN_INTEGER = 0x2;
	
	public static final byte ASN_BIT_STRING = 0x3; 

	public static final byte ASN_OCTET_STRING = 0x4;
	
	public static final byte ASN_NULL = 0x05;

	public static final byte ASN_OBJECT_IDENTIFIER = 0x6;
	
	public static final byte ASN_SEQUENCE = 0x10;
	
	public static final byte ASN_SET = 0x11;
	
	public static final byte ASN_PRINTABLE_STRING = 0x13;
	
	public static final byte ASN_T61STRING = 0x14;
	
	public static final byte ASN_IA5STRING = 0x16;
	
	public static final byte ASN_UTCTIME = 0x17;
	
	public static final int ASN_IMPLICIT = 0xA0;
	
	public static final int PRIMITIVE_ASN_IMPLICIT = 0x80;
	
	public static final byte ASN_UNIVERSAL = 0x00;
	
	public static final byte ASN_APPLICATION = 0x40;
	
	public static final int ASN_CONTEXT_SPECIFIC = 0x80;
	
	public static final int ASN_PRIVATE = 0xC0;
	
	public static final byte ASN_CONSTRUCTED =0x20;
	
	public static final byte ASN_PRIMITIVE = 0x00;
	
	public static final byte ASN_CHOICE = 0x30;

	//Finish Declaration of ASN.1 Tag Types
	
	
	public static final byte ASN_LONG_LEN = (byte) 0x80;

	public static final int V1TRAP = ((byte) 0x80 | (byte) 0x20 | 0x4);

	public static final byte ASN_EXTENSION_ID = (byte) 0x1F;

	public static final byte ASN_CONSTRUCTOR = (byte) 0x20;

	public static final byte ASN_CONTEXT = (byte)0x80;

	public static final byte ASN_BIT8 = (byte)0x80;


	/**
	 * SNMP Codes
	 */
	public static final int SNMP_IPADDRESS = ASN_APPLICATION | 0x00;
	
	public static final int SNMP_COUNTER32 = ASN_APPLICATION | 0x01;
	
	public static final int SNMP_GAUGE = ASN_APPLICATION | 0x02;
	
	public static final int SNMP_TIMETICKS = ASN_APPLICATION | 0x03;
	
	public static final int SNMP_OPAQUE = ASN_APPLICATION | 0x04;  // NOT CLEAR, ANY VALUE
	
	
	public static final int SNMP_GET = ASN_IMPLICIT | 0x0; 
    
    public static final int SNMP_GETNEXT = ASN_IMPLICIT | 0x1;
    
    public static final int SNMP_RESPONSE = ASN_IMPLICIT | 0x2;
    
    public static final int SNMP_SETREQUEST = ASN_IMPLICIT | 0x3;

    public static final int SNMP_TRAP =  ASN_IMPLICIT | 0x4;
    
    public static final int SNMP_ERRORSTATUS_NOERROR = 0x00;
    
    public static final int SNMP_ERRORSTATUS_TOOBIG = 0x01;
    
    public static final int SNMP_ERRORSTATUS_NOSUCHNAME = 0x02;
    
    public static final int SNMP_ERRORSTATUS_BADVALUE = 0x03;
    
    public static final int SNMP_ERRORSTATUS_READONLY = 0x04;
    
    public static final int SNMP_ERRORSTATUS_GENERR = 0x05;
    
    public static final int SNMP_GETBULKREQUEST = ASN_IMPLICIT | 0x5;
    
    public static final int SNMP_TRAPV2 = ASN_IMPLICIT | 0x7;
    
    
    
    public static final int SNMP_UNSPECIFIED =  ASN_NULL ;
    
    public static final int SNMP_NO_SUCH_OBJECT = PRIMITIVE_ASN_IMPLICIT | 0x0;
    
    public static final int SNMP_NO_SUCH_INSTANCE = PRIMITIVE_ASN_IMPLICIT | 0x1;
    
    public static final int SNMP_END_OF_MIBVIEW = PRIMITIVE_ASN_IMPLICIT  | 0x2 ;
    
    public static final int SNMP_ERRORSTATUS_NOACCESS = 0x06;

    public static final int SNMP_ERRORSTATUS_WRONGTYPE = 0x07;
    
    public static final int SNMP_ERRORSTATUS_WRONGLENGTH = 0x08;
    
    public static final int SNMP_ERRORSTATUS_WRONGENCODING = 0x09;
    
    public static final int SNMP_ERRORSTATUS_WRONGVALUE = 0xA;
    
    public static final int SNMP_ERRORSTATUS_NOCREATION = 0xB;
    
    public static final int SNMP_ERRORSTATUS_INCONSISTENTVALUE = 0xC;
    
    public static final int SNMP_ERRORSTATUS_RESOURCEUNAVAILABLE = 0xD;
    
    public static final int SNMP_ERRORSTATUS_COMMITFAILED = 0xE;
    
    public static final int SNMP_ERRORSTATUS_UNDOFAILED = 0xF;
    
    public static final int SNMP_ERRORSTATUS_AUTHORIZATIONERROR = 0x10;
    
    public static final int SNMP_ERRORSTATUS_NOTWRITABLE = 0x11;
    
    public static final int SNMP_ERRORSTATUS_INCONSISTENTNAME = 0x12;


	private static final String MODULE = "Base ASN1 TLV - SNMP";

	protected final int readTag(InputStream in) throws IOException {
		return in.read();
	}

	private int length;

	public final int getLength() {
		return length;
	}
	
	public byte []getLongLengthBytes(int length) {
		byte lenOfLen = (byte)BIT_10000000;
		byte [] len = intToByteArray(length);
		byte [] entireLenarr;
		byte cnt=0;
		int actLen;
		while(len[cnt] == 0) {
			cnt++;
		}
		actLen = 4 - cnt;
		entireLenarr = new byte[1 + (4 - cnt)];
		entireLenarr[0] = (byte)(lenOfLen | (4 - cnt));
		
		for(int counter = 4-actLen,cnt1 = 1; counter < 4 ; counter++ ,cnt1++) {
			entireLenarr[cnt1] = len[counter];
		}
		return entireLenarr;
	}
	public final void setLength(int length) {
		this.length = length;
	}

	protected final int readLength(InputStream in) throws IOException {
		int bytesRead=0;
		int length = 0;
		int lengthByte = in.read();
		bytesRead++;
		
		if ((lengthByte & ASN_LONG_LEN) > 0 ){
			System.out.println("Length form is in large form");
			lengthByte &= ~ASN_LONG_LEN;
			if(lengthByte == 0) {
				throw new IOException(MODULE + ": Infinite length integer not supported");
			}
			if (lengthByte > 4 ) {
				throw new IOException(MODULE + ": integer more than 4 byte long is not supported");
			} 
	
			for (int counter=0; counter<lengthByte; counter++) {
				int l = in.read() & 0xFF;
				bytesRead++;
				length |= (l << (8*((lengthByte-1)-counter)));
			}
			
			if(length < 0) {
				throw new IOException(MODULE + ": Data Length more than 2^31 is not supported");
			}
		} else {
			length = (lengthByte & 0xFF);
		}
		
		if(length < 0) {
			throw new IOException(MODULE + ": Data Length more than 2^31 is not supported");
		}
		this.length = length;
		return bytesRead;
	}
	
	public int getExtraBytes(int integer) {
		if(integer > 255) {
			int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
			return byteNum;
		}else if(integer > 127){
			return 1;
		}else {
			return 0;
		}
	}

	private byte[] intToByteArray (final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];
		
		for (int n = 0; n < byteNum; n++){
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		}
		return (byteArray);
	}
}
