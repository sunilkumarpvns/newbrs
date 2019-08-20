package com.elitecore.aaa.rm.service.rdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;

public abstract class BaseRDR {
	public HashMap<Integer, BaseRDRTLV> fields=new HashMap<Integer, BaseRDRTLV>();
	
	public void addField(int id,BaseRDRTLV rdrTLV){
		fields.put(id, rdrTLV);
	}
	public HashMap<Integer, BaseRDRTLV> getField(){
		return fields;
	}
	
	public abstract void read(InputStream is);
	
	public BaseRDRTLV getTLV(InputStream is){
		int type=0;
		long length=0;
		byte[] bytes = null;
		try {
			type = is.read();
			length=is.read();
			length=(((((length << 8 ) | (is.read() & 0xFF)) <<8 )| (is.read() & 0xFF)) << 8) | (is.read());
			bytes = new byte[(int) length];
			is.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BaseRDRTLV rdrTLV=RDRDictionary.getInstance().getRDRTLV(type);
		rdrTLV.setLength(length);
		rdrTLV.setType(type);
		rdrTLV.setBytes(bytes);
		return rdrTLV;
	}
}
