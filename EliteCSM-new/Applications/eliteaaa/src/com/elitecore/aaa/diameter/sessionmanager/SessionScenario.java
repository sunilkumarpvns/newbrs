package com.elitecore.aaa.diameter.sessionmanager;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.radius.sessionx.data.FieldMappingParser;
import com.elitecore.aaa.radius.sessionx.data.ImproperSearchCriteriaException;
import com.elitecore.aaa.radius.sessionx.data.PropertyType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/***
 * 
 * @author malav.desai
 *
 */
class SessionScenario {
	private static final String MODULE = "SESSION-SCENARIO"; 
	private static final int OPERATION_FAILURE = -1;
	
	private final String scenarioName;
	private final FieldMappingParser parser;
	private final SessionFactory factory;
	private LogicalExpression logicalExpression;
	private final String tableName;
	private final List<String> criteriaParams;
	private final String multiValueDelimeter;
	private final String expressionString;
	private final String schemaName;
	
	SessionScenario(String scenarioName, FieldMappingParser parser, SessionFactory factory, String expressionString, String tableName, 
			String schemaName, List<String> criteriaParams, String multiValueDelimeter) {
		this.scenarioName = scenarioName;
		this.parser = parser;
		this.factory = factory;
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.criteriaParams = criteriaParams;
		this.expressionString = expressionString;
		this.multiValueDelimeter = multiValueDelimeter;
	}
	
	void init() throws InvalidExpressionException {
		this.logicalExpression = Compiler.getDefaultCompiler().parseLogicalExpression(expressionString.replaceAll("\\$", "\\\\\\$"));
	}
	
	String getExpressionString() {
		return this.expressionString;
	}
	
	boolean isApplicable(ValueProvider valueProvider) {
		return logicalExpression.evaluate(valueProvider);
	}

	List<SessionData> locate(ValueProvider valueProvider) throws ImproperSearchCriteriaException {
		Session session = factory.getSession();
		Criteria criteria = createCriteria(session, valueProvider);
		List<SessionData> receivedSessions = session.list(criteria);
		if (Collectionz.isNullOrEmpty(receivedSessions)) {
			return receivedSessions;
		}

		List<SessionData> sessionDatas = new ArrayList<SessionData>();
		for (SessionData sessionData : receivedSessions) {
			SessionData newSessionData = new SessionDataImpl(sessionData.getSchemaName(), 
					sessionData.getSessionId(), sessionData.getCreationTime(), sessionData.getLastUpdateTime());
			for (String key : sessionData.getKeySet()) {
				newSessionData.addValue(parser.getColumnByPropertyName(key), sessionData.getValue(key));
			}
			sessionDatas.add(newSessionData);
		}
		return sessionDatas;
	}
	
	private Criteria createCriteria(Session session, ValueProvider valueProvider) throws ImproperSearchCriteriaException {
		Criteria criteria = session.createCriteria(this.schemaName);
		List<String> missingAVPs = new ArrayList<String>();
		for (String element : criteriaParams) {
			for (PropertyType propertyType : parser.getPropertyListByColumn(element)) {
				try {
					criteria.add(Restrictions.eq(element, valueProvider.getStringValue(propertyType.getPropertyName())));
				} catch (InvalidTypeCastException e) {
					//this will never occur
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				} catch (MissingIdentifierException e) {
					missingAVPs.add(propertyType.getPropertyName());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				} 
			}
		}
		
		if (criteria.getCriterions().isEmpty()) {
			throw new ImproperSearchCriteriaException("ImproperSearchCriteriaException : Each Criteria avps configured in session scenario is either unavailable in request or empty.", missingAVPs);
		}
		return criteria;
	}

	int save(ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
		Session session = factory.getSession();
		SessionData sessionData = factory.createSessionData(this.schemaName);
		
		for (FieldMapping element : parser.getFieldMappings()) {
			String valueString = element.getDefaultValue();
			List<PropertyType> propertyTypes = parser.getPropertyListByProperties(element.getPropertyName());
			for (PropertyType propertyType : propertyTypes) {
				try {
					if(propertyType.getPacketType() == PropertyType.ACCESS_REQ) {
						valueString = Strings.join(multiValueDelimeter, requestValueProvider.getStringValues(propertyType.getPropertyName()));
					} else {
						valueString = Strings.join(multiValueDelimeter, responseValueProvider.getStringValues(propertyType.getPropertyName()));
					}
					break;
				} catch (InvalidTypeCastException e) {
					// this will never occur here
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().info(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				} catch (MissingIdentifierException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				}
			}
			sessionData.addValue(element.getPropertyName(), valueString);
		}
		return session.save(sessionData);
	}

	int update(ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
		Session session = factory.getSession();
		SessionData sessionData = factory.createSessionData(this.schemaName);
		
		for (FieldMapping element : parser.getFieldMappings()) {
			String valueString = null;
			List<PropertyType> properties = parser.getPropertyListByProperties(element.getPropertyName());
			for (PropertyType propertyType : properties) {
				try {
					if(propertyType.getPacketType() == PropertyType.ACCESS_REQ) {
						valueString = RadiusUtility.getDelimiterSeparatedString(requestValueProvider.getStringValues(propertyType.getPropertyName()), multiValueDelimeter);
					} else {
						valueString = RadiusUtility.getDelimiterSeparatedString(responseValueProvider.getStringValues(propertyType.getPropertyName()), multiValueDelimeter);
					}
					break;
				} catch (InvalidTypeCastException e) {
					// this will never occur here
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				} catch (MissingIdentifierException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Skipping mapping, Reason: " + e.getMessage());
					}
				}
			}
			if(valueString != null) {
				sessionData.addValue(element.getPropertyName(), valueString);
			}
		}
		try {
			return session.update(sessionData, createCriteria(session, requestValueProvider));
		} catch (ImproperSearchCriteriaException e) {
			LogManager.getLogger().warn(MODULE,"Update Operation Failed : Reason : " + e.getMessage());
			return OPERATION_FAILURE;
		}
	}

	int delete(List<SessionData> sessionDatas) {
		Session session = factory.getSession();
		int result = 0;
		for (SessionData sessionData : sessionDatas) {
			int deleteData = session.delete(sessionData);
			if(deleteData != OPERATION_FAILURE) {
				result +=deleteData;
			}
		}
		return result;
	}
	
	int delete(ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
		Session session = factory.getSession();
		Criteria criteria;
		try {
			criteria = createCriteria(session, requestValueProvider);
			return session.delete(criteria);
		} catch (ImproperSearchCriteriaException e) {
			LogManager.getLogger().warn(MODULE,"Delete Operation Failed : Reason : " + e.getMessage());
			return OPERATION_FAILURE;
		}
	}

	int truncate() {
		return factory.getSession().truncate(this.tableName);
	}
	
	String getScenarioName() {
		return this.scenarioName;
	}
}