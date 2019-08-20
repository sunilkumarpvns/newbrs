package com.elitecore.elitesm.web.expressionbuilder.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;

public class ListUtility {

	
	private final static List<String> operatorList= new ArrayList<String>();
	private final static List<String> logicalOperatorList = new ArrayList<String>();
	private final static Map<String,String> attrMap = new HashMap<String, String>();
	private final static Map<String,String> operMap = new HashMap<String, String>();
	private final static Map<String,String> logicalMap = new HashMap<String, String>();
    private final List<AttributeData> attributeList= new ArrayList<AttributeData>();

	private static TreeSet<String> set = null;
	private static TreeSet<String> logicalOpset = null;
	
	public ListUtility(List<AttributeData> attributeList){
		
		set = new TreeSet<String>();
		logicalOpset = new TreeSet<String>();
		/* add to Attribute List*/
		
		getAttributes(attributeList);
		

		/*operator List*/
		set.addAll(attrMap.keySet());
		//setTreeSet();
		operMap.put("=", "=");
		operMap.put("!=", "!=");
		operMap.put("<", "<");
		operMap.put(">", ">");

		operatorList.add("=");
		operatorList.add("!=");
		operatorList.add(">");
		operatorList.add("<");

		/*logical Operator*/
		
		logicalMap.put("&&", "&&");
		logicalMap.put("||", "||");
		logicalOperatorList.add("&&");
		logicalOperatorList.add("||");
		logicalOpset.addAll(logicalMap.keySet());
		this.attributeList.addAll(attributeList);

	}
	
	/*private void setTreeSet() {
		for(int i=0;i<attrMap.size();i++){
			
			
		}
		
	}*/

	private void getAttributes(List<AttributeData> attributeList) {
		
		for(int i=0;i<attributeList.size();i++){	
			AttributeData attributeData = (AttributeData) attributeList.get(i);
			String attributeId=attributeData.getAttributeId();
			String attributeName=attributeData.getName();
			attrMap.put(attributeName,attributeName+" [" +attributeId+ "]");
		}
		
		
		
	}

	public Map<String,String> getCompletionsForS0(String prefix){
		Map<String,String> comp = new HashMap<String, String>();
		try{
		
		prefix=prefix.trim();
		
		
		
		if(prefix.trim().length()>0){
			SortedSet<String> tail = set.tailSet(prefix);
			for(String str:tail){
				if(str.startsWith(prefix)){
					comp.put(str,attrMap.get(str));
				}else{
					break;
				}
			}
		}else{
			comp.putAll(attrMap);
		}
		}catch(Exception e){
			Log.error("Error",e.getCause());
		}
		return comp;
	}

	public Map<String,String> getCompletionsForS1(String prefix){
		
		prefix = prefix.trim();
		Map<String,String> comp = new HashMap<String, String>();
		if(prefix.trim().length()>0){
		SortedSet<String> tail = set.tailSet(prefix.toUpperCase());
		Log.debug("Tail"+tail);
		for(String str:tail){
            
			if(str.toLowerCase().startsWith(prefix.toLowerCase())){
				comp.put(str,attrMap.get(str));
				Log.debug("str if: "+str);
				
			}/*else{
				Log.debug("str else:"+str);
				break;
			}*/
		}
		}else{
			comp.putAll(attrMap);
		}

		
		
		if(!comp.isEmpty()){
			
			Iterator<String> itr=operMap.keySet().iterator();
			while (itr.hasNext()) {
				String str = (String) itr.next();
				comp.put(str, str);
			}
			
		}

		if(comp.keySet().contains(prefix)){
			comp.keySet().remove(prefix);
		}
		return comp;

	}
	
	
	public Map<String,String> getCompletionsForS2(String expr){
		Map<String,String> comp = new HashMap<String, String>();
		
		/*
		 *   get key-operator-val array
		 */
		
		String[] keyValArray=splitKeyAndValue(expr);
		
		Log.debug("attributeName"+keyValArray[0].trim());
		Map<String,String> supportedValueMap = getSupportedValues(keyValArray[0].trim()); 
		
		/* 
		 *  TO DO
		 *  if Attribute type is integer add supported value to completions 
		 */
		
		if(supportedValueMap != null && !supportedValueMap.isEmpty()){
			comp.putAll(supportedValueMap);
		}else{
			comp.put(" "," ");	
		}
		

		return comp;

	}

	

