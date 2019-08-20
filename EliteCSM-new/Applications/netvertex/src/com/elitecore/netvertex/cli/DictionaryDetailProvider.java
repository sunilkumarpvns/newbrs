package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

public class DictionaryDetailProvider extends DetailProvider{

	private static final String DICTIONARY		= "dictionary";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static DictionaryDetailProvider dictionaryDetailProvider;
	
	static{
		dictionaryDetailProvider = new DictionaryDetailProvider();
	}
	
	private DictionaryDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	public static DictionaryDetailProvider getInstance(){
		return dictionaryDetailProvider;
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
		
		return getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" " + DICTIONARY +" <option>");
		out.println(" Description : Display dictionary infomation based on given parameter.");
		out.println(" Possible Options : ");
		for(String provider : detailProviderMap.keySet()){
			out.println("   " + provider);
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return DICTIONARY;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
