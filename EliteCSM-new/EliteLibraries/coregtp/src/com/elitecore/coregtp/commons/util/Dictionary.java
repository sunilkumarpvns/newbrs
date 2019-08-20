/**
 * 
 */
package com.elitecore.coregtp.commons.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.coregtp.commons.elements.GTPPrimeDataRecordPacket;
import com.elitecore.coregtp.commons.elements.IpAddress;
import com.elitecore.coregtp.commons.elements.Octet;
import com.elitecore.coregtp.commons.elements.Sequence;
import com.elitecore.coregtp.commons.packet.ParseException;

/**
 * @author dhaval.jobanputra
 *
 */
public class Dictionary {

	public static final String MODULE = "GTP-DictionaryXMLParser" ;

	private static Dictionary dictionaryInstance;

	public static final String ATTRIBUTE_LIST = "attribute-list";
	public static final String ATTRIBUTE = "attribute";
	public static final String SUPPORTED_VALUES = "supported-values";

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String TYPE = "type" ;
	public static final String VALUE = "value";
	public static final String VENDORID = "vendor-id";
	public static final String VENDOR_NAME = "vendor-name";

	public static final String OCTET = "octet";
	public static final String IP_ADDRESS = "ip-address";
	public static final String SEQUENCE = "sequence";
	public static final String CDR = "cdr";

	Map <String, String> vendorMap;
	Map <Integer, BaseGTPPrimeElement> idObjectTypeMap;
	Map <Integer, Map<Integer,String>> supportedValueMap;
	private Dictionary(){
		vendorMap = new HashMap<String, String>();
		idObjectTypeMap = new HashMap<Integer, BaseGTPPrimeElement>();
		supportedValueMap = new HashMap<Integer, Map<Integer, String>>();

	}

	public String getVendorNameOrId(String value){
		return vendorMap.get(value);
	}
	public BaseGTPPrimeElement getElement(int value) throws CloneNotSupportedException, ParseException{
		BaseGTPPrimeElement element = idObjectTypeMap.get(value);
		if (element != null){
			return element.clone();
		}
		else {
			throw new ParseException("Element: " +  value +" not Found in Dictionary");
		}
	}
	public Map<Integer, String> getSupportedValueMap(int type){
		return supportedValueMap.get(type);
	}
	public static Dictionary getInstance(){
		if (dictionaryInstance==null){
			synchronized (Dictionary.class) {
				if (dictionaryInstance==null){
					dictionaryInstance = new Dictionary();
				}
			}
		}
		return dictionaryInstance;
	}

	public void readDictionary(File dictFile) throws Exception{

		if (!dictFile.exists()){
			System.out.println(MODULE + "- File not found");
		}
		else{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(dictFile);

			String vendorName="";
			String vendorId="";

			NodeList nodeList = document.getElementsByTagName(ATTRIBUTE_LIST).item(0).getChildNodes();

			Node vendorNode = document.getElementsByTagName(ATTRIBUTE_LIST).item(0);

			if (vendorNode.getNodeName().equals(ATTRIBUTE_LIST)){
				if (vendorNode.getAttributes().getNamedItem(VENDORID).getTextContent()!=null){
					vendorId = vendorNode.getAttributes().getNamedItem(VENDORID).getTextContent();
				}
				else {
					throw new Exception("Vendor-Id not defined for the dictionary.");
				}
				if (vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent()!=null){
					vendorName = vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent();
				}
				else {
					throw new Exception("Vendor-Id not defined for the dictionary.");
				}

				vendorMap.put(vendorId, vendorName);
				vendorMap.put(vendorName, vendorId);
			}

			for (int i=0 ; i<nodeList.getLength() ; i++){

				@SuppressWarnings("unused")
				String attrName=null;
				String attrId=null;
				String attrType=null;
				int iAttrID=0;
				BaseGTPPrimeElement gtpElement=null;

				Node node = nodeList.item(i);

				if (node.getNodeName().equalsIgnoreCase(ATTRIBUTE)){
					Map <Integer, String> keyValueMap = new HashMap<Integer, String>();
					boolean hasSupportedValues = false;
					if(node.getAttributes().getNamedItem(ID).getTextContent() != null){
						attrId = node.getAttributes().getNamedItem(ID).getTextContent();
						try {
							iAttrID = Integer.parseInt(attrId);
						} catch (NumberFormatException e) {
							throw new Exception("Attribute id "+attrId+" is not in proper format for " + vendorId + ":" + vendorName,e);
						}
					}
					if(node.getAttributes().getNamedItem(NAME).getTextContent() != null){
						attrName = node.getAttributes().getNamedItem(NAME).getTextContent().trim();
					}else {
						throw new Exception("Attribute name not specified for " + vendorId + ":" + vendorName + ":" + attrId);
					}
					if(node.getAttributes().getNamedItem(TYPE).getTextContent() != null){
						attrType = node.getAttributes().getNamedItem(TYPE).getTextContent().trim();
					}else {
						throw new Exception("Attribute type not specified for " + vendorId + ":" + vendorName + ":" + attrId);
					}

					if (attrType.equalsIgnoreCase(OCTET)){
						gtpElement = (Octet)(new Octet()).clone();
					}else if(attrType.equalsIgnoreCase(IP_ADDRESS)){
						gtpElement = (IpAddress)(new IpAddress()).clone();
					}else if (attrType.equalsIgnoreCase(SEQUENCE)){
						gtpElement = (Sequence)(new Sequence()).clone();
					}else if (attrType.equalsIgnoreCase(CDR)){
						gtpElement = new GTPPrimeDataRecordPacket().clone();
					}

					NodeList subNodeList = node.getChildNodes();

					for (int j=0 ; j<subNodeList.getLength() ; j++){
						String valueID=null;
						String valueName=null;
						int iValueID=0;
						Node suppNode = subNodeList.item(j);

						if (suppNode.getNodeName().equalsIgnoreCase(SUPPORTED_VALUES)){
							hasSupportedValues = true;
							NodeList valueList = suppNode.getChildNodes();

							for (int k=0 ; k<valueList.getLength();k++){
								Node valueNode = valueList.item(k);
								if (valueNode.getNodeName().equalsIgnoreCase(VALUE)){
									if (valueNode.getAttributes().getNamedItem(ID).getTextContent().trim()!=null){
										valueID = valueNode.getAttributes().getNamedItem(ID).getTextContent();
										try {
											iValueID = Integer.parseInt(valueID);
										} catch (NumberFormatException e) {
											throw new Exception("Value id "+valueID+" is not in proper format for ",e);
										}
									}
									else{
										throw new Exception("ID not defined for value");
									}
									if (valueNode.getAttributes().getNamedItem(NAME)!=null){
										valueName = valueNode.getAttributes().getNamedItem(NAME).getTextContent();
									}
									else{
										throw new Exception("Name not defined for value");
									}
									keyValueMap.put(iValueID, valueName);
								}
							}
						}
					}
					idObjectTypeMap.put(iAttrID, gtpElement);
					if (hasSupportedValues){
						supportedValueMap.put(iAttrID,keyValueMap);
					}
				}
			}
		}
	}
}