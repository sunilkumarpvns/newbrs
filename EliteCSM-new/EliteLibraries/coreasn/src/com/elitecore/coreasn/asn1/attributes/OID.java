package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class OID extends BaseASN1Attribute {

	private int[] oid;
	boolean flag = false;
	private static final String MODULE = "Object Identifier - SNMP";

	public int readFrom(InputStream in) throws IOException {
		int[] oid;
		int bytesRead = 0;
		int subidentifier;
		bytesRead += readLength(in);
		int length = getLength();
		oid = new int[length + 1];
		
		if(length == 0) {
			oid[0] = oid[1] = 0;
		}
		
		int pos = 1;
		while(length > 0) {
			subidentifier = 0;
			int b;
			do {	/* shift and add in low order 7 bits */
				int next = in.read();
				bytesRead++;
				if (next < 0) {
					throw new IOException("Unexpected end of input stream");
				}
				b = next & 0xFF;
				subidentifier = (subidentifier << 7) + (b & ~ASN_BIT8);
				length--;
	        } while ((length > 0) && ((b & ASN_BIT8) != 0));	/* last byte has high bit clear */
			oid[pos++] = subidentifier;
	      }

	      /*
	      * The first two subidentifiers are encoded into the first component
	      * with the value (X * 40) + Y, where:
	      *	X is the value of the first subidentifier.
	      *   Y is the value of the second subidentifier.
	      */
	      subidentifier = oid[1];
	      if (subidentifier == 0x2B){
	        oid[0] = 1;
	        oid[1] = 3;
	      }
	      else {
	        oid[1] = (subidentifier % 40);
	        oid[0] = ((subidentifier - oid[1]) / 40);
	      }
	      setValue(oid);
	      
	      return bytesRead;
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

	public String getStrOID() {
		StringBuffer strOID = new StringBuffer();
		int cnt;
		for(cnt=0;cnt<oid.length;cnt++) {
			strOID.append('.');
			strOID.append(oid[cnt]);
		}
		return strOID.toString();
	}

	public String getStrOIDwithZero() {
		StringBuffer strOID = new StringBuffer();
		int cnt;
		for(cnt=0;cnt<oid.length;cnt++) {
			strOID.append('.');
			strOID.append(oid[cnt]);
		}
		if(oid[cnt-1] != 0) {
			strOID.append(".0");
		}
		return strOID.toString();
	}

	public boolean isLastZero() {
		 return(oid[oid.length-1] == 0);
	 }
	 
	 public void removeLastZero() {
		 int [] temp = new int[oid.length-1];
		 
		 System.arraycopy(oid, 0, temp,0, oid.length-1);
		 oid = temp;
	 }
	 
	 public void increamentLastComponent() {
		 oid[oid.length-1] ++;
	 }
	 
	 public void decreseLastComponent() {
		 oid[oid.length-1] --;
	 }

	 public void putLastZero() {
		 int [] temp = new int[oid.length + 1];
		 		
		 System.arraycopy(oid, 0, temp, 0, oid.length);
		 temp[oid.length] = 0;

		 oid = temp;
	 }
	 
	 public void expandOID (int lastComponent) {
		 int [] temp = new int[oid.length + 1];
		
		 System.arraycopy(oid, 0,temp, 0,oid.length); 
		 temp[oid.length] = lastComponent;
		
		 oid = temp;
	 }
	 
	 public String getStrOIDwithoutLastComponent () {
		StringBuffer strOID = new StringBuffer();
		int cnt;
		for(cnt=0;cnt<oid.length-1;cnt++) {
			strOID.append('.');
			strOID.append(oid[cnt]);
		}
		return strOID.toString();
	 }
	 
	 public int getLastComponent() {
		 return oid[oid.length-1];
	 }
	 
	 public void removeLastComponent() {
		 int [] temp = new int[oid.length-1];
		 		
		 System.arraycopy(oid, 0, temp,0, oid.length-1);
		 oid = temp;
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

	public int getIntValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
