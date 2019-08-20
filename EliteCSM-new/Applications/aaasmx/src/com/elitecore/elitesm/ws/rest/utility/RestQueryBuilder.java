package com.elitecore.elitesm.ws.rest.utility;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.rest.constant.RestWSConstants;

public class RestQueryBuilder {

	private static final String MODULE = "REST-UTILITY";
	protected final MultivaluedMap<String, String> queryParameters;
	private static final String ORDER_BY = " order by ";
	private static final String AND_OPERATOR = " and ";
	private static final String LIKE_OPERATOR = " like ";
	private static final String EQUAL_OPERATOR = " = ";
	private static final String PREFIX_ID_FOR_LIKE_OPERATOR = "%";
	private Map<String, String> fieldMappingMap;

	public RestQueryBuilder(MultivaluedMap<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}

	
	public RestQueryBuilder(MultivaluedMap<String, String> queryParameters, Map<String, String> fieldMappingMap) {
		this.queryParameters = queryParameters;
		this.fieldMappingMap = fieldMappingMap;
	}

	public MediaType getFormatParameter() {
		List<String> list = queryParameters.remove(RestWSConstants.FORMAT);
		
		if(Collectionz.isNullOrEmpty(list) == false) {
			if(RestWSConstants.FORMAT_XML.equals(list.get(0))) {
				return MediaType.APPLICATION_XML_TYPE;
			} else if(RestWSConstants.FORMAT_JSON.equals(list.get(0))){
				return MediaType.APPLICATION_JSON_TYPE;
			} else {
				return new MediaType(list.get(0),list.get(0));
			}
		}
		return MediaType.APPLICATION_JSON_TYPE;
	}
	
	public boolean isDebugEnable() {
		
		if(this.queryParameters.containsKey(RestWSConstants.DEBUG) == false) {
			return false;
		}

		List<String> debugParameter = queryParameters.remove(RestWSConstants.DEBUG);
		return RestWSConstants.DEBUG_TRUE.equals(debugParameter.get(0));
	}

