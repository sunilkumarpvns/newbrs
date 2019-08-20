package com.elitecore.diameterapi.diameter.translator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.GroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderFields;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.Key;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Parses the Copy Packet Mappings and creates an OperatoionData for Copy Packet Operation.
 *  
 * @author monica.lulla
 */
public abstract class CopyPacketParser<P> {

	private static final String ASTERISK = "*";
	private static final Compiler compiler = Compiler.getDefaultCompiler();
	private static final String WITH = "with";
	private static final String WHEN = "when";
	private static final String DO = "do";
	public static final String THIS = "this";
	
	private Pattern keywordPattern;
	
	protected CopyPacketParser(Pattern keywordPattern){
		this.keywordPattern = keywordPattern;
	}
	
	public  OperationData parse(String checkExpression, String destMapping, String srcMapping, String defaultValue, String valueMapping) throws InvalidExpressionException {

		//Destination Mapping is Must
		if(Strings.isNullOrBlank(destMapping)){
			throw new InvalidExpressionException("Invalid Destination Mapping specified.");
		}

		//Check Expression for Mapping
		LogicalExpression checkExp = buildLogicalExpression(checkExpression);

		//Parse Destination Key
		Key<?> destKey = parseDestinationKey(destMapping);
		
		//Parse Source Key
		Key<?> sourceKey = parseSourceKey(srcMapping, defaultValue , valueMapping);

		return new OperationData(checkExp, new AttributeMapping(sourceKey, destKey));
	}
	
	/**
	 * Parses a Logical Expression. 
	 * If Logical Expression is "*" it will return null, 
	 * which indicates Always true expression. 
	 * 
	 * @throws InvalidExpressionException if any parser exception occurs.
	 */
	private  LogicalExpression buildLogicalExpression(String checkExpression)
			throws InvalidExpressionException {

		if(Strings.isNullOrBlank(checkExpression)) {
			return null;
		}
		checkExpression = checkExpression.trim();
			
		if(ASTERISK.equalsIgnoreCase(checkExpression)) {
			return null;
		}
		return compiler.parseLogicalExpression(checkExpression);
	}

	private  Key<?> parseDestinationKey(String destMapping)
			throws InvalidExpressionException {

		destMapping = destMapping.trim();

		HeaderFields<P> headerField = getHeaderField(destMapping);
		if(headerField != null){
			return new HeaderKey<P>(headerField);
		}
		
		String parent = null;
		String preCheckExpression = null;
		
		//Check for JSON {'with': .....
		if(destMapping.startsWith("{")){
			Object[] withObjects =  parseWithObject(JSONObject.fromObject(destMapping));
			
			// Destination Key is not Allowed to contain any other JSON except with JSON
			if(withObjects == null) {
				throw new InvalidExpressionException("Invalid JSON in Destination Mapping");
			}
			
			// Operation in With JSON has to Identifier or Literal, JSON Object not Allowed. 
			if(withObjects[2] instanceof JSONObject) {
				throw new InvalidExpressionException("Group Attribute not allowed in Destination Mapping");
			}
			
			parent = (String) withObjects[0];
			preCheckExpression = (String) withObjects[1];
			destMapping = (String) withObjects[2];
		}
		return  buildDestinationKey(destMapping, parent, preCheckExpression);
	}

	/**
	 * Parse source Mapping. This parses JSON Source key
	 * 
	 * @param srcKeyArgs
	 * @param defaultValue
	 * @param valueMapping
	 * @return source key
	 * @throws InvalidExpressionException if expression library parser encounters Invalid Expression
	 */
	private  Key<?> parseSourceKey(String srcKeyStr, String defaultValue, String valueMapping) throws InvalidExpressionException {

		// No source key then it must be an only default Value case
		if(Strings.isNullOrBlank(srcKeyStr)){
			return new NonGroupedKey(defaultValue);
		}
		srcKeyStr = srcKeyStr.trim();
		
		//If Source is a JSON
		if(srcKeyStr.startsWith("{")){
			return parseJSONKey(srcKeyStr, valueMapping, defaultValue);
		}
		
		// Simple Non Grouped Key
		return buildSourceKey(srcKeyStr, defaultValue, parseValue(valueMapping), null, null);
	}

