package com.elitecore.corenetvertex.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class FlowAccessAdapter extends TypeAdapter<String> {

	@Override
	public String read(JsonReader in) throws IOException {
		 String flowAccess = null;

		if(in.hasNext() == false) {
			return null;
		}
	
		flowAccess = in.nextString();
		if(flowAccess == null) {
			return null;
		}
		
		return flowAccess.toLowerCase();
	}

	@Override
	public void write(JsonWriter out, String flowAccess) throws IOException {
		if(flowAccess != null) {
			out.value(flowAccess.toUpperCase());
		} else {
			out.value((String)null);
		}
	}
	
}
