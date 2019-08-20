package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class ClearCommand extends EliteBaseCommand {

	private static HashMap<String ,DetailProvider> detailProviderMap =new HashMap<String, DetailProvider>();

	public static void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{
		
		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}
		
		if(detailProviderMap.containsKey(detailprovider.getKey())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Clear Command already contains detail provider with Key : " + detailprovider.getKey());
		}
		
		detailProviderMap.put(detailprovider.getKey() , detailprovider);	
	}

	@Override
	public String execute(String parameter) {
		StringTokenizer stk = new StringTokenizer(parameter);
		if(stk.countTokens() > 0){
			String[] parameters = new String[stk.countTokens()];
			for(int i = 0 ; i < parameters.length ; i++){
				parameters[i] = stk.nextToken();
			}
			if(parameters[0].equalsIgnoreCase("?") || parameters[0].equalsIgnoreCase("help")){
				return getHelpMsg();
			}else if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}else{
				return "Invalid Option\n" +getHelpMsg();
			}
		}
		return getHelpMsg();
	}

	@Override
	public String getCommandName() {
		return "clear";
	}

	@Override
	public String getDescription() {
		return "Clears details for given parameter.";
	}

	@Override
	public String getHelpMsg(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" clear <option>");
		out.println(" Description : "+ getDescription());
		if (detailProviderMap.isEmpty()) {
			out.println(" No options are available");
			out.close();
			return stringWriter.toString();
		}
		out.println(" Possible Options : ");
		out.println();
		for(String provider : detailProviderMap.keySet()){
			out.println(" " +provider);
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{'clear':{");
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
		out.print("}}");
		return writer.toString();	
	}

}
