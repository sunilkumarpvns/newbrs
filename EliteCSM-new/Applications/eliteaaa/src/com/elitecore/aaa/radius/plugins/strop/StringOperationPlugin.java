package com.elitecore.aaa.radius.plugins.strop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.plugins.strop.conf.StringOperationPluginConf;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class StringOperationPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {

	private List<Map<String, Object>> operationDetails;
	private Map<String,String[]> argumentMap = null;
	private Map<String,String> functionMap = null;
	private static final String MODULE = "STRING_OPERATION-PLUGIN";
	private static final String VENDOR_ID = "VENDOR_ID";
	private static final String ATTRIBUTE_ID = "ATTRIBUTE_ID";
	private static final String OPERATION = "OPERATION";
	private static final String CONDITIONS = "CONDITIONS";
	private static final String FIRST_ONLY = "FIRST_ONLY";
	
	public StringOperationPlugin(PluginContext pluginContext,PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo); 
		argumentMap = new HashMap<String,String[]>();
		functionMap = new HashMap<String,String>();
		
	}
	
	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		String strOperation;
		RadServiceResponse radServiceResponse = (RadServiceResponse)serviceResponse;
		for(int i=0;i<operationDetails.size();i++){
			
			HashMap<String, Object> detailsMap = (HashMap<String, Object>)operationDetails.get(i);   
    		
			long vendorId = (Long) detailsMap.get(VENDOR_ID);
    		int[] attribute_id = (int[])detailsMap.get(ATTRIBUTE_ID);
 
    		String operation = (String)detailsMap.get(OPERATION);
    		String condition = (String)detailsMap.get(CONDITIONS);
    		
    		String attributeName = "";
    		if(vendorId == 0){
    			attributeName = Dictionary.getInstance().getAttributeName(0, attribute_id);
    		}else{
    			attributeName = Dictionary.getInstance().getAttributeName(vendorId, attribute_id);
    		}

    		strOperation = functionMap.get(operation).toLowerCase();
    		Collection<IRadiusAttribute> radAttributes = radServiceResponse.getRadiusAttributes(vendorId, attribute_id);
    		if(radAttributes  == null){
    			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
    	        	LogManager.getLogger().info(MODULE, attributeName+" is not present in response packet, so operation : "+strOperation+" can't apply for this attribute.");
    			continue;
    		}
    		String[] conditionArguments = (String[])argumentMap.get(condition);
			String strCondition = (String)functionMap.get(condition);
			
			String [] operationArguments = (String[])argumentMap.get(operation);
			boolean bFirstOnly = true;
			Iterator<IRadiusAttribute> iterator = radAttributes.iterator();
			if(detailsMap.get(FIRST_ONLY)!=null)
				bFirstOnly = (Boolean)detailsMap.get(FIRST_ONLY);
			if(bFirstOnly){
				IRadiusAttribute attribute = iterator.next();
				checkForApplyOperation(strCondition, conditionArguments, strOperation, operationArguments,attributeName,attribute,radServiceResponse);
			}
			else{
				while(iterator.hasNext()){
					IRadiusAttribute attribute = iterator.next();
					checkForApplyOperation(strCondition, conditionArguments, strOperation, operationArguments,attributeName,attribute,radServiceResponse);
				}
			}
		}
	
		
	}

	@Override
	public void handlePreRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		String strOperation;
		RadServiceRequest radServiceRequest = (RadServiceRequest)serviceRequest;
		for(int i=0;i<operationDetails.size();i++){
			
			HashMap<String, Object> detailsMap = (HashMap<String, Object>)operationDetails.get(i);   
    		
			long vendorId = (Long) detailsMap.get(VENDOR_ID);
    		int[] attribute_id = (int[])detailsMap.get(ATTRIBUTE_ID);
 
    		String operation = (String)detailsMap.get(OPERATION);
    		String condition = (String)detailsMap.get(CONDITIONS);
    		
    		String attributeName = "";
    		if(vendorId == 0){
    			attributeName = Dictionary.getInstance().getAttributeName(0, attribute_id);
    		}else{
    			attributeName = Dictionary.getInstance().getAttributeName(vendorId, attribute_id);
    		}

    		strOperation = functionMap.get(operation).toLowerCase();
    		Collection<IRadiusAttribute> radAttributes = radServiceRequest.getRadiusAttributes(vendorId, attribute_id);
    		if(radAttributes  == null){
    			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
    	        	LogManager.getLogger().info(MODULE, attributeName+" is not present in request packet, so operation : "+strOperation+" can't apply for this attribute.");
    			continue;
    		}
    		String[] conditionArguments = (String[])argumentMap.get(condition);
			String strCondition = (String)functionMap.get(condition);
			
			String [] operationArguments = (String[])argumentMap.get(operation);
			boolean bFirstOnly = true;
			Iterator<IRadiusAttribute> iterator = radAttributes.iterator();
			if(detailsMap.get(FIRST_ONLY)!=null)
				bFirstOnly = (Boolean)detailsMap.get(FIRST_ONLY);
			if(bFirstOnly){
				IRadiusAttribute attribute = iterator.next();
				checkForApplyOperation(strCondition, conditionArguments, strOperation, operationArguments,attributeName,attribute,radServiceRequest);
			}
			else{
				while(iterator.hasNext()){
					IRadiusAttribute attribute = iterator.next();
					checkForApplyOperation(strCondition, conditionArguments, strOperation, operationArguments,attributeName,attribute,radServiceRequest);
				}
			}
		}
	}

	private void checkForApplyOperation(String strCondition,
			String[] conditionArguments, String strOperation,
			String[] operationArguments, String attributeName,
			IRadiusAttribute attribute, RadServiceRequest radServiceRequest) {
		boolean isConditionApplicable = true;
		String strAttributeValue = attribute.getStringValue();
		try{
			isConditionApplicable = applyCondition(strCondition, conditionArguments,strAttributeValue,attributeName);
		}catch(IllegalArgumentException ie){
			isConditionApplicable = false;
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
	        	LogManager.getLogger().info(MODULE, ie.getMessage()+" so,operation :" + strOperation + " can't apply for this attribute");
		}
		if(isConditionApplicable){    			
			try{
				String strResult = applyOperation(strOperation, operationArguments,strAttributeValue,attributeName);
				
				if (strResult != null) {
					if ("im2replace".equals(strOperation)) {					
						attribute.setStringValue(strResult);
						VendorSpecificAttribute radAttr = (VendorSpecificAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
						IVendorSpecificAttribute vendorSpAttr = Dictionary.getInstance().getVendorAttributeType(RadiusConstants.ELITECORE_VENDOR_ID);
						
						IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RATING_AVPAIR);
						String value = strAttributeValue.substring((strAttributeValue.indexOf('/')) + 1);
						
						radiusAttribute.setStringValue(value.substring((value.indexOf('@')) + 1,value.length()));
						vendorSpAttr.addAttribute(radiusAttribute);
						radAttr.setVendorTypeAttribute(vendorSpAttr);
						
						radServiceRequest.addInfoAttribute(radAttr);
						
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Value of "+ attributeName + " is changed from "+ strAttributeValue + " to "+ strResult);
					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Value of "+ attributeName + " is changed from "+ strAttributeValue + " to "+ strResult);
						attribute.setStringValue(strResult);
					}
				}
    	 }catch(IllegalArgumentException ie){
    			LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }catch(StringIndexOutOfBoundsException ie){
    		 	LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }catch(NullPointerException ie){
    		 	LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }
	  }
		
		
	}
	
	private void checkForApplyOperation(String strCondition,
			String[] conditionArguments, String strOperation,
			String[] operationArguments, String attributeName,
			IRadiusAttribute attribute, RadServiceResponse radServiceResponse) {
		boolean isConditionApplicable = true;
		String strAttributeValue = attribute.getStringValue();
		try{
			isConditionApplicable = applyCondition(strCondition, conditionArguments,strAttributeValue,attributeName);
		}catch(IllegalArgumentException ie){
			isConditionApplicable = false;
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
	        	LogManager.getLogger().info(MODULE, ie.getMessage()+" so,operation :" + strOperation + " can't apply for this attribute");
		}
		if(isConditionApplicable){    			
			try{
				String strResult = applyOperation(strOperation, operationArguments,strAttributeValue,attributeName);
				
				if (strResult != null) {
					if ("im2replace".equals(strOperation)) {					
						attribute.setStringValue(strResult);
						VendorSpecificAttribute radAttr = (VendorSpecificAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
						IVendorSpecificAttribute vendorSpAttr = Dictionary.getInstance().getVendorAttributeType(RadiusConstants.ELITECORE_VENDOR_ID);
						
						IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RATING_AVPAIR);
						String value = strAttributeValue.substring((strAttributeValue.indexOf('/')) + 1);
						
						radiusAttribute.setStringValue(value.substring((value.indexOf('@')) + 1,value.length()));
						vendorSpAttr.addAttribute(radiusAttribute);
						radAttr.setVendorTypeAttribute(vendorSpAttr);
						
						radServiceResponse.addAttribute(radAttr);
						
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Value of "+ attributeName + " is changed from "+ strAttributeValue + " to "+ strResult);
					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Value of "+ attributeName + " is changed from "+ strAttributeValue + " to "+ strResult);
						attribute.setStringValue(strResult);
					}
				}
    	 }catch(IllegalArgumentException ie){
    			LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }catch(StringIndexOutOfBoundsException ie){
    		 	LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }catch(NullPointerException ie){
    		 	LogManager.getLogger().trace(MODULE, "Error during applyling operation :"+strOperation+" for attribute : "+attributeName+" , reason: "+ie.getMessage());
    	 }
	  }
		
		
	}
	

	private String applyOperation(String operationName,
			String[] arguments, String attributeValue,
			String attributeName) {
		String resultString = attributeValue;
 		
		if("replacefirst".equals(operationName)){
			if(arguments == null ||arguments.length>2){
				
				throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			}
			if(arguments.length==1){
				resultString = attributeValue.replaceFirst(arguments[0], "");
			}else {
				resultString = attributeValue.replaceFirst(arguments[0], arguments[1]);
			}
						
		}else if("concat".equals(operationName)){
			if(arguments == null || arguments.length != 1){
				throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			}
			resultString = attributeValue.concat(arguments[0]);
		}else if("replaceall".equals(operationName)){
			if(arguments == null || arguments.length>2){
				throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			}
			if(arguments.length==1){
				resultString = attributeValue.replaceAll(arguments[0],"");
			}else if (arguments.length==2) {
				resultString = attributeValue.replaceAll(arguments[0],arguments[1]);
			}
			
		}else if("substring".equals(operationName)){
			 if(arguments != null ){
				 if(arguments.length==1)
					 resultString = attributeValue.substring(Integer.parseInt(arguments[0]));
				 else if(arguments.length==2)
					 resultString = attributeValue.substring(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
				 else
					 throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			 }else{
				 throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			 }
			 
			 
		}else if("tolowercase".equals(operationName)){
			if(arguments.length==1 && "".equals(arguments[0]))
				resultString = attributeValue.toLowerCase();
			else
				 throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
		}else if("touppercase".equals(operationName)){
			if(arguments.length==1 && "".equals(arguments[0]))
				resultString = attributeValue.toUpperCase();
			else
				 throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
		}else if("trim".equals(operationName)){
			if(arguments.length==1 && "".equals(arguments[0]))
				resultString = attributeValue.trim();
			else
				 throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			
		}else if("stripprefix".equals(operationName)|| "stripsuffix".equals(operationName)){
			if(arguments==null||arguments.length!=2)
				throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			return resultString = stripAttributeValue(operationName , arguments, attributeValue);
			
		}else if ("im2replace".equals(operationName)) {
			if (arguments == null) {
		
				throw new IllegalArgumentException("The number of arguments for operation :"+operationName+" for attribute : "+attributeName+" is invalid ,operation can't apply for this attribute");
			}			
			if (attributeValue.indexOf('@') >= 0) {				
				String attrValue = attributeValue.substring((attributeValue.indexOf('/')) + 1);
				if(arguments.length == 1 && (!arguments[0].equals(""))){
					resultString = attrValue.replace((attrValue.substring((attrValue.indexOf('@')) + 1, attrValue.indexOf('.',attrValue.indexOf('@')))), arguments[0]);
				}
				else{
					resultString = attrValue.replace((attrValue.substring((attrValue.indexOf('@')) + 1, attrValue.indexOf('.',attrValue.indexOf('@')))),attributeValue.substring(0,attributeValue.indexOf('/')));
				}				
				//resultString = resultString+ ","+ attrValue.substring((attrValue.indexOf('@')) + 1,attrValue.length());
			}
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Operation name "+operationName+" for attribute "+attributeName+" is invalid");
		}
		
	return resultString;

	}

	private String stripAttributeValue(String operationName,
			String[] arguments, String attributeValue) {
		int index = -1;
		String resultString =attributeValue;
		index = attributeValue.indexOf(arguments[0]);
			if(index == -1){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Attribute Value does not contain String :"+arguments[0]);
				return resultString;
			}else{
				if("stripprefix".equals(operationName)){
					if("0".equals(arguments[1]))
						resultString = attributeValue.substring(index+arguments[0].length(),attributeValue.length());
					else
						resultString = attributeValue.substring(index, attributeValue.length());
				}else if("stripsuffix".equals(operationName)){
					if("0".equals(arguments[1]))
						resultString = attributeValue.substring(0,index);
					else
						resultString = attributeValue.substring(0,index+arguments[0].length());
				}
					
			}
		return resultString;
	}

	private boolean applyCondition(String functionName,
			String[] arguments, String attributeValue,
			String attributeName) {
		if(functionName==null || functionName.length()==0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
	        	LogManager.getLogger().info(MODULE, "No Condition is  configured for attribute :"+attributeName+" ,so operation will be apply for this attribute");
			return true;
		}
		if("startsWith".equalsIgnoreCase(functionName)){
			if(arguments == null || arguments.length >2 || arguments.length < 1){
				throw new IllegalArgumentException("The number of arguments for condition:"+functionName+ " is not valid "+" for attributte : "+attributeName);
			}
			if(arguments.length == 1){
				return startsWith(attributeValue, arguments[0], false);
			}
			else{
				return startsWith(attributeValue, arguments[0], new Boolean(arguments[1]));
			}
		}else if("endsWith".equalsIgnoreCase(functionName)){
			if(arguments == null || arguments.length >2 || arguments.length < 1){
				throw new IllegalArgumentException("The number of arguments for condition:"+functionName+ " is not valid "+" for attributte : "+attributeName);
			}
			if(arguments.length == 1){
				return endsWith(attributeValue, arguments[0], false);
			}
			else{
				return endsWith(attributeValue, arguments[0], new Boolean(arguments[1]));
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
	        	LogManager.getLogger().info(MODULE, "Condition :"+functionName+ " for "+attributeName+" is invalid ,so operation can't apply for this attribute");
			return false;
		}
	}

	private boolean startsWith(String str, String prefix, boolean ignoreCase) {
		if (str == null || prefix == null || str.equals("")|| prefix.equals("")) {
			return (str == null && prefix == null);
		}
		if (prefix.length() > str.length()) {
			return false;
		}
		return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
	}
	private boolean endsWith(String str, String suffix, boolean ignoreCase) {
		if (str == null || suffix == null || str.equals("")|| suffix.equals("")) {
			return (str == null && suffix == null);
		}
		if (suffix.length() > str.length()) {
			return false;
		}
		return str.regionMatches(ignoreCase,str.length()-suffix.length(),suffix,0,suffix.length());
	}

	@Override
	public void init() throws InitializationFailedException {
		
		HashMap<String,String> tmpFunctionMap = new HashMap<String, String>();
		HashMap<String,String[]> tmpArgumentMap = new HashMap<String, String[]>();	
		List<Map<String, Object>> operationDetails = new ArrayList<Map<String, Object>>();		
		StringOperationPluginConf config = (StringOperationPluginConf)getPluginConfiguration();
		if(config == null){
			throw new InitializationFailedException("String Plugin configuration is null");
		}
		List<Map<String, Object>> tmpOperationDetails = config.getOperationDetails();
		Iterator<Map<String, Object>> tmpOperationIterator = tmpOperationDetails.iterator();
		HashMap<String, Object> detailsMap;
		while(tmpOperationIterator.hasNext())
		{
				detailsMap  = (HashMap<String, Object>)tmpOperationIterator.next();
			try{
	    		String operation = (String)detailsMap.get(OPERATION);
	    		if(operation!=null && operation.length()>0){

	    			String operationName = operation.substring(0, operation.indexOf('('));
		    		String[] operationArguments = operation.substring(operation.indexOf('(') + 1 ,operation.lastIndexOf(')')).split(",");
	
		    		String condition = (String)detailsMap.get(CONDITIONS);
		    		String conditionName="";
		    		String []conditionArguments=null;
		    		if(condition!=null && condition.length()>0){
		    			conditionName = condition.substring(0,condition.indexOf('('));
			    		conditionArguments = condition.substring(condition.indexOf('(') + 1, condition.lastIndexOf(')')).split(",");
			    	}
		    		tmpFunctionMap.put(operation, operationName);
		    		tmpArgumentMap.put(operation, operationArguments);
		    		tmpFunctionMap.put(condition, conditionName);
		    		tmpArgumentMap.put(condition, conditionArguments);
		    		operationDetails.add(detailsMap);
	    		}else{
	    			long vendorId = (Long) detailsMap.get(VENDOR_ID);
	        		int[] attribute_id = (int[])detailsMap.get(ATTRIBUTE_ID);
	    			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
	    				LogManager.getLogger().debug(MODULE, "No operation configured for attribute :"+Dictionary.getInstance().getAttributeName(vendorId,attribute_id));
	    		}
    		}catch (StringIndexOutOfBoundsException e) {
    			long vendorId = (Long) detailsMap.get(VENDOR_ID);
        		int[] attribute_id = (int[])detailsMap.get(ATTRIBUTE_ID);
        		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        			LogManager.getLogger().debug(MODULE, " Operation or Condition configured for Attribute: "+Dictionary.getInstance().getAttributeName(vendorId,attribute_id)+" is not proper, operation can't be apply for this attribute");
			}
			
			
		}
		this.functionMap = tmpFunctionMap;
		this.argumentMap = tmpArgumentMap;
		this.operationDetails = operationDetails;
		LogManager.getLogger().info(MODULE, "String Operation Plugin Initialized successfuly.");
		
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}
	
	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getPluginName());
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource("--");

		//FIXME: Implement reload.
		/*		
		try {
			StringOperationPluginConfImpl config = (StringOperationPluginConfImpl)getPluginConfiguration();
			if(config == null){
				cacheDetail.setResultCode(CacheConstants.FAIL);
				cacheDetail.setDescription("Failed, reason: String Plugin configuration is null");
			}else{
				config.readConfiguration();			
				this.init();
			}

		} catch (LoadConfigurationException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		} catch (InitializationFailedException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		}				
		return cacheDetail;
		 */
		return cacheDetail;
	}

}
