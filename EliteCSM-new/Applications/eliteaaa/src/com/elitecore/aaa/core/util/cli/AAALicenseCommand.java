package com.elitecore.aaa.core.util.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.JMXException;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;
public abstract class AAALicenseCommand extends EliteBaseCommand {
	
	public static final String LICENSE_INFO_ACRONYM = "-v";

	public static final String DOWNLOAD_PUBLIC_KEY_ACRONYM = "-pk";

	public static final String UPLOAD_LICENSE_ACRONYM = "-u";

	public static final String LICENSE_INFO = "-view";

	public static final String DOWNLOAD_PUBLIC_KEY = "-public-key";

	public static final String UPLOAD_LICENSE = "-upload";

	private static final String LICENSE_KEY_FILE_NAME = "local_node.lic";

	@Override
	public String execute(String parameter) {
		String responseMessage = "";
		String filePath = null;
		String cmdOption = "";
		
		
		try {
			
			if (parameter != null) {

				String cmdParameters[] = parseArgumentString(parameter);

				if (cmdParameters != null && cmdParameters.length >= 2) {
					cmdOption = cmdParameters[0];
					filePath = cmdParameters[1];
				} else
					cmdOption = parameter.trim();

				if (cmdOption.equalsIgnoreCase(AAALicenseCommand.DOWNLOAD_PUBLIC_KEY) 
						|| cmdOption.equalsIgnoreCase(AAALicenseCommand.DOWNLOAD_PUBLIC_KEY_ACRONYM)) {

					responseMessage = downloadPublicKey(filePath, getPublicKeyFileName());

				} else if (cmdOption.equalsIgnoreCase(AAALicenseCommand.UPLOAD_LICENSE)
						|| cmdOption.equalsIgnoreCase(AAALicenseCommand.UPLOAD_LICENSE_ACRONYM)) {

					responseMessage = uploadLicense(filePath, AAALicenseCommand.LICENSE_KEY_FILE_NAME);

				} else if (cmdOption.equalsIgnoreCase(AAALicenseCommand.LICENSE_INFO)
						|| cmdOption.equalsIgnoreCase(AAALicenseCommand.LICENSE_INFO_ACRONYM)) {

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
		return "{'license':{'-view':{},'-public-key':{},'-upload':{},'-help':{}}}";
	}
	protected abstract String readLicense();
    private String showLicenseInformation() throws JMXException, Exception{
		
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		LicenseData licenseData=null;
		String strValue=null;
		// Reads the License
		String compounedLicenseKey = (String) readLicense( );
		
		if (compounedLicenseKey != null) {
			Map<String,LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(compounedLicenseKey);
			Iterator<String> licenseDataKeyIterator = licenseDataMap.keySet().iterator();
	        String key = null;
	        
	        out.println(fillChar("", 100, '-'));
			out.println(" " + fillChar("Display Name", 25) + "| "
							+ fillChar("Version" , 10) + "| "
							+ fillChar("Validity", 50)); 
			out.println(fillChar("", 100,'-'));
			
			while (licenseDataKeyIterator.hasNext()) {
	        	key = licenseDataKeyIterator.next();
	        	licenseData = licenseDataMap.get(key);
	        	strValue=licenseData.getValue().toString();
	        	
				if((licenseData.getType().equalsIgnoreCase(LicenseTypeConstants.NODE))){
					int index = strValue.indexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR+LicenseConstants.DEFAULT_ADDITIONAL_KEY);
					String tempString = strValue.substring(0, index);
					tempString = tempString.substring(0, tempString.lastIndexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR));
					tempString = tempString.substring(0, tempString.lastIndexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR));
					String ipaddress = tempString;
					index = strValue.lastIndexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR);
					String home = strValue.substring(index, strValue.length());					
					strValue="IP=" + ipaddress + " Home Path="+ home ;
				}	
				if("-1".equals(strValue)){
					strValue="Unlimited";
					continue;
				}
				
				if((LicenseTypeConstants.SUPPORTED_VENDORS.equalsIgnoreCase(licenseData.getType()) || LicenseTypeConstants.VENDOR_TYPE.equalsIgnoreCase(licenseData.getType())) && !"Unlimited".equals(strValue)){ 
					out.print(" "	+ fillChar(licenseData.getDisplayName(),25) +"| "+ fillChar(licenseData.getVersion(), 10) + "| ");
					
					StringReader readerValue = new StringReader(strValue);
					char[] line = new char[60];
					int charRead=0; int cntr=0;
					while((charRead=readerValue.read(line))!=-1){
						if(cntr!=0)
							out.print(" " +fillChar(" ",25) + "  " + fillChar(" ",10) + "| ");
						
							out.print((new String(line,0,charRead))+ "\n");
							cntr++;
					}
				}else{
					out.println(" "	+ fillChar(licenseData.getDisplayName(),25) +"| "
							+ fillChar(licenseData.getVersion(), 10) + "| " 
							+ fillChar(strValue,60) );
				}
	        }
			out.println(fillChar("", 100, '-'));
		}else{
			out.print("License Information not available.");
		}
		out.close();	
		return sb.toString();
	}	
    
    public String readLicensePublicKey( ) throws Exception {
        ElitePublickeyGenerator elitePublickeyGen = new ElitePublickeyGenerator();    	
        return elitePublickeyGen.generatePublicKey(getServerHomeLocation(),LicenseConstants.DEFAULT_ADDITIONAL_KEY, getInstanceId(), getInstanceName());
    }
    
    public abstract String getServerHomeLocation();
    public abstract String getPublicKeyFileName();
    public abstract String getModuleName();
    protected abstract String getInstanceName();
    protected abstract String getInstanceId();
    
    private String downloadPublicKey(String downloadFilePath, String downloadFileName) throws Exception {
		String response = null;
		
		if (downloadFilePath == null) {
			downloadFilePath = ".";
		}
		
		String publicKeyString = (String) readLicensePublicKey( );

		if (publicKeyString != null) {
			File licenseFile = new File(downloadFilePath + File.separator + downloadFileName);
			
			if (!licenseFile.exists())
				licenseFile.createNewFile();
			else{
				licenseFile.delete();
				licenseFile.createNewFile();
			}
			
			FileOutputStream fout = new FileOutputStream(licenseFile);
			try{
				fout.write(publicKeyString.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				fout.write(publicKeyString.getBytes());
			}
			fout.close();

			response = "Public Key data copied to " + licenseFile.getCanonicalPath();
		} else {
			response = "Problem generating Public Key...";
		}
		return response;
	}
    
    private String uploadLicense(String uploadFilePath, String uploadFileName) throws Exception {
		String response = null; 
			
		if (uploadFilePath == null) {
			uploadFilePath = ".";
		}

		File licenseKeyFile = new File(uploadFilePath + File.separator + uploadFileName);
		
		if (!licenseKeyFile.exists()){
			response = "License Key file not found at location " + licenseKeyFile.getCanonicalPath();
		}else {
			FileInputStream fin = new FileInputStream(licenseKeyFile);
			
			byte[] licenseKeyData = new byte[(int) licenseKeyFile.length()];
			fin.read(licenseKeyData);
			fin.close();
		
			String fileData = null;
			try{
				fileData = new String(licenseKeyData,CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				fileData = new String(licenseKeyData);
			}
			String fileName = licenseKeyFile.getName();
	
			Map<String, String> argsMap = new LinkedHashMap<String, String>();
	
			argsMap.put("FILEDATA", fileData);
			argsMap.put("FILENAME", fileName);
	
			saveLicense(fileData ,fileName );
			response = "License uploaded successfully. Please restart the server for new license to be effective.";
		}
		return response;
	}
    
    public void saveLicense( String fileData ,String fileName ) throws Exception {
    	try{
    		saveFile(fileName, fileData.getBytes(CommonConstants.UTF8));
    	}catch(UnsupportedEncodingException e){
    		saveFile(fileName, fileData.getBytes());
    	}
    }
    
    public void saveFile( String fileName ,byte[] doc ) throws Exception {

    	String destinationDirectory = getDestinationPath(null);
    	LogManager.getLogger().info(getModuleName(), "SERVER HOME : " + getServerHomeLocation());
    	if (getServerHomeLocation() == null || doc == null || fileName == null) {
    		LogManager.getLogger().error(getModuleName(), "Required Arguments found null");
    		throw new Exception("Required Argument found null");
    	}
    	FileOutputStream fs = null;
    	try {
    		LogManager.getLogger().info(getModuleName(), "File Name : " + fileName);
    		File srcDir = new File(getServerHomeLocation() + File.separator + destinationDirectory);
    		File srcFile = new File(getServerHomeLocation() + File.separator + destinationDirectory + File.separator + fileName);
    		LogManager.getLogger().info(getModuleName(), "File Absolute path : " + srcFile.getAbsolutePath());

    		if (!srcDir.exists())
    			srcDir.mkdir();

    		if (srcFile.exists())
    			srcFile.delete();

    		srcFile.createNewFile();
    		fs = new FileOutputStream(srcFile);
    		fs.write(doc);
    	}
    	catch (IOException exception) {
    		LogManager.getLogger().error(getModuleName(), "Error in File Operation");
    		LogManager.getLogger().trace(getModuleName(), exception);
    		throw new Exception("Unable Perform File operation due to Access Rights or already exists read-only file.", exception);
    	}	
    	finally {
    		try {
    			if (fs != null)
    				fs.close();
    		}
    		catch (Exception e) {
    			fs = null;
    		}

    	}
    }
   
    public String getDestinationPath( String groupName ) throws Exception {
        return LicenseConstants.LICENSE_DIRECTORY;
    }
    
	public String getHelpMsg() {

		/*StringBuilder sb = new StringBuilder();

		String paramName[] = { LICENSE_INFO, DOWNLOAD_PUBLIC_KEY, UPLOAD_LICENSE };
		String paramDesc[] = { 	"Displays server instance license information.", 
								"Downloads the public key.",
								"Uploads the license for server instance." };

		sb.append("Usage : license <option> [path] \nPossible options");

		for (int i = 0; i < paramDesc.length; i++) {
			sb.append("\n    " + fillChar(paramName[i], 12) + paramDesc[i]);
		}

		return sb.toString();*/
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<options>]");
		out.println();
		out.println(fillChar("where options include:", 30));		
		out.println("    " + fillChar("-v | -view", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Displays server instance license information.", 30));
		out.println("    " + fillChar("-pk | -public-key", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Downloads the public key.", 30));
		out.println("    " + fillChar("-u | -upload", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Uploads the license for server instance.", 30));
		out.close();
		return stringWriter.toString();
	}

}


