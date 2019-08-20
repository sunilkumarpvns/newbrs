package com.elitecore.aaa.diameter.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class DiameterDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap =new HashMap<String, DetailProvider>();

	private static final String DIAMETER = "diameter";
	private static final String HELP = "help";
	
	@Override
	public String execute(String[] parameters) {
		if(parameters.length >0  ){

			if("?".equals(parameters[0]) || HELP.equals(parameters[0])){
				return getHelpMsg();
			}else if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get( parameters[0]).execute( Arrays.copyOfRange(parameters, 1, parameters.length));
			}else{
				return "Invalid Option\n"+getHelpMsg();
			}
		}
		return getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println(StringUtility.fillChar("-", 80, '-'));
		for(DetailProvider childProviders : detailProviderMap.values()){
			out.print(childProviders.getHelpMsg());
		}
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return DIAMETER;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'"+DIAMETER+"':{");
		int size = detailProviderMap.size();
		int i =1;
		for(DetailProvider provider : detailProviderMap.values()){
			if(i != (size)){
				out.print(provider.getHotkeyHelp() + ",");
			}else{
				out.print(provider.getHotkeyHelp());
			}
			i++;
		}
		out.print("}");
		return writer.toString();	
	}
}