	/**
	 * 
	 * Builds a NonGrouped Key, containing Map of KeywordAVPValueProvider a as, 
	 * 
	 * Pattern is designed to give Groups as,
	 *  
	 * For String, ${SRCREQ}:0:456.0:431
	 * 
	 * Group 0 -> ${SRCREQ}:0:456.0:431
	 * Group 1 -> ${SRCREQ}
	 * Group 2 -> SRCREQ
	 * Group 3 -> :0:456.0:431
	 * Group 4 -> 0:456.0:431
	 * 
	 * After Constructing Map Entry for Extractor, 
	 * It creates an Escaped Identifier Expression out of Keyword, So as fetch value for Keyword. 
	 * 
	 */
	private  NonGroupedKey buildSourceKey(String srcKeyStr, String defaultValue, Map<String, String> valueMappings, String parent, String preCheckCondition) throws InvalidExpressionException {
		
		if(defaultValue != null) {
			defaultValue = defaultValue.trim();
		}
		if(Strings.isNullOrBlank(srcKeyStr)) {
			return new NonGroupedKey(defaultValue);
		}
		srcKeyStr = srcKeyStr.trim();

		if(Strings.isNullOrBlank(parent)) {
			parent = null;
		} else {
			parent = parent.trim();
		}
		
		// Build Init time Map for Keywords, ${SRCREQ} and ${DSTREQ} 
		Map<String, KeywordValueProvider> packetExtractorMap = new HashMap<String, KeywordValueProvider>();
		
		Matcher matcher = keywordPattern.matcher(srcKeyStr);
		
		StringBuffer sb = new StringBuffer();
		
		while(matcher.find()) {
			
			String matchedkey = matcher.group();
			KeywordValueProvider keywordValueProvider = createKeywordValueProvider(matcher.group(1), matcher.group(4));
			packetExtractorMap.put(matchedkey, keywordValueProvider);
			
			matcher.appendReplacement(sb, "\\\\\\$\\\\\\{" + matcher.group(2) + "\\\\\\}" + matcher.group(3));
		}
		matcher.appendTail(sb);
		srcKeyStr = sb.toString();
		
		if(packetExtractorMap.isEmpty()) {
			packetExtractorMap =  null;
		}
		
		return new NonGroupedKey(parent,
				buildLogicalExpression(preCheckCondition),
				compiler.parseExpression(srcKeyStr), 
				defaultValue, 
				valueMappings,
				packetExtractorMap);
	}
	
	protected abstract KeywordValueProvider createKeywordValueProvider(String keywordType, String argument) throws InvalidExpressionException;

	/**
	 * This Makes Normal Attribute to LiteralExpression instead of IdentifierExpression, 
	 * So that we can obtain AVP Id instead of AVP Value.
	 * 
	 * This is achieved by Quoting destinationKey value. 
	 * 
	 * @throws InvalidExpressionException if expression library parser encounters Invalid Expression 
	 */
	private  NonGroupedKey buildDestinationKey(String destKeyStr, String parent, String preCheckCondition) throws InvalidExpressionException {
		
		if(Strings.isNullOrBlank(destKeyStr)) {
			throw new InvalidExpressionException("Destination Key not found");
		}
		destKeyStr = destKeyStr.trim();
			
		if(Strings.isNullOrBlank(parent)) {
			parent = null;
		} else {
			parent = parent.trim();
		}
		return new NonGroupedKey(parent,
				buildLogicalExpression(preCheckCondition),
				compiler.parseExpression("\"" + destKeyStr + "\""));
	}

