/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author nitul.kukadia
 *
 */
public class BaseRDRFields {
	
	private int type=0;
	private long length=0;
	private String value;
	private long lvalue=0L;
	
	public int readOnwordsLength(InputStream is,int c) throws IOException {
		int bytesread=0;
		bytesread=getLength(is);		
		if(type==41){
			byte[] valuebytes=new byte[(int)length];
			for(int i=0;i<length;i++){
				valuebytes[i]=(byte) is.read();
				bytesread++;
			}
			value=new String(valuebytes);
		}
		else{
			for(int i=0;i<length;i++){
				lvalue = ((lvalue << 8) |  (is.read() & 0xFF));	
				bytesread++;
			}			
		}
		return bytesread;
	}
	
	private int getLength(InputStream is) throws IOException {
		length=0;
		length=is.read();
		length=(((((length << 8 ) | (is.read() & 0xFF)) <<8 )| (is.read() & 0xFF)) << 8) | (is.read());
		return 4;
	}
	
	public void setType(int type) {
		this.type=type;		
	}
	
	public int getType() {
		return type;
	}
	
	public long getLength() {
		return length;		
	}
	
	public String getValue() {
		return value;
	}
	
	public long getLValue() {
		return lvalue;
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.print("Type , " + getType());	
		out.print("; Length , " + length);
		out.print("; Value , "+(value!=null ? value : lvalue));
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
}
