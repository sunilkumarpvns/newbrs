package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.license.base.License;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseModuleConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.util.SystemUtil;

public class LicenseDetailProvider extends DetailProvider{

	
	private static final String MODULE = "NetVertex";
	
	//--Fields to display output in Tabuler format--//
	private static final int DISP_NAME_WIDTH = 30;
	private static final int VALIDITY_WIDTH = 60;
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private int[] width = {DISP_NAME_WIDTH, VALIDITY_WIDTH};
	private String[] header = {"License Key","Value"};
	private String licenseKey;

	public LicenseDetailProvider(String licenseKey){

		detailProviderMap =new HashMap<String, DetailProvider>();
		this.licenseKey = licenseKey;
	}

	public  HashMap<String ,DetailProvider> getDetailProviderMap(){
		return detailProviderMap;
	}

	
	@Override
	public String execute(String [] parameters) {
		
		if(parameters.length >0 &&  "?".equals(parameters[0])){
			return getHelpMsg();
		}
		
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		LicenseData licenseData=null;
		String strValue=null;
		// Reads the License
		TableFormatter formatter=new TableFormatter(header, width,TableFormatter.OUTER_BORDER);
		String[] data=new String[width.length];
		
		if (licenseKey != null) {
			Map<String, LicenseData> licenseDataMap = new HashMap<String, LicenseData>();
			try {
				licenseDataMap = SystemUtil.getLicenseInformationMap(licenseKey);
			} catch (InvalidLicenseKeyException e) {
				LogManager.ignoreTrace(e);
				return "License Information not available.";
			}
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
				if(License.UNLIMITED_ACCESS.equals(strValue)){
					continue;
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
		return "  It will display license information.";
	}

	@Override
	public String getKey() {
		return "license";
	}
}
