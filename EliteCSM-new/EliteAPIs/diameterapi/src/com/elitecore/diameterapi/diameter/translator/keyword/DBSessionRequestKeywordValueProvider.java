package com.elitecore.diameterapi.diameter.translator.keyword;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
/**
 * 
 * Incorporates DBSESSION Keyword in Copy Packet Request Translation. 
 * <p>
 * Syntax: ${DBSESSION}:SessionlookupColumnName - here DBSESSION is keyword and SessionlookupColumnName
 * 		can be any column from configured field mapping.
 * </p>
 *
 * <li> SessionlookupColumnName, in ${DBSESSION}:SessionlookupColumnName configuration in copy 
 * 		packet translation, must be one of the column name from field mapping bound to session 
 * 		scenario of diameter session manager.
 * 
 * <li> First {@link ValueProvider} is generated using the located session data stored in 
 * 		the request packet.
 * 
 * <li> {@link ValueProvider} provides the value or values on the basis of called method
 * 		using located session data, if column name is found as a key then it will return value
 * 		or it throws {@link MissingIdentifierException}
 * 
 * <li> If request packet does not contains any located session data or found session data is 
 * 		empty then it throws {@link MissingIdentifierException}
 * 
 * @author malav.desai
 *
 */
public class DBSessionRequestKeywordValueProvider extends KeywordValueProvider {
	
	private static final String MODULE = "DBSESSION-REQ-KEYWORD-VAL-PROVIDR";

	/**
	 * Creates a new instance of {@link DBSessionRequestKeywordValueProvider} with column name as a parameter 
	 * 
	 * @param columnName for the required session value
	 * @throws NullPointerException in case of value passed is null
	 */
	public DBSessionRequestKeywordValueProvider(@Nonnull String columnName) {
		super(checkNotNull(columnName, "columnName is null"));
	}