	/**
	 * Parses a JSON String. 
	 * It can be 'with' JSON String (for Conditional Attribute creation) 
	 * as well as normal JSON (for Grouped Attribute creation)  
	 * 
	 * @param jsonString to parse
	 * @param valueMapping Raw String includes both types {"key = value" , " id.key = val" }
	 * @param defaultValue Raw String includes both types {"defaltvalue" , " id = val" }
	 * @return Source Key
	 * 
	 * @throws InvalidExpressionException if Expression Library Parser encounters Invalid expression
	 */
	private  Key<?> parseJSONKey(String jsonString, String valueMapping, String defaultValue) throws InvalidExpressionException {

		JSONObject json = JSONObject.fromObject(jsonString);

		// Check for Conditional with attribute JSON
		Object [] withObjects = parseWithObject(json);
		if(withObjects == null) {

			// This JSON is for Grouped as with objects not received.
			return new GroupedKey(parseAttributeMappings(
					json, 
					parseGrpValueMapping(valueMapping),
					parseValue(defaultValue)));
		}
		//Conditional 'with' JSON arrived.
		// If operation is again JSON
		if(withObjects[2] instanceof JSONObject) {

			// Create a Conditional Grouped Key.
			return new GroupedKey(
					(String) withObjects[0], 
					buildLogicalExpression((String) withObjects[1]), 
					parseAttributeMappings(JSONObject.fromObject(withObjects[2]), 
							parseGrpValueMapping(valueMapping),
							parseValue(defaultValue)));
		} 
		// Non Grouped conditional key
		return buildSourceKey((String) withObjects[2], defaultValue, parseValue(valueMapping), (String) withObjects[0], (String) withObjects[1]);
	}

	/**
	 * This is similar to Parse JSON Key method, 
	 * Difference lies that this entertains JSON objects, 
	 * Grouped Value Mapping Maps and Default Value Map instead of raw Strings
	 * 
	 * @param json
	 * @param valueMapForGrpAVP map : Map<Attribute Key, Map<Key, value>> 
	 * @param defaultValues map : Map<Attribute Key, DefaultValue>
	 * @return Source Key
	 * 
	 * @throws InvalidExpressionException if Expression Library Parser encounters Invalid expression
	 */
	private  Key<?> parseJSONObject(JSONObject json, 
			Map<String, Map<String, String>> valueMapForGrpAVP, 
			Map<String,String> defaultValues) throws InvalidExpressionException {

		// Check for Conditional with attribute JSON
		Object [] withObjects = parseWithObject(json);

		if(withObjects == null) {

			// This JSON is for Grouped as with objects not received.
			return new GroupedKey(parseAttributeMappings(
					JSONObject.fromObject(json), 
					valueMapForGrpAVP,
					defaultValues));
		}
		//Conditional 'with' JSON arrived.
		// If operation is again JSON
		if(withObjects[2] instanceof JSONObject) {

			// Create a Conditional Grouped Key.
			return new GroupedKey(
					(String) withObjects[0], 
					buildLogicalExpression((String) withObjects[1]), 
					parseAttributeMappings(JSONObject.fromObject(withObjects[2]), 
							valueMapForGrpAVP,
							defaultValues));
		} 
		// Non Grouped conditional key
		String defaultValue = null;
		if(defaultValues != null) {
			defaultValue = defaultValues.get(withObjects[0]);
		}
		return buildSourceKey((String) withObjects[2], defaultValue, valueMapForGrpAVP.get(withObjects[0]), (String) withObjects[0], (String) withObjects[1]);

	}

