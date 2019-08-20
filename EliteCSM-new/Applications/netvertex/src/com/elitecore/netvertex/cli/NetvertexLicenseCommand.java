package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseModuleConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.util.SystemUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class NetvertexLicenseCommand extends EliteBaseCommand {
	
	public static final String LICENSE_INFO_ACRONYM = "-v";

	public static final String LICENSE_INFO = "-view";

	private static final String MODULE = "NetVertex";
	
	//--Fields for output in Tabuler format--//
	private static final int DISP_NAME_WIDTH = 30;
	private static final int VALIDITY_WIDTH = 60;
	private int[] width = {DISP_NAME_WIDTH, VALIDITY_WIDTH};
	private String[] header = {"License Key","Value"};

	@Override
	public String execute(String parameter) {
		String responseMessage = "";
		String cmdOption = "";

		try {
			
			if (parameter != null) {

				String[] cmdParameters = parseArgumentString(parameter);

				if (cmdParameters != null && cmdParameters.length >= 1) {
					cmdOption = cmdParameters[0];
				} else
					cmdOption = parameter.trim();

				if (cmdOption.equalsIgnoreCase(NetvertexLicenseCommand.LICENSE_INFO)
						|| cmdOption.equalsIgnoreCase(NetvertexLicenseCommand.LICENSE_INFO_ACRONYM)) {

					responseMessage = showLicenseInformation();

				}
				else {
					responseMessage = getHelpMsg();
				}
			} else {
				responseMessage = getHelpMsg();
			}
		}  catch (Exception e){
			responseMessage = e.getMessage();
			getLogger().error(MODULE, "Error occurred while executing License command. " +
					"Reason: "+e.getMessage());
			getLogger().trace(e);
		}

		return responseMessage;

	}

	@Override
	public String getCommandName() {
		return "license";
	}

	@Override
	public String getDescription() {
		return "display license information";
	}
	
	@Override
	public String getHotkeyHelp() {
		return "{'license':{'-view':{},'-help':{}}}";
	}
	protected abstract String readLicense();
    private String showLicenseInformation() throws Exception{
		
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		LicenseData licenseData=null;
		String strValue=null;
		// Reads the License
		String compounedLicenseKey = readLicense();
		TableFormatter formatter=new TableFormatter(header, width,TableFormatter.OUTER_BORDER);
		String[] data=new String[width.length];
		
		if (compounedLicenseKey != null) {
			Map<String,LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(compounedLicenseKey);
			Iterator<String> licenseDataKeyIterator = licenseDataMap.keySet().iterator();
	        String key = null;
	        
			while (licenseDataKeyIterator.hasNext()) {
	        	key = licenseDataKeyIterator.next();
	        	
	        	licenseData = licenseDataMap.get(key);
	        	strValue=licenseData.getValue();
	        	
	        	if(key.contains(LicenseModuleConstants.SYSTEM_NV)){
	        		out.println();
	        		out.println("Module  : " + MODULE);
	        		out.println("Version : " + licenseData.getVersion());
	        		continue;
	        	}
	        	
				if((licenseData.getType().equalsIgnoreCase(LicenseTypeConstants.NODE))){
					String[] temp = strValue.split(":");
					strValue="IP=" + temp[0] + "Home Path="+ temp[4];
				}

				if("-1".equals(strValue)){
					strValue="Unlimited";
				}
				
				data[0] = licenseData.getDisplayName();
				data[1] = strValue;
				formatter.addRecord(data);
	        }
		}else{
			return "License Information not available.";
		}
		out.close();	
		return sb.toString() + formatter.getFormattedValues();
	}

	@Override
	public String getHelpMsg() {

		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<options>]");
		out.println();
		out.println(fillChar("where options include:", 30));		
		out.println("    " + fillChar("-v | -view", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Displays server instance license information.", 30));
		out.close();
		return stringWriter.toString();
	}

}


