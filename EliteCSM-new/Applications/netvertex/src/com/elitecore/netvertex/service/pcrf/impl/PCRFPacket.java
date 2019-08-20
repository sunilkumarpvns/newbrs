package com.elitecore.netvertex.service.pcrf.impl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Harsh Patel
 */

public class PCRFPacket implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
	private Map<String,String>attributesMap;
	
	public PCRFPacket() {
		attributesMap=new HashMap<String, String>();
	}
	
	public String getAttribute(String name) {
		return attributesMap.get(name);
	}

	public void removeAttribute(String name) {
		attributesMap.remove(name);
	}

	public void setAttribute(String name, String value) {
		if(value == null)
			removeAttribute(name);
		else
			attributesMap.put(name, value);
	}
	
	
	@Override
	public String toString(){
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		if(attributesMap!=null){
			for(Map.Entry<String, String>s : attributesMap.entrySet()){
				out.println("           "+ s.getKey() + " = " + s.getValue());
			}
		}
		out.close();
		
		return stringBuffer.toString();
	}

	public Set<String> getKeySet() {
		return attributesMap.keySet();
	}
	
	@Override
	protected PCRFPacket clone() throws CloneNotSupportedException {
		PCRFPacket copiedPCRFPacket = (PCRFPacket) super.clone();
		copiedPCRFPacket.attributesMap = (Map<String, String>)((HashMap<String, String>) this.attributesMap).clone();
		return  copiedPCRFPacket;
	}
}
