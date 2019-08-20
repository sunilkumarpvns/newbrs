package com.elitecore.aaa.exprlib.function.diameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractStringFunctionExpression;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetFirst;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetLast;

/**
 * Fetches the located {@link SessionData} for a diameter request.<br>
 * 
 * When it is called with one argument, that argument is considered to be the column name whose value will
 * be fetched from all sessions. 
 * 
 * <p><b>Usage:</b>
 * <code>dbsession("DBCOLUMN")</code> the column name can be passed as literal value if already known.
 * <br/>
 * <code>dbsession(0:25)</code> the column name can be fetched from an identifier if dynamic.
 * <br/>
 * <code>dbsession("*")</code> returns the count of located sessions.
 * 
 * <p>When it is called with two arguments, the first argument is considered to be column name and second argument
 * can only be where function. Expression inside where function is treated as selector expression. All the sessions
 * for which that expression matches are selected and the value of column name, first argument; is fetched from
 * all selected sessions. 
 * 
 * <p><b>Usage:</b>
 * <code>dbsession("DBCOLUMN", where("CALLING_STATION_ID"=0:31))</code> returns the value of column DBCOLUMN for all the
 * sessions that match with where expression.
 * <br/>
 * <code>dbsession("*", where("CALLING_STATION_ID"=0:31))</code> returns the count of sessions that match with where
 * expression.
 * 
 * <p>It can also be composed with function {@link FunctionGetFirst getFirst(dbsession(dbcolumn, where(condition)))} or
 * {@link FunctionGetLast getLast(dbsession(dbcolumn, where(condition)))} which will return first and last value from 
 * result of dbsession function.</p>
 * 
 * @author khushbu.chauhan
 *
 */
public class FunctionDBSession extends AbstractStringFunctionExpression {

	private static final int WITH_SELECTOR = 2;
	private static final int WITHOUT_SELECTOR = 1;
	private final static String COUNT_TOKEN = "*";

