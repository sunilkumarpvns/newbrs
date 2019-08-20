package com.elitecore.aaa.rm.service.rdr.tlv;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringTLV extends BaseRDRTLV {

	public String getValue() {
		//this.value = new String(getBytes());
		return new String(getBytes());
	}	
	
	public  String toString() {
		//setValue("");
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		//out.println("String Value : "+ getValue());
		out.print(getValue());
		return stringBuffer.toString();
	}
}
