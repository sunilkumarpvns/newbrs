package com.elitecore.aaa.core.radius.service.auth.handlers;

import com.elitecore.aaa.core.radius.service.auth.handlers.RadEapHandlerValues.RadEapHandlerValuesBuilder;


public class RadEapHandlerValuesProvider {
	public static RadEapHandlerValuesBuilder defaultRSABuilder;
	public static RadEapHandlerValuesBuilder defaultDSABuilder;
	static{
		try {
			defaultRSABuilder = new RadEapHandlerValues.
			RadEapHandlerValuesBuilder().getDefaultRSABuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			defaultDSABuilder = new RadEapHandlerValues.
			RadEapHandlerValuesBuilder().getDefaultDSABuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};
}

