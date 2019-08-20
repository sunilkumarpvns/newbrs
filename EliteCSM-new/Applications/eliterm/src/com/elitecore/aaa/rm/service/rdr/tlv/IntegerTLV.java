package com.elitecore.aaa.rm.service.rdr.tlv;

import java.io.PrintWriter;
import java.io.StringWriter;

public class IntegerTLV extends BaseRDRTLV{

	public long getValue() {
		long lvalue=0L;
		byte[] valueBytes=new byte[(int) getLength()];
		valueBytes=getBytes();
		for(int i=0;i<getLength();i++){
			lvalue = ((lvalue << 8) |  (valueBytes[i] & 0xFF));	
		}
		return lvalue;
	}
	public  String toString() {
		//setValue(0L);
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		//out.println("Integer Value : "+ getValue());
		out.print(getValue());
		return stringBuffer.toString();
	}	
}
