package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

public class DiameterStatisticsDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap;
	private static final String DIAMETER		= "diameter";
	
	public DiameterStatisticsDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0){
			return "Provide valid arguments" + getHelpMsg();
		}
		
		if(isHelpSymbol(parameters[0])){
			return getHelpMsg();
		}

		if(detailProviderMap.containsKey(parameters[0])){
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
	
		return "Provide valid arguments" + getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("   "+DIAMETER+" <option>");
		out.println(" Description : Display statistics of diameter interface base on given parameter.");
		out.println(" Possible Options : ");
		out.println();
		for(String provider : detailProviderMap.keySet()){
			out.println(" " +provider);
		}
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
}
