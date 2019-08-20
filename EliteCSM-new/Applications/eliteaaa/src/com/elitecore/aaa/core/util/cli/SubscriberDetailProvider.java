package com.elitecore.aaa.core.util.cli;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;

public class SubscriberDetailProvider extends DetailProvider{

	public static final String HELP = "-help";
	
	private static SubscriberDetailProvider subscriberDetailProvider;
	private HashMap<String, DetailProvider> detailProviderMap;

	private SubscriberDetailProvider() {
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	public static SubscriberDetailProvider getInstance() {
		if (subscriberDetailProvider == null) {
			synchronized (SubscriberDetailProvider.class) {
				if (subscriberDetailProvider == null) {
					subscriberDetailProvider = new SubscriberDetailProvider();
				}
			}
		}
		return subscriberDetailProvider;
	}

	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length >0  ){

			if("?".equals(parameters[0]) || HELP.equals(parameters[0])){
				return getHelpMsg();
			}

			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
		}
		return "Invalid Option\n"+getHelpMsg();
	}

	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
			return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return "subscriber";
	}
}
