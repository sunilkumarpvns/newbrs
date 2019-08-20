package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ASN1ObjectIdentifierAttribute extends BaseASN1Attribute{
	
	private int[] oid;
	boolean flag = false;
	//private static final String MODULE = "ASN1ObjectIdentifierAttribute - SNMP";

	public ASN1ObjectIdentifierAttribute() {
	}

	public ASN1ObjectIdentifierAttribute(int[] oid) {
		setValue(oid);
	}
	
	
	public int readFrom(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeTo(OutputStream os) throws IOException {
		int len = getCalculatedLength();
		os.write((byte) (ASN_UNIVERSAL | ASN_PRIMITIVE| ASN_OBJECT_IDENTIFIER));
		os.write((byte)len);
		
		for(int cnt=0;cnt<len;cnt++) {
			os.write((byte)oid[cnt]);
		}
	}
	
	public void setValue(int[] oid) {
		this.oid = oid;
	}

	public int[] getOID() {
		return oid;
	}

	public int getIntValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCalculatedLength() {

		 if(!flag) {
			 ArrayList <Integer> tempOID = new ArrayList <Integer>();
			 int len=0;
			 int tempOIDarr [];
			 if (oid[0] == 1 && oid[1] == 3){
				 tempOID.add(43);
				 len++;
			 }else {
				 tempOID.add(((oid[1] + (oid[0] * 40)) & 0xFF));
				 len++;
			 }
			 for(int cnt=2; cnt<oid.length ;cnt++) {
				 long subid = (oid[cnt] & 0xFFFFFFFFL);
				
				 if(oid[cnt] <= 127) {
					 tempOID.add((oid[cnt] & 0xFF));
					 len++;
				 } else {
					 long mask = 0x7F; /* handle subid == 0 case */
					 long bits = 0;
	
			        /* testmask *MUST* !!!! be of an unsigned type */
					 for (long testmask = 0x7F, testbits = 0; testmask != 0; testmask <<= 7, testbits += 7) {
						 if ((subid & testmask) > 0) {	/* if any bits set */
							 mask = testmask;
							 bits = testbits;
						 }
					 }
			        /* mask can't be zero here */
					 for (; mask != 0x7F; mask >>= 7, bits -= 7){
			          /* fix a mask that got truncated above */
						 if (mask == 0x1E00000) {
							 mask = 0xFE00000;
						 }
						 tempOID.add((int)(((subid & mask) >> bits) | ASN_BIT8));
						 len++;
					 }
					 tempOID.add((int)(subid & mask));
					 len++;
				 }
			 }
			 
			 Iterator itr = tempOID.iterator();
			 tempOIDarr = new int[len];
			 int cnt=0;
			 while(itr.hasNext()) {
				 tempOIDarr[cnt] = (Integer) itr.next();
				 cnt++;
			 }
		 
			 this.oid = tempOIDarr;
			 flag = true;
			 return len;
		 }else {
			 return oid.length;
		 }
	 
	}

	
}
