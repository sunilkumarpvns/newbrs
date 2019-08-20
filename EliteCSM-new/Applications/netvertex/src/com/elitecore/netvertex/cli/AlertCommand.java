package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class AlertCommand extends EliteBaseCommand{
	
	private static final String ALERT = "alert";
	
	private static HashMap<String ,DetailProvider> detailProviderMap = new HashMap<String, DetailProvider>();

	public static void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{
		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}

		if(detailProviderMap.containsKey(detailprovider.getKey())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Detail Provider already contains detail provider with Key : " + detailprovider.getKey());
		}

		detailProviderMap.put(detailprovider.getKey() , detailprovider);
	}

	@Override
	public String execute(String parameter) {

		if (Strings.isNullOrBlank(parameter)) {
			return getHelpMsg();
		}
		
		Splitter splitter = Splitter.on(' ');
		String[] parameters = splitter.splitToArray(parameter);
		if (parameters.length > 0) {
			
			if(isHelpParameter(parameters[0])){
				
				return getHelpMsg();
			} else if (detailProviderMap.containsKey(parameters[0])) {
				
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			} else {
				
				return "Invalid Option: " + parameter + getHelpMsg();
			}
		}
		
		return getHelpMsg();
		
	}

	@Override
	public String getCommandName() {
		return ALERT;
	}

	@Override
	public String getDescription() {
		return "Displays alert information";
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("{'" + ALERT + "':{'" + HELP_OPTION + "' :{}");
		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		out.print("}}");
		
		return writer.toString() ;
		
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" " + getCommandName() + "  <option>");
		out.println(" Description: " + getDescription());
		out.println(" Possible Options: ");
		out.println();
		for (Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()) {
			out.println(" " + provider + "\t:\t" + provider.getValue().getDescription());
		}
		out.close();
		return stringWriter.toString();
	}
	
}
