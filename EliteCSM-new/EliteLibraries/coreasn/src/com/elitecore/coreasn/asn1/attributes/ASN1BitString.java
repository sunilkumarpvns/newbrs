package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ASN1BitString extends BaseASN1Attribute {

	private byte[] value;

	public ASN1BitString() {
		value = new byte[0];
	}

	public int readFrom(InputStream in) throws IOException {
		readLength(in);
		value = new byte[getLength()];

		int pos = 0;
	    while((pos < getLength()) && (in.available()>0)) {
	      int read = in.read(value);

	      if(read > 0) {
	        pos += read;
	      }else if (read < 0) {
	        throw new IOException("Wrong string length " + read + " < " + getLength());
	      }
	    }
		return 1;
	}

	public void writeTo(OutputStream os) throws IOException {
		os.write(3);
		os.write(getLength());
		os.write(value);
	}

	public int getIntValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringValue() {
		try{
			return new String(value,"UTF-8");
		}catch(UnsupportedEncodingException e){
			return new String(value);
		}
	}
	
	public int getCalculatedLength(){
		return 0;
	}

}