	/**
	 * Extracts {@link DiameterRequest} from the passed parameter {@link TranslatorParams}.
	 * From this request get the located session data and generate {@link ValueProvider} for that 
	 * data. If the session data from request is null or empty, it will throw {@link MissingIdentifierException} 
	 * 
	 * 
	 * @return {@link ValueProvider} for session data retried from source request packet 
	 * @throws MissingIdentifierException if session data does not found in source request packet
	 * @throws ClassCastException if it is used for translation other than Diameter-To-AnyType
	 */
	@Override
	protected final ValueProvider getValueProvider(TranslatorParams params)
			throws MissingIdentifierException {
		
		DiameterRequest diameterRequest = (DiameterRequest) params.getParam(TranslatorConstants.FROM_PACKET);
		
		List<SessionData> locatedSessionDatas = diameterRequest.getLocatedSessionData();
		if (Collectionz.isNullOrEmpty(locatedSessionDatas)) {
			throw new MissingIdentifierException("No session data was found from source packet with HbH Id: "
					+ diameterRequest.getHop_by_hopIdentifier() + " and EtE Id: " + diameterRequest.getEnd_to_endIdentifier());
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Located session data from source packet with HbH Id: "
					+ diameterRequest.getHop_by_hopIdentifier() + " and EtE Id: " + diameterRequest.getEnd_to_endIdentifier());
			for ( SessionData sessionData : locatedSessionDatas) {
				LogManager.getLogger().debug(MODULE, sessionData.toString());
			}
		}
		return new SessionDataProvider(locatedSessionDatas);
	}

	/**
	 * Value Provider for the session data.
	 * 
	 * @author malav.desai
	 */
	private class SessionDataProvider implements ValueProvider {
		
		private static final String MODULE = "SESSION-DATA-PROVIDER";
		private @Nonnull List<SessionData> locatedSessionDatas;
		
		public SessionDataProvider(@Nonnull List<SessionData> locatedSessionDatas) throws MissingIdentifierException {
			this.locatedSessionDatas = locatedSessionDatas;
		}
		
		/**
		 * Using the column name as a key finds the first non-null and non-empty value.
		 * If found, returns that value otherwise throws {@link MissingIdentifierException}   
		 * 
		 * @param columnName of field containing the required value
		 * @return {@link String} value of first encountered column name session data 
		 * @throws MissingIdentifierException if no value is found from session data using column name    
		 */
		@Override
		public String getStringValue(String columnName)
				throws InvalidTypeCastException, MissingIdentifierException {
			for (SessionData sessionData : locatedSessionDatas) {
				String value = sessionData.getValue(columnName);
				if (Strings.isNullOrBlank(value) == false) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Value: " + value + " was found for the DB field: " + columnName);
					}
					return value;
				}
			}
			throw new MissingIdentifierException("No data was found for DB field: " + columnName + " in Session Data");
		}
		
		/**
		 * Using the column name as a key finds the first long value.
		 * If found, returns that value otherwise throws {@link MissingIdentifierException}   
		 * 
		 * @param columnName of field containing the required value
		 * @return long value of first encountered column name session data
		 * @throws MissingIdentifierException if no value is found from session data using column name
		 * @throws InvalidTypeCastException if found value is not long    
		 */
		@Override
		public long getLongValue(String columnName)
				throws InvalidTypeCastException, MissingIdentifierException {
			for (SessionData sessionData : locatedSessionDatas) {
				String value = sessionData.getValue(columnName);
				if (Strings.isNullOrBlank(value) == false) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Value: " + value + " was found for the DB field: " + columnName);
					}
					try {
						return Long.parseLong(value);
					} catch (NumberFormatException nfe) {
						throw new InvalidTypeCastException("Value: " + value + " retrived from column: " + columnName + " is not of long type");
					}
				}
			}
			throw new MissingIdentifierException("No data was found for DB field: " + columnName + " in Session Data");
		}
		
		/**
		 * Using the column name as a key finds the all non-null and non-empty value.
		 * If found returns all values as list otherwise throw {@link MissingIdentifierException}   
		 * 
		 * @param columnName of field containing the required value
		 * @return list of all {@link String} values containing column name
		 * @throws MissingIdentifierException if no value is found from session data using column name    
		 */
		@Override
		public List<String> getStringValues(String columnName)
				throws InvalidTypeCastException, MissingIdentifierException {
			List<String> stringValues = new ArrayList<String>();
			
			for (SessionData sessionData : locatedSessionDatas) {
				String value = sessionData.getValue(columnName);
				if (Strings.isNullOrBlank(value) == false) {
					stringValues.add(value);
				}
			}
			if (stringValues.isEmpty()) {
				throw new MissingIdentifierException("No data was found for DB field: " + columnName + " in Session Data");
			} 
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Values: " + stringValues + " was found for the DB field: " + columnName);
			}
			return stringValues;
		}
		
		/**
		 * Using the column name as a key finds the first long value.
		 * If found returns that value otherwise throw {@link MissingIdentifierException}   
		 * 
		 * @param columnName of field containing the required value
		 * @return list of all long values containing column name
		 * @throws MissingIdentifierException if no value is found from session data using column name
		 * @throws InvalidTypeCastException when any of value is not long
		 */
		@Override
		public List<Long> getLongValues(String columnName)
				throws InvalidTypeCastException, MissingIdentifierException {
			
			List<Long> longValues = new ArrayList<Long>();
			
			for (SessionData sessionData : locatedSessionDatas) {
				String value = sessionData.getValue(columnName);
				if (Strings.isNullOrBlank(value) == false) {
					try {
						longValues.add(Long.parseLong(value));
					} catch (NumberFormatException nfe) {
						throw new InvalidTypeCastException("Value: " + value + " retrived from column: " + columnName + " is not of long type"); 
					}
				}
			}
			if (longValues.isEmpty()) {
				throw new MissingIdentifierException("No data was found for DB field: " + columnName + " in Session Data");
			} 
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Values: " + longValues + " was found for the DB field: " + columnName);
			}
			return longValues;
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}
}