	private Map<String, String> getSupportedValues(String attributeName) {
		Map<String, String> supportedValuesMap = null;
		try{
			AttributeData attributeData= null;
			boolean flag=false;
			for(int i=0;i<attributeList.size();i++){
				attributeData=attributeList.get(i);
				String  name=attributeData.getName();
				if(attributeName.equalsIgnoreCase(name)){
					flag=true;
					break;
				}
			}
			if(flag){
				String predefinedValues=attributeData.getPredefinedValues();
				String[] supportedValArray=predefinedValues.split(",");
				if(supportedValArray != null && supportedValArray.length>0){
					supportedValuesMap = new HashMap<String, String>();
					for(int i=0;i<supportedValArray.length;i++){
						String supportedValue=supportedValArray[i];
						String[] supportedValueArray=supportedValue.split(":");
						if(supportedValueArray != null && supportedValueArray.length > 0 ){
							String displayName=supportedValueArray[0]+" [ "+supportedValueArray[1]+" ]";
							String actualValue=supportedValueArray[1];
							supportedValuesMap.put(actualValue,displayName);
						}
					}
				}
			}

		}catch(Exception exp){
			Log.error("Error in getSupportedValues Method",exp);
		}
		return supportedValuesMap;

	}

	public Map<String,String> getCompletionsForS3(String prefix){
		return new HashMap<String,String>();
	}

	public Map<String,String> getCompletionsForS4(String prefix){
		prefix = prefix.trim();
		Log.debug("Prefix Data is"+prefix);
		Map<String,String> comp = new HashMap<String, String>();
		if(prefix.trim().length()>0){
		SortedSet<String> tail = logicalOpset.tailSet(prefix);
		for(String str:tail){

			if(str.startsWith(prefix)){
				comp.put(str,logicalMap.get(str));
				Log.debug("str"+str);
			}else{
				break;
			}
		}
		}else{
			comp.putAll(logicalMap);
		}


		if(comp.keySet().contains(prefix)){
			comp.keySet().remove(prefix);
		}
		return comp;

	}



	/*
	 * Suggestion getter and setter
	 */

	

	public static List<String> getOperatorList() {
		return operatorList;
	}

	public static List<String> getLogicalOperatorList() {
		return logicalOperatorList;
	}

	
	/**
	 * 
	 * @param attr
	 * @return 
	 */
	
	public static String[] splitKeyAndValue(String attr){
		/***
		 * string[0] = key
		 * string[1] = operator
		 * string[2] = value
		 */
		//System.out.println("Attr : " + attr);
		String[] splitedAttr = new String[3];
		StringBuilder strBuilder = new StringBuilder();
		int pos=0;
		char[] charString = attr.toCharArray();
		final int len = charString.length;
		while(pos < len){
			char ch = charString[pos];
			if(ch == '='){
				if(strBuilder.toString().length()>0)
					splitedAttr[0] = strBuilder.toString();
				splitedAttr[1] = "=";
				splitedAttr[2] = attr.substring(pos+1);
				return splitedAttr;
			}else if(ch == '!'){
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "!=";
					splitedAttr[2] = attr.substring(pos+1);
					return splitedAttr;
				}				
			}else if(ch == '<'){
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "<=";
					splitedAttr[2] = attr.substring(pos+1);					
				}else{						
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "<";
					splitedAttr[2] = attr.substring(pos+1);								
				}
				return splitedAttr;
			}else if(ch == '>'){				
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = ">=";
					splitedAttr[2] = attr.substring(pos+1);					
				}else{						
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = ">";
					splitedAttr[2] = attr.substring(pos+1);								
				}
				return splitedAttr;
			}else {
				strBuilder.append(ch);
				pos++;
			}						
		}
		return splitedAttr;
	}



}
