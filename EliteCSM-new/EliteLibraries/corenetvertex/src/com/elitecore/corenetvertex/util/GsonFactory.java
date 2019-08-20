package com.elitecore.corenetvertex.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

//TODO remove GsonFactory class from netvertex
public class GsonFactory {
	
	private static Gson gson;

	static {
		gson = new GsonBuilder().create();
	}

	public static Gson defaultInstance(){ return gson;}
}
