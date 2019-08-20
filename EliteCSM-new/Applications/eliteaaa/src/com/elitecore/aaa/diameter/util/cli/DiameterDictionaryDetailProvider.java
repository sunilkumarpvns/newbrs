package com.elitecore.aaa.diameter.util.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DiameterDictionaryDetailProvider extends DetailProvider {

	private static final String DICTIONARYATTR	= "attrid";
	private static final String FILENAME		= "name";
	private static final String DICTIONARY		= "dictionary";
	private static final String LIST			= "list";

	private String serverHome;
	private HashMap<String ,DetailProvider> detailProviderMap;

	public DiameterDictionaryDetailProvider(String serverHome){
		this.serverHome = serverHome;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters != null && parameters.length > 0){

			AttributeData diameterAvpId=null;
			if(DICTIONARYATTR.equalsIgnoreCase(parameters[0]) && parameters.length > 1){
				String attrValue= parameters[1];
				diameterAvpId=DiameterDictionary.getInstance().getAttributeId(attrValue);
				if (diameterAvpId != null){
					return diameterAvpId.toString();
				} else {
					return "No such Diameter AVP exists!!";
				}
			}else if(FILENAME.equalsIgnoreCase(parameters[0]) && parameters.length > 1){
				String attrValue= parameters[1];
				File diaDictionaryFolder = new File(serverHome + File.separator + "dictionary" + File.separator + "diameter");
				return getFileContent(diaDictionaryFolder,attrValue);
			}else if(LIST.equalsIgnoreCase(parameters[0])){
				List<String> dictionryNameList = DiameterDictionary.getInstance().getDictionaryNames();
				return getDictionaryList(dictionryNameList);
			} else {
				StringBuilder responseBuilder = new StringBuilder();
				responseBuilder.append("Provide the valid arguments\n");
				return responseBuilder.append(getHelpMsg()).toString();
			}
		} else {
			StringBuilder responseBuilder = new StringBuilder();
			responseBuilder.append("Provide the valid arguments\n");
			return responseBuilder.append(getHelpMsg()).toString();
		}

	}

	private String getDictionaryList(List<String> dictionryNameList){

		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n=====================================\n");
		responseBuilder.append("\n        List of Files                \n");
		for(String dictionary : dictionryNameList){
			responseBuilder.append(dictionary+"\n");
		}
		responseBuilder.append(	 "=====================================");
		return responseBuilder.toString();
	}

	private String getFileContent(File fileSource,String fileName) {
		StringBuilder builder=new StringBuilder();
		if (fileSource.isDirectory()) {
			File [] fileList = fileSource.listFiles();
			for (int i=0;i<fileList.length;i++){
				builder.append(getFileContent(fileList[i],fileName));
			}
		} else {
			if (fileSource.getName().equalsIgnoreCase(fileName)) {

				try(BufferedReader br =
							new BufferedReader(new FileReader(fileName))) {
					String line;

					while((line = br.readLine()) != null) {
						builder.append(line+"\n");

					}
				}catch(IOException e){
					builder.append("Error in fileOperation ");
				}
			}
		}
		return builder.toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : show diameter dictionary [<options>]\n");
		responseBuilder.append("--------------------------------Possible Options:--------------------------------\n");
		responseBuilder.append(DICTIONARYATTR + " <Attribute-id>      	   : Display the attribute details\n");
		responseBuilder.append(LIST +"                       	   : Display the list of Diameter Dictionaries \n");
		responseBuilder.append(FILENAME + " <filename>            	   : Display Dictionary content\n");
		return responseBuilder.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "'"+DICTIONARY+"':{'"+ FILENAME +"':{},'"+ DICTIONARYATTR +"':{},'"+LIST+"':{}}";
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
