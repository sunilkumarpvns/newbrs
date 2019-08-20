package com.elitecore.aaa.rm.service.rdr;

import java.util.Dictionary;
import java.util.HashMap;

import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.aaa.rm.service.rdr.tlv.BooleanTLV;
import com.elitecore.aaa.rm.service.rdr.tlv.IntegerTLV;
import com.elitecore.aaa.rm.service.rdr.tlv.StringTLV;

public class RDRDictionary {
	public static final String ATTRIBUTE_LIST = "";
	private HashMap<Integer, BaseRDRTLV> typeMap;
	private static RDRDictionary dictionaryInstance;
	private RDRDictionary(){
		typeMap=new HashMap<Integer, BaseRDRTLV>();
	}

	static {
		dictionaryInstance = new RDRDictionary();
	}
	
	public static RDRDictionary getInstance(){
		return dictionaryInstance;
	}
	
	public  void initDictionary() {
		typeMap.put(41,new StringTLV());
		for(int i=11;i<=16;i++){
			typeMap.put(i,new IntegerTLV());
		}
		typeMap.put(31,new BooleanTLV());
	}
	
	public BaseRDRTLV getRDRTLV(int type){
		return typeMap.get(type);
	}
}