	/**
	 * Parses JSON Object for with, when, do key. 
	 * Parses, JSON like,
	 * 
	 *  { 'with' : 'someAttribute' , 'when' : 'someLogicalExpression' , 'do': 'someAttribute'}
	 *  
	 * This will give Object Array of these three keys, 
	 * iff 'with' key is Available in JSON Object, since 'with' key is mandatory.
	 * JSON Object must contain 'with' key.
	 * 
	 * Keys 'do' and 'when' are optional.
	 * 
	 * If 'do' key is not found, object value of 'with' key will be considered.
	 * 
	 * @param json Object
	 * @return Object Array of With, When and Do Argument respectively, null if no 'with' key is found
	 * @throws InvalidExpressionException
	 */
	private  Object[] parseWithObject(JSONObject json) throws InvalidExpressionException {

		@SuppressWarnings("unchecked")
		Set<String> keySet = json.keySet();

		Object[] objects = null;

		Iterator<String> jsonIterator = keySet.iterator();
		if(jsonIterator.hasNext()) {
			if(keySet.contains(WITH)){

				objects =  new Object[3];

				objects[0] = json.get(WITH);
				objects[1] = json.get(WHEN);
				objects[2] = json.get(DO);
				
				if(objects[2] == null) {
					objects[2] = THIS;
				}
			}
		}
		return objects;
	}


	/**
	 * Handles JSON Recursive Parsing to give Grouped Attribute Mappings.
	 * 
	 * @param json
	 * @param valueMapForGrpAVP
	 * @param defaultValues
	 * @return
	 * @throws InvalidExpressionException
	 */
	private  List<AttributeMapping> parseAttributeMappings(JSONObject json,Map<String, Map<String, String>> valueMapForGrpAVP, Map<String,String> defaultValues) throws InvalidExpressionException {

		List<AttributeMapping> attributeMappings = new ArrayList<AttributeMapping>();

		@SuppressWarnings("unchecked")
		Set<String> keySet = json.keySet();
		for(String attributekey : keySet){

			Key<Expression> destinationKey = buildDestinationKey(attributekey, null, null);
			Object object = json.get(attributekey);
			AttributeMapping attributeMapping;
			if(object instanceof JSONObject){

				Key<?> sourceKey = parseJSONObject((JSONObject) object, valueMapForGrpAVP, defaultValues);
				attributeMapping = new AttributeMapping(sourceKey ,destinationKey);

			}else{

				String srcKey = (String)object;
				String defaultValue = null;
				
				if(defaultValues != null) {
					defaultValue = defaultValues.get(srcKey);
				}
				Map<String, String> valueMap = valueMapForGrpAVP.get(srcKey);
				
				NonGroupedKey sourceKey =  buildSourceKey(srcKey, defaultValue, valueMap, null, null);
				attributeMapping = new AttributeMapping(sourceKey , destinationKey);
			}
			attributeMappings.add(attributeMapping);
		}
		return attributeMappings;
	}

	/**
	 * Parsing string attr.key = value , ...
	 * to Map<attr, Map<key, value>>
	 */
	private  Map<String, Map<String,String>> parseGrpValueMapping(String valueMappings) {
		Map<String, Map<String,String>> grpAttMap = new HashMap<String, Map<String,String>>();
		if(valueMappings == null){
			return grpAttMap;
		}
		for(String value : valueMappings.split(",")){
			String [] values = value.split("=");
			
			String key = values[0].substring(0,values[0].lastIndexOf('.'));
			key = key.trim();
			
			Map<String,String> tempValMap = grpAttMap.get(key);
			if(tempValMap == null) {
				tempValMap = new HashMap<String,String>();
				grpAttMap.put(key, tempValMap);
			}
			tempValMap.put(values[0].substring(values[0].lastIndexOf('.')+1).trim() , values[1].trim() );
		}
		return grpAttMap;
	}

	/**
	 * Parsing string key = value , ...
	 * to Map<key, value>
	 */
	private  Map<String,String> parseValue(String value){
		if(value == null || value.trim().length() == 0){
			return null;
		}
		Map<String,String> valueMap = new HashMap<String, String>();
		for(String val : value.split(",")){
			String[] data = val.split("=");
			valueMap.put(data[0].trim(), data[1].trim());
		}
		return valueMap;
	}
	
	public abstract HeaderFields<P> getHeaderField(String name);
	
}
