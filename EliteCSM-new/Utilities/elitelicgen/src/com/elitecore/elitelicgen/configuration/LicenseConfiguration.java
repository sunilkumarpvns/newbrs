
package com.elitecore.elitelicgen.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.license.base.LicenseData;
import com.elitecore.license.configuration.LicenseConfigurationManager;


public class LicenseConfiguration  {

	public static final String MODULE 						 = "LICENSE CONFIGURATION";
	public static final String SUPPORTED_VENDOR_LIST		 = "SUPPORTED_VENDOR_LIST";
	public static final String SYSTEM_LICENSE_LIST			 = "SYSTEM_LICENSE_LIST"; 
	//public static final String RADIUS_SERVICE_LIST			 = "RADIUS_SERVICE_LIST";
	public static final String AAA_SERVICE_LIST			 	 = "AAA_SERVICE_LIST";
	public static final String NV_SERVICE_LIST				 = "NV_SERVICE_LIST";
	//public static final String DIAMETER_SERVICE_LIST		 = "DIAMETER_SERVICE_LIST";
	//public static final String RESOURCE_MANAGER_SERVICE_LIST = "RESOURCE_MANAGER_SERVICE_LIST";
	public static final String NAS_TYPE_LIST				 = "NAS_TYPE_LIST";

	public LicenseConfiguration() {
	}

	public static Map<String,Object> readConfiguration() {

		Map<String,Object> localParameterMap = new HashMap<String,Object>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();

			Document document = documentBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("/com/elitecore/elitelicgen/configuration/license-config.xml"));
			NodeList licenseList = document.getElementsByTagName("license");

