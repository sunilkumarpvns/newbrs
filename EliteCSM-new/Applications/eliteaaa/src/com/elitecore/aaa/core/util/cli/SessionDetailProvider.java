package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class SessionDetailProvider extends DetailProvider {

	public static final String HELP = "-help";
	
	private static SessionDetailProvider sessionDetailProvider;
	private HashMap<String, DetailProvider> detailProviderMap;

	public SessionDetailProvider(AAAServerContext serverContext) {
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	public static SessionDetailProvider getInstance(AAAServerContext serverContext) {
		if (sessionDetailProvider == null) {
			synchronized (SessionDetailProvider.class) { //NOSONAR - Reason: Double-checked locking should not be used
				if (sessionDetailProvider == null) {
					sessionDetailProvider = new SessionDetailProvider(serverContext);
				}
			}
		}
		return sessionDetailProvider;
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
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("Usage :show session [<options>]");
		out.println(StringUtility.fillChar("-", 80, '-'));
		for(DetailProvider childProviders : detailProviderMap.values()){
			out.print(childProviders.getHelpMsg());
		}
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "'session':{'eap':{'-device_id':{'-count':{}},'-all':{'-count':{}},'-sub_id':{'-count':{}},'-nas_id':{'-count':{}}},'wimax':{'-device_id':{'-count':{}},'-all':{'-count':{}},'-sub_id':{'-count':{}},'-nas_id':{'-count':{}}},'-help':{}}";
	}

	@Override
	public String getKey() {
		return "session";
	}
}
