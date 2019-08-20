package com.elitecore.aaa.radius.plugins.pmqm.conf.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.aaa.radius.plugins.pmqm.conf.PMQMAcctWSPluginConfiguration;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.conf.impl.BasePluginConfigurationImpl;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;

public class PMQMAcctWSPluginConfigurationImpl extends BasePluginConfigurationImpl implements PMQMAcctWSPluginConfiguration{

	private static final String MODULE = "PMQM_ACCT_PLUGIN_CONF_IMPL";
	private Map<Object, Object> pluginDetail ;
	private String fileName = getServerContext().getServerHome() + File.separator + "conf" + File.separator + "plugins" + File.separator + "pmqm-acct-plugin.xml";;
	
	public PMQMAcctWSPluginConfigurationImpl(ServerContext serverContext) {
		super(serverContext, "PMQM_ACCT_PLUGIN");
	}
	
	public PMQMAcctWSPluginConfigurationImpl(ServerContext serverContext,String pluginName) {
		super(serverContext, pluginName);
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);

			DocumentBuilder documentBuilder =  factory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(fileName));
			NodeList pmqmSystemList = document.getElementsByTagName("pmqm-acct-plugin").item(0).getChildNodes();
			pluginDetail = readAll(pmqmSystemList);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error reading the configuration file :" + fileName);
			throw new LoadConfigurationException(e.getMessage());
		}
	}
	
	private HashMap<Object, Object> readAll(NodeList nodeList){
		HashMap<Object, Object> localParameterMap = new HashMap<Object, Object>();

		for(int i=0;i< nodeList.getLength();i++){
			Node baseNode = nodeList.item(i);
			if(baseNode.getNodeName().equals("webserver-properties")){
				NodeList subChildNodes = baseNode.getChildNodes();
				for(int j=0;j<subChildNodes.getLength();j++){
					Node subChildNode = subChildNodes.item(j);
					if(subChildNode.getNodeName().equalsIgnoreCase("url")) {
						localParameterMap.put(URL,subChildNode.getTextContent());					
										}
									}
			}else if(baseNode.getNodeName().equals("pm-field-mapping")){
				NodeList nodeChildNodeList = baseNode.getChildNodes();
				for(int k=0; k<nodeChildNodeList.getLength(); k++){
					Node subNode = nodeChildNodeList.item(k);
					if(subNode.getNodeName().equals("acct-field-mapping")){
						Collection<Object> acctAttributeList = new ArrayList<Object>();
						NodeList subChildNodes = subNode.getChildNodes();
						for(int j=0;j<subChildNodes.getLength();j++){
							Node subChildNode = subChildNodes.item(j);
							if(subChildNode.getNodeName().equals("attribute")){
								acctAttributeList.add(getAcctAttributeInfoMap(subChildNode.getChildNodes()));
							}
						}
						localParameterMap.put(ACCT_FIELD_MAPPING, acctAttributeList);
					}		
				}

			}
		}		
		return localParameterMap;
	}

	
	private Map<Object, Object> getAcctAttributeInfoMap(NodeList parameterList){
		Map<Object, Object> attributeMap = new HashMap<Object, Object>();
		for(int i=0; i<parameterList.getLength(); i++){
			Node subNode = parameterList.item(i);
			if(subNode.getNodeName().equals("acct-attribute-id")){
				String strAttributeId = subNode.getTextContent();
				ArrayList<HashMap<Object, Object>> multipleAttrIdList = new ArrayList<HashMap<Object, Object>>();
				StringTokenizer stringTokenizer = new StringTokenizer(strAttributeId,",;");
				while(stringTokenizer.hasMoreTokens()){
					multipleAttrIdList.add(getAttributeMap(stringTokenizer.nextToken()));
				}
				attributeMap.put(MULTIPLE_ATTRIBUTE_ID, multipleAttrIdList);
			}else if(subNode.getNodeName().equals("pm-field")){
				attributeMap.put(PM_FIELD, subNode.getTextContent());
			}else if(subNode.getNodeName().equals("default-value")){
				String defaultValue = subNode.getTextContent();
				if(defaultValue.length() == 0)
					defaultValue = null;
				attributeMap.put(DEFAULT_VALUE, defaultValue);
			}else if(subNode.getNodeName().equals("value-mapping")){
				ArrayList<Map<Object, Object>> valueMappingList = new ArrayList<Map<Object, Object>>();
				Map<Object, Object> radiusRatingValueMap = new HashMap<Object, Object>();
				NodeList subChildNodes = subNode.getChildNodes();
				for(int j=0;j<subChildNodes.getLength();j++){
					Node subChildNode = subChildNodes.item(j);
					if(subChildNode.getNodeName().equals("value")){
						NodeList valueSubNodeList = subChildNode.getChildNodes();
						String radiusValue = null;
						String ratingValue = null;
						for(int k=0; k<valueSubNodeList.getLength(); k++){
							Node valueSubNode = valueSubNodeList.item(k);
							if(valueSubNode.getNodeName().equals("radius")){
								radiusValue = valueSubNode.getTextContent();
							}else if(valueSubNode.getNodeName().equals("pm")){
								ratingValue = valueSubNode.getTextContent();
							}
						}
						if(radiusValue.trim().length() != 0 && ratingValue.trim().length() != 0){
							StringTokenizer stk = new StringTokenizer(radiusValue,",;");
							int count = stk.countTokens();
							if(valueMappingList.isEmpty()){
								for(int l=0;l<count;l++){
									radiusRatingValueMap = new HashMap<Object, Object>();
									valueMappingList.add(radiusRatingValueMap);
									radiusRatingValueMap.put(stk.nextToken(), ratingValue);
								}
							}else if(count <= valueMappingList.size()){
								for(int l=0;l<count;l++){
									radiusRatingValueMap = valueMappingList.get(l);
									radiusRatingValueMap.put(stk.nextToken(), ratingValue);
								}
							}else if(count > valueMappingList.size()){
								int diffCount = count - valueMappingList.size();
								for(int m=0;m<diffCount;m++){
									radiusRatingValueMap = new HashMap<Object, Object>();
									valueMappingList.add(radiusRatingValueMap);
								}
								for(int l=0;l<count;l++){
									radiusRatingValueMap = valueMappingList.get(l);
									radiusRatingValueMap.put(stk.nextToken(), ratingValue);
								}
							}
						}
					}
				}
				attributeMap.put(VALUE_MAPPING, valueMappingList);
			}
		}
		return attributeMap;
	}
	
	private HashMap<Object,Object> getAttributeMap(String attrId){
		HashMap<Object,Object> attrMap = new HashMap<Object,Object>();
		if(attrId.contains(":")){
			String strVendorId = attrId.substring(0,attrId.indexOf(':'));
			attrId = attrId.substring(attrId.indexOf(':')+1);
			attrMap.put(VENDOR_ID, strVendorId);
			attrMap.put(ATTRIBUTE_ID, getAttributeIds(attrId));
		}else{
			attrMap.put(VENDOR_ID, "0");			
			attrMap.put(ATTRIBUTE_ID, getAttributeIds(attrId));
		}
		return attrMap;
	}

	private int[] getAttributeIds(String strAttributeId) {
		int ids[] = null;
		int iattrId = 0;
		if(strAttributeId.contains(":")){
			String strIds[] = strAttributeId.split(":");
			ids = new int[strIds.length];
			for(int i=0; i<strIds.length; i++){
				try{
					iattrId = Integer.parseInt(strIds[i]);
				}catch (NumberFormatException nfe) {
					iattrId = 0;
				}
				ids[i] = iattrId;
			}
		}else{
			try{
				iattrId = Integer.parseInt(strAttributeId);
			}catch (NumberFormatException nfe) {
				iattrId = 0;
			}
			ids = new int[1];
			ids[0] = iattrId;
		}
		return ids;
	}

	@Override
	public Object getConfigValue(String key) {
		return pluginDetail.get(key);
	}

	@Override
	public String getConfigFileName() {
		return fileName;
	}
	
	@Override
	public String getKey() {
		return "PMQM_ACCT_PLUGIN";
}

	@Override
	public EliteNetConfigurationData getNetConfigurationData() {
		try {
			return read(getConfigFileName(),MODULE, getKey());
		} catch (ReadConfigurationFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
		}		
		return null;
	}
	
	@Override
	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException {
		boolean bSuccess = true;
		String strReturn = "";
		//strReturn =getServerContext().getServerHome() + File.separator + "conf" + File.separator + "services" + File.separator ;
		strReturn = getServerContext().getServerHome() + File.separator + "tempconf"  + File.separator  + "plugins"  + File.separator ;
		Iterator iterator = lstConfiguration.iterator();
		while(iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)iterator.next();
			
			String configurationKey = eliteNetConfigurationData.getNetConfigurationKey();
			
			if(configurationKey != null && configurationKey.equalsIgnoreCase(getKey())) {
				try{
					bSuccess = write(strReturn, "pmqm-acct-plugin.xml", eliteNetConfigurationData.getNetConfigurationData());
				}catch(Exception e){
					bSuccess = false;
					throw new UpdateConfigurationFailedException("Update PMQM Accounting Plugin Configuration failed. Reason: " + e.getMessage(),e);
				}
			}				
		}
		
		return bSuccess;
	}
}

