package com.elitecore.core.driverx.cdr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.driverx.DateExpression;
import com.elitecore.core.driverx.ExpressionFactory;
import com.elitecore.core.driverx.ValueExpression;
import com.elitecore.core.driverx.ValueProviderExt;

public class CSVPathAllocatorImpl implements CSVPathAllocator {

	private static final String MODULE = "CSV-PATH-ALLOCATOR";
	private final CSVPathAllocatorParams csvPathAllocatorParams;
	private final List<ValueExpression> basePathExprs;
	private final ValueExpression folderExpression;
	//FIXME change to instance variable
	private static  Map<ValueExpression,String> exprToExprSignature = new IdentityHashMap<ValueExpression, String>();
	
	public CSVPathAllocatorImpl(CSVPathAllocatorParams csvPathAllocatorParams,List<ValueExpression> basePathExprs, ValueExpression folderExpression){
		this.csvPathAllocatorParams = csvPathAllocatorParams;
		this.basePathExprs = basePathExprs;
		this.folderExpression = folderExpression;
	}
	
	@Override
	public String getPath(ValueProviderExt valueProvider) {
		String strPath = File.separator;
		strPath = strPath + getFileLocationPath(valueProvider);
		String defaultDirName = csvPathAllocatorParams.getDefaultDirectoryName();
		defaultDirName = (Strings.isNullOrEmpty(defaultDirName)==false) ? csvPathAllocatorParams.getDefaultDirectoryName(): ""; 
		if(folderExpression != null) {
			String value = folderExpression.getValue(valueProvider);
			if(value == null){
				strPath = strPath + defaultDirName ;
			} else {
				strPath = strPath + value ;
			}
		} else {
			strPath = strPath + defaultDirName ;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Generated Path is " + strPath);
		}
		return strPath;
	}

	
	private String getFileLocationPath(ValueProviderExt valueProvider) {
		String fileLocationPath = "";
		
		
		if(Collectionz.isNullOrEmpty(basePathExprs) == false){
			for(ValueExpression avpExpression : basePathExprs){
				String avpValue = avpExpression.getValue(valueProvider);
				if(Strings.isNullOrEmpty(avpValue) == false){
					fileLocationPath =  fileLocationPath + avpValue + File.separator ;

				}else{
					fileLocationPath = fileLocationPath + "NO_" +exprToExprSignature.get(avpExpression) + File.separator;
				}
			}
		}else{
			fileLocationPath = getBasePath();
		}
		return fileLocationPath;
	}

	public static CSVPathAllocator create(CSVPathAllocatorParams csvPathAllocatorParams, ExpressionFactory expressionFactory) throws Exception{
		List<ValueExpression> expressions = parseExpression(csvPathAllocatorParams.getFileLocation(), expressionFactory); 
		ValueExpression folderExpression =  expressionFactory.newInstance(csvPathAllocatorParams.getDirectoryName());
		return new CSVPathAllocatorImpl(csvPathAllocatorParams, expressions,folderExpression);
	}


	private static List<ValueExpression> parseExpression(String identifier, ExpressionFactory expressionFactory) throws Exception {
		String delimiter = File.separator;
		List<ValueExpression> lstAVPValues = new ArrayList<ValueExpression>();
		if(identifier == null){
			return Collections.emptyList();
		}

		if(identifier.contains(delimiter)){
			String[] lstAVP = identifier.split(delimiter);
			if(lstAVP.length <= 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Invalid identifier " + identifier+" is passed in File Location parameter");
				}
				throw new IllegalArgumentException("Invalid identifier passed" + identifier);
			}
			int temp=0;
			if(identifier.startsWith(File.separator)){
				temp=1;
			}
			for(int i=temp;i<lstAVP.length;i++){
				lstAVPValues.add(createExpresssion(lstAVP[i], expressionFactory));
			}
		
		}else {
			lstAVPValues.add(createExpresssion(identifier,expressionFactory));
		}
		return lstAVPValues;
	
	}
	
	
	private static ValueExpression createExpresssion(String identifier,ExpressionFactory expressionFactory) throws Exception{
		if(identifier.startsWith("{") && identifier.endsWith("}")){
			String strAVP = identifier.trim();
			if(strAVP.startsWith("{date:")){
				return new DateExpression(strAVP.substring(strAVP.indexOf(":")+1, strAVP.indexOf("}")));
			}else{
				String key = strAVP.substring(strAVP.indexOf("{")+1, strAVP.indexOf("}"));
				ValueExpression valueExpression = expressionFactory.newInstance(key);
				exprToExprSignature.put(valueExpression, key);
				return valueExpression;
			}
		}else{
			return expressionFactory.newInstance("\"" + identifier + "\"");
		}
	}

	@Override
	public String getBasePath() {
		return csvPathAllocatorParams.getFileLocation();
	}


}
