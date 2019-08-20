package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;


public class SendPacketCommand extends EliteBaseCommand{

	private static HashMap<String ,DetailProvider> detailProviderMap =new HashMap<String, DetailProvider>();

	public static void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{

		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}
		if(detailProviderMap.containsKey(detailprovider.getKey())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Send Packet Detail Provider already contains detail provider with Key : " + detailprovider.getKey());
		}
		detailProviderMap.put(detailprovider.getKey() , detailprovider);	
	}

	@Override
	public String execute(String parameter) {

		String[] parameters = ParserUtility.splitString(parameter, ' ');
		if(parameters.length > 0){

			if(parameters.length == 1 && parameters[0] != null && (parameters[0].equalsIgnoreCase("?") || parameters[0].equalsIgnoreCase("help"))){
				return getHelpMsg();
			}

			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
		}
		return "Invalid Option\n" +getHelpMsg();
	}

	@Override
	public String getCommandName() {
		return "sendpacket";
	}

	@Override
	public String getDescription() {
		return "Send Packet fo specific protocol as per given arguments.";
	}

	@Override
	public String getHelpMsg(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" " + getCommandName() + " <protocol>");
		out.println(" Description : "+ getDescription());
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
		out.print("{'" + getCommandName() + "':{");
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