	@Override
	public String getName() {
		return "dbsession";
	}

	
	/** @param columnName dbcolumn name from where data are extracted. 
	 * 	@param selector Any logical expression passed in {@link FunctionWhere#getStringValue(ValueProvider) where(condition)}.
	 *  @return value of <code>dbcolumn</code> or count depending upon how it is called. 
	 *  @exception InvalidTypeCastException if second argument is other than where(condition)				
	 *  @exception IllegalArgumentException if Number of arguments is less than one or more than two
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getStringValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {

		List<SessionData> sessions = (List<SessionData>) valueProvider.getValue(DiameterRequest.LOCATED_SESSION_DATA);

		if (Collectionz.isNullOrEmpty(sessions)) {
			throw new MissingIdentifierException("No session found");
		}

		switch (argumentList.size()) {
		case WITHOUT_SELECTOR:
			return fetchSessionWithoutSelector(valueProvider, sessions);
		case WITH_SELECTOR:
			return fetchSessionsWithSelector(valueProvider, sessions);
		default:
			throw new IllegalArgumentException(
					"Number of arguments mismatch, DBSESSION function must have atleast 1 and atmost 2 arguments");
		}
	}

	private List<String> fetchSessionsWithSelector(ValueProvider valueProvider,
			List<SessionData> sessionDatas)
					throws InvalidTypeCastException, IllegalArgumentException,
					MissingIdentifierException {

		if (argumentList.get(1).getExpressionType() != Expression.FunctionExpression ||
				argumentList.get(1).getName().equals("where")== false) {
			throw new InvalidTypeCastException("Second Argument must be where function");
		}

		String	columnName = argumentList.get(0).getStringValue(valueProvider);
		String selectedIndexes = argumentList.get(1).getStringValue(valueProvider);

		if (selectedIndexes.isEmpty()) {
			throw new MissingIdentifierException("No session found with matching expression");
		}
		
		List<String> dbSessionResult = null;
		String [] selectedIndexesArr = FunctionWhere.splitIndexes(selectedIndexes);
		if (COUNT_TOKEN.equals(columnName) == true) {
			dbSessionResult = Arrays.asList(String.valueOf(selectedIndexesArr.length));
		} else {
			dbSessionResult = new ArrayList<String>(selectedIndexesArr.length);
			for (String record : selectedIndexesArr) {
				String data = sessionDatas.get(Integer.valueOf(record)).getValue(columnName);
				if (data != null) {
					dbSessionResult.add(data);
				}
			}
			
			if (Collectionz.isNullOrEmpty(dbSessionResult)) {
				throw new MissingIdentifierException("Column name is invalid or not mapped: " + columnName);
			}
		}
		return dbSessionResult;
	}

	private List<String> fetchSessionWithoutSelector(
			ValueProvider valueProvider, List<SessionData> sessionDatas)
					throws InvalidTypeCastException, IllegalArgumentException,
					MissingIdentifierException {

		String columnName = argumentList.get(0).getStringValue(valueProvider);
		List<String> dbSessionResult;
		if (COUNT_TOKEN.equals(columnName)) {
			dbSessionResult = Arrays.asList(String.valueOf(sessionDatas.size()));
		} else {
			dbSessionResult = new ArrayList<String>(sessionDatas.size());
			for (SessionData record : sessionDatas) {
				String data = record.getValue(columnName);
				if (data != null) {
					dbSessionResult.add(data);
				}
			}
			if (Collectionz.isNullOrEmpty(dbSessionResult)) {
				throw new MissingIdentifierException("Column name is invalid or not mapped: " + columnName);
			}
		}
		return dbSessionResult;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		return getStringValues(valueProvider).get(0);
	}
	
	
	public static class FunctionWhere extends AbstractStringFunctionExpression {

		private static final String MULTIPLE_RECORD_IDX_SEPARATOR = ",";

		@Override
		public String getName() {
			return "where";
		}

		private static String[] splitIndexes(String selectedIndexes) {
			return selectedIndexes.split(MULTIPLE_RECORD_IDX_SEPARATOR);
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getStringValue(ValueProvider valueProvider)
				throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
			
			List<SessionData> sessionDatas = (List<SessionData>) valueProvider.getValue(DiameterRequest.LOCATED_SESSION_DATA);	

			LogicalExpression expression = (LogicalExpression) argumentList.get(0);

			int index = 0;
			StringBuilder selectedRows = new StringBuilder();
			for (SessionData data : sessionDatas) {
				ValueProvider provider = new ValueProviderImpl(valueProvider, data);
				if (expression.evaluate(provider)) {
					selectedRows.append(index).append(MULTIPLE_RECORD_IDX_SEPARATOR);				
				} 
				index++;	
			}
			return selectedRows.toString();
		}


		private static class ValueProviderImpl implements ValueProvider {

			private ValueProvider userValueProvider;
			private SessionData sessionDatas;

			public ValueProviderImpl(ValueProvider valueProvider, SessionData sessionDatas) {
				this.userValueProvider = valueProvider;
				this.sessionDatas = sessionDatas;
			}
			
			@Override
			public String getStringValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {

				if (sessionDatas.getValue(identifier) != null) {
					return sessionDatas.getValue(identifier);
				} else {
					return userValueProvider.getStringValue(identifier);
				}
			}

			@Override
			public long getLongValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {

				if (sessionDatas.getValue(identifier) != null) {
					return Long.parseLong(sessionDatas.getValue(identifier));	
				} else {
					return userValueProvider.getLongValue(identifier);
				}

			}

			@Override
			public List<String> getStringValues(String identifier)
					throws InvalidTypeCastException, MissingIdentifierException {
							
				List<String> listValue = new ArrayList<String>();
				
				listValue.add(getStringValue(identifier));
				return listValue;
			}

			@Override
			public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
				
				List<Long> listValue = new ArrayList<Long>();
				
				listValue.add(getLongValue(identifier));
				return listValue;
				
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		}
	}
	
}

