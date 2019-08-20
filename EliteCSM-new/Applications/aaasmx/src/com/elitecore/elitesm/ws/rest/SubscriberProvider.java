package com.elitecore.elitesm.ws.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.elitecore.commons.base.Collectionz;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.rest.constant.RestWSConstants;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.DBField;
import com.elitecore.elitesm.ws.rest.utility.DBFieldList;
import com.elitecore.elitesm.ws.rest.utility.ListWrapper;
import com.elitecore.elitesm.ws.rest.utility.ResponseBuilder;
import com.elitecore.elitesm.ws.rest.utility.RestQueryBuilder;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;

public class SubscriberProvider {
	
	private static final String MODULE = "REST-PROVISION-WS";
	private final static RestSubscriberBlManager restBlManager = new RestSubscriberBlManager();
	private MediaType mediaType;
	private String userIdentity = null;
	
	public SubscriberProvider() {
		mediaType = MediaType.APPLICATION_JSON_TYPE;
	}

	@GET
	public @ResponseBody Response getSubscriber(@Context UriInfo uriInfo) {

		Response response = null;
		boolean isDebugEnable = false;
		ListWrapper<DBFieldList> subscriber;
		
		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);
		
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
		
		if(queryParameters == null || queryParameters.isEmpty()) {
			String message = Response.Status.FORBIDDEN + " - " + Response.Status.FORBIDDEN.getStatusCode() +" ,Reason: No input parameter specify for invocation of web service.";
			return responseHandler.generateResponseMessage(message);
		}
		