			for(int i=0;i<licenseList.getLength();i++) {
				NodeList nodeList = licenseList.item(i).getChildNodes();
				for(int j=0;j<nodeList.getLength();j++) {
					Node subNode = nodeList.item(j);

					if(subNode.getNodeName().equalsIgnoreCase("supported-vendor-list")) {
						SortedMap <Integer,String>vendorMap = new TreeMap<Integer,String>();
						NodeList vendorLists = subNode.getChildNodes();

						for(int k=0;k<vendorLists.getLength();k++) {
							Node vendorNode = vendorLists.item(k);
							if(vendorNode.getNodeName().equalsIgnoreCase("vendor"))
							{
								NamedNodeMap namedNodeMap  = vendorNode.getAttributes();

								String strVendorId   = namedNodeMap.getNamedItem("id").getNodeValue();
								String strVendorName = namedNodeMap.getNamedItem("name").getNodeValue();

								vendorMap.put(Integer.parseInt(strVendorId),strVendorName.toUpperCase());	
							}

							localParameterMap.put(SUPPORTED_VENDOR_LIST,vendorMap);
						}
					}else if(subNode.getNodeName().equalsIgnoreCase("nas-type-list")) {
						List <String>NasTypeList = new ArrayList<String>();
						NodeList nasTypeLists = subNode.getChildNodes();

						for(int k=0;k<nasTypeLists.getLength();k++) {
							Node nasTypeNode = nasTypeLists.item(k);
							if(nasTypeNode.getNodeName().equalsIgnoreCase("nas-type"))
							{
								NasTypeList.add(nasTypeNode.getTextContent().trim());	
							}
						}
						localParameterMap.put(NAS_TYPE_LIST,NasTypeList);
					}else if(subNode.getNodeName().equalsIgnoreCase("system-license")) {
						NodeList systemLicneseLists = subNode.getChildNodes();
						List <LicenseData>systemLicenseList = new ArrayList<LicenseData>();

						for(int k=0;k<systemLicneseLists.getLength();k++) {
							Node systemLicenseNode = systemLicneseLists.item(k);

							if(systemLicenseNode.getNodeName().equalsIgnoreCase("system"))
							{
								NodeList systemSubLicneseLists = systemLicenseNode.getChildNodes();
								LicenseData licData =readLicenseDetails(systemSubLicneseLists); 
								systemLicenseList.add(licData);
							}

						}
						localParameterMap.put(SYSTEM_LICENSE_LIST,systemLicenseList);
					}else if(subNode.getNodeName().equalsIgnoreCase("module-list")) {
						NodeList moduleLicneseLists = subNode.getChildNodes();

						for(int k=0; k<moduleLicneseLists.getLength();k++) {
							Node moduleLicenseNode = moduleLicneseLists.item(k);

							if(moduleLicenseNode.getNodeName().equalsIgnoreCase("aaa-server")){
								List <LicenseData>aaaLicenseList = readServiceDetails(moduleLicenseNode);
								localParameterMap.put(AAA_SERVICE_LIST,aaaLicenseList);
							}else if(moduleLicenseNode.getNodeName().equalsIgnoreCase("netvertex-server")){
								List <LicenseData>nvLicenseList = readServiceDetails(moduleLicenseNode);
								localParameterMap.put(NV_SERVICE_LIST,nvLicenseList);
							}							
							/*else if(moduleLicenseNode.getNodeName().equalsIgnoreCase("rm")){
								List <LicenseData>rmLicenseList=readServiceDetails(moduleLicenseNode);

								localParameterMap.put(RESOURCE_MANAGER_SERVICE_LIST,rmLicenseList);
							}else if(moduleLicenseNode.getNodeName().equalsIgnoreCase("diameter")){
								List <LicenseData>diameterLicenseList = readServiceDetails(moduleLicenseNode);//new ArrayList<LicenseData>();
								localParameterMap.put(DIAMETER_SERVICE_LIST,diameterLicenseList);
							}*/

						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		LicenseConfigurationManager.getInstance().setLicenseData(localParameterMap);
		return localParameterMap;
	}

	private static LicenseData readLicenseDetails(NodeList licneseDetailsLists){

		LicenseData licData = new LicenseData();
		for(int l=0;l<licneseDetailsLists.getLength();l++){
			Node licenseNode = licneseDetailsLists.item(l);
			if(licenseNode.getNodeName().equalsIgnoreCase("name")){
				licData.setName(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("display-name")){
				licData.setDisplayName(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("description")){
				licData.setDescription(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("module")){
				licData.setModule(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("license-type")){
				licData.setType(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("default-value")){
				licData.setValue(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("value-type")){
				licData.setValueType(licenseNode.getTextContent().trim());
			}else if(licenseNode.getNodeName().equalsIgnoreCase("operator")){
				licData.setOperator(getOperator(licenseNode.getTextContent().trim()));
			}else if(licenseNode.getNodeName().equalsIgnoreCase("selected")){
				if(licenseNode.getTextContent().trim().length() > 0)
					licData.setState(licenseNode.getTextContent().trim());
				else 
					licData.setState("true");
			}
		}
		return licData;
	}

	private static List<LicenseData> readServiceDetails(Node moduleLicenseNode){
		List <LicenseData>licenseList=new ArrayList<LicenseData>();	
		NodeList nodeList = moduleLicenseNode.getChildNodes();
		for(int n=0;n<nodeList.getLength();n++){
			Node serviceListNode = nodeList.item(n);

			if(serviceListNode.getNodeName().equalsIgnoreCase("service-list")){
				NodeList serviceLicneseNodesList = serviceListNode.getChildNodes();

				for(int l=0;l<serviceLicneseNodesList.getLength();l++) {
					Node servicelicenseNode = serviceLicneseNodesList.item(l);

					if(servicelicenseNode.getNodeName().equalsIgnoreCase("service"))
					{
						NodeList licneseDetailsLists = servicelicenseNode.getChildNodes();
						LicenseData licData =readLicenseDetails(licneseDetailsLists); 
						licenseList.add(licData);
					}
				}
			}
		}
		return licenseList;
	} 
	
	private static String getOperator(String operator){

		if(operator.equalsIgnoreCase("1"))
    		return "<";
    	else if(operator.equalsIgnoreCase("2"))
    		return ">";
    	if(operator.equalsIgnoreCase("3"))
    		return "<=";
    	if(operator.equalsIgnoreCase("4"))
    		return ">=";
    	if(operator.equalsIgnoreCase("5"))
    		return "=";
    	if(operator.equalsIgnoreCase("6"))
    		return "!=";
    	if(operator.equalsIgnoreCase("7"))
    		return "contains";
    	else
    		return "invalid";
	}

	/*	public static void main(String[] args) {
		new LicenseConfiguration().readConfiguration();
	}*/

}