	public int getValueOfParameter(String parameterName) {
		if(this.queryParameters.containsKey(parameterName) == false) {
			return 0;
		}
		
		List<String> removeList = this.queryParameters.remove(parameterName);
		String parameterValue = removeList.get(0);

		if(Strings.isNullOrBlank(parameterValue) == true) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "No value specify for the parameters: "+parameterName+", so taking 0 as default value for parameter: "+parameterName);
			}
			return 0;
		}
		
		try {
			return Integer.parseInt(parameterValue);
		}catch (NumberFormatException nfe) {
			if(Logger.getLogger().isLogLevel(LogLevel.ERROR)) {
				Logger.logError(MODULE, "Invalid value is specify for the parameters: "+parameterName+"="+parameterValue+ ", so taking 0 as default value for parameter: "+parameterName);
			}
			Logger.logTrace(MODULE, nfe);
			return 0;
		}
	}

	public String getWhereClauseForSubscriber() {
		
		StringBuilder whereClause = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);
		
		String orderByClause = null;
		
		int noOfParameters = 0;
		
		orderByClause = getOrderByClause();

		for (Entry<String, List<String>> queryParams : this.queryParameters.entrySet()) {

			String column = queryParams.getKey();
			
			if(Strings.isNullOrBlank(column) == true) {
				noOfParameters++;
				continue;
			} 
			
			if (fieldMappingMap != null && fieldMappingMap.size() > 0) {
				String fieldMappingColumn = fieldMappingMap.get(column);
				if (fieldMappingColumn != null){
					column = fieldMappingColumn;
				}
			}
			
			List<String> columnValueList = queryParams.getValue();
			
			if(columnValueList != null) {
				
				SubscriberQuery subscriberQuery = new SubscriberQuery();
				
				int size = columnValueList.size();
				if(size == 1) {
					String value = columnValueList.get(0);
					
					subscriberQuery.setColumnName(column);
					subscriberQuery.setColumnValue(value);
					
					SubscriberQuery query = checkForCaseSensitivity(subscriberQuery,whereClause);
					if(query.isCaseSensitivity()){
						whereClause.append(query.getColumnValue());
					}else{
						whereClause.append("'").append(columnValueList.get(0)).append("'");
					}
					
				}else {
					/**
					 * if same query parameters are specify 
					 * more than once than each value is anded
					 * with all values.
					 * 
					 * e.g
					 * ..../url/?username=aaa&username=aaa1
					 * 
					 * than queryparmaters are in form of
					 * key = username ,value = [aaa,aaa1]
					 */
					for (int paramIndex = 0 ; paramIndex < size ;  paramIndex ++) {
						
						if(paramIndex == size-1){
							String string = columnValueList.get(paramIndex);
							
							subscriberQuery.setColumnName(column);
							subscriberQuery.setColumnValue(string);
							
							SubscriberQuery query = checkForCaseSensitivity(subscriberQuery,whereClause);
							
							if(query.isCaseSensitivity()){
								whereClause.append(string);
							}else {
								whereClause.append("'").append(columnValueList.get(paramIndex)).append("'");
							}
						}else{
							String string = columnValueList.get(paramIndex);
							
							subscriberQuery.setColumnName(column);
							subscriberQuery.setColumnValue(string);
							
							SubscriberQuery query = checkForCaseSensitivity(subscriberQuery,whereClause);

							if(query.isCaseSensitivity()){
								whereClause.append(string);
							}else{
								whereClause.append("'").append(columnValueList.get(paramIndex)).append("'").append(AND_OPERATOR);
							}
						}
					}
				}
			}
			
			/**
			 * if multiple query parameters are given
			 * than all are combined using and operator.
			 * so this check is required to make the queries.
			 */
			if(this.queryParameters.size() > 1 && whereClause.length() > 0) {
				if(noOfParameters != this.queryParameters.size() - 1)  {
					whereClause.append(AND_OPERATOR);
				}
			}
			/**
			 * specify the number of query parameters
			 * increment on per iteration
			 */
			noOfParameters++;
		}
		
		if(orderByClause != null) {
			whereClause.append(orderByClause);
		}
		return whereClause.toString();
	}
	
	private SubscriberQuery checkForCaseSensitivity(SubscriberQuery query, StringBuilder whereClause) {
		
		String column = null;
		String value = null;
		
		String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
		
		if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
			column = "lower("+query.getColumnName()+")";
			value = "lower('"+query.getColumnValue()+"')";
			query.setCaseSensitivity(true);
		} else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
			column = "upper("+query.getColumnName()+")";
			value = "upper('"+query.getColumnValue()+"')";
			query.setCaseSensitivity(true);
		} else {
			column = query.getColumnName();
			value = query.getColumnValue();
		}
		
		if(value.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
			whereClause.append(column).append(LIKE_OPERATOR);
		}else {
			whereClause.append(column).append(EQUAL_OPERATOR);
		}
		
		query.setColumnName(column);
		query.setColumnValue(value);
		
		return query;
	}


	public String getWhereClauseForQuery() {
		
		StringBuilder whereClause = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);
		
		String orderByClause = null;
		
		int noOfParameters = 0;
		
		orderByClause = getOrderByClause();

		for (Entry<String, List<String>> queryParams : this.queryParameters.entrySet()) {

			String column = queryParams.getKey();
			
			if(Strings.isNullOrBlank(column) == true) {
				noOfParameters++;
				continue;
			} 
			
			if (fieldMappingMap != null && fieldMappingMap.size() > 0) {
				String fieldMappingColumn = fieldMappingMap.get(column);
				if (fieldMappingColumn != null){
					column = fieldMappingColumn;
				}
			}
			
			List<String> columnValueList = queryParams.getValue();
			
			if(columnValueList != null) {
				int size = columnValueList.size();
				if(size == 1) {
					String value = columnValueList.get(0);

					if(value.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
						whereClause.append(column).append(LIKE_OPERATOR);
					}else {
						whereClause.append(column).append(EQUAL_OPERATOR);
					}
					whereClause.append("'").append(columnValueList.get(0)).append("'");
					
				}else {
					/**
					 * if same query parameters are specify 
					 * more than once than each value is anded
					 * with all values.
					 * 
					 * e.g
					 * ..../url/?username=aaa&username=aaa1
					 * 
					 * than queryparmaters are in form of
					 * key = username ,value = [aaa,aaa1]
					 */
					for (int paramIndex = 0 ; paramIndex < size ;  paramIndex ++) {
						if(paramIndex == size-1){
							String string = columnValueList.get(paramIndex);
							if(string.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
								whereClause.append(column).append(LIKE_OPERATOR);
							}else {
								whereClause.append(column).append(EQUAL_OPERATOR);
							}
							whereClause.append("'").append(columnValueList.get(paramIndex)).append("'");
						}else{
							String string = columnValueList.get(paramIndex);
							if(string.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
								whereClause.append(column).append(LIKE_OPERATOR);
							}else {
								whereClause.append(column).append(EQUAL_OPERATOR);
							}
							whereClause.append("'").append(columnValueList.get(paramIndex)).append("'").append(AND_OPERATOR);
						}
					}
				}
			}
			
			/**
			 * if multiple query parameters are given
			 * than all are combined using and operator.
			 * so this check is required to make the queries.
			 */
			if(this.queryParameters.size() > 1 && whereClause.length() > 0) {
				if(noOfParameters != this.queryParameters.size() - 1)  {
					whereClause.append(AND_OPERATOR);
				}
			}
			/**
			 * specify the number of query parameters
			 * increment on per iteration
			 */
			noOfParameters++;
		}
		
		if(orderByClause != null) {
			whereClause.append(orderByClause);
		}
		return whereClause.toString();
	}
	
	public String getWhereClauseForQuery(Map<String, String> attributeFieldMap) {

		StringBuilder whereClause = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);
		String orderByClause = "";

		int noOfParameters = 0;

		orderByClause = getOrderByClause();

		for (Entry<String, List<String>> queryParams : this.queryParameters.entrySet()) {

			String column = queryParams.getKey();

			if(Strings.isNullOrBlank(column) == true) {
				noOfParameters++;
				continue;
			}
			
			if(!attributeFieldMap.isEmpty() && attributeFieldMap.containsKey(column)) {
				column = attributeFieldMap.get(column);
			}

			List<String> value = queryParams.getValue();

			if(value != null){
				int size = value.size();
				if(size == 1){
					String string = value.get(0);
					if(string.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
						whereClause.append(column).append(LIKE_OPERATOR);
					}else{
						whereClause.append(column).append(EQUAL_OPERATOR);
					}
					whereClause.append("'").append(value.get(0)).append("'");
				}else{
					for (int parameterIndex = 0;parameterIndex < size ;  parameterIndex ++) {
						if(parameterIndex == size-1) {
							String string = value.get(parameterIndex);
							if(string.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
								whereClause.append(column).append(LIKE_OPERATOR);
							}else{
								whereClause.append(column).append(EQUAL_OPERATOR);
							}
							whereClause.append("'").append(value.get(parameterIndex)).append("'");
						}else{
							String string = value.get(parameterIndex);
							if(string.startsWith(PREFIX_ID_FOR_LIKE_OPERATOR)) {
								whereClause.append(column).append(LIKE_OPERATOR);
							}else{
								whereClause.append(column).append(EQUAL_OPERATOR);
							}
							whereClause.append("'").append(value.get(parameterIndex)).append("'").append(AND_OPERATOR);
						}
					}
				}
			}

			if(this.queryParameters.size() > 1 && whereClause.length() > 0) {
				if(noOfParameters != this.queryParameters.size() - 1) {
					whereClause.append(AND_OPERATOR);
				}
			}
			noOfParameters++;
		}

		if(orderByClause != null && orderByClause.trim().length() > 0) {
			whereClause.append(orderByClause);
		}
		return whereClause.toString();
	}

	private String getOrderByClause() {
		String orderByClause = null;
		
		if(this.queryParameters.containsKey(RestWSConstants.SORT) == false) {
			return orderByClause;
		}
			
		List<String> orderBy = this.queryParameters.remove(RestWSConstants.SORT);
		String orderByColumn = orderBy.get(0);

		if(orderByColumn.trim().length() == 0) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "Invalid use of sort operation. Reason: Must require column name to perform sorting.");
			}
			return orderByClause;
		}

		orderByClause = ORDER_BY;
		if(orderByColumn.startsWith("-")) {
			return orderByClause += orderByColumn.substring(1) + " desc";
		}else {
			return orderByClause += orderByColumn;
		}
	}
}