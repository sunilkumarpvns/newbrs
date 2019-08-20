package com.elitecore.corenetvertex.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ProtocolAdapter extends TypeAdapter<String> {

	@Override
	public String read(JsonReader in) throws IOException {
		String protocol = null;

		
		if(in.hasNext() == false) {
			return null;
		}
		protocol = in.nextString();
		if(protocol == null) {
			return null;
		}
		

		if(protocol.equalsIgnoreCase("TCP")) {
			protocol = "6";
		} else if(protocol.equalsIgnoreCase("UPD")) {
			protocol = "17";
		} else if(protocol.equalsIgnoreCase("IP")) {
			protocol = "ip";
		}
	
	
	     
	     return protocol;
	}

	@Override
	public void write(JsonWriter out, String protocol) throws IOException {
		if(protocol != null) {
			if(protocol.equalsIgnoreCase("6")) {
	    		 out.value("TCP");
	    	 } else if(protocol.equalsIgnoreCase("17")) {
	    		 out.value("UDP");
	    	 } else if(protocol.equalsIgnoreCase("ip")) {
	    		 out.value("IP");
	    	 } else {
	    		 out.value(protocol);
	    	 }
		} else {
			out.value((String)null);
		}
	}
	
}
