package com.elitecore.aaa.radius.sessionx.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.FieldMapping;

/**
 * 
 * @author narendra.pathai
 * 
 * This class is used for providing mapping from column name to property names and property names to
 * property type list.
 * This class parses the comma separated tokens from field mapping given in session manager.
 * E.g. $REQ(0:1),$RES(0:4),0:31 and stores the mapping for them to column name.
 * This class is used just for Radius Concurrency Session manager.
 *  
 */
public final class FieldMappingParser {

	private static final String MODULE = "FIELD_MAPPING_PARSER";
	private static final String requestMappingRegx = "\\$REQ\\([0-9]+[:[0-9]+]+\\)";
	private static final String responseMappingRegx = "\\$RES\\([0-9]+[:[0-9]+]+\\)";
	private static final String attributeRegx = "[0-9]+[:[0-9]+]+";
	private static final Pattern requestMappingRegex;
	private static final Pattern responseMappingRegex;
	private static final Pattern attributeRegex;
	private static final List<PropertyType> EMPTY_PROPERTY_LIST = Collections.emptyList();
	
	static{
		//compiling the request, response and attribute regular expressions
		requestMappingRegex = Pattern.compile(requestMappingRegx);
		responseMappingRegex = Pattern.compile(responseMappingRegx);
		attributeRegex = Pattern.compile(attributeRegx);
	}
	
	
	/*
	 * This map contains the mapping of the 
	 * propertiesName (Comma separated values) ---> List<PropertyType>
	 */
	private Map<String,List<PropertyType>> propertiesMap;

	/*
	 * This map contains the mapping of the 
	 * columnName ---> propertiesName (Comma separated values)
	 */
	private Map<String,String> columnToPropertiesMap;
	/*
	 * This map contains the mapping of the 
	 * propertiesName ---> columnName (Comma separated values)
	 */
	private Map<String,String> propertiesNameToColumnMap;
	/*
	 * This map contains the mapping of
	 * columnName ---> List<PropertyType>
	 */
	private Map<String,List<PropertyType>> columnToPropertyTypeMap;
	
	private List<FieldMapping> fieldMappings;
	
	private String smInstanceName;

	public FieldMappingParser(String smInstanceName,List<FieldMapping> fieldMappingsList){
		this.smInstanceName = smInstanceName;
		this.fieldMappings = fieldMappingsList;
		this.propertiesMap = new HashMap<String, List<PropertyType>>();
		this.columnToPropertiesMap = new HashMap<String, String>();
		this.propertiesNameToColumnMap = new HashMap<String, String>();
		this.columnToPropertyTypeMap = new HashMap<String, List<PropertyType>>();
		parseFieldMappings();
	}

	private void parseFieldMappings(){
		if(fieldMappings == null){
			//TODO put logger here
			return;
		}

		for(FieldMapping mapping : fieldMappings){
			String propertyNames = mapping.getPropertyName();
			StringTokenizer tokenizer = new StringTokenizer(propertyNames, ",");
			String propertyName;
			List<PropertyType> properties = new ArrayList<PropertyType>();
			while(tokenizer.hasMoreTokens()){
				propertyName = tokenizer.nextToken();
				if(requestMappingRegex.matcher(propertyName).matches()){
					properties.add(new PropertyType(fetchAttributeIdFromToken(propertyName), PropertyType.ACCESS_REQ));
				}else if(responseMappingRegex.matcher(propertyName).matches()){
					properties.add(new PropertyType(fetchAttributeIdFromToken(propertyName), PropertyType.ACCESS_RES));
				}else if(attributeRegex.matcher(propertyName).matches()){
					properties.add(new PropertyType(propertyName, PropertyType.ACCESS_REQ));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, smInstanceName + "- Invalid Field Mapping : " + propertyName + ". Skipped, so will not be considered while dumping session values.");
					}
				}
			}
			propertiesMap.put(propertyNames, properties);
			columnToPropertyTypeMap.put(mapping.getColumnName(), properties);
			columnToPropertiesMap.put(mapping.getColumnName(), propertyNames);
			propertiesNameToColumnMap.put(propertyNames, mapping.getColumnName());
		}
	}

	private String fetchAttributeIdFromToken(String token){
		return token.substring(token.indexOf('(') + 1,token.indexOf(')'));
	}

	public List<PropertyType> getPropertyListByProperties(String properties){
		List<PropertyType> propertyList = propertiesMap.get(properties);
		return propertyList != null ? propertyList : EMPTY_PROPERTY_LIST;
	}

	public List<PropertyType> getPropertyListByColumn(String columnName){
		List<PropertyType> propertyList = columnToPropertyTypeMap.get(columnName);
		return propertyList != null ? propertyList : EMPTY_PROPERTY_LIST;
	}

	public String getPropertiesByColumn(String columnName){
		return columnToPropertiesMap.get(columnName);
	}
	
	public String getColumnByPropertyName(String propertyName){
		return propertiesNameToColumnMap.get(propertyName);
	}

	public List<FieldMapping> getFieldMappings(){
		return this.fieldMappings;
	}
}
