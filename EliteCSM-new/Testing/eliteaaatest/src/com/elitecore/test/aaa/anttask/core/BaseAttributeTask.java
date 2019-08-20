package com.elitecore.test.aaa.anttask.core;

import org.apache.tools.ant.Task;

public abstract class BaseAttributeTask extends Task{

	private int id;
	private String type;
	private String value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	protected int parseInt(String value) {
		int parsedValue = 0;
		try {
			parsedValue = Integer.parseInt(value);
		}catch(NumberFormatException n) {
		}
		return parsedValue;
	}

	protected final String bytesToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        buf.append("0x");
        for (int i = 0; i < data.length; i++) {
        	buf.append(byteToHex(data[i]));
        }
        return (buf.toString());
    }

	protected final String byteToHex(byte data) {
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	protected final char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }
	
	protected final byte[] HexToBytes(String data){
		if(data == null)
			return null;
		if(data.charAt(1) == 'x')
			data = data.substring(2);		
		int len = data.length();
		if(len % 2 != 0)
			len ++ ;		
		byte[] returnBytes = new byte[len/2];
		for(int i=0 ; i<len-1; ){
			returnBytes[i/2] = (byte) (HexToByte(data.substring(i, i+2)) & 0xFF);			
			i +=2;			
		}
		return returnBytes;
	}
	protected final int HexToByte(String data){
		int byteVal = toByte(data.charAt(0)) & 0xFF;
		byteVal = byteVal << 4;
		byteVal = byteVal | toByte(data.charAt(1));
		return byteVal;
	}
	protected final int toByte(char ch){
		if((ch >= '0') && (ch <= '9')){
			return ch - 48;
		}else if((ch >= 'A') && (ch <= 'F')){
			return ch - 65 + 10;
		}else if((ch >= 'a') && (ch <= 'f')){
			return ch - 97 + 10;
		}else {
			return 0;
		}
	}
	
	public abstract byte[] getBytes();

}
