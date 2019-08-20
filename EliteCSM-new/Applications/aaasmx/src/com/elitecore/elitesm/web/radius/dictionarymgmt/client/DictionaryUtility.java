package com.elitecore.elitesm.web.radius.dictionarymgmt.client;



public class DictionaryUtility {
	
	/*
	public static String buildXML(DictionaryItem dictionaryItem){
		
		DictionaryData dictionaryData=dictionaryItem.getDictionaryData();
		String xmlString="";
		//Hashtable htab_strAttributeName_strPredefinedValues = null;
		StringWriter writer = new StringWriter();
		//StringBuffer buffer= new StringBuffer();
		
		PrintWriter out = new PrintWriter(writer);
		Enumeration enumeration = null;
		Collection colDictionaryParameterDetailData = null;
		Iterator itLstDicParamList = null;
		AttributeData dictionaryParameterDetailData = null;
		Hashtable htab_strDataTypeId_strName = new Hashtable();
		String strAttributeName = null;
		String strPredefinedValues = null;
		String strFileData = null;
		StringTokenizer stComma = null;
		StringTokenizer stColon = null;

		try {
			//htab_strAttributeName_strPredefinedValues = new Hashtable();
	
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("");
			out.println("<attribute-list vendorid=\"" + dictionaryData.getVendorId() + "\" vendor-name=\"" + dictionaryData.getName() + "\">");
			//colDictionaryParameterDetailData = dictionaryData.getDictionaryParameterDetail();
			colDictionaryParameterDetailData=dictionaryData.getAttributeList();
			if (colDictionaryParameterDetailData != null && colDictionaryParameterDetailData.isEmpty() == false) {


				List lstDicParamList = Arrays.asList(colDictionaryParameterDetailData.toArray()); 
				//Collections.sort(lstDicParamList);


				itLstDicParamList = lstDicParamList.iterator();
				while (itLstDicParamList.hasNext()) {
					dictionaryParameterDetailData = (AttributeData) itLstDicParamList.next();
					if(dictionaryParameterDetailData.getParentDetailId() == null){

						boolean hasChildAttribue = false;    
						if(dictionaryParameterDetailData.getChildAttributeList()!= null && !dictionaryParameterDetailData.getChildAttributeList().isEmpty())
							hasChildAttribue =true;

						String endTag = "\">";
						Collection<AttributeData> nestedAttributeList = dictionaryParameterDetailData.getChildAttributeList();
						if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
							String hasTag = dictionaryParameterDetailData.getHasTag();

							if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
								strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
								+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
								+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
								+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag())
								+ "\">";

							}else{
								strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
								+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
								+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
								+ "\">";

							}
							out.println(strFileData);
							strAttributeName = (String) dictionaryParameterDetailData.getName();
							strPredefinedValues = (String) dictionaryParameterDetailData.getPredefinedValues();
							stComma = new StringTokenizer(strPredefinedValues, ",");
							out.println("                 <supported-values>");
							String valueId = null;
							String valueName = null;
							stComma = new StringTokenizer(strPredefinedValues, ",");
							while (stComma.hasMoreTokens()) {
								stColon = new StringTokenizer(stComma.nextToken(), ":");
								valueName = stColon.nextToken();
								valueId = stColon.nextToken();
								strFileData = "                          <value id=\"" + valueId + "\" name=\"" + valueName + "\"/>";
								out.println(strFileData);
							}
							out.println("                 </supported-values>");
							if(!hasChildAttribue){
								out.println("        </attribute>");
							}
						} else {
							strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\"/>";
							String hasTag = dictionaryParameterDetailData.getHasTag();
							if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
								strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
								+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
								+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
								+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag());
								//+ "\"/>";
								if(hasChildAttribue){
									strFileData +=endTag;

								}else{
									strFileData +="\"/>";
								}
							}else{
								strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
								+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
								+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()); 
								//+ "\"/>";
								if(hasChildAttribue){
									strFileData +=endTag;

								}else{
									strFileData +="\"/>";
								}
							}

							out.println(strFileData);
						}

						// for child attribute...
						if(hasChildAttribue){

							for (Iterator iterator = nestedAttributeList.iterator(); iterator.hasNext();) {

								AttributeData dictionaryParameterDetailData2 = (AttributeData) iterator.next();
								recursivePrintNestedAttributeAsXml(out, dictionaryParameterDetailData2,dictionaryParameterDetailData.getDictionaryParameterDetailId());
							}
							out.println("        </attribute>");	

						}



					} 
				}

			}
			out.println("</attribute-list>");
			writer.close();
			xmlString=writer.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return xmlString;

	}
	*/
/*
	private static void recursivePrintNestedAttributeAsXml(PrintWriter out,AttributeData dictionaryParameterDetailData,long parentId) throws IOException {

		long parentDetailId = dictionaryParameterDetailData.getParentDetailId();
		if(parentId == parentDetailId){

			String strAttributeName = null;
			String strPredefinedValues = null;
			String strFileData = null;
			StringTokenizer stComma = null;
			StringTokenizer stColon = null;

			boolean hasChildAttribue = false;    
			if(dictionaryParameterDetailData.getChildAttributeList()!= null && !dictionaryParameterDetailData.getChildAttributeList().isEmpty())
				hasChildAttribue =true;

			String endTag = "\">";
			Collection<AttributeData> nestedAttributeList = dictionaryParameterDetailData.getChildAttributeList();
			if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
				String hasTag = dictionaryParameterDetailData.getHasTag();

				if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
					strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
					+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
					+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
					+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag())
					+ "\">";

				}else{
					strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
					+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
					+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
					+ "\">";

				}
				out.println(strFileData);
				strAttributeName = (String) dictionaryParameterDetailData.getName();
				strPredefinedValues = (String) dictionaryParameterDetailData.getPredefinedValues();
				stComma = new StringTokenizer(strPredefinedValues, ",");
				out.println("                                  <supported-values>");
				String valueId = null;
				String valueName = null;
				stComma = new StringTokenizer(strPredefinedValues, ",");
				while (stComma.hasMoreTokens()) {
					stColon = new StringTokenizer(stComma.nextToken(), ":");
					valueName = stColon.nextToken();
					valueId = stColon.nextToken();
					strFileData = "                                                    <value id=\"" + valueId + "\" name=\"" + valueName + "\"/>";
					out.println(strFileData);
				}
				out.println("                                  </supported-values>");
				if(!hasChildAttribue){
					out.println("                </attribute>");
				}
			} else {
				strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\"/>";
				String hasTag = dictionaryParameterDetailData.getHasTag();
				if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
					strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
					+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
					+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
					+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag());
					//+ "\"/>";
					if(hasChildAttribue){
						strFileData +=endTag;

					}else{
						strFileData +="\"/>";
					}
				}else{
					strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
					+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
					+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()); 
					//+ "\"/>";
					if(hasChildAttribue){
						strFileData +=endTag;

					}else{
						strFileData +="\"/>";
					}
				}

				out.println(strFileData);
			}

			// for child attribute...
			if(hasChildAttribue){

				for (Iterator iterator = nestedAttributeList.iterator(); iterator.hasNext();) {

					AttributeData dictionaryParameterDetailData2 = (AttributeData) iterator.next();
					recursivePrintNestedAttributeAsXml(out, dictionaryParameterDetailData2,dictionaryParameterDetailData.getDictionaryParameterDetailId());
				}
				out.println("                </attribute>");	

			}



		}   


	}
	
*/	
/*	
	public static void buildDictionaryItem(String xmlString){
	        
	        
	        DocumentBuilderFactory factory = null;
	        DocumentBuilder documentBuilder = null;
	        Document docDictionaryXMLParse = null;
	        NodeList nodeList = null;
	        
	        Map<Object, Object> tempVendorDetailMap = new HashMap<Object, Object>();
	        Map<Object, Object> attributDetailMap = new LinkedHashMap<Object,Object>();
	        Map<String, String> attributeMap = null;
	        
	        try {
	            
	            factory = DocumentBuilderFactory.newInstance();
	            factory.setIgnoringComments(true);
	            factory.setIgnoringElementContentWhitespace(true);
	            factory.setValidating(false);
	            documentBuilder = factory.newDocumentBuilder();
	            
	            InputSource inputSource = new InputSource(inStream);
	            docDictionaryXMLParse = documentBuilder.parse(inputSource);
	            
	            nodeList = docDictionaryXMLParse.getElementsByTagName(ATTRIBUTE_LIST).item(0).getChildNodes();
	            
	            Node vendorNode = docDictionaryXMLParse.getElementsByTagName(ATTRIBUTE_LIST).item(0);
	            Element vendorElement = null;
	            if (vendorNode instanceof Element) {
	                vendorElement = (Element) vendorNode;
	            }
	            String strVendorID = null;
	            String strVendorName = null;
	            String strAVPairSeparator = " ";
	               
	            if (vendorElement != null && vendorElement.getNodeName().equals(ATTRIBUTE_LIST)) {
	                
	                if (vendorElement.hasAttribute(VENDORID) && vendorElement.getAttribute(VENDORID) != null) {
	                    strVendorID = vendorElement.getAttribute(VENDORID).trim();
	                    
	                    try {
	                        Integer.parseInt(strVendorID);
	                        tempVendorDetailMap.put(VENDORID,strVendorID);
	                    }
	                    catch (Exception e) {
	                        throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id",e);
	                    }
	                } else {
	                    throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
	                }
	                
	                if (vendorElement.hasAttribute(VENDOR_NAME) && vendorElement.getAttribute(VENDOR_NAME) != null) {
	                    strVendorName = vendorElement.getAttribute(VENDOR_NAME).trim();
	                    tempVendorDetailMap.put(VENDOR_NAME, strVendorName);
	                } else {
	                    throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
	                }
	                
	                if(vendorNode.getAttributes().getNamedItem(AVPAIR_SEPARATOR) != null){
						if(vendorNode.getAttributes().getNamedItem(AVPAIR_SEPARATOR).getTextContent() != null){
							strAVPairSeparator = vendorNode.getAttributes().getNamedItem(AVPAIR_SEPARATOR).getTextContent();
						}else {
							if(Logger.isLogLevel(LogLevel.INFO.LEVEL))
								Logger.logInfo(MODULE, "No AVPair separator specified for this dictionary, considering default \b (space)");
						}
					}else {
						if(Logger.isLogLevel(LogLevel.INFO.LEVEL))
							Logger.logInfo(MODULE, "No AVPair separator specified for this dictionary, considering default \b (space)");
					}
	                tempVendorDetailMap.put(AVPAIR_SEPARATOR, strAVPairSeparator);
	                
	                if(Logger.isLogLevel(LogLevel.DEBUG.LEVEL))
	                Logger.logDebug(MODULE, "Dictionary parsing dictioary for " + strVendorID + ":" + strVendorName + " started.");
	                tempVendorDetailMap.put(ATTRIBUTE, attributDetailMap);
	            }
	            
	            readGroupedAttributeForServerManager(nodeList,attributDetailMap,strVendorID,strVendorName);
	            
	            return tempVendorDetailMap;
	        }
	        catch (Exception exp) {
	            throw new DictionaryParseException("Unexpected error while parsing dictionary ",exp);
	        }
	    }

	private static void readGroupedAttributeForServerManager(NodeList nodeList,Map<Object, Object> attributDetailMap, String strVendorID,String strVendorName) {
		//private  Map<String, Object> readGroupedAttributeForServerManager(NodeList nodeList, Map<Object, Object> attributeDetailMap, String strVendorID,String strVendorName) throws DictionaryParseException{
			 Map<String, Object> nestedAttributeMap = new LinkedHashMap<String,Object>();
			 Map<String, Object> attributeMap=null;
			 for ( int i = 0; i < nodeList.getLength(); i++ ) {
	             
	             Node node = nodeList.item(i);
	             String StrAttrID = null;
	             String strAttrName = null;
	             String strAttrDataType = null;
	             String strAttrTagDataType = "no";
	             String strAttrIgnoreCase = "no";
	             String strAttrIsAvpair = "no";
	             String strAttrEncryptStandard = null;
	             
	             if (node.getNodeName().equals(ATTRIBUTE)) {
	                 Element attributeElement = (Element) node;
	                 
	                 if (attributeElement.hasAttribute(ID) && attributeElement.getAttribute(ID) != null) {
	                     
	                     StrAttrID = attributeElement.getAttribute(ID).trim();
	                     try {
	                         Integer.parseInt(StrAttrID);
	                         attributeMap = new LinkedHashMap<String,Object>();
	                         attributeDetailMap.put(StrAttrID, attributeMap);
	                         
	                         attributeMap.put(ID,StrAttrID);
	                         
	                         // System.out.print("id :" + iAttrID + " ");
	                     }
	                     catch (NumberFormatException e) {
	                     	throw new DictionaryParseException("Attribute id " + StrAttrID + " is not in proper format for " + strVendorID + ":" + strVendorName,e);
	                     }
	                 }
	                 
	                 if (attributeElement.hasAttribute(NAME) && attributeElement.getAttribute(NAME) != null) {
	                     strAttrName = attributeElement.getAttribute(NAME).trim();
	                     attributeMap.put(NAME, strAttrName);
	                     //System.out.print("Name :" + strAttrName + " ");
	                     
	                 } else {
	                     throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                 }
	                 
	                 if (attributeElement.hasAttribute(TYPE) && attributeElement.getAttribute(TYPE) != null) {
	                     strAttrDataType = attributeElement.getAttribute(TYPE).trim();
	                     attributeMap.put(TYPE, strAttrDataType);
	                     
	                     if(strAttrDataType.equalsIgnoreCase(GROUPED)){
	                     	Map tempNestedAttributeMap = readGroupedAttributeForServerManager(node.getChildNodes(), attributeDetailMap,strVendorID,strVendorName);
	                     	attributeMap.put(GROUPED,tempNestedAttributeMap);
	                     }
	                     // System.out.print("Type :" + attrDataType + " \n");
	                     
	                 } else {
	                     throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                 }
	                 
	                 if (attributeElement.hasAttribute(HAS_TAG) && attributeElement.getAttribute(HAS_TAG) != null) {
	                 	strAttrTagDataType = attributeElement.getAttribute(HAS_TAG).trim();
	                     if (strAttrTagDataType.equalsIgnoreCase("yes") || strAttrTagDataType.equalsIgnoreCase("no") ||
	                     		strAttrTagDataType.equalsIgnoreCase("true") || strAttrTagDataType.equalsIgnoreCase("false")) {
	                         strAttrTagDataType = attributeElement.getAttribute(HAS_TAG).trim();
	                         // System.out.print("has-tag :" + attrTagDataType + " \n");
	                         
	                     } else {
	                         throw new DictionaryParseException("Invalid value for has-tag attribute specified for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                     }
	                 }
	                 attributeMap.put(HAS_TAG, strAttrTagDataType);
	                 
	                 if (attributeElement.hasAttribute(IGNORECASE) && attributeElement.getAttribute(IGNORECASE) != null) {
	                 	strAttrIgnoreCase = attributeElement.getAttribute(IGNORECASE).trim();
	                     if (strAttrIgnoreCase.equalsIgnoreCase("yes") || strAttrIgnoreCase.equalsIgnoreCase("no") ||
	                     		strAttrIgnoreCase.equalsIgnoreCase("true") || strAttrIgnoreCase.equalsIgnoreCase("false")) {
	                     	strAttrIgnoreCase = attributeElement.getAttribute(IGNORECASE).trim();
	                     } else {
	                         throw new DictionaryParseException("Invalid value for ignore-case attribute specified for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                     }
	                 }
	                 attributeMap.put(IGNORECASE, strAttrIgnoreCase);
	                 
	                 if (attributeElement.hasAttribute(AVPAIR) && attributeElement.getAttribute(AVPAIR) != null) {
	                 	strAttrIsAvpair = attributeElement.getAttribute(AVPAIR).trim();
	                     if (strAttrIsAvpair.equalsIgnoreCase("yes") || strAttrIsAvpair.equalsIgnoreCase("no") ||
	                     		strAttrIsAvpair.equalsIgnoreCase("true") || strAttrIsAvpair.equalsIgnoreCase("false")) {
	                     	strAttrIsAvpair = attributeElement.getAttribute(AVPAIR).trim();
	                     } else {
	                         throw new DictionaryParseException("Invalid value for avpair attribute specified for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                     }
	                 }
	                 attributeMap.put(AVPAIR, strAttrIsAvpair);
	                 
	                 if (attributeElement.hasAttribute(ENCRYPT_STANDARD) && attributeElement.getAttribute(ENCRYPT_STANDARD) != null) {
	                     
	                     strAttrEncryptStandard = attributeElement.getAttribute(ENCRYPT_STANDARD).trim();
	                     try {
	                         Integer.parseInt(strAttrEncryptStandard);
	                         attributeMap.put(ENCRYPT_STANDARD,strAttrEncryptStandard);
	                     } catch (NumberFormatException e) {
	                     	throw new DictionaryParseException("Encryption standard " + strAttrEncryptStandard + " is not in proper format for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                     }
	                 }
	                 
	                 
	                 StringBuffer strSupportedValue = new StringBuffer("");
	                 attributeMap.put(SUPPORTED_VALUES, strSupportedValue.toString());
	                 
	                 // Now search for supported values for the attribute.
	                 NodeList subNodeList = node.getChildNodes();
	                 for ( int k = 0; k < subNodeList.getLength(); k++ ) {
	                     Node DictNameNode = subNodeList.item(k);
	                     
	                     if (DictNameNode.getNodeName().equals(SUPPORTED_VALUES)) {
	                         NodeList valueNodeList = DictNameNode.getChildNodes();
	                         
	                         for ( int m = 0; m < valueNodeList.getLength(); m++ ) {
	                             String valueID = null;
	                             String valueName = null;
	                             Node valueNameNode = valueNodeList.item(m);
	                             
	                             if (valueNameNode.getNodeName().equals(VALUE)) {
	                                 
	                                 Element valueElement = (Element) valueNameNode;
	                                 
	                                 if (valueElement.hasAttribute(ID) && valueElement.getAttribute(ID) != null) {
	                                     valueID = valueElement.getAttribute(ID).trim();
	                                     
	                                     try {
	                                         Integer.parseInt(valueID);
	                                         
	                                     }
	                                     catch (NumberFormatException e) {
	                                     	if(Logger.isLogLevel(LogLevel.DEBUG.LEVEL))
	                                         Logger.logDebug(MODULE, "Not a vlid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + StrAttrID);
	                                         
	                                     }
	                                 }
	                                 
	                                 if (valueElement.hasAttribute(NAME) && valueElement.getAttribute(NAME) != null) {
	                                     valueName = valueElement.getAttribute(NAME).trim();
	                                 }
	                                 
	                                 if (valueID != null && valueName != null) {
	                                     
	                                     if (strSupportedValue.toString().equals("")){
	                                         strSupportedValue.append(valueName);
	                                         strSupportedValue.append(':');
	                                         strSupportedValue.append(valueID);
	                                     }else strSupportedValue.append("," + valueName + ":" + valueID);
	                                 }
	                             }
	                         } // end of for loop
	                         attributeMap.put(SUPPORTED_VALUES, strSupportedValue.toString());
	                     } else {
	                     	if(Logger.isLogLevel(LogLevel.DEBUG.LEVEL))
	                         Logger.logDebug(MODULE, "Supported values not defined for " + strVendorID + ":" + strVendorName + ":" + StrAttrID + ":" + strAttrName);
	                     }
	                 }
	             }
	             nestedAttributeMap.put(StrAttrID, attributeMap);
	         }
			 return nestedAttributeMap;
		}
		
	//}
*/		
		
		
		
	
	

}
