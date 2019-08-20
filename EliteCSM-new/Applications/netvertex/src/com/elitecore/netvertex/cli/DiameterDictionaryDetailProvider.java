package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DiameterDictionaryDetailProvider extends DetailProvider {

	private static final String ATTRID			= "attrid";
	private static final String FILENAME		= "filename";
	private static final String DIAMETER		= "diameter";
	private static final String LIST			= "list";

	private String serverHome;
	private HashMap<String ,DetailProvider> detailProviderMap;

	public DiameterDictionaryDetailProvider(String serverHome){
		this.serverHome = serverHome;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters == null || parameters.length == 0){
			return "Provide valid arguments" + getHelpMsg();
		}


		AttributeData diameterAvpId=null;
		if(parameters.length > 1 && ATTRID.equalsIgnoreCase(parameters[0])){
			String attrValue= parameters[1];
			diameterAvpId=DiameterDictionary.getInstance().getAttributeId(attrValue);
			if (diameterAvpId != null){
				return diameterAvpId.toString();
			} else {
				return "No such Diameter AVP exists!!";
			}
		}else if(parameters.length > 1 && FILENAME.equalsIgnoreCase(parameters[0])){
			String attrValue= parameters[1];
			File diaDictionaryFolder = new File(serverHome + File.separator + "dictionary" + File.separator + "diameter");
			return getFileContent(diaDictionaryFolder,attrValue);
		}else if(LIST.equalsIgnoreCase(parameters[0])){
			List<String> dictionryNameList = DiameterDictionary.getInstance().getDictionaryNames();
			return getDictionaryList(dictionryNameList);
		}

		return "Provide valid arguments" + getHelpMsg();



	}

	private String getDictionaryList(List<String> dictionryNameList){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("=====================================");
		out.println("        List of Files                ");
		for(String dictionary : dictionryNameList){
			out.println(dictionary);
		}
		out.println("=====================================");
		out.close();
		return stringWriter.toString();
	}

	private String getFileContent(File fileSource,String fileName) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		if (fileSource.isDirectory()) {
			File [] fileList = fileSource.listFiles();
			for (int i=0;i<fileList.length;i++){
				out.print(getFileContent(fileList[i],fileName));
			}
		} else if (fileSource.getName().equalsIgnoreCase(fileName)) {

			try(BufferedReader br =
						new BufferedReader(new FileReader(fileName))) {
				String line;

				while((line = br.readLine()) != null) {
					out.println(line);
				}
			}catch(IOException e){
				Logger.getLogger("Error in fileOperation" + e.getMessage());
				com.elitecore.commons.logging.LogManager.ignoreTrace(e);
			}
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : show dictionary diameter [<options>]");
		out.println("--------------------------------Possible Options:--------------------------------");
		out.println(" " + ATTRID + " <Attribute-id>    : Display the attribute details");
		out.println(" " + LIST +"                              : Display the list of Diameter Dictionaries ");
		out.println(" " + FILENAME + "       <filename>        : Display Dictionary content");
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "'"+DIAMETER+"':{'"+ FILENAME +"':{},'"+ ATTRID +"':{},'"+LIST+"':{}}";
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
