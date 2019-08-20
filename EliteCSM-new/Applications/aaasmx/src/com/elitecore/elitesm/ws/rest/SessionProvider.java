package com.elitecore.elitesm.ws.rest;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.rest.constant.RestWSConstants;
import com.elitecore.elitesm.ws.rest.utility.DBFieldList;
import com.elitecore.elitesm.ws.rest.utility.ListWrapper;
import com.elitecore.elitesm.ws.rest.utility.ResponseBuilder;
import com.elitecore.elitesm.ws.rest.utility.RestQueryBuilder;

public class SessionProvider {

	private MediaType mediaType;
	private final static RestSessionMgmtBlManager restSessionMgmtBlManager = new RestSessionMgmtBlManager();
	
	public SessionProvider() {
		this.mediaType = MediaType.APPLICATION_JSON_TYPE;
	}
	
	@GET
	public @ResponseBody Response getSession(@Context UriInfo uriInfo) {
		
		Response response = null;
		boolean isDebugEnable = false;
		ListWrapper<DBFieldList> sessionData;
		
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);
		
		if(queryParameters == null || queryParameters.isEmpty()) {
			String message = Response.Status.FORBIDDEN + " - " + Response.Status.FORBIDDEN.getStatusCode() + " ,Reason: No parameters were used for invocation of web service.";
			return responseHandler.generateResponseMessage(message);
		}
		
		try {
			RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters);

			responseHandler.setResponseMediaType(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();

			int offset = queryBuilder.getValueOfParameter(RestWSConstants.OFFSET);
			
			int limit = queryBuilder.getValueOfParameter(RestWSConstants.LIMIT);

			String whereClause = queryBuilder.getWhereClauseForQuery();

			String query = getQuery(whereClause);

			if(query == null) {
				String message = Response.Status.FORBIDDEN + " - " + Response.Status.FORBIDDEN.getStatusCode() +" ,Reason: No query/input parameter specify for invocation of web service.";
				return responseHandler.generateResponseMessage(message);
			}
			
			if(limit > 0 || offset > 0) {
				query = getLimitAndOffsetQuery(query,offset, limit);
				/**
				 * now onward all provided arguments used as where clause
				 * and if limit and offset is given then we will generate
				 * query using Hibernate.
				 * as hiberanate 3.X.X generates parameterized query
				 * and does not support to bind parameters directly.
				 * so limit and offset are passed and set by blmanager.
				 */
				sessionData = restSessionMgmtBlManager.getSessionData(query,limit,offset);
			}else {
				sessionData = restSessionMgmtBlManager.getSessionData(query,RestWSConstants.DEFAULT_LIMIT_PARAM_VALUE,RestWSConstants.DEFAULT_OFFSET_PARAM_VALUE);
			}
			
			return responseHandler.generateResponse(sessionData);
			
		} catch (SQLException e) {
			response = responseHandler.generateExceptionResponse(e, isDebugEnable);
		} catch (DatabaseConnectionException e) {
			response = responseHandler.generateExceptionResponse(e, isDebugEnable);
		}
		return response;
	}

	@GET
	@Path("/{findbyattribute}/")
	public Response getSession(@PathParam("findbyattribute") String pathParam, @Context UriInfo uriInfo) {
		
		Response response = null;
		boolean isDebugEnable = false;
		ListWrapper<DBFieldList> sessionData;
		ResponseBuilder responseHandler = new ResponseBuilder(mediaType);

		if(!RestWSConstants.FIND_BY_ATTRIBUTE.equals(pathParam)) {
			String message = Response.Status.EXPECTATION_FAILED + " - " + Response.Status.EXPECTATION_FAILED.getStatusCode() + " ,Reason: Invalid path parameter: "
						+ pathParam+" specify for invocation of find by attribute ws. ";
			return responseHandler.generateResponseMessage(message);
		}
		
		
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

		if(queryParameters == null || queryParameters.isEmpty()) {
			String message = Response.Status.FORBIDDEN + " - " + Response.Status.FORBIDDEN.getStatusCode() + " ,Reason: No input parameter specify for invocation of web service.";
			return responseHandler.generateResponseMessage(message);
		}
		
		try {
			RestQueryBuilder queryBuilder = new RestQueryBuilder(queryParameters, restSessionMgmtBlManager.getAttributeMap());
			
			responseHandler = new ResponseBuilder(queryBuilder.getFormatParameter());
			
			isDebugEnable = queryBuilder.isDebugEnable();

			int offset = queryBuilder.getValueOfParameter(RestWSConstants.OFFSET);
			
			int limit = queryBuilder.getValueOfParameter(RestWSConstants.LIMIT);

			String whereClause = queryBuilder.getWhereClauseForQuery();

			String query = getQuery(whereClause);

			if(query == null) {
				String message = Response.Status.FORBIDDEN + " ,Reason: No input parameter specify for invocation of web service";
				return responseHandler.generateResponseMessage(message);
			}
			
			if(limit > 0 || offset > 0) {
				query = getLimitAndOffsetQuery(query,offset, limit);
				sessionData = restSessionMgmtBlManager.getSessionData(query,limit,offset);
			}else{
				sessionData = restSessionMgmtBlManager.getSessionData(query,RestWSConstants.DEFAULT_LIMIT_PARAM_VALUE,RestWSConstants.DEFAULT_OFFSET_PARAM_VALUE);
			}
			
			return responseHandler.generateResponse(sessionData);
			
		} catch (SQLException e) {
			response = responseHandler.generateExceptionResponse(e,isDebugEnable);
		} catch (DatabaseConnectionException e) {
			response = responseHandler.generateExceptionResponse(e,isDebugEnable);
		}
		return response;
	}

	private String getQuery(String whereClause) {
		
		if(Strings.isNullOrBlank(whereClause)) {
			return null;
		}
		
		return restSessionMgmtBlManager.getSessionQuery()+" where " + whereClause;
	}
	
	/**
	 * generates the query for limit and offset.
	 * if both are positive value which is greater
	 * than 0, then it will generate parameterized
	 * query with 2 in parameters,limit and offset
	 * respectively,
	 * if offset <= 0 then only limit is applied if
	 * it is > 0.
	 * @param query
	 * @param offset
	 * @param limit
	 * @return
	 */
	private String getLimitAndOffsetQuery(String query,int offset, int limit) {
		SessionFactory session = HibernateSessionFactory.createSession().getSessionFactory();
		if(session instanceof SessionFactoryImpl) {
			Dialect dialect = ((SessionFactoryImpl) session).getDialect();
			query = dialect.getLimitString(query, offset, limit);
		}
		return query;
	}
}