		String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
		for (Entry<String, List<String>> element : queryParameters.entrySet()) {
			if(element.getKey().equals("USER_IDENTITY")){
				if(Strings.isNullOrBlank(element.getValue().get(0)) == false){
					userIdentity = element.getValue().get(0);
					if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
						element.getValue().set(0,element.getValue().get(0).toLowerCase());
					}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
						element.getValue().set(0,element.getValue().get(0).toUpperCase());
					}
				}
			}
		}
		RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters, restBlManager.getDbConfiguration().getWsRequestFieldMap());
		
		try {
						
			responseHandler.setResponseMediaType(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();

			int offset = queryBuilder.getValueOfParameter(RestWSConstants.OFFSET);
			
			int limit = queryBuilder.getValueOfParameter(RestWSConstants.LIMIT);
			
			String whereClause = queryBuilder.getWhereClauseForSubscriber();

			String query = restBlManager.generateSelectQuery(whereClause);

			if(Strings.isNullOrBlank(query)) {
				String message = Response.Status.FORBIDDEN + "-" + Response.Status.FORBIDDEN.getStatusCode() + " ,Reason: Response mapping is not provided for subscriber, so returning null.";
				return responseHandler.generateResponseMessage(message);
			}

			if(limit > 0 || offset > 0) {
				query = getLimitAndOffsetQuery(query,offset, limit);
			}
			
			subscriber = restBlManager.getResult(query,limit,offset);

			if(subscriber != null){
				for(DBFieldList dbFieldList : subscriber.getDatalist()){
					for(DBField dbField: dbFieldList.getDbFieldList()){
						if(dbField.getColumn().equalsIgnoreCase("USER_IDENTITY")){
							if(Strings.isNullOrEmpty(userIdentity) == false){
								dbField.setValue(userIdentity);
							}
						}
					}
				}
			}else{
				return responseHandler.generateResponseMessage("No Subscriber found with the name " + userIdentity);
			}

			return responseHandler.generateResponse(subscriber);
			
		} catch (SQLException e) {
			response = responseHandler.generateExceptionResponse(e,isDebugEnable);
		} catch (DatabaseConnectionException e) {
			response = responseHandler.generateExceptionResponse(e,isDebugEnable);
		}
		return response;
	}
	
	@DELETE
	public Response deleteSubscriber(@Context UriInfo uriInfo) {
		
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
		
		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);
		
		if(queryParameters == null || queryParameters.isEmpty()) {
			String message = Response.Status.FORBIDDEN + " - " + Response.Status.FORBIDDEN.getStatusCode() +" ,Reason: No input parameter specify for invocation of web service.";
			return responseHandler.generateResponseMessage(message);
		}

		boolean isDebugEnable = false;

		try {
		
			RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters, restBlManager.getDbConfiguration().getWsRequestFieldMap());
			
			responseHandler.setResponseMediaType(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();
			
			String whereClause = queryBuilder.getWhereClauseForSubscriber();
			
			String deleteQuery = restBlManager.getDeleteQuery(whereClause);
			
			if(Strings.isNullOrBlank(deleteQuery)) {
				String message = Response.Status.FORBIDDEN + "-" + Response.Status.FORBIDDEN.getStatusCode() + " ,Reason: where clause does not provided for delete operation.";
				return responseHandler.generateResponseMessage(message);
			}
			
			int deleteCount = restBlManager.delSubscriber(deleteQuery);
			
			String message = "Total numbers of record deleted are: "+deleteCount;
			Logger.logDebug(MODULE, message);
			return responseHandler.generateResponseMessage(message);
		
		} catch (SQLException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		} catch (DatabaseConnectionException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		}
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response updateSubscriber(DBFieldList subScriberDetail,@Context UriInfo uriInfo,@Context HttpServletRequest request) {

		int updateSubscriberCount = 0;
		boolean isDebugEnable = false;
		
		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);
		
		try {
			
			MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

			RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters, restBlManager.getDbConfiguration().getWsRequestFieldMap());

			boolean isRequestValid = validateRequestMediaType(queryBuilder.getFormatParameter(), request);
			if(isRequestValid == false){
				String message = Response.Status.BAD_REQUEST +"-"+ Response.Status.BAD_REQUEST.getStatusCode() +" ,Reason: Format is mismatched with the request content type.";
				return responseHandler.generateResponseMessage(message);
			}
			
			String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
			for (Entry<String, List<String>> element : queryParameters.entrySet()) {
				if(element.getKey().equals("USER_IDENTITY")){
					if(Strings.isNullOrBlank(element.getValue().get(0)) == false){
						userIdentity = element.getValue().get(0);
						if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
							element.getValue().set(0,element.getValue().get(0).toLowerCase());
						}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
							element.getValue().set(0,element.getValue().get(0).toUpperCase());
						}
					}
				}
			}
			
			for (DBField dbField : subScriberDetail.getDbFieldList()) {
				if(dbField.getColumn().equalsIgnoreCase("USER_IDENTITY")){
					if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
						dbField.setValue(dbField.getValue().toLowerCase().trim());
					}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
						dbField.setValue(dbField.getValue().toUpperCase().trim());
					}
				}
			}
			
			responseHandler.setResponseMediaType(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();
			
			String whereClauseForUpdateQuery = queryBuilder.getWhereClauseForSubscriber();

			Map<String, Object> subScriberDetailMap = convertToMapFromDBFieldList(subScriberDetail);

			updateSubscriberCount = restBlManager.updateSubscriber(subScriberDetailMap, whereClauseForUpdateQuery);
			
			String message = "Total numbers of record updated are: "+updateSubscriberCount;
			Logger.logDebug(MODULE, message);
			
			return responseHandler.generateResponseMessage(message);
		
		} catch (SQLException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		} catch (DatabaseConnectionException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		}
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response addSubscriber(ListWrapper<DBFieldList> subScriberDetail,@Context UriInfo uriInfo,@Context HttpServletRequest request) throws ConstraintViolationException {

		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);
		
		
		int addedSubscribersCount = 0;
		
		boolean isDebugEnable = false;
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
		
		try {
			RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters);
			
			boolean isRequestValid = validateRequestMediaType(queryBuilder.getFormatParameter(), request);
			
			if(isRequestValid == false) {
				String message = Response.Status.BAD_REQUEST +"-"+ Response.Status.BAD_REQUEST.getStatusCode() +" ,Reason: Format is mismatched with the request content type.";
				return responseHandler.generateResponseMessage(message);
			}
			
			SubscriberProfileWebServiceBLManager subscriberManager = new SubscriberProfileWebServiceBLManager();
			
			for (DBFieldList dbFields : subScriberDetail.getDatalist()) {
				for (DBField dbField : dbFields.getDbFieldList()) {
					if(dbField.getColumn().equalsIgnoreCase("USER_IDENTITY")){
						if(Maps.isNullOrEmpty(subscriberManager.findByUserIdentity(dbField.getValue().toLowerCase())) == false){
							return Response.ok(RestUtitlity.getResponse("Duplicate name found",ResultCode.ALREADY_EXIST)).build();
						}
						String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
						
						if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
							dbField.setValue(dbField.getValue().toLowerCase());
						}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
							dbField.setValue(dbField.getValue().toUpperCase());
						}
					}
				}
			}
			
			responseHandler.setResponseMediaType(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();
			
			List<DBFieldList> listDBFieldList = subScriberDetail.getDatalist();
			
			List<Map<String, Object>> subScriberDetailMap = convertToMapFromListDBFieldList(listDBFieldList);
			for (Map<String, Object> subscriberLogicalFieldToValue : subScriberDetailMap) {
				addedSubscribersCount += restBlManager.addSubscriber(subscriberLogicalFieldToValue);
			}
			
			String message = "Total numbers of record inserted are: "+addedSubscribersCount;
			Logger.logDebug(MODULE, message);
			
			return responseHandler.generateResponseMessage(message);

		} catch (SubscriberProfileWebServiceException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable );
		} catch (SQLException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		} catch (DatabaseConnectionException e) {
			return responseHandler.generateExceptionResponse(e, isDebugEnable);
		}
	}
	
	private String getLimitAndOffsetQuery(String query, int offset, int limit) {
		SessionFactory session = HibernateSessionFactory.createSession().getSessionFactory();
		if(session instanceof SessionFactoryImpl) {
			Dialect dialect = ((SessionFactoryImpl) session).getDialect();
			query = dialect.getLimitString(query, offset, limit);
		}
		return query;
	}
	
	private boolean validateRequestMediaType(MediaType mediaType, HttpServletRequest request) {
		Logger.logInfo(MODULE, "Request Type: " + request.getContentType());
		
		if(Strings.isNullOrEmpty(request.getContentType())) {
			if(Logger.getLogger().isLogLevel(LogLevel.WARN)) {
				Logger.logWarn(MODULE,"Content-Type received was null, so request will not process.");
			}
			return false;
		}
		
		if(request.getContentType().toLowerCase().contains(mediaType.toString().toLowerCase()) == false) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE,"Format: "+mediaType+ " is mismatched with the request content type:" + request.getContentType());
			}
			return false;
		}
		return true;
	}

	private List<Map<String, Object>> convertToMapFromListDBFieldList(List<DBFieldList> subscriberProfileData) {
		List<Map<String, Object>> subscribersDataList = new ArrayList<Map<String,Object>>();
		
		for (DBFieldList dbFieldList : subscriberProfileData) {
			List<DBField> dbFieldsList = dbFieldList.getDbFieldList();
			Map<String, Object> subscriberProfileDataMap = new HashMap<String, Object>();
			for (DBField dbField : dbFieldsList) {
				subscriberProfileDataMap.put(dbField.getColumn(), dbField.getValue());
			}
			subscribersDataList.add(subscriberProfileDataMap);
		}
		return subscribersDataList;
	}
	
	/**
	 * Used for Update Subscriber Operation.
	 * @param subscriberProfileData
	 */
	private Map<String, Object> convertToMapFromDBFieldList(DBFieldList subscriberProfileData) {
		Map<String, Object> subscriberProfileDataMap = new HashMap<String, Object>();
		for (DBField dbField : subscriberProfileData.getDbFieldList()) {
			subscriberProfileDataMap.put(dbField.getColumn(), dbField.getValue());
		}
		return subscriberProfileDataMap;
	}
}