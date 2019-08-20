package com.elitecore.aaa.rm.service.rdr.tlv;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BooleanTLV extends BaseRDRTLV {

	public void setValue(boolean value) {
		byte[] valueBytes=new byte[(int) getLength()];
		long lvalue=0L;
		for(int i=0;i<getLength();i++){
			lvalue = ((lvalue << 8) |  (valueBytes[i] & 0xFF));	
		}
		if(lvalue==0){
			value=false;
		}else{
			value=true;
		}	
	}
	public boolean getValue() {
		//setValue(false);
		if (((int)value[0]) == 1){
			return true;
		}else{
			return false;
		}
	}
	public  String toString() {
		setValue(false);
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		//out.println("Boolean Value : "+ value);
		out.print(getValue());
		return stringBuffer.toString();
	}
}
